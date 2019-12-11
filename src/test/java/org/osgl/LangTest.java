package org.osgl;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.osgl.util.C;
import org.osgl.util.S;
import org.osgl.util.converter.TypeConverterRegistry;

import java.lang.reflect.Field;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(Enclosed.class)
public class LangTest extends TestBase {

    private static class MyFrom {
        public String id;

        public MyFrom(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyFrom myFrom = (MyFrom) o;
            return id != null ? id.equals(myFrom.id) : myFrom.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    private static class MyTo {
        public String id;

        public MyTo(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyTo MyTo = (MyTo) o;
            return id != null ? id.equals(MyTo.id) : MyTo.id == null;
        }

        @Override
        public String toString() {
            return id;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    @Ignore
    static class MyConverter extends $.TypeConverter<MyFrom, MyTo> {
        @Override
        public MyTo convert(MyFrom myFrom) {
            return new MyTo(myFrom.id);
        }
    }

    @Ignore
    static class StringToMyFrom extends $.TypeConverter<String, MyFrom> {
        @Override
        public MyFrom convert(String s) {
            return new MyFrom(s);
        }
    }

    private static class Foo {
        private String f1;
        private static String fs1;
    }

    private static class Bar extends Foo {
        private String f1;
        private int f2;
    }

    enum Code {
        AB, bc, RED_GREEN_BLUE
    }

    private interface I0 {
    }

    private interface I0_1 extends I0 {
    }

    private static class C0 implements I0_1 {
    }

    private static class C1 extends C0 {
    }


    // -------------- Tests -----------------------

    public static class NullTest extends TestBase {
        @Test
        public void testAnyNull() {
            yes($.anyNull("", 5, null));
            no($.anyNull("", 5));
        }

        @Test
        public void testNoneNull() {
            no($.noneNull("", 5, null));
            yes($.noneNull("", 5));
        }
    }

    public static class ToStringTest extends TestBase {
        @Test
        public void testToString2() {
            String[][] sa = {{"foo", "bar"}, {"1", "2"}};
            eq("[[foo, bar], [1, 2]]", $.toString2(sa));
        }
    }

    public static class RandomTest extends TestBase {
        @Test
        public void testRandom() {
            C.Range<Integer> r = C.range(10, 100);
            for (int i = 0; i < 100; ++i) {
                int n = $.random(r);
                yes(n >= 10);
                yes(n < 100);
            }
        }
    }

    public static class FunctionTest {
        @Test
        public void testPredicateOr() {
            C.List<String> l = C.list("a.xml", "b.html", "c.txt", "d.txt");
            l = l.filter(S.F.endsWith(".xml").or(S.F.endsWith(".html")));
            yes(l.contains("a.xml"));
            yes(l.contains("b.html"));
            no(l.contains("c.txt"));
            no(l.contains("d.txt"));
        }
    }

    public static class ReflectionTest extends TestBase {
        @Test
        public void testFieldsOf() {
            List<Field> fields = $.fieldsOf(Bar.class, false);
            int jacocoFields = 0;
            if (4 != fields.size()) {
                // we are running with jacoco enhancement
                for (Field f : fields) {
                    if (f.getName().contains("jacoco")) {
                        jacocoFields++;
                    }
                }
            }
            eq(4 + jacocoFields, fields.size());
            eq(4 + jacocoFields, new HashSet<>(fields).size());
            fields = $.fieldsOf(Bar.class, true);
            eq(3, fields.size());
            eq(3, new HashSet<>(fields).size());
        }

        @Test
        public void testInterfacesOf() {
            Set<Class> interfaces = $.interfacesOf(C1.class);
            yes(interfaces.contains(I0.class));
            yes(interfaces.contains(I0_1.class));
        }

        @Test
        public void testSuperClassesOf() {
            List<Class> superClasses = $.superClassesOf(C1.class);
            eq(2, superClasses.size());
            eq(C0.class, superClasses.get(0));
            eq(Object.class, superClasses.get(1));
        }
    }

    public static class MiscTest {
        @Test
        public void testAsEnum() {
            assertSame(Code.AB, $.asEnum(Code.class, "ab"));
            assertSame(Code.bc, $.asEnum(Code.class, "bc"));
            assertNull($.asEnum(Code.class, "abc"));
            assertNull($.asEnum(Code.class, null));

            assertSame(Code.AB, $.asEnum(Code.class, "AB", true));
            assertNull($.asEnum(Code.class, "ab", true));

            assertSame(Code.RED_GREEN_BLUE, $.asEnum(Code.class, "redGreenBlue"));
            assertSame(Code.RED_GREEN_BLUE, $.asEnum(Code.class, "Red.Green.Blue"));
            assertSame(Code.RED_GREEN_BLUE, $.asEnum(Code.class, "Red Green Blue"));
            assertNull($.asEnum(Code.class, "Red.Green.Blue", true));
        }
    }


    public static class ConvertTest {
        @Test
        public void testConvert() {
            String s = "60";
            eq((byte) 60, $.convert(s).to(Byte.class));
            eq(255, $.convert("FF").hint(16).toInteger());
            Date date = $.convert("06 Apr 2018").hint("dd MMM yyyy").toDate();
            eq("2018-04-06", $.convert(date).hint("yyyy-MM-dd").toString());

        }

        @Test
        public void testPipeline() {
            final Date date = new Date();
            byte[] ba1 = $.convert(date).pipeline(String.class).to(byte[].class);
            byte[] ba2 = $.convert(date).to(byte[].class);
            eq(ba1, ba2);
            eq("2018-04-06", $.convert("06/Apr/2018").hint("dd/MMM/yyyy").pipeline(Date.class).hint("yyyy-MM-dd").toString());
        }

        @Test
        public void testConvertEnum() {
            eq(Code.AB, $.convert("AB").strictMatching().to(Code.class));
            eq(Code.AB, $.convert("ab").to(Code.class));
            eq(Code.RED_GREEN_BLUE, $.convert("red-green-blue").to(Code.class));
        }

        @Test(expected = IllegalArgumentException.class)
        public void testConvertEnumFailureWithStrictMatching() {
            $.convert("ab").strictMatching().to(Code.class);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testConvertEnumFailureWithUnknownName() {
            $.convert("some-random-str").to(Code.class);
        }

        @Test
        public void testConvertNullValue() {
            eq(0, $.convert(null).toInt());
            assertNull($.convert(null).toInteger());
            assertNull($.convert(null).to(Date.class));
        }

        @Test
        public void testConvertNullWithDef() {
            eq(5, $.convert(null).defaultTo(5).toInteger());
            eq(2, $.convert("2").defaultTo(5).toInteger());
        }

        @Test
        public void testConvertDate() throws Exception {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat();
            Date expected = format.parse(format.format(date)); // calibrate the date
            eq(expected, $.convert(format.format(date)).toDate());

            String pattern = "yyyy-MM-dd";
            format = new SimpleDateFormat(pattern);
            String dateStr = format.format(date);
            expected = format.parse(dateStr); // calibrate the date
            eq(expected, $.convert(dateStr).hint(pattern).toDate());
        }

        @Test
        public void testConvertExtension() {
            TypeConverterRegistry.INSTANCE
                    .register(new MyConverter())
                    .register(new StringToMyFrom());
            String id = S.random();
            eq(new MyTo(id), $.convert(new MyFrom(id)).to(MyTo.class));
            eq("abc", $.convert("abc").to(MyTo.class).toString());
        }

        @Test
        public void testConvertArray() {
            int[] source = {1, 2, 3};
            String[] target = $.convert(source).to(String[].class);
            eq(3, target.length);
            eq("3", target[2]);
            Iterable iterable = $.convert(source).to(Iterable.class);
            eq("123", S.join(iterable).get());
        }

        @Test
        public void testSqlDateTypeConverters() {
            Date now = new Date();
            Timestamp ts = $.convert(now).to(Timestamp.class);
            eq(ts.getTime(), now.getTime());

            Time time = $.convert(now).to(Time.class);
            eq(time.getTime(), now.getTime());
        }
    }

    public static class FluentApiTest extends TestBase {
        @Test
        public void testIs() {
            List<String> list = new ArrayList<>(C.list("a", "b", "c"));
            yes($.is(list).list());
            no($.is(list).set());
            no($.is(list).array());
            no($.is(list).instanceOf(LinkedList.class));
            yes($.is(list).public_());
            yes($.is(List.class).abstract_());
            yes($.is(List.class).interface_());

            List<Integer> list2 = new ArrayList<>();
            yes($.is(list2).kindOf(list));
        }
    }


}
