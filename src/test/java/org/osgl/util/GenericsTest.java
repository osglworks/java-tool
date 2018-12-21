package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

import java.lang.reflect.Method;

public class GenericsTest extends TestBase {

    public abstract static class Foo<T> {
        public abstract T get();
    }

    public static class StringFoo extends Foo<String> {
        public String get() {
            return "foo";
        }

        public int bar() {return 1;}
    }

    @Test
    public void testGetReturnType() throws Exception {
        Method method = Foo.class.getMethod("get");
        same(String.class, Generics.getReturnType(method, StringFoo.class));
    }

    @Test
    public void getReturnTypeShallReturnNormalMethodReturnTypeIfNotGeneric() throws Exception {
        Method method = StringFoo.class.getMethod("bar");
        same(int.class, Generics.getReturnType(method, StringFoo.class));
    }
}
