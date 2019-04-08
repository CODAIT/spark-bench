package consumer

import producer.KafkaGenerator.PageClick
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, TaskContext}
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

    /**
      * ========================缓存数据====================
      */
    val ds = getKafkaStreamingRDD(Array(topicName), consumerGroup)
      .map(record => {
        (record.key(), PageClick.fromString(record.value()))
      }).persist()

    /**
      * ======================map操作======================
      * 把状态码乘以10
      */
    ds.foreachRDD(rdd => {
      rdd.foreachPartition(part => {
        part.map("\n[map]==>Http Status times 10:" + _._2.status*10).foreach(print)
      })
    })

    /**
      * ===================flatMap操作===================
      * 把key拆分铺开
      */
    ds.flatMap(rec=>{
      rec._1.split("-")
    }).foreachRDD(rdd => {
      rdd.foreachPartition(part => {
        part.map("\n[flatMap]==>key splits:" + _).foreach(print)
      })
    })

    /**
      * =====================filter操作=====================
      */
    ds.filter(_._2.zipCode == "ZZ").foreachRDD(rdd => {
      rdd.foreachPartition(part => {
        part.map("\n[filter]==>郑州员工访问记录:" + _).foreach(print)
      })
    })

    /**
      * =================repartition重分区操作=================
      */
    ds.foreachRDD(rdd => {
      rdd.partitions.foreach(part => {
        println("Old partitionId:"+part.index)
      })
    })
    ds.repartition(5).foreachRDD(rdd => {
      rdd.partitions.foreach(part => {
        println("New partitionId:"+part.index)
      })
    })

    /**
      *======================union/transform操作==============
      */
    val arrays = List(PageClick("我是路人甲",200,"HK",100),PageClick("我是路人乙",200,"HK",100))
    val rdd = ssc.sparkContext.parallelize(arrays)
    ds.map(_._2).transform(p=>{
      p.union(rdd).map(_.toString)
    }).print()

    /**
      *======================count操作======================
      */
    println("本批次的消息数:"+ds.count().print())
    /**
      *======================reduce操作=====================
      */
    /**
      *======================countByValue操作===============
      */
    /**
      *======================reduceByKey操作================
      */
    /**
      *=====================join、transform操作==============
      */
    /**
      *======================cogroup操作=====================
      */
    /**
      *===================updateStateByKey操作================
      */

    println("This app is running now…… hold on!")
    ssc.start()
    ssc.awaitTermination()

  }
}
