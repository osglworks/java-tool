package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

import java.util.Comparator;

public class ComparatorTest extends TestBase {

    protected static class Foo extends _.T2<Integer, String> {
        public Foo(int _1, String _2) {
            super(_1, _2);
            E.NPE(_2);
        }
    }

    protected Foo abc = new Foo(1, "abc");
    protected Foo xyz = new Foo(1, "xyz");

    protected _.Comparator<Integer> c = new _.Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    };

    protected _.Comparator<Foo> cmp = new _.Comparator<Foo>() {
        @Override
        public int compare(Foo o1, Foo o2) {
            return o1._1 - o2._1;
        }
    };

    protected _.F1<Foo, String> keyExtractor = new _.F1<Foo, String>() {
        @Override
        public String apply(Foo foo) throws NotAppliedException, _.Break {
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
        yes(cmp.thenComparing(new _.Comparator<Foo>() {
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
        yes(_.F.NATURAL_ORDER.compare(one, two) < 0);
        yes(_.F.NATURAL_ORDER.compare(a, b) < 0);
    }

    @Test
    public void reverseOrderShallCompareObjectInNaturalWay() {
        no(_.F.REVERSE_ORDER.compare(one, two) < 0);
        no(_.F.REVERSE_ORDER.compare(a, b) < 0);
    }

    @Test
    public void reversedReverseOrderShallBeNaturalOrder() {
        yes(_.F.REVERSE_ORDER.reversed() == _.F.NATURAL_ORDER);
    }

    @Test
    public void reversedNaturalOrderShallBeReverseOrder() {
        yes(_.F.NATURAL_ORDER.reversed() == _.F.REVERSE_ORDER);
    }
}
