package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
class FlatMappedIterator<T, R> extends StatefulIterator<R> {
    private final Iterator<? extends T> data;
    private final _.Function<? super T, ? extends Iterable<? extends R>> mapper;
    private Iterator<? extends R> curMapped = null;

    FlatMappedIterator(Iterator<? extends T> itr, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(itr, mapper);
        this.data = itr;
        this.mapper = mapper;
    }

    @Override
    protected _.Option<R> getCurrent() {
        while (null == curMapped || !curMapped.hasNext()) {
            if (!data.hasNext()) {
                return _.none();
            }
            curMapped = mapper.apply(data.next()).iterator();
        }
        return _.some((R)curMapped.next());
    }
}
