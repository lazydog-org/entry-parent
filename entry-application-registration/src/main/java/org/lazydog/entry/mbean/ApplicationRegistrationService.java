package org.lazydog.entry.mbean;

import javax.management.MXBean;


/**
 * Application registration service MXBean.
 *
 * @author  Ron Rickard
 */
@MXBean
public interface ApplicationRegistrationService {

    /**
     * Get the server authentication module class.
     *
     * @param  applicationId  the application ID.
     *
     * @return  the server authentication module class.
     */
    public String getServerAuthModuleClass(String applicationId);
}
