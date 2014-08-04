package org.bdawg.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdawg.controllers.SingletonManager;
import org.bdawg.dbObjects.PushRegister;
import org.bdawg.exceptions.SingletonInitException;
import org.bdawg.webObjects.PlaybackHeartBeat;
import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class SNSHelper {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String gcmAppARN = "arn:aws:sns:us-east-1:224842466274:app/GCM/OpenAudio";
    
    public static enum SNSPlatform {
        // Apple Push Notification Service
        APNS,
        // Sandbox version of Apple Push Notification Service
        APNS_SANDBOX,
        // Amazon Device Messaging
        ADM,
        // Google Cloud Messaging
        GCM
    }
    
    private static CreatePlatformEndpointResult createPlatformEndpoint(
            String customData, String platformToken, String applicationArn) throws SingletonInitException {
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setCustomUserData(customData);
        platformEndpointRequest.setToken(platformToken);
        platformEndpointRequest.setPlatformApplicationArn(applicationArn);
        return SingletonManager.getSNSClient().createPlatformEndpoint(platformEndpointRequest);
    }
    
    private static String getEndpointARN(SNSPlatform sns){
    	switch (sns) {
		case GCM:
			return gcmAppARN;
		default:
			return null;
		}
    }
    
    private static PublishResult publish(String jsonMessage, String regId, SNSPlatform platform) throws SingletonInitException {
        PublishRequest publishRequest = new PublishRequest();
        CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
                "CustomData - Useful to store endpoint specific data", regId, getEndpointARN(platform));
        System.out.println(platformEndpointResult);
        
        Map<String, String> messageMap = new HashMap<String, String>();
        String message;
        messageMap.put(platform.name(), "{\"data\":" + jsonMessage + "}");
        // For direct publish to mobile end points, topicArn is not relevant.
        publishRequest.setTargetArn(platformEndpointResult.getEndpointArn());
        publishRequest.setMessageStructure("json");
        message = jsonify(messageMap);

        // Display the message that will be sent to the endpoint/
        System.out.println(message);

        publishRequest.setMessage(message);
        PublishResult pr = null;
        try {
        	pr = SingletonManager.getSNSClient().publish(publishRequest);
        } catch (Exception ex){
        	ex.printStackTrace();
        }
        return pr;
    }

    private static String jsonify(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw (RuntimeException) e;
        }
    }
    
    public static void pushProgress(PlaybackHeartBeat pbh, SNSPlatform platform, String regId ){
    	Map<String, Object> androidMessageMap = new HashMap<String, Object>();
        androidMessageMap.put("collapse_key", pbh.getItemId());
        androidMessageMap.put("data", jsonify(pbh));
        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", 125);
        androidMessageMap.put("dry_run", false);
        String toPass = jsonify(androidMessageMap);
        try {
			publish(toPass, regId, platform);
		} catch (SingletonInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public static void pushProgress(PlaybackHeartBeat pbh, List<PushRegister> lpr){
    	for (PushRegister pr : lpr){
    		pushProgress(pbh, pr.getType(), pr.getRegisterId());
    	}
    }
    

}
