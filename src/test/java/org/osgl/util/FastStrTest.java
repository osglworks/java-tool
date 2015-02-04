package org.osgl.util;

import org.junit.Test;

public class FastStrTest extends StrTestBase<FastStr> {
    @Override
    protected FastStr copyOf(String s) {
        return FastStr.of(s);
    }

    @Override
    protected FastStr empty() {
        return FastStr.EMPTY_STR;
    }

    @Test
    public void testRevertBeginPointer() {
        final String s = "http://abc.com:8038/xyz/123";
        char[] buf = Unsafe.bufOf(s);
        FastStr fs = FastStr.unsafeOf(s);
        fs = fs.afterFirst("://").afterFirst('/');
        ceq(fs, "xyz/123");
        FastStr fs0 = fs.prepend('/');
        ceq(fs0, "/xyz/123");
        fs0 = fs.prepend("/");
        ceq(fs0, "/xyz/123");
    }

    @Test
    public void testTrimOnBeginAndEndPointerSet() {
        String s = "123 Hello world! 123";
        int sz = s.length();
        FastStr fs = FastStr.unsafeOf(s).substr(3, sz - 3);
        ceq(" Hello world! ", fs);
        ceq("Hello world!", fs.trim());
    }

    @Test
    public void testReplaceChar() {
        String s = "C:\\Users\\luog.IKARI\\Google Drive\\XYZ Research\\Client Docs\\7. CRM templates.pdf";
        FastStr fs = FastStr.unsafeOf(s);
        fs = fs.replace('\\', '/');
        eq(fs.toString(), "C:/Users/luog.IKARI/Google Drive/XYZ Research/Client Docs/7. CRM templates.pdf");
    }

    @Test
    public void testReplaceCharInSubStr() {
        String s = "C:\\Users\\luog.IKARI\\Google Drive\\XYZ Research\\Client Docs\\7. CRM templates.pdf";
        FastStr fs = FastStr.unsafeOf(s).afterFirst("C:\\Users\\luog.IKARI\\Google Drive").beforeLast(".");
        fs = fs.replace('\\', '/');
        eq(fs.toString(), "/XYZ Research/Client Docs/7. CRM templates");
    }
}
