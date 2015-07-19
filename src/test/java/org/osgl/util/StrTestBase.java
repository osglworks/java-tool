package org.osgl.util;

import org.junit.Test;

public abstract class StrTestBase<T extends StrBase<T>> extends StrTestUtil<T> {

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

}
