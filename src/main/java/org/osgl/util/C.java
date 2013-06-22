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
public class C {

    public static final Map EMPTY_MAP = Collections.EMPTY_MAP;
    public static final List EMPTY_LIST = list();
    public static final Set EMPTY_SET = Collections.EMPTY_SET;

    public static final <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    public static final <T> List<T> emptyList() {
        return (List<T>)EMPTY_LIST;
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

    private static <T> List<T> rw(java.util.List l) {
        return flist(l, false);
    }

    private static <T> List<T> ro(java.util.List l) {
        return flist(l, true);
    }

    private static <T> List<T> flist(java.util.List l, boolean readOnly) {
        if (l instanceof List) {
            List<T> fl = (List) l;
            if (fl.readonly() ^ readOnly) {
                l = fl.get();
            } else {
                return fl;
            }
        }
        if (readOnly) {
            return new List<T>(Collections.unmodifiableList(l), true);
        } else {
            return new List<T>(new ArrayList<T>(l), false);
        }
    }

    /**
     * return a readonly list of specified objects
     *
     * @param t
     * @param <T>
     * @return the list
     */
    public static <T> List<T> list(T... t) {
        return ro(Arrays.asList(t));
    }

    public static List<Integer> listp(int... ia) {
        return ro(newListp(ia));
    }

    public static List<Long> listp(long... ia) {
        return ro(newListp(ia));
    }

    public static List<Boolean> listp(boolean... ia) {
        return ro(newListp(ia));
    }

    public static List<Float> listp(float... ia) {
        return ro(newListp(ia));
    }

    public static List<Double> listp(double... ia) {
        return ro(newListp(ia));
    }

    public static List<Short> listp(short... ia) {
        return ro(newListp(ia));
    }

    public static List<Byte> listp(byte... ia) {
        return ro(newListp(ia));
    }

    public static List<Character> listp(char... ia) {
        return ro(newListp(ia));
    }

    public static <T> List<T> list(Iterable<T> it) {
        if (it instanceof List) {
            return ro((List)it);
        }
        return ro(newList(it));
    }
    
    public static <T> List<T> list(Iterable<T> it, boolean readonly) {
        if (!readonly) {
            return newList(it);
        } else {
            return list(it);
        }
    }
    

    public static <T> List<T> newList(T... t) {
        return rw(Arrays.asList(t));
    }

    public static List<Integer> newListp(int... ia) {
        List<Integer> l = newList();
        for (int i : ia) {
            l.add(i);
        }
        return l;
    }

    public static List<Long> newListp(long... ia) {
        List<Long> l = newList();
        for (long i : ia) {
            l.add(i);
        }
        return l;
    }

    public static List<Boolean> newListp(boolean... ia) {
        List<Boolean> l = newList();
        for (boolean i : ia) {
            l.add(i);
        }
        return l;
    }

    public static List<Float> newListp(float... ia) {
        List<Float> l = newList();
        for (float i : ia) {
            l.add(i);
        }
        return l;
    }

    public static List<Double> newListp(double... ia) {
        List<Double> l = newList();
        for (double i : ia) {
            l.add(i);
        }
        return l;
    }

    public static List<Short> newListp(short... ia) {
        List<Short> l = newList();
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

    public static List<Character> newListp(char... ia) {
        List<Character> l = newList();
        for (char i : ia) {
            l.add(i);
        }
        return l;
    }

    public static <T> List<T> newList(Iterable<T> it) {
        if (it instanceof List) {
            return rw((List)it);
        }
        List<T> l = newList();
        for (T t : it) {
            l.add(t);
        }
        return l;
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

    public static <T> Set<T> set(T... t) {
        return readOnly(new HashSet<T>(Arrays.asList(t)));
    }

    public static <T> Set<T> set(Collection<T> c) {
        return readOnly(new HashSet<T>(c));
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

    public static <K, V> Map<K, V> map(Map<K, V> map) {
        if (null == map) {
            return EMPTY_MAP;
        }
        return readOnly(map);
    }

    public static <K, V> Map<K, V> newMap(Object... args) {
        Map<K, V> ro = map(args);
        return new HashMap<K, V>(ro);
    }

    public static <K, V> Map<K, V> newMap(Map<K, V> map) {
        return new HashMap<K, V>(map);
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
            return lc(list).filterOnIndex(_.f.lessThan(n)).asList();
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
            return lc(list).filterOnIndex(_.f.greatThan(n0)).asList();
        }
    }

    
    // --- Collections support functional programming

    public static class List<T> extends AbstractList<T> implements java.util.List<T> {
        private final java.util.List<T> _l;
        private final boolean readonly;

        public List(java.util.List<T> list, boolean readonly) {
            E.NPE(list);
            _l = list;
            this.readonly = readonly;
        }

        @Override
        public int size() {
            return _l.size();
        }

        @Override
        public boolean isEmpty() {
            return _l.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return _l.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return _l.iterator();
        }

        @Override
        public Object[] toArray() {
            return _l.toArray();
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            return _l.toArray(a);
        }

        @Override
        public boolean add(T t) {
            return _l.add(t);
        }
        
        @Override
        public boolean remove(Object o) {
            return _l.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return _l.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            return _l.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            return _l.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return _l.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return _l.retainAll(c);
        }

        @Override
        public void clear() {
            _l.clear();
        }

        @Override
        public T get(int index) {
            return _l.get(index);
        }

        @Override
        public T set(int index, T element) {
            return _l.set(index, element);
        }

        @Override
        public void add(int index, T element) {
            _l.add(index, element);
        }

        @Override
        public T remove(int index) {
            return _l.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return _l.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return _l.lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            return _l.listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return _l.listIterator(index);
        }

        @Override
        public java.util.List<T> subList(int fromIndex, int toIndex) {
            return _l.subList(fromIndex, toIndex);
        }
        
        // --- extensions to java.util.List
        
        public java.util.List<T> get() {
            return _l;
        }

        /**
         * Is this list reaonly
         * @return true if this list is an immutable list
         */
        public boolean ro() {
            return readonly;
        }

        /**
         * Alias of {@link #ro()}
         * @return true if this list is an immutable list
         */
        public boolean readonly() {
            return readonly;
        }

        /**
         * Is this list writable
         * @return true if this list is a mutable list
         */
        public boolean rw() {
            return !readonly;
        }

        /**
         * alias of {@link #rw()}
         * @return true if this list is a mutable list
         */
        public boolean readwrite() {
            return !readonly;
        }

        /**
         * Return a list contains all elements of this list with
         * readonly attribute specified
         * 
         * @param readonly 
         * @return self or an new list depending on the specified readonly param and readonly attribute of this list
         */
        public List<T> readonly(boolean readonly) {
            if (readonly() == readonly) {
                return this;
            } else {
                return C.list(this, readonly);
            }
        }

        /**
         * Return a {@link ListComprehension list comprehension instance} of this list
         */
        public ListComprehension<T> lc() {
            return C.lc(this);
        }

        /**
         * Accept a visitor to iterate ach element in the list
         * @param visitor
         * @return this list
         */
        public List<T> accept(F.IFunc1<?, T> visitor) {
            lc().each(visitor);
            return this;
        }

        /**
         * Apply a list of mapper (transformer) to each element in this list, 
         * and return an new list contains the transformed element
         * 
         * @param mappers
         * @param <E>
         * @return an new list contains transform result with the same mutable state as this list
         */
        public <E> List<E> map(F.IFunc1... mappers) {
            return (List<E>)lc().map(mappers).asList(readonly());
        }

        /**
         * Print out this list each element in a separate line
         * 
         * @return this list
         */
        public List<T> println() {
            return accept(IO.f.PRINTLN);
        }

        /**
         * Return a string concatenated by elements of this list separated by "," 
         * 
         * @return the string
         */
        public String join() {
            return S.join(",", this);
        }

        /**
         * Return a string concatenated by elements of this list separated by specified separator
         * @param sep
         * @return the string
         */
        public String join(String sep) {
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
        public String join(String sep, String prefix, String suffix) {
            return S.join(sep, prefix, suffix, this);
        }


        /**
         * Return a list contains all elements of this list with the specified elements prepended.
         * 
         * <p>The mutable state of the new list is the same as the underline list</p> 
         * 
         * <p>If no elements specified then a copy of this list is returned</p>
         * 
         * @param elements to be prepended
         * @return an new list
         */
        public List<T> prepend(T... elements) {
            return C.prepend(this, elements).readonly(readonly());
        }
        
        /**
         * Return a list contains all elements of this list with the specified elements appended.
         * 
         * <p>The mutable state of the new list is the same as the underline list</p> 
         * 
         * <p>If no elements specified then a copy of this list is returned</p>
         * 
         * @param elements to be appended
         * @return an new list
         */
        public List<T> append(T... elements) {
            return C.append(this, elements).readonly(readonly());
        }

        /**
         * Return a set contains all unique elments in this list  
         * 
         * @return a set
         */
        public Set<T> uniq() {
            return C.uniq(this);
        }

        /**
         * Return a list contains all elements in this list in reversed order
         * 
         * <p>The new list's mutable state is the same as the underline list</p>
         * 
         * @return an new list
         */
        public List<T> reverse() {
            return C.lc(C.reverse(this)).asList(readonly());
        }

        /**
         * Return a list of all elements of this list without null
         * 
         * <p>The new list's mutable state is the same as the underline list</p>
         * 
         * @return an new list
         */
        public List<T> compact() {
            return lc().filter(F.If.NOT_NULL).asList(readonly());
        }

        /**
         * Search the list with condition specified and return the first element in this list match the condition
         * 
         * @param cond a 1 param functor return boolean typed value 
         * @return the first element matches or <code>null</code> if match not found
         */
        public T first(final F.IFunc1<Boolean, T> cond) {
            return lc().first(cond);
        }
        
        /**
         * Search the list with condition specified and return the last element in this list matches the condition
         * 
         * @param cond a 1 param functor return boolean typed value 
         * @return the first element matches or <code>null</code> if match not found
         */
        public T last(final F.IFunc1<Boolean, T> cond) {
            return reverse().first(cond);
        }

        /**
         * Return a List contains all elements contained in this list but not in the specified collection 
         * @param c
         * @return an new list with mutable state the same as the underline list
         */
        public List<T> without(Collection<T> c) {
            List<T> l0 = C.newList(get());
            l0.removeAll(c);
            return l0.readonly(readonly());
        }
        
        /**
         * Return a List contains all elements contained in this list but not in the specified array 
         * @param elements
         * @return an new list with mutable state the same as the underline list
         */
        public List<T> without(T ... elements) {
            return without(C.list(elements));
        }
        
        /**
         * Return a List contains elements that contained in both this list and the specified collection 
         * @param c the collection
         * @return an new list with mutable state the same as the underline list
         */
        public List<T> intersect(Collection<T> c) {
            List<T> l0 = C.newList(get());
            l0.retainAll(c);
            return l0.readonly(readonly());
        }

        /**
         * Return a List contains elements that contained in both this list and the specified elements 
         * @param elements
         * @return an new list with mutable state the same as the underline list
         */
        public List<T> intersect(T... elements) {
            return intersect(C.list(elements));
        }

        /**
         * Return a list contains elements in this list matches the filters specified
         * @param filters an array of filters (a functor accept one param and return boolean type
         * @return an new list with mutable state the same as this list
         */
        public List<T> filter(final F.IFunc1<Boolean, T>... filters) {
            return lc().filter(filters).asList(readonly());
        }

        /**
         * Run a <code>F.IFunc2&lt;E, T, E&gt;</code> style functor across
         * the list and return the final result
         * @param initVal
         * @param func2
         * @param <E>
         * @return the result of the iteration by the functor specified
         */
        public <E> E reduce(final E initVal, final F.IFunc2<E, T, E> func2) {
            return lc().reduce(initVal, func2);
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
