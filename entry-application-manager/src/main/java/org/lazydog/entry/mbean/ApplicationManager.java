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
     * Get the default group name.
     * 
     * @param  applicationId  the application identifier.
     * 
     * @return  the default group name.
     */
    public String getDefaultGroupName(String applicationId);

    /**
     * Get the authentication module class name.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the authentication module class name.
     */
    public String getAuthModuleClassName(String applicationId);
}
