package com.greenlaw110.util;

import com.greenlaw110.exception.UnexpectedException;

/**
 * Utility class to throw common exceptions
 */
public class E {
    private static String msg(String tmpl, Object... args) {
        if (args.length == 0) {
            return tmpl;
        } else {
            return String.format(tmpl, args);
        }
    }

    /**
     * Throw out {@link IllegalStateException}
     */
    public static IllegalStateException illegalState() {
        throw new IllegalStateException();
    }

    /**
     * throw out {@link IllegalStateException}
     * @param msg message template
     * @param args message arguments
     */
    public static IllegalStateException illegalState(String msg, Object... args) {
        return new IllegalStateException(msg(msg, args));
    }

    /**
     * Throw out NullPointerException if any one of the passed objects is null
     * 
     * @param objects the object instances to be tested
     */
    public static void NPE(Object... objects) {
        NPE("", objects);
    }

    /**
     * Throw out NullPointerException if any one of the passed objects is null
     *
     * @param msg the error message
     * @param objects the object instances to be tested
     */
    public static void NPE(String msg, Object... objects) {
        for (Object o : objects) {
            if (null == o) {
                throw new NullPointerException(msg);
            }
        }
    }

    /**
     * Throw out {@link com.greenlaw110.exception.UnexpectedException}
     * 
     * @param msg the message template
     * @param args the message arguments
     */
    public static UnexpectedException unexpected(String msg, Object... args) {
        throw new UnexpectedException(msg(msg, args));
    }
    
    /**
     * Throw out {@link com.greenlaw110.exception.UnexpectedException}
     * 
     * @param cause 
     */
    public static UnexpectedException unexpected(Throwable cause) {
        throw new UnexpectedException(cause);
    }

    /**
     * Throw out {@link com.greenlaw110.exception.UnexpectedException}
     * 
     * @param cause 
     * @param msg the message template
     * @param args the message arguments
     */
    public static UnexpectedException unexpected(Throwable cause, String msg, Object... args) {
        throw new UnexpectedException(cause, msg(msg, args));
    }
}
