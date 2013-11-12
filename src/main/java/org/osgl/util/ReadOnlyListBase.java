package org.osgl.util;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 6/11/13
 * Time: 8:29 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ReadOnlyListBase<T> extends ListBase<T> implements C.List<T> {

    protected abstract EnumSet<C.Feature> internalInitFeatures();

    @Override
    protected final EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = internalInitFeatures();
        fs.add(C.Feature.READONLY);
        return fs;
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
