package org.osgl.util;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImmutableListTest extends ListTestBase {
    @Override
    protected C.List<Integer> prepareData(int... ia) {
        return C.list(ia);
    }

    @Override
    protected C.List<Integer> prepareEmptyData() {
        return C.list();
    }

    @Override
    protected <T> C.List<T> prepareTypedData(T... ta) {
        return C.listOf(ta);
    }
}
