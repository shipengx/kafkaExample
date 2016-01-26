#!/bin/bash
#

mvn clean package

java -cp ./target/kafkaExample-1.0-SNAPSHOT-jar-with-dependencies.jar com.shipeng.KafkaConsumerProducerDemo



