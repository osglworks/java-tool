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
