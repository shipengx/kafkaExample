package com.shipeng.Twitter;

import com.shipeng.Twitter.DFSchemas.*;
import com.shipeng.Twitter.functions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

import scala.Tuple2;
import com.google.common.collect.Lists;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.api.java.StorageLevels;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SaveMode;

import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/*
 * consumes messages from one or more topics in kafka and does wordCount.
 * 
 * Usage: TwitterSparkConsumer <zkQuorum> <group> <topics> <numThreads>
 * <zkQuorum> is a list of one or more zooKeeper servers that make quorum
 * <group> is the name of kafka consumer group
 * <topics> is a list of one or more kafka topics to consume from 
 * <numThreads> is the number of threads the kafka consumer should use.
 *
 * to run this example:
 * spark-submit --master yarn-cluster TwitterSparkStreamingConsumer   frak4,frak5,frak6  my-consumer-group  kafka_twitter1 1
 *
 */


public class TwitterSparkStreamingConsumer {

  private static final Pattern SPACE = Pattern.compile(" ");

  public static void main(String[] args) {

    if (args.length < 4) {
      System.err.println("Usage: TwitterSparkStreamingConsumer <zkQuorum> <group> <topics> <numThreads>");
      System.exit(1);
    }

    SparkConf sparkConf = new SparkConf().setAppName("TwitterKafkaSparkStreaming");
    // Create the context with 2 seconds batch size
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(2000));

    int numThreads = Integer.parseInt(args[3]);
    Map<String, Integer> topicMap = new HashMap<String, Integer>();
    String[] topics = args[2].split(",");
    for (String topic: topics) {
      topicMap.put(topic, numThreads);
    }

    JavaPairReceiverInputDStream<String, String> messages =
            KafkaUtils.createStream(jssc, args[0], args[1], topicMap);

    JavaDStream<String> statuses = messages.map(new Function<Tuple2<String, String>, String>() {
      @Override
      public String call(Tuple2<String, String> tuple2) {
        return tuple2._2();
      }
    });

    statuses.print();


    // Convert RDDs of the statuses DStream to DataFrame and run SQL query
    statuses.foreachRDD(new Function2<JavaRDD<String>, Time, Void>() {
        @Override
        public Void call(JavaRDD<String> rdd, Time time) {
            SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
            sqlContext.setConf("spark.sql.tungsten.enabled", "false");

            JavaRDD<Row> tweetRowRDD = rdd.map(new TweetMapLoadFunction());

            DataFrame statusesDataFrame = sqlContext.createDataFrame(tweetRowRDD, tweetSchema.createTweetStructType());
            statusesDataFrame.show();
            /*
            if (statusesDataFrame.count() > 0) {
                //persist tweet status into the database.
                statusesDataFrame.write().
                    mode(SaveMode.Append).
                    format("com.objy.spark.sql").
                    option("objy.BootFilePath",bootFile+"lh4.boot").
                    option("objy.DataClassName", "Tweet").save();
            }
            */
            return null;
        }
    });

    /*
    JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
      @Override
      public Iterable<String> call(String x) {
        return Lists.newArrayList(SPACE.split(x));
      }
    });

    JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
      new PairFunction<String, String, Integer>() {
        @Override
        public Tuple2<String, Integer> call(String s) {
          return new Tuple2<String, Integer>(s, 1);
        }
      }).reduceByKey(new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer i1, Integer i2) {
          return i1 + i2;
        }
      });

    wordCounts.print();
    */


    jssc.start();
    jssc.awaitTermination();

  }//end main()

}//end class TwitterSparkStreamingConsumer



/** lazily instantiated singleton instance of SQLContext */
class JavaSQLContextSingleton {
    private static transient SQLContext instance = null;
    public static SQLContext getInstance(SparkContext sparkContext) {
        if (instance == null) {
            instance = new SQLContext(sparkContext);
        }
        return instance;
    }
}




