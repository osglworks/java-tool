package org.osgl.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/10/13
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
class SingletonItr<T> implements Iterator<T> {
    private final T t_;
    private volatile boolean consumed_;

    SingletonItr(T t) {
        t_ = t;
    }

    @Override
    public boolean hasNext() {
        return !consumed_;
    }

    @Override
    public T next() {
        if (consumed_) {
            throw new NoSuchElementException();
        }
        consumed_ = true;
        return t_;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
