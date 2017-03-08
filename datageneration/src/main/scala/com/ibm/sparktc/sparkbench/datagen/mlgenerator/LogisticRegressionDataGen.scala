/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ibm.sparktc.sparkbench.datagen.mlgenerator

import org.apache.spark.mllib.util.LogisticRegressionDataGenerator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import org.apache.log4j.Level

class LogisticRegressionDataGen {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    if (args.length < 5) {
      println("usage: <output> <nExamples> <nFeatures> <eps> <probone> [numPar]")
      sys.exit(0)
    }

    val output: String = args(0)
    val nExamples: Int = args(1).toInt
    val nFeatures: Int = args(2).toInt
    val eps: Double = args(3).toDouble
    val probOne: Double = args(4).toDouble
    val numPar: Int = {
      if (args.length > 5) args(5).toInt
      else if (System.getProperty("spark.default.parallelism") != null) sys.props("spark.default.parallelism").toInt
      else 2
    }

    val conf: SparkConf = new SparkConf().setAppName("Liner Regression data generation")
    val sc = new SparkContext(conf)

    val data: RDD[LabeledPoint] = LogisticRegressionDataGenerator.generateLogisticRDD(sc,nExamples, nFeatures,eps,numPar,probOne)

    val parsedData: RDD[String] = data.map(_.toString)

    parsedData.saveAsTextFile(output)
  }
}

//
//public class LogisticRegressionDataGen {
//        public static void main(String[] args) {
//        if (args.length < 5) {
//            System.out.println("usage: <output> <nExamples> <nFeatures> <eps> <probone> [numPar]");
//            System.exit(0);
//        }
//	Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
//	Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
//        String output = args[0];
//        int nExamples = Integer.parseInt(args[1]);
//        int nFeatures = Integer.parseInt(args[2]);
//        double eps = Double.parseDouble(args[3]);
//        double probOne = Double.parseDouble(args[4]);
//        int numPar = (args.length > 5) ? Integer.parseInt(args[5]) : System.getProperty("spark.default.parallelism") != null ? Integer.parseInt(System.getProperty("spark.default.parallelism")) : 2;
//
//        SparkConf conf = new SparkConf().setAppName("Logisitc Regression data generation");
//        SparkContext sc = new SparkContext(conf);
//        RDD<LabeledPoint> data=LogisticRegressionDataGenerator.generateLogisticRDD(sc,nExamples,
//                nFeatures,eps,numPar,probOne);
//        JavaRDD<LabeledPoint> tmpdata=data.toJavaRDD();
//        JavaRDD<String> parsedData = tmpdata.map(
//               new Function<LabeledPoint, String>() {
//                    public String call(LabeledPoint s) {
//                        return s.toString();
//                    }
//                }
//        );
//        parsedData.saveAsTextFile(output);
//		sc.stop();
//    }
//}
