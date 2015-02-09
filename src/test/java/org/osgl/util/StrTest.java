package org.osgl.util;

public class StrTest extends StrTestBase<Str> {

    @Override
    protected Str copyOf(String s) {
        return Str.of(s);
    }

    @Override
    protected Str empty() {
        return Str.EMPTY_STR;
    }
}
