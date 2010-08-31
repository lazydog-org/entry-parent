package org.lazydog.entry.account.manager;

import java.io.Serializable;


/**
 * No such group exception.
 *
 * @author  Ron Rickard
 */
public class NoSuchGroupException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;
    private String groupName;

    /**
     * Constructs a new exception with no message.
     *
     * @param  groupName  the group name of the non-existent group.
     */
    public NoSuchGroupException(String groupName) {
        super();
        this.groupName = groupName;
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param  groupName  the group name of the non-existent group.
     * @param  message    the message.
     */
    public NoSuchGroupException(String groupName, String message) {
        super(message);
        this.groupName = groupName;
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param  groupName  the group name of the non-existent group.
     * @param  message    the message.
     * @param  cause      the cause.
     */
    public NoSuchGroupException(String groupName, String message, Throwable cause) {
        super(message, cause);
        this.groupName = groupName;
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  groupName  the group name of the non-existent group.
     * @param  cause      the cause.
     */
    public NoSuchGroupException(String groupName, Throwable cause) {
        super(cause);
        this.groupName = groupName;
    }

    /**
     * Get the group name of the non-existent group.
     *
     * @return  the group name of the non-existent group.
     */
    public String getGroupName() {
        return this.groupName;
    }
}
