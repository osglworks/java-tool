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
import java.util.EnumSet;

/**
 * Created by luog on 2/02/14.
 */
class ImmutableSet<T> extends DelegatingSet<T> {
    ImmutableSet(Collection<? extends T> collection) {
        super(collection, true);
    }

    @Override
    public boolean add(T t) {
        throw E.unsupport();
    }

    @Override
    public boolean remove(Object o) {
        throw E.unsupport();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw E.unsupport();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw E.unsupport();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw E.unsupport();
    }

    @Override
    public void clear() {
        throw E.unsupport();
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = super.initFeatures();
        fs.add(C.Feature.IMMUTABLE);
        fs.add(C.Feature.READONLY);
        return fs;
    }

    static <T> C.Set<T> of(T[] data) {
        E.NPE(data);
        int len = data.length;
        if (len == 0) {
            return Nil.set();
        } else if (len == 1) {
            return $.val(data[0]);
        } else {
            return new ImmutableSet<T>(C.listOf(data));
        }
    }

    static <T> C.Set<T> of(Collection<? extends T> data) {
        if (data instanceof C.Set) {
            C.Set<T> set = (C.Set<T>)data;
            if (set.is(C.Feature.IMMUTABLE)) {
                return set;
            }
        }
        return new ImmutableSet<T>(data);
    }

}
