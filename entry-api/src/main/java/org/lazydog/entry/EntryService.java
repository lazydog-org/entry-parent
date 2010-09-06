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

    public UserProfile getUserProfile(String username);

    public void modify(UserProfile userProfile);
    
    public boolean register(UserProfile userProfile);

    public boolean unregister(String username);
}
