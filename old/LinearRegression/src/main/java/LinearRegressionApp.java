/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 */

import java.util.Arrays;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
//import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionModel;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;
import org.apache.spark.rdd.RDD;
import scala.Tuple2;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class LinearRegressionApp {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("usage: <input> <output>  <maxIterations> ");
            System.exit(0);
        }
        String input = args[0];
        String output = args[1];
        int numIterations = Integer.parseInt(args[2]);
        Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
	Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        SparkConf conf = new SparkConf().setAppName("LinerRegressionApp Example");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load and parse data
	long start = System.currentTimeMillis();
        JavaRDD<String> data = sc.textFile(input);
        
        JavaRDD<LabeledPoint> parsedData = data.map(
                new Function<String, LabeledPoint>() {
                    public LabeledPoint call(String line) {                        
                        return LabeledPoint.parse(line);
                    }
                }
        );
        //parsedData.cache();
        /*JavaRDD<LabeledPoint> parsedData = data.map(
                new Function<String, LabeledPoint>() {
                    public LabeledPoint call(String line) {
                        String[] parts = line.split(",");
                        String[] features = parts[1].split(" ");
                        double[] v = new double[features.length];
                        for (int i = 0; i < features.length - 1; i++) {
                            v[i] = Double.parseDouble(features[i]);
                        }
                        return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(v));
                    }
                }
        );*/
        RDD<LabeledPoint> parsedRDD_Data=JavaRDD.toRDD(parsedData);
        parsedRDD_Data.cache();
	double loadTime = (double)(System.currentTimeMillis() - start) / 1000.0;

    // Building the model
	start = System.currentTimeMillis();
        final LinearRegressionModel model
                = LinearRegressionWithSGD.train(parsedRDD_Data, numIterations);
	double trainingTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        // Evaluate model on training examples and compute training error
	start = System.currentTimeMillis();
        JavaRDD<Tuple2<Double, Double>> valuesAndPreds = parsedData.map(
                new Function<LabeledPoint, Tuple2<Double, Double>>() {
                    public Tuple2<Double, Double> call(LabeledPoint point) {
                        double prediction = model.predict(point.features());
                        return new Tuple2<Double, Double>(prediction, point.label());
                    }
                }
        );
        //JavaRDD<Object>
        Double MSE = new JavaDoubleRDD(valuesAndPreds.map(
                new Function<Tuple2<Double, Double>, Object>() {
                    public Object call(Tuple2<Double, Double> pair) {
                        return Math.pow(pair._1() - pair._2(), 2.0);
                    }
                }
        ).rdd()).mean();
	double testTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f}\n", loadTime, trainingTime, testTime);
        //System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f,\"saveTime\":%.3f}\n", loadTime, trainingTime, testTime, saveTime);
        System.out.println("training Mean Squared Error = " + MSE);
        System.out.println("training Weight = " + 
                Arrays.toString(model.weights().toArray()));
        sc.stop();
    }
}
