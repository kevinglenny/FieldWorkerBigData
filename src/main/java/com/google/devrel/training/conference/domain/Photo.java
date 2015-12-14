package com.google.devrel.training.conference.domain;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.common.base.Preconditions;
import com.google.devrel.training.conference.form.JobForm;
import com.google.devrel.training.conference.servlet.GCSUpload;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a User's Photo.  Contains all of the properties that
 * allow the Photo to be rendered and managed.
 *
 */
@Entity
@Cache
public class Photo extends Jsonifiable {
	
	private static final Logger LOG = Logger.getLogger(
			Photo.class.getName());	
	
	/**
	 * Holds Job key as the parent.
	 */
	@Parent
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private Key<Job> jobKey;
	
	/**
	 * The jobId of the Job.
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	private String jobOwnerID;	
 
  public static String kind = "photohunt#photo";
  
  /**
   * Default size of thumbnails.
   */
  public static final int DEFAULT_THUMBNAIL_SIZE = 400;  

/**
   * ImagesService to use for image operation execution.
   */
  protected static ImagesService images =
      ImagesServiceFactory.getImagesService();

  /**
   * @param id ID of Photo for which to get a Key.
   * @return Key representation of given Photo's ID.
   */
  public static Key<Photo> key(long id) {
    return Key.create(Photo.class, id);
  }

  /**
   * Primary identifier of this Photo.
   */
  @Id
  public Long id;

  /**
   * ID of the User who owns this Photo.
   */
  @Index
  public Long ownerUserId;

  /**
   * Display name of the User who owns this Photo.
   */
  public String ownerDisplayName;

  /**
   * Profile URL of the User who owns this Photo.
   */
  public String ownerProfileUrl;

  /**
   * Profile photo of the User who owns this Photo.
   */
  public String ownerProfilePhoto;

  /**
   * ID of the Theme to which this Photo belongs.
   */
  @Index
  public Long themeId;

  /**
   * Display name of the Theme to which this Photo belongs.
   */
  @Index
  public String themeDisplayName;

  /**
   * Number of votes this Photo has received.
   */
  @Index
  public int numVotes;

  /**
   * True if the current user has already voted this Photo.
   */
  public boolean voted;

  /**
   * Image blob key for this Photo.
   */
  private String imageBlobKey;

  /**
   * Date this Photo was uploaded to PhotoHunt.
   */
  public Date created;

  /**
   * URL for full-size image of this Photo.
   */
  public String fullsizeUrl;

  /**
   * URL for thumbnail image of this Photo.
   */
  public String thumbnailUrl;

  /**
   * URL for vote call to action on this photo.
   */
  public String voteCtaUrl;

  /**
   * URL for interactive posts and deep linking to this photo.
   */
  public String photoContentUrl;
  
  /**
   * Just making the default constructor private.
   */
  public Photo() {};
  
  public Photo(final long id, final String jobOwnerID, final JobForm jobForm) {
		this.id = id;
		this.jobKey = Key.create(Job.class, jobOwnerID);
		this.jobOwnerID = jobOwnerID;
		// updateWithJobForm(jobForm);
	}


  /**
   * Setup image URLs (fullsizeUrl and thumbnailUrl) after this Photo has been
   * loaded.
   */
  @OnLoad
  protected void setupImageUrls() {
    fullsizeUrl = getImageUrl();
    thumbnailUrl = getImageUrl(DEFAULT_THUMBNAIL_SIZE);
  }

  
  public String getKind() {
	  return kind;
  }
  /**
   * @return URL for full-size image of this photo.
   */
  public String getImageUrl() {
    return getImageUrl(-1);
  }

  /**
   * @param size Size of image for URL to return.
   * @return URL for images for this Photo of given size.
   */
  public String getImageUrl(int size) {
    ServingUrlOptions options = ServingUrlOptions.Builder
        .withGoogleStorageFileName(imageBlobKey)
        .secureUrl(true);
    if (size > -1) {
      options.imageSize(size);
    }
    LOG.log(Level.INFO, String.format("Photo object image key = " + imageBlobKey )); 
    return images.getServingUrl(options);
  }

  /**
   * Setup voteCtaUrl after this Photo has been loaded.
   */
  @OnLoad
  protected void setupVoteCtaUrl() {
    voteCtaUrl = "baseURL " + "/photo.html?photoId=" + id +
        "&action=VOTE";
  }

  /**
   * Setup photoContentUrl after this Photo has been loaded.
   */
  @OnLoad
  protected void photoDeepLinkUrl() {
    photoContentUrl = "baseURL " + "/photo.html?photoId=" + id;
  }
  
  public Long getId() {
	  return id;
  }
  
  public void setOwnerUserId(Long ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}

	public void setOwnerProfileUrl(String ownerProfileUrl) {
		this.ownerProfileUrl = ownerProfileUrl;
	}

	public void setOwnerProfilePhoto(String ownerProfilePhoto) {
		this.ownerProfilePhoto = ownerProfilePhoto;
	}

	public void setThemeId(Long themeId) {
		this.themeId = themeId;
	}

	public void setThemeDisplayName(String themeDisplayName) {
		this.themeDisplayName = themeDisplayName;
	}

	public void setPhotoContentUrl(String photoContentUrl) {
		this.photoContentUrl = photoContentUrl;
	}  
  
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public void setImageBlobKey(String imageBlobKey) {
		this.imageBlobKey = imageBlobKey;
	}
	
	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}
	
	public String getThemeDisplayName() {
		return themeDisplayName;
	}
	
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
}
