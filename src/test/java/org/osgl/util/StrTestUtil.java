package org.osgl.util;

import org.junit.Before;
import org.junit.Ignore;
import org.osgl.TestBase;

@Ignore
public abstract class StrTestUtil<T extends StrBase<T>> extends TestBase {
    protected T aaa;
    protected T abc;
    protected T abc2;
    protected T zabcd;
    protected T 码农码代码戏码农;
    protected T 农码代;
    protected T midLength;
    protected T longStr;
    protected T empty = empty();

    private T _aaa = copyOf("aaa");
    private T _abc = copyOf("abc");
    private T _zabcd = copyOf("zabcd");
    private T _abc2 = _zabcd.substr(1, 4);
    private T _mid = copyOf(S.random(22));
    private T _long = copyOf(S.random(5000));
    private T _码农码代码戏码农 = copyOf("码农码代码戏码农");
    private T _农码代 = _码农码代码戏码农.substr(1, 4);

    protected static void ceq(CharSequence c1, CharSequence c2) {
        eq(c1.toString(), c2.toString());
    }

    protected abstract T copyOf(String s);

    protected abstract T empty();

    @Before
    public void prepare() {
        aaa = _aaa;
        abc = _abc;
        midLength = _mid;
        longStr = _long;
        abc2 = _abc2;
        zabcd = _zabcd;
        码农码代码戏码农 = _码农码代码戏码农;
        农码代 = _农码代;
        eq(abc2.toString(), "abc");
    }

}
