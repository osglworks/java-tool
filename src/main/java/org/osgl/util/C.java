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

import static org.osgl.Lang.Visitor;

import org.osgl.$;
import org.osgl.Lang;
import org.osgl.Lang.Func2;
import org.osgl.Lang.IndexedVisitor;
import org.osgl.exception.NotAppliedException;
import org.osgl.exception.ReadOnlyException;
import org.osgl.util.algo.Algorithms;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * The namespace for OSGL collection utilities
 * Alias of {@link CollectionUtil}
 */
public class C {

    C() {
    }

    /**
     * The character enum for a data structure
     */
    public enum Feature {
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

        @SuppressWarnings("unused")
        class Factory {
            public static Featured identity(final EnumSet<Feature> predefined) {
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
    public interface Traversable<T> extends Iterable<T>, Featured {

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
         * <pre>
         *     Traversable traversable = C.list(23, _.NONE, null);
         *     assertEquals(C.list(true, false, false), traversal.map(_.F.NOT_NULL));
         *     assertEquals(C.list("23", "", ""), traversal.map(_.F.AS_STRING));
         * </pre>
         * <p>For Lazy Traversable, it must use lazy evaluation for this method.
         * Otherwise it is up to implementation to decide whether use lazy
         * evaluation or not</p>
         *
         * @param mapper the function that applied to element in this traversal and returns element in the result traversal
         * @param <R>    the element type of the new traversal
         * @return the new traversal contains results of the mapper function applied to this traversal
         * @since 0.2
         */
        <R> Traversable<R> map($.Function<? super T, ? extends R> mapper);

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
        <R> Traversable<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper);

        <R> Traversable<R> collect(String path);

        /**
         * Returns an new traversable that contains all elements in the current traversable
         * except that does not pass the test of the filter function specified.
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
        Traversable<T> filter($.Function<? super T, Boolean> predicate);

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
        <R> R reduce(R identity, Func2<R, T, R> accumulator);

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
         * <p>The above shows a typical left side reduction. However depending on the
         * implementation, it might choose another way to do the reduction, including
         * reduction in a parallel way</p>
         *
         * @param accumulator the function takes previous accumulating
         *                    result and the current element being
         *                    iterated
         * @return an option describing the accumulating result or {@link Lang#none()} if
         * the structure is empty
         * @since 0.2
         */
        $.Option<T> reduce(Func2<T, T, T> accumulator);

        /**
         * Check if all elements match the predicate specified
         *
         * @param predicate the function to test the element
         * @return {@code true} if all elements match the predicate
         * @since 0.2
         */
        boolean allMatch($.Function<? super T, Boolean> predicate);

        /**
         * Check if any elements matches the predicate specified
         *
         * @param predicate the function to test the element
         * @return {@code true} if any element matches the predicate
         * @since 0.2
         */
        boolean anyMatch($.Function<? super T, Boolean> predicate);

        /**
         * Check if no elements matches the predicate specified. This should be
         * equivalent to:
         * <pre>
         *      this.allMatch(_.F.negate(predicate));
         * </pre>
         *
         * @param predicate the function to test the element
         * @return {@code true} if none element matches the predicate
         * @since 0.2
         */
        boolean noneMatch($.Function<? super T, Boolean> predicate);

        /**
         * Returns an element that matches the predicate specified. The interface
         * does not indicate if it should be the first element matches the predicate
         * be returned or in case of parallel computing, whatever element matches
         * found first is returned. It's all up to the implementation to refine the
         * semantic of this method
         *
         * @param predicate the function map element to Boolean
         * @return an element in this traversal that matches the predicate or
         * {@link Lang#NONE} if no element matches
         * @since 0.2
         */
        $.Option<T> findOne($.Function<? super T, Boolean> predicate);

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
        Traversable<T> accept(Lang.Visitor<? super T> visitor);

        /**
         * Alias of {@link #accept(Lang.Visitor)}
         * @param visitor the visitor to tranverse the elements
         * @return this {@code Traversable} instance
         */
        Traversable<T> each($.Visitor<? super T> visitor);

        /**
         * Alias of {@link #accept(Lang.Visitor)}
         * @param visitor the visitor function
         * @return this {@code Traversable} instance
         */
        Traversable<T> forEach($.Visitor<? super T> visitor);
    }

    public interface Sequence<T>
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
         * @return the first element in the sequence
         * @since 0.2
         */
        T first() throws NoSuchElementException;

        /**
         * Returns an {@link Lang.Option} of the first element in the
         * {@code Sequence}
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
         * @param n the number of elements to be taken into the return sequence
         * @return the first {@code n} element in the sequence
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
         * <p>If this {@code Sequence} contains less than {@code n} elements, then a {@code Sequence} consisting
         * the whole elements of this {@code Sequence} is returned. Note it might return this {@code Sequence}
         * itself if the {@code Sequence} is immutable.</p>
         * <p>If the number {@code n} is zero, then an empty {@code Sequence} is returned in reverse
         * order</p>
         * <p>If the number {@code n} is negative, then the last {@code -n} elements from this
         * {@code Sequence} is returned in an new {@code Sequence}, or throw {@link UnsupportedOperationException}
         * if this operation is not supported</p>
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
        Sequence<T> takeWhile($.Function<? super T, Boolean> predicate);

        /**
         * Returns a {@code Sequence} consisting of the elements from this {@code Sequence} except the first {@code n}
         * if number {@code n} is positive and the {@code Sequence} contains more than {@code n} elements
         * <p>If this {@code Sequence} contains less than {@code n} elements, then an empty {@code Sequence}
         * is returned</p>
         * <p>If the number {@code n} is zero, then a copy of this {@code Sequence} or this {@code Sequence}
         * itself is returned depending on the implementation</p>
         * <p>If the number {@code n} is negative, then either {@link IllegalArgumentException} should
         * be thrown out if this sequence is not {@link org.osgl.util.C.Feature#LIMITED} or it drop
         * {@code -n} element starts from the tail side</p>
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
         * <pre>
         *      Sequence seq = C.list(1, 2, 3, 4, 3, 2, 1);
         *      assertTrue(C.list(), seq.dropWhile(_.F.gt(100)));
         *      assertTrue(C.list(4, 3, 2, 1), seq.dropWhile(_.F.lt(3)));
         * </pre>
         * <p>Note this method does NOT modify the current sequence, instead it returns an new sequence structure
         * containing the elements as required</p>
         *
         * @param predicate the function that check if drop operation should stop
         * @return the sequence after applying the drop operations
         * @since 0.2
         */
        Sequence<T> dropWhile($.Function<? super T, Boolean> predicate);

        /**
         * Returns a sequence consists of all elements of this sequence
         * followed by all elements of the specified iterable.
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param iterable the iterable in which elements will be append to this sequence
         * @return the sequence after append the iterable
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
        Sequence<T> append(Sequence<? extends T> seq);

        /**
         * Returns a sequence consists of all elements of this sequence
         * followed by all elements of the specified iterator.
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param iterator the iterator in which elements will be append to the returned sequence
         * @return a sequence consists of elements of this sequence and the elements in the iterator
         * @since 0.9
         */
        Sequence<T> append(Iterator<? extends T> iterator);

        /**
         * Returns a sequence consists of all elements of this sequence
         * followed by all elements of the specified enumeration.
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might append specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param enumeration the enumeration in which elements will be append to the returned sequence
         * @return a sequence consists of elements of this sequence and the elements in the iterator
         * @since 0.9
         */
        Sequence<T> append(Enumeration<? extends T> enumeration);

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
         * Returns a sequence consists of all elements of the iterator specified
         * followed by all elements of this sequence
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might prepend specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param iterator the iterator to be prepended
         * @return a sequence consists of elements of both sequences
         * @since 0.2
         */
        Sequence<T> prepend(Iterator<? extends T> iterator);

        /**
         * Returns a sequence consists of all elements of the enumeration specified
         * followed by all elements of this sequence
         * <p>An {@link C.Feature#IMMUTABLE immutable} Sequence must
         * return an new Sequence; while a mutable Sequence implementation
         * might prepend specified seq to {@code this} sequence instance
         * directly</p>
         *
         * @param enumeration the enumeration to be prepended
         * @return a sequence consists of elements of both sequences
         * @since 0.2
         */
        Sequence<T> prepend(Enumeration<? extends T> enumeration);

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
        Sequence<T> prepend(Sequence<? extends T> seq);

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
         * Returns a List contains all the elements in this sequence with the same order.
         * @return the list as described above
         */
        List<T> asList();

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a Sequence of {@code R} that are mapped from this sequence
         * @since 0.2
         */
        @Override
        <R> Sequence<R> map($.Function<? super T, ? extends R> mapper);

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a Sequence of {@code R} type element that are mapped from this sequences
         * @since 0.2
         */
        @Override
        <R> Sequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper);

        @Override
        <R> Sequence<R> collect(String path);

        /**
         * {@inheritDoc}
         *
         * @param predicate {@inheritDoc}
         * @return An new {@code Sequence} consists of elements that passed the predicate
         * @since 0.2
         */
        @Override
        Sequence<T> filter(final $.Function<? super T, Boolean> predicate);

        /**
         * {@inheritDoc}
         * This method does not specify how to run the accumulator. It might be
         * {@link C.Sequence#reduceLeft(Object, Func2)} or
         * {@link ReversibleSequence#reduceRight(Object, Func2)}, or
         * even run reduction in parallel, it all depending on the implementation.
         * <p>For a guaranteed reduce from left to right, use
         * {@link C.Sequence#reduceLeft(Object, Func2)}  instead</p>
         *
         * @param identity    {@inheritDoc}
         * @param accumulator {@inheritDoc}
         * @param <R>         {@inheritDoc}
         * @return {@inheritDoc}
         * @since 0.2
         */
        @Override
        <R> R reduce(R identity, Func2<R, T, R> accumulator);

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
         * @param <R> the aggregation result type
         * @return the reduced result
         * @since 0.2
         */
        <R> R reduceLeft(R identity, Func2<R, T, R> accumulator);

        /**
         * {@inheritDoc}
         * This method does not specify the approach to run reduction.
         * For a guaranteed reduction from head to tail, use
         * {@link #reduceLeft(Func2)} instead
         *
         * @param accumulator {@inheritDoc}
         * @return {@inheritDoc}
         * @since 0.2
         */
        @Override
        $.Option<T> reduce(Func2<T, T, T> accumulator);

        /**
         * Run reduction from head to tail. This is equivalent to
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
         * @param accumulator the function accumulate each element to the final result
         * @return an {@link Lang.Option} describing the accumulating result
         * @since 0.2
         */
        $.Option<T> reduceLeft(Func2<T, T, T> accumulator);

        /**
         * Apply the predicate specified to the element of this sequence
         * from head to tail. Stop at the element that returns {@code true},
         * and returns an {@link Lang.Option} describing the element. If none
         * of the element applications in the sequence returns {@code true}
         * then {@link Lang#none()} is returned
         *
         * @param predicate the function map the element to Boolean
         * @return an option describe the first element matches the
         * predicate or {@link Lang#none()}
         * @since 0.2
         */
        $.Option<T> findFirst($.Function<? super T, Boolean> predicate);

        Sequence<T> accept($.Visitor<? super T> visitor);

        Sequence<T> each($.Visitor<? super T> visitor);

        Sequence<T> forEach($.Visitor<? super T> visitor);

        /**
         * Iterate through this sequence from head to tail with
         * the visitor function specified
         *
         * @param visitor the function to visit elements in this sequence
         * @return this sequence
         * @see Traversable#accept(Lang.Visitor)
         * @see ReversibleSequence#acceptRight(Lang.Visitor)
         * @since 0.2
         */
        Sequence<T> acceptLeft($.Visitor<? super T> visitor);

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
        <T2> Sequence<? extends $.Binary<T, T2>> zip(Iterable<T2> iterable);

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
        <T2> Sequence<? extends $.Binary<T, T2>> zipAll(Iterable<T2> iterable, T def1, T2 def2);

        /**
         * Zip this sequence with its indices
         *
         * @return A new list containing pairs consisting of all
         * elements of this list paired with their index.
         * Indices start at 0.
         */
        @SuppressWarnings("unused")
        Sequence<? extends $.Binary<T, Integer>> zipWithIndex();

        /**
         * Count the element occurence in this sequence
         * @param t the element
         * @return the number of times the element be presented in this sequence
         */
        int count(T t);
    }

    /**
     * A bidirectional sequence which can be iterated from tail to head
     *
     * @param <T> the element type
     */
    public interface ReversibleSequence<T>
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
        ReversibleSequence<T> takeWhile($.Function<? super T, Boolean> predicate);

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
        ReversibleSequence<T> dropWhile($.Function<? super T, Boolean> predicate);

        @Override
        ReversibleSequence<T> filter($.Function<? super T, Boolean> predicate);

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
         * <p>If this {@code Sequence} contains less than {@code n} elements, then a {@code Sequence} consisting
         * the whole elements of this {@code Sequence} is returned. Note it might return this {@code Sequence}
         * itself if the {@code Sequence} is immutable.</p>
         * <p>If the number {@code n} is zero, then an empty {@code Sequence} is returned in reverse
         * order</p>
         * <p>If the number {@code n} is negative, then the first {@code -n} elements from this
         * {@code Sequence} is returned in an new {@code Sequence}</p>
         * <pre>
         *     Sequence seq = C1.list(1, 2, 3, 4);
         *     assertEquals(C1.list(3, 4), seq.tail(2));
         *     assertEquals(C1.list(1, 2, 3, 4), seq.tail(100));
         *     assertEquals(C1.list(), seq.tail(0));
         *     assertEquals(C1.list(1, 2, 3), seq.tail(-3));
         *     assertEquals(C1.list(1, 2, 3, 4), seq.tail(-200));
         * </pre>
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
         * @param identity the initial value
         * @param accumulator the function performs accumulation from {@code T} an {@code R} to anthoer {@code R}
         * @param <R> the accumulation result
         * @return the aggregation result
         * @see #reduce(Object, Func2)
         * @since 0.2
         */
        <R> R reduceRight(R identity, Func2<R, T, R> accumulator);

        /**
         * Run reduction from tail to head. This is equivalent to
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
         * @param accumulator the function accumulate each element to the final result
         * @return an {@link Lang.Option} describing the accumulating result
         * @since 0.2
         */
        $.Option<T> reduceRight(Func2<T, T, T> accumulator);


        /**
         * Apply the predicate specified to the element of this sequence
         * from tail to head. Stop at the element that returns {@code true},
         * and returns an {@link Lang.Option} describing the element. If none
         * of the element applications in the sequence returns {@code true}
         * then {@link Lang#none()} is returned
         *
         * @param predicate the function map the element to Boolean
         * @return an option describe the first element matches the
         * predicate or {@link Lang#none()}
         * @since 0.2
         */
        $.Option<T> findLast($.Function<? super T, Boolean> predicate);

        @Override
        <R> ReversibleSequence<R> map($.Function<? super T, ? extends R> mapper);

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a ReversibleSequence of {@code R} type element that are mapped from this sequences
         * @since 0.2
         */
        @Override
        <R> ReversibleSequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper);

        ReversibleSequence<T> accept($.Visitor<? super T> visitor);


        ReversibleSequence<T> each($.Visitor<? super T> visitor);

        ReversibleSequence<T> forEach($.Visitor<? super T> visitor);

        ReversibleSequence<T> acceptLeft($.Visitor<? super T> visitor);

        /**
         * Iterate through this sequence from tail to head with the visitor function
         * specified
         *
         * @param visitor the function to visit elements in this sequence
         * @return this sequence
         * @see Traversable#accept(Lang.Visitor)
         * @see Sequence#acceptLeft(Lang.Visitor)
         * @since 0.2
         */
        ReversibleSequence<T> acceptRight($.Visitor<? super T> visitor);

        <T2> C.ReversibleSequence<$.Binary<T, T2>> zip(C.ReversibleSequence<T2> rseq);

        @SuppressWarnings("unused")
        <T2> C.ReversibleSequence<$.Binary<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, T def1, T2 def2);

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

        public boolean isEmpty() {
            return 0 == size();
        }

        public boolean isNotEmpty() {
            return 0 < size();
        }

        public T get(int idx) {
            return data[idx];
        }

        public Array<T> set(int idx, T val) {
            data[idx] = val;
            return this;
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
        @SuppressWarnings("unchecked")
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

        @Override
        public C.List<T> asList() {
            return C.listOf(data);
        }

        public C.List<T> asNewList() {
            return C.newListOf(data);
        }

        public static <T> Array<T> of(T[] data) {
            return new Array<>(data);
        }

        public static <T> Array<T> copyOf(T[] data) {
            int len = data.length;
            T[] newData = $.newArray(data, len);
            System.arraycopy(data, 0, newData, 0, len);
            return new Array<>(newData);
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
    public interface Range<ELEMENT> extends Sequence<ELEMENT> {
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
         * Returns a {@link Func2} function that takes two elements in the range domain and returns an integer to
         * determine the order of the two elements. See {@link java.util.Comparator#compare(Object, Object)} for
         * semantic of the function.
         * <p>If any one of the element applied is {@code null} the function should throw out
         * {@link NullPointerException}</p>
         *
         * @return a function implement the ordering logic
         * @since 0.2
         */
        Comparator<ELEMENT> order();

        /**
         * Returns a {@link Func2} function that applied to an element in this {@code Range} and
         * an integer {@code n} indicate the number of steps. The result of the function is an element in
         * the range or the range domain after moving {@code n} steps based on the element.
         * <p>If the element apply is {@code null}, the function should throw out
         * {@link NullPointerException}; if the resulting element is not defined in the range
         * domain, the function should throw out {@link NoSuchElementException}</p>
         *
         * @return a function implement the stepping logic
         * @since 0.2
         */
        Func2<ELEMENT, Integer, ELEMENT> step();

        /**
         * Returns an new range this range and another range {@code r2} merged together. The two ranges must have
         * the equal {@link #step()} and {@link #order()} operator to be merged, otherwise,
         * {@link org.osgl.exception.InvalidArgException} will be thrown out
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

        @SuppressWarnings("unused")
        <R> R reduceRight(R identity, Func2<R, ELEMENT, R> accumulator);

        @SuppressWarnings("unused")
        $.Option<ELEMENT> reduceRight(Func2<ELEMENT, ELEMENT, ELEMENT> accumulator);

        @SuppressWarnings("unused")
        $.Option<ELEMENT> findLast($.Function<? super ELEMENT, Boolean> predicate);

        /**
         * {@inheritDoc}
         *
         * @param visitor {@inheritDoc}
         * @return this Range instance
         * @since 0.2
         */
        @Override
        Range<ELEMENT> accept($.Visitor<? super ELEMENT> visitor);

        @Override
        Range<ELEMENT> each($.Visitor<? super ELEMENT> visitor);

        @Override
        Range<ELEMENT> forEach($.Visitor<? super ELEMENT> visitor);

        /**
         * {@inheritDoc}
         *
         * @param visitor {@inheritDoc}
         * @return this Range instance
         * @since 0.2
         */
        @Override
        Range<ELEMENT> acceptLeft($.Visitor<? super ELEMENT> visitor);

        /**
         * iterate through the range from tail to head
         *
         * @param visitor a function to visit elements in the range
         * @return this Range instance
         * @since 0.2
         */
        @SuppressWarnings("unused")
        Range<ELEMENT> acceptRight($.Visitor<? super ELEMENT> visitor);
    }

    /**
     * The osgl List interface is a mixture of {@link java.util.List} and osgl {@link Sequence}
     *
     * @param <T> the element type of the {@code List}
     * @since 0.2
     */
    public interface List<T> extends java.util.List<T>, ReversibleSequence<T> {

        /**
         * A cursor points to an element of a {@link List}. It performs like
         * {@link java.util.ListIterator} but differs in the following way:
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
        interface Cursor<T> {

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
             * <p>After calling this method, {@link #isDefined()}
             * shall return {@code false}</p>
             *
             * @return this cursor
             */
            Cursor<T> parkLeft();

            /**
             * Park the cursor at the position after the last element
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
        @SuppressWarnings("unused")
        List<T> readOnly();

        /**
         * Returns a mutable copy of this list
         *
         * @return a mutable list contains all elements of this list
         */
        List<T> copy();

        /**
         * Returns a sorted copy of this list.
         * <p>Note if the element type T is not a {@link java.lang.Comparable} then
         * this method returns a {@link #copy() copy} of this list without any order
         * changes</p>
         *
         * @return a sorted copy of this list
         */
        List<T> sorted();

        /**
         * Return a list that contains unique set of this list and keep the orders. If
         * this list doesn't have duplicated items, it could return this list directly
         * or choose to return an new copy of this list depends on the sub class
         * implementation
         * @return a list contains only unique elements in this list
         */
        List<T> unique();

        /**
         * Return a list that contains unique set as per the comparator specified of
         * this list and keep the orders. If this list doesn't have duplicated items,
         * it could return this list directly or choose to return an new copy of this list
         * depends on the sub class implementation
         * @param comp the comparator check the duplicate elements
         * @return a list contains unique element as per the comparator
         */
        List<T> unique(Comparator<T> comp);

        /**
         * Returns a sorted copy of this list. The order is specified by the comparator
         * provided
         *
         * @param comparator specify the order of elements in the result list
         * @return an ordered copy of this list
         */
        List<T> sorted(Comparator<? super T> comparator);

        @Override
        List<T> subList(int fromIndex, int toIndex);

        /**
         * Add all elements from an {@link Iterable} into this list.
         * Return {@code true} if the list has changed as a result of
         * call.
         * <p><b>Note</b> if this list is immutable or readonly, {@code UnsupportedOperationException}
         * will be thrown out with this call</p>
         * @param iterable the iterable provides the elements to be
         *                 added into the list
         * @return {@code true} if this list changed as result of addd
         */
        boolean addAll(Iterable<? extends T> iterable);

        /**
         * {@inheritDoc}
         *
         * @param n specify the number of elements to be included in the return list
         * @return A List contains first {@code n} items in this List
         */
        @Override
        List<T> head(int n);

        /**
         * {@inheritDoc}
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
         * <p>This method does not alter the underline list</p>
         *
         * @param n {@inheritDoc}
         * @return A list contains last {@code n} items in this list
         */
        @Override
        List<T> tail(int n);

        /**
         * {@inheritDoc}
         * <p>This method does not alter the underline list</p>
         *
         * @param n {@inheritDoc}
         * @return a List contains all elements of this list
         * except the first {@code n} number
         */
        List<T> drop(int n);

        /**
         * {@inheritDoc}
         * <p>This method does not alter the underline list</p>
         *
         * @param predicate the predicate function
         * @return {@inheritDoc}
         */
        @Override
        List<T> dropWhile($.Function<? super T, Boolean> predicate);

        /**
         * {@inheritDoc}
         * <p>This method does not alter the underline list</p>
         *
         * @param predicate {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        List<T> takeWhile($.Function<? super T, Boolean> predicate);

        /**
         * For mutable list, remove all element that matches the predicate
         * specified from this List and return this list once done.
         * <p>For immutable or readonly list, an new List contains all element from
         * this list that does not match the predicate specified is returned</p>
         *
         * @param predicate test whether an element should be removed frmo
         *                  return list
         * @return a list contains all element that does not match the
         * predicate specified
         */
        List<T> remove($.Function<? super T, Boolean> predicate);

        @Override
        <R> C.List<R> map($.Function<? super T, ? extends R> mapper);

        /**
         * {@inheritDoc}
         *
         * @param mapper {@inheritDoc}
         * @param <R>    {@inheritDoc}
         * @return a List of {@code R} type element that are mapped from this sequences
         * @since 0.2
         */
        @Override
        <R> List<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper);

        @Override
        <R> List<R> collect(String path);

        @Override
        List<T> filter($.Function<? super T, Boolean> predicate);

        /**
         * Split this list into two list based on the predicate specified.
         * <p>
         *     The function use the predicate to test all elements in this list. If test passed
         *     then it add the element into {@link Lang.T2#_1 left side list}, otherwise the
         *     element will be added into {@link Lang.T2#_2 right side list}. The result
         *     is returned as a {@link org.osgl.Lang.Tuple tuple} contains the left and
         *     right side lift
         * </p>
         * @param predicate the function to test the elements in this list
         * @return a tuple of two lists
         */
        $.T2<List<T>, List<T>> split($.Function<? super T, Boolean> predicate);

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
        Cursor<T> locateFirst($.Function<T, Boolean> predicate);

        /**
         * Locate any one element in the list that matches the predicate.
         * Returns the cursor point to the element found, or a cursor
         * that is not defined if no such element found in the list. In
         * a parallel locating the element been found might not be the
         * first element matches the predicate
         *
         * @param predicate the function that used to check the element
         *                  at the cursor
         * @return the reference to the list itself or an new List without
         * and element matches the predicate if this is a readonly
         * list
         */
        Cursor<T> locate($.Function<T, Boolean> predicate);

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
        Cursor<T> locateLast($.Function<T, Boolean> predicate);

        /**
         * Insert an element at the position specified by {@code index}.
         * <p>If this list is readonly or immutable, then an new
         * list should be created with all elements in this list
         * and the new element inserted at the specified position.
         * The new list should have the same feature as this list</p>
         * <p>If index is less than zero then it will insert at
         * {@code (size() + index)}</p>
         *
         * @param index specify the position where the element should be inserted
         * @param t     the element to be inserted
         * @return a list as specified above
         * @throws IndexOutOfBoundsException Math.abs(index) &gt; size()
         */
        List<T> insert(int index, T t) throws IndexOutOfBoundsException;

        /**
         * Insert an array of elements at the position specified by {@code index}.
         * <p>If this list is readonly or immutable, then an new
         * list should be created with all elements in this list
         * and the new element inserted at the specified position.
         * The new list should have the same feature as this list</p>
         * <p>If index is less than zero then it will insert at
         * {@code (size() + index)}</p>
         *
         * @param index specify the position where the element should be inserted
         * @param ta    the array of elements to be inserted
         * @return a list as specified above
         * @throws IndexOutOfBoundsException Math.abs(index) &gt; size()
         */
        List<T> insert(int index, T... ta) throws IndexOutOfBoundsException;

        /**
         * Insert a sub list at the position specified by {@code index}.
         * <p>If this list is readonly or immutable, then an new
         * list should be created with all elements in this list
         * and the elements of sub list inserted at the specified position.
         * The new list should have the same feature as this list</p>
         * <p>If index is less than zero then it will insert at
         * {@code (size() + index)}</p>
         *
         * @param index specify the position where the element should be inserted
         * @param subList the sub list contains elements to be inserted
         * @return a list as specified above
         * @throws IndexOutOfBoundsException Math.abs(index) &gt; size()
         */
        List<T> insert(int index, java.util.List<T> subList) throws IndexOutOfBoundsException;

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
         * @param iterable the iterable from which elements will be appended to this list
         * @return a List contains all elements of this list followed
         * by all elements in the iterable
         */
        List<T> append(Collection<? extends T> iterable);

        /**
         * Returns a List contains all elements in this List followed by
         * all elements in the specified List.
         * <p>A mutable List implementation might choose to add elements
         * from the specified list directly to this list and return this
         * list directly</p>
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
        List<T> accept($.Visitor<? super T> visitor);

        @Override
        List<T> each($.Visitor<? super T> visitor);

        @Override
        List<T> forEach($.Visitor<? super T> visitor);

        @Override
        List<T> acceptLeft($.Visitor<? super T> visitor);

        @Override
        List<T> acceptRight($.Visitor<? super T> visitor);

        /**
         * Loop through the list and for each element, call on the
         * indexedVisitor function specified
         * @param indexedVisitor the function to be called on each element along with the index
         * @return this list
         */
        List<T> accept(IndexedVisitor<Integer, ? super T> indexedVisitor);

        /**
         * Alias of {@link #accept(Visitor)}
         * @param indexedVisitor the function to be called on each element along with the index
         * @return this list
         */
        List<T> each(IndexedVisitor<Integer, ? super T> indexedVisitor);

        /**
         * Alias of {@link #accept(Visitor)}
         * @param indexedVisitor the function to be called on each element along with the index
         * @return this list
         */
        List<T> forEach(IndexedVisitor<Integer, ? super T> indexedVisitor);

        /**
         * Loop through the list from {@code 0} to {@code size - 1}. Call the indexedVisitor function
         * on each element along with the index
         * @param indexedVisitor the function to be called on each element along with the index
         * @return this list
         */
        @SuppressWarnings("unused")
        List<T> acceptLeft(IndexedVisitor<Integer, ? super T> indexedVisitor);

        /**
         * Loop through the list from {@code size() - 1} to {@code 0}. Call the indexedVisitor function
         * on each element along with the index
         * @param indexedVisitor the function to be called on each element along with the index
         * @return this list
         */
        @SuppressWarnings("unused")
        List<T> acceptRight(IndexedVisitor<Integer, ? super T> indexedVisitor);

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
        <T2> List<$.Binary<T, T2>> zip(java.util.List<T2> list);

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
        <T2> List<$.Binary<T, T2>> zipAll(java.util.List<T2> list, T def1, T2 def2);

        /**
         * Zip this sequence with its indices
         *
         * @return A new list containing pairs consisting of all
         * elements of this list paired with their index.
         * Indices start at 0.
         */
        Sequence<$.Binary<T, Integer>> zipWithIndex();

        /**
         * Create a {@link Map} from this list using a key extract function and a value extract function
         *
         * The key extractor will take the element stored in this list and calculate a key,
         * and then store the element being used along with the key calculated into the map
         * to be returned.
         *
         * The value extractor will take the element stored in this list and calculate a value,
         * and then store the element as the key along with the outcome as the value
         *
         * @param keyExtractor the function that generate map key from the element in this list
         * @param valExtractor the function that generate map value from the element in this list
         * @param <K> the generic type of key in the map
         * @param <V> the generic type of value in the map
         * @return a map as described above
         */
        <K, V> Map<K, V> toMap($.Function<? super T, ? extends K> keyExtractor, $.Function<? super T, ? extends V> valExtractor);


        /**
         * Create a {@link Map} from this list using a key extract function.
         *
         * The key extractor will take the element stored in this list and calculate a key,
         * and then store the element being used along with the key calculated into the map
         * to be returned.
         *
         * @param keyExtractor the function that generate map key from the element in this list
         * @param <K> the generic type of key in the map
         * @return a map that indexed by key generated by the function from the element in this list
         */
        <K> Map<K, T> toMapByVal($.Function<? super T, ? extends K> keyExtractor);

        /**
         * Create a {@link Map} from this list using a value extract function.
         *
         * The value extractor will take the element stored in this list and calculate a value,
         * and then store the element as the key along with the outcome as the value
         *
         * @param valExtractor the function that generate map value from the element in this list
         * @param <V> the generic type of value in the map
         * @return a map that stores the value calculated along with the corresponding element as the key
         */
        <V> Map<T, V> toMapByKey($.Function<? super T, ? extends V> valExtractor);
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
        public static class Entry<K, V> extends $.T2<K, V> implements java.util.Map.Entry<K, V> {
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

        public class _Builder {
            private K key;
            private _Builder(K key) {
                this.key = $.requireNotNull(key);
            }
            public Map<K, V> to(V val) {
                Map<K, V> me = Map.this;
                if (me.ro) {
                    Map<K, V> mapBuffer = C.newMap(me);
                    mapBuffer.put(key, val);
                    return C.Map(mapBuffer);
                }
                Map.this.put(key, val);
                return Map.this;
            }
        }

        private java.util.Map<K, V> _m;

        private boolean ro;

        @SuppressWarnings("unchecked")
        protected Map(boolean readOnly, Object... args) {
            HashMap<K, V> map = new HashMap<>();
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
            ensureWritable();
            return _m.put(key, value);
        }

        @Override
        public V remove(Object key) {
            ensureWritable();
            return _m.remove(key);
        }

        @Override
        public void putAll(java.util.Map<? extends K, ? extends V> m) {
            ensureWritable();
            _m.putAll(m);
        }

        @Override
        public void clear() {
            ensureWritable();
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
        public _Builder map(K key) {
            return new _Builder(key);
        }

        @SuppressWarnings("unused")
        public boolean isReadOnly() {
            return ro;
        }

        @SuppressWarnings("unused")
        public Map<K, V> readOnly(boolean readOnly) {
            if (ro ^ readOnly) {
                return new Map<>(readOnly, _m);
            } else {
                return this;
            }
        }

        public Map<V, K> flipped() {
            Map<V, K> flip = C.newMap();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                flip.put(entry.getValue(), entry.getKey());
            }
            return flip;
        }

        /**
         * Loop through this map on each key/value pair, apply them to the function specified
         * @param indexedVisitor the function that takes argument of (key, value) pair
         * @return this map
         */
        public Map<K, V> forEach(IndexedVisitor<? super K, ? super V> indexedVisitor) {
            for (java.util.Map.Entry<K, V> entry: entrySet()) {
                try {
                    indexedVisitor.apply(entry.getKey(), entry.getValue());
                } catch (NotAppliedException e) {
                    // ignore
                }
            }
            return this;
        }

        /**
         * Alias of {@link #forEach(IndexedVisitor)}
         * @param indexedVisitor the visitor that can be applied on Key/Value pair stored in this Map
         * @return this map
         */
        public Map<K, V> each(IndexedVisitor<? super K, ? super V> indexedVisitor) {
            return forEach(indexedVisitor);
        }


        /**
         * Alias of {@link #forEach(IndexedVisitor)}
         * @param indexedVisitor the visitor that can be applied on Key/Value pair stored in this Map
         * @return this map
         */
        public Map<K, V> accept(IndexedVisitor<? super K, ? super V> indexedVisitor) {
            return forEach(indexedVisitor);
        }

        public Map<K, V> filter($.Function<K, Boolean> predicate) {
            java.util.Map<K, V> map = new HashMap<>();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                K k = entry.getKey();
                if (predicate.apply(k)) {
                    map.put(k, entry.getValue());
                }
            }
            Map<K, V> filtered = new Map<>(isReadOnly(), map);
            return filtered;
        }

        public Map<K, V> valueFilter($.Function<V, Boolean> predicate) {
            java.util.Map<K, V> map = new HashMap<>();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                V v = entry.getValue();
                if (predicate.apply(v)) {
                    map.put(entry.getKey(), v);
                }
            }
            Map<K, V> filtered = new Map<>(isReadOnly(), map);
            return filtered;
        }

        public <NV> Map<K, NV> transformValues($.Function<V, NV> valueTransformer) {
            Map<K, NV> newMap = C.newMap();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                newMap.put(entry.getKey(), valueTransformer.apply(entry.getValue()));
            }
            return newMap;
        }

        public <NK> Map<NK, V> transformKeys($.Function<K, NK> keyTransformer) {
            Map<NK, V> newMap = C.newMap();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                newMap.put(keyTransformer.apply(entry.getKey()), entry.getValue());
            }
            return newMap;
        }

        public <NK, NV> Map<NK, NV> transform($.Function<K, NK> keyTransformer, $.Function<V, NV> valueTransformer) {
            Map<NK, NV> newMap = C.newMap();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                newMap.put(keyTransformer.apply(entry.getKey()), valueTransformer.apply(entry.getValue()));
            }
            return newMap;
        }

        public Set<$.Binary<K, V>> zip() {
            C.Set<$.Binary<K, V>> zipped = C.newSet();
            for (java.util.Map.Entry<K, V> entry : entrySet()) {
                zipped.add($.T2(entry.getKey(), entry.getValue()));
            }
            return zipped;
        }

        private void writeObject(java.io.ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            s.writeObject(_m);
            if (ro) s.writeInt(1);
            else s.writeInt(0);
        }

        private static final long serialVersionUID = 262498820763181265L;

        @SuppressWarnings("unchecked")
        private void readObject(java.io.ObjectInputStream s)
        throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            _m = (java.util.Map)s.readObject();
            int i = s.readInt();
            ro = i != 0;
        }

        private void ensureWritable() {
            C.ensureWritable(ro, "map");
        }
    }

    public interface Set<T> extends java.util.Set<T>, Traversable<T> {
        @Override
        Set<T> parallel();

        @Override
        Set<T> sequential();

        @Override
        Set<T> lazy();

        @Override
        Set<T> eager();

        @Override
        Set<T> filter($.Function<? super T, Boolean> predicate);

        @Override
        Set<T> accept($.Visitor<? super T> visitor);

        @Override
        Set<T> each($.Visitor<? super T> visitor);

        @Override
        Set<T> forEach($.Visitor<? super T> visitor);

        /**
         * Returns a set contains all elements in the {@code col}
         * collection specified but not in this set
         *
         * @param col the collection in which elements should
         *            be included from the result set
         * @return a set contains elements only in the col
         */
        @SuppressWarnings("unused")
        Set<T> onlyIn(Collection<? extends T> col);

        /**
         * Returns a set contains only elements in both {@code col}
         * collection specified and this set
         *
         * @param col the collection in which elements should
         *            be included from the result set
         * @return a set contains elements in both col and this set
         */
        Set<T> withIn(Collection<? extends T> col);


        /**
         * Returns a set contains all elements in this set and not in
         * the {@code col} collection specified
         *
         * @param col the collection in which elements should
         *            be excluded from the result set
         * @return a set contains elements only in this set
         */
        Set<T> without(Collection<? super T> col);

        /**
         * Returns a set contains all elements in this set and all
         * elements in the {@code col} collection specified
         *
         * @param col the collection in which elements should be
         *            included in the result set
         * @return a set contains elements in both this set and the collection
         */
        Set<T> with(Collection<? extends T> col);

        /**
         * Returns a set contains all elements in this set plus the element
         * specified
         *
         * @param element the new element that will be contained in the returning set
         * @return a set as described above
         */
        Set<T> with(T element);

        /**
         * Returns a set contains all elements in this set plus all the elements
         * specified in the parameter list
         *
         * @param element the first element to be added into the returning set
         * @param elements rest elements to be added into the returning set
         * @return a set as described above
         */
        Set<T> with(T element, T... elements);

        /**
         * Returns a set contains all elements in the set except the
         * one specified
         *
         * @param element the element that should not be in the resulting set
         * @return a set without the element specified
         */
        Set<T> without(T element);

        /**
         * Returns a set contains all elements in the set except the
         * ones specified
         *
         * @param element  the element that should not be in the resulting set
         * @param elements the array contains elements that should not be in the resulting set
         * @return a set without the element specified
         */
        Set<T> without(T element, T... elements);

    }

    public interface ListOrSet<T> extends List<T>, Set<T> {
        @Override
        ListOrSet<T> parallel();

        @Override
        ListOrSet<T> sequential();

        @Override
        ListOrSet<T> lazy();

        @Override
        ListOrSet<T> eager();

        @Override
        ListOrSet<T> accept($.Visitor<? super T> visitor);

        @Override
        ListOrSet<T> each($.Visitor<? super T> visitor);

        @Override
        ListOrSet<T> forEach($.Visitor<? super T> visitor);

        @Override
        ListOrSet<T> filter($.Function<? super T, Boolean> predicate);

        @Override
        ListOrSet<T> without(Collection<? super T> col);

        @Override
        ListOrSet<T> without(T element);

        @Override
        ListOrSet<T> without(T element, T... elements);

        @Override
        <R> ListOrSet<R> map($.Function<? super T, ? extends R> mapper);

        @Override
        default Spliterator<T> spliterator() {
            return Spliterators.spliterator(this, Spliterator.DISTINCT);
        }
    }

    /**
     * Defines a factory to create {@link java.util.List java List} instance
     * used by {@link DelegatingList} to create it's backing data structure
     *
     * @since 0.2
     */
    public interface ListFactory {
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
         * @exception  NullPointerException if the specified collection is null
         */
        <ET> java.util.List<ET> create(Collection<? extends ET> collection) throws NullPointerException;

        /**
         * Create a <code>java.util.List</code> with initial capacity
         *
         * @param initialCapacity the initial capacity of the new List
         * @param <ET>            the generic type of the list element
         * @return the list been created
         */
        <ET> java.util.List<ET> create(int initialCapacity);

        enum Predefined {
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
            $.Option<ListFactory> fact = $.safeNewInstance(factCls);
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
            $.Option<ListFactory> fact = $.safeNewInstance(factCls);
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

    @SuppressWarnings("unused")
    public static boolean notEmpty(Collection<?> col) {
        return !empty(col);
    }

    public static boolean isEmpty(Collection<?> col) {
        return empty(col);
    }

    public static boolean empty(java.util.Map map) {
        return null == map || map.isEmpty();
    }

    public static boolean notEmpty(java.util.Map map) {
        return !empty(map);
    }

    public static boolean isEmpty(java.util.Map map) {
        return empty(map);
    }

    // --- conversion methods ---
    public static <T> Collection<T> asCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            return $.cast(iterable);
        }
        return C.list(iterable);
    }
    // --- eof conversion methods ---

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
    @SuppressWarnings("unused")
    public static Range<Integer> naturalNumbers() {
        return new LazyRange<Integer>(1, Integer.MAX_VALUE, N.F.INT_RANGE_STEP);
    }

    /**
     * Returns a {@link Range} of non-negative even numbers starts from {@code 0} to
     * {@code Integer.MAX_VALUE}.
     *
     * @return a range of non-negative even numbers
     */
    @SuppressWarnings("unused")
    public static Range<Integer> evenNumbers() {
        return new LazyRange<Integer>(0, Integer.MAX_VALUE, N.F.intRangeStep(2));
    }

    /**
     * Returns a {@link Range} of positive odd numbers starts from {@code 1} to
     * {@code Integer.MAX_VALUE}.
     *
     * @return a range of positive odd numbers
     */
    @SuppressWarnings("unused")
    public static Range<Integer> oddNumbers() {
        return new LazyRange<Integer>(1, Integer.MAX_VALUE, N.F.intRangeStep(2));
    }

    @SuppressWarnings("unused")
    public static final List EMPTY_LIST = Nil.list();
    @SuppressWarnings("unused")
    public static final Set EMPTY_SET = Nil.set();
    @SuppressWarnings("unused")
    public static final Map EMPTY_MAP = Nil.EMPTY_MAP;
    public static final ListOrSet EMPTY = Nil.EMPTY;

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unused")
    public static <T> List<T> emptyListOf(Class<T> c) {
        return Nil.list();
    }

    public static <T> List<T> list(T t) {
        return $.val(t);
    }

    /**
     * Creates an immutable list of an array of elements.
     * <p>Note the array will not be copied, instead it will
     * be used directly as the backing data for the list.
     * To create an list with a copy of the array specified.
     * Use the {@link #newListOf(Object[])} method</p>
     *
     * @param ta  an array of elements
     * @param <T> the element type
     * @return an immutable list backed by the specified array
     */
    public static <T> List<T> listOf(T... ta) {
        return ImmutableList.of(ta);
    }

    /**
     * Creates an immutable list from an element plus an array of elements
     *
     * @param t   the first element
     * @param ta  the array
     * @param <T> the element type
     * @return an immutable list contains the first element and followed by all element in the array
     */
    public static <T> List<T> list(T t, T... ta) {
        int len = ta.length;
        T[] a = $.newArray(ta, len + 1);
        a[0] = t;
        System.arraycopy(ta, 0, a, 1, len);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Boolean list from a boolean (primitive type) array.
     * <p>At the moment the implementation will convert the boolean (primary)
     * array to Boolean (wraper) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary boolean
     * @return a Boolean typed list
     */
    public static List<Boolean> listOf(boolean[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable Boolean list of a byte (primitive type) array.
     * The elements of the array is copied into the returned list
     *
     * @param elements an array of bytes
     * @return an immutable list contains specified elements
     */
    public static List<Boolean> list(boolean[] elements) {
        if (null == elements || 0 == elements.length) {
            return Nil.list();
        }
        Boolean[] ba = $.asObject(elements);
        return ImmutableList.of(ba);
    }

    /**
     * Create an immutable Byte list from a byte (primitive type) array.
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
     * Create an immutable Byte list of a byte (primitive type) array.
     * The elements of the array is copied into the returned list
     *
     * @param elements an array of bytes
     * @return an immutable list contains specified elements
     */
    public static List<Byte> list(byte[] elements) {
        if (null == elements || 0 == elements.length) {
            return Nil.list();
        }
        Byte[] ba = $.asObject(elements);
        return ImmutableList.of(ba);
    }

    /**
     * Create an immutable Short list from a char (primitive type) array.
     * <p>At the moment the implementation will convert the char (primary)
     * array to Character (reference) array, thus a copy of the array
     * will actually take place. However it should assume the
     * array will directly be used as backing data in user application
     * to cater to the future optimized implementation</p>
     *
     * @param elements an array of primary short
     * @return a Short typed list
     */
    public static List<Character> listOf(char[] elements) {
        return list(elements);
    }

    /**
     * Create an immutable Character list of a char array.
     * The elements of the array is copied into the returned list
     *
     * @param elements an array of shorts
     * @return an immutable list contains specified elements
     */
    public static List<Character> list(char[] elements) {
        if (null == elements || 0 == elements.length) {
            return Nil.list();
        }
        Character[] a = $.asObject(elements);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Short list from a short (primitive type) array.
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
        if (null == elements || 0 == elements.length) {
            return Nil.list();
        }
        Short[] a = $.asObject(elements);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Integer list from an int (primitive type) array.
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
        if (null == elements || 0 == elements.length) {
            return Nil.list();
        }
        Integer[] a = $.asObject(elements);
        return ImmutableList.of(a);
    }

    /**
     * Create an immutable Long list from a long (primitive type) array.
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
        if (null == elements || 0 == elements.length) {
            return list();
        }
        return ImmutableList.of($.asObject(elements));
    }

    /**
     * Create an immutable Float list from a float (primitive type) array.
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
        if (null == elements || 0 == elements.length) {
            return list();
        }
        return ImmutableList.of($.asObject(elements));
    }

    /**
     * Create an immutable Double list from an double (primitive type) array.
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
        if (null == elements || 0 == elements.length) {
            return list();
        }
        return ImmutableList.of($.asObject(elements));
    }

    public static <T> List<T> list(Iterable<? extends T> iterable) {
        return null == iterable ? C.<T>list() : ListBuilder.toList(iterable);
    }

    public static <T> List<T> list(Iterator<? extends T> iterator) {
        return null == iterator ? C.<T>list() : ListBuilder.toList(iterator);
    }

    public static <T> List<T> list(Enumeration<? extends T> enumeration) {
        return null == enumeration ? C.<T>list() : ListBuilder.toList(enumeration);
    }

    public static <T> List<T> list(Collection<? extends T> col) {
        return null == col ? C.<T>list() : ListBuilder.toList(col);
    }

    public static <T> List<T> list(java.util.List<? extends T> javaList) {
        if (null == javaList) {
            return C.list();
        }
        if (javaList instanceof List) {
            List<T> list = $.cast(javaList);

            if (list.is(Feature.IMMUTABLE)) {
                return list;
            } else {
                return new ReadOnlyDelegatingList<T>(list);
            }
        }
        return new ReadOnlyDelegatingList<T>(javaList);
    }

    public static <T> List<T> singletonList(T t) {
        return list(t);
    }

    public static <T> List<T> wrap(java.util.List<T> list) {
        return null == list ? C.<T>list() : DelegatingList.wrap(list);
    }

    public static <T> List<T> newSizedList(int size) {
        return new DelegatingList<T>(size);
    }

    public static <T> List<T> newList() {
        return newSizedList(10);
    }

    public static <T> List<T> newList(Iterable<? extends T> iterable) {
        return null == iterable ? C.<T>newList() : new DelegatingList<T>(iterable);
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
        return null == ts ? C.<T>newList() : new DelegatingList<>(C.listOf(ts));
    }

    /**
     * Return a {@link Sequence} consists of all elements in the
     * iterable specified
     * @param iterable the iterable in which elements will be used to fill into the sequence
     * @param <T> the element type
     * @return the sequence
     */
    @SuppressWarnings("unchecked")
    public static <T> Sequence<T> seq(Iterable<? extends T> iterable) {
        if (null == iterable) {
            return C.list();
        }
        if (iterable instanceof Sequence) {
            return ((Sequence<T>) iterable);
        }
        return IterableSeq.of(iterable);
    }

    public static <T> Sequence<T> seq(Iterator<? extends T> iterator) {
        return null == iterator ? C.<T>list() : IteratorSeq.of(iterator);
    }

    public static <T> Sequence<T> seq(Enumeration<? extends T> enumeration) {
        return null == enumeration ? C.<T>list() : IteratorSeq.of(new EnumerationIterator<T>(enumeration));
    }


    /**
     * Alias of {@link #collect(Iterable, String)}
     * @param collection
     * @param propertyPath
     * @param <PROPERTY>
     * @return
     */
    public static <PROPERTY> C.List<PROPERTY> extract(java.util.Collection<?> collection, final String propertyPath) {
        if (null == collection || collection.isEmpty()) {
            return C.list();
        }
        $.Transformer<Object, PROPERTY> extractor = new $.Transformer<Object, PROPERTY>() {
            @Override
            public PROPERTY transform(Object element) {
                return (PROPERTY)$.getProperty(element, propertyPath);
            }
        };
        return C.list(collection).map(extractor);
    }

    public static <PROPERTY> Sequence<PROPERTY> lazyExtract(Iterable<?> iterable, final String propertyPath) {
        $.Transformer<Object, PROPERTY> extractor = new $.Transformer<Object, PROPERTY>() {
            @Override
            public PROPERTY transform(Object element) {
                return (PROPERTY)$.getProperty(element, propertyPath);
            }
        };
        return map(iterable, extractor);
    }

    public static <T, R> Sequence<R> map(Iterable<? extends T> seq, $.Function<? super T, ? extends R> mapper) {
        if (null == seq) {
            return C.list();
        }
        if (seq instanceof ReversibleSequence) {
            ReversibleSequence<? extends T> rseq = $.cast(seq);
            return map(rseq, mapper);
        }
        return new MappedSeq<>(seq, mapper);
    }

    public static <T, R> ReversibleSequence<R> map(ReversibleSequence<? extends T> seq, $.Function<? super T, ? extends R> mapper
    ) {
        if (null == seq) {
            return C.list();
        }
        return new ReversibleMappedSeq<>(seq, mapper);
    }

    public static <T> Sequence<T> filter(Iterable<? extends T> iterable, $.Function<? super T, Boolean> predicate) {
        return null == iterable ? C.<T>list() : new FilteredSeq<>(iterable, predicate);
    }

    @SuppressWarnings("unchecked")
    public static <T> Sequence<T> prepend(T t, Sequence<T> sequence) {
        if (null == sequence) {
            return C.list(t);
        }
        if (sequence instanceof ReversibleSequence) {
            return prepend(t, (ReversibleSequence) sequence);
        } else {
            return concat(C.list(t), sequence);
        }
    }

    /**
     * Concatenate two {@link Sequence} into one
     * @param s1 the first sequence
     * @param s2 the second sequence
     * @param <T> the element type
     * @return the concatenated sequence
     */
    public static <T> Sequence<T> concat(Sequence<T> s1, Sequence<T> s2) {
        if (null == s1) {
            return null == s2 ? C.<T>list() : s2;
        }
        if (null == s2) {
            return s1;
        }
        return s1.append(s2);
    }

    /**
     * Concatenate two {@link ReversibleSequence} into one
     * @param s1 the first reversible sequence
     * @param s2 the second reversible sequence
     * @param <T> the element type
     * @return the concatenated reversible sequence
     */
    @SuppressWarnings("unused")
    public static <T> ReversibleSequence<T> concat(ReversibleSequence<T> s1, ReversibleSequence<T> s2) {
        if (null == s1) {
            return null == s2 ? C.<T>list() : s2;
        }
        if (null == s2) {
            return s1;
        }
        return s1.append(s2);
    }

    /**
     * Concatenate two {@link List} into one.
     * <p><b>Note</b> if the first list is readonly or immutable an new list instance
     * will be created with elements in both list 1 and list 2 filled in. Otherwise
     * all elemnets from list 2 will be appended to list 1 and return list 1 instance</p>
     * @param l1 list 1
     * @param l2 list 2
     * @param <T> the element type
     * @return a list with elements of both list 1 and list 2
     */
    @SuppressWarnings("unused")
    public static <T> List<T> concat(List<T> l1, List<T> l2) {
        if (null == l1) {
            return null == l2 ? C.<T>list() : l2;
        }
        if (null == l2) {
            return l1;
        }
        return l1.append(l2);
    }

    /**
     * This method is deprecated. Please use {@link #Set()} instead
     */
    @Deprecated
    public static <T> Set<T> set() {
        return Nil.set();
    }

    /**
     * Create an empty immutable set
     * @param <T> the generic type
     * @return the empty set
     */
    public static <T> Set<T> Set() {
        return Nil.set();
    }

    /**
     * Create an immutable set of a single element
     * @param element the single element
     * @param <T> the element type
     * @return the set that contains only specified element
     */
    public static <T> Set<T> set(T element) {
        java.util.Set<T> set = new HashSet<T>();
        set.add(element);
        return ImmutableSet.of(set);
    }

    /**
     * Create an immutable set contains specified elements
     * @param t1 one element to be added into the result set
     * @param ta an array from which all elements will be added into the result set
     * @param <T> the element type
     * @return a set that contains all elements specified
     */
    public static <T> Set<T> set(T t1, T... ta) {
        java.util.Set<T> set = new HashSet<T>();
        set.add(t1);
        Collections.addAll(set, ta);
        return ImmutableSet.of(set);
    }

    /**
     * Create an immutable set of an array of elements
     * @param ta the array from which all elements will be added into
     *           the result set
     * @param <T> the element type
     * @return the set contains all elements in the array
     */
    public static <T> Set<T> setOf(T... ta) {
        java.util.Set<T> set = new HashSet<T>();
        Collections.addAll(set, ta);
        return ImmutableSet.of(set);
    }

    /**
     * This method is deprecated. Please use {@link #Set(Collection)} instead
     */
    @Deprecated
    public static <T> Set<T> set(Collection<? extends T> col) {
        return ImmutableSet.of(col);
    }

    /**
     * Create an immutable set of all elements contained in the collection specified
     * @param col the collection from which elements will be added into the
     *            result set
     * @param <T> the element type
     * @return the set contains all elements in the collection
     * @see #newSet(Collection)
     */
    public static <T> Set<T> Set(Collection<? extends T> col) {
        return null == col ? C.<T>Set() : ImmutableSet.of(col);
    }

    /**
     * Create an immutable set of all elements supplied by the iterable specified
     * @param itr the iterable from where elements will be added into the result set
     * @param <T> the element type
     * @return the set contains all elements supplied by the iterable
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> set(Iterable<? extends T> itr) {
        if (itr instanceof Collection) {
            return set((Collection<T>) itr);
        }
        java.util.Set<T> set = new HashSet<T>();
        for (T t : itr) set.add(t);
        return ImmutableSet.of(set);
    }

    /**
     * Create an new empty set
     * @param <T> the element type
     * @return an empty set
     */
    public static <T> Set<T> newSet() {
        return new DelegatingSet<T>();
    }

    /**
     * Create an mutable set contains specified elements
     * @param t1 one element to be added into the result set
     * @param ta an array from which all elements will be added into the result set
     * @param <T> the element type
     * @return a set that contains all elements specified
     */
    public static <T> Set<T> newSet(T t1, T... ta) {
        Set<T> set = new DelegatingSet<>();
        set.add(t1);
        Collections.addAll(set, ta);
        return set;
    }


    /**
     * Create an new set with all elements contained in the collection
     * specified
     * @param col the collection from which all elements will be added into
     *            the result set
     * @param <T> the element type
     * @return the set contains all elements in the collection
     * @see #set(Collection)
     */
    public static <T> Set<T> newSet(Collection<? extends T> col) {
        return null == col ? C.<T>Set() : new DelegatingSet<>(col);
    }

    public static <T> Set<T> unionOf(Collection<? extends T> col1, Collection<? extends T> col2) {
        if (null == col1) {
            return null == col2 ? C.<T>Set() : Set(col2);
        }
        if (null == col2) {
            return C.Set(col1);
        }
        return ((Set<T>)C.Set(col1)).with(col2);
    }

    public static <T> Set<T> unionOf(Collection<? extends T> col1, Collection<? extends T> col2, Collection<? extends T> col3, Collection<? extends T> ... otherCols) {
        Set<T> union = C.newSet(col1);
        if (null != col2) {
            union.addAll(col2);
        }
        if (null != col3) {
            union.addAll(col3);
        }
        for (Collection<? extends T> col : otherCols) {
            if (null != col) {
                union.addAll(col);
            }
        }
        return C.set(union);
    }

    public static <T> Set<T> intercectionOf(Collection<? extends T> col1, Collection<? extends T> col2) {
        return ((Set<T>) C.Set(col1)).withIn(col2);
    }

    public static <T> Set<T> interceptionOf(Collection<? extends T> col1, Collection<? extends T> col2, Collection<? extends T> col3, Collection<? extends T>... otherCols) {
        Set<T> interception = C.newSet(col1);
        if (interception.isEmpty()) {
            return interception;
        }
        if (null == col2) {
            return C.Set();
        }
        interception.retainAll(col2);
        if (null == col3) {
            return C.Set();
        }
        interception.retainAll(col3);
        for (Collection<? extends T> col : otherCols) {
            if (null == col) {
                return C.Set();
            }
            interception.retainAll(col);
        }
        return interception;
    }

    /**
     * Create a immutable {@link Map} from elements specified in an array.
     * <p>Example</p>
     * <pre>
     *     Map&lt;String, Integer&gt; scores = C.map("Tom", 80, "Peter", 93, ...);
     * </pre>
     * <p>The above code will create an immutable Map with the following entries</p>
     * <ul>
     *     <li>(Tom, 80)</li>
     *     <li>(Peter, 93)</li>
     *     <li>...</li>
     * </ul>
     * <p><b>Note</b> the array size must be an even number, otherwise {@link IndexOutOfBoundsException}
     * will be thrown out</p>
     * @param args the argument array specifies the entries
     * @param <K> the key type
     * @param <V> the value type
     * @return an immutable map contains of specified entries
     * @see #newMap(Object...)
     */
    public static <K, V> Map<K, V> Map(Object... args) {
        if (null == args || args.length == 0) {
            return Nil.EMPTY_MAP;
        }
        return new Map<>(true, args);
    }

    public static <K, V> Map<K, V> Map(boolean readOnly, java.util.Map<K, V> map) {
        return new Map(readOnly, map);
    }

    public static <K, V> Map<K, V> Map(Collection<$.Tuple<K, V>> kvCol) {
        Map<K, V> map = C.newMap();
        for ($.Tuple<K, V> entry : kvCol) {
            map.put(entry._1, entry._2);
        }
        return new Map<>(true, map);
    }

    /**
     * Create an immutable {@link java.util.Map} from existing {@link java.util.Map}
     * @param map the map from which entries will be put into the new immutable map
     * @param <K> the key type
     * @param <V> the value type
     * @return an immutable map of the existing map
     */
    public static <K, V> Map<K, V> Map(java.util.Map<? extends K, ? extends V> map) {
        if (null == map) {
            return Nil.EMPTY_MAP;
        }
        return new Map(true, map);
    }

    /**
     * Create an new {@link Map} from an array of elements.
     * <p>Example</p>
     * <pre>
     *     Map&lt;String, Integer&gt; scores = C.newMap("Tom", 80, "Peter", 93, ...);
     * </pre>
     * <p>The above code will create a Map with the following entries</p>
     * <ul>
     *     <li>(Tom, 80)</li>
     *     <li>(Peter, 93)</li>
     *     <li>...</li>
     * </ul>
     * <p><b>Note</b> the array size must be an even number, otherwise {@link IndexOutOfBoundsException}
     * will be thrown out</p>
     * @param args the argument array specifies the entries
     * @param <K> the key type
     * @param <V> the value type
     * @return a map contains of specified entries
     * @see #Map(Object...)
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> newMap(Object... args) {
        return new Map(false, args);
    }

    /**
     * Create an new {@link Map} from existing {@link java.util.Map}
     * @param map the map that contains elements to be put into the new map
     * @param <K> the key type
     * @param <V> the value type
     * @return a map that contains all entries in the existing map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> newMap(java.util.Map<? extends K, ? extends V> map) {
        return new Map(false, map);
    }

    /**
     * Convert a {@link Enumeration} to an {@link Iterable}
     * @param e the enumeration
     * @param <T> the element type
     * @return an iterable corresponding to the enumeration
     */
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
    @SuppressWarnings("unused")
    public static boolean isReadOnly(Traversable<?> t) {
        return t.is(Feature.READONLY) || t.is(Feature.IMMUTABLE);
    }

    /**
     * Check if a {@link Traversable} structure is immutable.
     *
     * @param t the traversable strucure to be checked
     * @return {@code true} if the traversable is immutable
     */
    @SuppressWarnings("unused")
    public static boolean isImmutable(Traversable<?> t) {
        return t.is(Feature.IMMUTABLE);
    }

    /**
     * Run visitor function on each element supplied by the iterable. The visitor function can throw out
     * {@link org.osgl.Lang.Break} if it need to break the loop.
     * <p>Note if {@link NotAppliedException} thrown out by visitor function, it will be ignored
     * and keep looping through the Map entry set. It is kind of {@code continue} mechanism in a funcitonal
     * way</p>
     * @param iterable supply the element to be applied to the visitor function
     * @param visitor the function called on element provided by the iterable
     * @param <T> the generic type of the iterable elements
     * @throws $.Break break the loop
     */
    //TODO: implement forEach iteration in parallel
    public static <T> void forEach(Iterable<? extends T> iterable, $.Visitor<? super T> visitor) throws $.Break {
        for (T t : iterable) {
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    /**
     * Run visitor function on each element supplied by the iterator. The visitor function can throw out
     * {@link org.osgl.Lang.Break} if it need to break the loop.
     * <p>Note if {@link NotAppliedException} thrown out by visitor function, it will be ignored
     * and keep looping through the Map entry set. It is kind of {@code continue} mechanism in a funcitonal
     * way</p>
     * @param iterator iterator provides elements to be applied to the visitor function
     * @param visitor the function applied on the element
     * @param <T> the generic type of the element
     */
    public static <T> void forEach(Iterator<? extends T> iterator, $.Visitor<? super T> visitor) {
        while (iterator.hasNext()) {
            T t = iterator.next();
            visitor.apply(t);
        }
    }

    /**
     * Run indexedVisitor function on all key/value pair in a given map. The indexedVisitor function can
     * throw out {@link org.osgl.Lang.Break} if it need to break the loop.
     * <p>Note if {@link NotAppliedException} thrown out by indexedVisitor function, it will be ignored
     * and keep looping through the Map entry set. It is kind of {@code continue} mechanism in a funcitonal
     * way</p>
     * @param map the map in which enties will be applied to the indexedVisitor function
     * @param indexedVisitor the function that takes (key,value) pair
     * @param <K> the generic type of Key
     * @param <V> the generic type of Value
     * @throws $.Break the {@link org.osgl.Lang.Break} with payload throwed out by indexedVisitor function to break to loop
     */
    public static <K, V> void forEach(java.util.Map<K, V> map, IndexedVisitor<? super K, ? super V> indexedVisitor) throws $.Break {
        for (java.util.Map.Entry<K, V> entry : map.entrySet()) {
            try {
                indexedVisitor.apply(entry.getKey(), entry.getValue());
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    public static class _CollectStage {
        Iterable source;
        public _CollectStage(Iterable source) {
            this.source = source;
        }
        public <T> List<T> by(String propertyPath) {
            return C.collect(source, propertyPath);
        }
    }

    public static class _CollectStage2 {
        String propertyPath;
        public _CollectStage2(String propertyPath) {
            this.propertyPath = propertyPath;
        }

        public <T> List<T> on(Iterable source) {
            return C.collect(source, propertyPath);
        }
    }

    public static _CollectStage collect(Iterable source) {
        return new _CollectStage(source);
    }

    public static _CollectStage2 collect(String propertyPath) {
        return new _CollectStage2(propertyPath);
    }

    public static <T> List<T> collect(Iterable source, String propertyPath) {
        if (null == source) {
            return C.list();
        }
        int sz = 10;
        if (source instanceof Collection) {
            sz = ((Collection) source).size();
            if (0 == sz) {
                return C.list();
            }
        }
        List<T> retList = newSizedList(sz);
        for (Object o: source) {
            retList.add((T) $.getProperty(o, propertyPath));
        }
        return retList;
    }

    public static class _MapStage<T> {
        Iterable<? extends T> source;
        _MapStage(Iterable<? extends T> source) {
            this.source = source;
        }
        public <R> Sequence<R> with($.Function<? super T, ? extends R> mapper) {
            return C.map(source, mapper);
        }
    }

    public static class _MapStage2<T, R> {
        $.Function<? super T, ? extends R> mapper;
        _MapStage2($.Function<? super T, ? extends R> mapper) {
            this.mapper = $.requireNotNull(mapper);
        }
        public Sequence<R> on(Iterable<T> source) {
            return C.map(source, mapper);
        }
    }

    public static <T> _MapStage<T> map(Iterable<? extends T> source) {
        return new _MapStage<>(source);
    }

    public static <T, R> _MapStage2 map($.Function<? super T, ? extends R> mapper) {
        return new _MapStage2<T, R>(mapper);
    }

    public static class _FilterStage<T> {
        Iterable<? extends T> source;
        _FilterStage(Iterable<? extends T> source) {
            this.source = source;
        }
        public Sequence<T> by($.Predicate<? super T> predicate) {
            return filter(source, predicate);
        }
    }

    public static class _FilterStage2<T> {
        $.Predicate<? super T> predicate;
        _FilterStage2($.Predicate<? super T> predicate) {
            this.predicate = $.requireNotNull(predicate);
        }
        public Sequence<T> on(Iterable<? extends T> source) {
            return filter(source, predicate);
        }
    }

    public static <T> _FilterStage<T> filter(Iterable<? extends T> source) {
        return new _FilterStage<>(source);
    }

    public static <T> _FilterStage2<T> filter($.Predicate<? super T> predicate) {
        return new _FilterStage2<>(predicate);
    }

    private static void ensureWritable(boolean ro, String containerName) {
        if (ro) {
            throw new ReadOnlyException(containerName + " is readonly");
        }
    }

    // --- eof utility methods ---

    // --- Mutable collection/map constructors
    public enum Mutable {

    }

    /**
     * the namespace of function definitions relevant to Collection manipulation
     */
    public enum F {
        ;

        public static <T> $.Transformer<Iterable<T>, Collection<T>> asCollection() {
            return new Lang.Transformer<Iterable<T>, Collection<T>>() {
                @Override
                public Collection<T> transform(Iterable<T> iterable) {
                    return C.asCollection(iterable);
                }
            };
        }

        /**
         * Returns a predicate function that check if the argument is contained in
         * the collection specified
         *
         * @param collection the collection to be checked on against the argument when applying the prediate
         * @param <T> the generic type of the element of the collection
         * @return a predicate function
         * @see Collection#contains(Object)
         * @see #contains(Object)
         */
        public static <T> $.Predicate<T> containsIn(final Collection<? super T> collection) {
            return new $.Predicate<T>() {
                @Override
                public boolean test(T t) throws NotAppliedException, $.Break {
                    return collection.contains(t);
                }
            };
        }

        /**
         * Returns a predicate function that check if the argument (collection) contains the
         * element specified
         *
         * @param element the element to be checked
         * @param <T> the type of the element
         * @return the function that do the check
         * @see Collection#contains(Object)
         * @see #containsIn(Collection)
         */
        public static <T> $.Predicate<Collection<? super T>> contains(final T element) {
            return new $.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> collection) {
                    return collection.contains(element);
                }
            };
        }

        /**
         * Returns a predicate function that check if all element in the argument (a collection) contained
         * in the collection specified
         * @param collection the collection to be checked on against all elements in the argument when
         *                   applying the function
         * @param <T> the generic type of the element of the collection or argument
         * @return the function that do the check
         * @see Collection#containsAll(Collection)
         * @see #containsAll(Collection)
         */
        @SuppressWarnings("unused")
        public static <T> $.Predicate<Collection<? extends T>> allContainsIn(final Collection<? super T> collection) {
            return new $.Predicate<Collection<? extends T>>() {
                @Override
                public boolean test(Collection<? extends T> theCollection) {
                    return collection.containsAll(theCollection);
                }
            };
        }

        /**
         * Returns a predicate function that check if all element in the collection specified are contained in
         * the argument collection
         * @param collection the collection in which all elements will be checked if contained in the argument
         *                   collection when applying the function
         * @param <T> the element type
         * @return the function that do the check
         * @see Collection#containsAll(Collection)
         * @see #allContainsIn(Collection)
         */
        @SuppressWarnings("unused")
        public static <T> $.Predicate<Collection<? super T>> containsAll(final Collection<? extends T> collection) {
            return new $.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> theCollection) {
                    return theCollection.contains(collection);
                }
            };
        }

        /**
         * Returns a function that add the argument to a collection specified and returns
         * {@code true} if added successfully or {@code false} otherwise
         * @param destination the collection into which the argument to be added
         * @param <T> the generic type of the collection elements
         * @return a function that do the add operation
         * @see Collection#add(Object)
         * @see #add(Object)
         */
        public static <T> $.Predicate<T> addTo(final Collection<? super T> destination) {
            return new $.Predicate<T>() {
                @Override
                public boolean test(T t) throws NotAppliedException, $.Break {
                    return destination.add(t);
                }
            };
        }

        /**
         * Returns a function that add the specified element into the argument collection and
         * return {@code true} if add successfully or {@code false} otherwise
         * @param element the element to be added when applying the function
         * @param <T> the element type
         * @return the function
         * @see Collection#add(Object)
         * @see #addTo(Collection)
         */
        public static <T> $.Predicate<Collection<? super T>> add(final T element) {
            return new $.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> collection) {
                    return collection.add(element);
                }
            };
        }

        /**
         * Returns a function that add the argument into the specified list at specified position.
         * the function returns {@code true} if added successfully or {@code false} otherwise
         * @param destination a list into which the argument to be added
         * @param index specify the position where the argument can be added
         * @param <L> the generic type of the list
         * @param <T> the generic type of the list element
         * @return the function that do the add operation
         * @see java.util.List#add(int, Object)
         * @see #add(int, Object)
         */
        @SuppressWarnings("unused")
        public static <L extends List<? super T>, T> $.F1<T, L> addTo(final int index, final L destination) {
            return new $.F1<T, L>() {
                @Override
                public L apply(T t) throws NotAppliedException, $.Break {
                    destination.add(index, t);
                    return destination;
                }
            };
        }

        /**
         * Returns a function that add specified element into the argument list at specified position. The
         * function returns the argument list after element added
         * @param index the location at where the element should be added to
         * @param element the element the be added to the argument list
         * @param <L> the list type
         * @param <T> the element type
         * @return the function
         * @see java.util.List#add(int, Object)
         * @see #addTo(int, List)
         */
        public static <L extends List<? super T>, T> $.F1<L, L> add(final int index, final T element) {
            return new $.F1<L, L>() {
                @Override
                public L apply(L list) throws NotAppliedException, Lang.Break {
                    list.add(index, element);
                    return list;
                }
            };
        }

        /**
         * Returns a function that takes argument of type {@link Collection} and add all elements inside
         * into the specified collection. The function returns {@code true} if the collection specified
         * has been changed as a result of adding elements
         * @param destination the collection into which all elements in the argument collection will be added
         *                    when applying the function
         * @param <T> the generic type of the collection element and the argument collection element
         * @return the function that add all elements from iterable argument into the collection specified
         * @see Collection#addAll(Collection)
         * @see #addAll(Collection)
         */
        @SuppressWarnings({"unchecked"})
        public static <T> $.Predicate<Iterable<? extends T>> addAllTo(final Collection<? super T> destination) {
            return new $.Predicate<Iterable<? extends T>>() {
                @Override
                public boolean test(Iterable<? extends T> source) throws NotAppliedException, $.Break {
                    if (source instanceof Collection) {
                        return destination.addAll((Collection)(source));
                    }
                    return destination.addAll(C.list(source));
                }
            };
        }

        /**
         * Returns a function that add all elements in the source collection specified into the destination
         * collection as argument. The function returns {@code true} if the argument collection has been
         * changes as a result of call.
         * @param source the collection from which the elements will be added into the argument collection
         *               when applying the function
         * @param <T> the element type
         * @return the function the perform the add operation
         * @see Collection#addAll(Collection)
         * @see #addAllTo(Collection)
         */
        @SuppressWarnings({"unchecked"})
        public static <T> $.Predicate<Collection<? super T>> addAll(final Collection<? extends T> source) {
            return new $.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> destination) {
                    return destination.addAll(source);
                }
            };
        }

        /**
         * Returns a function that add all elements from the argument collection into the destination list specified
         * at the position specified
         * @param index the position at where the element shall be inserted into the destination list
         * @param destination the list into which the elements will be added
         * @param <T> the element type
         * @return the function that do the add operation
         * @see java.util.List#addAll(int, Collection)
         * @see #addAll(int, Collection)
         */
        @SuppressWarnings({"unused"})
        public static <T> $.Predicate<Collection<? extends T>> addAllTo(final int index, final List<? super T> destination) {
            if (0 > index || destination.size() < index) {
                throw new IndexOutOfBoundsException();
            }
            return new $.Predicate<Collection<? extends T>>() {
                @Override
                public boolean test(Collection<? extends T> collection) throws NotAppliedException, $.Break {
                    return destination.addAll(index, collection);
                }
            };
        }

        /**
         * Returns a function that add all elements from the source collection specified into the argument list at
         * the position specified
         * @param index the position where the element should be insert in the argument list
         * @param source the collection from which the elements to be get to added into the argument list
         * @param <T> the element type
         * @return the function that do the add operation
         * @see java.util.List#addAll(int, Collection)
         * @see #addAllTo(int, List)
         */
        @SuppressWarnings({"unused"})
        public static <T> $.Predicate<List<? super T>> addAll(final int index, final Collection<? extends T> source) {
            return new $.Predicate<List<? super T>>() {
                @Override
                public boolean test(List<? super T> destination) {
                    return destination.addAll(index, source);
                }
            };
        }


        /**
         * Returns a function that remove the argument from a collection specified.
         * <p>The function returns {@code true} if argument removed successfully or
         * {@code false} otherwise</p>
         * @param collection the collection from which the argument to be removed
         *                   when applying the function returned
         * @return the function that remove element from the collection
         * @see Collection#remove(Object)
         * @see #remove(Object)
         */
        @SuppressWarnings("unused")
        public static <T> $.Predicate<T> removeFrom(final Collection<? super T> collection) {
            return new $.Predicate<T>() {
                @Override
                public boolean test(T t) throws NotAppliedException, $.Break {
                    return collection.remove(t);
                }
            };
        }

        /**
         * Returns a function that remove the element specified from the argument collection. The
         * function returns {@code true} if the argument collection changed as a result of the call.
         * @param toBeRemoved the element to be removed from the argument when applying the function
         * @param <T> the element type
         * @return the function that do removing
         * @see Collection#remove(Object)
         * @see #removeFrom(Collection)
         */
        @SuppressWarnings("unused")
        public static <T> $.Predicate<Collection<? super T>> remove(final T toBeRemoved) {
            return new $.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> collection) {
                    return collection.remove(toBeRemoved);
                }
            };
        }

        /**
         * Returns a function that remove all elements in the argument collection from
         * the {@code fromCollection} specified. The function returns {@code true} if
         * the fromCollection changed as a result of call
         * @param fromCollection the collection from which elements will be removed
         * @param <T> the element type
         * @return the function
         * @see Collection#removeAll(Collection)
         * @see #removeAll(Collection)
         */
        @SuppressWarnings("unused")
        public static <T> $.Predicate<Collection<? extends T>> removeAllFrom(final Collection<? super T> fromCollection) {
            return new Lang.Predicate<Collection<? extends T>>() {
                @Override
                public boolean test(Collection<? extends T> theCollection) {
                    return fromCollection.removeAll(theCollection);
                }
            };
        }

        /**
         * Returns a function that remove all elements in the {@code source} collection from the
         * argument collection. The function returns {@code true} if the argument collection changed
         * as a result of call
         * @param source the collection in which elements will be used to remove from argument collection
         * @param <T> the element type
         * @return the function
         * @see Collection#removeAll(Collection)
         * @see #removeAllFrom(Collection)
         */
        public static <T> $.Predicate<Collection<? super T>> removeAll(final Collection<? extends T> source) {
            return new Lang.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> collection) {
                    return collection.removeAll(source);
                }
            };
        }


        /**
         * Returns a function that retains only elements contained in the argument collection in the
         * collection specified. The function returns {@code true} if the collection specified
         * changed as a result of call
         * @param collection the collection in which elements will be retained/removed
         * @param <T> the element type
         * @return the function as described
         * @see Collection#retainAll(Collection)
         * @see #retainAll(Collection)
         */
        @SuppressWarnings({"unused"})
        public static <T> $.Predicate<Collection<? extends T>> retainAllIn(final Collection<? super T> collection) {
            return new $.Predicate<Collection<? extends T>>() {
                @Override
                public boolean test(Collection<? extends T> theCollection) {
                    return collection.retainAll(theCollection);
                }
            };
        }

        /**
         * Returns a function that retains only elements contained in the specified collection in
         * the argument collection. The function returns {@code true} if argument collection changes
         * as a result of the call
         * @param collection the collection in which elements will be used to check if argument collection
         *                   element shall be retained or not
         * @param <T> the element type
         * @return the function as described above
         * @see Collection#retainAll(Collection)
         * @see #retainAllIn(Collection)
         */
        @SuppressWarnings({"unused"})
        public static <T> $.Predicate<Collection<? super T>> retainAll(final Collection<? extends T> collection) {
            return new $.Predicate<Collection<? super T>>() {
                @Override
                public boolean test(Collection<? super T> theCollection) {
                    return theCollection.retainAll(collection);
                }
            };
        }

        /**
         * Returns a function that prepend an element to a deque specified and return the
         * deque instance
         * @param deque the deque to which the element argument will be prepend to
         * @param <T> the element type
         * @return the function as described
         * @see Deque#addFirst(Object)
         * @see #dequePrepend(Object)
         */
        @SuppressWarnings({"unused"})
        public static <T> $.F1<T, Deque<? super T>> prependTo(final Deque<? super T> deque) {
            return new $.F1<T, Deque<? super T>>() {
                @Override
                public Deque<? super T> apply(T t) throws NotAppliedException, $.Break {
                    deque.addFirst(t);
                    return deque;
                }
            };
        }

        /**
         * Returns a function that prepend specified element to argument deque
         * @param element the element to be added to the head of the argument (deque type)
         * @param <T> the element type
         * @return the function as described
         * @see Deque#addFirst(Object)
         * @see #prependTo(Deque)
         */
        @SuppressWarnings("unused")
        public static <T> $.Processor<Deque<? super T>> dequePrepend(final T element) {
            return new $.Processor<Deque<? super T>>() {
                @Override
                public void process(Deque<? super T> deque) throws Lang.Break, NotAppliedException {
                    deque.addFirst(element);
                }
            };
        }

        /**
         * Returns a function that append the argument to a {@link Deque} specified
         * @param deque the deque to which the argument shall be append when applying the function returned
         * @param <T> the generic type of the argument/deque element
         * @return the function that do the append operation
         * @see Deque#add(Object)
         * @see #dequeAppend(Object)
         */
        @SuppressWarnings("unused")
        public static <T> $.F1<T, Deque<? super T>> appendTo(final Deque<? super T> deque) {
            return new $.F1<T, Deque<? super T>>() {
                @Override
                public Deque<? super T> apply(T t) throws NotAppliedException, $.Break {
                    deque.addLast(t);
                    return deque;
                }
            };
        }

        /**
         * Returns a function that append specified element to argument deque
         * @param element the element to be added to the tail of the argument (deque type)
         * @param <T> the element type
         * @return the function as described
         * @see Deque#add(Object)
         * @see #appendTo(Deque)
         */
        @SuppressWarnings("unused")
        public static <T> $.Processor<Deque<? super T>> dequeAppend(final T element) {
            return new $.Processor<Deque<? super T>>() {
                @Override
                public void process(Deque<? super T> deque) throws Lang.Break, NotAppliedException {
                    deque.add(element);
                }
            };
        }

        /**
         * Returns a function that prepend the argument to a {@link Sequence} specified
         * @param sequence the sequence to which the argument shall be prepend whene applying the function
         * @param <T> the generic type of the argument/sequence element
         * @return the function that do the prepend operation
         * @see Sequence#prepend(Object)
         * @see #sequencePrepend(Object)
         */
        @SuppressWarnings("unused")
        public static <T> $.F1<T, Sequence<? super T>> prependTo(final Sequence<? super T> sequence) {
            return new $.F1<T, Sequence<? super T>>() {
                @Override
                public Sequence<? super T> apply(T t) throws NotAppliedException, $.Break {
                    sequence.prepend(t);
                    return sequence;
                }
            };
        }

        /**
         * Returns a function that preppend specified element to argument sequence
         * @param element the element to be added to the head of the argument (sequence type)
         * @param <T> the element type
         * @return the function as described
         * @see Sequence#prepend(Object)
         * @see #prependTo(Sequence)
         */
        @SuppressWarnings("unused")
        public static <T> $.Processor<Sequence<? super T>> sequencePrepend(final T element) {
            return new Lang.Processor<Sequence<? super T>>() {
                @Override
                public void process(Sequence<? super T> sequence) throws Lang.Break, NotAppliedException {
                    sequence.prepend(element);
                }
            };
        }

        /**
         * Returns a function that append the argument to a {@link Sequence} specified
         * <p><b>Note</b> the function returns the sequence with the argument been removed</p>
         * @param sequence the sequence to which the argument shall be append when applying the function
         * @param <T> the generic type of the argument/sequence element
         * @return the function that do the append operation
         * @see Sequence#append(Iterable)
         * @see #sequenceAppend(Object)
         */
        @SuppressWarnings("unused")
        public static <T> $.F1<T, Sequence<? super T>> appendTo(final Sequence<? super T> sequence) {
            return new $.F1<T, Sequence<? super T>>() {
                @Override
                public Sequence<? super T> apply(T t) throws NotAppliedException, $.Break {
                    sequence.append(t);
                    return sequence;
                }
            };
        }


        /**
         * Returns a function that append specified element to argument sequence
         * @param element the element to be added to the tail of the argument (sequence type)
         * @param <T> the element type
         * @return the function as described
         * @see Sequence#append(Iterable)
         * @see #appendTo(Sequence)
         */
        @SuppressWarnings("unused")
        public static <T> $.Processor<Sequence<? super T>> sequenceAppend(final T element) {
            return new Lang.Processor<Sequence<? super T>>() {
                @Override
                public void process(Sequence<? super T> sequence) throws Lang.Break, NotAppliedException {
                    sequence.append(element);
                }
            };
        }

        /**
         * Returns a function that apply the visitor function specified on the argument (iterable)
         * @param visitor the function to be used to loop through the argument
         * @param <T> the element type
         * @return the function as described
         * @see C#forEach(Iterable, org.osgl.Lang.Visitor)
         */
        @SuppressWarnings("unused")
        public static <T> $.F1<Iterable<? extends T>, Void> forEachIterable(final $.Visitor<? super T> visitor) {
            return new $.F1<Iterable<? extends T>, Void>() {
                @Override
                public Void apply(Iterable<? extends T> iterable) throws NotAppliedException, $.Break {
                    C.forEach(iterable, visitor);
                    return null;
                }
            };
        }
    }


}
