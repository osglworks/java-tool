package org.osgl.util;

import org.osgl._;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/10/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
class CompositeReversibleSeq<T>
extends CompositeSeq<T>
implements C.ReversibleSequence<T> {

    CompositeReversibleSeq(
            C.ReversibleSequence<T> left,
            C.ReversibleSequence<T> right
    ) {
        super(left, right);
    }

    <T1> CompositeReversibleSeq<T1> of(C.ReversibleSequence<T1> left, C.ReversibleSequence<T1> right) {
        return new CompositeReversibleSeq<T1>(left, right);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C.ReversibleSequence<T> left() {
        return _.cast(super.left());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected C.ReversibleSequence<T> right() {
        return _.cast(super.right());
    }

    @Override
    public C.ReversibleSequence<T> head(int n) {
        return _.cast(super.head(n));
    }

    @Override
    public C.ReversibleSequence<T> take(int n) throws UnsupportedOperationException {
        return _.cast(super.take(n));
    }

    @Override
    public C.ReversibleSequence<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        return _.cast(super.takeWhile(predicate));
    }

    @Override
    public C.ReversibleSequence<T> tail() throws UnsupportedOperationException {
        return _.cast(super.tail());
    }

    @Override
    public C.ReversibleSequence<T> drop(int n) {
        return _.cast(super.drop(n));
    }

    @Override
    public C.ReversibleSequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        return _.cast(super.dropWhile(predicate));
    }

    @Override
    public <R> C.ReversibleSequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return _.cast(super.map(mapper));
    }

    @Override
    public C.ReversibleSequence<T> filter(_.Function<? super T, Boolean> predicate) {
        return _.cast(super.filter(predicate));
    }

    @Override
    public CompositeReversibleSeq<T> accept(_.Function<? super T, ?> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public CompositeReversibleSeq<T> acceptLeft(_.Function<? super T, ?> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public C.ReversibleSequence<T> append(T t) {
        return _.cast(super.append(t));
    }

    @Override
    public C.ReversibleSequence<T> prepend(T t) {
        return _.cast(super.prepend(t));
    }

    @Override
    public final T last() throws UnsupportedOperationException, NoSuchElementException {
        return right().isEmpty() ? left().last() : right().last();
    }

    @Override
    public C.ReversibleSequence<T> reverse() throws UnsupportedOperationException {
        return of(right().reverse(), left().reverse());
    }

    @Override
    public C.ReversibleSequence<T> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
        int size = right().size();
        if (size < n) {
            return of(left().tail(n - size), right());
        } else if (size == n) {
            return right();
        } else {
            return right().tail(n);
        }
    }

    @Override
    public final Iterator<T> reverseIterator() {
        return reverse().iterator();
    }

    @Override
    public final <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        return reverse().reduceLeft(identity, accumulator);
    }

    @Override
    public final _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return reverse().reduceLeft(accumulator);
    }

    @Override
    public C.ReversibleSequence<T> acceptRight(_.Function<? super T, ?> visitor) {
        return reverse().acceptLeft(visitor);
    }

    @Override
    public final _.Option<T> findLast(_.Function<? super T, Boolean> predicate) {
        return reverse().findFirst(predicate);
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        return C.concat(this, seq);
    }

    @Override
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        return C.concat(seq, this);
    }

}
