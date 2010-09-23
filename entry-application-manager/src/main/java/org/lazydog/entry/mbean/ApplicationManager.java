package org.lazydog.entry.mbean;

import javax.management.MXBean;


/**
 * Application manager MXBean.
 *
 * @author  Ron Rickard
 */
@MXBean
public interface ApplicationManager {

    /**
     * Get the authentication module class name.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the authentication module class name.
     */
    public String getAuthModuleClassName(String applicationId);

    /**
     * Get the registration URL.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the registration URL.
     */
    public String getRegistrationURL(String applicationId);

}
