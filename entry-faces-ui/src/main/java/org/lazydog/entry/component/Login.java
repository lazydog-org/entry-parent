package org.lazydog.entry.component;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


/**
 * Login component.
 * 
 * @author  Ron Rickard
 */
@FacesComponent(value="login")
public class Login extends UINamingContainer {
    
    private static final long serialVersionUID = 1L;
    private String password;
    private String username;
    
    /**
     * Get the password.
     * 
     * @return  the password.
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Get the HTTP servlet request.
     * 
     * @return  the HTTP servlet request.
     */
    private HttpServletRequest getRequest() {
        return (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }
    
    /**
     * Get the username.
     * 
     * @return  the username.
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * Check if the user is authenticated.
     * 
     * @return  true if the user is authenticated, otherwise false.
     */
    public boolean isAuthenticated() {
        return this.getRequest().getUserPrincipal() != null;
    }
    
    /**
     * Is a global message available?
     * 
     * @return  true if a global message is available, otherwise false.
     */
    public boolean isGlobalMessageAvailable() {
        
        // Assume no global message is available.
        boolean isGlobalMessageAvailable = false;
        
        // Get the client IDs with messages.
        Iterator<String> clientIdsIterator = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        
        // Loop through the client IDs.
        while (clientIdsIterator.hasNext()) {
            
            // Check if the client ID does not exist.
            if (clientIdsIterator.next() == null) {
                
                // A global message is available.
                isGlobalMessageAvailable = true;
                break;
            }
        }
        
        return isGlobalMessageAvailable;
    }
    
    /**
     * Is a message available?
     * 
     * @return  true if a message is available, otherwise false.
     */
    public boolean isMessageAvailable() {
        return FacesContext.getCurrentInstance().getClientIdsWithMessages().hasNext();
    }
    
    /**
     * Check if the user is in the role.
     * 
     * @param  role  the role.
     * 
     * @return  true if the user is in the role, otherwise false.
     */
    public boolean isUserInRole(String role) {
        return this.getRequest().isUserInRole(role);
    }
    
    /**
     * Login the user.
     * 
     * @param  actionEvent  the action event.
     */
    public void login(ActionEvent actionEvent) {
      
        // Check if the user is not authenticated.
        if (!this.isAuthenticated()) {
          
            try {
             
                // Login the user.
                this.getRequest().login(this.username, this.password);
            }
            catch (ServletException e) {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your username or password is incorrect.", null));
            }
        }
    }
        
    /**
     * Logout the user.
     * 
     * @param  actionEvent  the action event.
     */
    public void logout(ActionEvent actionEvent) {
        
        // Check if the user is authenticated.
        if (this.isAuthenticated()) {
           
            try {
                
                // Logout the user.
                this.getRequest().logout();
            }
            catch (ServletException e) {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to logout.", null));
            }
        }
    }
    
    /**
     * Set the password.
     * 
     * @param  password  the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Set the username.
     * 
     * @param  username  the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
