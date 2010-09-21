package org.lazydog.entry.security.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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


/**
 * Entry server authentication module.
 *
 * This class is a JSR-196 based Server Authentication Module (SAM).
 *
 * @author  Ron Rickard
 */
public class EntryServerAuthModule implements ServerAuthModule {

    private static final Logger logger = Logger.getLogger(EntryServerAuthModule.class.getName());
    private static final Class[] supportedMessageTypes = new Class[] {
      HttpServletRequest.class,
      HttpServletResponse.class
    };
    private static final String AUTH_TYPE = "EntryAuthModule";
    private static final String AUTH_TYPE_KEY = "javax.servlet.http.authType";
    private static final String DEFAULT_LOGIN_URL = "/entry/pages/login.jsf";
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;
    private static final int DEFAULT_SESSION_TIMEOUT = -1;
    private static final String GROUPS_KEY = "org.lazydog.entry.auth.module.groups";
    private static final String LOGIN_ACTION = "/entry-login";
    private static final String LOGIN_URL_KEY = "login.url";
    private static final String LOGIN_URL_PARAMETER = "loginURL";
    private static final String LOG_LEVEL_KEY = "log.level";
    private static final String LOGOUT_ACTION = "/entry-logout";
    private static final String LOGOUT_URL_PARAMETER = "logoutURL";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String RETRY_KEY = "org.lazydog.entry.auth.module.retry";
    private static final String RETURN_URL_KEY = "org.lazydog.entry.auth.module.returnURL";
    private static final String RETURN_URL_PARAMETER = "returnURL";
    private static final String SESSION_TIMEOUT_KEY = "session.timeout";
    private static final String USERNAME_KEY = "org.lazydog.entry.auth.module.username";
    private static final String USERNAME_PARAMETER = "username";

    private CallbackHandler callbackHandler;
    private String loginURL;
    private MessagePolicy requestPolicy;
    private MessagePolicy responsePolicy;
    private int sessionTimeout;

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
    private boolean authenticate(HttpServletRequest request, Subject subject) throws AuthException {

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

                trace(Level.INFO, "authenticating username %s", username);
                
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

                    trace(Level.INFO, "username %s is authenticated", username);

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

                    trace(Level.FINE, "session set attributeMap['%s'] is %s", GROUPS_KEY, groups.toArray(new String[groups.size()]));
                    trace(Level.FINE, "session set attributeMap['%s'] is %s", USERNAME_KEY, username);
                }
                else {
                    trace(Level.INFO, "username %s is not authenticated", username);
                }
            }
        }
        catch(Exception e) {
            trace(Level.WARNING, "unable to authenticate username " + username);
        }

        trace(Level.FINE, "authenticate return %s", isAuthenticated);
        return isAuthenticated;
    }

    /**
     * Clear the subject.
     *
     * @param  messageInfo  the message information.
     * @param  subject      the subject.
     *
     * @throws  AuthException  if unable to clear the subject.
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
    private void clearSession(HttpServletRequest request) {

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
        return supportedMessageTypes;
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
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy,
            CallbackHandler callbackHandler, Map options) throws AuthException {

        try {

            // Set the login URL to the supplied login URL
            // or the default login URL if the supplied login URL does not exist.
            this.loginURL = (options.get(LOGIN_URL_KEY) == null)
                    ? DEFAULT_LOGIN_URL : (String)options.get(LOGIN_URL_KEY);

            try {

                // Set the log level to the supplied log level.
                logger.setLevel(Level.parse((String)options.get(LOG_LEVEL_KEY)));
            }
            catch(Exception e) {

                // The supplied log level is invalid,
                // so set the log level to the default log level.
                logger.setLevel(DEFAULT_LOG_LEVEL);
            }

            try {

                // Set the session timeout to the supplied session timeout.
                this.sessionTimeout = Integer.parseInt((String)options.get(SESSION_TIMEOUT_KEY));
            }
            catch(Exception e) {

                // The supplied session timeout is invalid,
                // so set the session timeout to the default session timeout.
                this.sessionTimeout = DEFAULT_SESSION_TIMEOUT;
            }

            trace(Level.CONFIG, "%s is %s", LOG_LEVEL_KEY, logger.getLevel());
            trace(Level.CONFIG, "%s is %s", LOGIN_URL_KEY, this.loginURL);
            trace(Level.CONFIG, "%s is %s", SESSION_TIMEOUT_KEY, this.sessionTimeout);

            this.callbackHandler = callbackHandler;
            this.requestPolicy = requestPolicy;
            this.responsePolicy = responsePolicy;
        }
        catch(Exception e) {
            AuthException authException;
            authException = new AuthException();
            authException.initCause(e);
            throw authException;
        }
    }

    /**
     * Check if the request has been authenticated.
     *
     * @param  request  the HTTP servlet request.
     *
     * @return  true if the request has been authenticated, otherwise false.
     */
    private static boolean isAuthenticated(HttpServletRequest request) {
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
    private static boolean isRequestURIAction(HttpServletRequest request, String action) {
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
    private void redirectTo(HttpServletResponse response, String redirectURL) throws AuthException {

        try {

            // Declare.
            String encodedRedirectURL;

            // Redirect the response to the encoded redirect URL.
            encodedRedirectURL = response.encodeRedirectURL(redirectURL);
            response.sendRedirect(encodedRedirectURL);
        }
        catch(Exception e) {
            AuthException authException;
            authException = new AuthException();
            authException.initCause(e);
            throw authException;
        }
    }

    /**
     * Respond with the login URL.
     *
     * @param  request   the HTTP servlet request.
     * @param  response  the HTTP servlet response.
     *
     * @throws  AuthException  if unable to respond with the login URL.
     */
    private void respondWithLoginURL(HttpServletRequest request, HttpServletResponse response) throws AuthException {

        // Declare.
        String loginURL;

        loginURL = request.getParameter(LOGIN_URL_PARAMETER);

        if (loginURL == null) {
            request.getSession().setAttribute(RETURN_URL_KEY, request.getRequestURI());

            trace(Level.FINE, "session set attributeMap['%s'] is %s", RETURN_URL_KEY, request.getRequestURI());

            loginURL = this.loginURL;
        }
        else {
            request.getSession().setAttribute(RETRY_KEY, "true");
            request.getSession().setAttribute(RETURN_URL_KEY, request.getParameter(RETURN_URL_PARAMETER));
            request.getSession().setAttribute(USERNAME_KEY, request.getParameter(USERNAME_PARAMETER));

            trace(Level.FINE, "session set attributeMap['%s'] is %s", RETRY_KEY, "true");
            trace(Level.FINE, "session set attributeMap['%s'] is %s", RETURN_URL_KEY, request.getParameter(RETURN_URL_PARAMETER));
            trace(Level.FINE, "session set attributeMap['%s'] is %s", USERNAME_KEY, request.getParameter(USERNAME_PARAMETER));
        }

        // Respond with the login URL.
        redirectTo(response, loginURL);
    }

    /**
     * Respond with the return URL.
     *
     * @param  request   the HTTP servlet request.
     * @param  response  the HTTP servlet response.
     *
     * @throws  AuthException  if unable to respond with the return URL.
     */
    private void respondWithReturnURL(HttpServletRequest request, HttpServletResponse response) throws AuthException {

            // Declare.
            String returnURL;

            returnURL = request.getParameter(RETURN_URL_PARAMETER);

            if (returnURL == null) {
                returnURL = request.getParameter(LOGIN_URL_PARAMETER);

                if (returnURL == null) {
                    returnURL = request.getParameter(LOGOUT_URL_PARAMETER);
                }
            }

            request.getSession().removeAttribute(RETRY_KEY);
            request.getSession().removeAttribute(RETURN_URL_KEY);

            trace(Level.FINE, "session remove attributeMap['%s']", RETRY_KEY);
            trace(Level.FINE, "session remove attributeMap['%s']", RETURN_URL_KEY);

            // Respond with the return URL.
            redirectTo(response, returnURL);
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
    private void setPrincipals(HttpServletRequest request, Subject subject) throws AuthException {

        try {

            // Declare.
            CallerPrincipalCallback callerPrincipalCallback;
            GroupPrincipalCallback groupPrincipalCallback;
            String[] groups;
            String username;

            groups = (String[])request.getSession().getAttribute(GROUPS_KEY);
            username = (String)request.getSession().getAttribute(USERNAME_KEY);

            trace(Level.FINE, "session get attributeMap['%s'] is %s", GROUPS_KEY, (String[])request.getSession().getAttribute(GROUPS_KEY));
            trace(Level.FINE, "session get attributeMap['%s'] is %s", USERNAME_KEY, (String)request.getSession().getAttribute(USERNAME_KEY));

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
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject,
            Subject serviceSubject) throws AuthException {

        // Declare.
        AuthStatus authStatus;
        HttpServletRequest request;
        HttpServletResponse response;

        // Assume the validation is incomplete.
        authStatus = AuthStatus.SEND_CONTINUE;

        // Get the request and response.
        request = (HttpServletRequest)messageInfo.getRequestMessage();
        response = (HttpServletResponse)messageInfo.getResponseMessage();

        trace(Level.FINE, "request get parameterMap['%s'] is %s", LOGIN_URL_PARAMETER, request.getParameter(LOGIN_URL_PARAMETER));
        trace(Level.FINE, "request get parameterMap['%s'] is %s", LOGOUT_URL_PARAMETER, request.getParameter(LOGOUT_URL_PARAMETER));
        trace(Level.FINE, "request get parameterMap['%s'] is %s", RETURN_URL_PARAMETER, request.getParameter(RETURN_URL_PARAMETER));
        trace(Level.FINE, "request get parameterMap['%s'] is %s", USERNAME_PARAMETER, request.getParameter(USERNAME_PARAMETER));
       
        // Check if the request URI is the login action.
        if (isRequestURIAction(request, LOGIN_ACTION)) {

            trace(Level.INFO, "accessing login action %s", request.getRequestURI());

            // Authenticate the request.
            if (authenticate(request, clientSubject)) {
                respondWithReturnURL(request, response);
            }
            else {
                respondWithLoginURL(request, response);
            }
        }

        // Check if the request URI is the logout action.
        else if (isRequestURIAction(request, LOGOUT_ACTION)) {

            trace(Level.INFO, "accessing logout action %s", request.getRequestURI());

            clearSession(request);
            respondWithReturnURL(request, response);
        }
        else {

            // Check if the request has been authenticated.
            if (isAuthenticated(request)) {

                trace(Level.INFO, "accessing page %s", request.getRequestURI());

                // Set the principals.
                setPrincipals(request, clientSubject);
                messageInfo.getMap().put(AUTH_TYPE_KEY, AUTH_TYPE);

                // Validation is complete.
                authStatus = AuthStatus.SUCCESS;
            }
            else {

                // Check if the request is for a secure page.
                if (this.requestPolicy.isMandatory()) {

                    trace(Level.INFO, "accessing secured page %s", request.getRequestURI());
                    respondWithLoginURL(request, response);
                }
                else {

                    trace(Level.INFO, "accessing unsecured page %s", request.getRequestURI());
                    
                    // Validation is not required.
                    authStatus = AuthStatus.SUCCESS;
                }
            }
        }

        trace(Level.FINE, "validate request return %s", authStatus);
        return authStatus;
    }
}
