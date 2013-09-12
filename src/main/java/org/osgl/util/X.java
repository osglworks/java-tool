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

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
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
=======
import org.osgl.exception.FastRuntimeException;

import java.util.*;

/**
 * <code>_</code> is the umbrella namespace aggregates core utilities of OSGL toolkit:
 * 
 * <ul>
 * <li>Function interfaces and base implementations</li>
 * <li>currying utilities </li>
 * <li>Tuple and multi-elements tupbles</li>
 * <li>Option</li>
 * <li>core utilities like ts()</li>
 * <li>predefined functions aggregated in the <code>_.F</code> namespace</li>
 * </ul>
 * 
 * @author Gelin Luo
 * @version 0.2
 */
// Some functional relevant code come from Play!Framework F.java, under Apache License 2.0
public final class _ {

    private _() {}
    
    public static final _ INSTANCE = new _();
    public static final _ instance = INSTANCE;

    // --- Functors and their default implementations

    /**
     * Define a function that take no parameter 
     * 
     * @param <R> the generic type of the return value, could be <code>Void</code>
     * @see IFunc1
     * @see IFunc2
     * @see IFunc3
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc0<R> {
        R apply();
    }
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java

    /**
     * Default implementation for {@link IFunc0}. Implementation of {@link IFunc0} should
     * (nearly) always extend to this class instead of implement the interface directly 
     * 
     * @since 0.2
     */
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
    public final X INSTANCE = new X();
    public final X instance = INSTANCE;
=======
    public static abstract class F0<R> implements IFunc0<R> {
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java

        /**
         * Return a {@link Break} with payload. Here is an example of how to use this method:
         * 
         * <pre>
         *     myData.accept(new Visitor(){
         *         public void apply(T e) {
         *             if (...) {
         *                 throw breakOut(e);
         *             }
         *         }
         *     })
         * </pre>
         * 
         * @param payload the object passed through the <code>Break</code>
         * @return a {@link Break} instance
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }
    }

    /**
     * A dumb function for {@link IFunc0} that does nothing and return <code>null</code>
     * 
     * @since 0.2
     * @see #f0() 
     */
    public static final F0 F0 = new F0() {
        @Override
        public Object apply() {
            return null;
        }
    };

    /**
     * Return a dumb function for {@link IFunc0}. This is the type-safe version of {@link #F0}
     * 
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <T> F0<T> f0() {
        return F0;
    }

    /**
     * Convert a general {@link IFunc0} typed function to {@link F0} type
     * 
     * @since 0.2
     */
    public static <R> F0<R> f0(final IFunc0<R> f0) {
        if (f0 instanceof F0) {
            return (F0<R>)f0;
        }
        return new F0<R>() {
            @Override
            public R apply() {
                return f0.apply();
            }
        };
    }

    /**
     * Define a function structure that accept one parameter 
     * 
     * @see IFunc0
     * @see IFunc2
     * @see IFunc3
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc1<R, P1> {
        R apply(P1 p1);
    }

    /**
     * Base implementation of {@link IFunc1} function. User application should
     * (nearly) always make their implementation extend to this base class 
     * instead of implement {@link IFunc1} directly
     * 
     * @since 0.2
     */
    public static abstract class F1<R, P1> implements IFunc1<R, P1> {
        public final F0<R> curry(final P1 p1) {
            final IFunc1<R, P1> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(p1);
                }
            };
        }

        /**
         * @see F0#breakOut(Object) 
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }
    }
    
    /**
     * A dumb {@link IFunc1} implementation that does nothing and return null
     * 
     * @see #f1() 
     * @since 0.2
     */
    public static final F1 F1 = new F1(){
        @Override
        public Object apply(Object o) {
            return null;
        }
    };

    /**
     * The type-safe version of {@link #F1}
     * 
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <R, P1> F1<R, P1> f1() {
        return F1;
    }


    /**
     * Convert a general {@link IFunc1} function into a {@link F1} typed
     * function
     * 
     * @since 0.2
     */
    public static <R, P1> F1 f1(final IFunc1<R, P1> f1) {
        if (f1 instanceof F1) {
            return (F1<R, P1>)f1;
        }
        return new F1<R, P1>() {
            @Override
            public R apply(P1 p1) {
                return f1.apply(p1);
            }
        };
    }

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
    public final static S.Str str(Object o) {
        return S.str(o);
=======
    /**
     * Define a function structure that accept two parameter 
     * 
     * @see IFunc0
     * @see IFunc1
     * @see IFunc3
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc2<R, P1, P2> {
        R apply(P1 p1, P2 p2);
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
    }

    /**
     * Base implementation of {@link IFunc2} function. User application should
     * (nearly) always make their implementation extend to this base class 
     * instead of implement {@link IFunc2} directly
     * 
     * @since 0.2
     */
    public static abstract class F2<R, P1, P2> implements IFunc2<R, P1, P2> {
        public Break breakOut(Object payload) {
            return new Break(payload);
        }
        public final F0<R> curry(final P1 p1, final P2 p2) {
            return _.curry(this, p1, p2);
        }
        public final F1<R, P1> curry(final P2 p2) {
            return _.curry(this, p2);
        }
    }
    
    /**
     * A dumb {@link IFunc2} implementation that does nothing and return null
     * 
     * @see #f2() 
     * @since 0.2
     */
    public static final F2 F2 = new F2(){
        @Override
        public Object apply(Object p1, Object p2) {
            return null;
        }
    };

    /**
     * The type-safe version of {@link #F2}
     * 
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <R, P1, P2> F2<R, P1, P2> f2() {
        return F2;
    }


    /**
     * Convert a general {@link IFunc2} function into a {@link F2} typed
     * function
     * 
     * @since 0.2
     */
    public static <R, P1, P2> F2 f2(final IFunc2<R, P1, P2> f2) {
        if (f2 instanceof F2) {
            return (F2<R, P1, P2>)f2;
        }
        return new F2<R, P1, P2>() {
            @Override
            public R apply(P1 p1, P2 p2) {
                return f2.apply(p1, p2);
            }
        };
    }

    /**
     * Define a function structure that accept three parameter 
     * 
     * @see IFunc0
     * @see IFunc1
     * @see IFunc2
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc3<R, P1, P2, P3> {
        /**
         * Run the function with parameters specified. This is where
         * the main function logic to be implemented
         */
        R apply(P1 p1, P2 p2, P3 p3);
    }

    /**
     * Base implementation of {@link IFunc3} function. User application should
     * (nearly) always make their implementation extend to this base class 
     * instead of implement {@link IFunc3} directly
     * 
     * @since 0.2
     */
    public static abstract class F3<R, P1, P2, P3> implements IFunc3<R, P1, P2, P3> {
        /**
         * @see F1#breakOut(Object)  
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }
        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3) {
            return _.curry(this, p1, p2, p3);
        }
        public final F1<R, P1> curry(final P2 p2, final P3 p3) {
            return _.curry(this, p2, p3);
        }
        public final F2<R, P1, P2> curry(final P3 p3) {
            return _.curry(this, p3);
        }
    }
    
    /**
     * A dumb {@link IFunc3} implementation that does nothing and return null
     * 
     * @see #f3() 
     * @since 0.2
     */
    public static final F3 F3 = new F3(){
        @Override
        public Object apply(Object p1, Object p2, Object p3) {
            return null;
        }
    };

    /**
     * The type-safe version of {@link #F3}
     * 
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <R, P1, P2, P3> F3<R, P1, P2, P3> f3() {
        return F3;
    }


    /**
     * Convert a general {@link IFunc3} function into a {@link F3} typed
     * function
     * 
     * @since 0.2
     */
    public static <R, P1, P2, P3> F3 f3(final IFunc3<R, P1, P2, P3> f3) {
        if (f3 instanceof F3) {
            return (F3<R, P1, P2, P3>)f3;
        }
        return new F3<R, P1, P2, P3>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3) {
                return f3.apply(p1, p2, p3);
            }
        };
    }

    /**
     * Define a function structure that accept four parameter 
     * 
     * @see IFunc0
     * @see IFunc1
     * @see IFunc2
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc4<R, P1, P2, P3, P4> {
        /**
         * Run the function with parameters specified. This is where
         * the main function logic to be implemented
         */
        R apply(P1 p1, P2 p2, P3 p3, P4 p4);
    }

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java

    public final static <T> List<T> list(T... el) {
        return C0.list(el);
=======
    /**
     * Base implementation of {@link IFunc4} function. User application should
     * (nearly) always make their implementation extend to this base class 
     * instead of implement {@link IFunc4} directly
     * 
     * @since 0.2
     */
    public static abstract class F4<R, P1, P2, P3, P4> implements IFunc4<R, P1, P2, P3, P4> {
        /**
         * @see F1#breakOut(Object) 
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }
        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4) {
            return _.curry(this, p1, p2, p3, p4);
        }
        public final F1<R, P1> curry(final P2 p2, final P3 p3, final P4 p4) {
            return _.curry(this, p2, p3, p4);
        }
        public final F2<R, P1, P2> curry(final P3 p3, final P4 p4) {
            return _.curry(this, p3, p4);
        }
        public final F3<R, P1, P2, P3> curry(final P4 p4) {
            return _.curry(this, p4);
        }
    }
    
    /**
     * A dumb {@link IFunc4} implementation that does nothing and return null
     * 
     * @see #f4() 
     * @since 0.2
     */
    public static final F4 F4 = new F4(){
        @Override
        public Object apply(Object p1, Object p2, Object p3, Object p4) {
            return null;
        }
    };

    /**
     * The type-safe version of {@link #F4}
     * 
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <R, P1, P2, P3, P4> F4<R, P1, P2, P3, P4> f4() {
        return F4;
    }


    /**
     * Convert a general {@link IFunc4} function into a {@link F4} typed
     * function
     * 
     * @since 0.2
     */
    public static <R, P1, P2, P3, P4> F4 f4(final IFunc4<R, P1, P2, P3, P4> f4) {
        if (f4 instanceof F4) {
            return (F4<R, P1, P2, P3, P4>)f4;
        }
        return new F4<R, P1, P2, P3, P4>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                return f4.apply(p1, p2, p3, p4);
            }
        };
    }

    /**
     * Define a function structure that accept five parameter 
     * 
     * @see IFunc0
     * @see IFunc1
     * @see IFunc2
     * @see IFunc3
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc5<R, P1, P2, P3, P4, P5> {
        /**
         * Run the function with parameters specified. This is where
         * the main function logic to be implemented
         */
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
    }

    /**
     * Base implementation of {@link IFunc5} function. User application should
     * (nearly) always make their implementation extend to this base class 
     * instead of implement {@link IFunc5} directly
     * 
     * @since 0.2
     */
    public static abstract class F5<R, P1, P2, P3, P4, P5> implements IFunc5<R, P1, P2, P3, P4, P5> {
        /**
         * @see F1#breakOut(Object) 
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }
        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
            return _.curry(this, p1, p2, p3, p4, p5);
        }

        public final F1<R, P1> curry(final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
            return _.curry(this, p2, p3, p4, p5);
        }

        public final F2<R, P1, P2> curry(final P3 p3, final P4 p4, final P5 p5) {
            return _.curry(this, p3, p4, p5);
        }

        public final F3<R, P1, P2, P3> curry(P4 p4, P5 p5) {
            return _.curry(this, p4, p5);
        }

        public final F4<R, P1, P2, P3, P4> curry(final P5 p5) {
            return _.curry(this, p5);
        }
    }
    
    /**
     * A dumb {@link IFunc5} implementation that does nothing and return null
     * 
     * @see #f5() 
     * @since 0.2
     */
    public static final F5 F5 = new F5(){
        @Override
        public Object apply(Object p1, Object p2, Object p3, Object p4, Object p5) {
            return null;
        }
    };

    /**
     * The type-safe version of {@link #F5}
     * 
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <R, P1, P2, P3, P4, P5> F5<R, P1, P2, P3, P4, P5> f5() {
        return F5;
    }


    /**
     * Convert a general {@link IFunc5} function into a {@link F5} typed
     * function
     * 
     * @since 0.2
     */
    public static <R, P1, P2, P3, P4, P5> F5 f5(final IFunc5<R, P1, P2, P3, P4, P5> f5) {
        if (f5 instanceof F5) {
            return (F5<R, P1, P2, P3, P4, P5>)f5;
        }
        return new F5<R, P1, P2, P3, P4, P5>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }

    public static <R, P1> F0<R> curry(final IFunc1<R, P1> f1, final P1 p1) {
        return new F0<R>() {
            @Override
            public R apply() {
                return f1.apply(p1);
            }
        };
    }

    public static <R, P1, P2> F0<R> curry(final IFunc2<R, P1, P2> f2, final P1 p1, final P2 p2) {
        return new F0<R>() {
            @Override
            public R apply() {
                return f2.apply(p1, p2);
            }
        };
    }
    
    public static <R, P1, P2> F1<R, P1> curry(final IFunc2<R, P1, P2> f2, final P2 p2) {
        return new F1<R, P1>() {
            @Override
            public R apply(P1 p1) {
                return f2.apply(p1, p2);
            }
        };
    }
    
    public static <R, P1, P2, P3> F0<R> curry(final IFunc3<R, P1, P2, P3> f3, final P1 p1, final P2 p2, final P3 p3) {
        return new F0<R>() {
            @Override
            public R apply() {
                return f3.apply(p1, p2, p3);
            }
        };
    }
    
    public static <R, P1, P2, P3> F1<R, P1> curry(final IFunc3<R, P1, P2, P3> f3, final P2 p2, final P3 p3) {
        return new F1<R, P1>() {
            @Override
            public R apply(P1 p1) {
                return f3.apply(p1, p2, p3);
            }
        };
    }
    
    public static <R, P1, P2, P3> F2<R, P1, P2> curry(final IFunc3<R, P1, P2, P3> f3, final P3 p3) {
        return new F2<R, P1, P2>() {
            @Override
            public R apply(P1 p1, P2 p2) {
                return f3.apply(p1, p2, p3);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4> F0<R> curry(final IFunc4<R, P1, P2, P3, P4> f4, final P1 p1, final P2 p2, final P3 p3, final P4 p4) {
        return new F0<R>() {
            @Override
            public R apply() {
                return f4.apply(p1, p2, p3, p4);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4> F1<R, P1> curry(final IFunc4<R, P1, P2, P3, P4> f4, final P2 p2, final P3 p3, final P4 p4) {
        return new F1<R, P1>() {
            @Override
            public R apply(P1 p1) {
                return f4.apply(p1, p2, p3, p4);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4> F2<R, P1, P2> curry(final IFunc4<R, P1, P2, P3, P4> f4, final P3 p3, final P4 p4) {
        return new F2<R, P1, P2>() {
            @Override
            public R apply(P1 p1, P2 p2) {
                return f4.apply(p1, p2, p3, p4);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4> F3<R, P1, P2, P3> curry(final IFunc4<R, P1, P2, P3, P4> f4, final P4 p4) {
        return new F3<R, P1, P2, P3>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3) {
                return f4.apply(p1, p2, p3, p4);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4, P5> F0<R> curry(final IFunc5<R, P1, P2, P3, P4, P5> f5, final P1 p1, final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
        return new F0<R>() {
            @Override
            public R apply() {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4, P5> F1<R, P1> curry(final IFunc5<R, P1, P2, P3, P4, P5> f5, final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
        return new F1<R, P1>() {
            @Override
            public R apply(P1 p1) {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4, P5> F2<R, P1, P2> curry(final IFunc5<R, P1, P2, P3, P4, P5> f5, final P3 p3, final P4 p4, final P5 p5) {
        return new F2<R, P1, P2>() {
            @Override
            public R apply(P1 p1, P2 p2) {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4, P5> F3<R, P1, P2, P3> curry(final IFunc5<R, P1, P2, P3, P4, P5> f5, final P4 p4, final P5 p5) {
        return new F3<R, P1, P2, P3>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3) {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }
    
    public static <R, P1, P2, P3, P4, P5> F4<R, P1, P2, P3, P4> curry(final IFunc5<R, P1, P2, P3, P4, P5> f5, final P5 p5) {
        return new F4<R, P1, P2, P3, P4>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }
    
    // Define common used Function classes including If and Visitor

    /**
     * <code>If</code> is a predefined <code>IFunc1&lt;Boolean, T&gt;</code> typed
     * function with a set of utilities dealing with boolean operations.
     * 
     * <p>Note in user application, it should NOT assume an argument is of <code>If</code>
     * typed function, instead the argument should always be declared as <code>IFunc1&lt;Boolean T&gt;</code>:</p>
     * 
     * <pre>
     *     // bad way
     *     void foo(If&lt;MyData&gt; predicate) {
     *         ...
     *     }
     *     // good way
     *     void foo(IFunc1&lt;Boolean, MyData&gt; predicate) {
     *         If&lt;MyData&gt; p = _.toIf(predicate);
     *         ...
     *     }
     * </pre>
     * 
     * @since 0.2
     */
    public static abstract class If<T> extends _.F1<Boolean, T> {
        @Override
        public final Boolean apply(T t) {
            return eval(t);
        }

        /**
         * Sub class to implement this method to test on the supplied elements
         */
        public abstract boolean eval(T t);

        /**
         * Return an <code>If</code> predicate from a list of <code>IFunc1&lt;Boolean, T&gt;</code>
         * with AND operation. For any <code>T t</code> to be tested, if any specified predicates
         * must returns <code>false</code> on it, the resulting predicate will return <code>false</code>. 
         * 
         * @since 0.2
         */
        public If<T> and(_.IFunc1<Boolean, T>... predicates) {
            return X.f.and(C0.prepend(C0.list(predicates), this));
        }

        /**
         * Return an <code>If</code> predicate from a list of <code>IFunc1&lt;Boolean, T&gt;</code>
         * with OR operation. For any <code>T t</code> to be tested, if any specified predicates
         * must returns <code>true</code> on it, the resulting predicate will return <code>true</code>. 
         * 
         * @since 0.2
         */
        public If<T> or(_.IFunc1<Boolean, T>... conds) {
            return X.f.or(C0.prepend(C0.list(conds), this));
        }

    }

    /**
     * Convert a general <code>IFunc1&lt;Boolean, T&gt; typed function to {@link If If&lt;T&gt;} function</code>
     * 
     * @since 0.2
     */
    public static <T> If<T> toIf(final IFunc1<Boolean, T> f1) {
        if (f1 instanceof If) {
            return (If<T>)f1;
        }
        return new If<T>(){
            @Override
            public boolean eval(T t) {
                return f1.apply(t);
            }
        };
    }
    
    public abstract static class Visitor<T> extends _.F1<Void, T> {

        protected Map<String, ?> _attr = C0.newMap();
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
            _attr = C0.newMap(map);
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
        
        @Override
        public final Void apply(T t) {
            visit(t);
            return null;
        }
        
        public abstract void visit(T t) throws Break;
    }
    
    public static <T> Visitor<T> guardedVisitor(final _.IFunc1<Boolean, T> guard, final Visitor<T> visitor) {
        return new Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                if (guard.apply(t)) {
                    visitor.apply(t);
                }
            }
        };
    }

    public static <E> Break breakOut(E e){
        throw new Break(e);
    }

    public static Break BREAK = new Break();
    
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

    public static abstract class IndexedVisitor<K,T>  extends _.F2<Void, K, T> {

        protected Map<String, ?> _attr = C0.newMap();
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
            _attr = C0.newMap(map);
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

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
    public final static <T> T times(_.IFunc0<T> func, int n) {
        if (n < 0) {
            throw E.invalidArg("the number of times must not be negative");
=======
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
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }

        public <E> E get(String key) {
            return (E) _attr.get(key);
        }

        @Override
        public final Void apply(K id, T t) {
            visit(id, t);
            return null;
        }
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        T result = func.apply();
        for (int i = 1; i < n; ++i) {
            func.apply();
=======
        
        public abstract void visit(K id, T t);
    }
    
    public static <K, T> IndexedVisitor<K, T> indexGuardedVisitor(final _.IFunc1<Boolean, K> guard, final Visitor<T> visitor) {
        return new IndexedVisitor<K, T>() {
            @Override
            public void visit(K id, T t) throws Break {
                if (guard.apply(id)) {
                    visitor.apply(t);
                }
            }
        };
    }

    public static abstract class Transformer<FROM, TO> extends _.F1<TO, FROM> {
        @Override
        public TO apply(FROM from) {
            return transform(from);
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }
        public abstract TO transform(FROM from);
    }
    
    public static abstract class Op1<T> extends _.F1<T, T> {
        @Override
        public T apply(T t) {
            operate(t);
            return t;
        }
        public abstract void operate(T t);
    }
    
    public static abstract class Op2<T, P1> extends _.F2<T, T, P1> {
        @Override
        public T apply(T t, P1 p1) {
            operate(t, p1);
            return t;
        }
        public abstract void operate(T t, P1 p1);
    }
    
    public static abstract class Op3<T, P1, P2> extends _.F3<T, T, P1, P2> {
        @Override
        public T apply(T t, P1 p1, P2 p2) {
            operate(t, p1, p2);
            return t;
        }
        public abstract void operate(T t, P1 p1, P2 p2);
    }
    
    public static abstract class Op4<T, P1, P2, P3> extends _.F4<T, T, P1, P2, P3> {
        @Override
        public T apply(T t, P1 p1, P2 p2, P3 p3) {
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
                return X.eq(that._1, _1) && X.eq(that._2, _2);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return X.hc(_1, _2);
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
                return X.eq(that._1, _1) && X.eq(that._2, _2) && X.eq(that._3, _3);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return X.hc(_1, _2, _3);
        }

        @Override
        public String toString() {
            return "T3(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ")";
        }
    }
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
    
    public final static <T> T times(_.IFunc1<T, T> func, T initVal, int n) {
        if (n < 0) {
            throw E.invalidArg("the number of times must not be negative");
=======

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
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof T4) {
                T4 that = (T4) o;
                return X.eq(that._1, _1) && X.eq(that._2, _2) && X.eq(that._3, _3) && X.eq(that._4, _4);
            }
            return false;
        }
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        T retVal = initVal;
        for (int i = 1; i < n; ++i) {
            retVal = func.apply(retVal);
=======

        @Override
        public int hashCode() {
            return X.hc(_1, _2, _3, _4);
        }

        @Override
        public String toString() {
            return "T4(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ")";
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }
    }

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        public Meta(T obj) {
            X.NPE(obj);
            o = obj;
=======
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
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof T5) {
                T5 that = (T5) o;
                return X.eq(that._1, _1) && X.eq(that._2, _2) && X.eq(that._3, _3) && X.eq(that._4, _4) && X.eq(that._5, _5);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return X.hc(_1, _2, _3, _4, _5);
        }

        @Override
        public String toString() {
            return "T5(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ", _5:" + _5 + ")";
        }
    }

    public static <A, B, C, D, E> T5<A, B, C, D, E> T5(A a, B b, C c, D d, E e) {
        return new T5<A, B, C, D, E>(a, b, c, d, e);
    }
    
    public static abstract class Option<T> implements Iterable<T> {

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        public static <T> _.If<T> and(final _.IFunc1<Boolean, T>... conds) {
            return and(C0.list(conds));
        }
    
        public static <T> _.If<T> or(final java.util.List<_.IFunc1<Boolean, T>> conds) {
            return not(and(conds));
        }
        
        public static <T> _.If<T> or(final _.IFunc1<Boolean, T>... conds) {
            return not(and(conds));
=======
        public abstract boolean isDefined();
        
        public boolean notDefined() {
            return !isDefined();
        }

        public abstract T get();

        public static <T> None<T> None() {
            return (None<T>) (Object) None;
        }

        public static <T> Some<T> Some(T value) {
            return new Some<T>(value);
        }
    }

    public static class None<T> extends Option<T> {

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public T get() {
            throw new IllegalStateException("No value");
        }

        public Iterator<T> iterator() {
            return Collections.<T>emptyList().iterator();
        }

        @Override
        public String toString() {
            return "None";
        }
    }

    public static class Some<T> extends Option<T> {

        final T value;

        public Some(T value) {
            this.value = value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public T get() {
            return value;
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }

        public Iterator<T> iterator() {
            return Collections.singletonList(value).iterator();
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }
    
    public static None<Object> None = new None<Object>();

    public static <T> Some<T> Some(T a) {
        return new Some(a);
    }
    
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        public static <T> _.If<T> not(final _.IFunc1<Boolean, T> cond) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return !cond.apply(t);
=======
    public static <T> None<T> None() {
        return (None<T>) (Object) None; 
    }

    // --- common utilities

    /**
     * Alias of {@link S#fmt(String, Object...)}
     * 
     * @since 0.2
     */
    public static final String fmt(String tmpl, Object... args) {
        return S.fmt(tmpl, args);
    }

    /**
     * Return {@link System#currentTimeMillis() current time millis}
     * 
     * @since 0.2
     */
    public static final long ms() {
        return System.currentTimeMillis();
    }

    /**
     * Return {@link System#nanoTime() current nano time} 
     * 
     * @since 0.2
     */
    public static final long ns() {
        return System.nanoTime();
    }
    // --- eof common utilities

    /**
     * The namespace to aggregate predefined core functions
     */
    public static final class F {
        private F() {}
        
        /**
         * Return a one variable function that throw out a {@link Break} with payload specified when a predicate return
         * <code>true</code> on an element been tested
         * 
         * @since 0.2
         */
        public static <P, T> F1<Void, T> breakIf(final IFunc1<Boolean, T> predicate, final P payload) {
            return new F1<Void, T>() {
                @Override
                public Void apply(T t) {
                    if (predicate.apply(t)) {
                        throw breakOut(payload);
                    }
                    return null;
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
                }
            };
        }
        
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        public static <T> _.If<T> and(final java.util.List<_.IFunc1<Boolean, T>> conds) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    for (_.IFunc1<Boolean, T> cond : conds) {
                        if (!cond.apply(t)) {
                            return false;
                        }
=======
        /**
         * Return a one variable function that throw out a {@link Break} when a predicate return
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         * 
         * @since 0.2
         */
        public static <T> F1<Void, T> breakIf(final IFunc1<Boolean, T> predicate) {
            return new F1<Void, T>() {
                @Override
                public Void apply(T t) {
                    if (predicate.apply(t)) {
                        throw breakOut(true);
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
                    }
                    return null;
                }
            };
        }
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        
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
=======

        /**
         * Return a two variables function that throw out a {@link Break} with payload specified when 
         * a two variables predicate return <code>true</code> on an element been tested
         * 
         * 
         * @since 0.2
         */
        public static <P, T1, T2> F2<Void, T1, T2> breakIf(final IFunc2<Boolean, T1, T2> predicate, final P payload) {
            return new F2<Void, T1, T2>() {
                @Override
                public Void apply(T1 t1, T2 t2) {
                    if (predicate.apply(t1, t2)) {
                        throw breakOut(payload);
                    }
                    return null;
                }
            };
        }
        
        /**
         * Return a two variables function that throw out a {@link Break} when a two variables predicate return
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         * 
         * @since 0.2
         */
        public static <T1, T2> F2<Void, T1, T2> breakIf(final IFunc2<Boolean, T1, T2> predicate) {
            return new F2<Void, T1, T2>() {
                @Override
                public Void apply(T1 t1, T2 t2) {
                    if (predicate.apply(t1, t2)) {
                        throw breakOut(true);
                    }
                    return null;
                }
            };
        }

        /**
         * Return a three variables function that throw out a {@link Break} with payload specified when 
         * a three variables predicate return <code>true</code> on an element been tested
         * 
         * 
         * @since 0.2
         */
        public static <P, T1, T2, T3> F3<Void, T1, T2, T3> breakIf(final IFunc3<Boolean, T1, T2, T3> predicate, final P payload) {
            return new F3<Void, T1, T2, T3>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3) {
                    if (predicate.apply(t1, t2, t3)) {
                        throw breakOut(payload);
                    }
                    return null;
                }
            };
        }
        
        /**
         * Return a three variables function that throw out a {@link Break} when a three variables predicate return
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         * 
         * @since 0.2
         */
        public static <T1, T2, T3> F3<Void, T1, T2, T3> breakIf(final IFunc3<Boolean, T1, T2, T3> predicate) {
            return new F3<Void, T1, T2, T3>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3) {
                    if (predicate.apply(t1, t2, t3)) {
                        throw breakOut(true);
                    }
                    return null;
                }
            };
        }

        /**
         * Return a four variables function that throw out a {@link Break} with payload specified when 
         * a four variables predicate return <code>true</code> on an element been tested
         * 
         * 
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4> F4<Void, T1, T2, T3, T4> breakIf(final IFunc4<Boolean, T1, T2, T3, T4> predicate, final P payload) {
            return new F4<Void, T1, T2, T3, T4>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3, T4 t4) {
                    if (predicate.apply(t1, t2, t3, t4)) {
                        throw breakOut(payload);
                    }
                    return null;
                }
            };
        }
        
        /**
         * Return a four variables function that throw out a {@link Break} when a four variables predicate return
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         * 
         * @since 0.2
         */
        public static <T1, T2, T3, T4> F4<Void, T1, T2, T3, T4> breakIf(final IFunc4<Boolean, T1, T2, T3, T4> predicate) {
            return new F4<Void, T1, T2, T3, T4>() {
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3, T4 t4) {
                    if (predicate.apply(t1, t2, t3, t4)) {
                        throw breakOut(true);
                    }
                    return null;
                }
            };
        }
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        
        public static <T extends Comparable<T>> _.If<T> lt(final T o) {
            return lessThan(o);
        }
        
        public static <T extends Comparable<T>> _.If<T> lessThan(final T o) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return t.compareTo(o) < 0;
=======

        /**
         * Return a five variables function that throw out a {@link Break} with payload specified when 
         * a five variables predicate return <code>true</code> on an element been tested
         * 
         * 
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4, T5> F5<Void, T1, T2, T3, T4, T5> breakIf(final IFunc5<Boolean, T1, T2, T3, T4, T5> predicate, final P payload) {
            return new F5<Void, T1, T2, T3, T4, T5>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
                    if (predicate.apply(t1, t2, t3, t4, t5)) {
                        throw breakOut(payload);
                    }
                    return null;
                }
            };
        }
        
        /**
         * Return a five variables function that throw out a {@link Break} when a five variables predicate return
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         * 
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4, T5> F5<Void, T1, T2, T3, T4, T5> breakIf(final IFunc5<Boolean, T1, T2, T3, T4, T5> predicate) {
            return new F5<Void, T1, T2, T3, T4, T5>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
                    if (predicate.apply(t1, t2, t3, t4, t5)) {
                        throw breakOut(true);
                    }
                    return null;
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
                }
            };
        }

        public static <T> _.If<T> and(final _.IFunc1<Boolean, T>... conds) {
            return and(C0.list(conds));
        }
    
        public static <T> _.If<T> or(final java.util.List<_.IFunc1<Boolean, T>> conds) {
            return not(and(conds));
        }
        
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
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
=======
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

        /**
         * A predefined forever true predicate which returns <code>true</code>
         * on any element been tested
         * 
         * @see #yes() 
         * @since 0.2
         */
        public static final If TRUE = new If(){
            @Override
            public boolean eval(Object o) {
                return true;
            }
        };

        /**
         * A type-safe version of {@link #TRUE}
         * 
         * @since 0.2
         */
         @SuppressWarnings("unchecked")
        public static <T> If<T> yes() {
            return TRUE;
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }

        /**
         * A predefined forever FALSE predicate which always return
         * <code>false</code> for whatever element been tested
         * 
         * @see #no() 
         * @since 0.2
         */
        public static If FALSE = not(TRUE);

        /**
         * A type-safe version of {@link #FALSE}
         * 
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> If<T> no() {
            return FALSE;
        }

        /**
         * A predefined <code>If</code> predicate test if the 
         * element specified is <code>null</code>.
         * 
         * @since 0.2
         */
        public static If IS_NULL = new If(){
            @Override
            public boolean eval(Object o) {
                return null == o;
            }
        };

        /**
         * The type-safe version of {@link #IS_NULL}
         * 
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> If<T> isNull() {
            return IS_NULL;
        }

        /**
         * A predefined <code>If</code> predicate test if the element
         * specified is NOT null
         */
        public static If NOT_NULL = X.f.not(IS_NULL);
        
<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
        public static <T> _.If<T> equal(final T o) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return X.eq(t, o);
                }
            };
=======
        /**
         * The type-safe version of {@link #NOT_NULL}
         * 
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> If<T> notNull() {
            return NOT_NULL;
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
        }
        
    }

<<<<<<< HEAD:src/main/java/org/osgl/util/X.java
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
=======
}
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0:src/main/java/org/osgl/util/_.java
