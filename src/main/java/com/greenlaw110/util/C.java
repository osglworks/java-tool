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
package com.greenlaw110.util;

import java.lang.reflect.Array;
import java.util.*;

import static com.greenlaw110.util.ListComprehension.valueOf;


/**
 * Collection utilities
 */
public class C {

    public static final Map EMPTY_MAP = Collections.EMPTY_MAP;
    public static final List EMPTY_LIST = Collections.EMPTY_LIST;
    public static final Set EMPTY_SET = Collections.EMPTY_SET;

    public static final <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    public static final <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static final <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

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
    public static <T> Set<T> unique(Collection<T> c) {
        return new TreeSet<T>(c);
    }

    /**
     * Alias of {@link #unique(java.util.Collection)}
     *
     * @param c the collection
     * @return unique set of elements in <code>c</code>
     */
    public static <T> Set<T> uniq(Collection<T> c) {
        return unique(c);
    }

    public static <T> Set<T> readOnly(Set<? extends T> set) {
        return Collections.unmodifiableSet(set);
    }

    public static <K, V> Map<K, V> readOnly(Map<? extends K, ? extends V> m) {
        return Collections.unmodifiableMap(m);
    }

    public static <E> ListComprehension<E> lc(ListComprehension<E> lc) {
        return lc;
    }

    public static <E> ListComprehension<E> lc(E... ta) {
        return valueOf(ta);
    }

    public static <E> ListComprehension<E> lc(Iterable<E> ta) {
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

    private static <T> F.List<T> rw(List l) {
        return flist(l, false);
    }

    private static <T> F.List<T> ro(List l) {
        return flist(l, true);
    }

    private static <T> F.List<T> flist(List l, boolean readOnly) {
        if (l instanceof F.List) {
            F.List<T> fl = (F.List) l;
            if (fl.readonly() ^ readOnly) {
                l = fl.get();
            } else {
                return fl;
            }
        }
        if (readOnly) {
            return new F.List<T>(Collections.unmodifiableList(l), true);
        } else {
            return new F.List<T>(new ArrayList<T>(l), false);
        }
    }

    /**
     * return a readonly list of specified objects
     *
     * @param t
     * @param <T>
     * @return the list
     */
    public static <T> F.List<T> list(T... t) {
        return ro(Arrays.asList(t));
    }

    public static F.List<Integer> listp(int... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Long> listp(long... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Boolean> listp(boolean... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Float> listp(float... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Double> listp(double... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Short> listp(short... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Byte> listp(byte... ia) {
        return ro(newListp(ia));
    }

    public static F.List<Character> listp(char... ia) {
        return ro(newListp(ia));
    }

    public static <T> F.List<T> list(Iterable<T> it) {
        if (it instanceof List) {
            return ro((List)it);
        }
        return ro(newList(it));
    }
    
    public static <T> F.List<T> list(Iterable<T> it, boolean readonly) {
        if (!readonly) {
            return newList(it);
        } else {
            return list(it);
        }
    }
    

    public static <T> F.List<T> newList(T... t) {
        return rw(Arrays.asList(t));
    }

    public static F.List<Integer> newListp(int... ia) {
        F.List<Integer> l = newList();
        for (int i : ia) {
            l.add(i);
        }
        return l;
    }

    public static F.List<Long> newListp(long... ia) {
        F.List<Long> l = newList();
        for (long i : ia) {
            l.add(i);
        }
        return l;
    }

    public static F.List<Boolean> newListp(boolean... ia) {
        F.List<Boolean> l = newList();
        for (boolean i : ia) {
            l.add(i);
        }
        return l;
    }

    public static F.List<Float> newListp(float... ia) {
        F.List<Float> l = newList();
        for (float i : ia) {
            l.add(i);
        }
        return l;
    }

    public static F.List<Double> newListp(double... ia) {
        F.List<Double> l = newList();
        for (double i : ia) {
            l.add(i);
        }
        return l;
    }

    public static F.List<Short> newListp(short... ia) {
        F.List<Short> l = newList();
        for (short i : ia) {
            l.add(i);
        }
        return l;
    }

    public static List<Byte> newListp(byte... ia) {
        List<Byte> l = newList();
        for (byte i : ia) {
            l.add(i);
        }
        return l;
    }

    public static F.List<Character> newListp(char... ia) {
        F.List<Character> l = newList();
        for (char i : ia) {
            l.add(i);
        }
        return l;
    }

    public static <T> F.List<T> newList(Iterable<T> it) {
        if (it instanceof List) {
            return rw((List)it);
        }
        F.List<T> l = newList();
        for (T t : it) {
            l.add(t);
        }
        return l;
    }

    public static <T> T[] array(Collection<T> col) {
        E.invalidArg(col.isEmpty(), "Class type must be present if collection is empty");
        Class<T> clz = (Class<T>) col.iterator().next().getClass();
        return array(col, clz);
    }

    public static <T> T[] array(Collection<T> col, Class<T> clz) {
        T[] ta = (T[]) Array.newInstance(clz, 0);
        return col.toArray(ta);
    }

    public static <T> Set<T> set(T... t) {
        return readOnly(new HashSet<T>(Arrays.asList(t)));
    }

    public static <T> Set<T> newSet(T... t) {
        return new HashSet<T>(set(t));
    }

    public static <K, V> Map<K, V> map(Object... args) {
        Map<K, V> map = new HashMap<K, V>();
        int len = args.length;
        for (int i = 0; i < len; i += 2) {
            K k = (K) args[i];
            V v = null;
            if (i + 1 < len) {
                v = (V) args[i + 1];
            }
            map.put(k, v);
        }
        return readOnly(map);
    }

    public static <K, V> Map<K, V> newMap(Object... args) {
        Map<K, V> ro = map(args);
        return new HashMap<K, V>(ro);
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

    public static void walkThrough(Iterable iterable) {
        try {
            for (Object el : iterable) {
            }
        } catch (F.Visitor.Break e) {
            // ignore
        }
    }

    public static <T> T fold(Iterable iterable) {
        try {
            for (Object el : iterable) {
            }
            return null;
        } catch (F.Visitor.Break b) {
            return b.get();
        }
    }

    public static <T> F.List<T> prepend(List<T> list, T... ts) {
        F.List<T> l = newList(ts);
        l.addAll(list);
        return l;
    }

    public static <T> F.List<T> append(List<T> list, T... ts) {
        F.List<T> l = newList(list);
        l.addAll(Arrays.asList(ts));
        return l;
    }

    public static <T> List<T> head(final List<T> list, final int n) {
        if (0 == n) {
            return list;
        }
        int size = list.size();
        if (n > size) {
            return list;
        } else if (n < 0) {
            return tail(list, size + n);
        } else {
            return lc(list).filterOnIndex(_.f.lessThan(n)).asList();
        }
    }

    public static <T> List<T> tail(List<T> list, final int n) {
        if (0 == n) {
            return list;
        }

        int size = list.size();
        if (n > size) {
            return list;
        } else if (n < 0) {
            return head(list, size + n);
        } else {
            int n0 = size - n - 1;
            return lc(list).filterOnIndex(_.f.greatThan(n0)).asList();
        }
    }

    // --- functors

    public static class f {
        public static <T> F.Visitor<T> addTo(final Collection<T> col) {
            return new F.Visitor<T>() {
                @Override
                public void visit(T t) throws Break {
                    col.add(t);
                }
            };
        }

        public static <T> F.Visitor<T> removeFrom(final Collection<T> col) {
            return new F.Visitor<T>() {
                @Override
                public void visit(T t) throws Break {
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
