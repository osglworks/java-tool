package org.osgl.util;

import org.junit.Test;

public class FastStrTest extends StrTestBase<FastStr> {
    @Override
    protected FastStr copyOf(String s) {
        return FastStr.of(s);
    }

    @Test
    public void testRevertBeginPointer() {
        final String s = "http://abc.com:8038/xyz/123";
        char[] buf = Unsafe.bufOf(s);
        FastStr fs = FastStr.unsafeOf(s);
        assertSame(buf, Unsafe.bufOf(fs));
        fs = fs.afterFirst("://").afterFirst('/');
        ceq(fs, "xyz/123");
        assertSame(buf, Unsafe.bufOf(fs));
        FastStr fs0 = fs.prepend('/');
        ceq(fs0, "/xyz/123");
        assertSame(buf, Unsafe.bufOf(fs0));
        fs0 = fs.prepend("/");
        assertSame(buf, Unsafe.bufOf(fs0));
        ceq(fs0, "/xyz/123");
    }
}
