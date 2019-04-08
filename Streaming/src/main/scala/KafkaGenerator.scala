import java.util.Properties

import org.apache.log4j.{Level, Logger}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.util.Random

/**
  * 模拟Kafka数据流生产者，生成Mock数据
  */
object KafkaGenerator {
  /**
    * 定义页面访问类
    *
    * @param url     网址
    * @param status  状态码
    * @param zipCode 地区码
    * @param userID  用户id
    */
  case class PageClick(url: String, status: Int, zipCode: String, userID: Int)
    extends Serializable {
    override def toString: String = {
      "%s\t%s\t%s\t%s\n".format(url, status, zipCode, userID)
    }
  }

  //模拟访问的网址
  val pages = Map("office.inspur.com/home" -> .3,
    "office.inspur.com/news" -> .2,
    "nice.inspur.com/portal" -> .3,
    "m-learning.inspur.com" -> .1,
    "office.inspur.com/jobs" -> .1)
  //模拟网站http状态码
  val httpStatus = Map(200 -> .90, 403 -> .05, 404 -> .05)
  //模拟用户来源地区北京、郑州、济南
  val userZipCode = Map("BJ" -> .4, "ZZ" -> .3, "JN" -> .3)
  //模拟工号
  val userID = Map((100 to 199).map(_ -> .01): _*)

  //概率分布选择器
  def pickFromDistribution[T](inputMap: Map[T, Double]): T = {
    val rand = new Random().nextDouble()
    var total = 0.0
    for ((item, prob) <- inputMap) {
      total = total + prob
      if (total > rand) {
        return item
      }
    }
    inputMap.take(1).head._1 // Shouldn't get here if probabilities add up to 1.0
  }

  //生成一条点击事件
  def getNextClickEvent: String = {
    val id = pickFromDistribution(userID)
    val page = pickFromDistribution(pages)
    val status = pickFromDistribution(httpStatus)
    val zipCode = pickFromDistribution(userZipCode)
    PageClick(page, status, zipCode, id).toString()
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: KafkaGenerator <Broker:port> <ClicksPerSecond> [Topic] [Limit]")
      System.exit(1)
    }
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

//    val brokers = "10.166.16.35:6667"
//    val viewsPerSecond = 5.0
    //两个主要参数
    val brokers = args(0)
    val viewsPerSecond = args(1).toFloat
    var toTopic = "my-output-topic"
    var limit = 999
    if (args.length == 3) {
      toTopic = args(2)
      println("复写了Kafka Topic,默认是my-output-topic")
    }
    if (args.length == 4) {
      limit = args(3).toInt
      println("复写了单次生成消息上限,默认是999")
    }
    val sleepDelayMs = (1000.0 / viewsPerSecond).toInt
    println("开始运行 Start Writing to topic: " + toTopic)
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("acks", "all")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    var count = 0
    var start = true
    while (start) {
      count += 1
      Thread.sleep(sleepDelayMs)
      val key = new StringBuilder("key-" + count + "-" + System.currentTimeMillis()).toString()
      producer.send(new ProducerRecord[String, String](toTopic,
        key, getNextClickEvent))
      if (count == 1) println("消息发送成功 connected to: " + brokers)
      if (count > limit) {
        start = false
        println("程序即将关闭 for messages more than [Limit]: " + limit)
      }
    }
    producer.flush()
    producer.close()
  }

}
