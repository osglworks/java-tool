/**
 * _.java
 *
 * Defines handy methods for common java programming tasks
 *
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package com.greenlaw110.util;

import com.greenlaw110.exception.UnexpectedException;

import java.util.List;

/**
 * The most common utilities including some utilities coming from {@link E}, {@link S}
 */
public class _ {
    /**
     * Defines an instance to be used in views
     */
    public final _ INSTANCE = new _();
    public final _ instance = INSTANCE;

    /**
     * Throw out NullPointerException if any one of the passed objects is null
     *
     * @param args the object instances to be tested
     */
    public final static void NPE(Object... args) {
        E.NPE(args);
    }

    public final static IllegalStateException illegalState() {
        return E.illegalState();
    }

    public final static IllegalStateException illegalState(String message, String args) {
        return E.illegalState(message, args);
    }

    public final static UnexpectedException unexpected(Throwable cause) {
        return E.unexpected(cause);
    }

    public final static UnexpectedException unexpected(String message, String args) {
        return E.unexpected(message, args);
    }

    public final static String str(Object o, boolean quoted) {
        return S.str(o, quoted);
    }

    public final static String str(Object o) {
        return S.str(o, false);
    }

    public final static boolean equal(Object a, Object b) {
        return isEqual(a, b);
    }

    public final static boolean eq(Object a, Object b) {
        return isEqual(a, b);
    }

    public final static boolean neq(Object a, Object b) {
        return !isEqual(a, b);
    }

    public final static boolean notEqual(Object a, Object b) {
        return !isEqual(a, b);
    }

    public final static boolean isEqual(Object a, Object b) {
        if (a == b) return true;
        if (null == a) return b != null;
        else return a.equals(b);
    }

    public final static boolean isNotEqual(Object a, Object b) {
        return !isEqual(a, b);
    }

    /**
     * Calculate hashcode from specified objects
     * @param args
     * @return the calculated hash code
     */
    public final static int hc(Object... args) {
        int i = 17;
        for (Object o: args) {
            i = 31 * i + ((null == o) ? 0 : o.hashCode());
        }
        return i;
    }

    /**
     * Alias of {@link #hc(Object...)}
     * 
     * @param args
     * @return the calculated hash code
     */
    public final static int hashCode(Object... args) {
        return hc(args);
    }

    public final static <T> List<T> list(T... el) {
        return C.list(el);
    }

}