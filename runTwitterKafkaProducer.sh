#!/bin/bash
#
#

kafka_topic="kafka_twitter1"

java -cp ./target/kafkaExample-1.0-SNAPSHOT-jar-with-dependencies.jar com.shipeng.Twitter.TwitterProducer  $kafka_topic


