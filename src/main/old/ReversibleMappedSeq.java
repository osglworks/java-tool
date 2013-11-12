package org.osgl.util;

import org.osgl._;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/10/13
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReversibleMappedSeq<T, R>
extends MappedSeq<T, R> implements C.ReversibleSequence<R> {

    ReversibleMappedSeq(C.ReversibleSequence<? extends T> seq, _.Function<? super T, ? extends R> mapper) {
        super(seq, mapper);
    }

    protected <T1, R1> ReversibleMappedSeq<T1, R1> of(C.ReversibleSequence<? extends T1> data, _.Function<? super T1, ? extends R1> mapper
    ) {
        return new ReversibleMappedSeq<T1, R1>(data, mapper);
    }

    @SuppressWarnings("unchecked")
    protected C.ReversibleSequence<T> data() {
        return (C.ReversibleSequence<T>)super.data();
    }

    @Override
    public R last() throws UnsupportedOperationException, NoSuchElementException {
        return map(data().last());
    }

    @Override
    public C.ReversibleSequence<R> take(int n)
    throws UnsupportedOperationException {
        return head(n);
    }

    @Override
    public C.ReversibleSequence<R> head(int n) {
        return of(data().head(n), mapper);
    }

    @Override
    public C.ReversibleSequence<R> tail() throws UnsupportedOperationException {
        return of(data().tail(), mapper);
    }

    public C.ReversibleSequence<R> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
        return of(data().tail(n), mapper);
    }

    @Override
    public C.ReversibleSequence<R> reverse() throws UnsupportedOperationException {
        return of(data().reverse(), mapper);
    }

    @Override
    public Iterator<R> reverseIterator() {
        return reverse().iterator();
    }

    @Override
    public <R1> R1 reduceRight(R1 identity, _.Func2<R1, R, R1> accumulator) {
        return reverse().reduceLeft(identity, accumulator);
    }

    @Override
    public _.Option<R> reduceRight(_.Func2<R, R, R> accumulator) {
        return reverse().reduceLeft(accumulator);
    }

    @Override
    public _.Option<R> findLast(_.Function<? super R, Boolean> predicate) {
        return reverse().findFirst(predicate);
    }

    @Override
    public C.ReversibleSequence<R> accept(_.Function<? super R, ?> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public C.ReversibleSequence<R> acceptLeft(_.Function<? super R, ?> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public C.ReversibleSequence<R> acceptRight(_.Function<? super R, ?> visitor) {
        return reverse().acceptLeft(visitor);
    }

    @Override
    public C.ReversibleSequence<R> append(R r) {
        return C.concat(this, C.list(r));
    }

    @Override
    public C.ReversibleSequence<R> append(C.ReversibleSequence<R> seq) {
        return C.concat(this, seq);
    }

    @Override
    public C.ReversibleSequence<R> prepend(R r) {
        return C.concat(C.list(r), this);
    }

    @Override
    public C.ReversibleSequence<R> prepend(C.ReversibleSequence<R> seq) {
        return C.concat(seq, this);
    }

    @Override
    public <R1> C.ReversibleSequence<R1> map(_.Function<? super R, ? extends R1> mapper) {
        return of(this, mapper);
    }

    @Override
    public C.ReversibleSequence<R> takeWhile(_.Function<? super R, Boolean> predicate) {
        int offset = 0;
        Iterator<T> itr = data().iterator();
        while(itr.hasNext()) {
            T t = itr.next();
            if (!predicate.apply(map(t))) {
                break;
            }
            offset++;
        }
        if (offset == 0) {
            return Nil.rseq();
        }
        if (offset >= data().size()) {
            return this;
        }
        return of(data().head(offset), mapper);
    }

    @Override
    public C.ReversibleSequence<R> filter(_.Function<? super R, Boolean> predicate) {
    }
}
