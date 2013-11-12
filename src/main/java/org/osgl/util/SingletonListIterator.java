package org.osgl.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/10/13
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingletonListIterator<T> implements ListIterator<T> {
    private final T t_;
    private volatile boolean consumed_;

    public SingletonListIterator(T t) {
        t_ = t;
    }

    @Override
    public boolean hasNext() {
        return !consumed_;
    }

    @Override
    public boolean hasPrevious() {
        return consumed_;
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
    public T previous() {
        if (!consumed_) {
            throw new NoSuchElementException();
        }
        consumed_ = false;
        return t_;
    }

    @Override
    public int nextIndex() {
        return consumed_ ? 1 : 0;
    }

    @Override
    public int previousIndex() {
        return consumed_ ? 0 : -1;
    }

    @Override
    public void set(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
