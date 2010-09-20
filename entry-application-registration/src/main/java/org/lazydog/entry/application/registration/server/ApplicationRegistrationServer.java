package org.lazydog.entry.application.registration.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.lazydog.entry.mbean.ApplicationRegistrationService;
import org.lazydog.mbean.utilities.MBeanFactory;
import org.lazydog.mbean.utilities.MBeanUtility;


/**
 * Application registration server context listener.
 *
 * @author  Ron Rickard
 */
public class ApplicationRegistrationServer implements ServletContextListener {


    /**
     * Destroy the servlet context.
     *
     * @param  event  the servlet context event.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        try {

            // Unregister the MBean.
            MBeanUtility.unregister(
                    MBeanUtility.getObjectName(ApplicationRegistrationService.class));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the servlet context.
     *
     * @param  event  the servlet context event.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {

        try {

            // Register the MBean.
            MBeanUtility.register(
                    MBeanUtility.getObjectName(ApplicationRegistrationService.class),
                    MBeanFactory.create(ApplicationRegistrationService.class));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
