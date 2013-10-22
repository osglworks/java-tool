package org.osgl.util;

import org.osgl._;

import java.util.*;

/**
 */
class SingletonList<T> extends AbstractList<T> implements C.List<T> {

    private final T data;
    private static EnumSet<C.Feature> features = EnumSet.of(
        C.Feature.IMMUTABLE,
        C.Feature.ORDERED,
        C.Feature.LIMITED,
        C.Feature.RANDOM_ACCESS);

    SingletonList(T t) {
        data = t;
    }

    @Override
    public EnumSet<C.Feature> features() {
        return EnumSet.copyOf(features);
    }

    @Override
    public boolean is(C.Feature c) {
        return features.contains(c);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public T get(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return data;
    }

    @Override
    public boolean contains(Object o) {
        return _.eq(data, o);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
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
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return anyMatch(predicate);
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return predicate.apply(data);
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        return anyMatch(predicate) ? _.some(data) : _.NONE;
    }

    @Override
    public T first() {
        return data;
    }

    @Override
    public T head() throws NoSuchElementException {
        return data;
    }

    @Override
    public C.Sequence<T> takeWhile(_.Function<T, Boolean> predicate) {
        if (anyMatch(predicate)) {
            return this;
        }
        return C.list();
    }

    @Override
    public C.Sequence<T> drop(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n should be positive int");
        }
        if (0 == n) {
            return this;
        } else {
            return C.list();
        }
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<T, Boolean> predicate) {
        if (predicate.apply(data)) {
            return C.list();
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> append(C.Sequence<T> seq) {
        if (seq instanceof C.ReversibleSequence) {
            return append((C.ReversibleSequence) seq);
        }
        return C.concat(this, seq);
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        if (seq instanceof C.ReversibleSequence) {
            return prepend((C.ReversibleSequence) seq);
        }
        return C.concat(seq, this);
    }

    @Override
    public <R> C.ReversibleSequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return C.list(mapper.apply(data));
    }

    @Override
    public <R> C.Sequence<R> flatMap(_.Function<? super T, Iterable<R>> mapper) {
        return C.listOf(mapper.apply(data));
    }

    @Override
    public C.Sequence<T> filter(_.Function<? super T, Boolean> predicate) {
        if (predicate.apply(data)) {
            return this;
        }
        return Nil.list();
    }

    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return accumulator.apply(identity, data);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        return reduce(identity, accumulator);
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return _.some(data);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        return reduce(accumulator);
    }

    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        if (predicate.apply(data)) {
            return _.some(data);
        }
        return _.none();
    }

    @Override
    public C.ReversibleSequence<T> head(int n) {
        if (0 == n) {
            return Nil.list();
        }
        return this;
    }

    @Override
    public C.ReversibleSequence<T> tail() {
        return Nil.list();
    }

    @Override
    public C.ReversibleSequence<T> take(int n) {
        return head(n);
    }

    @Override
    public C.ReversibleSequence<T> append(T t) {
        return C.concat(this, C.list(t));
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        return C.concat(this, seq);
    }

    @Override
    public C.ReversibleSequence<T> prepend(T t) {
        return C.concat(C.list(t), this);
    }

    @Override
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        return C.concat(seq, this);
    }

    @Override
    public T last() throws UnsupportedOperationException, NoSuchElementException {
        return data;
    }

    @Override
    public C.ReversibleSequence<T> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
        if (0 == n) {
            return Nil.list();
        }
        return this;
    }

    @Override
    public C.ReversibleSequence<T> reverse() throws UnsupportedOperationException {
        return this;
    }

    @Override
    public Iterator<T> reverseIterator() {
        return iterator();
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        return reduce(identity, accumulator);
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return reduce(accumulator);
    }

    @Override
    public _.Option<T> findLast(_.Function<? super T, Boolean> predicate) {
        T data = this.data;
        if (predicate.apply(data)) {
            return _.some(data);
        }
        return _.none();
    }

    @Override
    public C.ReversibleSequence<T> accept(_.Function<? super T, ?> visitor) {
        visitor.apply(data);
        return this;
    }

    @Override
    public C.ReversibleSequence<T> acceptLeft(_.Function<? super T, ?> visitor) {
        return accept(visitor);
    }

    @Override
    public C.ReversibleSequence<T> acceptRight(_.Function<? super T, ?> visitor) {
        return accept(visitor);
    }
}

