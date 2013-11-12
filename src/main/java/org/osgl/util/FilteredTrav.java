package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 9/11/13
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
class FilteredTrav<T> extends IterableTrav<T> {
    private _.Predicate<? super T> filter;

    FilteredTrav(Iterable<? extends T> iterable, _.Function<? super T, Boolean> predicate) {
        super(iterable);
        E.NPE(predicate);
        filter = _.predicate(predicate);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(super.iterator(), filter);
    }

    public static <T> C.Traversable<T> of(Iterable<? extends T> iterable, _.Function<? super T, Boolean> predicate) {
        return new FilteredTrav<T>(iterable, predicate);
    }
}
