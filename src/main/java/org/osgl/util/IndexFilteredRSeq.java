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
class IndexFilteredRSeq<T> extends DelegatingRSeq<T> {

    private _.Predicate<Integer> filter;

    IndexFilteredRSeq(C.ReversibleSequence<T> rseq, _.Function<Integer, Boolean> predicate) {
        super(rseq);
        E.NPE(predicate);
        filter = _.predicate(predicate);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filterIndex(super.iterator(), filter);
    }

    static <T> C.ReversibleSequence<T> of(C.ReversibleSequence<T> rseq, _.Function<Integer, Boolean> predicate) {
        return new IndexFilteredRSeq<T>(rseq, predicate);
    }

}
