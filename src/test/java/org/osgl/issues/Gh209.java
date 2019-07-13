package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2019 OSGL (Open Source General Library)
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
import org.osgl.util.N;
import org.osgl.util.S;

public class Gh209 extends TestBase {
    public static class Foo implements Cloneable {
        public String name;
        public int level;
        public boolean flag;
    }

    public static class Bar implements Cloneable {
        public String name;
        public Foo foo;
    }

    @Test
    public void test() {
        Bar source = new Bar();
        source.name = S.random();
        Foo foo = new Foo();
        foo.name = S.random();
        foo.level = N.randInt();
        foo.flag = true;
        source.foo = foo;

        Bar target = $.cloneOf(source);
    }
}
