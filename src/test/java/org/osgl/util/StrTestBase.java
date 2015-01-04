package org.osgl.util;

import org.junit.Test;

public abstract class StrTestBase<T extends StrBase<T>> extends StrTestUtil<T> {

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
    public void testBeforeLastBug1() {
        String s = "{abc{123}ii}";
        ceq("abc{123}ii", FastStr.of(s).afterFirst("{").beforeLast("}"));
    }

}
