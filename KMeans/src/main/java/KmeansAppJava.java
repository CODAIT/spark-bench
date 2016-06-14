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
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

public class KmeansAppJava {
  public static void main(String[] args) {
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
    if (args.length < 5) {
      System.out.println("usage: <input> <output> <numClusters> <maxIterations> <runs> - optional");
      System.exit(0);
    }
    String input = args[0];
    String output = args[1];
    int K = Integer.parseInt(args[2]);
    int maxIterations = Integer.parseInt(args[3]);
    int runs = Integer.parseInt(args[4]);


    SparkConf conf = new SparkConf().setAppName("K-means Example");
    JavaSparkContext jsc = new JavaSparkContext(conf);

    // Load and parse data
    long start = System.currentTimeMillis();
    JavaRDD<String> data = jsc.textFile(input);
    JavaRDD<Vector> parsedData = data.map(
        new Function<String, Vector>() {
          public Vector call(String s) {
            String[] sarray = s.split(" ");
            double[] values = new double[sarray.length];
            for (int i = 0; i < sarray.length; i++) {
              values[i] = Double.parseDouble(sarray[i]);
            }
            return Vectors.dense(values);
          }
        }).cache();
    double loadTime = (double) (System.currentTimeMillis() - start) / 1000.0;

    start = System.currentTimeMillis();
    KMeansModel clusters = KMeans.train(parsedData.rdd(), K, maxIterations, runs, KMeans.K_MEANS_PARALLEL(), 127L);
    double trainingTime = (double) (System.currentTimeMillis() - start) / 1000.0;

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    start = System.currentTimeMillis();
    double WSSSE = clusters.computeCost(parsedData.rdd());
    double testTime = (double) (System.currentTimeMillis() - start) / 1000.0;

    start = System.currentTimeMillis();
    JavaRDD<String> vectorIndex = parsedData.map(
        new Function<Vector, String>() {
          public String call(Vector point) {
            int ind = clusters.predict(point);
            return point.toString() + " " + Integer.toString(ind);
          }
        });
    vectorIndex.saveAsTextFile(output);
    double saveTime = (double) (System.currentTimeMillis() - start) / 1000.0;

    System.out.printf("loadTime:%.3f, trainingTime:%.3f, testTime:%.3f, saveTime:%.3f\n", loadTime, trainingTime, testTime, saveTime);
    System.out.println("Within Set Sum of Squared Errors = " + WSSSE);
    jsc.stop();
  }
}
