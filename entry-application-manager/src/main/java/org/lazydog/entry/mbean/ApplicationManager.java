package org.lazydog.entry.mbean;

import javax.management.MXBean;


/**
 * Application manager MXBean.
 *
 * @author  Ron Rickard
 */
@MXBean
public interface ApplicationManager {

    public static final String AUTHENTICATION_MODULE_CLASS_NAME_ATTRIBUTE = "authenticationModuleClassName";
    public static final String REGISTRATION_URL_ATTRIBUTE = "registrationURL";
    
    /**
     * Set the authentication module class name.
     *
     * @param  authenticationModuleClassName  the authentication module class name.
     */
    public void setAuthenticationModuleClassName(String authenticationModuleClassName);

    /**
     * Set the registration URL.
     *
     * @param  registrationURL  the registration URL.
     */
    public void setRegistrationURL(String registrationURL);

}
