package org.osgl.util;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexIterable implements Iterable<Integer> {
    private Iterable<?> data;

    IndexIterable(Iterable<?> iterable) {
        E.NPE(iterable);
        data = iterable;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IndexIterator(data.iterator());
    }
}
