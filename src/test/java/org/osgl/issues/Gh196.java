package org.osgl.issues;

import org.junit.Test;
import org.osgl.TestBase;
import org.osgl.util.Generics;

import java.util.Map;
import java.util.TreeMap;

public class Gh196 extends TestBase {

    public static class GrandParent<T> {
        T t;
    }

    public static class Parent<K, V, M extends Map<K, V>> extends GrandParent<M> {
    }

    public static class Me extends Parent<String, Integer, TreeMap<String, Integer>> {}

    @Test
    public void test() {
        Map<String, Class> lookup = Generics.buildTypeParamImplLookup(Me.class);
        eq(String.class, lookup.get("K"));
        eq(Integer.class, lookup.get("V"));
        eq(TreeMap.class, lookup.get("M"));
    }

}
