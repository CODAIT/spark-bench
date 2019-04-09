package consumer

import consumer.StreamingBasic.getStreamByKafka
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream
import producer.KafkaGenerator.PageClick

/**
  * 对Kafka流进行Window操作的测试
  */
object StreamingWindow {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("StreamingKafkaWindowTest")
      .setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.registerKryoClasses(Array(classOf[ConsumerRecord[String, String]]))
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

    val ssc = new StreamingContext(conf, Seconds(10))
    ssc.checkpoint("file:///data/spark/checkpoint/streamingTest_2")

    val KafkaServerHostsCluster = "10.166.16.35:6667"
    val consumerGroup = "windowTest"
    val topicName = "my-output-topic"

    def getKafkaStreamingRDD(topic: Array[String], consumerGroup: String)
    : InputDStream[ConsumerRecord[String, String]] = {
      getStreamByKafka(ssc, topic, consumerGroup, KafkaServerHostsCluster)
    }

    val ds = getKafkaStreamingRDD(Array(topicName), consumerGroup)
      .map(record => {
        (record.key(), PageClick.fromString(record.value()))
      }).persist()

    val windowedStream1 = ds.window(Seconds(20), Seconds(10))
      .map(a => {
        (a._2.userID, a._2.url)
      })
    val windowedStream2 = ds.window(Minutes(1), Seconds(10))
      .map(a => {
        (a._2.userID, a._2.url)
      })
    windowedStream2.join(windowedStream1).print()
    ds.countByWindow(Seconds(20),Seconds(10)).print()
    ds.map(_._2.status).reduceByWindow(_+_,Seconds(20),Seconds(10)).print()
    ds.map(a=>(a._2.url,1)).reduceByKeyAndWindow(_+_,Seconds(20)).print()
    ds.map(a => {
      (a._2.url, a._2.zipCode)
    }).countByValueAndWindow(Seconds(20),Seconds(10)).print()



  }
}
