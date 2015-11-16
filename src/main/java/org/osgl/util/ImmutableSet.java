package org.osgl.util;

import org.osgl.$;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Created by luog on 2/02/14.
 */
class ImmutableSet<T> extends DelegatingSet<T> {
    ImmutableSet(Collection<? extends T> collection) {
        super(collection, true);
    }

    @Override
    public boolean add(T t) {
        throw E.unsupport();
    }

    @Override
    public boolean remove(Object o) {
        throw E.unsupport();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw E.unsupport();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw E.unsupport();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw E.unsupport();
    }

    @Override
    public void clear() {
        throw E.unsupport();
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = super.initFeatures();
        fs.add(C.Feature.IMMUTABLE);
        fs.add(C.Feature.READONLY);
        return fs;
    }

    static <T> C.Set<T> of(T[] data) {
        E.NPE(data);
        int len = data.length;
        if (len == 0) {
            return Nil.set();
        } else if (len == 1) {
            return $.val(data[0]);
        } else {
            return new ImmutableSet<T>(C.listOf(data));
        }
    }

    static <T> C.Set<T> of(Collection<? extends T> data) {
        if (data instanceof C.Set) {
            C.Set<T> set = (C.Set<T>)data;
            if (set.is(C.Feature.IMMUTABLE)) {
                return set;
            }
        }
        return new ImmutableSet<T>(data);
    }

}
