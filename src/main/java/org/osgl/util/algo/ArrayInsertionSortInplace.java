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
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayInsertionSortInplace<T> extends ArraySortInplaceBase<T> implements ArraySort<T> {

    protected T[] sort0(T[] ts, int from, int to, Comparator<T> comp) {
        int size = from + 1;
        for (int i = from + 1; i < to; ++i) {
            T t = ts[i];
            int loc = Arrays.binarySearch(ts, from, size, t, comp);
            if (loc < 0) {
                loc = ~loc;
            }
            System.arraycopy(ts, loc, ts, loc + 1, (size - loc));
            ts[loc] = t;
            size++;
        }
        return ts;
    }

    public static void test() {
        Integer[] ia = {5, 4, 3, 1, 3, 2, 1, 2};
        ArrayInsertionSortInplace<Integer> s = new ArrayInsertionSortInplace<Integer>();
        Integer[] a = s.apply(ia, 1, 7, $.F.NATURAL_ORDER);
        System.out.println(Arrays.toString(a));
    }

    private static ArrayInsertionSortInplace<Integer> s = new ArrayInsertionSortInplace<Integer>();
    private static ArraySimpleInsertionSortInplace<Integer> x = new ArraySimpleInsertionSortInplace<Integer>();
    private static ArrayMergeSortInplace<Integer> m = new ArrayMergeSortInplace<Integer>();

    private static long osgl(Integer[] a) {
        long l = System.nanoTime();
        //s.sort(a, 0, a.length, _.F.NATURAL_ORDER);
        //x.sort(a, 0, a.length, _.F.NATURAL_ORDER);
        m.sort(a, 0, a.length, $.F.NATURAL_ORDER);
        return System.nanoTime() - l;
    }

    private static long jdk(Integer[] a) {
        long l = System.nanoTime();
        Arrays.sort(a, $.F.NATURAL_ORDER);
        //m.sort(a, 0, a.length, _.F.NATURAL_ORDER);
        //s.sort(a, 0, a.length, _.F.NATURAL_ORDER);
        return System.nanoTime() - l;
    }

    public static void benchmark() {
        final int ARRAY_LEN = 5000;
        final int FACT = ARRAY_LEN  == 0 ? 1 : ARRAY_LEN;
        final int TIMES = 12800000 / FACT;
        $.Var< Long > osgl = $.var(0L);
        $.Var<Long> jdk = $.var(0L);
        for (int i = 0; i < TIMES; ++i) {
            Random r = new Random();
            Integer[] a = new Integer[ARRAY_LEN];
            for (int j = 0; j < ARRAY_LEN; ++j) {
                a[j] = r.nextInt(Integer.MAX_VALUE);
            }
            Integer[] a1 = (Integer[])a.clone();
            Integer[] a2 = (Integer[])a.clone();
            long o = osgl(a1);
            long j = jdk(a2);
            if (!Arrays.equals(a1, a2)) {
                System.err.print("Error:");
                System.out.println(Arrays.toString(a1));
                System.out.println(Arrays.toString(a2));
                break;
            }
            if (i > TIMES / 2) {
                osgl.set(o + osgl.get());
                jdk.set(j + jdk.get());
            }
        }
        System.out.println(String.format("osgl: %s, jdk: %s", osgl.get() / TIMES / FACT, jdk.get() / TIMES / FACT));
    }

    public static void main(String[] args) {
        benchmark();
    }

}
