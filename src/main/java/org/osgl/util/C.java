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
import org.osgl.exception.NotAppliedException;
import org.osgl.util.algo.Algorithms;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * The namespace for OSGL collection utilities
 */
public enum C {

    INSTANCE;

    /**
     * The character enum for a data structure
     */
    public static enum Feature {
        /**
         * Indicate whether a structure is immutable
         */
        IMMUTABLE,

        /**
         * Indicate the client cannot modify the structure.
         * However a readonly structure might not be immutable.
         * For example a view of a backing structure is readonly
         * while the backing structure is immutable
         */
        READONLY,

        /**
         * Indicate whether a list structure support random access
         */
        RANDOM_ACCESS,

        /**
         * Indicate whether the structure is limited
         */
        LIMITED,

        /**
         * Indicate whether the structure supports lazy evaluation
         */
        LAZY,

        /**
         * Indicate whether the structure support parallel operation
         */
        PARALLEL,

        /**
         * Indicate whether this structure is ordered. E.g. a
         * {@link List} and {@link java.util.LinkedHashSet} is ordered
         * structure, while {@link java.util.HashSet} might not be
         * ordered
         */
        ORDERED,

        /**
         * Indicate whether this structure is sorted
         */
        SORTED
    }

    /**
     * Define a type that holds a set of {@link org.osgl.util.C.Feature}
     */
    public interface Featured {
        /**
         * Get all characteristics in {@link EnumSet}
         *
         * @return an {@code EnumSet} of all characteristics hold by this object
         * @since 0.2
         */
        EnumSet<Feature> features();

        /**
         * Check if this object has a certain {@link org.osgl.util.C.Feature}
         *
         * @param c the characteristic to be tested
         * @return {@code true} if this object has the characteristic, or {@code false} otherwise
         * @since 0.2
         */
        boolean is(Feature c);

        public static class Factory {
            public static final Featured identity(final EnumSet<Feature> predefined) {
                return new Featured() {
                    @Override
                    public EnumSet<Feature> features() {
                        return predefined;
                    }

                    @Override
                    public boolean is(Feature c) {
                        return predefined.contains(c);
                    }
                };
            }
        }
    }

    /**
     * Define a traversable structure with functional programming support,
     * including map, reduce etc.
     *
     * @param <T> The element type
     */
    public static interface Traversable<T> extends Iterable<T>, Featured {

        /**
         * Returns this traversable and try to turn on
         * {@link C.Feature#PARALLEL}. If this traversable does not
         * support {@link org.osgl.util.C.Feature#PARALLEL} then
         * return this traversable directly without any state change
         *
         * @return this reference with parallel turned on if parallel
         * is supported
         */
        Traversable<T> parallel();

        /**
         * Returns this traversable and turn off
         * {@link C.Feature#PARALLEL}
         *
         * @return this reference with parallel turned off
         */
        Traversable<T> sequential();

        /**
         * Returns this traversable and try to turn on {@link C.Feature#LAZY}.
         * If lazy is not supported then return this traversable directly without
         * any state change
         *
         * @return this reference with lazy turned on if it is supported
         */
        Traversable<T> lazy();

        /**
         * Returns this traversable and turn off {@link C.Feature#LAZY}
         *
         * @return this reference with lazy turned off
         */
        Traversable<T> eager();

        /**
         * Is this traversal empty?
         *
         * @return {@code true} if the traversal is empty or {@code false} otherwise
         * @since 0.2
         */
        boolean isEmpty();

        /**
         * Return the size of this traversal
         *
         * @return the size of the structure
         * @throws UnsupportedOperationException if this structure does not support this method
         * @since 0.2
         */
        int size() throws UnsupportedOperationException;


        /**
         * Returns an new traversable with a mapper function specified. The element in the new traversal is the result of the
         * mapper function applied to this traversal element.
         * <p/>
         * <pre>
         *     Traversable traversable = C.list(23, _.NONE, null);
         *     assertEquals(C.list(true, false, false), traversal.map(_.F.NOT_NULL));
         *     assertEquals(C.list("23", "", ""), traversal.map(_.F.AS_STRING));
         * </pre>
         * <p/>
         * <p>For Lazy Traversable, it must use lazy evaluation for this method.
         * Otherwise it is up to implementation to decide whether use lazy
         * evaluation or not</p>
         *
         * @param mapper the function that applied to element in this traversal and returns element in the result traversal
         * @param <R>    the element type of the new traversal
         * @return the new traversal contains results of the mapper function applied to this traversal
         * @since 0.2
         */
        <R> Traversable<R> map(_.Function<? super T, ? extends R> mapper);

        /**
         * Returns a traversable consisting of the results of replacing each element of this
         * stream with the contents of the iterable produced by applying the provided mapping
         * function to each element. If the result of the mapping function is {@code null},
         * this is treated as if the result is an empty traversable.
         *
         * @param mapper the function produce an iterable when applied to an element
         * @param <R>    the element type of the the new traversable
         * @return the new traversable
         * @since 0.2
         */
        <R> Traversable<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper);

        /**
         * Returns an new traversable that contains all elements in the current traversable
         * except that does not pass the test of the filter function specified.
         * <p/>
         * <pre>
         *     Traversable traversable = C.list(-1, 0, 1, -3, 7);
         *     Traversable filtered = traversable.filter(_.F.gt(0));
         *     assertTrue(filtered.contains(1));
         *     assertFalse(filtered.contains(-3));
         * </pre>
         *
         * @param predicate the function that test if the element in the traversable should be
         *                  kept in the resulting traversable. When applying the filter function
         *                  to the element, if the result is {@code true} then the element will
         *                  be kept in the resulting traversable.
         * @return the new traversable consists of elements passed the filter function test
         * @since 0.2
         */
        Traversable<T> filter(_.Function<? super T, Boolean> predicate);

        /**
         * Performs a reduction on the elements in this traversable, using the provided
         * identity and accumulating function. This might be equivalent to:
         * <pre>
         *      R result = identity;
         *      for (T element: this traversable) {
         *          result = accumulator.apply(result, element);
         *      }
         *      return result;
         * </pre>
         * <p/>
         * <p>The above shows a typical left side reduce. However depending on the
         * implementation, it might choose another way to do the reduction, including
         * reduction in a parallel way</p>
         *
         * @param identity    the identity value for the accumulating function
         * @param accumulator the function the combine two values
         * @param <R>         the type of identity and the return value
         * @return the result of reduction
         * @since 0.2
         */
        <R> R reduce(R identity, _.Func2<R, T, R> accumulator);

        /**
         * Performs a reduction on the elements in this traversable, using provided accumulating
         * function. This might be equivalent to:
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
         * <p/>
         * <p>The above shows a typical left side reduction. However depending on the
         * implementation, it might choose another way to do the reduction, including
         * reduction in a parallel way</p>
         *
         * @param accumulator the function takes previous accumulating
         *                    result and the current element being
         *                    iterated
         * @return an option describing the accumulating result or {@link org.osgl._#none()} if
         * the structure is empty
         * @since 0.2
         */
        _.Option<T> reduce(_.Func2<T, T, T> accumulator);

        /**
         * Check if all elements match the predicate specified
         *
         * @param predicate the function to test the element
         * @return {@code true} if all elements match the predicate
         * @since 0.2
         */
        boolean allMatch(_.Function<? super T, Boolean> predicate);

        /**
         * Check if any elements matches the predicate specified
         *
         * @param predicate the function to test the element
         * @return {@code true} if any element matches the predicate
         * @since 0.2
         */
        boolean anyMatch(_.Function<? super T, Boolean> predicate);

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
         * @since 0.2
         */
        boolean noneMatch(_.Function<? super T, Boolean> predicate);

        /**
         * Returns an element that matches the predicate specified. The interface
         * does not indicate if it should be the first element matches the predicate
         * be returned or in case of parallel computing, whatever element matches
         * found first is returned. It's all up to the implementation to refine the
         * semantic of this method
         *
         * @param predicate the function map element to Boolean
         * @return an element in this traversal that matches the predicate or
         * {@link _#NONE} if no element matches
         * @since 0.2
         */
        _.Option<T> findOne(_.Function<? super T, Boolean> predicate);

        /**
         * Iterate this {@code Traversable} with a visitor function. This method
         * does not specify the approach to iterate through this structure. The
         * implementation might choose iterate from left to right, or vice versa.
         * It might even choose to split the structure into multiple parts, and
         * iterate through them in parallel
         *
         * @param visitor a function that apply to element in this
         *                {@code Traversable}. The return value
         *                of the function is ignored
         * @return this {@code Traversable} instance for chained call
         * @since 0.2
         */
        Traversable<T> accept(_.Function<? super T, ?> visitor);

        /**
         * Alias of {@link #accept(org.osgl._.Function)}
         */
        Traversable<T> each(_.Function<? super T, ?> visitor);

        /**
         * Alias of {@link #accept(org.osgl._.Function)}
         */
        Traversable<T> forEach(_.Function<? super T, ?> visitor);
    }

    public static interface Sequence<T>
            extends Traversable<T> {

        /**
         * Returns this traversable and make sure {@link C.Feature#PARALLEL} is set
         *
         * @return this reference with parallel turned on
         */
        Sequence<T> parallel();

        /**
         * Returns this traversable and make sure {@link C.Feature#PARALLEL} is unset
         *
         * @return this reference with parallel turned off
         */
        Sequence<T> sequential();

        /**
         * Returns this traversable and make sure {@link C.Feature#LAZY} is set
         *
         * @return this reference with lazy turned on
         */
        Sequence<T> lazy();

        /**
         * Returns this traversable and make sure {@link C.Feature#LAZY} is unset
         *
         * @return this reference with lazy turned off
         */
        Sequence<T> eager();

        /**
         * Alias of {@link #head()}
         *
         * @since 0.2
         */
        T first() throws NoSuchElementException;

        /**
         * Returns an {@link _.Option} of the first element in the
         * {@code Sequence} or {@link _#NONE} if the {@code Sequence} is empty
         *
         * @return the first element from the {@code Sequence}
         * @throws NoSuchElementException if the {@code Sequence} is empty
         * @see #tail()
         * @see #first()
         * @since 0.2
         */
        T head() throws NoSuchElementException;

        /**
         * Alias of {@link #take(int)}
         *
         * @since 0.2
         */
        Sequence<T> head(int n);

        /**
         * Returns the rest part of the {@code Sequence} except the first element
         *
         * @return a {@code Sequence} without the first element
         * @throws UnsupportedOperationException if the {@code Sequence} is empty
         * @see #head()
         * @see ReversibleSequence#tail(int)
         * @since 0.2
         */
        Sequence<T> tail() throws UnsupportedOperationException;

        /**
         * Returns a {@code Sequence} consisting the first {@code n} elements from this {@code Sequence} if
         * number {@code n} is positive and the {@code Sequence} contains more than {@code n} elements
         * <p/>
         * <p>If this {@code Sequence} contains less than {@code n} elements, then a {@code Sequence} consisting
         * the whole elements of this {@code Sequence} is returned. Note it might return this {@code Sequence}
         * itself if the {@code Sequence} is immutable.</p>
         * <p/>
         * <p>If the number {@code n} is zero, then an empty {@code Sequence} is returned in reverse
         * order</p>
         * <p/>
         * <p>If the number {@code n} is negative, then the last {@code -n} elements from this
         * {@code Sequence} is returned in an new {@code Sequence}, or throw {@link UnsupportedOperationException}
         * if this operation is not supported</p>
         * <p/>
         * <pre>
         *     Sequence seq = C.list(1, 2, 3, 4);
         *     assertEquals(C.list(1, 2), seq.take(2));
         *     assertEquals(C.list(1, 2, 3, 4), seq.take(100));
         *     assertEquals(C.list(), seq.take(0));
         *     assertEquals(C.list(3, 4), seq.take(-2));
         *     assertEquals(C.list(1, 2, 3, 4), seq.take(-200));
         * </pre>
         *
         * @param n specify the number of elements to be taken from the head of this {@code Sequence}
         * @return a {@code Sequence} consisting of the first {@code n} elements of this {@code Sequence}
         * @see #head(int)
         * @since 0.2
         */
        Sequence<T> take(int n);

        /**
         * Returns an new {@code Sequence} that takes the head of this {@code Sequence} until the predicate
         * evaluate to {@code false}:
         * <p/>
         * <pre>
         *     C.Sequence seq = C.list(1, 2, 3, 4, 5, 4, 3, 2, 1);
         *     assertEquals(C.list(C.list(1, 2, 3), seq.takeWhile(_.F.lt(4)));
         *     assertEquals(C.list(C.list(1, 2, 3, 3, 2, 1), seq.filter(_.F.lt(4)));
         * </pre>
         *
         * @param predicate specify which the elements in this {@code Sequence} will put into the new
         *                  {@code Sequence}
         * @return the new {@code Sequence}
         * @since 0.2
         */
        Sequence<T> takeWhile(_.Function<? super T, Boolean> predicate);

        /**
         * Returns a {@code Sequence} consisting of the elements from this {@code Sequence} except the first {@code n}
         * if number {@code n} is positive and the {@code Sequence} contains more than {@code n} elements
         * <p/>
         * <p>If this {@code Sequence} contains less than {@code n} elements, then an empty {@code Sequence}
         * is returned</p>
         * <p/>
         * <p>If the number {@code n} is zero, then a copy of this {@code Sequence} or this {@code Sequence}
         * itself is returned depending on the implementation</p>
         * <p/>
         * <p>If the number {@code n} is negative, then either {@link IllegalArgumentException} should
         * be thrown out if this sequence is not {@link org.osgl.util.C.Feature#LIMITED} or it drop
         * {@code -n} element starts from the tail side</p>
         * <p/>
         * <pre>
         *     C.Sequence seq = C.list(1, 2, 3, 4, 5);
         *     assertEquals(C.list(3, 4, 5), seq.drop(2));
         *     assertEquals(C.list(1, 2, 3, 4, 5), seq.drop(0));
         *     assertEquals(C.list(), seq.drop(100));
         * </pre>
         * <p>Note this method does NOT modify the current sequence, instead it returns an new sequence structure
         * containing the elements as required</p>
         *
         * @param n specify the number of elements to be taken from the head of this {@code Sequence}
         *          must not less than 0
         * @return a {@code Sequence} consisting of the elements of this {@code Sequence} except the first {@code n} ones
         * @since 0.2
         */
        Sequence<T> drop(int n) throws IllegalArgumentException;

        /**
         * Returns a {@code Sequence} consisting of the elements from this sequence with leading elements
         * dropped until the predicate returns {@code true}
         * <p/>
         * <pre>
         *      Sequence seq = C.list(1, 2, 3, 4, 3, 2, 1);
         *      assertTrue(C.list(), seq.dropWhile(_.F.gt(100)));
         *      assertTrue(C.list(4, 3, 2, 1), seq.dropWhile(_.F.lt(3)));
         * </pre>
         * <p>Note this method does NOT modify the current sequence, instead it returns an new sequence structure
         * containing the elements as required</p>
         *
         * @param predicate
         * @return
         * @since 0.2
         */
        Sequence<T> dropWhile(_.Function<? super T, Boolean> predicate);

        /**
         * Returns a sequence consists of all elements of this sequence
         * followed by all elements of the specified iterable.
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param iterable
         * @return
         */
        Sequence<T> append(Iterable<? extends T> iterable);

        /**
         * Returns a sequence consists of all elements of this sequence
         * followed by all elements of the specified sequence.
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param seq the sequence to be appended
         * @return a sequence consists of elements of both sequences
         * @since 0.2
         */
        Sequence<T> append(Sequence<T> seq);

        /**
         * Returns a sequence consists of all elements of this sequence
         * followed by the element specified.
         * <p>an {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append the element to {@code this} sequence instance
         * directly</p>
         *
         * @param t the element to be appended to this sequence
         * @return a sequence consists of elements of this sequence
         * and the element {@code t}
         * @since 0.2
         */
        Sequence<T> append(T t);

        /**
         * Returns a sequence consists of all elements of the iterable specified
         * followed by all elements of this sequence
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might prepend specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param iterable the iterable to be prepended
         * @return a sequence consists of elements of both sequences
         * @since 0.2
         */
        Sequence<T> prepend(Iterable<? extends T> iterable);

        /**
         * Returns a sequence consists of all elements of the sequence specified
         * followed by all elements of this sequence
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might prepend specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param seq the sequence to be prepended
         * @return a sequence consists of elements of both sequences
         * @since 0.2
         */
        Sequence<T> prepend(Sequence<T> seq);

        /**
         * Returns a sequence consists of the element specified followed by
         * all elements of this sequence.
         * <p>an {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append the element to {@code this} sequence instance
         * directly</p>
         *
         * @param t the element to be appended to this sequence
         * @return the sequence consists of {@code t} followed
         * by all elements in this sequence
         * @since 0.2
         */
        Sequence<T> prepend(T t);


        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a Sequence of {@code R} that are mapped from this sequence
         * @since 0.2
         */
        @Override
        <R> Sequence<R> map(_.Function<? super T, ? extends R> mapper);

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a Sequence of {@code R} type element that are mapped from this sequences
         * @since 0.2
         */
        @Override
        <R> Sequence<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper);

        /**
         * {@inheritDoc}
         *
         * @param predicate {@inheritDoc}
         * @return An new {@code Sequence} consists of elements that passed the predicate
         * @since 0.2
         */
        @Override
        Sequence<T> filter(final _.Function<? super T, Boolean> predicate);

        /**
         * {@inheritDoc}
         * <p/>
         * This method does not specify how to run the accumulator. It might be
         * {@link #reduceLeft(Object, org.osgl._.Func2)} or
         * {@link ReversibleSequence#reduceRight(Object, org.osgl._.Func2)}, or
         * even run reduction in parallel, it all depending on the implementation.
         * <p>For a guaranteed reduce from left to right, use
         * {@link #reduceLeft(Object, org.osgl._.Func2)} instead</p>
         *
         * @param identity    {@inheritDoc}
         * @param accumulator {@inheritDoc}
         * @param <R>         {@inheritDoc}
         * @return {@inheritDoc}
         * @since 0.2
         */
        @Override
        <R> R reduce(R identity, _.Func2<R, T, R> accumulator);

        /**
         * Run reduction from header side. This is equivalent to:
         * <pre>
         *      R result = identity;
         *      for (T element: this sequence) {
         *          result = accumulator.apply(result, element);
         *      }
         *      return result;
         * </pre>
         *
         * @param identity    the identity value for the accumulating function
         * @param accumulator the function to accumulate two values
         * @return the reduced result
         * @see #reduce(Object, org.osgl._.Func2)
         * @since 0.2
         */
        <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator);

        /**
         * {@inheritDoc}
         * <p/>
         * This method does not specify the approach to run reduction.
         * For a guaranteed reduction from head to tail, use
         * {@link #reduceLeft(org.osgl._.Func2)} instead
         *
         * @param accumulator {@inheritDoc}
         * @return {@inheritDoc}
         * @since 0.2
         */
        @Override
        _.Option<T> reduce(_.Func2<T, T, T> accumulator);

        /**
         * Run reduction from head to tail. This is equivalent to
         * <p/>
         * <pre>
         *      if (isEmpty()) {
         *          return _.none();
         *      }
         *      T result = head();
         *      for (T element: this traversable.tail()) {
         *          result = accumulator.apply(result, element);
         *      }
         *      return _.some(result);
         * </pre>
         *
         * @param accumulator
         * @return an {@link _.Option} describing the accumulating result
         * @since 0.2
         */
        _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator);

        /**
         * Apply the predicate specified to the element of this sequence
         * from head to tail. Stop at the element that returns {@code true},
         * and returns an {@link _.Option} describing the element. If none
         * of the element applications in the sequence returns {@code true}
         * then {@link org.osgl._#none()} is returned
         *
         * @param predicate the function map the element to Boolean
         * @return an option describe the first element matches the
         * predicate or {@link org.osgl._#none()}
         * @since 0.2
         */
        _.Option<T> findFirst(_.Function<? super T, Boolean> predicate);

        Sequence<T> accept(_.Function<? super T, ?> visitor);

        Sequence<T> each(_.Function<? super T, ?> visitor);

        Sequence<T> forEach(_.Function<? super T, ?> visitor);

        /**
         * Iterate through this sequence from head to tail with
         * the visitor function specified
         *
         * @param visitor the function to visit elements in this sequence
         * @return this sequence
         * @see Traversable#accept(org.osgl._.Function)
         * @see ReversibleSequence#acceptRight(org.osgl._.Function)
         * @since 0.2
         */
        Sequence<T> acceptLeft(_.Function<? super T, ?> visitor);

        /**
         * Returns a sequence formed from this sequence and another iterable
         * collection by combining corresponding elements in pairs.
         * If one of the two collections is longer than the other,
         * its remaining elements are ignored.
         *
         * @param iterable the part B to be zipped with this sequence
         * @param <T2>     the type of the iterable
         * @return a new sequence containing pairs consisting of
         * corresponding elements of this sequence and that.
         * The length of the returned collection is the
         * minimum of the lengths of this sequence and that.
         */
        <T2> Sequence<_.T2<T, T2>> zip(Iterable<T2> iterable);

        /**
         * Returns a sequence formed from this sequence and another iterable
         * collection by combining corresponding elements in pairs.
         * If one of the two collections is longer than the other,
         * placeholder elements are used to extend the shorter collection
         * to the length of the longer.
         *
         * @param iterable the part B to be zipped with this sequence
         * @param <T2>     the type of the iterable
         * @param def1     the element to be used to fill up the result if
         *                 this sequence is shorter than that iterable
         * @param def2     the element to be used to fill up the result if
         *                 the iterable is shorter than this sequence
         * @return a new sequence containing pairs consisting of
         * corresponding elements of this sequence and that.
         * The length of the returned collection is the
         * maximum of the lengths of this sequence and that.
         */
        <T2> Sequence<_.T2<T, T2>> zipAll(Iterable<T2> iterable, T def1, T2 def2);

        /**
         * Zip this sequence with its indices
         *
         * @return A new list containing pairs consisting of all
         * elements of this list paired with their index.
         * Indices start at 0.
         */
        Sequence<_.T2<T, Integer>> zipWithIndex();
    }

    /**
     * A bidirectional sequence which can be iterated from tail to head
     *
     * @param <T> the element type
     */
    public static interface ReversibleSequence<T>
            extends Sequence<T> {

        /**
         * Returns this traversable and make sure {@link C.Feature#PARALLEL} is set
         *
         * @return this reference with parallel turned on
         */
        ReversibleSequence<T> parallel();

        /**
         * Returns this traversable and make sure {@link C.Feature#PARALLEL} is unset
         *
         * @return this reference with parallel turned off
         */
        ReversibleSequence<T> sequential();

        /**
         * Returns this traversable and make sure {@link C.Feature#LAZY} is set
         *
         * @return this reference with lazy turned on
         */
        ReversibleSequence<T> lazy();

        /**
         * Returns this traversable and make sure {@link C.Feature#LAZY} is unset
         *
         * @return this reference with lazy turned off
         */
        ReversibleSequence<T> eager();

        /**
         * {@inheritDoc}
         *
         * @param n {@inheritDoc}
         * @return an new reversible sequence contains the first
         * {@code n} elements in this sequence
         */
        @Override
        ReversibleSequence<T> head(int n);

        /**
         * {@inheritDoc}
         *
         * @return an new reversible sequence contains all elements
         * in this sequence except the first element
         */
        @Override
        ReversibleSequence<T> tail();

        /**
         * {@inheritDoc}
         *
         * @param n {@inheritDoc}
         * @return an new reversible sequence contains the first
         * {@code n} elements in this sequence
         */
        @Override
        ReversibleSequence<T> take(int n);

        /**
         * {@inheritDoc}
         *
         * @param predicate {@inheritDoc}
         * @return an new reversible sequence contains the elements
         * in this sequence until predicate evaluate to false
         */
        @Override
        ReversibleSequence<T> takeWhile(_.Function<? super T, Boolean> predicate);

        /**
         * {@inheritDoc}
         *
         * @param n specify the number of elements to be taken from the head of this {@code Sequence} or
         *          the {@code -n} number of elements to be taken from the tail of this sequence if n is
         *          an negative number
         * @return a reversible sequence without the first {@code n} number of elements
         */
        @Override
        ReversibleSequence<T> drop(int n);

        @Override
        ReversibleSequence<T> dropWhile(_.Function<? super T, Boolean> predicate);

        @Override
        ReversibleSequence<T> filter(_.Function<? super T, Boolean> predicate);

        /**
         * {@inheritDoc}
         *
         * @param t {@inheritDoc}
         * @return a reversible sequence contains this seq's element
         * followed by {@code t}
         */
        @Override
        ReversibleSequence<T> append(T t);

        /**
         * Returns an new reversible sequence contains all elements
         * in this sequence followed by all elements in the specified
         * reverse sequence
         *
         * @param seq another reversible sequence
         * @return an new reversible sequence contains both seq's elements
         */
        ReversibleSequence<T> append(ReversibleSequence<T> seq);

        /**
         * {@inheritDoc}
         *
         * @param t {@inheritDoc}
         * @return a reversible sequence contains by {@code t}
         * followed this seq's element
         */
        @Override
        ReversibleSequence<T> prepend(T t);

        /**
         * Returns an new reversible sequence contains all elements
         * in specified reversible sequence followed by all elements
         * in this sequence
         *
         * @param seq another reversible sequence
         * @return an new reversible sequence contains both seq's elements
         */
        ReversibleSequence<T> prepend(ReversibleSequence<T> seq);

        /**
         * Returns the last element from this {@code Sequence}
         *
         * @return the last element
         * @throws UnsupportedOperationException if this {@code Sequence} is not limited
         * @throws NoSuchElementException        if the {@code Sequence} is empty
         * @see #isEmpty()
         * @see org.osgl.util.C.Feature#LIMITED
         * @see #is(org.osgl.util.C.Feature)
         * @since 0.2
         */
        T last() throws UnsupportedOperationException, NoSuchElementException;


        /**
         * Returns a {@code Sequence} consisting the last {@code n} elements from this {@code Sequence}
         * if number {@code n} is positive and the {@code Sequence} contains more than {@code n} elements
         * <p/>
         * <p>If this {@code Sequence} contains less than {@code n} elements, then a {@code Sequence} consisting
         * the whole elements of this {@code Sequence} is returned. Note it might return this {@code Sequence}
         * itself if the {@code Sequence} is immutable.</p>
         * <p/>
         * <p>If the number {@code n} is zero, then an empty {@code Sequence} is returned in reverse
         * order</p>
         * <p/>
         * <p>If the number {@code n} is negative, then the first {@code -n} elements from this
         * {@code Sequence} is returned in an new {@code Sequence}</p>
         * <p/>
         * <pre>
         *     Sequence seq = C1.list(1, 2, 3, 4);
         *     assertEquals(C1.list(3, 4), seq.tail(2));
         *     assertEquals(C1.list(1, 2, 3, 4), seq.tail(100));
         *     assertEquals(C1.list(), seq.tail(0));
         *     assertEquals(C1.list(1, 2, 3), seq.tail(-3));
         *     assertEquals(C1.list(1, 2, 3, 4), seq.tail(-200));
         * </pre>
         * <p/>
         * <p>This method does not mutate the underline container</p>
         *
         * @param n specify the number of elements to be taken from the tail of this {@code Sequence}
         * @return a {@code Sequence} consisting of the last {@code n} elements from this {@code Sequence}
         * @throws UnsupportedOperationException if the traversal is unlimited or empty
         * @throws IndexOutOfBoundsException     if {@code n} is greater than the size of this {@code Sequence}
         * @see org.osgl.util.C.Feature#LIMITED
         * @see #is(org.osgl.util.C.Feature)
         * @since 0.2
         */
        ReversibleSequence<T> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException;

        /**
         * Returns an new {@code Sequence} that reverse this {@code Sequence}.
         *
         * @return a reversed {@code Sequence}
         * @throws UnsupportedOperationException if this {@code Sequence} is unlimited
         * @see org.osgl.util.C.Feature#LIMITED
         * @see #is(org.osgl.util.C.Feature)
         * @since 0.2
         */
        ReversibleSequence<T> reverse() throws UnsupportedOperationException;

        /**
         * Returns an {@link Iterator} iterate the sequence from tail to head
         *
         * @return the iterator
         * @since 0.2
         */
        Iterator<T> reverseIterator();

        /**
         * Run reduction from tail side. This is equivalent to:
         * <pre>
         *      R result = identity;
         *      for (T element: this sequence.reverse()) {
         *          result = accumulator.apply(result, element);
         *      }
         *      return result;
         * </pre>
         *
         * @see #reduce(Object, org.osgl._.Func2)
         * @since 0.2
         */
        <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator);

        /**
         * Run reduction from tail to head. This is equivalent to
         * <p/>
         * <pre>
         *      if (isEmpty()) {
         *          return _.none();
         *      }
         *      T result = last();
         *      for (T element: this sequence.reverse.tail()) {
         *          result = accumulator.apply(result, element);
         *      }
         *      return _.some(result);
         * </pre>
         *
         * @param accumulator
         * @return an {@link _.Option} describing the accumulating result
         * @since 0.2
         */
        _.Option<T> reduceRight(_.Func2<T, T, T> accumulator);


        /**
         * Apply the predicate specified to the element of this sequence
         * from tail to head. Stop at the element that returns {@code true},
         * and returns an {@link _.Option} describing the element. If none
         * of the element applications in the sequence returns {@code true}
         * then {@link org.osgl._#none()} is returned
         *
         * @param predicate the function map the element to Boolean
         * @return an option describe the first element matches the
         * predicate or {@link org.osgl._#none()}
         * @since 0.2
         */
        _.Option<T> findLast(_.Function<? super T, Boolean> predicate);

        @Override
        <R> ReversibleSequence<R> map(_.Function<? super T, ? extends R> mapper);

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a ReversibleSequence of {@code R} type element that are mapped from this sequences
         * @since 0.2
         */
        @Override
        <R> ReversibleSequence<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper);

        ReversibleSequence<T> accept(_.Function<? super T, ?> visitor);


        ReversibleSequence<T> each(_.Function<? super T, ?> visitor);

        ReversibleSequence<T> forEach(_.Function<? super T, ?> visitor);

        ReversibleSequence<T> acceptLeft(_.Function<? super T, ?> visitor);

        /**
         * Iterate through this sequence from tail to head with the visitor function
         * specified
         *
         * @param visitor the function to visit elements in this sequence
         * @return this sequence
         * @see Traversable#accept(org.osgl._.Function)
         * @see Sequence#acceptLeft(org.osgl._.Function)
         * @since 0.2
         */
        ReversibleSequence<T> acceptRight(_.Function<? super T, ?> visitor);

        <T2> C.ReversibleSequence<_.T2<T, T2>> zip(C.ReversibleSequence<T2> rseq);

        <T2> C.ReversibleSequence<_.T2<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, T def1, T2 def2);

    }

    public static class Array<T> extends ReversibleSeqBase<T> implements ReversibleSequence<T> {
        @Override
        public Array<T> lazy() {
            super.lazy();
            return this;
        }

        @Override
        public Array<T> eager() {
            super.eager();
            return this;
        }

        @Override
        public Array<T> parallel() {
            super.parallel();
            return this;
        }

        @Override
        public Array<T> sequential() {
            super.sequential();
            return this;
        }

        T[] data;

        Array(T[] data) {
            E.NPE(data);
            this.data = data;
        }

        @Override
        public int size() throws UnsupportedOperationException {
            return data.length;
        }

        @Override
        public Iterator<T> iterator() {
            final int size = size();
            return new ReadOnlyIterator<T>() {
                int cursor = 0;

                @Override
                public boolean hasNext() {
                    return cursor < size;
                }

                @Override
                public T next() {
                    if (cursor >= size) {
                        throw new NoSuchElementException();
                    }
                    return data[cursor++];
                }
            };
        }

        @Override
        public Iterator<T> reverseIterator() {
            final int size = size();
            return new ReadOnlyIterator<T>() {
                int cursor = size - 1;

                @Override
                public boolean hasNext() {
                    return cursor < 0;
                }

                @Override
                public T next() {
                    if (cursor < 0) {
                        throw new NoSuchElementException();
                    }
                    return data[cursor--];
                }
            };
        }

        @Override
        public ReversibleSequence<T> reverse() throws UnsupportedOperationException {
            if (isLazy()) {
                return ReversedRSeq.of(this);
            }
            if (isMutable()) {
                Algorithms.arrayReverseInplace().reverse(data, 0, data.length);
                return this;
            }
            T[] newData = (T[]) Algorithms.ARRAY_REVERSE.apply(data, 0, data.length);
            return of(newData);
        }

        public static <T> Array<T> of(T[] data) {
            return new Array<T>(data);
        }

        public static <T> Array<T> copyOf(T[] data) {
            int len = data.length;
            T[] newData = _.newArray(data, len);
            System.arraycopy(data, 0, newData, 0, len);
            return new Array<T>(newData);
        }
    }

    /**
     * Define a Range data structure which contains a discrete sequence of elements start from {@link #from()}
     * until {@link #to()}. The {@code from} element should be contained in the range, while the {@code to}
     * element should be exclusive from the range. While the {@code from} and {@code to} defines the boundary of
     * an range, the {@link #step()} defines how to step from one element to another in the range.
     *
     * @param <ELEMENT> the element type
     */
    public static interface Range<ELEMENT> extends Sequence<ELEMENT> {
        /**
         * Returns the {@code from} value (inclusive) in the range
         *
         * @return {@code from}
         * @since 0.2
         */
        ELEMENT from();

        /**
         * Returns the {@code to} value (exclusive) of the range
         *
         * @return {@code to}
         * @since 0.2
         */
        ELEMENT to();

        /**
         * Check if an element is contained in this range
         *
         * @param element the element to be checked
         * @return {@code true} if the element specified is contained in the range
         * @since 0.2
         */
        boolean contains(ELEMENT element);

        /**
         * Check if this range contains all elements of another range of the same type (identified by
         * {@link #order()} and {@link #step()}).
         *
         * @param r2 the range to be tested
         * @return {@code true} if this range contains all elements of {@code r2}
         * @since 0.2
         */
        boolean containsAll(Range<ELEMENT> r2);

        /**
         * Returns a {@link _.Func2} function that takes two elements in the range domain and returns an integer to
         * determine the order of the two elements. See {@link java.util.Comparator#compare(Object, Object)} for
         * semantic of the function.
         * <p/>
         * <p>If any one of the element applied is {@code null} the function should throw out
         * {@link NullPointerException}</p>
         *
         * @return a function implement the ordering logic
         * @since 0.2
         */
        Comparator<ELEMENT> order();

        /**
         * Returns a {@link _.Func2} function that applied to an element in this {@code Range} and
         * an integer {@code n} indicate the number of steps. The result of the function is an element in
         * the range or the range domain after moving {@code n} steps based on the element.
         * <p/>
         * <p>If the element apply is {@code null}, the function should throw out
         * {@link NullPointerException}; if the resulting element is not defined in the range
         * domain, the function should throw out {@link NoSuchElementException}</p>
         *
         * @return a function implement the stepping logic
         * @since 0.2
         */
        _.Func2<ELEMENT, Integer, ELEMENT> step();

        /**
         * Returns an new range this range and another range {@code r2} merged together. The two ranges must have
         * the equal {@link #step()} and {@link #order()} operator to be merged, otherwise,
         * {@link org.osgl.exception.InvalidArgException} will be thrown out
         * <p/>
         * <p/>
         * <p>The two ranges must be either overlapped or immediately connected to each other as per
         * {@link #step()} definition. Otherwise an {@link org.osgl.exception.InvalidArgException}
         * will be throw out:
         * <ul>
         * <li>if one range contains another range entirely, then the larger range is returned</li>
         * <li>if the two ranges overlapped or immediately connected to each other, then an range
         * contains all elements of the two ranges will be returned</li>
         * <li>an {@link org.osgl.exception.InvalidArgException} will be thrown out if the two ranges does not connected
         * to each other</li>
         * </ul>
         * </p>
         *
         * @param r2 the range to be merged with this range
         * @return an new range contains all elements in this range and r2
         * @throws org.osgl.exception.InvalidArgException if the two ranges does not have
         *                                                the same {@link #step()} operator or does not connect to each other
         * @since 0.2
         */
        Range<ELEMENT> merge(Range<ELEMENT> r2);

        ELEMENT last();

        Range<ELEMENT> tail(int n);

        Range<ELEMENT> reverse();

        Iterator<ELEMENT> reverseIterator();

        <R> R reduceRight(R identity, _.Func2<R, ELEMENT, R> accumulator);

        _.Option<ELEMENT> reduceRight(_.Func2<ELEMENT, ELEMENT, ELEMENT> accumulator);

        _.Option<ELEMENT> findLast(_.Function<? super ELEMENT, Boolean> predicate);

        /**
         * {@inheritDoc}
         *
         * @param visitor {@inheritDoc}
         * @return this Range instance
         * @since 0.2
         */
        @Override
        Range<ELEMENT> accept(_.Function<? super ELEMENT, ?> visitor);

        @Override
        Range<ELEMENT> each(_.Function<? super ELEMENT, ?> visitor);

        @Override
        Range<ELEMENT> forEach(_.Function<? super ELEMENT, ?> visitor);

        /**
         * {@inheritDoc}
         *
         * @param visitor {@inheritDoc}
         * @return this Range instance
         * @since 0.2
         */
        @Override
        Range<ELEMENT> acceptLeft(_.Function<? super ELEMENT, ?> visitor);

        /**
         * iterate through the range from tail to head
         *
         * @param visitor a function to visit elements in the range
         * @return this Range instance
         * @since 0.2
         */
        Range<ELEMENT> acceptRight(_.Function<? super ELEMENT, ?> visitor);
    }

    /**
     * The osgl List interface is a mixture of {@link java.util.List} and osgl {@link Sequence}
     *
     * @param <T> the element type of the {@code List}
     * @since 0.2
     */
    public static interface List<T> extends java.util.List<T>, ReversibleSequence<T> {

        /**
         * A cursor points to an element of a {@link List}. It performs like
         * {@link java.util.ListIterator} but differs in the following way:
         * <p/>
         * <ul>
         * <li>Add insert, append method</li>
         * <li>Support method chain calling style for most methods</li>
         * <li>A clear get() method to get the element the cursor point to</li>
         * <li>Unlike next/previous method, the new forward/backward method
         * returns a Cursor reference</li>
         * </ul>
         *
         * @param <T>
         */
        public interface Cursor<T> {

            /**
             * Returns true if the cursor is not obsolete and points to an element
             * in the list
             *
             * @return true if this cursor is not obsolete and point to an element
             */
            boolean isDefined();

            /**
             * Returns the index of the element to which the cursor pointed
             *
             * @return the cursor index
             */
            int index();

            /**
             * Returns if the cursor can be moved forward to get the
             * next element
             *
             * @return {@code true} if there are element after the cursor in the
             * underline list
             */
            boolean hasNext();

            /**
             * Returns if the cursor can be moved backward to get the previous
             * element
             *
             * @return {@code true} if there are element before the cursor in the
             * underline list
             */
            boolean hasPrevious();

            /**
             * Move the cursor forward to make it point to the next element to
             * the current element
             *
             * @return the cursor points to the next element
             * @throws UnsupportedOperationException if cannot move forward anymore
             */
            Cursor<T> forward() throws UnsupportedOperationException;

            /**
             * Move the cursor backward to make it point to the previous element to
             * the current element
             *
             * @return the cursor points to the previous element
             * @throws UnsupportedOperationException if cannot move backward anymore
             */
            Cursor<T> backward() throws UnsupportedOperationException;

            /**
             * Park the cursor at the position before the first element.
             * <p/>
             * <p>After calling this method, {@link #isDefined()}
             * shall return {@code false}</p>
             *
             * @return this cursor
             */
            Cursor<T> parkLeft();

            /**
             * Park the cursor at the position after the last element
             * <p/>
             * <p>After calling this method, {@link #isDefined()}
             * shall return {@code false}</p>
             *
             * @return this cursor
             */
            Cursor<T> parkRight();

            /**
             * Returns the element this cursor points to. If the cursor isn't point
             * to any element, calling to this method will trigger
             * {@code NoSuchElementException} been thrown out. The only case
             * the cursor doesn't point to any element is when it is initialized
             * in which case the cursor index is -1
             *
             * @return the current element
             * @throws NoSuchElementException if the cursor isn't point to any element
             */
            T get() throws NoSuchElementException;

            /**
             * Replace the element this cursor points to with the new element specified.
             *
             * @param t the new element to be set to this cursor
             * @return the cursor itself
             * @throws IndexOutOfBoundsException if the cursor isn't point to any element
             * @throws NullPointerException      if when passing null value to this method and
             *                                   the underline list does not allow null value
             */
            Cursor<T> set(T t) throws IndexOutOfBoundsException, NullPointerException;

            /**
             * Remove the current element this cursor points to. After the element
             * is removed, the cursor points to the next element if there is next,
             * or if there isn't next element, the cursor points to the previous
             * element, or if there is previous element neither, then the cursor
             * points to {@code -1} position and the current element is not defined
             *
             * @return the cursor itself
             * @throws UnsupportedOperationException if the operation is not supported
             *                                       by the underline container does not support removing elements
             * @throws NoSuchElementException        if the cursor is parked either left or
             *                                       right
             */
            Cursor<T> drop() throws NoSuchElementException, UnsupportedOperationException;

            /**
             * Add an element in front of the element this cursor points to.
             * After added, the cursor should still point to the current element
             *
             * @param t the element to be inserted
             * @return this cursor which is still pointing to the current element
             * @throws IndexOutOfBoundsException if the current element is undefined
             */
            Cursor<T> prepend(T t) throws IndexOutOfBoundsException;

            /**
             * Add an element after the element this cursor points to.
             * After added, the cursor should still point to the current element
             *
             * @param t the element to be added
             * @return this cursor which is still pointing to the current element
             */
            Cursor<T> append(T t);
        }


        /**
         * Returns this traversable and make sure {@link C.Feature#PARALLEL} is set
         *
         * @return this reference with parallel turned on
         */
        @Override
        List<T> parallel();

        /**
         * Returns this traversable and make sure {@link C.Feature#PARALLEL} is unset
         *
         * @return this reference with parallel turned off
         */
        @Override
        List<T> sequential();

        /**
         * Returns this traversable and make sure {@link C.Feature#LAZY} is set
         *
         * @return this reference with lazy turned on
         */
        @Override
        List<T> lazy();

        /**
         * Returns this traversable and make sure {@link C.Feature#LAZY} is unset
         *
         * @return this reference with lazy turned off
         */
        @Override
        List<T> eager();

        /**
         * Returns an immutable list contains all elements of the current list.
         * If the current list is immutable, then return the current list itself.
         *
         * @return an immutable list.
         * @see #readOnly()
         */
        List<T> snapshot();

        /**
         * Returns a view of this list that is readonly. If the current list is
         * readonly or immutable then return the current list itself
         *
         * @return a readonly view of this list
         */
        List<T> readOnly();

        /**
         * Returns a mutable copy of this list
         *
         * @return a mutable list contains all elements of this list
         */
        List<T> copy();

        /**
         * Returns a sorted copy of this list.
         * <p/>
         * <p>Note if the element type T is not a {@link java.lang.Comparable} then
         * this method returns a {@link #copy() copy} of this list without any order
         * changes</p>
         *
         * @return an ordered copy of this list
         */
        List<T> sort();

        /**
         * Returns a sorted copy of this list. The order is specified by the comparator
         * provided
         *
         * @param comparator specify the order of elements in the result list
         * @return an ordered copy of this list
         */
        List<T> sort(Comparator<? super T> comparator);

        @Override
        List<T> subList(int fromIndex, int toIndex);

        boolean addAll(Iterable<? extends T> iterable);

        /**
         * {@inheritDoc}
         *
         * @param n {@inheritDoc}
         * @return A List contains first {@code n} items in this List
         */
        @Override
        List<T> head(int n);

        /**
         * {@inheritDoc}
         * <p/>
         * <p>This method does not alter the underline list</p>
         *
         * @param n {@inheritDoc}
         * @return A list contains first {@code n} items in this list
         */
        @Override
        List<T> take(int n);

        /**
         * {@inheritDoc}
         *
         * @return A list contains all elements in this list except
         * the first one
         */
        @Override
        List<T> tail();

        /**
         * {@inheritDoc}
         * <p/>
         * <p>This method does not alter the underline list</p>
         *
         * @param n {@inheritDoc}
         * @return A list contains last {@code n} items in this list
         */
        @Override
        List<T> tail(int n);

        /**
         * {@inheritDoc}
         * <p/>
         * <p>This method does not alter the underline list</p>
         *
         * @param n {@inheritDoc}
         * @return a List contains all elements of this list
         * except the first {@code n} number
         */
        List<T> drop(int n);

        /**
         * {@inheritDoc}
         * <p/>
         * <p>This method does not alter the underline list</p>
         *
         * @param predicate
         * @return {@inheritDoc}
         */
        @Override
        List<T> dropWhile(_.Function<? super T, Boolean> predicate);

        /**
         * {@inheritDoc}
         * <p/>
         * <p>This method does not alter the underline list</p>
         *
         * @param predicate {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        List<T> takeWhile(_.Function<? super T, Boolean> predicate);

        /**
         * For mutable list, remove all element that matches the predicate
         * specified from this List and return this list once done.
         * <p/>
         * <p>For immutable or readonly list, an new List contains all element from
         * this list that does not match the predicate specified is returned</p>
         *
         * @param predicate test whether an element should be removed frmo
         *                  return list
         * @return a list contains all element that does not match the
         * predicate specified
         */
        List<T> remove(_.Function<? super T, Boolean> predicate);

        @Override
        <R> C.List<R> map(_.Function<? super T, ? extends R> mapper);

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a List of {@code R} type element that are mapped from this sequences
         * @since 0.2
         */
        @Override
        <R> List<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper);

        @Override
        List<T> filter(_.Function<? super T, Boolean> predicate);

        /**
         * Find the first element in this list that matches the predicate.
         * Return a cursor point to the location of the element. If no
         * such element is found then a cursor that point to {@code -1}
         * is returned.
         *
         * @param predicate test the element
         * @return the reference to the list itself or an new List without the
         * first element matches the predicate if this is a readonly
         * list
         */
        Cursor<T> locateFirst(_.Function<T, Boolean> predicate);

        /**
         * Locate any one element in the list that matches the predicate.
         * Returns the cursor point to the element found, or a cursor
         * that is not defined if no such element found in the list. In
         * a parallel locating the element been found might not be the
         * first element matches the predicate
         *
         * @param predicate
         * @return the reference to the list itself or an new List without
         * and element matches the predicate if this is a readonly
         * list
         */
        Cursor<T> locate(_.Function<T, Boolean> predicate);

        /**
         * Locate the first element in this list that matches the predicate.
         * Return a cursor point to the location of the element. If no
         * such element is found then a cursor that point to {@code -1}
         * is returned.
         *
         * @param predicate test the element
         * @return the reference to the list itself or an new List without the
         * first element matches the predicate if this is a readonly
         * list
         */
        Cursor<T> locateLast(_.Function<T, Boolean> predicate);

        /**
         * Insert an element at the position specified by {@code index}.
         * <p>If this list is readonly or immutable, then an new
         * list should be created with all elements in this list
         * and the new element inserted at the specified position.
         * The new list should be mutable and read write</p>
         *
         * @param index specify the position where the element should be inserted
         * @param t     the element to be inserted
         * @return a reference to the List itself
         * @throws IndexOutOfBoundsException if index &lt; 0 or index &gt; size()
         */
        List<T> insert(int index, T t) throws IndexOutOfBoundsException;

        /**
         * {@inheritDoc}
         *
         * @param t {@inheritDoc}
         * @return a list contains elements in this list followed
         * by {@code t}
         */
        @Override
        List<T> append(T t);

        /**
         * {@inheritDoc}
         *
         * @param iterable
         * @return a List contains all elements of this list followed
         * by all elements in the iterable
         */
        List<T> append(Collection<? extends T> iterable);

        /**
         * Returns a List contains all elements in this List followed by
         * all elements in the specified List.
         * <p/>
         * <p>A mutable List implementation might choose to add elements
         * from the specified list directly to this list and return this
         * list directly</p>
         * <p/>
         * <p>For a read only or immutable list, it must create an new list
         * to avoid update this list</p>
         *
         * @param list the list in which elements will be appended
         *             to this list
         * @return a list contains elements of both list
         */
        List<T> append(List<T> list);

        @Override
        List<T> prepend(T t);

        List<T> prepend(Collection<? extends T> collection);

        List<T> prepend(List<T> list);

        @Override
        List<T> reverse();

        /**
         * Returns a List contains all elements in this List and not in
         * the {@code col} collection specified
         *
         * @param col the collection in which elements should
         *            be excluded from the result List
         * @return a List contains elements only in this list
         */
        List<T> without(Collection<? super T> col);

        /**
         * Returns a list contains all elements in the list except the
         * one specified
         *
         * @param element the element that should not be in the resulting list
         * @return a list without the element specified
         */
        List<T> without(T element);

        /**
         * Returns a list contains all elements in the list except the
         * ones specified
         *
         * @param element  the element that should not be in the resulting list
         * @param elements the array contains elements that should not be in the resulting list
         * @return a list without the element specified
         */
        List<T> without(T element, T... elements);

        @Override
        List<T> accept(_.Function<? super T, ?> visitor);

        @Override
        List<T> each(_.Function<? super T, ?> visitor);

        @Override
        List<T> forEach(_.Function<? super T, ?> visitor);

        @Override
        List<T> acceptLeft(_.Function<? super T, ?> visitor);

        @Override
        List<T> acceptRight(_.Function<? super T, ?> visitor);


        /**
         * Returns a list formed from this list and another iterable
         * collection by combining corresponding elements in pairs.
         * If one of the two collections is longer than the other,
         * its remaining elements are ignored.
         *
         * @param list the part B to be zipped with this list
         * @param <T2> the type of the iterable
         * @return an new list containing pairs consisting of
         * corresponding elements of this sequence and that.
         * The length of the returned collection is the
         * minimum of the lengths of this sequence and that.
         */
        <T2> List<_.T2<T, T2>> zip(java.util.List<T2> list);

        /**
         * Returns a list formed from this list and another iterable
         * collection by combining corresponding elements in pairs.
         * If one of the two collections is longer than the other,
         * placeholder elements are used to extend the shorter collection
         * to the length of the longer.
         *
         * @param list the part B to be zipped with this list
         * @param <T2> the type of the iterable
         * @param def1 the element to be used to fill up the result if
         *             this sequence is shorter than that iterable
         * @param def2 the element to be used to fill up the result if
         *             the iterable is shorter than this sequence
         * @return a new list containing pairs consisting of
         * corresponding elements of this list and that.
         * The length of the returned collection is the
         * maximum of the lengths of this list and that.
         */
        <T2> List<_.T2<T, T2>> zipAll(java.util.List<T2> list, T def1, T2 def2);

        /**
         * Zip this sequence with its indices
         *
         * @return A new list containing pairs consisting of all
         * elements of this list paired with their index.
         * Indices start at 0.
         */
        Sequence<_.T2<T, Integer>> zipWithIndex();
    }

//    /**
//     * The osgl Set interface is a mixture of {@link java.util.Set} and osgl {@link Traversable}
//     *
//     * @param <T> the element type of the {@code Set}
//     * @since 0.2
//     */
//    public static interface Set<T> extends java.util.Set<T>, Traversable<T> {
//    }
//
//    /**
//     * The osgl sorted Set interface is a mixture of {@link java.util.Set} and osgl {@link Sequence}
//     *
//     * @param <T> the element type of the {@code SortedSet}
//     * @since 0.2
//     */
//    public static interface SortedSet<T> extends java.util.SortedSet<T>, ReversibleSequence<T> {
//    }
//

    public static class Map<K, V> implements java.util.Map<K, V>, Serializable {
        public static class Entry<K, V> extends _.T2<K, V> implements java.util.Map.Entry<K, V> {
            public Entry(K _1, V _2) {
                super(_1, _2);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override
            public K getKey() {
                return _1;
            }

            @Override
            public V getValue() {
                return _2;
            }

            @Override
            public V setValue(V value) {
                throw E.unsupport();
            }

            public static <K, V> Entry<K, V> valueOf(K k, V v) {
                return new Entry<K, V>(k, v);
            }
        }

        private java.util.Map<K, V> _m;

        private boolean ro;

        protected Map(boolean readOnly, Object... args) {
            HashMap<K, V> map = new HashMap<K, V>();
            int len = args.length;
            for (int i = 0; i < len; i += 2) {
                K k = (K) args[i];
                V v = null;
                if (i + 1 < len) {
                    v = (V) args[i + 1];
                }
                map.put(k, v);
            }
            ro = readOnly;
            if (readOnly) {
                _m = Collections.unmodifiableMap(map);
            } else {
                _m = map;
            }
        }

        protected Map(boolean readOnly, java.util.Map<? extends K, ? extends V> map) {
            E.NPE(map);
            boolean sorted = map instanceof SortedMap;
            java.util.Map<K, V> m = sorted ? new TreeMap<K, V>() : new HashMap<K, V>();
            for (K k : map.keySet()) {
                V v = map.get(k);
                m.put(k, v);
            }
            ro = readOnly;
            if (readOnly) {
                _m = Collections.unmodifiableMap(m);
            } else {
                _m = m;
            }
        }

        @Override
        public int size() {
            return _m.size();
        }

        @Override
        public boolean isEmpty() {
            return _m.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return _m.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return _m.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return _m.get(key);
        }

        @Override
        public V put(K key, V value) {
            return _m.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return _m.remove(key);
        }

        @Override
        public void putAll(java.util.Map<? extends K, ? extends V> m) {
            _m.putAll(m);
        }

        @Override
        public void clear() {
            _m.clear();
        }

        @Override
        public java.util.Set<K> keySet() {
            return _m.keySet();
        }

        @Override
        public Collection<V> values() {
            return _m.values();
        }

        @Override
        public Set<java.util.Map.Entry<K, V>> entrySet() {
            Set<java.util.Map.Entry<K, V>> set = C.newSet();
            for (K k : _m.keySet()) {
                V v = _m.get(k);
                set.add(Entry.valueOf(k, v));
            }
            return set;
        }

        @Override
        public int hashCode() {
            return _m.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder sb = S.builder(_m.toString());
            if (ro) {
                sb.append("[ro]");
            }
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

           	if (!(o instanceof java.util.Map)) {
                return false;
            }

            if (o instanceof Map) {
                return o.equals(_m) && ((Map)o).ro == ro;
            }

            return o.equals(_m);
        }

        // --- extensions
        public boolean readOnly() {
            return ro;
        }

        public Map<K, V> readOnly(boolean readOnly) {
            if (ro ^ readOnly) {
                return new Map<K, V>(readOnly, _m);
            } else {
                return this;
            }
        }

        private void writeObject(java.io.ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            s.writeObject(_m);
            if (ro) s.writeInt(1);
            else s.writeInt(0);
        }

        private static final long serialVersionUID = 262498820763181265L;

        private void readObject(java.io.ObjectInputStream s)
        throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            _m = (java.util.Map)s.readObject();
            int i = s.readInt();
            ro = i != 0;
        }
    }

    public static interface Set<T> extends java.util.Set<T>, Traversable<T> {
        @Override
        Set<T> parallel();

        @Override
        Set<T> sequential();

        @Override
        Set<T> lazy();

        @Override
        Set<T> eager();

        @Override
        Set<T> filter(_.Function<? super T, Boolean> predicate);

        @Override
        Set<T> accept(_.Function<? super T, ?> visitor);

        @Override
        Set<T> each(_.Function<? super T, ?> visitor);

        @Override
        Set<T> forEach(_.Function<? super T, ?> visitor);
    }

    public static interface ListOrSet<T> extends List<T>, Set<T> {
        @Override
        ListOrSet<T> parallel();

        @Override
        ListOrSet<T> sequential();

        @Override
        ListOrSet<T> lazy();

        @Override
        ListOrSet<T> eager();

        @Override
        ListOrSet<T> accept(_.Function<? super T, ?> visitor);

        @Override
        ListOrSet<T> each(_.Function<? super T, ?> visitor);

        @Override
        ListOrSet<T> forEach(_.Function<? super T, ?> visitor);

        @Override
        ListOrSet<T> filter(_.Function<? super T, Boolean> predicate);
    }

    /**
     * Defines a factory to create {@link java.util.List java List} instance
     * used by {@link DelegatingList} to create it's backing data structure
     *
     * @since 0.2
     */
    public static interface ListFactory {
        /**
         * Create an empty <code>java.util.List</code> contains the generic type E
         *
         * @param <ET> the generic type of the list element
         * @return A java List instance contains elements of generic type E
         */
        <ET> java.util.List<ET> create();

        /**
         * Create a <code>java.util.List</code> pre populated with elements
         * of specified collection
         *
         * @param collection the collection whose elements are to be placed into this list
         * @param <ET>       the generic type of the list element
         * @return The List been created
         * @throws <code>NullPointerException</code> if the specified collection is null
         */
        <ET> java.util.List<ET> create(Collection<? extends ET> collection);

        /**
         * Create a <code>java.util.List</code> with initial capacity
         *
         * @param initialCapacity
         * @param <ET>            the generic type of the list element
         * @return the list been created
         */
        <ET> java.util.List<ET> create(int initialCapacity);

        static enum Predefined {
            ;
            static final ListFactory JDK_ARRAYLIST_FACT = new ListFactory() {
                @Override
                public <ET> java.util.List<ET> create() {
                    return new ArrayList<ET>();
                }

                @Override
                public <ET> java.util.List<ET> create(Collection<? extends ET> collection) {
                    return new ArrayList<ET>(collection);
                }

                @Override
                public <ET> java.util.List<ET> create(int initialCapacity) {
                    return new ArrayList<ET>(initialCapacity);
                }
            };
            static final ListFactory JDK_LINKEDLIST_FACT = new ListFactory() {
                @Override
                public <ET> java.util.List<ET> create() {
                    return new LinkedList<ET>();
                }

                @Override
                public <ET> java.util.List<ET> create(Collection<? extends ET> collection) {
                    return new LinkedList<ET>(collection);
                }

                @Override
                public <ET> java.util.List<ET> create(int initialCapacity) {
                    return new LinkedList<ET>();
                }
            };

            static ListFactory defLinked() {
                return JDK_LINKEDLIST_FACT;
            }

            static ListFactory defRandomAccess() {
                return JDK_ARRAYLIST_FACT;
            }
        }
    }

    /**
     * "osgl.list.factory", the property key to configure user defined
     * {@link ListFactory list factory}.
     * <p/>
     * Upon loaded, osgl tried to get a class name string from system
     * properties use this configuration key. If osgl find the String
     * returned is not empty then it will initialize the list factory
     * use the class name configured. If any exception raised during the
     * initialization, then it might cause the JVM failed to boot up
     *
     * @since 0.2
     */
    public static final String CONF_LINKED_LIST_FACTORY = "osgl.linked_list.factory";

    /**
     * "osgl.random_access_list.factory", the property key to configure user defined {@link ListFactory
     * random access list factory}. See {@link #CONF_LINKED_LIST_FACTORY} for how osgl use this configuration
     *
     * @since 0.2
     */
    public static final String CONF_RANDOM_ACCESS_LIST_FACTORY = "osgl.random_access_list.factory";

    static ListFactory linkedListFact;

    static {
        String factCls = System.getProperty(CONF_LINKED_LIST_FACTORY);
        if (null == factCls) {
            linkedListFact = ListFactory.Predefined.defLinked();
        } else {
            _.Option<ListFactory> fact = _.safeNewInstance(factCls);
            if (fact.isDefined()) {
                linkedListFact = fact.get();
            } else {
                linkedListFact = ListFactory.Predefined.defLinked();
            }
        }
    }

    static ListFactory randomAccessListFact;

    static {
        String factCls = System.getProperty(CONF_RANDOM_ACCESS_LIST_FACTORY);
        if (null == factCls) {
            randomAccessListFact = ListFactory.Predefined.defRandomAccess();
        } else {
            _.Option<ListFactory> fact = _.safeNewInstance(factCls);
            if (fact.isDefined()) {
                randomAccessListFact = fact.get();
            } else {
                randomAccessListFact = ListFactory.Predefined.defRandomAccess();
            }
        }
    }

    public static boolean empty(Collection<?> col) {
        return null == col || col.isEmpty();
    }

    public static boolean notEmpty(Collection<?> col) {
        return !empty(col);
    }

    public static boolean isEmpty(Collection<?> col) {
        return empty(col);
    }

    // --- factory methods ---

    /**
     * Returns a {@link Range} of integer specified by {@code from} and {@code to}. {@code from}
     * can be less or larger than {@code to}.
     *
     * @param from specify the left side of the range (inclusive)
     * @param to   specify the right hand side of the range (exclusive)
     * @return a range of integer @{code [from .. to)}
     */
    public static Range<Integer> range(int from, int to) {
        return new LazyRange<Integer>(from, to, N.F.INT_RANGE_STEP);
    }

    /**
     * Returns a {@link Range} of byte specified by {@code from} and {@code to}. {@code from}
     * can be less or larger than {@code to}.
     *
     * @param from specify the left side of the range (inclusive)
     * @param to   specify the right hand side of the range (exclusive)
     * @return a range of byte @{code [from .. to)}
     */
    public static Range<Byte> range(byte from, byte to) {
        return new LazyRange<Byte>(from, to, N.F.BYTE_RANGE_STEP);
    }

    /**
     * Returns a {@link Range} of short specified by {@code from} and {@code to}. {@code from}
     * can be less or larger than {@code to}.
     *
     * @param from specify the left side of the range (inclusive)
     * @param to   specify the right hand side of the range (exclusive)
     * @return a range of short @{code [from .. to)}
     */
    public static Range<Short> range(short from, short to) {
        return new LazyRange<Short>(from, to, N.F.SHORT_RANGE_STEP);
    }

    /**
     * Returns a {@link Range} of long specified by {@code from} and {@code to}. {@code from}
     * can be less or larger than {@code to}.
     *
     * @param from specify the left side of the range (inclusive)
     * @param to   specify the right hand side of the range (exclusive)
     * @return a range of long @{code [from .. to)}
     */
    public static Range<Long> range(long from, long to) {
        return new LazyRange<Long>(from, to, N.F.LONG_RANGE_STEP);
    }

    /**
     * Returns a {@link Range} of non-negative integers start from {@code 0} to {@code Integer.MAX_VALUE}. Note
     * unlike traditional definition of natural number, zero is included in the range returned
     *
     * @return a range of non negative integers
     */
    public static Range<Integer> naturalNumbers() {
        return new LazyRange<Integer>(1, Integer.MAX_VALUE, N.F.INT_RANGE_STEP);
    }

    /**
     * Returns a {@link Range} of non-negative even numbers starts from {@code 0} to
     * {@code Integer.MAX_VALUE}.
     *
     * @return a range of non-negative even numbers
     */
    public static Range<Integer> evenNumbers() {
        return new LazyRange<Integer>(0, Integer.MAX_VALUE, N.F.intRangeStep(2));
    }

    /**
     * Returns a {@link Range} of positive odd numbers starts from {@code 1} to
     * {@code Integer.MAX_VALUE}.
     *
     * @return a range of positive odd numbers
     */
    public static Range<Integer> oddNumbers() {
        return new LazyRange<Integer>(1, Integer.MAX_VALUE, N.F.intRangeStep(2));
    }

    public static final List EMPTY_LIST = Nil.list();
    public static final Set EMPTY_SET = Nil.set();
    public static final java.util.Map EMPTY_MAP = Collections.EMPTY_MAP;
    public static final ListOrSet EMPTY = Nil.EMPTY;

    public static <T> ListOrSet<T> empty() {
        return EMPTY;
    }

    /**
     * Returns an empty immutable list
     *
     * @param <T> the type of the list element
     * @return the empty list
     */
    public static <T> List<T> list() {
        return Nil.list();
    }

    public static <T> List<T> emptyListOf(Class<T> c) {
        return Nil.list();
    }

    public static <T> List<T> list(T t) {
        return _.val(t);
    }

    /**
     * Creates an immutable list of an array of elements.
     * <p/>
     * <p>Note the array will not be copied, instead it will
     * be used directly as the backing data for the list.
     * To create an immutable list with a copy of the array
     * specified. Use the {@link #listOf(Object[])} method</p>
     *
     * @param ta  an array of elements
     * @param <T> the element type
     * @return an immutable list backed by the specified array
     */
    public static <T> List<T> listOf(T... ta) {
        return ImmutableList.of(ta);
    }

    /**
     * Creates an immutable list from an array. The element of the array is copied
     * to the list been returned.
     *
     * @param ta  the array
     * @param <T>
     * @return
     */
    public static <T> List<T> list(T t, T... ta) {
        int len = ta.length;
        T[] a = _.newArray(ta, len + 1);
        a[0] = t;
        System.arraycopy(ta, 0, a, 1, len);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Byte list from a byte (primary type) array.
     * <p>At the moment the implementation will convert the byte (primary)
     * array to Byte (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary byte
     * @return a Byte typed list
     */
    public static List<Byte> listOf(byte[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable Byte list of a byte (pimary type) array.
     * The elements of the array is copied into the returned list
     *
     * @param elements an array of bytes
     * @return an immutable list contains specified elements
     */
    public static List<Byte> list(byte[] elements) {
        if (elements.length == 0) {
            return Nil.list();
        }
        Byte[] ba = _.asObject(elements);
        return ImmutableList.of(ba);
    }


    /**
     * Create an immutable Short list from a short (primary type) array.
     * <p>At the moment the implementation will convert the short (primary)
     * array to Short (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary short
     * @return a Short typed list
     */
    public static List<Short> listOf(short[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable Short list of a short array.
     * The elements of the array is copied into the returned list
     *
     * @param elements an array of shorts
     * @return an immutable list contains specified elements
     */
    public static List<Short> list(short[] elements) {
        if (0 == elements.length) {
            return Nil.list();
        }
        Short[] a = _.asObject(elements);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Integer list from an int (primary type) array.
     * <p>At the moment the implementation will convert the int (primary)
     * array to Integer (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary int
     * @return an Integer list
     */
    public static List<Integer> listOf(int[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable integer list of a int array. If an empty array specified,
     * the nan empty immutable list is returned
     *
     * @param elements an array of int
     * @return an immutable list contains specified elements
     */
    public static List<Integer> list(int[] elements) {
        if (elements.length == 0) {
            return Nil.list();
        }
        Integer[] a = _.asObject(elements);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Long list from a long (primary type) array.
     * <p>At the moment the implementation will convert the long (primary)
     * array to Long (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary long
     * @return an Long list
     */
    public static List<Long> listOf(long[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable Long list of a long array. If an empty array specified,
     * the nan empty immutable list is returned
     *
     * @param elements an array of long
     * @return an immutable list contains specified elements
     */
    public static List<Long> list(long[] elements) {
        if (0 == elements.length) {
            return list();
        }
        return ImmutableList.of(_.asObject(elements));
    }

    /**
     * Create an immutable Float list from a float (primary type) array.
     * <p>At the moment the implementation will convert the float (primary)
     * array to Float (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary float
     * @return an Float list
     */
    public static List<Float> listOf(float[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable byte list of a float array. If an empty array specified,
     * the nan empty immutable list is returned
     *
     * @param elements an array of floats
     * @return an immutable list contains specified elements
     */
    public static List<Float> list(float[] elements) {
        if (0 == elements.length) {
            return list();
        }
        return ImmutableList.of(_.asObject(elements));
    }

    /**
     * Create an immutable Double list from an double (primary type) array.
     * <p>At the moment the implementation will convert the double (primary)
     * array to Double (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary double
     * @return an Double list
     */
    public static List<Double> listOf(double[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable Byte list of a double array. If an empty array specified,
     * the nan empty immutable list is returned
     *
     * @param elements an array of double
     * @return an immutable list contains specified elements
     */
    public static List<Double> list(double[] elements) {
        if (0 == elements.length) {
            return list();
        }
        return ImmutableList.of(_.asObject(elements));
    }

    public static <T> List<T> list(Iterable<? extends T> iterable) {
        return ListBuilder.toList(iterable);
    }

    public static <T> List<T> list(Collection<? extends T> col) {
        return ListBuilder.toList(col);
    }

    public static <T> List<T> list(java.util.List<T> javaList) {
        if (javaList instanceof List) {
            List<T> list = _.cast(javaList);
            if (list.is(Feature.IMMUTABLE)) {
                return list;
            }
        }
        return new ReadOnlyDelegatingList<T>(javaList);
    }

    public static <T> List<T> singletonList(T t) {
        return list(t);
    }

    public static <T> List<T> wrap(java.util.List<T> list) {
        return DelegatingList.wrap(list);
    }

    public static <T> List<T> newSizedList(int size) {
        return new DelegatingList<T>(size);
    }

    public static <T> List<T> newList() {
        return newSizedList(10);
    }

    public static <T> List<T> newList(Iterable<? extends T> iterable) {
        return new DelegatingList<T>(iterable);
    }

    public static <T> List<T> newList(T t) {
        return new DelegatingList<T>(10).append(t);
    }

    public static <T> List<T> newList(T t1, T t2) {
        return new DelegatingList<T>(10).append(t1).append(t2);
    }

    public static <T> List<T> newList(T t1, T t2, T t3) {
        return new DelegatingList<T>(10).append(t1).append(t2).append(t3);
    }

    public static <T> List<T> newList(T t1, T t2, T t3, T... ta) {
        int len = ta.length;
        List<T> l = new DelegatingList<T>(len + 3).append(t1).append(t2).append(t3);
        l.addAll(listOf(ta));
        return l;
    }

    public static <T> List<T> newListOf(T[] ts) {
        return new DelegatingList<T>(C.listOf(ts));
    }

    public static <T> Sequence<T> seq(Iterable<? extends T> iterable) {
        if (iterable instanceof Sequence) {
            return ((Sequence<T>) iterable);
        }
        return IterableSeq.of(iterable);
    }

    public static <T, R> Sequence<R> map(Sequence<T> seq, _.Function<? super T, ? extends R> mapper) {
        if (seq instanceof ReversibleSequence) {
            return map((ReversibleSequence<T>) seq, mapper);
        }
        return new MappedSeq<T, R>(seq, mapper);
    }

    public static <T, R> ReversibleSequence<R> map(ReversibleSequence<T> seq, _.Function<? super T, ? extends R> mapper
    ) {
        return new ReversibleMappedSeq<T, R>(seq, mapper);
    }

    public static <T> Sequence<T> filter(Sequence<T> seq, _.Function<? super T, Boolean> predicate) {
        return new FilteredSeq<T>(seq, predicate);
    }

    @SuppressWarnings("unchecked")
    public static <T> Sequence<T> prepend(T t, Sequence<T> sequence) {
        if (sequence instanceof ReversibleSequence) {
            return prepend(t, (ReversibleSequence) sequence);
        } else {
            return concat(C.list(t), sequence);
        }
    }

    public static <T> Sequence<T> concat(Sequence<T> s1, Sequence<T> s2) {
        return s1.append(s2);
    }

    public static <T> ReversibleSequence<T> concat(ReversibleSequence<T> s1, ReversibleSequence<T> s2) {
        return s1.append(s2);
    }

    public static <T> List<T> concat(List<T> l1, List<T> l2) {
        return l1.append(l2);
    }

    public static <T> Set<T> set(T t) {
        java.util.Set<T> set = new HashSet<T>();
        set.add(t);
        return ImmutableSet.of(set);
    }

    public static <T> Set<T> set(T t1, T... ta) {
        java.util.Set<T> set = new HashSet<T>();
        set.add(t1);
        for (T t : ta) {
            set.add(t);
        }
        return ImmutableSet.of(set);
    }

    public static <T> Set<T> setOf(T... ta) {
        java.util.Set<T> set = new HashSet<T>();
        for (T t : ta) {
            set.add(t);
        }
        return ImmutableSet.of(set);
    }

    public static <T> Set<T> set(Collection<? extends T> col) {
        return ImmutableSet.of(col);
    }

    public static <T> Set<T> set(Iterable<? extends T> itr) {
        if (itr instanceof Collection) {
            return set((Collection<T>) itr);
        }
        java.util.Set<T> set = new HashSet<T>();
        for (T t : itr) set.add(t);
        return ImmutableSet.of(set);
    }

    public static <T> Set<T> newSet() {
        return new DelegatingSet<T>();
    }

    public static <K, V> java.util.Map<K, V> map(Object... args) {
        if (null == args || args.length == 0) {
            return Collections.EMPTY_MAP;
        }
        return new Map(true, args);
    }

    public static <K, V> java.util.Map<K, V> map(java.util.Map<? extends K, ? extends V> map) {
        if (null == map) {
            return Collections.EMPTY_MAP;
        }
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> java.util.Map<K, V> map(Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> newMap(Object... args) {
        return new Map(false, args);
    }

    public static <K, V> Map<K, V> newMap(java.util.Map<? extends K, ? extends V> map) {
        return new Map(false, map);
    }

    public static <T> Iterable<T> enumerable(final Enumeration<T> e) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    @Override
                    public T next() {
                        return e.nextElement();
                    }

                    @Override
                    public void remove() {
                        throw E.unsupport();
                    }
                };
            }
        };
    }
    // --- eof factory methods ---

    // --- utility methods ---

    /**
     * Check if a {@link Traversable} structure is read only. A
     * Traversable is considered to be read only structure when
     * {@code is(Feature.READONLY) || is(Feature.IMMUTABLE}
     * evaluate to {@code true}
     *
     * @param t the structure to be checked
     * @return {@code true} if the structure is read only
     * or immutable
     */
    public static boolean isReadOnly(Traversable<?> t) {
        return t.is(Feature.READONLY) || t.is(Feature.IMMUTABLE);
    }

    /**
     * Check if a {@link Traversable} structure is immutable.
     *
     * @param t the traversable strucure to be checked
     * @return {@code true} if the traversable is immutable
     */
    public static boolean isImmutable(Traversable<?> t) {
        return t.is(Feature.IMMUTABLE);
    }

    /**
     * Iterate through an iterable, apply the visitor function
     * to each element.
     * <p/>
     * <p>It is possible to stop the iteration by throwing out
     * the {@link org.osgl._.Break} from within the visitor
     * function</p>
     * <p/>
     * <p>This method support partial function by allowing the
     * visitor throw out the {@link NotAppliedException} which
     * will be simply ignored</p>
     *
     * @param iterable
     * @param visitor
     * @param <T>
     * @throws _.Break
     */
    //TODO: implement forEach iteration in parallel
    public static <T> void forEach(Iterable<? extends T> iterable, _.Function<? super T, ?> visitor) throws _.Break {
        for (T t : iterable) {
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    public static <T> void forEach(Iterator<? extends T> iterator, _.Function<? super T, ?> visitor) {
        while (iterator.hasNext()) {
            T t = iterator.next();
            visitor.apply(t);
        }
    }
    // --- eof utility methods ---

    public static enum F {
        ;

        public static <T> _.Predicate<T> containsIn(final Collection<? super T> c) {
            return new _.Predicate<T>() {
                @Override
                public boolean test(T t) throws NotAppliedException, _.Break {
                    return c.contains(t);
                }
            };
        }

        public static <T> _.Predicate<T> addTo(final Collection<? super T> c) {
            return new _.Predicate<T>() {
                @Override
                public boolean test(T t) throws NotAppliedException, _.Break {
                    return c.add(t);
                }
            };
        }

        public static <C extends List<? super T>, T> _.F1<T, C> addTo(final C c, final int index) {
            return new _.F1<T, C>() {
                @Override
                public C apply(T t) throws NotAppliedException, _.Break {
                    c.add(index, t);
                    return c;
                }
            };
        }

        public static <T> _.F1<T, Deque<? super T>> prependToDeque(final Deque<? super T> c) {
            return new _.F1<T, Deque<? super T>>() {
                @Override
                public Deque<? super T> apply(T t) throws NotAppliedException, _.Break {
                    c.addFirst(t);
                    return c;
                }
            };
        }

        public static <T> _.F1<T, Deque<? super T>> appendToDeque(final Deque<? super T> c) {
            return new _.F1<T, Deque<? super T>>() {
                @Override
                public Deque<? super T> apply(T t) throws NotAppliedException, _.Break {
                    c.addLast(t);
                    return c;
                }
            };
        }

        public static <T> _.F1<T, Sequence<? super T>> prependTo(final Sequence<? super T> c) {
            return new _.F1<T, Sequence<? super T>>() {
                @Override
                public Sequence<? super T> apply(T t) throws NotAppliedException, _.Break {
                    c.prepend(t);
                    return c;
                }
            };
        }

        public static <T> _.F1<T, Sequence<? super T>> appendTo(final Sequence<? super T> c) {
            return new _.F1<T, Sequence<? super T>>() {
                @Override
                public Sequence<? super T> apply(T t) throws NotAppliedException, _.Break {
                    c.append(t);
                    return c;
                }
            };
        }

        public static _.F1<?, Boolean> removeFrom(final Collection<?> c) {
            return new _.F1<Object, Boolean>() {
                @Override
                public Boolean apply(Object t) throws NotAppliedException, _.Break {
                    return c.remove(t);
                }
            };
        }

        public static <T> _.F1<Iterable<? extends T>, Boolean> addAllTo(final Collection<? super T> c) {
            return new _.F1<Iterable<? extends T>, Boolean>() {
                @Override
                public Boolean apply(Iterable<? extends T> c1) throws NotAppliedException, _.Break {
                    if (c1 instanceof Collection) {
                        return c.addAll((Collection<? extends T>) c1);
                    }
                    boolean modified = false;
                    for (T t : c1) {
                        c.add(t);
                        modified = true;
                    }
                    return modified;
                }
            };
        }

        public static <T> _.Predicate<Iterable<? extends T>> addAllTo(final List<? super T> l, final int index) {
            if (0 > index || l.size() < index) {
                throw new IndexOutOfBoundsException();
            }
            return new _.Predicate<Iterable<? extends T>>() {
                @Override
                public boolean test(Iterable<? extends T> itr) throws NotAppliedException, _.Break {
                    if (itr instanceof Collection) {
                        return l.addAll(index, ((Collection<? extends T>) itr));
                    }
                    boolean modified = false;
                    for (T t : itr) {
                        l.add(index, t);
                        modified = true;
                    }
                    return modified;
                }
            };
        }

        public static <T> _.Predicate<Collection<? super T>> removeAllFrom(final Iterable<? extends T> c) {
            return new _.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> c1) throws NotAppliedException, _.Break {
                    if (c instanceof Collection) {
                        return c1.removeAll((Collection<?>) c);
                    }
                    HashSet<T> s = new HashSet<T>();
                    for (T t : c) {
                        s.add(t);
                    }
                    return c1.removeAll(s);
                }
            };
        }

        public static <T> _.Predicate<? extends Collection<? super T>> retainAllIn(final Iterable<? extends T> c) {
            return new _.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> c1) throws NotAppliedException, _.Break {
                    if (c instanceof Collection) {
                        return c1.retainAll((Collection) c);
                    }
                    HashSet<T> s = new HashSet<T>();
                    for (T t : c) {
                        s.add(t);
                    }
                    return c1.retainAll(s);
                }
            };
        }

        public static <T> _.F1<Iterable<? extends T>, Void> forEach(final _.Function<? super T, ?> visitor) {
            return new _.F1<Iterable<? extends T>, Void>() {
                @Override
                public Void apply(Iterable<? extends T> iterable) throws NotAppliedException, _.Break {
                    C.forEach(iterable, visitor);
                    return null;
                }
            };
        }
    }

    public static void main(String[] args) {
        Range<Integer> r = range(0, Integer.MAX_VALUE);
        for (Number i : r.take(5).map(N.F.mul(10))) {
            System.out.println(i);
        }
    }

}
