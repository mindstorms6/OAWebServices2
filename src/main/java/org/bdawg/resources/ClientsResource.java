package org.bdawg.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bdawg.dbObjects.Client;
import org.bdawg.dbObjects.PushRegister;
import org.bdawg.exceptions.SingletonInitException;
import org.bdawg.helpers.ClientHelper;
import org.bdawg.helpers.SNSHelper;
import org.bdawg.helpers.UserHelper;
import org.bdawg.webObjects.HBResponse;
import org.bdawg.webObjects.HearbeatObject;
import org.bdawg.webObjects.PlaybackHeartBeat;


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
		respHB.setManualOffset(resp.getManualOffset());
		return Response.ok().entity(respHB).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/pb_hb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response playback_hb(final PlaybackHeartBeat pbHB) throws Exception{
		Thread pushThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				List<PushRegister> pr;
				try {
					pr = UserHelper.getDeviceRegInfoForPlayableId(pbHB.getItemId());
					SNSHelper.pushProgress(pbHB, pr);
				} catch (SingletonInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		pushThread.start();
		return Response.ok().build();
	}
}
