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

import org.osgl.exception.InvalidStateException;
import org.osgl.exception.UnexpectedException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

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

    /**
     * Calculate hashcode from specified objects
     * @param args
     * @return the calculated hash code
     */
    public final static int hc(Object... args) {
        int i = 17;
        for (Object o: args) {
            i = 31 * i + ((null == o) ? 0 : o.hashCode());
        }
        return i;
    }

    /**
     * Alias of {@link #hc(Object...)}
     * 
     * @param args
     * @return the calculated hash code
     */
    public final static int hashCode(Object... args) {
        return hc(args);
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
                public boolean eval(T t) {
                    return !cond.apply(t);
                }
            };
        }
        
        public static <T> _.If<T> and(final java.util.List<_.IFunc1<Boolean, T>> conds) {
            return new _.If<T>() {
                @Override
                public boolean eval(T t) {
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
                public boolean eval(T t) {
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
                public boolean eval(T t) {
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
                public boolean eval(T t) {
                    return X.eq(t, o);
                }
            };
        }
        
    }
    
    
}