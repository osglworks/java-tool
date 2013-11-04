package org.osgl.util;

import org.osgl._;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/10/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompositeList<T> extends CompositeReversibleSeq<T> implements C.List<T> {

    CompositeList(C.List<T> left, C.List<T> right) {
        super(left, right);
    }

    <T1> CompositeList<T1> of(C.List<T1> left, C.List<T1> right) {
        return new CompositeList<T1>(left, right);
    }

    protected UnsupportedOperationException noMutableOperation() {
        throw new UnsupportedOperationException("mutable operation not allowed in this read only structure");
    }

    @Override
    protected C.List<T> left() {
        return _.cast(super.left());
    }

    @Override
    protected C.List<T> right() {
        return _.cast(super.right());
    }

    @Override
    public boolean contains(Object o) {
        return left().contains(o) || right().contains(o);
    }

    @Override
    public Object[] toArray() {
        Object[] la = left().toArray();
        Object[] ra = right().toArray();
        return _.concat(la, ra);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] la = left().toArray(a);
        T[] ra = right().toArray(a);
        return _.concat(la, ra);
    }

    @Override
    public boolean add(T t) {
        throw noMutableOperation();
    }

    @Override
    public boolean remove(Object o) {
        throw noMutableOperation();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Collection<?> c1 = new HashSet<Object>(c);
        c1.removeAll(left());
        c1.removeAll(right());
        return c1.isEmpty();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw noMutableOperation();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw noMutableOperation();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw noMutableOperation();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw noMutableOperation();
    }

    @Override
    public void clear() {
        throw noMutableOperation();
    }

    @Override
    public T get(int index) {
        int size = left().size();
        if (index >= size) {
            return right().get(index - size);
        } else {
            return left().get(index);
        }
    }

    @Override
    public T set(int index, T element) {
        throw noMutableOperation();
    }

    @Override
    public void add(int index, T element) {
        throw noMutableOperation();
    }

    @Override
    public T remove(int index) {
        throw noMutableOperation();
    }

    @Override
    public int indexOf(Object o) {
        int ret = left().indexOf(o);
        return ret == -1 ? right().indexOf(o) : ret;
    }

    @Override
    public int lastIndexOf(Object o) {
        int ret = right().lastIndexOf(o);
        return ret == -1 ? left().lastIndexOf(o) : ret;
    }

    @Override
    public ListIterator<T> listIterator() {
        final ListIterator<T> ll = left().listIterator();
        final ListIterator<T> rl = right().listIterator();
        final int lsize = left().size();
        final int rsize = right().size();
        return new ListIterator<T>() {
            boolean leftClosed = false;
            @Override
            public boolean hasNext() {
                return !leftClosed || rl.hasNext();
            }

            @Override
            public T next() {
                if (!leftClosed) {
                    T t = ll.next();
                    leftClosed = !ll.hasNext();
                    return t;
                } else {
                    return rl.next();
                }
            }

            @Override
            public boolean hasPrevious() {
                return leftClosed || ll.hasPrevious();
            }

            @Override
            public T previous() {
                if (leftClosed) {
                    T t = rl.previous();
                    leftClosed = rl.hasPrevious();
                    return t;
                } else {
                    return ll.previous();
                }
            }

            @Override
            public int nextIndex() {
                return leftClosed ? lsize + rl.nextIndex() : ll.nextIndex();
            }

            @Override
            public int previousIndex() {
                return leftClosed ? rl.previousIndex() : rsize + ll.previousIndex();
            }

            @Override
            public void remove() {
                throw noMutableOperation();
            }

            @Override
            public void set(T t) {
                throw noMutableOperation();
            }

            @Override
            public void add(T t) {
                throw noMutableOperation();
            }
        };
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return subList(index, size()).listIterator();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || toIndex < fromIndex) {
            throw new IllegalArgumentException();
        }
        if (fromIndex == toIndex) {
            return Nil.list();
        }
        int lsize = left().size();
        if (fromIndex >= lsize) {
            return right().subList(fromIndex - lsize, toIndex);
        }
        if (toIndex < lsize) {
            return left().subList(fromIndex, toIndex);
        }
        C.List<T> lsub = new DelegatingList1<T>(left().subList(fromIndex, lsize), true);
        C.List<T> rsub = new DelegatingList1<T>(right().subList(0, toIndex), true);
        return of(lsub, rsub);
    }

    @Override
    public C.List<T> append(C.List<T> list) {
        return of(this, list);
    }

    @Override
    public C.List<T> prepend(C.List<T> list) {
        return of(list, this);
    }

    @Override
    public C.List<T> without(Collection<? extends T> col) {
        return of(left().without(col), right().without(col));
    }

    @Override
    public C.List<T> head(int n) {
        return _.cast(super.head(n));
    }

    @Override
    public C.List<T> take(int n) throws UnsupportedOperationException {
        return _.cast(super.take(n));
    }

    @Override
    public C.List<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        return _.cast(super.takeWhile(predicate));
    }

    @Override
    public C.List<T> tail() throws UnsupportedOperationException {
        return _.cast(super.tail());
    }

    @Override
    public C.List<T> drop(int n) {
        return _.cast(super.drop(n));
    }

    @Override
    public C.List<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        return _.cast(super.dropWhile(predicate));
    }

    @Override
    public <R> C.List<R> map(_.Function<? super T, ? extends R> mapper) {
        return _.cast(super.map(mapper));
    }

    @Override
    public C.List<T> filter(_.Function<? super T, Boolean> predicate) {
        return _.cast(super.filter(predicate));
    }

    @Override
    public CompositeList<T> accept(_.Function<? super T, ?> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public CompositeList<T> acceptLeft(_.Function<? super T, ?> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public C.List<T> append(T t) {
        return _.cast(super.append(t));
    }

    @Override
    public C.List<T> prepend(T t) {
        return _.cast(super.prepend(t));
    }

    @Override
    public C.List<T> reverse() throws UnsupportedOperationException {
        return _.cast(super.reverse());
    }

    @Override
    public C.List<T> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
        return _.cast(super.tail(n));
    }

    @Override
    public C.List<T> acceptRight(_.Function<? super T, ?> visitor) {
        super.acceptRight(visitor);
        return this;
    }

    @Override
    public Cursor<T> locateFirst(_.Function<T, Boolean> predicate) {
        ListIterator<T> l1 = left().listIterator();
        ListIterator<T> l2 = right().listIterator();
        CompositeListIterator<T> l = new CompositeListIterator<T>(l1, l2);
        Cursor<T> c = new ListIteratorCursor<T>(l);
        while (c.hasNext()) {
            T t = c.forward().get();
            if (predicate.apply(t)) {
                return c;
            }
        }
        return c.obsolete();
    }

    @Override
    public Cursor<T> locate(_.Function<T, Boolean> predicate) {
        return locateFirst(predicate);
    }

    @Override
    public Cursor<T> locateLast(_.Function<T, Boolean> predicate) {
        C.List<T> left = left(), right = right();

        ListIterator<T> l1 = left.listIterator(left.size());
        ListIterator<T> l2 = right.listIterator(right.size());
        Cursor<T> c = new ListIteratorCursor<T>(new CompositeListIterator<T>(l1, l2));
        while (c.hasPrevious()) {
            T t = c.backward().get();
            if (predicate.apply(t)) {
                return c;
            }
        }
        return c.obsolete();
    }

    @Override
    public C.List<T> insert(T t, int index) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException();
    }
}
