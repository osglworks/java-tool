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
        } catch (UnexpectedNewInstanceException e) {
            yes(e.getCause() instanceof IllegalArgumentException);
        }
    }
}
