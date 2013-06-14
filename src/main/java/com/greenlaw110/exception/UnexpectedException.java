package com.greenlaw110.exception;

/**
 * Could be used when programmer think it is not logic to reach somewhere. 
 * For example, the default branch of a switch case on an enum value
 */
public class UnexpectedException extends FastRuntimeException {

    public UnexpectedException(){
        super();
    }

    public UnexpectedException(String message){
        super(message);
    }

    public UnexpectedException(String message, Throwable cause){
        super(message, cause);
    }

    public UnexpectedException(Throwable cause){
        super(cause);
    }

    /**
     * Construct a FastRuntimeException with cause, message and message arguments
     * @param cause
     * @param message
     * @param args
     */
    public UnexpectedException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }
}
