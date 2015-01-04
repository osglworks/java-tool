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

    @Test
    public void toLowerCase() {
        ceq(Unsafe.toLowerCase(_short), _short.toLowerCase());
        ceq(Unsafe.toLowerCase(_mid), _mid.toLowerCase());
        ceq(Unsafe.toLowerCase(_long), _long.toLowerCase());
    }

    @Test
    public void toUpperCase() {
        ceq(Unsafe.toUpperCase(_short), _short.toUpperCase());
        ceq(Unsafe.toUpperCase(_mid), _mid.toUpperCase());
        ceq(Unsafe.toUpperCase(_long), _long.toUpperCase());
    }

    static void ceq(CharSequence c1, CharSequence c2) {
        eq(c1.toString(), c2.toString());
    }
}
