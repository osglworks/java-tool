package org.osgl.util;

import org.osgl._;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 9/11/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
class FlatMappedTrav<T, R> extends TraversableBase<R> {
    private Iterable<? extends T> data;
    private _.F1<? super T, ? extends Iterable<? extends R>> mapper;

    FlatMappedTrav(Iterable<? extends T> itr, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(itr, mapper);
        this.data = itr;
        this.mapper = _.f1(mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection) data).size();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.flatMap(data.iterator(), mapper);
    }

    public static <T, R> C.Traversable<R> of(Iterable<? extends T> itr, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return new FlatMappedTrav<T, R>(itr, mapper);
    }
}
