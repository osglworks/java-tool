package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Implement a Lazy evaluation list
 */
class LazySeq<T> extends SequenceBase<T> implements C.Sequence<T> {
    protected T head;
    protected _.F0<C.Sequence<T>> tail;
    private volatile C.Sequence<T> tail_;


    /**
     * Sub classes shall init {@link #head} and {@link #tail} in this constructor
     */
    protected LazySeq() {
    }

    LazySeq(T head, _.Func0<? extends C.Sequence<T>> tail) {
        E.NPE(head, tail);
        this.head = head;
        this.tail = _.f0(tail);
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.IMMUTABLE, C.Feature.LAZY, C.Feature.READONLY, C.Feature.ORDERED);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T first() {
        return head;
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        return tail.apply();
    }

    @Override
    public Iterator<T> iterator() {
        final C.Sequence<T> seq = this;
        return new Iterator<T>() {
            private C.Sequence<T> _seq = seq;

            public boolean hasNext() {
                return !_seq.isEmpty();
            }

            @Override
            public T next() {
                T head = _seq.head();
                _seq = _seq.tail();
                return head;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public String debug() {
        StringBuilder sb = new StringBuilder("[_LS_");
        for (T t : this) {
            sb.append(", ").append(t);
        }
        return sb.append("]").toString();
    }

}
