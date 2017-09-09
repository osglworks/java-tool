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

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
class FilteredRSeq<T> extends DelegatingRSeq<T> implements C.ReversibleSequence<T> {

    private $.Function<? super T, Boolean> filter;
    private FilteredIterator.Type type;

    FilteredRSeq(C.ReversibleSequence<T> rseq, $.Function<? super T, Boolean> predicate) {
        this(rseq, predicate, FilteredIterator.Type.ALL);
    }

    FilteredRSeq(C.ReversibleSequence<T> rseq, $.Function<? super T, Boolean> predicate, FilteredIterator.Type type) {
        super(rseq);
        E.NPE(predicate, type);
        filter = predicate;
        this.type = type;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> itr = super.iterator();
        return type.filter(itr, filter);
    }

    public static <T> FilteredRSeq<T> of(C.ReversibleSequence<T> rseq, $.Function<? super T, Boolean> predicate) {
        return new FilteredRSeq<T>(rseq, predicate);
    }

    public static <T> FilteredRSeq<T> of(C.ReversibleSequence<T> rseq, $.Function<? super T, Boolean> predicate, FilteredIterator.Type type) {
        return new FilteredRSeq<T>(rseq, predicate, type);
    }
}
