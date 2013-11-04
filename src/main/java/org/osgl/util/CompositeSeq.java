package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.osgl.util.C.Feature.READONLY;
import static org.osgl.util.C.Feature.SORTED;

/**
 * Create a view of two sequence combined together
 */
class CompositeSeq<T> implements C.Sequence<T> {
    private final C.Sequence<T> left;
    private final C.Sequence<T> right;
    private EnumSet<C.Feature> features;

    CompositeSeq(C.Sequence<T> left, C.Sequence<T> right) {
        E.NPE(left, right);
        if (left.isEmpty() || right.isEmpty()) {
            throw new IllegalArgumentException("left or right cannot be empty");
        }
        this.left = left;
        this.right = right;
        features = left.features();
        features.retainAll(right.features());
        features.add(READONLY);
        features.remove(SORTED);
    }

    <T> C.Sequence<T> of(C.Sequence<T> left, C.Sequence<T> right) {
        return C.concat(left, right);
    }

    protected C.Sequence<T> left() {
        return left;
    }

    protected C.Sequence<T> right() {
        return right;
    }

    public final EnumSet<C.Feature> features() {
        return EnumSet.copyOf(features);
    }

    @Override
    public final boolean is(C.Feature c) {
        return features.contains(c);
    }

    @Override
    public final int size() throws UnsupportedOperationException {
        return left.size() + right.size();
    }

    @Override
    public final Iterator<T> iterator() {
        final Iterator<T> li = left.iterator();
        final Iterator<T> ri = right.iterator();
        return new Iterator<T>() {
            boolean liEnded = false;
            @Override
            public boolean hasNext() {
                if (liEnded) {
                    return ri.hasNext();
                } else {
                    liEnded = li.hasNext();
                    return liEnded;
                }
            }

            @Override
            public T next() {
                return liEnded ? ri.next() : li.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("CompositeSeq does not support mutable operations");
            }
        };
    }

    @Override
    public CompositeSeq<T> accept(_.Function<? super T, ?> visitor) {
        return acceptLeft(visitor);
    }

    @Override
    public CompositeSeq<T> acceptLeft(_.Function<? super T, ?> visitor) {
        left.accept(visitor);
        right.accept(visitor);
        return this;
    }

    @Override
    public final boolean isEmpty() {
        return left.isEmpty() && right.isEmpty();
    }

    @Override
    public final <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return reduceLeft(identity, accumulator);
    }

    @Override
    public final <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        R r1 = left.reduce(identity, accumulator);
        return right.reduce(r1, accumulator);
    }

    @Override
    public final _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return reduceLeft(accumulator);
    }

    @Override
    public final _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        _.Option<T> o1 = left.reduce(accumulator);
        if (o1.notDefined()) {
            return right.reduce(accumulator);
        }
        T t1 = o1.get();
        return _.some(right.reduce(t1, accumulator));
    }

    @Override
    public final boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return left.allMatch(predicate) && right.allMatch(predicate);
    }

    @Override
    public final boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return left.anyMatch(predicate) || right.anyMatch(predicate);
    }

    @Override
    public final boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return left.noneMatch(predicate) && right.noneMatch(predicate);
    }

    @Override
    public final _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        return findFirst(predicate);
    }

    @Override
    public final _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        _.Option<T> o1 = left.findOne(predicate);
        if (o1.isDefined()) {
            return o1;
        }
        return right.findOne(predicate);
    }

    @Override
    public final T first() {
        return iterator().next();
    }

    @Override
    public final T head() throws NoSuchElementException {
        return first();
    }

    @Override
    public C.Sequence<T> head(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be positive int");
        } else if (n == 0) {
            return Nil.seq();
        } else {
            int size = left.size();
            if (size >= n) {
                return left.head(n);
            } else {
                return of(left, right.head(n - size));
            }
        }
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        if (left.size() == 1) {
            return right;
        } else {
            return of(left.tail(), right);
        }
    }

    @Override
    public C.Sequence<T> take(int n) throws UnsupportedOperationException {
        return head(n);
    }

    @Override
    public C.Sequence<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        C.Sequence<T> l1 = left.takeWhile(predicate);
        if (l1.size() < left.size()) {
            return of(l1, right.takeWhile(predicate));
        } else {
            return l1;
        }
    }

    @Override
    public C.Sequence<T> drop(int n) {
        int size = left.size();
        if (n < size) {
            return of(left.drop(n), right);
        } else {
            return right.drop(n - size);
        }
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        C.Sequence<T> l1 = left.dropWhile(predicate);
        if (l1.isEmpty()) {
            return right.dropWhile(predicate);
        } else {
            return of(l1, right);
        }
    }

    @Override
    public <R> C.Sequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return of(left.map(mapper), right.map(mapper));
    }

    @Override
    public <R> C.Sequence<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return of(left.flatMap(mapper), right.flatMap(mapper));
    }

    @Override
    public C.Sequence<T> filter(_.Function<? super T, Boolean> predicate) {
        return of(left.filter(predicate), right.filter(predicate));
    }

    @Override
    public C.Sequence<T> append(final T t) {
        return C.append(this, t);
    }

    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        return of(this, seq);
    }

    @Override
    public C.Sequence<T> prepend(T t) {
        return C.prepend(t, this);
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        return of(seq, this);
    }
}
