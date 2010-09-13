package org.lazydog.entry.auth.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * Entry authentication module.
 *
 * This class is a JSR-196 based Server Authentication Module.
 *
 * @author  Ron Rickard
 */
public class EntryAuthModule implements ServerAuthModule {

    private static final Class[] supportedMessageTypes = new Class[] {
      HttpServletRequest.class,
      HttpServletResponse.class
    };
    private static final String AUTH_TYPE = "EntryAuthModule";
    private static final String AUTH_TYPE_KEY = "javax.servlet.http.authType";
    private static final String GROUPS_KEY = "org.lazydog.entry.auth.module.groups";
    private static final String LOGIN_ACTION = "/entry-login";
    private static final String LOGIN_PAGE = "/entry/pages/login.jsf";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String RETURN_URL_PARAMETER = "returnURL";
    private static final String USERNAME_KEY = "org.lazydog.entry.auth.module.username";
    private static final String USERNAME_PARAMETER = "username";

    private CallbackHandler callbackHandler;
    private Map options;
    private MessagePolicy requestPolicy;
    private MessagePolicy responsePolicy;

    private boolean authenticate(HttpServletRequest request, Subject subject) throws AuthException {

        // Declare.
        boolean isAuthenticated;
        
        isAuthenticated = false;
        
        try {
            // Declare.
            String password;
            String username;

            // Get the username and password.
            username = request.getParameter(USERNAME_PARAMETER);
            password = request.getParameter(PASSWORD_PARAMETER);
        
            if (exists(username) && exists(password)) {
                
                // Declare.
                PasswordValidationCallback passwordValidationCallback;
                
                passwordValidationCallback = new PasswordValidationCallback(subject, username, password.toCharArray());
                
                callbackHandler.handle(new Callback[]{passwordValidationCallback});
                passwordValidationCallback.clearPassword();

                isAuthenticated = passwordValidationCallback.getResult();

                if (isAuthenticated) {

                    // Declare.
                    List<String> groups;

                    groups = new ArrayList<String>();

                    setUsername(request, username);

                    for (Principal principal : passwordValidationCallback.getSubject().getPrincipals()) {
                        if (!principal.getName().equals(username)) {
                            groups.add(principal.getName());
                        }
                    }

                    setGroups(request, groups.toArray(new String[groups.size()]));
                }
            }
        }
        catch(Exception e) {
            AuthException authException;
            authException = new AuthException();
            authException.initCause(e);
            throw authException;
        }

        return isAuthenticated;
    }

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

    private static boolean exists(String value) {
        return (value != null && !value.isEmpty());
    }

    private static String[] getGroups(HttpServletRequest request) {
        return (String[])request.getSession().getAttribute(GROUPS_KEY);
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    private static String getUsername(HttpServletRequest request) {
        return (String)request.getSession().getAttribute(USERNAME_KEY);
    }

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy,
            CallbackHandler callbackHandler, Map options) throws AuthException {

        this.callbackHandler = callbackHandler;
        this.options = options;
        this.requestPolicy = requestPolicy;
        this.responsePolicy = responsePolicy;
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        return (getUsername(request) != null);
    }

    private boolean isRequestLoginAction(HttpServletRequest request) {
        return (request.getRequestURI().endsWith(LOGIN_ACTION));
    }

    private void redirectTo(HttpServletResponse response, String redirectURL) throws AuthException {

        try {

            // Declare.
            String encodedRedirectURL;

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

    private void respondWithLoginForm(HttpServletRequest request, HttpServletResponse response, boolean again) throws AuthException {

        // Declare.
        StringBuffer returnURLQueryString;

        returnURLQueryString = new StringBuffer();
        returnURLQueryString
                .append("?")
                .append(USERNAME_PARAMETER)
                .append("=")
                .append((again) ? request.getParameter(USERNAME_PARAMETER) : "")
                .append("&")
                .append(RETURN_URL_PARAMETER)
                .append("=")
                .append((again) ? request.getParameter(RETURN_URL_PARAMETER) : request.getRequestURI());

        redirectTo(response, LOGIN_PAGE + returnURLQueryString.toString());
    }

    private void respondWithReturnURL(HttpServletRequest request, HttpServletResponse response) throws AuthException {

            // Declare.L;
            String returnURL;

            returnURL = request.getParameter(RETURN_URL_PARAMETER);

            redirectTo(response, returnURL);
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject subject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    private static void setGroups(HttpServletRequest request, String[] groups) {
        request.getSession().setAttribute(GROUPS_KEY, groups);
    }

    private void setPrincipals(HttpServletRequest request, Subject subject) throws AuthException {

        try {

            // Declare.
            CallerPrincipalCallback callerPrincipalCallback;
            GroupPrincipalCallback groupPrincipalCallback;
            String[] groups;
            String username;

            groups = getGroups(request);
            username = getUsername(request);

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

    private static void setUsername(HttpServletRequest request, String username) {
        request.getSession().setAttribute(USERNAME_KEY, username);
    }



    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject,
            Subject serviceSubject) throws AuthException {
System.err.println("validateRequest() entered");
        // Declare.
        AuthStatus authStatus;
        HttpServletRequest request;
        HttpServletResponse response;

        // Assume the validation is incomplete.
        authStatus = AuthStatus.SEND_CONTINUE;

        // Get the request and response.
        request = (HttpServletRequest)messageInfo.getRequestMessage();
        response = (HttpServletResponse)messageInfo.getResponseMessage();

System.err.println("request authType = " + request.getAuthType());
System.err.println("request requestURI = " + request.getRequestURI());
System.err.println("request parameter username = " + request.getParameter(USERNAME_PARAMETER));
System.err.println("request parameter password = " + request.getParameter(PASSWORD_PARAMETER));
System.err.println("request parameter returnURL = " + request.getParameter(RETURN_URL_PARAMETER));
System.err.println("session attribute returnURL = " + request.getSession().getAttribute("returnURL"));

        if (isRequestLoginAction(request)) {

            if (authenticate(request, clientSubject)) {
                respondWithReturnURL(request, response);
            }
            else {
                respondWithLoginForm(request, response, true);
            }
        }
        else {

            if (isAuthenticated(request)) {

                setPrincipals(request, clientSubject);
                messageInfo.getMap().put(AUTH_TYPE_KEY, AUTH_TYPE);

                // Validation is complete.
                authStatus = AuthStatus.SUCCESS;
            }
            else {

                if (this.requestPolicy.isMandatory()) {
System.err.println("mandatory");
                    respondWithLoginForm(request, response, false);
request.getSession().setAttribute("returnURL", request.getRequestURI());
                }
                else {
System.err.println("not mandatory");
                    // Validation is not required.
                    authStatus = AuthStatus.SUCCESS;
                }
            }
        }

        return authStatus;
    }
}
