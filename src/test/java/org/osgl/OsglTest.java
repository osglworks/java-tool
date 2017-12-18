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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

public class OsglTest extends TestBase {

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

}
