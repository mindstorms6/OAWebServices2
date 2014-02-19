package org.bdawg.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bdawg.controllers.SingletonManager;
import org.bdawg.dbObjects.Client;
import org.bdawg.dbObjects.Playable;
import org.bdawg.exceptions.SingletonInitException;
import org.bdawg.helpers.SNSHelper.SNSPlatform;
import org.bdawg.open_audio.Utils.OAConstants;
import org.bdawg.webObjects.PushRegister;

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
	
	public static void registerPushNotifier(PushRegister toRegister) throws Exception{
		org.bdawg.dbObjects.PushRegister toReg = new org.bdawg.dbObjects.PushRegister();
		toReg.setDeviceId(toRegister.getDeviceId());
		toReg.setRegisterId(toRegister.getPushRegId());
		toReg.setUserId(toRegister.getUserId());
		//TODO:make this based on whatever client passes in
		toReg.setType(SNSPlatform.GCM);
		SingletonManager.getMapper().save(toReg);
	}
	
	public static List<org.bdawg.dbObjects.PushRegister> getDeviceRegInfoForPlayableId(String playableId) throws SingletonInitException{
		Playable p = SingletonManager.getMapper().load(Playable.class, playableId);
		String userId = p.getUserIdStartedBy();
		org.bdawg.dbObjects.PushRegister key = new org.bdawg.dbObjects.PushRegister();
		key.setUserId(userId);
		PaginatedQueryList<org.bdawg.dbObjects.PushRegister> fromDB = SingletonManager.getMapper().query(org.bdawg.dbObjects.PushRegister.class, new DynamoDBQueryExpression<org.bdawg.dbObjects.PushRegister>().withHashKeyValues(key));
		fromDB.loadAllResults();
		List<org.bdawg.dbObjects.PushRegister> tr = new ArrayList<org.bdawg.dbObjects.PushRegister>();
		for (org.bdawg.dbObjects.PushRegister pr : fromDB){
			tr.add(pr);
		}
		return tr;
		
	}
}
