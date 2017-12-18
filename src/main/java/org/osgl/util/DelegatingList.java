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

/**
 * Implement {@link C.List} with a backing {@link java.util.List} instance
 */
class DelegatingList<T> extends ListBase<T> implements C.List<T>, Serializable {

    private static EnumSet<C.Feature> freeFeatures = EnumSet.of(C.Feature.LAZY, C.Feature.PARALLEL);
    private static EnumSet<C.Feature> setableFeatures = EnumSet.of(C.Feature.READONLY); static {
        setableFeatures.addAll(freeFeatures);
    }

    protected java.util.List<T> data;

    protected DelegatingList(boolean noInit) {
        if (noInit) {
            return;
        }
        data = C.randomAccessListFact.create(10);
    }

    DelegatingList() {
        this(10, C.randomAccessListFact);
    }

    DelegatingList(int initialCapacity) {
        this(initialCapacity, C.randomAccessListFact);
    }

    DelegatingList(C.ListFactory fact) {
        this(10, fact);
    }

    DelegatingList(int initialCapacity, C.ListFactory fact) {
        data = fact.create(initialCapacity);
    }

    DelegatingList(Iterable<? extends T> iterable) {
        this(iterable, C.randomAccessListFact);
    }

    DelegatingList(Iterable<? extends T> iterable, C.ListFactory fact) {
        E.NPE(iterable);
        if (iterable instanceof Collection) {
            data = fact.create((Collection<? extends T>)iterable);
        } else {
            data = fact.create();
            for (T t : iterable) {
                data.add(t);
            }
        }
    }

    private DelegatingList(java.util.List<T> list, boolean wrapDirectly) {
        E.NPE(list);
        if (wrapDirectly) {
            data = list;
        } else {
            data = C.randomAccessListFact.create(list);
        }
    }

    DelegatingList(Collection<T> col, C.ListFactory fact) {
        data = fact.create(col);
    }

    // -----------------------------------------------------------------------------------------


    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = (data instanceof C.List) ?
                ((C.List<T>)data).features()
                : EnumSet.of(C.Feature.LIMITED, C.Feature.ORDERED);
        if (data instanceof RandomAccess) {
            fs.add(C.Feature.RANDOM_ACCESS);
        }
        return fs;
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return data.listIterator(index);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public T set(int index, T element) {
        if (isMutable()) {
            return data.set(index, element);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        if (isMutable()) {
            data.add(index, element);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        if (isMutable()) {
            return data.remove(index);
        }
        throw new UnsupportedOperationException();
    }

    static <T> C.List<T> wrap(java.util.List<T> list) {
        if (list instanceof C.List) {
            C.List<T> cl = (C.List<T>)list;
            if (cl.is(C.Feature.IMMUTABLE) && cl.isEmpty()) {
                return Nil.list();
            }
        }
        if (list instanceof DelegatingList) {
            list = ((DelegatingList<T>)list).data;
        }
        return new DelegatingList<T>(list, true);
    }

    static <T> C.List<T> wrap(java.util.List<T> list, C.Feature f1) {
        C.List<T> l = wrap(list);
        if (l instanceof DelegatingList) {
            DelegatingList<T> dl = (DelegatingList<T>)l;
            if (setableFeatures.contains(f1)) {
                dl.features_().add(f1);
            }
        }
        return l;
    }

    static <T> C.List<T> wrap(java.util.List<T> list, C.Feature f1, C.Feature f2) {
        C.List<T> l = wrap(list);
        if (l instanceof DelegatingList) {
            DelegatingList<T> dl = (DelegatingList<T>)l;
            if (setableFeatures.contains(f1)) {
                dl.features_().add(f1);
            }
            if (setableFeatures.contains(f2)) {
                dl.features_().add(f2);
            }
        }
        return l;
    }

    static <T> C.List<T> wrap(java.util.List<T> list, C.Feature f1, C.Feature f2, C.Feature f3) {
        C.List<T> l = wrap(list);
        if (l instanceof DelegatingList) {
            DelegatingList<T> dl = (DelegatingList<T>)l;
            if (setableFeatures.contains(f1)) {
                dl.features_().add(f1);
            }
            if (setableFeatures.contains(f2)) {
                dl.features_().add(f2);
            }
            if (setableFeatures.contains(f3)) {
                dl.features_().add(f3);
            }
        }
        return l;
    }

    static <T> DelegatingList<T> copyOf(Iterable<T> iterable) {
        return new DelegatingList<T>(iterable);
    }

    static <T> DelegatingList<T> copyOf(Iterable<T> iterable, C.Feature f1) {
        DelegatingList<T> l = copyOf(iterable);
        if (setableFeatures.contains(f1)) {
            l.features_().add(f1);
        }
        return l;
    }

    static <T> DelegatingList<T> copyOf(Iterable<T> iterable, C.Feature f1, C.Feature f2) {
        DelegatingList<T> l = copyOf(iterable);
        if (setableFeatures.contains(f1)) {
            l.features_().add(f1);
        }
        if (setableFeatures.contains(f2)) {
            l.features_().add(f2);
        }
        return l;
    }

    static <T> DelegatingList<T> copyOf(Iterable<T> iterable, C.Feature f1, C.Feature f2, C.Feature f3) {
        DelegatingList<T> l = copyOf(iterable);
        if (setableFeatures.contains(f1)) {
            l.features_().add(f1);
        }
        if (setableFeatures.contains(f2)) {
            l.features_().add(f2);
        }
        if (setableFeatures.contains(f3)) {
            l.features_().add(f3);
        }
        return l;
    }

    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
        s.defaultWriteObject();
        s.writeObject(data);
        s.writeObject(features_());
    }

    private void readObject(java.io.ObjectInputStream s)
    throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        data = (List)s.readObject();
        EnumSet<C.Feature> features = (EnumSet)s.readObject();
        features_().addAll(features);
    }

}

class DelegatingStringList extends DelegatingList<String> implements S.List {
    public DelegatingStringList(boolean noInit) {
        super(noInit);
    }

    public DelegatingStringList() {
    }

    public DelegatingStringList(int initialCapacity) {
        super(initialCapacity);
    }

    public DelegatingStringList(C.ListFactory fact) {
        super(fact);
    }

    public DelegatingStringList(int initialCapacity, C.ListFactory fact) {
        super(initialCapacity, fact);
    }

    public DelegatingStringList(Iterable<? extends String> iterable) {
        super(iterable);
    }

    public DelegatingStringList(Iterable<? extends String> iterable, C.ListFactory fact) {
        super(iterable, fact);
    }

    public DelegatingStringList(Collection<String> col, C.ListFactory fact) {
        super(col, fact);
    }



}
