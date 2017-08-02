package org.osgl.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgl.TestBase;

public class SBufferTest extends TestBase {

    @Before
    public void reset() {
        System.out.println("reset...");

        S.buffer().reset();
    }

    @Test
    public void itShallNotReuseUnconsumedBuffer() {
        S.Buffer sb = S.buffer("abc");
        S.Buffer sb2 = S.buffer("123");
        assertNotSame(sb, sb2);
    }

    @Test
    @Ignore
    //TODO: fix me!!
    public void itShallReuseConsumedBuffer() {
        S.Buffer sb = S.buffer("abc");
        eq("abc", sb.toString());
        S.Buffer sb2 = S.buffer("123");
        assertSame(sb, sb2);
        eq("123", sb2.toString());
        assertSame(sb, sb2);
    }

    @Test
    public void testPrepend() {
        S.Buffer sb = S.newBuffer("abc");
        sb.prepend("1234");
        eq("1234abc", sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(true);
        eq("trueabc", sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(100);
        eq("100abc", sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(Long.MAX_VALUE);
        eq(S.builder(Long.MAX_VALUE).append("abc").toString(), sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(3.3f);
        eq("3.3abc", sb.toString());
    }

}
