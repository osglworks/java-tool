package org.osgl.util.algo;

import org.osgl.$;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Array insertion sort algorithm.
 *
 * Use {@link Arrays#binarySearch(Object[], int, int, Object, java.util.Comparator)}
 * to locate the index to be inserted, and use
 */
public class ArrayInsertionSort<T> extends ArraySortBase<T> implements ArraySort<T> {

    protected T[] sort0(T[] ts, int from, int to, Comparator<T> comp, T[] newTs) {
        int size = 1;
        newTs[0] = ts[from];
        for (int i = from + 1; i < to; ++i) {
            T t = ts[i];
            int loc = Arrays.binarySearch(newTs, 0, size, t, comp);
            if (loc < 0) {
                loc = ~loc;
            }
            System.arraycopy(newTs, loc, newTs, loc + 1, (size - loc));
            newTs[loc] = t;
            size++;
        }
        return newTs;
    }

    public static void main(String[] args) {
        Integer[] ia = {5, 4, 3, 2, 1};
        ArrayInsertionSort<Integer> s = new ArrayInsertionSort<Integer>();
        Integer[] a = s.apply(ia, 2, 4, $.F.NATURAL_ORDER);
        System.out.println(Arrays.toString(a));
    }

}
