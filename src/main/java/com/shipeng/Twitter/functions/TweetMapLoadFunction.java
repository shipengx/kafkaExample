package com.shipeng.Twitter.functions;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class TweetMapLoadFunction implements Function<String, Row> {

    @Override
    public Row call(String arg0) throws Exception {
        String[] split = arg0.split(",,,,");
        return  RowFactory.create(    
                                      Double.parseDouble(split[0]),   //longitude
                                      Double.parseDouble(split[1]),   //latitude
                                      Long.parseLong(split[2]),       //timestamp
                                      Long.parseLong(split[3]),       //userId
                                      split[4]                        //tweet message

                ); 
    }

}






