package org.osgl.util;

import org.osgl._;

import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/11/13
 * Time: 6:41 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class ReversibleSeqBase<T> extends SequenceBase<T> implements C.ReversibleSequence<T> {
    @Override
    public C.ReversibleSequence<T> lazy() {
        super.lazy();
        return this;
    }

    @Override
    public C.ReversibleSequence<T> eager() {
        super.eager();
        return this;
    }

    @Override
    public C.ReversibleSequence<T> parallel() {
        super.parallel();
        return this;
    }

    @Override
    public C.ReversibleSequence<T> sequential() {
        super.sequential();
        return this;
    }

    @Override
    public ReversibleSeqBase<T> accept(_.Function<? super T, ?> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public ReversibleSeqBase<T> each(_.Function<? super T, ?> visitor) {
        return accept(visitor);
    }

    @Override
    public ReversibleSeqBase<T> forEach(_.Function<? super T, ?> visitor) {
        return accept(visitor);
    }

    @Override
    public C.ReversibleSequence<T> acceptLeft(_.Function<? super T, ?> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public C.ReversibleSequence<T> head(int n) {
        if (n == 0) {
            return Nil.rseq();
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
            return IndexFilteredRSeq.of(this, _.F.lessThan(n));
        }
    }

    @Override
    public C.ReversibleSequence<T> tail() throws UnsupportedOperationException {
        return IndexFilteredRSeq.of(this, _.F.greaterThan(0));
    }

    @Override
    public C.ReversibleSequence<T> take(int n) {
        return head(n);
    }

    @Override
    public C.ReversibleSequence<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        return FilteredRSeq.of(this, predicate, FilteredIterator.Type.WHILE);
    }

    @Override
    public C.ReversibleSequence<T> drop(int n) throws IllegalArgumentException {
        int sz = size();
        if (n < 0) {
            n = -n;
            if (n >= sz) return Nil.rseq();
            return take(sz - n);
        }
        if (n == 0) {
            return this;
        }
        return IndexFilteredRSeq.of(this, _.F.gte(n));
    }

    @Override
    public C.ReversibleSequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        return FilteredRSeq.of(this, _.F.negate(predicate), FilteredIterator.Type.UNTIL);
    }

    @Override
    public C.ReversibleSequence<T> append(T t) {
        return CompositeRSeq.of(this, _.val(t));
    }

    @Override
    public C.ReversibleSequence<T> prepend(T t) {
        return CompositeRSeq.of(_.val(t), this);
    }

    @Override
    public C.ReversibleSequence<T> filter(_.Function<? super T, Boolean> predicate) {
        return FilteredRSeq.of(this, predicate);
    }

    @Override
    public <R> C.ReversibleSequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return MappedRSeq.of(this, mapper);
    }

    @Override
    public <R> C.ReversibleSequence<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return FlatMappedRSeq.of(this, mapper);
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        if (seq.isEmpty()) {
            return this;
        }
        return CompositeRSeq.of(this, seq);
    }

    @Override
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        if (seq.isEmpty()) {
            return this;
        }
        return CompositeRSeq.of(seq, this);
    }

    @Override
    public T last() throws UnsupportedOperationException, NoSuchElementException {
        return reverseIterator().next();
    }

    @Override
    public C.ReversibleSequence<T> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
        boolean immutable = isImmutable();
        int sz = size();
        if (n < 0) {
            return head(-n);
        } else if (n == 0) {
            return Nil.rseq();
        } else if (n >= sz) {
            return this;
        }
        return reverse().take(n).reverse();
    }

    @Override
    public C.ReversibleSequence<T> reverse() throws UnsupportedOperationException {
        if (isEmpty()) {
            return Nil.rseq();
        }
        return ReversedRSeq.of(this);
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        return reverse().reduceLeft(identity, accumulator);
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return reverse().reduceLeft(accumulator);
    }

    @Override
    public _.Option<T> findLast(_.Function<? super T, Boolean> predicate) {
        return reverse().findFirst(predicate);
    }

    @Override
    public C.ReversibleSequence<T> acceptRight(_.Function<? super T, ?> visitor) {
        return reverse().acceptLeft(visitor);
    }

    @Override
    public <T2> C.ReversibleSequence<_.T2<T, T2>> zip(C.ReversibleSequence<T2> rseq) {
        return new ZippedRSeq<T, T2>(this, rseq);
    }

    @Override
    public <T2> C.ReversibleSequence<_.T2<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, T def1, T2 def2) {
        return new ZippedRSeq<T, T2>(this, rseq, def1, def2);
    }
}
