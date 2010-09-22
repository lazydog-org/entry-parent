package org.lazydog.entry.application.registration.server;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.lazydog.entry.mbean.ApplicationRegistrationService;
import org.lazydog.entry.security.config.EntryAuthConfigProvider;
import org.lazydog.mbean.utilities.MBeanFactory;
import org.lazydog.mbean.utilities.MBeanUtility;
import org.lazydog.utility.Tracer;


/**
 * Application registration server context listener.
 *
 * @author  Ron Rickard
 */
public class ApplicationRegistrationServer implements ServletContextListener {

    private static final Tracer TRACER = Tracer.getTracer(ApplicationRegistrationServer.class.getName());

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

        ApplicationRegistrationService applicationRegistrationService = MBeanFactory.create(ApplicationRegistrationService.class);

        String applicationId = null;
        String serverAuthModuleClass = applicationRegistrationService.getServerAuthModuleClass(applicationId);
TRACER.trace(Level.FINE, "serverAuthModuleClass is %s", serverAuthModuleClass);
        serverAuthModuleClass = "org.lazydog.entry.security.module.PageServerAuthModule";

        Map<String,String> options = new HashMap<String,String>();
        options.put(EntryAuthConfigProvider.SERVER_AUTH_MODULE_CLASS_KEY, serverAuthModuleClass);
        options.put(EntryAuthConfigProvider.CONTEXT_PATH_KEY, event.getServletContext().getContextPath());
        options.put(EntryAuthConfigProvider.TRACE_LEVEL_KEY, "INFO");

        AuthConfigFactory factory = AuthConfigFactory.getFactory();
TRACER.trace(Level.INFO, "factory is %s", factory);
        AuthConfigProvider authConfigProvider = new EntryAuthConfigProvider(options, factory);
TRACER.trace(Level.INFO, "authConfigProvider is %s", authConfigProvider);

    }
}
