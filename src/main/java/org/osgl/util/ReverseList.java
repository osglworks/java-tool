package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/10/13
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ReverseList<T> extends DelegatingList<T> implements C.List<T> {

    private ReverseList(C.List<T> list) {
        super(true);
        data = list;
    }

    C.List<T> data() {
        return (C.List<T>)data;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = data().features();
        fs.add(C.Feature.READONLY);
        return fs;
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ReverseListIterator<T>(data().listIterator(size() - index));
    }

    @Override
    public T get(int index) {
        return data.get(size() - index);
    }

    @Override
    protected void forEachLeft(_.Function<? super T, ?> visitor) throws _.Break {
        data().acceptRight(visitor);
    }

    @Override
    protected void forEachRight(_.Function<? super T, ?> visitor) throws _.Break {
        data().acceptLeft(visitor);
    }

    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        return data().reduceRight(identity, accumulator);
    }

    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        return data().reduceRight(accumulator);
    }

    @Override
    public <R> R reduceRight(R identity, _.Func2<R, T, R> accumulator) {
        return data().reduceLeft(identity, accumulator);
    }

    @Override
    public _.Option<T> reduceRight(_.Func2<T, T, T> accumulator) {
        return data().reduceLeft(accumulator);
    }

    @Override
    public T head() throws NoSuchElementException {
        return data().last();
    }

    @Override
    public T last() throws NoSuchElementException {
        return data().head();
    }

    @Override
    public Iterator<T> reverseIterator() {
        return data().iterator();
    }

    @Override
    public C.List<T> reverse() {
        return data();
    }

    @Override
    public C.List<T> subList(int fromIndex, int toIndex) {
        return data().subList(toIndex - 1, fromIndex - 1);
    }

    static <T> C.List<T> wrap(C.List<T> list) {
        if (list instanceof ReverseList) {
            return ((ReverseList<T>)list).data();
        }
        if (list.size() == 0 && list.is(C.Feature.IMMUTABLE)) {
            return Nil.list();
        }
        return new ReverseList<T>(list);
    }
}
