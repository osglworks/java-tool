package org.osgl.exception;

import org.osgl.exception.UnexpectedException;

/**
 * A generic exception thrown when access to a certain resource is denied.
 */
public class AccessDeniedException extends UnexpectedException {

    public AccessDeniedException() {
        super("Access Denied");
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Object... args) {
        super(message, args);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

    public AccessDeniedException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
