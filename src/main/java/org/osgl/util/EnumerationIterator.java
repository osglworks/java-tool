package org.osgl.util;

import org.osgl.$;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Bridge from {@link java.util.Enumeration} to {@link java.util.Iterator}
 */
class EnumerationIterator<T> implements Iterator<T> {
    private Enumeration<? extends T> e;

    EnumerationIterator(Enumeration<? extends T> enumeration) {
        e = $.notNull(enumeration);
    }

    @Override
    public boolean hasNext() {
        return e.hasMoreElements();
    }

    @Override
    public T next() {
        return e.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
