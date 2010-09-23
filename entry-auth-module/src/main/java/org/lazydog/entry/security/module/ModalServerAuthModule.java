package org.lazydog.entry.security.module;

import java.io.IOException;
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
 * Modal server authentication module.
 *
 * This class is a JSR-196 based Server Authentication Module (SAM).
 *
 * @author  Ron Rickard
 */
public class ModalServerAuthModule extends EntryServerAuthModule implements ServerAuthModule {

    private static final String AUTH_TYPE = "ModalServerAuthModule";

     protected static final String CURRENT_LOGIN_URL_PARAMETER = "currentLoginURL";
      protected static final String CURRENT_LOGOUT_URL_PARAMETER = "currentLogoutURL";
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

            // Set the trace level to the level name or the default trace level.
            TRACER.setLevel((String)options.get(TRACE_LEVEL_KEY), DEFAULT_TRACE_LEVEL);

            TRACER.trace(Level.CONFIG, "%s is %s", TRACE_LEVEL_KEY, TRACER.getLevel().getName());

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
     * Respond with the current URL.
     *
     * @param  request   the HTTP servlet request.
     * @param  response  the HTTP servlet response.
     * @param  retry     true to retry login, otherwise false.
     *
     * @throws  AuthException  if unable to respond with the current URL.
     */
    private void respondWithCurrentURL(HttpServletRequest request, HttpServletResponse response, boolean retry) throws AuthException {

        // Declare.
        String currentURL;

        // Check if the current login URL is defined.
        if (request.getParameterMap().containsKey(CURRENT_LOGIN_URL_PARAMETER)) {

            // Get the current login URL.
            currentURL = request.getParameter(CURRENT_LOGIN_URL_PARAMETER);
        }
        else {

            // Get the current logout URL.
            currentURL = request.getParameter(CURRENT_LOGOUT_URL_PARAMETER);
        }

        // Check if the login is to be retried.
        if (retry) {

            // Put the retry and username on the session.
            request.getSession().setAttribute(RETRY_KEY, retry);
            request.getSession().setAttribute(USERNAME_KEY, request.getParameter(USERNAME_PARAMETER));

            TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", RETRY_KEY, retry);
            TRACER.trace(Level.FINE, "session set attributeMap['%s'] is %s", USERNAME_KEY, request.getParameter(USERNAME_PARAMETER));
        }

        // Check if the session is valid.
        else if (request.isRequestedSessionIdValid()) {

            // Remove the retry from the session.
            request.getSession().removeAttribute(RETRY_KEY);

            TRACER.trace(Level.FINE, "session remove attributeMap['%s']", RETRY_KEY);
        }

        // Redirect to the current URL.
        redirectTo(response, currentURL);
    }

    /**
     * Respond with the forbidden (403) status.
     *
     * @param  request   the HTTP servlet request.
     * @param  response  the HTTP servlet response.
     *
     * @throws  AuthException  if unable to respond with the forbidden (403) status.
     */
    private void respondWithForbiddenStatus(HttpServletRequest request, HttpServletResponse response) throws AuthException {

        try {

            // Respond with the forbidden (403) status.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "authentication required");

            TRACER.trace(Level.INFO, "respond with forbidden (403) status");
        }
        catch(IOException e) {
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

        TRACER.trace(Level.FINE, "request get parameterMap['%s'] is %s", CURRENT_LOGIN_URL_PARAMETER, request.getParameter(CURRENT_LOGIN_URL_PARAMETER));
        TRACER.trace(Level.FINE, "request get parameterMap['%s'] is %s", CURRENT_LOGOUT_URL_PARAMETER, request.getParameter(CURRENT_LOGOUT_URL_PARAMETER));
        TRACER.trace(Level.FINE, "request get parameterMap['%s'] is %s", USERNAME_PARAMETER, request.getParameter(USERNAME_PARAMETER));
       
        // Check if the request URI is the login action.
        if (isRequestURIAction(request, LOGIN_ACTION)) {

            TRACER.trace(Level.INFO, "accessing login action %s", request.getRequestURI());

            // Authenticate the request and send response.
            respondWithCurrentURL(request, response, !authenticate(request, clientSubject));
        }

        // Check if the request URI is the logout action.
        else if (isRequestURIAction(request, LOGOUT_ACTION)) {

            TRACER.trace(Level.INFO, "accessing logout action %s", request.getRequestURI());

            clearSession(request);
            respondWithCurrentURL(request, response, false);
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
                    respondWithForbiddenStatus(request, response);

                    // Validation failed.
                    authStatus = AuthStatus.FAILURE;
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
