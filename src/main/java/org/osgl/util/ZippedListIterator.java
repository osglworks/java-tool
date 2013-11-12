package org.osgl.util;

import org.osgl._;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ZippedListIterator<A, B> implements ListIterator<_.T2<A, B>> {
    private ListIterator<A> a;
    private ListIterator<B> b;
    private _.Option<A> defA = _.none();
    private _.Option<B> defB = _.none();

    ZippedListIterator(ListIterator<A> a, ListIterator<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedListIterator(ListIterator<A> a, ListIterator<B> b, A defA, B defB) {
        this(a, b);
        this.defA = _.some(defA);
        this.defB = _.some(defB);
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
    public boolean hasPrevious() {
        boolean hasA = a.hasPrevious(), hasB = b.hasPrevious();
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
    public _.T2<A, B> next() {
        boolean hasA = a.hasNext(), hasB = b.hasNext();
        if (hasA && hasB) {
            return _.T2(a.next(), b.next());
        }
        if (defA.isDefined()) {
            if (hasA) {
                return _.T2(a.next(), defB.get());
            } else if (hasB) {
                return _.T2(defA.get(), b.next());
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public _.T2<A, B> previous() {
        boolean hasA = a.hasPrevious(), hasB = b.hasPrevious();
        if (hasA && hasB) {
            return _.T2(a.previous(), b.previous());
        }
        if (defA.isDefined()) {
            if (hasA) {
                return _.T2(a.previous(), defB.get());
            } else if (hasB) {
                return _.T2(defA.get(), b.previous());
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int nextIndex() {
        int idA = a.nextIndex(), idB = b.nextIndex();
        if (defA.isDefined()) {
            return Math.max(idA, idB);
        } else {
            return Math.min(idA, idB);
        }
    }

    @Override
    public int previousIndex() {
        int idA = a.previousIndex(), idB = b.previousIndex();
        if (defA.isDefined()) {
            return Math.max(idA, idB);
        } else {
            return Math.min(idA, idB);
        }
    }

    @Override
    public void set(_.T2<A, B> abt2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(_.T2<A, B> abt2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
