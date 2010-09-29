package org.lazydog.entry.model;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Entity class used to represent an application server profile.
 *
 * @author  Ron Rickard
 */
public class ApplicationServerProfile extends Entity<ApplicationServerProfile> implements Comparable<ApplicationServerProfile>, Serializable {
    
    private static final long serialVersionUID = 1L;
    @NotNull(message="Application server identifier is required.")
    @Size(max=255, message="Application server identifier cannot contain more than 255 characters.")
    private String applicationServerId;
    @NotNull(message="JMX host is required.")
    @Size(max=255, message="JMX host cannot contain more than 255 characters.")
    private String jmxHost;
    @NotNull(message="JMX login is required.")
    @Size(max=255, message="JMX login cannot contain more than 255 characters.")
    private String jmxLogin;
    @NotNull(message="JMX password is required.")
    @Size(max=255, message="JMX password cannot contain more than 255 characters.")
    private String jmxPassword;
    @NotNull(message="JMX port is required.")
    @Min(value=1, message="JMX port must be at least 1.")
    @Max(value=65535, message="JMX port must be at most 66535.")
    private Integer jmxPort = new Integer(1);

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
    public int compareTo(ApplicationServerProfile that) {
        
        // Declare.
        int lastCompare;
        String thatApplicationServerId;
        String thisApplicationServerId;

        // Initialize.
        lastCompare = 0;
        thatApplicationServerId = replaceNull(that.getApplicationServerId(), "");
        thisApplicationServerId = replaceNull(this.getApplicationServerId(), "");
        
        // Compare this object to the object.
        lastCompare = thisApplicationServerId.compareTo(thatApplicationServerId);

        return lastCompare;
    }
                            
    /**
     * Create a copy of this object.
     *
     * @return  a copy of this object.
     */
    @Override
    public ApplicationServerProfile copy() {
        
        // Declare.
        ApplicationServerProfile copy;
        
        // Create a copy.
        copy = super.copy();
        copy.setApplicationServerId(this.getApplicationServerId());
        copy.setJmxHost(this.getJmxHost());
        copy.setJmxLogin(this.getJmxLogin());
        copy.setJmxPassword(this.getJmxPassword());
        copy.setJmxPort(this.getJmxPort());


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
        if (object instanceof ApplicationServerProfile &&
            this.compareTo((ApplicationServerProfile)object) == 0) {
            equals = true;
        }
        
        return equals;
    }

    /**
     * Get the application server identifier.
     * 
     * @return  the application server identifier.
     */
    public String getApplicationServerId() {
        return this.applicationServerId;
    }

    /**
     * Get the JMX host.
     * 
     * @return  the JMX host.
     */
    public String getJmxHost() {
        return this.jmxHost;
    }

    /**
     * Get the JMX login.
     *
     * @return  the JMX login.
     */
    public String getJmxLogin() {
        return this.jmxLogin;
    }

    /**
     * Get the JMX password.
     *
     * @return  the JMX password.
     */
    public String getJmxPassword() {
        return this.jmxPassword;
    }

    /**
     * Get the JMX port.
     *
     * @return  the JMX port.
     */
    public Integer getJmxPort() {
        return this.jmxPort;
    }
    /**
     * Returns a hash code for this object.
     *
     * @return  a hash code for this object.
     */
    @Override
    public int hashCode() {
        
        // Declare.
        String thisApplicationServerId;
        
        // Initialize.
        thisApplicationServerId = replaceNull(this.getApplicationServerId(), "");
        
        return thisApplicationServerId.hashCode();
    }

    /**
     * Get a new instance of this class.
     * 
     * @return  a new instance of this class.
     */
    public static ApplicationServerProfile newInstance() {
        return new ApplicationServerProfile();
    }

    /**
     * Set the application server identifier.
     *
     * @param  applicationServerId  the application server identifier.
     */
    public void setApplicationServerId(String applicationServerId) {
        this.applicationServerId = applicationServerId;
    }

    /**
     * Set the JMX host.
     *
     * @param  jmxHost  the JMX host.
     */
    public void setJmxHost(String jmxHost) {
        this.jmxHost = jmxHost;
    }

    /**
     * Set the JMX login.
     *
     * @param  jmxLogin  the JMX login.
     */
    public void setJmxLogin(String jmxLogin) {
        this.jmxLogin = jmxLogin;
    }

    /**
     * Set the JMX password.
     *
     * @param  jmxPassword  the JMX password.
     */
    public void setJmxPassword(String jmxPassword) {
        this.jmxPassword = jmxPassword;
    }

    /**
     * Set the JMX port.
     *
     * @param  jmxPort  the JMX port.
     */
    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = replaceNull(jmxPort, new Integer(1));
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
        
        toString.append("AuthenticationModule [");
        toString.append("applicationServerId = ").append(this.getApplicationServerId());
        toString.append(", createTime = ").append(this.getCreateTime());
        toString.append(", id = ").append(this.getId());
        toString.append(", jmxHost = ").append(this.getJmxHost());
        toString.append(", jmxLogin = ").append(this.getJmxLogin());
        toString.append(", jmxPassword = ").append(this.getJmxPassword());
        toString.append(", jmxPort = ").append(this.getJmxPort());
        toString.append(", modifyTime = ").append(this.getModifyTime());
        toString.append("]");
        
        return toString.toString();
    }
}

