package com.shipeng.Twitter;


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

    JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
      @Override
      public String call(Tuple2<String, String> tuple2) {
        return tuple2._2();
      }
    });

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
    jssc.start();
    jssc.awaitTermination();

  }//end main()

}//end class TwitterSparkStreamingConsumer



