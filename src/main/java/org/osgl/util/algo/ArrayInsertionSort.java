package org.osgl.util.algo;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
