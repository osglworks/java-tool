package org.osgl.util;

import java.util.EnumSet;
import java.util.Iterator;

import static org.osgl.util.C.Feature.LAZY;
import static org.osgl.util.C.Feature.READONLY;

/**
 * Create a view of two sequence combined together
 */
class CompositeRSeq<T> extends ReversibleSeqBase<T> {

    private final C.ReversibleSequence<T> left;
    private final C.ReversibleSequence<T> right;
    private EnumSet<C.Feature> features;

    CompositeRSeq(C.ReversibleSequence<T> left, C.ReversibleSequence<T> right) {
        E.NPE(left, right);
        if (left.isEmpty() || right.isEmpty()) {
            throw new IllegalArgumentException("left or right cannot be empty");
        }
        this.left = left;
        this.right = right;
        features = left.features();
        features.retainAll(right.features());
        features.add(READONLY);
    }

    static <T> C.ReversibleSequence<T> of(C.ReversibleSequence<T> left, C.ReversibleSequence<T> right) {
        return new CompositeRSeq<T>(left, right);
    }

    protected C.ReversibleSequence<T> left() {
        return left;
    }

    protected C.ReversibleSequence<T> right() {
        return right;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> features = left.features();
        features.retainAll(right.features());
        features.add(READONLY);
        features.add(LAZY);
        return features;
    }

    @Override
    public final int size() throws UnsupportedOperationException {
        return left.size() + right.size();
    }

    @Override
    public final Iterator<T> iterator() {
        return Iterators.composite(left.iterator(), right.iterator());
    }

    @Override
    public Iterator<T> reverseIterator() {
        return Iterators.composite(right.reverseIterator(), left.reverseIterator());
    }

    @Override
    public final boolean isEmpty() {
        return left.isEmpty() && right.isEmpty();
    }

}
