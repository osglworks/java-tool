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
import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.util.*;

import static org.osgl.util.C.Feature.SORTED;

public abstract class ListBase<T> extends AbstractList<T> implements C.List<T> {

    private boolean sorted = false;

    // utilities
    protected final boolean isLazy() {
        return is(C.Feature.LAZY);
    }

    protected final boolean isImmutable() {
        return is(C.Feature.IMMUTABLE);
    }

    protected final boolean isReadOnly() {
        return is(C.Feature.READONLY);
    }

    protected final boolean isMutable() {
        return !isImmutable() && !isReadOnly();
    }

    protected void forEachLeft($.Visitor<? super T> visitor) throws $.Break {
        for (T t : this) {
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    protected void forEachLeft($.IndexedVisitor<Integer, ? super T> indexedVisitor) throws $.Break {
        for (int i = 0, j = size(); i < j; ++i) {
            try {
                indexedVisitor.apply(i, get(i));
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    protected void forEachRight($.Visitor<? super T> visitor) throws $.Break {
        Iterator<T> itr = reverseIterator();
        while (itr.hasNext()) {
            try {
                visitor.apply(itr.next());
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    protected void forEachRight($.IndexedVisitor<Integer, ? super T> indexedVisitor) throws $.Break {
        for (int i = size() - 1; i >= 0; --i) {
            try {
                indexedVisitor.apply(i, get(i));
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    @Override
    public C.List<T> parallel() {
        setFeature(C.Feature.PARALLEL);
        return this;
    }

    @Override
    public C.List<T> sequential() {
        unsetFeature(C.Feature.PARALLEL);
        return this;
    }

    @Override
    public C.List<T> lazy() {
        setFeature(C.Feature.LAZY);
        return this;
    }

    @Override
    public C.List<T> eager() {
        unsetFeature(C.Feature.LAZY);
        return this;
    }

    @Override
    public C.List<T> snapshot() {
        if (isImmutable()) {
            return this;
        }
        return ListBuilder.toList(this);
    }

    @Override
    public C.List<T> readOnly() {
        if (isMutable()) {
            return new ReadOnlyDelegatingList<T>(this);
        }
        return this;
    }

    @Override
    public C.List<T> copy() {
        return C.newList(this);
    }

    @Override
    public C.List<T> sorted() {
        if (size() == 0) return C.newList();
        T t = get(0);
        C.List<T> l = copy();
        if (!(t instanceof Comparable)) {
            return l;
        }
        Object[] a = l.toArray();
       	Arrays.sort(a);
       	ListIterator<T> i = l.listIterator();
       	for (int j=0; j<a.length; j++) {
       	    i.next();
       	    i.set((T)a[j]);
       	}
        ((ListBase)l).setFeature(SORTED);
        sorted = true;
        return l;
    }

    @Override
    public C.List<T> sorted(Comparator<? super T> comparator) {
        C.List<T> l = copy();
        Collections.sort(l, comparator);
        ((ListBase)l).setFeature(SORTED);
        return l;
    }

    @Override
    public C.List<T> unique() {
        Set<T> set = C.newSet();
        C.List<T> retList = null;
        int i = 0;
        for (T t: this) {
            i++;
            if (set.contains(t)) {
                if (null == retList) {
                    retList = C.newSizedList(size());
                    retList.addAll(subList(0, i - 1));
                }
            } else if (null != retList) {
                retList.add(t);
            }
            set.add(t);
        }
        return null == retList ? this : retList;
    }

    public C.List<T> unique(Comparator<T> comp) {
        Set<T> set = new TreeSet<T>(comp);
        C.List<T> retList = null;
        int i = 0;
        for (T t: this) {
            i++;
            if (set.contains(t)) {
                if (null == retList) {
                    retList = C.newSizedList(size());
                    retList.addAll(subList(0, i - 1));
                }
            } else if (null != retList) {
                retList.add(t);
            }
            set.add(t);
        }
        return null == retList ? this : retList;
    }

    @Override
    public C.List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return Nil.list();
        }
        if (is(C.Feature.RANDOM_ACCESS)) {
            return new RandomAccessSubList<T>(this, fromIndex, toIndex);
        } else {
            return new SubList<T>(this, fromIndex, toIndex);
        }
    }

    @Override
    public boolean add(T t) {
        boolean b = super.add(t);
        if (b) {
            sorted = false;
        }
        return b;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        sorted = false;
        unsetFeature(SORTED);
    }

    public boolean addAll(Iterable<? extends T> iterable) {
        boolean modified = false;
        Iterator<? extends T> e = iterable.iterator();
        while (e.hasNext()) {
            if (add(e.next())) {
                modified = true;
            }
        }
        sorted = !modified;
        if (!sorted) {
            unsetFeature(SORTED);
        }
        return modified;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof ListBase) {
            return $.eq(features_, ((ListBase) o).features_) && super.equals(o);
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + $.hc(features_);
    }

    // --- Featured methods

    volatile private EnumSet<C.Feature> features_;

    protected final EnumSet<C.Feature> features_() {
        if (null == features_) {
            synchronized (this) {
                if (null == features_) {
                    features_ = initFeatures();
                    assert (null != features_);
                }
            }
        }
        return features_;
    }

    /**
     * Sub class should override this method to provide initial feature
     * set for the feature based instance
     *
     * @return the initial feature set configuration
     */
    abstract protected EnumSet<C.Feature> initFeatures();

    @Override
    public final EnumSet<C.Feature> features() {
        return EnumSet.copyOf(features_());
    }

    @Override
    public final boolean is(C.Feature feature) {
        return features_().contains(feature);
    }

    protected ListBase<T> setFeature(C.Feature feature) {
        features_().add(feature);
        return this;
    }

    protected ListBase<T> unsetFeature(C.Feature feature) {
        features_().remove(feature);
        return this;
    }

    // --- eof Featured methods

    // --- Traversal methods

    @Override
    public boolean allMatch($.Function<? super T, Boolean> predicate) {
        return !anyMatch($.F.negate(predicate));
    }

    @Override
    public boolean anyMatch($.Function<? super T, Boolean> predicate) {
        return findOne(predicate).isDefined();
    }

    @Override
    public boolean noneMatch($.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public $.Option<T> findOne(final $.Function<? super T, Boolean> predicate) {
        try {
            forEach(new $.Visitor<T>() {
                @Override
                public void visit(T t) throws $.Break {
                    if (predicate.apply(t)) {
                        throw new $.Break(t);
                    }
                }
            });
            return $.none();
        } catch ($.Break b) {
            T t = b.get();
            return $.some(t);
        }
    }

    // --- eof Traversal methods


    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    public abstract ListIterator<T> listIterator(int index);

    @Override
    public Iterator<T> reverseIterator() {
        final ListIterator<T> li = listIterator(size());

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return li.hasPrevious();
            }

            @Override
            public T next() {
                return li.previous();
            }

            @Override
            public void remove() {
                li.remove();
            }
        };
    }

    @Override
    public final T first() throws NoSuchElementException {
        return head();
    }

    @Override
    public T head() throws NoSuchElementException {
        return iterator().next();
    }

    @Override
    public T last() throws NoSuchElementException {
        return reverseIterator().next();
    }

    @Override
    public C.List<T> take(int n) {
        boolean immutable = isImmutable();
        if (n == 0) {
            if (immutable) {
                return Nil.list();
            } else {
                return C.newList();
            }
        } else if (n < 0) {
            return drop(size() + n);
        } else if (n >= size()) {
            return this;
        }
        if (immutable) {
            return subList(0, n);
        }
        C.List<T> l = C.newSizedList(n);
        l.addAll(subList(0, n));
        return l;
    }

    @Override
    public C.List<T> takeWhile($.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            for (T t : this) {
                if (predicate.apply(t)) {
                    lb.add(t);
                } else {
                    break;
                }
            }
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            for (T t : this) {
                if (predicate.apply(t)) {
                    l.add(t);
                } else {
                    break;
                }
            }
            return l;
        }
    }

    @Override
    public C.List<T> drop(int n) throws IndexOutOfBoundsException {
        int sz = size();
        boolean immutable = isImmutable();
        if (n < 0) {
            n = -n;
            if (n >= sz) {
                if (immutable) return C.newList();
                else return C.list();
            } else {
                return take(sz - n);
            }
        }
        if (0 == n) {
            return this;
        }
        // TODO handle lazy drop
        if (immutable) {
            return subList(n, size());
        }
        if (n >= sz) {
            return C.newList();
        }
        C.List<T> l = C.newSizedList(sz - n);
        l.addAll(subList(n, sz));
        return l;
    }

    @Override
    public C.List<T> dropWhile($.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            boolean found = false;
            for (T t : this) {
                if (!found && predicate.apply(t)) {
                    continue;
                } else {
                    found = true;
                    lb.add(t);
                }
            }
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            boolean found = false;
            for (T t : this) {
                if (!found && predicate.apply(t)) {
                    continue;
                } else {
                    found = true;
                    l.add(t);
                }
            }
            return l;
        }
    }

    @Override
    public C.List<T> remove($.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy remove
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            forEach($.visitor($.predicate(predicate).elseThen(C.F.addTo(lb))));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            forEach($.visitor($.predicate(predicate).elseThen(C.F.addTo(l))));
            return l;
        }
    }

    @Override
    public <R> C.List<R> map($.Function<? super T, ? extends R> mapper) {
        boolean immutable = isImmutable();
        int sz = size();
        if (isLazy()) {
            return MappedList.of(this, mapper);
        }
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<R> lb = new ListBuilder<R>(sz);
            forEach($.visitor($.f1(mapper).andThen(C.F.addTo(lb))));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newSizedList(sz);
            forEach($.visitor($.f1(mapper).andThen(C.F.addTo(l))));
            return l;
        }
    }

    @Override
    public <R> C.List<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy flatmap
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<R> lb = new ListBuilder<R>(sz * 3);
            forEach($.visitor($.f1(mapper).andThen(C.F.addAllTo(lb))));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newSizedList(sz * 3);
            forEach($.visitor($.f1(mapper).andThen(C.F.addAllTo(l))));
            return l;
        }
    }

    @Override
    public C.List<T> filter($.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy filter
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            forEach($.visitor($.predicate(predicate).ifThen(C.F.addTo(lb))));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            forEach($.visitor($.predicate(predicate).ifThen(C.F.addTo(l))));
            return l;
        }
    }

    @Override
    public Osgl.T2<C.List<T>, C.List<T>> split(final Osgl.Function<? super T, Boolean> predicate) {
        final C.List<T> left = C.newList();
        final C.List<T> right = C.newList();
        accept(new $.Visitor<T>() {
            @Override
            public void visit(T t) throws Osgl.Break {
                if (predicate.apply(t)) {
                    left.add(t);
                } else {
                    right.add(t);
                }
            }
        });
        if (isImmutable() || isReadOnly()) {
            return $.T2(C.list(left), C.list(right));
        }
        return $.T2(left, right);
    }

    private Cursor<T> fromLeft() {
        return new ListIteratorCursor<T>(listIterator(0));
    }

    private Cursor<T> fromRight() {
        return new ListIteratorCursor<T>(listIterator(size()));
    }

    @Override
    public Cursor<T> locateFirst($.Function<T, Boolean> predicate) {
        Cursor<T> c = fromLeft();
        while (c.hasNext()) {
            T t = c.forward().get();
            if (predicate.apply(t)) {
                return c;
            }
        }
        return c;
    }

    @Override
    public Cursor<T> locate($.Function<T, Boolean> predicate) {
        return locateFirst(predicate);
    }

    @Override
    public Cursor<T> locateLast($.Function<T, Boolean> predicate) {
        Cursor<T> c = fromRight();
        while (c.hasPrevious()) {
            T t = c.backward().get();
            if (predicate.apply(t)) {
                return c;
            }
        }
        return c;
    }

    @Override
    public C.List<T> insert(int index, T t) throws IndexOutOfBoundsException {
        int sz = size();
        if (sz < Math.abs(index)) {
            throw new IndexOutOfBoundsException();
        }
        if (index < 0) {
            index = sz + index;
        }
        if (isMutable()) {
            add(index, t);
            return this;
        }
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(sz + 1);
            if (index > 0) {
                lb.addAll(subList(0, index));
            }
            lb.add(t);
            if (index < sz) {
                lb.addAll(subList(index, sz));
            }
            return lb.toList();
        } else {
            C.List<T> l = C.newSizedList(sz + 1);
            if (index > 0) {
                l.addAll(subList(0, index));
            }
            l.add(t);
            if (index < sz) {
                l.addAll(subList(index, sz));
            }
            return l;
        }
    }

    @Override
    public C.List<T> insert(int index, T... ta) throws IndexOutOfBoundsException {
        if (ta.length == 0) {
            return this;
        }
        return insert(index, C.listOf(ta));
    }

    @Override
    public C.List<T> insert(int index, List<T> subList) throws IndexOutOfBoundsException {
        if (subList.isEmpty()) {
            return this;
        }
        int sz = size();
        if (sz < Math.abs(index)) {
            throw new IndexOutOfBoundsException();
        }
        if (index < 0) {
            index = sz + index;
        }
        if (isMutable()) {
            addAll(index, subList);
            return this;
        }
        if (isImmutable()) {
            int delta = subList.size();
            ListBuilder<T> lb = new ListBuilder<T>(sz + delta);
            if (index > 0) {
                lb.addAll(subList(0, index));
            }
            lb.addAll(subList);
            if (index < sz) {
                lb.addAll(subList(index, sz));
            }
            return lb.toList();
        } else {
            C.List<T> l = C.newSizedList(sz + 1);
            if (index > 0) {
                l.addAll(subList(0, index));
            }
            l.addAll(subList);
            if (index < sz) {
                l.addAll(subList(index, sz));
            }
            return l;
        }
    }

    @Override
    public C.List<T> reverse() {
        if (isLazy()) {
            return ReverseList.wrap(this);
        }
        boolean immutable = isImmutable();
        int sz = size();
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            Iterator<T> itr = reverseIterator();
            while (itr.hasNext()) {
                lb.add(itr.next());
            }
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            Iterator<T> itr = reverseIterator();
            while (itr.hasNext()) {
                l.add(itr.next());
            }
            return l;
        }
    }

    @Override
    public C.List<T> without(Collection<? super T> col) {
        return filter($.F.negate(C.F.containsIn(col)));
    }

    @Override
    public C.List<T> without(T element) {
        return filter(($.F.ne().curry(element)));
    }

    @Override
    public C.List<T> without(T element, T... elements) {
        elements = $.concat(elements, element);
        C.List<T> l = without(element);
        int len = elements.length;
        if (0 == len) {
            return l;
        }
        boolean c = false;
        if (8 < len) {
            T t0 = elements[0];
            if (t0 instanceof Comparable) {
                c = true;
                Arrays.sort(elements);
            }
        }
        C.List<T> lr = C.newSizedList(l.size());
        if (c) {
            for (T t : l) {
                int id = Arrays.binarySearch(elements, t);
                if (id == -1) continue;
                lr.add(t);
            }
        } else {
            for (T t : l) {
                boolean found = false;
                for (int i = 0; i < len; ++i) {
                    if ($.eq(elements[i], t)) {
                        found = true;
                        break;
                    }
                }
                if (!found) lr.add(t);
            }
        }
        return lr;
    }

    @Override
    public C.List<T> accept($.Visitor<? super T> visitor) {
        forEachLeft(visitor);
        return this;
    }

    @Override
    public C.List<T> each($.Visitor<? super T> visitor) {
        return accept(visitor);
    }

    @Override
    public C.List<T> forEach($.Visitor<? super T> visitor) {
        return accept(visitor);
    }

    @Override
    public C.List<T> acceptLeft($.Visitor<? super T> visitor) {
        forEachLeft(visitor);
        return this;
    }

    @Override
    public C.List<T> acceptRight($.Visitor<? super T> visitor) {
        forEachRight(visitor);
        return this;
    }

    @Override
    public C.List<T> accept($.IndexedVisitor<Integer, ? super T> indexedVisitor) {
        forEachLeft(indexedVisitor);
        return this;
    }

    @Override
    public C.List<T> each($.IndexedVisitor<Integer, ? super T> indexedVisitor) {
        return accept(indexedVisitor);
    }

    @Override
    public C.List<T> forEach($.IndexedVisitor<Integer, ? super T> indexedVisitor) {
        return accept(indexedVisitor);
    }

    @Override
    public C.List<T> acceptLeft($.IndexedVisitor<Integer, ? super T> indexedVisitor) {
        forEachLeft(indexedVisitor);
        return this;
    }

    @Override
    public C.List<T> acceptRight($.IndexedVisitor<Integer, ? super T> indexedVisitor) {
        forEachRight(indexedVisitor);
        return this;
    }

    @Override
    public C.List<T> head(int n) {
        return take(n);
    }

    @Override
    public C.List<T> tail() {
        int sz = size();
        if (0 == sz) {
            throw new UnsupportedOperationException();
        }
        if (isImmutable()) {
            return subList(1, sz);
        }
        C.List<T> l = C.newSizedList(sz - 1);
        l.addAll(subList(1, sz));
        return l;
    }

    @Override
    public C.List<T> tail(int n) {
        boolean immutable = isImmutable();
        int sz = size();
        if (n < 0) {
            return head(-n);
        } else if (n == 0) {
            if (immutable) {
                return Nil.list();
            } else {
                return C.newList();
            }
        } else if (n >= sz) {
            return this;
        }
        C.List<T> sl = subList(sz - n, sz);
        if (immutable) {
            return sl;
        }
        C.List<T> l = C.newSizedList(n);
        l.addAll(sl);
        return l;
    }

    @SuppressWarnings("unchecked")
    private C.List<T> unLazyAppend(Iterable<? extends T> iterable) {
        if (isMutable()) {
            if (iterable instanceof Collection) {
                addAll((Collection<? extends T>) iterable);
            } else {
                C.forEach(iterable, $.visitor(C.F.addTo(this)));
            }
            return this;
        }
        // immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
            lb.append(this).append(iterable);
            return lb.toList();
        }
        // mutable but read only
        C.List<T> l = C.newSizedList(size() * 2);
        l.addAll(this);
        l.addAll(iterable);
        return l;
    }

    private C.List<T> unLazyAppend(Iterator<? extends T> iterator) {
        if (isMutable()) {
            C.forEach(iterator, $.visitor(C.F.addTo(this)));
            return this;
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(this).append(iterator);
        return lb.toList();
    }

    private C.List<T> unLazyAppend(Enumeration<? extends T> enumeration) {
        if (isMutable()) {
            C.forEach(new EnumerationIterator<T>(enumeration), $.visitor(C.F.addTo(this)));
            return this;
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(this).append(enumeration);
        return lb.toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> append(Iterable<? extends T> iterable) {
        if (iterable instanceof C.List) {
            return appendList((C.List<T>) iterable);
        } else if (iterable instanceof C.Sequence) {
            return append((C.Sequence<T>) iterable);
        } else if (iterable instanceof Collection) {
            return append((Collection<? extends T>) iterable);
        } else if (isLazy()) {
            return CompositeSeq.of(this, IterableSeq.of(iterable));
        } else {
            return unLazyAppend(iterable);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.List<T> append(Collection<? extends T> collection) {
        if (collection instanceof C.List) {
            return appendList((C.List<T>) collection);
        } else {
            return unLazyAppend(collection);
        }
    }

    @Override
    public C.Sequence<T> append(C.Sequence<? extends T> seq) {
        if (seq instanceof C.List) {
            return appendList((C.List<T>) seq);
        }
        if (isLazy()) {
            return CompositeSeq.of(this, seq);
        }
        return unLazyAppend(seq);
    }

    @Override
    public C.Sequence<T> append(Iterator<? extends T> iterator) {
        if (isLazy()) {
            return CompositeSeq.of(this, C.seq(iterator));
        }
        return unLazyAppend(iterator);
    }

    @Override
    public C.Sequence<T> append(Enumeration<? extends T> enumeration) {
        return append(new EnumerationIterator<T>(enumeration));
    }

    protected C.ReversibleSequence<T> appendReversibleSeq(C.ReversibleSequence<T> seq) {
        if (seq instanceof C.List) {
            return appendList((C.List<T>) seq);
        }
        // TODO support lazy append reversible sequence
        return unLazyAppend(seq);
    }

    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        return appendReversibleSeq(seq);
    }

    protected C.List<T> appendList(C.List<T> list) {
        if (isLazy()) {
            return CompositeList.of(this, list);
        }
        return unLazyAppend(list);
    }

    @Override
    public C.List<T> append(C.List<T> list) {
        return appendList(list);
    }

    @Override
    public C.List<T> append(T t) {
        if (isMutable()) {
            add(t);
            return this;
        }
        // Immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() + 1);
            lb.addAll(this);
            lb.add(t);
            return lb.toList();
        }
        // mutable but readonly
        C.List<T> l = C.newSizedList(size() + 1);
        l.addAll(this);
        l.add(t);
        return l;
    }

    private C.List<T> unLazyPrepend(Iterable<? extends T> iterable) {
        if (isMutable()) {
            int pos = 0;
            for (T t : iterable) {
                add(pos++, t);
            }
            return this;
        }
        // immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
            lb.append(iterable).append(this);
            return lb.toList();
        }
        // mutable but read only
        C.List<T> l = C.newSizedList(size() * 2);
        l.addAll(iterable);
        l.addAll(this);
        return l;
    }

    private C.List<T> unLazyPrepend(Iterator<? extends T> iterator) {
        if (isMutable()) {
            int pos = 0;
            while (iterator.hasNext()) {
                add(pos++, iterator.next());
            }
            return this;
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(iterator).append(this);
        return lb.toList();
    }

    private C.List<T> unLazyPrepend(Enumeration<? extends T> enumeration) {
        if (isMutable()) {
            int pos = 0;
            while (enumeration.hasMoreElements()) {
                add(pos++, enumeration.nextElement());
            }
            return this;
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(enumeration).append(this);
        return lb.toList();
    }


    @Override
    @SuppressWarnings("unchecked")
    public C.List<T> prepend(Collection<? extends T> collection) {
        if (collection instanceof C.List) {
            return prependList((C.List<T>) collection);
        }
        return unLazyPrepend(collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> prepend(Iterable<? extends T> iterable) {
        if (iterable instanceof C.List) {
            return prependList((C.List<T>) iterable);
        } else if (iterable instanceof C.Sequence) {
            return prepend((C.Sequence<T>) iterable);
        } else if (iterable instanceof Collection) {
            return prepend((Collection<? extends T>) iterable);
        } else if (isLazy()) {
            return CompositeSeq.of(IterableSeq.of(iterable), this);
        } else {
            return unLazyPrepend(iterable);
        }
    }

    @Override
    public C.Sequence<T> prepend(Iterator<? extends T> iterator) {
        if (!iterator.hasNext()) {
            return this;
        }
        if (isLazy()) {
            return CompositeSeq.of(C.seq(iterator), this);
        }
        return unLazyAppend(iterator);
    }

    @Override
    public C.Sequence<T> prepend(Enumeration<? extends T> enumeration) {
        if (isLazy()) {
            return CompositeSeq.of(C.seq(enumeration), this);
        }
        return unLazyAppend(enumeration);
    }

    /**
     * {@inheritDoc}
     * This method will NOT change the underline list
     *
     * @param seq the sequence to be prepended
     * @return the prepended sequence
     */
    @Override
    public C.Sequence<T> prepend(C.Sequence<? extends T> seq) {
        if (seq instanceof C.List) {
            return prependList((C.List<T>) seq);
        }
        if (isLazy()) {
            return new CompositeSeq<T>(seq, this);
        }
        return unLazyPrepend(seq);
    }

    protected C.ReversibleSequence<T> prependReversibleSeq(C.ReversibleSequence<T> seq) {
        if (seq instanceof C.List) {
            return prependList((C.List<T>) seq);
        }
        // TODO support lazy append reversible sequence
        return unLazyPrepend(seq);
    }

    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        return prependReversibleSeq(seq);
    }

    protected C.List<T> prependList(C.List<T> list) {
        if (isLazy()) {
            return CompositeList.of(list, this);
        }
        return unLazyPrepend(list);
    }

    /**
     * {@inheritDoc}
     * This method will NOT change the underline list
     */
    @Override
    public C.List<T> prepend(C.List<T> list) {
        return prependList(list);
    }

    /**
     * {@inheritDoc}
     * For mutable list, this method will insert the
     * element at {@code 0} position.
     */
    @Override
    public C.List<T> prepend(T t) {
        if (isMutable()) {
            add(0, t);
            return this;
        }
        // immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() + 1);
            lb.add(t);
            lb.addAll(this);
            return lb.toList();
        }
        // readonly but mutable
        C.List<T> l = C.newSizedList(size() + 1);
        l.add(t);
        l.addAll(this);
        return l;
    }

    @Override
    public <R> R reduce(R identity, $.Func2<R, T, R> accumulator) {
        return reduceLeft(identity, accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, $.Func2<R, T, R> accumulator) {
        R ret = identity;
        for (T t : this) {
            ret = accumulator.apply(ret, t);
        }
        return ret;
    }

    @Override
    public <R> R reduceRight(R identity, $.Func2<R, T, R> accumulator) {
        R ret = identity;
        Iterator<T> i = reverseIterator();
        while (i.hasNext()) {
            ret = accumulator.apply(ret, i.next());
        }
        return ret;
    }

    @Override
    public $.Option<T> reduce($.Func2<T, T, T> accumulator) {
        return reduceLeft(accumulator);
    }

    private $.Option<T> reduceIterator(Iterator<T> itr, $.Func2<T, T, T> accumulator) {
        if (!itr.hasNext()) {
            return $.none();
        }
        T ret = itr.next();
        while (itr.hasNext()) {
            ret = accumulator.apply(ret, itr.next());
        }
        return $.some(ret);
    }

    @Override
    public $.Option<T> reduceLeft($.Func2<T, T, T> accumulator) {
        return reduceIterator(iterator(), accumulator);
    }

    @Override
    public $.Option<T> reduceRight($.Func2<T, T, T> accumulator) {
        return reduceIterator(reverseIterator(), accumulator);
    }

    private $.Option<T> findIterator(Iterator<T> itr, $.Function<? super T, Boolean> predicate) {
        while (itr.hasNext()) {
            T t = itr.next();
            if (predicate.apply(t)) {
                return $.some(t);
            }
        }
        return $.none();
    }

    public $.Option<T> findFirst($.Function<? super T, Boolean> predicate) {
        return findIterator(iterator(), predicate);
    }

    @Override
    public $.Option<T> findLast($.Function<? super T, Boolean> predicate) {
        return findIterator(reverseIterator(), predicate);
    }

    @Override
    public <T2> C.List<$.Binary<T, T2>> zip(List<T2> list) {
        return new ZippedList<>(this, list);
    }

    @Override
    public <T2> C.List<$.Binary<T, T2>> zipAll(List<T2> list, T def1, T2 def2) {
        return new ZippedList<>(this, list, def1, def2);
    }

    @Override
    public C.Sequence<$.Binary<T, Integer>> zipWithIndex() {
        return new ZippedSeq<>(this, new IndexIterable(this));
    }

    @Override
    public <T2> C.Sequence<? extends $.Binary<T, T2>> zip(Iterable<T2> iterable) {
        if (iterable instanceof List) {
            return zip((List<T2>) iterable);
        }
        return new ZippedSeq<>(this, iterable);
    }

    @Override
    public <T2> C.Sequence<? extends $.Binary<T, T2>> zipAll(Iterable<T2> iterable, T def1, T2 def2) {
        if (iterable instanceof List) {
            return zipAll((List<T2>) iterable, def1, def2);
        }
        return new ZippedSeq<>(this, iterable, def1, def2);
    }

    @Override
    public <T2> C.ReversibleSequence<$.Binary<T, T2>> zip(C.ReversibleSequence<T2> rseq) {
        if (rseq instanceof C.List) {
            return zip((java.util.List<T2>) rseq);
        }
        return new ZippedRSeq<>(this, rseq);
    }

    @Override
    public <T2> C.ReversibleSequence<$.Binary<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, T def1, T2 def2) {
        if (rseq instanceof C.List) {
            return zipAll((java.util.List<T2>) rseq, def1, def2);
        }
        return new ZippedRSeq<>(this, rseq, def1, def2);
    }

    @Override
    public int count(T t) {
        if (sorted) {
            int pos = indexOf(t);
            if (pos < 0) {
                return 0;
            }
            int n = 1;
            for (int i = pos + 1; i < size(); ++i) {
                if ($.eq(t, get(i))) {
                    n++;
                } else {
                    break;
                }
            }
            return n;
        }
        return SequenceBase.count(this, t);
    }

    @Override
    public <K, V> C.Map<K, V> toMap(Osgl.Function<? super T, ? extends K> keyExtractor, Osgl.Function<? super T, ? extends V> valExtractor) {
        C.Map<K, V> map = C.newMap();
        for (T v : this) {
            map.put(keyExtractor.apply(v), valExtractor.apply(v));
        }
        return map;
    }

    @Override
    public <K> C.Map<K, T> toMapByVal(Osgl.Function<? super T, ? extends K> keyExtractor) {
        C.Map<K, T> map = C.newMap();
        for (T v : this) {
            map.put(keyExtractor.apply(v), v);
        }
        return map;
    }

    @Override
    public <V> C.Map<T, V> toMapByKey(Osgl.Function<? super T, ? extends V> valExtractor) {
        C.Map<T, V> map = C.newMap();
        for (T v : this) {
            map.put(v, valExtractor.apply(v));
        }
        return map;
    }

    int modCount() {
        return modCount;
    }

    void removeRange2(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }
}

class SubList<E> extends ListBase<E> implements C.List<E> {
    private ListBase<E> l;
    private int offset;
    private int size;
    private int expectedModCount;

    SubList(ListBase<E> list, int fromIndex, int toIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
        l = list;
        offset = fromIndex;
        size = toIndex - fromIndex;
        expectedModCount = l.modCount();
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return l.features();
    }

    public E set(int index, E element) {
        rangeCheck(index);
        checkForComodification();
        return l.set(index + offset, element);
    }

    public E get(int index) {
        rangeCheck(index);
        checkForComodification();
        return l.get(index + offset);
    }

    public int size() {
        checkForComodification();
        return size;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();
        checkForComodification();
        l.add(index + offset, element);
        expectedModCount = l.modCount();
        size++;
        modCount++;
    }

    public E remove(int index) {
        rangeCheck(index);
        checkForComodification();
        E result = l.remove(index + offset);
        expectedModCount = l.modCount();
        size--;
        modCount++;
        return result;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        checkForComodification();
        l.removeRange2(fromIndex + offset, toIndex + offset);
        expectedModCount = l.modCount();
        size -= (toIndex - fromIndex);
        modCount++;
    }

    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size);
        int cSize = c.size();
        if (cSize == 0)
            return false;

        checkForComodification();
        l.addAll(offset + index, c);
        expectedModCount = l.modCount();
        size += cSize;
        modCount++;
        return true;
    }

    public Iterator<E> iterator() {
        return listIterator();
    }

    public ListIterator<E> listIterator(final int index) {
        checkForComodification();
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size);

        return new ListIterator<E>() {
            private ListIterator<E> i = l.listIterator(index + offset);

            public boolean hasNext() {
                return nextIndex() < size;
            }

            public E next() {
                if (hasNext())
                    return i.next();
                else
                    throw new NoSuchElementException();
            }

            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            public E previous() {
                if (hasPrevious())
                    return i.previous();
                else
                    throw new NoSuchElementException();
            }

            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            public void remove() {
                i.remove();
                expectedModCount = l.modCount();
                size--;
                modCount++;
            }

            public void set(E e) {
                i.set(e);
            }

            public void add(E e) {
                i.add(e);
                expectedModCount = l.modCount();
                size++;
                modCount++;
            }
        };
    }

    public C.List<E> subList(int fromIndex, int toIndex) {
        if (is(C.Feature.RANDOM_ACCESS)) {
            return new RandomAccessSubList<E>(this, fromIndex, toIndex);
        } else {
            return new SubList<E>(this, fromIndex, toIndex);
        }
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index +
                    ",Size: " + size);
    }

    protected void checkForComodification() {
        if (l.modCount() != expectedModCount)
            throw new ConcurrentModificationException();
    }
}

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
    RandomAccessSubList(ListBase<E> list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public C.List<E> subList(int fromIndex, int toIndex) {
        return new RandomAccessSubList<E>(this, fromIndex, toIndex);
    }
}
