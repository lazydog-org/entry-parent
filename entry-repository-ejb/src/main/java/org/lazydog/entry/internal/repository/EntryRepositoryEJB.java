package org.lazydog.entry.internal.repository;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.lazydog.entry.spi.repository.EntryRepository;
import org.lazydog.utilities.ejbmonitor.interceptor.EJBMonitor;


/**
 * Entry repository Enterprise Java Bean.
 * 
 * @author  Ron Rickard
 */
@Singleton(name="ejb/EntryRepository")
@Local(EntryRepository.class)
@Interceptors(EJBMonitor.class)
public class EntryRepositoryEJB extends EntryRepositoryImpl implements EntryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    private void initialize() {
        this.setEntityManager(this.entityManager);
    }
}
