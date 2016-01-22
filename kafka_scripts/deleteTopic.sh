#!/bin/bash
#
#

topic=$1
if [ $# -eq 0 ]; 
    then
        echo "No arguments supplied"
        echo "Usage: ./deleteTopic.sh <topic name>"
else 
    echo "trying to delete topic: "$topic
    kafka-topics --delete --zookeeper frak6:2181 --topic $topic 
fi




