package org.lazydog.entry.internal.mbean;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import org.lazydog.entry.mbean.ApplicationManager;


/**
 * Application manager implementation.
 *
 * @author  Ron Rickard
 */
public class ApplicationManagerImpl extends NotificationBroadcasterSupport implements ApplicationManager {

    private String authenticationModuleClassName;
    private String registrationURL;

    /**
     * Get the notification information.
     *
     * @return  the notification information.
     */
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {

        // Declare.
        MBeanNotificationInfo[] mBeanNotificationInfo;

        // Set the notification information.
        mBeanNotificationInfo = new MBeanNotificationInfo[1];
        mBeanNotificationInfo[0] = new MBeanNotificationInfo(
                new String[] {AttributeChangeNotification.ATTRIBUTE_CHANGE},
                AttributeChangeNotification.class.getName(),
                "Application manager");

        return mBeanNotificationInfo;
    }

    /**
     * Set the authentication module class name.
     *
     * @param  authenticationModuleClassName  the authentication module class name.
     */
    @Override
    public void setAuthenticationModuleClassName(String authenticationModuleClassName) {

        // Declare.
        String oldAuthenticationModuleClassName;

        // Initialize.
        oldAuthenticationModuleClassName = this.authenticationModuleClassName;
        this.authenticationModuleClassName = authenticationModuleClassName;

        // Send the notification.
        sendNotification(new AttributeChangeNotification(
                this,
                -1,
                System.currentTimeMillis(),
                "authentication module class name changed",
                AUTHENTICATION_MODULE_CLASS_NAME_ATTRIBUTE,
                String.class.getName(),
                oldAuthenticationModuleClassName,
                this.authenticationModuleClassName));
    }

    /**
     * Set the registration URL.
     *
     * @param  registrationURL  the registration URL.
     */
    @Override
    public void setRegistrationURL(String registrationURL) {

        // Declare.
        String oldRegistrationURL;

        // Initialize.
        oldRegistrationURL = this.registrationURL;
        this.registrationURL = registrationURL;

        // Send the notification.
        sendNotification(new AttributeChangeNotification(
                this,
                -1,
                System.currentTimeMillis(),
                "registration URL changed",
                REGISTRATION_URL_ATTRIBUTE,
                String.class.getName(),
                oldRegistrationURL,
                this.registrationURL));
    }
}
