package org.lazydog.entry.account.manager;

import java.io.Serializable;


/**
 * Entry already exists exception.
 *
 * @author  Ron Rickard
 */
public class EntryAlreadyExistsException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;
    private String entryName;

    /**
     * Constructs a new exception with no message.
     *
     * @param  entryName  the entry name of the already existing entry.
     */
    public EntryAlreadyExistsException(String entryName) {
        super();
        this.entryName = entryName;
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param  entryName  the entry name of the already existing entry.
     * @param  message    the message.
     */
    public EntryAlreadyExistsException(String entryName, String message) {
        super(message);
        this.entryName = entryName;
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param  entryName  the entry name of the already existing entry.
     * @param  message    the message.
     * @param  cause      the cause.
     */
    public EntryAlreadyExistsException(String entryName, String message, Throwable cause) {
        super(message, cause);
        this.entryName = entryName;
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  entryName  the entry name of the already existing entry.
     * @param  cause      the cause.
     */
    public EntryAlreadyExistsException(String entryName, Throwable cause) {
        super(cause);
        this.entryName = entryName;
    }

    /**
     * Get the entry name of the already existing entry.
     *
     * @return  the entry name of the already existing entry.
     */
    public String getEntryName() {
        return this.entryName;
    }
}
