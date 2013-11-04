package org.osgl.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A simple array based List that support append element to the tail of
 * the list only. This class is NOT thread safe. Do not use it in
 * multiple thread context
 */
class ListBuilder<T> extends AbstractList<T> {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    T[] buf;
    int size;

    ListBuilder() {
        this(10);
    }

    @SuppressWarnings("unchecked")
    ListBuilder(int initialCapacity) {
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

    boolean addAll(Iterable<? extends T> iterable) {
        checkState();
        boolean modified = false;
        Iterator<? extends T> e = iterable.iterator();
        while (e.hasNext()) {
            if (add(e.next())) {
                modified = true;
            }
        }
        return modified;
    }

    private void trimToSize() {
        checkState();
        modCount++;
        int oldCapacity = buf.length;
        if (size < oldCapacity) {
            buf = Arrays.copyOf(buf, size);
        }
    }

    private void ensureCapacity(int capacity) {
        checkState();
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
        return buf[index];
    }

    @Override
    public boolean add(T t) {
        checkState();
        int sz = size++;
        ensureCapacity(sz + 1);
        buf[sz] = t;
        return true;
    }

    /**
     * Return an immutable list contains all element of this list builder
     * and then release references to the internal buffer. The list builder
     * is obsolete after calling this method, and calling other building
     * method thereafter will trigger {@link IllegalStateException}
     *
     * @return an immutable list of all element of this builder
     */
    public C.List<T> asList() {
        trimToSize();
        T[] data = buf;
        buf = null;
        return ImmutableList.of(data);
    }
}
