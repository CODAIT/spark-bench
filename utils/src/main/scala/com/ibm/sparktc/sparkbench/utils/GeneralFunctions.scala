/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.utils

import java.io.StringWriter

import scala.util.{Failure, Random, Success, Try}

object GeneralFunctions {

  val any2Long: (Any) => Long = {
    case x: Number => x.longValue
    case x => x.toString.toLong
  }

  private def defaultFn[A](any: Any): A = any.asInstanceOf[A]

  private[utils] def tryWithDefault[A](a: Any, default: A, func: Any => A): A = Try(func(a)) match {
    case Success(b) => b
    case Failure(_) => default
  }

  def getOrDefaultOpt[A](opt: Option[Any], default: A, func: Any => A = defaultFn[A](_: Any)): A = {
    opt.map(tryWithDefault(_, default, func)).getOrElse(default)
  }

  def getOrDefault[A](map: Map[String, Any], name: String, default: A, func: Any => A = defaultFn[A](_: Any)): A = {
    getOrDefaultOpt(map.get(name), default, func)
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

  def getOrThrow[A](opt: Option[A], msg: String = "Empty option"): A = opt match {
    case Some(x) => x
    case _ => throw SparkBenchException(s"Error: $msg")
  }

  def getOrThrow(m: Map[String, Any], key: String): Any = getOrThrow(m.get(key))
  def getOrThrowT[T](m: Map[String, Any], key: String): T = getOrThrow(m.get(key)).asInstanceOf[T]

  def optionallyGet[A](m: Map[String, Any], key: String): Option[A] = m.get(key).map { any =>
    any.asInstanceOf[A]
  }

  def stringifyStackTrace(e: Throwable): String = {
    import java.io.PrintWriter
    val sw = new StringWriter()
    val pw = new PrintWriter(sw)
    e.printStackTrace(pw)
    sw.toString
  }

  def randomLong(max: Long): Long = {
    val start = 0L
    (start + (Random.nextDouble() * (max - start) + start)).toLong
  }

}