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

import com.alibaba.fastjson.*;
import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.XML;
import org.w3c.dom.Document;

import java.util.*;

public class GH192 extends TestBase {

    public static class Bar {
        public int id;
        public String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bar bar = (Bar) o;
            return id == bar.id &&
                    Objects.equals(name, bar.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    public static class Foo {
        public List<Bar> barList = new ArrayList<>();
        public int id;
        public String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Foo foo = (Foo) o;
            return id == foo.id &&
                    Objects.equals(barList, foo.barList) &&
                    Objects.equals(name, foo.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(barList, id, name);
        }
    }

    @Test
    public void testJsonObject() {
        Bar b1 = new Bar();
        b1.id = 1;
        b1.name = "b1";

        Bar b2 = new Bar();
        b2.id = 2;
        b2.name = "b2";

        Foo foo = new Foo();
        foo.barList.add(b1);
        foo.barList.add(b2);
        foo.id = 1;
        foo.name = "foo";

        JSONObject json = $.convert(foo).to(JSONObject.class);
        System.out.println(JSON.toJSONString(json, true));

        Document doc = $.convert(json).to(Document.class);
        System.out.println(XML.toString(doc, true));

        JSONObject converted = $.convert(doc).to(JSONObject.class);
        System.out.println(JSON.toJSONString(converted, true));

        Foo foo1 = JSON.parseObject(JSON.toJSONString(converted), Foo.class);
        eq(foo, foo1);
    }

    @Test
    public void testJsonArray() {
        Bar b1 = new Bar();
        b1.id = 1;
        b1.name = "b1";

        Bar b2 = new Bar();
        b2.id = 2;
        b2.name = "b2";

        List<Bar> barList = new ArrayList<>();
        barList.add(b1);
        barList.add(b2);

        JSONArray array = $.convert(barList).to(JSONArray.class);
        System.out.println(JSON.toJSONString(array, true));

        Document doc = $.convert(array).to(Document.class);
        System.out.println(XML.toString(doc, true));

        JSONArray converted = $.convert(doc).to(JSONArray.class);
        System.out.println(JSON.toJSONString(converted, true));

        List<Bar> barList1 = JSON.parseObject(JSON.toJSONString(converted), new TypeReference<List<Bar>>(){});
        eq(barList, barList1);
    }

}
