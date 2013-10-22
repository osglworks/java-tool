package org.osgl.util;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
class SubSeq<T> extends SequenceBase<T> implements C.Sequence<T> {

    private final C.Sequence<T> data;

    private final int from;

    private final int to;

    SubSeq(C.Sequence<T> data, int from, int to) {
        E.NPE(data);
        if (to >= 0 && from >= to) {
            throw new IllegalArgumentException("from should be less than to");
        }
        if (from < 0) {
            throw new IllegalArgumentException("from should be at least zero");
        }
        try {
            if (to > size()) {
                to = -1;
            }
        } catch (Exception e) {
            // ignore the case when some lazy sequence does not support size()
        }
        this.data = data;
        this.from = from;
        this.to = to;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> itr0 = data.iterator();
        final int max = to < 0 ? Integer.MAX_VALUE : to;
        return new Iterator<T>() {
            int id = 0;
            @Override
            public boolean hasNext() {
                if (id >= max) {
                    return false;
                }
                return itr0.hasNext();
            }

            @Override
            public T next() {
                T t = null;
                while(id < from) {
                    t = itr0.next();
                    id++;
                }
                t = itr0.next();
                id++;
                if (id > to) {
                    throw new NoSuchElementException();
                }
                return t;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static EnumSet<C.Feature> featuresOf(C.Featured featured) {
        EnumSet<C.Feature> f = featured.features();
        f.add(C.Feature.READONLY);
        return f;
    }
}
