package org.osgl.util;

import org.apache.commons.codec.Charsets;
import org.junit.Test;
import org.osgl.$;
import org.osgl.Osgl;

import java.util.Collection;
import java.util.TreeSet;

public abstract class StrTestBase<T extends StrBase<T>> extends StrTestUtil<T> {

    private static $.Predicate<Character> charIsIn(final char ... ca) {
        return new Osgl.Predicate<Character>() {
            @Override
            public boolean test(Character character) {
                for (char c: ca) {
                    if (character == c) return true;
                }
                return false;
            }
        };
    }

    private static $.Predicate<Character> charIsNotIn(final char ... ca) {
        return new Osgl.Predicate<Character>() {
            @Override
            public boolean test(Character character) {
                for (char c: ca) {
                    if (character == c) return false;
                }
                return true;
            }
        };
    }

    @Test
    public void emptyToString() {
        ceq("", empty.toString());
    }

    @Test
    public void testEmptyAndBlank() {
        T s = copyOf("");
        yes(s.isEmpty());
        s = copyOf("  ");
        no(s.isEmpty());
        yes(s.isBlank());
        no(abc.isBlank());
    }

    @Test
    public void subList() {
        ceq("ab", abc.subList(0, 2));
        ceq("ab", abc2.subList(0, 2));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subListWithWrongStartIndex() {
        abc.subList(-1, 2);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subListWithWrongStartIndex2() {
        abc2.subList(-1, 2);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subListWithWrongEndIndex() {
        abc.subList(0, 4);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subListWithWrongEndIndex2() {
        abc2.subList(0, 4);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subListWithReversedFromAndToIndex() {
        abc.subList(2, 1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void subListWithReversedFromAndToIndex2() {
        abc2.subList(2, 1);
    }

    @Test
    public void subListWithEqFromAndToIndex() {
        ceq("", abc.subList(2, 2));
        ceq("", abc2.subList(2, 2));
    }

    @Test
    public void subListOfSubList() {
        ceq("b", abc.subList(1, 3).subList(0, 1));
        ceq("b", abc2.subList(1, 3).subList(0, 1));
    }

    @Test
    public void testAfterFirst() {
        ceq("bc", abc.afterFirst('a'));
        ceq("bc", abc.afterFirst("a"));
        ceq("bc", abc2.afterFirst('a'));
        ceq("bc", abc2.afterFirst("a"));
        ceq("bc", abc2.afterFirst(abc2.substr(0, 1)));
    }

    @Test
    public void testAfterLast() {
        ceq("bc", abc.afterLast('a'));
        ceq("bc", abc.afterLast("a"));
        ceq("bc", abc.afterLast(abc2.substr(0, 1)));
        ceq("bc", abc2.afterLast('a'));
        ceq("bc", abc2.afterLast("a"));
        ceq("bc", abc2.afterLast(abc2.substr(0, 1)));

        ceq("", abc.afterLast('c'));
        ceq("", abc.afterLast("c"));
        ceq("", abc.afterLast(abc2.substr(2, 3)));
        ceq("", abc2.afterLast('c'));
        ceq("", abc2.afterLast("c"));
        ceq("", abc2.afterLast(abc2.substr(2, 3)));
    }

    @Test
    public void testBeforeLast() {
        ceq("", abc.beforeLast('a'));
        ceq("", abc.beforeLast("a"));
        ceq("", abc.beforeLast(abc2.substr(0, 1)));
        ceq("", abc2.beforeLast('a'));
        ceq("", abc2.beforeLast("a"));
        ceq("", abc2.beforeLast(abc2.substr(0, 1)));

        ceq("ab", abc.beforeLast('c'));
        ceq("ab", abc.beforeLast("c"));
        ceq("ab", abc.beforeLast(abc2.substr(2, 3)));
        ceq("ab", abc2.beforeLast('c'));
        ceq("ab", abc2.beforeLast("c"));
        ceq("ab", abc2.beforeLast(abc2.substr(2, 3)));
    }

    @Test
    public void testBeforeFirst() {
        ceq("", abc.beforeFirst('d'));
    }

    @Test
    public void testBeforeLastBug1() {
        String s = "{abc{123}ii}";
        ceq("abc{123}ii", FastStr.of(s).afterFirst("{").beforeLast("}"));
    }

    @Test
    public void testInsert() {
        T t = abc.insert(0, 'x');
        ceq(t, "xabc");
        ceq(abc, "abc");
        t = abc.insert(-3, 'x');
        ceq(t, "xabc");
        t = abc.insert(3, 'y');
        ceq(t, "abcy");
        t = abc.insert(-1, 'z');
        ceq(t, "abzc");
        try {
            abc.insert(5, 'd');
            fail("should throw out StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {
            // pass
        }
        try {
            abc.insert(-4, 'd');
            fail("should throw out StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testInsertStr() {
        T t = abc.insert(0, S.str("xy"));
        ceq("xyabc", t);
        ceq("abc", abc);
        t = abc.insert(-3, S.str("xy"));
        ceq("xyabc", t);
        t = abc.insert(3, S.str("yz"));
        ceq("abcyz", t);
        t = abc.insert(-1, S.str("zi"));
        ceq("abzic", t);
        try {
            abc.insert(5, S.str("d"));
            fail("should throw out StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {
            // pass
        }
        try {
            abc.insert(-4, S.str("d"));
            fail("should throw out StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testRemove() {
        T t = zabcd.remove(charIsIn('b', 'c'));
        ceq("zad", t);

        t = zabcd.remove(charIsIn('z', 'd'));
        ceq("abc", t);
    }

    @Test
    public void testSubList() {
        T t = zabcd.subList(0, 2);
        ceq("za", t);
        t = zabcd.subList(3, 5);
        ceq("cd", t);
    }

    @Test
    public void testTakeWhile() {
        T t = zabcd.takeWhile(charIsIn('z', 'a', 'c'));
        ceq("za", t);
    }

    @Test
    public void testDropWhile() {
        T t = zabcd.dropWhile(charIsIn('z', 'a', 'c'));
        ceq("bcd", t);
    }

    @Test
    public void testAppendCollection() {
        Collection<Character> col = new TreeSet<Character>();
        col.add('1');
        col.add('2');
        T t = abc.append(col);
        ceq("abc12", t);
    }

    @Test
    public void testAppendList() {
        C.List<Character> list = C.list('1', '2');
        T t = abc.append(list);
        ceq("abc12", t);
    }

    @Test
    public void testAppendChar() {
        T t = abc.append('1');
        ceq("abc1", t);
    }

    @Test
    public void testAppendT() {
        T t = abc.append(zabcd);
        ceq("abczabcd", t);
    }

    @Test
    public void testAppendString() {
        T t = abc.append("1234");
        ceq("abc1234", t);
    }

    @Test
    public void testPrependCollection() {
        Collection<Character> col = new TreeSet<Character>();
        col.add('1');
        col.add('2');
        T t = abc.prepend(col);
        ceq("12abc", t);
    }

    @Test
    public void testPrependList() {
        C.List<Character> list = C.list('1', '2');
        T t = abc.prepend(list);
        ceq("12abc", t);
    }

    @Test
    public void testPrependChar() {
        T t = abc.prepend('1');
        ceq("1abc", t);
    }

    @Test
    public void testPrependT() {
        T t = abc.prepend(zabcd);
        ceq("zabcdabc", t);
    }

    @Test
    public void testPrependString() {
        T t = abc.prepend("1234");
        ceq("1234abc", t);
    }

    @Test
    public void testSubSequence() {
        T t = zabcd.subSequence(0, 2);
        ceq("za", t);
        t = zabcd.subSequence(3, 5);
        ceq("cd", t);
    }

    @Test
    public void testTimes() {
        T t = abc.times(2);
        ceq("abcabc", t);
        t = abc.times(0);
        ceq("", t);
        try {
            abc.times(-1);
            fail("IllegalArgumentException shall be raised when n is negative");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void testGetChars() {
        char[] dst = {'0', '1', '2', '3'};
        abc2.getChars(0, 2, dst, 0);
        ceq("ab23", FastStr.of(dst));
        dst = new char[]{'0', '1', '2', '3'};
        abc2.getChars(1, 3, dst, 2);
        ceq("01bc", FastStr.of(dst));
    }

    @Test
    public void testGetByteAscII() {
        byte[] ba = abc2.getBytesAscII();
        eq(3, ba.length);
        eq(ba[0], "a".getBytes()[0]);
        eq(ba[1], "b".getBytes()[0]);
    }

    @Test
    public void testGetByteUTF8() {
        byte[] ba = abc2.getBytesUTF8();
        eq(3, ba.length);
        eq(ba[0], "a".getBytes()[0]);
        eq(ba[1], "b".getBytes()[0]);

        T t = copyOf("中国");
        ba = t.getBytesUTF8();
        eq(6, ba.length);
    }

    @Test
    public void testToFastStr() {
        FastStr fs = longStr.toFastStr();
        ceq(fs, longStr);
    }

    @Test
    public void testContentEquals() {
        yes(abc.contentEquals("abc"));
        yes(abc2.contentEquals("abc"));
        yes(abc2.contentEquals(abc));
    }

    @Test
    public void testIsBlank() {
        yes(FastStr.EMPTY_STR.isBlank());
        yes(Str.EMPTY_STR.isBlank());
        yes(copyOf("  ").isBlank());
        yes(copyOf("\t").isBlank());
    }

    @Test
    public void testIndexOf() {
        eq(0, abc.indexOf((int) 'a'));
        eq(1, abc.indexOf((int) 'b'));
        eq(2, abc.indexOf((int) 'c'));
        eq(-1, abc.indexOf((int) 'd'));
        eq(-1, abc.indexOf((int) 'a', 1));
        eq(1, abc.indexOf((int) 'b', 1));
        eq(3, 码农码代码戏码农.indexOf((int) '代'));
        eq(2, 农码代.indexOf((int) '代'));

        eq(0, abc2.indexOf((int) 'a'));
        eq(1, abc2.indexOf((int) 'b'));
        eq(2, abc2.indexOf((int) 'c'));
        eq(-1, abc2.indexOf((int) 'd'));
        eq(-1, abc2.indexOf((int) 'a', 1));
        eq(1, abc2.indexOf((int) 'b', 1));

        eq(1, abc.indexOf("b"));
        eq(1, abc2.indexOf("b"));

        eq(1, 码农码代码戏码农.indexOf("农码代"));
        eq(-1, 码农码代码戏码农.indexOf("码代码", 3));
        C.List<Character> list = C.list('码', '代', '码');
        eq(2, 码农码代码戏码农.indexOf(list));
        eq(2, 码农码代码戏码农.indexOf(list, 1));
        eq(-1, 码农码代码戏码农.indexOf(list, 3));

        Object o = 'a';
        eq(0, abc.indexOf(o));
        o = abc;
        eq(0, abc.indexOf(o));
        eq(1, zabcd.indexOf(o));
    }


    @Test
    public void testLastIndexOf() {
        eq(0, abc.lastIndexOf((int) 'a'));
        eq(1, abc.lastIndexOf((int) 'b'));
        eq(2, abc.lastIndexOf((int) 'c'));
        eq(-1, abc.lastIndexOf((int) 'd'));
        eq(-1, abc.lastIndexOf((int) 'c', 1));
        eq(1, abc.lastIndexOf((int) 'b', 1));
        eq(6, 码农码代码戏码农.lastIndexOf((int) '码'));

        eq(0, abc2.lastIndexOf((int) 'a'));
        eq(1, abc2.lastIndexOf((int) 'b'));
        eq(2, abc2.lastIndexOf((int) 'c'));
        eq(-1, abc2.lastIndexOf((int) 'd'));
        eq(-1, abc2.lastIndexOf((int) 'c', 1));
        eq(1, abc2.lastIndexOf((int) 'b', 1));
        eq(2, 农码代.lastIndexOf((int) '代'));

        eq(1, 码农码代码戏码农.lastIndexOf("农码代"));
        eq(-1, 码农码代码戏码农.lastIndexOf("码代码", 1));

        C.List<Character> list = C.list('码', '农');
        eq(6, 码农码代码戏码农.lastIndexOf(list));
        eq(0, 码农码代码戏码农.lastIndexOf(list, 1));

        Object o = 'a';
        eq(0, abc.lastIndexOf(o));
        o = abc;
        eq(0, abc.lastIndexOf(o));
        eq(1, zabcd.lastIndexOf(o));
    }

    @Test
    public void testGetBytes() {
        yes($.eq2(abc.getBytes(), abc2.getBytes()));
        yes($.eq2(abc2.getBytes(), "abc".getBytes()));
        yes($.eq2(abc.getBytesAscII(), "abc".getBytes()));
        yes($.eq2(码农码代码戏码农.getBytes(), "码农码代码戏码农".getBytes()));
        yes($.eq2(码农码代码戏码农.getBytesUTF8(), "码农码代码戏码农".getBytes(Charsets.UTF_8)));
        yes($.eq2(码农码代码戏码农.getBytesAscII(), "码农码代码戏码农".getBytes(Charsets.US_ASCII)));
        yes($.eq2(农码代.getBytesAscII(), "农码代".getBytes(Charsets.US_ASCII)));
        yes($.eq2(农码代.getBytesUTF8(), "农码代".getBytes(Charsets.UTF_8)));
        yes($.eq2(农码代.getBytesUTF8(), 农码代.getBytes(Charsets.UTF_8)));
        yes($.eq2(农码代.getBytesUTF8(), 农码代.getBytes("UTF-8")));
    }

    @Test
    public void testStartsWith() {
        yes(zabcd.startsWith("za"));
        no(zabcd.startsWith("ab"));
        yes(zabcd.startsWith("ab", 1));
        yes(zabcd.startsWith(abc, 1));
        yes(abc2.startsWith("ab"));
        yes(abc2.startsWith(abc));
        yes(abc.startsWith(abc2));
        yes(zabcd.startsWith(abc2, 1));
    }

    @Test
    public void testEndsWith() {
        yes(zabcd.endsWith("cd"));
        no(zabcd.endsWith("bc"));
        yes(zabcd.endsWith("bc", 1));
        yes(zabcd.endsWith(abc, 1));
        yes(abc2.endsWith("bc"));
        yes(abc2.endsWith(abc));
        yes(abc.endsWith(abc2));
        no(abc.endsWith("abcde"));
        yes(zabcd.endsWith(abc2, 1));
        yes(abc.endsWith("", -1));
    }

    @Test
    public void testSubstring() {
        eq("zab", zabcd.substring(0, 3));
        eq("bc", abc2.substring(1));
        eq("b", abc2.substring(1, 2));
        eq("码代码戏码农", 码农码代码戏码农.substring(2));
        eq("码代", 农码代.substring(1));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void substringException1() {
        abc2.substring(-1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void substringException2() {
        abc2.substring(4);
    }

    @Test
    public void testEqualsIgnoreCase() {
        StringBuilder sb = S.builder().append("abC");
        yes(abc.equalsIgnoreCase(sb));
        yes(abc2.equalsIgnoreCase(sb));
        no(zabcd.equalsIgnoreCase(sb));
    }


    @Test
    public void testCompareToCharSequence() {
        StringBuilder sb = S.builder().append("abC");
        yes(abc.compareTo(sb) > 0);
        sb.deleteCharAt(2);
        sb.append("c");
        yes(abc.compareTo(sb) == 0);
        sb.append("d");
        yes(abc.compareTo(sb) < 0);
        sb.deleteCharAt(3);
        sb.insert(0, "z");
        yes(abc.compareTo(sb) < 0);
    }

    @Test
    public void testCompareTo() {
        yes(abc.compareTo(abc2) == 0);
        yes(abc.compareTo(zabcd) < 0);
        yes(zabcd.compareTo(abc) > 0);
    }

    @Test
    public void testCompareToIgnoreCase() {
        StringBuilder sb = S.builder().append("abC");
        yes(abc.compareToIgnoreCase(sb) == 0);
        yes(abc2.compareToIgnoreCase(sb) == 0);
        yes(abc.compareToIgnoreCase(zabcd) < 0);
        yes(zabcd.compareToIgnoreCase(abc) > 0);
    }

    @Test
    public void testRegionMatch() {
        yes(abc.regionMatches(false, 0, zabcd, 1, 3));
        yes(abc2.regionMatches(false, 0, zabcd, 1, 3));
        yes(zabcd.regionMatches(false, 1, abc2, 0, 2));
        yes(zabcd.regionMatches(false, 1, "abc", 0, 2));
        no(zabcd.regionMatches(false, 1, abc2, 0, 4));
        no(zabcd.regionMatches(false, -1, abc2, 0, 2));
        yes(码农码代码戏码农.regionMatches(false, 1, 农码代, 0, 2));
        yes(码农码代码戏码农.regionMatches(false, 1, "农码代", 0, 2));
    }

    @Test
    public void testReplaceChar() {
        ceq("acc", abc.replace('b', 'c'));
        ceq("acc", abc2.replace('b', 'c'));
        ceq("c农c代c戏c农", 码农码代码戏码农.replace('码', 'c'));
        ceq("农c代", 农码代.replace('码', 'c'));
        yes(农码代 == 农码代.replace('x', 'y'));
    }

    @Test
    public void testReplaceCharSequence() {
        ceq("ba", aaa.replace("aa", "b"));
        ceq("coder码代码戏coder", 码农码代码戏码农.replace("码农", "coder"));
        ceq("农code代", 农码代.replace("码", "code"));
    }

    @Test
    public void testReplaceAll() {
        ceq("coder码代码戏coder", 码农码代码戏码农.replaceAll("(码农|农码)", "coder"));
        ceq("codeXcodeXcodeXcodeX", 码农码代码戏码农.replaceAll("码.", "codeX"));
        ceq("zxyzd", zabcd.replaceAll("(a.c)", "xyz"));
    }

    @Test
    public void testReplaceFirst() {
        ceq("coder码代码戏码农", 码农码代码戏码农.replaceFirst("(码农|农码)", "coder"));
        ceq("codeX码代码戏码农", 码农码代码戏码农.replaceFirst("码.", "codeX"));
        ceq("zxyzd", zabcd.replaceFirst("(a.c)", "xyz"));
    }

    public void testMatches() {
        yes(码农码代码戏码农.matches("(码农|农码)"));
    }
}
