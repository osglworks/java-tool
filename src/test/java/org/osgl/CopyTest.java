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

import org.apache.commons.beanutils.BeanUtils;
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

public class CopyTest extends TestBase {

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
        private int id = N.randInt();
        private String name = S.random();

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Foo2 extends Foo {
        List<Integer> list1 = C.list(new int[] {1, 5, 3});

        public List<Integer> getList1() {
            return list1;
        }

        public void setList1(List<Integer> list1) {
            this.list1 = list1;
        }
    }

    public static class Bar {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Bar2 extends Bar {
        SortedSet<String> list1;
    }

    // BeanUtils does not support type conversion so
    // we need to create a specific Bean type for it
    public static class Bar2ForBeanUtils extends Bar {
        List<Integer> list1;

        public List<Integer> getList1() {
            return list1;
        }

        public void setList1(List<Integer> list1) {
            this.list1 = list1;
        }
    }

    public static class FooHolder {
        private int id = N.randInt();
        private Foo embedded = new Foo();
        private Date date = new Date();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Foo getEmbedded() {
            return embedded;
        }

        public void setEmbedded(Foo embedded) {
            this.embedded = embedded;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static class BarHolder {
        private int id;
        private Bar embedded;
        private DateTime date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Bar getEmbedded() {
            return embedded;
        }

        public void setEmbedded(Bar embedded) {
            this.embedded = embedded;
        }

        public DateTime getDate() {
            return date;
        }

        public void setDate(DateTime date) {
            this.date = date;
        }
    }

    @Test
    public void testSimpleBeanCopy() {
        Foo foo = new Foo();
        Bar bar = new Bar();
        $.copy(foo).to(bar);
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
    }

    @Test
    public void testSimpleBeanCopy_BeanUtils() throws Exception {
        Foo foo = new Foo();
        Bar bar = new Bar();
        BeanUtils.copyProperties(bar, foo);
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
    }

    @Test
    public void testNullValue() throws Exception {
        Foo foo = new Foo();
        foo.name = null;
        Bar bar = new Bar();
        bar.name = S.random();
        $.copy(foo).to(bar);
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
    }

    @Test
    public void testNullValue_BeanUtils() throws Exception {
        Foo foo = new Foo();
        foo.name = null;
        Bar bar = new Bar();
        bar.name = S.random();
        BeanUtils.copyProperties(bar, foo);
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
    }

    @Test
    public void testCopyMapToBean() {
        Map<String, Object> map = C.Map("id", "123", "name", 10);
        Bar bar = new Bar();
        $.copy(map).to(bar);
        eq(123, bar.id);
        eq("10", bar.name);
    }

    @Test
    public void testCopyMapToBean_BeanUtils() throws Exception {
        Map<String, Object> map = C.Map("id", "123", "name", 10);
        Bar bar = new Bar();
        BeanUtils.copyProperties(bar, map);
        eq(123, bar.id);
        eq("10", bar.name);
    }

    @Test
    public void testEmbedded() {
        FooHolder from = new FooHolder();
        BarHolder to = new BarHolder();
        $.copy(from).to(to);
        eq(from.id, to.id);
        eq(from.embedded.id, to.embedded.id);
        eq(from.embedded.name, to.embedded.name);
        eq(from.date.getTime(), to.date.getMillis());
    }

    // Oops - BeanUtils cannot handle type conversion
    @Test(expected = IllegalArgumentException.class)
    public void testEmbedded_BeanUtils() throws Exception {
        FooHolder from = new FooHolder();
        BarHolder to = new BarHolder();
        BeanUtils.copyProperties(to, from);
        eq(from.id, to.id);
        eq(from.embedded.id, to.embedded.id);
        eq(from.embedded.name, to.embedded.name);
        eq(from.date.getTime(), to.date.getMillis());
    }

    @Test
    public void testInherited() {
        Foo2 from = new Foo2();
        Bar2 to = new Bar2();
        $.copy(from, to);
        eq(from.getId(), to.getId());
        eq(from.getName(), to.getName());
        eq("135", S.join(to.list1).get());
    }

    @Test
    public void testInherited_BeanUtils() throws Exception {
        Foo2 from = new Foo2();
        Bar2ForBeanUtils to = new Bar2ForBeanUtils();
        BeanUtils.copyProperties(to, from);
        eq(from.getId(), to.getId());
        eq(from.getName(), to.getName());
        eq("153", S.join(to.list1).get());
    }
}
