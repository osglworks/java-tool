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

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 24/10/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
class MappedTrav<T, R> extends TraversableBase<R> {
    private final Iterable<? extends T> data;
    private final $.F1<T, R> mapper_;

    MappedTrav(Iterable<? extends T> iterable, $.Function<? super T, ? extends R> mapper) {
        E.NPE(iterable, mapper);
        data = iterable;
        mapper_ = $.f1(mapper);
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(data.iterator(), mapper_);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>) data).size();
        }
        throw new UnsupportedOperationException();
    }

    public static <T, R> C.Traversable<R> of(Iterable<? extends T> iterable, $.Function<? super T, ? extends R> mapper) {
        return new MappedTrav<T, R>(iterable, mapper);
    }
}
