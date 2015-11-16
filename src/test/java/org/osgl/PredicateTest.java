package org.osgl;

import org.junit.Test;
import org.osgl.util.N;

public class PredicateTest extends TestBase {

    private class ReturnPreSetBool extends $.Predicate {

        private boolean preset;

        ReturnPreSetBool(boolean b) {
            preset = b;
        }

        @Override
        public boolean test(Object o) {
            return preset;
        }
    }

    @Test
    public void testNegative() {
        int i = $.random(0, 1);
        $.Predicate p = new ReturnPreSetBool(i == 0);
        yes(p.apply(new Object()) != p.negate().apply(new Object()));
    }

    @Test
    public void testAnd() {
        no(N.F.greaterThan(5).and(N.F.lessThan(10), $.F.eq(8)).apply(7));
        no(N.F.greaterThan(5).and(N.F.lessThan(10), $.F.eq(8)).apply(4));
        yes(N.F.greaterThan(5).and(N.F.lessThan(10), $.F.eq(8)).apply(8));
    }

    @Test
    public void testOr() {
        yes(N.F.greaterThan(5).or(N.F.lessThan(10), $.F.eq(8)).apply(7));
        yes(N.F.greaterThan(5).or(N.F.lessThan(10), $.F.eq(8)).apply(4));
        no(N.F.greaterThan(5).or($.F.eq(10), $.F.eq(8)).apply(4));
    }

    @Test
    public void testIfThen() {
        eq(4, N.F.greaterThan(0).ifThen(N.F.dbl()).apply(2).get());
        yes(N.F.greaterThan(0).ifThen(N.F.dbl()).apply(-1).notDefined());
    }

    @Test
    public void testElseThen() {
        eq(4, N.F.lessThan(0).elseThen(N.F.dbl()).apply(2).get());
        yes(N.F.lessThan(0).elseThen(N.F.dbl()).apply(-1).notDefined());
    }

    @Test
    public void testOsglPredicate() {
        $.Predicate p = N.F.greaterThan(4);
        assertSame(p, $.predicate(p));
    }

}
