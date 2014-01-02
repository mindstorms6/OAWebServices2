package org.bdawg.torrent_tracker;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.turn.ttorrent.tracker.Tracker;

public class TrackerManager {

	private Tracker tracker;
	
	public TrackerManager() throws IOException{
		tracker = new Tracker(new InetSocketAddress(6969));
	}
	
	public Tracker getTracker(){
		return this.tracker;
	}

}
