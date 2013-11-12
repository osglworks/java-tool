package org.osgl.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 26/10/13
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
class ListIteratorCursor<T> extends CursorBase<T> implements C.List.Cursor<T> {
    private final ListIterator<T> itr_;
    private boolean parked = false;
    ListIteratorCursor(ListIterator<T> itr) {
        E.NPE(itr);
        itr_ = itr;
    }

    @Override
    public T next() {
        return itr_.next();
    }

    @Override
    public T previous() {
        return itr_.previous();
    }

    @Override
    public void update(T t) throws IndexOutOfBoundsException, NullPointerException {
        itr_.previous();
        itr_.set(t);
    }

    @Override
    public void remove() throws NoSuchElementException {
        itr_.remove();
    }

    @Override
    public void add(T t) throws IndexOutOfBoundsException {
        itr_.add(t);
    }

    @Override
    public int index() {
        return itr_.previousIndex();
    }

    @Override
    public boolean hasNext() {
        return itr_.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return itr_.hasPrevious();
    }

    @Override
    public C.List.Cursor<T> parkLeft() {
        while (itr_.hasPrevious()) {
            itr_.previous();
        }
        parked = true;
        return this;
    }

    @Override
    public C.List.Cursor<T> parkRight() {
        while (itr_.hasNext()) {
            itr_.nextIndex();
        }
        parked = true;
        return this;
    }
}
