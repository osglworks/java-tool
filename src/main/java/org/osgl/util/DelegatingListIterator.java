package org.osgl.util;

import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 6/10/13
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
class DelegatingListIterator<T>
extends DelegatingIterator<T> implements ListIterator<T> {

    DelegatingListIterator(ListIterator<T> itr, boolean readOnly) {
        super(itr, readOnly);
    }

    @Override
    protected ListIterator<T> itr() {
        return (ListIterator)super.itr();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPrevious() {
        return itr().hasPrevious();
    }

    @Override
    public T previous() {
        return itr().previous();
    }

    @Override
    public int nextIndex() {
        return itr().nextIndex();
    }

    @Override
    public int previousIndex() {
        return itr().previousIndex();
    }

    @Override
    public void set(T t) {
        mutableOperation();
        itr().set(t);
    }

    @Override
    public void add(T t) {
        mutableOperation();
        itr().add(t);
    }

}
