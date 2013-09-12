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
package org.osgl;

import org.osgl.exception.FastRuntimeException;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.*;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * <code>_</code> is the umbrella namespace aggregates core utilities of OSGL toolkit:
 * <p/>
 * <ul>
 * <li>Function interfaces and base implementations</li>
 * <li>currying utilities </li>
 * <li>Tuple and multi-elements tupbles</li>
 * <li>Option</li>
 * <li>core utilities like ts()</li>
 * <li>predefined functions aggregated in the <code>_.F</code> namespace</li>
 * </ul>
 * <p/>
 * <p>More about function interface</p>
 * <p/>
 * <p>Under <code>_</code>, there are six function interfaces defined, from <code>_.IFunc0</code>
 * to <code>_.IFunc5</code>, where the last digit means the number of parameters the function
 * is applied to. For example, the <code>apply</code> method of <code>IFunc0</code> takes
 * no parameter while that of <code>IFunc2</code> takes two parameters. All these function
 * interfaces are defined with generic type parameters, corresponding to the type of all
 * parameters and that of the return value. For procedure (a function that does not return anything),
 * the user application could use <code>Void</code> as the return value type, and return
 * <code>null</code> in the <code>apply</code> method implementation.</p>
 * <p/>
 * <p>For each function interface, OSGL provide a base class, from <code>_.F0</code> to
 * <code>_.F5</code>. Within the base class, OSGL implement several utility methods, including</p>
 * <p/>
 * <ul>
 * <li>currying methods, returns function takes fewer parameter with given parameter specified</li>
 * <li>chain, returns composed function with a function takes the result of this function</li>
 * <li>andThen, returns composed function with an array of same signature functions</li>
 * <li>breakOut, short cut the function execution sequence by throwing out a {@link Break} instance</li>
 * </ul>
 * <p/>
 * <p>Usually user application should define their function implementation by extending the base class in order
 * to benefit from the utility methods; however in certain cases, e.g. a function implementation already
 * extends another base class, user implementation cannot extends the base function class, OSGL provides
 * easy way to convert user's implementation to corresponding base class implementation, here is one
 * example of how to do it:</p>
 * <p/>
 * <pre>
 *     void foo(_.IFunc2<Integer, String> f) {
 *         F2<Integer, String> newF = _.toF2(f);
 *         newF.chain(...);
 *         ...
 *     }
 * </pre>
 * <p/>
 * <p>Dumb functions, for certain case where a dumb function is needed, OSGL defines dumb function instances for
 * each function interface, say, from <code>_.F0</code> to <code>_.F5</code>. Note the name of the dumb function
 * instance is the same as the name of the base function class. But they belong to different concept, class and
 * instance, so there is no conflict in the code. For each dumb function instance, a corresponding type safe
 * version is provided, <code>_.f0()</code> to <code>_.f5()</code>, this is the same case of
 * <code>java.util.Collections.EMPTY_LIST</code> and <code>java.util.Collections.emptyList()</code></p>
 * <p/>
 * <p>Utility methods</p>
 *
 * @author Gelin Luo
 * @version 0.2
 */
public final class _ {

    private _() {
    }

    public static final _ INSTANCE = new _();
    public static final _ instance = INSTANCE;

    // --- Functions and their default implementations

    /**
     * Define a function that apply to no parameter (strictly this is not a function)
     *
     * @param <R> the generic type of the return value, could be <code>Void</code>
     * @see IFunc1
     * @see IFunc2
     * @see IFunc3
     * @see IFunc4
     * @see IFunc5
     * @see F0
     * @since 0.2
     */
    public static interface IFunc0<R> {

        /**
         * user application to implement main logic of applying the function
         *
         * @throws NotAppliedException if the function doesn't apply to the current context
         * @throws Break               to short cut collecting operations (fold/reduce) on an {@link C.ITraversable container}
         */
        R apply() throws NotAppliedException, Break;

    }

    /**
     * Default implementation for {@link IFunc0}. Implementation of {@link IFunc0} should
     * (nearly) always extend to this class instead of implement the interface directly
     *
     * @since 0.2
     */
    public static abstract class F0<R> implements IFunc0<R> {

        /**
         * Return a {@link Break} with payload. Here is an example of how to use this method:
         * <p/>
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
        protected Break breakOut(Object payload) {
            return new Break(payload);
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param fallback
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(F0<? extends R> fallback) {
            try {
                return apply();
            } catch (NotAppliedException e) {
                return fallback.apply();
            }
        }

        /**
         * Returns a composed function that applies this function to it's input and
         * then applies the {@code after} function to the result. If evaluation of either
         * function throws an exception, it is relayed to the caller of the composed
         * function.
         *
         * @param after the function applies after this function is applied
         * @param <T>   the type of the output of the {@code before} function
         * @return the composed function
         * @throws NullPointerException if {@code before} is null
         */
        public <T> F0<T> andThen(final IFunc1<? super R, ? extends T> after) {
            E.NPE(after);
            final F0<R> me = this;
            return new F0<T>() {
                @Override
                public T apply() {
                    return after.apply(me.apply());
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p/>
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs a sequence of function to be applied after this function
         * @return a composed function
         */
        public F0<R> andThen(final IFunc0<? extends R>... fs) {
            if (fs.length == 0) {
                return this;
            }
            final F0<R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    R r = me.apply();
                    for (IFunc0<? extends R> f : fs) {
                        r = f.apply();
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link NotAppliedException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback the function to applied if this function doesn't apply in the current situation
         * @return the final result
         */
        public F0<R> orElse(final IFunc0<? extends R> fallback) {
            final F0<R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    try {
                        return me.apply();
                    } catch (NotAppliedException e) {
                        return fallback.apply();
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @returna function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F0<Option<R>> lift() {
            final F0<R> me = this;
            return new F0<Option<R>>() {
                @Override
                public Option<R> apply() {
                    try {
                        return _.some(me.apply());
                    } catch (NotAppliedException e) {
                        return _.none();
                    }
                }
            };
        }

        /**
         * Composes this partial function with an action function which gets applied to results
         * of this partial function. The action function is invoked only for its side effects;
         * its result is ignored.
         *
         * @param action the function that apply to the result of this function
         * @return a function which maps arguments x to {@code true} if this function is applied and run
         *         the action function for the side effect, or {@code false} if this function is not applied.
         */
        public F0<Boolean> runWith(final F1<? super R, ?> action) {
            final F0<R> me = this;
            return new F0<Boolean>() {
                @Override
                public Boolean apply() {
                    try {
                        action.apply(me.apply());
                        return true;
                    } catch (NotAppliedException e) {
                        return false;
                    }
                }
            };
        }

    }

    /**
     * A dumb function for {@link IFunc0} that does nothing and return <code>null</code>
     *
     * @see #f0()
     * @since 0.2
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
    @SuppressWarnings("unchecked")
    public static <R> F0<R> toF0(final IFunc0<? extends R> f0) {
        E.NPE(f0);
        if (f0 instanceof F0) {
            return (F0<R>) f0;
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
     * @param <P1> the type of first (and the only one) parameter this function applied to
     * @param <R> the type of the return value when this function applied to the parameter(s)
     * @see IFunc0
     * @see IFunc2
     * @see IFunc3
     * @see IFunc4
     * @see IFunc5
     * @see F1
     * @since 0.2
     */
    public static interface IFunc1<P1, R> {

        /**
         * Apply this function to &lt;P1&gt; type parameter.
         * <p/>
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @throws NotAppliedException if the function doesn't apply to the parameter(s)
         * @throws Break               to short cut collecting operations (fold/reduce) on an {@link C.ITraversable container}
         */
        R apply(P1 p1) throws NotAppliedException, Break;

    }

    /**
     * Base implementation of {@link IFunc1} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link IFunc1} directly
     *
     * @since 0.2
     */
    public static abstract class F1<P1, R> implements IFunc1<P1, R> {

        /**
         * @see F0#breakOut(Object)
         */
        protected Break breakOut(Object payload) {
            return new Break(payload);
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         * @param fallback
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, F1<? super P1, ? extends R> fallback) {
            try {
                return apply(p1);
            } catch (NotAppliedException e) {
                return fallback.apply(p1);
            }
        }

        public final F0<R> curry(final P1 p1) {
            final IFunc1<P1, R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(p1);
                }
            };
        }

        /**
         * Returns a composed function that applies this function to it's input and
         * then applies the {@code after} function to the result. If evaluation of either
         * function throws an exception, it is relayed to the caller of the composed
         * function.
         *
         * @param <T> the type of the new function's application result
         * @param after the function applies after this function is applied
         * @param <T>   the type of the output of the {@code before} function
         * @return the composed function
         * @throws NullPointerException if @{code after} is null
         */
        public <T> F1<P1, T> andThen(final IFunc1<? super R, ? extends T> after) {
            E.NPE(after);
            final IFunc1<P1, R> me = this;
            return new F1<P1, T>() {
                @Override
                public T apply(P1 p1) {
                    return after.apply(me.apply(p1));
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p/>
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param afters a sequence of function to be applied after this function
         * @return a composed function
         */
        public F1<P1, R> andThen(final IFunc1<? super P1, ? extends R>... afters) {
            if (0 == afters.length) {
                return this;
            }
            final F1<P1, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    R r = me.apply(p1);
                    for (IFunc1<? super P1, ? extends R> f : afters) {
                        r = f.apply(p1);
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link NotAppliedException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F1<P1, R> orElse(final IFunc1<? super P1, ? extends R> fallback) {
            final F1<P1, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    try {
                        return me.apply(p1);
                    } catch (NotAppliedException e) {
                        return fallback.apply(p1);
                    }
                }
            };
        }

        /**
         * Returns an {@code F0&lt;R&gt;>} function by composing the specified {@code IFunc0&ltP1&gt;} function
         * with this function applied last
         *
         * @param before the function to be applied first when applying the return function
         * @return an new function such that f() == apply(f0())
         */
        public F0<R> compose(final IFunc0<? extends P1> before) {
            final F1<P1, R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(before.apply());
                }
            };
        }

        /**
         * Returns an {@code F1&lt;R, A&gt;>} function by composing the specified
         * {@code IFunc1&ltP1, A&gt;} function with this function applied last
         *
         * @param before the function to be applied first when applying the return function
         * @return an new function such that f(a) == apply(f1(a))
         */
        public <A> F1<A, R> compose(final IFunc1<? super A, ? extends P1> before) {
            final F1<P1, R> me = this;
            return new F1<A, R>() {
                @Override
                public R apply(A a) {
                    return me.apply(before.apply(a));
                }
            };
        }

        /**
         * Returns an {@code F2&lt;R, A, B&gt;>} function by composing the specified
         * {@code IFunc2&ltP1, A, B&gt;} function with this function applied last
         *
         * @param <A> the type of first param the new function applied to
         * @param <B> the type of second param the new function applied to
         * @param before the function to be applied first when applying the return function
         * @return an new function such that f(a, b) == apply(f1(a, b))
         */
        public <A, B> F2<A, B, R> compose(final IFunc2<? super A, ? super B, ? extends P1> before) {
            final F1<P1, R> me = this;

            return new F2<A, B, R>() {
                @Override
                public R apply(A a, B b) {
                    return me.apply(before.apply(a, b));
                }
            };
        }

        /**
         * Returns an {@code F3&lt;R, A, B, C&gt;>} function by composing the specified
         * {@code IFunc3&ltP1, A, B, C&gt;} function with this function applied last
         *
         * @param <A> the type of first param the new function applied to
         * @param <B> the type of second param the new function applied to
         * @param <C> the type of third param the new function applied to
         * @param before the function to be applied first when applying the return function
         * @return an new function such that f(a, b, c) == apply(f1(a, b, c))
         */
        public <A, B, C> F3<A, B, C, R> compose(
                final IFunc3<? super A, ? super B, ? super C, ? extends P1> before
        ) {
            final F1<P1, R> me = this;
            return new F3<A, B, C, R>() {
                @Override
                public R apply(A a, B b, C c) {
                    return me.apply(before.apply(a, b, c));
                }
            };
        }

        /**
         * Returns an {@code F4&lt;R, A, B, C, D&gt;>} function by composing the specified
         * {@code IFunc4&ltP1, A, B, C, D&gt;} function with this function applied last
         *
         * @param <A> the type of first param the new function applied to
         * @param <B> the type of second param the new function applied to
         * @param <C> the type of third param the new function applied to
         * @param <D> the type of the fourth param the new function applied to
         * @param before the function to be applied first when applying the return function
         * @return an new function such that f(a, b, c, c) == apply(f1(a, b, c, d))
         */
        public <A, B, C, D> F4<A, B, C, D, R> compose(
                final IFunc4<? super A, ? super B, ? super C, ? super D, ? extends P1> before
        ) {
            final F1<P1, R> me = this;
            return new F4<A, B, C, D, R>() {
                @Override
                public R apply(A a, B b, C c, D d) {
                    return me.apply(before.apply(a, b, c, d));
                }
            };
        }

        /**
         * Returns an {@code F5&lt;R, A, B, C, D, E&gt;>} function by composing the specified
         * {@code IFunc4&ltP1, A, B, C, D, E&gt;} function with this function applied last
         *
         * @param <A> the type of first param the new function applied to
         * @param <B> the type of second param the new function applied to
         * @param <C> the type of third param the new function applied to
         * @param <D> the type of the fourth param the new function applied to
         * @param <E> the type of the fifth param the new function applied to
         * @param before the function to be applied first when applying the return function
         * @return an new function such that f(a, b, c, d, e) == apply(f1(a, b, c, d, e))
         */
        public <A, B, C, D, E> F5<A, B, C, D, E, R> compose(
                final IFunc5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends P1> before
        ) {
            final F1<P1, R> me = this;
            return new F5<A, B, C, D, E, R>() {
                @Override
                public R apply(A a, B b, C c, D d, E e) {
                    return me.apply(before.apply(a, b, c, d, e));
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @returna function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F1<P1, Option<R>> lift() {
            final F1<P1, R> me = this;
            return new F1<P1, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1) {
                    try {
                        return _.some(me.apply(p1));
                    } catch (NotAppliedException e) {
                        return _.none();
                    }
                }
            };
        }

        /**
         * Composes this partial function with an action function which gets applied to results
         * of this partial function. The action function is invoked only for its side effects;
         * its result is ignored.
         *
         * @param action the function that apply to the result of this function
         * @return a function which maps arguments x to {@code true} if this function is applied and run
         *         the action function for the side effect, or {@code false} if this function is not applied.
         */
        public F1<P1, Boolean> runWith(final F1<? super R, ?> action) {
            final F1<P1, R> me = this;
            return new F1<P1, Boolean>() {
                @Override
                public Boolean apply(P1 p1) {
                    try {
                        action.apply(me.apply(p1));
                        return true;
                    } catch (NotAppliedException e) {
                        return false;
                    }
                }
            };
        }
    }

    /**
     * A dumb {@link IFunc1} implementation that does nothing and return null
     *
     * @see #f1()
     * @since 0.2
     */
    public static final F1 F1 = new F1() {
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
    public static <P1, R> F1<P1, R> f1() {
        return F1;
    }


    /**
     * Convert a general {@link IFunc1} function into a {@link F1} typed
     * function
     *
     * @since 0.2
     */
    public static <P1, R> F1 toF1(final IFunc1<? super P1, ? extends R> f1) {
        E.NPE(f1);
        if (f1 instanceof F1) {
            return (F1<P1, R>) f1;
        }
        return new F1<P1, R>() {
            @Override
            public R apply(P1 p1) {
                return f1.apply(p1);
            }
        };
    }

    /**
     * Define a function structure that accept two parameter
     *
     * @param <P1> the type of first parameter this function applied to
     * @param <P2> the type of second parameter this function applied to
     * @param <R> the type of the return value when this function applied to the parameter(s)
     * @see IFunc0
     * @see IFunc1
     * @see IFunc3
     * @see IFunc4
     * @see IFunc5
     * @see F2
     * @since 0.2
     */
    public static interface IFunc2<P1, P2, R> {

        /**
         * Apply the two params to the function
         * <p/>
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @throws NotAppliedException if the function doesn't apply to the parameter(s)
         * @throws Break               to short cut collecting operations (fold/reduce) on an {@link C.ITraversable container}
         */
        R apply(P1 p1, P2 p2) throws NotAppliedException, Break;

    }

    /**
     * Base implementation of {@link IFunc2} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link IFunc2} directly
     *
     * @since 0.2
     */
    public static abstract class F2<P1, P2, R> implements IFunc2<P1, P2, R> {

        protected Break breakOut(Object payload) {
            return new Break(payload);
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         * @param p2
         * @param fallback
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, P2 p2, F2<? super P1, ? super P2, ? extends R> fallback) {
            try {
                return apply(p1, p2);
            } catch (NotAppliedException e) {
                return fallback.apply(p1, p2);
            }
        }

        public final F0<R> curry(final P1 p1, final P2 p2) {
            final F2<P1, P2, R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(p1, p2);
                }
            };
        }

        public final F1<P1, R> curry(final P2 p2) {
            final F2<P1, P2, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    return me.apply(p1, p2);
                }
            };
        }

        /**
         * Returns a composed function from this function and the specified function that takes the
         * result of this function. When applying the composed function, this function is applied
         * first to given parameter and then the specified function is applied to the result of
         * this function.
         *
         * @param f the function takes the <code>R</code> type parameter and return <code>T</code>
         *          type result
         * @return the composed function
         * @throws NullPointerException if <code>f</code> is null
         */
        public <T> F2<P1, P2, T> andThen(final IFunc1<? super R, ? extends T> f) {
            E.NPE(f);
            final IFunc2<P1, P2, R> me = this;
            return new F2<P1, P2, T>() {
                @Override
                public T apply(P1 p1, P2 p2) {
                    R r = me.apply(p1, p2);
                    return f.apply(r);
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p/>
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs a sequence of function to be applied after this function
         * @return a composed function
         */
        public F2<P1, P2, R> andThen(final IFunc2<? super P1, ? super P2, ? extends R>... fs) {
            if (0 == fs.length) {
                return this;
            }
            final IFunc2<P1, P2, R> me = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    R r = me.apply(p1, p2);
                    for (IFunc2<? super P1, ? super P2, ? extends R> f : fs) {
                        r = f.apply(p1, p2);
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link NotAppliedException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F2<P1, P2, R> orElse(final IFunc2<? super P1, ? super P2, ? extends R> fallback) {
            final F2<P1, P2, R> me = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    try {
                        return me.apply(p1, p2);
                    } catch (NotAppliedException e) {
                        return fallback.apply(p1, p2);
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @returna function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F2<P1, P2, Option<R>>  lift() {
            final F2<P1, P2, R> me = this;
            return new F2<P1, P2, Option<R>> () {
                @Override
                public Option<R> apply(P1 p1, P2 p2) {
                    try {
                        return _.some(me.apply(p1, p2));
                    } catch (NotAppliedException e) {
                        return _.none();
                    }
                }
            };
        }

        /**
         * Composes this partial function with an action function which gets applied to results
         * of this partial function. The action function is invoked only for its side effects;
         * its result is ignored.
         *
         * @param action the function that apply to the result of this function
         * @return a function which maps arguments x to {@code true} if this function is applied and run
         *         the action function for the side effect, or {@code false} if this function is not applied.
         */
        public F2<P1, P2, Boolean> runWith(final F1<? super R, ?> action) {
            final F2<P1, P2, R> me = this;
            return new F2<P1, P2, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2) {
                    try {
                        action.apply(me.apply(p1, p2));
                        return true;
                    } catch (NotAppliedException e) {
                        return false;
                    }
                }
            };
        }
    }

    /**
     * A dumb {@link IFunc2} implementation that does nothing and return null
     *
     * @see #f2()
     * @since 0.2
     */
    public static final F2 F2 = new F2() {
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
    public static <P1, P2, R> F2<P1, P2, R> f2() {
        return F2;
    }


    /**
     * Convert a general {@link IFunc2} function into a {@link F2} typed
     * function
     *
     * @param <P1> the type of the first param the new function applied to
     * @param <P2> the type of the second param the new function applied to
     * @param <R> the type of new function application result
     * @return a {@code F2} instance corresponding to the specified {@code IFunc2} instance
     * @since 0.2
     */
    public static <P1, P2, R> F2 toF2(final IFunc2<? super P1, ? super P2, ? extends R> f2) {
        E.NPE(f2);
        if (f2 instanceof F2) {
            return (F2<P1, P2, R>) f2;
        }
        return new F2<P1, P2, R>() {
            @Override
            public R apply(P1 p1, P2 p2) {
                return f2.apply(p1, p2);
            }
        };
    }

    /**
     * Define a function structure that accept three parameter
     *
     * @param <P1> the type of first parameter this function applied to
     * @param <P2> the type of second parameter this function applied to
     * @param <P3> the type of thrid parameter this function applied to
     * @param <R> the type of the return value when this function applied to the parameter(s)
     * @see IFunc0
     * @see IFunc1
     * @see IFunc2
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc3<P1, P2, P3, R> {
        /**
         * Run the function with parameters specified.
         * <p/>
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @throws NotAppliedException if the function doesn't apply to the parameter(s)
         * @throws Break               to short cut collecting operations (fold/reduce) on an {@link C.ITraversable container}
         */
        R apply(P1 p1, P2 p2, P3 p3) throws NotAppliedException, Break;

    }

    /**
     * Base implementation of {@link IFunc3} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link IFunc3} directly
     *
     * @since 0.2
     */
    public static abstract class F3<P1, P2, P3, R> implements IFunc3<P1, P2, P3, R> {
        /**
         * @see F1#breakOut(Object)
         */
        protected Break breakOut(Object payload) {
            return new Break(payload);
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         * @param p2
         * @param p3
         * @param fallback
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, P2 p2, P3 p3, F3<? super P1, ? super P2, ? super P3, ? extends R> fallback) {
            try {
                return apply(p1, p2, p3);
            } catch (NotAppliedException e) {
                return fallback.apply(p1, p2, p3);
            }
        }

        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3) {
            final F3<P1, P2, P3, R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(p1, p2, p3);
                }
            };
        }

        public final F1<P1, R> curry(final P2 p2, final P3 p3) {
            final F3<P1, P2, P3, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    return me.apply(p1, p2, p3);
                }
            };
        }

        public final F2<P1, P2, R> curry(final P3 p3) {
            final F3<P1, P2, P3, R> me = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    return me.apply(p1, p2, p3);
                }
            };
        }

        /**
         * Returns a composed function from this function and the specified function that takes the
         * result of this function. When applying the composed function, this function is applied
         * first to given parameter and then the specified function is applied to the result of
         * this function.
         *
         * @param f the function takes the <code>R</code> type parameter and return <code>T</code>
         *          type result
         * @return the composed function
         * @throws NullPointerException if <code>f</code> is null
         */
        public <T> IFunc3<P1, P2, P3, T> andThen(final IFunc1<? super R, ? extends T> f) {
            E.NPE(f);
            final IFunc3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, T>() {
                @Override
                public T apply(P1 p1, P2 p2, P3 p3) {
                    R r = me.apply(p1, p2, p3);
                    return f.apply(r);
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p/>
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs a sequence of function to be applied after this function
         * @return a composed function
         */
        public IFunc3<P1, P2, P3, R> andThen(
                final IFunc3<? super P1, ? super P2, ? super P3, ? extends R>... fs
        ) {
            if (0 == fs.length) {
                return this;
            }

            final IFunc3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3) {
                    R r = me.apply(p1, p2, p3);
                    for (IFunc3<? super P1, ? super P2, ? super P3, ? extends R> f : fs) {
                        r = f.apply(p1, p2, p3);
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link NotAppliedException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F3<P1, P2, P3, R> orElse(final IFunc3<? super P1, ? super P2, ? super P3, ? extends R> fallback) {
            final F3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3) {
                    try {
                        return me.apply(p1, p2, p3);
                    } catch (NotAppliedException e) {
                        return fallback.apply(p1, p2, p3);
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @returna function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F3<P1, P2, P3, Option<R>> lift() {
            final F3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1, P2 p2, P3 p3) {
                    try {
                        return _.some(me.apply(p1, p2, p3));
                    } catch (NotAppliedException e) {
                        return _.none();
                    }
                }
            };
        }

        /**
         * Composes this partial function with an action function which gets applied to results
         * of this partial function. The action function is invoked only for its side effects;
         * its result is ignored.
         *
         * @param action the function that apply to the result of this function
         * @return a function which maps arguments x to {@code true} if this function is applied and run
         *         the action function for the side effect, or {@code false} if this function is not applied.
         */
        public F3<P1, P2, P3, Boolean> runWith(final F1<? super R, ?> action) {
            final F3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3) {
                    try {
                        action.apply(me.apply(p1, p2, p3));
                        return true;
                    } catch (NotAppliedException e) {
                        return false;
                    }
                }
            };
        }
    }

    /**
     * A dumb {@link IFunc3} implementation that does nothing and return null
     *
     * @see #f3()
     * @since 0.2
     */
    public static final F3 F3 = new F3() {
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
    public static <P1, P2, P3, R> F3<P1, P2, P3, R> f3() {
        return F3;
    }


    /**
     * Convert a general {@link IFunc3} function into a {@link F3} typed
     * function
     *
     * @since 0.2
     */
    public static <P1, P2, P3, R> F3 toF3(final IFunc3<? super P1, ? super P2, ? super P3, ? extends R> f3) {
        E.NPE(f3);
        if (f3 instanceof F3) {
            return (F3<P1, P2, P3, R>) f3;
        }
        return new F3<P1, P2, P3, R>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3) {
                return f3.apply(p1, p2, p3);
            }
        };
    }

    /**
     * Define a function structure that accept four parameter
     *
     * @param <P1> the type of first parameter this function applied to
     * @param <P2> the type of second parameter this function applied to
     * @param <P3> the type of thrid parameter this function applied to
     * @param <P4> the type of fourth parameter this function applied to
     * @param <R> the type of the return value when this function applied to the parameter(s)
     * @see IFunc0
     * @see IFunc1
     * @see IFunc2
     * @see IFunc4
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc4<P1, P2, P3, P4, R> {
        /**
         * Run the function with parameters specified.
         * <p/>
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @throws NotAppliedException if the function doesn't apply to the parameter(s)
         * @throws Break               to short cut collecting operations (fold/reduce) on an {@link C.ITraversable container}
         */
        R apply(P1 p1, P2 p2, P3 p3, P4 p4) throws NotAppliedException, Break;

    }

    /**
     * Base implementation of {@link IFunc4} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link IFunc4} directly
     *
     * @since 0.2
     */
    public static abstract class F4<P1, P2, P3, P4, R> implements IFunc4<P1, P2, P3, P4, R> {
        /**
         * @see F1#breakOut(Object)
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         * @param p2
         * @param p3
         * @param p4
         * @param fallback
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, P2 p2, P3 p3, P4 p4,
                             F4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fallback
        ) {
            try {
                return apply(p1, p2, p3, p4);
            } catch (NotAppliedException e) {
                return fallback.apply(p1, p2, p3, p4);
            }
        }

        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(p1, p2, p3, p4);
                }
            };
        }

        public final F1<P1, R> curry(final P2 p2, final P3 p3, final P4 p4) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    return me.apply(p1, p2, p3, p4);
                }
            };
        }

        public final F2<P1, P2, R> curry(final P3 p3, final P4 p4) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    return me.apply(p1, p2, p3, p4);
                }
            };
        }

        public final F3<P1, P2, P3, R> curry(final P4 p4) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F3<P1, P2, P3, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3) {
                    return me.apply(p1, p2, p3, p4);
                }
            };
        }

        /**
         * Returns a composed function from this function and the specified function that takes the
         * result of this function. When applying the composed function, this function is applied
         * first to given parameter and then the specified function is applied to the result of
         * this function.
         *
         * @param f the function takes the <code>R</code> type parameter and return <code>T</code>
         *          type result
         * @return the composed function
         * @throws NullPointerException if <code>f</code> is null
         */
        public <T> F4<P1, P2, P3, P4, T> andThen(final IFunc1<? super R, ? extends T> f) {
            E.NPE(f);
            final IFunc4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, T>() {
                @Override
                public T apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    R r = me.apply(p1, p2, p3, p4);
                    return f.apply(r);
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p/>
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs a sequence of function to be applied after this function
         * @return a composed function
         */
        public F4<P1, P2, P3, P4, R> andThen(
                final IFunc4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R>... fs
        ) {
            if (0 == fs.length) {
                return this;
            }

            final IFunc4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    R r = me.apply(p1, p2, p3, p4);
                    for (IFunc4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> f : fs) {
                        r = f.apply(p1, p2, p3, p4);
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link NotAppliedException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F4<P1, P2, P3, P4, R> orElse(
                final IFunc4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fallback
        ) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        return me.apply(p1, p2, p3, p4);
                    } catch (NotAppliedException e) {
                        return fallback.apply(p1, p2, p3, p4);
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @return a function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F4<P1, P2, P3, P4, Option<R>> lift() {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        return _.some(me.apply(p1, p2, p3, p4));
                    } catch (NotAppliedException e) {
                        return _.none();
                    }
                }
            };
        }

        /**
         * Composes this partial function with an action function which gets applied to results
         * of this partial function. The action function is invoked only for its side effects;
         * its result is ignored.
         *
         * @param action the function that apply to the result of this function
         * @return a function which maps arguments x to {@code true} if this function is applied and run
         *         the action function for the side effect, or {@code false} if this function is not applied.
         */
        public F4<P1, P2, P3, P4, Boolean> runWith(final F1<? super R, ?> action) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        action.apply(me.apply(p1, p2, p3, p4));
                        return true;
                    } catch (NotAppliedException e) {
                        return false;
                    }
                }
            };
        }
    }

    /**
     * A dumb {@link IFunc4} implementation that does nothing and return null
     *
     * @see #f4()
     * @since 0.2
     */
    public static final F4 F4 = new F4() {
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
    public static <P1, P2, P3, P4, R> F4<P1, P2, P3, P4, R> f4() {
        return F4;
    }


    /**
     * Convert a general {@link IFunc4} function into a {@link F4} typed
     * function
     *
     * @since 0.2
     */
    public static <P1, P2, P3, P4, R> F4 toF4(
            final IFunc4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> f4
    ) {
        E.NPE(f4);
        if (f4 instanceof F4) {
            return (F4<P1, P2, P3, P4, R>) f4;
        }
        return new F4<P1, P2, P3, P4, R>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                return f4.apply(p1, p2, p3, p4);
            }
        };
    }

    /**
     * Define a function structure that accept five parameter
     *
     * @param <P1> the type of first parameter this function applied to
     * @param <P2> the type of second parameter this function applied to
     * @param <P3> the type of thrid parameter this function applied to
     * @param <P4> the type of fourth parameter this function applied to
     * @param <P5> the type of fifth parameter this function applied to
     * @param <R> the type of the return value when this function applied to the parameter(s)
     * @see IFunc0
     * @see IFunc1
     * @see IFunc2
     * @see IFunc3
     * @see IFunc5
     * @since 0.2
     */
    public static interface IFunc5<P1, P2, P3, P4, P5, R> {
        /**
         * Run the function with parameters specified.
         * <p/>
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @throws NotAppliedException if the function doesn't apply to the parameter(s)
         * @throws Break               to short cut collecting operations (fold/reduce) on an {@link C.ITraversable container}
         */
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws NotAppliedException, Break;
    }

    /**
     * Base implementation of {@link IFunc5} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link IFunc5} directly
     *
     * @since 0.2
     */
    public static abstract
    class F5<P1, P2, P3, P4, P5, R> implements IFunc5<P1, P2, P3, P4, P5, R> {
        /**
         * @see F1#breakOut(Object)
         */
        public Break breakOut(Object payload) {
            return new Break(payload);
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         * @param p2
         * @param p3
         * @param p4
         * @param p5
         * @param fallback
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5,
                             F5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> fallback
        ) {
            try {
                return apply(p1, p2, p3, p4, p5);
            } catch (NotAppliedException e) {
                return fallback.apply(p1, p2, p3, p4, p5);
            }
        }

        public final F0<R> curry(final P1 p1, final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
            final F5<P1, P2, P3, P4, P5, R> f5 = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return f5.apply(p1, p2, p3, p4, p5);
                }
            };
        }

        public final F1<P1, R> curry(final P2 p2, final P3 p3, final P4 p4, final P5 p5) {
            final F5<P1, P2, P3, P4, P5, R> f5 = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    return f5.apply(p1, p2, p3, p4, p5);
                }
            };
        }

        public final F2<P1, P2, R> curry(final P3 p3, final P4 p4, final P5 p5) {
            final F5<P1, P2, P3, P4, P5, R> f5 = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    return f5.apply(p1, p2, p3, p4, p5);
                }
            };
        }

        public final F3<P1, P2, P3, R> curry(final P4 p4, final P5 p5) {
            final F5<P1, P2, P3, P4, P5, R> f5 = this;
            return new F3<P1, P2, P3, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3) {
                    return f5.apply(p1, p2, p3, p4, p5);
                }
            };
        }

        public final F4<P1, P2, P3, P4, R> curry(final P5 p5) {
            final F5<P1, P2, P3, P4, P5, R> f5 = this;
            return new F4<P1, P2, P3, P4, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    return f5.apply(p1, p2, p3, p4, p5);
                }
            };
        }

        /**
         * Returns a composed function from this function and the specified function that takes the
         * result of this function. When applying the composed function, this function is applied
         * first to given parameter and then the specified function is applied to the result of
         * this function.
         *
         * @param f the function takes the <code>R</code> type parameter and return <code>T</code>
         *          type result
         * @return the composed function
         * @throws NullPointerException if <code>f</code> is null
         */
        public <T> F5<P1, P2, P3, P4, P5, T> andThen(final IFunc1<? super R, ? extends T> f) {
            E.NPE(f);
            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, T>() {
                @Override
                public T apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    R r = me.apply(p1, p2, p3, p4, p5);
                    return f.apply(r);
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p/>
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs a sequence of function to be applied after this function
         * @return a composed function
         */
        public F5<P1, P2, P3, P4, P5, R> andThen(
                final IFunc5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R>... fs
        ) {
            if (0 == fs.length) {
                return this;
            }

            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    R r = me.apply(p1, p2, p3, p4, p5);
                    for (IFunc5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> f : fs) {
                        r = f.apply(p1, p2, p3, p4, p5);
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link NotAppliedException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F5<P1, P2, P3, P4, P5, R> orElse(
                final IFunc5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> fallback
        ) {
            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        return me.apply(p1, p2, p3, p4, p5);
                    } catch (NotAppliedException e) {
                        return fallback.apply(p1, p2, p3, p4, p5);
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @return a function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F5<P1, P2, P3, P4, P5, Option<R>> lift() {
            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        return _.some(me.apply(p1, p2, p3, p4, p5));
                    } catch (NotAppliedException e) {
                        return _.none();
                    }
                }
            };
        }

        /**
         * Composes this partial function with an action function which gets applied to results
         * of this partial function. The action function is invoked only for its side effects;
         * its result is ignored.
         *
         * @param action the function that apply to the result of this function
         * @return a function which maps arguments x to {@code true} if this function is applied and run
         *         the action function for the side effect, or {@code false} if this function is not applied.
         */
        public F5<P1, P2, P3, P4, P5, Boolean> runWith(final F1<? super R, ?> action) {
            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        action.apply(me.apply(p1, p2, p3, p4, p5));
                        return true;
                    } catch (NotAppliedException e) {
                        return false;
                    }
                }
            };
        }
    }

    /**
     * A dumb {@link IFunc5} implementation that does nothing and return null
     *
     * @see #f5()
     * @since 0.2
     */
    public static final F5 F5 = new F5() {
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
    public static <P1, P2, P3, P4, P5, R> F5<P1, P2, P3, P4, P5, R> f5() {
        return F5;
    }


    /**
     * Convert a general {@link IFunc5} function into a {@link F5} typed
     * function
     *
     * @since 0.2
     */
    public static <P1, P2, P3, P4, P5, R> F5 toF5(
            final IFunc5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> f5
    ) {
        E.NPE(f5);
        if (f5 instanceof F5) {
            return (F5<P1, P2, P3, P4, P5, R>) f5;
        }
        return new F5<P1, P2, P3, P4, P5, R>() {
            @Override
            public R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                return f5.apply(p1, p2, p3, p4, p5);
            }
        };
    }

    // Define common used Function classes including If and Visitor

    /**
     * <code>If</code> is a predefined <code>IFunc1&lt;Boolean, T&gt;</code> typed
     * function with a set of utilities dealing with boolean operations. This is often known
     * as <a href="http://en.wikipedia.org/wiki/Predicate_(mathematical_logic)">Predicate</a>
     * <p/>
     * <p>Note in user application, it should NOT assume an argument is of <code>If</code>
     * typed function, instead the argument should always be declared as <code>IFunc1&lt;Boolean T&gt;</code>:</p>
     * <p/>
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
    public static abstract class If<T> extends _.F1<T, Boolean> {
        @Override
        public final Boolean apply(T t) {
            return test(t);
        }

        /**
         * Sub class to implement this method to test on the supplied elements
         */
        public abstract boolean test(T t);

        /**
         * Returns a negate function of this
         *
         * @return the negate function
         */
        public If<T> negate() {
            return _.F.negate(this);
        }

        /**
         * Return an <code>If</code> predicate from a list of <code>IFunc1&lt;Boolean, T&gt;</code>
         * with AND operation. For any <code>T t</code> to be tested, if any specified predicates
         * must returns <code>false</code> on it, the resulting predicate will return <code>false</code>.
         *
         * @since 0.2
         */
        public If<T> and(final _.IFunc1<? super T, Boolean>... predicates) {
            final If<T> me = this;
            return new If<T>() {
                @Override
                public boolean test(T t) {
                    if (!me.test(t)) {
                        return false;
                    }
                    for (_.IFunc1<? super T, Boolean> f : predicates) {
                        if (!f.apply(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }

        /**
         * Return an <code>If</code> predicate from a list of <code>IFunc1&lt;Boolean, T&gt;</code>
         * with OR operation. For any <code>T t</code> to be tested, if any specified predicates
         * must returns <code>true</code> on it, the resulting predicate will return <code>true</code>.
         *
         * @since 0.2
         */
        public If<T> or(final _.IFunc1<? super T, Boolean>... predicates) {
            final If<T> me = this;
            return new If<T>() {
                @Override
                public boolean test(T t) {
                    if (me.test(t)) {
                        return true;
                    }
                    for (_.IFunc1<? super T, Boolean> f : predicates) {
                        if (f.apply(t)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
    }

    /**
     * Convert a general <code>IFunc1&lt;Boolean, T&gt; typed function to {@link If If&lt;T&gt;} function</code>
     *
     * @since 0.2
     */
    public static <T> If<T> toIf(final IFunc1<? super T, Boolean> f1) {
        if (f1 instanceof If) {
            return (If<T>) f1;
        }
        return new If<T>() {
            @Override
            public boolean test(T t) {
                return f1.apply(t);
            }
        };
    }

    /**
     * Define a visitor (known as Consumer in java 8) function which applied to one parameter and without return type
     *
     * @param <T> the type of the parameter the visitor function applied to
     */
    public abstract static class Visitor<T> extends _.F1<T, Void> {

        /**
         * Construct a visitor without payload
         */
        public Visitor() {
        }

        @Override
        public final Void apply(T t) {
            visit(t);
            return null;
        }

        /**
         * User application to implement visit logic in this method
         *
         * @param t the element been visited
         * @throws Break if the logic decide to break visit progress (usually when visiting a sequence of elements)
         */
        public abstract void visit(T t) throws Break;
    }

    /**
     * Return a composed visitor function that only applies when the guard predicate test returns <code>true</code>
     *
     * @param guard   the predicate to test the element been visited
     * @param visitor the function that visit(accept) the element if the guard tested the element successfully
     * @param <T>     the type of the element be tested and visited
     * @return the composed function
     */
    public static <T> Visitor<T> guardedVisitor(final _.IFunc1<? super T, Boolean> guard,
                                                final Visitor<? super T> visitor
    ) {
        return new Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                if (guard.apply(t)) {
                    visitor.apply(t);
                }
            }
        };
    }

    /**
     * A Break is used to shortcut a sequence of function executions
     */
    public static class Break extends FastRuntimeException {
        private Object payload;

        /**
         * construct a Break without payload
         */
        public Break() {
        }

        /**
         * Construct a Break with payload specified. Note here we can't use generic type for
         * the payload as java does not support generic typed throwable
         *
         * @param payload
         */
        public Break(Object payload) {
            this.payload = payload;
        }

        /**
         * Return the payload
         *
         * @param <T> the type of the return value
         * @return the payload
         */
        public <T> T get() {
            return (T) payload;
        }
    }


    /**
     * A utility method to throw out a Break with payload
     *
     * @param e   the payload object
     * @param <T> the type of the payload
     * @return a Break instance with the payload specified
     */
    public static <T> Break breakOut(T e) {
        throw new Break(e);
    }

    /**
     * A predefined Break instance without payload
     */
    public static final Break BREAK = new Break();

    public static abstract class IndexedVisitor<K, T> extends _.F2<K, T, Void> {

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
        public final Void apply(K id, T t) {
            visit(id, t);
            return null;
        }

        public abstract void visit(K id, T t);
    }

    public static <K, T> IndexedVisitor<K, T>
    indexGuardedVisitor(final _.IFunc1<? super K, Boolean> guard,
                        final Visitor<? super T> visitor
    ) {
        return new IndexedVisitor<K, T>() {
            @Override
            public void visit(K id, T t) throws Break {
                if (guard.apply(id)) {
                    visitor.apply(t);
                }
            }
        };
    }

    /**
     * A Transformer is literally a kind of {@link F1} function
     *
     * @param <FROM> The type of the element the transformer function applied to
     * @param <TO>   The type of the result of transform of <code>&lt;FROM&gt;</code>
     */
    public static abstract class Transformer<TO, FROM> extends _.F1<FROM, TO> {
        @Override
        public final TO apply(FROM from) {
            return transform(from);
        }

        /**
         * The place sub class to implement the transform logic
         */
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

    public static abstract class Op2<P1, T> extends _.F2<T, P1, T> {
        @Override
        public T apply(T t, P1 p1) {
            operate(t, p1);
            return t;
        }

        public abstract void operate(T t, P1 p1);
    }

    public static abstract class Op3<P1, P2, T> extends _.F3<T, P1, P2, T> {
        @Override
        public T apply(T t, P1 p1, P2 p2) {
            operate(t, p1, p2);
            return t;
        }

        public abstract void operate(T t, P1 p1, P2 p2);
    }

    public static abstract class Op4<P1, P2, P3, T> extends _.F4<T, P1, P2, P3, T> {
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
                return X.eq(that._1, _1) && X.eq(that._2, _2) && X.eq(that._3, _3);
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
                return X.eq(that._1, _1) && X.eq(that._2, _2) && X.eq(that._3, _3) && X.eq(that._4, _4);
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
                return X.eq(that._1, _1) && X.eq(that._2, _2) && X.eq(that._3, _3) && X.eq(that._4, _4) && X.eq(that._5, _5);
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

    public static abstract class Option<T> implements Iterable<T> {

        private Option(){}

        /**
         * Returns  {@code true} if this {@code Option} is not {@link #NONE}
         *
         * @return {@code true} if there is a value present, otherwise {@code false}
         * @since 0.2
         */
        public final boolean isDefined() {
            return this != NONE;
        }

        /**
         * Negate of {@link #isDefined()}
         *
         * @since 0.2
         */
        public boolean notDefined() {
            return !isDefined();
        }

        /**
         * If a value is present in this {@code Option}, returns the value,
         * otherwise throws NoSuchElementException.
         *
         * @return the non-null value held by this {@code Option}
         * @throws NoSuchElementException if this {@code Option} is {@link #NONE}
         */
        public abstract T get();

        /**
         * If a value is present, and the value matches the given predicate,
         * return an {@code Option} describing the value, otherwise return
         * {@link #NONE}.
         *
         * @param predicate the function to test the value held by this {@code Option}
         * @return an {@code Option} describing the value of this {@code Option} if
         *              a value is present and the value matches the given predicate,
         *              otherwise {@link #NONE}
         */
        public final Option<T> filter(IFunc1<T, Boolean> predicate) {
            E.NPE(predicate);
            if (notDefined()) {
                return none();
            }
            T v = get();
            if (predicate.apply(v)) {
                return this;
            } else {
                return none();
            }
        }

        /**
         * If a value is present, apply the provided mapping function to it,
         * and if the result is non-null, return an {@code Option} describing
         * the result. Otherwise return {@link #NONE}.
         *
         * @param mapper a mapping function to apply to the value, if present
         * @param <B> The type of the result of the mapping function
         * @return an Optional describing the result of applying a mapping
         *          function to the value of this {@code Option}, if a value is
         *          present, otherwise {@link #NONE}
         * @throws NullPointerException if the mapper function is {@code null}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public final <B> Option<B> map(final IFunc1<T, B> mapper) {
            return isDefined() ? of(mapper.apply(get())) : NONE;
        }

        /**
         * If a value is present, apply the provided {@code Option}-bearing
         * mapping function to it, return that result, otherwise return
         * {@link #NONE}. This method is similar to {@link #map(org.osgl._.IFunc1)},
         * but the provided mapper is one whose result is already an
         * {@code Option}, and if invoked, {@code flatMap} does not wrap it
         * with an additional {@code Option}.
         *
         * @param <B> The type parameter to the {@code Option} returned by
         * @param mapper a mapping function to apply to the value,
         * @return the result of applying an {@code Option}-bearing mapping
         *         function to the value of this {@code Option}, if a value
         *         is present, otherwise {@link #NONE}
         * @throws NullPointerException if the mapping function is {@code null}
         *              or returns a {@code null} result
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public final <B> Option<B> flatMap(final IFunc1<? super T, Option<B>> mapper) {
            E.NPE(mapper);
            Option<B> result = isDefined() ? mapper.apply(get()) : NONE;
            E.NPE(null == result);
            return result;
        }

        /**
         * Return the value if present, otherwise return {@code other}.
         *
         * @param other the value to be returned if there is no value present,
         *              may be {@code null}
         * @return the value, if present, otherwise {@code other}
         */
        public final T orElse(T other) {
            return isDefined() ? get() : other;
        }

        /**
         * Return the value if present, otherwise invoke {@code other} and return
         * the result of that invocation.
         *
         * @param other the function that is applied when no value is presented
         * @return the value if present otherwise the result of {@code other.apply()}
         * @throws NullPointerException if value is not present and other is null
         * @since 0.2
         */
        public final T orElse(IFunc0<? extends T> other) {
            return isDefined() ? get() : other.apply();
        }

        @SuppressWarnings("unchecked")
        public static <T> None<T> none() {
            return (None<T>) NONE;
        }

        /**
         * Returns an {@code Option} with the specified present non-null value.
         *
         * @param value the value that cannot be {@code null}
         * @param <T> the type of the value
         * @return an Option instance describing the value
         * @throws NullPointerException if the value specified is {@code null}
         * @since 0.2
         */
        public static <T> Some<T> some(T value) {
            E.NPE(value);
            return new Some<T>(value);
        }

        /**
         * Returns an {@code Option} with the specified present value if it is not
         * {@code null} or {@link #NONE} otherwise.
         *
         * @param value the value
         * @param <T> the type of the value
         * @return an {@code Option} describing the value if it is not {@code null}
         *          or {@link #NONE} if the value is {@code null}
         * @since 0.2
         */
        public static <T> Option<T> of(T value) {
            return null == value ? NONE : some(value);
        }

        /**
         * The runtime/instance function namespace
         */
        public final class f {
            private f() {
            }

            public final F0<Boolean> IS_DEFINED = new F0<Boolean>() {
                @Override
                public Boolean apply() {
                    return Option.this != NONE;
                }
            };
            public final F0<Boolean> NOT_DEFINED = _.F.negate(IS_DEFINED);
        }

        public final f f = new f();
    }

    public static class None<T> extends Option<T> {

        private None() {
        }

        public static final None INSTANCE = new None();


        @Override
        public T get() {
            throw new NoSuchElementException();
        }

        public Iterator<T> iterator() {
            return Collections.<T>emptyList().iterator();
        }

        @Override
        public String toString() {
            return "NONE";
        }
    }

    public static class Some<T> extends Option<T> {

        final T value;

        public Some(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        public Iterator<T> iterator() {
            return Collections.singletonList(value).iterator();
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }

    public static final None NONE = None.INSTANCE;

    @SuppressWarnings("unchecked")
    public static <T> Some<T> some(T a) {
        return new Some(a);
    }

    @SuppressWarnings("unchecked")
    public static <T> None<T> none() {
        return (None<T>) NONE;
    }

    // --- common utilities

    /**
     * Check if two object is equals to each other.
     *
     * @param a
     * @param b
     * @return {@code true} if {@code a} equals to {@code b}
     * @see #ne(Object, Object)
     */
    public static boolean eq(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (null == a || null == b) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * Check if two object is equals to each other.
     *
     * @param a
     * @param b
     * @return {@code false} if {@code a} equals to {@code b}
     * @see #eq(Object, Object)
     */
    public static boolean ne(Object a, Object b) {
        return !eq(a, b);
    }

    /**
     * Evaluate an object's bool value. The rules are:
     * <table>
     * <thead>
     * <tr>
     * <th>case</th><th>bool value</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr><td>{@code null}</td><td>{@code false}</td></tr>
     * <tr><td>{@link #NONE}</td><td>{@code false}</td></tr>
     * <tr><td>String</td><td>{@link S#notEmpty(String) S.notEmpty(v)}</td></tr>
     * <tr><td>Collection</td><td>{@link java.util.Collection#isEmpty() !v.isEmpty()}</td></tr>
     * <tr><td>Array</td><td>length of the array > 0</td></tr>
     * <tr><td>Byte</td><td>{@code v != 0}</td></tr>
     * <tr><td>Char</td><td>{@code v != 0}</td></tr>
     * <tr><td>Integer</td><td>{@code v != 0}</td></tr>
     * <tr><td>Long</td><td>{@code v != 0L}</td></tr>
     * <tr><td>Float</td><td>{@code Math.abs(v) > Float.MIN_NORMAL}</td></tr>
     * <tr><td>Double</td><td>{@code Math.abs(v) > Double.MIN_NORMAL}</td></tr>
     * <tr><td>BigInteger</td><td>{@code !BigInteger.ZERO.equals(v)}</td></tr>
     * <tr><td>BigDecimal</td><td>{@code !BigDecimal.ZERO.equals(v)}</td></tr>
     * <tr><td>File</td><td>{@link java.io.File#exists() v.exists()}</td></tr>
     * <tr><td>{@link IFunc0}</td><td>{@code bool(v.apply())}</td></tr>
     * <tr><td>Other types</td><td>{@code true}</td></tr>
     * </tbody>
     * </table>
     * @param v the value to be evaluated
     * @return {@code true} if v evaluate to true, {@code false} otherwise
     */
    public static boolean bool(Object v) {
        if (null == v || NONE == v) {
            return false;
        }
        if (v instanceof String) {
            return S.notEmpty((String) v);
        }
        if (v instanceof Collection) {
            return !((Collection)v).isEmpty();
        }
        if (v.getClass().isArray()) {
            return 0 < Array.getLength(v);
        }
        if (v instanceof Number) {
            if (v instanceof Float) {
                return bool((float)(Float)v);
            }
            if (v instanceof Double) {
                return bool((double)(Double) v);
            }
            if (v instanceof BigInteger) {
                return bool((BigInteger) v);
            }
            if (v instanceof BigDecimal) {
                return bool((BigDecimal) v);
            }
            return bool(((Number) v).intValue());
        }
        if (v instanceof File) {
            return bool((File) v);
        }
        if (v instanceof IFunc0) {
            return not(((IFunc0)v).apply());
        }
        for (IFunc1<Object, Boolean> tester: conf.boolTesters) {
            try {
                return !(tester.apply(v));
            } catch (NotAppliedException e) {
                // ignore
            }
        }
        return true;
    }

    public static boolean bool(boolean v) {
        return v;
    }

    /**
     * Do bool evaluation on a String.
     *
     * @param s the string to be evaluated
     * @return {@code true} if s is not empty
     * @see S#empty(String)
     */
    public static boolean bool(String s) {
        return !S.empty(s);
    }

    /**
     * Do bool evaluation on a collection.
     *
     * @param c the collection to be evaluated
     * @return {@code true} if the collection is not empty
     * @see java.util.Collection#isEmpty()
     */
    public static boolean bool(Collection<?> c) {
        return null != c && !c.isEmpty();
    }

    /**
     * Do bool evaluation on a byte value
     *
     * @param v the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(byte v) {
        return 0 != v;
    }

    /**
     * Do bool evaluation on a char value
     *
     * @param v the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(char v) {
        return 0 != v;
    }

    /**
     * Do bool evaluation on a int value
     *
     * @param v the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(int v) {
        return 0 != v;
    }

    /**
     * Do bool evaluation on a long value
     *
     * @param v the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(long v) {
        return 0L != v;
    }

    /**
     * Do bool evaluation on a float value
     *
     * @param v the value to be evaluated
     * @return {@code true} if {@code Math.abs(v) > Float.MIN_NORMAL}
     */
    public static boolean bool(float v) {
        return Math.abs(v) > Float.MIN_NORMAL;
    }

    /**
     * Do bool evaluation on a double value
     *
     * @param v the value to be evaluated
     * @return {@code true} if {@code Math.abs(v) > Double.MIN_NORMAL}
     */
    public static boolean bool(double v) {
        return Math.abs(v) > Double.MIN_NORMAL;
    }

    /**
     * Do bool evaluation on a BigDecimal value
     *
     * @param v the value to be evaluated
     * @return {@code true} if {@code !BigDecimal.ZERO.equals(v)}
     */
    public static boolean bool(BigDecimal v) {
        return null != v && !BigDecimal.ZERO.equals(v);
    }

    /**
     * Do bool evaluation on a BigInteger value
     *
     * @param v the value to be evaluated
     * @return {@code true} if {@code !BigInteger.ZERO.equals(v)}
     */
    public static boolean bool(BigInteger v) {
        return null != v && !BigInteger.ZERO.equals(v);
    }

    /**
     * Do bool evaluation on a File instance
     *
     * @param v the file to be evaluated
     * @return {@code true} if {@code !v.exists}
     */
    public static boolean bool(File v) {
        return null != v && v.exists();
    }

    /**
     * Do bool evaluation on an {@link IFunc0} instance. This will call
     * the {@link org.osgl._.IFunc0#apply()} method and continue to
     * do bool evaluation on the return value
     *
     * @param v the function to be evaluated
     * @return {@code bool(v.apply())}
     */
    public static boolean bool(IFunc0<?> v) {
        return bool(v.apply());
    }

    /**
     * Returns negative of {@link #bool(java.lang.Object)}
     *
     * @param o the object to be evaluated
     * @return {@code !(bool(o))}
     */
    public static boolean not(Object o) {
        return !(bool(o));
    }

    /**
     * Returns negative of a boolean value
     *
     * @param v the value to be evaluated
     * @return {@code !v}
     */
    public static boolean not(boolean v) {
        return !v;
    }

    /**
     * Returns negative of {@link #bool(java.lang.String)}
     *
     * @param s the String to be evaluated
     * @return {@code !(bool(s))}
     * @see #bool(String)
     */
    public static boolean not(String s) {
        return !bool(s);
    }

    /**
     * Returns negative of {@link #bool(java.util.Collection)}
     *
     * @param c the Collection to be evaluated
     * @return {@code !(bool(c))}
     * @see #bool(java.util.Collection)
     */
    public static boolean not(Collection<?> c) {
        return null == c || c.isEmpty();
    }

    /**
     * Returns negative of {@link #bool(byte)}
     *
     * @param v the byte to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(byte v) {
        return 0 == v;
    }

    /**
     * Returns negative of {@link #bool(char)}
     *
     * @param v the char to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(char v) {
        return 0 == v;
    }

    /**
     * Returns negative of {@link #bool(int)}
     *
     * @param v the int to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(int v) {
        return 0 == v;
    }

    /**
     * Returns negative of {@link #bool(long)}
     *
     * @param v the long to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(long v) {
        return 0L == v;
    }

    /**
     * Returns negative of {@link #bool(float)}
     *
     * @param v the float to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(float v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(double)}
     *
     * @param v the double to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(double v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(BigDecimal)}
     *
     * @param v the value to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(BigDecimal v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(BigInteger)}
     *
     * @param v the value to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(BigInteger v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(File)}
     *
     * @param file the file to be evaluated
     * @return {@code !(bool(file))}
     */
    public static boolean not(File file) {
        return !bool(file);
    }

    /**
     * Returns negative of {@link #bool(IFunc0)}
     *
     * @param f the function to be evaluated
     * @return {@code !(bool(f))}
     */
    public static boolean not(IFunc0<?> f) {
        return !bool(f);
    }

    /**
     * Check if an object is {@code null} or {@link #NONE}
     *
     * @param o the object to test
     * @return {@link true} if {@code o} is {@code null} or {@code _.NONE}
     */
    public static boolean isNull(Object o) {
        return null == o || NONE == o;
    }

    /**
     * Returns String representation of an object instance. If the object specified
     * is {@code null} or {@code _.NONE}, then an empty string is returned
     *
     * @param o
     * @return a String representation of object
     */
    public static String toString(Object o) {
        if (isNull(o)) {
            return "";
        }
        return o.toString();
    }

    /**
     * Alias of {@link org.osgl.util.S#fmt(String, Object...)}
     *
     * @since 0.2
     */
    public static final String fmt(String tmpl, Object... args) {
        return S.fmt(tmpl, args);
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param args
     * @return the calculated hash code
     */
    public final static int hc(Object... args) {
        int i = 17;
        for (Object o : args) {
            i = 31 * i + hc(o);
        }
        return i;
    }

    private static int hc(Object o) {
        if (null == o) {
            return 0;
        }
        if (o.getClass().isArray()) {
            int len = Array.getLength(o);
            int hc = 17;
            for (int i = 0; i < len; ++i) {
                hc = 31 * hc + hc(Array.get(o, i));
            }
            return hc;
        }
        return o.hashCode();
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

    /**
     * Return current time stamp in nano seconds. Alias of {@link #ns()}
     *
     * @since 0.2
     */
    public static final long ts() {
        return System.nanoTime();
    }
    // --- eof common utilities

    /**
     * Namespace to configure or extend OSGL _ utilities
     */
    public static final class Conf {
        private final List<IFunc1<Object, Boolean>> boolTesters = new ArrayList<IFunc1<Object, Boolean>>();

        /**
         * Register a boolean tester. A boolean tester is a {@link IFunc1 IFunc1&ltBoolean, Object&gt} type function
         * that applied to {@code Object} type parameter and returns a boolean value of the Object been tested. It
         * should throw out {@link NotAppliedException} if the type of the object been tested is not recognized.
         * there is no need to test if the parameter is {@code null} in the tester as the utility will garantee the
         * object passed in is not null
         *
         * <pre>
         *  _.Conf.registerBoolTester(new _.F1&lt;Boolean, Object&gt;() {
         *      @Override
         *      public Boolean apply(Object o) {
         *          if (o instanceof Score) {
         *              return ((Score)o).intValue() > 60;
         *          }
         *          if (o instanceof Person) {
         *              return ((Person)o).age() > 16;
         *          }
         *          ...
         *          // since we do not recognize the object type, raise the NotAppliedException out
         *          throw new org.osgl.E.NotAppliedException();
         *      }
         *  });
         * </pre>
         *
         * @param tester
         */
        public Conf registerBoolTester(IFunc1<Object, Boolean> tester) {
            E.NPE(tester);
            boolTesters.add(tester);
            return this;
        }
    }

    public static final Conf conf = new Conf();

    /**
     * The namespace to aggregate predefined core functions
     */
    public static final class F {
        private F() {
        }


        /**
         * Return a one variable function that throw out a {@link Break} with payload specified when a predicate return
         * <code>true</code> on an element been tested
         *
         * @since 0.2
         */
        public static <P, T> F1<T, Void> breakIf(final IFunc1<? super T, Boolean> predicate, final P payload) {
            return new F1<T, Void>() {
                @Override
                public Void apply(T t) {
                    if (predicate.apply(t)) {
                        throw breakOut(payload);
                    }
                    return null;
                }
            };
        }

        /**
         * Return a one variable function that throw out a {@link Break} when a predicate return
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         *
         * @since 0.2
         */
        public static <T> F1<T, Void> breakIf(final IFunc1<? super T, Boolean> predicate) {
            return new F1<T, Void>() {
                @Override
                public Void apply(T t) {
                    if (predicate.apply(t)) {
                        throw breakOut(true);
                    }
                    return null;
                }
            };
        }

        /**
         * Return a two variables function that throw out a {@link Break} with payload specified when
         * a two variables predicate return <code>true</code> on an element been tested
         *
         * @since 0.2
         */
        public static <P, T1, T2> F2<T1, T2, Void> breakIf(final IFunc2<? super T1, ? super T2, Boolean> predicate,
                                                           final P payload
        ) {
            return new F2<T1, T2, Void>() {
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
        public static <T1, T2> F2<T1, T2, Void> breakIf(final IFunc2<? super T1, ? super T2, Boolean> predicate) {
            return new F2<T1, T2, Void>() {
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
         * @since 0.2
         */
        public static <P, T1, T2, T3> F3<T1, T2, T3, Void> breakIf(
                final IFunc3<? super T1, ? super T2, ? super T3, Boolean> predicate,
                final P payload
        ) {
            return new F3<T1, T2, T3, Void>() {
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
        public static <T1, T2, T3> F3<T1, T2, T3, Void> breakIf(
                final IFunc3<? super T1, ? super T2, ? super T3, Boolean> predicate
        ) {
            return new F3<T1, T2, T3, Void>() {
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
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4> F4<T1, T2, T3, T4, Void> breakIf(
                final IFunc4<? super T1, ? super T2, ? super T3, ? super T4, Boolean> predicate, final P payload
        ) {
            return new F4<T1, T2, T3, T4, Void>() {
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
        public static <T1, T2, T3, T4> F4<T1, T2, T3, T4, Void> breakIf(
                final IFunc4<? super T1, ? super T2, ? super T3, ? super T4, Boolean> predicate
        ) {
            return new F4<T1, T2, T3, T4, Void>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3, T4 t4) {
                    if (predicate.apply(t1, t2, t3, t4)) {
                        throw breakOut(true);
                    }
                    return null;
                }
            };
        }

        /**
         * Return a five variables function that throw out a {@link Break} with payload specified when
         * a five variables predicate return <code>true</code> on an element been tested
         *
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4, T5> F5<T1, T2, T3, T4, T5, Void> breakIf(
                final IFunc5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Boolean> predicate,
                final P payload
        ) {
            return new F5<T1, T2, T3, T4, T5, Void>() {
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
        public static <T1, T2, T3, T4, T5> F5<T1, T2, T3, T4, T5, Void> breakIf(
                final IFunc5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Boolean> predicate
        ) {
            return new F5<T1, T2, T3, T4, T5, Void>() {
                @Override
                public Void apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
                    if (predicate.apply(t1, t2, t3, t4, t5)) {
                        throw breakOut(true);
                    }
                    return null;
                }
            };
        }

        /**
         * Returns a composed {@link If} function that for any given parameter, the test result is <code>true</code>
         * only when all of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates an iterable of predicates that can be applied to a parameter and returns boolean value
         * @param <T>        the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> _.If<T> and(final Iterable<_.IFunc1<? super T, Boolean>> predicates) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    for (_.IFunc1<? super T, Boolean> cond : predicates) {
                        if (!cond.apply(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }

        /**
         * Returns a composed {@link If} function that for any given parameter, the test result is <code>true</code>
         * only when all of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates an array of predicates that can be applied to a parameter and returns boolean value
         * @param <T>        the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> _.If<T> and(final _.IFunc1<? super T, Boolean>... predicates) {
            return and(C1.list(predicates));
        }

        /**
         * Returns a composed {@link If} function that for any given parameter, the test result is <code>true</code>
         * only when any one of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates an iterable of predicates that can be applied to a parameter and returns boolean value
         * @param <T>        the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> _.If<T> or(final Iterable<_.IFunc1<? super T, Boolean>> predicates) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    for (_.IFunc1<? super T, Boolean> cond : predicates) {
                        if (cond.apply(t)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        /**
         * Returns a composed {@link If} function that for any given parameter, the test result is <code>true</code>
         * only when any one of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates an array of predicates that can be applied to a parameter and returns boolean value
         * @param <T>        the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> _.If<T> or(final _.IFunc1<? super T, Boolean>... predicates) {
            return or(C1.list(predicates));
        }

        /**
         * Alias of {@link #or(Iterable)}
         *
         * @since 0.2
         */
        public static <T> _.If<T> any(final Iterable<_.IFunc1<? super T, Boolean>> predicates) {
            return or(predicates);
        }

        /**
         * Alias of {@link #or(_.IFunc1[])}
         *
         * @since 0.2
         */
        public static <T> _.If<T> any(final _.IFunc1<? super T, Boolean>... predicates) {
            return or(predicates);
        }

        /**
         * Negation of {@link #or(Iterable)}
         *
         * @since 0.2
         */
        public static <T> _.If<T> none(final Iterable<_.IFunc1<? super T, Boolean>> predicates) {
            return negate(or(predicates));
        }

        /**
         * Negation of {@link #or(_.IFunc1[])}
         *
         * @since 0.2
         */
        public static <T> _.If<T> none(final _.IFunc1<? super T, Boolean>... predicates) {
            return negate(or(predicates));
        }

        /**
         * Returns a function that evaluate an argument's boolean value and negate the value
         *
         * @param <T> the type of the argument the function applied to
         * @return the function returns true if an argument evaluated to false, or vice versa
         */
        public static <T> F1<T, Boolean> not() {
            return new F1<T, Boolean>() {
                @Override
                public Boolean apply(T t) throws NotAppliedException, Break {
                    return _.not(t);
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate function
         *
         * @param predicate the specified function that returns boolean value
         * @return the function that negate the specified predicate
         */
        public static F0<Boolean> negate(final IFunc0<Boolean> predicate) {
            return new F0<Boolean>() {
                @Override
                public Boolean apply() {
                    return !predicate.apply();
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate function that applied to 1 params and
         * returns boolean value
         *
         * @param predicate the specified function that applied to the parameter and returns boolean value
         * @param <T>       the type of the parameter to be applied
         * @return the function that negate the specified predicate
         */
        public static <T> If<T> negate(final IFunc1<? super T, Boolean> predicate) {
            return new _.If<T>() {
                @Override
                public boolean test(T t) {
                    return !predicate.apply(t);
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate that applied to 2 parameters and returns boolean value
         *
         * @param predicate the function applied to 2 params and return boolean value
         * @param <P1>      type of param one
         * @param <P2>      type of param two
         * @return the function that negate predicate specified
         */
        public static <P1, P2> F2<P1, P2, Boolean> negate(final IFunc2<? super P1, ? super P2, Boolean> predicate) {
            return new F2<P1, P2, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2) {
                    return !predicate.apply(p1, p2);
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate that applied to 3 parameters and returns boolean value
         *
         * @param predicate the function applied to 2 params and return boolean value
         * @param <P1>      type of param one
         * @param <P2>      type of param two
         * @param <P3>      type of param three
         * @return the function that negate predicate specified
         */
        public static <P1, P2, P3> F3<P1, P2, P3, Boolean> negate(
                final IFunc3<? super P1, ? super P2, ? super P3, Boolean> predicate
        ) {
            return new F3<P1, P2, P3, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3) {
                    return !predicate.apply(p1, p2, p3);
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate that applied to 3 parameters and returns boolean value
         *
         * @param predicate the function applied to 2 params and return boolean value
         * @param <P1>      type of param one
         * @param <P2>      type of param two
         * @param <P3>      type of param three
         * @param <P4>      type of param four
         * @return the function that negate predicate specified
         */
        public static <P1, P2, P3, P4> F4<P1, P2, P3, P4, Boolean> negate(
                final IFunc4<? super P1, ? super P2, ? super P3, ? super P4, Boolean> predicate
        ) {
            return new F4<P1, P2, P3, P4, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    return !predicate.apply(p1, p2, p3, p4);
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate that applied to 3 parameters and returns boolean value
         *
         * @param predicate the function applied to 2 params and return boolean value
         * @param <P1>      type of param one
         * @param <P2>      type of param two
         * @param <P3>      type of param three
         * @param <P4>      type of param four
         * @param <P5>      type of param five
         * @return the function that negate predicate specified
         */
        public static <P1, P2, P3, P4, P5> F5<P1, P2, P3, P4, P5, Boolean> negate(
                final IFunc5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, Boolean> predicate
        ) {
            return new F5<P1, P2, P3, P4, P5, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    return !predicate.apply(p1, p2, p3, p4, p5);
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
        public static final If TRUE = new If() {
            @Override
            public boolean test(Object o) {
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
        }

        /**
         * A predefined forever FALSE predicate which always return
         * <code>false</code> for whatever element been tested
         *
         * @see #no()
         * @since 0.2
         */
        public static final If FALSE = negate(TRUE);

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
         * element specified is <code>null</code> or {@link _#NONE}.
         *
         * @since 0.2
         */
        public static final If IS_NULL = new If() {
            @Override
            public boolean test(Object o) {
                return null == o || NONE == o;
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
         * The type-safe version of {@link #IS_NULL}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> If<T> isNull(Class<T> c) {
            return IS_NULL;
        }

        /**
         * A predefined <code>If</code> predicate test if the element
         * specified is NOT null
         *
         * @since 0.2
         */
        public static final If NOT_NULL = negate(IS_NULL);

        /**
         * The type-safe version of {@link #NOT_NULL}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> If<T> notNull() {
            return NOT_NULL;
        }

        /**
         * A predefined function that when apply to a parameter it return it directly, so for any Object o
         * <pre>
         *     IDENTITY.apply(o) == o;
         * </pre>
         *
         * @since 0.2
         */
        public static final F1 IDENTITY = new F1() {
            @Override
            public Object apply(Object o) {
                return o;
            }
        };

        /**
         * The type-safe version of {@link #IDENTITY}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> F1<T, T> identity() {
            return IDENTITY;
        }

        /**
         * A predefined function that applies to two parameters and check if they are equals to each other
         */
        public static final F2 EQ = new F2() {
            @Override
            public Object apply(Object a, Object b) {
                return _.eq(a, b);
            }
        };

        /**
         * The type-safe version of {@link #EQ}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <A, B> F2<Boolean, A, B> eq() {
            return EQ;
        }

        /**
         * A predefined function that applies to two parameters and check if they are not equals to
         * each other. This is a negate function of {@link #EQ}
         *
         * @since 0.2
         */
        public static final F2 NE = negate(EQ);

        /**
         * The type-safe version of {@link #NE}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <A, B> F2<Boolean, A, B> ne() {
            return NE;
        }

        /**
         * A predefined function that calculate hash code of an object
         *
         * @since 0.2
         */
        public static final F1 HC = new F1() {
            @Override
            public Integer apply(Object o) {
                return _.hc(o);
            }
        };

        /**
         * The type-safe version of {@link #HC}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> F1<T, Integer> hc() {
            return HC;
        }

        /**
         * A predefined function that when applied to an object instance, returns
         * String representation of the instance
         *
         * @see #toString(Object)
         * @since 0.2
         */
        public static final F1 AS_STRING = new F1() {
            @Override
            public Object apply(Object o) {
                return _.toString(o);
            }
        };

        /**
         * A type-safe version of {@link #AS_STRING}
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> F1<T, String> asString() {
            return AS_STRING;
        }
    }

    public static void main(String[] args) {
        None<String> none = none();
        System.out.println(none);

        F1<String, Integer> hc = _.F.hc();
        System.out.println(hc.apply("ABC"));

        F1<Integer, String> toString = _.F.asString();
        System.out.println(toString.apply(33));
    }

}
