package org.osgl.util;

import org.osgl.$;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
class FilteredSeq<T> extends IterableSeq<T> implements C.Sequence<T> {

    private $.Function<? super T, Boolean> filter;
    private FilteredIterator.Type type;

    FilteredSeq(Iterable<? extends T> iterable, $.Function<? super T, Boolean> predicate) {
        this(iterable, predicate, FilteredIterator.Type.ALL);
    }

    FilteredSeq(Iterable<? extends T> iterable, $.Function<? super T, Boolean> predicate, FilteredIterator.Type type) {
        super(iterable);
        E.NPE(predicate, type);
        filter = predicate;
        this.type = type;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> itr = super.iterator();
        return type.filter(itr, filter);
    }

    public static <T> FilteredSeq<T> of(Iterable<? extends T> iterable, $.Function<? super T, Boolean> predicate) {
        return new FilteredSeq<T>(iterable, predicate);
    }

    public static <T> FilteredSeq<T> of(Iterable<? extends T> iterable, $.Function<? super T, Boolean> predicate, FilteredIterator.Type type) {
        return new FilteredSeq<T>(iterable, predicate, type);
    }
}
