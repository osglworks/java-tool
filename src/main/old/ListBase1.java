package org.osgl.util;

import org.osgl._;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 27/10/13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class ListBase1<T> extends ReversibleSeqBase<T> implements C.List<T> {

    private class Itr implements Iterator<T> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size();
        }

        public T next() {
            checkForComodification();
            try {
                T next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ListBase1.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class ListItr extends Itr implements ListIterator<T> {
        ListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public T previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                T previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public void set(T t) {
            if (lastRet == -1)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ListBase1.this.set(lastRet, t);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(T t) {
            checkForComodification();

            try {
                ListBase1.this.add(cursor++, t);
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }


    protected transient int modCount;


    protected ListBase1<T> setFeature(C.Feature feature) {
        super.setFeature(feature);
        return this;
    }

    protected ListBase1<T> unsetFeature(C.Feature feature) {
        super.unsetFeature(feature);
        return this;
    }

    @Override
    public C.List<T> accept(_.Function<? super T, ?> visitor) {
        return _.cast(super.accept(visitor));
    }

    @Override
    public C.List<T> acceptLeft(_.Function<? super T, ?> visitor) {
        return _.cast(super.acceptLeft(visitor));
    }

    @Override
    public C.List<T> acceptRight(_.Function<? super T, ?> visitor) {
        return _.cast(super.acceptRight(visitor));
    }

    @Override
    public <R> C.List<R> map(_.Function<? super T, ? extends R> mapper) {
        if (is(C.Feature.LAZY)) {
            return new MappedList<T, R>(this, mapper);
        }
        _.F1<? super T, ? extends R> m = _.f1(mapper);
        if (is(C.Feature.IMMUTABLE)) {
            ListBuilder<R> lb = new ListBuilder<R>(size());
            forEach(m.andThen(C.F.addTo(lb)));
            return lb.toList();
        } else {
            C.List<R> ret = C.newList(size());
            forEach(m.andThen(C.F.addTo(ret)));
            return ret;
        }
    }

    @Override
    public <R> C.List<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        if (is(C.Feature.LAZY)) {
            return new FlatMappedList<T, R>(this, mapper);
        }
        _.F1<? super T, ? extends Iterable<? extends R>> m = _.f1(mapper);
        int capacity = size() * 2;
        if (is(C.Feature.IMMUTABLE)) {
            ListBuilder<R> lb = new ListBuilder<R>(capacity);
            forEach(m.andThen(C.F.forEach(C.F.addTo(lb))));
            return lb.toList();
        } else {
            C.List<R> ret = C.newList(capacity);
            forEach(m.andThen(C.F.forEach(C.F.addTo(ret))));
            return ret;
        }
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) > -1;
    }

    /**
     * The implementation is copied from JDK6 AbstractCollection
     * <p/>
     * <p>Reallocates the array being used within toArray when the iterator
     * returned more elements than expected, and finishes filling it from
     * the iterator.</p>
     *
     * @param r  the array, replete with previously stored elements
     * @param it the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any
     *         further elements returned by the iterator, trimmed to size
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = ((cap / 2) + 1) * 3;
                if (newCap <= cap) { // integer overflow
                    if (cap == Integer.MAX_VALUE)
                        throw new OutOfMemoryError
                                ("Required array size too large");
                    newCap = Integer.MAX_VALUE;
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T) it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    /**
     * The implementation is copied from
     * {@link java.util.AbstractCollection#toArray()}
     * <p>This implementation returns an array containing all the elements
     * returned by this collection's iterator, in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * The length of the returned array is equal to the number of elements
     * returned by the iterator, even if the size of this collection changes
     * during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     * <p/>
     * <p>This method is equivalent to:
     * <p/>
     * <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray();
     * }</pre>
     */
    @Override
    public Object[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        Object[] r = new Object[size()];
        Iterator<T> it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext())    // fewer elements than expected
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * The implementation is copied from
     * {@link java.util.AbstractCollection#toArray(Object[])}
     * <p/>
     * <p>This implementation returns an array containing all the elements
     * returned by this collection's iterator in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * If the number of elements returned by the iterator is too large to
     * fit into the specified array, then the elements are returned in a
     * newly allocated array with length equal to the number of elements
     * returned by the iterator, even if the size of this collection
     * changes during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     * <p/>
     * <p>This method is equivalent to:
     * <p/>
     * <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray(a);
     * }</pre>
     *
     * @throws ArrayStoreException  {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        T[] r = a.length >= size ? a :
                (T[]) java.lang.reflect.Array
                                       .newInstance(a.getClass().getComponentType(), size);
        Iterator<?> it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                if (a != r)
                    return Arrays.copyOf(r, i);
                r[i] = null; // null-terminate
                return r;
            }
            r[i] = (T) it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    @Override
    public boolean add(T t) {
        add(size(), t);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean added = false;
        for (T t : c) {
            add(t);
            added = true;
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean b = false;
        for (T t : c) {
            add(index, t);
            b = true;
        }
        return b;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean b = false;
        Iterator<?> i = iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (c.contains(o)) {
                i.remove();
                b = true;
            }
        }
        return b;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean b = false;
        Iterator<?> i = iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (!c.contains(o)) {
                i.remove();
                b = true;
            }
        }
        return b;
    }

    @Override
    public void clear() {
        Iterator<?> i = iterator();
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
    }

    @Override
    public abstract T get(int index);

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
        ListIterator<T> i = listIterator();
        if (o == null) {
            while (i.hasNext()) {
                if (i.next() == null) {
                    return i.previousIndex();
                }
            }
        } else {
            while (i.hasNext()) {
                if (o.equals(i.next())) {
                    return i.previousIndex();
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        ListIterator<T> i = listIterator(size());
        if (o == null) {
            while (i.hasPrevious()) {
                if (i.previous() == null) {
                    return i.nextIndex();
                }
            }
        } else {
            while (i.hasPrevious()) {
                if (o.equals(i.previous())) {
                    return i.nextIndex();
                }
            }
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        if (index<0 || index>size())
       	  throw new IndexOutOfBoundsException("Index: "+index);

       	return new ListItr(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        //TODO ...
        return null;
    }
}



