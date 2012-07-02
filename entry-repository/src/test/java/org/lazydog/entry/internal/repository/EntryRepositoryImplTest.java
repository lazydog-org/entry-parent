package org.lazydog.entry.internal.repository;

import java.util.Date;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lazydog.entry.model.ApplicationProfile;
import org.lazydog.entry.model.ApplicationServerProfile;
import org.lazydog.entry.model.AuthenticationModule;
import org.lazydog.entry.model.UserProfile;
import org.lazydog.entry.spi.repository.EntryRepository;
import org.lazydog.repository.Criteria;
import org.lazydog.repository.criterion.Comparison;


/**
 * Unit tests for EntryRepositoryImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryRepositoryImplTest {

    private static EntryRepository repository;
    private static ApplicationProfile applicationProfile;
    private static ApplicationProfile persistedApplicationProfile;
    private static ApplicationServerProfile applicationServerProfile;
    private static ApplicationServerProfile persistedApplicationServerProfile;
    private static AuthenticationModule authenticationModule;
    private static AuthenticationModule persistedAuthenticationModule;
    private static UserProfile persistedUserProfile;
    private static UserProfile userProfile;
    

    @BeforeClass
    public static void initialize() throws Exception {

        // Ensure the derby.log file is in the target directory.
        System.setProperty("derby.system.home", "./target");
        
        // Get the Entry repository.
        repository = new EntryRepositoryWrapper();

        // Get the application server profile.
        applicationServerProfile = new ApplicationServerProfile();
        applicationServerProfile.setApplicationServerId("testApplicationServerId");
        applicationServerProfile.setCreateTime(new Date());
        applicationServerProfile.setJmxHost("testhost");
        applicationServerProfile.setJmxLogin("testlogin");
        applicationServerProfile.setJmxPassword("testpassword");
        applicationServerProfile.setJmxPort(9999);
        applicationServerProfile.setModifyTime(new Date());

        // Get the authentication module.
        authenticationModule = new AuthenticationModule();
        authenticationModule.setClassName("org.lazydog.test.module.AuthenticationModule");
        authenticationModule.setCreateTime(new Date());
        authenticationModule.setModifyTime(new Date());

        // Get the application profile.
        applicationProfile = new ApplicationProfile();
        applicationProfile.setApplicationId("testApplicationId");
        applicationProfile.setApplicationServerProfile(applicationServerProfile);
        applicationProfile.setAuthenticationModule(authenticationModule);
        applicationProfile.setCreateTime(new Date());
        applicationProfile.setDefaultGroupName("testgroup");
        applicationProfile.setModifyTime(new Date());
        applicationProfile.setRegistrationURL("/test/registration.jsf");

        // Get the user profile.
        userProfile = new UserProfile();
        userProfile.setActivationCode(UUID.randomUUID().toString());
        userProfile.setCreateTime(new Date());
        userProfile.setEmailAddress("testaccount@test.com");
        userProfile.setFirstName("Test");
        userProfile.setLastName("Account");
        userProfile.setModifyTime(new Date());
        userProfile.setPassword("test123");
        userProfile.setUsername("testaccount");
        userProfile.setUuid(UUID.randomUUID().toString());
    }

    @Before
    public void beforeTest() throws Exception {
        try {
            Criteria<ApplicationProfile> applicationProfileCriteria = repository.getCriteria(ApplicationProfile.class);
            applicationProfileCriteria.add(Comparison.eq("applicationId", applicationProfile.getApplicationId()));
            persistedApplicationProfile = repository.find(ApplicationProfile.class, applicationProfileCriteria);
            if (persistedApplicationProfile != null) {
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
                repository.remove(ApplicationProfile.class, persistedApplicationProfile.getId());
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
            }

            Criteria<ApplicationServerProfile> applicationServerProfileCriteria = repository.getCriteria(ApplicationServerProfile.class);
            applicationServerProfileCriteria.add(Comparison.eq("applicationServerId", applicationServerProfile.getApplicationServerId()));
            persistedApplicationServerProfile = repository.find(ApplicationServerProfile.class, applicationServerProfileCriteria);
            if (persistedApplicationServerProfile != null) {
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
                repository.remove(ApplicationServerProfile.class, persistedApplicationServerProfile.getId());
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
            }

            Criteria<AuthenticationModule> authenticationModuleCriteria = repository.getCriteria(AuthenticationModule.class);
            authenticationModuleCriteria.add(Comparison.eq("className", authenticationModule.getClassName()));
            persistedAuthenticationModule = repository.find(AuthenticationModule.class, authenticationModuleCriteria);
            if (persistedAuthenticationModule != null) {
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
                repository.remove(AuthenticationModule.class, persistedAuthenticationModule.getId());
                ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
            }

            Criteria<UserProfile> userProfileCriteria = repository.getCriteria(UserProfile.class);
            userProfileCriteria.add(Comparison.eq("username", userProfile.getUsername()));
            persistedUserProfile = repository.find(UserProfile.class, userProfileCriteria);
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
    public void findApplicationProfile() {
        persistApplicationProfile();
        assertEquals(persistedApplicationProfile, repository.find(ApplicationProfile.class, persistedApplicationProfile.getId()));
    }

    @Test
    public void persistApplicationProfile() {
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        persistedApplicationProfile = repository.persist(applicationProfile);
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertEquals(persistedApplicationProfile, applicationProfile);
    }

    @Test
    public void removeApplicationProfile() {
        persistApplicationProfile();
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        repository.remove(ApplicationProfile.class, persistedApplicationProfile.getId());
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertNull(repository.find(ApplicationProfile.class, persistedApplicationProfile.getId()));
    }

    @Test
    public void findApplicationServerProfile() {
        persistApplicationServerProfile();
        assertEquals(persistedApplicationServerProfile, repository.find(ApplicationServerProfile.class, persistedApplicationServerProfile.getId()));
    }

    @Test
    public void persistApplicationServerProfile() {
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        persistedApplicationServerProfile = repository.persist(applicationServerProfile);
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertEquals(persistedApplicationServerProfile, applicationServerProfile);
    }

    @Test
    public void removeApplicationServerProfile() {
        persistApplicationServerProfile();
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        repository.remove(ApplicationServerProfile.class, persistedApplicationServerProfile.getId());
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertNull(repository.find(ApplicationServerProfile.class, persistedApplicationServerProfile.getId()));
    }

    @Test
    public void findAuthenticationModule() {
        persistAuthenticationModule();
        assertEquals(persistedAuthenticationModule, repository.find(AuthenticationModule.class, persistedAuthenticationModule.getId()));
    }

    @Test
    public void persistAuthenticationModule() {
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        persistedAuthenticationModule = repository.persist(authenticationModule);
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertEquals(persistedAuthenticationModule, authenticationModule);
    }

    @Test
    public void removeAuthenticationModule() {
        persistAuthenticationModule();
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().begin();
        repository.remove(AuthenticationModule.class, persistedAuthenticationModule.getId());
        ((EntryRepositoryWrapper)repository).getEntityManager().getTransaction().commit();
        assertNull(repository.find(AuthenticationModule.class, persistedAuthenticationModule.getId()));
    }

    @Test
    public void findUserProfile() {
        persistAuthenticationModule();
        assertEquals(persistedAuthenticationModule, repository.find(AuthenticationModule.class, persistedAuthenticationModule.getId()));
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
