package org.osgl.util;

public class FastStrTest extends StrTestBase<FastStr> {
    @Override
    protected FastStr copyOf(String s) {
        return FastStr.of(s);
    }
}
