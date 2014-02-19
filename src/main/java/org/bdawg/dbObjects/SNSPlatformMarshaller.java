package org.bdawg.dbObjects;

import org.bdawg.helpers.SNSHelper.SNSPlatform;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

public class SNSPlatformMarshaller implements DynamoDBMarshaller<SNSPlatform> {

	@Override
	public String marshall(SNSPlatform arg0) {
		return arg0.toString();

	}

	@Override
	public SNSPlatform unmarshall(Class<SNSPlatform> arg0, String arg1) {
		return SNSPlatform.valueOf(arg1);
	}

}
