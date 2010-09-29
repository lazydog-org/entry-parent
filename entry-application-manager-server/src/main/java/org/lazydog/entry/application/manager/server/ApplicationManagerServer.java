package org.lazydog.entry.application.manager.server;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.lazydog.entry.EntryService;
import org.lazydog.entry.mbean.ApplicationManager;
import org.lazydog.entry.security.utility.AuthModuleUtility;
import org.lazydog.mbean.utilities.MBeanUtility;
import org.lazydog.utility.Tracer;


/**
 * Application manager server notification and context listener.
 *
 * @author  Ron Rickard
 */
@WebListener
public class ApplicationManagerServer implements NotificationListener, ServletContextListener {

    private static final Tracer TRACER = Tracer.getTracer(ApplicationManagerServer.class.getName());
// TODO: change the level to WARNING.
    private static final Level DEFAULT_TRACE_LEVEL = Level.WARNING;

    private static final String APPLICATION_ID_KEY = "org.lazydog.entry.application.manager.applicationId";
    private static final String REGISTRATION_URL_KEY = "org.lazydog.entry.application.manager.registrationURL";
    private static final String TRACE_LEVEL_KEY = "org.lazydog.entry.application.manager.traceLevel";

    @EJB(beanName="ejb/EntryService", beanInterface=EntryService.class)
    private EntryService entryService;
    private ObjectName objectName;
    private String registrationId;
    private ServletContext servletContext;

    /**
     * Destroy the servlet context.
     *
     * @param  event  the servlet context event.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        try {

            // Unregister the MBean.
            MBeanUtility.unregister(this.objectName);
            TRACER.trace(Level.INFO, "unregistered the MBean (%s)", this.objectName.getCanonicalName());

            // Unregister the authentication module.
            AuthModuleUtility.unregisterAuthModule(this.registrationId);
            TRACER.trace(Level.INFO, "unregistered the authentication module (%s)", this.registrationId);
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

            // Declare.
            String applicationId;
            ApplicationManager applicationManager;
            String authenticationModuleClassName;
            String registrationURL;

            // Set the servlet context.
            this.servletContext = event.getServletContext();

            // Set the trace level to the configured trace level or the default trace level.
            TRACER.setLevel(this.servletContext.getInitParameter(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            // Get the application identifier.
            applicationId = this.servletContext.getInitParameter(APPLICATION_ID_KEY);

            TRACER.trace(Level.CONFIG, "%s is %s", TRACE_LEVEL_KEY, TRACER.getLevel().getName());
            TRACER.trace(Level.CONFIG, "%s is %s", APPLICATION_ID_KEY, applicationId);

            // Register the MBean.
            this.objectName = MBeanUtility.register(
                    ApplicationManager.class,
                    MBeanUtility.getObjectName(ApplicationManager.class, "applicationId", applicationId));
            TRACER.trace(Level.INFO, "registered the MBean %s (%s)", ApplicationManager.class.getName(), this.objectName.getCanonicalName());

            // Add as notification listener.
            ManagementFactory.getPlatformMBeanServer().addNotificationListener(this.objectName, this, null, null);

            // Get the authentication module class name and registration URL.
            authenticationModuleClassName = entryService.getAuthenticationModuleClassName(applicationId);
            registrationURL = entryService.getRegistrationURL(applicationId);
            TRACER.trace(Level.FINE, "authenticationModuleClassName is %s", authenticationModuleClassName);
            TRACER.trace(Level.FINE, "registrationURL is %s", registrationURL);

            // Set the authentication module class name and registration URL.
            applicationManager = MBeanUtility.getMBean(ApplicationManager.class, this.objectName);
            applicationManager.setAuthenticationModuleClassName(authenticationModuleClassName);
            applicationManager.setRegistrationURL(registrationURL);

            // Put the application identifier in the application scope.
            this.servletContext.setAttribute(APPLICATION_ID_KEY, applicationId);
            TRACER.trace(Level.INFO, "application set attributeMap['%s'] is %s", APPLICATION_ID_KEY, applicationId);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleNotification(Notification notification, Object object) {

        // Declare.
        AttributeChangeNotification attributeChangeNotification;

        // Cast notification to an attribute change notification.
        attributeChangeNotification = (AttributeChangeNotification)notification;

        // Check if the attribute change notification is for authentication module class name.
        if (attributeChangeNotification.getAttributeName().equals(ApplicationManager.AUTHENTICATION_MODULE_CLASS_NAME_ATTRIBUTE)) {

            // Check if a previous authentication module was registered.
            if (this.registrationId != null) {

                // Unregister the previous authentication module.
                AuthModuleUtility.unregisterAuthModule(this.registrationId);
                TRACER.trace(Level.INFO, "unregistered the authentication module (%s)", this.registrationId);
            }

            // Register the new authentication module.
            this.registrationId = AuthModuleUtility.registerAuthModule(
                    (String)attributeChangeNotification.getNewValue(),
                    this.servletContext.getContextPath(),
                    this.servletContext.getInitParameter(TRACE_LEVEL_KEY));
            TRACER.trace(Level.INFO, "registered the authentication module %s (%s) for %s",
                    (String)attributeChangeNotification.getNewValue(),
                    this.registrationId,
                    this.servletContext.getContextPath());
        }
        else if (attributeChangeNotification.getAttributeName().equals(ApplicationManager.REGISTRATION_URL_ATTRIBUTE)) {

            // Put the registration URL in the application scope.
            this.servletContext.setAttribute(
                    REGISTRATION_URL_KEY,
                    (String)attributeChangeNotification.getNewValue());
            TRACER.trace(Level.INFO, "application set attributeMap['%s'] is %s",
                    REGISTRATION_URL_KEY,
                    (String)attributeChangeNotification.getNewValue());
        }
    }
}
