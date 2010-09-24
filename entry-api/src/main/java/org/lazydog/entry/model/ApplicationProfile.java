package org.lazydog.entry.model;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Entity class used to represent an application profile.
 *
 * @author  Ron Rickard
 */
public class ApplicationProfile extends Entity<ApplicationProfile> implements Comparable<ApplicationProfile>, Serializable {
    
    private static final long serialVersionUID = 1L;
    @NotNull(message="Application identifier is required.")
    @Size(max=255, message="Application identifier cannot contain more than 255 characters.")
    private String applicationId;
    @Valid @NotNull(message="Application server profile is required.")
    private ApplicationServerProfile applicationServerProfile;
    @Valid @NotNull(message="Authentication module is required.")
    private AuthenticationModule authenticationModule;
    @Size(max=255, message="Default group name cannot contain more than 255 characters.")
    private String defaultGroupName;
    @Size(max=255, message="Registration URL cannot contain more than 255 characters.")
    private String registrationURL;

    /**
     * Compare this object to the specified object.
     *
     * @param  that  the object to compare this object against.
     *
     * @return  the value 0 if this object is equal to the object;
     *          a value less than 0 if this object is less than the object;
     *          and a value greater than 0 if this object is greater than the
     *          object.
     */
    @Override
    public int compareTo(ApplicationProfile that) {
        
        // Declare.
        int lastCompare;
        String thatApplicationId;
        String thisApplicationId;

        // Initialize.
        lastCompare = 0;
        thatApplicationId = replaceNull(that.getApplicationId(), "");
        thisApplicationId = replaceNull(this.getApplicationId(), "");
        
        // Compare this object to the object.
        lastCompare = thisApplicationId.compareTo(thatApplicationId);

        return lastCompare;
    }
                            
    /**
     * Create a copy of this object.
     *
     * @return  a copy of this object.
     */
    @Override
    public ApplicationProfile copy() {
        
        // Declare.
        ApplicationProfile copy;
        
        // Create a copy.
        copy = super.copy();
        copy.setApplicationId(this.getApplicationId());
        copy.setApplicationServerProfile(this.getApplicationServerProfile());
        copy.setAuthenticationModule(this.getAuthenticationModule());
        copy.setDefaultGroupName(this.getDefaultGroupName());
        copy.setRegistrationURL(this.getRegistrationURL());

        return copy;
    }
    
    /**
     * Compare this object to the specified object.
     *
     * @param  object  the object to compare this object against.
     *
     * @return  true if the objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        
        // Declare.
        boolean equals;

        // Initialize.
        equals = false;
        
        // Check if the object is an instance of this class
        // and is equal to this object.
        if (object instanceof ApplicationProfile &&
            this.compareTo((ApplicationProfile)object) == 0) {
            equals = true;
        }
        
        return equals;
    }

    /**
     * Get the application identifier.
     * 
     * @return  the application identifier.
     */
    public String getApplicationId() {
        return this.applicationId;
    }

    /**
     * Get the application server profile.
     * 
     * @return  the application server profile.
     */
    public ApplicationServerProfile getApplicationServerProfile() {
        return this.applicationServerProfile;
    }

    /**
     * Get the authentication module.
     *
     * @return  the authentication module.
     */
    public AuthenticationModule getAuthenticationModule() {
        return this.authenticationModule;
    }

    /**
     * Get the default group name.
     *
     * @return  the default group name.
     */
    public String getDefaultGroupName() {
        return this.defaultGroupName;
    }

    /**
     * Get the registration URL.
     *
     * @return  the registration URL.
     */
    public String getRegistrationURL() {
        return this.registrationURL;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return  a hash code for this object.
     */
    @Override
    public int hashCode() {
        
        // Declare.
        String thisApplicationId;
        
        // Initialize.
        thisApplicationId = replaceNull(this.getApplicationId(), "");
        
        return thisApplicationId.hashCode();
    }

    /**
     * Get a new instance of this class.
     * 
     * @return  a new instance of this class.
     */
    public static ApplicationProfile newInstance() {
        return new ApplicationProfile();
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
     * Set the application server profile.
     *
     * @param  applicationServerProfile  the application server profile.
     */
    public void setApplicationServerProfile(ApplicationServerProfile applicationServerProfile) {
        this.applicationServerProfile = applicationServerProfile;
    }

    /**
     * Set the authentication module.
     *
     * @param  authenticationModule  the authentication module.
     */
    public void setAuthenticationModule(AuthenticationModule authenticationModule) {
        this.authenticationModule = authenticationModule;
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
     * Set the registration URL.
     *
     * @param  registrationURL  the registration URL.
     */
    public void setRegistrationURL(String registrationURL) {
        this.registrationURL = registrationURL;
    }

    /**
     * Get this object as a String.
     *
     * @return  this object as a String.
     */
    @Override
    public String toString() {
                
        // Declare.
        StringBuffer toString;
        
        // Initialize.
        toString = new StringBuffer();
        
        toString.append("ApplicationProfile [");
        toString.append("applicationId = ").append(this.getApplicationId());
        toString.append(", applicationServerProfile = ").append(this.getApplicationServerProfile());
        toString.append(", authenticationModule = ").append(this.getAuthenticationModule());
        toString.append(", createTime = ").append(this.getCreateTime());
        toString.append(", defaultGroupName = ").append(this.getDefaultGroupName());
        toString.append(", id = ").append(this.getId());
        toString.append(", modifyTime = ").append(this.getModifyTime());
        toString.append(", registrationURL = ").append(this.getRegistrationURL());
        toString.append("]");
        
        return toString.toString();
    }
}

