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
import org.osgl.util.C;
import org.osgl.util.N;
import org.osgl.util.S;

public class Gh97 extends TestBase {

    public static class Bar {
        public String s1 = S.random();
        public String s2 = S.random();
        public String s3 = S.random();
    }

    public static class Car {
        public String x1 = S.random();
        public String x2 = S.random();
        public String x3 = S.random();
    }

    public static class Foo {
        public int id = N.randInt();
        public String name = S.random();
        public Bar bar = new Bar();
    }

    public static class Phoo {
        public int num = N.randInt();
        public String desc = S.random();
        public Car car = new Car();
    }

    @Test
    public void testSimpleCase() {
        Foo foo = new Foo();
        Phoo phoo = new Phoo();
        $.copy(foo)
                .mapHead("id").to("num")
                .mapHead("name").to("desc")
                .to(phoo);
        eq(foo.id, phoo.num);
        eq(foo.name, phoo.desc);
    }

    @Test
    public void testSimpleCaseUsingDifferentAPI() {
        Foo foo = new Foo();
        Phoo phoo = new Phoo();
        $.copy(foo)
                .withSpecialNameMappings(C.<String, String>Map("num", "id", "desc", "name"))
                .to(phoo);
        eq(foo.id, phoo.num);
        eq(foo.name, phoo.desc);
    }

    @Test
    public void testNested() {
        Foo foo = new Foo();
        Phoo phoo = new Phoo();
        $.deepCopy(foo)
                .mapHead("bar.s1").to("car.x1")
                .to(phoo);
        ne(foo.id, phoo.num);
        ne(foo.name, phoo.desc);
        eq(foo.bar.s1, phoo.car.x1);
        ne(foo.bar.s2, phoo.car.x2);
        ne(foo.bar.s3, phoo.car.x3);
    }

    @Test
    public void testCrossNestBoundaryA() {
        Foo foo = new Foo();
        Phoo phoo = new Phoo();
        $.deepCopy(foo)
                .mapHead("name").to("car.x1")
                .to(phoo);
        eq(foo.name, phoo.car.x1);
        ne(foo.id, phoo.num);
        ne(foo.name, phoo.desc);
    }

    @Test
    public void testCrossNestBoundaryB() {
        Foo foo = new Foo();
        Phoo phoo = new Phoo();
        $.deepCopy(foo)
                .mapHead("bar.s2").to("desc")
                .to(phoo);
        eq(foo.bar.s2, phoo.desc);
        ne(foo.id, phoo.num);
        ne(foo.name, phoo.desc);
    }

}
