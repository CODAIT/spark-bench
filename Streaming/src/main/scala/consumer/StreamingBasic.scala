package consumer

import producer.KafkaGenerator.PageClick
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingBasic {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("StreamingKafkaTest")
      .setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    conf.registerKryoClasses(Array(classOf[ConsumerRecord[String, String]]))
    val ssc = new StreamingContext(conf, Seconds(5))
    val KafkaServerHostsCluster = "10.166.16.35:6667"
    val consumerGroup = "oneTest"
    val topicName = "my-output-topic"

    //ssc.checkpoint("/user/data/checkpoint/streamingTest_1")
    //ssc.checkpoint("file:///data/spark/checkpoint/streamingTest_1")
    def getStreamByKafka(ssc: StreamingContext, topic: Array[String], group: String, broker: String)
    : InputDStream[ConsumerRecord[String, String]] = {
      //消费者配置
      val kafkaParam = Map(
        "bootstrap.servers" -> broker, //用于初始化链接到集群的地址
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> group, //用于标识这个消费者属于哪个消费团体
        // 如果没有初始化偏移量或者当前的偏移量不存在任何服务器上,可使用这个配置，latest自动重置偏移量为最新的偏移量
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (true: java.lang.Boolean) //如果是true，则这个消费者的偏移量会在后台自动提交
      )
      //创建DStream，返回接收到的输入数据
      val stream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](topic, kafkaParam))
      stream
    }

    def getKafkaStreamingRDD(topic: Array[String], consumerGroup: String)
    : InputDStream[ConsumerRecord[String, String]] = {
      getStreamByKafka(ssc, topic, consumerGroup, KafkaServerHostsCluster)
    }

    val ds = getKafkaStreamingRDD(Array(topicName), consumerGroup)
      .map(record => {
        (record.key(), PageClick.fromString(record.value()))
      }).persist()
    ds.foreachRDD(rdd => {
      rdd.foreachPartition(part => {
        part.map("\n[map]==>Http Status times 10:" + _._2.status*10).foreach(print)
      })
    })
    ds.flatMap(rec=>{
      rec._1.split("-")
    }).foreachRDD(rdd => {
      rdd.foreachPartition(part => {
        part.map("\n[flatMap]==>key split number:" + _(1)).foreach(print)
      })
    })
    println("This App is Running now…… hold on!")
    ssc.start()
    ssc.awaitTermination()

  }
}
