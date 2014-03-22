package org.osgl.util;

import org.osgl._;

import java.util.*;

/**
 * A simple array based List that support append element to the tail of
 * the list only.
 *
 * <p>Notes:</p>
 * <ul>
 * <li>This class is NOT thread safe. Don't use it in multiple thread context</li>
 * <li>{@link List#remove(int) removeX} methods are not supported</li>
 * <li>After running {@link #toList()} the builder is obsolete and cannot be used anymore</li>
 * <li>{@link #clear()} method can be called to reset the builder before calling {@link #toList()}</li>
 * </ul>
 */
public class ListBuilder<T> extends AbstractList<T> implements RandomAccess {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    Object[] buf;
    int size;

    ListBuilder() {
        this(10);
    }

    @SuppressWarnings("unchecked")
    private ListBuilder(Collection<? extends T> collection) {
        int len = collection.size();
        if (len == 0) {
            buf = new Object[10];
        } else {
            Object[] a0 = collection.toArray();
            T t = collection.iterator().next();
            buf = new Object[len];
            System.arraycopy(a0, 0, buf, 0, len);
            size = len;
        }
    }

    @SuppressWarnings("unchecked")
    public ListBuilder(int initialCapacity) {
        if (initialCapacity < 0 || initialCapacity > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException();
        }
        buf = (T[]) new Object[initialCapacity];
    }

    private void checkState() {
        if (null == buf) {
            throw new IllegalStateException("ListBuilder is consumed");
        }
    }

    private void trimToSize() {
        modCount++;
        int oldCapacity = buf.length;
        if (size < oldCapacity) {
            buf = Arrays.copyOf(buf, size);
        }
    }

    private void ensureCapacity(int capacity) {
        if (capacity > MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }
        int oldCapacity = buf.length;
        if (capacity > oldCapacity) {
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity < capacity) {
                newCapacity = capacity;
            }
            // minCapacity is usually close to size, so this is a win:
            buf = Arrays.copyOf(buf, newCapacity);
        }
    }

    @Override
    public int size() {
        checkState();
        return size;
    }

    @Override
    public T get(int index) {
        checkState();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (T)buf[index];
    }

    @Override
    public boolean add(T t) {
        checkState();
        int sz = size++;
        ensureCapacity(sz + 1);
        buf[sz] = t;
        return true;
    }

    @Override
    public Object[] toArray() {
        checkState();
        return Arrays.copyOf(buf, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        checkState();
        final int sz = size, len = a.length;
        if (len < sz) {
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(buf, sz, a.getClass());
        }
        System.arraycopy(buf, 0, a, 0, sz);
        if (len > sz) {
            a[sz] = null;
        }
        return a;
    }

    @Override
    public boolean addAll(Collection <? extends T> c) {
        checkState();
        int cSz = c.size(), oldSz = size;
        if (cSz < 1) {
            return false;
        }
        size += cSz;
        ensureCapacity(size);
        Object[] newData = c.toArray();
        System.arraycopy(newData, 0, buf, oldSz, cSz);
        return true;

//        Object[] a = c.toArray();
//               int numNew = a.length;
//       	ensureCapacity(size + numNew);  // Increments modCount
//               System.arraycopy(a, 0, buf, size, numNew);
//               size += numNew;
//       	return numNew != 0;
    }

    public boolean addAll(ListBuilder<? extends T> c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, ListBuilder<? extends T> c) {
        checkState();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (null == c.buf) {
            return false;
        }
        int cSz = c.size(), oldSz = size;
        if (cSz == 0) {
            return false;
        }
        size += cSz;
        ensureCapacity(size);
        Object[] data = buf;
        if (index < oldSz) {
            System.arraycopy(data, index, data, index + cSz, oldSz - index);
        }
        ListBuilder<T> that = (ListBuilder<T>) c;
        Object[] newData = that.buf;
        System.arraycopy(newData, 0, data, index, cSz);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkState();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        int cSz = c.size(), oldSz = size;
        if (cSz == 0) {
            return false;
        }
        size += cSz;
        ensureCapacity(size);
        Object[] data = buf;
        if (index < oldSz) {
            System.arraycopy(data, index, data, index + cSz, oldSz - index);
        }
        Object[] newData = c.toArray();
        System.arraycopy(newData, 0, data, index, cSz);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        buf = (T[]) new Object[10];
        size = 0;
    }

    public ListBuilder<T> append(T t) {
        add(t);
        return this;
    }

    public ListBuilder<T> append(Object[] ta) {
        checkState();
        int len = ta.length, oldSz = size;
        size += len;
        ensureCapacity(size);
        Object[] data = buf;
        System.arraycopy(ta, 0, data, oldSz, len);
        return this;
    }

    public ListBuilder<T> append(Object[] ta1, Object[] ta2) {
        checkState();
        int l1 = ta1.length, l2 = ta2.length, oldSz = size;
        size += l1 + l2;
        ensureCapacity(size);
        Object[] data = buf;
        System.arraycopy(ta1, 0, data, oldSz, l1);
        System.arraycopy(ta2, 0, data, oldSz + l1, l2);
        return this;
    }

    public ListBuilder<T> append(Object[] ta1, Object[] ta2, Object[]... taa) {
        checkState();
        int l1 = ta1.length, l2 = ta2.length, oldSz = size;
        int len = l1 + l2;
        for (Object[] ta : taa) {
            len += ta.length;
        }
        size += len;
        ensureCapacity(size);
        Object[] data = buf;
        System.arraycopy(ta1, 0, data, oldSz, l1);
        System.arraycopy(ta2, 0, data, oldSz += l1, l2);
        oldSz += l2;
        for (Object[] ta : taa) {
            int l = ta.length;
            System.arraycopy(ta, 0, data, oldSz, l);
            oldSz += l;
        }

        return this;
    }

    public ListBuilder<T> append(T t1, T t2) {
        checkState();
        size += 2;
        int sz = size;
        ensureCapacity(sz);
        Object[] data = buf;
        data[sz - 1] = t2;
        data[sz - 2] = t1;
        return this;
    }

    public ListBuilder<T> append(T t1, T t2, T t3) {
        checkState();
        size += 3;
        int sz = size;
        ensureCapacity(sz);
        Object[] data = buf;
        data[sz - 1] = t3;
        data[sz - 2] = t2;
        data[sz - 3] = t1;
        return this;
    }

    public ListBuilder<T> append(T t1, T t2, T t3, T t4) {
        checkState();
        size += 4;
        int sz = size;
        ensureCapacity(sz);
        Object[] data = buf;
        data[sz - 1] = t4;
        data[sz - 2] = t3;
        data[sz - 3] = t2;
        data[sz - 4] = t1;
        return this;
    }

    public ListBuilder<T> append(T t1, T t2, T t3, T t4, T t5) {
        checkState();
        size += 5;
        int sz = size;
        ensureCapacity(sz);
        Object[] data = buf;
        data[sz - 1] = t5;
        data[sz - 2] = t4;
        data[sz - 3] = t3;
        data[sz - 4] = t2;
        data[sz - 5] = t1;
        return this;
    }

    public ListBuilder<T> append(T t1, T t2, T t3, T t4, T t5, T... ta) {
        checkState();
        int len = ta.length, oldSz = size;
        size += (len + 5);
        ensureCapacity(size);
        Object[] data = buf;
        System.arraycopy(ta, 0, data, oldSz + 5, len);
        data[oldSz + 4] = t5;
        data[oldSz + 3] = t4;
        data[oldSz + 2] = t3;
        data[oldSz + 1] = t2;
        data[oldSz] = t1;
        return this;
    }

    public ListBuilder<T> append(Collection<? extends T> col) {
        addAll(col);
        return this;
    }

    public ListBuilder<T> append(Collection<? extends T> c1, Collection<? extends T> c2) {
        checkState();
        int l1 = c1.size(), l2 = c2.size(), oldSz = size;
        size += l1 + l2;
        int sz = size;
        ensureCapacity(sz);
        Object[] data = buf;
        Object[] newData = c1.toArray();
        System.arraycopy(newData, 0, data, oldSz, l1);
        newData = c2.toArray();
        System.arraycopy(newData, 0, data, oldSz += l1, l2);
        return this;
    }

    public ListBuilder<T> append(Collection<? extends T> c1, Collection<? extends T> c2, Collection<? extends T>... ca
    ) {
        checkState();
        int l1 = c1.size(), l2 = c2.size(), oldSz = size;
        int len = l1 + l2;
        for (Collection<? extends T> c : ca) {
            len += c.size();
        }
        size += len;
        int sz = size;
        ensureCapacity(sz);
        Object[] data = buf;

        Object[] newData = c1.toArray();
        System.arraycopy(newData, 0, data, oldSz, l1);

        newData = c2.toArray();
        System.arraycopy(newData, 0, data, oldSz += l1, l2);

        oldSz += l2;
        for (Collection<? extends T> c : ca) {
            newData = c.toArray();
            int l = newData.length;
            System.arraycopy(newData, 0, data, oldSz, l);
            oldSz += l;
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListBuilder<T> append(Iterable<? extends T> iterable) {
        checkState();
        if (iterable instanceof Collection) {
            append((Collection<? extends T>) iterable);
        }
        Iterator<? extends T> e = iterable.iterator();
        while (e.hasNext()) {
            append(e.next());
        }
        return this;
    }

    /**
     * Return an immutable list contains all element of this list builder
     * and then release references to the internal buffer. The list builder
     * is obsolete after calling this method, and calling other building
     * method thereafter will trigger {@link IllegalStateException}
     *
     * @return an immutable list of all element of this builder
     */
    public C.List<T> toList() {
        checkState();
        trimToSize();
        Object[] data = buf;
        buf = null;
        return (C.List<T>)ImmutableList.of(data);
    }

    public C.Set<T> toSet() {
        checkState();
        return (C.Set<T>)ImmutableSet.of(buf);
    }

    /**
     * Returns an immutable {@link C.List} from iterable
     * @param iterable the iterable
     * @param <T> the element type
     * @return an immutable list contains all elements from the iterable
     */
    public static <T> C.List<T> toList(Iterable<? extends T> iterable) {
        if (iterable instanceof Collection) {
            return toList((Collection<T>) iterable);
        }
        ListBuilder<T> lb = new ListBuilder<T>(10);
        for (T t : iterable) {
            lb.add(t);
        }
        return lb.toList();
    }

    /**
     * Returns an immutable {@link C.List} from a collection
     *
     * @param col the collection specified
     * @param <T> element type
     * @return an immutable list contains all elements in the collection
     */
    public static <T> C.List<T> toList(Collection<? extends T> col) {
        if (col.size() == 0) {
            return Nil.list();
        }
        if (col instanceof C.List) {
            C.List<T> list = _.cast(col);
            if (list.is(C.Feature.IMMUTABLE)) {
                return list;
            }
        }
        return new ListBuilder<T>(col).toList();
    }

    /**
     * Create an empty ListBuilder instance
     *
     * @param <T> the type of the element in the builder
     * @return a list builder
     */
    public static <T> ListBuilder<T> create() {
        return new ListBuilder<T>();
    }
}
