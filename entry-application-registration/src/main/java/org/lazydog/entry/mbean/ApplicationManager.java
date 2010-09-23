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
     * Get the server authentication module class.
     *
     * @param  applicationId  the application identifier.
     *
     * @return  the server authentication module class.
     */
    public String getServerAuthModuleClass(String applicationId);
}
