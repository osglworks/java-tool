package org.osgl.exception;

import org.osgl.util.E;

import java.lang.reflect.InvocationTargetException;

/**
 * A runtime exception that is raised when exception encountered
 * invoking a method by Java reflection
 */
public class UnexpectedMethodInvocationException extends UnexpectedException {
    public UnexpectedMethodInvocationException(Throwable cause) {
        super(triage(cause));
    }

    public UnexpectedMethodInvocationException(Throwable cause, String message, Object... args) {
        super(triage(cause), message, args);
    }

    private static Throwable triage(Throwable cause) {
        E.NPE(cause);
        if (cause instanceof InvocationTargetException) {
            return ((InvocationTargetException) cause).getTargetException();
        } else {
            return cause;
        }
    }

    public static RuntimeException handle(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new UnexpectedMethodInvocationException(cause);
    }
}
