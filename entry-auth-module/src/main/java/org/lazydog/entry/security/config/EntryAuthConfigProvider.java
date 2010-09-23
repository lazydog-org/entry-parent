package org.lazydog.entry.security.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import static javax.security.auth.message.MessagePolicy.ProtectionPolicy;
import static javax.security.auth.message.MessagePolicy.TargetPolicy;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import org.lazydog.utility.Tracer;


/**
 * Entry authentication configuration provider.
 *
 * @author  Ron Rickard
 */
public class EntryAuthConfigProvider implements AuthConfigProvider {

    private static final Tracer TRACER = Tracer.getTracer(EntryAuthConfigProvider.class.getName());

    private static final String IS_MANDATORY_KEY = "javax.security.auth.message.MessagePolicy.isMandatory";
    public static final String SUPPORTED_MESSAGE_LAYER = "HttpServlet";
    
    private static final String DEFAULT_CALLBACK_HANDLER_CLASS_NAME = "com.sun.enterprise.security.jmac.callback.ContainerCallbackHandler";
    private static final String DEFAULT_SERVER_AUTH_MODULE_CLASS_NAME = "org.lazydog.entry.security.module.ModalServerAuthModule";
    private static final Level DEFAULT_TRACE_LEVEL = Level.WARNING;
    
    public static final String CALLBACK_HANDLER_CLASS_NAME_KEY = "org.lazydog.entry.security.callbackHandlerClass";
    public static final String CONTEXT_PATH_KEY = "org.lazydog.entry.security.contextPath";
    public static final String SERVER_AUTH_MODULE_CLASS_NAME_KEY = "org.lazydog.entry.security.serverAuthModuleClass";
    public static final String TRACE_LEVEL_KEY = "org.lazydog.entry.security.traceLevel";

    private Map options;
    private String registrationID;

    /**
     * Create the Entry authentication configuration provider.
     *
     * @param  options            the options.
     * @param  authConfigFactory  the authentication configuration factory.
     */
    @SuppressWarnings("unchecked")
    public EntryAuthConfigProvider(Map options,
            AuthConfigFactory authConfigFactory) {

        TRACER.trace(Level.FINEST, "entering EntryAuthConfigProvider(%s, %s)",
                options, authConfigFactory);

        // If the options is null, create an empty Map.
        if (options == null) {
            options = new HashMap();
        }

        // If there is no trace level option, use the default.
        if (!options.containsKey(TRACE_LEVEL_KEY)) {
            options.put(TRACE_LEVEL_KEY, DEFAULT_TRACE_LEVEL.getName());
        }

        // If there is no callback handler class name option, use the default.
        if (!options.containsKey(CALLBACK_HANDLER_CLASS_NAME_KEY)) {
            options.put(CALLBACK_HANDLER_CLASS_NAME_KEY, DEFAULT_CALLBACK_HANDLER_CLASS_NAME);
        }

        // If there is no server authentication module class name option, use the default.
        if (!options.containsKey(SERVER_AUTH_MODULE_CLASS_NAME_KEY)) {
            options.put(SERVER_AUTH_MODULE_CLASS_NAME_KEY, DEFAULT_SERVER_AUTH_MODULE_CLASS_NAME);
        }

        // Set the trace level to the level name or the default trace level.
        TRACER.setLevel((String)options.get(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);
        TRACER.trace(Level.CONFIG, "%s is %s", TRACE_LEVEL_KEY, TRACER.getLevel().getName());
        TRACER.trace(Level.CONFIG, "%s is %s",
                CALLBACK_HANDLER_CLASS_NAME_KEY, (String)options.get(CALLBACK_HANDLER_CLASS_NAME_KEY));
        TRACER.trace(Level.CONFIG, "%s is %s",
                SERVER_AUTH_MODULE_CLASS_NAME_KEY, (String)options.get(SERVER_AUTH_MODULE_CLASS_NAME_KEY));

        // Check if the authentication configuration factory and a context path option exist.
        if (authConfigFactory != null && options.containsKey(CONTEXT_PATH_KEY)) {

            TRACER.trace(Level.CONFIG, "%s is %s",
                    CONTEXT_PATH_KEY, (String)options.get(CONTEXT_PATH_KEY));

            // Register the authentication configuration provider.
            this.registrationID = registerProvider(
                    authConfigFactory, this, SUPPORTED_MESSAGE_LAYER, (String)options.get(CONTEXT_PATH_KEY));

            // Remove the context path from the options (it is no longer needed.)
            options.remove(CONTEXT_PATH_KEY);
        }

        this.options = options;
    }

    /**
     * Get the application context.
     *
     * @param  contextPath  the context path.
     *
     * @return  the application context.
     */
    private static String getAppContext(String contextPath) {
        return new StringBuffer().append("server ").append(contextPath).toString();
    }

    /**
     * Get the client authentication configuration.
     *
     * @param  layer            the message layer.
     * @param  appContext       the application context.
     * @param  callbackHandler  the callback handler.
     *
     * @return  the client authentication configuration.
     *
     * @throws  AuthException  if unable to get the client authentication configuration.
     */
    @Override
    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, 
            CallbackHandler callbackHandler) throws AuthException {
        TRACER.trace(Level.FINEST, 
                "entering getClientAuthConfig(%s, %s, %s)",
                layer, appContext, callbackHandler);
        throw new AuthException("Not supported.");
    }

    /**
     * Get the registration identifier.
     * 
     * @return  the registration identifier.
     */
    public String getRegistrationID() {
        return this.registrationID;
    }

    /**
     * Get the server authentication configuration.
     *
     * @param  layer            the message layer.
     * @param  appContext       the application context.
     * @param  callbackHandler  the callback handler.
     *
     * @return  the server authentication configuration.
     *
     * @throws  AuthException  if unable to get the server authentication configuration.
     */
    @Override
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, 
            CallbackHandler callbackHandler) throws AuthException {
        TRACER.trace(Level.FINEST, 
                "entering getServerAuthConfig(%s, %s, %s)",
                layer, appContext, callbackHandler);
        return new EntryServerAuthConfig(layer, appContext, callbackHandler, this.options);
    }


    /**
     * Refresh the internal state of this authentication configuration provider.
     */
    @Override
    public void refresh() {
        TRACER.trace(Level.FINEST, "entering refresh()");
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Register the authentication configuration provider.
     *
     * @param  authConfigFactory  the authentication configuration factory.
     * @param  layer              the layer.
     * @param  appContext         the context path.
     *
     * @return  the registration identifier.
     */
    private static String registerProvider(AuthConfigFactory authConfigFactory,
            AuthConfigProvider authConfigProvider, String layer,
            String contextPath) {

        TRACER.trace(Level.INFO,
                "registering authentication configuration provider %s (%s, %s)",
                authConfigProvider.getClass().getName(), layer, getAppContext(contextPath));

        // Register this provider.
        return authConfigFactory.registerConfigProvider(
                authConfigProvider, layer, getAppContext(contextPath), null);

        
    }

    /**
     * Entry server authentication configuration.
     */
    protected class EntryServerAuthConfig implements ServerAuthConfig {
 
        private String appContext;
        private CallbackHandler callbackHandler;
        private String layer;
        private Map options;
        private ServerAuthModule serverAuthModule;

        /**
         * Create the Entry server authentication configuration.
         *
         * @param  layer            the message layer.
         * @param  appContext       the application context.
         * @param  callbackHandler  the callback handler.
         * @param  options          the options.
         *
         * @throws  AuthException  if unable to create the Entry server authentication configuration.
         */
        protected EntryServerAuthConfig(String layer, String appContext, 
                CallbackHandler callbackHandler, Map options)
                throws AuthException {

            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthContext(%s, %s, %s, %s)",
                    layer, appContext, callbackHandler, options);

            try {

                // If the callback handler does not exist, get the callback handler in the options.
                if (callbackHandler == null) {
                    callbackHandler = (CallbackHandler)createObject(
                            (String)options.get(CALLBACK_HANDLER_CLASS_NAME_KEY));
                }

                // Get the server authentication module in the options.
                this.serverAuthModule = (ServerAuthModule)createObject(
                        (String)options.get(SERVER_AUTH_MODULE_CLASS_NAME_KEY));

                this.layer = layer;
                this.appContext = appContext;
                this.callbackHandler = callbackHandler;
                this.options = options;
            }
            catch(Exception e) {

                // Declare.
                AuthException authException;

                authException = new AuthException();
                authException.initCause(e);
                throw authException;
            }
        }

        /**
         * Create the object.
         *
         * @param  className  the class name for the object.
         *
         * @return  the object.
         *
         * @throws  ClassNotFoundException  if the class is not found.
         * @throws  IllegalAccessException  if the class is not accessible.
         * @throws  InstantiationException  if the class instantiation fails.
         */
        private Object createObject(String className)
                throws ClassNotFoundException, IllegalAccessException,
                InstantiationException {

            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthContext.createObject(%s)",
                    className);

            // Declare.
            ClassLoader classLoader;
            Class objectClass;

            // Get the class loader.
            classLoader = Thread.currentThread().getContextClassLoader();

            // Create the class.
            objectClass = Class.forName(className, true, classLoader);

            TRACER.trace(Level.INFO, "creating instance of %s", className);
            
            // Create the object from the class.
            return objectClass.newInstance();
        }

        /**
         * Get the server authentication context.
         *
         * @param  authContextID   the authentication context
         * @param  serviceSubject  the service subject.
         * @param  options         the options.
         *
         * @return  the server authentication context.
         *
         * @throws  AuthException  if unable to get the server authentication context.
         */
        @Override
        @SuppressWarnings("unchecked")
        public ServerAuthContext getAuthContext(String authContextID, 
                Subject serviceSubject, Map options) throws AuthException {

            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthConfig.getAuthContext(%s, %s, %s)",
                    authContextID, serviceSubject, options);

            // Check if there are options.
            if (options != null && !options.isEmpty()) {

                // Add the options to the options.
                this.options.putAll(options);
            }

            return new EntryServerAuthContext(authContextID, this.serverAuthModule,
                    this.callbackHandler, this.options);
        }

        /**
         * Get the message layer.
         *
         * @return  the message layer.
         */
        @Override
        public String getMessageLayer() {
            TRACER.trace(Level.FINEST, "entering EntryServerAuthConfig.getMessageLayer()");
            return this.layer;
        }

        /**
         * Get the application context.
         *
         * @return  the application context.
         */
        @Override
        public String getAppContext() {
            TRACER.trace(Level.FINEST, "entering EntryServerAuthConfig.getAppContext()");
            return this.appContext;
        }

	/**
	 * Get the authentication context identifier.
	 *
	 * @param  messageInfo  the message information.
	 *
	 * @return the authentication context identifier.
	 *
	 * @throws  IllegalArgumentException  if the message layer is unsupported.
	 */
        @Override
        public String getAuthContextID(MessageInfo messageInfo) {

            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthConfig.getAuthContextID(%s)",
                    messageInfo);

            // Check if the message layer is the supported message layer.
            if (!SUPPORTED_MESSAGE_LAYER.equals(this.layer)) {
                throw new IllegalArgumentException("The message layer is unsupported.");
            }

            TRACER.trace(Level.FINE, "authContextID is %s",
                    Boolean.valueOf((String)messageInfo.getMap().get(IS_MANDATORY_KEY)).toString());

            // The authentication context identifier is "true" or "false".
            return Boolean.valueOf((String)messageInfo.getMap().get(IS_MANDATORY_KEY)).toString();
        }

        /**
         * Refresh the internal state of this server authentication configuration.
         */
        @Override
        public void refresh() {
            TRACER.trace(Level.FINEST, "entering EntryServerAuthConfig.refresh()");
            throw new UnsupportedOperationException("Not supported.");
        }

	/**
	 * Check if the server authentication configuration is protected.
         *
         * @return  true if the server authentication configuration is protected, otherwise false.
	 */
        @Override
	public boolean isProtected() {
            TRACER.trace(Level.FINEST, "entering EntryServerAuthConfig.isProtected()");
	    return true;
	}
    }

    /**
     * Entry server authentication context.
     */
    protected class EntryServerAuthContext implements ServerAuthContext {

        private ServerAuthModule serverAuthModule;

        /**
         * Create the Entry server authentication context.
         *
         * @param  authContextID    the authentication context identifier.
         * @param  serverAuthModule  the server authentication module.
         * @param  callbackHandler  the callback handler.
         * @param  options          the options.
         *
         * @throws  AuthException  if unable to create the Entry server authentication context.
         */
        protected EntryServerAuthContext(String authContextID,
                ServerAuthModule serverAuthModule,
                CallbackHandler callbackHandler, Map options) throws AuthException {

            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthContext(%s, %s, %s, %s)",
                    authContextID, serverAuthModule, callbackHandler, options);

            // Declare.
            List<TargetPolicy> targetPolicies;
            MessagePolicy requestPolicy;

            // Get the target policies.
            targetPolicies = new ArrayList<TargetPolicy>();
            targetPolicies.add(new TargetPolicy(null,
                new ProtectionPolicy() {
                    @Override
                    public String getID() {
                        return ProtectionPolicy.AUTHENTICATE_SENDER;
                    }
                })
            );

            // Get the request policy.
            requestPolicy = new MessagePolicy(
                    targetPolicies.toArray(new TargetPolicy[targetPolicies.size()]),
                    Boolean.parseBoolean(authContextID));

            // Initialize the server authentication module.
            serverAuthModule.initialize(requestPolicy, null, callbackHandler, options);

            this.serverAuthModule = serverAuthModule;
        }

        /**
         * Clean the subject.
         *
         * @param  messageInfo  the message information.
         * @param  subject      the subject.
         *
         * @throws  AuthException  if unable to clean the subject.
         */
        @Override
        public void cleanSubject(MessageInfo messageInfo, Subject subject)
                throws AuthException {
            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthContext.cleanSubject(%s, %s)",
                    messageInfo, subject);
            serverAuthModule.cleanSubject(messageInfo, subject);
        }

        /**
         * Secure the response before sending it to the client.
         *
         * @param  messageInfo     the message information.
         * @param  serviceSubject  the service subject.
         *
         * @return  AuthStatus.SEND_SUCCESS indicating the response is secure.
         *
         * @throws  AuthException  if unable to secure the response.
         */
        @Override
        public AuthStatus secureResponse(MessageInfo messageInfo,
                Subject serviceSubject) throws AuthException {
            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthContext.secureResponse(%s, %s)",
                    messageInfo, serviceSubject);
            return serverAuthModule.secureResponse(messageInfo, serviceSubject);
        }

        /**
         * Validate the request.
         *
         * @param  messageInfo     the message information.
         * @param  clientSubject   the client subject.
         * @param  serviceSubject  the service subject.
         *
         * @return  AuthStatus.SUCCESS indicating the request was successfully validated
         *          or AuthStatus.SEND_CONTINUE indicating the request validation is incomplete.
         *
         * @throws  AuthException  if unable to validate the request.
         */
        @Override
        public AuthStatus validateRequest(MessageInfo messageInfo, 
                Subject clientSubject, Subject serviceSubject)
                throws AuthException {
            TRACER.trace(Level.FINEST, 
                    "entering EntryServerAuthContext.validateRequest(%s, %s, %s)",
                    messageInfo, clientSubject, serviceSubject);
            return serverAuthModule.validateRequest(messageInfo, clientSubject, serviceSubject);
        }
    }
}
