
package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

public class F3Test extends FuncTestBase {

    private _.F3<String, String, String, Integer> calcHashCode = new _.F3<String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3) throws NotAppliedException, _.Break {
            E.NPE(s, s2, s3);
            return (s + s2 + s3).hashCode();
        }
    };

    private _.F3<String, String, String, Integer> calcStringSize = new _.F3<String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3) throws NotAppliedException, _.Break {
            return (s + s2 + s3).length();
        }
    };

    @Test
    public void elseShallNotBeCalledNormally() {
        String s = rs1 + rs2 + rs3;
        eq(s.hashCode(), calcHashCode.applyOrElse(rs1, rs2, rs3, calcStringSize));
        eq(s.hashCode(), calcHashCode.orElse(calcStringSize).apply(rs1, rs2, rs3));
    }

    @Test
    public void elseShallBeCalledInCaseNotApplied() {
        eq((null + rs2 + rs3).length(), calcHashCode.applyOrElse(null, rs2, rs3, calcStringSize));
        eq((null + rs2 + rs3).length(), calcHashCode.orElse(calcStringSize).apply(null, rs2, rs3));
    }

    @Test
    public void testCurry() {
        String s = rs1 + rs2 + rs3;
        eq(s.hashCode(), calcHashCode.curry(rs3).apply(rs1, rs2));
    }

    @Test
    public void testCurry2() {
        String s = rs1 + rs2 + rs3;
        eq(s.hashCode(), calcHashCode.curry(rs2, rs3).apply(rs1));
    }

    @Test
    public void testCurry3() {
        String s = rs1 + rs2 + rs3;
        eq(s.hashCode(), calcHashCode.curry(rs1, rs2, rs3).apply());
    }

    @Test
    public void testChainedStyleAndThen() {
        String s = rs1 + rs2 + rs3;
        eq(-1 * s.hashCode(), calcHashCode.andThen(NEGATIVE).apply(rs1, rs2, rs3).intValue());
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        String s = rs1 + rs2 + rs3;
        eq(s.length(), calcHashCode.andThen(calcStringSize).apply(rs1, rs2, rs3));
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        no(calcHashCode.lift().apply(null, rs2, rs3).isDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(calcHashCode.lift().apply(rs1, rs2, rs3).isDefined());
    }

    @Test
    public void dumbF3AlwaysReturnNull() {
        eq(null, _.F3.apply(rs1, rs2, rs3));
    }

    @Test
    public void f3ShallChangeFunc3ImplToF3Type() {
        _.Func3<String, String, String, Integer> foo = new _.Func3<String, String, String, Integer>() {
            @Override
            public Integer apply(String s, String s2, String s3) throws NotAppliedException, _.Break {
                return null;
            }
        };
        yes(_.f3(foo) instanceof _.F3);
    }

    @Test
    public void f3ShallReturnTheSameInstanceIfAppliedToF3Type() {
        yes(calcHashCode == _.f3(calcHashCode));
    }
}
