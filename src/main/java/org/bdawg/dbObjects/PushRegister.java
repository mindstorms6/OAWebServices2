package org.bdawg.dbObjects;

import org.bdawg.helpers.SNSHelper.SNSPlatform;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=PushRegister.TABLE_NAME)
public class PushRegister {
	
	public static final String TABLE_NAME = "OAPushRegister";
	public static final String HASH_KEY = "userId";
	public static final String RANGE_KEY = "deviceId";
	public static final String REGISTER_ID_KEY = "registerId";
	
	private String userId;
	private String deviceId;
	private String registerId;
	private SNSPlatform type;
	
	@DynamoDBHashKey(attributeName=HASH_KEY)
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBRangeKey(attributeName=RANGE_KEY)
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@DynamoDBAttribute(attributeName=REGISTER_ID_KEY)
	public String getRegisterId() {
		return registerId;
	}
	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}
	
	@DynamoDBMarshalling(marshallerClass=SNSPlatformMarshaller.class)
	public SNSPlatform getType() {
		return type;
	}
	public void setType(SNSPlatform type) {
		this.type = type;
	}

}
