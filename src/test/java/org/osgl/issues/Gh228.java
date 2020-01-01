package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.AdaptiveMap;
import org.osgl.util.SimpleAdaptiveMap;
import org.osgl.util.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Gh228 extends TestBase {

    public static class Bar {
        public int id;
    }

    public static class Foo {
        public String name;
        public Bar bar;
    }

    public static Foo createFoo(String name, int id) {
        Bar bar = new Bar();
        bar.id = id;
        Foo foo = new Foo();
        foo.name = name;
        foo.bar = bar;
        return foo;
    }

    @Test
    public void testMapToPojo() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "abc");
        map.put("bar.id", 123);
        Foo foo = $.map(map).to(Foo.class);
        eq("abc", foo.name);
        eq(123, foo.bar.id);
    }

    @Test
    public void testPojoToMap() {
        Foo foo = createFoo("abc", 123);
        Map<String, Object> map = new HashMap<>();
        $.map(foo).targetGenericType(TypeReference.mapOf(String.class, Object.class)).to(map);
        eq("abc", map.get("abc"));
        eq(123, map.get("bar.id"));
    }

    @Test
    public void testPropertiesToPojo() {
        Properties properties = new Properties();
        properties.put("name", "abc");
        properties.put("bar.id", "123");
        Foo foo = $.map(properties).to(Foo.class);
        eq("abc", foo.name);
        eq(123, foo.bar.id);
    }

    @Test
    public void testPojoToProperties() {
        Foo foo = createFoo("abc", 123);
        Properties map = $.map(foo).to(Properties.class);
        eq("abc", map.get("abc"));
        eq("123", map.get("bar.id"));
    }

    @Test
    public void testAdaptiveMapToPojo() {
        AdaptiveMap map = new SimpleAdaptiveMap();
        map.putValue("name", "abc");
        map.putValue("bar.id", 123);
        Foo foo = $.deepCopy(map).to(Foo.class);
        eq("abc", foo.name);
        eq(123, foo.bar.id);
    }

    @Test
    public void testPojoToAdaptiveMap() {
        Foo foo = createFoo("abc", 123);
        SimpleAdaptiveMap map = new SimpleAdaptiveMap();
        $.map(foo).to(map);
        eq("abc", map.getValue("abc"));
        eq(123, map.getValue("bar.id"));
    }

}
