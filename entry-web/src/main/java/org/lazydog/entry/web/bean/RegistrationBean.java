package org.lazydog.entry.web.bean;

import javax.ejb.EJB;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.lazydog.entry.EntryService;
import org.lazydog.entry.model.UserProfile;


/**
 * Registration bean.
 *
 * @author  Ron Rickard
 */
@ManagedBean
@RequestScoped
public class RegistrationBean {

    @ManagedProperty(value="#{param.applicationId}")
    private String applicationId;
    @EJB(mappedName="ejb/EntryService", beanInterface=EntryService.class)
    private EntryService entryService;
    private UserProfile userProfile;

    /**
     * Get the application identifier.
     * 
     * @return  the application identifier.
     */
    public String getApplicationId() {
        return this.applicationId;
    }

    /**
     * Get the user profile.
     *
     * @return  the user profile.
     */
    public UserProfile getUserProfile() {
        return this.userProfile;
    }


    /**
     * Initialize.
     */
    @PostConstruct
    public void initialize() {

        // Create a new user profile.
        this.userProfile = UserProfile.newInstance();
    }

    /**
     * Register the user profile.
     *
     * @return  "success" if the user profile was successfully registered,
     *          otherwise "failure".
     */
    public String register() {

        // Declare.
        String outcome;

        try {

            // Register the user profile.
            outcome = (entryService.register(userProfile, applicationId)) ? "success" : "failure";
        }
        catch(Exception e) {
e.printStackTrace();
            outcome = "failure";
        }

        return "protected?faces-redirect=true";
    }

    /**
     * Set the application identifier.
     * 
     * @param  applicationId  the application identifier.
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Set the user profile.
     *
     * @param  userProfile  the user profile.
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
