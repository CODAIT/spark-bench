/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package kmeans_java.src.main.java;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.SparkConf;

public class KmeansApp {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("usage: <input> <output> <numClusters> <maxIterations> <runs> - optional");
            System.exit(0);
        }
        String input = args[0];
        String output = args[1];        
        int numClusters = Integer.parseInt(args[2]);
        int numIterations = Integer.parseInt(args[3]);
        int numRuns = Integer.parseInt(args[4]);
        
        
        SparkConf conf = new SparkConf().setAppName("K-means Example");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load and parse data
        
        JavaRDD<String> data = sc.textFile(input);
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
                });

        // Cluster the data into two classes using KMeans
        
        
        final KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);
        JavaRDD<String> vectorIndex = parsedData.map(
                new Function<Vector, String>() {
                    public String call(Vector point) {
                        int ind = clusters.predict(point);
                        return point.toString()+" "+Integer.toString(ind);
                    }
                });
        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(parsedData.rdd());
        vectorIndex.saveAsTextFile(output);
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);
    }
}
