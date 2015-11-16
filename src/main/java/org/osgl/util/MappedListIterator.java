package org.osgl.util;

import org.osgl.$;

import java.util.ListIterator;

/**
 * Created by luog on 13/12/13.
 */
class MappedListIterator<T, R> extends MappedIterator<T, R> implements ListIterator<R> {

    MappedListIterator
    (ListIterator<? extends T> itr, $.Function<? super T, ? extends R> mapper) {
        super(itr, mapper);
    }

    @Override
    protected ListIterator<? extends T> data() {
        return (ListIterator<? extends T>)super.data();
    }

    @Override
    public boolean hasPrevious() {
        return data().hasPrevious();
    }

    @Override
    public R previous() {
        return mapper().apply(data().previous());
    }

    @Override
    public int nextIndex() {
        return data().nextIndex();
    }

    @Override
    public int previousIndex() {
        return data().previousIndex();
    }

    @Override
    public void set(R r) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(R r) {
        throw new UnsupportedOperationException();
    }
}
