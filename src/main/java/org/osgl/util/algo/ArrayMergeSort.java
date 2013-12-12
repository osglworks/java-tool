package org.osgl.util.algo;

import org.osgl._;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayMergeSort<T> extends ArraySortBase<T> implements ArraySort<T> {

    protected T[] sort0(T[] ts, int from, int to, Comparator<T> comp, T[] newTs) {
        int delta = to - from;
        if (delta < 30) {
            ArrayInsertionSort<T> is = Algorithms.arrayInsertionSort();
            return is.sort0(ts, from, to, comp, newTs);
        }
        Object[] merged = mergeSort(ts, from, to, comp);
        System.arraycopy(merged, 0, newTs, 0, to - from);
        return newTs;
    }

    static <T> Object[] mergeSort(T[] ts, int from, int to, Comparator<T> comp) {
        // divide
        int mid = (from + to) >>> 1;
        if (mid == from) {
            return new Object[] {ts[mid]};
        }
        Object[] left = mergeSort(ts, from, mid, comp);
        Object[] right = mergeSort(ts, mid, to, comp);

        // merge
        int llen = left.length;
        int rlen = right.length;
        Object[] ret = new Object[llen + rlen];
        int l = 0, r = 0;
        while ((l < llen) && (r < rlen)) {
            T tl = (T)left[l], tr = (T)right[r];
            if (comp.compare(tl, tr) < 0) {
                ret[l + r] = tl;
                l++;
            } else {
                ret[l + r] = tr;
                r++;
            }
        }

        if (l < llen) {
            System.arraycopy(left, l, ret, r + l, llen - l);
        } else {
            System.arraycopy(right, r, ret, r + l, rlen - r);
        }
        return ret;
    }

    public static void main(String[] args) {
        Integer[] ia = {5, 4, 3, 2, 1};
        ArrayMergeSort<Integer> s = new ArrayMergeSort<Integer>();
        Integer[] a = s.apply(ia, 0, 5, _.F.NATURAL_ORDER);
        System.out.println(Arrays.toString(a));
    }

}
