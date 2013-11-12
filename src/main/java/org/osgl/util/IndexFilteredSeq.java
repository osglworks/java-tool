package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 9/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class IndexFilteredSeq<T> extends IterableSeq<T> {

    private _.Predicate<Integer> filter;

    IndexFilteredSeq(Iterable<? extends T> iterable, _.Function<Integer, Boolean> predicate) {
        super(iterable);
        E.NPE(predicate);
        filter = _.predicate(predicate);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filterIndex(super.iterator(), filter);
    }

    static <T> C.Sequence<T> of(Iterable<? extends T> iterable, _.Function<Integer, Boolean> predicate) {
        return new IndexFilteredSeq<T>(iterable, predicate);
    }

}
