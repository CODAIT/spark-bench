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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.util.KMeansDataGenerator;
import org.apache.spark.rdd.RDD;

/**
 * @author minli
 */
public class KmeansDataGenJava {
  public static void main(String[] args) {
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
    if (args.length < 5) {
      System.out.println("usage: <output> <numPoints> <numClusters> <dimenstion> <scaling factor> [numPar]");
      System.exit(0);
    }
    String output = args[0];
    int numPoint = Integer.parseInt(args[1]);
    int numCluster = Integer.parseInt(args[2]);
    int numDim = Integer.parseInt(args[3]);
    double scaling = Double.parseDouble(args[4]);
    int numPar = (args.length > 5) ? Integer.parseInt(args[5]) : System.getProperty("spark.default.parallelism")
        != null ? Integer.parseInt(System.getProperty("spark.default.parallelism")) : 2;

    SparkConf conf = new SparkConf().setAppName("Kmeans data generation (Java version)");
    JavaSparkContext jsc = new JavaSparkContext(conf);
    RDD<double[]> data = KMeansDataGenerator.generateKMeansRDD(jsc.sc(), numPoint, numCluster, numDim, scaling, numPar);
    JavaRDD<double[]> tmpdata = data.toJavaRDD();
    JavaRDD<String> parsedData = tmpdata.map(
        new Function<double[], String>() {
          public String call(double[] s) {
            String sarray = "";
            for (int i = 0; i < s.length; i++) {
              sarray += s[i] + " ";
            }
            return sarray;
          }
        }
    );
    parsedData.saveAsTextFile(output);

    jsc.stop();
  }
}
