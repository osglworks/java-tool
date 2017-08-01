package org.osgl.util;

import org.junit.Test;

import java.util.Map;

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

    @Test
    public void testToMapByKey() {
        String keys = "abcd,xyz,funny";
        Map<String, Integer> map = S.fastSplit(keys, ",").toMapByKey(S.F.LENGTH);
        eq(3, map.size());
        eq(5, map.get("funny"));
    }

    @Test
    public void testToMapByVal() {
        String keys = "abcd,xyz,funny";
        Map<Integer, String> map = S.fastSplit(keys, ",").toMapByVal(S.F.LENGTH);
        eq(3, map.size());
        eq("funny", map.get(5));
    }
}
