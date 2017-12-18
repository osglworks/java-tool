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

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provide default implementation to some {@link C.Sequence} interface.
 *
 * <p>The most of the method of this implementation is lazy without
 * regarding to the {@link C.Feature#LAZY} setting of this
 * sequence instance</p>
 */
public abstract class SequenceBase<T>
extends TraversableBase<T> implements C.Sequence<T> {

    // utilities
    protected final boolean isLazy() {
        return is(C.Feature.LAZY);
    }

    protected final boolean isImmutable() {
        return is(C.Feature.IMMUTABLE);
    }

    protected final boolean isReadOnly() {
        return is(C.Feature.READONLY);
    }

    protected final boolean isMutable() {
        return !isImmutable() && !isReadOnly();
    }

    protected final boolean isLimited() {
        return is(C.Feature.LIMITED);
    }

    @Override
    public C.Sequence<T> lazy() {
        return (C.Sequence<T>)super.lazy();
    }

    @Override
    public C.Sequence<T> eager() {
        return (C.Sequence<T>)super.eager();
    }

    @Override
    public C.Sequence<T> parallel() {
        return (C.Sequence<T>)super.parallel();
    }

    @Override
    public C.Sequence<T> sequential() {
        return (C.Sequence<T>)super.sequential();
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.LAZY, C.Feature.READONLY);
    }

    @Override
    public SequenceBase<T> accept($.Visitor<? super T> visitor) {
        C.forEach(this, visitor);
        return this;
    }

    @Override
    public SequenceBase<T> forEach($.Visitor<? super T> visitor) {
        return accept(visitor);
    }

    @Override
    public C.Sequence<T> each($.Visitor<? super T> visitor) {
        return accept(visitor);
    }

    protected void forEachLeft($.Visitor<? super T> visitor) {
        forEach(visitor);
    }

    @Override
    public T first() throws NoSuchElementException {
        return iterator().next();
    }

    @Override
    public final T head() throws NoSuchElementException {
        return first();
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> acceptLeft($.Visitor<? super T> visitor) {
        forEachLeft(visitor);
        return this;
    }

    /**
     * Delegate to {@link TraversableBase#reduce(Object, org.osgl.Osgl.Func2)}
     * @param identity {@inheritDoc}
     * @param accumulator {@inheritDoc}
     * @param <R> {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <R> R reduceLeft(R identity, $.Func2<R, T, R> accumulator) {
        return reduce(identity, accumulator);
    }

    /**
     * Delegate to {@link TraversableBase#reduce(org.osgl.Osgl.Func2)}
     * @param accumulator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public $.Option<T> reduceLeft($.Func2<T, T, T> accumulator) {
        return reduce(accumulator);
    }

    /**
     * Delegate to {@link TraversableBase#findOne(org.osgl.Osgl.Function)}
     * @param predicate the function map the element to Boolean
     * @return {@inheritDoc}
     */
    @Override
    public $.Option<T> findFirst($.Function<? super T, Boolean> predicate) {
        return findOne(predicate);
    }

    @Override
    public C.Sequence<T> head(int n) {
        if (n == 0) {
            return Nil.seq();
        } else if (n < 0) {
            if (isLimited()) {
                return drop(size() + n);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            if (isLimited() && n >= size()) {
                return this;
            }
            return IndexFilteredSeq.of(this, $.F.lessThan(n));
        }
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        return IndexFilteredSeq.of(this, $.F.greaterThan(0));
    }

    @Override
    public C.Sequence<T> take(int n) {
        return head(n);
    }

    @Override
    public C.Sequence<T> takeWhile($.Function<? super T, Boolean> predicate) {
        return FilteredSeq.of(this, predicate, FilteredIterator.Type.WHILE);
    }

    @Override
    public C.Sequence<T> drop(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (n == 0) {
            return this;
        }
        return IndexFilteredSeq.of(this, $.F.gte(n));
    }

    @Override
    public C.Sequence<T> dropWhile($.Function<? super T, Boolean> predicate) {
        return FilteredSeq.of(this, $.F.negate(predicate), FilteredIterator.Type.UNTIL);
    }

    @Override
    public C.Sequence<T> append(Iterable<? extends T> iterable) {
        return append(C.seq(iterable));
    }

    @Override
    public C.Sequence<T> append(C.Sequence<? extends T> seq) {
        if (seq.isEmpty()) {
            return this;
        }
        return CompositeSeq.of(this, seq);
    }

    @Override
    public C.Sequence<T> append(Iterator<? extends T> iterator) {
        if (!iterator.hasNext()) {
            return this;
        }
        return CompositeSeq.of(this, C.seq(iterator));
    }

    @Override
    public C.Sequence<T> append(Enumeration<? extends T> enumeration) {
        if (!enumeration.hasMoreElements()) {
            return this;
        }
        return CompositeSeq.of(this, C.seq(enumeration));
    }

    @Override
    public C.Sequence<T> append(T t) {
        return CompositeSeq.of(this, $.val(t));
    }

    @Override
    public C.Sequence<T> prepend(Iterable<? extends T> iterable) {
        if (!iterable.iterator().hasNext()) {
            return this;
        }
        return prepend(C.seq(iterable));
    }

    @Override
    public C.Sequence<T> prepend(Iterator<? extends T> iterator) {
        if (!iterator.hasNext()) {
            return this;
        }
        return prepend(C.seq(iterator));
    }

    @Override
    public C.Sequence<T> prepend(Enumeration<? extends T> enumeration) {
        if (!enumeration.hasMoreElements()) {
            return this;
        }
        return prepend(C.seq(enumeration));
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<? extends T> seq) {
        if (seq.isEmpty()) {
            return this;
        }
        C.Sequence<T> casted = $.cast(seq);
        return casted.append(this);
    }

    @Override
    public C.Sequence<T> prepend(T t) {
        return CompositeSeq.of(C.singletonList(t), this);
    }

    @Override
    public C.Sequence<T> filter($.Function<? super T, Boolean> predicate) {
        return FilteredSeq.of(this, predicate);
    }

    @Override
    public <R> C.Sequence<R> map($.Function<? super T, ? extends R> mapper) {
        return MappedSeq.of(this, mapper);
    }

    @Override
    public <R> C.Sequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        return FlatMappedSeq.of(this, mapper);
    }

    @Override
    public <T2> C.Sequence<$.Binary<T, T2>> zip(Iterable<T2> iterable) {
        return new ZippedSeq<T, T2>(this, iterable);
    }

    @Override
    public <T2> C.Sequence<$.Binary<T, T2>> zipAll(Iterable<T2> iterable, T def1, T2 def2) {
        return new ZippedSeq<>(this, iterable, def1, def2);
    }

    @Override
    public C.Sequence<$.Binary<T, Integer>> zipWithIndex() {
        return new ZippedSeq<>(this, new IndexIterable(this));
    }

    @Override
    public int count(T t) {
        return count(this, t);
    }

    public static <T> int count(C.Sequence<T> sequence, T element) {
        int n = 0;
        for (T t : sequence) {
            if ($.eq(t, element)) {
                n++;
            }
        }
        return n;
    }
}
