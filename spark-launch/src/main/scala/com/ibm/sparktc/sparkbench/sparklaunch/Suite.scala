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

package com.ibm.sparktc.sparkbench.sparklaunch

case class Suite(
                  description: Option[String],
                  repeat: Int = 1,
                  parallel: Boolean = false,
                  benchmarkOutput: String,
                  workloadConfigs: Seq[Map[String, Any]]

                )

object Suite {

  def build(confsFromArgs: Seq[Map[String, Seq[Any]]],
            description: Option[String],
            repeat: Int,
            parallel: Boolean,
            benchmarkOutput: String): Suite = {
    Suite(
      description,
      repeat,
      parallel,
      benchmarkOutput,
      confsFromArgs.flatMap( splitToIndividualWorkloadConfigs )
    )
  }

  //http://stackoverflow.com/questions/14740199/cross-product-in-scala
  private def crossJoin(list: Seq[Seq[Any]]): Seq[Seq[Any]] =
    list match {
      case xs :: Nil => xs map (Seq(_))
      case x :: xs => for {
        i <- x
        j <- crossJoin(xs)
      } yield Seq(i) ++ j
    }

  private def splitToIndividualWorkloadConfigs(m: Map[String, Seq[Any]]): Seq[Map[String, Any]] = {
    val keys: Seq[String] = m.keys.toSeq
    val valuesSeq: Seq[Seq[Any]] = m.map(_._2).toSeq //for some reason .values wasn't working properly

    // All this could be one-lined, I've multi-lined it and explicit typed it for clarity
    val joined: Seq[Seq[Any]] = crossJoin(valuesSeq)
    val zipped: Seq[Seq[(String, Any)]] = joined.map(keys.zip(_))
    val mapSeq: Seq[Map[String, Any]] = zipped.map(_.map(kv => kv._1.toLowerCase -> kv._2).toMap)

    mapSeq
  }
}
