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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.osgl.exception.MappingException;
import org.osgl.util.C;
import org.osgl.util.N;
import org.osgl.util.S;

import java.util.*;

@RunWith(Enclosed.class)
public class MappingTest extends TestBase {

    static class Foo {
        public int id = N.randInt();
        public int[] ia = {1, 2, 3};
        public String name = S.random();
        public Date createDate = new Date();
        public Set<Integer> si = C.newSet(1, 2);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Foo foo = (Foo) o;
            return id == foo.id &&
                    Arrays.equals(ia, foo.ia) &&
                    Objects.equals(name, foo.name) &&
                    Objects.equals(createDate, foo.createDate);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(id, name, createDate);
            result = 31 * result + Arrays.hashCode(ia);
            return result;
        }
    }

    static class Bar {
        public DateTime create_date = DateTime.now();
        public int id = N.randInt();
        public int[] ia = {1, 2};
        public String name = S.random();
        public long value = N.randLong();
        public Set<Integer> si = C.newSet(10, 20);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bar bar = (Bar) o;
            return id == bar.id &&
                    value == bar.value &&
                    Arrays.equals(ia, bar.ia) &&
                    Objects.equals(name, bar.name) &&
                    Objects.equals(create_date, bar.create_date);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(id, name, value, create_date);
            result = 31 * result + Arrays.hashCode(ia);
            return result;
        }
    }

    static class Bean {
        public Foo foo = new Foo();
        public Map<String, Bar> map = C.Map("bar1", new Bar());

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bean bean = (Bean) o;
            return Objects.equals(foo, bean.foo) &&
                    Objects.equals(map, bean.map);
        }

        @Override
        public int hashCode() {

            return Objects.hash(foo, map);
        }
    }

    static Lang.TypeConverter<Date, DateTime> DATE_TO_DATETIME = new Lang.TypeConverter<Date, DateTime>() {
        @Override
        public DateTime convert(Date date) {
            return new DateTime(date);
        }
    };

    static Lang.TypeConverter<DateTime, Date> DATETIME_TO_DATE = new Lang.TypeConverter<DateTime, Date>() {
        @Override
        public Date convert(DateTime dateTime) {
            return new Date(dateTime.getMillis());
        }
    };


    @Ignore
    static class Base {

        Foo foo1;
        Foo foo2;
        Foo foo3;

        Bar bar1;
        Bar bar2;
        Bar bar3;

        Foo[] foo_1_array;
        Foo[] foo_2_array;
        Foo[] foo_3_array;

        Bar[] bar_1_array;
        Bar[] bar_2_array;
        Bar[] bar_3_array;

        List<Foo> foo_1_list;
        List<Foo> foo_2_list;
        List<Foo> foo_3_list;

        List<Bar> bar_1_list;
        List<Bar> bar_2_list;
        List<Bar> bar_3_list;

        Bean bean;

        int[] int_3_array;


        @Before
        public void init() {
            foo1 = new Foo();
            foo2 = new Foo();
            foo3 = new Foo();

            bar1 = new Bar();
            bar2 = new Bar();
            bar3 = new Bar();

            foo_1_array = new Foo[] {foo1};
            foo_2_array = new Foo[] {foo1, foo2};
            foo_3_array = new Foo[] {foo1, foo2, foo3};

            bar_1_array = new Bar[] {bar1};
            bar_2_array = new Bar[] {bar1, bar2};
            bar_3_array = new Bar[] {bar1, bar2, bar3};

            foo_1_list = C.list(foo1);
            foo_2_list = C.list(foo1, foo2);
            foo_3_list = C.list(foo1, foo2, foo3);

            bar_1_list = C.list(bar1);
            bar_2_list = C.list(bar1, bar2);
            bar_3_list = C.list(bar1, bar2, bar3);

            bean = new Bean();

            int_3_array = new int[] {1, 2, 3};
        }

    }


    public static class CopyArrayToArray extends Base {

        @Test
        public void simpleCase() {
            int[] ia = new int[3];
            int[] result = $.copy(int_3_array).to(ia);
            same(result, ia);
            eq("123", S.join(result).get());
        }

        @Test
        public void targetArrayWithNoEnoughSlot() {
            int[] ia = new int[2];
            int[] result = $.copy(int_3_array).to(ia);
            notSame(result, ia);
            eq("123", S.join(result).get());
        }

        @Test
        public void itShallClearExistingArray() {
            int[] ia = {10, 9, 8, 7};
            int[] result = $.copy(int_3_array).to(ia);
            same(result, ia);
            eq("1230", S.join(result).get());
        }

    }

    public static class MergeArrayToArray extends Base {

        @Test
        public void simpleCase() {
            int[] ia = new int[3];
            int[] result = $.merge(int_3_array).to(ia);
            same(result, ia);
            eq("123", S.join(result).get());
        }

        @Test
        public void itShallNotClearExistingArray() {
            int[] ia = {10, 9, 8, 7};
            int[] result = $.merge(int_3_array).to(ia);
            same(result, ia);
            eq("1237", S.join(result).get());
        }

    }

    public static class MapArrayToArray extends Base {

        @Test
        public void mapToArrayWithConvertibleType() {
            String[] sa = new String[3];
            String[] result = $.map(int_3_array).to(sa);
            same(result, sa);
            eq("2", result[1]);
            eq("123", S.join(result).get());
        }

        @Test(expected = MappingException.class)
        public void mapToArrayWithNonConvertibleType() {
            Class[] ca = new Class[3];
            $.map(int_3_array).to(ca);
        }

        @Test
        public void arrayOfPojo() {
            Bar bar1_copy = $.cloneOf(bar1);
            notSame(bar1_copy, bar1);
            eq(bar1_copy, bar1);

            Object source = foo_3_array;
            Bar[] ba = bar_3_array;
            Bar[] result = $.map(source).withConverter(DATE_TO_DATETIME, DATETIME_TO_DATE).to(ba);

            same(result, ba);
            meq(foo1, result[0]);
            meq(foo2, result[1]);
            meq(foo3, result[2]);

            ne(bar1_copy, bar1);
        }

    }

    public static class PojoToPojo extends Base {

        @Test
        public void testStackoverflowCase() {
            Bar source = new Bar();
            Bar target = new Bar();
            $.merge(source).to(target);
        }

        @Test
        public void deepCopySimpleCase() {
            Foo source = new Foo();
            Foo target = new Foo();
            Foo result = $.deepCopy(source).to(target);
            same(result, target);
            notSame(source, target);
            eq(source, target);
            notSame(source.ia, target.ia);
            eq(source.si, target.si);
            notSame(source.si, target.si);
        }

        @Test
        public void deepCopyToDifferentType() {
            Foo source = new Foo();
            Bar target = new Bar();
            Bar result = $.deepCopy(source).to(target);
            same(result, target);
            eq(source.id, target.id);
            eq(source.name, target.name);
            eq(source.ia, target.ia);
            eq(source.si, target.si);
            notSame(source.ia, target.ia);
            isNull(target.create_date);
        }

        @Test(expected = MappingException.class)
        public void deepCopyWithTypeMismatch() {
            Foo source = new Foo();
            Bar target = new Bar();
            $.deepCopy(source).keywordMatching().to(target);
        }

        @Test
        public void deepCopyIgnoreError() {
            Foo source = new Foo();
            Bar target = new Bar();
            $.deepCopy(source).keywordMatching().ignoreError().to(target);
            eq(source.id, target.id);
            eq(source.name, target.name);
            eq(source.ia, target.ia);
            eq(source.si, target.si);
            notSame(source.ia, target.ia);
            isNull(target.create_date);
        }


        @Test
        public void testMerge() throws Exception {
            Foo source = foo1;
            Thread.sleep(10);
            Bar target = new Bar();
            $.merge(source).to(target);
            eq(source.id, target.id);
            eq(source.name, target.name);
            eq(source.ia, target.ia);
            ne(source.si, target.si);
            notNull(target.create_date); // there is initial value
            ne(source.createDate.getTime(), target.create_date.getMillis());
            yes(target.si.containsAll(source.si));
        }

        @Test
        public void testMapping() {
            Foo source = foo1;
            Bar target = bar1;
            $.map(source).withConverter(DATE_TO_DATETIME).to(target);
            eq(source.id, target.id);
            eq(source.name, target.name);
            eq(source.ia, target.ia);
            ne(source.si, target.si);
            yes(target.si.containsAll(source.si));
            notNull(target.create_date);
            eq(source.createDate.getTime(), target.create_date.getMillis());
        }

        @Test
        public void testComplexDeepCopy() {
            Bean source = new Bean();
            Bean target = new Bean();
            Bean result = $.deepCopy(source).to(target);
            same(target, result);
            eq(target, source);
            notSame(source.foo, target.foo);
            notSame(source.foo.ia, target.foo.ia);
            notSame(source.map.get("bar1"), target.map.get("bar1"));
        }

        @Test
        public void testDeepCopyWithFilter() {
            Bean source = new Bean();
            Bean target = new Bean();
            $.deepCopy(source).filter("-map.name,-foo.name").to(target);
            ne(target, source);
            
            Foo sourceFoo = source.foo;
            Foo targetFoo = target.foo;
            ne(sourceFoo, targetFoo);
            eq(sourceFoo.createDate, targetFoo.createDate);
            eq(sourceFoo.id, targetFoo.id);
            eq(sourceFoo.ia, targetFoo.ia);
            notSame(sourceFoo.ia, targetFoo.ia);
            ne(sourceFoo.name, targetFoo.name);

            Bar sourceBar = source.map.get("bar1");
            Bar targetBar = target.map.get("bar1");
            ne(sourceBar, targetBar);
            eq(sourceBar.create_date, targetBar.create_date);
            eq(sourceBar.id, targetBar.id);
            eq(sourceBar.ia, targetBar.ia);
            notSame(sourceBar.ia, targetBar.ia);
            ne(sourceBar.name, targetBar.name);
        }

        @Test
        public void testShallowCopy() {
            Bean source = new Bean();
            Bean target = new Bean();
            Bean result = $.copy(source).to(target);
            same(target, result);
            eq(target, source);
            same(source.foo, target.foo);
            same(source.map.get("bar1"), target.map.get("bar1"));
            same(source.foo.si, target.foo.si);
        }
    }

    @Ignore // TBD
    public static class CopyListToList extends Base {

    }

    static void eq(Foo foo, Bar bar) {
        eq(foo, bar, false);
    }

    static void meq(Foo foo, Bar bar) {
        eq(foo, bar, true);
    }

    static void eq(Foo foo, Bar bar, boolean isMapping) {
        eq(foo.id, bar.id);
        eq(foo.name, bar.name);
        if (isMapping) {
            eq(foo.createDate.getTime(), bar.create_date.getMillis());
        }
        if (null == foo.ia) {
            isNull(bar.ia);
        } else {
            notNull(bar.ia);
            eq(foo.ia, bar.ia);
        }
    }
}
