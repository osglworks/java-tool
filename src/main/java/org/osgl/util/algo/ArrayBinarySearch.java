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
 * Implement the binary search on array which is assumed to be sorted already
 */
public class ArrayBinarySearch<T> implements ArraySearch<T> {
    @Override
    public Integer apply(T[] ts, Integer from, Integer to, T key, final Comparator<T> comp) {
        Util.checkIndex(ts, from, to);
        T t0 = ts[from], tn = ts[to - 1];
        if (comp.compare(t0, key) == 0) return 0;
        if (comp.compare(tn, key) == 0) return to - 1;
        int i = comp.compare(t0, tn);
        if (i == 0) {
            return 0;
        }
        boolean ascending = i < 0;
        Comparator<T> c = comp;
        if (!ascending) c = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return comp.compare(o2, o1);
            }
        };
        //return s(ts, from, to, key, c, ascending);
        return search(ts, from, to - 1, key, c);
    }

    private int search(T[] ts, int from, int to, T k, Comparator<T> c) {
        while (from <= to) {
            int mid = (from + to) >>> 1;
            T tm = ts[mid];
            int cm = c.compare(tm, k);
            if (cm == 0) return mid;
            if (cm > 0) {
                to = mid - 1;
            } else {
                from = mid + 1;
            }
        }
        return -(from + 1);
    }

    private static $.T2<Long, Integer> osgl(Integer[] a, int k, ArrayBinarySearch<Integer> s) {
        long start = System.nanoTime();
        int loc = s.apply(a, 0, a.length, k, $.F.NATURAL_ORDER);
        long time = System.nanoTime() - start;
        return $.T2(time, loc);
    }

    private static $.T2<Long, Integer> jdk(Integer[] a, int k) {
        long start = System.nanoTime();
        int loc = Arrays.binarySearch(a, k, $.F.NATURAL_ORDER);
        long time = System.nanoTime() - start;
        return $.T2(time, loc);
    }

    public static void main(String[] args) {
        //benchmark();
        test();
    }

    private static void test() {
        ArrayBinarySearch<Integer> s = new ArrayBinarySearch<Integer>();
        Integer[] ia = {1, 20, 300, 4000, 50000};
        $.T2<Long, Integer> o = osgl(ia, -1, s);
        $.T2<Long, Integer> j = jdk(ia, -1);
        System.out.println(o._2 + ":" + j._2);

        o = osgl(ia, 1000000, s);
        j = jdk(ia, 1000000);
        System.out.println(o._2 + ":" + j._2);

        ia = new Integer[]{10, 8, 2, 1, -1};
        System.out.println(osgl(ia, 3, s));

        ia = new Integer[]{-1, 1, 2, 8, 10};
        System.out.println(osgl(ia, 0, s));
        System.out.println(jdk(ia, 0));
    }

    private static void benchmark() {

        final int MAX = Integer.MAX_VALUE / 512;
        final int TIMES = 1000000;
        Integer[] ia = new Integer[MAX];
        for (int i = 0; i < MAX; ++i) {
            ia[i] = i;
        }
        new ArrayBinarySearch<Integer>().apply(ia, 0, ia.length, 48, $.F.NATURAL_ORDER);
        $.Var<Long> osgl = $.var(0L);
        $.Var<Long> jdk = $.var(0L);
        ArrayBinarySearch<Integer> s = new ArrayBinarySearch<Integer>();
        for (int time = 0; time < TIMES; ++time) {
            Random r = new Random();
            int k = r.nextInt(MAX);
            $.T2<Long, Integer> jdkRet = jdk(ia, k);
            $.T2<Long, Integer> osglRet = osgl(ia, k, s);
            if (!jdkRet._2.equals(osglRet._2)) {
                throw new RuntimeException(String.format("jdk: %s; osgl: %s; k: %s", jdkRet._2, osglRet._2, k));
            }
            if (time > 0) {
                jdk.set(jdkRet._1 + jdk.get());
                osgl.set(osglRet._1 + osgl.get());
            }
        }
        System.out.println(String.format("osgl: %s; jdk: %s", osgl.get() / TIMES, jdk.get() / TIMES));
    }
}
