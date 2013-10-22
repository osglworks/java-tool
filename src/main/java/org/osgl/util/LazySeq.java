package org.osgl.util;

import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Implement a Lazy evaluation list
 */
class LazySeq<T> implements C.Sequence<T> {
    protected T head;
    protected _.F0<C.Sequence<T>> tail;
    private volatile C.Sequence<T> tail_;
    protected EnumSet<C.Feature> feature = EnumSet.of(C.Feature.IMMUTABLE, C.Feature.LAZY, C.Feature.READONLY, C.Feature.ORDERED);

    /**
     * Sub classes shall init {@link #head} and {@link #tail} in this constructor
     */
    protected LazySeq() {
    }

    LazySeq(T head, _.Func0<? extends C.Sequence<T>> tail) {
        E.NPE(head, tail);
        this.head = head;
        this.tail = _.f0(tail);
    }

    protected C.Sequence<T> emptySeq() {
        return Nil.seq();
    }

    public final T first() {
        return head;
    }

    @Override
    public final T head() {
        return first();
    }

    @Override
    public C.Sequence<T> head(final int n) {
        return take(n);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public C.Sequence<T> tail() throws UnsupportedOperationException {
        if (null != tail_) {
            return tail_;
        }
        synchronized (this) {
            if (null == tail_) {
                tail_ = tail.apply();
            }
        }
        return tail_;
    }

    @Override
    public C.Sequence<T> take(final int n) {
        if (n < 0) {
            throw new UnsupportedOperationException();
        }
        if (0 == n) {
            return emptySeq();
        }
        return new LazySeq<T>(head, new _.F0<C.Sequence<T>>() {
            @Override
            public C.Sequence<T> apply() throws NotAppliedException, _.Break {
                return tail().take(n - 1);
            }
        });
    }

    @Override
    public C.Sequence<T> takeWhile(final _.Function<? super T, Boolean> predicate) {
        if (predicate.apply(head)) {
            return new LazySeq<T>(head, new _.F0<C.Sequence<T>>() {
                @Override
                public C.Sequence<T> apply() throws NotAppliedException, _.Break {
                    return tail().takeWhile(predicate);
                }
            });
        }
        return emptySeq();
    }

    @Override
    public C.Sequence<T> drop(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        if (0 == n) {
            return this;
        }
        C.Sequence<T> seq = this;
        for (int i = 0; i < n; ++i) {
            seq = seq.tail();
            if (seq.isEmpty()) {
                return seq;
            }
        }
        return seq;
    }

    @Override
    public C.Sequence<T> dropWhile(_.Function<? super T, Boolean> predicate) {
        T h = head;
        C.Sequence<T> tail = tail();
        while (true) {
            boolean appliedToHead = predicate.apply(h);
            boolean tailIsEmpty = tail.isEmpty();
            if (appliedToHead && !tailIsEmpty) {
                h = tail.head();
                tail = tail.tail();
                continue;
            }
            if (appliedToHead && tailIsEmpty) {
                return emptySeq();
            }
            final C.Sequence<T> seq = tail;
            return new LazySeq<T>(h, new _.F0<C.Sequence<T>>() {
                @Override
                public C.Sequence<T> apply() throws NotAppliedException, _.Break {
                    return seq;
                }
            });
        }
    }

    @Override
    public C.Sequence<T> append(final T t) {
        return C.append(this, t);
    }

    @Override
    public C.Sequence<T> append(final C.Sequence<T> tail) {
        return C.concat(this, tail);
    }

    @Override
    public C.Sequence<T> prepend(final T t) {
        return C.prepend(t, this);
    }

    @Override
    public C.Sequence<T> prepend(C.Sequence<T> seq) {
        return seq.append(this);
    }

    @Override
    public <R> C.Sequence<R> map(final _.Function<? super T, ? extends R> mapper) {
        return C.map(this, mapper);
    }

    /**
     * @param mapper the function produce an iterable when applied to an element
     * @param <R>
     * @return
     * @throws UnsupportedOperationException if the mapper generates {@code null} or
     *                                       empty list
     */
    @Override
    public <R> C.Sequence<R>
    flatMap(final _.Function<? super T, ? extends Iterable<? extends R>> mapper) throws UnsupportedOperationException {
        return IteratorSeq.of(new FlatMappedIterator<T, R>(iterator(), mapper));
    }

    @Override
    public C.Sequence<T>
    filter(final _.Function<? super T, Boolean> predicate) {
        C.Sequence<T> seq = this;
        T h = seq.head();
        while (!predicate.apply(h)) {
            seq = seq.tail();
            if (seq.isEmpty()) {
                return seq;
            }
            h = seq.head();
        }
        final C.Sequence<T> tail = seq.tail();
        return new LazySeq<T>(h, new _.F0<C.Sequence<T>>() {
            @Override
            public C.Sequence<T> apply() throws NotAppliedException, _.Break {
                return tail.filter(predicate);
            }
        });
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation use {@link #reduceLeft(Object, org.osgl._.Func2)} for
     * the method
     *
     * @param identity    {@inheritDoc}
     * @param accumulator {@inheritDoc}
     * @param <R>         {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return reduceLeft(identity, accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        C.Sequence<T> seq = this;
        while (!seq.isEmpty()) {
            T head = seq.head();
            identity = accumulator.apply(identity, head);
            seq = seq.tail();
        }
        return identity;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation use {@link #reduceLeft(org.osgl._.Func2)} for the method
     *
     * @param accumulator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return reduceLeft(accumulator);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        C.Sequence<T> seq = this;
        T identity = seq.head();
        while (true) {
            seq = seq.tail();
            if (seq.isEmpty()) {
                break;
            }
            identity = accumulator.apply(identity, seq.head());
        }
        return _.some(identity);
    }


    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        C.Sequence<T> seq = this;
        while (!seq.isEmpty()) {
            T head = seq.head();
            if (!predicate.apply(head)) {
                return false;
            }
            seq = seq.tail();
        }
        return true;
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return findOne(predicate).isDefined();
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return anyMatch(_.F.negate(predicate));
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This method use {@link #findFirst(org.osgl._.Function)} for implementation
     *
     * @param predicate {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        return findFirst(predicate);
    }

    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        C.Sequence<T> seq = this;
        while (!seq.isEmpty()) {
            T head = seq.head();
            if (predicate.apply(head)) {
                return _.some(head);
            }
            seq = seq.tail();
        }
        return _.none();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This method use {@link #acceptLeft(org.osgl._.Function)} implementation
     *
     * @param visitor {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public LazySeq<T> accept(_.Function<? super T, ?> visitor) {
        return acceptLeft(visitor);
    }

    @Override
    public LazySeq<T> acceptLeft(_.Function<? super T, ?> visitor) {
        for (T t : this) {
            visitor.apply(t);
        }
        return this;
    }

    @Override
    public Iterator<T> iterator() {
        final C.Sequence<T> seq = this;
        return new Iterator<T>() {
            private C.Sequence<T> _seq = seq;

            public boolean hasNext() {
                return !_seq.isEmpty();
            }

            @Override
            public T next() {
                T head = _seq.head();
                _seq = _seq.tail();
                return head;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public EnumSet<C.Feature> features() {
        return EnumSet.copyOf(feature);
    }

    @Override
    public boolean is(C.Feature c) {
        return feature.contains(c);
    }

    public String debug() {
        StringBuilder sb = new StringBuilder("[_LS_");
        for (T t : this) {
            sb.append(", ").append(t);
        }
        return sb.append("]").toString();
    }

}
