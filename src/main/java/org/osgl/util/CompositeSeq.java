package org.osgl.util;

import java.util.EnumSet;
import java.util.Iterator;

import static org.osgl.util.C.Feature.LAZY;
import static org.osgl.util.C.Feature.READONLY;

/**
 * Create a view of two sequence combined together
 */
class CompositeSeq<T> extends SequenceBase<T> {

    private final C.Sequence<? extends T> left;
    private final C.Sequence<? extends T> right;
    private EnumSet<C.Feature> features;

    CompositeSeq(C.Sequence<? extends T> left, C.Sequence<? extends T> right) {
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

    static <T> C.Sequence<T> of(C.Sequence<? extends T> left, C.Sequence<? extends T> right) {
        return new CompositeSeq<T>(left, right);
    }

    protected C.Sequence<? extends T> left() {
        return left;
    }

    protected C.Sequence<? extends T> right() {
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
    public final boolean isEmpty() {
        return left.isEmpty() && right.isEmpty();
    }

}
