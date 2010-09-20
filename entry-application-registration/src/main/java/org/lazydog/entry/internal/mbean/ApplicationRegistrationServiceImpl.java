package org.lazydog.entry.internal.mbean;

import org.lazydog.entry.mbean.ApplicationRegistrationService;


/**
 * Application registration service implementation.
 *
 * @author  Ron Rickard
 */
public class ApplicationRegistrationServiceImpl implements ApplicationRegistrationService {

    /**
     * Get the server authentication module.
     *
     * @param  applicationId  the application ID.
     *
     * @return  the server authentication module.
     */
    @Override
    public String getServerAuthModule(String applicationId) {
        return "EntryAuthModule";
    }

    /**
     * Get a new instance of this class.
     *
     * @return  a new instance of this class.
     */
    public static ApplicationRegistrationService newInstance() {
        return new ApplicationRegistrationServiceImpl();
    }
}
