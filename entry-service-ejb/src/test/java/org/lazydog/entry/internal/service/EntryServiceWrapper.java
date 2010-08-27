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

        // Inject the comic repository.
        this.setEntryRepository(new EntryRepositoryWrapper());
    }
}
