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

import java.util.*;

/**
 * Collection utilities
 */
public class C {
    
    public static final Map EMPTY_MAP = Collections.EMPTY_MAP;
    public static final List EMPTY_LIST = Collections.EMPTY_LIST;
    public static final Set EMPTY_SET = Collections.EMPTY_SET;
    
    public static final <K,V> Map<K,V> emptyMap() {
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
     * @param c the collection
     * @return unique set of elements in <code>c</code>
     */
    public static <T> Set<T> unique(Collection <T> c) {
        return new TreeSet<T>(c);
    }

    /**
     * Alias of {@link #unique(java.util.Collection)}
     * @param c the collection
     * @return unique set of elements in <code>c</code>
     */
    public static <T> Set<T> uniq(Collection<T> c) {
        return unique(c);
    }

    public static <T> List<T> readOnly(List<? extends T> list) {
        return Collections.unmodifiableList(list);
    }
    
    public static <T> Set<T> readOnly(Set<? extends T> set) {
        return Collections.unmodifiableSet(set);
    }
    
    public static <K,V> Map<K,V> readOnly(Map<? extends K, ? extends V> m) {
        return Collections.unmodifiableMap(m);
    }

    /**
     * return a readonly list of specified objects
     *
     * @param t
     * @param <T>
     * @return the list
     */
    public static <T> List<T> list(T ... t) {
        return Collections.unmodifiableList(Arrays.asList(t));
    }

    /**
     * Return a readonly map of specified args
     * @param args
     * @return the map
     */
    public static <K, V> Map<K, V> map(Object... args) {
        int len = args.length;
        if (0 == len) {
            return emptyMap();
        }
        Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < len; i += 2) {
            K k = (K) args[i];
            V v = null;
            if (i + 1 < len) {
                v = (V)args[i + 1];
            }
            map.put(k, v);
        }
        return readOnly(map);
    }

    public static <T> Iterable<T> reverse(final List<T> l) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    int cursor = l.size() - 1;
                    @Override
                    public boolean hasNext() {
                        return cursor > -1;
                    }

                    @Override
                    public T next() {
                        return l.get(cursor--);
                    }

                    @Override
                    public void remove() {
                        l.remove(cursor);
                    }
                };
            }
        };
    }

}
