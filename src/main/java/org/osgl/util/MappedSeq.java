package org.osgl.util;

import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 4/10/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
class MappedSeq<T, R> extends SequenceBase<R> implements C.Sequence<R> {
    private final C.Sequence<? extends T> data;
    protected final _.F1<? super T, ? extends R> mapper;
    private volatile C.Sequence<R> mappedData;

    private static EnumSet<C.Feature> featuresOf(C.Sequence<?> seq) {
        EnumSet<C.Feature> fs = seq.features();
        fs.add(C.Feature.READONLY);
        return fs;
    }

    MappedSeq(C.Sequence<? extends T> seq, _.Function<? super T, ? extends R> mapper) {
        E.NPE(seq, mapper);
        this.data = seq;
        this.mapper = _.f1(mapper);
        if (data.isEmpty()) {
            mappedData = Nil.seq();
        }
    }

    protected C.Sequence<? extends T> data() {
        return data;
    }

    protected <T, R> MappedSeq<T, R>
    of(C.Sequence<? extends T> data, _.Function<? super T, ? extends R> mapper) {
        return new MappedSeq<T, R>(data, mapper);
    }

    protected C.Sequence<R> applyMapper() {
        if (null == mappedData) {
            synchronized (this) {
                if (null == mappedData) {
                    mappedData = new LazySeq<R>(mapper.apply(data.head()), new _.F0<C.Sequence<R>>() {
                        @Override
                        public C.Sequence<R> apply() throws NotAppliedException, _.Break {
                            return new MappedSeq<T, R>(data.tail(), mapper);
                        }
                    });
                }
            }
        }
        return mappedData;
    }

    protected final R map(T t) {
        return mapper.apply(t);
    }

    @Override
    public R first() {
        return map(data.first());
    }

    @Override
    public C.Sequence<R> head(int n) {
        return of(data.head(n), mapper);
    }

    @Override
    public C.Sequence<R> tail() throws UnsupportedOperationException {
        return of(data.tail(), mapper);
    }

    @Override
    public C.Sequence<R> take(int n) throws UnsupportedOperationException {
        return of(data.take(n), mapper);
    }

    @Override
    public C.Sequence<R> takeWhile(_.Function<? super R, Boolean> predicate) {
        return applyMapper().takeWhile(predicate);
    }

    @Override
    public C.Sequence<R> drop(int n) {
        return of(data.drop(n), mapper);
    }

    @Override
    public C.Sequence<R> dropWhile(_.Function<? super R, Boolean> predicate) {
        return applyMapper().dropWhile(predicate);
    }

    @Override
    public <R1> C.Sequence<R1> map(_.Function<? super R, ? extends R1> mapper) {
        return of(this, mapper);
    }

    @Override
    public <R1> C.Sequence<R1> flatMap(_.Function<? super R, ? extends Iterable<? extends R1>> mapper) {
        return applyMapper().flatMap(mapper);
    }

    @Override
    public C.Sequence<R> filter(_.Function<? super R, Boolean> predicate) {
        return applyMapper().filter(predicate);
    }

    @Override
    public C.Sequence<R> append(final R r) {
        return applyMapper().append(r);
    }

    @Override
    public C.Sequence<R> append(C.Sequence<R> seq) {
        return applyMapper().append(seq);
    }

    @Override
    public C.Sequence<R> prepend(R r) {
        return applyMapper().prepend(r);
    }

    @Override
    public C.Sequence<R> prepend(C.Sequence<R> seq) {
        return seq.append(this);
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
    public <R1> R1 reduce(R1 identity, _.Func2<R1, R, R1> accumulator) {
        return applyMapper().reduce(identity, accumulator);
    }

    @Override
    public <R1> R1 reduceLeft(R1 identity, _.Func2<R1, R, R1> accumulator) {
        return applyMapper().reduceLeft(identity, accumulator);
    }

    @Override
    public _.Option<R> reduce(_.Func2<R, R, R> accumulator) {
        return applyMapper().reduce(accumulator);
    }

    @Override
    public _.Option<R> reduceLeft(_.Func2<R, R, R> accumulator) {
        return applyMapper().reduceLeft(accumulator);
    }

    @Override
    public boolean allMatch(_.Function<? super R, Boolean> predicate) {
        return applyMapper().allMatch(predicate);
    }

    @Override
    public boolean anyMatch(_.Function<? super R, Boolean> predicate) {
        return applyMapper().anyMatch(predicate);
    }

    @Override
    public boolean noneMatch(_.Function<? super R, Boolean> predicate) {
        return applyMapper().noneMatch(predicate);
    }

    @Override
    public _.Option<R> findOne(_.Function<? super R, Boolean> predicate) {
        return applyMapper().findOne(predicate);
    }

    @Override
    public _.Option<R> findFirst(_.Function<? super R, Boolean> predicate) {
        return applyMapper().findFirst(predicate);
    }

    @Override
    public C.Sequence<R> accept(_.Function<? super R, ?> visitor) {
        return acceptLeft(visitor);
    }

    @Override
    public C.Sequence<R> acceptLeft(_.Function<? super R, ?> visitor) {
        return applyMapper().acceptLeft(visitor);
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = data.features();
        fs.add(C.Feature.READONLY);
        return fs;
    }

    @Override
    public Iterator<R> iterator() {
        return new MappedIterator<T, R>(data.iterator(), mapper);
    }
}
