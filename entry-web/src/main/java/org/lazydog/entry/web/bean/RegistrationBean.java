package org.lazydog.entry.web.bean;

import javax.ejb.EJB;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
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

    @EJB(beanName="ejb/EntryService", beanInterface=EntryService.class)
    private EntryService entryService;
    private UserProfile userProfile;

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
            outcome = (entryService.register(userProfile)) ? "success" : "failure";
        }
        catch(Exception e) {
e.printStackTrace();
            outcome = "failure";
        }

        return "protected";
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
