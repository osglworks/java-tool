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

    public static final ArrayReverseInplace ARRAY_REVERSE_INPLACE = new ArrayReverseInplace();
    public static final <T> ArrayReverseInplace<T> arrayReverseInplace() {
        return (ArrayReverseInplace<T>)ARRAY_REVERSE_INPLACE;
    }

    public static final ArrayInsertionSort ARRAY_INSERTION_SORT = new ArrayInsertionSort();
    public static final <T> ArrayInsertionSort<T> arrayInsertionSort() {
        return ARRAY_INSERTION_SORT;
    }


    public static final ArrayInsertionSortInplace ARRAY_INSERTION_SORT_INPLACE = new ArrayInsertionSortInplace();
    public static final <T> ArrayInsertionSortInplace<T> arrayInsertionSortInplace() {
        return ARRAY_INSERTION_SORT_INPLACE;
    }
}
