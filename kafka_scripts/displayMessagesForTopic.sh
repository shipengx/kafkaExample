#!/bin/bash
#
#

topic=$1
if [ $# -eq 0 ]; 
    then
        echo "No arguments supplied"
        echo "Usage: ./displayMessagesForTopic.sh <topic name>"
else 
    echo "display messages for topic: "$topic
    kafka-console-consumer --zookeeper frak6:2181 --topic $topic --from-beginning
fi





