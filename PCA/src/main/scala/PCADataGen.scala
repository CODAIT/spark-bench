
/*
 * (C) Copyright IBM Corp. 2015 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PCA.src.main.scala
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.{SparkContext,SparkConf}


object PCADataGen {
    def main(args: Array[String]) {
        Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        if (args.length < 3) {
            println("usage: <output> <numExamples> <numFeatures> [numpar]")
                System.exit(0)
        }
        val conf = new SparkConf
            conf.setAppName("Spark PCA DataGen")
            val sc = new SparkContext(conf)

            val output = args(0)
            val numExamples = args(1).toInt
            val numFeatures = args(2).toInt
            val epsilon = 100
            val defPar = if (System.getProperty("spark.default.parallelism") == null) 2 else System.getProperty("spark.default.parallelism").toInt
            val numPar = if (args.length > 3) args(3).toInt else defPar

            val data= LinearDataGenerator.generateLinearRDD(sc,numExamples,numFeatures,epsilon,numPar)
            data.saveAsTextFile(output)

            sc.stop();
    }

}
