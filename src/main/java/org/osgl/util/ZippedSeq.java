package org.osgl.util;

import org.osgl._;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedSeq<A, B> extends SequenceBase<_.T2<A, B>> {

    private Iterable<A> a;
    private Iterable<B> b;

    private _.Option<A> defA = _.none();
    private _.Option<B> defB = _.none();


    ZippedSeq(Iterable<A> a, Iterable<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedSeq(Iterable<A> a, Iterable<B> b, A defA, B defB) {
        this(a, b);
        this.defA = _.some(defA);
        this.defB = _.some(defB);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (a instanceof Collection && b instanceof Collection) {
            int szA = ((Collection) a).size();
            int szB = ((Collection) b).size();
            if (defA.isDefined()) {
                return Math.max(szA, szB);
            } else {
                return Math.min(szA, szB);
            }
        }
        throw new UnsupportedOperationException();
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
}
