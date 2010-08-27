package org.lazydog.entry.internal.service;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.lazydog.entry.EntryRepository;
import org.lazydog.entry.EntryService;
import org.lazydog.entry.model.ApplicationUser;
import org.lazydog.utilities.ejbmonitor.interceptor.EJBMonitor;


/**
 * Entry service Enterprise Java Beans.
 * 
 * @author  Ron Rickard
 */
@Stateless(name="ejb/EntryService")
@Remote(EntryService.class)
@Interceptors(EJBMonitor.class)
public class EntryServiceImpl
       implements EntryService {

    @EJB(beanName="ejb/EntryRepository", beanInterface=EntryRepository.class)
    private EntryRepository entryRepository;

    @Override
    public void activate(ApplicationUser applicationUser) {
    }

    @Override
    public void deactivate(ApplicationUser applicationUser) {
    }

    @Override
    public ApplicationUser find(String username) {
        return null;
    }

    @Override
    public ApplicationUser register(ApplicationUser applicationUser) {
        return null;
    }

    /**
     * Set the entry repository.
     *
     * @param  entryRepository  the entry repository.
     */
    public void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }
}
