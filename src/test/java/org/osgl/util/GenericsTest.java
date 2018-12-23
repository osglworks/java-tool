package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
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
