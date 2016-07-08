package org.osgl.exception;

/**
 * A `RuntimeException` version of JDK's {@link NoSuchMethodException}
 */
public class UnexpectedNoSuchMethodException extends UnexpectedException {

    public UnexpectedNoSuchMethodException(Class<?> cls, String methodName) {
        super("Method not found by name[%s] and supplied arguments on class %s", methodName, cls.getName());
    }

    public UnexpectedNoSuchMethodException(String message, Object... args) {
        super(message, args);
    }

    public UnexpectedNoSuchMethodException(NoSuchMethodException cause) {
        super(cause.getCause(), cause.getMessage());
    }

    public UnexpectedNoSuchMethodException(NoSuchMethodException cause, String message, Object... args) {
        super(cause.getCause(), message, args);
    }
}
