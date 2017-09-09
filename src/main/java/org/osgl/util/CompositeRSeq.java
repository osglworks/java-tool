package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
