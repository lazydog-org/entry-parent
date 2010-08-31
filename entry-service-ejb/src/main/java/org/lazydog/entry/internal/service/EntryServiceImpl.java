package org.lazydog.entry.internal.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
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

    /**
     * Register the application user.
     *
     * @param  applicationUser  the application user.
     */
    @Override
    public void register(ApplicationUser applicationUser) {

        // Set the register time, modify time, and UUID for the application user.
        applicationUser.setRegisterTime(new Date());
        applicationUser.setModifyTime(applicationUser.getRegisterTime());
        applicationUser.setUuid(UUID.randomUUID().toString());

        // Check if the application user is valid.
        if (applicationUser.isValid()) {

            // Persist the application user.
            entryRepository.persist(applicationUser);
        }
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
