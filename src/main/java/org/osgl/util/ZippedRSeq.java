package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedRSeq<A, B> extends ReversibleSeqBase<_.T2<A, B>> {

    private C.ReversibleSequence<A> a;
    private C.ReversibleSequence<B> b;

    private _.Option<A> defA = _.none();
    private _.Option<B> defB = _.none();


    ZippedRSeq(C.ReversibleSequence<A> a, C.ReversibleSequence<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedRSeq(C.ReversibleSequence<A> a, C.ReversibleSequence<B> b, A defA, B defB) {
        this(a, b);
        this.defA = _.some(defA);
        this.defB = _.some(defB);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return a.size() + b.size();
    }

    @Override
    public Iterator<_.T2<A, B>> iterator() {
        final Iterator<A> ia = a.iterator();
        final Iterator<B> ib = b.iterator();
        if (defA.isDefined()) {
            return new ZippedIterator<A, B>(ia, ib, defA.get(), defB.get());
        } else {
            return new ZippedIterator<A, B>(ia, ib);
        }
    }

    @Override
    public Iterator<_.T2<A, B>> reverseIterator() {
        final Iterator<A> ia = a.reverseIterator();
        final Iterator<B> ib = b.reverseIterator();
        if (defA.isDefined()) {
            return new ZippedIterator<A, B>(ia, ib, defA.get(), defB.get());
        } else {
            return new ZippedIterator<A, B>(ia, ib);
        }
    }
}
