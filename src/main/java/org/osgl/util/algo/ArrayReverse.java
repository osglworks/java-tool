package org.osgl.util.algo;

import org.osgl._;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

/**
 * Return an new array contains all element of specified array in reverse order
 */
public class ArrayReverse<T> implements ArrayAlgorithm<T, T[]> {
    @Override
    public T[] apply(T[] ts) throws NotAppliedException, _.Break {
        E.NPE(ts);
        T[] newTs = _.newArray(ts, ts.length);
        int len = ts.length;
        if (0 == len) {
            return ts;
        }
        int steps = len / 2, max = len - 1;
        for (int i = 0; i < steps; ++i) {
            newTs[i] = ts[max - i];
            newTs[max - i] = ts[i];
        }
        return newTs;
    }
}
