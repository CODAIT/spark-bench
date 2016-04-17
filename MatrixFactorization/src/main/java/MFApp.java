/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author limin
 */
package MatrixFactorization.src.main.java;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import scala.Tuple2;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.SparkConf;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class MFApp {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("usage: <input> <output> <rank> <maxIterations> <lambda> <storageLevel>");
            System.exit(0);
        }
        String input = args[0];
        String output = args[1];
        int rank = Integer.parseInt(args[2]);
        int numIterations = Integer.parseInt(args[3]);
        double lambda = Double.parseDouble(args[4]);
        String storage_level = args[5];
        
        Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        
        SparkConf conf = new SparkConf().setAppName("MFApp Example");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
    // Load and parse the data
	long start = System.currentTimeMillis();
        JavaRDD<String> data = sc.textFile(input);
        JavaRDD<Rating> ratings = data.map(
                new Function<String, Rating>() {
                    public Rating call(String s) {
                        String[] sarray = s.split(",");
                        return new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]),
                                Double.parseDouble(sarray[2]));
                    }
                }
        );
        
        if (storage_level.equals("MEMORY_AND_DISK_SER")) {
            ratings.persist(StorageLevel.MEMORY_AND_DISK_SER());
        }else if (storage_level.equals("MEMORY_AND_DISK")) {
            ratings.persist(StorageLevel.MEMORY_AND_DISK());
        } else {
            ratings.cache();

        }
        

        RDD<Rating> parsed_data = JavaRDD.toRDD(ratings);

        if (storage_level.equals("MEMORY_AND_DISK_SER")) {
            parsed_data.persist(StorageLevel.MEMORY_AND_DISK_SER());
        }else if (storage_level.equals("MEMORY_AND_DISK")) {
            parsed_data.persist(StorageLevel.MEMORY_AND_DISK());
        }         else {
            parsed_data.cache();

        }
	double loadTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        // Build the recommendation model using ALS        
	start = System.currentTimeMillis();
        MatrixFactorizationModel model = ALS.train(parsed_data, rank, numIterations, lambda);
	double trainingTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        // Evaluate the model on rating data
	start = System.currentTimeMillis();
        JavaRDD<Tuple2<Object, Object>> userProducts = ratings.map(
                new Function<Rating, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(Rating r) {
                        return new Tuple2<Object, Object>(r.user(), r.product());
                    }
                }
        );
        
        if (storage_level.equals("MEMORY_AND_DISK_SER")) {
            userProducts.persist(StorageLevel.MEMORY_AND_DISK_SER());
        }else if (storage_level.equals("MEMORY_AND_DISK")) {
            userProducts.persist(StorageLevel.MEMORY_AND_DISK());
        } else {
            userProducts.cache();

        }
        
        JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
                model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
                        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
                            public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r) {
                                return new Tuple2<Tuple2<Integer, Integer>, Double>(
                                        new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
                            }
                        }
                ));
        JavaRDD<Tuple2<Double, Double>> ratesAndPreds
                = JavaPairRDD.fromJavaRDD(ratings.map(
                                new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
                                    public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r) {
                                        return new Tuple2<Tuple2<Integer, Integer>, Double>(
                                                new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
                                    }
                                }
                        )).join(predictions).values();
        
        
        double MSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(
                new Function<Tuple2<Double, Double>, Object>() {
                    public Object call(Tuple2<Double, Double> pair) {
                        Double err = pair._1() - pair._2();
                        return err * err;
                    }
                }
        ).rdd()).mean();
	double testTime = (double)(System.currentTimeMillis() - start) / 1000.0;

        System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f}\n", loadTime, trainingTime, testTime);
        //System.out.printf("{\"loadTime\":%.3f,\"trainingTime\":%.3f,\"testTime\":%.3f,\"saveTime\":%.3f}\n", loadTime, trainingTime, testTime, saveTime);
        System.out.println("Mean Squared Error = " + MSE);
        sc.stop();
    }
}
