package org.osgl.util;

import org.junit.Before;
import org.junit.Ignore;
import org.osgl.TestBase;

@Ignore
public abstract class StrTestUtil<T extends StrBase<T>> extends TestBase {
    protected T abc;
    protected T abc2;
    protected T midLength;
    protected T longStr;

    private T _abc = copyOf("abc");
    private T _abc2 = copyOf("zabcd").substr(1, 4);
    private T _mid = copyOf(S.random(22));
    private T _long = copyOf(S.random(5000));

    protected static void ceq(CharSequence c1, CharSequence c2) {
        eq(c1.toString(), c2.toString());
    }

    protected abstract T copyOf(String s);

    @Before
    public void prepare() {
        abc = _abc;
        midLength = _mid;
        longStr = _long;
        abc2 = _abc2;
        eq(abc2.toString(), "abc");
    }

}
