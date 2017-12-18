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

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ReversedRSeq<T> extends DelegatingRSeq<T> implements C.ReversibleSequence<T> {

    private ReversedRSeq(C.ReversibleSequence<T> rseq) {
        super(rseq);
    }

    @Override
    public Iterator<T> iterator() {
        return data().reverseIterator();
    }

    @Override
    public Iterator<T> reverseIterator() {
        return data().iterator();
    }
}
