package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Adapt an {@link java.util.Iterator} to a {@link C.Sequence}
 */
class IteratorSeq<T> extends SequenceBase<T> {

    private final Iterator<? extends T> itr_;

    IteratorSeq(final Iterator<? extends T> itr) {
        E.NPE(itr);
        itr_ = itr;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY);
    }

    @Override
    public Iterator<T> iterator() {
        return new DelegatingIterator<T>(itr_, true);
    }

    @Override
    public C.Sequence<T> head(int n) {
        return of(Iterators.filterIndex(itr_, N.F.lt(n)));
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        if (isEmpty()) {
            throw new UnsupportedOperationException();
        }
        return of(Iterators.filterIndex(itr_, N.F.gt(0)));
    }

    @Override
    public C.Sequence<T> take(int n) {
        return head(n);
    }

    @Override
    public C.Sequence<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        return of(Iterators.filterWhile(itr_, predicate));
    }

    @Override
    public C.Sequence<T> drop(int n) {
        return of(Iterators.filterIndex(itr_, N.F.gte(n)));
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        return of(Iterators.filterUntil(itr_, _.F.negate(predicate)));
    }

    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        return of(Iterators.composite(itr_, seq.iterator()));
    }

    @Override
    public C.Sequence<T> append(T t) {
        return of(Iterators.composite(itr_, Iterators.of(t)));
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        return of(Iterators.composite(seq.iterator(), itr_));
    }

    @Override
    public C.Sequence<T> prepend(T t) {
        return of(Iterators.composite(Iterators.of(t), itr_));
    }

    @Override
    public C.Sequence<T> filter(_.Function<? super T, Boolean> predicate) {
        return of(Iterators.filter(itr_, predicate));
    }

    @Override
    public int size() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> C.Sequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return new IteratorSeq<R>(Iterators.map(itr_, mapper));
    }

    @Override
    public <R> C.Sequence<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        return new IteratorSeq<R>(Iterators.flatMap(itr_, mapper));
    }

    static <T> IteratorSeq<T> of(Iterator<? extends T> itr) {
        return new IteratorSeq<T>(itr);
    }
}
