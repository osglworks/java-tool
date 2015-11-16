package org.osgl.util;

import org.osgl.$;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 4/10/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
class MappedSeq<T, R> extends SequenceBase<R> implements C.Sequence<R> {
    private final Iterable<? extends T> data;
    protected final $.F1<? super T, ? extends R> mapper;

    MappedSeq(Iterable<? extends T> seq, $.Function<? super T, ? extends R> mapper) {
        E.NPE(seq, mapper);
        this.data = seq;
        this.mapper = $.f1(mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>)data).size();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(data.iterator(), mapper);
    }

    public static <T, R> MappedSeq<T, R>
    of(C.Sequence<? extends T> data, $.Function<? super T, ? extends R> mapper) {
        return new MappedSeq<T, R>(data, mapper);
    }

}
