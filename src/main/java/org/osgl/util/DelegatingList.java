package org.osgl.util;

import org.osgl._;

import java.util.*;

/**
 * Implement {@link C.List} with a backing {@link java.util.List} instance
 */
class DelegatingList<T> implements C.List<T> {

    private final int hc = getClass().hashCode();

    protected java.util.List<T> data;

    protected final EnumSet<C.Feature> features = EnumSet.of(C.Feature.LIMITED, C.Feature.ORDERED);

    protected final void setFeature(C.Feature feature) {
        features.add(feature);
    }

    private DelegatingList() {
    }

    DelegatingList(Iterable<? extends T> i) {
        if (i instanceof Collection) {
            Collection<? extends T> c = (Collection<? extends T>)i;
            data = newJavaList(c.size());
            data.addAll(c);
        } else {
            data = newJavaList(10);
            for (T t : i) {
                data.add(t);
            }
        }
    }

    DelegatingList(Collection<? extends T> c) {
        data = newJavaList(c.size());
        data.addAll(c);
    }

    DelegatingList(java.util.List<T> list, boolean noCopy) {
        if (noCopy) {
            data = list;
        } else {
            data = newJavaList(list.size());
            data.addAll(list);
        }
    }

    DelegatingList(T t) {
        data = newJavaList(1);
        data.add(t);
    }

    DelegatingList(T t1, T t2) {
        data = newJavaList(2);
        data.add(t1);
        data.add(t2);
    }

    DelegatingList(T t1, T t2, T t3) {
        data = newJavaList(3);
        data.add(t1);
        data.add(t2);
        data.add(t3);
    }

    DelegatingList(T t1, T t2, T t3, T... ta) {
        data = newJavaList(3 + ta.length);
        data.add(t1);
        data.add(t2);
        data.add(t3);
        for (T t : ta) {
            data.add(t);
        }
    }

    @SuppressWarnings("unchecked")
    private C.List<T> emptyList() {
        if (mutable()) {
            return new DelegatingList<T>(Nil.LIST);
        } else {
            return Nil.list();
        }
    }

    private C.List<T> copy(int from, int to) {
        boolean mutable = mutable();
        return new DelegatingList<T>(data.subList(from, to), !mutable).setMutability(mutable);
    }

    private DelegatingList<T> setMutability(boolean mutable) {
        if (mutable) {
            features.remove(C.Feature.IMMUTABLE);
        } else {
            features.add(C.Feature.IMMUTABLE);
        }
        return this;
    }

    protected boolean immutable() {
        return C.isImmutable(this);
    }

    protected boolean mutable() {
        return !immutable();
    }

    /**
     * Check if this List is read only or immutable
     * @return {@code true} if this list is read only or immutable
     * @see C#isReadOnly(org.osgl.util.C.Traversable)
     */
    protected boolean readOnly() {
        return C.isReadOnly(this);
    }

    /**
     * Throws {@link UnsupportedOperationException} if this
     * List is {@link #readOnly()}
     */
    protected void mutableOperation() throws UnsupportedOperationException {
        if (readOnly()) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * By default use {@link C#linkedListFact} to create new backing list
     *
     * @param initialCapacity
     * @return an {code java.util.List} with {@code initialCapacity} allocated
     */
    protected java.util.List<T> newJavaList(int initialCapacity) {
        return C.linkedListFact.create(initialCapacity);
    }

    public int hashCode() {
        return _.hc(hc, data, features);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DelegatingList) {
            DelegatingList<?> that = (DelegatingList<?>)obj;
            return that.features().equals(this.features()) && that.data.equals(this.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    // -- eof override Object methods ---

    // -- override Featured methods ---

    @Override
    public EnumSet<C.Feature> features() {
        return EnumSet.copyOf(features);
    }

    @Override
    public boolean is(C.Feature c) {
        return features.contains(c);
    }

    // -- eof override Featured methods ---

    // -- override java.util.List methods ---

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new DelegatingIterator<T>(data.iterator(), readOnly());
    }

    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return data.toArray(a);
    }

    @Override
    public boolean add(T t) {
        mutableOperation();
        boolean ret = data.add(t);
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        mutableOperation();
        boolean ret = data.remove(o);
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        mutableOperation();
        return data.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        mutableOperation();
        return data.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        mutableOperation();
        return data.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        mutableOperation();
        return data.retainAll(c);
    }

    @Override
    public void clear() {
        mutableOperation();
        data.clear();
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public T set(int index, T element) {
        mutableOperation();
        return data.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        mutableOperation();
        data.add(index, element);
    }

    @Override
    public T remove(int index) {
        mutableOperation();
        return data.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new DelegatingListIterator<T>(data.listIterator(), readOnly());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new DelegatingListIterator<T>(data.listIterator(index), readOnly());
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new DelegatingList<T>(data.subList(fromIndex, toIndex), true);
    }

    // -- eof override java.util.List methods ---

    // -- override Traversable methods ---

    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        //TODO: implement for parallel processing
        return reduceLeft(identity, accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        //TODO: implement for parallel processing
        return reduceLeft(accumulator);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(_.F.negate(predicate));
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean > predicate) {
        try {
            accept(_.F.breakIf(predicate));
            return false;
        } catch (_.Break b) {
            return true;
        }
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        //TODO: implement for parallel processing
        return findFirst(predicate);
    }

    @Override
    public C.Sequence<T> accept(_.Function<? super T, ?> visitor) {
        //TODO implement for parallel processing
        return acceptLeft(visitor);
    }

    @Override
    public C.Sequence<T> acceptLeft(_.Function<? super T, ?> visitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // -- eof override Traversable methods ---

    // --- override Sequence methods ---

    @Override
    public T first() {
        return iterator().next();
    }

    @Override
    public T head() throws NoSuchElementException {
        return first();
    }

    @Override
    public C.List<T> head(int n) {
        return take(n);
    }

    /**
     * {@inheritDoc}
     *
     * If the list is {@link C.Feature#IMMUTABLE immutable} then
     * this method will return an new List that is actually a
     * view window to this list from second element till the last
     * element. Otherwise an new List with copied element reference
     * will returned. The mutability of the returned List
     * is the same as of this List
     *
     * @return {@inheritDoc}
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    @Override
    public C.List<T> tail() throws UnsupportedOperationException {
        if (data.isEmpty()) {
            throw new UnsupportedOperationException("List is empty");
        }
        return copy(1, size());
    }

    /**
     * {@inheritDoc}
     *
     * If the list is {@link C.Feature#IMMUTABLE immutable} then
     * this method will return an new List that is actually a
     * view window. Otherwise an new List with copied element reference
     * will returned. The mutability of the returned List
     * is the same as of this List
     *
     * @return {@inheritDoc}
     */
    @Override
    public C.List<T> take(int n) {
        if (n < 0) {
            return tail(-n);
        } else if (n == 0) {
            return emptyList();
        } else {
            return copy(0, Math.min(n, size()));
        }
    }

    /**
     * {@inheritDoc}
     * @param predicate {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public C.List<T> takeWhile(_.Function<T, Boolean> predicate) {
        DelegatingList<T> ret = new DelegatingList<T>();
        ret.data = newJavaList(size());
        for (T t : this) {
            if (predicate.apply(t)) {
                ret.data.add(t);
            }
            break;
        }
        return ret;
    }

    @Override
    public C.List<T> drop(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        int size = data.size();
        if (size <= n) {
            return emptyList();
        }
        return new DelegatingList<T>(data.subList(n, size));
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<T, Boolean> predicate) {
        DelegatingList<T> ret = new DelegatingList<T>();
        ret.data = newJavaList(size());
        boolean dropClosed = false;
        for (T t : this) {
            if (!dropClosed && predicate.apply(t)) {
                continue;
            }
            dropClosed = true;
            ret.data.add(t);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * Unlike some other methods, {@code map} doesn't return a {@link C.List}
     * type structure, instead it returns a {@link C.Sequence} structure for
     * lazy evaluation of the mapping function. And the returned Sequence
     * is immutable.
     *
     * @param mapper the function that applied to element in this traversal and returns element in the result traversal
     * @param <R> the type of the element in the returned sequence structure
     * @return a {@link C.Sequence} structure contains elements that are mapped
     *         from all elements contained in this {@code List}
     */
    @Override
    public <R> C.Sequence<R> map(_.Function<? super T, ? extends R> mapper) {
        return new MappedSeq<T, R>(this, mapper);
    }

    /**
     * {@inheritDoc}
     *
     * This method is not lazy. It will create an new {@link C.List} instance contains elements
     * that are flat mapped from all elements in this {@code C.List} by the mapper specified.
     *
     * @param mapper a function that maps elements to {@link Iterable} of elements
     * @param <R> the type of mapped Iterable elements
     * @return an new List that
     */
    @Override
    public <R> C.List<R> flatMap(_.Function<? super T, Iterable<R>> mapper) {
        DelegatingList<R> ret = DelegatingList.createWithInitialCapacity(data.size() * 2);
        for (T t : data) {
            ret.append(IteratorSeq.of(mapper.apply(t).iterator()));
        }
        return ret;
    }

    @Override
    public C.Sequence<T> filter(_.Function<? super T, Boolean> predicate) {
        DelegatingList<T> ret = DelegatingList.createWithInitialCapacity(data.size() * 2);
        accept(_.predicate(predicate).ifThen(C.F.addTo(ret)));
        return ret;
    }

    /**
     * If this List is {@link #readOnly()} then returns an new
     * {@link CompositeReversibleSeq} combined this List and
     * an new List of the single element {@code t} specified;
     * Otherwise add the element {@code t} to this List and
     * return this List directly
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public C.Sequence<T> append(T t) {
        if (readOnly()) {
            return new CompositeReversibleSeq<T>(this, C.list(t));
        } else {
            add(t);
            return this;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <ul>
     * <li>If both this list and the appending sequence is immutable then a
     * {@link CompositeSeq} view is returned</li>
     * <li>Other wise if this list is read only an new
     * {@code DelegatingList} is created with elements from both this
     * and the appending sequence is added into</li>
     * <li>If this list is not immutable, the the appending list is appended
     * to this list directly</li>
     * </ul>
     *
     * @param seq the sequence to be appended
     * @return this list after append
     */
    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        if (readOnly()) {
            if (seq instanceof C.ReversibleSequence) {
                return new CompositeReversibleSeq<T>(this, (C.ReversibleSequence)seq);
            } else {
                return C.concat(this, seq);
            }
        } else {
            seq.accept(C.F.appendTo(this));
            return this;
        }
    }

    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        if (readOnly()) {
            return C.concat(this, seq);
        } else {
            seq.accept(C.F.appendTo(this));
            return this;
        }
    }

    @Override
    public C.ReversibleSequence<T> prepend(T t) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        if (immutable()) {
            if (C.isImmutable(seq)) {
                return C.concat(seq, this);
            } else {
                DelegatingList<T> ret = new DelegatingList<T>(seq);
                this.accept(C.F.addTo(ret));
                return ret;
            }
        } else {
            seq.accept(C.F.prependTo(this));
            return this;
        }
    }


    // --- eof override Sequence methods

    // --- override ReversibleSequence methods ---

    @Override
    public C.List<T> reverse() throws UnsupportedOperationException {
        int size = data.size();
        ListIterator<T> itr = data.listIterator(size);
        DelegatingList<T> ret = new DelegatingList<T>();
        ret.data = newJavaList(size);
        for (; ;) {
            if (!itr.hasPrevious()) {
                return ret;
            }
            ret.add(itr.previous());
        }
    }

    @Override
    public T last() throws UnsupportedOperationException, NoSuchElementException {
        if (data.isEmpty()) {
            throw new NoSuchElementException();
        }

        if (data instanceof LinkedList) {
            LinkedList<T> ll = (LinkedList<T>) data;
            return ll.getLast();
        }
        T ret = null;
        for (T t: data) {
            ret = t;
        }
        return ret;
    }

    @Override
    public C.List<T> tail(int n) throws IllegalArgumentException {
        if (n < 0) {
            return take(-n);
        }
        return copy(n, size());
    }

    @Override
    public Iterator<T> reverseIterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public _.Option<T> findLast(_.Function<? super T, Boolean> predicate) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public C.Sequence<T> acceptRight(_.Function<? super T, ?> visitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // --- override ReversibleSequence methods ---

    static <T> DelegatingList<T> createWithInitialCapacity(int initialCapacity) {
        DelegatingList<T> ret = new DelegatingList<T>();
        ret.data = ret.newJavaList(initialCapacity);
        return ret;
    }
}
