package org.lazydog.entry.internal.service;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.lazydog.entry.internal.repository.EntryRepositoryImpl;


/**
 * Entry repository wrapper.
 *
 * @author  Ron Rickard
 */
public class EntryRepositoryWrapper extends EntryRepositoryImpl {

    /**
     * Constructor.
     */
    public EntryRepositoryWrapper () {

        // Declare.
        EntityManager entityManager;

        // Get a entity manager.
        entityManager = Persistence.createEntityManagerFactory("EntryPUTest").createEntityManager();

        // Inject the entity manager.
        this.setEntityManager(entityManager);
    }

    /**
     * Get the entity manager.
     *
     * @return  the entity manager.
     */
    @Override
    public EntityManager getEntityManager() {
        return super.getEntityManager();
    }
}
