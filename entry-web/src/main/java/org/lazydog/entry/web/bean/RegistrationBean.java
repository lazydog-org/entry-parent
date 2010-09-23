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

    @ManagedProperty(value="#{param.defaultGroupName}")
    private String defaultGroupName;
    @EJB(beanName="ejb/EntryService", beanInterface=EntryService.class)
    private EntryService entryService;
    private UserProfile userProfile;

    /**
     * Get the default group name.
     * 
     * @return  the default group name.
     */
    public String getDefaultGroupName() {
        return this.defaultGroupName;
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
            outcome = (entryService.register(userProfile, defaultGroupName)) ? "success" : "failure";
        }
        catch(Exception e) {
e.printStackTrace();
            outcome = "failure";
        }

        return "protected?faces-redirect=true";
    }

    /**
     * Set the default group name.
     * 
     * @param  defaultGroupName  the default group name.
     */
    public void setDefaultGroupName(String defaultGroupName) {
        this.defaultGroupName = defaultGroupName;
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
