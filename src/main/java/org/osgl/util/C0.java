/* 
 * Copyright (C) 2013 The Java Tool project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.osgl.util;

import java.lang.reflect.Array;
import java.util.*;

import static org.osgl.util.ListComprehension.valueOf;


/**
 * Collection utilities
 */
public class C0 {

    public static final Map EMPTY_MAP = new Map(true);
    public static final List EMPTY_LIST = new List(true);
    public static final Set EMPTY_SET = new Set(true);

    // -- factories --

    public static final <K, V> Map<K, V> map() {
        return EMPTY_MAP;
    }

    public static final <T> List<T> list() {
        return EMPTY_LIST;
    }

    public static final <T> Set<T> set() {
        return EMPTY_SET;
    }

    private static <T> List<T> rw(java.util.List l) {
        return _list(l, false);
    }

    private static <T> List<T> ro(java.util.List l) {
        return _list(l, true);
    }

    private static <T> List<T> _list(java.util.List l, boolean readOnly) {
        if (l instanceof List) {
            List<T> ll = (List) l;
            if (readOnly && ll.readOnly()) {
                return ll;
            }
            l = ll.get();
        }
        return new List<T>(readOnly, l);
    }

    public static <T> List<T> list(T... t) {
        return ro(Arrays.asList(t));
    }

    public static List<Integer> list(int[] a) {
        return ro(newList(a));
    }

    public static List<Long> list(long[] a) {
        return ro(newList(a));
    }

    public static List<Boolean> list(boolean[] a) {
        return ro(newList(a));
    }

    public static List<Float> list(float[] a) {
        return ro(newList(a));
    }

    public static List<Double> list(double[] a) {
        return ro(newList(a));
    }

    public static List<Short> list(short[] a) {
        return ro(newList(a));
    }

    public static List<Byte> list(byte[] a) {
        return ro(newList(a));
    }

    public static List<Character> list(char[] a) {
        return ro(newList(a));
    }

    public static <T> List<T> list(Iterable<? extends T> it) {
        if (it instanceof List) {
            return ro((List) it);
        }
        return ro(newList(it));
    }

    public static <T> List<T> list(Iterable<T> it, boolean readOnly) {
        if (!readOnly) {
            return newList(it);
        } else {
            return list(it);
        }
    }

    public static <T> List<T> newSizedList(int size) {
        return new List<T>(false, new ArrayList<T>(size));
    }

    public static <T> List<T> newList(T... t) {
        return rw(Arrays.asList(t));
    }

    public static List<Integer> newList(int[] a) {
        List<Integer> l = newSizedList(a.length);
        for (int e : a) {
            l.add(e);
        }
        return l;
    }

    public static List<Long> newList(long[] a) {
        List<Long> lst = newSizedList(a.length);
        for (long e : a) {
            lst.add(e);
        }
        return lst;
    }

    public static List<Boolean> newList(boolean[] a) {
        List<Boolean> l = newSizedList(a.length);
        for (boolean e : a) {
            l.add(e);
        }
        return l;
    }

    public static List<Float> newList(float[] a) {
        List<Float> l = newSizedList(a.length);
        for (float e : a) {
            l.add(e);
        }
        return l;
    }

    public static List<Double> newList(double[] a) {
        List<Double> l = newSizedList(a.length);
        for (double e : a) {
            l.add(e);
        }
        return l;
    }

    public static List<Short> newList(short[] a) {
        List<Short> l = newSizedList(a.length);
        for (short i : a) {
            l.add(i);
        }
        return l;
    }

    public static List<Byte> newList(byte[] a) {
        List<Byte> l = newSizedList(a.length);
        for (byte i : a) {
            l.add(i);
        }
        return l;
    }

    public static List<Character> newList(char[] ia) {
        List<Character> l = newSizedList(ia.length);
        for (char i : ia) {
            l.add(i);
        }
        return l;
    }

    public static <T> List<T> newList(Iterable<? extends T> it) {
        if (it instanceof List) {
            return rw((List) it);
        }
        List<T> l = newList();
        for (T t : it) {
            l.add(t);
        }
        return l;
    }

    private static <T> Set<T> rw(java.util.Set l) {
        return _set(l, false);
    }

    private static <T> Set<T> ro(java.util.Set l) {
        return _set(l, true);
    }

    private static <T> Set<T> _set(java.util.Set l, boolean readOnly) {
        if (l instanceof Set) {
            Set<T> fl = (Set) l;
            if (fl.readOnly() ^ readOnly) {
                l = fl.get();
            } else {
                return fl;
            }
        }
        if (readOnly) {
            return new Set<T>(true, Collections.unmodifiableSet(l));
        } else {
            return new Set<T>(false, new HashSet<T>(l));
        }
    }

    public static <T> Set<T> set(T... t) {
        return ro(new HashSet<T>(Arrays.asList(t)));
    }

    public static Set<Integer> set(int[] a) {
        return ro(newSet(a));
    }

    public static Set<Long> set(long [] a) {
        return ro(newSet(a));
    }

    public static Set<Boolean> set(boolean[] a) {
        return ro(newSet(a));
    }

    public static Set<Float> set(float[] a) {
        return ro(newSet(a));
    }

    public static Set<Double> set(double[] ia) {
        return ro(newSet(ia));
    }

    public static Set<Short> set(short[] ia) {
        return ro(newSet(ia));
    }

    public static Set<Byte> set(byte[] ia) {
        return ro(newSet(ia));
    }

    public static Set<Character> set(char[] ia) {
        return ro(newSet(ia));
    }

    public static <T> Set<T> set(Iterable<? extends T> it) {
        if (it instanceof Set) {
            return ro((Set) it);
        }
        return ro(newSet(it));
    }

    public static <T> Set<T> set(Iterable<T> it, boolean readOnly) {
        if (!readOnly) {
            return newSet(it);
        } else {
            return set(it);
        }
    }

    public static <T> Set<T> newSizedSet(int n) {
        return rw(new HashSet<T>(n));
    }


    public static <T> Set<T> newSet(T... t) {
        return rw(new HashSet<T>(Arrays.asList(t)));
    }

    public static Set<Integer> newSet(int[] a) {
        Set<Integer> s = newSizedSet(a.length);
        for (int e : a) {
            s.add(e);
        }
        return s;
    }

    public static Set<Long> newSet(long[] a) {
        Set<Long> s = newSizedSet(a.length);
        for (long i : a) {
            s.add(i);
        }
        return s;
    }

    public static Set<Boolean> newSet(boolean[] a) {
        Set<Boolean> s = newSizedSet(a.length + 1);
        for (boolean i : a) {
            s.add(i);
        }
        return s;
    }

    public static Set<Float> newSet(float[] a) {
        Set<Float> s = newSizedSet(a.length + 1);
        for (float i : a) {
            s.add(i);
        }
        return s;
    }

    public static Set<Double> newSet(double[] ia) {
        Set<Double> l = newSet();
        for (double i : ia) {
            l.add(i);
        }
        return l;
    }

    public static Set<Short> newSet(short[] ia) {
        Set<Short> l = newSet();
        for (short i : ia) {
            l.add(i);
        }
        return l;
    }

    public static Set<Byte> newSet(byte[] ia) {
        Set<Byte> l = newSet();
        for (byte i : ia) {
            l.add(i);
        }
        return l;
    }

    public static Set<Character> newSet(char[] ia) {
        Set<Character> l = newSet();
        for (char i : ia) {
            l.add(i);
        }
        return l;
    }

    public static <T> Set<T> newSet(Iterable<? extends T> it) {
        if (it instanceof Set) {
            return rw((Set) it);
        }
        Set<T> l = newSet();
        for (T t : it) {
            l.add(t);
        }
        return l;
    }

    public static <K, V> Map<K, V> map(Object... args) {
        if (null == args || args.length == 0) {
            return EMPTY_MAP;
        }
        return new Map(true, args);
    }

    public static <K, V> Map<K, V> map(java.util.Map<? extends K, ? extends V> map) {
        if (null == map) {
            return EMPTY_MAP;
        }
        return readOnly(map);
    }

    public static <K, V> Map<K, V> newMap(Object... args) {
        return new Map(false, args);
    }

    public static <K, V> Map<K, V> newMap(java.util.Map<? extends K, ? extends V> map) {
        return new Map(false, map);
    }

    // -- eof factories ---

    /**
     * Return true if the object specified can be used in for (T e: o)
     *
     * @param o
     * @return
     */
    public static boolean isArrayOrIterable(Object o) {
        if (o.getClass().isArray()) return true;
        if (o instanceof Iterable) return true;
        return false;
    }

    /**
     * Return a unique set from a collection
     *
     * @param c the collection
     * @return unique set of elements in <code>c</code>
     */
    public static <T> Set<T> unique(Iterable<? extends T> c) {
        return newSet(c);
    }

    /**
     * Alias of {@link #unique(Iterable)}
     *
     * @param c the collection
     * @return unique set of elements in <code>c</code>
     */
    public static <T> Set<T> uniq(Iterable<? extends T> c) {
        return unique(c);
    }

    public static <T> Set<T> readOnly(java.util.Set<? extends T> set) {
        return set(set);
    }

    public static <K, V> Map<K, V> readOnly(java.util.Map<? extends K, ? extends V> m) {
        if (m instanceof Map) {
            Map mm = (Map) m;
            if (mm.readOnly()) {
                return mm;
            }
        }
        return new Map(true, m);
    }

    public static <E> ListComprehension<E> lc(ListComprehension<E> lc) {
        return lc;
    }

    public static <E> ListComprehension<E> lc(E... ta) {
        return valueOf(ta);
    }

    public static <E> ListComprehension<E> lc(Iterable<? extends E> ta) {
        return valueOf(ta);
    }

    public static ListComprehension<Integer> lc(int[] ta) {
        return valueOf(ta);
    }

    public static ListComprehension<Long> lc(long[] ta) {
        return valueOf(ta);
    }

    public static ListComprehension<Boolean> lc(boolean[] ta) {
        return valueOf(ta);
    }


    public static ListComprehension<Float> lc(float[] ta) {
        return valueOf(ta);
    }

    public static ListComprehension<Double> lc(double[] ta) {
        return valueOf(ta);
    }


    public static ListComprehension<Byte> lc(byte[] ta) {
        return valueOf(ta);
    }


    public static ListComprehension<Short> lc(short[] ta) {
        return valueOf(ta);
    }


    public static ListComprehension<Character> lc(char[] ta) {
        return valueOf(ta);
    }

    public static <T> T[] array(Collection<T> col) {
        E.invalidArgIf(col.isEmpty(), "Class type must be present if collection is empty");
        Class<T> clz = (Class<T>) col.iterator().next().getClass();
        return array(col, clz);
    }

    public static <T> T[] array(Collection<T> col, Class<T> clz) {
        T[] ta = (T[]) Array.newInstance(clz, 0);
        return col.toArray(ta);
    }

    public static <T> Iterable<T> reverse(final List<T> l) {
        return Itr.valueOf(l).reverse();
    }

    public static <T> Iterable<T> reverse(final T[] l) {
        return Itr.valueOf(l).reverse();
    }

    public static int[] reverse(int[] l) {
        int len = l.length;
        int[] l0 = new int[len];
        int j = 0;
        for (int i = len - 1; i >= 0; --i) {
            l0[j++] = l[i];
        }
        return l0;
    }

    public static char[] reverse(char[] l) {
        int len = l.length;
        char[] l0 = new char[len];
        int j = 0;
        for (int i = len - 1; i >= 0; --i) {
            l0[j++] = l[i];
        }
        return l0;
    }

    public static byte[] reverse(byte[] l) {
        int len = l.length;
        byte[] l0 = new byte[len];
        int j = 0;
        for (int i = len - 1; i >= 0; --i) {
            l0[j++] = l[i];
        }
        return l0;
    }

    public static void walkThrough(Iterable<?> iterable) {
        try {
            for (Object el : iterable) {
            }
        } catch (_.Break e) {
            // ignore
        }
    }

    public static <T> T fold(Iterable iterable) {
        try {
            for (Object el : iterable) {
            }
            return null;
        } catch (_.Break b) {
            return b.get();
        }
    }

    public static <T> List<T> prepend(java.util.List<T> list, T... ts) {
        List<T> l = newList(ts);
        l.addAll(list);
        return l;
    }

    public static <T> List<T> append(java.util.List<T> list, T... ts) {
        List<T> l = newList(list);
        l.addAll(Arrays.asList(ts));
        return l;
    }

    public static <T> List<T> head(final java.util.List<T> list, final int n) {
        if (0 == n) {
            return list(list);
        }
        int size = list.size();
        if (n > size) {
            return list(list);
        } else if (n < 0) {
            return tail(list, size + n);
        } else {
            return lc(list).filterOnIndex(X.f.lessThan(n)).asList();
        }
    }

    public static <T> List<T> tail(java.util.List<T> list, final int n) {
        if (0 == n) {
            return list(list);
        }

        int size = list.size();
        if (n > size) {
            return list(list);
        } else if (n < 0) {
            return head(list, size + n);
        } else {
            int n0 = size - n - 1;
            return lc(list).filterOnIndex(X.f.greatThan(n0)).asList();
        }
    }

    public static <E1, E2> List<_.T2<E1, E2>> zip(java.util.List<E1> l1, java.util.List<E2> l2) {
        int len = N.min(l1.size(), l2.size());
        List<_.T2<E1, E2>> l = newSizedList(len);
        for (int i = 0; i < len; ++i) {
            l.add(_.T2(l1.get(i), l2.get(i)));
        }
        return l;
    }

    public static <E1, E2> List<_.T2<E1, E2>> zipAll(java.util.List<E1> l1, java.util.List<E2> l2) {
        return zipAll(l1, l2, null, null);
    }

    public static <E1, E2> List<_.T2<E1, E2>> zipAll(java.util.List<E1> l1, java.util.List<E2> l2, E1 def1, E2 def2) {
        int len1 = l1.size(), len2 = l2.size(), lmax = N.max(len1, len2), lmin = N.min(len1, len2);
        List<_.T2<E1, E2>> l = newSizedList(lmax);
        for (int i = 0; i < lmin; ++i) {
            l.add(_.T2(l1.get(i), l2.get(i)));
        }
        if (lmin == len1) {
            for (int i = lmin; i < lmax; ++i) {
                l.add(_.T2(def1, l2.get(i)));
            }
        } else {
            for (int i = lmin; i < lmax; ++i) {
                l.add(_.T2(l1.get(i), def2));
            }
        }
        return l;
    }

    public static <E1, E2> _.T2<List<E1>, List<E2>> unzip(List<_.T2<E1, E2>> list) {
        int len = list.size();
        List<E1> l1 = newSizedList(len);
        List<E2> l2 = newSizedList(len);
        for (_.T2<E1, E2> t2 : list) {
            l1.add(t2._1);
            l2.add(t2._2);
        }
        return _.T2(l1, l2);
    }

    public static <E1, E2, E3> _.T3<List<E1>, List<E2>, List<E3>> unzip3(List<_.T3<E1, E2, E3>> list) {
        int len = list.size();
        List<E1> l1 = newSizedList(len);
        List<E2> l2 = newSizedList(len);
        List<E3> l3 = newSizedList(len);
        for (_.T3<E1, E2, E3> t3 : list) {
            l1.add(t3._1);
            l2.add(t3._2);
            l3.add(t3._3);
        }
        return _.T3(l1, l2, l3);
    }

    /* --------------------------------------------------------------------------------
     * Extending java.util.Collection framework
     * ------------------------------------------------------------------------------*/

    /**
     * The abstract collection implementation with extensions
     *
     * @param <T>
     */
    private static abstract class Col<T> implements Collection<T> {
        protected final Collection<T> _c;
        protected final boolean _ro;

        protected abstract Collection<T> ensureReadOnly(boolean readOnly, Collection<T> col);

        protected Col(boolean readOnly, Collection<T> col) {
            X.NPE(col);
            _c = ensureReadOnly(readOnly, col);
            _ro = readOnly;
        }

        /**
         * Return a copy of this collection that is not read only
         *
         * @param <L>
         * @return
         */
        protected final <C extends Col<T>> C copyNew() {
            return copy(false);
        }

        /**
         * Sub class overwrites this method to return an copy of this
         * of <code>Col</code> and make it mutable
         *
         * @return an new instance of {@link Col collection}
         */
        protected abstract <C extends Col<T>> C copy(boolean readOnly);

        /**
         * Sub class overwrites this method to return an new copy of collection
         * where elements coming from the list comprehension specified
         *
         * @param lc
         * @param readOnly
         * @param <L>
         * @return an new copy of collection
         */
        protected abstract <E, L extends Col<E>> L copy(ListComprehension<E> lc, boolean readOnly);

        @Override
        public final int size() {
            return _c.size();
        }

        @Override
        public final boolean isEmpty() {
            return _c.isEmpty();
        }

        @Override
        public final boolean contains(Object o) {
            return _c.contains(o);
        }

        @Override
        public final Iterator<T> iterator() {
            return _c.iterator();
        }

        @Override
        public final Object[] toArray() {
            return _c.toArray();
        }

        @Override
        public final <T1> T1[] toArray(T1[] a) {
            return _c.toArray(a);
        }

        @Override
        public final boolean add(T t) {
            return _c.add(t);
        }

        @Override
        public final boolean remove(Object o) {
            return _c.remove(o);
        }

        @Override
        public final boolean containsAll(Collection<?> c) {
            return _c.containsAll(c);
        }

        @Override
        public final boolean addAll(Collection<? extends T> c) {
            return _c.addAll(c);
        }

        @Override
        public final boolean removeAll(Collection<?> c) {
            return _c.removeAll(c);
        }

        @Override
        public final boolean retainAll(Collection<?> c) {
            return _c.retainAll(c);
        }

        @Override
        public final void clear() {
            _c.clear();
        }

        // --- start extensions ---

        /**
         * Is this collection immutable
         *
         * @return true if this list is an immutable collection
         */
        public final boolean readOnly() {
            return _ro;
        }

        /**
         * Return a list contains all elements of this list with
         * readOnly attribute specified
         *
         * @param readOnly
         * @return self or an new list depending on the specified readOnly param and readOnly attribute of this list
         */
        public final <L extends Col<T>> L readOnly(boolean readOnly) {
            if (readOnly() == readOnly) {
                return (L) this;
            } else {
                return copy(readOnly);
            }
        }

        /**
         * Alias of {@link #readOnly()}
         *
         * @return true if this list is an immutable collection
         */
        public final boolean ro() {
            return _ro;
        }

        /**
         * Is this collection writable
         *
         * @return true if this list is a mutable collection
         */
        public final boolean rw() {
            return !_ro;
        }

        /**
         * alias of {@link #rw()}
         *
         * @return true if this list is a mutable collection
         */
        public final boolean readWrite() {
            return !_ro;
        }

        /**
         * Return a {@link ListComprehension list comprehension instance} of this list
         */
        public final ListComprehension<T> lc() {
            return C0.lc(this);
        }

        /**
         * Accept a visitor to iterate ach element in the collection
         *
         * @param visitor
         * @return this list
         */
        public final <L extends Col<T>> L accept(_.IFunc1<?, T> visitor) {
            lc().each(visitor);
            return (L) this;
        }


        /**
         * Print out each element in a separate line
         *
         * @return this collection
         */
        public final <L extends Col<T>> L println() {
            return (L) accept(IO.f.PRINTLN);
        }

        /**
         * Return a string concatenated by elements of this list separated by ","
         *
         * @return the string
         */
        public final String join() {
            return S.join(",", this);
        }

        /**
         * Return a string concatenated by elements of this list separated by specified separator
         *
         * @param sep
         * @return the string
         */
        public final String join(String sep) {
            return S.join(sep, this);
        }

        /**
         * Return a string concatenated by elements of this list separated by specified separator
         * with prefix and suffix applied on each element's string presentation
         *
         * @param sep
         * @param prefix
         * @param suffix
         * @return a string
         */
        public final String join(String sep, String prefix, String suffix) {
            return S.join(sep, prefix, suffix, this);
        }


        /**
         * Return a set contains all unique elments in this list
         *
         * @return a set
         */
        public final Set<T> uniq() {
            return C0.uniq(this);
        }

        /**
         * Return a List contains all elements contained in this list but not in the specified collection
         *
         * @param c
         * @return an new list with mutable state the same as the underline list
         */
        public final <L extends Col<T>> L without(Collection<T> c) {
            L l0 = copyNew();
            l0.removeAll(c);
            return l0.readOnly(readOnly());
        }

        /**
         * Return a List contains all elements contained in this list but not in the specified array
         *
         * @param elements
         * @return an new list with mutable state the same as the underline list
         */
        public final <L extends Col<T>> L without(T... elements) {
            return without(C0.list(elements));
        }

        /**
         * Return a List contains elements that contained in both this list and the specified collection
         *
         * @param c the collection
         * @return an new list with mutable state the same as the underline list
         */
        public final <L extends Col<T>> L intersect(Collection<T> c) {
            L l0 = copyNew();
            l0.retainAll(c);
            return l0.readOnly(readOnly());
        }

        /**
         * Return a List contains elements that contained in both this list and the specified elements
         *
         * @param elements
         * @return an new list with mutable state the same as the underline list
         */
        public final <L extends Col<T>> L intersect(T... elements) {
            return intersect(C0.list(elements));
        }

        /**
         * Return a list contains elements in this list matches the filters specified
         *
         * @param filters an array of filters (a functor accept one param and return boolean type
         * @return an new list with mutable state the same as this list
         */
        public final <L extends Col<T>> L filter(final _.IFunc1<Boolean, T>... filters) {
            return copy(lc().filter(filters), readOnly());
        }

        /**
         * Apply a list of mapper (transformer) to each element in this collection,
         * and return an new list contains the transformed element
         *
         * @param mappers
         * @param <E>
         * @return an new list contains transform result with the same mutable state as this list
         */
        public final <E, L extends Col<E>> L map(Class<L> clz, _.IFunc1... mappers) {
            ListComprehension<E> lc = lc().map(mappers);
            return copy(lc, readOnly());
        }

        /**
         * Run a <code>F.IFunc2&lt;E, T, E&gt;</code> style functor across
         * the list and return the final result
         *
         * @param initVal
         * @param func2
         * @param <E>
         * @return the result of the iteration by the functor specified
         */
        public final <E> E reduce(final E initVal, final _.IFunc2<E, T, E> func2) {
            return lc().reduce(initVal, func2);
        }

        /**
         * Run a 2 params function which accept the list element type and return the same type
         * without initial value
         *
         * @param func2
         * @return
         */
        public final T reduce(final _.IFunc2<T, T, T> func2) {
            return lc().reduce(func2);
        }

        public final boolean or(final _.IFunc1<Boolean, T> test) {
            return lc().or(test);
        }

        public final boolean and(final _.IFunc1<Boolean, T> test) {
            return lc().and(test);
        }
    }

    /**
     * Implement a {@link java.util.List} with extensions
     *
     * @param <T>
     */
    public static class List<T> extends Col<T> implements java.util.List<T> {

        public final java.util.List<T> _() {
            return (java.util.List<T>) _c;
        }

        @Override
        protected Collection<T> ensureReadOnly(boolean readOnly, Collection<T> col) {
            java.util.List<T> l = (java.util.List<T>) col;
            if (l instanceof List) {
                List<T> ll = (List<T>) l;
                if (ll.ro() == readOnly) {
                    return ll._c;
                }
                l = (List) ll._c;
            }
            if (readOnly) {
                return Collections.unmodifiableList(l);
            } else {
                return new LinkedList<T>(l);
            }
        }

        protected List(boolean readOnly) {
            super(readOnly, Collections.EMPTY_LIST);
        }

        protected List(boolean readOnly, java.util.List<T> list) {
            super(readOnly, list);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof java.util.List))
                return false;

            ListIterator<T> e1 = listIterator();
            ListIterator e2 = ((java.util.List) o).listIterator();
            while (e1.hasNext() && e2.hasNext()) {
                T o1 = e1.next();
                Object o2 = e2.next();
                if (!(o1 == null ? o2 == null : o1.equals(o2)))
                    return false;
            }
            return !(e1.hasNext() || e2.hasNext());
        }

        @Override
        public String toString() {
            return "[" + join() + "]";
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            return _().addAll(index, c);
        }

        @Override
        public T get(int index) {
            return _().get(index);
        }

        @Override
        public T set(int index, T element) {
            return _().set(index, element);
        }

        @Override
        public void add(int index, T element) {
            _().add(index, element);
        }

        @Override
        public T remove(int index) {
            return _().remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return _().indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return _().lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            return _().listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return _().listIterator(index);
        }

        @Override
        public java.util.List<T> subList(int fromIndex, int toIndex) {
            return _().subList(fromIndex, toIndex);
        }

        @Override
        protected List<T> copy(boolean readOnly) {
            return new List(readOnly, _());
        }

        @Override
        protected <E, L extends Col<E>> L copy(ListComprehension<E> lc, boolean readOnly) {
            return C0.list(lc).readOnly(readOnly);
        }

        // --- extensions to java.util.List

        public java.util.List<T> get() {
            return _();
        }

        /**
         * Return a list contains all elements of this list with the specified elements prepended.
         * <p/>
         * <p>The mutable state of the new list is the same as the underline list</p>
         * <p/>
         * <p>If no elements specified then a copy of this list is returned</p>
         *
         * @param elements to be prepended
         * @return an new list
         */
        public List<T> prepend(T... elements) {
            return C0.prepend(this, elements).readOnly(readOnly());
        }

        /**
         * Return a list contains all elements of this list with the specified elements appended.
         * <p/>
         * <p>The mutable state of the new list is the same as the underline list</p>
         * <p/>
         * <p>If no elements specified then a copy of this list is returned</p>
         *
         * @param elements to be appended
         * @return an new list
         */
        public List<T> append(T... elements) {
            return C0.append(this, elements).readOnly(readOnly());
        }

        /**
         * Return a list contains all elements in this list in reversed order
         * <p/>
         * <p>The new list's mutable state is the same as the underline list</p>
         *
         * @return an new list
         */
        public List<T> reverse() {
            return C0.lc(C0.reverse(this)).asList(readOnly());
        }

        /**
         * Return a list of all elements of this list without null
         * <p/>
         * <p>The new list's mutable state is the same as the underline list</p>
         *
         * @return an new list
         */
        public List<T> compact() {
            return lc().filter(_.If.NOT_NULL).asList(readOnly());
        }

        /**
         * Search the list with condition specified and return the first element in this list match the condition
         *
         * @param cond a 1 param functor return boolean typed value
         * @return the first element matches or <code>null</code> if match not found
         */
        public T first(final _.IFunc1<Boolean, T> cond) {
            return lc().first(cond);
        }

        /**
         * Search the list with condition specified and return the last element in this list matches the condition
         *
         * @param cond a 1 param functor return boolean typed value
         * @return the first element matches or <code>null</code> if match not found
         */
        public T last(final _.IFunc1<Boolean, T> cond) {
            return reverse().first(cond);
        }

        /**
         * Reduce from the last element with initial value
         *
         * @param initVal
         * @param func2
         * @param <E>
         * @return the result
         * @see {@link #reduce(Object, _.IFunc2)}
         */
        public <E> E reduceRight(final E initVal, final _.IFunc2<E, T, E> func2) {
            return reverse().reduce(initVal, func2);
        }

        /**
         * Reduce from the last element without initial value
         *
         * @param func2
         * @return
         */
        public T reduceRight(final _.IFunc2<T, T, T> func2) {
            return reverse().reduce(func2);
        }

        public final List<T> map(_.IFunc1... mappers) {
            return map(List.class, mappers);
        }
    }


    public static final class Set<T> extends Col<T> implements java.util.Set<T> {

        @Override
        protected Collection<T> ensureReadOnly(boolean readOnly, Collection<T> col) {
            java.util.Set<T> l = (java.util.Set<T>) col;
            if (l instanceof Set) {
                Set<T> ll = (Set<T>) l;
                if (ll.ro() == readOnly) {
                    return ll._c;
                }
                l = (Set) ll._c;
            }
            if (readOnly) {
                return Collections.unmodifiableSet(l);
            } else {
                if (col instanceof SortedSet) {
                    return new TreeSet<T>(l);
                } else {
                    return new HashSet<T>(l);
                }
            }
        }

        public java.util.Set<T> get() {
            return _();
        }

        private final java.util.Set<T> _() {
            return (java.util.Set<T>) _c;
        }

        protected Set(boolean readOnly, T... ta) {
            super(readOnly, new HashSet<T>(Arrays.asList(ta)));
        }

        protected Set(boolean readOnly, java.util.Set<T> set) {
            super(readOnly, set);
        }

        @Override
        protected Set<T> copy(boolean readOnly) {
            return C0.set(_c, readOnly);
        }

        @Override
        protected <E, L extends Col<E>> L copy(ListComprehension<E> lc, boolean readOnly) {
            return C0.set(lc).readOnly(readOnly);
        }

        public final Set<T> map(_.IFunc1... mappers) {
            return map(Set.class, mappers);
        }
    }

    public static class Map<K, V> implements java.util.Map<K, V> {
        public static class Entry<K, V> extends _.T2<K, V> implements java.util.Map.Entry<K, V> {
            public Entry(K _1, V _2) {
                super(_1, _2);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override
            public K getKey() {
                return _1;
            }

            @Override
            public V getValue() {
                return _2;
            }

            @Override
            public V setValue(V value) {
                throw E.unsupport();
            }

            public static <K, V> Entry<K, V> valueOf(K k, V v) {
                return new Entry<K, V>(k, v);
            }
        }

        private java.util.Map<K, V> _m;

        private boolean ro;

        protected Map(boolean readOnly, Object... args) {
            HashMap<K, V> map = new HashMap<K, V>();
            int len = args.length;
            for (int i = 0; i < len; i += 2) {
                K k = (K) args[i];
                V v = null;
                if (i + 1 < len) {
                    v = (V) args[i + 1];
                }
                map.put(k, v);
            }
            ro = readOnly;
            if (readOnly) {
                _m = Collections.unmodifiableMap(map);
            } else {
                _m = map;
            }
        }

        protected Map(boolean readOnly, java.util.Map<? extends K, ? extends V> map) {
            X.NPE(map);
            boolean sorted = map instanceof SortedMap;
            java.util.Map<K, V> m = sorted ? new TreeMap<K, V>() : new HashMap<K, V>();
            for (K k : map.keySet()) {
                V v = map.get(k);
                m.put(k, v);
            }
            ro = readOnly;
            if (readOnly) {
                _m = Collections.unmodifiableMap(m);
            } else {
                _m = m;
            }
        }

        @Override
        public int size() {
            return _m.size();
        }

        @Override
        public boolean isEmpty() {
            return _m.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return _m.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return _m.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return _m.get(key);
        }

        @Override
        public V put(K key, V value) {
            return _m.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return remove(key);
        }

        @Override
        public void putAll(java.util.Map<? extends K, ? extends V> m) {
            _m.putAll(m);
        }

        @Override
        public void clear() {
            _m.clear();
        }

        @Override
        public java.util.Set<K> keySet() {
            return _m.keySet();
        }

        @Override
        public Collection<V> values() {
            return _m.values();
        }

        @Override
        public Set<java.util.Map.Entry<K, V>> entrySet() {
            Set<java.util.Map.Entry<K, V>> set = C0.newSet();
            for (K k : _m.keySet()) {
                V v = _m.get(k);
                set.add(Entry.valueOf(k, v));
            }
            return set;
        }

        // --- extensions
        public boolean readOnly() {
            return ro;
        }

        public Map<K, V> readOnly(boolean readOnly) {
            if (ro ^ readOnly) {
                return new Map<K, V>(readOnly, _m);
            } else {
                return this;
            }
        }
    }


    // --- functors

    public static class f {
        public static <T> _.Visitor<T> addTo(final Collection<T> col) {
            return new _.Visitor<T>() {
                @Override
                public void visit(T t) throws _.Break {
                    col.add(t);
                }
            };
        }

        public static <T> _.Visitor<T> removeFrom(final Collection<T> col) {
            return new _.Visitor<T>() {
                @Override
                public void visit(T t) throws _.Break {
                    col.remove(t);
                }
            };
        }

    }

    public static void main(String[] args) {
        List<String> l = newList();
        String[] sa = array(l, String.class);
        System.out.println(sa);
    }
}
