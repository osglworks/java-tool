package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
class FilteredRSeq<T> extends DelegatingRSeq<T> implements C.ReversibleSequence<T> {

    private _.Function<? super T, Boolean> filter;
    private FilteredIterator.Type type;

    FilteredRSeq(C.ReversibleSequence<T> rseq, _.Function<? super T, Boolean> predicate) {
        this(rseq, predicate, FilteredIterator.Type.ALL);
    }

    FilteredRSeq(C.ReversibleSequence<T> rseq, _.Function<? super T, Boolean> predicate, FilteredIterator.Type type) {
        super(rseq);
        E.NPE(predicate, type);
        filter = predicate;
        this.type = type;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> itr = super.iterator();
        return type.filter(itr, filter);
    }

    public static <T> FilteredRSeq<T> of(C.ReversibleSequence<T> rseq, _.Function<? super T, Boolean> predicate) {
        return new FilteredRSeq<T>(rseq, predicate);
    }

    public static <T> FilteredRSeq<T> of(C.ReversibleSequence<T> rseq, _.Function<? super T, Boolean> predicate, FilteredIterator.Type type) {
        return new FilteredRSeq<T>(rseq, predicate, type);
    }
}
