package org.bdawg.resources;

import java.io.ByteArrayInputStream;

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

import org.apache.commons.io.IOUtils;
import org.bdawg.controllers.SingletonManager;
import org.bdawg.webObjects.ByteWrapper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.turn.ttorrent.tracker.TrackedTorrent;

@Path("/files")
public class FileResource {

	private static final String torrentBucket = "oa_torrents";

	@GET
	@Path("/{fileId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFile(@NotNull @PathParam("fileId") String fileId)
			throws Exception {
		try {
			S3Object tr = SingletonManager.getS3Client().getObject(
					torrentBucket, fileId);
			ByteWrapper w = new ByteWrapper();
			w.setName(fileId);
			w.setMimeType(tr.getObjectMetadata().getContentType());
			w.setData(IOUtils.toByteArray(tr.getObjectContent()));
			SingletonManager.getTracker().getTracker().announce(new TrackedTorrent(w.getData()));
			return Response.ok().entity(w).build();
		} catch (AmazonClientException clientEx) {
			return Response.status(Status.NOT_FOUND).build();
		}

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putFile(ByteWrapper wrapper) throws Exception {
		ObjectMetadata md = new ObjectMetadata();
		md.setContentType(wrapper.getMimeType());
		PutObjectRequest rq = new PutObjectRequest(torrentBucket,
				wrapper.getName(), new ByteArrayInputStream(wrapper.getData()),
				md);
		try {
			SingletonManager.getS3Client().putObject(rq);
			SingletonManager.getTracker().getTracker().announce(new TrackedTorrent(wrapper.getData()));
			return Response.ok().build();
		} catch (AmazonClientException ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
}
