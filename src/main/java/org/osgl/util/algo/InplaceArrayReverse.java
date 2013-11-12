package org.osgl.util.algo;

import org.osgl._;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 5/11/13
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class InplaceArrayReverse<T> implements ArrayAlgorithm<T, T[]> {
    @Override
    public T[] apply(T[] ts) throws NotAppliedException, _.Break {
        E.NPE(ts);
        int len = ts.length;
        if (0 == len) {
            return ts;
        }
        int steps = len / 2, max = len - 1;
        for (int i = 0; i < steps; ++i) {
            T t = ts[i];
            ts[i] = ts[max - i];
            ts[max - i] = t;
        }
        return ts;
    }
}
