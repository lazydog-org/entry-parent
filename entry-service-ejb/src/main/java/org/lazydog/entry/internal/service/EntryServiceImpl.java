package org.lazydog.entry.internal.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import org.lazydog.entry.spi.account.manager.EntryAccountManager;
import org.lazydog.entry.spi.repository.EntryRepository;
import org.lazydog.entry.EntryService;
import org.lazydog.entry.model.UserProfile;
import org.lazydog.repository.Criteria;
import org.lazydog.repository.criterion.ComparisonOperation;
import org.lazydog.utilities.ejbmonitor.interceptor.EJBMonitor;


/**
 * Entry service Enterprise Java Beans.
 * 
 * @author  Ron Rickard
 */
@Singleton(name="ejb/EntryService")
@Local(EntryService.class)
@Interceptors(EJBMonitor.class)
public class EntryServiceImpl implements EntryService {

    @EJB(beanName="ejb/EntryRepository", beanInterface=EntryRepository.class)
    private EntryRepository entryRepository;

    @EJB(beanName="ejb/EntryAccountManager", beanInterface=EntryAccountManager.class)
    private EntryAccountManager entryAccountManager;

    /**
     * Activate the user profile for the specified username using the
     * specified activate code.
     *
     * @param  username        the username.
     * @param  activationCode  the activation code.
     *
     * @return  true if the user profile is activated, otherwise false.
     */
    @Override
    public boolean activate(String username, String activationCode) {

        // Declare.
        UserProfile userProfile;

        // Get the user profile for username.
        userProfile = getUserProfile(username);

        // Check if the supplied activation code is correct.
        if (activationCode.equals(userProfile.getActivationCode())) {

            // Unlock the user account.
            entryAccountManager.unlockAccount(username);
        }

        return !entryAccountManager.isAccountLocked(username);
    }

    /**
     * Deactivate the user profile for the specified username.
     *
     * @param  username  the username.
     *
     * @return  true if the user profile is deactivated, otherwise false.
     */
    @Override
    public boolean deactivate(String username) {

        // Lock the user account.
        entryAccountManager.lockAccount(username);

        return entryAccountManager.isAccountLocked(username);
    }

    /**
     * Get the entry repository.
     * 
     * @return  the entry repository.
     */
    protected EntryRepository getEntryRepository() {
        return this.entryRepository;
    }

    /**
     * Get the user profile for the specified username.
     *
     * @param  username  the username.
     *
     * @return  the user profile.
     */
    @Override
    public UserProfile getUserProfile(String username) {

        // Declare.
        Criteria<UserProfile> criteria;
        UserProfile userProfile;

        // Get the user profile for username.
        criteria = entryRepository.getCriteria(UserProfile.class);
        criteria.add(ComparisonOperation.eq("username", username));
        userProfile = entryRepository.find(UserProfile.class, criteria);

        return userProfile;
    }

    /**
     * Modify the user profile.
     *
     * @param  userProfile  the user profile.
     */
    @Override
    public void modify(UserProfile userProfile) {

        // Set the modify time.
        userProfile.setModifyTime(new Date());

        // Persist the user profile.
        entryRepository.persist(userProfile);

        // Check if the password exists.
        if (userProfile.getPassword() != null) {

            // Change the user account password.
            entryAccountManager.changePassword(userProfile.getUsername(), userProfile.getPassword());
        }
    }

    /**
     * Register the user profile for the specified application.
     *
     * @param  userProfile    the user profile.
     * @param  applicationId  the application identifier.
     *
     * @return  true if the user account exists, otherwise false.
     */
    @Override
    public boolean register(UserProfile userProfile, String applicationId) {

        // Declare.
        Set<String> accountNames;

        // Set the register time, modify time, activation code, 
        // and UUID for the user profile.
        userProfile.setRegisterTime(new Date());
        userProfile.setModifyTime(userProfile.getRegisterTime());

        // Persist the user profile.
        entryRepository.persist(userProfile);

        // Create the user account.
        entryAccountManager.createAccount(userProfile.getUsername(), userProfile.getPassword());

        // Lock the user account.
//entryAccountManager.lockAccount(userProfile.getUsername());
        
        // Add the user account to the default group.
        accountNames = new HashSet<String>();
        accountNames.add(userProfile.getUsername());
// TODO: lookup default group for application identifier.
        entryAccountManager.addMembers("comicmanageruser", accountNames);

        return entryAccountManager.accountExists(userProfile.getUsername());
    }

    /**
     * Set the Entry account manager.
     *
     * @param  entryAccountManager  the Entry account manager.
     */
    protected void setEntryAccountManager(EntryAccountManager entryAccountManager) {
        this.entryAccountManager = entryAccountManager;
    }

    /**
     * Set the Entry repository.
     *
     * @param  entryRepository  the Entry repository.
     */
    protected void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    /**
     * Unregister the user profile.
     *
     * @param  id  the ID.
     */
    @Override
    public boolean unregister(String username) {

        // Declare.
        UserProfile userProfile;

        // Get the user profile for username.
        userProfile = getUserProfile(username);

        // Remove the user profile.
        entryRepository.remove(UserProfile.class, userProfile.getId());

        // Remove the user account.
        entryAccountManager.removeAccount(userProfile.getUsername());

        return !entryAccountManager.accountExists(userProfile.getUsername());
    }
}
