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

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;
import org.osgl.util.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class Gh177 extends TestBase {

    public static class Foo {
        Bar bar;
        public Foo() {}
        public Foo(Bar bar) {
            this.bar = bar;
        }
    }

    public static class Bar {
        String name;
        int id;
        public Bar() {}
        public Bar(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

    @Test
    public void test() {
        Foo foo = $.map(new Foo(new Bar("abc", 123))).filter("-bar.name").to(Foo.class);
        Bar bar = foo.bar;
        eq(123, bar.id);
        isNull(bar.name);
    }

    @Test
    public void testInList() {
        List<Foo> fooList = C.list(new Foo(new Bar("abc", 1)));
        List<JSONObject> result = new ArrayList<>();
        $.map(fooList).filter("-bar.name").targetGenericType(new TypeReference<List<JSONObject>>() {
        }).to(result);
        yes(result.size() == 1);
        JSONObject json = result.get(0);
        notNull(json);
        yes(json.containsKey("bar"));
        Object obj = json.get("bar");
        yes(obj instanceof Bar);
        Bar bar = $.cast(obj);
        eq(1, bar.id);
        isNull(bar.name);
    }

}
