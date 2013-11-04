package org.osgl.util;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 6/10/13
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class DelegatingIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> itr_;
    protected final boolean readOnly;

    DelegatingIterator(Iterator<? extends T> itr, boolean readOnly) {
        this.itr_ = itr;
        this.readOnly = readOnly;
    }

    protected Iterator<? extends T> itr() {
        return itr_;
    }

    protected final void mutableOperation() {
        if (readOnly) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasNext() {
        return itr_.hasNext();
    }

    @Override
    public T next() {
        return itr_.next();
    }

    @Override
    public void remove() {
        mutableOperation();
        itr_.remove();
    }
}
