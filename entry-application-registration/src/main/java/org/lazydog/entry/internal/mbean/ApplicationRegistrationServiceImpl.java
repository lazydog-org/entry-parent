package org.lazydog.entry.internal.mbean;

import org.lazydog.entry.mbean.ApplicationRegistrationService;


/**
 * Application registration service implementation.
 *
 * @author  Ron Rickard
 */
public class ApplicationRegistrationServiceImpl implements ApplicationRegistrationService {

    /**
     * Get the server authentication module class.
     *
     * @param  applicationId  the application ID.
     *
     * @return  the server authentication module class.
     */
    @Override
    public String getServerAuthModuleClass(String applicationId) {
        return "org.lazydog.entry.security.module.EntryServerAuthModule";
    }
}
