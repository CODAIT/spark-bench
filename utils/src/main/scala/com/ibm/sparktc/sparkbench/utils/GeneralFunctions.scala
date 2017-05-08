package com.ibm.sparktc.sparkbench.utils


object GeneralFunctions {

  val any2Int2Long = (a: Any) => {
    val x = a.asInstanceOf[Int]
    x.toLong
  }

  def getOrDefault[A](
                       map: Map[String, Any],
                       name: String, default: A,
                       optFunc: Option[(Any) => A] = None
                     ): A =
    (map.get(name), optFunc) match {
      case (Some(a), Some(f)) => f(a)
      case (Some(x), None) => x.asInstanceOf[A]
      case (_, _) => default
    }




  def time[R](block: => R): (Long, R) = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    (t1 - t0, result)
  }

  // https://gist.github.com/lauris/7dc94fb29804449b1836
  def ccToMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }

  def verifyOrThrow[A](
                        m: Map[String, Any],
                        key: String,
                        whatItShouldBe: A,
                        errorMessage: String): A = m.get(key) match {
    case None => throw SparkBenchException(s"Required key not found: $key")
    case Some(`whatItShouldBe`) => whatItShouldBe.asInstanceOf[A]
    case _ => throw SparkBenchException(errorMessage)
  }

  def getOrThrow[A](opt: Option[A]): A = opt match {
    case Some(x) => x
    case _ => throw SparkBenchException("Error: empty option")
  }

  def getOrThrow(m: Map[String, Any], key: String): Any = getOrThrow(m.get(key))



}