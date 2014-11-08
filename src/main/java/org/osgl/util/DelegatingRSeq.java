package org.osgl.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

class DelegatingRSeq<T> extends ReversibleSeqBase<T> implements Serializable {
    private C.ReversibleSequence<T> data;

    DelegatingRSeq(C.ReversibleSequence<T> rseq) {
        data = rseq;
    }

    C.ReversibleSequence<T> data() {return data;}

    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>)data).size();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = data().features();
        fs.add(C.Feature.READONLY);
        return fs;
    }

    @Override
    public Iterator<T> iterator() {
        return DelegatingIterator.of(data.iterator(), is(C.Feature.READONLY));
    }

    @Override
    public Iterator<T> reverseIterator() {
        return data.reverseIterator();
    }

    @SuppressWarnings("unchecked")
    public static <T> C.ReversibleSequence<T> of(C.ReversibleSequence<T> rseq) {
        return new DelegatingRSeq<T>(rseq);
    }
}
