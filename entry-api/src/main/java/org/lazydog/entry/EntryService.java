package org.lazydog.entry;

import org.lazydog.entry.model.UserProfile;


/**
 * Entry service.
 *
 * @author  Ron Rickard
 */
public interface EntryService {

    public boolean activate(String username, String activationCode);

    public boolean deactivate(String username);

    public String getAuthenticationModuleClassName(String applicationId);

    public String getRegistrationURL(String applicationId);

    public UserProfile getUserProfile(String username);

    public void modify(UserProfile userProfile);
    
    public boolean register(UserProfile userProfile, String applicationId);

    public boolean unregister(String username);
}
