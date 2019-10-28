package org.osgl.exception;

/**
 * This is a RuntimeException version of {@link InterruptedException}
 */
public class UnexpectedInterruptedException extends UnexpectedException {
    public UnexpectedInterruptedException(InterruptedException cause) {
        super(cause);
    }

    public InterruptedException toInterruptedException() {
        return (InterruptedException) getCause();
    }
}
