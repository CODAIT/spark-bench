/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans_java.src.main.java;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.apache.spark.mllib.util.KMeansDataGenerator;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.rdd.RDD;
/**
 *
 * @author minli
 */
public class KmeansGenData {

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
        int numPar = (args.length > 5) ? Integer.parseInt(args[5]) : System.getProperty("spark.default.parallelism") != null ? Integer.parseInt(System.getProperty("spark.default.parallelism")) : 2;
        
        SparkConf conf = new SparkConf().setAppName("Kmeans data generation");
        //JavaSparkContext sc = new JavaSparkContext(conf);
        SparkContext sc = new SparkContext(conf);
        RDD<double[]> data = KMeansDataGenerator.generateKMeansRDD(sc, numPoint, numCluster, numDim, scaling, numPar);
        JavaRDD<double[]> tmpdata=data.toJavaRDD();
        JavaRDD<String> parsedData = tmpdata.map(
               new Function<double[], String>() {
                    public String call(double[] s) {
                        String sarray = "";
                            for(int i=0;i<s.length;i++){
                                   sarray+=s[i]+" ";
                            }                        
                        //return sarray+"\n";
                        return sarray;
                    }
                }
        );
        parsedData.saveAsTextFile(output);
    }
}
