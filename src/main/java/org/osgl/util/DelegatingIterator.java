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
    private final Iterator<T> itr;
    protected final boolean readOnly;

    DelegatingIterator(Iterator<T> itr, boolean readOnly) {
        this.itr = itr;
        this.readOnly = readOnly;
    }

    protected Iterator<T> itr() {
        return itr;
    }

    protected final void mutableOperation() {
        if (readOnly) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }

    @Override
    public T next() {
        return itr.next();
    }

    @Override
    public void remove() {
        mutableOperation();
        itr.remove();
    }
}
