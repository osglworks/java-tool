package org.osgl.util.algo;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

/**
 * Return an new array contains elements specified by from and to in an array in reverse order
 */
public class ArrayReverse<T> implements ArrayAlgorithm, $.Func3<T[], Integer, Integer,  T[]> {
    @Override
    public T[] apply(T[] ts, Integer from, Integer to) throws NotAppliedException, $.Break {
        return reverse(ts, from, to);
    }

    public T[] reverse(T[] ts, int from, int to) {
        E.NPE(ts);
        Util.checkIndex(ts, from, to);
        if (to < from) {
            int t = to; to = from; from = t;
        }
        int len = to - from;
        T[] newTs = $.newArray(ts, len);
        if (0 == len) {
            return newTs;
        }
        int steps = len / 2, max = to - 1;
        for (int i = from; i < from + steps; ++i) {
            newTs[i] = ts[max - i];
            newTs[max - i] = ts[i];
        }
        return newTs;
    }
}
