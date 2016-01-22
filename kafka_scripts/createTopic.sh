#!/bin/bash
#
#

topic=$1
if [ $# -eq 0 ]; 
    then
        echo "No arguments supplied"
        echo "Usage: ./createTopic.sh <topic name>"
else 
    echo "creating topic: "$topic
    kafka-topics --create --zookeeper frak6:2181 --replication-factor 3 --partitions 1 --topic $topic
fi







