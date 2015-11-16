package org.osgl.util;

import org.osgl.$;

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
    private final $.Function<? super T, ? extends Iterable<? extends R>> mapper;
    private Iterator<? extends R> curMapped = null;

    FlatMappedIterator(Iterator<? extends T> itr, $.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(itr, mapper);
        this.data = itr;
        this.mapper = mapper;
    }

    protected Iterator<? extends T> data() {return data;}

    protected $.Function<? super T, ? extends Iterable<? extends R>> mapper() {return mapper;}

    @Override
    protected $.Option<R> getCurrent() {
        while (null == curMapped || !curMapped.hasNext()) {
            if (!data.hasNext()) {
                return $.none();
            }
            curMapped = mapper.apply(data.next()).iterator();
        }
        return $.some((R)curMapped.next());
    }
}
