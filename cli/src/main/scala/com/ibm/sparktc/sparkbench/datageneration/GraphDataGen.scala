/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
package com.ibm.sparktc.sparkbench.datageneration

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{getOrDefault, getOrThrow, time, any2Long}
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.graphx.util.GraphGenerators

object GraphDataGen extends WorkloadDefaults {

  val name = "graph-data-generator"
  val defaultMu = 4.0
  val defaultSigma = 1.3
  val defaultSeed = -1L
  val defaultNumOfPartitions = 0

  override def apply(m: Map[String, Any]): GraphDataGen = {
      val numVertices = getOrThrow(m, "vertices").asInstanceOf[Int]
      val mu = getOrDefault[Double](m, "mu", defaultMu)
      val sigma = getOrDefault[Double](m, "sigma", defaultSigma)
      val numPartitions = getOrDefault[Int](m, "partitions", defaultNumOfPartitions)
      val seed = getOrDefault[Long](m, "seed", defaultSeed, any2Long)
      val output = {
        val str = getOrThrow(m, "output").asInstanceOf[String]
        val s = verifySuitabilityOfOutputFileFormat(str)
        Some(s)
      }

    new GraphDataGen(
      numVertices = numVertices,
      input = None,
      output = output,
      mu = mu,
      sigma = sigma,
      seed = seed,
      numPartitions = numPartitions
    )
  }

  /*
   *  GraphLoader will only take in edge lists of a very specific form. From the documentation:
   *  -------
   *
   *  GraphLoader.edgeListFile provides a way to load a graph from a list of edges on disk.
   *  It parses an adjacency list of (source vertex ID, destination vertex ID) pairs of the following form, skipping comment lines that begin with #:
   *
   *    # This is a comment
   *    2 1
   *    4 1
   *    1 2
   *
   *  -------
   *  Due to to this limitation, any Graph Data Generator in spark-bench can only write out graphs in text format.
   *  Parquet, CSV, etc. are not permitted.
   */
  private[datageneration] def verifySuitabilityOfOutputFileFormat(str: String): String = {
    val strArr: Array[String] = str.split('.')

    (strArr.length, strArr.last) match {
      case (1, _) => throw SparkBenchException("Output file for GraphDataGen must have \".txt\" as the file extension." +
        "Please modify your config file.")
      case (2, "txt") => str
      case (_, _) => throw SparkBenchException("Due to limitations of the GraphX GraphLoader, " +
        "the graph data generators may only save files as \".txt\"." +
        "Please modify your config file.")
    }
  }

}

case class GraphDataGen (
                          numVertices: Int,
                          input: Option[String] = None,
                          output: Option[String],
                          mu: Double = 4.0,
                          sigma: Double = 1.3,
                          seed: Long = 1,
                          numPartitions: Int = 0
                        ) extends Workload {

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (generateTime, graph) = time(GraphGenerators.logNormalGraph(spark.sparkContext, numVertices, numPartitions, mu, sigma))
    val (convertTime, out) = time(graph.edges.map(e => s"${e.srcId.toString} ${e.dstId}"))
    val (saveTime, _) = time(out.saveAsTextFile(output.get))

    val timeResultSchema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("generate", LongType, nullable = true),
        StructField("convert", LongType, nullable = true),
        StructField("save", LongType, nullable = true),
        StructField("total_runtime", LongType, nullable = false)
      )
    )
    val total = generateTime + convertTime + saveTime
    val timeList = spark.sparkContext.parallelize(Seq(Row(GraphDataGen.name, timestamp, generateTime, convertTime, saveTime, total)))
    spark.createDataFrame(timeList, timeResultSchema)
  }
}
