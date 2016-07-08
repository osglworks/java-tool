package org.osgl.exception;

/**
 * A `RuntimeException` version of JDK's {@link ClassNotFoundException}
 */
public class UnexpectedClassNotFoundException extends UnexpectedException {

    public UnexpectedClassNotFoundException(ClassNotFoundException cause) {
        super(cause.getCause(), cause.getMessage());
    }

    public UnexpectedClassNotFoundException(ClassNotFoundException cause, String message, Object... args) {
        super(cause.getCause(), message, args);
    }
}
