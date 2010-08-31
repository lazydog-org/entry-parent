package org.lazydog.entry.account.manager;

import java.io.Serializable;


/**
 * No such account exception.
 *
 * @author  Ron Rickard
 */
public class NoSuchAccountException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;

    /**
     * Constructs a new exception with no message.
     *
     * @param  username  the username of the non-existent account.
     */
    public NoSuchAccountException(String username) {
        super();
        this.username = username;
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param  username  the username of the non-existent account.
     * @param  message   the message.
     */
    public NoSuchAccountException(String username, String message) {
        super(message);
        this.username = username;
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param  username  the username of the non-existent account.
     * @param  message   the message.
     * @param  cause     the cause.
     */
    public NoSuchAccountException(String username, String message, Throwable cause) {
        super(message, cause);
        this.username = username;
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  username  the username of the non-existent account.
     * @param  cause     the cause.
     */
    public NoSuchAccountException(String username, Throwable cause) {
        super(cause);
        this.username = username;
    }

    /**
     * Get the username of the non-existent account.
     *
     * @return  the username of the non-existent account.
     */
    public String getUsername() {
        return this.username;
    }
}
