package org.osgl.util;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.algo.Algorithms;

import java.io.Serializable;
import java.util.*;

/**
 * An immutable list implementation. This class is guaranteed to be NOT empty
 */
class ImmutableList<T> extends ListBase<T>
implements C.List<T>, RandomAccess, Serializable {

    private final T[] data_;

    /**
     * Construct the ImmutableList with an array. The array will be used
     * directly as the backing data of this list. No data copy happen
     *
     * @param data an array of element backing this list
     */
    protected ImmutableList(T[] data) {
        E.NPE(data);
        data_ = data;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY, C.Feature.LIMITED, C.Feature.ORDERED, C.Feature.IMMUTABLE, C.Feature.LAZY, C.Feature.PARALLEL, C.Feature.RANDOM_ACCESS);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data_.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) > -1;
    }

    @Override
    public Object[] toArray() {
        T[] da = data_;
        int sz = da.length;
        Object[] ret = new Object[sz];
        System.arraycopy(da, 0, ret, 0, sz);

        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(T1[] a) {
        int sza = a.length;
        T[] da = data_;
        int sz = da.length;
        if (sza < sz) {
            return (T1[]) Arrays.copyOf(da, sz, a.getClass());
        }
        System.arraycopy(da, 0, a, 0, sz);
        if (sza > sz) {
            a[sz] = null;
        }
        return a;
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(c)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        return data_[index];
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }



    @Override
    public int indexOf(Object o) {
        T[] da = data_;
        int sz = da.length;
        if (o == null) {
            for (int i = 0; i < sz; i++)
                if (da[i] == null)
                    return i;
        } else {
            for (int i = 0; i < sz; i++)
                if (o.equals(da[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        T[] da = data_;
        int sz = da.length;
        if (o == null) {
            for (int i = sz - 1; i >= 0; i--)
                if (da[i] == null)
                    return i;
        } else {
            for (int i = sz - 1; i >= 0; i--)
                if (o.equals(da[i]))
                    return i;
        }
        return -1;
    }

    private class Itr implements Iterator<T> {
        protected int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor != data_.length;
        }

        @Override
        public T next() {
            T[] da = data_;
            if (cursor >= da.length) {
                throw new NoSuchElementException();
            }
            int i = cursor++;
            return da[i];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    private class LstItr extends Itr implements ListIterator<T> {

        LstItr() {this(0);}

        LstItr(int index) {
            if (index < 0 || index > data_.length) {
                throw new IndexOutOfBoundsException();
            }
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public T previous() {
            if (cursor < 0) {
                throw new NoSuchElementException();
            }
            cursor--;
            return data_[cursor];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        return new LstItr();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new LstItr(index);
    }

    @Override
    public <R> C.List<R> map($.Function<? super T, ? extends R> mapper) {
        if (isLazy()) {
            return MappedList.of(this, mapper);
        }
        int sz = size();
        ListBuilder<R> lb = new ListBuilder<R>(sz);
        forEach($.f1(mapper).andThen(C.F.addTo(lb)));
        return lb.toList();
    }

    @Override
    public <R> C.List<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        // TODO: handle lazy operation
        int sz = size();
        ListBuilder<R> lb = new ListBuilder<R>(sz * 3);
        forEach($.f1(mapper).andThen(C.F.addAllTo(lb)));
        return lb.toList();
    }

    @Override
    public C.Sequence<T> append(Iterable<? extends T> iterable) {
        return super.append(iterable);
    }

    private C.List<T> unLazyAppend(Collection<? extends T> collection) {
        int szB = collection.size();
        if (szB == 0) {
            return this;
        }
        int szA = size();
        T[] dataA = data_;
        Object[] dataB = collection.toArray();
        T[] data = $.newArray(dataA, szA + szB);
        System.arraycopy(dataA, 0, data, 0, szA);
        System.arraycopy(dataB, 0, data, szA, szB);
        return of(data);
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
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        if (seq instanceof C.List) {
            return appendList((C.List<T>) seq);
        }
        return super.appendReversibleSeq(seq);
    }

    @Override
    public C.Sequence<T> append(C.Sequence<? extends T> seq) {
        if (seq instanceof C.List) {
            return appendList((C.List<T>) seq);
        }
        if (isLazy()) {
            return CompositeSeq.of(this, seq);
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(this).append(seq);
        return lb.toList();
    }

    @Override
    public C.Sequence<T> append(Iterator<? extends T> iterator) {
        if (isLazy()) {
            return CompositeSeq.of(this, C.seq(iterator));
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(this).append(iterator);
        return lb.toList();
    }

    @Override
    public C.Sequence<T> append(Enumeration<? extends T> enumeration) {
        if (isLazy()) {
            return CompositeSeq.of(this, C.seq(enumeration));
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(this).append(enumeration);
        return lb.toList();
    }

    protected C.List<T> appendList(C.List<T> l) {
        if (isLazy()) {
            return CompositeList.of(this, l);
        }
        if (l instanceof ImmutableList) {
            return appendImmutableList((ImmutableList<T>) l);
        }
        return unLazyAppend(l);
    }

    public C.List<T> append(C.List<T> l) {
        return appendList(l);
    }

    private C.List<T> appendImmutableList(ImmutableList<T> l) {
        int szA = size();
        int szB = l.size();
        T[] dataA = data_;
        T[] data = $.newArray(dataA, szA + szB);
        System.arraycopy(dataA, 0, data, 0, szA);
        System.arraycopy(l.data_, 0, data, szA, szB);
        return of(data);
    }

    public C.List<T> append(ImmutableList<T> l) {
        return appendImmutableList(l);
    }

    private C.List<T> unLazyPrepend(Collection<? extends T> collection) {
        int szB = collection.size();
        if (szB == 0) {
            return this;
        }
        int szA = size();
        T[] dataA = data_;
        Object[] dataB = collection.toArray();
        T[] data = $.newArray(dataA, szA + szB);
        System.arraycopy(dataB, 0, data, 0, szB);
        System.arraycopy(dataA, 0, data, szB, szA);
        return of(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.List<T> prepend(Collection<? extends T> collection) {
        if (collection instanceof C.List) {
            return prependList((C.List<T>) collection);
        } else {
            return unLazyPrepend(collection);
        }
    }

    @Override
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        return super.prependReversibleSeq(seq);
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<? extends T> seq) {
        if (seq instanceof C.List) {
            return prependList((C.List<T>) seq);
        }
        if (isLazy()) {
            return CompositeSeq.of(seq, this);
        }
        ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
        lb.append(seq).append(this);
        return lb.toList();
    }

    public C.List<T> prepend(C.List<T> l) {
        if (isLazy()) {
            return CompositeList.of(l, this);
        }
        if (l instanceof ImmutableList) {
            return prependList((ImmutableList<T>) l);
        }
        return unLazyPrepend(l);
    }

    public C.List<T> prepend(ImmutableList<T> l) {
        if (isLazy()) {
            return CompositeList.of(l, this);
        }
        int szA = size();
        int szB = l.size();
        T[] myData = data_;
        T[] data = $.newArray(myData, szA + szB);
        System.arraycopy(l.data_, 0, data, 0, szB);
        System.arraycopy(myData, 0, data, szB, szA);
        return of(data);
    }

    private class ReverseItr implements Iterator<T> {
        private LstItr itr;

        ReverseItr() {
            itr = new LstItr(size());
        }

        @Override
        public boolean hasNext() {
            return itr.hasPrevious();
        }

        @Override
        public T next() {
            return itr.previous();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<T> reverseIterator() {
        return new ReverseItr();
    }

    @Override
    public $.Option<T> findLast($.Function<? super T, Boolean> predicate) {
        T[] ta = data_;
        for (int i = ta.length - 1; i >= 0; --i) {
            T t = ta[i];
            if (predicate.apply(t)) {
                return $.some(t);
            }
        }
        return $.none();
    }

    @Override
    public C.List<T> takeWhile($.Function<? super T, Boolean> predicate) {
        int sz = size();
        ListBuilder<T> lb = new ListBuilder<T>(sz);
        for (T t : this) {
            if (predicate.apply(t)) {
                lb.add(t);
            } else {
                break;
            }
        }
        return lb.toList();
    }

    @Override
    public C.List<T> without(Collection<? super T> col) {
        int sz = size();
        T[] data = data_;
        ListBuilder<T> lb = new ListBuilder<T>(sz);
        for (int i = 0; i < sz; ++i) {
            T t = data[i];
            if (!col.contains(t)) {
                lb.add(t);
            }
        }
        return lb.toList();
    }

    @Override
    public C.List<T> without(T element) {
        int sz = size();
        T[] data = data_;
        ListBuilder<T> lb = new ListBuilder<T>(sz);
        for (int i = 0; i < sz; ++i) {
            T t = data[i];
            if ($.ne(t, element)) {
                lb.add(t);
            }
        }
        return lb.toList();
    }

    @Override
    public C.List<T> without(T element, T... elements) {
        int len = elements.length;
        if (len == 0) return without(element);
        int sz = size();
        T[] data = data_;
        ListBuilder<T> lb = new ListBuilder<T>(sz);
        T t0 = elements[0];
        boolean c = false;
        if (len < 8 && t0 instanceof Comparable) {
            Arrays.sort(elements);
            c = true;
        }
        if (c) {
            for (int i = 0; i < sz; ++i) {
                T t = data[i];
                if ($.eq(t, element)) {
                    continue;
                }
                int id = Arrays.binarySearch(elements, t);
                if (id > -1) continue;
                lb.add(t);
            }
        } else {
            for (int i = 0; i < sz; ++i) {
                T t = data[i];
                if ($.eq(t, element)) {
                    continue;
                }
                boolean found = false;
                for (int j = 0; j < len; ++j) {
                    if ($.eq(elements[j], t)) {
                        found = true;
                        break;
                    };
                }
                if (!found) {
                    lb.add(t);
                }
            }
        }
        return lb.toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.List<T> reverse() {
        if (isLazy()) {
            return ReverseList.wrap(this);
        }
        T[] data = (T[]) Algorithms.ARRAY_REVERSE.apply(data_, 0, data_.length);
        return of(data);
    }

    @Override
    public C.List<T> prepend(T t) {
        int sz = size();
        T[] myData = data_;
        T[] data = $.newArray(myData, sz + 1);
        data[0] = t;
        System.arraycopy(myData, 0, data, 1, sz);
        return of(data);
    }

    @Override
    public C.List<T> append(T t) {
        int sz = size();
        T[] myData = data_;
        T[] data = $.newArray(myData, sz + 1);
        data[sz] = t;
        System.arraycopy(myData, 0, data, 0, sz);
        return of(data);
    }

    @Override
    public C.List<T> insert(int index, T t) throws IndexOutOfBoundsException {
        T[] myData = data_;
        int sz = data_.length;
        if (sz < Math.abs(index)) {
            throw new IndexOutOfBoundsException();
        }
        if (index < 0) {
            index = sz + index;
        }

        T[] data = $.newArray(myData, sz + 1);


        System.arraycopy(myData, 0, data, 0, index);
        data[index] = t;
        System.arraycopy(myData, index, data, index + 1, sz - index);
        return of(data);
    }

    @Override
    public C.List<T> insert(int index, T... ta) throws IndexOutOfBoundsException {
        if (ta.length == 0) {
            return this;
        }
        T[] myData = data_;
        int sz = data_.length;
        if (sz < Math.abs(index)) {
            throw new IndexOutOfBoundsException();
        }
        if (index < 0) {
            index = sz + index;
        }

        int delta = ta.length;
        T[] data = $.newArray(myData, sz + delta);
        if (index > 0) {
            System.arraycopy(myData, 0, data, 0, index);
        }
        System.arraycopy(ta, 0, data, index, delta);
        if (index < sz) {
            System.arraycopy(myData, index, data, index + delta, sz - index);
        }
        return of(data);
    }

    @Override
    public C.List<T> insert(int index, List<T> subList) throws IndexOutOfBoundsException {
        if (subList.isEmpty()) {
            return this;
        }
        T[] myData = data_;
        int sz = data_.length;
        if (sz < Math.abs(index)) {
            throw new IndexOutOfBoundsException();
        }
        if (index < 0) {
            index = sz + index;
        }

        int delta = subList.size();
        T[] data = $.newArray(myData, sz + delta);
        if (index > 0) {
            System.arraycopy(myData, 0, data, 0, index);
        }
        for (int i = 0; i < delta; ++i) {
            data[index + i] = subList.get(i);
        }
        if (index < sz) {
            System.arraycopy(myData, index, data, index + delta, sz - index);
        }
        return of(data);
    }

    private class Csr implements Cursor<T> {

        private int id_;

        Csr() {this(-1);}

        Csr(int index) {
            if (index < -1) {
                index = -1;
            } else if (index > size()) {
                index = size();
            }
            id_ = index;
        }

        @Override
        public boolean isDefined() {
            int id = id_;
            return id > -1 && id < size();
        }

        @Override
        public int index() {
            return id_;
        }

        @Override
        public T get() throws NoSuchElementException {
            if (!isDefined()) {
                throw new NoSuchElementException();
            }
            return data_[id_];
        }

        @Override
        public boolean hasNext() {
            return id_ < size() - 1;
        }

        @Override
        public boolean hasPrevious() {
            return id_ > 0;
        }

        @Override
        public Cursor<T> parkLeft() {
            id_ = -1;
            return this;
        }

        @Override
        public Cursor<T> parkRight() {
            id_ = size();
            return this;
        }

        @Override
        public Cursor<T> forward() {
            if (id_ >= size()) {
                id_ = size();
                throw new UnsupportedOperationException();
            }
            id_++;
            return this;
        }

        @Override
        public Cursor<T> backward() throws UnsupportedOperationException {
            if (id_ <= -1) {
                id_ = -1;
                throw new UnsupportedOperationException();
            }
            id_--;
            return this;
        }

        @Override
        public Cursor<T> set(T t) throws IndexOutOfBoundsException, NullPointerException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cursor<T> drop() throws NoSuchElementException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cursor<T> prepend(T t) throws IndexOutOfBoundsException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cursor<T> append(T t) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Cursor<T> locateLast($.Function<T, Boolean> predicate) {
        int sz = size();
        T[] data = data_;
        for (int i = sz - 1; i >= 0; --i) {
            T t = data[i];
            if (predicate.apply(t)) {
                return new Csr(i);
            }
        }
        return new Csr(-1);
    }

    @Override
    public Cursor<T> locate($.Function<T, Boolean> predicate) {
        return locateFirst(predicate);
    }

    @Override
    public Cursor<T> locateFirst($.Function<T, Boolean> predicate) {
        int sz = size();
        T[] data = data_;
        for (int i = 0; i < sz; ++i) {
            T t = data[i];
            if (predicate.apply(t)) {
                return new Csr(i);
            }
        }
        return new Csr(sz);
    }

    @Override
    public C.List<T> filter($.Function<? super T, Boolean> predicate) {
        // TODO: handle lazy operation
        int sz = size();
        if (0 == sz) {
            return Nil.list();
        }
        T[] data = $.newArray(data_);
        int cursor = 0;
        for (int i = 0; i < sz; ++i) {
            T t = data_[i];
            if (predicate.apply(t)) {
                data[cursor++] = t;
            }
        }
        if (0 == cursor) {
            return Nil.list();
        }
        data = Arrays.copyOf(data, cursor);
        return of(data);
    }

    @Override
    public C.List<T> dropWhile($.Function<? super T, Boolean> predicate) {
        //TODO: handle lazy operation
        int sz = size();
        $.Function<T, Boolean> f = $.F.negate(predicate);
        Cursor<T> cursor = locateFirst(f);
        if (!cursor.isDefined()) {
            return Nil.list();
        }
        int id = cursor.index();
        return subList(id, size());
    }

    @Override
    public C.List<T> drop(int n) throws IndexOutOfBoundsException {
        int size = size();
        if (n < 0) {
            n = -n;
            if (n >= size) return C.list();
            return take(size - n);
        }
        if (0 == n) {
            return this;
        }
        if (n >= size) {
            return Nil.list();
        }
        return subList(n, size());
    }

    @Override
    public C.List<T> tail(int n) {
        if (n < 0) {
            return head(-n);
        }
        int sz = size();
        if (n >= sz) {
            return this;
        }
        return subList(sz - n, sz);
    }

    @Override
    public C.List<T> tail() {
        int sz = size();
        if (sz == 0) {
            throw new UnsupportedOperationException();
        }
        return subList(1, sz);
    }

    @Override
    public C.List<T> take(int n) {
        if (n < 0) {
            return tail(-n);
        }
        int sz = size();
        if (n >= sz) {
            return this;
        }
        return subList(0, n);
    }

    @Override
    protected void forEachLeft($.Function<? super T, ?> visitor) throws $.Break {
        int sz = size();
        T[] data = data_;
        for (int i = 0; i < sz; ++i) {
            T t = data[i];
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    @Override
    protected void forEachRight($.Function<? super T, ?> visitor) throws $.Break {
        int sz = size();
        T[] data = data_;
        for (int i = sz - 1; i >= 0; --i) {
            T t = data[i];
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    @Override
    public $.Option<T> findOne($.Function<? super T, Boolean> predicate) {
        //todo parallel finding
        int sz = size();
        T[] data = data_;
        for (int i = 0; i < sz; ++i) {
            T t = data[i];
            if (predicate.apply(t)) {
                return $.some(t);
            }
        }
        return $.none();
    }

    @Override
    public T head() throws NoSuchElementException {
        return data_[0];
    }

    @Override
    public T last() throws NoSuchElementException {
        return data_[size() - 1];
    }

    @Override
    public <R> R reduce(R identity, $.Func2<R, T, R> accumulator) {
        // TODO: parallel
        return reduceLeft(identity, accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, $.Func2<R, T, R> accumulator) {
        int sz = size();
        R ret = identity;
        T[] data = data_;
        for (int i = 0; i < sz; ++i) {
            ret = accumulator.apply(ret, data[i]);
        }
        return ret;
    }

    @Override
    public <R> R reduceRight(R identity, $.Func2<R, T, R> accumulator) {
        int sz = size();
        R ret = identity;
        T[] data = data_;
        for (int i = sz - 1; i >= 0; --i) {
            ret = accumulator.apply(ret, data[i]);
        }
        return ret;
    }

    @Override
    public $.Option<T> reduce($.Func2<T, T, T> accumulator) {
        // TODO parallel
        return reduceLeft(accumulator);
    }

    @Override
    public $.Option<T> reduceLeft($.Func2<T, T, T> accumulator) {
        int sz = size();
        T[] data = data_;
        T ret = data[0];
        for (int i = 1; i < sz; ++i) {
            ret = accumulator.apply(ret, data[i]);
        }
        return $.some(ret);
    }

    @Override
    public $.Option<T> reduceRight($.Func2<T, T, T> accumulator) {
        int sz = size();
        T[] data = data_;
        T ret = data[sz - 1];
        for (int i = sz - 2; i >= 0; --i) {
            ret = accumulator.apply(ret, data[i]);
        }
        return $.some(ret);
    }

    @Override
    int modCount() {
        return 0;
    }

    @Override
    void removeRange2(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    static <T> C.List<T> of(T[] data) {
        E.NPE(data);
        int len = data.length;
        if (len == 0) {
            return Nil.list();
        } else if (len == 1) {
            return $.val(data[0]);
        } else {
            return new ImmutableList<T>(data);
        }
    }

    static <T> C.List<T> copyOf(T[] data) {
        int sz = data.length;
        if (sz == 0) {
            return Nil.list();
        }
        T[] a = Arrays.copyOf(data, sz);
        return new ImmutableList<T>(a);
    }

}

class ImmutableSubList<T> extends RandomAccessSubList<T> {

    ImmutableSubList(ListBase<T> list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    @Override
    public C.List<T> subList(int fromIndex, int toIndex) {
        return new ImmutableSubList<T>(this, fromIndex, toIndex);
    }

    @Override
    protected void checkForComodification() {
        return;
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Iterable<? extends T> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

}

class ImmutableStringList extends ImmutableList<String> implements S.List {

    public ImmutableStringList(String[] data) {
        super(data);
    }

    static S.List of(String[] data) {
        E.NPE(data);
        int len = data.length;
        if (len == 0) {
            return new Nil.EmptyStringList();
        } else if (len == 1) {
            return S.val(data[0]);
        } else {
            return new ImmutableStringList(data);
        }
    }

    static S.List of(java.util.Collection<String> strings) {
        return of(strings.toArray(new String[strings.size()]));
    }

    static S.List of(Iterable<String> strings) {
        if (strings instanceof S.List) {
            return $.cast(strings);
        }
        if (strings instanceof Collection) {
            return of((Collection) strings);
        }
        return of(C.list(strings));
    }


}