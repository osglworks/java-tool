package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 9/11/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
class FlatMappedRSeq<T, R> extends ReversibleSeqBase<R> {
    private C.ReversibleSequence<? extends T> data;
    private _.F1<? super T, ? extends Iterable<? extends R>> mapper;

    FlatMappedRSeq(C.ReversibleSequence<? extends T> rseq, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(rseq, mapper);
        this.data = rseq;
        this.mapper = _.f1(mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data.size();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.flatMap(data.iterator(), mapper);
    }

    @Override
    public Iterator<R> reverseIterator() {
        return Iterators.flatMap(data.reverseIterator(), mapper);
    }

    static <T, R> C.ReversibleSequence<R>
    of(C.ReversibleSequence<? extends T> data, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return new FlatMappedRSeq<T, R>(data, mapper);
    }
}
