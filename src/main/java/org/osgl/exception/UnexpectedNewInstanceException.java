package org.osgl.exception;

import org.osgl.util.E;

import java.lang.reflect.InvocationTargetException;

/**
 * A runtime exception that is raised when exception encountered creating
 * new instance by Java reflection
 */
public class UnexpectedNewInstanceException extends UnexpectedException {

    public UnexpectedNewInstanceException(String message, Object ... args) {
        super(message, args);
    }

    public UnexpectedNewInstanceException(Exception cause) {
        super(triage(cause));
    }

    public UnexpectedNewInstanceException(Exception cause, String message, Object... args) {
        super(triage(cause), message, args);
    }

    private static Throwable triage(Exception cause) {
        E.NPE(cause);
        if (cause instanceof InvocationTargetException) {
            return ((InvocationTargetException) cause).getTargetException();
        } else {
            return cause;
        }
    }
}
