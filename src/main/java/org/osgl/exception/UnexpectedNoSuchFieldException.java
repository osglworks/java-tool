package org.osgl.exception;

/**
 * A `RuntimeException` version of JDK's {@link NoSuchFieldException}
 */
public class UnexpectedNoSuchFieldException extends UnexpectedException {
    public UnexpectedNoSuchFieldException(NoSuchFieldException cause) {
        super(cause.getCause(), cause.getMessage());
    }

    public UnexpectedNoSuchFieldException(NoSuchMethodException cause, String message, Object... args) {
        super(cause.getCause(), message, args);
    }
}
