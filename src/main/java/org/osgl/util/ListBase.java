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
abstract class ListBase<T> extends AbstractList<T> implements C.List<T> {

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

    protected final void forEachLeft(_.Function<? super T, ?> visitor) throws _.Break{
        for (T t : this) {
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
    }

    protected final void forEachRight(_.Function<? super T, ?> visitor) throws _.Break {
        Iterator<T> itr = reverseIterator();
        while (itr.hasNext()) {
            try {
                visitor.apply(itr.next());
            } catch (NotAppliedException e) {
                // ignore
            }
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
            return new DelegatingList<T>(subList(0, n));
        }
        C.List<T> l = C.newList(n);
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
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newList(sz);
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
            return new DelegatingList<T>(subList(n, size()));
        }
        C.List<T> l = C.newList(size() - n);
        l.addAll(subList(n, size()));
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
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newList(sz);
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
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newList(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(l)));
            return l;
        }
    }

    @Override
    public <R> C.List<R> map(_.Function<? super T, ? extends R> mapper) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy map
        if (immutable) {
            if (0 == sz) {
                return Nil.list();
            }
            ListBuilder<R> lb = new ListBuilder<R>(sz);
            forEach(_.f1(mapper).andThen(C.F.addTo(lb)));
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newList(sz);
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
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newList(sz * 3);
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
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newList(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(l)));
            return l;
        }
    }

    private Cursor<T> start() {
        return new ListIteratorCursor<T>(listIterator());
    }

    private Cursor<T> stop() {
        return new ListIteratorCursor<T>(listIterator(size()));
    }

    @Override
    public Cursor<T> locateFirst(_.Function<T, Boolean> predicate) {
        Cursor<T> c = start();
        while (c.isDefined()) {
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
        Cursor<T> c = stop();
        while (c.isDefined()) {
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
            return lb.asList();
        } else {
            C.List<T> l = C.newList(sz + 1);
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
            return lb.asList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<T> l = C.newList(sz);
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
    public final C.List<T> head(int n) {
        return take(n);
    }

    @Override
    public C.List<T> tail() {
        int sz = size();
        if (0 == sz) {
            throw new UnsupportedOperationException();
        }
        if (isImmutable()) {
            return new DelegatingList<T>(subList(1, sz));
        }
        C.List<T> l = C.newList(sz - 1);
        l.addAll(subList(1, sz));
        return null;
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
        java.util.List<T> sl = subList(sz - n, sz);
        if (immutable) {
            return new DelegatingList<T>(sl);
        }
        C.List<T> l = C.newList(n);
        l.addAll(sl);
        return null;
    }


    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        if (isLazy() && seq.is(C.Feature.LAZY)) {
            return C.concat(this, seq);
        }
        if (isMutable()) {
            C.forEach(seq, C.F.addTo(this));
            return this;
        }
        // immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
            lb.addAll(this);
            lb.addAll(seq);
            return lb.asList();
        }
        // mutable but read only
        C.List<T> l = C.newList(size() * 2);
        l.addAll(this);
        return l.append(seq);
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        return (C.ReversibleSequence<T>) append((C.Sequence<T>) seq);
    }

    @Override
    public C.List<T> append(C.List<T> list) {
        return (C.List<T>) append((C.Sequence<T>) list);
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
            return lb.asList();
        }
        // mutable but readonly
        C.List<T> l = C.newList(size() + 1);
        l.addAll(this);
        l.add(t);
        return l;
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        if (isLazy() && seq.is(C.Feature.LAZY)) {
            return C.concat(this, seq);
        }
        if (isMutable()) {
            C.forEach(seq, C.F.addTo(this));
            return this;
        }
        // immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() * 2);
            lb.addAll(this);
            lb.addAll(seq);
            return lb.asList();
        }
        C.List<T> l = C.newList(size() * 2);
        l.addAll(this);
        l.addAll(seq);
        return l;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        return (C.ReversibleSequence<T>) prepend((C.Sequence<T>) seq);
    }

    @Override
    public C.List<T> prepend(C.List<T> list) {
        return (C.List<T>) prepend((C.Sequence<T>) list);
    }

    @Override
    public C.List<T> prepend(T t) {
        if (isMutable()) {
            add(t);
            return this;
        }
        // immutable
        if (isImmutable()) {
            ListBuilder<T> lb = new ListBuilder<T>(size() + 1);
            lb.addAll(this);
            lb.add(t);
            return lb.asList();
        }
        // readonly but mutable
        C.List<T> l = C.newList(size() + 1);
        l.addAll(this);
        l.add(t);
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



}
