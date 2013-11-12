package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/10/13
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
enum Iterators {
    ;
    public static <T> Iterator<T> filterIndex(Iterator<? extends T> itr, _.Function<Integer, Boolean> predicate) {
        return new IndexFilteredIterator<T>(itr, predicate);
    }

    public static <T> Iterator<T> filter(Iterator<? extends T> itr, _.Function<? super T, Boolean> predicate) {
        return new FilteredIterator<T>(itr, predicate);
    }

    public static <T> Iterator<T> filterWhile(Iterator<? extends T> itr, _.Function<? super T, Boolean> predicate) {
        return new FilteredIterator<T>(itr, predicate, FilteredIterator.Type.WHILE);
    }

    public static <T> Iterator<T> filterUntil(Iterator<? extends T> itr, _.Function<? super T, Boolean> predicate) {
        return new FilteredIterator<T>(itr, predicate, FilteredIterator.Type.UNTIL);
    }

    public static <T> Iterator<T> composite(Iterator<T> i1, Iterator<T> i2) {
        return new CompositeIterator<T>(i1, i2);
    }

    public static <T> Iterator<T> of(T t) {
        return new SingletonIterator<T>(t);
    }

    public static <T, R> Iterator<R> map(Iterator<? extends T> itr, _.Function<? super T, ? extends R> mapper) {
        return new MappedIterator<T, R>(itr, mapper);
    }

    public static <T, R> Iterator<R> flatMap(Iterator<? extends T> itr, _.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return new FlatMappedIterator<T, R>(itr, mapper);
    }

}
