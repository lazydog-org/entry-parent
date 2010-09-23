package org.lazydog.entry.application.manager.server;

import java.util.logging.Level;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.lazydog.entry.mbean.ApplicationManager;
import org.lazydog.entry.security.utility.AuthModuleUtility;
import org.lazydog.mbean.utilities.MBeanUtility;
import org.lazydog.utility.Tracer;


/**
 * Application manager server context listener.
 *
 * @author  Ron Rickard
 */
@WebListener
public class ApplicationManagerServer implements ServletContextListener {

    private static final Tracer TRACER = Tracer.getTracer(ApplicationManagerServer.class.getName());
// TODO: change the level to WARNING.
    private static final Level DEFAULT_TRACE_LEVEL = Level.WARNING;

    private static final String APPLICATION_ID_KEY = "org.lazydog.entry.application.manager.applicationId";
    private static final String TRACE_LEVEL_KEY = "org.lazydog.entry.application.manager.traceLevel";

    private String objectId;
    private String registrationId;

    /**
     * Destroy the servlet context.
     *
     * @param  event  the servlet context event.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        try {

            // Unregister the MBean.
            MBeanUtility.unregister(this.objectId);
            TRACER.trace(Level.INFO, "unregistered the MBean (%s)", this.objectId);

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
            String authModuleClassName;

            // Set the trace level to the configured trace level or the default trace level.
            TRACER.setLevel(event.getServletContext().getInitParameter(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            // Get the application identifier.
            applicationId = event.getServletContext().getInitParameter(APPLICATION_ID_KEY);

            TRACER.trace(Level.CONFIG, "%s is %s", TRACE_LEVEL_KEY, TRACER.getLevel().getName());
            TRACER.trace(Level.CONFIG, "%s is %s", APPLICATION_ID_KEY, applicationId);
            
            // Register the MBean.
            this.objectId = MBeanUtility.register(ApplicationManager.class);
            TRACER.trace(Level.INFO, "registered the MBean %s (%s)", ApplicationManager.class.getName(), this.objectId);

            // Get the application manager.
            applicationManager = MBeanUtility.getMBean(ApplicationManager.class);

            // Get the authentication module class name.
            authModuleClassName = applicationManager.getAuthModuleClassName(applicationId);
            TRACER.trace(Level.FINE, "authModuleClassName is %s", authModuleClassName);
// TODO: remove this.
authModuleClassName = "org.lazydog.entry.security.module.PageServerAuthModule";

            // Register the authentication module.
            this.registrationId = AuthModuleUtility.registerAuthModule(
                    authModuleClassName,
                    event.getServletContext().getContextPath(),
                    event.getServletContext().getInitParameter(TRACE_LEVEL_KEY));
            TRACER.trace(Level.INFO, "registered the authentication module %s (%s) for %s", authModuleClassName, this.registrationId, event.getServletContext().getContextPath());

            // Put the application identifier in the application scope.
            event.getServletContext().setAttribute(APPLICATION_ID_KEY, applicationId);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
