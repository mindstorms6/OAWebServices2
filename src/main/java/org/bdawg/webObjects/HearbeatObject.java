package org.bdawg.webObjects;

public class HearbeatObject {

	private String clientId;
	private long timestampClient;
	private long timestampServer;
	private boolean initHeartbeat;
	
	public HearbeatObject(){
		
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public long getTimestampClient() {
		return timestampClient;
	}
	public void setTimestampClient(long timestampClient) {
		this.timestampClient = timestampClient;
	}
	public long getTimestampServer() {
		return timestampServer;
	}
	public void setTimestampServer(long timestampServer) {
		this.timestampServer = timestampServer;
	}
	public boolean isInitHeartbeat() {
		return initHeartbeat;
	}
	public void setInitHeartbeat(boolean initHeartbeat) {
		this.initHeartbeat = initHeartbeat;
	}
}
