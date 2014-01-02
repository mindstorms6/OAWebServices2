package org.bdawg.helpers;

import java.util.List;

import org.bdawg.controllers.SingletonManager;
import org.bdawg.dbObjects.Client;
import org.bdawg.open_audio.Utils.OAConstants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

public class UserHelper {

	public static List<Client> getClientsForUser(String userId) throws Exception{
		if (userId.equals(OAConstants.NOT_OWNED_STRING)){
			return null;
		}
		Client temp = new Client();
		temp.setUserId(userId);
		DynamoDBQueryExpression<Client> q = new DynamoDBQueryExpression<Client>().withHashKeyValues(temp);
		PaginatedQueryList<Client> found = SingletonManager.getMapper().query(Client.class, q);
		return found;
		
		
	}
}
