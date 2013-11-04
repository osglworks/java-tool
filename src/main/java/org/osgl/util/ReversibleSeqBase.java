package org.osgl.util;

import org.osgl._;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 27/10/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class ReversibleSeqBase<T> extends SequenceBase<T> implements C.ReversibleSequence<T> {

    @Override
    public T last() throws UnsupportedOperationException, NoSuchElementException {
        return reverseIterator().next();
    }

    @Override
    public C.ReversibleSequence<T> accept(_.Function<? super T, ?> visitor) {
        return _.cast(super.accept(visitor));
    }

    @Override
    public C.ReversibleSequence<T> acceptLeft(_.Function<? super T, ?> visitor) {
        return _.cast(super.acceptLeft(visitor));
    }

    protected void forEachRight(_.Function<? super T, ?> visitor) {
        C.forEach(reverseIterator(), visitor);
    }

    @Override
    public C.ReversibleSequence<T> acceptRight(_.Function<? super T, ?> visitor) {
        forEachRight(visitor);
        return this;
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        Iterator<T> itr = reverseIterator();
        R ret = identity;
        while (itr.hasNext()) {
            ret = accumulator.apply(ret, itr.next());
        }
        return ret;
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        Iterator<T> itr = reverseIterator();
        if (!itr.hasNext()) {
            return _.none();
        }
        T ret = itr.next();
        while (itr.hasNext()) {
            ret = accumulator.apply(ret, itr.next());
        }
        return _.some(ret);
    }
}
