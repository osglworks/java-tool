package org.osgl.util.algo;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 5/11/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Algorithms {
    ;
    public static final ArrayReverse ARRAY_REVERSE = new ArrayReverse();
    public static final <T> ArrayReverse<T> arrayReverse() {
        return (ArrayReverse<T>)ARRAY_REVERSE;
    }

    public static final InplaceArrayReverse ARRAY_REVERSE_INPLACE = new InplaceArrayReverse();
    public static final <T> InplaceArrayReverse<T> arrayReverseInplace() {
        return (InplaceArrayReverse<T>)ARRAY_REVERSE_INPLACE;
    }
}
