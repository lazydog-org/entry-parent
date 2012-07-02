package org.lazydog.entry.model;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;


/**
 * Entity.
 *
 * @author  Ron Rickard
 */
public abstract class Entity<T extends Entity<T>> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @NotNull(message="Create time is required.")
    private Date createTime;
    private Class<T> entityClass;
    private Integer id;
    private Date modifyTime;

    /**
     * Create entity.
     */
    @SuppressWarnings("unchecked")
    public Entity() {

        // Set the entity class.
        this.entityClass = (Class<T>)((ParameterizedType)this.getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Create a copy of this object.
     *
     * @return  a copy of this object.
     */
    protected T copy() {

        // Initialize.
        T copy = null;

        try {

            // Create an instance of this entity class.
            copy = this.entityClass.newInstance();

            // Create a copy.
            copy.setCreateTime(this.getCreateTime());
            copy.setId(this.getId());
            copy.setModifyTime(this.getModifyTime());
        }
        catch (IllegalAccessException e) {
            // Ignore.
        }
        catch (InstantiationException e) {
            // Ignore.
        }

        return copy;
    }




    /**
     * Get the create time.
     *
     * @return  the create time.
     */
    public Date getCreateTime() {
        return this.createTime;
    }

    /**
     * Get the identifier.
     *
     * @return  the identifier.
     */
    public Integer getId() {
        return this.id;
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
     * Check if this object is valid.
     *
     * @return  true if this object is valid, otherwise false.
     */
    public boolean isValid() {
        return this.validate().isEmpty();
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
            throw new IllegalArgumentException("The replacement object cannot be null.");
        }

        return (original == null) ? replacement : original;
    }

    /**
     * Set the create time.
     *
     * @param  createTime  the create time.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Set the identifier.
     *
     * @param  id  the identifier.
     */
    public void setId(Integer id) {
        this.id = id;
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
     * Get the trimmed string.
     *
     * @param  value  the string.
     *
     * @return  the trimmed string.
     */
    protected static String trimmed(String value) {
        return (value != null) ? value.trim() : null;
    }

    /**
     * Validate this object.
     *
     * @return  a set of constraint violations or an empty set if there are no
     *          constraint violations.
     */
    @SuppressWarnings("unchecked")
    public Set<ConstraintViolation<T>> validate() {

        // Declare.
        Set<ConstraintViolation<T>> constraintViolations;
        Validator validator;
        ValidatorFactory validatorFactory;

        // Get the validator factory.
        validatorFactory = Validation.buildDefaultValidatorFactory();

        // Get the validator for this object type.
        validator = validatorFactory.getValidator();

        // Validate this object.
        constraintViolations = validator.validate((T)this);

        return constraintViolations;
    }
}

