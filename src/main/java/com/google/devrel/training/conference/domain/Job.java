package com.google.devrel.training.conference.domain;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.googlecode.objectify.condition.IfNotDefault;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.devrel.training.conference.form.JobForm;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Job class stores job information.
 */
@Entity
@Cache
public class Job {

    private static final String DEFAULT_STATUS = "Unassigned";
    
    private static final String DEFAULT_PRIORITY = "Low";
	
    /**
     * The id for the datastore key.
     *
     * We use automatic id assignment for entities of Job class.
     */
    @Id
    private long id;
    
    /**
     * The name of the Job.
     */
    @Index
    private String name;

    /**
     * The description of the Job.
     */
    private String description;
    
    /**
     * The first line address of the Job.
     */
    private String firstLineAddress;
    
    /**
     * The post code of the Job.
     */
    private String postCode;
    
    /**
     * Is the job assigned?
     */
    @Index
    private Boolean isAssigned;    

    /**
     * Holds Profile key as the parent.
     */
    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<Profile> jobOwnerKey;

    /**
     * The userId of the field worker.
     */
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private String jobOwnerUserID;
    
    /**
     * Status for this Job.
     */
    @Index
    private String status;
    
    /**
     * Priority for this Job.
     */
    @Index
    private String priority;

    /**
     * The date of this Job.
     */
    private Date date;
    
    /**
     * The invoice cost of the Job. 
     */
    @Index
    private Double invoiceCost; 


    /**
     * Just making the default constructor private.
     */
    private Job() {}

    public Job(final long id, final String jobOwnerID,
                      final JobForm jobForm) {
        Preconditions.checkNotNull(jobForm.getName(), "The name is required");
        this.id = id;
        this.jobOwnerKey = Key.create(Profile.class, jobOwnerID);
        this.jobOwnerUserID = jobOwnerID;
        updateWithJobForm(jobForm);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Profile> getProfileKey() {
        return jobOwnerKey;
    }

    // Get a String version of the key
    public String getWebsafeKey() {
        return Key.create(jobOwnerKey, Job.class, id).getString();
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getOwnerUserId() {
        return jobOwnerUserID;
    }

    /**
     * Returns job owner's display name.
     *
     * @return job owner's display name. If there is no Profile, return his/her userId.
     */
    public String getOwnerDisplayName() {
        Profile owner = ofy().load().key(getProfileKey()).now();
        if (owner == null) {
            return jobOwnerUserID;
        } else {
            return owner.getDisplayName();
        }
    }
    
	public String getPostCode() {
		return postCode;
	}
	
	public Double getInvoiceCost() {
		return invoiceCost;
	}
	
	public Boolean isAssigned() {
		return isAssigned;
	}

	public void setInvoiceCost(Double invoiceCost) {
		this.invoiceCost = invoiceCost;
	}

    /**
     * Returns job status.
     * @return a job status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns a job priority.
     * @return a job priority.
     */
    public String getPriority() {
        return priority;
    }
    
    /**
     * Returns a defensive copy of date if not null.
     * @return a defensive copy of date if not null.
     */
    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }


    /**
     * Updates the Job with JobForm.
     * This method is used upon object creation as well as updating existing Jobs.
     *
     * @param jobForm contains form data sent from the client.
     */
    public void updateWithJobForm(JobForm jobForm) {
        this.name = jobForm.getName();
        this.status = jobForm.getStatus() == null ? DEFAULT_STATUS : jobForm.getStatus();
        this.priority = jobForm.getPriority() == null ? DEFAULT_PRIORITY : jobForm.getPriority();        
        
        
        this.description = jobForm.getDescription();
        this.firstLineAddress = jobForm.getFirstLineAddress();
        this.postCode = jobForm.getPostCode();
        
        Date date = jobForm.getDate();
        this.date = date == null ? null : new Date(date.getTime());
        
        this.invoiceCost = jobForm.getInvoiceCost();
    }

    public void assign() {
        this.isAssigned = true;
    }

    public void giveBack() {
        this.isAssigned = false;
    }    
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Id: " + id + "\n")
                .append("Name: ").append(name).append("\n");
        if (description != null) {
            stringBuilder.append("Description: ").append(description).append("\n");
        }
        if (firstLineAddress != null) {
            stringBuilder.append("First line address: ").append(firstLineAddress).append("\n");
        }
        if (postCode != null) {
            stringBuilder.append("Post code: ").append(postCode).append("\n");
        }
        if (status != null) {
            stringBuilder.append("Status: ").append(status).append("\n");
        }
        if (priority != null) {
            stringBuilder.append("Priority: ").append(priority).append("\n");
        }
        if (date != null) {
            stringBuilder.append("Date: ").append(date.toString()).append("\n");
        }
        if (invoiceCost != null) {
            stringBuilder.append("Invoice cost: ").append(invoiceCost).append("\n");
        }
        return stringBuilder.toString();
    }

	public String getFirstLineAddress() {
		return firstLineAddress;
	}


}
