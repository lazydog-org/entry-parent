package org.lazydog.entry.internal.service;


/**
 * Entry service wrapper.
 *
 * @author  Ron Rickard
 */
public class EntryServiceWrapper extends EntryServiceImpl {

    /**
     * Constructor.
     */
    public EntryServiceWrapper () {

        // Inject the Entry account manager.
        this.setEntryAccountManager(new EntryAccountManagerWrapper());
        
        // Inject the Entry repository.
        this.setEntryRepository(new EntryRepositoryWrapper());
    }
}
