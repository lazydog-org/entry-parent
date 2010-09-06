package org.lazydog.entry.internal.service;

import org.lazydog.entry.EntryService;
import org.lazydog.entry.model.UserProfile;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit tests for EntryServiceImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryServiceImplTest {

    private static EntryService service;

    @BeforeClass
    public static void initialize() {

        // Get the entry service.
        service = new EntryServiceWrapper();
    }

    @AfterClass
    @Ignore
    public static void destroy() {

        try {
            service.unregister("testaccount1");
        }
        catch(Exception e) {}
    }

    @Before
    public void beforeTest() {

        try {
            service.unregister("testaccount1");
        }
        catch(Exception e) {}
    }

    @Test
    public void activate() {

    }

    @Test
    public void deactivate() {

    }

    @Test
    public void getUserProfile() {

    }

    @Test
    public void modify() {

    }

    @Test
    public void register() {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmailAddress("testaccount1@test.com");
        userProfile.setFirstName("Test");
        userProfile.setLastName("Account1");
        userProfile.setPassword("test123");
        userProfile.setUsername("testaccount1");

        assertTrue(service.register(userProfile));
    }

    @Test
    public void unregister() {

    }
}
