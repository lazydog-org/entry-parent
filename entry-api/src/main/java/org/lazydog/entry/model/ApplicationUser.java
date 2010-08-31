package org.lazydog.entry.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * Entity class used to represent an application user.
 *
 * @author  Ron Rickard
 */
public class ApplicationUser 
       implements Comparable<ApplicationUser>,
                  Serializable {
    
    private static final long serialVersionUID = 1L;
    @NotNull(message="Email address is required.")
    @Size(max=50, message="Email address cannot contain more than 50 characters.")
    @Pattern(regexp="")
    private String emailAddress;
    @NotNull(message="First name is required.")
    @Size(max=50, message="First name cannot contain more than 50 characters.")
    private String firstName;
    private Integer id;
    @NotNull(message="Last name is required.")
    @Size(max=50, message="Last name cannot contain more than 50 characters.")
    private String lastName;
    @NotNull(message="Modify time is required.")
    private Date modifyTime;
    @NotNull(message="Register time is required.")
    private Date registerTime;
    @NotNull(message="Username is required.")
    @Size(max=50, message="Username cannot contain more than 50 characters.")
    private String username;
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
    public int compareTo(ApplicationUser that) {
        
        // Declare.
        int lastCompare;
        String thatUsername;
        String thisUsername;

        // Initialize.
        lastCompare = 0;
        thatUsername = replaceNull(that.getUsername(), "");
        thisUsername = replaceNull(this.getUsername(), "");
        
        // Compare this object to the object.
        lastCompare = thisUsername.compareTo(thatUsername);

        return lastCompare;
    }
                            
    /**
     * Create a copy of this object.
     *
     * @return  a copy of this object.
     */
    public ApplicationUser copy() {
        
        // Declare.
        ApplicationUser copy;
        
        // Create a copy.
        copy = new ApplicationUser();
        copy.setEmailAddress(this.getEmailAddress());
        copy.setFirstName(this.getFirstName());
        copy.setId(this.getId());
        copy.setLastName(this.getLastName());
        copy.setModifyTime(this.getModifyTime());
        copy.setRegisterTime(this.getRegisterTime());
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
        
        // Declare.
        boolean equals;

        // Initialize.
        equals = false;
        
        // Check if the object is an instance of this class
        // and is equal to this object.
        if (object instanceof ApplicationUser &&
            this.compareTo((ApplicationUser)object) == 0) {
            equals = true;
        }
        
        return equals;
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
     * Get the ID.
     *
     * @return  the ID.
     */
    public Integer getId() {
        return this.id;
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
     * Get the modify time.
     * 
     * @return  the modify time.
     */
    public Date getModifyTime() {
        return this.modifyTime;
    }

    /**
     * Get the register time.
     *
     * @return  the register time.
     */
    public Date getRegisterTime() {
        return this.registerTime;
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
        
        // Declare.
        String thisUsername;
        
        // Initialize.
        thisUsername = replaceNull(this.getUsername(), "");
        
        return thisUsername.hashCode();
    }

    /**
     * Check if this object is valid.
     *
     * @return  true if this object is valid, otherwise false.
     */
    public boolean isValid() {
        return (this.validate().isEmpty()) ? true : false;
    }

    /**
     * Replace the original object with the replacement object
     * if the original object is null.
     *
     * @param  original     the original object.
     * @param  replacement  the replacement object.
     *
     * @return  the original object if it is not null, otherwise the replacement
     *          object.
     *
     * @throws  IllegalArgumentException  if the replacement object is null.
     */
    protected static <U, V extends U> U replaceNull(U original, V replacement) {

        // Check if the replacement object is null.
        if (replacement == null) {
            throw new IllegalArgumentException(
                    "The replacement object cannot be null.");
        }

        return (original == null) ? replacement : original;
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
     * Set the ID.
     *
     * @param  id  the ID.
     */
    public void setId(Integer id) {
        this.id = id;
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
     * Set the modify time.
     *
     * @param  modifyTime  the modify time.
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * Set the register time.
     *
     * @param  registerTime  the register time.
     */
    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
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
                
        // Declare.
        StringBuffer toString;
        
        // Initialize.
        toString = new StringBuffer();
        
        toString.append("ApplicationUser [");
        toString.append("emailAddress = ").append(this.getEmailAddress());
        toString.append("firstName = ").append(this.getFirstName());
        toString.append("id = ").append(this.getId());
        toString.append("lastName = ").append(this.getLastName());
        toString.append("modifyTime = ").append(this.getModifyTime());
        toString.append("registerTime = ").append(this.getRegisterTime());
        toString.append("username = ").append(this.getUsername());
        toString.append("uuid = ").append(this.getUuid());
        toString.append("]");
        
        return toString.toString();
    }

    /**
     * Get the trimmed string.
     *
     * @param  value  the string.
     *
     * @return  the trimmed string.
     */
    private static String trimmed(String value) {
        return (value != null) ? value.trim() : null;
    }

    /**
     * Validate this object.
     *
     * @return  a set of constraint violations or an empty set if there are no
     *          constraint violations.
     */
    public Set<ConstraintViolation<ApplicationUser>> validate() {

        // Declare.
        Set<ConstraintViolation<ApplicationUser>> constraintViolations;
        Validator validator;
        ValidatorFactory validatorFactory;

        // Get the validator factory.
        validatorFactory = Validation.buildDefaultValidatorFactory();

        // Get the validator for this object type.
        validator = validatorFactory.getValidator();

        // Validate this object.
        constraintViolations = validator.validate(this);

        return constraintViolations;
    }
}

