package org.osgl.util;

import org.osgl._;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
class ImmutableList<T> extends ListBase<T> implements C.List<T>, RandomAccess {
    private final T[] data_;

    /**
     * Construct the ImmutableList with an array. The array will be used
     * directly as the backing data of this list. No data copy happen
     *
     * @param data an array of element backing this list
     */
    private ImmutableList(T[] data) {
        E.NPE(data);
        data_ = data;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY, C.Feature.LIMITED, C.Feature.ORDERED, C.Feature.IMMUTABLE, C.Feature.LAZY);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data_.length;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) > -1;
    }

    @Override
    public Object[] toArray() {
        T[] da = data_;
        int sz = da.length;
        if (0 == sz) {
            return new Object[0];
        }
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
            for (int i = sz; i < sza; ++i) {
                a[i] = null;
            }
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



    private class SubLst extends ListBase1<T> implements C.List<T> {

    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public <R> C.List<R> map(_.Function<? super T, ? extends R> mapper) {
        //TODO ...
        return null;
    }

    @Override
    public <R> C.List<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        //TODO ...
        return null;
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
    public _.Option<T> findLast(_.Function<? super T, Boolean> predicate) {
        T[] ta = data_;
        for (int i = ta.length - 1; i >= 0; --i) {
            T t = ta[i];
            if (predicate.apply(t)) {
                return _.some(t);
            }
        }
        return _.none();
    }

    @Override
    public C.List<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        return null;
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        //TODO ...
        return null;
    }

    @Override
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> without(Collection<? extends T> col) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> reverse() {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> prepend(C.List<T> list) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> prepend(T t) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> append(C.List<T> list) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> append(T t) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> insert(T t, int index) throws IndexOutOfBoundsException {
        //TODO ...
        return null;
    }

    @Override
    public Cursor<T> locateLast(_.Function<T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public Cursor<T> locate(_.Function<T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public Cursor<T> locateFirst(_.Function<T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> filter(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> drop(int n) throws IndexOutOfBoundsException {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> tail(int n) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> tail() {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> take(int n) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> head(int n) {
        //TODO ...
        return null;
    }

    static <T> C.List<T> of(T[] data) {
        E.NPE(data);
        if (data.length == 0) {
            return Nil.list();
        } else {
            return new ImmutableList<T>(data);
        }
    }

    static <T> ImmutableList<T> copyOf(T[] data) {
        T[] a = Arrays.copyOf(data, data.length);
        return new ImmutableList<T>(a);
    }
}
