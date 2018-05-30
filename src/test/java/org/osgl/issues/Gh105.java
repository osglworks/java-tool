package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
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
