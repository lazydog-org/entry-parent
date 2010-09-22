package org.lazydog.entry.security.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.PasswordValidationCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.lazydog.utility.Tracer;


/**
 * Entry server authentication module.
 *
 * This class is a JSR-196 based Server Authentication Module (SAM).
 *
 * @author  Ron Rickard
 */
public abstract class EntryServerAuthModule implements ServerAuthModule {

    protected static final Tracer TRACER = Tracer.getTracer(EntryServerAuthModule.class.getName());
    private static final Class[] SUPPORTED_MESSAGE_TYPES = new Class[] {
      HttpServletRequest.class,
      HttpServletResponse.class
    };

    protected static final Level DEFAULT_TRACE_LEVEL = Level.WARNING;

    protected static final String LOGIN_ACTION = "/entry-login";
    protected static final String LOGOUT_ACTION = "/entry-logout";

    protected static final String PASSWORD_PARAMETER = "password";
    protected static final String USERNAME_PARAMETER = "username";

    protected static final String AUTH_TYPE_KEY = "javax.servlet.http.authType";
    protected static final String GROUPS_KEY = "org.lazydog.entry.security.groups";
    protected static final String TRACE_LEVEL_KEY = "org.lazydog.entry.security.traceLevel";
    protected static final String RETRY_KEY = "org.lazydog.entry.security.retry";
    protected static final String USERNAME_KEY = "org.lazydog.entry.security.username";

    protected CallbackHandler callbackHandler;
    protected MessagePolicy requestPolicy;

    /**
     * Authenticate the request.
     *
     * @param  request  the request.
     * @param  subject  the client subject.
     *
     * @return  true if the request is authenticated, otherwise false.
     *
     * @throws  AuthException  if unable to authenticate the request.
     */
    protected boolean authenticate(HttpServletRequest request, Subject subject) throws AuthException {

        // Declare.
        boolean isAuthenticated;
        String username;

        // Set authenticated to false.
        isAuthenticated = false;

        // Get the username.
        username = request.getParameter(USERNAME_PARAMETER);
        
        try {

            // Declare.
            String password;

            // Get the password.
            password = request.getParameter(PASSWORD_PARAMETER);

            // Check if there is a username and password.
            if (exists(username) && exists(password)) {
                
                // Declare.
                PasswordValidationCallback passwordValidationCallback;

                TRACER.trace(Level.INFO, "authenticating username %s", username);
                
                // Authenticate the username and password.
                passwordValidationCallback = new PasswordValidationCallback(subject, username, password.toCharArray());
                callbackHandler.handle(new Callback[]{passwordValidationCallback});
                passwordValidationCallback.clearPassword();
                isAuthenticated = passwordValidationCallback.getResult();

                // Check if username and password are authenticated.
                if (isAuthenticated) {

                    // Declare.
                    List<String> groups;

                    // Initialize.
                    groups = new ArrayList<String>();

                    TRACER.trace(Level.INFO, "username %s is authenticated", username);

                    // Loop through the principals.
                    for (Principal principal : passwordValidationCallback.getSubject().getPrincipals()) {

                        // Check if the principal name is not the username.
                        if (!principal.getName().equals(username)) {

                            // Add the principal name to the groups.
                            groups.add(principal.getName());
                        }
                    }

                    // Put the groups and username on the session.
                    request.getSession().setAttribute(GROUPS_KEY, groups.toArray(new String[groups.size()]));
                    request.getSession().setAttribute(USERNAME_KEY, username);

                    TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", GROUPS_KEY, groups.toArray(new String[groups.size()]));
                    TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", USERNAME_KEY, username);
                }
                else {
                    TRACER.trace(Level.INFO, "username %s is not authenticated", username);
                }
            }
        }
        catch(Exception e) {
            TRACER.trace(Level.WARNING, "unable to authenticate username " + username);
        }

        TRACER.trace(Level.FINE, "authenticate return %s", isAuthenticated);
        return isAuthenticated;
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
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {

        // Check if the subject exists.
        if (subject != null) {
            
            // Check if the subject has principals.
            if (subject.getPrincipals() != null) {
                
                // Clear the principals.
                subject.getPrincipals().clear();
            }

            // Check if the subject has private credentials.
            if (subject.getPrivateCredentials() != null) {

                // Clear the private credentials.
                subject.getPrivateCredentials().clear();
            }

            // Check if the subject has public credentials.
            if (subject.getPublicCredentials() != null) {

                // Clear the public credentials.
                subject.getPublicCredentials().clear();
            }
        }
    }

    /**
     * Clear the session.
     *
     * @param  request  the HTTP servlet request.
     */
    protected void clearSession(HttpServletRequest request) {

        // Check if the session is valid.
        if (request.isRequestedSessionIdValid()) {

            // Clear the session.
            request.getSession().invalidate();
        }
    }

    /**
     * Check if the specified value is not null and not empty.
     *
     * @param  value  the value.
     *
     * @return  true if the value is not null and not empty, otherwise false.
     */
    private static boolean exists(String value) {
        return (value != null && !value.isEmpty());
    }

    /**
     * Get the supported message types.
     * The supported message types are HttpServetRequest and HttpServletResponse.
     *
     * @return  the supported message types.
     */
    @Override
    public Class[] getSupportedMessageTypes() {
        return SUPPORTED_MESSAGE_TYPES;
    }

    /**
     * Initialize the SAM with request and response policies,
     * a callback handler, and configuration properties.
     *
     * @param  requestPolicy    the request message policy.
     * @param  responsePolicy   the response message policy.
     * @param  callbackHandler  the callback handler.
     * @param  options          the configuration properties.
     *
     * @throws  AuthException  if unable to initialize the SAM.
     */
    @Override
    public abstract void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy,
            CallbackHandler callbackHandler, Map options) throws AuthException;

    /**
     * Check if the request has been authenticated.
     *
     * @param  request  the HTTP servlet request.
     *
     * @return  true if the request has been authenticated, otherwise false.
     */
    protected static boolean isAuthenticated(HttpServletRequest request) {
        return (request.isRequestedSessionIdValid() && request.getSession().getAttribute(GROUPS_KEY) != null);
    }

    /**
     * Check if the request URI is the specified action.
     *
     * @param  request  the HTTP servlet request.
     * @param  action   the action.
     *
     * @return  true if the request URI is the specified action, other false.
     */
    protected static boolean isRequestURIAction(HttpServletRequest request, String action) {
        return (request.getRequestURI().endsWith(action));
    }

    /**
     * Redirect the response to the specified redirect URL.
     *
     * @param  response     the HTTP servlet response.
     * @param  redirectURL  the redirect URL.
     *
     * @throws  AuthException  if unable to redirect the response.
     */
    protected void redirectTo(HttpServletResponse response, String redirectURL) throws AuthException {

        try {

            // Declare.
            String encodedRedirectURL;

            // Redirect the response to the encoded redirect URL.
            encodedRedirectURL = response.encodeRedirectURL(redirectURL);
            response.sendRedirect(encodedRedirectURL);

            TRACER.trace(Level.INFO, "redirect to URL %s", redirectURL);
        }
        catch(Exception e) {
            AuthException authException;
            authException = new AuthException();
            authException.initCause(e);
            throw authException;
        }
    }

    /**
     * Secure the response before sending it to the client.
     *
     * @param  messageInfo  the message information.
     * @param  subject      the service subject.
     *
     * @return  AuthStatus.SEND_SUCCESS indicating the response is secure.
     *
     * @throws  AuthException  if unable to secure the response.
     */
    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject subject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    /**
     * Set the principals.
     * This method is where the JEE security principals are defined.
     *
     * @param  request  the HTTP servlet request.
     * @param  subject  the subject.
     *
     * @throws  AuthException  if unable to set the principals.
     */
    protected void setPrincipals(HttpServletRequest request, Subject subject) throws AuthException {

        try {

            // Declare.
            CallerPrincipalCallback callerPrincipalCallback;
            GroupPrincipalCallback groupPrincipalCallback;
            String[] groups;
            String username;

            groups = (String[])request.getSession().getAttribute(GROUPS_KEY);
            username = (String)request.getSession().getAttribute(USERNAME_KEY);

            TRACER.trace(Level.FINE, "session get attributeMap['%s'] is %s", GROUPS_KEY, (String[])request.getSession().getAttribute(GROUPS_KEY));
            TRACER.trace(Level.FINE, "session get attributeMap['%s'] is %s", USERNAME_KEY, (String)request.getSession().getAttribute(USERNAME_KEY));

            callerPrincipalCallback = new CallerPrincipalCallback(subject, username);
            groupPrincipalCallback = new GroupPrincipalCallback(subject, groups);

            callbackHandler.handle(new Callback[] {callerPrincipalCallback, groupPrincipalCallback});
        }
        catch(Exception e) {
            AuthException authException;
            authException = new AuthException();
            authException.initCause(e);
            throw authException;
        }
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
    public abstract AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject,
            Subject serviceSubject) throws AuthException;
}
