package org.osgl.util;

import org.osgl.$;

import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class StatefulIterator<T> extends ReadOnlyIterator<T> {

    private $.Option<T> current = $.none();

    /**
     * If there are still elements, then return the an option describing the next element,
     * otherwise return {@link $.Option#NONE}
     *
     * @return either next element or none if no element in the iterator
     */
    protected abstract $.Option<T> getCurrent();

    public boolean hasNext() {
        if (current.isDefined()) {
            return true;
        }
        current = getCurrent();
        return current.isDefined();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T ret = current.get();
        current = $.none();
        return ret;
    }

}
