package org.bdawg.helpers;

import java.util.HashMap;
import java.util.Map;

import org.bdawg.controllers.SingletonManager;
import org.bdawg.dbObjects.Client;
import org.bdawg.exceptions.AlreadyOwnedException;
import org.bdawg.exceptions.ClientNotFoundException;
import org.bdawg.exceptions.SingletonInitException;
import org.bdawg.open_audio.Utils.OAConstants;
import org.bdawg.webObjects.HearbeatObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudfront.model.InvalidArgumentException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class ClientHelper {

	public static Client updateOrcreate(HearbeatObject incoming)
			throws AmazonServiceException, AmazonClientException, SingletonInitException {

		Map<String, Condition> conds = new HashMap<String, Condition>();
		conds.put(
				Client.CLIENT_ID_KEY,
				new Condition().withAttributeValueList(
						new AttributeValue().withS(incoming.getClientId()))
						.withComparisonOperator(ComparisonOperator.EQ));

		QueryRequest r = new QueryRequest(Client.TABLE_NAME)
				.withIndexName(Client.CLIENT_ID_INDEX)
				.withKeyConditions(conds)
				.withAttributesToGet(Client.CLIENT_ID_KEY, Client.USER_ID_KEY,
						Client.NAME_KEY).withLimit(1);

		QueryResult result = SingletonManager.getDynamoClient().query(r);

		Client lookup = null;
		if (result.getCount() == 0) {
			// Not found, make a new!
			lookup = new Client();
			lookup.setUserId(OAConstants.NOT_OWNED_STRING);
			lookup.setName(OAConstants.NOT_OWNED_STRING);
			lookup.setClientId(incoming.getClientId());
		} else {
			// Already there, just write new field
			Map<String, AttributeValue> fromDB = result.getItems().get(0);
			lookup = new Client();
			lookup.setClientId(fromDB.get(Client.CLIENT_ID_KEY).getS());
			lookup.setUserId(fromDB.get(Client.USER_ID_KEY).getS());
			lookup.setName(fromDB.get(Client.NAME_KEY).getS());
		}

		lookup.setLastHB(System.currentTimeMillis());
		SingletonManager.getMapper().save(lookup);
		return lookup;
	}

	public static Client claimClient(String userId, String clientId, String name) throws AmazonServiceException, AmazonClientException, SingletonInitException, ClientNotFoundException, AlreadyOwnedException {
		if (userId.equals(OAConstants.NOT_OWNED_STRING)) {
			throw new InvalidArgumentException("Bad userId");
		}
		Map<String, Condition> conds = new HashMap<String, Condition>();
		conds.put(
				Client.CLIENT_ID_KEY,
				new Condition().withAttributeValueList(
						new AttributeValue().withS(clientId))
						.withComparisonOperator(ComparisonOperator.EQ));

		QueryRequest r = new QueryRequest(Client.TABLE_NAME)
				.withIndexName(Client.CLIENT_ID_INDEX)
				.withKeyConditions(conds)
				.withAttributesToGet(Client.CLIENT_ID_KEY, Client.USER_ID_KEY,
						Client.NAME_KEY).withLimit(1);

		QueryResult result = SingletonManager.getDynamoClient().query(r);

		Client lookup = null;
		if (result.getCount() == 0) {
			throw new ClientNotFoundException();
		} else {
			// Already there, just write new field
			Map<String, AttributeValue> fromDB = result.getItems().get(0);
			String eUser = fromDB.get(Client.USER_ID_KEY).getS();
			if (OAConstants.NOT_OWNED_STRING.equals(eUser)){
				lookup = new Client();
				lookup.setClientId(fromDB.get(Client.CLIENT_ID_KEY).getS());
				lookup.setUserId(userId);
				lookup.setName(name);
				SingletonManager.getMapper().save(lookup);
				Client toDel = new Client();
				toDel.setClientId(clientId);
				toDel.setUserId(OAConstants.NOT_OWNED_STRING);
				SingletonManager.getMapper().delete(toDel);
				return lookup;
			} else {
				throw new AlreadyOwnedException();
			}
		}

	}
}
