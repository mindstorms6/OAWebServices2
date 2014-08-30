package org.bdawg.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/health")
public class ELBHealthyResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHealth() throws Exception {
		// TODO:Add actual health checks here.
		return Response.ok().build();
	}
}
