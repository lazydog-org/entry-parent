package org.lazydog.entry.internal.repository;

import java.util.Date;
import java.util.UUID;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lazydog.entry.spi.repository.EntryRepository;
import org.lazydog.entry.model.UserProfile;
import org.lazydog.repository.Criteria;
import org.lazydog.repository.criterion.ComparisonOperation;


/**
 * Unit tests for EntryRepositoryImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryRepositoryImplTest {

    private static EntryRepository repository;
    private static UserProfile userProfile;
    private static UserProfile persistedUserProfile;

    @BeforeClass
    public static void initialize() throws Exception {

        // Ensure the derby.log file is in the target directory.
        System.setProperty("derby.system.home", "./target");
        
        // Get the Entry repository.
        repository = new EntryRepositoryWrapper();

        // Get the user profile.
        userProfile = new UserProfile();
        userProfile.setActivationCode(UUID.randomUUID().toString());
        userProfile.setEmailAddress("testaccount1@test.com");
        userProfile.setFirstName("Test");
        userProfile.setLastName("Account1");
        userProfile.setModifyTime(new Date());
        userProfile.setPassword("test123");
        userProfile.setRegisterTime(new Date());
        userProfile.setUsername("testaccount1");
        userProfile.setUuid(UUID.randomUUID().toString());
    }

    @Before
    public void beforeTest() throws Exception {
        try {
            Criteria<UserProfile> criteria = repository.getCriteria(UserProfile.class);
            criteria.add(ComparisonOperation.eq("username", userProfile.getUsername()));
            persistedUserProfile = repository.find(UserProfile.class, criteria);
            if (persistedUserProfile != null) {
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
                repository.remove(UserProfile.class, persistedUserProfile.getId());
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
            }
        }
        catch(Exception e) {
            ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().rollback();
        }
    }

    @Test
    public void findUserProfile() {
        persistUserProfile();
        assertEquals(persistedUserProfile, repository.find(UserProfile.class, persistedUserProfile.getId()));
    }

    @Test
    public void persistUserProfile() {
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        persistedUserProfile = repository.persist(userProfile);
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertEquals(persistedUserProfile, userProfile);
    }

    @Test
    public void removeUserProfile() {
        persistUserProfile();
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        repository.remove(UserProfile.class, persistedUserProfile.getId());
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertNull(repository.find(UserProfile.class, persistedUserProfile.getId()));
    }
}
