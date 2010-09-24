package org.lazydog.entry.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Entity class used to represent an authentication module.
 *
 * @author  Ron Rickard
 */
public class AuthenticationModule extends Entity<AuthenticationModule> implements Comparable<AuthenticationModule>, Serializable {
    
    private static final long serialVersionUID = 1L;
    @NotNull(message="Class name is required.")
    @Size(max=255, message="Class name cannot contain more than 255 characters.")
    private String className;

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
    public int compareTo(AuthenticationModule that) {
        
        // Declare.
        int lastCompare;
        String thatClassName;
        String thisClassName;

        // Initialize.
        lastCompare = 0;
        thatClassName = replaceNull(that.getClassName(), "");
        thisClassName = replaceNull(this.getClassName(), "");
        
        // Compare this object to the object.
        lastCompare = thisClassName.compareTo(thatClassName);

        return lastCompare;
    }
                            
    /**
     * Create a copy of this object.
     *
     * @return  a copy of this object.
     */
    @Override
    public AuthenticationModule copy() {
        
        // Declare.
        AuthenticationModule copy;
        
        // Create a copy.
        copy = super.copy();
        copy.setClassName(this.getClassName());

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
        if (object instanceof AuthenticationModule &&
            this.compareTo((AuthenticationModule)object) == 0) {
            equals = true;
        }
        
        return equals;
    }

    /**
     * Get the class name.
     * 
     * @return  the class name.
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return  a hash code for this object.
     */
    @Override
    public int hashCode() {
        
        // Declare.
        String thisClassName;
        
        // Initialize.
        thisClassName = replaceNull(this.getClassName(), "");
        
        return thisClassName.hashCode();
    }

    /**
     * Get a new instance of this class.
     * 
     * @return  a new instance of this class.
     */
    public static AuthenticationModule newInstance() {
        return new AuthenticationModule();
    }

    /**
     * Set the class name.
     *
     * @param  className  the class name.
     */
    public void setClassName(String className) {
        this.className = className;
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
        toString.append("className = ").append(this.getClassName());
        toString.append(", createTime = ").append(this.getCreateTime());
        toString.append(", id = ").append(this.getId());
        toString.append(", modifyTime = ").append(this.getModifyTime());
        toString.append("]");
        
        return toString.toString();
    }
}

