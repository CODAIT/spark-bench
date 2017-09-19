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

import java.util

import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigObject}

object TypesafeAccessories {

  /**
    * Utility method to get a piece of the config by the key name.
    * @param name
    * @param config
    * @return
    */
  def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  /**
    * Unwraps a Typesafe Config object into a Map[String, Seq[Any]].
    * Parameters that are not already in a list format will be wrapped in a Seq().
    *
    * Example:
    *
    * {
    *   a = "blah"
    *   b = [1, 2]
    *   c = ["cool", "stuff"]
    * }
    *
    * is unwrapped and transformed to:
    *
    * Map(
    *   "a" -> Seq("blah"),
    *   "b" -> Seq(1, 2),
    *   "c" -> Seq("cool", "stuff")
    * )
    *
    * @param config
    * @return
    */
  def configToMapStringAny(config: Config): Map[String, Seq[Any]] = {
    val cfgObj = config.root()
    val unwrapped = cfgObj.unwrapped().asScala.toMap
    val stuff: Map[String, Seq[Any]] = unwrapped.map(kv => {
      val newValue: Seq[Any] = kv._2 match {
        case al: util.ArrayList[Any] => al.asScala
        case b: Any => Seq(b)
        //        case _ => throw SparkBenchException(s"Key ${kv._1} with value ${kv._2} had an unexpected type: ${kv._2.getClass.toString}")
      }
      kv._1 -> newValue
    })
    stuff
  }

  /**
    * http://stackoverflow.com/questions/14740199/cross-product-in-scala
    *
    * Returns a cross product of the the sequences.
    *
    * Example:
    *
    * Seq(
    *   Seq(1, 2),
    *   Seq("aa", "bb"),
    *   Seq(4.4)
    * )
    *
    * is crossjoined and the resulting output is:
    *
    * Seq(
    *   Seq(1, "aa", 4.4),
    *   Seq(1, "bb", 4.4),
    *   Seq(2, "aa", 4.4),
    *   Seq(2, "bb", 4.4)
    * )
    *
    * @param list
    * @return
    */
  private def crossJoin(list: Seq[Seq[Any]]): Seq[Seq[Any]] =
    list match {
      case xs :: Nil => xs map (Seq(_))
      case x :: xs => for {
        i <- x
        j <- crossJoin(xs)
      } yield Seq(i) ++ j
    }


  /**
    * Splits a Map of String to Sequences in a map of string to individual Any items.
    * @param m
    * @return
    */
  def splitGroupedConfigToIndividualConfigs(m: Map[String, Seq[Any]]): Seq[Map[String, Any]] = {
    val keys: Seq[String] = m.keys.toSeq
    val valuesSeq: Seq[Seq[Any]] = m.map(_._2).toSeq //for some reason .values wasn't working properly

    // All this could be one-lined, I've multi-lined it and explicit typed it for clarity
    val joined: Seq[Seq[Any]] = crossJoin(valuesSeq)
    val zipped: Seq[Seq[(String, Any)]] = joined.map(keys.zip(_))
    val mapSeq: Seq[Map[String, Any]] = zipped.map(_.map(kv => kv._1.toLowerCase -> kv._2).toMap)

    mapSeq
  }

  /**
    * Takes in a Typesafe config object and splits it into a cross-joined Sequence of Map[String, Any]]
    * @param config
    * @return
    */
  def configToMapSeq(config: Config): Seq[Map[String, Any]] =
    splitGroupedConfigToIndividualConfigs( configToMapStringAny( config ) )

}
