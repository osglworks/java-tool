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
 * A simple readonly {@link C.Traversable} implementation based on an {@link java.util.Iterator}
 */
class IteratorTrav<T> extends TraversableBase<T> {

    private Iterator<T> itr_;

    private EnumSet<C.Feature> features;

    IteratorTrav(Iterator<T> iterator) {
        E.NPE(iterator);
        itr_ = iterator;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY);
    }

    @Override
    public Iterator<T> iterator() {
        return itr_;
    }

    @Override
    public int size() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> C.Traversable<R> map($.Function<? super T, ? extends R> mapper) {
        return new IteratorTrav<R>(new MappedIterator<T, R>(itr_, mapper));
    }

    @Override
    public <R> C.Traversable<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        return new IteratorTrav<R>(new FlatMappedIterator<T, R>(itr_, mapper));
    }

    @Override
    public C.Traversable<T> filter($.Function<? super T, Boolean> predicate) {
        return new IteratorTrav<T>(new FilteredIterator<T>(itr_, predicate));
    }


}
