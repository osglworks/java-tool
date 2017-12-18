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

import org.osgl.$;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Implement a Lazy evaluation list
 */
class LazySeq<T> extends SequenceBase<T> implements C.Sequence<T> {
    protected T head;
    protected $.F0<C.Sequence<T>> tail;
    private volatile C.Sequence<T> tail_;


    /**
     * Sub classes shall init {@link #head} and {@link #tail} in this constructor
     */
    protected LazySeq() {
    }

    LazySeq(T head, $.Func0<? extends C.Sequence<T>> tail) {
        E.NPE(head, tail);
        this.head = head;
        this.tail = $.f0(tail);
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
