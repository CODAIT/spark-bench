package consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.log4j.Level
import org.apache.spark.SparkConf

object StreamingBasic {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("StreamingKafkaTest")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    org.apache.log4j.Logger.getRootLogger.setLevel(Level.WARN)
    conf.registerKryoClasses(Array(classOf[ConsumerRecord[String, String]]))

  }
}
