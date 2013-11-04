package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * A {@link C.Traversable} implementation based on an {@link Iterable}
 */
class IterableTrav<T> extends TraversalBase<T> {
    private final Iterable<T> itrb_;
    IterableTrav(Iterable<T> iterable) {
        E.NPE(iterable);
        itrb_ = iterable;
    }

    @Override
    public Iterator<T> iterator() {
        return itrb_.iterator();
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return reduce(1, N.F.<T>counter());
    }

    @Override
    public <R> C.Traversable<R> map(_.Function<? super T, ? extends R> mapper) {
        return Traversals.map(itrb_, mapper);
    }

    @Override
    public <R> C.Traversable<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        //TODO ...
        return null;
    }

    @Override
    public C.Traversable<T> filter(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }
}
