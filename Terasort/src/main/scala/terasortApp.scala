/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package src.main.scala
import org.apache.log4j.Logger
import org.apache.log4j.Level

import com.google.common.primitives.UnsignedBytes
import org.apache.spark.SparkContext._
import org.apache.spark._
import org.apache.spark.{SparkConf, SparkContext}

/**
 * This file is modified from  com.github.ehiggs.spark.terasort.*
 */
object terasortApp {

    implicit val caseInsensitiveOrdering = UnsignedBytes.lexicographicalComparator

        def main(args: Array[String]) {
            Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
            Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);

            if (args.length < 2) {
                println("Usage:[input-file] [output-file]")      
                    System.exit(0)
            }

            // Process command line arguments
            val inputFile = args(0)
                val outputFile = args(1)

                val conf = new SparkConf()
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .setAppName(s"TeraSort")
                val sc = new SparkContext(conf)

                val dataset = sc.newAPIHadoopFile[Array[Byte], Array[Byte], TeraInputFormat](inputFile)
                val sorted = dataset.partitionBy(new TeraSortPartitioner(dataset.partitions.size)).sortByKey()
                sorted.saveAsNewAPIHadoopFile[TeraOutputFormat](outputFile)
        }
}
