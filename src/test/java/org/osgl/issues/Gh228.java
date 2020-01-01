package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2020 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
