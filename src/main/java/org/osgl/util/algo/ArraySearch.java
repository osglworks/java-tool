package org.osgl.util.algo;

import org.osgl._;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/11/13
 * Time: 10:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ArraySearch<T> extends ArrayAlgorithm, _.Func5<T[], Integer, Integer, T, Comparator<T>, Integer> {
    /**
     * Search array region specified by {@code from} and {@code to} using
     * the predicate specified
     *
     * @param ts
     * @param from
     * @param to
     * @param key
     * @param comp
     * @return
     */
    @Override
    Integer apply(T[] ts, Integer from, Integer to, T key, Comparator<T> comp);
}
