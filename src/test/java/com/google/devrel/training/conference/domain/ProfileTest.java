package com.google.devrel.training.conference.domain;

import static org.junit.Assert.*;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for Profile POJO.
 */
public class ProfileTest {

    private static final String EMAIL = "example@gmail.com";
    
    private static final String MOBILE_NUMBER = "07896712342";

    private static final String POST_CODE = "CR0 6AT";
    
    private static final String FIRST_LINE_ADDRESS = "Flat 13";
    
    private static final String USER_ID = "123456789";

    private static final String DISPLAY_NAME = "Your Name Here";

    private Profile profile;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        profile = new Profile(USER_ID, DISPLAY_NAME, EMAIL);
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(USER_ID, profile.getUserId());
        assertEquals(DISPLAY_NAME, profile.getDisplayName());
        assertEquals(EMAIL, profile.getMainEmail());
    }

    @Test
    public void testUpdate() throws Exception {
        String newDisplayName = "New Display Name";
        profile.update(newDisplayName);
        assertEquals(USER_ID, profile.getUserId());
        assertEquals(newDisplayName, profile.getDisplayName());
        assertEquals(EMAIL, profile.getMainEmail());
    }

    /*
    @Test
    public void testListValues() throws Exception {
        List<String> conferenceKeys = new ArrayList<>();
        assertEquals(conferenceKeys, profile.getConferenceKeysToAttend());
        Key<Conference> conferenceKey = Key.create(Conference.class, 123L);
        profile.addToConferenceKeysToAttend(conferenceKey.getString());
        conferenceKeys.add(conferenceKey.getString());
        assertEquals(conferenceKeys, profile.getConferenceKeysToAttend());
    }
    */
}
