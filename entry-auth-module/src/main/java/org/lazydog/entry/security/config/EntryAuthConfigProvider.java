package org.lazydog.entry.security.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.lazydog.entry.security.module.EntryServerAuthModule;


/**
 * Entry authentication configuration provider.
 *
 * @author  Ron Rickard
 */
public class EntryAuthConfigProvider implements AuthConfigProvider {

    private static final String HTTPSERVLET = "HttpServlet";
    private static final String IS_MANDATORY = "javax.security.auth.message.MessagePolicy.isMandatory";
    private static final String DEFAULT_CALLBACK_HANDLER_CLASS = "com.sun.enterprise.security.jmac.callback.ContainerCallbackHandler";
    private AuthConfigFactory factory;

    public EntryAuthConfigProvider(Map properties, AuthConfigFactory factory) {
        this.factory = factory;
    }

    /**
     * Get an instance of ClientAuthConfig from this provider.
     *
     * <p> The implementation of this method returns a ClientAuthConfig
     * instance that describes the configuration of ClientAuthModules
     * at a given message layer, and for use in an identified application
     * context.
     *
     * @param layer a String identifying the message layer
     *                for the returned ClientAuthConfig object.
     *          This argument must not be null.
     *
     * @param appContext a String that identifies the messaging context
     *          for the returned ClientAuthConfig object.
     *          This argument must not be null.
     *
     * @param handler a CallbackHandler to be passed to the ClientAuthModules
     *                encapsulated by ClientAuthContext objects derived from
     *                the returned ClientAuthConfig. This argument may be null,
     *                in which case the implementation may assign a default
     *                handler to the configuration.
     *
     * @return a ClientAuthConfig Object that describes the configuration
     *                of ClientAuthModules at the message layer and messaging
     *                context identified by the layer and appContext arguments.
     *                This method does not return null.
     *
     * @exception AuthException if this provider does not support the
     *          assignment of a default CallbackHandler to the returned
     *          ClientAuthConfig.
     *
     * @exception SecurityException if the caller does not have permission
     *                to retrieve the configuration.
     *
     * The CallbackHandler assigned to the configuration must support
     * the Callback objects required to be supported by the profile of this
     * specification being followed by the messaging runtime.
     * The CallbackHandler instance must be initialized with any application
     * context needed to process the required callbacks
     * on behalf of the corresponding application.
     */
    @Override
    public ClientAuthConfig getClientAuthConfig
            (String layer, String appContext, CallbackHandler handler)
            throws AuthException {
        // TODO: handle this.
        throw new AuthException("Not supported.");
    }

    /**
     * Get an instance of ServerAuthConfig from this provider.
     *
     * <p> The implementation of this method returns a ServerAuthConfig
     * instance that describes the configuration of ServerAuthModules
     * at a given message layer, and for a particular application context.
     *
     * @param layer a String identifying the message layer
     *                for the returned ServerAuthConfig object.
     *          This argument must not be null.
     *
     * @param appContext a String that identifies the messaging context
     *          for the returned ServerAuthConfig object.
     *          This argument must not be null.
     *
     * @param handler a CallbackHandler to be passed to the ServerAuthModules
     *                encapsulated by ServerAuthContext objects derived from
     *                thr returned ServerAuthConfig. This argument may be null,
     *                in which case the implementation may assign a default
     *                handler to the configuration.
     *
     * @return a ServerAuthConfig Object that describes the configuration
     *                of ServerAuthModules at a given message layer,
     *                and for a particular application context.
     *                This method does not return null.
     *
     * @exception AuthException if this provider does not support the
     *          assignment of a default CallbackHandler to the returned
     *          ServerAuthConfig.
     *
     * @exception SecurityException if the caller does not have permission
     *                to retrieve the configuration.
     * <p>
     * The CallbackHandler assigned to the configuration must support
     * the Callback objects required to be supported by the profile of this
     * specification being followed by the messaging runtime.
     * The CallbackHandler instance must be initialized with any application
     * context needed to process the required callbacks
     * on behalf of the corresponding application.
     */
    @Override
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler callbackHandler)
            throws AuthException {
        return new EntryServerAuthConfig(layer, appContext, callbackHandler);
    }


    /**
     * Causes a dynamic configuration provider to update its internal
     * state such that any resulting change to its state is reflected in
     * the corresponding authentication context configuration objects
     * previously created by the provider within the current process context.
     *
     * @exception AuthException if an error occured during the refresh.
     *
     * @exception SecurityException if the caller does not have permission
     *                to refresh the provider.
     */
    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported.");
    }

    protected class EntryServerAuthConfig implements ServerAuthConfig {

        private String layer;
        private String appContext;
        private CallbackHandler callbackHandler;

        private EntryServerAuthConfig(String layer, String appContext, CallbackHandler callbackHandler) 
                throws AuthException {

            this.layer = layer;
            this.appContext = appContext;

            if (callbackHandler == null) {

                try {

                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    Class c = Class.forName(DEFAULT_CALLBACK_HANDLER_CLASS, true, loader);
                    this.callbackHandler = (CallbackHandler)c.newInstance();
                }
                catch(Exception e) {
                    AuthException authException = new AuthException();
                    authException.initCause(e);
                    throw authException;
                }
            }
        }

        @Override
        public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map options)
                throws AuthException {
            return new EntryServerAuthContext(authContextID, callbackHandler, options);
        }

        /**
         * Get the message layer name of this authentication context
	 * configuration object.
         *
         * @return the message layer name of this configuration object, or null
         * if the configuration object pertains to an unspecified message
	 * layer.
         */
        @Override
        public String getMessageLayer() {
            return layer;
        }

        /**
         * Get the application context identifier of this authentication
         * context configuration object.
         *
         * @return the String identifying the application context of this
         * configuration object or null if the configuration object pertains
         * to an unspecified application context.
         */
        @Override
        public String getAppContext() {
            return appContext;
        }

	/**
	 * Get the authentication context identifier corresponding to the
	 * request and response objects encapsulated in messageInfo.
	 *
	 * @param messageInfo a contextual Object that encapsulates the
	 *          client request and server response objects.
	 *
	 * @return the authentication context identifier corresponding to the
	 *          encapsulated request and response objects, or null.
	 *
	 * @throws IllegalArgumentException if the type of the message
	 * objects incorporated in messageInfo are not compatible with
	 * the message types supported by this
	 * authentication context configuration object.
	 */
        @Override
        public String getAuthContextID(MessageInfo messageInfo) {
            if (EntryAuthConfigProvider.HTTPSERVLET.equals(layer)) {
		String isMandatoryStr =
		    (String)messageInfo.getMap().get(IS_MANDATORY);
		return Boolean.valueOf(isMandatoryStr).toString();
	    } else {
                return null;
            }
        }

        // we should be able to replace the following with a method on packet

        /**
         * Causes a dynamic anthentication context configuration object to
         * update the internal state that it uses to process calls to its
         * <code>getAuthContext</code> method.
         *
         * @exception AuthException if an error occured during the update.
         *
         * @exception SecurityException if the caller does not have permission
         *                to refresh the configuration object.
         */
        @Override
        public void refresh() {
            throw new UnsupportedOperationException("Not supported.");
        }

	/**
	 * Used to determine whether or not the <code>getAuthContext</code>
	 * method of the authentication context configuration will return null
	 * for all possible values of authentication context identifier.
	 *
	 * @return false when <code>getAuthContext</code> will return null for
	 *        all possible values of authentication context identifier.
	 *        Otherwise, this method returns true.
	 */
        @Override
	public boolean isProtected() {
	    return true;
	}
    }

    protected class EntryServerAuthContext implements ServerAuthContext {

        private ServerAuthModule serverAuthModule;

        private EntryServerAuthContext(String authContextID, CallbackHandler callbackHandler, Map options)
                throws AuthException {

            List<TargetPolicy> targetPolicies = new ArrayList<TargetPolicy>();
            targetPolicies.add(new TargetPolicy(null,
                new ProtectionPolicy() {
                    @Override
                    public String getID() {
                        return ProtectionPolicy.AUTHENTICATE_SENDER;
                    }
                })
            );
            MessagePolicy requestPolicy = new MessagePolicy(
                    targetPolicies.toArray(new TargetPolicy[targetPolicies.size()]),
                    Boolean.parseBoolean(authContextID));

            this.serverAuthModule = new EntryServerAuthModule();

            if (options == null) {
                options = new HashMap<String,String>();
            }

            this.serverAuthModule.initialize(requestPolicy, null, callbackHandler, options);
        }

        @Override
        public AuthStatus validateRequest(MessageInfo messageInfo,
                Subject clientSubject, Subject serviceSubject)
                throws AuthException {

            if (serverAuthModule == null) {
                throw new AuthException();
            }

            return serverAuthModule.validateRequest(messageInfo, clientSubject, serviceSubject);
        }

        @Override
        public AuthStatus secureResponse(MessageInfo messageInfo,
                Subject serviceSubject) throws AuthException {

            if (serverAuthModule == null) {
                throw new AuthException();
            }

            return serverAuthModule.secureResponse(messageInfo, serviceSubject);
        }

        @Override
        public void cleanSubject(MessageInfo messageInfo, Subject subject)
                throws AuthException {

            if (serverAuthModule == null) {
                 throw new AuthException();
            }

            serverAuthModule.cleanSubject(messageInfo, subject);
        }
    }
}
