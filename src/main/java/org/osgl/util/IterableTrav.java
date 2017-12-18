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

import java.util.Collection;
import java.util.Iterator;

/**
 * A {@link C.Traversable} implementation based on an {@link Iterable}
 */
class IterableTrav<T> extends TraversableBase<T> {

    private final Iterable<? extends T> data;

    IterableTrav(Iterable<? extends T> iterable) {
        E.NPE(iterable);
        data = iterable;
    }

    @Override
    public Iterator<T> iterator() {
        return DelegatingIterator.of(data.iterator(), is(C.Feature.READONLY));
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (data instanceof Collection) {
            return ((Collection<?>) data).size();
        }
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public static <T> C.Traversable<T> of(Iterable<? extends T> iterable) {
        if (iterable instanceof C.Traversable) {
            return (C.Traversable<T>)iterable;
        }
        return new IterableTrav<T>(iterable);
    }
}
