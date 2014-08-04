package org.bdawg.controllers;

import java.io.IOException;

import org.bdawg.exceptions.SingletonInitException;
import org.bdawg.mqtt.MQTTManager;
import org.bdawg.torrent_tracker.TrackerManager;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;

/**
 * Created by breland on 12/19/13.
 */
public class SingletonManager {

    private static boolean inited = false;
    private static SingletonManager instance;
    private DynamoDBMapper daMapper;
    private AmazonDynamoDB daDynamoClient;
    private AWSCredentials daCreds;
    private DynamoDBMapperConfig config;
    private TrackerManager tracker;
    private MQTTManager mqtt;
    private AmazonS3Client s3Client;
    private AmazonSNSClient snsClient;
    
    private SingletonManager() throws SingletonInitException{
    	
        this.daCreds = new BasicAWSCredentials(System.getProperty("AWS_ACCESS_KEY_ID"), System.getProperty("AWS_SECRET_KEY"));
        this.daDynamoClient = new AmazonDynamoDBClient(daCreds);
        this.config = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES, DynamoDBMapperConfig.ConsistentReads.CONSISTENT, null);
        this.daMapper = new DynamoDBMapper(daDynamoClient,this.config);
        this.s3Client = new AmazonS3Client(daCreds);
        this.snsClient = new AmazonSNSClient(daCreds);
        snsClient.setEndpoint("sns.us-east-1.amazonaws.com");

        try {
			this.tracker = new TrackerManager();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new SingletonInitException(e);
		}
        this.tracker.getTracker().start();
        this.mqtt = MQTTManager.getMQInstance();
        inited=true;
    }
    
    static {
    	try {
			getTracker();
		} catch (SingletonInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private static void checkInit() throws SingletonInitException{
        if (!inited || instance == null){
            instance = new SingletonManager();
        }
    }
    
    public static AmazonDynamoDB getDynamoClient() throws SingletonInitException{
    	checkInit();
    	return instance.daDynamoClient;
    }

    public static DynamoDBMapper getMapper() throws SingletonInitException{
        checkInit();
        return instance.daMapper;
    }
    
    public static TrackerManager getTracker() throws SingletonInitException{
    	checkInit();
    	return instance.tracker;
    }
    
    public static MQTTManager getMQTT() throws SingletonInitException{
    	checkInit();
    	return instance.mqtt;
    }
    
    public static AmazonS3Client getS3Client() throws SingletonInitException{
    	checkInit();
    	return instance.s3Client;
    }
    
    public static AmazonSNSClient getSNSClient() throws SingletonInitException{
    	checkInit();
    	return instance.snsClient;
    }
}

