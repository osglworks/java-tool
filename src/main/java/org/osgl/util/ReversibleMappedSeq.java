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
 * Date: 20/11/13
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
class ReversibleMappedSeq<T, R> extends ReversibleSeqBase<R> implements C.ReversibleSequence<R> {
    private final C.ReversibleSequence<? extends T> data;
    protected final $.F1<? super T, ? extends R> mapper;

    ReversibleMappedSeq(C.ReversibleSequence<? extends T> seq, $.Function<? super T, ? extends R> mapper) {
        E.NPE(seq, mapper);
        this.data = seq;
        this.mapper = $.f1(mapper);
    }

    @Override
    public Iterator<R> reverseIterator() {
        return Iterators.map(data.reverseIterator(), mapper);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data.size();
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(data.iterator(), mapper);
    }
}
