package org.bdawg.dbObjects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=Client.TABLE_NAME)
public class Client {

	public static final String TABLE_NAME = "OAClients";
	public static final String USER_ID_KEY = "user_id";
	public static final String CLIENT_ID_KEY = "client_id";
	public static final String LAST_HB_KEY = "last_hb";
	public static final String LAST_HB_INDEX_NAME = "last_hb-index";
	public static final String CLIENT_ID_INDEX = "client_id-user_id-index";
	public static final String NAME_KEY = "name";
	
	private String userId;
	private String clientId;
	private long lastHB;
	private String name;
	
	@DynamoDBHashKey(attributeName=USER_ID_KEY)
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBRangeKey(attributeName=CLIENT_ID_KEY)
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	@DynamoDBIndexRangeKey(attributeName=LAST_HB_KEY, localSecondaryIndexName=LAST_HB_INDEX_NAME)
	public long getLastHB() {
		return lastHB;
	}
	public void setLastHB(long lastHB) {
		this.lastHB = lastHB;
	}
	@DynamoDBAttribute(attributeName=NAME_KEY)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
