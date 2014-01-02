package org.bdawg.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bdawg.dbObjects.Client;
import org.bdawg.helpers.ClientHelper;
import org.bdawg.webObjects.HBResponse;
import org.bdawg.webObjects.HearbeatObject;


@Path("/clients")
public class ClientsResource {
	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/hb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response heartbeat(HearbeatObject hb) throws Exception{
		Client resp = ClientHelper.updateOrcreate(hb);
		HBResponse respHB = new HBResponse();
		respHB.setOwner(resp.getUserId());
		return Response.ok().entity(respHB).build();
	}
	
	
}
