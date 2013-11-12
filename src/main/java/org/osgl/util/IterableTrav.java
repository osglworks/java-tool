package org.osgl.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * A {@link C.Traversable} implementation based on an {@link Iterable}
 */
class IterableTrav<T> extends TraversableBase<T> {

    private final Iterable<? extends T> data;

    IterableTrav(Iterable<? extends T> iterable) {
        E.NPE(iterable);
        data = iterable;
    }

    @Override
    public Iterator<T> iterator() {
        return DelegatingIterator.of(data.iterator(), is(C.Feature.READONLY));
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>) data).size();
        }
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public static <T> C.Traversable<T> of(Iterable<? extends T> iterable) {
        if (iterable instanceof C.Traversable) {
            return (C.Traversable<T>)iterable;
        }
        return new IterableTrav<T>(iterable);
    }
}
