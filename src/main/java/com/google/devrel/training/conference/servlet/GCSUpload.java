package com.google.devrel.training.conference.servlet;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.util.ByteStreams;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.devrel.training.conference.domain.Jsonifiable;
import com.google.devrel.training.conference.domain.Photo;
import com.google.devrel.training.conference.domain.Profile;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;


public class GCSUpload extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(
			GCSUpload.class.getName());	
	

	/**
	 * MIME type to use when sending responses back to PhotoHunt clients.
	 */
	public static final String JSON_MIMETYPE = "application/json";
	
	/**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    private final ServletFileUpload upload = new ServletFileUpload();
    
    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
        .initialRetryDelayMillis(10)
        .retryMaxAttempts(10)
        .totalRetryPeriodMillis(15000)
        .build());

    /**
     * Writes the payload of the incoming post as the contents of a file to GCS.
     * If the request path is /gcs/Foo/Bar this will be interpreted as
     * a request to create a GCS file named Bar in bucket Foo.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.log(Level.INFO, String.format("GCS UPLOAD " +  req.getParameter("websafeJobKey")));
        
        GcsFilename gcsFileName = this.getFileName(req);	

        GcsFileOptions options = new GcsFileOptions.Builder()
                .mimeType("image/jpg")
                .acl("project-private")
                .addUserMetadata("myfield1", "my field value")
                .build();

        GcsOutputChannel writeChannel = gcsService.createOrReplace(gcsFileName, options);        

        try {
            FileItemIterator iterator = upload.getItemIterator(req);

                while (iterator.hasNext()) {
                    FileItemStream item = iterator.next();
                    InputStream stream = item.openStream();

                    if (item.isFormField()) {
                        LOG.log(Level.INFO, String.format("Champs texte avec id: " + item.getFieldName()+", et nom: "));
                    } else {
                    	LOG.log(Level.INFO, String.format("Nous avons un fichier Ã  uploader : " + item.getFieldName() + ", appelÃ© = " + item.getName()));

                      // You now have the filename (item.getName() and the
                      // contents (which you can read from stream). Here we just
                      // print them back out to the servlet output stream, but you
                      // will probably want to do something more interesting (for
                      // example, wrap them in a Blob and commit them to the
                      // datastore).
                      // Open a channel to write to it
                      byte[] bytes = com.google.common.io.ByteStreams.toByteArray(stream);

                      try {
                            writeChannel.write(ByteBuffer.wrap(bytes));
                      } finally {
                            writeChannel.close();
                            stream.close();
                      }        
                    }        
              }
            } catch (FileUploadException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	
	     String imageKey = "/gs/" + gcsFileName.getBucketName() + "/" + gcsFileName.getObjectName();
	     
	     LOG.log(Level.INFO, String.format("Image key = " + imageKey.toString()));    
	    
	    // Long currentUserId = (Long) req.getSession().getAttribute("userID");
	    // Profile author = ofy().load().key(Key.create(Profile.class, currentUserId)).now();
	    // GoogleCredential credential = this.getCredentialFromLoggedInUser(req);
	    Photo photo = new Photo();
	    // photo.setOwnerUserId(author.getUserId());
	    // photo.setOwnerDisplayName(author.getGoogleDisplayName());
	    // photo.setOwnerProfilePhoto(author.getGooglePublicProfilePhotoUrl());
	    // photo.setOwnerProfileUrl(author.getGooglePublicProfileUrl());
	    // photo.setThemeId(Theme.getCurrentTheme().getId());
	    // photo.setThemeDisplayName(Theme.getCurrentTheme().getDisplayName());
	    photo.setCreated(Calendar.getInstance().getTime());
	    // photo.setNumVotes(0);
	    photo.setImageBlobKey(imageKey);
	    ofy().save().entity(photo).now();
	    ofy().clear();
	    photo = ofy().load().type(Photo.class).id(photo.getId()).now();
	    // addPhotoToGooglePlusHistory(author, photo, credential);
	    sendResponse(req, resp, photo);	
    }    
    
    private GcsFilename getFileName(HttpServletRequest req) {
        String[] splits = req.getRequestURI().split("/", 4);
        if (!splits[0].equals("") || !splits[1].equals("gcs")) {
          throw new IllegalArgumentException("The URL is not formed as expected. " +
              "Expecting /gcs/<bucket>/<object>");
        }
        return new GcsFilename(splits[2], splits[3]);
      }    
    
    /**
     * Transfer the data from the inputStream to the outputStream. Then close both streams.
     */
    private void copy(InputStream input, OutputStream output) throws IOException {
      try {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = input.read(buffer);
        while (bytesRead != -1) {
          output.write(buffer, 0, bytesRead);
          bytesRead = input.read(buffer);
        }
      } finally {
        input.close();
        output.close();
      }
    }    
    
	/**
	 * Send the given object (via body.toString()) down the given response.
	 * 
	 * Attempts to send an HTTP 500 if there was an error in writing the
	 * response.
	 * 
	 * @param resp
	 *            Response to use in transmitting body.
	 * @param body
	 *            Object on which to call toString() to generate response.
	 */
	protected void sendResponse(HttpServletRequest req,
			HttpServletResponse resp, Jsonifiable body) {
		resp.setContentType(JSON_MIMETYPE);
		try {
			if (req.getParameter("items") != null) {
				Map<String, Object> jsonObject = new HashMap<String, Object>();
				jsonObject.put("kind", body.kind);
				jsonObject.put("item", body);
				resp.getWriter().print(Jsonifiable.GSON.toJson(jsonObject));
			} else {
				resp.getWriter().print(body.toString());
			}
		} catch (IOException e) {
			sendError(
					resp,
					500,
					new StringBuffer()
							.append("Servlet received an IOException trying to write response ")
							.append("body to HttpServletResponse.").toString());
		}
	}
    

	/**
	 * Send an error down the given response.
	 * 
	 * @param resp
	 *            Response to use in transmitting error.
	 * @param code
	 *            HTTP response code to issue.
	 * @param message
	 *            Message to attach to response.
	 */
	protected void sendError(HttpServletResponse resp, int code, String message) {
		try {
			if (code == 401) {
				resp.addHeader("WWW-Authenticate", "OAuth realm=\"PhotoHunt\", error=\"invalid-token\"");
			}
			
			resp.sendError(code, message);
		} catch (IOException e) {
			throw new RuntimeException(message);
		}
	}
    /*
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blobs.get("myFile");

        if (blobKeys == null || blobKeys.isEmpty()) {
            res.sendRedirect("/");
        } else {
            res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
        }
    }
    */
}