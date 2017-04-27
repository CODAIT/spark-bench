package com.ibm.sparktc.sparkbench.utils

object GeneralFunctions {

  def getOrDefault[A](map: Map[String, Any], name: String, default: A): A = map.get(name) match {
    case Some(x) => x.asInstanceOf[A]
    case None => default
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

}