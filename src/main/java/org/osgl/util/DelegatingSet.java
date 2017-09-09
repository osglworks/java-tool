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

import java.io.Serializable;
import java.util.*;

class DelegatingSet<T> extends SetBase<T> implements C.Set<T>, Serializable {

    protected Set<T> data;

    DelegatingSet(Collection<? extends T> c) {
        data = new HashSet<T>(c);
    }

    DelegatingSet() {
        data = new HashSet<T>();
    }

    DelegatingSet(Collection<? extends T> c, boolean immutable) {
        if (c instanceof C.Set) {
            C.Set<T> set = (C.Set<T>) c;
            boolean setIsImmutable = set.is(C.Feature.IMMUTABLE);
            if (immutable && setIsImmutable) {
                data = set;
            } else {
                if (immutable) {
                    data = Collections.unmodifiableSet(set);
                } else {
                    data = new HashSet<T>(set);
                }
            }
        } else if (c instanceof java.util.Set) {
            Set<? extends T> set = (Set<? extends T>)c;
            if (immutable) {
                data = Collections.unmodifiableSet(set);
            } else {
                data = new HashSet<T>(set);
            }
        } else {
            Set<T> set = new HashSet<T>(c);
            if (immutable) {
                data = Collections.unmodifiableSet(set);
            } else {
                data = set;
            }
        }
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = (data instanceof C.Set) ?
                ((C.Set<T>)data).features()
                : EnumSet.of(C.Feature.LIMITED);
        return fs;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return data.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return data.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return data.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return data.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return data.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return data.removeAll(c);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public int hashCode() {
        return data.hashCode() + DelegatingSet.class.hashCode();
    }

}
