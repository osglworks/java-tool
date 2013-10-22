package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractSeq<T>
extends SequenceBase<T, C.Sequence<T>>
implements C.Sequence<T>
 {

    private EnumSet<C.Feature> features;

    AbstractSeq(EnumSet<C.Feature> features) {
        this.features = features.clone();
    }

    protected C.Sequence<T> subSeq(int from, int to) {
        return _.cast(new SubSeq<T>(this, from, to));
    }

    protected C.Sequence<T> toLazy() {
        return IteratorSeq.of(iterator());
    }

    public abstract Iterator<T> iterator();

    @Override
    public EnumSet<C.Feature> features() {
        return features.clone();
    }

    @Override
    public boolean is(C.Feature c) {
        return features.contains(c);
    }

    @Override
    public boolean isEmpty() {
        return iterator().hasNext();
    }

    @Override
    public int size() throws UnsupportedOperationException {
        _.F2<Integer, T, Integer> counter = N.F.counter();
        return reduceLeft(0, counter);
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return findOne(predicate).isDefined();
    }

    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(_.F.negate(predicate));
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        try {
            accept(_.F.breakIf(predicate));
            return _.none();
        } catch (_.Break b) {
            T t = b.get();
            return _.some(t);
        }
    }

    @Override
    public T first() {
        return iterator().next();
    }

    @Override
    public final T head() throws NoSuchElementException {
        return first();
    }

    @Override
    public C.Sequence<T> head(int n) {
        return subSeq(0, n);
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        return subSeq(1, -1);
    }

    @Override
    public C.Sequence<T> take(int n) {
        return head(n);
    }

    @Override
    public C.Sequence<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        return toLazy().takeWhile(predicate);
    }

    @Override
    public C.Sequence<T> drop(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n should be negative number");
        }
        if (n == 0) {
            return this;
        }
        return subSeq(n, -1);
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        return toLazy().dropWhile(predicate);
    }

    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        return C.concat(this, seq);
    }

    @Override
    public C.Sequence<T> append(T t) {
        return C.append(this, t);
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        return C.concat(seq, this);
    }

    @Override
    public C.Sequence<T> prepend(T t) {
        return C.prepend(t, this);
    }

    @Override
    public <R> C.Sequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return C.map(this, mapper);
    }

    @Override
    public <R> C.Sequence<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        return IteratorSeq.of(new FlatMappedIterator<T, R>(iterator(), mapper));
    }

    @Override
    public C.Sequence<T> filter(_.Function<? super T, Boolean> predicate) {
        return _.cast(C.filter(this, predicate));
    }

    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return reduceLeft(identity, accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        R ret = identity;
        for (T t : this) {
            ret = accumulator.apply(ret, t);
        }
        return ret;
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return reduceLeft(accumulator);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        Iterator<T> itr = iterator();
        if (!itr.hasNext()) {
            return _.none();
        }
        T t = itr.next();
        while (itr.hasNext()) {
            t = accumulator.apply(t, itr.next());
        }
        return _.some(t);
    }

    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        try {
            accept(_.F.breakIf(predicate));
            return _.none();
        } catch (_.Break b) {
            T t = b.get();
            return _.some(t);
        }
    }

}
