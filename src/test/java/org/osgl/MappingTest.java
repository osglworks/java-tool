package org.osgl;

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

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.C;
import org.osgl.util.N;
import org.osgl.util.S;
import org.osgl.util.converter.TypeConverterRegistry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class MappingTest extends TestBase {

    @BeforeClass
    public static void registerJodaTimeConverter() {
        TypeConverterRegistry.INSTANCE.register(new $.TypeConverter<ReadableInstant, Long>() {
            @Override
            public Long convert(ReadableInstant o) {
                return o.getMillis();
            }
        }).register(new $.TypeConverter<Long, DateTime>() {
            @Override
            public DateTime convert(Long o) {
                return new DateTime().withMillis(o);
            }
        });
    }

    public static class Foo {
        int id = N.randInt();
        String name = S.random();
    }

    public static class Foo2 extends Foo {
        List<Integer> theList = C.list(new int[] {1, 5, 3});
    }

    public static class Bar {
        int id = 0;
        String name;
    }

    public static class Bar2 extends Bar {
        SortedSet<String> the_List;
    }

    public static class FooHolder {
        int id = N.randInt();
        Foo embedded = new Foo();
        Date createDate = new Date();
    }

    public static class BarHolder {
        int id;
        Bar embedded;
        DateTime create_date;
    }

    @Test
    public void testSimpleBeanMapping() {
        Foo foo = new Foo();
        Bar bar = new Bar();
        $.map(foo).to(bar);
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
    }

    @Test
    public void testNullValue() {
        Foo foo = new Foo();
        foo.name = null;
        Bar bar = new Bar();
        bar.name = S.random();
        $.map(foo).to(bar);
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
    }

    @Test
    public void testCopyMapToBean() {
        Map<String, Object> map = C.Map("id", "123", "name", 10);
        Bar bar = new Bar();
        $.map(map).to(bar);
        eq(123, bar.id);
        eq("10", bar.name);
    }

    @Test
    public void testEmbedded() {
        FooHolder from = new FooHolder();
        BarHolder to = new BarHolder();
        $.map(from).to(to);
        eq(from.id, to.id);
        eq(from.embedded.id, to.embedded.id);
        eq(from.embedded.name, to.embedded.name);
        eq(from.createDate.getTime(), to.create_date.getMillis());
    }

    @Test
    public void testInherited() {
        Foo2 from = new Foo2();
        Bar2 to = new Bar2();
        $.map(from).to(to);
        eq(from.id, to.id);
        eq(from.name, to.name);
        eq("135", S.join(to.the_List).get());
    }

    @Test
    public void testFilter() {
        Foo2 from = new Foo2();
        Bar2 to = new Bar2();
        $.map(from).filter("-name").to(to);
        eq(from.id, to.id);
        isNull(to.name);
        eq("135", S.join(to.the_List).get());

        to = new Bar2();
        $.map(from).filter("name").to(to);
        eq(0, to.id);
        eq(from.name, to.name);
        isNull(to.the_List);
    }

    @Test
    public void testEmbeddedFilter() {
        FooHolder from = new FooHolder();
        BarHolder to = new BarHolder();
        $.map(from).filter("-embedded.name").to(to);
        eq(from.id, to.id);
        eq(from.embedded.id, to.embedded.id);
        isNull(to.embedded.name);
        eq(from.createDate.getTime(), to.create_date.getMillis());

        to = new BarHolder();
        $.map(from).filter("-embedded").to(to);
        eq(from.id, to.id);
        isNull(to.embedded);
        eq(from.createDate.getTime(), to.create_date.getMillis());
    }

}
