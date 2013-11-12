package org.osgl.util;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexIterator implements Iterator<Integer> {
    private Iterator<?> itr;
    int cursor = 0;

    IndexIterator(Iterator<?> itr) {
        E.NPE(itr);
        this.itr = itr;
    }

    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }

    @Override
    public Integer next() {
        itr.next();
        return cursor++;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
