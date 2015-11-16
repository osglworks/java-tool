package org.osgl.util.algo;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 5/11/13
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayReverseInplace<T> implements ArrayAlgorithm, $.Func3<T[], Integer, Integer,  T[]> {

    @Override
    public T[] apply(T[] ts, Integer from, Integer to) throws NotAppliedException, $.Break {
        return reverse(ts, from, to);
    }

    public T[] reverse(T[] ts, int from, int to) {
        E.NPE(ts);
        Util.checkIndex(ts, from, to);
        if (to < from) {
            int t = to;
            to = from;
            from = t;
        }
        int len = to - from;
        if (0 == len) {
            return ts;
        }
        int steps = len / 2, max = to - 1;
        for (int i = from; i < from + steps; ++i) {
            T t = ts[i];
            ts[i] = ts[max - i];
            ts[max - i] = t;
        }
        return ts;
    }
}
