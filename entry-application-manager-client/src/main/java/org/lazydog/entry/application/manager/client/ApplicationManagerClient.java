package org.lazydog.entry.application.manager.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.lazydog.entry.mbean.ApplicationManager;
import org.lazydog.entry.security.config.EntryAuthConfigProvider;
import org.lazydog.mbean.utilities.MBeanUtility;
import org.lazydog.utility.Tracer;


/**
 * Application manager client context listener.
 *
 * @author  Ron Rickard
 */
@WebListener
public class ApplicationManagerClient implements ServletContextListener {

    private static final Tracer TRACER = Tracer.getTracer(ApplicationManagerClient.class.getName());

    private static final Level DEFAULT_TRACE_LEVEL = Level.FINEST;

    private static final String APPLICATION_ID_KEY = "org.lazydog.entry.application.manager.applicationId";
    private static final String DEFAULT_GROUP_NAME_KEY = "org.lazydog.entry.application.manager.defaultGroupName";
    private static final String TRACE_LEVEL_KEY = "org.lazydog.entry.application.manager.traceLevel";

    @Resource(name="EntryApplicationManagerEnvironment")
    private Properties environment;
    private String registrationID;

    /**
     * Destroy the servlet context.
     *
     * @param  event  the servlet context event.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        try {

            // Unregister the authentication module.
            unregisterAuthModule(this.registrationID);
            TRACER.trace(Level.INFO, "unregistered the authentication module (%s)", registrationID);
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
            String authModuleClassName;
            String defaultGroupName;

            // Set the trace level to the configured trace level or the default trace level.
            TRACER.setLevel(event.getServletContext().getInitParameter(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            // Get the application ID.
            applicationId = event.getServletContext().getInitParameter(APPLICATION_ID_KEY);

            TRACER.trace(Level.CONFIG, "%s is %s", TRACE_LEVEL_KEY, TRACER.getLevel().getName());
            TRACER.trace(Level.CONFIG, "%s is %s", APPLICATION_ID_KEY, applicationId);
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_HOST_KEY, environment.getProperty(MBeanUtility.JMX_HOST_KEY));
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_PORT_KEY, environment.getProperty(MBeanUtility.JMX_PORT_KEY));
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_LOGIN_KEY, environment.getProperty(MBeanUtility.JMX_LOGIN_KEY));
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_PASSWORD_KEY, environment.getProperty(MBeanUtility.JMX_PASSWORD_KEY));

            // Get the application manager.
            applicationManager = MBeanUtility.getMBean(ApplicationManager.class, environment);

            // Get the authentication module class name.
            authModuleClassName = applicationManager.getAuthModuleClassName(applicationId);
            TRACER.trace(Level.FINE, "authModuleClassName is %s", authModuleClassName);

            // Register the authentication module.
            this.registrationID = registerAuthModule(authModuleClassName, event.getServletContext().getContextPath());
            TRACER.trace(Level.INFO, "registered the authentication module %s (%s) for %s", authModuleClassName, registrationID, event.getServletContext().getContextPath());

            // Get the default group name.
            defaultGroupName = applicationManager.getDefaultGroupName(applicationId);
            TRACER.trace(Level.FINE, "defaultGroupName is %s", defaultGroupName);

            // Put the default group name in the application scope.
            event.getServletContext().setAttribute(DEFAULT_GROUP_NAME_KEY, defaultGroupName);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register the authentication module.
     *
     * @param  authModuleClassName  the authentication module class name.
     * @param  contextPath          the context path.
     *
     * @return  the registration identifier.
     */
    private static String registerAuthModule(String authModuleClassName, String contextPath) {

        // Declare.
        EntryAuthConfigProvider entryAuthConfigProvider;
        AuthConfigFactory factory;
        Map<String,String> options;

        // Set the options.
        options = new HashMap<String,String>();
        options.put(EntryAuthConfigProvider.SERVER_AUTH_MODULE_CLASS_NAME_KEY, authModuleClassName);
        options.put(EntryAuthConfigProvider.CONTEXT_PATH_KEY, contextPath);
// TODO: remove this.
options.put(EntryAuthConfigProvider.TRACE_LEVEL_KEY, "INFO");

        // Get the authentication configuration factory.
        factory = AuthConfigFactory.getFactory();

        // Register the authentication module (self-registering.)
        entryAuthConfigProvider = new EntryAuthConfigProvider(options, factory);

        return entryAuthConfigProvider.getRegistrationID();
    }

    /**
     * Unregister the authentication module.
     *
     * @param  registrationID  the registration identifier.
     */
    private static void unregisterAuthModule(String registrationID) {

        // Declare.
        AuthConfigFactory factory;

        // Get the authentication configuration factory.
        factory = AuthConfigFactory.getFactory();

        // Unregister the authentication module.
        factory.removeRegistration(registrationID);
    }
}
