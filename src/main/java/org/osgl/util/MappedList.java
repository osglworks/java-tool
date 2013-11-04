package org.osgl.util;

import org.osgl._;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class MappedList<T, R> extends ListBase1<R> implements C.List<R> {
    private final C.List<T> l_;
    private final _.Function<? super T, ? extends R> m_;

    MappedList(C.List<T> list, _.Function<? super T, ? extends R> mapper) {
        E.NPE(list, mapper);
        l_ = list;
        m_ = mapper;
    }
}
