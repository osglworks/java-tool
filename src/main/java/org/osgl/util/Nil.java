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

import org.osgl.$;
import org.osgl.Lang;
import org.osgl.exception.NotAppliedException;

import java.io.Serializable;
import java.util.*;

/**
 * Implementing immutable empty collection types
 */
abstract class Nil<T> extends SequenceBase<T> implements C.Traversable<T>, Collection<T>, Serializable {

    public static final EmptySequence SEQUENCE = EmptySequence.INSTANCE;
    public static final EmptyReversibleSequence REVERSIBLE_SEQUENCE = EmptyReversibleSequence.INSTANCE;
    public static final EmptyRange RANGE = EmptyRange.INSTANCE;
    public static final EmptyList LIST = EmptyList.INSTANCE;
    public static final Empty EMPTY = Empty.INSTANCE;
    private static final long serialVersionUID = -5058901899659394002L;
    public static EmptySet SET = EmptySet.INSTANCE;
    public static final C.Map EMPTY_MAP = new C.Map(true);

    //    public static final EmptySet SET = EmptySet.INSTANCE;
//
//    @SuppressWarnings("unchecked")
//    public static <T> EmptySet<T> set() {
//        return (EmptySet<T>) EmptySet.INSTANCE;
//    }
//
//    public static final EmptySortedSet SORTED_SET = EmptySortedSet.INSTANCE;
//
//    @SuppressWarnings("unchecked")
//    public static <T> EmptySortedSet<T> sortedSet() {
//        return (EmptySortedSet<T>) EmptySortedSet.INSTANCE;
//    }
//
    private Nil() {
    }

    @Override
    public <T1> C.ListOrSet<T1> collect(String path) {
        return EMPTY;
    }

    @Override
    public C.List<T> asList() {
        return list();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return $.cast(EMPTY_MAP);
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptySequence<T> seq() {
        return (EmptySequence<T>) SEQUENCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyReversibleSequence<T> rseq() {
        return (EmptyReversibleSequence<T>) REVERSIBLE_SEQUENCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyRange<T> range() {
        return (EmptyRange<T>) RANGE;
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyList<T> list() {
        return (EmptyList<T>) EmptyList.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptySet<T> set() {
        return (EmptySet<T>) EmptySet.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Empty<T> empty() {
        return (Empty<T>) Empty.INSTANCE;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.IMMUTABLE, C.Feature.ORDERED, C.Feature.LIMITED, C.Feature.RANDOM_ACCESS, C.Feature.READONLY, C.Feature.SORTED, C.Feature.PARALLEL);
    }

    protected final java.util.List<T> emptyJavaList() {
        return Collections.emptyList();
    }

    protected abstract <T> Nil<T> singleton();

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public final Iterator<T> iterator() {
        return emptyJavaList().iterator();
    }

    @Override
    public final int size() {
        return 0;
    }

    @Override
    public final boolean contains(Object o) {
        return false;
    }

    @Override
    public final Object[] toArray() {
        return new Object[0];
    }

    @Override
    public final <T1> T1[] toArray(T1[] a) {
        return emptyJavaList().toArray(a);
    }

    @Override
    public final boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean containsAll(Collection<?> c) {
        return c.isEmpty();
    }

    @Override
    public final boolean addAll(Collection<? extends T> c) {
        if (c.isEmpty()) {
            return false;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public final boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public final void clear() {
    }

    @Override
    public final boolean isEmpty() {
        return true;
    }

    @Override
    public <R> C.Sequence<R> map($.Function<? super T, ? extends R> mapper) {
        return singleton();
    }

    @Override
    public <R> C.Sequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return singleton();
    }

    @Override
    public Nil<T> filter($.Function<? super T, Boolean> predicate) {
        return this;
    }

    @Override
    public final <R> R reduce(R identity, $.Func2<R, T, R> accumulator) {
        return identity;
    }

    @Override
    public final $.Option<T> reduce($.Func2<T, T, T> accumulator) {
        return $.none();
    }

    @Override
    public final boolean allMatch($.Function<? super T, Boolean> predicate) {
        return false;
    }

    @Override
    public final boolean anyMatch($.Function<? super T, Boolean> predicate) {
        return false;
    }

    @Override
    public final boolean noneMatch($.Function<? super T, Boolean> predicate) {
        return true;
    }

    @Override
    public final $.Option<T> findOne($.Function<? super T, Boolean> predicate) {
        return $.none();
    }

    @Override
    public Nil<T> accept($.Visitor<? super T> visitor) {
        return this;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    static class EmptySequence<T> extends Nil<T> implements C.Sequence<T> {

        private static final EmptySequence INSTANCE = new EmptySequence();

        private EmptySequence() {
        }

        protected <T> EmptySequence<T> singleton() {
            return INSTANCE;
        }

        /**
         * {@inheritDoc}
         * <p>Returns an immutable singleton list contains the element
         * specified</p>
         *
         * @param t the element to be appended to this sequence
         * @return a singleton list of the element
         */
        @Override
        public C.Sequence<T> append(T t) {
            return C.list(t);
        }

        /**
         * {@inheritDoc}
         * <p>Returns an immutable singleton list contains the
         * element specified</p>
         *
         * @param t the element to be prepended to this sequence
         * @return a singleton list of the element
         */
        @Override
        public C.Sequence<T> prepend(T t) {
            return C.list(t);
        }

        /**
         * {@inheritDoc}
         * <p>Returns identity specified</p>
         *
         * @param identity    {@inheritDoc}
         * @param accumulator {@inheritDoc}
         * @param <R>         the type of identity and result
         * @return {@inheritDoc}
         */
        @Override
        public <R> R reduceLeft(R identity, $.Func2<R, T, R> accumulator) {
            return identity;
        }

        @Override
        public $.Option<T> reduceLeft($.Func2<T, T, T> accumulator) {
            return $.none();
        }

        @Override
        public $.Option<T> findFirst($.Function<? super T, Boolean> predicate) {
            return $.none();
        }

        @Override
        public EmptySequence<T> acceptLeft($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public EmptySequence<T> accept($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public T first() {
            throw new NoSuchElementException();
        }

        @Override
        public EmptySequence<T> head(int n) {
            return this;
        }

        @Override
        public EmptySequence<T> tail() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public EmptySequence<T> take(int n) {
            return this;
        }

        @Override
        public EmptySequence<T> takeWhile($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public EmptySequence<T> drop(int n) {
            return this;
        }

        @Override
        public EmptySequence<T> dropWhile($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public <R> EmptySequence<R> map($.Function<? super T, ? extends R> mapper) {
            return singleton();
        }

        @Override
        public <R> EmptySequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper) {
            return singleton();
        }

        @Override
        public EmptySequence<T> filter($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public C.Sequence<T> append(C.Sequence<? extends T> seq) {
            return Lang.cast(seq);
        }

        @Override
        public C.Sequence<T> prepend(C.Sequence<? extends T> seq) {
            return Lang.cast(seq);
        }

        @Override
        public C.Sequence<T> prepend(Iterable<? extends T> iterable) {
            if (!iterable.iterator().hasNext()) {
                return this;
            }
            return C.seq(iterable);
        }

        @Override
        public C.Sequence<T> prepend(Iterator<? extends T> iterator) {
            if (!iterator.hasNext()) {
                return this;
            }
            return C.seq(iterator);
        }

        @Override
        public C.Sequence<T> prepend(Enumeration<? extends T> enumeration) {
            if (!enumeration.hasMoreElements()) {
                return this;
            }
            return C.seq(enumeration);
        }

        @Override
        public C.Sequence<T> append(Iterator<? extends T> iterator) {
            return C.seq(iterator);
        }

        @Override
        public C.Sequence<T> append(Enumeration<? extends T> enumeration) {
            return C.seq(enumeration);
        }

        // Preserves singleton property
        private Object readResolve() {
            return INSTANCE;
        }
    }

    static class EmptyReversibleSequence<T>
            extends EmptySequence<T> implements C.ReversibleSequence<T> {

        private static final EmptyReversibleSequence INSTANCE = new EmptyReversibleSequence();

        @Override
        protected <T1> EmptyReversibleSequence<T1> singleton() {
            return INSTANCE;
        }

        @Override
        public C.ReversibleSequence<T> lazy() {
            return (C.ReversibleSequence<T>) super.lazy();
        }

        @Override
        public C.ReversibleSequence<T> eager() {
            return (C.ReversibleSequence<T>) super.eager();
        }

        @Override
        public C.ReversibleSequence<T> parallel() {
            return (C.ReversibleSequence<T>) super.parallel();
        }

        @Override
        public C.ReversibleSequence<T> sequential() {
            return (C.ReversibleSequence<T>) super.sequential();
        }

        @Override
        public T last() throws UnsupportedOperationException, NoSuchElementException {
            throw new NoSuchElementException();
        }

        @Override
        public EmptyReversibleSequence<T> reverse() throws UnsupportedOperationException {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> take(int n) {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> head(int n) {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> tail() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public EmptyReversibleSequence<T> tail(int n) throws UnsupportedOperationException {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> drop(int n) {
            return this;
        }

        @Override
        public Iterator<T> reverseIterator() {
            return iterator();
        }

        @Override
        public EmptyReversibleSequence<T> accept($.Visitor<? super T> visitor) {
            return this;
        }


        @Override
        public EmptyReversibleSequence<T> each($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> forEach($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> takeWhile($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> dropWhile($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public <R> EmptyReversibleSequence<R> map($.Function<? super T, ? extends R> mapper) {
            return singleton();
        }

        @Override
        public <R> EmptyReversibleSequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper) {
            return singleton();
        }

        @Override
        public EmptyReversibleSequence<T> filter($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public EmptyReversibleSequence<T> acceptLeft($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public <R> R reduceRight(R identity, $.Func2<R, T, R> accumulator) {
            return identity;
        }

        @Override
        public $.Option<T> reduceRight($.Func2<T, T, T> accumulator) {
            return $.none();
        }

        @Override
        public $.Option<T> findLast($.Function<? super T, Boolean> predicate) {
            return $.none();
        }

        @Override
        public EmptyReversibleSequence<T> acceptRight($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
            return seq;
        }

        @Override
        public C.ReversibleSequence<T> append(T t) {
            return C.list(t);
        }

        @Override
        public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
            return seq;
        }

        @Override
        public C.ReversibleSequence<T> prepend(T t) {
            return C.list(t);
        }

        @Override
        public <T2> C.ReversibleSequence<$.Binary<T, T2>> zip(C.ReversibleSequence<T2> rseq) {
            return rseq();
        }

        @Override
        public <T2> C.ReversibleSequence<$.Binary<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, final T def1, final T2 def2) {
            return rseq.map(new $.F1<T2, $.Binary<T, T2>>() {
                @Override
                public $.Binary<T, T2> apply(T2 t) throws NotAppliedException, $.Break {
                    return $.T2(def1, t);
                }
            });
        }
    }

    static class EmptyRange<T> extends EmptySequence<T> implements C.Range<T>, RandomAccess {
        private static final EmptyRange INSTANCE = new EmptyRange();

        @Override
        public T from() {
            throw new NoSuchElementException();
        }

        @Override
        public T to() {
            throw new NoSuchElementException();
        }

        @Override
        public T last() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean containsAll(C.Range<T> r2) {
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<T> order() {
            return (Comparator<T>) $.F.NATURAL_ORDER;
        }

        @Override
        public $.Func2<T, Integer, T> step() {
            return $.f2();
        }

        @Override
        public C.Range<T> merge(C.Range<T> r2) {
            return r2;
        }

        @Override
        public EmptyRange<T> accept($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public EmptyRange<T> forEach($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public EmptyRange<T> each($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public EmptyRange<T> acceptLeft($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public C.Range<T> acceptRight($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public C.Range<T> tail(int n) {
            return this;
        }

        @Override
        public C.Range<T> reverse() {
            return this;
        }

        @Override
        public Iterator<T> reverseIterator() {
            return iterator();
        }

        @Override
        public <R> R reduceRight(R identity, $.Func2<R, T, R> accumulator) {
            return identity;
        }

        @Override
        public $.Option<T> reduceRight($.Func2<T, T, T> accumulator) {
            return $.none();
        }

        @Override
        public $.Option<T> findLast($.Function<? super T, Boolean> predicate) {
            return $.none();
        }
    }

    static class EmptyList<T> extends ImmutableList<T> implements C.List<T>, RandomAccess {

        private static final long serialVersionUID = 2142813031316831861L;
        private static final EmptyList<?> INSTANCE = new EmptyList();

        private EmptyList() {
            super((T[]) new Object[0]);
        }

        @SuppressWarnings("unchecked")
        protected <T1> EmptyList<T1> singleton() {
            return (EmptyList<T1>) INSTANCE;
        }

        @Override
        public final boolean isEmpty() {
            return true;
        }

        @Override
        public Lang.T2<C.List<T>, C.List<T>> split(Lang.Function<? super T, Boolean> predicate) {
            C.List<T> empty = this;
            return $.T2(empty, empty);
        }

        // Preserves singleton property
        private Object readResolve() {
            return INSTANCE;
        }
    }

    static class EmptyStringList extends EmptyList<String> implements S.List {
        private static final long serialVersionUID = 2142813031316831811L;
        private static final EmptyStringList INSTANCE = new EmptyStringList();

        @SuppressWarnings("unchecked")
        protected EmptyStringList singleton() {
            return INSTANCE;
        }

        // Preserves singleton property
        private Object readResolve() {
            return INSTANCE;
        }
    }

    static class EmptySet<T> extends ImmutableSet<T> implements C.Set<T>, Serializable {

        private static final long serialVersionUID = 4142843931316831861L;
        private static final EmptySet<?> INSTANCE = new EmptySet();

        private EmptySet() {
            super(Collections.EMPTY_SET);
        }

        @SuppressWarnings("unchecked")
        protected EmptySet<T> singleton() {
            return (EmptySet<T>) INSTANCE;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override
        public EmptySet<T> accept($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public C.Set<T> onlyIn(Collection<? extends T> col) {
            return C.Set(col);
        }

        @Override
        public C.Set<T> withIn(Collection<? extends T> col) {
            return this;
        }

        @Override
        public C.Set<T> without(Collection<? super T> col) {
            return this;
        }

        @Override
        public C.Set<T> with(Collection<? extends T> col) {
            return C.Set(col);
        }

        @Override
        public C.Set<T> with(T element) {
            return C.set(element);
        }

        @Override
        public C.Set<T> with(T element, T... elements) {
            return C.set(element, elements);
        }

        @Override
        public C.Set<T> without(T element) {
            return null;
        }

        @Override
        public C.Set<T> without(T element, T... elements) {
            return null;
        }

        // Preserves singleton property
        private Object readResolve() {
            return INSTANCE;
        }
    }

    static class Empty<T> extends EmptyList<T> implements C.ListOrSet<T> {

        private static final Empty INSTANCE = new Empty();

        @Override
        public Empty<T> parallel() {
            return this;
        }

        @Override
        public Empty<T> lazy() {
            return this;
        }

        @Override
        public Empty<T> filter($.Function<? super T, Boolean> predicate) {
            return this;
        }

        @Override
        public Empty<T> eager() {
            return this;
        }

        @Override
        public Empty<T> sequential() {
            return this;
        }

        @Override
        public Empty<T> accept($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public Empty<T> each($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public Empty<T> forEach($.Visitor<? super T> visitor) {
            return this;
        }

        @Override
        public Empty<T> without(Collection<? super T> col) {
            return this;
        }

        @Override
        public Empty<T> without(T element) {
            return this;
        }

        @Override
        public Empty<T> without(T element, T... elements) {
            return this;
        }

        @Override
        public C.Set<T> with(Collection<? extends T> col) {
            return C.Set(col);
        }

        @Override
        public C.Set<T> with(T element) {
            return C.set(element);
        }

        @Override
        public C.Set<T> with(T element, T... elements) {
            return C.set(element, elements);
        }

        @Override
        public C.Set<T> onlyIn(Collection<? extends T> col) {
            return C.Set(col);
        }

        @Override
        public Empty<T> withIn(Collection<? extends T> col) {
            return this;
        }

        @Override
        public <R> Empty<R> map($.Function<? super T, ? extends R> mapper) {
            return (Empty<R>) this;
        }

        @Override
        public <R> Empty<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper) {
            return (Empty<R>) this;
        }
    }
//
//    static class EmptySortedSet<T> extends EmptyReversibleSequence<T> implements C.SortedSet<T>, Serializable {
//
//        private static final long serialVersionUID = 8142843931221131271L;
//
//        private EmptySortedSet() {
//        }
//
//        private static final EmptySortedSet<?> INSTANCE = new EmptySortedSet();
//
//        @Override
//        @SuppressWarnings("unchecked")
//        protected EmptySortedSet<T> singleton() {
//            return (EmptySortedSet<T>) INSTANCE;
//        }
//
//        @Override
//        public EmptySortedSet<T> accept(_.Function<? super T, ?> visitor) {
//            return this;
//        }
//
//        @Override
//        public Comparator<? super T> comparator() {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public SortedSet<T> subSet(T fromElement, T toElement) {
//            return this;
//        }
//
//        @Override
//        public SortedSet<T> headSet(T toElement) {
//            return this;
//        }
//
//        @Override
//        public SortedSet<T> tailSet(T fromElement) {
//            return this;
//        }
//
//        // Preserves singleton property
//        private Object readResolve() {
//            return INSTANCE;
//        }
//    }

}
