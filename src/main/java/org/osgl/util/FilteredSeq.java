package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
class FilteredSeq<T> implements C.Sequence<T> {
    private final C.Sequence<T> data;
    private final _.Function<? super T, Boolean> filter;

    FilteredSeq(C.Sequence<T> seq, _.Function<? super T, Boolean> predicate) {
        E.NPE(seq, predicate);
        data = seq;
        filter = predicate;
    }

    protected C.Sequence<T> data() {
        return data;
    }

    @Override
    public int hashCode() {
        return _.hc(filter, data, getClass());
    }

    @Override
    public String toString() {
        //TODO
        return data.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FilteredSeq) {
            FilteredSeq<?> that = (FilteredSeq<?>)obj;
            return _.eq(that.data, data) && _.eq(that.filter, filter);
        }
        return false;
    }

    @Override
    public EnumSet<C.Feature> features() {
        EnumSet<C.Feature> features = data.features();
        features.add(C.Feature.READONLY);
        return features;
    }

    @Override
    public boolean is(C.Feature c) {
        return C.Feature.READONLY == c || data.features().contains(c);
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> itr = data.iterator();
        return new Iterator<T>() {
            private _.Option<T> cur = _.none();
            @Override
            public boolean hasNext() {
                if (cur.isDefined()) {
                    return true;
                }
                while (itr.hasNext()) {
                    T nxt = itr.next();
                    if (filter.apply(nxt)) {
                        cur = _.some(nxt);
                        break;
                    }
                }
                return cur.isDefined();
            }

            @Override
            public T next() {
                if (cur.notDefined() && !hasNext()) {
                    throw new NoSuchElementException();
                }
                T ret = cur.get();
                cur = _.none();
                return ret;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return data.size();
    }

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
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        try {
            accept(_.F.breakIf(predicate));
            return _.none();
        } catch (_.Break b) {
            T t = b.get();
            return _.some(t);
        }
    }

    @Override
    public final T first() {
        return iterator().next();
    }

    @Override
    public T head() throws NoSuchElementException {
        return first();
    }

    @Override
    public C.Sequence<T> head(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        } else if (n == 0) {
            return Nil.seq();
        } else {
            C.List<T> l = DelegatingList.createWithInitialCapacity(n);
            Iterator<T> itr = iterator();
            for (int i = 0; i < n && itr.hasNext(); ++i) {
                l.add(itr.next());
            }
            return l;
        }
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> take(int n) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> takeWhile(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> drop(int n) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> append(C.Sequence<T> seq) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> append(T t) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> prepend(T t) {
        //TODO ...
        return null;
    }

    @Override
    public <R> C.Sequence<R> map(_.Function<? super T, ? extends R> mapper) {
        //TODO ...
        return null;
    }

    @Override
    public <R> C.Sequence<R> flatMap(_.Function<? super T, Iterable<R>> mapper) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> filter(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        //TODO ...
        return null;
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        //TODO ...
        return null;
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        //TODO ...
        return null;
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        //TODO ...
        return null;
    }

    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> accept(_.Function<? super T, ?> visitor) {
        //TODO ...
        return null;
    }

    @Override
    public C.Sequence<T> acceptLeft(_.Function<? super T, ?> visitor) {
        //TODO ...
        return null;
    }
}
