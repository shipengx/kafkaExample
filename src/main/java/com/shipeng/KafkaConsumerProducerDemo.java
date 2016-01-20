package com.shipeng;

public class KafkaConsumerProducerDemo implements KafkaProperties{

  public static void main(String[] args) {

    final boolean isAsync = args.length > 0 ? !args[0].trim().toLowerCase().equals("sync") : true;
    Producer producerThread = new Producer(KafkaProperties.topic, isAsync);
    producerThread.start();
    Consumer consumerThread = new Consumer(KafkaProperties.topic);
    consumerThread.start();
    
  }
}



