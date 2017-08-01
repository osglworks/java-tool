package org.osgl.util;

import org.osgl.$;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedRSeq<A, B> extends ReversibleSeqBase<$.Binary<A, B>> {

    private C.ReversibleSequence<A> a;
    private C.ReversibleSequence<B> b;

    private $.Option<A> defA = $.none();
    private $.Option<B> defB = $.none();


    ZippedRSeq(C.ReversibleSequence<A> a, C.ReversibleSequence<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedRSeq(C.ReversibleSequence<A> a, C.ReversibleSequence<B> b, A defA, B defB) {
        this(a, b);
        this.defA = $.some(defA);
        this.defB = $.some(defB);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return a.size() + b.size();
    }

    @Override
    public Iterator<$.Binary<A, B>> iterator() {
        final Iterator<A> ia = a.iterator();
        final Iterator<B> ib = b.iterator();
        if (defA.isDefined()) {
            return new ZippedIterator<>(ia, ib, defA.get(), defB.get());
        } else {
            return new ZippedIterator<>(ia, ib);
        }
    }

    @Override
    public Iterator<$.Binary<A, B>> reverseIterator() {
        final Iterator<A> ia = a.reverseIterator();
        final Iterator<B> ib = b.reverseIterator();
        if (defA.isDefined()) {
            return new ZippedIterator<>(ia, ib, defA.get(), defB.get());
        } else {
            return new ZippedIterator<>(ia, ib);
        }
    }
}
