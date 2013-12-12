package org.osgl.util.algo;

import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ArraySortBase<T> implements ArraySort<T> {

    @Override
    public final T[] apply(T[] ts, Integer from, Integer to, Comparator<T> comp) throws NotAppliedException, _.Break {
        return sort(ts, from, to, comp);
    }

    protected abstract T[] sort0(T[] ts, int from, int to, Comparator<T> comp, T[] newTs);

    public final T[] sort(T[] ts, int from, int to, Comparator<T> comp) {
        Util.checkIndex(ts, from, to);
        if (null == comp) {
            comp = _.F.NATURAL_ORDER;
        }
        int len = Math.abs(to - from);
        T[] newTs = _.newArray(ts, len);
        if (0 == len) {
            return newTs;
        } else {
            if (to < from) {
                return sort0(ts, to, from, comp, newTs);
            } else {
                return sort0(ts, from, to, comp, newTs);
            }
        }
    }

}
