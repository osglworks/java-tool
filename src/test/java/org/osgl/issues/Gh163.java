package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;

import java.nio.ByteBuffer;

public class Gh163 extends TestBase {

    @Test
    public void test() {
        String s = "Hello World";
        ByteBuffer buf = $.convert(s).toByteBuffer();
        eq(s, $.convert(buf).toString());
    }

    @Test
    public void test2() {
        byte[] ba = new byte[]{1, 2, 3, 0};
        ByteBuffer buf = $.convert(ba).toByteBuffer();
        eq(ba, $.convert(buf).toByteArray());
    }

}
