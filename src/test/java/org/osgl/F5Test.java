
package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

public class F5Test extends FuncTestBase {

    private _.F5<String, String, String, String, String, Integer> calcHashCode = new _.F5<String, String, String, String, String, Integer>() {
        @Override
        public Integer apply(String s1, String s2, String s3, String s4, String s5) throws NotAppliedException, _.Break {
            E.NPE(s1, s2, s3, s4, s5);
            return (s1 + s2 + s3 + s4 + s5).hashCode();
        }
    };

    private _.F5<String, String, String, String, String, Integer> calcStringSize = new _.F5<String, String, String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3, String s4, String s5) throws NotAppliedException, _.Break {
            return (s + s2 + s3 + s4 + s5).length();
        }
    };

    @Test
    public void elseShallNotBeCalledNormally() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.hashCode(), calcHashCode.applyOrElse(rs1, rs2, rs3, rs4, rs5, calcStringSize));
        eq(s.hashCode(), calcHashCode.orElse(calcStringSize).apply(rs1, rs2, rs3, rs4, rs5));
    }

    @Test
    public void elseShallBeCalledInCaseNotApplied() {
        eq((null + rs2 + rs3 + rs4 + rs5).length(), calcHashCode.applyOrElse(null, rs2, rs3, rs4, rs5, calcStringSize));
        eq((null + rs2 + rs3 + rs4 + rs5).length(), calcHashCode.orElse(calcStringSize).apply(null, rs2, rs3, rs4, rs5));
    }

    @Test
    public void testCurry() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.hashCode(), calcHashCode.curry(rs5).apply(rs1, rs2, rs3, rs4));
    }

    @Test
    public void testCurry2() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.hashCode(), calcHashCode.curry(rs4, rs5).apply(rs1, rs2, rs3));
    }

    @Test
    public void testCurry3() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.hashCode(), calcHashCode.curry(rs3, rs4, rs5).apply(rs1, rs2));
    }

    @Test
    public void testCurry4() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.hashCode(), calcHashCode.curry(rs2, rs3, rs4, rs5).apply(rs1));
    }

    @Test
    public void testCurry5() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.hashCode(), calcHashCode.curry(rs1, rs2, rs3, rs4, rs5).apply());
    }

    @Test
    public void testChainedStyleAndThen() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(-1 * s.hashCode(), calcHashCode.andThen(NEGATIVE).apply(rs1, rs2, rs3, rs4, rs5).intValue());
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        String s = rs1 + rs2 + rs3 + rs4 + rs5;
        eq(s.length(), calcHashCode.andThen(calcStringSize).apply(rs1, rs2, rs3, rs4, rs5));
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        no(calcHashCode.lift().apply(null, rs2, rs3, rs4, rs5).isDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(calcHashCode.lift().apply(rs1, rs2, rs3, rs4, rs5).isDefined());
    }

    @Test
    public void dumbF5AlwaysReturnNull() {
        eq(null, _.F5.apply(rs1, rs2, rs3, rs4, rs5));
    }

    @Test
    public void f5ShallChangeFunc3ImplToF5Type() {
        _.Func5<String, String, String, String, String, Integer> foo = new _.Func5<String, String, String, String, String, Integer>() {
            @Override
            public Integer apply(String s, String s2, String s3, String s4, String s5) throws NotAppliedException, _.Break {
                return null;
            }
        };
        yes(_.f5(foo) instanceof _.F5);
    }

    @Test
    public void f5ShallReturnTheSameInstanceIfAppliedToF5Type() {
        yes(calcHashCode == _.f5(calcHashCode));
    }
}
