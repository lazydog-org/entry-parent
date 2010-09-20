package org.lazydog.entry.application.registration.client;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.lazydog.entry.mbean.ApplicationRegistrationService;
import org.lazydog.mbean.utilities.MBeanFactory;


/**
 * Application registration client context listener.
 *
 * @author  Ron Rickard
 */
public class ApplicationRegistrationClient implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ApplicationRegistrationClient.class.getName());
    private static final String APPLICATION_ID_KEY = "applicationId";
    private static final Level DEFAULT_LOG_LEVEL = Level.FINEST;
    public static final String JMX_HOST_KEY = "jmxHost";
    public static final String JMX_PORT_KEY = "jmxPort";
    public static final String LOGIN_KEY = "login";
    public static final String LOG_LEVEL_KEY = "log.level";
    public static final String PASSWORD_KEY = "password";

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
            String serverAuthModule;


            try {

                // Set the log level to the supplied log level.
                logger.setLevel(Level.parse(event.getServletContext().getInitParameter(LOG_LEVEL_KEY)));
            }
            catch(Exception e) {

                // The supplied log level is invalid,
                // so set the log level to the default log level.
                logger.setLevel(DEFAULT_LOG_LEVEL);
            }

            // Get the application ID.
            applicationId = event.getServletContext().getInitParameter(APPLICATION_ID_KEY);
trace(Level.INFO, "%s is %s", APPLICATION_ID_KEY, applicationId);

            // Set the JMX service environment.
            environment = new Properties();
            environment.put(MBeanFactory.JMX_HOST_KEY, event.getServletContext().getInitParameter(JMX_HOST_KEY));
            environment.put(MBeanFactory.JMX_PORT_KEY, event.getServletContext().getInitParameter(JMX_PORT_KEY));
            environment.put(MBeanFactory.LOGIN_KEY, event.getServletContext().getInitParameter(LOGIN_KEY));
            environment.put(MBeanFactory.PASSWORD_KEY, event.getServletContext().getInitParameter(PASSWORD_KEY));
trace(Level.FINE, "%s is %s", JMX_HOST_KEY, event.getServletContext().getInitParameter(JMX_HOST_KEY));
trace(Level.FINE, "%s is %s", JMX_PORT_KEY, event.getServletContext().getInitParameter(JMX_PORT_KEY));
trace(Level.FINE, "%s is %s", LOGIN_KEY, event.getServletContext().getInitParameter(LOGIN_KEY));
trace(Level.FINE, "%s is %s", PASSWORD_KEY, event.getServletContext().getInitParameter(PASSWORD_KEY));
            applicationRegistrationService = MBeanFactory.create(ApplicationRegistrationService.class, environment);

            serverAuthModule = applicationRegistrationService.getServerAuthModule(applicationId);
trace(Level.FINE, "serverAuthModule is %s", serverAuthModule);

            AuthConfigFactory factory = AuthConfigFactory.getFactory();
trace(Level.INFO, "factory is %s", factory);
            String[] registrationIDs = factory.getRegistrationIDs(null);
            for (String registrationID : registrationIDs) {
trace(Level.INFO, "registrationID is %s", registrationID);
                AuthConfigFactory.RegistrationContext registrationContext = factory.getRegistrationContext(registrationID);
                String layer = registrationContext.getMessageLayer();
trace(Level.INFO, "layer is %s", layer);
                String appContext = registrationContext.getAppContext();
trace(Level.INFO, "appContext is %s", appContext);
                AuthConfigProvider authConfigProvider = factory.getConfigProvider(layer, appContext, null);
trace(Level.INFO, "authConfigProvider is %s", authConfigProvider);
                if (authConfigProvider != null) {
                    ServerAuthConfig serverAuthConfig = authConfigProvider.getServerAuthConfig(layer, appContext, null);
trace(Level.INFO, "serverAuthConfig is %s", serverAuthConfig);
                    if (serverAuthConfig != null) {
                        ServerAuthContext serverAuthContext1 = serverAuthConfig.getAuthContext("true", null, null);
                        ServerAuthContext serverAuthContext2 = serverAuthConfig.getAuthContext("false", null, null);
trace(Level.INFO, "serverAuthContext is %s", serverAuthContext1);
trace(Level.INFO, "serverAuthContext is %s", serverAuthContext2);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a trace log at the specified level.
     *
     * @param  level   the log level.
     * @param  format  the trace format.
     * @param  args    the trace arguments.
     */
    private void trace(Level level, String format, Object... args) {

        // Check if the level is appropriate to log.
        if (level.intValue() >= logger.getLevel().intValue()) {

            // Declare.
            StringBuffer message;

            // Set the trace message.
            message = new StringBuffer();
            message.append(String.format("[%1$tD %1$tT:%1$tL %1$tZ] %2$s ", new Date(), this.getClass().getName()));
            message.append(String.format(format, args));

            // Create the trace log.
            logger.log(level, message.toString());
        }
    }
}
