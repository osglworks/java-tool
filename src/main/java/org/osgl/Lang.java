package org.osgl;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.osgl.util.DataMapper.MappingRule.KEYWORD_MATCHING;
import static org.osgl.util.DataMapper.MappingRule.STRICT_MATCHING;
import static org.osgl.util.DataMapper.Semantic.*;

import com.alibaba.fastjson.*;
import org.osgl.cache.CacheService;
import org.osgl.concurrent.ContextLocal;
import org.osgl.exception.*;
import org.osgl.util.*;
import org.osgl.util.TypeReference;
import org.osgl.util.converter.*;
import org.w3c.dom.Document;
import osgl.version.Version;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <code>Osgl</code> is the umbrella namespace aggregates core utilities of OSGL toolkit:
 * <ul>
 * <li>Function interfaces and base implementations</li>
 * <li>currying utilities </li>
 * <li>Tuple and multi-elements tupbles</li>
 * <li>Option</li>
 * <li>core utilities like ts()</li>
 * <li>predefined functions aggregated in the <code>F</code> namespace</li>
 * </ul>
 * <p>More about function interface</p>
 * <p>Under <code>Osgl</code>, there are six function interfaces defined, from <code>Func0</code>
 * to <code>Func5</code>, where the last digit means the number of parameters the function
 * is applied to. For example, the <code>apply</code> method of <code>Func0</code> takes
 * no parameter while that of <code>Func2</code> takes two parameters. All these function
 * interfaces are defined with generic type parameters, corresponding to the type of all
 * parameters and that of the return value. For procedure (a function that does not return anything),
 * the user application could use <code>Void</code> as the return value type, and return
 * <code>null</code> in the <code>apply</code> method implementation.</p>
 * <p>For each function interface, OSGL provide a base class, from <code>F0</code> to
 * <code>F5</code>. Within the base class, OSGL implement several utility methods, including</p>
 * <ul>
 * <li>currying methods, returns function takes fewer parameter with given parameter specified</li>
 * <li>chain, returns composed function with a function takes the result of this function</li>
 * <li>andThen, returns composed function with an array of same signature functions</li>
 * <li>breakOut, short cut the function execution sequence by throwing out a {@link Break} instance</li>
 * </ul>
 * <p>Usually user application should define their function implementation by extending the base class in order
 * to benefit from the utility methods; however in certain cases, e.g. a function implementation already
 * extends another base class, user implementation cannot extends the base function class, OSGL provides
 * easy way to convert user's implementation to corresponding base class implementation, here is one
 * example of how to do it:</p>
 * <pre>
 *     void foo(Func2&lt;Integer, String&gt; f) {
 *         F2&lt;Integer, String&gt; newF = f2(f);
 *         newF.chain(...);
 *         ...
 *     }
 * </pre>
 * <p>Dumb functions, for certain case where a dumb function is needed, OSGL defines dumb function instances for
 * each function interface, say, from <code>F0</code> to <code>F5</code>. Note the name of the dumb function
 * instance is the same as the name of the base function class. But they belong to different concept, class and
 * instance, so there is no conflict in the code. For each dumb function instance, a corresponding type safe
 * version is provided, <code>f0()</code> to <code>f5()</code>, this is the same case of
 * <code>java.util.Collections.EMPTY_LIST</code> and <code>java.util.Collections.emptyList()</code></p>
 * <p>Utility methods</p>
 *
 * @author Gelin Luo
 * @version 0.8
 */
public class Lang implements Serializable {

    public static final Version VERSION = Version.get();

    public static final Lang INSTANCE = $.INSTANCE;

    protected Lang() {
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this || obj instanceof Lang;
    }

    @Override
    public final int hashCode() {
        return Lang.class.hashCode();
    }

    @Override
    public final String toString() {
        return "OSGL";
    }

    // --- Functions and their default implementations

    /**
     * The base for all Fx function class implemention
     */
    public static abstract class FuncBase {
        /**
         * Return a {@link Break} with payload. Here is an example of how to use this method:
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
         * @param payload
         *         the object passed through the <code>Break</code>
         * @return a {@link Break} instance
         */
        protected final Break breakOut(Object payload) {
            return new Break(payload);
        }

    }

    /**
     * Define a function that apply to no parameter (strictly this is not a function)
     *
     * @param <R>
     *         the generic type of the return value, could be <code>Void</code>
     * @see Function
     * @see Func2
     * @see Func3
     * @see Func4
     * @see Func5
     * @see F0
     * @since 0.2
     */
    public interface Func0<R> {
        /**
         * user application to implement main logic of applying the function
         *
         * @return the Result instance of type R after appplying the function
         * @throws NotAppliedException
         *         if the function doesn't apply to the current context
         * @throws Break
         *         to short cut collecting operations (fold/reduce) on an {@link org.osgl.util.C.Traversable container}
         */
        R apply() throws NotAppliedException, Break;
    }

    /**
     * Default implementation for {@link Func0}. Implementation of {@link Func0} should
     * (nearly) always extend to this class instead of implement the interface directly
     *
     * @since 0.2
     */
    public static abstract class F0<R> extends FuncBase implements Func0<R> {

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined, i.e. any
         * {@link java.lang.RuntimeException} is captured
         *
         * @param fallback
         *         　if {@link RuntimeException} captured then apply this fallback function
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(F0<? extends R> fallback) {
            try {
                return apply();
            } catch (RuntimeException e) {
                return fallback.apply();
            }
        }

        /**
         * Returns a composed function that applies this function to it's input and
         * then applies the {@code after} function to the result. If evaluation of either
         * function throws an exception, it is relayed to the caller of the composed
         * function.
         *
         * @param after
         *         the function applies after this function is applied
         * @param <T>
         *         the type of the output of the {@code before} function
         * @return the composed function
         * @throws NullPointerException
         *         if {@code before} is null
         */
        public <T> Producer<T> andThen(final Function<? super R, ? extends T> after) {
            E.NPE(after);
            final F0<R> me = this;
            return new Producer<T>() {
                @Override
                public T produce() {
                    return after.apply(me.apply());
                }
            };
        }

        /**
         * Returns a composed function that applied, in sequence, this function and
         * all functions specified one by one. If applying anyone of the functions
         * throws an exception, it is relayed to the caller of the composed function.
         * If an exception is thrown out, the following functions will not be applied.
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs
         *         a sequence of function to be applied after this function
         * @return a composed function
         */
        public F0<R> andThen(final Func0<? extends R>... fs) {
            if (fs.length == 0) {
                return this;
            }
            final F0<R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    R r = me.apply();
                    for (Func0<? extends R> f : fs) {
                        r = f.apply();
                    }
                    return r;
                }
            };
        }

        /**
         * Returns a composed function that when applied, try to apply this function first, in case
         * a {@link java.lang.RuntimeException} is captured apply to the fallback function specified. This
         * method helps to implement partial function
         *
         * @param fallback
         *         the function to applied if this function doesn't apply in the current situation
         * @return the final result
         */
        public F0<R> orElse(final Func0<? extends R> fallback) {
            final F0<R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    try {
                        return me.apply();
                    } catch (RuntimeException e) {
                        return fallback.apply();
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @return a function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F0<Option<R>> lift() {
            final F0<R> me = this;
            return new F0<Option<R>>() {
                @Override
                public Option<R> apply() {
                    try {
                        return some(me.apply());
                    } catch (RuntimeException e) {
                        return none();
                    }
                }
            };
        }

    }

    /**
     * The class adapt traditional Factory to Function
     *
     * @param <T>
     *         the type of the instance been created by the factory
     */
    public static abstract class Factory<T> extends F0<T> {

        @Override
        public T apply() throws NotAppliedException, Break {
            return create();
        }

        /**
         * The abstract create method that will be called by
         * the {@link #apply()} method
         *
         * @return an instance the factory create
         */
        public abstract T create();
    }

    @SuppressWarnings("unchecked")
    public static <T> Factory<T> factory(final Func0<T> func) {
        if (func instanceof Factory) {
            return (Factory<T>) func;
        }
        return new Factory<T>() {
            @Override
            public T create() {
                return func.apply();
            }
        };
    }

    private static class DumbF0 extends F0<Object> implements Serializable {
        private static final long serialVersionUID = 2856835860950L;

        @Override
        public Object apply() throws NotAppliedException, Break {
            return null;
        }
    }

    /**
     * A dumb function for {@link Func0} that does nothing and return <code>null</code>
     *
     * @see #f0()
     * @since 0.2
     */
    public static final F0 F0 = new DumbF0();

    /**
     * Return a dumb function for {@link Func0}. This is the type-safe version of {@link #F0}
     *
     * @param <T>
     *         a generic type that matches whatever type required by the context of applying the function
     * @return A dumb function that always return {@code null}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <T> F0<T> f0() {
        return (F0<T>) F0;
    }

    /**
     * Convert a general {@link Func0} typed function to {@link F0} type
     *
     * @param f0
     *         a function of type {@link Func0} that returns type R value
     * @param <R>
     *         the generic type of the return value when applying function f0
     * @return a {@link F0} type that is equaivlent to function f0
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <R> F0<R> f0(final Func0<? extends R> f0) {
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
     * Define a function structure that accept one parameter. This interface is created to make it
     * easily migrate to Java 8 in the future
     *
     * @param <T>
     *         the type of input parameter
     * @param <U>
     *         the type of the return value when this function applied to the parameter(s)
     * @see Func0
     * @see Function
     * @see Func2
     * @see Func3
     * @see Func4
     * @see Func5
     * @see F1
     * @since 0.2
     */
    public interface Function<T, U> {

        /**
         * Apply this function to &lt;T&gt; type parameter.
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @param t
         *         the argument
         * @return {@code U} type result
         * @throws NotAppliedException
         *         if the function doesn't apply to the parameter(s)
         * @throws Break
         *         to short cut collecting operations (fold/reduce) on an {@link org.osgl.util.C.Traversable container}
         */
        U apply(T t) throws NotAppliedException, Break;

    }

    /**
     * Alias of {@link Function}
     *
     * @param <P1>
     *         the argument type
     * @param <R>
     *         the return value type
     * @since 0.2
     */
    public interface Func1<P1, R> extends Function<P1, R> {
    }

    /**
     * A {@link Function} function that support {@link #times(int)} operation
     *
     * @param <P1>
     *         the type of parameter the function applied to
     * @param <R>
     *         the type of return value of the function
     */
    public interface MultiplicableFunction<P1, R> extends Function<P1, R> {
        /**
         * Returns a function with {@code n} times factor specified. When the function
         * returned applied to a param, the effect is the same as apply this function
         * {@code n} times to the same param
         *
         * @param n
         *         specify the times factor
         * @return the new function
         */
        MultiplicableFunction<P1, R> times(int n);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Bijection">http://en.wikipedia.org/wiki/Bijection</a>. A
     * {@code Bijection} (mapping from {@code X} to {@code Y} is a special {@link Function} that has an
     * inverse function by itself also a {@code Bijection} mapping from {@code Y} to {@code X}
     *
     * @param <X>
     *         the type of parameter
     * @param <Y>
     *         the type of return value
     */
    public interface Bijection<X, Y> extends Function<X, Y> {
        /**
         * Returns the inverse function mapping from {@code Y} back to {@code X}
         *
         * @return a function that map {@code Y} type element to {@code X} type element
         */
        Bijection<Y, X> invert();
    }

    /**
     * Base implementation of {@link Function} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link Function} directly
     *
     * @since 0.2
     */
    public static abstract class F1<P1, R>
            extends FuncBase
            implements Func1<P1, R>, Bijection<P1, R>, MultiplicableFunction<P1, R> {

        @Override
        public Bijection<R, P1> invert() {
            throw new NotAppliedException();
        }

        @Override
        public MultiplicableFunction<P1, R> times(final int n) {
            E.illegalArgumentIf(n < 1);
            if (n == 1) return this;
            final F1<P1, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) throws NotAppliedException, Break {
                    R r = null;
                    for (int i = 0; i < n; ++i) {
                        r = me.apply(p1);
                    }
                    return r;
                }
            };
        }

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         *         the argument this function to be applied
         * @param fallback
         *         the function to be applied to the argument p1 when this function failed with any runtime exception
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, F1<? super P1, ? extends R> fallback) {
            try {
                return apply(p1);
            } catch (RuntimeException e) {
                return fallback.apply(p1);
            }
        }

        public final F0<R> curry(final P1 p1) {
            final Function<P1, R> me = this;
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
         * @param <T>
         *         the type of return value of the new composed function
         * @param after
         *         the function applies after this function is applied
         * @return the composed function
         * @throws NullPointerException
         *         if @{code after} is null
         */
        public <T> F1<P1, T> andThen(final Function<? super R, ? extends T> after) {
            E.NPE(after);
            final Function<P1, R> me = this;
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
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param afters
         *         a sequence of function to be applied after this function
         * @return a composed function
         */
        public F1<P1, R> andThen(final Function<? super P1, ? extends R>... afters) {
            if (0 == afters.length) {
                return this;
            }
            final F1<P1, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    R r = me.apply(p1);
                    for (Function<? super P1, ? extends R> f : afters) {
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
         * @param fallback
         *         the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F1<P1, R> orElse(final Function<? super P1, ? extends R> fallback) {
            final F1<P1, R> me = this;
            return new F1<P1, R>() {
                @Override
                public R apply(P1 p1) {
                    try {
                        return me.apply(p1);
                    } catch (RuntimeException e) {
                        return fallback.apply(p1);
                    }
                }
            };
        }

        /**
         * Returns an {@code F0&lt;R&gt;>} function by composing the specified {@code Func0&ltP1&gt;} function
         * with this function applied last
         *
         * @param before
         *         the function to be applied first when applying the return function
         * @return an new function such that f() == apply(f0())
         */
        public F0<R> compose(final Func0<? extends P1> before) {
            final F1<P1, R> me = this;
            return new F0<R>() {
                @Override
                public R apply() {
                    return me.apply(before.apply());
                }
            };
        }

        /**
         * Returns an {@code F1&lt;X1, R&gt;>} function by composing the specified
         * {@code Function&ltX1, P1&gt;} function with this function applied last
         *
         * @param before
         *         the function to be applied first when applying the return function
         * @param <X1>
         *         type of argument takes by the {@code before} function
         * @return an new function such that f(a) == apply(f1(a))
         */
        public <X1> F1<X1, R>
        compose(final Function<? super X1, ? extends P1> before) {
            final F1<P1, R> me = this;
            return new F1<X1, R>() {
                @Override
                public R apply(X1 x1) {
                    return me.apply(before.apply(x1));
                }
            };
        }

        /**
         * Returns an {@code F2&lt;X1, X2, R&gt;>} function by composing the specified
         * {@code Func2&ltX1, X2, P1&gt;} function with this function applied last
         *
         * @param <X1>
         *         the type of first param the new function applied to
         * @param <X2>
         *         the type of second param the new function applied to
         * @param before
         *         the function to be applied first when applying the return function
         * @return an new function such that f(x1, x2) == apply(f1(x1, x2))
         */
        public <X1, X2> F2<X1, X2, R>
        compose(final Func2<? super X1, ? super X2, ? extends P1> before) {
            final F1<P1, R> me = this;

            return new F2<X1, X2, R>() {
                @Override
                public R apply(X1 x1, X2 x2) {
                    return me.apply(before.apply(x1, x2));
                }
            };
        }

        /**
         * Returns an {@code F3&lt;X1, X2, X3, R&gt;>} function by composing the specified
         * {@code Func3&ltX1, X2, X3, P1&gt;} function with this function applied last
         *
         * @param <X1>
         *         the type of first param the new function applied to
         * @param <X2>
         *         the type of second param the new function applied to
         * @param <X3>
         *         the type of third param the new function applied to
         * @param before
         *         the function to be applied first when applying the return function
         * @return an new function such that f(x1, x2, x3) == apply(f1(x1, x2, x3))
         */
        public <X1, X2, X3> F3<X1, X2, X3, R>
        compose(final Func3<? super X1, ? super X2, ? super X3, ? extends P1> before) {
            final F1<P1, R> me = this;
            return new F3<X1, X2, X3, R>() {
                @Override
                public R apply(X1 x1, X2 x2, X3 x3) {
                    return me.apply(before.apply(x1, x2, x3));
                }
            };
        }

        /**
         * Returns an {@code F3&lt;X1, X2, X3, X4, R&gt;>} function by composing the specified
         * {@code Func3&ltX1, X2, X3, X4, P1&gt;} function with this function applied last
         *
         * @param <X1>
         *         the type of first param the new function applied to
         * @param <X2>
         *         the type of second param the new function applied to
         * @param <X3>
         *         the type of third param the new function applied to
         * @param <X4>
         *         the type of fourth param the new function applied to
         * @param before
         *         the function to be applied first when applying the return function
         * @return an new function such that f(x1, x2, x3, x4) == apply(f1(x1, x2, x3, x4))
         */
        public <X1, X2, X3, X4> F4<X1, X2, X3, X4, R>
        compose(final Func4<? super X1, ? super X2, ? super X3, ? super X4, ? extends P1> before) {
            final F1<P1, R> me = this;
            return new F4<X1, X2, X3, X4, R>() {
                @Override
                public R apply(X1 x1, X2 x2, X3 x3, X4 x4) {
                    return me.apply(before.apply(x1, x2, x3, x4));
                }
            };
        }

        /**
         * Returns an {@code F3&lt;X1, X2, X3, X4, X5, R&gt;>} function by composing the specified
         * {@code Func3&ltX1, X2, X3, X4, X5, P1&gt;} function with this function applied last
         *
         * @param <X1>
         *         the type of first param the new function applied to
         * @param <X2>
         *         the type of second param the new function applied to
         * @param <X3>
         *         the type of third param the new function applied to
         * @param <X4>
         *         the type of fourth param the new function applied to
         * @param <X5>
         *         the type of fifth param the new function applied to
         * @param before
         *         the function to be applied first when applying the return function
         * @return an new function such that f(x1, x2, x3, x4, x5) == apply(f1(x1, x2, x3, x4, x5))
         */
        public <X1, X2, X3, X4, X5> F5<X1, X2, X3, X4, X5, R>
        compose(final Func5<? super X1, ? super X2, ? super X3, ? super X4, ? super X5, ? extends P1> before) {
            final F1<P1, R> me = this;
            return new F5<X1, X2, X3, X4, X5, R>() {
                @Override
                public R apply(X1 x1, X2 x2, X3 x3, X4 x4, X5 x5) {
                    return me.apply(before.apply(x1, x2, x3, x4, x5));
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @return a function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F1<P1, Option<R>> lift() {
            final F1<P1, R> me = this;
            return new F1<P1, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1) {
                    try {
                        return some(me.apply(p1));
                    } catch (RuntimeException e) {
                        return none();
                    }
                }
            };
        }
    }

    private static class DumbF1 extends F1<Object, Object> implements Serializable {
        private static final long serialVersionUID = 2856835860951L;

        @Override
        public Object apply(Object o) throws NotAppliedException, Break {
            return null;
        }
    }

    /**
     * A dumb {@link Function} implementation that does nothing and return null
     *
     * @see #f1()
     * @since 0.2
     */
    public static final F1 F1 = new DumbF1();

    /**
     * The type-safe version of {@link #F1}
     *
     * @param <P1>
     *         the argument type
     * @param <R>
     *         the return value type
     * @return a dumb function {@link #F1}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, R> F1<P1, R> f1() {
        return (F1<P1, R>) F1;
    }


    /**
     * Convert a general {@link Function} function into a {@link F1} typed
     * function
     *
     * @param f1
     *         the function that consumes {@code P1} and produce {@code R}
     * @param <P1>
     *         the argument type
     * @param <R>
     *         the return value type
     * @return whatever of type {@code R}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, R> F1<P1, R> f1(final Function<? super P1, ? extends R> f1) {
        E.NPE(f1);
        if (f1 instanceof F1) {
            return (F1<P1, R>) f1;
        }
        return new F1<P1, R>() {
            @Override
            public R apply(P1 p1) {
                return f1.apply(p1);
            }

            @Override
            public MultiplicableFunction<P1, R> times(int n) {
                if (f1 instanceof MultiplicableFunction) {
                    return ((MultiplicableFunction<P1, R>) f1).times(n);
                }
                return super.times(n);
            }
        };
    }

    public static <X, Y> F1<X, Y> f1(final Bijection<X, Y> f1) {
        E.NPE(f1);
        if (f1 instanceof F1) {
            return (F1<X, Y>) f1;
        }
        return new F1<X, Y>() {
            @Override
            public Y apply(X p1) {
                return f1.apply(p1);
            }

            @Override
            public F1<Y, X> invert() {
                return f1(f1.invert());
            }

            @Override
            @SuppressWarnings("unchecked")
            public MultiplicableFunction<X, Y> times(int n) {
                if (f1 instanceof MultiplicableFunction) {
                    return ((MultiplicableFunction<X, Y>) f1).times(n);
                }
                return super.times(n);
            }
        };
    }

    /**
     * Define a function structure that accept two parameter
     *
     * @param <P1>
     *         the type of first parameter this function applied to
     * @param <P2>
     *         the type of second parameter this function applied to
     * @param <R>
     *         the type of the return value when this function applied to the parameter(s)
     * @see Func0
     * @see Function
     * @see Func3
     * @see Func4
     * @see Func5
     * @see F2
     * @since 0.2
     */
    public interface Func2<P1, P2, R> {

        /**
         * Apply the two params to the function
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @param p1
         *         the first argument of type P1
         * @param p2
         *         the second argument of type P2
         * @return the result of type R
         * @throws NotAppliedException
         *         if the function doesn't apply to the parameter(s)
         * @throws Break
         *         to short cut collecting operations (fold/reduce) on an {@link org.osgl.util.C.Traversable container}
         */
        R apply(P1 p1, P2 p2) throws NotAppliedException, Break;

    }

    /**
     * Base implementation of {@link Func2} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link Func2} directly
     *
     * @since 0.2
     */
    public static abstract class F2<P1, P2, R>
            extends FuncBase
            implements Func2<P1, P2, R> {

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         *         the first param with type P1
         * @param p2
         *         the second param with type P2
         * @param fallback
         *         the function to be called when an {@link RuntimeException} caught
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, P2 p2, F2<? super P1, ? super P2, ? extends R> fallback) {
            try {
                return apply(p1, p2);
            } catch (RuntimeException e) {
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
         * @param f
         *         the function takes the <code>R</code> type parameter and return <code>T</code>
         *         type result
         * @param <T>
         *         the return type of function {@code f}
         * @return the composed function
         * @throws NullPointerException
         *         if <code>f</code> is null
         */
        public <T> F2<P1, P2, T> andThen(final Function<? super R, ? extends T> f) {
            E.NPE(f);
            final Func2<P1, P2, R> me = this;
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
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs
         *         a sequence of function to be applied after this function
         * @return a composed function
         */
        public F2<P1, P2, R> andThen(final Func2<? super P1, ? super P2, ? extends R>... fs) {
            if (0 == fs.length) {
                return this;
            }
            final Func2<P1, P2, R> me = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    R r = me.apply(p1, p2);
                    for (Func2<? super P1, ? super P2, ? extends R> f : fs) {
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
         * @param fallback
         *         the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F2<P1, P2, R> orElse(final Func2<? super P1, ? super P2, ? extends R> fallback) {
            final F2<P1, P2, R> me = this;
            return new F2<P1, P2, R>() {
                @Override
                public R apply(P1 p1, P2 p2) {
                    try {
                        return me.apply(p1, p2);
                    } catch (RuntimeException e) {
                        return fallback.apply(p1, p2);
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @return a function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F2<P1, P2, Option<R>> lift() {
            final F2<P1, P2, R> me = this;
            return new F2<P1, P2, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1, P2 p2) {
                    try {
                        return some(me.apply(p1, p2));
                    } catch (RuntimeException e) {
                        return none();
                    }
                }
            };
        }
    }

    private static class DumbF2 extends F2<Object, Object, Object> implements Serializable {
        private static final long serialVersionUID = 2856835860952L;

        @Override
        public Object apply(Object o, Object o2) throws NotAppliedException, Break {
            return null;
        }
    }

    /**
     * A dumb {@link Func2} implementation that does nothing and return null
     *
     * @see #f2()
     * @since 0.2
     */
    public static final F2 F2 = new DumbF2();

    /**
     * The type-safe version of {@link #F2}
     *
     * @param <P1>
     *         the type of the first param the new function applied to
     * @param <P2>
     *         the type of the second param the new function applied to
     * @param <R>
     *         the type of new function application result
     * @return the dumb function {@link #F2}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, P2, R> F2<P1, P2, R> f2() {
        return (F2<P1, P2, R>) F2;
    }


    /**
     * Convert a general {@link Func2} function into a {@link F2} typed
     * function
     *
     * @param f2
     *         the function that takes two arguments and return type {@code R}
     * @param <P1>
     *         the type of the first param the new function applied to
     * @param <P2>
     *         the type of the second param the new function applied to
     * @param <R>
     *         the type of new function application result
     * @return a {@code F2} instance corresponding to the specified {@code Func2} instance
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, P2, R> F2<P1, P2, R> f2(final Func2<? super P1, ? super P2, ? extends R> f2) {
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
     * @param <P1>
     *         the type of first parameter this function applied to
     * @param <P2>
     *         the type of second parameter this function applied to
     * @param <P3>
     *         the type of thrid parameter this function applied to
     * @param <R>
     *         the type of the return value when this function applied to the parameter(s)
     * @see Func0
     * @see Function
     * @see Func2
     * @see Func4
     * @see Func5
     * @since 0.2
     */
    public interface Func3<P1, P2, P3, R> {
        /**
         * Run the function with parameters specified.
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @param p1
         *         argument 1
         * @param p2
         *         argument 2
         * @param p3
         *         argument 3
         * @return the result of function applied
         * @throws NotAppliedException
         *         if the function doesn't apply to the parameter(s)
         * @throws Break
         *         to short cut collecting operations (fold/reduce) on an {@link org.osgl.util.C.Traversable container}
         */
        R apply(P1 p1, P2 p2, P3 p3) throws NotAppliedException, Break;
    }

    /**
     * Base implementation of {@link Func3} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link Func3} directly
     *
     * @since 0.2
     */
    public static abstract class F3<P1, P2, P3, R>
            extends FuncBase
            implements Func3<P1, P2, P3, R> {

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         *         the first argument
         * @param p2
         *         the second argument
         * @param p3
         *         the third argument
         * @param fallback
         *         the function to be called of application of this function failed with any runtime exception
         * @return the result of this function or the fallback function application
         */
        public R applyOrElse(P1 p1, P2 p2, P3 p3, F3<? super P1, ? super P2, ? super P3, ? extends R> fallback) {
            try {
                return apply(p1, p2, p3);
            } catch (RuntimeException e) {
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
         * @param f
         *         the function takes the <code>R</code> type parameter and return <code>T</code>
         *         type result
         * @param <T>
         *         the return type of function {@code f}
         * @return the composed function
         * @throws NullPointerException
         *         if <code>f</code> is null
         */
        public <T> Func3<P1, P2, P3, T> andThen(final Function<? super R, ? extends T> f) {
            E.NPE(f);
            final Func3<P1, P2, P3, R> me = this;
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
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs
         *         a sequence of function to be applied after this function
         * @return a composed function
         */
        public Func3<P1, P2, P3, R> andThen(
                final Func3<? super P1, ? super P2, ? super P3, ? extends R>... fs
        ) {
            if (0 == fs.length) {
                return this;
            }

            final Func3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3) {
                    R r = me.apply(p1, p2, p3);
                    for (Func3<? super P1, ? super P2, ? super P3, ? extends R> f : fs) {
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
         * @param fallback
         *         the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F3<P1, P2, P3, R> orElse(final Func3<? super P1, ? super P2, ? super P3, ? extends R> fallback) {
            final F3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3) {
                    try {
                        return me.apply(p1, p2, p3);
                    } catch (RuntimeException e) {
                        return fallback.apply(p1, p2, p3);
                    }
                }
            };
        }

        /**
         * Turns this partial function into a plain function returning an Option result.
         *
         * @return a function that takes an argument x to Some(this.apply(x)) if this can be applied, and to None otherwise.
         */
        public F3<P1, P2, P3, Option<R>> lift() {
            final F3<P1, P2, P3, R> me = this;
            return new F3<P1, P2, P3, Option<R>>() {
                @Override
                public Option<R> apply(P1 p1, P2 p2, P3 p3) {
                    try {
                        return some(me.apply(p1, p2, p3));
                    } catch (RuntimeException e) {
                        return none();
                    }
                }
            };
        }
    }


    private static class DumbF3 extends F3<Object, Object, Object, Object> implements Serializable {
        private static final long serialVersionUID = 2856835860953L;

        @Override
        public Object apply(Object o, Object o2, Object o3) throws NotAppliedException, Break {
            return null;
        }
    }

    /**
     * A dumb {@link Func3} implementation that does nothing and return null
     *
     * @see #f3()
     * @since 0.2
     */
    public static final F3 F3 = new DumbF3();

    /**
     * The type-safe version of {@link #F3}
     *
     * @param <P1>
     *         the type of first parameter this function applied to
     * @param <P2>
     *         the type of second parameter this function applied to
     * @param <P3>
     *         the type of thrid parameter this function applied to
     * @param <R>
     *         the type of the return value when this function applied to the parameter(s)
     * @return the dumb function {@link #F3}
     * @since 0.2
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static <P1, P2, P3, R> F3<P1, P2, P3, R> f3() {
        return F3;
    }


    /**
     * Convert a general {@link Func3} function into a {@link F3} typed
     * function
     *
     * @param f3
     *         the general function with three params
     * @param <P1>
     *         type of argument 1
     * @param <P2>
     *         type of argument 2
     * @param <P3>
     *         type of argument 3
     * @param <R>
     *         return type
     * @return the {@link #F3} typed instance which is equivalent to f3
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, P2, P3, R> F3<P1, P2, P3, R> f3(final Func3<? super P1, ? super P2, ? super P3, ? extends R> f3
    ) {
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
     * @param <P1>
     *         the type of first parameter this function applied to
     * @param <P2>
     *         the type of second parameter this function applied to
     * @param <P3>
     *         the type of thrid parameter this function applied to
     * @param <P4>
     *         the type of fourth parameter this function applied to
     * @param <R>
     *         the type of the return value when this function applied to the parameter(s)
     * @see Func0
     * @see Function
     * @see Func2
     * @see Func4
     * @see Func5
     * @since 0.2
     */
    public interface Func4<P1, P2, P3, P4, R> {
        /**
         * Run the function with parameters specified.
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @param p1
         *         the first argument
         * @param p2
         *         the second argument
         * @param p3
         *         the third argument
         * @param p4
         *         the fourth argument
         * @return whatever value of type {@code R}
         * @throws NotAppliedException
         *         if the function doesn't apply to the parameter(s)
         * @throws Break
         *         to short cut collecting operations (fold/reduce) on an {@link org.osgl.util.C.Traversable container}
         */
        R apply(P1 p1, P2 p2, P3 p3, P4 p4) throws NotAppliedException, Break;

    }

    /**
     * Base implementation of {@link Func4} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link Func4} directly
     *
     * @since 0.2
     */
    public static abstract class F4<P1, P2, P3, P4, R>
            extends FuncBase
            implements Func4<P1, P2, P3, P4, R> {

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         *         the first argument
         * @param p2
         *         the second argument
         * @param p3
         *         the third argument
         * @param p4
         *         the fourth argument
         * @param fallback
         *         the failover function to be called if application of this function failed with any
         *         runtime exception
         * @return a composite function that apply to this function first and if failed apply to the callback function
         */
        public R applyOrElse(P1 p1, P2 p2, P3 p3, P4 p4,
                             F4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fallback
        ) {
            try {
                return apply(p1, p2, p3, p4);
            } catch (RuntimeException e) {
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
         * @param f
         *         the function takes the <code>R</code> type parameter and return <code>T</code>
         *         type result
         * @param <T>
         *         the return type of function {@code f}
         * @return the composed function
         * @throws NullPointerException
         *         if <code>f</code> is null
         */
        public <T> F4<P1, P2, P3, P4, T> andThen(final Function<? super R, ? extends T> f) {
            E.NPE(f);
            final Func4<P1, P2, P3, P4, R> me = this;
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
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs
         *         a sequence of function to be applied after this function
         * @return a composed function
         */
        public F4<P1, P2, P3, P4, R> andThen(
                final Func4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R>... fs
        ) {
            if (0 == fs.length) {
                return this;
            }

            final Func4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    R r = me.apply(p1, p2, p3, p4);
                    for (Func4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> f : fs) {
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
         * @param fallback
         *         the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F4<P1, P2, P3, P4, R> orElse(
                final Func4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> fallback
        ) {
            final F4<P1, P2, P3, P4, R> me = this;
            return new F4<P1, P2, P3, P4, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        return me.apply(p1, p2, p3, p4);
                    } catch (RuntimeException e) {
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
                        return some(me.apply(p1, p2, p3, p4));
                    } catch (RuntimeException e) {
                        return none();
                    }
                }
            };
        }

    }

    private static class DumbF4 extends F4<Object, Object, Object, Object, Object> implements Serializable {
        private static final long serialVersionUID = 2856835860954L;

        @Override
        public Object apply(Object o, Object o2, Object o3, Object o4) throws NotAppliedException, Break {
            return null;
        }
    }

    /**
     * A dumb {@link Func4} implementation that does nothing and return null
     *
     * @see #f4()
     * @since 0.2
     */
    public static final F4 F4 = new DumbF4();

    /**
     * The type-safe version of {@link #F4}
     *
     * @param <P1>
     *         type of first argument
     * @param <P2>
     *         type of second argument
     * @param <P3>
     *         type of third argument
     * @param <P4>
     *         type of fourth argument
     * @param <R>
     *         type of return value
     * @return the dumb {@link #F4} function
     * @since 0.2
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static <P1, P2, P3, P4, R> F4<P1, P2, P3, P4, R> f4() {
        return F4;
    }


    /**
     * Convert a general {@link Func4} function into a {@link F4} typed
     * function
     *
     * @param f4
     *         the function to be converted
     * @param <P1>
     *         type of first argument
     * @param <P2>
     *         type of second argument
     * @param <P3>
     *         type of third argument
     * @param <P4>
     *         type of fourth argument
     * @param <R>
     *         type of return value
     * @return the function of {@link F4} type that is equivalent to function {@code f4}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, P2, P3, P4, R> F4<P1, P2, P3, P4, R> f4(
            final Func4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> f4
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
     * @param <P1>
     *         the type of first parameter this function applied to
     * @param <P2>
     *         the type of second parameter this function applied to
     * @param <P3>
     *         the type of thrid parameter this function applied to
     * @param <P4>
     *         the type of fourth parameter this function applied to
     * @param <P5>
     *         the type of fifth parameter this function applied to
     * @param <R>
     *         the type of the return value when this function applied to the parameter(s)
     * @see Func0
     * @see Function
     * @see Func2
     * @see Func3
     * @see Func5
     * @since 0.2
     */
    public interface Func5<P1, P2, P3, P4, P5, R> {
        /**
         * Run the function with parameters specified.
         * <p>In case implementing a partial function, it can throw out an
         * {@link NotAppliedException} if the function is not defined for
         * the given parameter(s)</p>
         *
         * @param p1
         *         first argument
         * @param p2
         *         second argument
         * @param p3
         *         third argument
         * @param p4
         *         fourth argument
         * @param p5
         *         fifth argument
         * @return whatever with type {@code R}
         * @throws NotAppliedException
         *         if the function doesn't apply to the parameter(s)
         * @throws Break
         *         to short cut collecting operations (fold/reduce) on an {@link org.osgl.util.C.Traversable container}
         */
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws NotAppliedException, Break;
    }

    /**
     * Base implementation of {@link Func5} function. User application should
     * (nearly) always make their implementation extend to this base class
     * instead of implement {@link Func5} directly
     *
     * @since 0.2
     */
    public static abstract class F5<P1, P2, P3, P4, P5, R>
            extends FuncBase
            implements Func5<P1, P2, P3, P4, P5, R> {

        /**
         * Applies this partial function to the given argument when it is contained in the function domain.
         * Applies fallback function where this partial function is not defined.
         *
         * @param p1
         *         the first argument
         * @param p2
         *         the second argument
         * @param p3
         *         the third argument
         * @param p4
         *         the fourth argument
         * @param p5
         *         the fifth argument
         * @param fallback
         *         the function to be called if application of this function failed with any runtime exception
         * @return a composite function apply to this function and then the callback function if this function failed
         */
        public R applyOrElse(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5,
                             F5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> fallback
        ) {
            try {
                return apply(p1, p2, p3, p4, p5);
            } catch (RuntimeException e) {
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
         * @param f
         *         the function takes the <code>R</code> type parameter and return <code>T</code>
         *         type result
         * @param <T>
         *         the return type of function {@code f}
         * @return the composed function
         * @throws NullPointerException
         *         if <code>f</code> is null
         */
        public <T> F5<P1, P2, P3, P4, P5, T> andThen(final Function<? super R, ? extends T> f) {
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
         * <p>When apply the composed function, the result of the last function
         * is returned</p>
         *
         * @param fs
         *         a sequence of function to be applied after this function
         * @return a composed function
         */
        public F5<P1, P2, P3, P4, P5, R> andThen(
                final Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R>... fs
        ) {
            if (0 == fs.length) {
                return this;
            }

            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    R r = me.apply(p1, p2, p3, p4, p5);
                    for (Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> f : fs) {
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
         * @param fallback
         *         the function to applied if this function doesn't apply to the parameter(s)
         * @return the composed function
         */
        public F5<P1, P2, P3, P4, P5, R> orElse(
                final Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> fallback
        ) {
            final F5<P1, P2, P3, P4, P5, R> me = this;
            return new F5<P1, P2, P3, P4, P5, R>() {
                @Override
                public R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        return me.apply(p1, p2, p3, p4, p5);
                    } catch (RuntimeException e) {
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
                        return some(me.apply(p1, p2, p3, p4, p5));
                    } catch (RuntimeException e) {
                        return none();
                    }
                }
            };
        }

    }

    private static class DumbF5 extends F5<Object, Object, Object, Object, Object, Object> implements Serializable {
        private static final long serialVersionUID = 2856835860955L;

        @Override
        public Object apply(Object o, Object o2, Object o3, Object o4, Object o5) throws NotAppliedException, Break {
            return null;
        }
    }

    /**
     * A dumb {@link Func5} implementation that does nothing and return null
     *
     * @see #f5()
     * @since 0.2
     */
    public static final F5 F5 = new DumbF5();

    /**
     * The type-safe version of {@link #F5}
     *
     * @param <P1>
     *         type of first argument
     * @param <P2>
     *         type of second argument
     * @param <P3>
     *         type of third argument
     * @param <P4>
     *         type of fourth argument
     * @param <P5>
     *         type of fifth argument
     * @param <R>
     *         type of return value
     * @return a dumb {@link #F5} function
     * @since 0.2
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static <P1, P2, P3, P4, P5, R> F5<P1, P2, P3, P4, P5, R> f5() {
        return F5;
    }


    /**
     * Convert a general {@link Func5} function into a {@link F5} typed
     * function
     *
     * @param f5
     *         the function to be converted
     * @param <P1>
     *         type of first argument
     * @param <P2>
     *         type of second argument
     * @param <P3>
     *         type of third argument
     * @param <P4>
     *         type of fourth argument
     * @param <P5>
     *         type of fifth argument
     * @param <R>
     *         type of return value
     * @return the function of {@link F5} type that is equivalent to function {@code f5}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <P1, P2, P3, P4, P5, R> F5<P1, P2, P3, P4, P5, R> f5(
            final Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> f5
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

    // Define common used Function classes including Predicate and Visitor

    /**
     * Adapt JDK Comparator (since 1.2) to Functional programming. The class provides several java8 Comparator methods
     *
     * @param <T>
     *         the type of the element to be compared
     * @since 0.2
     */
    public static abstract class Comparator<T>
            extends F2<T, T, Integer>
            implements java.util.Comparator<T>, Serializable {

        @Override
        public Integer apply(T p1, T p2) throws NotAppliedException, Break {
            return compare(p1, p2);
        }

        /**
         * See <a href="http://download.java.net/jdk8/docs/api/java/util/Comparator.html#reversed()">Java 8 doc</a>
         *
         * @since 0.2
         */
        public Comparator<T> reversed() {
            if (this instanceof Reversed) {
                return ((Reversed<T>) this).cmp;
            }
            return new Reversed<T>(this);
        }

        /**
         * See <a href="http://download.java.net/jdk8/docs/api/java/util/Comparator.html#thenComparing(java.util.Comparator)">Java 8 doc</a>
         *
         * @since 0.2
         */
        public Comparator<T> thenComparing(final java.util.Comparator<? super T> other) {
            final Comparator<T> me = this;
            return new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    int ret = (me.compare(o1, o2));
                    return (0 == ret) ? other.compare(o1, o2) : ret;
                }
            };
        }

        /**
         * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#thenComparing-java.util.function.Function-java.util.Comparator-">Java 8 doc</a>
         *
         * @param keyExtractor
         *         The function to extract the key for comparison
         * @param keyComparator
         *         The function to compare the extracted key
         * @param <U>
         *         the generic type of the key
         * @return a function that extract key of type {@code U} from element of type {@code T}
         * and run {@code keyComparator} to compare the two keys
         * @since 0.2
         */
        public <U extends Comparable<? super U>> Comparator<T> thenComparing(
                Function<? super T, ? extends U> keyExtractor,
                java.util.Comparator<? super U> keyComparator
        ) {
            return thenComparing(F.comparing(keyExtractor, keyComparator));
        }

        /**
         * See <a href="http://download.java.net/jdk8/docs/api/java/util/Comparator.html#thenComparing(java.util.function.Function)">Java 8 doc</a>
         *
         * @param keyExtractor
         *         the function that extract key of type U from instance of type T
         * @param <U>
         *         the key type
         * @return a comparator that applied if the result of this comparator is even
         */
        public <U extends Comparable<? super U>> Comparator<T> thenComparing(
                Function<? super T, ? extends U> keyExtractor
        ) {
            return thenComparing(F.comparing(keyExtractor));
        }


        private static class NaturalOrder extends Comparator<Comparable<Object>> implements Serializable {

            private static final long serialVersionUID = -2658673713566062292L;

            static final NaturalOrder INSTANCE = new NaturalOrder();

            @Override
            public int compare(Comparable<Object> c1, Comparable<Object> c2) {
                if (null == c1) return -1;
                if (null == c2) return 1;
                return c1.compareTo(c2);
            }

            private Object readResolve() {
                return INSTANCE;
            }

            @Override
            public Comparator<Comparable<Object>> reversed() {
                return ReverseOrder.INSTANCE;
            }
        }

        private static class ReverseOrder extends Comparator<Comparable<Object>> implements Serializable {

            private static final long serialVersionUID = -8026361379173504545L;

            static final ReverseOrder INSTANCE = new ReverseOrder();

            @Override
            public int compare(Comparable<Object> c1, Comparable<Object> c2) {
                return c2.compareTo(c1);
            }

            private Object readResolve() {
                return INSTANCE;
            }

            @Override
            public Comparator<Comparable<Object>> reversed() {
                return NaturalOrder.INSTANCE;
            }
        }

        private static class Reversed<T> extends Comparator<T> {

            private static final long serialVersionUID = -8555952576466749416L;
            final Comparator<T> cmp;

            Reversed(Comparator<T> cmp) {
                E.NPE(cmp);
                this.cmp = cmp;
            }

            @Override
            public int compare(T t1, T t2) {
                return cmp.compare(t2, t1);
            }

            public boolean equals(Object o) {
                return (o == this) ||
                        (o instanceof Reversed &&
                                cmp.equals(((Reversed) o).cmp));
            }

            public int hashCode() {
                return cmp.hashCode() ^ Integer.MIN_VALUE;
            }

        }

    }

    /**
     * Adapt a general {@link Func2} function with (T, T, Integer) type into {@link Comparator}
     *
     * @param f
     *         The function takes two params (the same type) and returns integer
     * @param <T>
     *         the type of the parameter
     * @return a {@link Comparator} instance backed by function {@code f}
     */
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> comparator(final Func2<? super T, ? super T, Integer> f) {
        E.NPE(f);
        if (f instanceof Comparator) {
            return (Comparator<T>) f;
        }
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return f.apply(o1, o2);
            }
        };
    }

    /**
     * Adapt a jdk {@link java.util.Comparator} into {@link Comparator osgl Comparator}
     *
     * @param <T>
     *         the element type the comparator compares
     * @param c
     *         the jdk compator
     * @return a {@link Comparator} instance backed by comparator {@code c}
     */
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> comparator(final java.util.Comparator<? super T> c) {
        E.NPE(c);
        if (c instanceof Comparator) {
            return (Comparator<T>) c;
        }
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return c.compare(o1, o2);
            }
        };
    }

    /**
     * <code>Predicate</code> is a predefined <code>Function&lt;Boolean, T&gt;</code> typed
     * function with a set of utilities dealing with boolean operations. This is often known
     * as <a href="http://en.wikipedia.org/wiki/Predicate_(mathematical_logic)">Predicate</a>
     * <p>Note in user application, it should NOT assume an argument is of <code>Predicate</code>
     * typed function, instead the argument should always be declared as <code>Function&lt;Boolean T&gt;</code>:</p>
     * <pre>
     *     // bad way
     *     void foo(Predicate&lt;MyData&gt; predicate) {
     *         ...
     *     }
     *     // good way
     *     void foo(Function&lt;Boolean, MyData&gt; predicate) {
     *         Predicate&lt;MyData&gt; p = predicate(predicate);
     *         ...
     *     }
     * </pre>
     *
     * @since 0.2
     */
    public static abstract class Predicate<T> extends F1<T, Boolean> {
        @Override
        public final Boolean apply(T t) {
            return test(t);
        }

        /**
         * Sub class to implement this method to test on the supplied elements
         *
         * @param t
         *         the element to be test
         * @return {@code true} or {@code false} depends on the implementation
         */
        public abstract boolean test(T t);

        /**
         * Returns a negate function of this
         *
         * @return the negate function
         */
        public Predicate<T> negate() {
            return F.negate(this);
        }

        /**
         * Return an <code>Predicate</code> predicate from a list of <code>Function&lt;Boolean, T&gt;</code>
         * with AND operation. For any <code>T t</code> to be tested, if any specified predicates
         * must returns <code>false</code> on it, the resulting predicate will return <code>false</code>.
         *
         * @param predicates
         *         the predicate function array
         * @return a function that returns {@code true} only when all functions in {@code predicates} returns
         * {@code true} on a given argument
         * @since 0.2
         */
        public Predicate<T> and(final Function<? super T, Boolean>... predicates) {
            final Predicate<T> me = this;
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    if (!me.test(t)) {
                        return false;
                    }
                    for (Function<? super T, Boolean> f : predicates) {
                        if (!f.apply(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }

        /**
         * Return an <code>Predicate</code> predicate from a list of <code>Function&lt;Boolean, T&gt;</code>
         * with OR operation. For any <code>T t</code> to be tested, if any specified predicates
         * must returns <code>true</code> on it, the resulting predicate will return <code>true</code>.
         *
         * @param predicates
         *         the predicate functions
         * @return a function returns {@code true} if any one of the predicate functions returns
         * {@code true} on a given argument
         * @since 0.2
         */
        public Predicate<T> or(final Function<? super T, Boolean>... predicates) {
            final Predicate<T> me = this;
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    if (me.test(t)) {
                        return true;
                    }
                    for (Function<? super T, Boolean> f : predicates) {
                        if (f.apply(t)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        public <R> F1<T, Option<R>> ifThen(final Function<? super T, R> func) {
            return new F1<T, Option<R>>() {
                @Override
                public Option<R> apply(T t) throws NotAppliedException, Break {
                    if (test(t)) {
                        return some(func.apply(t));
                    }
                    return none();
                }
            };
        }

        public <R> F1<T, Option<R>> elseThen(final Function<? super T, R> func) {
            return negate().ifThen(func);
        }

        public static <T> Predicate<T> negate(final Function<T, Boolean> predicate) {
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    return !predicate.apply(t);
                }
            };
        }
    }

    /**
     * Convert a <code>Function&lt;T, Boolean&gt;</code> typed function to
     * {@link Predicate Predicate&lt;T&gt;} function.
     * <p>If the function specified is already a {@link Predicate}, then
     * the function itself is returned</p>
     *
     * @param f
     *         the function to be converted
     * @param <T>
     *         the argument type
     * @return a function of {@link Predicate} type that is equivalent to function {@code f}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> predicate(final Function<? super T, Boolean> f) {
        if (f instanceof Predicate) {
            return (Predicate<T>) f;
        }
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return f.apply(t);
            }
        };
    }

    /**
     * Convert a general <code>Function&lt;T, ?&gt;</code> typed function to
     * {@link Predicate Predicate&lt;T&gt;} function. When the predicate function
     * returned apply to a param, it will first apply the specified {@code f1} to the
     * param, and they call {@link #bool(java.lang.Object)} to evaluate the boolean
     * value of the return object of the application.
     * <p>If the function specified is already a {@link Predicate}, then
     * the function itself is returned</p>
     *
     * @param f
     *         the function
     * @param <T>
     *         the argument type
     * @return the function of {@link Predicate} type that is equivalent to the function {@code f}
     * @since 0.2
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> Predicate<T> generalPredicate(final Visitor<? super T> f) {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return bool(f.apply(t));
            }
        };
    }

    /**
     * Define a processor function which applied to one parameter and return the parameter instance after
     * processing on the parameter
     *
     * @param <T>
     *         the paramete type
     */
    public abstract static class Processor<T> extends F1<T, T> {

        @Override
        public T apply(T t) throws NotAppliedException, Break {
            process(t);
            return t;
        }

        /**
         * Subclass must override thie method to process the parameter
         *
         * @param t
         *         the object to be processed
         * @throws Break
         *         if logic decide it shall break external loop
         * @throws NotAppliedException
         *         if logic decide to skip further processing
         *         on the object passed in
         */
        public abstract void process(T t) throws Break, NotAppliedException;
    }

    /**
     * Define a visitor (known as Consumer in java 8) function which applied to one parameter and without return type
     *
     * @param <T>
     *         the type of the parameter the visitor function applied to
     */
    public abstract static class Visitor<T> extends F1<T, Void> {

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
         * @param t
         *         the element been visited
         * @throws Break
         *         if the logic decide to break visit progress (usually when visiting a sequence of elements)
         */
        public abstract void visit(T t) throws Break;
    }

    public abstract static class V1<P1> extends Visitor<P1> {
    }

    @SuppressWarnings("unused")
    public abstract static class V2<P1, P2> extends F2<P1, P2, Void> {
        @Override
        public final Void apply(P1 p1, P2 p2) throws NotAppliedException, Break {
            visit(p1, p2);
            return null;
        }

        public abstract void visit(P1 p1, P2 p2);
    }

    @SuppressWarnings("unused")
    public abstract static class V3<P1, P2, P3> extends F3<P1, P2, P3, Void> {
        @Override
        public final Void apply(P1 p1, P2 p2, P3 p3) throws NotAppliedException, Break {
            visit(p1, p2, p3);
            return null;
        }

        public abstract void visit(P1 p1, P2 p2, P3 p3);
    }

    @SuppressWarnings("unused")
    public abstract static class V4<P1, P2, P3, P4> extends F4<P1, P2, P3, P4, Void> {
        @Override
        public final Void apply(P1 p1, P2 p2, P3 p3, P4 p4) throws NotAppliedException, Break {
            visit(p1, p2, p3, p4);
            return null;
        }

        public abstract void visit(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    @SuppressWarnings("unused")
    public abstract static class V5<P1, P2, P3, P4, P5> extends F5<P1, P2, P3, P4, P5, Void> {
        @Override
        public final Void apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws NotAppliedException, Break {
            visit(p1, p2, p3, p4, p5);
            return null;
        }

        public abstract void visit(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    /**
     *
     */
    /**
     * Convert a {@code Function&lt;? super T, Void&gt;} function into a {@link Visitor}
     *
     * @param f
     *         the function to be cast
     * @param <T>
     *         the argument type
     * @return a {@link Visitor} type function that is equal with the function {@code f}
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public static <T> Visitor<T> visitor(final Function<? super T, ?> f) {
        if (f instanceof Visitor) {
            return (Visitor<T>) f;
        }
        return new Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                f.apply(t);
            }
        };
    }

    /**
     * Return a composed visitor function that only applies when the guard predicate test returns <code>true</code>
     *
     * @param predicate
     *         the predicate to test the element been visited
     * @param visitor
     *         the function that visit(accept) the element if the guard tested the element successfully
     * @param <T>
     *         the type of the element be tested and visited
     * @return the composed function
     */
    public static <T> Visitor<T>
    guardedVisitor(final Function<? super T, Boolean> predicate, final Visitor<? super T> visitor) {
        return new Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                if (predicate.apply(t)) {
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
         *         the payload object
         */
        public Break(Object payload) {
            super("break out");
            this.payload = payload;
        }

        /**
         * Return the payload
         *
         * @param <T>
         *         the type of the return value
         * @return the payload
         */
        public <T> T get() {
            return cast(payload);
        }
    }


    /**
     * A utility method to throw out a Break with payload
     *
     * @param e
     *         the payload object
     * @param <T>
     *         the type of the payload
     * @return a Break instance with the payload specified
     */
    @SuppressWarnings("unused")
    public static <T> Break breakOut(T e) {
        throw new Break(e);
    }

    /**
     * A predefined Break instance without payload
     */
    @SuppressWarnings("unused")
    public static final Break BREAK = new Break();

    /**
     * <p>An {@code IndexedVisitor} provide a tool to iterate through a Map or indexed list
     * by passing the key/index and the value to the function.</p>
     * <p>Use {@code IndexedVisitor} to iterate through a {@code List}</p>
     * <pre>
     *     C.List&lt;Student&gt; students = ...
     *     students.each(new IndexedVisitor() {
     *         {@literal @}Override
     *         public void visit(int id, Student student) {
     *             System.out.printf("ID: %s, Name: %s", id, student.getName());
     *         }
     *     })
     * </pre>
     * <p>Use {@code IndexedVisitor} to iterate through a {@code Map}</p>
     *
     * @param <K>
     *         the generic type of the key
     * @param <T>
     *         the generic type of the value
     */
    public static abstract class IndexedVisitor<K, T> extends F2<K, T, Void> {

        protected Map<String, ?> attr = new HashMap<String, Object>();
        protected T any;
        protected String aStr;
        protected boolean aBool;
        protected int anInt;
        protected float aFloat;
        protected double aDouble;
        protected byte aByte;
        protected char aChar;
        protected long aLong;

        public IndexedVisitor() {
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(Map<String, ?> map) {
            attr = new HashMap<String, Object>(map);
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(T t) {
            any = t;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(String s) {
            aStr = s;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(boolean b) {
            aBool = b;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(int i) {
            anInt = i;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(long l) {
            aLong = l;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(double d) {
            aDouble = d;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(float f) {
            aFloat = f;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(byte b) {
            aByte = b;
        }

        @SuppressWarnings("unused")
        public IndexedVisitor(char c) {
            aChar = c;
        }

        public T get() {
            return any;
        }

        public String getStr() {
            return aStr;
        }

        public boolean getBoolean() {
            return aBool;
        }

        public int getInt() {
            return anInt;
        }

        public long getLong() {
            return aLong;
        }

        public float getFloat() {
            return aFloat;
        }

        public double getDouble() {
            return aDouble;
        }

        public char getChar() {
            return aChar;
        }

        public byte getByte() {
            return aByte;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) attr.get(key);
        }

        @Override
        public final Void apply(K id, T t) {
            visit(id, t);
            return null;
        }

        public abstract void visit(K id, T t);
    }

    @SuppressWarnings("unused")
    public static <K, T> IndexedVisitor<K, T>
    indexGuardedVisitor(final Function<? super K, Boolean> guard,
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
     * A Provider is a function that apply to nothing and return an element
     *
     * @param <ELEMENT>
     *         the type of the returning element
     */
    public static abstract class Provider<ELEMENT> extends F0<ELEMENT> {
        @Override
        public final ELEMENT apply() throws NotAppliedException, Break {

            return get();
        }

        /**
         * Get the element
         *
         * @return the element the provider provides
         */
        public abstract ELEMENT get();
    }

    /**
     * A Producer is a provider that {@link Producer#produce()} product
     *
     * @param <PRODUCT>
     *         the generic type of the produce the producer produces
     */
    public static abstract class Producer<PRODUCT> extends Provider<PRODUCT> {

        @Override
        public final PRODUCT get() {
            return produce();
        }

        public abstract PRODUCT produce();
    }

    /**
     * A Transformer is literally a kind of {@link F1} function
     *
     * @param <FROM>
     *         The type of the element the transformer function applied to
     * @param <TO>
     *         The type of the result of transform of <code>&lt;FROM&gt;</code>
     */
    public static abstract class Transformer<FROM, TO> extends F1<FROM, TO> {
        @Override
        public final TO apply(FROM from) {
            return transform(from);
        }

        /**
         * The place sub class to implement the transform logic
         *
         * @param from
         *         the element to be transformed
         * @return the transformed object
         */
        public abstract TO transform(FROM from);

        public static <F, T> Transformer<F, T> adapt(final Function<F, T> func) {
            return new Transformer<F, T>() {
                @Override
                public T transform(F f) {
                    return func.apply(f);
                }
            };
        }
    }

    /**
     * A `TypeConverter` is a {@link Transformer} that convert a type to another
     *
     * @param <FROM>
     *         the generic type of input
     * @param <TO>
     *         the generic type of output
     */
    @SuppressWarnings("unused")
    public static abstract class TypeConverter<FROM, TO> extends Transformer<FROM, TO> {

        /**
         * When this hint is used, it forces enum converter to do exact name
         * matching rather than keyword pattern matching.
         */
        public static final Object HINT_STRICT = new Object();

        /**
         * This hint is no longer used. Please refer to {@link #HINT_STRICT}
         */
        @Deprecated
        public static final Object HINT_CASE_INSENSITIVE = new Object();

        public Class<FROM> fromType;
        public Class<TO> toType;

        public TypeConverter(Class<FROM> fromType, Class<TO> toType) {
            this.fromType = fromType;
            this.toType = toType;
        }

        public int hops() {
            return 1;
        }

        protected TypeConverter() {
            this(true);
        }

        protected TypeConverter(boolean exploreType) {
            if (exploreType) {
                exploreTypes();
            }
        }

        private void exploreTypes() {
            List<Type> types = Generics.typeParamImplementations(getClass(), TypeConverter.class);
            int sz = types.size();
            E.illegalArgumentIf(sz < 2, "expected at least two type parameters");
            fromType = Generics.classOf(types.get(0));
            toType = Generics.classOf(types.get(1));
        }

        protected Charset charsetHint(Object hint) {
            Charset charset = StandardCharsets.UTF_8;
            if (hint instanceof Charset) {
                charset = (Charset) hint;
            } else if (hint instanceof String) {
                try {
                    charset = Charset.forName(S.string(hint));
                } catch (Exception e) {
                    throw E.unexpected("Unknown charset: " + hint);
                }
            } else if (null != hint) {
                throw E.unexpected("Unknown charset: " + hint);
            }
            return charset;
        }


        @Override
        public final TO transform(FROM from) {
            return convert(from);
        }

        @Override
        public String toString() {
            return fromType.getName() + " -> " + toType.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeConverter<?, ?> that = (TypeConverter<?, ?>) o;

            if (fromType != null ? !fromType.equals(that.fromType) : that.fromType != null) return false;
            return toType != null ? toType.equals(that.toType) : that.toType == null;
        }

        @Override
        public int hashCode() {
            int result = fromType != null ? fromType.hashCode() : 0;
            result = 31 * result + (toType != null ? toType.hashCode() : 0);
            return result;
        }

        /**
         * Convert value into `TO` type
         *
         * @param from
         *         the from value
         * @return the converted value
         */
        public abstract TO convert(FROM from);

        /**
         * Convert value into `TO` type with hint
         *
         * @param from
         *         the from value
         * @param hint
         *         the convert hint (e.g. date format)
         * @return the converted value
         */
        public TO convert(FROM from, Object hint) {
            return convert(from);
        }

        public static TypeConverter<Object, String> ANY_TO_STRING = new TypeConverter<Object, String>(Object.class, String.class) {
            @Override
            public String convert(Object o) {
                return S.string(o);
            }
        };

        public static TypeConverter<?, Boolean> ANY_TO_BOOLEAN = new TypeConverter<Object, Boolean>(Object.class, Boolean.class) {
            @Override
            public Boolean convert(Object o) {
                return bool(o);
            }
        };

        public static TypeConverter<String, Byte> STRING_TO_BYTE = new TypeConverter<String, Byte>(String.class, Byte.class) {
            @Override
            public Byte convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                return Byte.valueOf(s);
            }
        };

        public static TypeConverter<String, Character> STRING_TO_CHARACTER = new TypeConverter<String, Character>(String.class, Character.class) {
            @Override
            public Character convert(String s) {
                return S.empty(s) ? null : s.charAt(0);
            }
        };

        public static TypeConverter<String, Short> STRING_TO_SHORT = new TypeConverter<String, Short>(String.class, Short.class) {
            @Override
            public Short convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                return Short.valueOf(s);
            }
        };

        public static TypeConverter<String, Integer> STRING_TO_INTEGER = new TypeConverter<String, Integer>(String.class, Integer.class) {
            @Override
            public Integer convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                if (N.isInt(s)) {
                    return Integer.valueOf(s);
                }
                // try parse 10 * 60 style
                if (s.contains("*")) {
                    List<String> factors = S.fastSplit(s, "*");
                    int result = 1;
                    for (String factor: factors) {
                        int l = Integer.parseInt(factor.trim());
                        result = result * l;
                    }
                    return result;
                }
                throw new NumberFormatException(s);
            }

            @Override
            public Integer convert(String s, Object hint) {
                if (S.isEmpty(s)) {
                    return null;
                }
                if (hint instanceof Integer) {
                    return Integer.valueOf(s, (Integer) hint);
                }
                return convert(s);
            }
        };

        public static TypeConverter<String, Float> STRING_TO_FLOAT = new TypeConverter<String, Float>(String.class, Float.class) {
            @Override
            public Float convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                return Float.valueOf(s);
            }
        };

        public static TypeConverter<String, Long> STRING_TO_LONG = new TypeConverter<String, Long>(String.class, Long.class) {
            @Override
            public Long convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                if (N.isInt(s)) {
                    return Long.valueOf(s);
                }
                // try parse 10 * 60 style
                if (s.contains("*")) {
                    List<String> factors = S.fastSplit(s, "*");
                    long result = 1;
                    for (String factor: factors) {
                        long l = Long.parseLong(factor.trim());
                        result = result * l;
                    }
                    return result;
                }
                throw new NumberFormatException(s);
            }
        };

        public static TypeConverter<String, Double> STRING_TO_DOUBLE = new TypeConverter<String, Double>(String.class, Double.class) {
            @Override
            public Double convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                return Double.valueOf(s);
            }
        };

        public static TypeConverter<String, BigInteger> STRING_TO_BIG_INT = new TypeConverter<String, BigInteger>(String.class, BigInteger.class) {
            @Override
            public BigInteger convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                return new BigInteger(s);
            }
        };

        public static TypeConverter<String, BigDecimal>
                STRING_TO_BIG_DEC = new TypeConverter<String, BigDecimal>(String.class, BigDecimal.class) {
            @Override
            public BigDecimal convert(String s) {
                if (S.isEmpty(s)) {
                    return null;
                }
                return new BigDecimal(s);
            }
        };

        public static TypeConverter<String, Date> STRING_TO_DATE = new TypeConverter<String, Date>(String.class, Date.class) {
            private SimpleDateFormat format = new SimpleDateFormat();

            @Override
            public Date convert(String s) {
                return convert(s, format);
            }

            @Override
            public Date convert(String s, Object hint) {
                if (S.isEmpty(s)) {
                    return null;
                }
                if (null == hint) {
                    return convert(s);
                }
                SimpleDateFormat format = new SimpleDateFormat(hint.toString());
                return convert(s, format);
            }

            private Date convert(String s, DateFormat format) {
                if (S.isEmpty(s)) {
                    return null;
                }
                try {
                    return format.parse(s);
                } catch (ParseException e) {
                    throw E.unexpected(e);
                }
            }
        };

        public static TypeConverter<String, Class> STRING_TO_CLASS = new TypeConverter<String, Class>() {
            @Override
            public Class convert(String s) {
                return classForName(s);
            }

            @Override
            public Class convert(String s, Object hint) {
                if (hint instanceof ClassLoader) {
                    ClassLoader cl = (ClassLoader) hint;
                    return classForName(s, cl);
                }
                return super.convert(s, hint);
            }
        };

        public static TypeConverter<String, JSONObject> STRING_TO_JSON_OBJECT = new TypeConverter<String, JSONObject>() {
            @Override
            public JSONObject convert(String s) {
                return JSON.parseObject(s);
            }
        };

        public static TypeConverter<String, JSONArray> STRING_TO_JSON_ARRAY = new TypeConverter<String, JSONArray>() {
            @Override
            public JSONArray convert(String s) {
                return JSON.parseArray(s);
            }
        };

        public static TypeConverter<String, Reader> STRING_TO_READER = new TypeConverter<String, Reader>() {
            @Override
            public Reader convert(String s) {
                return new StringReader(s);
            }
        };

        public static final TypeConverter<String, Document> STRING_TO_XML_DOCUMENT = XML.STRING_TO_XML_DOCUMENT;

        public static final TypeConverter<InputStream, Document> IS_TO_XML_DOCUMENT = XML.IS_TO_XML_DOCUMENT;

        public static final TypeConverter<Document, String> XML_DOCUMENT_TO_STRING = XML.XML_DOCUMENT_TO_STRING;

        public static final TypeConverter<Document, JSONObject> XML_DOCUMENT_TO_JSON = new XmlDocumentToJsonObject();

        public static final TypeConverter<JSONObject, Document> JSON_TO_XML_DOCUMENT = new JsonObjectToXmlDocument();

        public static TypeConverter<Iterator, Iterable> ITERATOR_TO_ITERABLE = new TypeConverter<Iterator, Iterable>() {
            @Override
            public Iterable convert(final Iterator iterator) {
                return new Iterable() {
                    @Override
                    public Iterator iterator() {
                        return iterator;
                    }
                };
            }
        };

        public static TypeConverter<Iterable, Iterator> ITERABLE_TO_ITERATOR = new TypeConverter<Iterable, Iterator>() {
            @Override
            public Iterator convert(Iterable iterable) {
                return iterable.iterator();
            }
        };

        public static TypeConverter<Iterable, List> ITERABLE_TO_LIST = new TypeConverter<Iterable, List>() {
            @Override
            public List convert(Iterable iterable) {
                return iterable instanceof List ? (List) iterable : C.list(iterable);
            }
        };

        public static TypeConverter<Iterable, Set> ITERABLE_TO_SET = new TypeConverter<Iterable, Set>() {
            @Override
            public Set convert(Iterable iterable) {
                return iterable instanceof Set ? (Set) iterable : C.set(iterable);
            }
        };

        public static TypeConverter<Enumeration, Iterator> ENUMERATION_TO_ITERATOR = new TypeConverter<Enumeration, Iterator>() {
            @Override
            public Iterator convert(Enumeration enumeration) {
                return new EnumerationIterator(enumeration);
            }
        };

        public static TypeConverter<Iterator, Enumeration> ITERATOR_TO_ENUMERATION = new TypeConverter<Iterator, Enumeration>() {
            @Override
            public Enumeration convert(Iterator iterator) {
                return new IteratorEnumeration(iterator);
            }
        };

        public static TypeConverter<Reader, String> READER_TO_STRING = new TypeConverter<Reader, String>() {
            @Override
            public String convert(Reader reader) {
                return IO.read(reader).toString();
            }
        };

        public static TypeConverter<Date, String> DATE_TO_STRING = new TypeConverter<Date, String>(Date.class, String.class) {
            private SimpleDateFormat format = new SimpleDateFormat();

            @Override
            public String convert(Date date) {
                return convert(date, format);
            }

            @Override
            public String convert(Date date, Object hint) {
                if (null == hint) {
                    return convert(date);
                }
                SimpleDateFormat format = new SimpleDateFormat(hint.toString());
                return convert(date, format);
            }

            private String convert(Date date, DateFormat format) {
                return format.format(date);
            }
        };

        public static TypeConverter<Date, Long> DATE_TO_LONG = new TypeConverter<Date, Long>() {
            @Override
            public Long convert(Date date) {
                return date.getTime();
            }
        };

        public static TypeConverter<Long, Date> LONG_TO_DATE = new TypeConverter<Long, Date>() {
            @Override
            public Date convert(Long time) {
                return null == time ? null : new Date(time);
            }
        };

        public static TypeConverter<Date, java.sql.Date> DATE_TO_SQL_DATE = new TypeConverter<Date, java.sql.Date>() {
            @Override
            public java.sql.Date convert(Date date) {
                return new java.sql.Date(date.getTime());
            }
        };

        public static TypeConverter<Date, java.sql.Time> DATE_TO_SQL_TIME = new TypeConverter<Date, java.sql.Time>() {
            @Override
            public java.sql.Time convert(Date date) {
                return new java.sql.Time(date.getTime());
            }
        };

        public static TypeConverter<Date, java.sql.Timestamp> DATE_TO_SQL_TIMESTAMP = new TypeConverter<Date, java.sql.Timestamp>() {
            @Override
            public java.sql.Timestamp convert(Date date) {
                return new java.sql.Timestamp(date.getTime());
            }
        };

        public static TypeConverter<Date, Calendar> DATE_TO_CALENDAR = new TypeConverter<Date, Calendar>() {
            @Override
            public Calendar convert(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTime());
                return calendar;
            }
        };

        public static TypeConverter<Calendar, Date> CALENDAR_TO_DATE = new TypeConverter<Calendar, Date>() {
            @Override
            public Date convert(Calendar calendar) {
                return new Date(calendar.getTimeInMillis());
            }
        };

        public static <ENUM extends Enum<ENUM>> TypeConverter<String, ENUM> stringToEnum(final Class<ENUM> enumClass) {
            return new TypeConverter<String, ENUM>(String.class, enumClass) {
                @Override
                public ENUM convert(String s) {
                    return convert(s, null);
                }

                @Override
                public ENUM convert(String s, Object hint) {
                    if (HINT_STRICT == hint) {
                        return Enum.valueOf(enumClass, s);
                    }
                    Enum[] enums = enumClass.getEnumConstants();
                    for (Enum e : enums) {
                        if (Keyword.of(e.name()).equals(Keyword.of(s))) {
                            return (ENUM) e;
                        }
                    }
                    throw new IllegalArgumentException("No enum constant " + enumClass.getCanonicalName() + "." + s);
                }
            };
        }


        public static TypeConverter<ByteBuffer, byte[]> BYTEBUFFER_TO_BYTEARRAY = new TypeConverter<ByteBuffer, byte[]>() {
            @Override
            public byte[] convert(ByteBuffer byteBuffer) {
                ByteBuffer copy = byteBuffer.duplicate();
                int length = copy.remaining();
                byte[] retVal = new byte[length];
                copy.get(retVal, 0, length);
                return retVal;
            }
        };

        public static TypeConverter<byte[], ByteBuffer> BYTEARRAY_TO_BYTEBUFFER = new TypeConverter<byte[], ByteBuffer>() {
            @Override
            public ByteBuffer convert(byte[] bytes) {
                return ByteBuffer.wrap(bytes);
            }
        };

        public static TypeConverter<byte[], String> BYTEARRAY_TO_STRING = new TypeConverter<byte[], String>() {
            @Override
            public String convert(byte[] bytes) {
                return new String(bytes, StandardCharsets.UTF_8);
            }

            @Override
            public String convert(byte[] bytes, Object hint) {
                if (null == hint) {
                    return convert(bytes);
                }
                if (hint instanceof Charset) {
                    return new String(bytes, ((Charset) hint));
                } else if (hint instanceof String) {
                    return new String(bytes, Charset.forName(S.string(hint)));
                }
                return convert(bytes);
            }
        };

        public static TypeConverter<String, byte[]> STRING_TO_BYTEARRAY = new TypeConverter<String, byte[]>() {
            @Override
            public byte[] convert(String s) {
                return s.getBytes(StandardCharsets.UTF_8);
            }
        };

        public static TypeConverter<Number, Byte> NUM_TO_BYTE = new TypeConverter<Number, Byte>(Number.class, Byte.class) {
            @Override
            public Byte convert(Number number) {
                return number.byteValue();
            }
        };

        public static TypeConverter<Number, Short> NUM_TO_SHORT = new TypeConverter<Number, Short>(Number.class, Short.class) {
            @Override
            public Short convert(Number number) {
                return number.shortValue();
            }
        };

        public static TypeConverter<Number, Integer> NUM_TO_INT = new TypeConverter<Number, Integer>(Number.class, Integer.class) {
            @Override
            public Integer convert(Number number) {
                return number.intValue();
            }
        };

        public static TypeConverter<Number, Float> NUM_TO_FLOAT = new TypeConverter<Number, Float>(Number.class, Float.class) {
            @Override
            public Float convert(Number number) {
                return number.floatValue();
            }
        };

        public static TypeConverter<Number, Long> NUM_TO_LONG = new TypeConverter<Number, Long>(Number.class, Long.class) {
            @Override
            public Long convert(Number number) {
                return number.longValue();
            }
        };

        public static TypeConverter<Number, Double> NUM_TO_DOUBLE = new TypeConverter<Number, Double>(Number.class, Double.class) {
            @Override
            public Double convert(Number number) {
                return number.doubleValue();
            }
        };

        public static TypeConverter<Number, BigInteger> NUM_TO_BIG_INT = new TypeConverter<Number, BigInteger>(Number.class, BigInteger.class) {
            @Override
            public BigInteger convert(Number number) {
                if (number instanceof BigInteger) {
                    return (BigInteger) number;
                }
                if (number instanceof BigDecimal) {
                    return ((BigDecimal) number).toBigInteger();
                }
                return BigInteger.valueOf(number.longValue());
            }
        };

        public static TypeConverter<Number, BigDecimal> NUM_TO_BIG_DEC = new TypeConverter<Number, BigDecimal>(Number.class, BigDecimal.class) {
            @Override
            public BigDecimal convert(Number number) {
                if (number instanceof BigDecimal) {
                    return (BigDecimal) number;
                }
                if (number instanceof BigInteger) {
                    return new BigDecimal((BigInteger) number);
                }
                return BigDecimal.valueOf(number.doubleValue());
            }
        };


        public static TypeConverter<CharSequence, char[]> CHAR_SEQUENCE_TO_CHAR_ARRAY = new TypeConverter<CharSequence, char[]>() {
            @Override
            public char[] convert(CharSequence charSequence) {
                return charSequence.toString().toCharArray();
            }
        };

        public static TypeConverter<char[], CharSequence> CHAR_ARRAY_TO_CHAR_SEQUENCE = new TypeConverter<char[], CharSequence>() {
            @Override
            public CharSequence convert(final char[] chars) {
                return convert(chars, 0, chars.length);
            }

            private CharSequence convert(final char[] chars, final int start, final int end) {
                E.NPE(chars);
                if (start < 0)
                    throw new StringIndexOutOfBoundsException(start);
                if (end > chars.length)
                    throw new StringIndexOutOfBoundsException(end);
                if (start > end)
                    throw new StringIndexOutOfBoundsException(end - start);
                final int length = end - start;
                final int start0 = start;
                final int end0 = end;
                return new CharSequence() {
                    @Override
                    public int length() {
                        return length;
                    }

                    @Override
                    public char charAt(int index) {
                        return chars[start + index];
                    }

                    @Override
                    public CharSequence subSequence(int start, int end) {
                        return convert(chars, start0 + start, start0 + end);
                    }

                    @Override
                    public String toString() {
                        char[] ca = new char[length];
                        System.arraycopy(chars, start, ca, 0, length);
                        return new String(ca);
                    }
                };
            }
        };

        public static TypeConverter<String, Reader> STRING_TO_RADER = new TypeConverter<String, Reader>() {
            @Override
            public Reader convert(String s) {
                return new StringReader(s);
            }
        };

        public static TypeConverter<File, InputStream> FILE_TO_INPUTSTREAM = new TypeConverter<File, InputStream>() {
            @Override
            public InputStream convert(File file) {
                return IO.inputStream(file);
            }
        };

        public static TypeConverter<File, Reader> FILE_TO_READER = new TypeConverter<File, Reader>() {
            @Override
            public Reader convert(File file) {
                return IO.reader(file);
            }
        };

        public static TypeConverter<byte[], InputStream> BYTES_TO_INPUT_STREAM = new TypeConverter<byte[], InputStream>() {
            @Override
            public InputStream convert(byte[] bytes) {
                return new ByteArrayInputStream(bytes);
            }
        };

        public static TypeConverter<InputStream, byte[]> INPUT_STREAM_TO_BYTES = new TypeConverter<InputStream, byte[]>() {
            @Override
            public byte[] convert(InputStream inputStream) {
                return IO.read(inputStream).toByteArray();
            }
        };

        public static TypeConverter<InputStream, Reader> INPUT_STREAM_TO_READER = new TypeConverter<InputStream, Reader>() {
            @Override
            public Reader convert(InputStream inputStream) {
                return convert(inputStream, StandardCharsets.UTF_8);
            }

            @Override
            public Reader convert(InputStream inputStream, Object hint) {
                return new InputStreamReader(inputStream, charsetHint(hint));
            }
        };

        public static TypeConverter<Reader, InputStream> READER_TO_INPUT_STREAM = new TypeConverter<Reader, InputStream>() {
            @Override
            public InputStream convert(Reader reader) {
                return convert(reader, StandardCharsets.UTF_8);
            }

            @Override
            public InputStream convert(Reader reader, Object hint) {
                return new ReaderInputStream(reader, charsetHint(hint));
            }
        };

        public static TypeConverter<OutputStream, Writer> OUTPUT_STREAM_TO_WRITER = new TypeConverter<OutputStream, Writer>() {
            @Override
            public Writer convert(OutputStream outputStream) {
                return convert(outputStream, StandardCharsets.UTF_8);
            }

            @Override
            public Writer convert(OutputStream outputStream, Object hint) {
                return new OutputStreamWriter(outputStream, charsetHint(hint));
            }
        };

        public static TypeConverter<Writer, OutputStream> WRITER_TO_OUTPUT_STREAM = new TypeConverter<Writer, OutputStream>() {
            @Override
            public OutputStream convert(Writer reader) {
                return convert(reader, StandardCharsets.UTF_8);
            }

            @Override
            public OutputStream convert(Writer reader, Object hint) {
                return new WriterOutputStream(reader, charsetHint(hint));
            }
        };

        public static TypeConverter<URL, InputStream> URL_TO_INPUT_STREAM = new TypeConverter<URL, InputStream>() {
            @Override
            public InputStream convert(URL url) {
                return IO.inputStream(url);
            }
        };

        public static TypeConverter<URI, URL> URI_TO_URL = new TypeConverter<URI, URL>() {
            @Override
            public URL convert(URI uri) {
                try {
                    return uri.toURL();
                } catch (MalformedURLException e) {
                    throw E.unexpected(e);
                }
            }
        };

        public static TypeConverter<File, URL> FILE_TO_URL = new TypeConverter<File, URL>() {
            @Override
            public URL convert(File file) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw E.unexpected(e);
                }
            }
        };

        public static TypeConverter<Appendable, Writer> APPENDABLE_TO_WRITER = new TypeConverter<Appendable, Writer>() {
            @Override
            public Writer convert(final Appendable appendable) {
                return new Writer() {
                    @Override
                    public void write(char[] cbuf, int off, int len) throws IOException {
                        appendable.append($.convert(cbuf).to(CharSequence.class), off, len);
                    }

                    @Override
                    public void flush() {
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        };

        public static TypeConverter<Appendable, Output> APPENDABLE_TO_OUTPUT = new TypeConverter<Appendable, Output>() {
            @Override
            public Output convert(Appendable appendable) {
                return Output.Adaptors.of(appendable);
            }
        };

        public static TypeConverter<Output, Writer> OUTPUT_TO_WRITER = new TypeConverter<Output, Writer>() {
            @Override
            public Writer convert(Output output) {
                return output.asWriter();
            }
        };

        public static TypeConverter<Writer, Output> WRITER_TO_OUTPUT = new TypeConverter<Writer, Output>() {
            @Override
            public Output convert(Writer writer) {
                return Output.Adaptors.of(writer);
            }
        };

        public static TypeConverter<BufferedImage, byte[]> BUFFERED_IMG_TO_OUTPUTSTREAM = new TypeConverter<BufferedImage, byte[]>() {
            @Override
            public byte[] convert(BufferedImage bufferedImage) {
                return convert(bufferedImage, null);
            }

            @Override
            public byte[] convert(BufferedImage bufferedImage, Object hint) {
                String contentType = "image/png";
                if (null != hint) {
                    contentType = hint.toString();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IO.write(bufferedImage, contentType).to(baos);
                return baos.toByteArray();
            }
        };

    }

    public static class _ConvertStage<FROM> {
        private FROM from;
        private Object defVal;
        private Object hint;
        private boolean reportError;
        private Class<?> fromType;
        private TypeConverterRegistry converterRegistry = TypeConverterRegistry.INSTANCE;

        private _ConvertStage(FROM from) {
            this.from = from;
            this.fromType = null == from ? Void.class : from.getClass();
        }

        public _ConvertStage<FROM> defaultTo(Object defVal) {
            this.defVal = requireNotNull(defVal);
            return this;
        }

        public _ConvertStage<FROM> hint(Object hint) {
            this.hint = hint;
            return this;
        }

        public _ConvertStage<FROM> reportError() {
            reportError = true;
            return this;
        }

        public _ConvertStage<FROM> strictMatching() {
            return hint(TypeConverter.HINT_STRICT);
        }

        public _ConvertStage<FROM> customTypeConverters(TypeConverterRegistry typeConverterRegistry) {
            this.converterRegistry = $.requireNotNull(typeConverterRegistry);
            return this;
        }

        public <TO> TO to(Class<TO> toType) {
            if (null == from) {
                return null != defVal ? (TO) defVal : isPrimitiveType(toType) ? primitiveDefaultValue(toType) : null;
            }
            if (fromType == toType || toType.isAssignableFrom(fromType)) {
                return cast(from);
            }
            TypeConverter<FROM, TO> converter = cast(converterRegistry.get(fromType, toType));
            if (null == converter) {
                if (Enum.class.isAssignableFrom(toType)) {
                    TypeConverter<String, Enum> enumConverter = TypeConverter.stringToEnum((Class<Enum>) toType);
                    return (TO) enumConverter.convert(TypeConverter.ANY_TO_STRING.convert(from), hint);
                } else if (fromType.isArray()) {
                    if (Iterable.class.isAssignableFrom(toType)) {
                        Iterable iterable = new Iterable() {
                            @Override
                            public Iterator iterator() {
                                return new ArrayObjectIterator(from);
                            }
                        };
                        return $.convert(iterable).to(toType);
                    } else if (Iterator.class.isAssignableFrom(toType)) {
                        Iterator iterator = new ArrayObjectIterator(from);
                        return $.convert(iterator).to(toType);
                    } else if (toType.isArray()) {
                        Class<?> fromComponentType = fromType.getComponentType();
                        Class<?> toComponentType = toType.getComponentType();
                        final TypeConverter componentConverter = converterRegistry.get(fromComponentType, toComponentType);
                        if (null != componentConverter) {
                            int len = Array.getLength(from);
                            Object toArray = Array.newInstance(toComponentType, len);
                            for (int i = 0; i < len; ++i) {
                                Array.set(toArray, i, componentConverter.convert(Array.get(from, i)));
                            }
                            return (TO) toArray;
                        }
                    }
                }
                if (null != defVal) {
                    return (TO) defVal;
                } else {
                    if (reportError) {
                        throw new IllegalArgumentException(S.fmt("Unable to find converter from %s to %s", fromType, toType));
                    }
                    return null;
                }
            }

            TO to = null == hint ? converter.convert(from) : converter.convert(from, hint);
            return null == to ? (TO) defVal : to;
        }

        public boolean toBool() {
            return to(boolean.class);
        }

        public boolean toBooleanPrimitive() {
            return toBool();
        }

        public Boolean toBoolean() {
            return to(Boolean.class);
        }

        public char toChar() {
            return to(char.class);
        }

        public char toCharacterPrimitive() {
            return toChar();
        }

        public Character toCharacter() {
            return to(Character.class);
        }

        public byte toBytePrimitive() {
            return to(byte.class);
        }

        public Byte toByte() {
            return to(Byte.class);
        }

        public byte[] toByteArray() {
            return to(byte[].class);
        }

        public ByteBuffer toByteBuffer() {
            return to(ByteBuffer.class);
        }

        public short toShortPrimitive() {
            return to(short.class);
        }

        public Short toShort() {
            return to(Short.class);
        }

        public int toIntegerPrimitive() {
            return toInt();
        }

        public Integer toInteger() {
            return to(Integer.class);
        }

        public int toInt() {
            return defaultTo(0).toInteger();
        }

        public float toFloatPrimitive() {
            return to(float.class);
        }

        public Float toFloat() {
            return to(Float.class);
        }

        public long toLongPrimitive() {
            return to(long.class);
        }

        public Long toLong() {
            return to(Long.class);
        }

        public double toDoublePrimitive() {
            return to(double.class);
        }

        public Double toDouble() {
            return to(Double.class);
        }

        public Date toDate() {
            return to(Date.class);
        }

        public String toString() {
            return to(String.class);
        }

        public <NEW_FROM> _ConvertStage<NEW_FROM> pipeline(Class<NEW_FROM> newFromClass) {
            return new _ConvertStage<>(to(newFromClass));
        }

    }

    public static <FROM> _ConvertStage<FROM> convert(FROM from) {
        return new _ConvertStage(from);
    }

    @SuppressWarnings("unused")
    public static abstract class Operator<T> extends F1<T, T> {
        @Override
        public abstract Operator<T> invert();

        @Override
        public abstract Operator<T> times(int n);
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
                return eq(that._1, _1) && eq(that._2, _2);
            }
            return false;
        }

        public final A left() {
            return _1;
        }

        public final B right() {
            return _2;
        }

        public A first() {
            return _1;
        }

        public B second() {
            return _2;
        }

        public B last() {
            return _2;
        }

        public T2<A, B> set1(A a) {
            return T2(a, _2);
        }

        public T2<A, B> set2(B b) {
            return T2(_1, b);
        }

        @Override
        public int hashCode() {
            return hc(_1, _2);
        }

        @Override
        public String toString() {
            return "T2(_1: " + _1 + ", _2: " + _2 + ")";
        }

        public org.osgl.util.C.List toList() {
            return org.osgl.util.C.list(_1, _2);
        }

        /**
         * Convert this {@code Tuple} instance into a Map with one key,value pair. Where
         * {@code key} is {@code _1} and {@code value} is {@code _2};
         *
         * @return the Map as described
         */
        @SuppressWarnings("unused")
        public Map<A, B> toMap() {
            Map<A, B> m = new HashMap<A, B>();
            m.put(_1, _2);
            return m;
        }

        /**
         * Convert a list of {@code Tuple} instances into a Map. Where
         * {@code key} is {@code _1} and {@code value} is {@code _2};
         * <p>
         * <b>Note</b> that the size of the returned Map might be lesser than
         * the size of the tuple list if there are multiple {@code _1} has
         * the same value, and the last one is the winner and it's {@code _2}
         * will be put into the Map
         * </p>
         *
         * @param <K>
         *         the key type
         * @param <V>
         *         the value type
         * @param list
         *         the list of tuples to be transformed into Map
         * @return the Map as described
         */
        @SuppressWarnings("unused")
        public static <K, V> Map<K, V> toMap(Collection<Tuple<K, V>> list) {
            Map<K, V> m = C.newMap();
            for (Tuple<K, V> t : list) {
                m.put(t._1, t._2);
            }
            return m;
        }
    }

    /**
     * Alias of {@link Tuple}
     *
     * @param <LEFT>
     *         the left side element type
     * @param <RIGHT>
     *         the right side element type
     */
    public static class Binary<LEFT, RIGHT> extends Tuple<LEFT, RIGHT> {
        public Binary(LEFT _1, RIGHT _2) {
            super(_1, _2);
        }
    }

    @SuppressWarnings("unused")
    public static <P1, P2> Binary<P1, P2> Tuple(P1 a, P2 b) {
        return new T2<>(a, b);
    }


    /**
     * Alias of {@link Binary}
     *
     * @param <LEFT>
     *         the left hand side element type
     * @param <RIGHT>
     *         the right hand side element type
     */
    public static class Pair<LEFT, RIGHT> extends Binary<LEFT, RIGHT> {
        public Pair(LEFT _1, RIGHT _2) {
            super(_1, _2);
        }
    }


    /**
     * Alias of {@link Pair}
     *
     * @param <A>
     *         the left hand side element type
     * @param <B>
     *         the right hand side element type
     */
    public static class T2<A, B> extends Pair<A, B> {

        public T2(A _1, B _2) {
            super(_1, _2);
        }

    }

    public static <A, B> T2<A, B> T2(A a, B b) {
        return new T2<>(a, b);
    }

    public static <A, B> T2<A, B> Binary(A a, B b) {
        return T2(a, b);
    }

    public static <A, B> T2<A, B> Pair(A a, B b) {
        return T2(a, b);
    }

    /**
     * A tuple with three elements
     *
     * @param <A>
     *         the first element type
     * @param <B>
     *         the second element type
     * @param <C>
     *         the third element type
     */
    public static class Triple<A, B, C> {
        final public A _1;
        final public B _2;
        final public C _3;

        public Triple(A _1, B _2, C _3) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
        }

        public A first() {
            return _1;
        }

        public B second() {
            return _2;
        }

        public C third() {
            return _3;
        }

        public C last() {
            return _3;
        }

        public T3<A, B, C> set1(A a) {
            return T3(a, _2, _3);
        }

        public T3<A, B, C> set2(B b) {
            return T3(_1, b, _3);
        }

        public T3<A, B, C> set3(C c) {
            return T3(_1, _2, c);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Triple) {
                Triple that = (Triple) o;
                return eq(that._1, _1) && eq(that._2, _2) && eq(that._3, _3);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hc(_1, _2, _3);
        }

        @Override
        public String toString() {
            return "T3(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ")";
        }
    }

    public static class T3<A, B, C> extends Triple<A, B, C> {
        public T3(A _1, B _2, C _3) {
            super(_1, _2, _3);
        }
    }

    public static <A, B, C> T3<A, B, C> T3(A a, B b, C c) {
        return new T3<A, B, C>(a, b, c);
    }

    public static class Quadruple<A, B, C, D> {

        final public A _1;
        final public B _2;
        final public C _3;
        final public D _4;

        public Quadruple(A _1, B _2, C _3, D _4) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
        }

        public A first() {
            return _1;
        }

        public B second() {
            return _2;
        }

        public C third() {
            return _3;
        }

        public D forth() {
            return _4;
        }

        public D last() {
            return _4;
        }

        public T4<A, B, C, D> set1(A x) {
            return T4(x, _2, _3, _4);
        }

        public T4<A, B, C, D> set2(B x) {
            return T4(_1, x, _3, _4);
        }

        public T4<A, B, C, D> set3(C x) {
            return T4(_1, _2, x, _4);
        }

        public T4<A, B, C, D> set4(D x) {
            return T4(_1, _2, _3, x);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Quadruple) {
                Quadruple that = (Quadruple) o;
                return eq(that._1, _1) && eq(that._2, _2) && eq(that._3, _3) && eq(that._4, _4);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hc(_1, _2, _3, _4);
        }

        @Override
        public String toString() {
            return "T4(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ")";
        }
    }

    public static class T4<A, B, C, D> extends Quadruple<A, B, C, D> {
        public T4(A _1, B _2, C _3, D _4) {
            super(_1, _2, _3, _4);
        }
    }

    @SuppressWarnings("unchecked")
    public static <A, B, C, D> T4<A, B, C, D> T4(A a, B b, C c, D d) {
        return new T4<A, B, C, D>(a, b, c, d);
    }

    public static class Quintuple<A, B, C, D, E> {

        final public A _1;
        final public B _2;
        final public C _3;
        final public D _4;
        final public E _5;

        public Quintuple(A _1, B _2, C _3, D _4, E _5) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
            this._5 = _5;
        }

        public A first() {
            return _1;
        }

        public B second() {
            return _2;
        }

        public C third() {
            return _3;
        }

        public D forth() {
            return _4;
        }

        public E fifth() {
            return _5;
        }

        public E last() {
            return _5;
        }

        public T5<A, B, C, D, E> set1(A x) {
            return T5(x, _2, _3, _4, _5);
        }

        public T5<A, B, C, D, E> set2(B x) {
            return T5(_1, x, _3, _4, _5);
        }

        public T5<A, B, C, D, E> set3(C x) {
            return T5(_1, _2, x, _4, _5);
        }

        public T5<A, B, C, D, E> set4(D x) {
            return T5(_1, _2, _3, x, _5);
        }

        public T5<A, B, C, D, E> set5(E x) {
            return T5(_1, _2, _3, _4, x);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Quintuple) {
                Quintuple that = (Quintuple) o;
                return eq(that._1, _1) && eq(that._2, _2) && eq(that._3, _3) && eq(that._4, _4) && eq(that._5, _5);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hc(_1, _2, _3, _4, _5);
        }

        @Override
        public String toString() {
            return "T5(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ", _5:" + _5 + ")";
        }
    }

    public static class T5<A, B, C, D, E> extends Quintuple<A, B, C, D, E> {
        public T5(A _1, B _2, C _3, D _4, E _5) {
            super(_1, _2, _3, _4, _5);
        }
    }

    @SuppressWarnings("unused")
    public static <A, B, C, D, E> T5<A, B, C, D, E> T5(A a, B b, C c, D d, E e) {
        return new T5<A, B, C, D, E>(a, b, c, d, e);
    }

    /**
     * Defines an option of element {@code T}. This class can be used to implement
     * the {@code else-if} semantic in functional programming and eliminate the
     * {@code null} value
     *
     * @param <T>
     *         the element type
     */
    public static abstract class Option<T> implements Iterable<T>, Serializable {

        private Option() {
        }

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
         * @return {@code true} if this option is not defined
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
         * @throws NoSuchElementException
         *         if this {@code Option} is {@link #NONE}
         */
        public abstract T get() throws NoSuchElementException;

        /**
         * If a value is present, and the value matches the given predicate,
         * return an {@code Option} describing the value, otherwise return
         * {@link #NONE}.
         *
         * @param predicate
         *         the function to test the value held by this {@code Option}
         * @return an {@code Option} describing the value of this {@code Option} if
         * a value is present and the value matches the given predicate,
         * otherwise {@link #NONE}
         */
        public final Option<T> filter(Function<? super T, Boolean> predicate) {
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
         * @param mapper
         *         a mapping function to apply to the value, if present
         * @param <B>
         *         The type of the result of the mapping function
         * @return an Optional describing the result of applying a mapping
         * function to the value of this {@code Option}, if a value is
         * present, otherwise {@link #NONE}
         * @throws NullPointerException
         *         if the mapper function is {@code null}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public final <B> Option<B> map(final Function<? super T, ? extends B> mapper) {
            return isDefined() ? of(mapper.apply(get())) : NONE;
        }

        /**
         * If a value is present, apply the provided {@code Option}-bearing
         * mapping function to it, return that result, otherwise return
         * {@link #NONE}. This method is similar to {@link #map(Function)},
         * but the provided mapper is one whose result is already an
         * {@code Option}, and if invoked, {@code flatMap} does not wrap it
         * with an additional {@code Option}.
         *
         * @param <B>
         *         The type parameter to the {@code Option} returned by
         * @param mapper
         *         a mapping function to apply to the value,
         * @return the result of applying an {@code Option}-bearing mapping
         * function to the value of this {@code Option}, if a value
         * is present, otherwise {@link #NONE}
         * @throws NullPointerException
         *         if the mapping function is {@code null}
         *         or returns a {@code null} result
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public final <B> Option<B> flatMap(final Function<? super T, Option<B>> mapper) {
            E.NPE(mapper);
            Option<B> result = isDefined() ? mapper.apply(get()) : NONE;
            E.NPE(null == result);
            return result;
        }

        /**
         * Return the value if present, otherwise return {@code other}.
         *
         * @param other
         *         the value to be returned if there is no value present,
         *         may be {@code null}
         * @return the value, if present, otherwise {@code other}
         */
        public final T orElse(T other) {
            return isDefined() ? get() : other;
        }

        /**
         * Return the value if present, otherwise invoke {@code other} and return
         * the result of that invocation.
         *
         * @param other
         *         the function that is applied when no value is presented
         * @return the value if present otherwise the result of {@code other.apply()}
         * @throws NullPointerException
         *         if value is not present and other is null
         * @since 0.2
         */
        public final T orElse(Func0<? extends T> other) {
            return isDefined() ? get() : other.apply();
        }

        public final void runWith($.Visitor<? super T> consumer) {
            if (isDefined()) {
                consumer.apply(get());
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Option) {
                Option that = (Option) obj;
                return eq(get(), that.get());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return isDefined() ? get().hashCode() : 0;
        }

        @Override
        public abstract String toString();

        @SuppressWarnings("unchecked")
        public static <T> None<T> none() {
            return (None<T>) NONE;
        }

        /**
         * Returns an {@code Option} with the specified present non-null value.
         *
         * @param value
         *         the value that cannot be {@code null}
         * @param <T>
         *         the type of the value
         * @return an Option instance describing the value
         * @throws NullPointerException
         *         if the value specified is {@code null}
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
         * @param value
         *         the value
         * @param <T>
         *         the type of the value
         * @return an {@code Option} describing the value if it is not {@code null}
         * or {@link #NONE} if the value is {@code null}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> Option<T> of(T value) {
            return null == value ? NONE : some(value);
        }

        /**
         * The runtime/instance function namespace
         */
        public final class f {
            private f() {
            }

            /**
             * A function that when applied, returns if the {@code Option} is defined
             */
            public final F0<Boolean> IS_DEFINED = new F0<Boolean>() {
                @Override
                public Boolean apply() {
                    return Option.this != NONE;
                }
            };

            /**
             * Negate of {@link #IS_DEFINED}
             */
            @SuppressWarnings("unused")
            public final F0<Boolean> NOT_DEFINED = F.negate(IS_DEFINED);

            /**
             * A function that when applied, returns the value described by this {@code Option}
             */
            public final F0<T> GET = new F0<T>() {
                @Override
                public T apply() throws NotAppliedException, Break {
                    return Option.this.get();
                }
            };

            /**
             * Returns a function that when applied, run {@link Option#filter(Function)} on this
             * {@code Option}
             *
             * @param predicate
             *         the predicate function
             * @return the function that returns either this option or {@link #NONE} if predicate failed
             * to test on the element in this option
             */
            public final F0<Option<T>> filter(final Function<? super T, Boolean> predicate) {
                return new F0<Option<T>>() {
                    @Override
                    public Option<T> apply() throws NotAppliedException, Break {
                        return Option.this.filter(predicate);
                    }
                };
            }

            /**
             * Returns a function that when applied, run {@link Option#map(Function)} on this
             * {@code Option}
             *
             * @param mapper
             *         the function that map {@code T} element to {@code B} object
             * @param <B>
             *         the type of returning option element type
             * @return the function returns either a {@code B} type option if this option
             * is defined or {@link #NONE} if this option is not defined
             */
            public final <B> F0<Option<B>> map(final Function<? super T, ? extends B> mapper) {
                return new F0<Option<B>>() {
                    @Override
                    public Option<B> apply() throws NotAppliedException, Break {
                        return Option.this.map(mapper);
                    }
                };
            }

            /**
             * Returns a function that when applied, run {@link Option#flatMap(Function)} on this
             * {@code Option}
             *
             * @param mapper
             *         the function that map an elemnet of type T to a {@code Option} of type B
             * @param <B>
             *         the element type of the {@code Option}
             * @return the function that flat map all {@code T} element to {@code B} Options
             */
            public final <B> F0<Option<B>> flatMap(final Function<? super T, Option<B>> mapper) {
                return new F0<Option<B>>() {
                    @Override
                    public Option<B> apply() throws NotAppliedException, Break {
                        return Option.this.flatMap(mapper);
                    }
                };
            }

            /**
             * Returns a function that when applied, run {@link Option#orElse(Object)} on this
             * {@code Option}
             *
             * @param other
             *         the other value to be returned if this option is empty
             * @return the function implement {@code else if} semantic
             */
            @SuppressWarnings("unused")
            public final F0<T> orElse(final T other) {
                return new F0<T>() {
                    @Override
                    public T apply() throws NotAppliedException, Break {
                        return Option.this.orElse(other);
                    }
                };
            }

            /**
             * Returns a function that when applied, run {@link Option#orElse(Func0)}
             * on this {@code Option}
             *
             * @param other
             *         the function that generates another {@code T} element when this
             *         option is empty
             * @return the function that implement the {@code else if} semantic on this Option
             */
            @SuppressWarnings("unused")
            public final F0<T> orElse(final Func0<? extends T> other) {
                return new F0<T>() {
                    @Override
                    public T apply() throws NotAppliedException, Break {
                        return Option.this.orElse(other);
                    }
                };
            }

            /**
             * Returns a function that when applied, run {@link Option#runWith(Visitor)}
             * on this {@code Option}
             *
             * @param consumer
             *         the function that consumes the element in this Option
             * @return a function that apply to {@code consumer} function if this Option is defined
             */
            @SuppressWarnings("unused")
            public final F0<Void> runWith(final $.Visitor<? super T> consumer) {
                return new F0<Void>() {
                    @Override
                    public Void apply() throws NotAppliedException, Break {
                        Option.this.runWith(consumer);
                        return null;
                    }
                };
            }
        }

        /**
         * A reference to this runtime function namesapce
         */
        public final f f = new f();
    }

    public static class None<T> extends Option<T> {

        private static final long serialVersionUID = 962498820763181262L;

        private None() {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            super.clone();
            throw new CloneNotSupportedException();
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

        private Object readResolve() throws ObjectStreamException {
            return NONE;
        }
    }

    public static class Some<T> extends Option<T> {

        private static final long serialVersionUID = 962498820763181265L;

        final T value;

        public Some(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
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
    public static <T> Option<T> some(T a) {
        return null == a ? NONE : new Some(a);
    }

    @SuppressWarnings("unchecked")
    public static <T> None<T> none() {
        return (None<T>) NONE;
    }

    private static class VarListIterator<T> implements ListIterator<T> {
        private Var<T> var;
        private volatile boolean consumed;

        VarListIterator(Var<T> var, boolean consumed) {
            this(var);
            this.consumed = consumed;
        }

        VarListIterator(Var<T> var) {
            this.var = var;
        }

        @Override
        public boolean hasNext() {
            return !consumed;
        }

        @Override
        public boolean hasPrevious() {
            return consumed;
        }

        @Override
        public T next() {
            if (consumed) {
                throw new NoSuchElementException();
            }
            consumed = true;
            return var.get();
        }

        @Override
        public T previous() {
            if (!consumed) {
                throw new NoSuchElementException();
            }
            consumed = false;
            return var.get();
        }

        @Override
        public int nextIndex() {
            return consumed ? 1 : 0;
        }

        @Override
        public int previousIndex() {
            return consumed ? 0 : -1;
        }

        @Override
        public void set(T t) {
            var.set(t);
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Var<T> extends ListBase<T> implements C.ListOrSet<T>, Func0<T> {

        private T v;

        public Var(T value) {
            v = value;
        }

        public final boolean isDefined() {
            return null != v;
        }

        public final boolean isNull() {
            return !isDefined();
        }

        @Override
        public T apply() throws NotAppliedException, Break {
            return v;
        }

        @Override
        protected EnumSet<C.Feature> initFeatures() {
            return EnumSet.allOf(C.Feature.class);
        }

        @Override
        public Var<T> accept($.Visitor<? super T> visitor) {
            visitor.apply(v);
            return this;
        }

        @Override
        public Var<T> each($.Visitor<? super T> visitor) {
            return accept(visitor);
        }

        @Override
        public Var<T> forEach($.Visitor<? super T> visitor) {
            return accept(visitor);
        }

        @Override
        public T head() throws NoSuchElementException {
            return v;
        }

        @Override
        public Var<T> acceptLeft($.Visitor<? super T> visitor) {
            visitor.apply(v);
            return this;
        }

        @Override
        public <R> R reduceLeft(R identity, Func2<R, T, R> accumulator) {
            return accumulator.apply(identity, v);
        }

        @Override
        public Option<T> reduceLeft(Func2<T, T, T> accumulator) {
            return some(v);
        }

        @Override
        public Option<T> findFirst(Function<? super T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return some(v);
            } else {
                return none();
            }
        }

        @Override
        public C.List<T> take(int n) {
            if (n == 0) {
                return C.list();
            } else if (n < 0) {
                return drop(size() + n);
            } else {
                return this;
            }
        }

        @Override
        public C.List<T> tail() throws UnsupportedOperationException {
            return C.list();
        }

        @Override
        public C.List<T> takeWhile(Function<? super T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return this;
            }
            return C.list();
        }

        @Override
        public C.List<T> drop(int n) throws IllegalArgumentException {
            if (n == 0) {
                return this;
            }
            return C.list();
        }

        @Override
        public C.List<T> dropWhile(Function<? super T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return C.list();
            }
            return this;
        }

        @Override
        public C.ListOrSet<T> filter(Function<? super T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return this;
            }
            return C.empty();
        }

        @Override
        public <R> C.ListOrSet<R> map(Function<? super T, ? extends R> mapper) {
            return new Var<R>(mapper.apply(v));
        }

        @Override
        public <R> C.List<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper) {
            ListBuilder<R> lb = new ListBuilder<R>(3);
            forEach($.visitor(f1(mapper).andThen(C.F.addAllTo(lb))));
            return lb.toList();
        }

        @Override
        public <E> C.List<? extends Binary<T, E>> zip(Iterable<E> iterable) {
            Iterator<E> itr = iterable.iterator();
            if (itr.hasNext()) {
                return new Var<>(T2(v, itr.next()));
            }
            return C.list();
        }

        @Override
        public C.Sequence<Binary<T, Integer>> zipWithIndex() {
            return new Var<>((Binary<T, Integer>) T2(v, 0));
        }

        @Override
        public Var<T> lazy() {
            return this;
        }

        @Override
        public Var<T> eager() {
            return this;
        }

        @Override
        public Var<T> parallel() {
            return this;
        }

        @Override
        public Var<T> sequential() {
            return this;
        }

        @Override
        public int hashCode() {
            return hc(v);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Var) {
                Var v = (Var) o;
                return $.eq(v.get(), this.get());
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public <R> R reduce(R identity, Func2<R, T, R> accumulator) {
            return reduceLeft(identity, accumulator);
        }

        @Override
        public Option<T> reduce(Func2<T, T, T> accumulator) {
            return reduceLeft(accumulator);
        }

        @Override
        public Option<T> findOne(Function<? super T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return some(v);
            } else {
                return none();
            }
        }

        @Override
        public boolean anyMatch(Function<? super T, Boolean> predicate) {
            return predicate.apply(v);
        }

        @Override
        public boolean noneMatch(Function<? super T, Boolean> predicate) {
            return !anyMatch(predicate);
        }

        @Override
        public boolean allMatch(Function<? super T, Boolean> predicate) {
            return anyMatch(predicate);
        }

        @Override
        public int size() throws UnsupportedOperationException {
            return 1;
        }

        @Override
        public C.List<T> subList(int fromIndex, int toIndex) {
            if (fromIndex < 0 || toIndex > 1 || fromIndex > toIndex) {
                throw new IndexOutOfBoundsException();
            }
            if (fromIndex == toIndex) {
                return C.list();
            }
            return this;
        }

        @Override
        public boolean addAll(Iterable<? extends T> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public C.List<T> tail(int n) {
            if (n == 0) {
                return C.list();
            }
            return this;
        }

        @Override
        public C.List<T> remove(Function<? super T, Boolean> predicate) {
            throw new UnsupportedOperationException();
        }

        private class Csr implements Cursor<T> {
            private int id = 0;

            @Override
            public boolean isDefined() {
                return 0 == id;
            }

            @Override
            public int index() {
                return id;
            }

            @Override
            public boolean hasNext() {
                return -1 == id;
            }

            @Override
            public boolean hasPrevious() {
                return 1 == id;
            }

            @Override
            public Cursor<T> forward() throws UnsupportedOperationException {
                if (id == -1) {
                    id = 0;
                    return this;
                }
                throw new UnsupportedOperationException();
            }

            @Override
            public Cursor<T> backward() throws UnsupportedOperationException {
                if (id == 1) {
                    id = 0;
                }
                throw new UnsupportedOperationException();
            }

            @Override
            public Cursor<T> parkLeft() {
                id = -1;
                return this;
            }

            @Override
            public Cursor<T> parkRight() {
                id = 1;
                return this;
            }

            @Override
            public T get() throws NoSuchElementException {
                if (id == 0) {
                    return v;
                }
                throw new NoSuchElementException();
            }

            @Override
            public Cursor<T> set(T t) throws IndexOutOfBoundsException, NullPointerException {
                if (id == 0) {
                    v = t;
                    return this;
                }
                throw new IndexOutOfBoundsException();
            }

            @Override
            public Cursor<T> drop() throws NoSuchElementException, UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Cursor<T> prepend(T t) throws IndexOutOfBoundsException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Cursor<T> append(T t) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public Cursor<T> locateFirst(Function<T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return new Csr();
            }
            return new Csr().parkRight();
        }

        @Override
        public Cursor<T> locate(Function<T, Boolean> predicate) {
            return locateFirst(predicate);
        }

        @Override
        public Cursor<T> locateLast(Function<T, Boolean> predicate) {
            if (predicate.apply(v)) {
                return new Csr();
            }
            return new Csr().parkLeft();
        }

        @Override
        public C.List<T> insert(int index, T t) throws IndexOutOfBoundsException {
            if (index == 0) {
                return C.list(t, v);
            } else if (index == 1) {
                return C.list(v, t);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public C.List<T> insert(int index, T... ta) throws IndexOutOfBoundsException {
            if (index == 0) {
                return C.listOf(ta).prepend(v);
            } else if (index == 1) {
                return C.listOf(ta).append(v);
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public C.List<T> insert(int index, List<T> subList) throws IndexOutOfBoundsException {
            if (index == 0) {
                return C.list(subList).prepend(v);
            } else if (index == 1) {
                return C.list(subList).append(v);
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public C.List<T> reverse() {
            return this;
        }

        @Override
        public C.ListOrSet<T> without(Collection<? super T> col) {
            if (col.contains(v)) {
                return C.empty();
            }
            return this;
        }

        @Override
        public C.ListOrSet<T> without(T element) {
            if (eq(v, element)) return C.empty();
            return this;
        }

        @Override
        public C.ListOrSet<T> without(T element, T... elements) {
            if (eq(v, element)) return C.empty();
            int id = search(v, elements);
            if (-1 == id) return this;
            return C.empty();
        }

        @Override
        public C.Set<T> onlyIn(Collection<? extends T> col) {
            C.Set<T> set = C.newSet(col);
            if (col.contains(v)) {
                set.remove(v);
            }
            return set;
        }

        @Override
        public C.Set<T> with(Collection<? extends T> col) {
            return C.set(v).with(col);
        }

        @Override
        public C.Set<T> with(T element) {
            return C.set(v, element);
        }

        @Override
        public C.Set<T> with(T element, T... elements) {
            return C.set(v).with(element, elements);
        }

        @Override
        public C.List<T> acceptRight($.Visitor<? super T> visitor) {
            return accept(visitor);
        }

        @Override
        public C.List<T> acceptRight($.IndexedVisitor<Integer, ? super T> indexedVisitor) {
            return accept(indexedVisitor);
        }

        @Override
        protected void forEachLeft($.Visitor<? super T> visitor) throws Break {
            visitor.apply(v);
        }

        @Override
        protected void forEachLeft($.IndexedVisitor<Integer, ? super T> indexedVisitor) throws Break {
            indexedVisitor.apply(0, v);
        }

        @Override
        protected void forEachRight($.Visitor<? super T> visitor) throws Break {
            visitor.apply(v);
        }

        @Override
        protected void forEachRight($.IndexedVisitor<Integer, ? super T> indexedVisitor) throws Break {
            indexedVisitor.apply(0, v);
        }

        @Override
        public <B> C.List<Binary<T, B>> zip(List<B> list) {
            if (list.size() == 0) {
                return C.list();
            }
            return C.list((Binary<T, B>) T2(v, list.get(0)));
        }

        @Override
        public boolean add(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            if (index == 0) {
                return new VarListIterator<T>(this);
            } else if (index == 1) {
                return new VarListIterator<T>(this, true);
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public ListIterator<T> listIterator() {
            return new VarListIterator<T>(this);
        }


        @Override
        public T last() throws NoSuchElementException {
            return v;
        }

        @Override
        public Iterator<T> reverseIterator() {
            return listIterator(0);
        }

        @Override
        public T get(int index) {
            if (index == 0) {
                return v;
            }
            throw new IndexOutOfBoundsException();
        }


        public T get() {
            return v;
        }

        public Var<T> set(T value) {
            v = value;
            return this;
        }

        public Var<T> set(Var<T> var) {
            v = var.v;
            return this;
        }

        @Override
        public T set(int index, T element) {
            if (0 == index) {
                v = element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void add(int index, T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            if (eq(o, v)) {
                return 0;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            return indexOf(o);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return eq(v, o);
        }

        @Override
        public Object[] toArray() {
            return new Object[]{v};
        }

        @Override
        public <T> T[] toArray(T[] a) {
            T[] ta = (a.length > 0) ? a : newArray(a, 1);
            ta[0] = (T) v;
            if (ta.length > 1) {
                ta[1] = null;
            }
            return ta;
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (ne(o, v)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <R> R reduceRight(R identity, Func2<R, T, R> accumulator) {
            return reduce(identity, accumulator);
        }

        @Override
        public Option<T> reduceRight(Func2<T, T, T> accumulator) {
            return reduce(accumulator);
        }

        @Override
        public Option<T> findLast(Function<? super T, Boolean> predicate) {
            return findFirst(predicate);
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public C.Set<T> withIn(Collection<? extends T> col) {
            return col.contains(v) ? this : C.<T>Set();
        }

        public Var<T> update(Function<T, T> changer) {
            v = changer.apply(v);
            return this;
        }

        public Var<T> update(Func0<T> changer) {
            v = changer.apply();
            return this;
        }

        public Option<T> toOption() {
            if (null == v) {
                return none();
            } else {
                return some(v);
            }
        }

        public class _f {

            public F1<T, Var<T>> setter() {
                return new F1<T, Var<T>>() {
                    @Override
                    public Var<T> apply(T t) throws NotAppliedException, Break {
                        return Var.this.set(t);
                    }
                };
            }

            public <R> F1<R, Var<T>> mapper(final Function<R, T> mapper) {
                return new F1<R, Var<T>>() {
                    @Override
                    public Var<T> apply(R r) throws NotAppliedException, Break {
                        return Var.this.set(mapper.apply(r));
                    }
                };
            }

            public F1<T, Var<T>> updater(final Function<T, T> changer) {
                return new F1<T, Var<T>>() {
                    @Override
                    public Var<T> apply(T t) throws NotAppliedException, Break {
                        return Var.this.update(f1(changer).curry(t));
                    }
                };
            }

            public F1<T, Var<T>> updater(final Func2<T, T, T> changer) {
                return new F1<T, Var<T>>() {
                    @Override
                    public Var<T> apply(T t) throws NotAppliedException, Break {
                        F2<T, T, T> f2 = f2(changer);
                        return Var.this.update(f2.curry(t));
                    }
                };
            }

        }

        public _f f = new _f();

        public static <T> Var<T> of(T t) {
            return new Var<T>(t);
        }
    }

    public static class Val<T> extends Var<T> {
        public Val(T value) {
            super(value);
        }

        @Override
        public Var<T> set(T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T set(int index, T element) {
            throw new UnsupportedOperationException();
        }

        public static <T> Val<T> of(T t) {
            return new Val<T>(t);
        }
    }

    public static <T> Var<T> var(T t) {
        return Var.of(t);
    }

    public static <T> Var<T> var() {
        return Var.of(null);
    }

    public static <T> Val<T> val(T t) {
        return Val.of(t);
    }

    public static <T> Const<T> constant(T t) {
        return Const.of(t);
    }

    public static <T> Const<T> constant() {
        return Const.<T>of(null);
    }

    // --- common utilities

    /**
     * Check if two object is equals to each other.
     *
     * @param a
     *         the first object
     * @param b
     *         the second object
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
     * Check if two objects are equals to each other. The comparison will do
     * array equal matching if needed
     *
     * @param a
     *         the first object
     * @param b
     *         the second object
     * @return {@code true} if the first object equals to the second object
     */
    public static boolean eq2(Object a, Object b) {
        if (eq(a, b)) return true;
        Class<?> ca = a.getClass();
        if (!ca.isArray()) return false;
        Class<?> cb = b.getClass();
        if (ca != cb) return false;
        if (ca == boolean[].class) {
            return Arrays.equals((boolean[]) a, (boolean[]) b);
        } else if (ca == byte[].class) {
            return Arrays.equals((byte[]) a, (byte[]) b);
        } else if (ca == int[].class) {
            return Arrays.equals((int[]) a, (int[]) b);
        } else if (ca == char[].class) {
            return Arrays.equals((char[]) a, (char[]) b);
        } else if (ca == long[].class) {
            return Arrays.equals((long[]) a, (long[]) b);
        } else if (ca == float[].class) {
            return Arrays.equals((float[]) a, (float[]) b);
        } else if (ca == double[].class) {
            return Arrays.equals((double[]) a, (double[]) b);
        } else if (ca == short[].class) {
            return Arrays.equals((short[]) a, (short[]) b);
        } else {
            return Arrays.equals((Object[]) a, (Object[]) b);
        }
    }

    /**
     * Check if two {@code boolean} value equals to each other
     *
     * @param a
     *         boolean a
     * @param b
     *         boolean b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(boolean a, boolean b) {
        return a == b;
    }

    /**
     * Check if two {@code byte} value equals to each other
     *
     * @param a
     *         byte a
     * @param b
     *         byte b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(byte a, byte b) {
        return a == b;
    }

    /**
     * Check if two {@code char} value equals to each other
     *
     * @param a
     *         char a
     * @param b
     *         char b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(char a, char b) {
        return a == b;
    }

    /**
     * Check if two {@code short} value equals to each other
     *
     * @param a
     *         short a
     * @param b
     *         short b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(short a, short b) {
        return a == b;
    }

    /**
     * Check if two {@code int} value equals to each other
     *
     * @param a
     *         int a
     * @param b
     *         int b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(int a, int b) {
        return a == b;
    }

    /**
     * Check if two {@code float} value equals to each other
     *
     * @param a
     *         float a
     * @param b
     *         float b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(float a, float b) {
        return a == b;
    }

    /**
     * Check if two {@code long} value equals to each other
     *
     * @param a
     *         long a
     * @param b
     *         long b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(long a, long b) {
        return a == b;
    }

    /**
     * Check if two {@code double} value equals to each other
     *
     * @param a
     *         double a
     * @param b
     *         double b
     * @return {@code true} if {@code a == b}
     */
    public static boolean eq(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /**
     * Check if two objects are equals to each other.
     *
     * @param a
     *         the first object
     * @param b
     *         the second object
     * @return {@code false} if {@code a} equals to {@code b}
     * @see #eq(Object, Object)
     */
    public static boolean ne(Object a, Object b) {
        return !eq(a, b);
    }

    /**
     * Evaluate an object's bool value. The rules are:
     * <table summary="boolean value evaluation rules">
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
     * <tr><td>Array</td><td>length of the array &gt; 0</td></tr>
     * <tr><td>Byte</td><td>{@code v != 0}</td></tr>
     * <tr><td>Char</td><td>{@code v != 0}</td></tr>
     * <tr><td>Integer</td><td>{@code v != 0}</td></tr>
     * <tr><td>Long</td><td>{@code v != 0L}</td></tr>
     * <tr><td>Float</td><td>{@code Math.abs(v) > Float.MIN_NORMAL}</td></tr>
     * <tr><td>Double</td><td>{@code Math.abs(v) > Double.MIN_NORMAL}</td></tr>
     * <tr><td>BigInteger</td><td>{@code !BigInteger.ZERO.equals(v)}</td></tr>
     * <tr><td>BigDecimal</td><td>{@code !BigDecimal.ZERO.equals(v)}</td></tr>
     * <tr><td>File</td><td>{@link java.io.File#exists() v.exists()}</td></tr>
     * <tr><td>{@link Func0}</td><td>{@code bool(v.apply())}</td></tr>
     * <tr><td>Other types</td><td>{@code true}</td></tr>
     * </tbody>
     * </table>
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if v evaluate to true, {@code false} otherwise
     */
    public static boolean bool(Object v) {
        if (null == v || NONE == v) {
            return false;
        }
        if (v instanceof Boolean) {
            return (Boolean) v;
        }
        if (v instanceof String) {
            return bool((String) v);
        }
        if (v instanceof Collection) {
            return bool((Collection) v);
        }
        if (v.getClass().isArray()) {
            return 0 < Array.getLength(v);
        }
        if (v instanceof Number) {
            if (v instanceof Float) {
                return bool((float) (Float) v);
            }
            if (v instanceof Double) {
                return bool((double) (Double) v);
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
        if (v instanceof Func0) {
            return not(((Func0) v).apply());
        }
        for (Function<Object, Boolean> tester : conf.boolTesters) {
            try {
                return !(tester.apply(v));
            } catch (RuntimeException e) {
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
     * A String typed instance is considered to be `false` when
     *
     * 1. it is null or empty, or
     * 2. it equals to `false` (case insensitive mode)
     *
     * @param s
     *         the string to be evaluated
     * @return {@code true} if s is not empty and s is not `false`
     * @see S#empty(String)
     */
    public static boolean bool(String s) {
        return !S.empty(s) && !"false".equalsIgnoreCase(s);
    }

    /**
     * Do bool evaluation on a collection.
     *
     * @param c
     *         the collection to be evaluated
     * @return {@code true} if the collection is not empty
     * @see java.util.Collection#isEmpty()
     */
    public static boolean bool(Collection<?> c) {
        return null != c && !c.isEmpty();
    }

    /**
     * Do bool evaluation on a byte value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(byte v) {
        return 0 != v;
    }

    /**
     * Do bool evaluation on a char value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(char v) {
        return 0 != v;
    }

    /**
     * Do bool evaluation on a int value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(int v) {
        return 0 != v;
    }

    /**
     * Do bool evaluation on a long value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if the value != 0
     */
    public static boolean bool(long v) {
        return 0L != v;
    }

    /**
     * Do bool evaluation on a float value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if {@code Math.abs(v) > Float.MIN_NORMAL}
     */
    public static boolean bool(float v) {
        return Math.abs(v) > Float.MIN_NORMAL;
    }

    /**
     * Do bool evaluation on a double value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if {@code Math.abs(v) > Double.MIN_NORMAL}
     */
    public static boolean bool(double v) {
        return Math.abs(v) > Double.MIN_NORMAL;
    }

    /**
     * Do bool evaluation on a BigDecimal value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if {@code !BigDecimal.ZERO.equals(v)}
     */
    public static boolean bool(BigDecimal v) {
        return null != v && !BigDecimal.ZERO.equals(v);
    }

    /**
     * Do bool evaluation on a BigInteger value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code true} if {@code !BigInteger.ZERO.equals(v)}
     */
    public static boolean bool(BigInteger v) {
        return null != v && !BigInteger.ZERO.equals(v);
    }

    /**
     * Do bool evaluation on a File instance.
     *
     * @param v
     *         the file to be evaluated
     * @return {@code true} if {@code v.exists()}
     */
    public static boolean bool(File v) {
        return null != v && v.exists();
    }

    /**
     * Do bool evaluation on an {@link Func0} instance. This will call
     * the {@link Func0#apply()} method and continue to
     * do bool evaluation on the return value
     *
     * @param v
     *         the function to be evaluated
     * @return {@code bool(v.apply())}
     */
    public static boolean bool(Func0<?> v) {
        return bool(v.apply());
    }

    /**
     * Returns negative of {@link #bool(java.lang.Object)}
     *
     * @param o
     *         the object to be evaluated
     * @return {@code !(bool(o))}
     */
    public static boolean not(Object o) {
        return !(bool(o));
    }

    /**
     * Returns negative of a boolean value
     *
     * @param v
     *         the value to be evaluated
     * @return {@code !v}
     */
    public static boolean not(boolean v) {
        return !v;
    }

    /**
     * Returns negative of {@link #bool(java.lang.String)}
     *
     * @param s
     *         the String to be evaluated
     * @return {@code !(bool(s))}
     * @see #bool(String)
     */
    public static boolean not(String s) {
        return !bool(s);
    }

    /**
     * Returns negative of {@link #bool(java.util.Collection)}
     *
     * @param c
     *         the Collection to be evaluated
     * @return {@code !(bool(c))}
     * @see #bool(java.util.Collection)
     */
    public static boolean not(Collection<?> c) {
        return null == c || c.isEmpty();
    }

    /**
     * Returns negative of {@link #bool(byte)}
     *
     * @param v
     *         the byte to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(byte v) {
        return 0 == v;
    }

    /**
     * Returns negative of {@link #bool(char)}
     *
     * @param v
     *         the char to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(char v) {
        return 0 == v;
    }

    /**
     * Returns negative of {@link #bool(int)}
     *
     * @param v
     *         the int to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(int v) {
        return 0 == v;
    }

    /**
     * Returns negative of {@link #bool(long)}
     *
     * @param v
     *         the long to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(long v) {
        return 0L == v;
    }

    /**
     * Returns negative of {@link #bool(float)}
     *
     * @param v
     *         the float to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(float v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(double)}
     *
     * @param v
     *         the double to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(double v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(BigDecimal)}
     *
     * @param v
     *         the value to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(BigDecimal v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(BigInteger)}
     *
     * @param v
     *         the value to be evaluated
     * @return {@code !(bool(v))}
     */
    public static boolean not(BigInteger v) {
        return !bool(v);
    }

    /**
     * Returns negative of {@link #bool(File)}
     *
     * @param file
     *         the file to be evaluated
     * @return {@code !(bool(file))}
     */
    public static boolean not(File file) {
        return !bool(file);
    }

    /**
     * Returns negative of {@link #bool(Func0)}
     *
     * @param f
     *         the function to be evaluated
     * @return {@code !(bool(f))}
     */
    public static boolean not(Func0<?> f) {
        return !bool(f);
    }

    /**
     * Check if an object is {@code null} or {@link #NONE}
     *
     * @param o
     *         the object to test
     * @return {@code true} if {@code o} is {@code null} or {@link #NONE}, or `false` otherwise
     */
    public static boolean isNull(Object o) {
        return null == o || NONE == o;
    }

    /**
     * Check if an object is not `null` and not {@link #NONE}
     *
     * @param o
     *         the object to test
     * @return `false` if `o` is `null` or {@link #NONE}, or `true` otherwise
     */
    public static boolean isNotNull(Object o) {
        return !isNull(o);
    }

    /**
     * Check if any objects in the parameter list is null
     *
     * @param o
     *         the first object to be checked
     * @param oa
     *         the array of objects to be checked
     * @return {@code true} if any one of the argument is {@code null}
     */
    public static boolean anyNull(Object o, Object... oa) {
        if (isNull(o)) return true;
        for (int i = oa.length - 1; i >= 0; --i) {
            if (isNull(oa[i])) return true;
        }
        return false;
    }

    /**
     * Check if all objects in the parameter list is null
     *
     * @param o
     *         the first object to be checked
     * @param oa
     *         the array of objects to be checked
     * @return {@code false} if any one of the argument is not {@code null} and not {@link #NONE}
     */
    public static boolean allNull(Object o, Object... oa) {
        if (isNotNull(o)) {
            return false;
        }
        for (int i = oa.length - 1; i >= 0; --i) {
            if (isNotNull(oa[i])) return false;
        }
        return true;
    }

    public static boolean noneNull(Object o, Object... oa) {
        return !anyNull(o, oa);
    }

    /**
     * Returns String representation of an object instance. Predicate the object specified
     * is {@code null} or {@code NONE}, then an empty string is returned
     *
     * @param o
     *         the object which will be converted into a string
     * @return a String representation of object
     */
    public static String toString(Object o) {
        if (isNull(o)) {
            return "";
        }
        return o.toString();
    }

    public static String toString2(Object o) {
        if (isNull(o)) {
            return "";
        }
        if (o.getClass().isArray()) {
            StringBuilder sb = S.newBuilder();
            int len = Array.getLength(o);
            if (len == 0) {
                return "[]";
            }
            sb.append("[");
            sb.append(toString2(Array.get(o, 0)));
            for (int i = 1; i < len; ++i) {
                sb.append(", ").append(toString2(Array.get(o, i)));
            }
            sb.append("]");
            return sb.toString();
        }
        return o.toString();
    }

    /**
     * Alias of {@link org.osgl.util.S#fmt(String, Object...)}
     *
     * @param tmpl
     *         the format template
     * @param args
     *         the format arguments
     * @return the formatted string
     * @since 0.2
     */
    public static String fmt(String tmpl, Object... args) {
        return S.fmt(tmpl, args);
    }

    private static final int HC_INIT = 17;
    private static final int HC_FACT = 37;

    public static int iterableHashCode(Iterable<?> it) {
        int ret = HC_INIT;
        for (Object o : it) {
            ret = ret * HC_FACT + hc(o);
        }
        return ret;
    }

    // to fix https://github.com/actframework/actframework/issues/784
    public static int hc() {
        return HC_INIT;
    }

    public static int hc(boolean o) {
        return o ? 1231 : 1237;
    }

    public static int hc(boolean[] oa) {
        int ret = HC_INIT;
        for (boolean b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(short o) {
        return (int) o;
    }

    public static int hc(short[] oa) {
        int ret = HC_INIT;
        for (short b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(byte o) {
        return (int) o;
    }

    public static int hc(byte[] oa) {
        int ret = HC_INIT;
        for (byte b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(char o) {
        return (int) o;
    }

    public static int hc(char[] oa) {
        int ret = HC_INIT;
        for (char b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(int o) {
        return o;
    }

    public static int hc(int[] oa) {
        int ret = HC_INIT;
        for (int b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(float o) {
        return Float.floatToIntBits(o);
    }

    public static int hc(float[] oa) {
        int ret = HC_INIT;
        for (float b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(long o) {
        return (int) (o ^ (o >> 32));
    }

    public static int hc(long[] oa) {
        int ret = HC_INIT;
        for (long b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    public static int hc(double o) {
        return hc(Double.doubleToLongBits(o));
    }

    /**
     * Calculate hashcode of double array specified
     *
     * @param oa
     *         the double array
     * @return the hash code
     */
    public static int hc(double[] oa) {
        int ret = HC_INIT;
        for (double b : oa) {
            ret = ret * HC_FACT + hc(b);
        }
        return ret;
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param o
     *         object on which hash code to be calculated
     * @return the calculated hash code
     */
    public static int hc(Object o) {
        return hc_(o);
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param o1
     *         object 1
     * @param o2
     *         object 2
     * @return the calculated hash code
     */
    public static int hc(Object o1, Object o2) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        return i;
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param o1
     *         object 1
     * @param o2
     *         object 2
     * @param o3
     *         object 3
     * @return the calculated hash code
     */
    public static int hc(Object o1, Object o2, Object o3) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        i = 31 * i + hc_(o3);
        return i;
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param o1
     *         object 1
     * @param o2
     *         object 2
     * @param o3
     *         object 3
     * @param o4
     *         object 4
     * @return the calculated hash code
     */
    public static int hc(Object o1, Object o2, Object o3, Object o4) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        i = 31 * i + hc_(o3);
        i = 31 * i + hc_(o4);
        return i;
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param o1
     *         object 1
     * @param o2
     *         object 2
     * @param o3
     *         object 3
     * @param o4
     *         object 4
     * @param o5
     *         object 5
     * @return the calculated hash code
     */
    public static int hc(Object o1, Object o2, Object o3, Object o4, Object o5) {
        int i = 17;
        i = 31 * i + hc_(o1);
        i = 31 * i + hc_(o2);
        i = 31 * i + hc_(o3);
        i = 31 * i + hc_(o4);
        i = 31 * i + hc_(o5);
        return i;
    }

    /**
     * Calculate hashcode of objects specified
     *
     * @param o1
     *         object 1
     * @param o2
     *         object 2
     * @param o3
     *         object 3
     * @param o4
     *         object 4
     * @param o5
     *         object 5
     * @param args
     *         other objects
     * @return the calculated hash code
     */
    public static int hc(Object o1, Object o2, Object o3, Object o4, Object o5, Object... args) {
        int i = hc(o1, o2, o3, o4, o5);
        for (Object o : args) {
            i = 31 * i + hc(o);
        }
        return i;
    }

    private static int hc_(Object o) {
        if (null == o) {
            return HC_INIT * HC_FACT;
        }
        if (o.getClass().isArray()) {
            if (o instanceof int[]) {
                return hc((int[]) o);
            } else if (o instanceof long[]) {
                return hc((long[]) o);
            } else if (o instanceof char[]) {
                return hc((char[]) o);
            } else if (o instanceof byte[]) {
                return hc((byte[]) o);
            } else if (o instanceof double[]) {
                return hc((double[]) o);
            } else if (o instanceof float[]) {
                return hc((float[]) o);
            } else if (o instanceof short[]) {
                return hc((short[]) o);
            } else if (o instanceof boolean[]) {
                return hc((boolean[]) o);
            }
            int len = Array.getLength(o);
            int hc = 17;
            for (int i = 0; i < len; ++i) {
                hc = 31 * hc + hc_(Array.get(o, i));
            }
            return hc;
        } else {
            return o.hashCode();
        }
    }

    /**
     * Return {@link System#currentTimeMillis() current time millis}
     *
     * @return the current system time millis
     * @since 0.2
     */
    public static long ms() {
        return System.currentTimeMillis();
    }

    /**
     * Return {@link System#nanoTime() current nano time}
     *
     * @return the current system nano time
     * @since 0.2
     */
    public static long ns() {
        return System.nanoTime();
    }

    /**
     * Return current time stamp in nano seconds. Alias of {@link #ns()}
     *
     * @return the current system nao time
     * @since 0.2
     */
    public static long ts() {
        return System.nanoTime();
    }

    /**
     * An empty method.
     */
    public static void nil() {
    }

    /**
     * A dumb object instance
     */
    public static final Object DUMB = new Object();

    private static ConcurrentHashMap<Class<? extends Enum>, Map<Keyword, Enum>> enumLookup = new ConcurrentHashMap<>();

    /**
     * Return an enum value from code
     *
     * @param enumClass
     *         the enum class
     * @param name
     *         the name of the enum value. `name` is case insensitive
     * @param <T>
     *         the generic enum type
     * @return the enum value or `null` if there is no value has the name specified
     */
    public static <T extends Enum<T>> T asEnum(Class<T> enumClass, String name) {
        return asEnum(enumClass, name, false);
    }

    /**
     * Return an enum value from code
     *
     * @param enumClass
     *         the enum class
     * @param name
     *         the name of the enum value. `name` is case insensitive
     * @param exactMatch
     *         specify whether it should do exact name lookup or keyword variable lookup
     * @param <T>
     *         the generic enum type
     * @return the enum value or `null` if there is no value has the name specified
     */
    public static <T extends Enum<T>> T asEnum(final Class<T> enumClass, final String name, final boolean exactMatch) {
        if (S.blank(name)) {
            return null;
        }
        Map<Keyword, Enum> map = enumLookup.get(enumClass);
        if (null == map) {
            T[] values = enumClass.getEnumConstants();
            map = new HashMap<>(values.length * 2);
            for (T value : values) {
                map.put(Keyword.of(value.name()), value);
            }
            enumLookup.putIfAbsent(enumClass, map);
        }
        Keyword key = Keyword.of(name);
        T retVal = (T) map.get(key);
        return (null == retVal || (exactMatch && !retVal.name().equals(name))) ? null : retVal;
    }

    /**
     * Search an element in a array
     *
     * @param element
     *         the element to be located
     * @param elements
     *         the array of element to be searched
     * @param <T>
     *         the type
     * @return the location of the element inside elements, or {@code -1} if not found
     */
    public static <T> int search(T element, T... elements) {
        int len = elements.length;
        if (len == 0) return -1;

        boolean c = false;
        if (6 < len) {
            if (null != element) {
                c = element instanceof Comparable;
            } else {
                T t0 = elements[0];
                if (t0 == null) return 0;
                c = t0 instanceof Comparable;
            }
            if (c) {
                Arrays.sort(elements);
            }
        }
        if (c) {
            return Arrays.binarySearch(elements, element);
        } else {
            for (int i = 0; i < len; ++i) {
                if (eq(element, elements[i])) return i;
            }
            return -1;
        }
    }

    public static void echo(String msg, Object... args) {
        System.out.println(S.fmt(msg, args));
    }

    public static void error(String msg, Object... args) {
        System.err.println(S.fmt(msg, args));
    }

    public static <T> T ifNullThen(T t1, T def1) {
        return ensureGet(t1, def1);
    }

    public static <T> T ensureGet(T t1, T def1) {
        if (null != t1) {
            return t1;
        }
        E.NPE(def1);
        return def1;
    }

    public static <T> T ifNullThen(T t1, F0<T> def1) {
        return ensureGet(t1, def1);
    }

    public static <T> T ensureGet(T t1, F0<T> def1) {
        if (null != t1) {
            return t1;
        }
        t1 = def1.apply();
        E.NPE(t1);
        return t1;
    }

    public static <T> T ensureGet(T t1, T def1, T def2) {
        if (null != t1) {
            return t1;
        }
        if (null != def1) {
            return def1;
        }
        E.npeIf(null == def2);
        return def2;
    }

    public static <T> T ensureGet(T t1, T def1, T def2, T def3) {
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

    public static <T> T ensureGet(T t1, List<T> defs) {
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

    public static <T> T times(Function<T, T> func, T initVal, int n) {
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

    public static <T> Meta<T> meta(T t) {
        return new Meta<T>(t);
    }

    public static <T> Meta<T> given(T t) {
        return meta(t);
    }

    public static <T> Meta<T> having(T t) {
        return meta(t);
    }

    public static <T> Meta<T> take(T t) {
        return meta(t);
    }

    // ---
    public static class Meta<T> {
        private T o;

        public Meta(T obj) {
            E.NPE(obj);
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

    public static _IsClass is(Object t) {
        return new _IsClass(t);
    }

    public static _IsClass is(Class<?> c) {
        return new _IsClass(c);
    }

    public static _IsReflectMember is(Member member) {
        return new _IsReflectMember(member);
    }

    public static class _IsModifier {
        private int m;

        protected _IsModifier(int modifiers) {
            m = modifiers;
        }

        public boolean static_() {
            return Modifier.isStatic(m);
        }

        public boolean interface_() {
            return Modifier.isInterface(m);
        }

        public boolean public_() {
            return Modifier.isPublic(m);
        }

        public boolean abstract_() {
            return Modifier.isAbstract(m);
        }
    }

    public static class _IsReflectMember extends _IsModifier {
        public _IsReflectMember(Member member) {
            super(member.getModifiers());
        }
    }

    public static class _IsClass extends _IsModifier {
        private Class<?> c;
        private boolean allowBoxing;

        public _IsClass(Object obj) {
            this((obj instanceof Class ? (Class) obj : null == obj ? null : obj.getClass()));
        }

        public _IsClass(Class<?> clazz) {
            super(null == clazz ? 0 : clazz.getModifiers());
            c = clazz;
        }

        public _IsClass allowBoxing() {
            this.allowBoxing = true;
            return this;
        }

        public boolean instanceOf(Class<?> clz) {
            if (null == c) {
                return false;
            }
            boolean result = clz.isAssignableFrom(c);
            if (!result && allowBoxing) {
                if (isPrimitiveType(c)) {
                    return clz.isAssignableFrom(wrapperClassOf(c));
                }
                if (isWrapperType(c)) {
                    return clz.isAssignableFrom(primitiveTypeOf(c));
                }
            }
            return result;
        }

        public boolean notInstanceOf(Class<?> clz) {
            return !instanceOf(clz);
        }

        public boolean kindOf(Object object) {
            return instanceOf(object.getClass());
        }

        public boolean array() {
            if (null == c) {
                return false;
            }
            return c.isArray();
        }

        public boolean iterable() {
            return instanceOf(Iterable.class);
        }

        public boolean collection() {
            return instanceOf(Collection.class);
        }

        public boolean list() {
            return instanceOf(List.class);
        }

        public boolean set() {
            return instanceOf(Set.class);
        }

    }

    /**
     * Returns all implemented interfaces of a give type
     *
     * @param type
     *         a class
     * @return all interfaces `type` implements
     */
    public static Set<Class> interfacesOf(Class<?> type) {
        Set<Class> interfaces = new LinkedHashSet<>();
        Class<?> parent = type.getSuperclass();
        if (null != parent && Object.class != parent) {
            interfaces.addAll(interfacesOf(parent));
        }
        for (Class intf : type.getInterfaces()) {
            interfaces.addAll(interfacesOf(intf));
            interfaces.add(intf);
        }
        return interfaces;
    }

    /**
     * Returns all super classes of a given type ordered by affinity.
     * The `Object.class` is always the last element in the list.
     *
     * @param type
     *         a class
     * @return all super classes of `type`
     */
    public static List<Class> superClassesOf(Class<?> type) {
        List<Class> superClasses = new ArrayList();
        Class<?> parent = type.getSuperclass();
        while (null != parent) {
            superClasses.add(parent);
            parent = parent.getSuperclass();
        }
        return superClasses;
    }

    /**
     * Returns all types that
     *
     * @param type
     * @return
     */
    public static Set<Class> allTypesOf(Class<?> type) {
        Set<Class> allTypes = interfacesOf(type);
        allTypes.addAll(superClassesOf(type));
        return allTypes;
    }


    /**
     * Cast an object to a type. Returns an {@link Option} describing the casted value if
     * it can be casted to the type specified, otherwise returns {@link #NONE}.
     *
     * @param o
     *         the object to be casted
     * @param c
     *         specify the type to be casted to
     * @param <T>
     *         the type of the result option value
     * @return an {@code Option} describing the typed value or {@link #NONE}
     * if it cannot be casted
     */
    public static <T> Option<T> cast(Object o, Class<T> c) {
        if (null == o) {
            return Option.none();
        }
        if (c.isAssignableFrom(o.getClass())) {
            return Option.some((T) o);
        }
        return Option.none();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    /**
     * Return the object if it is not null, otherwise throw
     * out `NullPointerException`.
     *
     * This method is deprecated. Please use {@link #requireNotNull(Object)}
     * instead
     *
     * @param o
     *         the object
     * @param <T>
     *         the type parameter of object
     * @return the object if it is not null
     * @throws NullPointerException
     *         if `o` is `null`
     */
    @Deprecated
    public static <T> T notNull(T o) {
        E.NPE(o);
        return o;
    }

    /**
     * Return the object if it is not null, otherwise throw
     * out `NullPointerException`.
     *
     * @param o
     *         the object
     * @param <T>
     *         the type parameter of object
     * @return the object if it is not null
     * @throws NullPointerException
     *         if `o` is `null`
     */
    public static <T> T requireNotNull(T o) {
        if (null == o) {
            throw new NullPointerException();
        }
        return o;
    }

    /**
     *
     * Set an object field value using reflection.
     *
     * @param fieldName
     *         the name of the field to be set
     * @param obj
     *         the object on which the value will be set
     * @param val
     *         the value to be set to the field
     * @param <T>
     *         the type of the object
     * @param <F>
     *         the type of the field value
     * @return the object that has the new value set on the field specified
     */
    public static <T, F> T setField(String fieldName, T obj, F val) {
        Class<?> cls = obj.getClass();
        try {
            Field f;
            try {
                f = cls.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                f = cls.getField(fieldName);
            }
            f.setAccessible(true);
            f.set(obj, val);
        } catch (Exception e) {
            E.unexpected(e);
        }
        return obj;
    }

    /**
     * Alias of {@link #setField(String, Object, Object)}
     */
    public static <T, F> T setFieldValue(String fieldName, T obj, F val) {
        return setField(fieldName, obj, val);
    }

    /**
     * Set value to a static field of given class
     * @param fieldName
     *      the name of the field
     * @param type
     *      the class hosts the field
     * @param val
     *      the value to be set on the static field
     */
    public static void setField(String fieldName, Class<?> type, Object val) {
        Field f = fieldOf(type, fieldName, false);
        try {
            f.setAccessible(true);
            f.set(null, val);
        } catch (IllegalAccessException e) {
            throw E.unexpected(e);
        }
    }

    /**
     * Alias of {@link #setField(String, Class, Object)}
     */
    public static void setFieldValue(String fieldName, Class<?> type, Object val) {
        setField(fieldName, type, val);
    }

    /**
     * Get value of an object field.
     *
     * @param obj
     *         the object
     * @param field
     *         the field
     * @param <T>
     *         the field type
     * @return the value of the field in the object
     */
    public static <T> T getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            throw E.unexpected(e);
        }
    }

    public static void setFieldValue(Object obj, Field field, Object fieldValue) {
        try {
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (IllegalAccessException e) {
            throw E.unexpected(e);
        }
    }

    public static void setStaticFieldValue(Field field, Object fieldValue) {
        try {
            field.setAccessible(true);
            field.set(null, fieldValue);
        } catch (IllegalAccessException e) {
            throw E.unexpected(e);
        }
    }

    public static void setStaticFieldValue(Class host, String fieldName, Object fieldValue) {
        Field field = fieldOf(host, fieldName);
        E.illegalArgumentIf(null == field, "Unknown field: %s.%s", host.getName(), fieldName);
        setStaticFieldValue(field, fieldValue);
    }

    public static void resetFieldValue(Object obj, Field field) {
        setFieldValue(obj, field, $.convert(null).to(field.getType()));
    }

    private static Map<Object, Class> __primitiveTypes = new HashMap<>();

    static {
        __primitiveTypes.put("int", int.class);
        __primitiveTypes.put("boolean", boolean.class);
        __primitiveTypes.put("byte", byte.class);
        __primitiveTypes.put("short", short.class);
        __primitiveTypes.put("char", char.class);
        __primitiveTypes.put("long", long.class);
        __primitiveTypes.put("float", float.class);
        __primitiveTypes.put("double", double.class);
        __primitiveTypes.put("int.class", int.class);
        __primitiveTypes.put("boolean.class", boolean.class);
        __primitiveTypes.put("byte.class", byte.class);
        __primitiveTypes.put("short.class", short.class);
        __primitiveTypes.put("char.class", char.class);
        __primitiveTypes.put("long.class", long.class);
        __primitiveTypes.put("float.class", float.class);
        __primitiveTypes.put("double.class", double.class);
        __primitiveTypes.put("int[]", int[].class);
        __primitiveTypes.put("boolean[]", boolean[].class);
        __primitiveTypes.put("byte[]", byte[].class);
        __primitiveTypes.put("short[]", short[].class);
        __primitiveTypes.put("char[]", char[].class);
        __primitiveTypes.put("long[]", long[].class);
        __primitiveTypes.put("float[]", float[].class);
        __primitiveTypes.put("double[]", double[].class);
    }

    private static Map<Object, Object> __primitiveInstances = new HashMap<>();

    static {
        __primitiveInstances.put("int", 0);
        __primitiveInstances.put("boolean", false);
        __primitiveInstances.put("byte", 0);
        __primitiveInstances.put("short", 0);
        __primitiveInstances.put("char", 0);
        __primitiveInstances.put("long", 0l);
        __primitiveInstances.put("float", 0f);
        __primitiveInstances.put("double", 0d);
        __primitiveInstances.put("int.class", 0);
        __primitiveInstances.put("boolean.class", false);
        __primitiveInstances.put("byte.class", 0);
        __primitiveInstances.put("short.class", 0);
        __primitiveInstances.put("char.class", 0);
        __primitiveInstances.put("long.class", 0l);
        __primitiveInstances.put("float.class", 0f);
        __primitiveInstances.put("double.class", 0d);
        __primitiveInstances.put(int.class, 0);
        __primitiveInstances.put(boolean.class, false);
        __primitiveInstances.put(byte.class, 0);
        __primitiveInstances.put(short.class, 0);
        __primitiveInstances.put(char.class, 0);
        __primitiveInstances.put(long.class, 0l);
        __primitiveInstances.put(float.class, 0f);
        __primitiveInstances.put(double.class, 0d);
    }

    private static Map<Class, Class> __primitiveToWrappers = new HashMap<Class, Class>();
    private static Map<Class, Class> __wrapperToPrmitives = new HashMap<Class, Class>();

    static {
        __primitiveToWrappers.put(int.class, Integer.class);
        __primitiveToWrappers.put(boolean.class, Boolean.class);
        __primitiveToWrappers.put(byte.class, Byte.class);
        __primitiveToWrappers.put(short.class, Short.class);
        __primitiveToWrappers.put(char.class, Character.class);
        __primitiveToWrappers.put(long.class, Long.class);
        __primitiveToWrappers.put(float.class, Float.class);
        __primitiveToWrappers.put(double.class, Double.class);
        __primitiveToWrappers.put(int[].class, Integer[].class);
        __primitiveToWrappers.put(boolean[].class, Boolean[].class);
        __primitiveToWrappers.put(byte[].class, Byte[].class);
        __primitiveToWrappers.put(short[].class, Short[].class);
        __primitiveToWrappers.put(char[].class, Character[].class);
        __primitiveToWrappers.put(long[].class, Long[].class);
        __primitiveToWrappers.put(float[].class, Float[].class);
        __primitiveToWrappers.put(double[].class, Double[].class);

        for (Map.Entry<Class, Class> entry : __primitiveToWrappers.entrySet()) {
            __wrapperToPrmitives.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Check if a give class `c` is a simple type. The following classes are considered to be simple type:
     * 1. primitive types, e.g. int.class
     * 2. Wrapper type of primitive types, e.g. Integer.class
     * 3. String.class
     * 4. Any class extends `Enum.class`
     * 5. Locale.class
     *
     * @param c
     *         the class to be checked
     * @return `true` if the give type `c` is simple type as described above
     */
    public static boolean isSimpleType(Class<?> c) {
        return String.class == c || __wrapperToPrmitives.containsKey(c) || __primitiveToWrappers.containsKey(c) || Enum.class.isAssignableFrom(c) || Locale.class == c;
    }

    /**
     * Check if an Object or class is immutable
     *
     * @param o
     *         the object to be checked
     * @return `true` if the object is immutable or `false` otherwise
     */
    public static boolean isImmutable(Object o) {
        Class<?> type = (o instanceof Class) ? (Class) o : o.getClass();
        return isSimpleType(type) || OsglConfig.isImmutable(type);
    }

    /**
     * Check if a given class is a primitive type.
     *
     * @param c
     *         the class
     * @return `true` if `c` is primitive type
     */
    public static boolean isPrimitiveType(Class<?> c) {
        return __primitiveToWrappers.containsKey(c);
    }

    /**
     * Check if a given class is a primitive type.
     *
     * This method is deprecated. Please use {@link #isPrimitiveType(Class)} instead
     *
     * @param c
     *         the class
     * @return `true` if `c` is primitive type
     */
    @Deprecated
    public static boolean isPrimitive(Class<?> c) {
        return __primitiveToWrappers.containsKey(c);
    }

    /**
     * Check if a given string is a primitive type name.
     *
     * @param name
     *         the string to be test
     * @return `true` if `name` is primitive type name
     */
    public static boolean isPrimitiveType(String name) {
        return __primitiveTypes.containsKey(name);
    }

    /**
     * Check if a given class is a wrapper type of a primitive type.
     *
     * @param c
     *         the class
     * @return `true` if `c` is wrapper type
     */
    public static boolean isWrapperType(Class<?> c) {
        return __wrapperToPrmitives.containsKey(c);
    }

    /**
     * Check if a given class is a wrapper type of a primitive type.
     *
     * This method is deprecated, please use {@link #isWrapperType(Class)}
     * instead.
     *
     * @param c
     *         the class
     * @return `true` if `c` is wrapper type
     */
    @Deprecated
    public static boolean isWrapper(Class<?> c) {
        return __wrapperToPrmitives.containsKey(c);
    }

    public static Class wrapperClassOf(Class c) {
        if (c.isPrimitive()) {
            return __primitiveToWrappers.get(c);
        }
        if (c.isArray()) {
            Class c0 = __primitiveToWrappers.get(c);
            return null == c0 ? c : c0;
        }
        return c;
    }

    public static Class primitiveTypeOf(Class c) {
        if (c.isPrimitive()) {
            return c;
        }
        Class c0 = __wrapperToPrmitives.get(c);
        return null == c0 ? c : c0;
    }

    public static <T> Class<T> classForName(String className) {
        Class c = __primitiveTypes.get(className);
        if (null != c) return c;
        try {
            return (Class<T>) Class.forName(className);
        } catch (NoClassDefFoundError e) {
            throw new UnexpectedClassNotFoundException(e);
        } catch (ClassNotFoundException e) {
            throw new UnexpectedClassNotFoundException(e);
        }
    }

    public static <T> Class<T> classForName(String className, ClassLoader classLoader) {
        Class c = __primitiveTypes.get(className);
        if (null != c) return c;
        try {
            if (className.contains("[")) {
                className = S.buffer().append("[L").append(S.before(className, "[")).append(";").toString();
            }
            return (Class<T>) Class.forName(className, true, classLoader);
        } catch (NoClassDefFoundError e) {
            throw new UnexpectedClassNotFoundException(e);
        } catch (ClassNotFoundException e) {
            throw new UnexpectedClassNotFoundException(e);
        }
    }

    public static <T> Option<Class<T>> safeClassForName(String className) {
        Class c = __primitiveTypes.get(className);
        if (null != c) return some((Class<T>) c);
        try {
            return some((Class<T>) Class.forName(className));
        } catch (Exception e) {
            return none();
        }
    }

    public static <T> Option<Class<T>> safeClassForName(String className, ClassLoader classLoader) {
        Class c = __primitiveTypes.get(className);
        if (null != c) return some((Class<T>) c);
        try {
            return some((Class<T>) Class.forName(className, true, classLoader));
        } catch (Exception e) {
            return none();
        }
    }

    public static <T> T newInstance(final String className) {
        Object o = __primitiveInstances.get(className);
        if (null != o) return (T) o;
        // see http://stackoverflow.com/questions/27719295/java-lang-internalerror-callersensitive-annotation-expected-at-frame-1
        return new SecurityManager() {
            private T t;

            {
                try {
                    Class caller = getClassContext()[3];
                    Class<T> c = (Class<T>) Class.forName(className, true, caller.getClassLoader());
                    t = c.newInstance();
                } catch (Exception e) {
                    throw new UnexpectedNewInstanceException(e);
                }
            }
        }.t;
    }

    public static <T> T newInstance(final String className, ClassLoader cl) {
        Object o = __primitiveInstances.get(className);
        if (null != o) return (T) o;
        try {
            Class<T> c = (Class<T>) Class.forName(className, true, cl);
            return c.newInstance();
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e);
        }
    }

    public static <T> Option<T> safeNewInstance(String className) {
        Object o = __primitiveInstances.get(className);
        if (null != o) return some((T) o);
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return some(c.newInstance());
        } catch (Exception e) {
            return none();
        }
    }

    private static boolean testMethodParamType(Class[] pts, Object p, int pos) {
        E.invalidArgIf(pos < 0);
        if (pos < pts.length) {
            Class pt = pts[pos];
            pt = wrapperClassOf(pt);
            return (pt.isAssignableFrom(p.getClass()));
        } else {
            return false;
        }
    }

    public static <T> T primitiveDefaultValue(Class<T> c) {
        return (T) __primitiveInstances.get(c);
    }

    public static <T> T newInstance(Class<T> c) {
        Object o = __primitiveInstances.get(c);
        if (null != o) {
            return (T) o;
        }
        if (c.isInterface()) {
            if (Map.class == c) {
                return (T) new HashMap();
            } else if (List.class == c) {
                return (T) new ArrayList();
            } else if (Set.class == c) {
                return (T) new HashSet();
            } else if (SortedMap.class == c) {
                return (T) new TreeMap();
            } else if (SortedSet.class == c) {
                return (T) new TreeSet();
            } else if (ConcurrentMap.class == c) {
                return (T) new ConcurrentHashMap<>();
            } else if (Deque.class == c) {
                return (T) new ArrayDeque<>();
            } else if (BlockingDeque.class == c) {
                return (T) new LinkedBlockingDeque<>();
            } else if (Queue.class == c) {
                return (T) new LinkedList<>();
            } else {
                throw new UnsupportedOperationException("Instantiation of interface not supported: " + c);
            }
        }
        try {
            Constructor<T> ct = c.getDeclaredConstructor();
            ct.setAccessible(true);
            return ct.newInstance();
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
        }
    }

    public static <T, P1> T newInstance(Class<T> c, P1 p1) {
        try {
            Constructor[] ca = c.getDeclaredConstructors();
            for (Constructor<T> ct : ca) {
                Class[] pts = ct.getParameterTypes();
                if (pts.length != 1 && !ct.isVarArgs()) {
                    continue;
                }
                if (!testMethodParamType(pts, p1, 0)) {
                    continue;
                }
                return ct.newInstance(p1);
            }
            throw new UnexpectedNewInstanceException("Constructor not found on " + c.getName());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e);
        }
    }

    public static <T, P1, P2> T newInstance(Class<T> c, P1 p1, P2 p2) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                Class[] pts = ct.getParameterTypes();
                if (pts.length != 2 && !ct.isVarArgs()) {
                    continue;
                }
                if (!testMethodParamType(pts, p1, 0)) {
                    continue;
                }
                if (!testMethodParamType(pts, p2, 1)) {
                    continue;
                }
                ct.setAccessible(true);
                return ct.newInstance(p1, p2);
            }
            throw new UnexpectedNewInstanceException("constructor not found");
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e);
        }
    }

    public static <T, P1, P2, P3> T newInstance(Class<T> c, P1 p1, P2 p2, P3 p3) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                Class[] pts = ct.getParameterTypes();
                if (pts.length != 3 && !ct.isVarArgs()) {
                    continue;
                }
                if (!testMethodParamType(pts, p1, 0)) {
                    continue;
                }
                if (!testMethodParamType(pts, p2, 1)) {
                    continue;
                }
                if (!testMethodParamType(pts, p3, 2)) {
                    continue;
                }
                return ct.newInstance(p1, p2, p3);
            }
            throw new UnexpectedNewInstanceException("constructor not found on " + c.getName());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
        }
    }

    public static <T, P1, P2, P3, P4> T newInstance(Class<T> c, P1 p1, P2 p2, P3 p3, P4 p4) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                Class[] pts = ct.getParameterTypes();
                if (pts.length != 4 && !ct.isVarArgs()) {
                    continue;
                }
                if (!testMethodParamType(pts, p1, 0)) {
                    continue;
                }
                if (!testMethodParamType(pts, p2, 1)) {
                    continue;
                }
                if (!testMethodParamType(pts, p3, 2)) {
                    continue;
                }
                if (!testMethodParamType(pts, p4, 3)) {
                    continue;
                }
                return ct.newInstance(p1, p2, p3, p4);
            }
            throw new UnexpectedNewInstanceException("constructor not found on " + c.getName());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
        }
    }

    public static <T, P1, P2, P3, P4, P5> T newInstance(Class<T> c, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        try {
            Constructor[] ca = c.getConstructors();
            for (Constructor<T> ct : ca) {
                Class[] pts = ct.getParameterTypes();
                if (pts.length != 5 && !ct.isVarArgs()) {
                    continue;
                }
                if (!testMethodParamType(pts, p1, 0)) {
                    continue;
                }
                if (!testMethodParamType(pts, p2, 1)) {
                    continue;
                }
                if (!testMethodParamType(pts, p3, 2)) {
                    continue;
                }
                if (!testMethodParamType(pts, p4, 3)) {
                    continue;
                }
                if (!testMethodParamType(pts, p5, 4)) {
                    continue;
                }
                return ct.newInstance(p1, p2, p3, p4, p5);
            }
            throw new UnexpectedNewInstanceException("constructor not found on " + c.getName());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
        }
    }

    public static <T> T newInstance(Class<T> c, Object p1, Object p2, Object p3, Object p4, Object p5, Object... pa) {
        try {
            Constructor[] ca = c.getConstructors();
            int len = pa.length;
            for (Constructor<T> ct : ca) {
                Class[] pts = ct.getParameterTypes();
                if (pts.length != 5 + len && !ct.isVarArgs()) {
                    continue;
                }
                if (!testMethodParamType(pts, p1, 0)) {
                    continue;
                }
                if (!testMethodParamType(pts, p2, 1)) {
                    continue;
                }
                if (!testMethodParamType(pts, p3, 2)) {
                    continue;
                }
                if (!testMethodParamType(pts, p4, 3)) {
                    continue;
                }
                if (!testMethodParamType(pts, p5, 4)) {
                    continue;
                }
                boolean shouldContinue = false;
                for (int i = 0; i < len; ++i) {
                    Object p = pa[i];
                    if (!testMethodParamType(pts, p, 5 + i)) {
                        shouldContinue = true;
                        break;
                    }
                }
                if (shouldContinue) {
                    continue;
                }
                Object[] oa = new Object[5 + len];
                oa[0] = p1;
                oa[1] = p2;
                oa[2] = p3;
                oa[3] = p4;
                oa[4] = p5;
                System.arraycopy(pa, 0, oa, 5, len);
                return ct.newInstance(oa);
            }
            throw new UnexpectedNewInstanceException("constructor not found on " + c.getName());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedNewInstanceException(e, "error instantiate instance of " + c.getName());
        }
    }

    /**
     * Returns non-static field of a class by name. The field might be non-public declared in super classes
     * of the supplied class
     *
     * @param c
     *         the class
     * @param name
     *         the name of the field
     * @return the field instance or `null` if not found
     */
    public static Field fieldOf(Class<?> c, String name) {
        return fieldOf(c, name, true);
    }

    /**
     * Returns field of a class by name. The field could be non-public field of super class of
     * the class specified
     *
     * @param c
     *         the class
     * @param name
     *         the name of the field
     * @param noStatic
     *         specify if static fields shall be included
     * @return the field instance or `null` if not found
     */
    public static Field fieldOf(Class<?> c, String name, boolean noStatic) {
        return fieldOf(c, name, Object.class, noStatic);
    }

    /**
     * Returns field of a class by name. The field could be non-public field of super class of
     * the class specified
     *
     * @param c
     *         the class
     * @param name
     *         the name of the field
     * @param rootClass
     *         the class that stops of the recurive operation. Default is Object.class
     * @param noStatic
     *         specify if static fields shall be included
     * @return the field instance or `null` if not found
     */
    public static Field fieldOf(Class<?> c, String name, Class<?> rootClass, boolean noStatic) {
        List<Field> fields = fieldsOf(c, rootClass, noStatic);
        for (Field f : fields) {
            if (S.eq(f.getName(), name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Returns all fields of a class and all super classes. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`
     *
     * @param c
     *         the class
     * @return a list of fields
     */
    public static List<Field> fieldsOf(Class<?> c) {
        return fieldsOf(c, true);
    }

    /**
     * Returns all fields of a class and all super classes. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`
     *
     * @param c
     *         the class
     * @param filter
     *         specify which field will be put into the list if not `null`
     * @return a list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, $.Function<Field, Boolean> filter) {
        return fieldsOf(c, Object.class, filter);
    }

    /**
     * Returns all fields of a class and all super classes. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`
     *
     * @param c
     *         the class
     * @param noStatic
     *         specify if static fields shall be included. Default: `true`
     * @return a list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, boolean noStatic) {
        return fieldsOf(c, Object.class, noStatic);
    }

    /**
     * Returns all fields of a class and all super classes until root class. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`
     *
     * @param c
     *         the class
     * @param rootClass
     *         the class that stops the recursive operation
     * @param noStatic
     *         specify if static fields should be included
     * @return a list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, Class<?> rootClass, boolean noStatic) {
        return fieldsOf(c, rootClass, false, noStatic);
    }

    /**
     * Returns all fields of a class and all super classes until root class. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`
     *
     * @param c
     *         the class
     * @param rootClass
     *         the class that stops the recursive operation
     * @param noStatic
     *         specify if static fields should be included
     * @return a list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, Class<?> rootClass, boolean includeRootClass, boolean noStatic) {
        String key = c.getName();
        StringBuilder buf = null;
        if (Object.class != rootClass) {
            buf = new StringBuilder(key);
            buf.append(":");
            buf.append(rootClass.getName());
        }
        if (includeRootClass) {
            if (null == buf) {
                buf = new StringBuilder().append(key);
            }
            buf.append("+i");
        }
        if (!noStatic) {
            if (null == buf) {
                buf = new StringBuilder().append(key);
            }
            buf.append("-s");
        }
        if (null != buf) {
            key = buf.toString();
        }
        List<Field> fields = cache().get(key);
        if (null == fields) {
            fields = new ArrayList<>();
            cache().put(key, fields);
            $.Predicate<Field> filter = noStatic ? new $.Predicate<Field>() {
                @Override
                public boolean test(Field field) {
                    return !Modifier.isStatic(field.getModifiers());
                }
            } : null;
            addFieldsToList(fields, c, rootClass, includeRootClass, filter);
        }
        return fields;
    }

    /**
     * Returns all fields of a class and all super classes until root class. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`.
     *
     * @param c
     *         the class
     * @param rootClass
     *         the class that stops the recursive lookup
     * @param filter
     *         specify which field should be put into the list if not `null`
     * @return the list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, Class<?> rootClass, $.Function<Field, Boolean> filter) {
        List<Field> fields = new ArrayList<Field>();
        addFieldsToList(fields, c, rootClass, false, filter);
        return fields;
    }

    /**
     * Returns all fields of a class and all super classes until root class. Note all fields returned will
     * be called on {@link Field#setAccessible(boolean)} with value `true`.
     *
     * @param c
     *         the class
     * @param rootClass
     *         the class that stops the recursive lookup
     * @param includeRootClass
     *         specify root class itself needs to be checked or not
     * @param filter
     *         specify which field should be put into the list if not `null`
     * @return the list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, Class<?> rootClass, boolean includeRootClass, $.Function<Field, Boolean> filter) {
        List<Field> fields = new ArrayList<Field>();
        addFieldsToList(fields, c, rootClass, includeRootClass, filter);
        return fields;
    }

    /**
     * Returns all fields of a class and all super classes until classFilter not applied. Note all fields
     * returned will be called on {@link Field#setAccessible(boolean)} with value `true`.
     *
     * @param c
     *         the class
     * @param classFilter
     *         the filter to check if it should break recursive call
     * @param fieldFilter
     *         specify which field should be put into the list if not `null`
     * @return the list of fields
     */
    public static List<Field> fieldsOf(Class<?> c, $.Function<Class<?>, Boolean> classFilter, $.Function<Field, Boolean> fieldFilter) {
        List<Field> fields = new ArrayList<Field>();
        addFieldsToList(fields, c, classFilter, fieldFilter);
        return fields;
    }

    private static void addFieldsToList(List<Field> list, Class<?> c, Class<?> rootClass, boolean includeRootClass, $.Function<Field, Boolean> filter) {
        if (c.isInterface()) {
            return;
        }
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (field.isSynthetic() || null != filter && !filter.apply(field)) {
                continue;
            }
            field.setAccessible(true);
            list.add(field);
        }
        if (includeRootClass) {
            if (c != rootClass) {
                c = c.getSuperclass();
                if (null != c) {
                    addFieldsToList(list, c, rootClass, true, filter);
                }
            }
        } else {
            c = c.getSuperclass();
            if (null != c && c != rootClass) {
                addFieldsToList(list, c, rootClass, false, filter);
            }
        }
    }

    private static void addFieldsToList(List<Field> list, Class<?> c, $.Function<Class<?>, Boolean> classFilter, $.Function<Field, Boolean> fieldFilter) {
        if (c.isInterface()) {
            return;
        }
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (null != fieldFilter && !fieldFilter.apply(field)) {
                continue;
            }
            field.setAccessible(true);
            list.add(field);
        }
        if (null != classFilter) {
            c = c.getSuperclass();
            if (null != c && classFilter.apply(c)) {
                addFieldsToList(list, c, classFilter, fieldFilter);
            }
        }
    }

    /**
     * Invoke a static method by name and parameters
     *
     * @param c
     *         the class
     * @param methodName
     *         the method name
     * @param pa
     *         the parameters
     * @param <T>
     *         the generic type of the class
     * @param <R>
     *         the generic type of the return result
     * @return the result of method invocation
     */
    public static <T, R> R invokeStatic(Class<T> c, String methodName, Object... pa) {
        return invokeMethod(null, c, null, methodName, pa);
    }

    /**
     * Invoke a static method by name and parameters. After invocation
     * it will cache the method into method bag supplied. This method
     * will convert all checked exception into corresponding runtime exception
     *
     * @param methodBag
     *         the method bag
     * @param c
     *         the class
     * @param methodName
     *         the method name
     * @param pa
     *         the parameters
     * @param <T>
     *         the generic type of class
     * @param <R>
     *         the generic type of return instance
     * @return the result of method invocation
     */
    public static <T, R> R invokeStatic(Var<Method> methodBag, Class<T> c, String methodName, Object... pa) {
        return invokeMethod(methodBag, c, null, methodName, pa);
    }

    /**
     * Invoke a static method. This method will convert all checked exception
     * into corresponding runtime exception
     *
     * @param method
     *         the method
     * @param pa
     *         the arguments to invoke the method
     * @param <R>
     *         the generic type of the return result
     * @return the result of the method invocation
     */
    public static <R> R invokeStatic(Method method, Object... pa) {
        try {
            return (R) method.invoke(null, pa);
        } catch (Exception e) {
            throw UnexpectedMethodInvocationException.triage(e);
        }
    }

    /**
     * Invoke a virtual method by instance, method name and arguments. This method
     * will convert all checked exception into corresponding runtime exception.
     *
     * After invocation, the method will be cached into the method bag supplied
     *
     * @param methodBag
     *         A function to cache the method found by name and arguments
     * @param o
     *         the instance on which the virtual method will be invoked
     * @param methodName
     *         the method name
     * @param pa
     *         the arguments
     * @param <T>
     *         generic type of the instance object
     * @param <R>
     *         generic type of the return result
     * @return result of method invocation
     */
    public static <T, R> R invokeVirtual(Var<Method> methodBag, T o, String methodName, Object... pa) {
        E.NPE(o);
        return invokeMethod(methodBag, null, o, methodName, pa);
    }

    /**
     * Invoke a virtual method by instance, method name and arguments. This method
     * will convert all checked exception into corresponding runtime exception
     *
     * @param o
     *         the instance on which the virtual method will be invoked
     * @param methodName
     *         the method name
     * @param pa
     *         the arguments
     * @param <T>
     *         generic type of the instance object
     * @param <R>
     *         generic type of the return result
     * @return result of method invocation
     */
    public static <T, R> R invokeVirtual(T o, String methodName, Object... pa) {
        E.NPE(o);
        return invokeMethod(null, null, o, methodName, pa);
    }

    /**
     * Invoke a virtual {@link Method method}. This method will convert all checked exception
     * to corresponding runtime exception
     *
     * @param o
     *         the instance on which the method will be invoked
     * @param method
     *         the method
     * @param pa
     *         the arguments
     * @param <T>
     *         generic type of the instance
     * @param <R>
     *         generic type of the result
     * @return result of method invocation
     */
    public static <T, R> R invokeVirtual(T o, Method method, Object... pa) {
        E.NPE(o);
        try {
            return (R) method.invoke(o, pa);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw UnexpectedMethodInvocationException.triage(e);
        }
    }

    /**
     * Returns {@link Method} by name and parameter value
     *
     * @param c
     *         the class
     * @param methodName
     *         the method name
     * @param pa
     *         the parameter used to invoke the method
     * @return the method or `null` if not found
     */
    public static Method getMethod(Class c, String methodName, Object... pa) {
        Method[] ma = c.getMethods();
        for (Method m : ma) {
            if (!m.getName().equals(methodName)) {
                continue;
            }
            Class[] pts = m.getParameterTypes();
            boolean shouldContinue = false;
            int len = pts.length;
            for (int i = 0; i < len; ++i) {
                Object p = pa[i];
                if (!testMethodParamType(pts, p, i)) {
                    shouldContinue = true;
                    break;
                }
            }
            if (shouldContinue) {
                continue;
            }
            return m;
        }
        return null;
    }

    /**
     * Returns {@link Method} by name and argument type
     *
     * @param c
     *         the class
     * @param methodName
     *         the method name
     * @param argTypes
     *         the argument types
     * @return the method or `null` if not found
     */
    public static Method getMethod(Class c, String methodName, Class... argTypes) {
        try {
            return c.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static <T, R> R invokeMethod(Var<Method> methodBag, Class c, T o, String methodName, Object... pa) {
        try {
            if (null == c) {
                c = o.getClass();
            }
            Method[] ma = c.getMethods();
            for (Method m : ma) {
                if (!m.getName().equals(methodName)) {
                    continue;
                }
                Class[] pts = m.getParameterTypes();
                boolean shouldContinue = false;
                int len = pts.length;
                for (int i = 0; i < len; ++i) {
                    Object p = pa[i];
                    if (!testMethodParamType(pts, p, i)) {
                        shouldContinue = true;
                        break;
                    }
                }
                if (shouldContinue) {
                    continue;
                }
                if (!Modifier.isPublic(m.getModifiers())) {
                    m.setAccessible(true);
                }
                if (null != methodBag) {
                    methodBag.set(m);
                }
                return (R) m.invoke(o, pa);
            }
            throw new UnexpectedNoSuchMethodException(c, methodName);
        } catch (UnexpectedNoSuchMethodException e) {
            throw e;
        } catch (Exception e) {
            throw UnexpectedMethodInvocationException.triage(e);
        }
    }

    public static PropertyHandlerFactory propertyHandlerFactory = new ReflectionPropertyHandlerFactory();

    public static <T> T getProperty(Object entity, String property) {
        return getProperty(OsglConfig.internalCache(), entity, property);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(CacheService cache, Object entity, String property) {
        if (null == entity) {
            return null;
        }
        if (property.contains("]")) {
            property = property.replace('[', '.').replace("]", "");
        }
        if (property.contains(".")) {
            return getProperty(cache, entity, property.split("\\."));
        } else if (property.contains("/")) {
            return getProperty(cache, entity, property.split("\\/"));
        }
        PropertyGetter gettter = propertyGetter(cache, entity, property, false);
        return cast(gettter.get(entity, property));
    }

    public static void setProperty(Object entity, Object val, String property) {
        E.NPE(entity);
        if (property.contains("]")) {
            property = property.replace('[', '.').replace("]", "");
        }
        if (property.contains(".")) {
            setProperty(entity, val, property.split("\\."));
        } else if (property.contains("/")) {
            setProperty(entity, val, property.split("\\/"));
        } else {
            PropertySetter setter = propertySetter(null, entity, property);
            setter.set(entity, val, property);
        }
    }

    public static void setProperty(CacheService cache, Object entity, Object val, String property) {
        E.NPE(entity);
        if (property.contains("]")) {
            property = property.replace('[', '.').replace("]", "");
        }
        if (property.contains(".")) {
            setProperty(cache, entity, val, property.split("\\."));
        } else if (property.contains("/")) {
            setProperty(cache, entity, val, property.split("\\/"));
        } else {
            PropertySetter setter = propertySetter(cache, entity, property);
            setter.set(entity, val, property);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getProperty(Object entity, String... propertyPath) {
        return getProperty(null, entity, propertyPath);
    }

    private static <T> T getProperty(CacheService cache, Object entity, String... propertyPath) {
        if (null == entity) {
            return null;
        }
        int len = propertyPath.length;
        E.illegalArgumentIf(len < 1);
        Object lastEntity = null;
        for (int i = 0; i < len; ++i) {
            String prop = propertyPath[i];
            String lastProp = i == 0 ? prop : propertyPath[i - 1];
            if (entity instanceof ValueObject) {
                ValueObject vo = (ValueObject) entity;
                if (vo.isUDF()) {
                    entity = vo.value();
                }
            }
            if (entity.getClass().isArray()) {
                int lenArray = Array.getLength(entity);
                List list = new ArrayList(lenArray);
                for (int j = 0; j < lenArray; ++j) {
                    list.add(Array.get(entity, j));
                }
                entity = list;
            }
            if (entity instanceof List) {
                List<Class<?>> classList = findPropertyParameterizedType(lastEntity, lastProp);
                ListPropertyGetter getter = propertyHandlerFactory.createListPropertyGetter(classList.get(0));
                lastEntity = entity;
                if (N.isInt(prop)) {
                    entity = getter.get(lastEntity, prop);
                } else {
                    // See https://github.com/osglworks/java-tool/issues/20
                    entity = getter.get(lastEntity, 0);
                    // we injected a '.0' into the path thus we must retreat one step
                    i -= 1;
                }
            } else if (entity instanceof Map) {
                List<Class<?>> classList = null == lastEntity ? null : findPropertyParameterizedType(lastEntity, lastProp);
                if (null == classList) {
                    PropertyGetter getter = propertyGetter(cache, entity, prop, false);
                    lastEntity = entity;
                    entity = getter.get(entity, prop);
                } else {
                    MapPropertyGetter getter = propertyHandlerFactory.createMapPropertyGetter(classList.get(0), classList.get(1));
                    lastEntity = entity;
                    entity = getter.get(lastEntity, prop);
                }
            } else {
                PropertyGetter getter = propertyGetter(cache, entity, prop, false);
                lastEntity = entity;
                entity = getter.get(entity, null);
            }
            if (null == entity) {
                return null;
            }
        }
        return (T) entity;
    }

    private static String propertyGetterKey(Class c, String p, boolean requireField) {
        return S.builder("osgl:pg:").append(requireField ? "f:" : "").append(c.getName()).append(":").append(p).toString();
    }

    @SuppressWarnings("unchecked")
    private static PropertyGetter propertyGetter(CacheService cache, Object entity, String property, boolean requireField) {
        PropertyGetter propertyGetter;
        Class c = entity.getClass();
        String key = null;
        if (null != cache) {
            key = propertyGetterKey(c, property, requireField);
            propertyGetter = cache.get(key);
            if (null != propertyGetter) {
                return propertyGetter;
            }
        }
        propertyGetter = propertyHandlerFactory.createPropertyGetter(c, property, requireField);
        if (requireField) {
            propertyGetter.setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
        }
        if (null != cache) {
            cache.put(key, propertyGetter);
        }
        return propertyGetter;
    }

    private static Method findPropertyMethod(Class<?> c, String method) throws NoSuchMethodException {
        try {
            return c.getDeclaredMethod(method);
        } catch (NoSuchMethodException e) {
            return c.getMethod(method);
        }
    }

    private static Field findPropertyField(Class<?> c, String field) throws NoSuchFieldException {
        try {
            return c.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            return c.getField(field);
        }
    }

    private static List<Class<?>> findPropertyParameterizedType(Object entity, String prop) {
        Class<?> c = entity.getClass();
        Type type = null;
        while (null != c && !Object.class.equals(c)) {
            try {
                String p = S.capFirst(prop);
                String getter = "get" + p;
                Method m = findPropertyMethod(c, getter);
                m.setAccessible(true);
                type = m.getGenericReturnType();
            } catch (NoSuchMethodException e) {
                try {
                    Method m = findPropertyMethod(c, prop);
                    m.setAccessible(true);
                    type = m.getGenericReturnType();
                } catch (NoSuchMethodException e1) {
                    try {
                        Field f = findPropertyField(c, prop);
                        f.setAccessible(true);
                        type = f.getGenericType();
                    } catch (NoSuchFieldException e2) {
                        c = c.getSuperclass();
                        continue;
                    }
                }
            }
            if (null != type) {
                break;
            }
        }
        return null == type ? null : genericTypesOf(type);
    }

    private static List<Class<?>> genericTypesOf(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = cast(type);
            return findArgumentTypes(ptype);
        } else if (type instanceof Class) {
            Class classType = (Class) type;
            if (classType.isArray()) {
                return (List) C.list(classType.getComponentType());
            }
        }
        return null;
    }

    private static List<Class<?>> findArgumentTypes(ParameterizedType ptype) {
        List<Class<?>> retList = C.newList();
        Type[] ta = ptype.getActualTypeArguments();
        for (Type t : ta) {
            if (t instanceof Class) {
                retList.add((Class) t);
            } else if (t instanceof ParameterizedType) {
                retList.add((Class) ((ParameterizedType) t).getRawType());
            }
        }
        return retList;
    }

    @SuppressWarnings("unchecked")
    private static void setProperty(final CacheService cache, Object entity, final Object val, String... propertyPath) {
        E.NPE(entity);
        int len = propertyPath.length;
        E.illegalArgumentIf(len < 1);
        Object lastEntity = null;
        for (int i = 0; i < len; ++i) {
            String prop = propertyPath[i];
            String lastProp = i == 0 ? prop : propertyPath[i - 1];

            if (entity instanceof List) {
                List<Class<?>> classList = findPropertyParameterizedType(lastEntity, lastProp);
                if (i == len - 1) {
                    ListPropertySetter setter = propertyHandlerFactory.createListPropertySetter(classList.get(0));
                    setter.set(entity, val, prop);
                } else {
                    ListPropertyGetter getter = propertyHandlerFactory.createListPropertyGetter(classList.get(0));
                    getter.setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
                    lastEntity = entity;
                    entity = getter.get(lastEntity, prop);
                }
            } else if (entity instanceof Map) {
                List<Class<?>> classList = findPropertyParameterizedType(lastEntity, lastProp);
                if (i == len - 1) {
                    MapPropertySetter setter = propertyHandlerFactory.createMapPropertySetter(classList.get(0), classList.get(1));
                    setter.set(entity, val, prop);
                } else {
                    MapPropertyGetter getter = propertyHandlerFactory.createMapPropertyGetter(classList.get(0), classList.get(1));
                    getter.setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
                    lastEntity = entity;
                    entity = getter.get(lastEntity, prop);
                }
            } else {
                if (i == len - 1) {
                    PropertySetter setter = propertySetter(cache, entity, prop);
                    setter.set(entity, val, null);
                } else {
                    PropertyGetter getter = propertyGetter(cache, entity, prop, true);
                    lastEntity = entity;
                    entity = getter.get(entity, null);
                }
            }
        }
    }

    private static void setProperty(Object entity, Object val, String... propertyPath) {
        setProperty(null, entity, val, propertyPath);
    }

    private static String propertySetterKey(Class c, String p) {
        return S.builder("osgl:sg:").append(c.getName()).append(":").append(p).toString();
    }

    @SuppressWarnings("unchecked")
    private static PropertySetter propertySetter(CacheService cache, Object entity, String property) {
        if (null == entity) {
            return null;
        }
        PropertySetter propertySetter;
        Class c = entity.getClass();
        String key = null;
        if (null != cache) {
            key = propertySetterKey(c, property);
            propertySetter = cache.get(key);
            if (null != propertySetter) {
                return propertySetter;
            }
        }
        propertySetter = propertyHandlerFactory.createPropertySetter(c, property);
        if (null != cache) {
            cache.put(key, propertySetter);
        }
        return propertySetter;
    }

    public static <T> byte[] serialize(T obj) {
        E.NPE(obj);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    public static <T> T materialize(byte[] ba, Class<T> c) {
        E.NPE(ba);
        return materialize(ba);
    }

    public static <T> T deserialize(byte[] ba, Class<T> c) {
        return materialize(ba, c);
    }

    public static <T> T materialize(byte[] ba) {
        E.NPE(ba);
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            ObjectInputStream ois = new ObjectInputStream(bais);
            T t = (T) ois.readObject();
            ois.close();
            return t;
        } catch (IOException e) {
            throw E.ioException(e);
        } catch (ClassNotFoundException e) {
            throw E.unexpected(e);
        }
    }

    public static <T> T deserialize(byte[] ba) {
        return materialize(ba);
    }

    /**
     * Create an new array with specified array type and length
     *
     * @param model
     *         the model array
     * @param <T>
     *         the array component type
     * @return an new array with the same component type of model and length of model
     */
    public static <T> T[] newArray(T[] model) {
        return newArray(model, model.length);
    }

    /**
     * Create an new array with specified type and length
     *
     * @param model
     *         the model array
     * @param size
     *         the new array length
     * @param <T>
     *         the component type
     * @return an new array with the same type of model and length equals to size
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(T[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        Class<?> c = model.getClass();
        if (c == Object[].class) {
            return (T[]) new Object[size];
        }
        return (T[]) Array.newInstance(c.getComponentType(), size);
    }

    public static int[] newArray(int[] model) {
        return newArray(model, model.length);
    }

    public static int[] newArray(int[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new int[size];
    }


    public static byte[] newArray(byte[] model) {
        return newArray(model, model.length);
    }

    public static byte[] newArray(byte[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new byte[size];
    }

    public static char[] newArray(char[] model) {
        return newArray(model, model.length);
    }

    public static char[] newArray(char[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new char[size];
    }

    public static short[] newArray(short[] model) {
        return newArray(model, model.length);
    }

    public static short[] newArray(short[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new short[size];
    }

    public static long[] newArray(long[] model) {
        return newArray(model, model.length);
    }

    public static long[] newArray(long[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new long[size];
    }

    public static float[] newArray(float[] model) {
        return newArray(model, model.length);
    }

    public static float[] newArray(float[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new float[size];
    }

    public static double[] newArray(double[] model) {
        return newArray(model, model.length);
    }

    public static double[] newArray(double[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new double[size];
    }

    public static boolean[] newArray(boolean[] model) {
        return newArray(model, model.length);
    }

    public static boolean[] newArray(boolean[] model, int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        return new boolean[size];
    }

    private static final int EMPTY_ARRAY_LEN = 100;
    private static boolean[] _empty_boolean_array = new boolean[EMPTY_ARRAY_LEN];
    private static byte[] _empty_byte_array = new byte[EMPTY_ARRAY_LEN];
    private static short[] _empty_short_array = new short[EMPTY_ARRAY_LEN];
    private static char[] _empty_char_array = new char[EMPTY_ARRAY_LEN];
    private static int[] _empty_int_array = new int[EMPTY_ARRAY_LEN];
    private static long[] _empty_long_array = new long[EMPTY_ARRAY_LEN];
    private static float[] _empty_float_array = new float[EMPTY_ARRAY_LEN];
    private static double[] _empty_double_array = new double[EMPTY_ARRAY_LEN];
    private static Object[] _empty_object_array = new Object[EMPTY_ARRAY_LEN];

    public static boolean[] resetArray(boolean[] array) {
        _resetArray(array, _empty_boolean_array, array.length);
        return array;
    }

    public static byte[] resetArray(byte[] array) {
        _resetArray(array, _empty_byte_array, array.length);
        return array;
    }

    public static short[] resetArray(short[] array) {
        _resetArray(array, _empty_short_array, array.length);
        return array;
    }

    public static char[] resetArray(char[] array) {
        _resetArray(array, _empty_char_array, array.length);
        return array;
    }

    public static int[] resetArray(int[] array) {
        _resetArray(array, _empty_int_array, array.length);
        return array;
    }

    public static float[] resetArray(float[] array) {
        _resetArray(array, _empty_float_array, array.length);
        return array;
    }

    public static long[] resetArray(long[] array) {
        _resetArray(array, _empty_long_array, array.length);
        return array;
    }

    public static double[] resetArray(double[] array) {
        _resetArray(array, _empty_double_array, array.length);
        return array;
    }

    public static <T> T[] resetArray(T[] array) {
        _resetArray(array, _empty_object_array, array.length);
        return array;
    }

    public static Object resetArray(Object array) {
        Class<?> c = array.getClass();
        if (char[].class == c) {
            return resetArray((char[]) array);
        } else if (byte[].class == c) {
            return resetArray((byte[]) array);
        } else if (int[].class == c) {
            return resetArray((int[]) array);
        } else if (double[].class == c) {
            return resetArray((double[]) array);
        } else if (long[].class == c) {
            return resetArray((long[]) array);
        } else if (boolean[].class == c) {
            return resetArray((boolean[]) array);
        } else if (float[].class == c) {
            return resetArray((float[]) array);
        } else if (short[].class == c) {
            return resetArray((short[]) array);
        }
        _resetArray(array, _empty_object_array, Array.getLength(array));
        return array;
    }

    private static void _resetArray(Object array, Object empty, int len) {
        if (len <= EMPTY_ARRAY_LEN) {
            System.arraycopy(empty, 0, array, 0, len);
        } else {
            int limit = len - EMPTY_ARRAY_LEN;
            for (int i = 0; i < limit; i += EMPTY_ARRAY_LEN) {
                System.arraycopy(empty, 0, array, i, EMPTY_ARRAY_LEN);
            }
            int reminder = len % EMPTY_ARRAY_LEN;
            if (0 == reminder) {
                reminder = EMPTY_ARRAY_LEN;
            }
            System.arraycopy(empty, 0, array, len - reminder, reminder);
        }
    }

    public static <T> T[] subarray(T[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        T[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static boolean[] subarray(boolean[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        boolean[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static byte[] subarray(byte[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        byte[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static short[] subarray(short[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        short[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static char[] subarray(char[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        char[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static int[] subarray(int[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        int[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static float[] subarray(float[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        float[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static long[] subarray(long[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        long[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static double[] subarray(double[] src, int begin, int end) {
        E.illegalArgumentIf(begin < 0);
        E.illegalArgumentIf(begin > end);
        int arrayLen = src.length;
        E.illegalArgumentIf(end > arrayLen);
        if (begin == end) {
            return newArray(src, 0);
        }
        double[] retArray = newArray(src, end - begin);
        System.arraycopy(src, begin, retArray, 0, end - begin);
        return retArray;
    }

    public static <T> T[] concat(T[] a, T t) {
        int l = a.length;
        T[] ret = Arrays.copyOf(a, l + 1);
        ret[l] = t;
        return ret;
    }

    public static <T> T[] concat(T[] a1, T[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        T[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static <T> T[] concat(T[] a1, T[] a2, T[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (T[] a : rest) {
            len += a.length;
        }
        T[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (T[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
                offset += la;
            }
        }

        return ret;
    }

    public static int[] concat(int[] a1, int[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        int[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static int[] concat(int[] a1, int[] a2, int[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (int[] a : rest) {
            len += a.length;
        }
        int[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (int[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static boolean[] concat(boolean[] a1, boolean[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        boolean[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static boolean[] concat(boolean[] a1, boolean[] a2, boolean[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (boolean[] a : rest) {
            len += a.length;
        }
        boolean[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (boolean[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static byte[] concat(byte[] a1, byte[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        byte[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static byte[] concat(byte[] a1, byte[] a2, byte[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (byte[] a : rest) {
            len += a.length;
        }
        byte[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (byte[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static short[] concat(short[] a1, short[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        short[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static short[] concat(short[] a1, short[] a2, short[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (short[] a : rest) {
            len += a.length;
        }
        short[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (short[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static char[] concat(char[] a1, char[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        char[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static char[] concat(char[] a1, char[] a2, char[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (char[] a : rest) {
            len += a.length;
        }
        char[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (char[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static long[] concat(long[] a1, long[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        long[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static long[] concat(long[] a1, long[] a2, long[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (long[] a : rest) {
            len += a.length;
        }
        long[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (long[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static float[] concat(float[] a1, float[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        float[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static float[] concat(float[] a1, float[] a2, float[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (float[] a : rest) {
            len += a.length;
        }
        float[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (float[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static double[] concat(double[] a1, double[] a2) {
        int l1 = a1.length;
        if (0 == l1) {
            return a2;
        }
        int l2 = a2.length;
        if (0 == l2) {
            return a1;
        }
        double[] ret = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, ret, l1, l2);
        return ret;
    }

    public static double[] concat(double[] a1, double[] a2, double[]... rest) {
        int l1 = a1.length, l2 = a2.length, l12 = l1 + l2, len = l12;
        for (double[] a : rest) {
            len += a.length;
        }
        double[] ret = Arrays.copyOf(a1, len);
        if (l2 > 0) {
            System.arraycopy(a2, 0, ret, l1, l2);
        }
        int offset = l12;
        for (double[] a : rest) {
            int la = a.length;
            if (la > 0) {
                System.arraycopy(a, 0, ret, offset, la);
            }
            offset += la;
        }

        return ret;
    }

    public static void fill(boolean element, boolean[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(byte element, byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(char element, char[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(short element, short[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(int element, int[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(float element, float[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(long element, long[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static void fill(double element, double[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static <T> void fill(T element, T[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = element;
        }
    }

    public static boolean[] reverse(boolean[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            boolean e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static byte[] reverse(byte[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            byte e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static char[] reverse(char[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            char e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static short[] reverse(short[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            short e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static int[] reverse(int[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            int e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static float[] reverse(float[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            float e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static long[] reverse(long[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            long e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static double[] reverse(double[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            double e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static Object[] reverse(Object[] a) {
        int len = a.length;
        for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
            Object e = a[i];
            a[i] = a[j];
            a[j] = e;
        }
        return a;
    }

    public static boolean[] asPrimitive(Boolean[] oa) {
        int len = oa.length;
        boolean[] pa = new boolean[len];
        for (int i = 0; i < len; ++i) {
            Boolean O = oa[i];
            pa[i] = null == O ? false : O;
        }
        return pa;
    }

    public static Boolean[] asObject(boolean[] pa) {
        int len = pa.length;
        Boolean[] oa = new Boolean[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static byte[] asPrimitive(Byte[] oa) {
        int len = oa.length;
        byte[] pa = new byte[len];
        for (int i = 0; i < len; ++i) {
            Byte O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Byte[] asObject(byte[] pa) {
        int len = pa.length;
        Byte[] oa = new Byte[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static char[] asPrimitive(Character[] oa) {
        int len = oa.length;
        char[] pa = new char[len];
        for (int i = 0; i < len; ++i) {
            Character O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Character[] asObject(char[] pa) {
        int len = pa.length;
        Character[] oa = new Character[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static short[] asPrimitive(Short[] oa) {
        int len = oa.length;
        short[] pa = new short[len];
        for (int i = 0; i < len; ++i) {
            Short O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Short[] asObject(short[] pa) {
        int len = pa.length;
        Short[] oa = new Short[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static int[] asPrimitive(Integer[] oa) {
        int len = oa.length;
        int[] pa = new int[len];
        for (int i = 0; i < len; ++i) {
            Integer O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Integer[] asObject(int[] pa) {
        int len = pa.length;
        Integer[] oa = new Integer[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static long[] asPrimitive(Long[] oa) {
        int len = oa.length;
        long[] pa = new long[len];
        for (int i = 0; i < len; ++i) {
            Long O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Long[] asObject(long[] pa) {
        int len = pa.length;
        Long[] oa = new Long[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static float[] asPrimitive(Float[] oa) {
        int len = oa.length;
        float[] pa = new float[len];
        for (int i = 0; i < len; ++i) {
            Float O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Float[] asObject(float[] pa) {
        int len = pa.length;
        Float[] oa = new Float[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    public static double[] asPrimitive(Double[] oa) {
        int len = oa.length;
        double[] pa = new double[len];
        for (int i = 0; i < len; ++i) {
            Double O = oa[i];
            pa[i] = null == O ? 0 : O;
        }
        return pa;
    }

    public static Double[] asObject(double[] pa) {
        int len = pa.length;
        Double[] oa = new Double[len];
        for (int i = 0; i < len; ++i) {
            oa[i] = pa[i];
        }
        return oa;
    }

    /**
     * Pickup a random enum value from the enum type.
     *
     * @param enumType the enum type
     * @param <T> the type parameter
     * @return the random enum value
     */
    public static <T extends Enum> T random(Class<T> enumType) {
        return random(enumType.getEnumConstants());
    }

    /**
     * Returns a random element picked from `t1` and elements in `ta`.
     *
     * @param t1 the first element
     * @param ta the rest elements
     * @param <T> type parameter
     * @return the random picked up element.
     */
    @SafeVarargs
    public static <T> T random(T t1, T... ta) {
        int l = ta.length;
        if (l == 0) return t1;
        int i = ThreadLocalRandom.current().nextInt(l + 1);
        if (i == l) return t1;
        return ta[i];
    }

    /**
     * The secure version of {@link #random(Object, Object[])}.
     */
    public static <T> T secureRandom(T t1, T... ta) {
        int l = ta.length;
        if (l == 0) return t1;
        int i = new SecureRandom().nextInt(l + 1);
        if (i == l) return t1;
        return ta[i];
    }

    /**
     * Returns a random element picked from elements in `ta`.
     *
     * @param ta the elements
     * @param <T> type parameter
     * @return the random picked up element.
     */
    public static <T> T random(T[] ta) {
        int l = ta.length;
        if (0 == l) return null;
        int i = ThreadLocalRandom.current().nextInt(l);
        return ta[i];
    }

    /**
     * The secure version of {@link #random(Object[])}.
     */
    public static <T> T secureRandom(T[] ta) {
        int l = ta.length;
        if (0 == l) return null;
        int i = new SecureRandom().nextInt(l);
        return ta[i];
    }

    /**
     * Returns a random element picked from elements in a `list`.
     *
     * @param list the element list
     * @param <T> type parameter
     * @return the random picked up element.
     */
    public static <T> T random(List<T> list) {
        int l = list.size();
        if (0 == l) return null;
        int i = ThreadLocalRandom.current().nextInt(l);
        return list.get(i);
    }

    /**
     * Create a list contains random selected elements in the `list` specified.
     * @param list the list
     * @param <T> the type parameter
     * @return a list contains random selected elements in `list`
     */
    public static <T> List<T> randomSubList(List<T> list) {
        return randomSubList(list, 0);
    }

    /**
     * Create a list contains random selected elements in the `list` specified.
     * @param list the list
     * @param minSize the minimum number of elements in the result list
     * @param <T> the type parameter
     * @return a list contains random selected elements in `list`
     */
    public static <T> List<T> randomSubList(List<T> list, int minSize) {
        List<T> copy = C.newList(list);
        int listSize = list.size();
        E.illegalArgumentIf(minSize >= listSize || minSize < 0);
        Random r = new SecureRandom();
        int randomSize = minSize + r.nextInt(copy.size() - minSize);
        int toBeRemoved = listSize - randomSize;
        while (toBeRemoved-- > 0) {
            int i = r.nextInt(copy.size());
            copy.remove(i);
        }
        return copy;
    }

    /**
     * The secure version of {@link #random(List)}.
     */
    public static <T> T secureRandom(List<T> list) {
        int l = list.size();
        if (0 == l) return null;
        int i = new SecureRandom().nextInt(l);
        return list.get(i);
    }

    /**
     * Returns a random element picked from elements in a `range`.
     *
     * @param range the range
     * @param <T> type parameter
     * @return the random picked up element.
     */
    public static <T> T random(C.Range<T> range) {
        int n = ThreadLocalRandom.current().nextInt(range.size()) + 1;
        return range.tail(n).head();
    }

    /**
     * The secure version of {@link #random(C.Range)}.
     */
    public static <T> T secureRandom(C.Range<T> range) {
        int n = new SecureRandom().nextInt(range.size()) + 1;
        return range.tail(n).head();
    }

    /**
     * Alias of {@link S#random()}
     */
    public static String randomStr() {
        return S.random();
    }

    /**
     * Alias of {@link S#random(int)}
     */
    public static String randomStr(int len) {
        return S.random(len);
    }

    public static <T> T NPE(T o) {
        E.NPE(o);
        return o;
    }

    public static Class<?> commonSuperTypeOf(Object o1, Object o2, Object... others) {
        Class c1 = null == o1 ? null : o1.getClass();
        Class c2 = null == o2 ? null : o2.getClass();
        c1 = commonSuperTypeOf_(c1, c2);
        if (Object.class == c1) {
            return c1;
        }
        for (int i = others.length - 1; i >= 0; --i) {
            Object o = others[i];
            if (null == o) {
                continue;
            }
            Class c = o.getClass();
            c1 = commonSuperTypeOf_(c1, c);
            if (Object.class == c1) {
                return c1;
            }
        }
        return c1;
    }

    public static Class<?> commonSuperTypeOf(Collection<?> objects) {
        if (objects.isEmpty()) {
            return Object.class;
        }
        Class c1 = null;
        for (Object o : objects) {
            if (o == null) {
                continue;
            }
            Class c = o.getClass();
            c1 = commonSuperTypeOf_(c1, c);
            if (c1 == Object.class) {
                return c1;
            }
        }
        if (null == c1) {
            c1 = Object.class;
        }
        return c1;
    }

    private static Class<?> commonSuperTypeOf_(Class<?> c1, Class<?> c2) {
        if (c1 == c2) {
            return c1;
        }
        if (null == c1) {
            return c2;
        }
        if (null == c2) {
            return c1;
        }
        if (c1.isAssignableFrom(c2)) {
            return c1;
        }
        if (c2.isAssignableFrom(c1)) {
            return c2;
        }
        Class superClass = c1.getSuperclass();
        while (null != superClass && superClass != Object.class) {
            if (superClass.isAssignableFrom(c2)) {
                return superClass;
            }
            superClass = superClass.getSuperclass();
        }
        Set<Class> interfaces = interfacesOf(c1);
        boolean isComparable = false;
        boolean isSerializable = false;
        boolean isCloneable = false;
        for (Class intf : interfaces) {
            if (intf == Comparable.class) {
                isComparable = true;
                continue;
            }
            if (intf == Serializable.class) {
                isSerializable = true;
                continue;
            }
            if (intf == Cloneable.class) {
                isCloneable = true;
            }
            if (intf.isAssignableFrom(c2)) {
                return intf;
            }
        }
        if (isComparable) {
            return Comparable.class;
        }
        if (isSerializable) {
            return Serializable.class;
        }
        if (isCloneable) {
            return Cloneable.class;
        }
        return Object.class;
    }

    /**
     * The default thread factory
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (!t.isDaemon())
                t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    private static ExecutorService _exec = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory() {
    });

    /**
     * Execute callback asynchronously after delay specified
     *
     * @param callback
     *         the callback function to be executed
     * @param milliseconds
     *         the delay
     * @param <T>
     *         return type
     * @return the result of the callback
     */
    public static <T> Future<T> async(final F0<T> callback, final int milliseconds) {
        return _exec.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                Thread.sleep(milliseconds);
                return callback.apply();
            }
        });
    }

    private static ContextLocal.Factory clf;

    static {
        String clfCls = System.getProperty(ContextLocal.CONF_CONTEXT_LOCAL_FACTORY);
        if (null == clfCls) {
            clf = ContextLocal.Factory.Predefined.defaultFactory();
        } else {
            Option<ContextLocal.Factory> fact = safeNewInstance(clfCls);
            if (fact.isDefined()) {
                clf = fact.get();
            } else {
                clf = ContextLocal.Factory.Predefined.defaultFactory();
            }
        }

    }

    public static <T> ContextLocal<T> contextLocal() {
        return clf.create();
    }

    public static <T> ContextLocal<T> contextLocal(ContextLocal.InitialValueProvider<T> ivp) {
        return clf.create(ivp);
    }

    private static Set<String> standardsAnnotationMethods = C.newSet(C.list("equals", "hashCode", "toString", "annotationType", "getClass"));

    private static boolean isStandardAnnotationMethod(Method m) {
        return standardsAnnotationMethods.contains(m.getName());
    }

    /**
     * Evaluate Annotation properties
     *
     * @param anno
     *         the annotation instance
     * @return a Map contains annotation instance properties
     */
    public static Map<String, Object> evaluate(Annotation anno) {
        Map<String, Object> properties = new HashMap<String, Object>();
        Class<? extends Annotation> annoClass = anno.annotationType();
        Method[] ma = annoClass.getMethods();
        for (Method m : ma) {
            if (isStandardAnnotationMethod(m)) {
                continue;
            }
            properties.put(m.getName(), $.invokeVirtual(anno, m));
        }
        return properties;
    }

    /**
     * A stage of object for mapping operation
     */
    public static class _MappingStage {

        public class __SpecialMappingStage {
            private String from;

            private __SpecialMappingStage(String sourceField) {
                E.illegalArgumentIf(S.blank(sourceField));
                this.from = sourceField;
            }

            public _MappingStage to(String toField) {
                E.illegalArgumentIf(S.blank(toField));
                _MappingStage stage = _MappingStage.this;
                if (null == stage.specialMappings) {
                    stage.specialMappings = new HashMap<>();
                }
                stage.specialMappings.put(toField, from);

                return stage;
            }
        }

        private Object source;
        private Function<Class, ?> instanceFactory = OsglConfig.globalInstanceFactory();
        private Function keyTransformer;
        private Map<Class, Object> hints = C.EMPTY_MAP;
        private DataMapper.MappingRule rule = STRICT_MATCHING;
        private DataMapper.Semantic semantic;
        private TypeConverterRegistry converterRegistry;
        private String filterSpec;
        private boolean ignoreError;
        private boolean ignoreGlobalFilter;
        private Class<?> rootClass = Object.class;
        private ParameterizedType targetGenericType;
        private Map<String, String> specialMappings;

        private _MappingStage(Object source, DataMapper.Semantic semantic) {
            this.source = $.requireNotNull(source);
            this.semantic = $.requireNotNull(semantic);
        }

        /**
         * Change mapping semantic of this mapping stage.
         *
         * @param semantic
         *         the new {@link DataMapper.Semantic mapping semantic} to be used
         * @return this mapping stage for chained call
         */
        public _MappingStage semantic(DataMapper.Semantic semantic) {
            this.semantic = $.requireNotNull(semantic);
            return this;
        }

        /**
         * Specify the root class of target bean when getting the fields.
         *
         * For example, if your entity class extends from `ModelBase`, and
         * you do not want to copy the `ModelBase` defined fields, then you
         * can set `ModelBase.class` as root case:
         *
         * ```java
         * $.copy(source).rootClass(ModelBase.class).to(target);
         * ```
         *
         * @param rootClass
         *         The root class
         * @return this mapping stage for chained call
         * @see #fieldsOf(Class, Class, boolean)
         */
        public _MappingStage rootClass(Class<?> rootClass) {
            this.rootClass = null == rootClass ? Object.class : rootClass;
            return this;
        }

        /**
         * Specify target generic type by {@link TypeReference}.
         *
         * This make is it easy to specify a generic type for target generic type. For example,
         *
         * ```java
         * Map<String, User> userMap = $.map(sourceMap)
         * .targetGenericType(new TypeReference(Map<String, User>) {})
         * .to(new HashMap());
         * ```
         *
         * @param typeReference
         *         the type reference.
         * @return this mapping stage for chained call
         * @see #targetGenericType(Type)
         */
        public _MappingStage targetGenericType(TypeReference typeReference) {
            return targetGenericType(typeReference.getType());
        }

        /**
         * Specify target generic type.
         *
         * This might be helpful if the target type is a container, e.g. Map or List. With
         * the generic type specified the data mapper can handle the type matching
         * for the container's component types, like key and value type of a map or element
         * type of a list.
         *
         * Note the type specified must be an instance of {@link ParameterizedType}
         * in order to effect.
         *
         * @param type
         *         the target generic type
         * @return this mapping stage for chained call
         */
        public _MappingStage targetGenericType(Type type) {
            if (type instanceof ParameterizedType) {
                // we need Parameterized type only
                this.targetGenericType = (ParameterizedType) type;
            }
            return this;
        }

        /**
         * This method is deprecated. Please use {@link #withHeadMapping(Map)} instead
         */
        @Deprecated
        public _MappingStage map(Map<String, String> mapping) {
            return withHeadMapping(mapping);
        }

        public _MappingStage withHeadMapping(Map<String, String> mapping) {
            if (mapping.isEmpty()) {
                return this;
            }
            Map<String, String> fliped = C.Map(mapping).flipped();
            if (specialMappings != null) {
                specialMappings.putAll(fliped);
            } else {
                specialMappings = C.newMap(fliped);
            }
            return this;
        }

        /**
         * This is deprecated. Please use {@link #mapHead(String)} instead
         */
        @Deprecated
        public __SpecialMappingStage map(String sourceField) {
            return mapHead(sourceField);
        }

        public __SpecialMappingStage mapHead(String sourceField) {
            return new __SpecialMappingStage(sourceField);
        }

        /**
         * Apply type converters during the mapping process. If the mapping semantic is {@link DataMapper.Semantic#MAP map}
         * then it allows to do type convert if source type cannot be assigned to target type.
         *
         * By default it will use built-in or registered type converters in the global
         * {@link TypeConverterRegistry#INSTANCE type converter registry}.
         *
         * This method allows it to provide additional type converters in addition to built-in or registered
         * type converters in the global registry.
         *
         * @param converter
         *         the first type converter
         * @param otherConverters
         *         the rest type converters
         * @return this mapping stage for chained call
         */
        public _MappingStage withConverter(TypeConverter converter, TypeConverter... otherConverters) {
            if (null == converterRegistry) {
                converterRegistry = new TypeConverterRegistry();
            }
            converterRegistry.register(converter);
            for (TypeConverter other : otherConverters) {
                converterRegistry.register(other);
            }
            return this;
        }

        /**
         * Apply a collection of {@link TypeConverter type converters} during
         * the mapping process.
         *
         * @param converters
         *         A collection of type converters.
         * @return this mapping stage for chained call
         */
        public _MappingStage withConverter(Collection<TypeConverter> converters) {
            if (null == converterRegistry) {
                converterRegistry = new TypeConverterRegistry();
            }
            for (TypeConverter converter : converters) {
                converterRegistry.register(converter);
            }
            return this;
        }

        /**
         * Add conversion hint associated with class.
         *
         * A conversion hint could be used to manipulate conversion process employed
         * by the mapping process when {@link #semantic}. For example if the mapping
         * process might need to map a string to a Date, it can specify the format
         * as:
         *
         * ```java
         * $.map(postData).conversionHint(Date.class, "yyyy-MM-dd").to(Order);
         * ```
         *
         * The above code will ensure the string property (if named matched)
         * be parsed into `Date` using format "yyyy-MM-dd".
         *
         * @param type
         * @param hint
         * @return this mapping stage for chained call
         * @see #convert(Object)
         */
        public _MappingStage conversionHint(Class<?> type, Object hint) {
            E.NPE(type, hint);
            if (this.hints == C.EMPTY_MAP) {
                this.hints = new HashMap<>();
            }
            this.hints.put(type, hint);
            return this;
        }

        /**
         * Add conversion hints indexed by class.
         *
         * @param conversionHints
         *         A map of conversion hints
         * @return this mapping stage for chained call
         * @see #conversionHint(Class, Object)
         */
        public _MappingStage conversionHints(Map<Class, Object> conversionHints) {
            this.hints = ensureGet(conversionHints, C.EMPTY_MAP);
            return this;
        }

        /**
         * Specify a function used to create new instance during mapping process.
         *
         * If not specified, then it will use {@link OsglConfig#globalInstanceFactory()}
         *
         * @param instanceFactory
         *         the new instance factory
         * @return this mapping stage for chained call
         */
        public _MappingStage instanceFactory(Function<Class, ?> instanceFactory) {
            this.instanceFactory = instanceFactory;
            return this;
        }

        /**
         * Indicate field mapping shall be exactly string equal test.
         *
         * @return this mapping stage for chained call
         */
        public _MappingStage strictMatching() {
            this.rule = STRICT_MATCHING;
            return this;
        }

        /**
         * Alias of {@link #keywordMatching()}
         */
        public _MappingStage looseMatching() {
            this.rule = KEYWORD_MATCHING;
            return this;
        }

        /**
         * Indicate field mapping shall based on {@link Keyword} match.
         *
         * Say the following names are assumed to be matching to each other:
         *
         * * `fooBar`
         * * `foo_bar`
         *
         * @return this mapping stage for chained call
         */
        public _MappingStage keywordMatching() {
            this.rule = KEYWORD_MATCHING;
            return this;
        }

        /**
         * Specify filter spec for this mapping process.
         *
         * Example of filter spec:
         *
         * * `-foo` - do not map on field named `foo`
         * * `-foo.bar` - do not map on field named `bar` in a embedded field named `foo`
         * * `name,email` - map only fields `name` and `email`
         *
         * @param filterSpec
         *         the filter spec indicates which fields shall be subject or waived in the mapping process.
         * @return this mapping stage for chained call.
         */
        public _MappingStage filter(String filterSpec) {
            this.filterSpec = filterSpec;
            return this;
        }

        /**
         * Indicate this mapping process shall ignore global filter settings in {@link OsglConfig}.
         *
         * @return this mapping stage
         * @see OsglConfig#addGlobalMappingFilter(String)
         * @see OsglConfig#addGlobalMappingFilters(String, String...)
         */
        public _MappingStage ignoreGlobalFilter() {
            this.ignoreGlobalFilter = true;
            return this;
        }

        /**
         * Specify special name mapping rules.
         *
         * The rules shall be a string to string map with key be the target field name,
         * and value be the source field name. For example, if it shall map `id` in source
         * bean to `no` in the target bean, the map shall be
         *
         * ```
         * Map<String, String> rules = new HashMap();
         * rules.put("no", "id");
         * $.deepCopy(src).withSpecialNameMappings(rules).to(tgt);
         * ```
         *
         * @param specialNameMappings
         *         a Map of name mapping rules.
         * @return this mapping stage
         */
        public _MappingStage withSpecialNameMappings(Map<String, String> specialNameMappings) {
            if (null == specialNameMappings || specialNameMappings.isEmpty()) {
                return this;
            }
            this.specialMappings = specialNameMappings;
            return this;
        }

        public _MappingStage withKeyTransformer($.Function transformer) {
            this.keyTransformer = transformer;
            return this;
        }

        public _MappingStage withKeyTransformer(Keyword.Style targetStyle) {
            this.keyTransformer = DataMapper.keyTransformer(targetStyle);
            return this;
        }

        /**
         * Indicate ignore exceptions encountered during mapping process.
         *
         * For certain case you know that copy between source and target will fail as
         * they were not the same type object and some fields might encounter type mismatch
         * issue, however you do want to copy the fields that are able to be copied, in
         * this scenario you can dictate copy process to ignore error:
         *
         * ```java
         * $.deepCopy(source).ignoreError().to(target);
         * ```
         *
         * @return this mapping stage for chained call
         */
        public _MappingStage ignoreError() {
            this.ignoreError = true;
            return this;
        }

        /**
         * Commit the mapping stage and trigger the mapping process, return the target been copied.
         *
         * Generally the returned instance should be the same instance of `to` passed in. However
         * when copied to an array which has less elements than the source array or collection,
         * an new array instance with the same component type with `to` will be created and populated.
         * The original `to` array will be left unchanged. The return value is the new array instance.
         *
         * @param target
         *         target object
         * @param <T>
         *         the generic type parameter of the target.
         * @return the target been copied/mapped, might not be the same instance of `to` if `to` is an array
         */
        public <T> T to(T target) {
            return (T) new DataMapper(source, target, targetGenericType, rule, semantic, filterSpec, ignoreError, ignoreGlobalFilter, keyTransformer, hints, instanceFactory, converterRegistry, rootClass, specialMappings).getTarget();
        }

        /**
         * Commit the mapping stage and trigger the mapping process on an new instance created from
         * the `targetClass` specified.
         *
         * @param targetClass
         *         the class used to create target instance
         * @param <T>
         *         the generic type parameter of targetClass
         * @return the instance been copied/mapped
         * @see #to(Object)
         */
        public <T> T to(Class<T> targetClass) {
            if (targetClass.isArray()) {
                throw E.unsupport("target class must not be an array");
            }
            return (T) to(instanceFactory.apply(targetClass));
        }

    }

    /**
     * Prepare a {@link org.osgl.util.DataMapper.Semantic#DEEP_COPY} from `source` to an new instance with
     * the same time of `source`
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage} that are ready to do deep copy to
     * new instance of `source` type
     */
    public static _MappingStage clone(Object source) {
        return new _MappingStage(source, DataMapper.Semantic.DEEP_COPY);
    }

    /**
     * Returns clone of a given `source` object.
     *
     * This is done by doing a {@link org.osgl.util.DataMapper.Semantic#DEEP_COPY} from
     * `source` object to an new instance of `source` type
     *
     * @param source
     *         the object to be clone
     * @param <T>
     *         the type parameter of the object class
     * @return the clone of `source`
     */
    public static <T> T cloneOf(T source) {
        return cloneOf(source, OsglConfig.globalInstanceFactory());
    }

    public static String[] cloneOf(String[] array) {
        int len = array.length;
        if (0 == len) {
            return S.EMPTY_ARRAY;
        }
        String[] clone = new String[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static boolean[] cloneOf(boolean[] array) {
        int len = array.length;
        if (0 == len) {
            return new boolean[0];
        }
        boolean[] clone = new boolean[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static short[] cloneOf(short[] array) {
        int len = array.length;
        if (0 == len) {
            return new short[0];
        }
        short[] clone = new short[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static byte[] cloneOf(byte[] array) {
        int len = array.length;
        if (0 == len) {
            return new byte[0];
        }
        byte[] clone = new byte[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static char[] cloneOf(char[] array) {
        int len = array.length;
        if (0 == len) {
            return new char[0];
        }
        char[] clone = new char[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static int[] cloneOf(int[] array) {
        int len = array.length;
        if (0 == len) {
            return new int[0];
        }
        int[] clone = new int[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static float[] cloneOf(float[] array) {
        int len = array.length;
        if (0 == len) {
            return new float[0];
        }
        float[] clone = new float[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static long[] cloneOf(long[] array) {
        int len = array.length;
        if (0 == len) {
            return new long[0];
        }
        long[] clone = new long[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    public static double[] cloneOf(double[] array) {
        int len = array.length;
        if (0 == len) {
            return new double[0];
        }
        double[] clone = new double[len];
        System.arraycopy(array, 0, clone, 0, len);
        return clone;
    }

    /**
     * Returns clone of a given `source` object.
     *
     * This is done by doing a {@link org.osgl.util.DataMapper.Semantic#DEEP_COPY} from
     * `source` object to an new instance of `source` type
     *
     * @param source
     *         the object to be clone
     * @param instanceFactory
     *         A function that is used to create new instance of the clone
     * @param <T>
     *         the type parameter of the object class
     * @return the clone of `source`
     */
    public static <T> T cloneOf(T source, Function<Class, ?> instanceFactory) {
        if (OsglConfig.isSingleton(source)) {
            return source;
        }
        if (source instanceof Cloneable) {
            return (T) $.invokeVirtual(source, "clone");
        }
        Class type = source.getClass();
        Object target;
        if (type.isArray()) {
            int len = Array.getLength(source);
            target = Array.newInstance(type.getComponentType(), len);
        } else {
            target = instanceFactory.apply(source.getClass());
        }
        return (T) deepCopy(source).to(target);
    }

    /**
     * Prepare a Mapping operation with {@link DataMapper.Semantic#SHALLOW_COPY shallow copy} semantic
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage}
     */
    public static _MappingStage copy(Object source) {
        return new _MappingStage(source, SHALLOW_COPY);
    }

    /**
     * Prepare a Mapping operation with {@link DataMapper.Semantic#DEEP_COPY deep copy} semantic
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage}
     */
    public static _MappingStage deepCopy(Object source) {
        return new _MappingStage(source, DEEP_COPY);
    }

    /**
     * Prepare a Mapping operation with {@link DataMapper.Semantic#FLAT_COPY flat
     * copy} semantic
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage}
     */
    public static _MappingStage flatCopy(Object source) {
        return new _MappingStage(source, FLAT_COPY);
    }

    /**
     * Prepare a Mapping operation with {@link DataMapper.Semantic#MERGE merge} semantic
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage}
     */
    public static _MappingStage merge(Object source) {
        return new _MappingStage(source, MERGE);
    }


    /**
     * Prepare a Mapping operation with {@link DataMapper.Semantic#MAP map} semantic
     * and {@link DataMapper.MappingRule#KEYWORD_MATCHING keyword matching}
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage}
     */
    public static _MappingStage map(Object source) {
        return new _MappingStage(source, MAP).keywordMatching();
    }

    /**
     * Prepare a Mapping operation with {@link DataMapper.Semantic#MERGE_MAP merge map} semantic
     * and {@link DataMapper.MappingRule#KEYWORD_MATCHING keyword matching}
     *
     * @param source
     *         the source object
     * @return a {@link _MappingStage}
     */
    public static _MappingStage mergeMap(Object source) {
        return new _MappingStage(source, MERGE_MAP).keywordMatching();
    }

    // --- eof common utilities

    /**
     * Namespace to configure or extend OSGL _ utilities
     */
    public static final class Conf {
        private final List<Function<Object, Boolean>> boolTesters = new ArrayList<Function<Object, Boolean>>();

        /**
         * Register a boolean tester. A boolean tester is a {@link Function Function&lt;Object, Boolean&gt;} type function
         * that applied to {@code Object} type parameter and returns a boolean value of the Object been tested. It
         * should throw out {@link NotAppliedException} if the type of the object been tested is not recognized.
         * there is no need to test if the parameter is {@code null} in the tester as the utility will garantee the
         * object passed in is not null
         * <pre>
         *  Conf.registerBoolTester(new F1&lt;Boolean, Object&gt;() {
         *      {@literal @}Override
         *      public Boolean apply(Object o) {
         *          if (o instanceof Score) {
         *              return ((Score)o).intValue() &gt; 60;
         *          }
         *          if (o instanceof Person) {
         *              return ((Person)o).age() &gt; 16;
         *          }
         *          ...
         *          // since we do not recognize the object type, raise the NotAppliedException out
         *          throw new org.osgl.E.NotAppliedException();
         *      }
         *  });
         * </pre>
         *
         * @param tester
         *         the tester function takes an object as parameter and returns boolean
         * @return the {@link Conf} instance
         */
        public Conf registerBoolTester(Function<Object, Boolean> tester) {
            E.NPE(tester);
            boolTesters.add(tester);
            return this;
        }
    }

    public static final Conf conf = new Conf();

    public static final int JAVA_VERSION = VM.VERSION;
    public static final boolean IS_SERVER = VM.IS_SERVER;
    public static final boolean IS_64 = VM.IS_64;
    public static final OS OS = org.osgl.util.OS.get();

    /**
     * The namespace to aggregate predefined core functions
     */
    public enum F {
        ;

        /**
         * Return a one variable function that throw out a {@link Break} with payload specified when a predicate return
         * <code>true</code> on an element been tested
         *
         * @param predicate
         *         the predicate function that takes T type argument
         * @param payload
         *         the payload to be passed into the {@link Break} if predicate returns {@code true}
         * @param <P>
         *         the type of the payload object
         * @param <T>
         *         the type of the object to be consumed by the predicate
         * @return an new function that takes T argument and apply it to the predicate. If the result is {@code true}
         * then throw out the {@code Break} with payload specified
         * @since 0.2
         */
        public static <P, T> F1<T, Void> breakIf(final Function<? super T, Boolean> predicate, final P payload) {
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
         * @param predicate
         *         the predicate function
         * @param <T>
         *         the type of the object consumed by the predicate
         * @return a new function that applies the predicate and if the result is {@code true} then
         * throw out a {@link Break} with payload of the object been consumed
         * @since 0.2
         */
        public static <T> F1<T, Void> breakIf(final Function<? super T, Boolean> predicate) {
            return new F1<T, Void>() {
                @Override
                public Void apply(T t) {
                    if (predicate.apply(t)) {
                        throw breakOut(t);
                    }
                    return null;
                }
            };
        }

        /**
         * Return a two variables function that throw out a {@link Break} with payload specified when
         * a two variables predicate return <code>true</code> on an element been tested
         *
         * @param predicate
         *         the function test the arguments
         * @param payload
         *         the payload to be thrown out if predicate function returns {@code true} on
         *         the argument
         * @param <P>
         *         the payload type
         * @param <T1>
         *         the type of the first argument
         * @param <T2>
         *         the type of the second argument
         * @return a function that apply predicate to arguments and throw {@link Break} with payload
         * specified
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <P, T1, T2> F2<T1, T2, Void> breakIf(final Func2<? super T1, ? super T2, Boolean> predicate,
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
         * @param predicate
         *         the predicate function check if a break should be raised
         * @param <T1>
         *         the type of argument 1
         * @param <T2>
         *         the type of argument 2
         * @return the function that raise {@link Break} if predicate function says {@code true} on the
         * two arguments
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T1, T2> F2<T1, T2, Void> breakIf(final Func2<? super T1, ? super T2, Boolean> predicate) {
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
         * @param predicate
         *         the predicate function that takes three arguments and returns a boolean type value
         * @param payload
         *         the payload object to be
         * @param <P>
         *         Generic type for payload
         * @param <T1>
         *         generic type of the first argument taken by predicate
         * @param <T2>
         *         generic type of the second argument taken by predicate
         * @param <T3>
         *         generic type of the thrid argument taken by predicate
         * @return A function of {@link F3 F3&lt;T1, T2, T3, Void&gt;} type
         * @since 0.2
         */
        public static <P, T1, T2, T3> F3<T1, T2, T3, Void> breakIf(
                final Func3<? super T1, ? super T2, ? super T3, Boolean> predicate,
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
         * @param predicate
         *         the predicate function that takes three arguments and returns a {@code boolean} type value
         * @param <T1>
         *         the generic type of the argument 1
         * @param <T2>
         *         the generic type of the argument 2
         * @param <T3>
         *         the generic type of the argument 3
         * @return a function that check on three arguments and throw out {@code true} if the check returns {@code true}
         * @since 0.2
         */
        public static <T1, T2, T3> F3<T1, T2, T3, Void> breakIf(
                final Func3<? super T1, ? super T2, ? super T3, Boolean> predicate
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
         * @param predicate
         *         the predicate function that takes four arguments and returns a {@code boolean} type value
         * @param payload
         *         the payload to be throw out if the predicate function returns {@code true} on given arguments
         * @param <P>
         *         the generic type of the payload
         * @param <T1>
         *         the generic type of the argument 1
         * @param <T2>
         *         the generic type of the argument 2
         * @param <T3>
         *         the generic type of the argument 3
         * @param <T4>
         *         the generic type of the argument 4
         * @return a function that check on four arguments and throw out payload if the check returns {@code true}
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4> F4<T1, T2, T3, T4, Void> breakIf(
                final Func4<? super T1, ? super T2, ? super T3, ? super T4, Boolean> predicate, final P payload
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
         * Return a four variables function that throw out a {@link Break} when the predicate returns
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         *
         * @param predicate
         *         the predicate function that takes four arguments and returns a {@code boolean} type value
         * @param <T1>
         *         the generic type of the argument 1
         * @param <T2>
         *         the generic type of the argument 2
         * @param <T3>
         *         the generic type of the argument 3
         * @param <T4>
         *         the generic type of the argument 4
         * @return a function that check on four arguments and throw out {@code true} if the check returns {@code true}
         * @since 0.2
         */
        public static <T1, T2, T3, T4> F4<T1, T2, T3, T4, Void> breakIf(
                final Func4<? super T1, ? super T2, ? super T3, ? super T4, Boolean> predicate
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
         * @param predicate
         *         the predicate function that takes five arguments and returns a {@code boolean} type value
         * @param payload
         *         the payload to be throw out if the predicate function returns {@code true} on given arguments
         * @param <P>
         *         the generic type of the payload
         * @param <T1>
         *         the generic type of the argument 1
         * @param <T2>
         *         the generic type of the argument 2
         * @param <T3>
         *         the generic type of the argument 3
         * @param <T4>
         *         the generic type of the argument 4
         * @param <T5>
         *         the generic type of the argument 5
         * @return a function that check on five arguments and throw out payload if the check returns {@code true}
         * @since 0.2
         */
        public static <P, T1, T2, T3, T4, T5> F5<T1, T2, T3, T4, T5, Void> breakIf(
                final Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Boolean> predicate,
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
         * Return a five variables function that throw out a {@link Break} when the predicate returns
         * <code>true</code> on an element been tested. There is no payload specified and the <code>Break</code>
         * will use test result i.e. <code>true</code> as the payload
         *
         * @param predicate
         *         the predicate function that takes five arguments and returns a {@code boolean} type value
         * @param <T1>
         *         the generic type of the argument 1
         * @param <T2>
         *         the generic type of the argument 2
         * @param <T3>
         *         the generic type of the argument 3
         * @param <T4>
         *         the generic type of the argument 4
         * @param <T5>
         *         the generic type of the argument 5
         * @return a function that check on five arguments and throw out {@code true} if the check returns {@code true}
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T1, T2, T3, T4, T5> F5<T1, T2, T3, T4, T5, Void> breakIf(
                final Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Boolean> predicate
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
         * Returns a composed {@link Predicate} function that for any given parameter, the test result is <code>true</code>
         * only when all of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates
         *         a collection of predicates that can be applied to a parameter and returns boolean value
         * @param <T>
         *         the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> Predicate<T> and(final Collection<Function<? super T, Boolean>> predicates) {
            if (predicates.isEmpty()) {
                return yes();
            }
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    for (Function<? super T, Boolean> cond : predicates) {
                        if (!cond.apply(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }

        /**
         * Returns a composed {@link Predicate} function that for any given parameter, the test result is <code>true</code>
         * only when all of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates
         *         an array of predicates that can be applied to a parameter and returns boolean value
         * @param <T>
         *         the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> Predicate<T> and(final Function<? super T, Boolean>... predicates) {
            if (predicates.length == 0) {
                return yes();
            }
            return and(C.listOf(predicates));
        }

        /**
         * Returns a composed {@link Predicate} function that for any given parameter, the test result is <code>true</code>
         * only when any one of the specified predicates returns <code>true</code> when applied to the parameter
         *
         * @param predicates
         *         a collection of predicates that can be applied to a parameter and returns boolean value
         * @param <T>
         *         the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> Predicate<T> or(final Collection<Function<? super T, Boolean>> predicates) {
            if (predicates.isEmpty()) {
                return no();
            }
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    for (Function<? super T, Boolean> cond : predicates) {
                        if (cond.apply(t)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        /**
         * Returns a composed {@link Predicate} function that for any given parameter, the test result is <code>true</code>
         * when any one of the specified predicates returns <code>true</code> on the parameter
         *
         * @param predicates
         *         an array of predicates that can be applied to a parameter and returns boolean value
         * @param <T>
         *         the type of the parameter the predicates applied to
         * @return a composed function
         * @since 0.2
         */
        public static <T> Predicate<T> or(final Function<? super T, Boolean>... predicates) {
            if (predicates.length == 0) {
                return no();
            }
            return or(C.listOf(predicates));
        }

        /**
         * Alias of {@link #or(Collection)}
         *
         * @param predicates
         *         the predicate functions
         * @param <T>
         *         the element type
         * @return a function that returns {@code true} if any one of the predicates returns {@code true}
         * on a given argument
         * @since 0.2
         */
        public static <T> Predicate<T> any(final Collection<Function<? super T, Boolean>> predicates) {
            return or(predicates);
        }

        /**
         * Alias of {@link #or(Function[])}
         *
         * @param predicates
         *         an array of predicate functions
         * @param <T>
         *         the argument type
         * @return the function that returns {@code true} if any one of the predicate function
         * returns {@code true}
         * @since 0.2
         */
        public static <T> Predicate<T> any(final Function<? super T, Boolean>... predicates) {
            return or(predicates);
        }

        /**
         * Negation of {@link #or(Collection)}
         *
         * @param predicates
         *         an iterable of predicate functions
         * @param <T>
         *         the generic type of the argument the predicate functions take
         * @return a function that apply the argument to all predicate functions and return
         * {@code true} if all of them return {@code false} on the argument, or
         * {@code false} if any one of them returns {@code true}
         * @since 0.2
         */
        public static <T> Predicate<T> none(final Collection<Function<? super T, Boolean>> predicates) {
            return negate(or(predicates));
        }

        /**
         * Negation of {@link #or(Function[])}
         *
         * @param predicates
         *         an array of predicate functions
         * @param <T>
         *         the generic type of the argument the predicate functions take
         * @return a function that apply the argument to all predicate functions and return
         * {@code true} if all of them return {@code false} on the argument, or
         * {@code false} if any one of them returns {@code true}
         * @since 0.2
         */
        public static <T> Predicate<T> none(final Function<? super T, Boolean>... predicates) {
            return negate(or(predicates));
        }

        /**
         * Returns a function that evaluate an argument's boolean value and negate the value.
         *
         * @param <T>
         *         the type of the argument the function applied to
         * @return the function returns true if an argument evaluated to false, or vice versa
         */
        public static <T> F1<T, Boolean> not() {
            return new F1<T, Boolean>() {
                @Override
                public Boolean apply(T t) throws NotAppliedException, Break {
                    return Lang.not(t);
                }
            };
        }

        /**
         * Returns a inverted function of {@link Bijection} which map from X to Y, and the
         * returned function map from Y to X. This function will call {@link Bijection#invert()}
         * to get the return function
         *
         * @param f
         *         the bijection function to be inverted
         * @param <X>
         *         the argument type, and the result type of the return function
         * @param <Y>
         *         the result type, and the argument type of the return function
         * @return the inverted function of input function {@code f}
         */
        public static <X, Y> Bijection<Y, X> invert(final Bijection<X, Y> f) {
            return f1(f.invert());
        }

        /**
         * Returns a negate function of the specified predicate function
         *
         * @param predicate
         *         the specified function that returns boolean value
         * @return the function that negate the specified predicate
         */
        public static F0<Boolean> negate(final Func0<Boolean> predicate) {
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
         * @param predicate
         *         the specified function that applied to the parameter and returns boolean value
         * @param <T>
         *         the type of the parameter to be applied
         * @return the function that negate the specified predicate
         */
        public static <T> Predicate<T> negate(final Function<? super T, Boolean> predicate) {
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    return !predicate.apply(t);
                }
            };
        }

        /**
         * Returns a negate function of the specified predicate that applied to 2 parameters and returns boolean value
         *
         * @param predicate
         *         the function applied to 2 params and return boolean value
         * @param <P1>
         *         type of param one
         * @param <P2>
         *         type of param two
         * @return the function that negate predicate specified
         */
        public static <P1, P2> F2<P1, P2, Boolean> negate(final Func2<? super P1, ? super P2, Boolean> predicate) {
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
         * @param predicate
         *         the function applied to 2 params and return boolean value
         * @param <P1>
         *         type of param one
         * @param <P2>
         *         type of param two
         * @param <P3>
         *         type of param three
         * @return the function that negate predicate specified
         */
        public static <P1, P2, P3> F3<P1, P2, P3, Boolean> negate(
                final Func3<? super P1, ? super P2, ? super P3, Boolean> predicate
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
         * @param predicate
         *         the function applied to 2 params and return boolean value
         * @param <P1>
         *         type of param one
         * @param <P2>
         *         type of param two
         * @param <P3>
         *         type of param three
         * @param <P4>
         *         type of param four
         * @return the function that negate predicate specified
         */
        public static <P1, P2, P3, P4> F4<P1, P2, P3, P4, Boolean> negate(
                final Func4<? super P1, ? super P2, ? super P3, ? super P4, Boolean> predicate
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
         * @param predicate
         *         the function applied to 2 params and return boolean value
         * @param <P1>
         *         type of param one
         * @param <P2>
         *         type of param two
         * @param <P3>
         *         type of param three
         * @param <P4>
         *         type of param four
         * @param <P5>
         *         type of param five
         * @return the function that negate predicate specified
         */
        public static <P1, P2, P3, P4, P5> F5<P1, P2, P3, P4, P5, Boolean> negate(
                final Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, Boolean> predicate
        ) {
            return new F5<P1, P2, P3, P4, P5, Boolean>() {
                @Override
                public Boolean apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    return !predicate.apply(p1, p2, p3, p4, p5);
                }
            };
        }

        /**
         * Return a provider that when called will return
         * the object specified
         *
         * @param obj
         *         the object to be returned when calling the returning function
         * @param <T>
         *         the object type
         * @return a provider function that returns the object specified
         */
        public static <T> $.Val<T> provides(final T obj) {
            return val(obj);
        }

        /**
         * A predefined forever true predicate which returns <code>true</code>
         * on any element been tested
         *
         * @see #yes()
         * @since 0.2
         */
        public static final Predicate TRUE = new Predicate() {
            @Override
            public boolean test(Object o) {
                return true;
            }
        };

        /**
         * A type-safe version of {@link #TRUE}
         *
         * @param <T>
         *         the argument type
         * @return a function that always returns {@code true}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> Predicate<T> yes() {
            return TRUE;
        }

        /**
         * A predefined forever FALSE predicate which always return
         * <code>false</code> for whatever element been tested
         *
         * @see #no()
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static final Predicate FALSE = negate(TRUE);

        /**
         * A type-safe version of {@link #FALSE}
         *
         * @param <T>
         *         the argument type
         * @return a function that always return {@code false}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> Predicate<T> no() {
            return FALSE;
        }

        /**
         * A predefined <code>Predicate</code> predicate test if the
         * element specified is <code>null</code> or {@link #NONE}.
         *
         * @since 0.2
         */
        public static final Predicate IS_NULL = new Predicate() {
            @Override
            public boolean test(Object o) {
                return null == o || NONE == o;
            }
        };

        /**
         * The type-safe version of {@link #IS_NULL}
         *
         * @param <T>
         *         the argument type
         * @return a function that check if an argument is {@code null} or {@code NONE}
         * @since 0.2
         */
        @SuppressWarnings({"unused", "unchecked"})
        public static <T> Predicate<T> isNull() {
            return IS_NULL;
        }

        /**
         * The type-safe version of {@link #IS_NULL}
         *
         * @param c
         *         the class that specifies the argument type
         * @param <T>
         *         the argument type
         * @return a function that check if the argument is {@code null} or {@code NONE}
         * @since 0.2
         */
        @SuppressWarnings({"unused", "unchecked"})
        public static <T> Predicate<T> isNull(Class<T> c) {
            return IS_NULL;
        }

        /**
         * A predefined <code>Predicate</code> predicate test if the element
         * specified is NOT null
         *
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static final Predicate NOT_NULL = negate(IS_NULL);

        /**
         * The type-safe version of {@link #NOT_NULL}
         *
         * @param <T>
         *         the element type
         * @return a function that check if argument is {@code null} or {@code NONE}
         * @since 0.2
         */
        @SuppressWarnings({"unused", "unchecked"})
        public static <T> Predicate<T> requireNotNull() {
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

            @Override
            public Bijection invert() {
                return this;
            }
        };

        /**
         * The type-safe version of {@link #IDENTITY}
         *
         * @param <T>
         *         the element type
         * @return the identity function that always return the argument itself
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> F1<T, T> identity() {
            return IDENTITY;
        }

        /**
         * The type-safe version of {@link #IDENTITY}
         *
         * @param clz
         *         the class that restrict the type of &lt;T&gt;
         * @param <T>
         *         the generic type
         * @return the identity function that when get called, always return the parameter
         * @since 0.9
         */
        public static <T> F1<T, T> identity(Class<T> clz) {
            return IDENTITY;
        }

        private static <T extends Comparable<T>> F2<T, T, Boolean> _cmp(final boolean lt) {
            return new F2<T, T, Boolean>() {
                @Override
                public Boolean apply(T t1, T t2
                ) throws NotAppliedException, Break {
                    if (lt) {
                        return (t1.compareTo(t2) < 0);
                    } else {
                        return (t1.compareTo(t2) > 0);
                    }
                }
            };
        }

        private static <T extends Comparable<T>> F1<T, Boolean> _cmp(final T v, final boolean lt) {
            return new F1<T, Boolean>() {
                @Override
                public Boolean apply(T t) throws NotAppliedException, Break {
                    if (lt) {
                        return t.compareTo(v) < 0;
                    } else {
                        return t.compareTo(v) > 0;
                    }
                }
            };
        }

        /**
         * Returns a function that apply to one parameter and compare it with the value {@code v} specified,
         * returns {@code true} if the parameter applied is less than {@code v}
         *
         * @param v
         *         the value to be compare with the function parameter
         * @param <T>
         *         the type of the value and parameter
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F1<T, Boolean> lt(final T v) {
            return _cmp(v, true);
        }

        /**
         * Alias of {@link #lt(Comparable)}
         *
         * @param v
         *         a value used to check against function argument
         * @param <T>
         *         the element type
         * @return a function that check if a object is lesser than the value specified
         * @since 0.2
         */
        public static <T extends Comparable<T>> F1<T, Boolean> lessThan(final T v) {
            return lt(v);
        }

        /**
         * Returns a function that apply to one parameter and compare it with the value {@code v} specified,
         * returns {@code true} if the parameter applied is greater than {@code v}
         *
         * @param v
         *         the value to be compare with the function parameter
         * @param <T>
         *         the type of the value and parameter
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F1<T, Boolean> gt(final T v) {
            return _cmp(v, false);
        }

        /**
         * Alias of {@link #gt(Comparable)}
         *
         * @param v
         *         the value used to check against function argument
         * @param <T>
         *         the element type
         * @return a function that check if a object is greater than the value specified
         * @since 0.2
         */
        public static <T extends Comparable<T>> F1<T, Boolean> greaterThan(final T v) {
            return gt(v);
        }

        /**
         * Returns a function that apply to one parameter and compare it with the value {@code v} specified,
         * returns {@code true} if the parameter applied is greater than or equals to {@code v}
         *
         * @param v
         *         the value to be compare with the function parameter
         * @param <T>
         *         the type of the value and parameter
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F1<T, Boolean> gte(final T v) {
            return negate(lt(v));
        }

        /**
         * Alias of {@link #gte(Comparable)}
         *
         * @param v
         *         the value used to check against function argument
         * @param <T>
         *         the element type
         * @return a function that check if an object is greater than or equals to the value specified
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T extends Comparable<T>> F1<T, Boolean> greaterThanOrEqualsTo(final T v) {
            return gte(v);
        }

        /**
         * Returns a function that apply to one parameter and compare it with the value {@code v} specified,
         * returns {@code true} if the parameter applied is less than or equals to {@code v}
         *
         * @param v
         *         the value to be compare with the function parameter
         * @param <T>
         *         the type of the value and parameter
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F1<T, Boolean> lte(final T v) {
            return negate(gt(v));
        }

        /**
         * Alias of {@link #lte(Comparable)}
         *
         * @param v
         *         the value to be used to check agains function arugment
         * @param <T>
         *         the element type
         * @return a function that check if a object is lesser than or equals to specified value
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T extends Comparable<T>> F1<T, Boolean> lessThanOrEqualsTo(final T v) {
            return lte(v);
        }

        /**
         * A function that apply to two parameters then return {@code true} if the first parameter
         * is less than the second one. The type of the two parameter should be the same and should
         * implements {@link Comparable}
         *
         * @since 0.2
         */
        public static final F2 LESS_THAN = F.<Comparable>_cmp(true);

        /**
         * A function that apply to two parameters then return {@code true} if the first parameter
         * is greater than the second one. The type of the two parameter should be the same and should
         * implements {@link Comparable}
         *
         * @since 0.2
         */
        public static final F2 GREATER_THAN = F.<Comparable>_cmp(false);

        /**
         * A function that apply to two parameters then return {@code true} if the first parameter
         * is less than or equal to the second one. The type of the two parameter should be the same
         * and should implements {@link Comparable}
         *
         * @since 0.2
         */
        public static final F2 LESS_THAN_OR_EQUAL_TO = negate(GREATER_THAN);

        /**
         * A function that apply to two parameters then return {@code true} if the first parameter
         * is greater than or equal to the second one. The type of the two parameter should be the same
         * and should implements {@link Comparable}
         *
         * @since 0.2
         */
        public static final F2 GREATER_THAN_OR_EQUAL_TO = negate(LESS_THAN);

        /**
         * Returns a function that check if a value is less than another one. This is the
         * type safe version of {@link #LESS_THAN}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T extends Comparable<T>> F2<T, T, Boolean> lt() {
            return LESS_THAN;
        }

        /**
         * Alias of {@link #lt()}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F2<T, T, Boolean> lessThan() {
            return lt();
        }

        /**
         * Returns a function that check if a value is less than or equals to another one.
         * This is the type safe version of {@link #LESS_THAN_OR_EQUAL_TO}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T extends Comparable<T>> F2<T, T, Boolean> lte() {
            return LESS_THAN_OR_EQUAL_TO;
        }

        /**
         * Alias of {@link #lte()}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F2<T, T, Boolean> lessThanOrEqualsTo() {
            return lte();
        }

        /**
         * Returns a function that check if a value is less than another one.
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T extends Comparable<T>> F2<T, T, Boolean> gt() {
            return GREATER_THAN;
        }

        /**
         * Alias of {@link #gt()}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T extends Comparable<T>> F2<T, T, Boolean> greaterThan() {
            return gt();
        }

        /**
         * Returns a function that check if a value is greater than or equals to another one.
         * This is the type safe version of {@link #GREATER_THAN_OR_EQUAL_TO}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T extends Comparable<T>> F2<T, T, Boolean> gte() {
            return negate(LESS_THAN);
        }

        /**
         * Alias of {@link #gte()}
         *
         * @param <T>
         *         the element type
         * @return a function that check if one element is greater than or equals to another
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T extends Comparable<T>> F2<T, T, Boolean> greaterThanOrEqualsTo() {
            return gte();
        }


        private static <T> F2<T, T, Boolean> _cmp(final java.util.Comparator<? super T> c, final boolean lt) {
            return new F2<T, T, Boolean>() {
                @Override
                public Boolean apply(T t1, T t2) throws NotAppliedException, Break {
                    if (lt) {
                        return c.compare(t1, t2) < 0;
                    } else {
                        return c.compare(t1, t2) > 0;
                    }
                }
            };
        }

        /**
         * Returns a function that check if a value is less than another one using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lt(final java.util.Comparator<? super T> c) {
            return _cmp(c, true);
        }

        /**
         * Alias of {@link #lt(java.util.Comparator)}
         *
         * @param c
         *         a comparator function
         * @param <T>
         *         element type
         * @return a function that use {@code c} to check if an element is lesser than another
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lessThan(final java.util.Comparator<? super T> c) {
            return lt(c);
        }

        /**
         * Returns a function that check if a value is less than another one using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> gt(final java.util.Comparator<? super T> c) {
            return _cmp(c, false);
        }

        /**
         * Alias of {@link #gt(java.util.Comparator)}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> greaterThan(final java.util.Comparator<? super T> c) {
            return gt(c);
        }

        /**
         * Returns a function that check if a value is less than or equals to another one
         * using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lte(final java.util.Comparator<? super T> c) {
            return negate(gt(c));
        }

        /**
         * Alias of {@link #lte(java.util.Comparator)}
         *
         * @param c
         *         a comparator function
         * @param <T>
         *         the element type
         * @return a function that use {@code c} to check if an element is lesser than or equals to another
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T> F2<T, T, Boolean> lessThanOrEqualsTo(final java.util.Comparator<? super T> c) {
            return lte(c);
        }

        /**
         * Returns a function that check if a value is greater than or equals to another one
         * using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> gte(final java.util.Comparator<? super T> c) {
            return negate(lt(c));
        }

        /**
         * Alias of {@link #gte(java.util.Comparator)}
         *
         * @param c
         *         a comparator function
         * @param <T>
         *         the element type
         * @return a function that use {@code c} to check if an element is greater than or equals to another
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static <T> F2<T, T, Boolean> greaterThanOrEqualsTo(final java.util.Comparator<? super T> c) {
            return gte(c);
        }

        private static <T> F2<T, T, Boolean> _cmp(final Func2<? super T, ? super T, Integer> c, final boolean lt) {
            return new F2<T, T, Boolean>() {
                @Override
                public Boolean apply(T t1, T t2) throws NotAppliedException, Break {
                    if (lt) {
                        return c.apply(t1, t2) < 0;
                    } else {
                        return c.apply(t1, t2) > 0;
                    }
                }
            };
        }

        /**
         * Returns a function that check if a value is less than another one using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lt(final Func2<? super T, ? super T, Integer> c) {
            return _cmp(c, true);
        }

        /**
         * Alias of {@link #lt(java.util.Comparator)}
         *
         * @param c
         *         a comparator function
         * @param <T>
         *         the element type
         * @return a function that use function {@code c} to check if an element is lesser than another
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lessThan(final Func2<? super T, ? super T, Integer> c) {
            return lt(c);
        }

        /**
         * Returns a function that check if a value is less than another one using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> gt(final Func2<? super T, ? super T, Integer> c) {
            return _cmp(c, false);
        }

        /**
         * Alias of {@link #gt(java.util.Comparator)}
         *
         * @param c
         *         a comparator function
         * @param <T>
         *         the element type
         * @return a function that use comparator function {@code c} to check if an object is greater than another
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> greaterThan(final Func2<? super T, ? super T, Integer> c) {
            return gt(c);
        }

        /**
         * Returns a function that check if a value is less than or equals to another one
         * using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lte(final Func2<? super T, ? super T, Integer> c) {
            return negate(gt(c));
        }

        /**
         * Alias of {@link #lte(java.util.Comparator)}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> lessThanOrEqualsTo(final Func2<? super T, ? super T, Integer> c) {
            return lte(c);
        }

        /**
         * Returns a function that check if a value is greater than or equals to another one
         * using the {@link Comparator} specified
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> gte(final Func2<? super T, ? super T, Integer> c) {
            return negate(lt(c));
        }

        /**
         * Alias of {@link #gte(java.util.Comparator)}
         *
         * @param <T>
         *         The type of the value been compared, should implements {@link Comparable}
         * @param c
         *         The comparator that can compare the value
         * @return the function that do the comparison
         * @since 0.2
         */
        public static <T> F2<T, T, Boolean> greaterThanOrEqualsTo(final Func2<? super T, ? super T, Integer> c) {
            return gte(c);
        }

        /**
         * A predefined function that applies to two parameters and check if they are equals to each other
         */
        public static final F2 EQ = new F2() {
            @Override
            public Object apply(Object a, Object b) {
                return Lang.eq(a, b);
            }
        };

        /**
         * Alias of {@link #EQ}
         *
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static final F2 EQUAL = EQ;

        /**
         * The type-safe version of {@link #EQ}
         *
         * @param <P1>
         *         the type of the first argument
         * @param <P2>
         *         the type of the second argument
         * @return a type-safe function that check equility of two objects
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <P1, P2> F2<P1, P2, Boolean> eq() {
            return EQ;
        }

        /**
         * Alias of {@link #eq()}
         *
         * @param <P1>
         *         the type of the first argument
         * @param <P2>
         *         the type of the second argument
         * @return a type-safe function that check equility of two objects
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <P1, P2> F2<P1, P2, Boolean> equal() {
            return EQ;
        }

        /**
         * Returns a {@link Predicate} that checkes if the argument
         * equals to the element specified
         *
         * @param element
         *         the object to be checked with argument when applying
         *         the function
         * @param <P>
         *         the element type
         * @return the function that returns {@code true} if the argument equals
         * with the element specified or {@code false} otherwise
         */
        public static <P> Predicate<P> eq(final P element) {
            return new Predicate<P>() {
                @Override
                public boolean test(P p) {
                    return Lang.eq(p, element);
                }
            };
        }

        /**
         * A predefined function that applies to two parameters and check if they are not equals to
         * each other. This is a negate function of {@link #EQ}
         *
         * @since 0.2
         */
        public static final F2 NE = negate(EQ);

        /**
         * Alias of {@link #NE}
         *
         * @since 0.2
         */
        @SuppressWarnings("unused")
        public static final F2 NOT_EQUAL = NE;

        /**
         * The type-safe version of {@link #NE}
         *
         * @param <P1>
         *         type of the first argument
         * @param <P2>
         *         type of the second argument
         * @return the type-safe version of {@link #NE}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <P1, P2> F2<P1, P2, Boolean> ne() {
            return NE;
        }

        /**
         * Alias of {@link #ne()}
         *
         * @param <P1>
         *         type of the first argument
         * @param <P2>
         *         type of the second argument
         * @return the type-safe version of {@link #NE}
         * @since 0.2
         */
        @SuppressWarnings({"unchecked", "unused"})
        public static <P1, P2> F2<P1, P2, Boolean> notEqual() {
            return NE;
        }

        public static final Comparator NATURAL_ORDER = Comparator.NaturalOrder.INSTANCE;

        public static final Comparator REVERSE_ORDER = Comparator.ReverseOrder.INSTANCE;

        @SuppressWarnings("unchecked")
        public static <T extends Comparable<T>> Comparator<T> naturalOrder() {
            return (Comparator<T>) NATURAL_ORDER;
        }

        @SuppressWarnings("unchecked")
        public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
            return (Comparator<T>) REVERSE_ORDER;
        }

        public static <T> Comparator<T> reverse(final java.util.Comparator<? super T> c) {
            return new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    return c.compare(o2, o1);
                }
            };
        }

        public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
                final Function<? super T, ? extends U> keyExtractor
        ) {
            E.NPE(keyExtractor);
            return new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    return (keyExtractor.apply(o1).compareTo(keyExtractor.apply(o2)));
                }
            };
        }

        /**
         * Construct a {@link Comparator} with a function to extract the key of type U from given object of type T and
         * a comparator to compare type U
         *
         * @param keyExtractor
         *         the function to extract the key for comparison
         * @param keyComparator
         *         the {@link Comparator} that compares type U (the key type)
         * @param <T>
         *         the type of the object instance
         * @param <U>
         *         the type of the key extract from T
         * @return a comparator that compares type T objects
         */
        public static <T, U> Comparator<T> comparing(
                final Function<? super T, ? extends U> keyExtractor,
                final java.util.Comparator<? super U> keyComparator
        ) {
            E.NPE(keyExtractor, keyComparator);
            return new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    return keyComparator.compare(keyExtractor.apply(o1), keyExtractor.apply(o2));
                }
            };
        }

        /**
         * A predefined function that calculate hash code of an object
         *
         * @see #hc(Object)
         * @since 0.2
         */
        public static final F1 HASH_CODE = new F1() {
            @Override
            public Integer apply(Object o) {
                return Lang.hc(o);
            }
        };

        /**
         * The type-safe version of {@link #HASH_CODE}
         *
         * @param <T>
         *         specifies the generic type of the argument passed to the returned function
         * @return a function of type {@link F1 F1&lt;T, Integer&gt;} that takes type {@code T} argument and
         * returns {@link Object#hashCode()} of the argument
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> F1<T, Integer> hc() {
            return HASH_CODE;
        }

        /**
         * A predefined function that when applied to an object instance, returns
         * String representation of the instance
         *
         * @see #toString2(Object)
         * @since 0.2
         */
        public static final F1 AS_STRING = new F1() {
            @Override
            public Object apply(Object o) {
                return toString2(o);
            }
        };

        /**
         * A type-safe version of {@link #AS_STRING}. It returns a function
         * that takes argument of type {@code T} and returns a String by calling
         * {@link Object#toString()} function on the argument
         *
         * @param <T>
         *         the generic type T of the returning function
         * @return a function of type {@link F1 F1&lt;T, String&gt;}
         * @since 0.2
         */
        @SuppressWarnings("unchecked")
        public static <T> F1<T, String> asString() {
            return AS_STRING;
        }

        /**
         * Type safe version of {@link #AS_STRING}. It returns a function that
         * takes argument of type {@code T} and returns a String by calling
         * {@link Object#toString()} function on the argument
         *
         * @param tClass
         *         the class specify the generic type
         * @param <T>
         *         the generic type T of the returning function
         * @return a function of type {@link F1 F1&lt;T, String&gt;}
         */
        public static <T> F1<T, String> asString(Class<T> tClass) {
            return AS_STRING;
        }

        /**
         * A predicate function that when applied to a {@link java.lang.reflect.Field} type
         * object, returns `true` when the field is not static
         */
        public static Predicate<Field> NON_STATIC_FIELD = new Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                return !Modifier.isStatic(field.getModifiers());
            }
        };

        public static <BEAN> Transformer<BEAN, Object> propertyExtractor(final String property) {
            return new Transformer<BEAN, Object>() {
                @Override
                public Object transform(BEAN bean) {
                    return $.getProperty(bean, property);
                }
            };
        }

        /**
         * Returns a predicate function that when applied to a {@link Field} type
         * object, returns `true` if the field has specified annotation presented
         *
         * @param annoClass
         *         the annotation
         * @return a predicate function as described above
         */
        public static Predicate<Field> fieldWithAnnotation(final Class<? extends Annotation> annoClass) {
            return new Predicate<Field>() {
                @Override
                public boolean test(Field field) {
                    return field.isAnnotationPresent(annoClass);
                }
            };
        }
    }

    private static CacheService cache() {
        return OsglConfig.internalCache();
    }

    static {
        OsglConfig.registerExtensions();
    }

}
