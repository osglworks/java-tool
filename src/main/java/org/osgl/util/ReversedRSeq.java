package org.osgl.util;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ReversedRSeq<T> extends DelegatingRSeq<T> implements C.ReversibleSequence<T> {

    private ReversedRSeq(C.ReversibleSequence<T> rseq) {
        super(rseq);
    }

    @Override
    public Iterator<T> iterator() {
        return data().reverseIterator();
    }

    @Override
    public Iterator<T> reverseIterator() {
        return data().iterator();
    }
}
