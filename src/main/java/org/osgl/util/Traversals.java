package org.osgl.util;

import org.osgl._;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 24/10/13
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
enum Traversals {
    ;
    static <T, R> MappedTrav<T, R> map(Iterable<T> iterable, _.Function<? super T, ? extends R> mapper) {
        return new MappedTrav<T, R>(iterable, mapper);
    }
}
