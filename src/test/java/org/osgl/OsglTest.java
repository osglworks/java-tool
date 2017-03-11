package org.osgl;

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
        eq(4, fields.size());
        eq(4, new HashSet<Field>(fields).size());
        fields = $.fieldsOf(Bar.class, true);
        eq(3, fields.size());
        eq(3, new HashSet<Field>(fields).size());
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
