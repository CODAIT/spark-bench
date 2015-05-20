/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 * unused
 */
import org.apache.spark.mllib.util.SVMDataGenerator;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class SVMDataGen {
            public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("usage: <nExamples> <nFeatures> <eps> <nparts> <intercept> <output>");
            System.exit(0);
        }
						Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
				Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        int nExamples = Integer.parseInt(args[0]);
        int nFeatures = Integer.parseInt(args[1]);
        double eps = Double.parseDouble(args[2]);
        int numPar = Integer.parseInt(args[3]);
        double intercepts = Double.parseDouble(args[4]);        
        String output = args[5];
        
        SparkConf conf = new SparkConf().setAppName("SVM data generation");
        //JavaSparkContext sc = new JavaSparkContext(conf);
        SparkContext sc = new SparkContext(conf);
        //RDD<double[]> data = KMeansDataGenerator.generateKMeansRDD(sc, numPoint, numCluster, numDim, scaling, numPar);
        /*RDD<LabeledPoint> data=SVMDataGenerator.generateLinearRDD(sc,nExamples,
                nFeatures,eps,numPar,intercepts);
        JavaRDD<LabeledPoint> tmpdata=data.toJavaRDD();
        JavaRDD<String> parsedData = tmpdata.map(
               new Function<LabeledPoint, String>() {
                    public String call(LabeledPoint s) {                        
                        return s.toString();
                    }
                }
        );
        parsedData.saveAsTextFile(output);*/
    }
}
