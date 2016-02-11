package org.osgl.util;

import java.util.Collection;
import java.util.Iterator;

class IterableSeq<T> extends SequenceBase<T> {
    private Iterable<? extends T> data;

    IterableSeq(Iterable<? extends T> iterable) {
        data = iterable;
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>)data).size();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return DelegatingIterator.of(data.iterator(), is(C.Feature.READONLY));
    }

    @SuppressWarnings("unchecked")
    public static <T> C.Sequence<T> of(Iterable<? extends T> iterable) {
        if (iterable instanceof C.Sequence) {
            return (C.Sequence<T>)iterable;
        }
        return new IterableSeq<T>(iterable);
    }
}
