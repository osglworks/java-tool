package com.greenlaw110.exception;

/**
 * Runtime exception without fill the stack trace. Which
 * makes it much faster
 */
public class FastRuntimeException extends RuntimeException {

    public FastRuntimeException(){
        super();
    }

    public FastRuntimeException(String message){
        super(message);
    }

    public FastRuntimeException(String message, Throwable cause){
        super(message, cause);
    }

    public FastRuntimeException(Throwable cause){
        super(cause);
    }

    /**
     * Construct a FastRuntimeException with cause, message and message arguments
     * @param cause
     * @param message
     * @param args
     */
    public FastRuntimeException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
