package org.osgl.util;

import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 27/10/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class CursorBase<T> implements C.List.Cursor<T> {
    private boolean obsoleted_ = false;
    private boolean defined_ = false;
    private T cur_;

    protected final C.List.Cursor<T> setCurrent(T t) {
        cur_ = t;
        defined_ = true;
        return this;
    }

    @Override
    public final boolean isDefined() {
        return defined_;
    }

    @Override
    public final boolean isObsolete() {
        return obsoleted_;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final C.List.Cursor<T> obsolete() {
        obsoleted_ = true;
        cur_ = null;
        defined_ = false;
        return this;
    }

    private void checkState_() {
        if (obsoleted_) {
            throw new IllegalStateException();
        }
    }

    protected abstract int safeIndex();

    @Override
    public int index() {
        checkState_();
        return safeIndex();
    }

    protected abstract boolean safeHasNext();

    @Override
    public boolean hasNext() {
        checkState_();
        return safeHasNext();
    }

    protected abstract boolean safeHasPrevious();

    @Override
    public boolean hasPrevious() {
        checkState_();
        return safeHasPrevious();
    }

    protected abstract T next();

    @Override
    public C.List.Cursor<T> forward() {
        checkState_();
        setCurrent(next());
        return this;
    }

    protected abstract T previous();

    @Override
    public C.List.Cursor<T> backward() {
        checkState_();
        setCurrent(previous());
        return this;
    }

    @Override
    public T get() throws NoSuchElementException {
        if (!defined_) {
            throw new NoSuchElementException();
        }
        return cur_;
    }

    /**
     * Update the current element with new value {@code t}
     * @param t the new value of the current element
     */
    protected abstract void update(T t);

    @Override
    public C.List.Cursor<T> set(T t) throws IndexOutOfBoundsException, NullPointerException {
        checkState_();
        update(t);
        return this;
    }

    protected abstract void remove();

    @Override
    public C.List.Cursor<T> drop() throws NoSuchElementException {
        checkState_();
        remove();
        setCurrent(next());
        return this;
    }

    /**
     * Insert an element before this cursor
     * @param t the element to be inserted
     */
    protected abstract void add(T t);

    @Override
    public C.List.Cursor<T> prepend(T t) throws IndexOutOfBoundsException {
        checkState_();
        previous();
        add(t);
        next();
        return this;
    }

    @Override
    public C.List.Cursor<T> append(T t) {
        checkState_();
        add(t);
        previous();
        return this;
    }

    public String toString() {
        return get() + "@" + index();
    }

}
