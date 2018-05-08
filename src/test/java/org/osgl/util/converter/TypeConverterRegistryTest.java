package org.osgl.util.converter;

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
import org.osgl.$;
import org.osgl.Lang;
import org.osgl.TestBase;
import org.osgl.util.Iterators;
import org.osgl.util.S;

import java.util.Enumeration;
import java.util.Iterator;

public class TypeConverterRegistryTest extends TestBase {

    private static class Foo {
        public String id = S.random();

        @Override
        public String toString() {
            return S.wrap(id).with(S.BRACKETS);
        }
    }

    @Test
    public void testGlobalConverterRegistry() {
        Iterator<String> itr = Iterators.singleton("ABC");
        Enumeration<String> e = $.convert(itr).to(Enumeration.class);
        yes(e.hasMoreElements());
        eq("ABC", e.nextElement());
    }

    @Test
    public void testNewTypeConverterRegistry() {
        TypeConverterRegistry registry = new TypeConverterRegistry();
        Lang.TypeConverter<Foo, String> converter = registry.get(Foo.class, String.class);
        notNull(converter);
        Foo foo = new Foo();
        eq(S.wrap(foo.id).with(S.BRACKETS), converter.convert(foo));
    }

}
