package org.osgl.util.algo;

import org.osgl._;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ArraySort<T> extends ArrayAlgorithm, _.Func4<T[], Integer, Integer, Comparator<T>, T[]> {
    T[] sort(T[] ts, int from, int to, Comparator<T> comp);
}
