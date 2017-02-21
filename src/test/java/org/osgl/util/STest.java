package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

/**
 * Created by luog on 7/01/14.
 */
public class STest extends TestBase {
    @Test
    public void testAppend() {
        eq(S.str("a").append("b"), S.str("ab"));
    }

    @Test
    public void testPrepend() {
        eq(S.str("a").prepend("b"), S.str("ba"));
    }

    @Test
    public void testF_startsWith() {
        String s = "foo.bar";
        yes(S.F.startsWith("foo").apply(s));
        yes(S.F.endsWith("bar").apply(s));
        no(S.F.endsWith("foo").apply(s));
        no(S.F.startsWith("bar").apply(s));
    }

    @Test
    public void testTimes() {
        String s = "abc";
        eq(S.times(s, 0), "");
        eq(S.times(s, 1), s);
        eq(S.times(s, 2), s + s);
        eq(S.join(",", s, 2), s + "," + s);

        Str s0 = Str.of(s);
        eq(s0.times(2).toString(), s + s);
    }

    @Test
    public void testEnsureEndsWith() {
        eq("abc/", S.ensureEndsWith("abc", "/"));
        eq("abc/", S.ensureEndsWith("abc/", "/"));

        eq("abc/", S.ensureEndsWith("abc", '/'));
        eq("abc/", S.ensureEndsWith("abc/", '/'));
    }

    @Test
    public void testEnsureStartsWith() {
        eq("/abc", S.ensureStartsWith("abc", "/"));
        eq("/abc", S.ensureStartsWith("/abc", "/"));

        eq("/abc", S.ensureStartsWith("abc", '/'));
        eq("/abc", S.ensureStartsWith("/abc", '/'));
    }

    @Test
    public void testPathConcat() {
        eq("foo/bar", S.pathConcat("foo", '/', "bar"));
        eq("foo/bar", S.pathConcat("foo/", '/', "bar"));
        eq("foo/bar", S.pathConcat("foo", '/', "/bar"));
        eq("foo/bar", S.pathConcat("foo/", '/', "/bar"));
    }

    @Test
    public void testConcat() {
        eq("ab", S.concat("a", "b"));
        eq("abc", S.concat("a", "b", "c"));
        eq("abcd", S.concat("a", "b", "c", "d"));
        eq("abcde", S.concat("a", "b", "c", "d", "e"));
        eq("abcdef", S.concat("a", "b", "c", "d", "e", "f"));
        eq("abcdefg", S.concat("a", "b", "c", "d", "e", "f", "g"));
        eq("abcdefgh", S.concat("a", "b", "c", "d", "e", "f", "g", "h"));
    }
}
