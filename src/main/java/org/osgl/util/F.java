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

import org.osgl.exception.FastRuntimeException;

import java.util.*;

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
        
        public static F.If FALSE = _.f.not(TRUE);

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

        public static F.If NOT_NULL = _.f.not(IS_NULL);
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
    
}
