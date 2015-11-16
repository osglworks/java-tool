package org.osgl.util;

import org.osgl.$;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 9/11/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
class FlatMappedSeq<T, R> extends SequenceBase<R> {
    private Iterable<? extends T> itr;
    private $.F1<? super T, ? extends Iterable<? extends R>> mapper;

    FlatMappedSeq(Iterable<? extends T> itr, $.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(itr, mapper);
        this.itr = itr;
        this.mapper = $.f1(mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (itr instanceof Collection) {
            return ((Collection)itr).size();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.flatMap(itr.iterator(), mapper);
    }

    static <T, R> C.Sequence<R>
    of(Iterable<? extends T> itr, $.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return new FlatMappedSeq(itr, mapper);
    }
}
