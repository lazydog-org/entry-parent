package org.lazydog.entry.internal.repository;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.lazydog.ejbmonitor.interceptor.EJBMonitor;
import org.lazydog.entry.spi.repository.EntryRepository;


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
    protected void initialize() {
        this.setEntityManager(this.entityManager);
    }
}
