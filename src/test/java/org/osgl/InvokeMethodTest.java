package org.osgl;

import org.junit.Test;
import org.osgl.exception.UnexpectedMethodInvocationException;
import org.osgl.exception.UnexpectedNoSuchMethodException;
import org.osgl.util.E;

/**
 * Test `invokeXxx` method on {@link Osgl} class
 */
public class InvokeMethodTest extends TestBase {

    private static class Foo {

        public static String bar(String s, int i) {
            return s + i;
        }

        public boolean b(String s) {
            return Boolean.parseBoolean(s);
        }

        public void v(int i, int j) {
            E.illegalArgumentIf(i < j);
        }
    }

    @Test
    public void testInvokeStaticMethod() {
        eq("foo2", $.invokeStaticMethod(Foo.class, "bar", "foo", 2));
    }

    @Test
    public void testInvokeInstanceMethod() {
        eq(true, $.invokeInstanceMethod(new Foo(), "b", "true"));
        eq(false, $.invokeInstanceMethod(new Foo(), "b", "false"));
    }

    @Test(expected = UnexpectedNoSuchMethodException.class)
    public void testInvokeNoExistingMethod() {
        $.invokeInstanceMethod(new Foo(), "xx");
    }

    @Test
    public void testInvokeMethodThrowsOutException() {
        try {
            $.invokeInstanceMethod(new Foo(), "v", 1, 2);
            fail("Expected UnexpectedMethodInvocationException here");
        } catch (UnexpectedMethodInvocationException e) {
            yes(e.getCause() instanceof IllegalArgumentException);
        }
    }

}
