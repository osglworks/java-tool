package org.osgl.issues;

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
import org.osgl.TestBase;

public class Gh176 extends TestBase {

    public static class Foo {
        String name;
        Integer id;
        public Foo() {}
        public Foo(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

    public static class Bar {
        String name;
        Long id;
        public Bar() {
        }
        public Bar(String name, long id) {
            this.name = name;
            this.id = id;
        }
    }

    @Test
    public void test() {
        Bar bar = $.mergeMap(new Foo("x", 111)).to(Bar.class);
        eq("x", bar.name);
        eq(111L, bar.id);
    }

}
