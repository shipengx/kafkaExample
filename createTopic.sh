#!/bin/bash
#
#

kafka-topics --create --zookeeper frak6:2181 --replication-factor 3 --partitions 1 --topic shipeng_kafka1



