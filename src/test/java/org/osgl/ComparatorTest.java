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
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

import java.util.Comparator;

public class ComparatorTest extends OsglToolTestBase {

    protected static class Foo extends $.T2<Integer, String> {
        public Foo(int _1, String _2) {
            super(_1, _2);
            E.NPE(_2);
        }
    }

    protected Foo abc = new Foo(1, "abc");
    protected Foo xyz = new Foo(1, "xyz");

    protected $.Comparator<Integer> c = new $.Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    };

    protected $.Comparator<Foo> cmp = new $.Comparator<Foo>() {
        @Override
        public int compare(Foo o1, Foo o2) {
            return o1._1 - o2._1;
        }
    };

    protected $.F1<Foo, String> keyExtractor = new $.F1<Foo, String>() {
        @Override
        public String apply(Foo foo) throws NotAppliedException, $.Break {
            return foo._2;
        }
    };

    @Test
    public void reversedShallCompareInReverseWay() {
        int a = 0, b = 1;
        eq(c.apply(a, b), c.reversed().apply(b, a));
    }

    @Test
    public void testThenComparing() {
        yes(cmp.thenComparing(new $.Comparator<Foo>() {
            @Override
            public int compare(Foo o1, Foo o2) {
                return o1._2.compareTo(o2._2);
            }
        }).apply(abc, xyz) < 0);
    }

    @Test
    public void testThenComparing2() {
        Comparator<String> kcmp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        yes(cmp.thenComparing(keyExtractor, kcmp).apply(abc, xyz) < 0);
    }

    @Test
    public void testThenComparing3() {
        yes(cmp.thenComparing(keyExtractor).apply(abc, xyz) < 0);
    }

    int one = 1, two = 2;
    String a = "a", b = "b";

    @Test
    public void naturalOrderShallCompareObjectInNaturalWay() {
        yes($.F.NATURAL_ORDER.compare(one, two) < 0);
        yes($.F.NATURAL_ORDER.compare(a, b) < 0);
    }

    @Test
    public void reverseOrderShallCompareObjectInNaturalWay() {
        no($.F.REVERSE_ORDER.compare(one, two) < 0);
        no($.F.REVERSE_ORDER.compare(a, b) < 0);
    }

    @Test
    public void reversedReverseOrderShallBeNaturalOrder() {
        yes($.F.REVERSE_ORDER.reversed() == $.F.NATURAL_ORDER);
    }

    @Test
    public void reversedNaturalOrderShallBeReverseOrder() {
        yes($.F.NATURAL_ORDER.reversed() == $.F.REVERSE_ORDER);
    }
}
