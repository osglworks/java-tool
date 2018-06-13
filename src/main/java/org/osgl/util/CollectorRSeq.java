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

import java.util.Iterator;

class CollectorRSeq<T, R> extends ReversibleSeqBase<R> implements C.ReversibleSequence<R> {
    private final C.ReversibleSequence<? extends T> data;
    private final String path;

    CollectorRSeq(C.ReversibleSequence<? extends T> seq, String path) {
        this.data = $.requireNotNull(seq);
        this.path = S.requireNotBlank(path);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data.size();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.collect(data.iterator(), path);
    }

    @Override
    public Iterator<R> reverseIterator() {
        return Iterators.collect(data.reverseIterator(), path);
    }

    public static <T, R> CollectorRSeq<T, R>
    of(C.ReversibleSequence<? extends T> data, String path) {
        return new CollectorRSeq<>(data, path);
    }

}
