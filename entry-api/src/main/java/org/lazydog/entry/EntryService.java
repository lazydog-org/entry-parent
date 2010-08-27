package org.lazydog.entry;

import org.lazydog.entry.model.ApplicationUser;


/**
 * Entry service.
 *
 * @author  Ron Rickard
 */
public interface EntryService {

    public void activate(ApplicationUser applicationUser);

    public void deactivate(ApplicationUser applicationUser);

    public ApplicationUser find(String username);

    public ApplicationUser register(ApplicationUser applicationUser);
}
