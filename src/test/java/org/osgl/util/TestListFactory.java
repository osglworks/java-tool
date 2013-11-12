package org.osgl.util;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TestListFactory<T> {
    C.List<T> create(T ... elements);
}
