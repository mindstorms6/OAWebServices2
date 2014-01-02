package org.bdawg.webObjects;

import java.util.Set;

public class VolumeObject {
	
	private int newVolume;
	private Set<String> clientIds;
	private String userId;
	
	public VolumeObject(){
		
	}

	public int getNewVolume() {
		return newVolume;
	}

	public void setNewVolume(int newVolume) {
		this.newVolume = newVolume;
	}

	public Set<String> getClientIds() {
		return clientIds;
	}

	public void setClientIds(Set<String> clientIds) {
		this.clientIds = clientIds;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
