package org.lazydog.entry.internal.mbean;

import org.lazydog.entry.mbean.ApplicationManager;


/**
 * Application manager implementation.
 *
 * @author  Ron Rickard
 */
public class ApplicationManagerImpl implements ApplicationManager {

    /**
     * Get the default group name.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the default group name.
     */
    @Override
    public String getDefaultGroupName(String applicationId) {
        return "comicmanageruser";
    }

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
