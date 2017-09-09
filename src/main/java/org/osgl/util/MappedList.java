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
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class MappedList<T, R> extends ReadOnlyListBase<R> implements C.List<R> {

    private final C.List<? extends T> l_;
    private final $.Function<? super T, ? extends R> m_;

    MappedList(C.List<? extends T> list, $.Function<? super T, ? extends R> mapper) {
        E.NPE(list, mapper);
        l_ = list;
        m_ = mapper;
    }

    @Override
    protected EnumSet<C.Feature> internalInitFeatures() {
        return l_.features();
    }

    @Override
    public ListIterator<R> listIterator(int index) {
        return new MappedListIterator<T, R>(l_.listIterator(index), m_);
    }

    @Override
    public int size() {
        return l_.size();
    }

    @Override
    public R get(int index) {
        T t = l_.get(index);
        return m_.apply(t);
    }

    public static <T, R> MappedList<T, R>
    of(C.List<? extends T> data, $.Function<? super T, ? extends R> mapper) {
        return new MappedList<T, R>(data, mapper);
    }

}
