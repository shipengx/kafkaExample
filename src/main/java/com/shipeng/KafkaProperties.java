package com.shipeng;


public interface KafkaProperties {

  final static String zkConnect = "frak6:2181";
  final static  String groupId = "group1";
  final static String topic = "shipeng_kafka1";
  final static String kafkaServerURL = "frak6";
  final static int kafkaServerPort = 9092;
  final static int kafkaProducerBufferSize = 64*1024;
  final static int connectionTimeOut = 100000;
  final static int reconnectInterval = 10000;
  final static String topic2 = "shipeng_kafka2";
  final static String topic3 = "shipeng_kafka3";
  final static String clientId = "SimpleConsumerDemoClient";
}




