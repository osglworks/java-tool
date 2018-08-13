package org.osgl.util;

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

public class STest extends UtilTestBase {
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

    @Test
    public void testDos2Unix() {
        String origin = "abc\n\rxyz\n\r";
        eq("abc\nxyz\n", S.dos2unix(origin));
        origin = "abc\nxyz\n";
        eq(origin, S.dos2unix(origin));
    }

    @Test
    public void testUnix2dos() {
        String origin = "abc\nxyz\n";
        eq("abc\n\rxyz\n\r", S.unix2dos(origin));
        origin = "abc\n\rxyz\n\r";
        eq(origin, S.unix2dos(origin));
    }

    @Test
    public void testBinarySplitNormalCase() {
        S.Binary retval = S.binarySplit("abc.123", '.');
        eq("abc", retval._1);
        eq("123", retval._2);
    }

    @Test
    public void testBinarySplitSeparatorNotFound() {
        S.T2 retval = S.binarySplit("abc", '.');
        eq("abc", retval.left());
        eq("", retval.right());
    }

    @Test
    public void testBinarySplitMultipleSeparators() {
        S.Binary retval = S.binarySplit("abc..123", '.');
        eq("abc", retval.left());
        eq(".123", retval.right());

        retval = S.binarySplit("abc.1.23", '.');
        eq("abc", retval.first());
        eq("1.23", retval.last());
    }

    @Test
    public void testBinarySplitSepartorAtBeginning() {
        S.T2 retval = S.binarySplit(".123", '.');
        eq("", retval._1);
        eq("123", retval._2);
    }

    @Test
    public void testBinarySplitSeparatorAtEnding() {
        S.T2 retval = S.binarySplit("abc.", '.');
        eq("abc", retval._1);
        eq("", retval.second());
    }


    @Test
    public void testTripleSplitNormalCase() {
        S.T3 retval = S.tripleSplit("abc.123.xyz", '.');
        eq("abc", retval._1);
        eq("123", retval._2);
        eq("xyz", retval.third());
    }

    @Test
    public void testTripleSplitNoEnoughSeparators() {
        S.T3 retval = S.tripleSplit("abc", '.');
        eq("abc", retval.first());
        eq("", retval.second());
        eq("", retval.last());

        retval = S.tripleSplit("abc.xyz", '.');
        eq("abc", retval.first());
        eq("xyz", retval.second());
        eq("", retval.last());
    }

    @Test
    public void testTripleSplitMultipleSeparators() {
        S.T3 retval = S.tripleSplit("abc..123", '.');
        eq("abc", retval.first());
        eq("", retval.second());
        eq("123", retval.last());

        retval = S.tripleSplit("abc.1.1.23", '.');
        eq("abc", retval._1);
        eq("1", retval._2);
        eq("1.23", retval._3);
    }

    @Test
    public void testFastSplitNormalCase() {
        S.List result = S.fastSplit("abc.123.xyz", ".");
        eq(result, C.list("abc", "123", "xyz"));

        result = S.fastSplit("abc..123..xyz", "..");
        eq(result, C.list("abc", "123", "xyz"));
    }

    @Test
    public void testFastSplitTrimSeparator() {
        S.List result = S.fastSplit(".abc..123...xyz.", ".");
        eq(result, C.list("abc", "123", "xyz"));
    }

    @Test
    public void testFastSplitNoSeparator() {
        eq(C.list("abc"), S.fastSplit("abc", "."));
    }


    @Test
    public void testCharSplitNormalCase() {
        S.List result = S.split("abc.123.xyz", '.');
        eq(result, C.list("abc", "123", "xyz"));

        result = S.split("abc..123..xyz", '.');
        eq(result, C.list("abc", "123", "xyz"));
    }

    @Test
    public void testCharSplitTrimSeparator() {
        S.List result = S.split(".abc..123...xyz.", '.');
        eq(result, C.list("abc", "123", "xyz"));
    }

    @Test
    public void testCharSplitNoSeparator() {
        eq(C.list("abc"), S.split("abc", '.'));
    }

    @Test
    public void testEmptyBlank() {
        yes(S.isEmpty(""));
        yes(S.isEmpty(null));
        no(S.isEmpty("\t"));
        yes(S.isBlank("\t"));
    }

    @Test
    public void testFluentIs() {
        yes(S.is(null).equalsTo(null));
        yes(S.is(null).equalsTo(""));
        no(S.is("foo").empty());
        yes(S.is(" ").blank());
        yes(S.is("abc").contains("b"));
        yes(S.is("abc").startsWith("ab"));
        no(S.is("abc").startsWith("b"));
        yes(S.is("[abc]").wrappedWith("[", "]"));
        yes(S.is("<abc>").wrappedWith(S.ANGLE_BRACKETS));
    }

    @Test
    public void testEnsure() {
        eq("[abc]", S.ensure("abc").wrappedWith(S.SQUARE_BRACKETS));
        eq("[abc]", S.ensure("[abc").wrappedWith(S.SQUARE_BRACKETS));
        eq("[abc]", S.ensure("[abc]").wrappedWith(S.SQUARE_BRACKETS));
        eq("abc", S.ensure("abc").strippedOff(S.ANGLE_BRACKETS));
        eq("abc", S.ensure("<abc>").strippedOff(S.ANGLE_BRACKETS));
        eq("_abc", S.ensure("abc").startWith('_'));
        eq("abc.html", S.ensure("abc").endWith(".html"));
    }

    @Test
    public void testIsNumber() {
        yes(S.isIntOrLong("134556324325252"));
        no(S.isIntOrLong("123.333"));
        yes(S.isNumeric("343.000"));
    }

    @Test
    public void testFormat() {
        eq("Hello world", S.fmt("Hello %s", "world"));
        eq("Hello world", S.msgFmt("Hello {0}", "world"));
    }

    @Test
    public void testJoin() {
        S.List list = S.list("abc", "xyz");
        eq("abc-xyz", S.join(list).by("-").get());
        eq("[abc]-[xyz]", S.join(list).by("-").wrapElementWith(S.SQUARE_BRACKETS).get());
        list = S.list("abc", null, "xyz");
        eq("abc--xyz", S.join(list).by("-").get());
        eq("abc-xyz", S.join(list).by("-").ignoreEmptyElement().get());
    }

    @Test
    public void testSplit() {
        S.List list = S.list("abc", "xyz");
        eq(list, S.split("abc-xyz").by("-").get());
        eq(list, S.split("[abc]-[xyz]").by("-").stripElementWrapper(S.BRACKETS).get());
        eq(C.list("abc", "xyz", "ijk"), S.split("abc1xyz23ijk", "[0-9]+"));
        eq(C.list("tmp", "foo", "bar"), S.fastSplit("/tmp/foo/bar", "/"));
        eq(C.list("tmp", "foo", "bar"), S.split("/tmp/foo/bar").by("/").get());
    }

    @Test
    public void testFluentReplace() {
        eq("hello foo", S.replace("world").in("hello world").with("foo"));
        eq("times [N]", S.replace("[0-9]+").with("[N]").usingRegEx().in("times 10"));
        eq("hello foo", S.given("hello world").replace("world").with("foo"));
    }

    @Test
    public void testRepeat() {
        eq("aaa", S.repeat('a').times(3));
        eq("aaa", S.repeat('a').x(3));
        eq("aaaaa", S.repeat('a').forFiveTimes());
        eq("foofoo", S.repeat("foo").times(2));
        eq("foofoo", S.repeat("foo").x(2));
    }

    @Test
    public void testWrap() {
        eq("*abc*", S.wrap("abc").with("*"));
        eq("[abc]", S.wrap("abc").with("[", "]"));
        eq("[abc]", S.wrap("abc").with(S.BRACKETS));
        eq("(abc)", S.wrap("abc").with(S.PARENTHESES));
        eq("<abc>", S.wrap("abc").with(S.DIAMOND));
        eq("<abc>", S.wrap("abc").with(S.ANGLE_BRACKETS));
        eq("《abc》", S.wrap("abc").with(S.书名号));
    }

    @Test
    public void testTrip() {
        eq("abc", S.strip("[abc]").of(S.BRACKETS));
        eq("abc", S.strip("<abc>").of(S.DIAMOND));
        eq("abc", S.strip("*abc*").of("*"));
        eq("abc", S.strip("111abc222").of("111", "222"));
    }

    @Test
    public void testCut() {
        eq("abc12", S.cut("abc123").by(5));
        eq("ab", S.cut("abc123").first(2));
        eq("23", S.cut("abc123").last(2));
        eq("123", S.cut("abc123").after("abc"));
        eq("abc", S.cut("abc123").before("123"));
        eq("abc", S.cut("abc123abc123").before("123"));
        eq("abc", S.cut("abc123abc123").beforeFirst("123"));
        eq("abc123abc", S.cut("abc123abc123").beforeLast("123"));
        eq("123", S.cut("abc123abc123").after("abc"));
        eq("123", S.cut("abc123abc123").afterLast("abc"));
        eq("123abc123", S.cut("abc123abc123").afterFirst("abc"));
    }

    @Test
    public void testOthers() {
        yes(S.eq("foo", "foo"));
        yes(S.eq("foo", "Foo", S.IGNORECASE));
        no(S.eq("foobar", " FooBar "));
        yes(S.eq("foobar", " FooBar ", S.IGNORESPACE | S.IGNORECASE));
        yes(S.eq(null, null));
        no(S.eq(null, "foo"));

        eq("", S.trim(null));
        eq("abc", S.trim(" abc"));
        eq("abc\nxyz", S.dos2unix("abc\n\rxyz"));
        eq("abc\n\rxyz", S.unix2dos("abc\nxyz"));
        eq("this...", S.maxLength("this is a long text", 4));
        System.out.println(S.uuid());
        System.out.println(S.random());
        System.out.println(S.random(2));
    }

    @Test
    public void testToken() {
        final String s = "Hello World";
        eq("HelloWorld", S.camelCase(s));
        eq("hello_world", S.underscore(s));
        eq("hello-world", S.dashed(s));
        eq("Hello World", S.capFirst(s));
        eq("hello World", S.lowerFirst(s));
        eq("Hello-World", Keyword.of(s).httpHeader());
        eq("helloWorld", Keyword.of(s).javaVariable());
        eq("HELLO_WORLD", Keyword.of(s).constantName());
        eq("Hello world", Keyword.of(s).readable());
    }

    @Test
    public void testCount() {
        final String s = "1011101111";
        eq(3, S.count("11").in(s));
        eq(5, S.count("11").withOverlap().in(s));
    }

    @Test
    public void testReversed() {
        final String s1 = "abc";
        eq("cba", S.reversed(s1));
        final String s2 = "你好";
        eq("好你", S.reversed(s2));
    }

    @Test
    public void testCenter() {
        String s = "ab";
        eq(" ab ", S.center(s, 4));
        eq("*ab*", S.center(s, 4, '*'));
        eq("ab", S.center(s, -1));
        eq("ab ", S.center(s, 3));
    }

    @Test
    public void testF_dropHead() {
        String s = "abc123";
        eq("123", S.F.dropHead(3).transform(s));
        eq("", S.F.dropHead(6).transform(s));
        eq("", S.F.dropHead(7).transform(s));
        eq("123", S.F.dropHeadIfStartsWith("abc").transform(s));

        eq("abc", S.F.dropTail(3).transform(s));
        eq("", S.F.dropTail(6).transform(s));
        eq("", S.F.dropTail(7).transform(s));
        eq("abc", S.F.dropTailIfEndsWith("123").transform(s));
    }

}
