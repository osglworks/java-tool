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

import org.osgl._;
import org.osgl.exception.InvalidStateException;
import org.osgl.exception.UnexpectedException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Aggregate core utilities of OSGL, including those support functional programming
 */
public class X {

    // you know it 
    private X() {}

    /**
     * Defines an instance to be used in views
     */
    public final X INSTANCE = new X();
    public final X instance = INSTANCE;

    public final static String fmt(String tmpl, Object... args) {
        return S.fmt(tmpl, args);
    }
    
    public final static long ts() {
        return System.currentTimeMillis();
    }

    /**
     * Throw out NullPointerException if any one of the passed objects is null
     *
     * @param args the object instances to be tested
     */
    public final static void NPE(Object... args) {
        E.NPE(args);
    }

    public final static InvalidStateException invalidState() {
        return E.invalidState();
    }

    public final static InvalidStateException invalidState(String message, String args) {
        return E.invalidState(message, args);
    }

    public final static UnexpectedException unexpected(Throwable cause) {
        return E.unexpected(cause);
    }

    public final static UnexpectedException unexpected(String message, String args) {
        return E.unexpected(message, args);
    }

    public final static S.Str str(Object o) {
        return S.str(o);
    }

    public final static <T> Meta meta(T o) {
        return new Meta(o);
    }

    public final static boolean equal(Object a, Object b) {
        return isEqual(a, b);
    }

    public final static boolean eq(Object a, Object b) {
        return isEqual(a, b);
    }

    public final static boolean neq(Object a, Object b) {
        return !isEqual(a, b);
    }

    public final static boolean notEqual(Object a, Object b) {
        return !isEqual(a, b);
    }

    public final static boolean isEqual(Object a, Object b) {
        if (a == b) return true;
        if (null == a) return b != null;
        else return a.equals(b);
    }

    public final static boolean isEqual(Number a, Number b) {
        return N.eq(a, b);
    }

    public final static boolean isNotEqual(Object a, Object b) {
        return !isEqual(a, b);
    }


    public final static <T> List<T> list(T... el) {
        return C0.list(el);
    }

    public final static <T> Class<T> classForName(String className) {
        try {
            return (Class<T>)Class.forName(className);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
    
    public final static <T> T newInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
    
    private static boolean testConstructor(Constructor c, Object p, int pos) {
        E.invalidArgIf(pos < 0);
        Class[] pts = c.getParameterTypes();
        if (pos < pts.length) {
            Class pt = pts[pos];
            return (pt.isAssignableFrom(p.getClass()));
        } else {
            return false;
        }
    }
    
    public final static <T, P1> T newInstance(Class<T> c, P1 p1) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                if (testConstructor(ct, p1, 0)) {
                    return ct.newInstance(p1);
                }
            }
            throw E.unexpected("constructor not found");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
    
    public final static <T, P1, P2> T newInstance(Class<T> c, P1 p1, P2 p2) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                if (!testConstructor(ct, p1, 0)) {
                    continue;
                }
                if (!testConstructor(ct, p2, 1)) {
                    continue;
                }
                return ct.newInstance(p1, p2);
            }
            throw E.unexpected("constructor not found");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
    
    public final static <T, P1, P2, P3> T newInstance(Class<T> c, P1 p1, P2 p2, P3 p3) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                if (!testConstructor(ct, p1, 0)) {
                    continue;
                }
                if (!testConstructor(ct, p2, 1)) {
                    continue;
                }
                if (!testConstructor(ct, p3, 2)) {
                    continue;
                }
                return ct.newInstance(p1, p2, p3);
            }
            throw E.unexpected("constructor not found");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
    
    public final static <T, P1, P2, P3, P4> T newInstance(Class<T> c, P1 p1, P2 p2, P3 p3, P4 p4) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                if (!testConstructor(ct, p1, 0)) {
                    continue;
                }
                if (!testConstructor(ct, p2, 1)) {
                    continue;
                }
                if (!testConstructor(ct, p3, 2)) {
                    continue;
                }
                if (!testConstructor(ct, p4, 4)) {
                    continue;
                }
                return ct.newInstance(p1, p2, p3, p4);
            }
            throw E.unexpected("constructor not found");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
    
    public final static <T, P1, P2, P3, P4, P5> T newInstance(Class<T> c, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                if (!testConstructor(ct, p1, 0)) {
                    continue;
                }
                if (!testConstructor(ct, p2, 1)) {
                    continue;
                }
                if (!testConstructor(ct, p3, 2)) {
                    continue;
                }
                if (!testConstructor(ct, p4, 4)) {
                    continue;
                }
                if (!testConstructor(ct, p5, 5)) {
                    continue;
                }
                return ct.newInstance(p1, p2, p3, p4, p5);
            }
            throw E.unexpected("constructor not found");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public static final boolean isArray(Object o) {
        if (null == o) {
            return false;
        }
        return o.getClass().isArray();
    }

    public static final <T> T[] newArray(Class<T> clz, int len) {
        return (T[]) Array.newInstance(clz, len);
    }
    
    public static final <T> T[][] newArray(Class<T> clz, int len1, int len2) {
        return (T[][]) Array.newInstance(clz, len1, len2);
    }
    
    public static final <T> T[][][] newArray(Class<T> clz, int len1, int len2, int len3) {
        return (T[][][]) Array.newInstance(clz, len1, len2, len3);
    }
    
    public static int arrayLength(Object array) {
        return Array.getLength(array);
    }

    public static <T> T arrayGet(Object array, int index) {
        return (T)Array.get(array, index);
    }

    public static boolean arrayGetBool(Object array, int index) {
        return Array.getBoolean(array, index);
    }

    public static byte arrayGetByte(Object array, int index) {
        return Array.getByte(array, index);
    }

    public static char arrayGetChar(Object array, int index) {
        return Array.getChar(array, index);
    }

    public static short arrayGetShort(Object array, int index) {
        return Array.getShort(array, index);
    }
    
    public static int arrayGetInt(Object array, int index) {
        return Array.getInt(array, index);
    } 

    public static long arrayGetLong(Object array, int index) {
        return Array.getLong(array, index);
    }

    public static float arrayGetFloat(Object array, int index) {
        return Array.getFloat(array, index);
    }
    
    public static double arrayGetDouble(Object array, int index) {
        return Array.getDouble(array, index);
    }

    public static void arraySet(Object array, int index, Object o) {
        Array.set(array, index, o);
    }

    public static void arraySetBool(Object array, int index, boolean o) {
        Array.setBoolean(array, index, o);
    }

    public static void arraySetByte(Object array, int index, byte o) {
        Array.setByte(array, index, o);
    }

    public static void arraySetChar(Object array, int index, char o) {
        Array.setChar(array, index, o);
    }

    public static void arraySetShort(Object array, int index, short o) {
        Array.setShort(array, index, o);
    }

    public static void arraySetInt(Object array, int index, int o) {
        Array.setInt(array, index, o);
    }

    public static void arraySetLong(Object array, int index, long o) {
        Array.setLong(array, index, o);
    }

    public static void arraySetFloat(Object array, int index, float o) {
        Array.setFloat(array, index, o);
    }

    public static void arraySetDouble(Object array, int index, double o) {
        Array.setDouble(array, index, o);
    }

    public final static <T> T times(_.IFunc0<T> func, int n) {
        if (n < 0) {
            throw E.invalidArg("the number of times must not be negative");
        }
        if (n == 0) {
            return null;
        }
        T result = func.apply();
        for (int i = 1; i < n; ++i) {
            func.apply();
        }
        return result;
    }
    
    public final static <T> T ensureGet(T t1, T def1) {
        if (null != t1) {
            return t1;
        }
        E.invalidArgIf(null == def1);
        return def1;
    }
    
    public final static <T> T ensureGet(T t1, T def1, T def2) {
        if (null != t1) {
            return t1;
        }
        if (null != def1) {
            return def1;
        }
        E.npeIf(null == def2);
        return def2;
    }
    
    public final static <T> T ensureGet(T t1, T def1, T def2, T def3) {
        if (null != t1) {
            return t1;
        }
        if (null != def1) {
            return def1;
        }
        if (null != def2) {
            return def2;
        }
        E.npeIf(null == def3);
        return def2;
    }
    
    public final static <T> T ensureGet(T t1, List<T> defs) {
        if (null != t1) {
            return t1;
        }
        for (T t : defs) {
            if (null != t) {
                return t;
            }
        }
        throw new NullPointerException();
    }
    
    public final static <T> T times(_.IFunc1<T, T> func, T initVal, int n) {
        if (n < 0) {
            throw E.invalidArg("the number of times must not be negative");
        }
        if (n == 0) {
            return initVal;
        }
        T retVal = initVal;
        for (int i = 1; i < n; ++i) {
            retVal = func.apply(retVal);
        }
        return retVal;
    }
    
    // --- 
    public static class Meta<T> {
        private T o;

        public Meta(T obj) {
            X.NPE(obj);
            o = obj;
        }
        
        public boolean is(Class<?> clz) {
            return clz.isAssignableFrom(o.getClass());
        }

        public boolean isNot(Class<?> clz) {
            return !is(clz);
        }

        public boolean kindOf(Object object) {
            return is(object.getClass());
        }
        
    }
    
    // -- functors
    public static class f {

        public static <T> _.If<T> and(final _.IFunc1<Boolean, T>... conds) {
            return and(C0.list(conds));
        }
    
        public static <T> _.If<T> or(final java.util.List<_.IFunc1<Boolean, T>> conds) {
            return not(and(conds));
        }
        
        public static <T> _.If<T> or(final _.IFunc1<Boolean, T>... conds) {
            return not(and(conds));
        }
    
        public static <T> _.If<T> not(final _.IFunc1<Boolean, T> cond) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return !cond.apply(t);
                }
            };
        }
        
        public static <T> _.If<T> and(final java.util.List<_.IFunc1<Boolean, T>> conds) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    for (_.IFunc1<Boolean, T> cond : conds) {
                        if (!cond.apply(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }
        
        public static <T> _.IFunc0<T> next(final Iterator<T> itr) {
            final T t = itr.next();
            return new _.IFunc0<T>() {
                @Override
                public T apply() {
                    return t;
                }
            };
        }
    
        public static _.IFunc0<Integer> next(final int i) {
            return new _.IFunc0<Integer>() {
                @Override
                public Integer apply() {
                    return i + 1;
                }
            };
        }
    
        public static _.IFunc0<Integer> prev(final int i) {
            return new _.IFunc0<Integer>() {
                @Override
                public Integer apply() {
                    return i - 1;
                }
            };
        }
    
        public static _.IFunc0<Character> next(final char c) {
            return new _.IFunc0<Character>() {
                @Override
                public Character apply() {
                    return (char)(c + 1);
                }
            };
        }
    
        public static _.IFunc0<Character> prev(final char c) {
            return new _.IFunc0<Character>() {
                @Override
                public Character apply() {
                    return (char)(c - 1);
                }
            };
        }
    
        public static <T> _.Transformer<T, String> toStr() {
            return new _.Transformer<T, String>() {
                @Override
                public String transform(T t) {
                    return null == t ? "" : t.toString();
                }
            };
        }
        
        public static <T extends Comparable<T>> _.If<T> lt(final T o) {
            return lessThan(o);
        }
        
        public static <T extends Comparable<T>> _.If<T> lessThan(final T o) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return t.compareTo(o) < 0;
                }
            };
        }
        
        public static <T extends Comparable<T>> _.If<T> gt(final T o) {
            return greatThan(o);
        }
    
        public static <T extends Comparable<T>> _.If<T> greatThan(final T o) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return t.compareTo(o) > 0;
                }
            };
        }
        
        public static <T> _.If<T> eq(final T o) {
            return equal(o);
        }
        
        public static <T> _.If<T> equal(final T o) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return X.eq(t, o);
                }
            };
        }
        
    }

    private static class Trav<E> extends java.util.ArrayList<E> implements ITraversable<E> {
        @Override
        public E first() throws NoSuchElementException {
            if (isEmpty()) {
                throw new NoSuchElementException();
            }
            return get(0);
        }

        @Override
        public E head() throws NoSuchElementException {
            if (isEmpty()) {
                throw new NoSuchElementException();
            }
            return get(0);
        }

        @Override
        public E last() throws NoSuchElementException {
            if (isEmpty()) {
                throw new NoSuchElementException();
            }
            return get(size() - 1);
        }

        @Override
        public boolean isLimited() {
            return true;
        }

        @Override
        public Trav<E> tail() throws UnsupportedOperationException {
            if (isEmpty()) {
                throw new UnsupportedOperationException();
            }
            Trav<E> t = new Trav<E>();
            t.addAll(this);
            t.remove(0);
            return t;
        }

        @Override
        public Trav<E> head(int n) {
            if (n < 0) {
                return tail(-n);
            }

            Trav<E> t = new Trav<E>();
            int i = 0;
            if (0 == n) {
                return t;
            } else {
                for (E e: this) {
                    if (i++ >= n) {
                        break;
                    }
                    t.add(e);
                }
            }
            return t;
        }

        @Override
        public Trav<E> reverse() throws UnsupportedOperationException {
            Trav<E> trav = new Trav<E>();
            for (int i = size() - 1; i >= 0; --i) {
                trav.add(get(i));
            }
            return trav;
        }

        @Override
        public Trav<E> tail(int n) {
            if (n < 0) {
                return head(-n);
            }
            Trav<E> t = new Trav<E>();
            int i = 0;
            if (0 == n) {
                return t;
            } else {
                int size = size();
                for (i = size - n; i < size; ++i) {
                    t.add(get(i));
                }
            }
            return t;
        }

        @Override
        public Trav<E> take(int n) {
            return head(n);
        }

        @Override
        public <E2> ITraversable<E2> map(_.IFunc1<E2, E> mapper) {
            if (isEmpty()) {
                return new Trav<E2>();
            }
            return new Cons<E>(head(), tail()).map(mapper);
        }

        public <E2> ITraversable<E2> lazymap(_.IFunc1<E2, E> mapper) {
            if (isEmpty()) {
                return new Trav<E2>();
            }
            final ITraversable<E> me = this;
            return new Lazy<E>(head(), new _.F0<ITraversable<E>>(){
                @Override
                public ITraversable<E> apply() {
                    return me.tail();
                }
            }).map(mapper);
        }
    }

    public static class Cons<E> implements ITraversable<E> {
        private E head;
        private ITraversable<E> tail;
        Cons(E head, ITraversable<E> tail) {
            this.head = head;
            this.tail = tail;
            System.out.println("constructing cons...");
        }
        @Override
        public E first() throws NoSuchElementException {
            return head;
        }

        @Override
        public E head() throws NoSuchElementException {
            return head;
        }

        @Override
        public Cons<E> head(int n) {
            return new Cons(head, tail.head(n - 1));
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public E last() throws NoSuchElementException, UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isLimited() {
            return false;
        }

        @Override
        public Cons<E> reverse() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cons<E> tail() throws UnsupportedOperationException {
            if (tail instanceof Cons) {
                return (Cons<E>)tail;
            }
            return new Cons<E>(tail.head(), tail.tail());
        }

        @Override
        public Cons<E> tail(int n) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cons<E> take(int n) {
            return head(n);
        }

        @Override
        public <E2> Cons<E2> map(_.IFunc1<E2, E> mapper) {
            if (tail.isEmpty()) {
                return new Cons<E2>(mapper.apply(head()), new Trav<E2>());
            }
            return new Cons<E2>(mapper.apply(head()), tail().map(mapper));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            Cons<E> cons = this;
            sb.append(cons.head);
            while(!cons.tail.isEmpty()) {
                cons = cons.tail();
                sb.append(",").append(cons.head);
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class Lazy<E> implements ITraversable<E> {
        private E head;
        private _.F0<ITraversable<E>> tail;

        public Lazy(E head, _.IFunc0<ITraversable<E>> tail) {
            this.head = head;
            this.tail = _.toF0(tail);
            System.out.println("constructing lazy...");
        }

        @Override
        public E first() throws NoSuchElementException {
            return head;
        }

        @Override
        public E head() throws NoSuchElementException {
            return head;
        }

        @Override
        public ITraversable<? super E> head(int n) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isEmpty() {
            return null == head;
        }

        @Override
        public E last() throws NoSuchElementException, UnsupportedOperationException {
            return null;
        }

        @Override
        public boolean isLimited() {
            return true;
        }

        @Override
        public ITraversable<E> reverse() throws UnsupportedOperationException {
            return null;
        }

        private ITraversable<E> tail0;
        @Override
        public Lazy<E> tail() throws UnsupportedOperationException {
            if (null == tail0) {
                tail0 = tail.apply();
            }
            if (tail0.isEmpty()) {
                return new Lazy<E>(null, _.F0);
            }
            return new Lazy<E>(tail0.head(), new _.F0<ITraversable<E>>(){
                @Override
                public ITraversable<E> apply() {
                    if (tail0.isEmpty()) {
                        return new Trav<E>();
                    }
                    return tail0.tail();
                }
            });
        }

        @Override
        public ITraversable<E> tail(int n) throws UnsupportedOperationException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public ITraversable<E> take(int n) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <E2> Lazy<E2> map(final _.IFunc1<E2, E> mapper) {
            final Lazy<E> me = this;
            return new Lazy<E2>(mapper.apply(head), new _.F0<ITraversable<E2>>(){
                @Override
                public Lazy<E2> apply() {
                    if (me.isEmpty()) {
                        return new Lazy<E2>(null, _.F0);
                    }
                    return me.tail().map(mapper);
                }
            });
        }

        @Override
        public String toString() {
            Lazy<E> cons = this;
            if (cons.isEmpty()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder("[");
            sb.append(cons.head);
            cons = cons.tail();
            while(!cons.isEmpty()) {
                sb.append(",").append(cons.head);
                cons = cons.tail();
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Trav<String> ts = new Trav<String>();
        ts.addAll(Arrays.asList("ab".split(",")));
        ts.add(null);
        ITraversable<Boolean> ts2 = ts.map(_.F.isNull(String.class));
        ITraversable<Boolean> ts3 = ts.lazymap(_.F.isNull(String.class));
        System.out.println(ts2);
        System.out.println(ts3);

        C1.List<Integer> il = C1.Mutable.list(1, 2, 3);
        il.subList(0, 2).add(5);
        System.out.println(il);

        System.out.println(_.NONE.f.IS_DEFINED.apply());
        System.out.println(_.some(123).f.NOT_DEFINED.apply());
    }

}