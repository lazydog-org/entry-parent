package org.lazydog.entry.security.module;

import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
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
public class PageServerAuthModule extends EntryServerAuthModule implements ServerAuthModule {

    private static final String DEFAULT_LOGIN_URL = "/entry/pages/login.jsf";

    private static final String AUTH_TYPE = "PageEntryAuthModule";

    private static final String CURRENT_URL_PARAMETER = "currentURL";
    private static final String RETURN_URL_PARAMETER = "returnURL";

    private static final String LOGIN_URL_KEY = "org.lazydog.entry.security.loginURL";
    private static final String RETURN_URL_KEY = "org.lazydog.entry.security.returnURL";

    private String loginURL;

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
            this.loginURL = (!options.containsKey(LOGIN_URL_KEY))
                    ? DEFAULT_LOGIN_URL : (String)options.get(LOGIN_URL_KEY);

            // Set the trace level to the level name or the default trace level.
            TRACER.setLevel((String)options.get(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            TRACER.trace(Level.CONFIG, "%s is %s", TRACE_LEVEL_KEY, TRACER.getLevel());
            TRACER.trace(Level.CONFIG, "%s is %s", LOGIN_URL_KEY, this.loginURL);

            this.callbackHandler = callbackHandler;
            this.requestPolicy = requestPolicy;
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
     * @param  retry     true to retry login, otherwise false.
     *
     * @throws  AuthException  if unable to respond with the login URL.
     */
    private void respondWithLoginURL(HttpServletRequest request, HttpServletResponse response, boolean retry) throws AuthException {

        // Declare.
        String loginURL;

        // Get the current URL.
        loginURL = request.getParameter(CURRENT_URL_PARAMETER);

        // Check if the login is to be retried.
        if (retry) {

            // Put the retry, return URL, and username on the session.
            request.getSession().setAttribute(RETRY_KEY, retry);
            request.getSession().setAttribute(RETURN_URL_KEY, request.getParameter(RETURN_URL_PARAMETER));
            request.getSession().setAttribute(USERNAME_KEY, request.getParameter(USERNAME_PARAMETER));

            TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", RETRY_KEY, retry);
            TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", RETURN_URL_KEY, request.getParameter(RETURN_URL_PARAMETER));
            TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", USERNAME_KEY, request.getParameter(USERNAME_PARAMETER));
        }
        else {

            // Get the login URL.
            loginURL = this.loginURL;

            // Put the return URL on the session.
            request.getSession().setAttribute(RETURN_URL_KEY, request.getRequestURI());

            TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", RETURN_URL_KEY, request.getRequestURI());
        }

        // Redirect to the login URL.
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

        // Get the return URL.
        returnURL = request.getParameter(RETURN_URL_PARAMETER);

        // Check if the session is valid.
        if (request.isRequestedSessionIdValid()) {

            // Remove the retry and return URL from the session.
            request.getSession().removeAttribute(RETRY_KEY);
            request.getSession().removeAttribute(RETURN_URL_KEY);

            TRACER.trace(Level.FINE, "session remove attributeMap['%s']", RETRY_KEY);
            TRACER.trace(Level.FINE, "session remove attributeMap['%s']", RETURN_URL_KEY);
        }

        // Redirect to the return URL.
        redirectTo(response, returnURL);
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
    @SuppressWarnings("unchecked")
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

        TRACER.trace(Level.FINE, "request get parameterMap['%s'] is %s", CURRENT_URL_PARAMETER, request.getParameter(CURRENT_URL_PARAMETER));
        TRACER.trace(Level.FINE, "request get parameterMap['%s'] is %s", RETURN_URL_PARAMETER, request.getParameter(RETURN_URL_PARAMETER));
        TRACER.trace(Level.FINE, "request get parameterMap['%s'] is %s", USERNAME_PARAMETER, request.getParameter(USERNAME_PARAMETER));
       
        // Check if the request URI is the login action.
        if (isRequestURIAction(request, LOGIN_ACTION)) {

            TRACER.trace(Level.INFO, "accessing login action %s", request.getRequestURI());

            // Authenticate the request.
            if (authenticate(request, clientSubject)) {
                respondWithReturnURL(request, response);
            }
            else {
                respondWithLoginURL(request, response, true);
            }
        }

        // Check if the request URI is the logout action.
        else if (isRequestURIAction(request, LOGOUT_ACTION)) {

            TRACER.trace(Level.INFO, "accessing logout action %s", request.getRequestURI());

            clearSession(request);
            respondWithReturnURL(request, response);
        }
        else {

            // Check if the request has been authenticated.
            if (isAuthenticated(request)) {

                TRACER.trace(Level.INFO, "accessing page %s", request.getRequestURI());

                // Set the principals.
                setPrincipals(request, clientSubject);
                messageInfo.getMap().put(AUTH_TYPE_KEY, AUTH_TYPE);

                // Validation is complete.
                authStatus = AuthStatus.SUCCESS;
            }
            else {

                // Check if the request is for a secure page.
                if (this.requestPolicy.isMandatory()) {

                    TRACER.trace(Level.INFO, "accessing secured page %s", request.getRequestURI());
                    respondWithLoginURL(request, response, false);
                }
                else {

                    TRACER.trace(Level.INFO, "accessing unsecured page %s", request.getRequestURI());
                    
                    // Validation is not required.
                    authStatus = AuthStatus.SUCCESS;
                }
            }
        }

        TRACER.trace(Level.FINE, "validate request return %s", authStatus);
        return authStatus;
    }
}
