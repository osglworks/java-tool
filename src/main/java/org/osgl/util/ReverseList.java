package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ReverseList<T> extends ListBase1<T> implements C.List<T> {
    private final C.List<T> lst_;

    ReverseList(C.List<T> list) {
        E.NPE(list);
        lst_ = list;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return lst_.features();
    }

    @Override
    public Iterator<T> iterator() {
        return lst_.reverseIterator();
    }

    @Override
    public int size() throws UnsupportedOperationException {
        return lst_.size();
    }

    @Override
    public <R> C.List<R> map(_.Function<? super T, ? extends R> mapper) {
        //TODO ...
        return null;
    }

    @Override
    public <R> C.List<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        //TODO ...
        return null;
    }

    @Override
    public C.List<T> accept(_.Function<? super T, ?> visitor) {
        return acceptLeft(visitor);
    }

    @Override
    public C.List<T> acceptLeft(_.Function<? super T, ?> visitor) {
        return lst_.acceptRight(visitor);
    }

    @Override
    public C.List<T> acceptRight(_.Function<? super T, ?> visitor) {
        return lst_.acceptLeft(visitor);
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        return lst_.reduceLeft(identity, accumulator);
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return lst_.reduceLeft(accumulator);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        return lst_.reduceRight(identity, accumulator);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        return lst_.reduceRight(accumulator);
    }

    @Override
    public T first() throws NoSuchElementException {
        return lst_.last();
    }

    @Override
    public T last() throws UnsupportedOperationException, NoSuchElementException {
        return lst_.first();
    }

    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        return lst_.findLast(predicate);
    }

    @Override
    protected void forEachRight(_.Function<? super T, ?> visitor) {
        super.forEachLeft(visitor);
    }

    @Override
    protected void forEachLeft(_.Function<? super T, ?> visitor) {
        super.forEachRight(visitor);
    }

    @Override
    protected void forEach(_.Function<? super T, ?> visitor) {
        super.forEachRight(visitor);
    }

    @Override
    public boolean isEmpty() {
        return lst_.isEmpty();
    }

    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return lst_.reduceRight(identity, accumulator);
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return lst_.reduceRight(accumulator);
    }

    @Override
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        return lst_.findOne(predicate);
    }



    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return super.anyMatch(predicate);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return super.noneMatch(predicate);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return super.allMatch(predicate);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
