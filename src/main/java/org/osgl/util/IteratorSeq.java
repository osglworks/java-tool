package org.osgl.util;

import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Adapt an {@link java.util.Iterator} to a {@link C.Sequence}
 */
class IteratorSeq<T> extends SequenceBase<T> {

    private final Iterator<? extends T> itr_;

    private IteratorSeq(final Iterator<? extends T> itr) {
        E.NPE(itr);
        itr_ = itr;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY);
    }
}
