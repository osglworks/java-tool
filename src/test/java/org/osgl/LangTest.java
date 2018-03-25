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

import org.junit.Test;
import org.osgl.util.C;
import org.osgl.util.S;
import org.osgl.util.converter.TypeConverterRegistry;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class LangTest extends TestBase {

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

    @Test
    public void testToString2() {
        String[][] sa = {{"foo", "bar"}, {"1", "2"}};
        eq("[[foo, bar], [1, 2]]", $.toString2(sa));
    }

    @Test
    public void testRandom() {
        C.Range<Integer> r = C.range(10, 100);
        for (int i = 0; i < 100; ++i) {
            int n = $.random(r);
            yes(n >= 10);
            yes(n < 100);
        }
    }

    @Test
    public void testPredicateOr() {
        C.List<String> l = C.list("a.xml", "b.html", "c.txt", "d.txt");
        l = l.filter(S.F.endsWith(".xml").or(S.F.endsWith(".html")));
        yes(l.contains("a.xml"));
        yes(l.contains("b.html"));
        no(l.contains("c.txt"));
        no(l.contains("d.txt"));
    }

    private static class Foo {
        private String f1;
        private static String fs1;
    }

    private static class Bar extends Foo {
        private String f1;
        private int f2;
    }

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

    enum Code {
        AB, bc, Red;
    }

    @Test
    public void testAsEnum() {
        assertSame(Code.AB, $.asEnum(Code.class, "ab"));
        assertSame(Code.bc, $.asEnum(Code.class, "bc"));
        assertNull($.asEnum(Code.class, "abc"));
        assertNull($.asEnum(Code.class, null));

        assertSame(Code.AB, $.asEnum(Code.class, "AB", true));
        assertNull($.asEnum(Code.class, "ab", true));
    }


    @Test
    public void testConvert() {
        int n = 600;
        String s = "60";
        eq((byte) 600, $.convert(n).to(Byte.class));
        eq((byte) 60, $.convert(s).to(Byte.class));
    }

    @Test
    public void testConvertEnum() {
        eq(Code.AB, $.convert("AB").to(Code.class));
        eq(Code.AB, $.convert("ab").caseInsensitivie().to(Code.class));
    }

    @Test
    public void testConvertNullValue() {
        eq(0, $.convert(null).toInt());
        assertNull($.convert(null).toInteger());
        assertNull($.convert(null).to(Date.class));
    }

    @Test
    public void testConvertNullWithDef() {
        eq(5, $.convert(null).defaultTo(5).toInt());
        eq(2, $.convert("2").defaultTo(5).toInt());
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

    public static class MyFrom {
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

    public static class MyTo {
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
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    static class MyConverter extends $.TypeConverter<MyFrom, MyTo> {
        @Override
        public MyTo convert(MyFrom myFrom) {
            return new MyTo(myFrom.id);
        }
    }


    @Test
    public void testConvertExtension() {
        TypeConverterRegistry.INSTANCE.register(new MyConverter());
        String id = S.random();
        eq(new MyTo(id), $.convert(new MyFrom(id)).to(MyTo.class));
    }

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
