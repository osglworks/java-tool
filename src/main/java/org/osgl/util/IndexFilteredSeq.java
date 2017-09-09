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
 * Date: 9/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class IndexFilteredSeq<T> extends IterableSeq<T> {

    private $.Predicate<Integer> filter;

    IndexFilteredSeq(Iterable<? extends T> iterable, $.Function<Integer, Boolean> predicate) {
        super(iterable);
        E.NPE(predicate);
        filter = $.predicate(predicate);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filterIndex(super.iterator(), filter);
    }

    static <T> C.Sequence<T> of(Iterable<? extends T> iterable, $.Function<Integer, Boolean> predicate) {
        return new IndexFilteredSeq<T>(iterable, predicate);
    }

}
