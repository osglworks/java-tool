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

import com.greenlaw110.exception.FastRuntimeException;

import java.util.*;

import static com.greenlaw110.util._.f.*;

/**
 * Utilities to simulate functional programming in Java
 */
// Most of the code come from Play!Framework F.java, under Apache License 2.0
public class F {

    // Define Function Interfaces and their base implementation
    public static interface IFunc0<R> {
        R run();
    }
    
    public static abstract class F0<R> implements IFunc0<R> {}

    public static interface IFunc1<R, P1> {
        R run(P1 p1);
        IFunc0<R> curry(final P1 p1);
    }

    public static abstract class F1<R, P1> implements IFunc1<R, P1> {
        @Override
        public final F0<R> curry(final P1 p1) {
            final IFunc1<R, P1> me = this;
            return new F0<R>() {
                @Override
                public R run() {
                    return me.run(p1);
                }
            };
        }
    }
    
    public static interface IFunc2<R, P1, P2> {
        R run(P1 p1, P2 p2);
        F0<R> curry(final P1 p1, final P2 p2);
        F1<R, P1> curry(final P2 p2);
    }

    public static abstract class F2<R, P1, P2> implements IFunc2<R, P1, P2> {
        @Override
        public final F0<R> curry(final P1 p1, final P2 p2) {
            return curry(p2).curry(p1);
        }

        @Override
        public final F1<R, P1> curry(final P2 p2) {
            final F2<R, P1, P2> me = this;
            return new F1<R, P1>(){
                @Override
                public R run(P1 p1) {
                    return me.run(p1, p2);
                }
            };
        }
    }
    
    
    public static interface IFunc3<R, P1, P2, P3> {
        R run(P1 p1, P2 p2, P3 p3);
        F0<R> curry(final P1 p1, final P2 p2, final P3 p3);
        F1<R, P1> curry(final P2 p2, final P3 p3);
        F2<R, P1, P2> curry(final P3 p3);
    }

    public static abstract class F3<R, P1, P2, P3> implements IFunc3<R, P1, P2, P3> {
        @Override
        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3) {
            return curry(p2, p3).curry(p1);
        }

        @Override
        public final F1<R, P1> curry(final P2 p2, final P3 p3) {
            return curry(p3).curry(p2);
        }

        @Override
        public final F2<R, P1, P2> curry(final P3 p3) {
            final F3<R, P1, P2, P3> me = this;
            return new F2<R, P1, P2>(){
                @Override
                public R run(P1 p1, P2 p2) {
                    return me.run(p1, p2, p3);
                }
            };
        }
    }
    
    public static interface IFunc4<R, P1, P2, P3, P4> {
        R run(P1 p1, P2 p2, P3 p3, P4 p4);
        F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4);
        F1<R, P1> curry(final P2 p2, final P3 p3, final P4 p4);
        F2<R, P1, P2> curry(final P3 p3, final P4 p4);
        F3<R, P1, P2, P3> curry(final P4 p4);
    }

    public static abstract class F4<R, P1, P2, P3, P4> implements IFunc4<R, P1, P2, P3, P4> {
        @Override
        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4) {
            return curry(p2, p3, p4).curry(p1);
        }

        @Override
        public final F1<R, P1> curry(final P2 p2, final P3 p3, final P4 p4) {
            return curry(p3, p4).curry(p2);
        }

        @Override
        public final F2<R, P1, P2> curry(final P3 p3, final P4 p4) {
            return curry(p4).curry(p3);
        }

        @Override
        public final F3<R, P1, P2, P3> curry(final P4 p4) {
            final F4<R, P1, P2, P3, P4> me = this;
            return new F3<R, P1, P2, P3>(){
                @Override
                public R run(P1 p1, P2 p2, P3 p3) {
                    return me.run(p1, p2, p3, p4);
                }
            };
        }
    }
    
    public static interface IFunc5<R, P1, P2, P3, P4, P5> {
        R run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
        F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4, final P5 p5);
        F1<R, P1> curry(final P2 p2, final P3 p3, final P4 p4, final P5 p5);
        F2<R, P1, P2> curry(final P3 p3, final P4 p4, final P5 p5);
        F3<R, P1, P2, P3> curry(final P4 p4, final P5 p5);
        F4<R, P1, P2, P3, P4> curry(final P5 p5);
    }

    public static abstract class F5<R, P1, P2, P3, P4, P5> implements IFunc5<R, P1, P2, P3, P4, P5> {
        @Override
        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
            return curry(p2, p3, p4, p5).curry(p1);
        }

        @Override
        public final F1<R, P1> curry(final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
            return curry(p3, p4, p5).curry(p2);
        }

        @Override
        public final F2<R, P1, P2> curry(final P3 p3, final P4 p4, final P5 p5) {
            return curry(p4, p5).curry(p3);
        }

        @Override
        public final F3<R, P1, P2, P3> curry(P4 p4, P5 p5) {
            return curry(p5).curry(p4);
        }

        @Override
        public final F4<R, P1, P2, P3, P4> curry(final P5 p5) {
            final F5<R, P1, P2, P3, P4, P5> me = this;
            return new F4<R, P1, P2, P3, P4>(){
                @Override
                public R run(P1 p1, P2 p2, P3 p3, P4 p4) {
                    return me.run(p1, p2, p3, p4, p5);
                }
            };
        }
    }
    
    // Define common used Function classes including If and Visitor
    public static abstract class If<T> extends F.F1<Boolean, T> {
        @Override
        public final Boolean run(T t) {
            return eval(t);
        }
        
        public If<T> and(F.IFunc1<Boolean, T>... conds) {
            return _.f.and(C.prepend(C.list(conds), this));
        }

        public If<T> or(F.IFunc1<Boolean, T>... conds) {
            return _.f.or(C.prepend(C.list(conds), this));
        }
        
        public abstract boolean eval(T t);

        public static <T> F.If<T> yes() {
            return TRUE;
        } 
        
        public static F.If TRUE = new F.If(){
            @Override
            public boolean eval(Object o) {
                return true;
            }
        };
        
        public static <T> F.If<T> no() {
            return FALSE;
        }
        
        public static F.If FALSE = not(TRUE);

        public static <T> F.If<T> isNull() {
            return IS_NULL;
        }
        
        public static F.If IS_NULL = new F.If(){
            @Override
            public boolean eval(Object o) {
                return null == o;
            }
        };

        public static <T> F.If<T> notNull() {
            return NOT_NULL;
        }

        public static F.If NOT_NULL = not(IS_NULL);
    }
    
    public abstract static class Visitor<T> extends F.F1<Void, T> {

        protected Map<String, ?> _attr = C.newMap();
        protected T _t;
        protected String _s;
        protected boolean _b;
        protected int _i;
        protected float _f;
        protected double _d;
        protected byte _by;
        protected char _c;
        protected long _l;
        
        public Visitor() {
        }
        
        public Visitor(Map<String, ?> map) {
            _attr = C.newMap(map);
        }
        
        public Visitor(T t) {
            _t = t;
        }

        public Visitor(String s) {
            _s = s;
        }

        public Visitor(boolean b) {
            _b = b;
        }

        public Visitor(int i) {
            _i = i;
        }

        public Visitor(long l) {
            _l = l;
        }

        public Visitor(double d) {
            _d = d;
        }

        public Visitor(float f) {
            _f = f;
        }
        
        public Visitor(byte b) {
            _by = b;
        }
        
        public Visitor(char c) {
            _c = c;
        }
        
        public T get() {
            return _t;
        }
        
        public String getStr() {
            return _s;
        }
        
        public boolean getBoolea() {
            return _b;
        }
        
        public int getInt() {
            return _i;
        }
        
        public long getLong() {
            return _l;
        }
        
        public float getFloat() {
            return _f;
        }
        
        public double getDouble() {
            return _d;
        }
        
        public char getChar() {
            return _c;
        }
        
        public byte getByte() {
            return _by;
        }

        public <E> E get(String key) {
            return (E) _attr.get(key);
        }
    
        public static class Break extends FastRuntimeException {
            private Object payload;
            public Break() {}
            public Break(Object payload) {
                this.payload = payload;
            }
            public <T> T get() {
                return (T)payload;
            }
        }

        public static final Break BREAK = new Break();
        
        @Override
        public final Void run(T t) {
            visit(t);
            return null;
        }
        
        public abstract void visit(T t) throws Break;
    }
    
    public static <T> Visitor<T> guardedVisitor(final F.IFunc1<Boolean, T> guard, final Visitor<T> visitor) {
        return new Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                if (guard.run(t)) {
                    visitor.run(t);
                }
            }
        };
    }
    
    public static abstract class IndexedVisitor<T>  extends F.F2<Void, Integer, T> {

        protected Map<String, ?> _attr = C.newMap();
        protected T _t;
        protected String _s;
        protected boolean _b;
        protected int _i;
        protected float _f;
        protected double _d;
        protected byte _by;
        protected char _c;
        protected long _l;
        
        public IndexedVisitor() {
        }
        
        public IndexedVisitor(Map<String, ?> map) {
            _attr = C.newMap(map);
        }
        
        public IndexedVisitor(T t) {
            _t = t;
        }

        public IndexedVisitor(String s) {
            _s = s;
        }

        public IndexedVisitor(boolean b) {
            _b = b;
        }

        public IndexedVisitor(int i) {
            _i = i;
        }

        public IndexedVisitor(long l) {
            _l = l;
        }

        public IndexedVisitor(double d) {
            _d = d;
        }

        public IndexedVisitor(float f) {
            _f = f;
        }
        
        public IndexedVisitor(byte b) {
            _by = b;
        }
        
        public IndexedVisitor(char c) {
            _c = c;
        }
        
        public T get() {
            return _t;
        }
        
        public String getStr() {
            return _s;
        }
        
        public boolean getBoolea() {
            return _b;
        }
        
        public int getInt() {
            return _i;
        }
        
        public long getLong() {
            return _l;
        }
        
        public float getFloat() {
            return _f;
        }
        
        public double getDouble() {
            return _d;
        }
        
        public char getChar() {
            return _c;
        }
        
        public byte getByte() {
            return _by;
        }

        public <E> E get(String key) {
            return (E) _attr.get(key);
        }

        @Override
        public final Void run(Integer id, T t) {
            visit(id, t);
            return null;
        }
        
        public abstract void visit(Integer id, T t);
    }
    
    public static <T> IndexedVisitor<T> indexGuardedVisitor(final F.IFunc1<Boolean, Integer> guard, final Visitor<T> visitor) {
        return new IndexedVisitor<T>() {
            @Override
            public void visit(Integer id, T t) throws Visitor.Break {
                if (guard.run(id)) {
                    visitor.run(t);
                }
            }
        };
    }

    public static class Aggregator<T extends Number> extends F.Visitor<T> {
        public Aggregator(T initVal) {
            super(initVal);
        }
        @Override
        public void visit(T t) throws F.Visitor.Break {
            if (t instanceof Integer) {
                _t = (T)(Integer)(_t.intValue() + t.intValue()); 
            } else if (t instanceof Long) {
                _t = (T)(Long)(_t.longValue() + t.longValue()); 
            } else if (t instanceof Double) {
                _t = (T)(Double)(_t.doubleValue() + t.doubleValue()); 
            } else if (t instanceof Float) {
                _t = (T)(Float)(_t.floatValue() + t.floatValue()); 
            } else {
                _t = (T)(Integer)(_t.intValue() + t.intValue()); 
            }
        }
    }
    
    public static abstract class Transformer<FROM, TO> extends F.F1<TO, FROM> {
        @Override
        public TO run(FROM from) {
            return transform(from);
        }
        public abstract TO transform(FROM from);
    }
    
    public static abstract class Op1<T> extends F.F1<T, T> {
        @Override
        public T run(T t) {
            operate(t);
            return t;
        }
        public abstract void operate(T t);
    }
    
    public static abstract class Op2<T, P1> extends F.F2<T, T, P1> {
        @Override
        public T run(T t, P1 p1) {
            operate(t, p1);
            return t;
        }
        public abstract void operate(T t, P1 p1);
    }
    
    public static abstract class Op3<T, P1, P2> extends F.F3<T, T, P1, P2> {
        @Override
        public T run(T t, P1 p1, P2 p2) {
            operate(t, p1, p2);
            return t;
        }
        public abstract void operate(T t, P1 p1, P2 p2);
    }
    
    public static abstract class Op4<T, P1, P2, P3> extends F.F4<T, T, P1, P2, P3> {
        @Override
        public T run(T t, P1 p1, P2 p2, P3 p3) {
            operate(t, p1, p2, p3);
            return t;
        }
        public abstract void operate(T t, P1 p1, P2 p2, P3 p3);
    }
    
    // --- Tuple
    public static class Tuple<A, B> {

        final public A _1;
        final public B _2;

        public Tuple(A _1, B _2) {
            this._1 = _1;
            this._2 = _2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Tuple) {
                Tuple that = (Tuple) o;
                return _.eq(that._1, _1) && _.eq(that._2, _2);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return _.hc(_1, _2);
        }

        @Override
        public String toString() {
            return "T2(_1: " + _1 + ", _2: " + _2 + ")";
        }
    }

    public static <A, B> Tuple<A, B> Tuple(A a, B b) {
        return new Tuple(a, b);
    }

    public static class T2<A, B> extends Tuple<A, B> {

        public T2(A _1, B _2) {
            super(_1, _2);
        }
        
        public Map<A, B> asMap() {
            Map<A, B> m = new HashMap<A, B>();
            m.put(_1, _2);
            return m;
        }
    }

    public static <A, B> T2<A, B> T2(A a, B b) {
        return new T2(a, b);
    }

    public static class T3<A, B, C> {

        final public A _1;
        final public B _2;
        final public C _3;

        public T3(A _1, B _2, C _3) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
        }

        public T3 set1(A a) {
            return T3(a, _2, _3);
        }

        public T3 set2(B b) {
            return T3(_1, b, _3);
        }

        public T3 set3(C c) {
            return T3(_1, _2, c);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof T3) {
                T3 that = (T3) o;
                return _.eq(that._1, _1) && _.eq(that._2, _2) && _.eq(that._3, _3);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return _.hc(_1, _2, _3);
        }

        @Override
        public String toString() {
            return "T3(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ")";
        }
    }

    public static <A, B, C> T3<A, B, C> T3(A a, B b, C c) {
        return new T3(a, b, c);
    }

    public static class T4<A, B, C, D> {

        final public A _1;
        final public B _2;
        final public C _3;
        final public D _4;

        public T4(A _1, B _2, C _3, D _4) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof T4) {
                T4 that = (T4) o;
                return _.eq(that._1, _1) && _.eq(that._2, _2) && _.eq(that._3, _3) && _.eq(that._4, _4);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return _.hc(_1, _2, _3, _4);
        }

        @Override
        public String toString() {
            return "T4(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ")";
        }
    }

    public static <A, B, C, D> T4<A, B, C, D> T4(A a, B b, C c, D d) {
        return new T4<A, B, C, D>(a, b, c, d);
    }

    public static class T5<A, B, C, D, E> {

        final public A _1;
        final public B _2;
        final public C _3;
        final public D _4;
        final public E _5;

        public T5(A _1, B _2, C _3, D _4, E _5) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
            this._5 = _5;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof T5) {
                T5 that = (T5) o;
                return _.eq(that._1, _1) && _.eq(that._2, _2) && _.eq(that._3, _3) && _.eq(that._4, _4) && _.eq(that._5, _5);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return _.hc(_1, _2, _3, _4, _5);
        }

        @Override
        public String toString() {
            return "T5(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ", _5:" + _5 + ")";
        }
    }

    public static <A, B, C, D, E> T5<A, B, C, D, E> T5(A a, B b, C c, D d, E e) {
        return new T5<A, B, C, D, E>(a, b, c, d, e);
    }
    
    // --- 
    public static class Meta<T> {
        private T o;

        public Meta(T obj) {
            _.NPE(obj);
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
    
    // --- An easy to use String wrapper
    
    public static class Str {
        private String s;
        public Str(String s) {
            _.NPE(s);
            this.s = s;
        }
        
        public String get() {
            return s;
        }
        
        @Override
        public String toString() {
            return s;
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }
            if (obj instanceof Str) {
                return ((Str) obj).s.equals(s);
            }
            return false;
        }

        public static Str valueOf(Object o) {
            return new Str(S.string(o));
        }

        public static Str valueOf(int i) {
            return new Str(String.valueOf(i));
        }

        public static Str valueOf(boolean b) {
            return new Str(String.valueOf(b));
        }

        public static Str valueOf(char[] ca) {
            return new Str(String.valueOf(ca));
        }

        public static Str valueOf(long l) {
            return new Str(String.valueOf(l));
        }

        public static Str valueOf(char c) {
            return new Str(String.valueOf(c));
        }

        public static Str valueOf(double d) {
            return new Str(String.valueOf(d));
        }

        public static Str valueOf(float f) {
            return new Str(String.valueOf(f));
        }
        // -- helpers
        public Str after(String s) {
            return valueOf(S.after(this.s, s));
        }

        public Str afterFirst(String s) {
            return valueOf(S.afterFirst(this.s, s));
        }

        public Str afterLast(String s) {
            return valueOf(S.afterLast(this.s, s));
        }

        public Str before(String s) {
            return valueOf(S.before(this.s, s));
        }

        public Str beforeFirst(String s) {
            return valueOf(S.beforeFirst(this.s, s));
        }

        public Str beforeLast(String s) {
            return valueOf(S.beforeLast(this.s, s));
        }
        
        public Str trim() {
            return valueOf(s.trim());
        }
        
        public Str upperCase() {
            return valueOf(s.toUpperCase());
        }
        
        public Str lowerCase() {
            return valueOf(s.toLowerCase());
        }
        
        public Str replace(CharSequence target, CharSequence replacement) {
            return valueOf(s.replace(target, replacement));
        }
        
        public Str replaceAll(String regex, String replacement) {
            return valueOf(s.replaceAll(regex, replacement));
        }

        public Str capFirst() {
            return valueOf(S.capFirst(s));
        }

        public Str strip(String prefix, String suffix) {
            return valueOf(S.strip(s, prefix, suffix));
        }

        public Str urlEncode() {
            return valueOf(S.urlEncode(s));
        }
        
        public Str decodeBASE64() {
            return valueOf(S.decodeBASE64(s));
        }
        
        public Str encodeBASE64() {
            return valueOf(S.encodeBASE64(s));
        }
        
        public Str cutOff(int n) {
            return valueOf(S.cutOff(s, n));
        }
        
        public Str first(int n) {
            return valueOf(S.first(s, n));
        }

        public Str last(int n) {
            return valueOf(S.last(s, n));
        }

        public boolean startsWith(String prefix) {
            return s.startsWith(prefix);
        }

        public boolean endsWith(String suffix) {
            return s.endsWith(suffix);
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
        
        // --- added methods
        
        public java.util.List<T> get() {
            return _l;
        }
        
        public boolean ro() {
            return readonly;
        }
        
        public boolean readonly() {
            return readonly;
        }
        
        public boolean rw() {
            return !readonly;
        }
        
        public boolean readwrite() {
            return !readonly;
        }
        
        public List<T> readonly(boolean readonly) {
            if (readonly() == readonly) {
                return this;
            } else {
                return C.list(this, readonly);
            }
        }
        
        public ListComprehension<T> lc() {
            return C.lc(this);
        }
        
        public List<T> each(IFunc1<?, T> visitor) {
            lc().each(visitor);
            return this;
        }
        
        public List<T> println() {
            return each(IO.f.PRINTLN);
        }
        
        public String toStr() {
            return S.join(",", this);
        }
        
        public List<T> prepend(T... ts) {
            return C.prepend(this, ts).readonly(readonly());
        }
        
        public List<T> append(T... ts) {
            return C.append(this, ts).readonly(readonly());
        }
        
        public Set<T> uniq() {
            return C.uniq(this);
        }
        
        public List<T> reverse() {
            return C.lc(C.reverse(this)).asList(readonly());
        }

        /**
         * Return a list of all elements of this list without null
         * 
         * @return
         */
        public List<T> compact() {
            return lc().filter(If.NOT_NULL).asList(readonly());
        }
        
        public T first(final F.IFunc1<Boolean, T> cond) {
            return lc().first(cond);
        }
        
        public T last(final F.IFunc1<Boolean, T> cond) {
            return reverse().first(cond);
        }
        
        public List<T> without(Collection<T> c) {
            List<T> l0 = C.newList(get());
            l0.removeAll(c);
            return l0.readonly(readonly());
        }
        
        public List<T> without(T ... ts) {
            return without(C.list(ts));
        }
        
        public List<T> intersect(Collection<T> c) {
            List<T> l0 = C.newList(get());
            l0.retainAll(c);
            return l0.readonly(readonly());
        }

        public List<T> intersect(T... ts) {
            return intersect(C.list(ts));
        }
        
        public List<T> filter(final F.IFunc1<Boolean, T>... filters) {
            return lc().filter(filters).asList(readonly());
        }
        
        public <E> E reduce(final E initVal, final F.IFunc2<E, E, T> func2) {
            return lc().reduce(initVal, func2);
        }
    }
    
}
