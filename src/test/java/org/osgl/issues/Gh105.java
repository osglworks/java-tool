package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Gh105 extends TestBase {

    public static class Foo {
        Map<String, String> map = new HashMap<>();
    }

    public static class Bar {
        Map<String, String> map = new LinkedHashMap<>();
    }

    @Test
    public void test1() {
        Foo foo = new Foo();
        foo.map.put("X", "10");
        Bar bar = $.deepCopy(foo).to(Bar.class);
        eq("10", bar.map.get("X"));
        yes(bar.map instanceof LinkedHashMap, "It shall not change bar.map instance");
    }

    public static class FooWrapper {
        Foo foo = new Foo();
    }

    public static class BarWrapper {
        Bar bar = new Bar();
    }

    @Test
    public void test2() {
        FooWrapper fooW = new FooWrapper();
        fooW.foo.map.put("X", "10");

        BarWrapper barW = $.map(fooW).map("foo").to("bar").to(BarWrapper.class);
        eq("10", barW.bar.map.get("X"));
        yes(barW.bar.map instanceof LinkedHashMap);
    }

}
