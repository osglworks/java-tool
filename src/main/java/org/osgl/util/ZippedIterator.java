package org.osgl.util;

import org.osgl.$;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:07 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedIterator<A, B> implements Iterator<$.Binary<A, B>> {
    private Iterator<A> a;
    private Iterator<B> b;
    private $.Option<A> defA = $.none();
    private $.Option<B> defB = $.none();

    ZippedIterator(Iterator<A> a, Iterator<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedIterator(Iterator<A> a, Iterator<B> b, A defA, B defB) {
        this(a, b);
        this.defA = $.some(defA);
        this.defB = $.some(defB);
    }

    @Override
    public boolean hasNext() {
        boolean hasA = a.hasNext(), hasB = b.hasNext();
        if (hasA && hasB) {
            return true;
        }
        if (defA.isDefined()) {
            return hasA || hasB;
        } else {
            return false;
        }
    }

    @Override
    public $.T2<A, B> next() {
        boolean hasA = a.hasNext(), hasB = b.hasNext();
        if (hasA && hasB) {
            return $.T2(a.next(), b.next());
        }
        if (defA.isDefined()) {
            if (hasA) {
                return $.T2(a.next(), defB.get());
            } else if (hasB) {
                return $.T2(defA.get(), b.next());
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
