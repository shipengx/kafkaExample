#!/bin/bash
#
#

spark-submit --master yarn-cluster   --class com.shipeng.Twitter.TwitterSparkConsumer   ./target/kafkaExample-1.0-SNAPSHOT-jar-with-dependencies.jar   frak4,frak5,frak6  my-consumer-group  kafka_twitter1 1


