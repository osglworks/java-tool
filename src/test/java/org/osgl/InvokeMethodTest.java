package org.osgl;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
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
        } catch (IllegalArgumentException e) {
            // success
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
