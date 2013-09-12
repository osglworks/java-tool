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

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The namespace for OSGL collection utilities
 */
public final class C {
    /**
     * The character enum for a {@link ITraversable}
     */
    public static enum Characteristic {
        /**
         * Indicate whether a traversable is immutable
         */
        IMMUTABLE,

        /**
         * Indicate whether a list support random access
         */
        RANDOM_ACCESS,

        /**
         * Indicate whether the traversable is limited
         */
        LIMITED,

        /**
         * Indicate whether the traversable support lazy evaluation
         */
        LAZY,

        /**
         * Indicate whether this the traversable support parallel operation
         */
        PARALLEL
    }

    /**
     * Define a traversable structure with functional programming support,
     * including map, reduce etc.
     *
     * @param <T> The element type
     */
    public static interface ITraversable<T> extends Iterable<T> {

        /**
         * Check if the traversable has a certain characteristic
         *
         * @param c
         * @return
         */
        boolean is(Characteristic c);

        /**
         * Alias of {@link #head()}
         *
         * @since 0.2
         */
        _.Option<T> first();

        /**
         * Returns an {@link _.Option} of the first element in the
         * traversable or {@link _#NONE} if the traversable is empty
         *
         * @return the first element from the traversable
         * @see #tail()
         * @see #first()
         * @since 0.2
         */
        _.Option<T> head();

        /**
         * Alias of {@link #take(int)}
         *
         * @since 0.2
         */
        ITraversable<T> head(int n);

        /**
         * Is this traversal empty?
         *
         * @return {@code true} if the traversal is empty or {@code false} otherwise
         */
        boolean isEmpty();


        /**
         * Returns the last element from this traversable
         *
         * @return the last element
         * @throws UnsupportedOperationException if this traversable is not limited
         * @see #isEmpty()
         * @see Characteristic#LIMITED
         * @see #is(org.osgl.util.C.Characteristic)
         * @since 0.2
         */
        _.Option<T> last() throws NoSuchElementException, UnsupportedOperationException;

        /**
         * Returns an new traversable that reverse this traversable.
         *
         * @return a reversed traversable
         * @throws UnsupportedOperationException if this traversable is unlimited
         * @see Characteristic#LIMITED
         * @see #is(org.osgl.util.C.Characteristic)
         * @since 0.2
         */
        ITraversable<T> reverse() throws UnsupportedOperationException;

        /**
         * Returns the rest part of the traversable except the first element
         *
         * @return a traversable without the first element
         * @throws UnsupportedOperationException if the traversable is empty
         * @see #head()
         * @see #tail(int)
         * @since 0.2
         */
        ITraversable<T> tail() throws UnsupportedOperationException;

        /**
         * Returns a traversable consisting the last {@code n} elements from this traversable if
         * number {@code n} is positive and the traversable contains more than {@code n} elements
         * <p/>
         * <p>If this traversable contains less than {@code n} elements, then a traversable consisting
         * the whole elements of this traversable is returned. Note it might return this traversable
         * itself if the traversable is immutable.</p>
         * <p/>
         * <p>If the number {@code n} is zero, then an empty traversable is returned in reverse
         * order</p>
         * <p/>
         * <p>If the number {@code n} is negative, then the first {@code -n} elements from this
         * traversable is returned in an new traversable</p>
         * <p/>
         * <pre>
         *     ITraversable traversable = C1.list(1, 2, 3, 4);
         *     assertEquals(C1.list(3, 4), traversable.tail(2));
         *     assertEquals(C1.list(1, 2, 3, 4), traversable.tail(100));
         *     assertEquals(C1.list(), traversable.tail(0));
         *     assertEquals(C1.list(1, 2), traversable.tail(-2));
         *     assertEquals(C1.list(1, 2, 3, 4), traversable.take(-200));
         * </pre>
         *
         * @param n specify the number of elements to be taken from the tail of this traversable
         * @return a traversable consisting of the last {@code n} elements from this traversable
         * @throws UnsupportedOperationException if the traversal is unlimited or empty
         * @see Characteristic#LIMITED
         * @see #is(org.osgl.util.C.Characteristic)
         * @since 0.2
         */
        ITraversable<T> tail(int n) throws UnsupportedOperationException;

        /**
         * Returns a traversable consisting the first {@code n} elements from this traversable if
         * number {@code n} is positive and the traversable contains more than {@code n} elements
         * <p/>
         * <p>If this traversable contains less than {@code n} elements, then a traversable consisting
         * the whole elements of this traversable is returned. Note it might return this traversable
         * itself if the traversable is immutable.</p>
         * <p/>
         * <p>If the number {@code n} is zero, then an empty traversable is returned in reverse
         * order</p>
         * <p/>
         * <p>If the number {@code n} is negative, then the last {@code -n} elements from this
         * traversable is returned in an new traversable</p>
         * <p/>
         * <pre>
         *     ITraversable traversable = C1.list(1, 2, 3, 4);
         *     assertEquals(C1.list(1, 2), traversable.take(2));
         *     assertEquals(C1.list(1, 2, 3, 4), traversable.take(100));
         *     assertEquals(C1.list(), traversable.take(0));
         *     assertEquals(C1.list(3, 4), traversable.take(-2));
         *     assertEquals(C1.list(1, 2, 3, 4), traversable.take(-200));
         * </pre>
         *
         * @param n specify the number of elements to be taken from the head of this traversable
         * @return a traversable consisting of the first {@code n} elements of this traversable
         * @since 0.2
         */
        ITraversable<T> take(int n);

        /**
         * Returns an new traversable with a mapper function specified. The element in the new traversal is the result of the
         * mapper function applied to this traversal element.
         * <p/>
         * <pre>
         *     ITraversable traversable = C.list(23, _.NONE, null);
         *     assertEquals(C.list(true, false, false), traversal.map(_.F.NOT_NULL));
         *     assertEquals(C.list("23", "", ""), traversal.map(_.F.AS_STRING));
         * </pre>
         * <p/>
         * <p>It is up to implementation to decide whether use lazy evaluation or not</p>
         *
         * @param mapper the function that applied to element in this traversal and returns element in the result traversal
         * @param <R>    the element type of the new traversal
         * @return the new traversal contains results of the mapper function applied to this traversal
         * @since 0.2
         */
        <R> ITraversable<R> map(_.IFunc1<? super T, ? extends R> mapper);

        /**
         * Returns a traversable consisting of the results of replacing each element of this
         * stream with the contents of the iterable produced by applying the provided mapping
         * function to each element. If the result of the mapping function is {@code null},
         * this is treated as if the result is an empty traversable.
         *
         * @param mapper the function produce an iterable when applied to an element
         * @param <R>    the element type of the the new traversable
         * @return the new traversable
         */
        <R> ITraversable<R> flatMap(_.IFunc1<? super T, ? extends Iterable<? extends R>> mapper);

        /**
         * Returns an new traversable that contains all elements in the current traversable
         * except that does not pass the test of the filter function specified.
         * <p/>
         * <pre>
         *     ITraversable traversable = C.list(-1, 0, 1, -3, 7);
         *     ITraversable filtered = traversable.filter(_.F.gt(0));
         *     assertTrue(filtered.contains(1));
         *     assertFalse(filtered.contains(-3));
         * </pre>
         *
         * @param predicate the function that test if the element in the traversable should be
         *                  kept in the resulting traversable. When applying the filter function
         *                  to the element, if the result is {@code true} then the element will
         *                  be kept in the resulting traversable.
         * @return the new traversable contains elements passed the filter function test
         */
        ITraversable<T> filter(_.IFunc1<? super T, Boolean> predicate);

        /**
         * Performs a reduction on the elements in this traversable, using the provided
         * identity and accumulating function. This is equivalent to:
         * <pre>
         *      R result = identity;
         *      for (T element: this traversable) {
         *          result = accumulator.apply(result, element);
         *      }
         *      return result;
         * </pre>
         *
         * @param identity    the identity value for the accumulating function
         * @param accumulator the function the combine two values
         * @return the result of reduction
         */
        <R> R reduce(R identity, _.IFunc2<R, T, R> accumulator);

        /**
         * Performs a reduction on the elements in this traversable, using provided accumulating
         * function. This is equivalent to:
         * <pre>
         *      boolean found = false;
         *      T result = null;
         *      for (T element: this traversable) {
         *          if (found) {
         *              result = accumulator.apply(result, element);
         *          } else {
         *              found = true;
         *              result = element;
         *          }
         *      }
         *      return found ? _.some(result) : _.none();
         * </pre>
         *
         * @param accumulator
         * @return
         */
        _.Option<T> reduce(_.IFunc2<T, T, T> accumulator);

        /**
         * Check if all elements match the predicate specified
         *
         * @param predicate the function to test the element
         * @return {@code true} if all elements match the predicate
         */
        boolean allMatch(_.IFunc1<? super T, Boolean> predicate);

        /**
         * Check if any elements matches the predicate specified
         *
         * @param predicate the function to test the element
         * @return {@code true} if any element matches the predicate
         */
        boolean anyMatch(_.IFunc1<? super T, Boolean> predicate);

        /**
         * Check if no elements matches the predicate specified. This should be
         * equivalent to:
         * <p/>
         * <pre>
         *      this.allMatch(_.F.negate(predicate));
         * </pre>
         *
         * @param predicate the function to test the element
         * @return {@code true} if none element matches the predicate
         */
        boolean noneMatch(_.IFunc1<? super T, Boolean> predicate);

        /**
         * Returns an element that matches the predicate specified. The interface
         * does not indicate if it should be the first element matches the predicate
         * be returned or in case of parallel computing, whatever element matches
         * found first is returned. It's all up to the implementation to refine the
         * semantic of this method
         *
         * @return an element in this traversal that matches the predicate or
         *         {@link _#NONE} if no element matches
         */
        _.Option<T> findOne(_.IFunc1<? super T, Boolean> predicate);
    }

    /**
     * Implement an immutable {@link ITraversable} by wrapping around
     * an {@link Iterable}.
     *
     * @param <T> the element type
     */
    public static class Traversable<T> implements ITraversable<T> {
        final protected Iterable<? extends T> it;
        final protected EnumSet<Characteristic> feature =
                EnumSet.of(Characteristic.IMMUTABLE);

        private Traversable(Iterable<? extends T> iterable) {
            it = iterable;
        }

        @Override
        public Iterator<T> iterator() {
            final Iterator<? extends T> itr = it.iterator();
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return itr.hasNext();
                }

                @Override
                public T next() {
                    return itr.next();
                }

                @Override
                public void remove() {
                    itr.remove();
                }
            };
        }

        @Override
        public final boolean is(Characteristic c) {
            return feature.contains(c);
        }

        @Override
        public T first() throws NoSuchElementException {
            T t = it.iterator().next();
            if (null == t) {
                throw new NoSuchElementException();
            }
            return t;
        }

        @Override
        public T head() throws NoSuchElementException {
            return first();
        }
    }

    // --- factories
    public static <T> ITraversable<T> traversable(Iterable<? extends T> iterable) {
        if (iterable instanceof ITraversable) {
            return (ITraversable<T>) iterable;
        }
        return new Traversable<T>(iterable);
    }
}
