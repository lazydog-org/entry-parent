package org.lazydog.entry.account.manager;

import java.io.Serializable;


/**
 * Group already exists exception.
 *
 * @author  Ron Rickard
 */
public class GroupAlreadyExistsException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;
    private String groupName;

    /**
     * Constructs a new exception with no message.
     *
     * @param  groupName  the group name of the already existing group.
     */
    public GroupAlreadyExistsException(String groupName) {
        super();
        this.groupName = groupName;
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param  groupName  the group name of the already existing group.
     * @param  message    the message.
     */
    public GroupAlreadyExistsException(String groupName, String message) {
        super(message);
        this.groupName = groupName;
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param  groupName  the group name of the already existing group.
     * @param  message    the message.
     * @param  cause      the cause.
     */
    public GroupAlreadyExistsException(String groupName, String message, Throwable cause) {
        super(message, cause);
        this.groupName = groupName;
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  groupName  the group name of the already existing group.
     * @param  cause      the cause.
     */
    public GroupAlreadyExistsException(String groupName, Throwable cause) {
        super(cause);
        this.groupName = groupName;
    }

    /**
     * Get the group name of the already existing group.
     *
     * @return  the group name of the already existing group.
     */
    public String getGroupName() {
        return this.groupName;
    }
}
