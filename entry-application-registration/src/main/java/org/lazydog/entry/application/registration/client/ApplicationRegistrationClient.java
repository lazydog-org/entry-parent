package org.lazydog.entry.application.registration.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.lazydog.entry.mbean.ApplicationRegistrationService;
import org.lazydog.entry.security.config.EntryAuthConfigProvider;
import org.lazydog.mbean.utilities.MBeanFactory;
import org.lazydog.utility.Tracer;


/**
 * Application registration client context listener.
 *
 * @author  Ron Rickard
 */
public class ApplicationRegistrationClient implements ServletContextListener {

    private static final Tracer TRACER = Tracer.getTracer(ApplicationRegistrationClient.class.getName());

    public static final String JMX_HOST_KEY = "jmxHost";
    public static final String JMX_PORT_KEY = "jmxPort";
    public static final String LOGIN_KEY = "login";
    public static final String TRACE_LEVEL_KEY = "trace.level";
    public static final String PASSWORD_KEY = "password";

    private static final String APPLICATION_ID_KEY = "applicationId";
    private static final Level DEFAULT_TRACE_LEVEL = Level.FINEST;
    

    /**
     * Destroy the servlet context.
     *
     * @param  event  the servlet context event.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        try {

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
            ApplicationRegistrationService applicationRegistrationService;
            Properties environment;
            String serverAuthModuleClass;

            // Set the trace level to the level name or the default trace level.
            TRACER.setLevel(event.getServletContext().getInitParameter(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            // Get the application ID.
            applicationId = event.getServletContext().getInitParameter(APPLICATION_ID_KEY);
TRACER.trace(Level.INFO, "%s is %s", APPLICATION_ID_KEY, applicationId);


            // Set the remote environment properties.
            environment = new Properties();
            environment.put(MBeanFactory.JMX_HOST_KEY, event.getServletContext().getInitParameter(JMX_HOST_KEY));
            environment.put(MBeanFactory.JMX_PORT_KEY, event.getServletContext().getInitParameter(JMX_PORT_KEY));
            environment.put(MBeanFactory.LOGIN_KEY, event.getServletContext().getInitParameter(LOGIN_KEY));
            environment.put(MBeanFactory.PASSWORD_KEY, event.getServletContext().getInitParameter(PASSWORD_KEY));
TRACER.trace(Level.FINE, "%s is %s", JMX_HOST_KEY, event.getServletContext().getInitParameter(JMX_HOST_KEY));
TRACER.trace(Level.FINE, "%s is %s", JMX_PORT_KEY, event.getServletContext().getInitParameter(JMX_PORT_KEY));
TRACER.trace(Level.FINE, "%s is %s", LOGIN_KEY, event.getServletContext().getInitParameter(LOGIN_KEY));
TRACER.trace(Level.FINE, "%s is %s", PASSWORD_KEY, event.getServletContext().getInitParameter(PASSWORD_KEY));
            applicationRegistrationService = MBeanFactory.create(ApplicationRegistrationService.class, environment);

            serverAuthModuleClass = applicationRegistrationService.getServerAuthModuleClass(applicationId);
TRACER.trace(Level.FINE, "serverAuthModuleClass is %s", serverAuthModuleClass);

            Map<String,String> options = new HashMap<String,String>();
            options.put(EntryAuthConfigProvider.SERVER_AUTH_MODULE_CLASS_KEY, serverAuthModuleClass);
            options.put(EntryAuthConfigProvider.CONTEXT_PATH_KEY, event.getServletContext().getContextPath());
            options.put(EntryAuthConfigProvider.TRACE_LEVEL_KEY, "FINEST");

            AuthConfigFactory factory = AuthConfigFactory.getFactory();
TRACER.trace(Level.INFO, "factory is %s", factory);
            AuthConfigProvider authConfigProvider = new EntryAuthConfigProvider(options, factory);           
TRACER.trace(Level.INFO, "authConfigProvider is %s", authConfigProvider);

            String[] registrationIDs = factory.getRegistrationIDs(null);
            for (String registrationID : registrationIDs) {
TRACER.trace(Level.INFO, "registrationID is %s", registrationID);
                AuthConfigFactory.RegistrationContext registrationContext = factory.getRegistrationContext(registrationID);
                String layer = registrationContext.getMessageLayer();
TRACER.trace(Level.INFO, "layer is %s", layer);
                String appContext = registrationContext.getAppContext();
TRACER.trace(Level.INFO, "appContext is %s", appContext);
                authConfigProvider = factory.getConfigProvider(layer, appContext, null);
TRACER.trace(Level.INFO, "authConfigProvider is %s", authConfigProvider);
                if (authConfigProvider != null) {
                    ServerAuthConfig serverAuthConfig = authConfigProvider.getServerAuthConfig(layer, appContext, null);
TRACER.trace(Level.INFO, "serverAuthConfig is %s", serverAuthConfig);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
