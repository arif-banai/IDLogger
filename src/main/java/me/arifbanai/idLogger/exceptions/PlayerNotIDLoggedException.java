package me.arifbanai.idLogger.exceptions;

/**
 * Represents the exception where a name or uuid is not found in the DB
 */
public class PlayerNotIDLoggedException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public PlayerNotIDLoggedException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public PlayerNotIDLoggedException(String message) {
        super(message);
    }
}
