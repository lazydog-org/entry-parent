package org.lazydog.entry.application.registration.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.lazydog.entry.mbean.ApplicationManager;
import org.lazydog.entry.security.config.EntryAuthConfigProvider;
import org.lazydog.mbean.utilities.MBeanUtility;
import org.lazydog.utility.Tracer;


/**
 * Application registration client context listener.
 *
 * @author  Ron Rickard
 */
@WebListener
public class ApplicationRegistrationClient implements ServletContextListener {

    private static final Tracer TRACER = Tracer.getTracer(ApplicationRegistrationClient.class.getName());

    private static final String APPLICATION_ID_KEY = "applicationId";
    public static final String TRACE_LEVEL_KEY = "traceLevel";

    private static final Level DEFAULT_TRACE_LEVEL = Level.FINEST;
    
    @Resource(name="EntryApplicationRegistrationEnvironment")
    private Properties environment;

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
            ApplicationManager applicationRegistrationService;
            String serverAuthModuleClass;

            // Set the trace level to the level name or the default trace level.
            TRACER.setLevel(event.getServletContext().getInitParameter(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            // Get the application ID.
            applicationId = event.getServletContext().getInitParameter(APPLICATION_ID_KEY);
TRACER.trace(Level.INFO, "%s is %s", APPLICATION_ID_KEY, applicationId);

            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_HOST_KEY, environment.getProperty(MBeanUtility.JMX_HOST_KEY));
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_PORT_KEY, environment.getProperty(MBeanUtility.JMX_PORT_KEY));
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_LOGIN_KEY, environment.getProperty(MBeanUtility.JMX_LOGIN_KEY));
            TRACER.trace(Level.CONFIG, "%s is %s", MBeanUtility.JMX_PASSWORD_KEY, environment.getProperty(MBeanUtility.JMX_PASSWORD_KEY));

            applicationRegistrationService = MBeanUtility.getMBean(ApplicationManager.class, environment);

            serverAuthModuleClass = applicationRegistrationService.getServerAuthModuleClass(applicationId);
TRACER.trace(Level.FINE, "serverAuthModuleClass is %s", serverAuthModuleClass);
            serverAuthModuleClass = "org.lazydog.entry.security.module.ModalServerAuthModule";

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
