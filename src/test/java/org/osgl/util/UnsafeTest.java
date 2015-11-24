package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

public class UnsafeTest extends TestBase {
    private String _short;
    private String _mid;
    private String _long;

    public UnsafeTest() {
        _short = S.random(8);
        _mid = S.random(128);
        _long = S.random(4096);
    }

    static void ceq(CharSequence c1, CharSequence c2) {
        eq(c1.toString(), c2.toString());
    }
}
