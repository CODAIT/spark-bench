package consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf

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

  }
}
