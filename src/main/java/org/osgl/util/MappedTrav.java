package org.osgl.util;

import org.osgl._;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 24/10/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
class MappedTrav<T, R> extends TraversableBase<R> {
    private final Iterable<? extends T> data;
    private final _.F1<T, R> mapper_;

    MappedTrav(Iterable<? extends T> iterable, _.Function<? super T, ? extends R> mapper) {
        E.NPE(iterable, mapper);
        data = iterable;
        mapper_ = _.f1(mapper);
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(data.iterator(), mapper_);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>) data).size();
        }
        throw new UnsupportedOperationException();
    }

    public static <T, R> C.Traversable<R> of(Iterable<? extends T> iterable, _.Function<? super T, ? extends R> mapper) {
        return new MappedTrav<T, R>(iterable, mapper);
    }
}
