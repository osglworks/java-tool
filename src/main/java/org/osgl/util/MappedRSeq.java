package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 4/10/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
class MappedRSeq<T, R> extends ReversibleSeqBase<R> implements C.ReversibleSequence<R> {
    private final C.ReversibleSequence<? extends T> data;
    protected final _.F1<? super T, ? extends R> mapper;

    MappedRSeq(C.ReversibleSequence<? extends T> seq, _.Function<? super T, ? extends R> mapper) {
        E.NPE(seq, mapper);
        this.data = seq;
        this.mapper = _.f1(mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data.size();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(data.iterator(), mapper);
    }

    @Override
    public Iterator<R> reverseIterator() {
        return Iterators.map(data.reverseIterator(), mapper);
    }

    public static <T, R> MappedRSeq<T, R>
    of(C.ReversibleSequence<? extends T> data, _.Function<? super T, ? extends R> mapper) {
        return new MappedRSeq<T, R>(data, mapper);
    }

}
