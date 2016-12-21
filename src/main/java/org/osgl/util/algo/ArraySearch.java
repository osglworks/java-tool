package org.osgl.util.algo;

import org.osgl.$;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/11/13
 * Time: 10:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ArraySearch<T> extends ArrayAlgorithm, $.Func5<T[], Integer, Integer, T, Comparator<T>, Integer> {
    /**
     * Search array region specified by {@code from} and {@code to} using
     * the predicate specified
     *
     * @param ts the sorted array to be searched
     * @param from the from index
     * @param to the to index
     * @param key the key to search the array
     * @param comp the comparator function
     * @return index of the search key, if it is contained in the array;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element greater than the key, or <tt>a.length</tt> if all
     *         elements in the array are less than the specified key.  Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     */
    @Override
    Integer apply(T[] ts, Integer from, Integer to, T key, Comparator<T> comp);
}
