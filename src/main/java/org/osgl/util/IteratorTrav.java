package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * A simple readonly {@link C.Traversable} implementation based on an {@link java.util.Iterator}
 */
class IteratorTrav<T> extends TraversalBase<T> {

    private Iterator<T> itr_;

    private EnumSet<C.Feature> features;

    IteratorTrav(Iterator<T> iterator) {
        E.NPE(iterator);
        itr_ = iterator;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY);
    }

    @Override
    public Iterator<T> iterator() {
        return itr_;
    }

    @Override
    public int size() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> C.Traversable<R> map(_.Function<? super T, ? extends R> mapper) {
        return new IteratorTrav<R>(new MappedIterator<T, R>(itr_, mapper));
    }

    @Override
    public <R> C.Traversable<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        return new IteratorTrav<R>(new FlatMappedIterator<T, R>(itr_, mapper));
    }

    @Override
    public C.Traversable<T> filter(_.Function<? super T, Boolean> predicate) {
        return new IteratorTrav<T>(new FilteredIterator<T>(itr_, predicate));
    }


}
