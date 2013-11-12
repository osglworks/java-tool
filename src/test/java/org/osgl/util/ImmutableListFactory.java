package org.osgl.util;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
class ImmutableListFactory<T> implements TestListFactory<T> {
    @Override
    public C.List<T> create(T... elements) {
        return ImmutableList.of(elements);
    }
}
