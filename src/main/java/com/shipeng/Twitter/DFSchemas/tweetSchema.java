package com.shipeng.Twitter.DFSchemas;


import java.util.*;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StructField;

public class tweetSchema {

    public static StructType createTweetStructType() {
        List<StructField> tweetStructFields = new ArrayList<StructField>();

        StructField longitude        = DataTypes.createStructField("longitude", DataTypes.DoubleType, true);
        StructField latitude         = DataTypes.createStructField("latitude", DataTypes.DoubleType, true);
        StructField timestamp        = DataTypes.createStructField("timestamp", DataTypes.LongType, true);
        StructField userId           = DataTypes.createStructField("userId", DataTypes.LongType, true);
        StructField tweetMessage     = DataTypes.createStructField("text", DataTypes.StringType, true);


        tweetStructFields.add(longitude);
        tweetStructFields.add(latitude);
        tweetStructFields.add(timestamp);
        tweetStructFields.add(userId);
        tweetStructFields.add(tweetMessage);

        StructType tweetType = DataTypes.createStructType(tweetStructFields);

        return tweetType;
    }

}//end class tweetSchema


