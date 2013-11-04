package org.osgl.util;

import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 27/10/13
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
class CompositeListIterator<T> implements ListIterator<T> {
    private final ListIterator<T> left_;
    private final ListIterator<T> right_;
    private boolean leftOver_;

    CompositeListIterator(ListIterator<T> l1, ListIterator<T> l2) {
        E.NPE(l1, l2);
        left_ = l1;
        right_ = l2;
        leftOver_ = !l1.hasNext();
    }

    @Override
    public boolean hasNext() {
        if (!leftOver_) {
            return true;
        } else {
            return right_.hasNext();
        }
    }

    @Override
    public T next() {
        if (!leftOver_) {
            T t = left_.next();
            leftOver_ = !left_.hasNext();
            return t;
        }
        return right_.next();
    }

    @Override
    public boolean hasPrevious() {
        if (leftOver_) {
            if (right_.hasPrevious()) {
                return true;
            } else {
                return left_.hasPrevious();
            }
        } else {
            return left_.hasPrevious();
        }
    }

    @Override
    public T previous() {
        if (leftOver_) {
            if (right_.hasPrevious()) {
                return right_.previous();
            } else {
                leftOver_ = false;
                return left_.previous();
            }
        } else {
            return left_.previous();
        }
    }

    @Override
    public int nextIndex() {
        if (leftOver_) {
            return left_.nextIndex() + right_.nextIndex();
        }
        return left_.nextIndex();
    }

    @Override
    public int previousIndex() {
        if (leftOver_) {
            return left_.nextIndex() + right_.previousIndex();
        }
        return left_.previousIndex();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException();
    }
}
