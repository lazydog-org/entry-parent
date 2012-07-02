package org.lazydog.entry.model;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Entity class used to represent a user profile.
 *
 * @author  Ron Rickard
 */
public class UserProfile extends Entity<UserProfile> implements Comparable<UserProfile>, Serializable {
    
    private static final long serialVersionUID = 1L;
    private String activationCode;
    @NotNull(message="Email address is required.")
    @Size(max=255, message="Email address cannot contain more than 255 characters.")
    private String emailAddress;
    @NotNull(message="First name is required.")
    @Size(max=255, message="First name cannot contain more than 255 characters.")
    private String firstName;
    @NotNull(message="Last name is required.")
    @Size(max=255, message="Last name cannot contain more than 255 characters.")
    private String lastName;
    private String password;
    @NotNull(message="Username is required.")
    @Size(max=255, message="Username cannot contain more than 255 characters.")
    private String username;
    @NotNull(message="UUID is required.")
    private String uuid;

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
    public int compareTo(UserProfile that) {

        // Initialize.
        int lastCompare = 0;
        String thatUsername = replaceNull(that.getUsername(), "");
        String thisUsername = replaceNull(this.getUsername(), "");
        
        // Compare this object to the object.
        lastCompare = thisUsername.compareTo(thatUsername);

        return lastCompare;
    }
                            
    /**
     * Create a copy of this object.
     *
     * @return  a copy of this object.
     */
    @Override
    public UserProfile copy() {

        // Create a copy.
        UserProfile copy = super.copy();
        copy.setActivationCode(this.getActivationCode());
        copy.setEmailAddress(this.getEmailAddress());
        copy.setFirstName(this.getFirstName());
        copy.setLastName(this.getLastName());
        copy.setUsername(this.getUsername());
        copy.setUuid(this.getUuid());

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

        // Initialize.
        boolean equals = false;
        
        // Check if the object is an instance of this class and is equal to this object.
        if (object instanceof UserProfile && this.compareTo((UserProfile)object) == 0) {
            equals = true;
        }
        
        return equals;
    }

    /**
     * Generate a UUID.
     *
     * @return  the generated UUID.
     */
    private static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get the activation code.
     * 
     * @return  the activation code.
     */
    public String getActivationCode() {
        return this.activationCode;
    }

    /**
     * Get the email address.
     *
     * @return  the email address.
     */
    public String getEmailAddress() {
        return this.emailAddress;
    }

    /**
     * Get the first name.
     *
     * @return  the first name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Get the last name.
     *
     * @return  the last name.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Get the password.
     * 
     * @return  the password.
     */
    public String getPassword() {
        return this.password;
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
     * Get the universally unique identifier (UUID).
     * 
     * @return  the universally unique identifier (UUID).
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return  a hash code for this object.
     */
    @Override
    public int hashCode() {

        // Initialize.
        String thisUsername = replaceNull(this.getUsername(), "");
        
        return thisUsername.hashCode();
    }

    /**
     * Get a new instance of this class.
     * 
     * @return  a new instance of this class.
     */
    public static UserProfile newInstance() {

        // Create a new instance.
        UserProfile userProfile = new UserProfile();
        userProfile.setActivationCode(generateUuid());
        userProfile.setUuid(generateUuid());

        return userProfile;
    }

    /**
     * Set the activation code.
     *
     * @param  activationCode  the activation code.
     */
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    /**
     * Set the email address.
     *
     * @param  emailAddress  the email address.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = trimmed(emailAddress);
    }

    /**
     * Set the first name.
     *
     * @param  firstName  the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = trimmed(firstName);
    }

    /**
     * Set the last name.
     *
     * @param  lastName  the last name.
     */
    public void setLastName(String lastName) {
        this.lastName = trimmed(lastName);
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
        this.username = trimmed(username);
    }

    /**
     * Set the universally unique identifier (UUID).
     * 
     * @param  uuid  the universally unique identifier (UUID).
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get this object as a String.
     *
     * @return  this object as a String.
     */
    @Override
    public String toString() {

        // Initialize.
        StringBuffer toString = new StringBuffer();
        
        toString.append("UserProfile [");
        toString.append("activationCode = ").append(this.getActivationCode());
        toString.append(", createTime = ").append(this.getCreateTime());
        toString.append(", emailAddress = ").append(this.getEmailAddress());
        toString.append(", firstName = ").append(this.getFirstName());
        toString.append(", id = ").append(this.getId());
        toString.append(", lastName = ").append(this.getLastName());
        toString.append(", modifyTime = ").append(this.getModifyTime());
        toString.append(", username = ").append(this.getUsername());
        toString.append(", uuid = ").append(this.getUuid());
        toString.append("]");
        
        return toString.toString();
    }
}

