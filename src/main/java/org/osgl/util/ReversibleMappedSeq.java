package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/11/13
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
class ReversibleMappedSeq<T, R> extends ReversibleSeqBase<R> implements C.ReversibleSequence<R> {
    private final C.ReversibleSequence<? extends T> data;
    protected final _.F1<? super T, ? extends R> mapper;

    ReversibleMappedSeq(C.ReversibleSequence<? extends T> seq, _.Function<? super T, ? extends R> mapper) {
        E.NPE(seq, mapper);
        this.data = seq;
        this.mapper = _.f1(mapper);
    }

    @Override
    public Iterator<R> reverseIterator() {
        return Iterators.map(data.reverseIterator(), mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data.size();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(data.iterator(), mapper);
    }
}
