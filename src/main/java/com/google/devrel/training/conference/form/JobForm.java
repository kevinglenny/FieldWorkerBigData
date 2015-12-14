package com.google.devrel.training.conference.form;

import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

/**
 * A simple Java object (POJO) representing a Job form sent from the client.
 */
public class JobForm {
	
    /**
     * The name of the Job.
     */
    private String name;
    
    /**
     * The status of the Job.
     */
    private String status;    
    
    /**
     * The priority of the Job.
     */
    private String priority;
    
    /**
     * The description of the job.
     */
    private String description;

    /**
     * The first line address of the job.
     */
    private String firstLineAddress;
    
    /**
     * The post code of the job.
     */
    private String postCode;
    
    /**
     * The date of the job.
     */
    private Date date;
   
    /**
     * The invoice cost of the job.
     */
    private Double invoiceCost;
    
    private JobForm() {}

	/**
	 * Public constructor is solely for Unit Test.
	 * @param name
	 * @param status
	 * @param priority 
	 * @param description
	 * @param firstLineAddress
	 * @param postCode
	 * @param date
	 * @param invoiceCost
	 */
    public JobForm(String name, String status, String priority, String description, String firstLineAddress, String postCode,
    		Date date, Double invoiceCost) {
        this.name = name;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.firstLineAddress = firstLineAddress;
        this.postCode = postCode;
        this.date = date == null ? null : new Date(date.getTime());
        this.invoiceCost = invoiceCost;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
    	return status;
    }
    
    public String getPriority() {
        return priority;
    }
        
    public String getDescription() {
        return description;
    }
    
	public String getFirstLineAddress() {
		return firstLineAddress;
	}

	public String getPostCode() {
		return postCode;
	}

    public Date getDate() {
        return date;
    }
    
    public Double getInvoiceCost() {
    	return invoiceCost;
    }
}
