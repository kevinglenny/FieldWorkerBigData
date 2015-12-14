package com.google.devrel.training.conference.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


@Entity
@Cache

public class Profile {
    String displayName;
    String mainEmail;

    
    /**
     * Entity's key
     */
    @Id String userId;

    /**
     * Keys of the conferences that this user registers to attend.
     */
    private List<String> conferenceKeysToAttend = new ArrayList<>(0);
    
    /**
     * Keys of the job that this user is assigned to attend.
     */
    private List<String> jobKeysToAttend = new ArrayList<>(0);

    /**
     * Public constructor for Profile.
     * @param userId The user id, obtained from the email
     * @param displayName Any string user wants us to display him/her on this system.
     * @param mainEmail User's main e-mail address.
     *
     */
    public Profile (String userId, String displayName, String mainEmail) {
        this.userId = userId;
        this.displayName = displayName;
        this.mainEmail = mainEmail;
    }

    public String getUserId() {
        return userId;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getMainEmail() {
        return mainEmail;
    }
    
    /**
     * Getter for conferenceIdsToAttend.
     * @return an immutable copy of conferenceIdsToAttend.
     */
    public List<String> getConferenceKeysToAttend() {
        return ImmutableList.copyOf(conferenceKeysToAttend);
    }

    /**
     * Getter for jobIdsToAttend.
     * @return an immutable copy of jobIdsToAttend.
     */
    public List<String> getJobKeysToAttend() {
        return ImmutableList.copyOf(jobKeysToAttend);
    }    
    
    /**
     * Just making the default constructor private.
     */
    private Profile() {}

    /**
     * Update the Profile with the given displayName and teeShirtSize
     *
     * @param displayName
     */
    public void update(String displayName) {
        if (displayName != null) {
            this.displayName = displayName;
        }
    }

    /**
     * Adds a ConferenceId to conferenceIdsToAttend.
     *
     * The method initConferenceIdsToAttend is not thread-safe, but we need a transaction for
     * calling this method after all, so it is not a practical issue.
     *
     * @param conferenceKey a websafe String representation of the Conference Key.
     */
    public void addToConferenceKeysToAttend(String conferenceKey) {
        conferenceKeysToAttend.add(conferenceKey);
    }

    /**
     * Remove the conferenceId from conferenceIdsToAttend.
     *
     * @param conferenceKey a websafe String representation of the Conference Key.
     */
    public void unregisterFromConference(String conferenceKey) {
        if (conferenceKeysToAttend.contains(conferenceKey)) {
            conferenceKeysToAttend.remove(conferenceKey);
        } else {
            throw new IllegalArgumentException("Invalid conferenceKey: " + conferenceKey);
        }
    }
    
    /**
     * Adds a JobId to jobIdsToAttend.
     *
     * The method initJobIdsToAttend is not thread-safe, but we need a transaction for
     * calling this method after all, so it is not a practical issue.
     *
     * @param jobKey a websafe String representation of the Job Key.
     */
    public void addToJobKeysToAttend(String jobKey) {
        jobKeysToAttend.add(jobKey);
    }

    /**
     * Remove the jobId from jobIdsToAttend.
     *
     * @param jobKey a websafe String representation of the Job Key.
     */
    public void unregisterFromJob(String jobKey) {
        if (jobKeysToAttend.contains(jobKey)) {
            jobKeysToAttend.remove(jobKey);
        } else {
            throw new IllegalArgumentException("Invalid jobKey: " + jobKey);
        }
    }    
    

}
