package org.osgl.util;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 19/11/13
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
class ReadOnlyDelegatingList<T> extends DelegatingList<T> {
    ReadOnlyDelegatingList(Iterable<T> iterable) {
        super(iterable);
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> es = super.initFeatures();
        es.add(C.Feature.READONLY);
        return es;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
}
