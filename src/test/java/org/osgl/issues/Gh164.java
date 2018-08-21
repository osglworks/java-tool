package org.osgl.issues;

import org.junit.Before;
import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.StringWriter;
import java.nio.ByteBuffer;

public class Gh164 extends TestBase {

    private ByteBuffer buf;
    private String str;

    @Before
    public void prepare() {
        str = S.longUrlSafeRandom();
        buf = $.convert(str).toByteBuffer();
    }

    @Test
    public void test() {
        String s = IO.read(buf).toString();
        eq(str, s);
    }

    @Test
    public void test2() {
        StringWriter sw = new StringWriter();
        IO.write(buf).to(sw);
        eq(str, sw.toString());
    }

}
