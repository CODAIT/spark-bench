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
        
        long start = System.currentTimeMillis();
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
        double loadTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        // Cluster the data into two classes using KMeans
        
        
        start = System.currentTimeMillis();
        //final KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);
        final KMeansModel clusters = new KMeans().setK(numClusters).setMaxIterations(numIterations).setInitializationMode(KMeans.K_MEANS_PARALLEL()).setSeed(127).run(parsedData.rdd());
        double trainingTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        start = System.currentTimeMillis();
        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(parsedData.rdd());
        double testTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        start = System.currentTimeMillis();
        JavaRDD<String> vectorIndex = parsedData.map(
                new Function<Vector, String>() {
                    public String call(Vector point) {
                        int ind = clusters.predict(point);
                        return point.toString()+" "+Integer.toString(ind);
                    }
                });
        vectorIndex.saveAsTextFile(output);
        double saveTime = (double)(System.currentTimeMillis() - start) / 1000.0;
        System.out.printf("loadTime:%.3f, trainingTime:%.3f, testTime:%.3f, saveTime:%.3f\n", loadTime, trainingTime, testTime, saveTime);
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);
        sc.stop();
    }
}
