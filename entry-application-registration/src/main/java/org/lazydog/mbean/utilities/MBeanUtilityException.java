package org.lazydog.mbean.utilities;

import java.io.Serializable;


/**
 * Thrown to indicate that an error occurred in the MBean utility.
 *
 * @author  Ronald Rickard
 */
public class MBeanUtilityException
        extends Exception
        implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new exception.
     */
    public MBeanUtilityException() {
        super();
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param  message  the message.
     */
    public MBeanUtilityException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param  message  the message.
     * @param  cause    the cause.
     */
    public MBeanUtilityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause  the cause.
     */
    public MBeanUtilityException(Throwable cause) {
        super(cause);
    }
}
