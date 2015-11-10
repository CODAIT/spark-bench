/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 */
package DecisionTree.src.main.java;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import java.util.HashMap;
import scala.Tuple2;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.SparkConf;

public class DecisionTreeApp {
    
        public static void main(String[] args) {
            Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
            Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
            if (args.length < 3) {
                System.out.println("usage: <input> <output> <numClass>"
                        + " <impurity> <maxDepth> <maxBins> <mode:regression/Classification>");
                    System.exit(0);
            }
            String input = args[0];
                String output = args[1];
                //int numIterations = Integer.parseInt(args[2]);
                Integer numClasses = Integer.parseInt(args[2]);//2;
            
                String impurity = args[3];//"gini";
            Integer maxDepth = Integer.parseInt(args[4]);//5;
            Integer maxBins = Integer.parseInt(args[5]);//100;
            String mode=args[6];
                HashMap<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
                SparkConf conf = new SparkConf().setAppName("DecisionTree classification Example");
                JavaSparkContext sc = new JavaSparkContext(conf);
                
                
                
                
                
                // Load and parse the data file.
                // Cache the data since we will use it again to compute training error.
                long start = System.currentTimeMillis();
                
                //  JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc.sc(), input).toJavaRDD().cache();
                JavaRDD<String> tmpdata = sc.textFile(input);
                
                JavaRDD<LabeledPoint> data = tmpdata.map(
                        new Function<String, LabeledPoint>() {
                            public LabeledPoint call(String line) {                        
                                return LabeledPoint.parse(line);
                            }
                        }
                        );
                double loadTime = (double)(System.currentTimeMillis() - start) / 1000.0;
                // Set parameters.
                //  Empty categoricalFeaturesInfo indicates all features are continuous.
                
                
                // Train a DecisionTree model for classification.
                start = System.currentTimeMillis();
                DecisionTreeModel tmpmodel;
                if(mode.equals("Classification")){
                    tmpmodel = DecisionTree.trainClassifier(data, numClasses,
                            categoricalFeaturesInfo, impurity, maxDepth, maxBins);}
                else{
                    tmpmodel = DecisionTree.trainRegressor(data,
                            categoricalFeaturesInfo, impurity, maxDepth, maxBins);
                }
            double trainingTime = (double)(System.currentTimeMillis() - start) / 1000.0;
                
                start = System.currentTimeMillis();
                final DecisionTreeModel model=tmpmodel;
                // Evaluate model on training instances and compute training error
                JavaPairRDD<Double, Double> predictionAndLabel =
                data.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
                    
                    @Override
                    public Tuple2<Double, Double> call(LabeledPoint p) {
                        return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
                    }
                });
            Double trainErr =
                1.0 * predictionAndLabel.filter(new Function<Tuple2<Double, Double>, Boolean>() {
                    
                    @Override
                    public Boolean call(Tuple2<Double, Double> pl) {
                        return !pl._1().equals(pl._2());
                    }
                }).count() / data.count();
            double testTime = (double)(System.currentTimeMillis() - start) / 1000.0;
                
                System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f}\n", loadTime, trainingTime, testTime);
                //System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f,\"saveTime\":%.3f}\n", loadTime, trainingTime, testTime, saveTime);
                System.out.println("Training error: " + trainErr);
                System.out.println("Learned classification tree model:\n" + model);
                sc.stop();
        }
}
