package org.bdawg.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.bdawg.webObjects.TimeObject;

@Path("/time")
public class TimeResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTime(){
		long tickStart = System.nanoTime();
		TimeObject t = new TimeObject();
		long timeToReturn = System.currentTimeMillis();
		t.setMilliTime(timeToReturn);
		CacheControl c = new CacheControl();
		c.setMustRevalidate(true);
		c.setNoCache(true);
		ResponseBuilder r = javax.ws.rs.core.Response.ok(t);
		r.cacheControl(c);
		r.header("time", timeToReturn);
		t.setProcessDelay(System.nanoTime() - tickStart);
		r.build();
		return r.build();
	}
}

