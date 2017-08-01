package org.osgl.util;

import org.osgl.$;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ZippedListIterator<A, B> implements ListIterator<$.Binary<A, B>> {
    private ListIterator<A> a;
    private ListIterator<B> b;
    private $.Option<A> defA = $.none();
    private $.Option<B> defB = $.none();

    ZippedListIterator(ListIterator<A> a, ListIterator<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedListIterator(ListIterator<A> a, ListIterator<B> b, A defA, B defB) {
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
    public $.T2<A, B> previous() {
        boolean hasA = a.hasPrevious(), hasB = b.hasPrevious();
        if (hasA && hasB) {
            return $.T2(a.previous(), b.previous());
        }
        if (defA.isDefined()) {
            if (hasA) {
                return $.T2(a.previous(), defB.get());
            } else if (hasB) {
                return $.T2(defA.get(), b.previous());
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
    public void set($.Binary<A, B> abt2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add($.Binary<A, B> abt2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
