#!/bin/sh
#
#

kafka_topic="kafka_twitter1"
        
spark-submit --master yarn-cluster   --class com.shipeng.Twitter.TwitterSparkStreamingConsumer    ./target/kafkaExample-1.0-SNAPSHOT-jar-with-dependencies.jar   frak4,frak5,frak6  my-consumer-group $kafka_topic 1




