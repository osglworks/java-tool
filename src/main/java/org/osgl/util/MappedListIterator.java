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

import java.util.ListIterator;

/**
 * Created by luog on 13/12/13.
 */
class MappedListIterator<T, R> extends MappedIterator<T, R> implements ListIterator<R> {

    MappedListIterator
    (ListIterator<? extends T> itr, $.Function<? super T, ? extends R> mapper) {
        super(itr, mapper);
    }

    @Override
    protected ListIterator<? extends T> data() {
        return (ListIterator<? extends T>)super.data();
    }

    @Override
    public boolean hasPrevious() {
        return data().hasPrevious();
    }

    @Override
    public R previous() {
        return mapper().apply(data().previous());
    }

    @Override
    public int nextIndex() {
        return data().nextIndex();
    }

    @Override
    public int previousIndex() {
        return data().previousIndex();
    }

    @Override
    public void set(R r) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(R r) {
        throw new UnsupportedOperationException();
    }
}
