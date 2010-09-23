package org.lazydog.entry.security.utility;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.message.config.AuthConfigFactory;
import org.lazydog.entry.security.config.EntryAuthConfigProvider;

/**
 * Authentication module utility.
 *
 * @author  Ron Rickard
 */
public class AuthModuleUtility {

    /**
     * Register the authentication module.
     *
     * @param  authModuleClassName  the authentication module class name.
     * @param  contextPath          the context path.
     * @param  traceLevel           the trace level.
     *
     * @return  the registration identifier.
     */
    public static String registerAuthModule(String authModuleClassName,
            String contextPath, String traceLevel) {

        // Declare.
        EntryAuthConfigProvider entryAuthConfigProvider;
        AuthConfigFactory factory;
        Map<String,String> options;

        // Set the options.
        options = new HashMap<String,String>();
        options.put(EntryAuthConfigProvider.SERVER_AUTH_MODULE_CLASS_NAME_KEY, authModuleClassName);
        options.put(EntryAuthConfigProvider.CONTEXT_PATH_KEY, contextPath);
        options.put(EntryAuthConfigProvider.TRACE_LEVEL_KEY, traceLevel);

        // Get the authentication configuration factory.
        factory = AuthConfigFactory.getFactory();

        // Register the authentication module (self-registering.)
        entryAuthConfigProvider = new EntryAuthConfigProvider(options, factory);

        return entryAuthConfigProvider.getRegistrationId();
    }

    /**
     * Unregister the authentication module.
     *
     * @param  registrationID  the registration identifier.
     */
    public static void unregisterAuthModule(String registrationID) {

        // Declare.
        AuthConfigFactory factory;

        // Get the authentication configuration factory.
        factory = AuthConfigFactory.getFactory();

        // Unregister the authentication module.
        factory.removeRegistration(registrationID);
    }
}
