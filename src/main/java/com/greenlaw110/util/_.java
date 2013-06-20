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

import com.greenlaw110.exception.InvalidStateException;
import com.greenlaw110.exception.UnexpectedException;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

/**
 * The most common utilities including some utilities coming from {@link E}, {@link S}
 */
public class _ {
    /**
     * Defines an instance to be used in views
     */
    public final _ INSTANCE = new _();
    public final _ instance = INSTANCE;

    public final static String fmt(String tmpl, Object... args) {
        return S.fmt(tmpl, args);
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

    public final static F.Str str(Object o) {
        return F.Str.valueOf(o);
    }

    public final static F.Meta meta(Object o) {
        return new F.Meta(o);
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
        return C.list(el);
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
    
    public final static <T> T times(F.IFunc0<T> func, int n) {
        if (n < 0) {
            throw E.invalidArg("the number of times must not be negative");
        }
        if (n == 0) {
            return null;
        }
        T result = func.run();
        for (int i = 1; i < n; ++i) {
            func.run();
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
    
    public final static <T> T times(F.IFunc1<T, T> func, T initVal, int n) {
        if (n < 0) {
            throw E.invalidArg("the number of times must not be negative");
        }
        if (n == 0) {
            return initVal;
        }
        T retVal = initVal;
        for (int i = 1; i < n; ++i) {
            retVal = func.run(retVal);
        }
        return retVal;
    }
    
    // -- functors
    public static class f {

        public static <T> F.If<T> and(final F.IFunc1<Boolean, T>... conds) {
            return and(C.list(conds));
        }
    
        public static <T> F.If<T> or(final java.util.List<F.IFunc1<Boolean, T>> conds) {
            return not(and(conds));
        }
        
        public static <T> F.If<T> or(final F.IFunc1<Boolean, T>... conds) {
            return not(and(conds));
        }
    
        public static <T> F.If<T> not(final F.IFunc1<Boolean, T> cond) {
            return new F.If<T>() {
                @Override
                public boolean eval(T t) {
                    return !cond.run(t);
                }
            };
        }
        
        public static <T> F.If<T> and(final java.util.List<F.IFunc1<Boolean, T>> conds) {
            return new F.If<T>() {
                @Override
                public boolean eval(T t) {
                    for (F.IFunc1<Boolean, T> cond : conds) {
                        if (!cond.run(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }
        
        public static <T> F.IFunc0<T> next(final Iterator<T> itr) {
            final T t = itr.next();
            return new F.IFunc0<T>() {
                @Override
                public T run() {
                    return t;
                }
            };
        }
    
        public static F.IFunc0<Integer> next(final int i) {
            return new F.IFunc0<Integer>() {
                @Override
                public Integer run() {
                    return i + 1;
                }
            };
        }
    
        public static F.IFunc0<Integer> prev(final int i) {
            return new F.IFunc0<Integer>() {
                @Override
                public Integer run() {
                    return i - 1;
                }
            };
        }
    
        public static F.IFunc0<Character> next(final char c) {
            return new F.IFunc0<Character>() {
                @Override
                public Character run() {
                    return (char)(c + 1);
                }
            };
        }
    
        public static F.IFunc0<Character> prev(final char c) {
            return new F.IFunc0<Character>() {
                @Override
                public Character run() {
                    return (char)(c - 1);
                }
            };
        }
    
        public static <T> F.IFunc1<List<T>, T> repeat(final Class<T> clz, final int times) {
            return new F.F2<List<T>, T, Integer>() {
                @Override
                public List<T> run(T t, Integer times) {
                    E.invalidArgIf(times < 0, "times[%s] is less than zero");
                    if (times == 0) {
                        return C.emptyList();
                    } else {
                        List<T> l = C.newList();
                        for (int i = 0; i < times; ++i) {
                            l.add(t);
                        }
                        return l;
                    }
                }
            }.curry(times);
        }
        
        public static <T> F.Transformer<T, String> toStr() {
            return new F.Transformer<T, String>() {
                @Override
                public String transform(T t) {
                    return null == t ? "" : t.toString();
                }
            };
        }
        
        public static <T extends Comparable<T>> F.If<T> lt(final T o) {
            return lessThan(o);
        }
        
        public static <T extends Comparable<T>> F.If<T> lessThan(final T o) {
            return new F.If<T>() {
                @Override
                public boolean eval(T t) {
                    return t.compareTo(o) < 0;
                }
            };
        }
        
        public static <T extends Comparable<T>> F.If<T> gt(final T o) {
            return greatThan(o);
        }
    
        public static <T extends Comparable<T>> F.If<T> greatThan(final T o) {
            return new F.If<T>() {
                @Override
                public boolean eval(T t) {
                    return t.compareTo(o) > 0;
                }
            };
        }
        
        public static <T> F.If<T> eq(final T o) {
            return equal(o);
        }
        
        public static <T> F.If<T> equal(final T o) {
            return new F.If<T>() {
                @Override
                public boolean eval(T t) {
                    return _.eq(t, o);
                }
            };
        }
        
        public static <T extends Number> F.IFunc2<T, T, T> sum(Class<T> clz) {
            return sum();
        }
        
        public static <T extends Number> F.IFunc2<T, T, T> sum() {
            return new com.greenlaw110.util.F.F2<T, T, T>() {
                @Override
                public T run(T t, T t2) {
                    if (t instanceof Integer) {
                        return (T)(Integer)(((Integer)t).intValue() + ((Integer)t2).intValue());
                    } else if (t instanceof Long) {
                        return (T)(Long)(((Long)t).longValue() + ((Long)t2).longValue());
                    } else if (t instanceof Double) {
                        return (T)(Double)(((Double)t).doubleValue() + ((Double)t2).doubleValue());
                    } else if (t instanceof Float) {
                        return (T)(Float)(((Float)t).floatValue() + ((Float)t2).floatValue());
                    } else if (t instanceof Short) {
                        return (T)(Integer)(((Short)t).shortValue() + ((Short)t2).shortValue());
                    } else if (t instanceof Byte) {
                        return (T)(Integer)(((Byte)t).byteValue() + ((Byte)t2).byteValue());
                    }
                    return (T)(Integer)(((Integer)t).intValue() + ((Integer)t2).intValue());
                }
            };
        } 
        
        public static <T> F.IFunc1<T, T> dbl(Class<T> clz) {
            return multiply(clz, 2);
        };
        
        public static <T> F.IFunc1<T, T> dbl() {
            return multiply(2);
        }
        
        public static <T> F.IFunc1<T, T> multiply(final int fact) {
            return new F.F1<T, T>() {
                @Override
                public T run(T t) {
                    if (t instanceof Number) {
                        if (fact == 0) {
                            return (T)(Integer)0;
                        }
                        Number n = (Number)t;
                        if (n instanceof Integer) {
                            return (T)(Integer)(n.intValue() * fact);
                        } else if (n instanceof Long) {
                            return (T)(Long)(n.longValue() * fact);
                        } else if (n instanceof Float) {
                            return (T)(Float)(n.floatValue() * fact);
                        } else if (n instanceof Double) {
                            return (T)(Double)(n.doubleValue() * fact);
                        } else if (n instanceof Short) {
                            return (T)(Integer)(n.shortValue() * fact);
                        } else if (n instanceof Byte) {
                            return (T)(Integer)(n.byteValue() * fact);
                        } else {
                            return (T)(Integer)(n.intValue() * fact);
                        }
                    } else if (t instanceof String) {
                        if (fact == 0) {
                            return (T)"";
                        } 
                        String s = (String)t;
                        char[] chars = s.toCharArray();
                        int times = fact;
                        if (fact < 0) {
                            chars = C.reverse(chars);
                            times *= -1;
                        }
                        s = new String(chars);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < times; ++i) {
                            sb.append(s);
                        }
                        return (T)sb.toString();
                    }
                    throw E.invalidArg("multiply doesn't support the type [%s]", t.getClass().getName());
                }
            };
        }
        
        public static <T> F.IFunc1<T, T> multiply(final Class<T> clz, final int fact) {
            return multiply(fact);
        }
        
        public static <T extends Number> F.Aggregator<T> aggregate() {
            return aggregate((T)(Integer)0);
        }
        
        public static F.Aggregator AGGREGATE = aggregate(0);
        
        public static <T extends Number> F.Aggregator<T> aggregate(T initVal) {
            return new F.Aggregator<T>(initVal);
        }
        
    }
    
    
}