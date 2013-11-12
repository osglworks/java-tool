package org.osgl.util;

import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 6/11/13
 * Time: 8:52 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ReadOnlyListIterator<T> extends ReadOnlyIterator<T> implements ListIterator<T> {
    @Override
    public void set(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException();
    }
}
