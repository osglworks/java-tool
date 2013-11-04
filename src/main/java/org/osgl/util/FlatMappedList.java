package org.osgl.util;

import org.osgl._;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 10:21 PM
 * To change this template use File | Settings | File Templates.
 */
class FlatMappedList<T, R> extends ListBase1<R> implements C.List<R> {
    private final C.List<T> l_;
    private final _.Function<? super T, ? extends Iterable<? extends R>> m_;

    FlatMappedList(C.List<T> list, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(list, mapper);
        l_ = list;
        m_ = mapper;
    }
}
