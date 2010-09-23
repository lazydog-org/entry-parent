package org.lazydog.entry.internal.mbean;

import org.lazydog.entry.mbean.ApplicationManager;


/**
 * Application manager implementation.
 *
 * @author  Ron Rickard
 */
public class ApplicationManagerImpl implements ApplicationManager {

    /**
     * Get the authentication module class name.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the authentication module class name.
     */
    @Override
    public String getAuthModuleClassName(String applicationId) {
        return "org.lazydog.entry.security.module.ModalServerAuthModule";
    }

    /**
     * Get the registration URL.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the registration URL.
     */
    @Override
    public String getRegistrationURL(String applicationId) {
        return "http://localhost:8080/entry/pages/registration.jsf";
    }
}
