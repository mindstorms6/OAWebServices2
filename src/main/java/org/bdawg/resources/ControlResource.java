package org.bdawg.resources;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bdawg.controllers.SingletonManager;
import org.bdawg.dbObjects.Client;
import org.bdawg.dbObjects.Playable;
import org.bdawg.helpers.UserHelper;
import org.bdawg.open_audio.OpenAudioProtos.ClientCommand;
import org.bdawg.open_audio.OpenAudioProtos.ClientCommand.ClientAction;
import org.bdawg.open_audio.OpenAudioProtos.MasterCommand;
import org.bdawg.open_audio.OpenAudioProtos.MasterCommand.MasterAction;
import org.bdawg.open_audio.OpenAudioProtos.MasterPlayable;
import org.bdawg.open_audio.Utils;
import org.bdawg.open_audio.Utils.OAConstants;
import org.bdawg.webObjects.PlaybackObject;
import org.bdawg.webObjects.VolumeObject;

@Path("/control")
public class ControlResource {

	final String baseTopic = OAConstants.BASE_TOPIC;
	final String masterTopic = "/master";

	@Path("/play")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response play(PlaybackObject toPlay) throws Exception {
		UUID newUUID = UUID.randomUUID();
		Client mostRecentHeardFrom = null;
		List<Client> allClients = UserHelper.getClientsForUser(toPlay
				.getUserId());
		Map<String, Client> allClientsMap = new HashMap<String,Client>();
		for (Client ca : allClients) {
			allClientsMap.put(ca.getClientId(),ca);
		}
		
		for (String clientId : toPlay.getClientIds()) {
			if (!allClientsMap.containsKey(clientId)) {
				return Response.status(Status.BAD_REQUEST).build();
			} else {
				Client eligible = allClientsMap.get(clientId);
				if (mostRecentHeardFrom == null) {
					mostRecentHeardFrom = eligible;
				} else {
					if (eligible.getLastHB() > mostRecentHeardFrom.getLastHB()) {
						mostRecentHeardFrom = eligible;
					}
				}
			}
		}
		this.stopAll(toPlay.getClientIds());

		Playable pb = new Playable();
		pb.setClientsToPlayOn(toPlay.getClientIds());
		pb.setMasterClientId(mostRecentHeardFrom.getClientId());
		pb.setItemId(newUUID);
		pb.setPlayableType(toPlay.getPlayableType());
		pb.setMeta(toPlay.getMeta());
		SingletonManager.getMapper().save(pb);

		MasterPlayable.Builder mp = MasterPlayable.newBuilder();
		mp.setId(newUUID.toString());
		mp.setPlaybackType(toPlay.getPlayableType());
		mp.addAllMeta(Utils.mapToKVList(toPlay.getMeta()));
		mp.addAllClientId(toPlay.getClientIds());

		MasterCommand.Builder mc = MasterCommand.newBuilder().setMasterAction(
				MasterAction.NEW_PLAYABLE).setPlayable(mp);

		ByteBuffer toSendMQTT = ByteBuffer.wrap(mc.build().toByteArray());
		SingletonManager.getMQTT().sendMessage(toSendMQTT,
				baseTopic + pb.getMasterClientId() + masterTopic);

		return Response.ok().build();
	}

	@Path("/pause/{playbackId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response pause(@NotNull @PathParam("playbackId") String playbackId)
			throws Exception {
		long targetTimeStamp = System.currentTimeMillis() + 350;
		Playable p = SingletonManager.getMapper().load(Playable.class,
				playbackId);

		ClientCommand builder = ClientCommand.newBuilder()
				.setClientAction(ClientAction.PAUSE)
				.setTimestamp(targetTimeStamp).build();
		ByteBuffer toSend = ByteBuffer.wrap(builder.toByteArray());
		for (String clientId : p.getClientsToPlayOn()) {
			SingletonManager.getMQTT()
					.sendMessage(toSend, baseTopic + clientId);
		}
		return Response.ok().build();

	}

	@Path("/volume")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setVolume(VolumeObject toSet) throws Exception {

		ClientCommand builder = ClientCommand.newBuilder()
				.setClientAction(ClientAction.VOLUME)
				.setNewVolume(toSet.getNewVolume()).build();

		List<Client> allClients = UserHelper.getClientsForUser(toSet
				.getUserId());
		Set<String> allClientIds = new HashSet<String>();
		for (Client ca : allClients) {
			allClientIds.add(ca.getClientId());
		}
		for (String client : toSet.getClientIds()) {
			if (!allClientIds.contains(client)) {
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		ByteBuffer toSend = ByteBuffer.wrap(builder.toByteArray());
		for (String clientId : toSet.getClientIds()) {
			SingletonManager.getMQTT()
					.sendMessage(toSend, baseTopic + clientId);
		}
		return Response.ok().build();
	}

	private void stopAll(Collection<String> clients) throws Exception {
		ClientCommand builder = ClientCommand.newBuilder()
				.setClientAction(ClientAction.STOP)
				.setTimestamp(System.currentTimeMillis()).build();
		ByteBuffer toSend = ByteBuffer.wrap(builder.toByteArray());
		for (String clientId : clients) {
			SingletonManager.getMQTT()
					.sendMessage(toSend, baseTopic + clientId);
		}
	}
}