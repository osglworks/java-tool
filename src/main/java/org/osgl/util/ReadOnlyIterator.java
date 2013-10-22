package org.osgl.util;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 8:32 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ReadOnlyIterator<T> implements Iterator<T> {
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
