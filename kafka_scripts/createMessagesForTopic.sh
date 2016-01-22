#!/bin/bash
#
#

topic=$1
if [ $# -eq 0 ]; 
    then
        echo "No arguments supplied"
        echo "Usage: ./createMessagesForTopic.sh <topic name>"
else 
    echo "create messages for topic: "$topic
    kafka-console-producer --broker-list frak6:9092 --topic $topic
fi





