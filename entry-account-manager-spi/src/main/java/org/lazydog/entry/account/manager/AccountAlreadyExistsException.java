package org.lazydog.entry.account.manager;

import java.io.Serializable;


/**
 * Account already exists exception.
 *
 * @author  Ron Rickard
 */
public class AccountAlreadyExistsException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;

    /**
     * Constructs a new exception with no message.
     *
     * @param  username  the username of the already existing account.
     */
    public AccountAlreadyExistsException(String username) {
        super();
        this.username = username;
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param  username  the username of the already existing account.
     * @param  message   the message.
     */
    public AccountAlreadyExistsException(String username, String message) {
        super(message);
        this.username = username;
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param  username  the username of the already existing account.
     * @param  message   the message.
     * @param  cause     the cause.
     */
    public AccountAlreadyExistsException(String username, String message, Throwable cause) {
        super(message, cause);
        this.username = username;
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  username  the username of the already existing account.
     * @param  cause     the cause.
     */
    public AccountAlreadyExistsException(String username, Throwable cause) {
        super(cause);
        this.username = username;
    }

    /**
     * Get the username of the already existing account.
     *
     * @return  the username of the already existing account.
     */
    public String getUsername() {
        return this.username;
    }
}
