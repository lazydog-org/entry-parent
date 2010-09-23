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
    private static UserProfile userProfile;

    @BeforeClass
    public static void initialize() {

        // Ensure the derby.log file is in the target directory.
        System.setProperty("derby.system.home", "./target");
        
        // Get the entry service.
        service = new EntryServiceWrapper();

        // Get the user profile.
        userProfile = UserProfile.newInstance();
        userProfile.setEmailAddress("testaccount1@test.com");
        userProfile.setFirstName("Test");
        userProfile.setLastName("Account1");
        userProfile.setPassword("test123");
        userProfile.setUsername("testaccount1");
    }

    @AfterClass
    public static void destroy() {
        try {
            if (service.getUserProfile(userProfile.getUsername()) != null) {
                ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().begin();
                service.unregister(userProfile.getUsername());
                ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().commit();
            }
        }
        catch(Exception e) {}
    }

    @Before
    public void beforeTest() {
        try {
            if (service.getUserProfile(userProfile.getUsername()) != null) {
                ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().begin();
                service.unregister(userProfile.getUsername());
                ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().commit();
            }
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
    @Ignore
    public void getUserProfile() {
        register();
        ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().begin();
        assertEquals(userProfile, service.getUserProfile(userProfile.getUsername()));
        ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().commit();
    }

    @Test
    public void modify() {
    }

    @Test
    @Ignore
    public void register() {
        ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().begin();
        assertTrue(service.register(userProfile, null));
        ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().commit();
    }

    @Test
    @Ignore
    public void unregister() {
        register();
        ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().begin();
        assertTrue(service.unregister(userProfile.getUsername()));
        ((EntryRepositoryWrapper)((EntryServiceWrapper)service).getEntryRepository()).getEntityManager().getTransaction().commit();
    }
}
