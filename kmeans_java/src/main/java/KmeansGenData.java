/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans_java.src.main.java;

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
        if (args.length < 5) {
            System.out.println("usage: <numPoints> <numClusters> <dimenstion> <scaling factor> <partition> <output>");
            System.exit(0);
        }
        int numPoint = Integer.parseInt(args[0]);
        int numCluster = Integer.parseInt(args[1]);
        int numDim = Integer.parseInt(args[2]);
        double scaling = Double.parseDouble(args[3]);
        int numPar = Integer.parseInt(args[4]);
        String output = args[5];
        
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
