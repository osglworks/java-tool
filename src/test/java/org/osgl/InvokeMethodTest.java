package org.osgl;

import org.junit.Test;
import org.osgl.exception.UnexpectedMethodInvocationException;
import org.osgl.exception.UnexpectedNoSuchMethodException;
import org.osgl.util.E;

import java.lang.reflect.Method;

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

        public static void minusOne($.Var<Integer> num) {
            num.set(num.get() - 1);
        }
    }

    @Test
    public void testInvokeStaticMethod() {
        eq("foo2", $.invokeStatic(Foo.class, "bar", "foo", 2));
    }

    @Test
    public void testInvokeInstanceMethod() {
        eq(true, $.invokeVirtual(new Foo(), "b", "true"));
        eq(false, $.invokeVirtual(new Foo(), "b", "false"));
    }

    @Test(expected = UnexpectedNoSuchMethodException.class)
    public void testInvokeNoExistingMethod() {
        $.invokeVirtual(new Foo(), "xx");
    }

    @Test
    public void testInvokeMethodThrowsOutException() {
        try {
            $.invokeVirtual(new Foo(), "v", 1, 2);
            fail("Expected UnexpectedMethodInvocationException here");
        } catch (UnexpectedMethodInvocationException e) {
            yes(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testVoidReturnType() {
        $.Var<Integer> v = $.var(10);
        eq(null, $.invokeStatic(Foo.class, "minusOne", v));
        eq(9, v.get());
    }

    @Test
    public void testGetAndInvokeMethod() {
        Method method = $.getMethod(Foo.class, "bar", "foo", 1);
        eq("12", $.invokeStatic(method, "1", 2));
    }

    @Test
    public void testGetByTypeAndInvokeMethod() {
        Method method = $.getMethod(Foo.class, "bar", String.class, int.class);
        eq("12", $.invokeStatic(method, "1", 2));
    }

    @Test
    public void testInvokeStaticAndCache() {
        $.Var<Method> bag = $.var();
        eq("foo1", $.invokeStatic(bag, Foo.class, "bar", "foo", 1));
        eq("12", $.invokeStatic(bag.get(), "1", 2));
    }

}
