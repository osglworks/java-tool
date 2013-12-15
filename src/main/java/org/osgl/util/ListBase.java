package org.osgl.util;

import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 30/10/13
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ListBase<T> extends AbstractList<T> implements C.List<T> {

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

    /**
     * Sub class could override this method to implement iterating in parallel.
     *
     * <p>The iterating support partial function visitor by ignoring the
     * {@link NotAppliedException} thrown out by visitor's apply
     * method call</p>
     *
     * @param visitor
     * @throws org.osgl._.Break if visitor needs to terminate the iteration
     */
    protected void forEach(_.Function<? super T, ?> visitor) throws _.Break {
        forEachLeft(visitor);
    }

    protected void forEachLeft(_.Function<? super T, ?> visitor) throws _.Break{
        for (T t : this) {
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    protected void forEachRight(_.Function<? super T, ?> visitor) throws _.Break {
        Iterator<T> itr = reverseIterator();
        while (itr.hasNext()) {
            try {
                visitor.apply(itr.next());
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

    public boolean addAll(Iterable <? extends T> iterable) {
        boolean modified = false;
       	Iterator<? extends T> e = iterable.iterator();
       	while (e.hasNext()) {
       	    if (add(e.next()))
       		modified = true;
       	}
       	return modified;
    }

    // --- Featured methods

    volatile private EnumSet<C.Feature> features_;

    protected final EnumSet<C.Feature> features_() {
        if (null == features_) {
            synchronized (this) {
                if (null == features_) {
                    features_ = initFeatures();
                    assert(null != features_);
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
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(_.F.negate(predicate));
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return findOne(predicate).isDefined();
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public _.Option<T> findOne(final _.Function<? super T, Boolean> predicate) {
        try {
            forEach(new _.Visitor<T>() {
                @Override
                public void visit(T t) throws _.Break {
                    if (predicate.apply(t)) {
                        throw new _.Break(t);
                    }
                }
            });
            return _.none();
        } catch (_.Break b) {
            T t = b.get();
            return _.some(t);
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
    public final T first() throws NoSuchElementException{
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
    public C.List<T> takeWhile(_.Function<? super T, Boolean> predicate) {
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
        if (n < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (0 == n) {
            return this;
        }
        // TODO handle lazy drop
        boolean immutable = isImmutable();
        if (immutable) {
            return subList(n, size());
        }
        int sz = size();
        if (n >= sz) {
            return C.newList();
        }
        C.List<T> l = C.newSizedList(sz - n);
        l.addAll(subList(n, sz));
        return l;
    }

    @Override
    public C.List<T> dropWhile(_.Function<? super T, Boolean> predicate) {
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
    public C.List<T> remove(_.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy remove
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(lb)));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(l)));
            return l;
        }
    }

    @Override
    public <R> C.List<R> map(_.Function<? super T, ? extends R> mapper) {
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
            forEach(_.f1(mapper).andThen(C.F.addTo(lb)));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newSizedList(sz);
            forEach(_.f1(mapper).andThen(C.F.addTo(l)));
            return l;
        }
    }

    @Override
    public <R> C.List<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy flatmap
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<R> lb = new ListBuilder<R>(sz * 3);
            forEach(_.f1(mapper).andThen(C.F.addAllTo(lb)));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newSizedList(sz * 3);
            forEach(_.f1(mapper).andThen(C.F.addAllTo(l)));
            return l;
        }
    }

    @Override
    public C.List<T> filter(_.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy filter
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(lb)));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newSizedList(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(l)));
            return l;
        }
    }

    private Cursor<T> fromLeft() {
        return new ListIteratorCursor<T>(listIterator(0));
    }

    private Cursor<T> fromRight() {
        return new ListIteratorCursor<T>(listIterator(size()));
    }

    @Override
    public Cursor<T> locateFirst(_.Function<T, Boolean> predicate) {
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
    public Cursor<T> locate(_.Function<T, Boolean> predicate) {
        return locateFirst(predicate);
    }

    @Override
    public Cursor<T> locateLast(_.Function<T, Boolean> predicate) {
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
        if (isMutable()) {
            add(index, t);
            return this;
        }
        int sz = size();
        if (index < 0 || index > sz) {
            throw new IndexOutOfBoundsException();
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
        return filter(_.F.negate(C.F.containsIn(col)));
    }

    @Override
    public C.List<T> accept(_.Function<? super T, ?> visitor) {
        forEach(visitor);
        return this;
    }

    @Override
    public C.List<T> acceptLeft(_.Function<? super T, ?> visitor) {
        forEachLeft(visitor);
        return this;
    }

    @Override
    public C.List<T> acceptRight(_.Function<? super T, ?> visitor) {
        forEachRight(visitor);
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

    private C.List<T> unLazyAppend(Iterable<? extends T> iterable) {
        if (isMutable()) {
            if (iterable instanceof Collection) {
                addAll((Collection<? extends T>) iterable);
            } else {
                C.forEach(iterable, C.F.addTo(this));
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

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> append(Iterable<? extends T> iterable) {
        if (iterable instanceof C.List) {
            return append((C.List<T>) iterable);
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
            return append((C.List<T>) collection);
        } else {
            return unLazyAppend(collection);
        }
    }

    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        if (seq instanceof C.List) {
            return append((C.List<T>) seq);
        }
        if (isLazy()) {
            return CompositeSeq.of(this, seq);
        }
        return unLazyAppend(seq);
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        if (seq instanceof C.List) {
            return append((C.List<T>) seq);
        }
        // TODO support lazy append reversible sequence
        return unLazyAppend(seq);
    }

    @Override
    public C.List<T> append(C.List<T> list) {
        if (isLazy()) {
            return CompositeList.of(this, list);
        }
        return unLazyAppend(list);
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


    @Override
    @SuppressWarnings("unchecked")
    public C.List<T> prepend(Collection<? extends T> collection) {
        if (collection instanceof C.List) {
            return prepend((C.List<T>) collection);
        }
        return unLazyPrepend(collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> prepend(Iterable<? extends T> iterable) {
        if (iterable instanceof C.List) {
            return prepend((C.List<T>) iterable);
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

    /**
     * {@inheritDoc}
     *
     * This method will NOT change the underline list
     *
     * @param seq the sequence to be prepended
     * @return
     */
    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        if (seq instanceof C.List) {
            return prepend((C.List<T>) seq);
        }
        if (isLazy()) {
            return new CompositeSeq<T>(seq, this);
        }
        return unLazyPrepend(seq);
    }


    @Override
    @SuppressWarnings("unchecked")
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        if (seq instanceof C.List) {
            return prepend((C.List<T>) seq);
        }
        // TODO support lazy append reversible sequence
        return unLazyPrepend(seq);
    }

    /**
     * {@inheritDoc}
     *
     * This method will NOT change the underline list
     *
     * @param list
     * @return
     */
    @Override
    public C.List<T> prepend(C.List<T> list) {
        if (isLazy()) {
            return CompositeList.of(list, this);
        }
        return unLazyPrepend(list);
    }

    /**
     * {@inheritDoc}
     *
     * For mutable list, this method will insert the
     * element at {@code 0} position.
     *
     * @param t
     * @return
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
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return reduceLeft(identity, accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        R ret = identity;
        for (T t : this) {
            ret = accumulator.apply(ret, t);
        }
        return ret;
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        R ret = identity;
        Iterator<T> i = reverseIterator();
        while (i.hasNext()) {
            ret = accumulator.apply(ret, i.next());
        }
        return ret;
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return reduceLeft(accumulator);
    }

    private _.Option<T> reduceIterator(Iterator<T> itr, _.Func2<T, T, T> accumulator) {
        if (!itr.hasNext()) {
            return _.none();
        }
        T ret = itr.next();
        while (itr.hasNext()) {
            ret = accumulator.apply(ret, itr.next());
        }
        return _.some(ret);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        return reduceIterator(iterator(), accumulator);
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return reduceIterator(reverseIterator(), accumulator);
    }

    private _.Option<T> findIterator(Iterator<T> itr, _.Function<? super T, Boolean> predicate) {
        while (itr.hasNext()) {
            T t = itr.next();
            if (predicate.apply(t)) {
                return _.some(t);
            }
        }
        return _.none();
    }
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        return findIterator(iterator(), predicate);
    }

    @Override
    public _.Option<T> findLast(_.Function<? super T, Boolean> predicate) {
        return findIterator(reverseIterator(), predicate);
    }

    @Override
    public <T2> C.List<_.T2<T, T2>> zip(List<T2> list) {
        return new ZippedList<T, T2>(this, list);
    }

    @Override
    public <T2> C.List<_.T2<T, T2>> zipAll(List<T2> list, T def1, T2 def2) {
        return new ZippedList<T, T2>(this, list, def1, def2);
    }

    @Override
    public C.Sequence<_.T2<T, Integer>> zipWithIndex() {
        return new ZippedSeq<T, Integer>(this, new IndexIterable(this));
    }

    @Override
    public <T2> C.Sequence<_.T2<T, T2>> zip(Iterable<T2> iterable) {
        if (iterable instanceof List) {
            return zip((List<T2>) iterable);
        }
        return new ZippedSeq<T, T2>(this, iterable);
    }

    @Override
    public <T2> C.Sequence<_.T2<T, T2>> zipAll(Iterable<T2> iterable, T def1, T2 def2) {
        if (iterable instanceof List) {
            return zipAll((List<T2>) iterable, def1, def2);
        }
        return new ZippedSeq<T, T2>(this, iterable, def1, def2);
    }

    @Override
    public <T2> C.ReversibleSequence<_.T2<T, T2>> zip(C.ReversibleSequence<T2> rseq) {
        if (rseq instanceof C.List) {
            return zip((java.util.List<T2>)rseq);
        }
        return new ZippedRSeq<T, T2>(this, rseq);
    }

    @Override
    public <T2> C.ReversibleSequence<_.T2<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, T def1, T2 def2) {
        if (rseq instanceof C.List) {
            return zipAll((java.util.List<T2>) rseq, def1, def2);
        }
        return new ZippedRSeq<T, T2>(this, rseq, def1, def2);
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
        return l.set(index+offset, element);
    }

    public E get(int index) {
        rangeCheck(index);
        checkForComodification();
        return l.get(index+offset);
    }

    public int size() {
        checkForComodification();
        return size;
    }

    public void add(int index, E element) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException();
        checkForComodification();
        l.add(index+offset, element);
        expectedModCount = l.modCount();
        size++;
        modCount++;
    }

    public E remove(int index) {
        rangeCheck(index);
        checkForComodification();
        E result = l.remove(index+offset);
        expectedModCount = l.modCount();
        size--;
        modCount++;
        return result;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        checkForComodification();
        l.removeRange2(fromIndex+offset, toIndex+offset);
        expectedModCount = l.modCount();
        size -= (toIndex-fromIndex);
        modCount++;
    }

    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);
        int cSize = c.size();
        if (cSize==0)
            return false;

        checkForComodification();
        l.addAll(offset+index, c);
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
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);

        return new ListIterator<E>() {
            private ListIterator<E> i = l.listIterator(index+offset);

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
        if (index<0 || index>=size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                                ",Size: "+size);
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
