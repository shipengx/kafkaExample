package com.shipeng.Twitter;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;
import twitter4j.GeoLocation;



public class TwitterProducer {

    private static final Logger logger = LoggerFactory.getLogger(TwitterProducer.class);
    
    /** Information necessary for accessing the Twitter API */
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    
    /** The actual Twitter stream. It's set up to collect raw JSON data */
    private TwitterStream twitterStream;

    private String topic;
    
    public TwitterProducer(String topic) {
        this.topic = topic;
    }

    private void start() {
    
    /** Producer properties **/
    Properties props = new Properties();
    props.put("metadata.broker.list", "frak6:9092");
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "0");
    props.put("retries","0");
    props.put("retry.backoff.ms","10000");
    //props.put("producer.type","async");


    ProducerConfig config = new ProducerConfig(props);
    
    final Producer<String, String> producer = new Producer<String, String>(config);
    
    /** Twitter properties **/
    consumerKey = "TkASrqKdiPLB4QZZj11bVQftq";
    consumerSecret = "CH4YdJudWsDWPHpke94w6tSJF6IhECMm1OaJ0HVgkTETOzuphf";
    accessToken = "4048871658-l2Of5lEgSdZXOHFXf3Pwz2VnJBJSDdYbnEWmnOM";
    accessTokenSecret = "8gTsAlr0jsDBxNuIvevodcALJz4FLODpcJczLqDf8ahkB";
    
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setOAuthConsumerKey(consumerKey);
    cb.setOAuthConsumerSecret(consumerSecret);
    cb.setOAuthAccessToken(accessToken);
    cb.setOAuthAccessTokenSecret(accessTokenSecret);
    cb.setJSONStoreEnabled(true);
    cb.setIncludeEntitiesEnabled(true);
    
    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
    final Map<String, String> headers = new HashMap<String, String>();
    
    /** Twitter listener **/
    StatusListener listener = new StatusListener() {
        // The onStatus method is executed every time a new tweet comes
        // in.
        public void onStatus(Status status) {
            // The EventBuilder is used to build an event using the
            // the raw JSON of a tweet
            //logger.info(status.getUser().getScreenName() + ": " + status.getText());
            
            //KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, status.getText());
            //KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, DataObjectFactory.getRawJSON(status));
            //producer.send(data);
            
            GeoLocation geoLocation = status.getGeoLocation();
            if (geoLocation != null) {
                String text = status.getText().replaceAll("[\r\n]", " ");
                String line = geoLocation.getLongitude()+","+geoLocation.getLatitude()+","+status.getCreatedAt().getTime()+","+status.getUser().getId()+","+text;
                System.out.println("Message is: " + line);
                KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, line);
                producer.send(data);
            }

        }
            
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        
        public void onScrubGeo(long userId, long upToStatusId) {}
        
        public void onException(Exception ex) {
            ex.printStackTrace();
            logger.info("Shutting down Twitter sample stream...");
            //twitterStream.shutdown();
        }
        
        public void onStallWarning(StallWarning warning) {}
        };
    
    /** Bind the listener **/
    twitterStream.addListener(listener);
    /** GOGOGO **/
    twitterStream.sample();   
    }
    
    public static void main(String[] args) {
    try {

        String topic = args[0];
        if (topic == null || topic.length() == 0) {
            System.out.println("please pass a kafka topic as the first argument.");
            return;
        }

        TwitterProducer tp = new TwitterProducer(topic);
        tp.start();
        
    } catch (Exception e) {
        logger.info(e.getMessage());
    }
    
    }

}//end class TwitterProducer




