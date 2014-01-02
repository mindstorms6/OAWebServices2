package org.bdawg.resources;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bdawg.dbObjects.Client;
import org.bdawg.exceptions.AlreadyOwnedException;
import org.bdawg.exceptions.ClientNotFoundException;
import org.bdawg.exceptions.SingletonInitException;
import org.bdawg.helpers.ClientHelper;
import org.bdawg.helpers.UserHelper;
import org.bdawg.webObjects.ClaimObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

@Path("/users")
public class UserResource {

	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResponse(@NotNull @PathParam("userId") String userId) throws Exception{
		List<Client> tr = null;
		tr = UserHelper.getClientsForUser(userId);
		if (tr == null){
			return Response.status(Status.NOT_FOUND).build();
		} else {
			Client[] array = new Client[tr.size()];
			tr.toArray(array); // fill the array
			return Response.ok(array).build();
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response claimDevice(ClaimObject toClaim) throws AmazonServiceException, AmazonClientException, SingletonInitException{
		try {
			Client claimed = ClientHelper.claimClient(toClaim.getUserId(), toClaim.getClientId(), toClaim.getName());
			return Response.ok(claimed).build();
		} catch (ClientNotFoundException e) {
			// TODO Auto-generated catch block
			return Response.status(Status.NOT_FOUND).build();
		} catch (AlreadyOwnedException e) {
			// TODO Auto-generated catch block
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
