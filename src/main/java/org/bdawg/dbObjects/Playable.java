package org.bdawg.dbObjects;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=Playable.TABLE_NAME)
public class Playable {
	public static final String TABLE_NAME = "OAPlayables";
	public static final String HASH_KEY = "item_id";
	
	
	private UUID itemId;
	private Set<String> clientsToPlayOn;
	private String masterClientId;
	private String playableType;
	private Map<String, String> meta;
	private String userIdStartedBy;
	
	public Playable(){
		
	}

	@DynamoDBIgnore
	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}
	
	@DynamoDBHashKey(attributeName=HASH_KEY)
	public String getItemIdString(){
		return this.itemId.toString();
	}
	
	public void setItemIdString(String id){
		this.itemId = UUID.fromString(id);
	}	

	@DynamoDBAttribute
	public Set<String> getClientsToPlayOn() {
		return clientsToPlayOn;
	}

	public void setClientsToPlayOn(Set<String> clientsToPlayOn) {
		this.clientsToPlayOn = clientsToPlayOn;
	}
	
	@DynamoDBAttribute
	public String getMasterClientId() {
		return masterClientId;
	}

	public void setMasterClientId(String masterClientId) {
		this.masterClientId = masterClientId;
	}
	
	@DynamoDBAttribute
	public String getPlayableType() {
		return playableType;
	}

	public void setPlayableType(String playableType) {
		this.playableType = playableType;
	}
	
	@DynamoDBAttribute
	@DynamoDBMarshalling(marshallerClass=MapSerializer.class)
	public Map<String, String> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}

	@DynamoDBAttribute
	public String getUserIdStartedBy() {
		return userIdStartedBy;
	}

	public void setUserIdStartedBy(String userIdStartedBy) {
		this.userIdStartedBy = userIdStartedBy;
	}
	
	
}
