package org.osgl;

import org.junit.Test;
import org.osgl.exception.UnexpectedNewInstanceException;
import org.osgl.util.E;

/**
 * Test `newInstance(...)` method of {@link Osgl} class
 */
public class NewInstanceTest extends TestBase {

    private static class Foo {
        public Foo() {}
        public Foo(String s, int i) {}
        public Foo(String s, int i, char c, boolean b, float f) {}
        public Foo(String s, int i, char c, boolean b, float f, long l) {}
        public Foo(int i) throws IllegalArgumentException {
            E.illegalArgumentIf(i < 0);
        }
    }

    private static final Class<Foo> FOO_CLASS = Foo.class;

    @Test
    public void testNewIntanceWithClassName() {
        $.newInstance(FOO_CLASS.getName());
    }

    @Test
    public void testNewInstanceWithClass() {
        $.newInstance(FOO_CLASS);
    }

    @Test
    public void testNewInstanceWithCorrectConstructorArguments() {
        $.newInstance(FOO_CLASS, "foo", 5);
        $.newInstance(FOO_CLASS, "foo", 5, 'c', false, 2.3f);
        $.newInstance(FOO_CLASS, "foo", 5, 'c', false, 2.3f, Long.MAX_VALUE);
    }

    @Test(expected = UnexpectedNewInstanceException.class)
    public void testNewInstanceWithInvalidConstructorArguments() {
        $.newInstance(FOO_CLASS, false, "abc");
    }

    @Test
    public void newInstanceConstructorThrowsExceptionCase() {
        try {
            $.newInstance(FOO_CLASS, -3);
            fail("It shall throw out UnexpectedNewInstanceException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }
}
