package org.lazydog.entry.internal.repository;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.lazydog.entry.EntryRepository;
import org.lazydog.repository.AbstractRepository;
import org.lazydog.utilities.ejbmonitor.interceptor.EJBMonitor;


/**
 * Entry repository Enterprise Java Bean.
 * 
 * @author  Ron Rickard
 */
@Singleton(name="ejb/EntryDataAccess")
@Local(EntryRepository.class)
@Interceptors(EJBMonitor.class)
public class EntryRepositoryImpl extends AbstractRepository implements EntryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    private void initialize() {
        this.setEntityManager(this.entityManager);
    }
}
