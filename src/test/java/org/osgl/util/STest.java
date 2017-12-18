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

}
