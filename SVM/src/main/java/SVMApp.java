/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 */
package SVM.src.main.java;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import java.util.Arrays;
import java.util.Random;

import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.*;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.spark.storage.StorageLevel;

//static class Class1 {}
 // static class Class2 {}
  
public class SVMApp {
  public static void main(String[] args) {
      if (args.length < 3) {
            System.out.println("usage: <input> <output>  <maxIterations> <StorageLevel>");
            System.exit(0);
        }
		Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
		Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        String input = args[0];
        String output = args[1];
        int numIterations = Integer.parseInt(args[2]);
		String storage_level=args[3];
		
        SparkConf conf = new SparkConf().setAppName("SVM Classifier Example");
		
	//	conf.registerKryoClasses(new Class<?>[]{ Class1.class,Class2.class});
        JavaSparkContext sc = new JavaSparkContext(conf);
		//conf.registerKryoClasses(new Class<?>[]{ SVMApp.class});
       // SparkContext sc = new SparkContext(conf);
    
    //JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, input).toJavaRDD();

    long start = System.currentTimeMillis();
            JavaRDD<String> tmpdata = sc.textFile(input);
        
        JavaRDD<LabeledPoint> data = tmpdata.map(
                new Function<String, LabeledPoint>() {
                    public LabeledPoint call(String line) {                        
                        return LabeledPoint.parse(line);
                    }
                }
        );
    // Split initial RDD into two... [90% training data, 10% testing data].
    JavaRDD<LabeledPoint> training = data.sample(false, 0.9, 11L);
    
	
	if( storage_level.equals("MEMORY_AND_DISK_SER"))
			training.persist(StorageLevel.MEMORY_AND_DISK_SER());			
		else{
			training.cache();
						
		}
	
	System.out.println("test data " );
    JavaRDD<LabeledPoint> test = data.subtract(training);
    double loadTime = (double)(System.currentTimeMillis() - start) / 1000.0;
	
	/*if( storage_level.equals("MEMORY_AND_DISK_SER"))
			test.persist(StorageLevel.MEMORY_AND_DISK_SER());			
		else{
			test.cache();
						
		}    */
    // Run training algorithm to build the model.
    System.out.println("Train model " );
    start = System.currentTimeMillis();
    final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations);
    double trainingTime = (double)(System.currentTimeMillis() - start) / 1000.0;
    
    // Clear the default threshold.
    start = System.currentTimeMillis();
    model.clearThreshold();
	System.out.println("predict score and labels " );
    // Compute raw scores on the test set.
    JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test.map(
      new Function<LabeledPoint, Tuple2<Object, Object>>() {
        public Tuple2<Object, Object> call(LabeledPoint p) {
          Double score = model.predict(p.features());
          return new Tuple2<Object, Object>(score, p.label());
        }
      }
    );
    
    // Get evaluation metrics.
    BinaryClassificationMetrics metrics = 
      new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
    double auROC = metrics.areaUnderROC();
    double testTime = (double)(System.currentTimeMillis() - start) / 1000.0;
    
    System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f}\n", loadTime, trainingTime, testTime);
    //System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f,\"saveTime\":%.3f}\n", loadTime, trainingTime, testTime, saveTime);
    System.out.println("Area under ROC = " + auROC);
   // System.out.println("training Weight = " + 
     //           Arrays.toString(model.weights().toArray()));
    sc.stop();
  }
}
