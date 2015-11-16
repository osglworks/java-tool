
package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.E;

public class F4Test extends FuncTestBase {

    private $.F4<String, String, String, String, Integer> calcHashCode = new $.F4<String, String, String, String, Integer>() {
        @Override
        public Integer apply(String s1, String s2, String s3, String s4) throws NotAppliedException, $.Break {
            E.NPE(s1, s2, s3, s4);
            return (s1 + s2 + s3 + s4).hashCode();
        }
    };

    private $.F4<String, String, String, String, Integer> calcStringSize = new $.F4<String, String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3, String s4) throws NotAppliedException, $.Break {
            return (s + s2 + s3 + s4).length();
        }
    };

    @Test
    public void elseShallNotBeCalledNormally() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(s.hashCode(), calcHashCode.applyOrElse(rs1, rs2, rs3, rs4, calcStringSize));
        eq(s.hashCode(), calcHashCode.orElse(calcStringSize).apply(rs1, rs2, rs3, rs4));
    }

    @Test
    public void elseShallBeCalledInCaseNotApplied() {
        eq((null + rs2 + rs3 + rs4).length(), calcHashCode.applyOrElse(null, rs2, rs3, rs4, calcStringSize));
        eq((null + rs2 + rs3 + rs4).length(), calcHashCode.orElse(calcStringSize).apply(null, rs2, rs3, rs4));
    }

    @Test
    public void testCurry() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(s.hashCode(), calcHashCode.curry(rs4).apply(rs1, rs2, rs3));
    }

    @Test
    public void testCurry2() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(s.hashCode(), calcHashCode.curry(rs3, rs4).apply(rs1, rs2));
    }

    @Test
    public void testCurry3() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(s.hashCode(), calcHashCode.curry(rs2, rs3, rs4).apply(rs1));
    }

    @Test
    public void testCurry4() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(s.hashCode(), calcHashCode.curry(rs1, rs2, rs3, rs4).apply());
    }

    @Test
    public void testChainedStyleAndThen() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(-1 * s.hashCode(), calcHashCode.andThen(NEGATIVE).apply(rs1, rs2, rs3, rs4).intValue());
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        String s = rs1 + rs2 + rs3 + rs4;
        eq(s.length(), calcHashCode.andThen(calcStringSize).apply(rs1, rs2, rs3, rs4));
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        no(calcHashCode.lift().apply(null, rs2, rs3, rs4).isDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(calcHashCode.lift().apply(rs1, rs2, rs3, rs4).isDefined());
    }

    @Test
    public void dumbF4AlwaysReturnNull() {
        eq(null, $.F4.apply(rs1, rs2, rs3, rs4));
    }

    @Test
    public void f4ShallChangeFunc3ImplToF4Type() {
        $.Func4<String, String, String, String, Integer> foo = new $.Func4<String, String, String, String, Integer>() {
            @Override
            public Integer apply(String s, String s2, String s3, String s4) throws NotAppliedException, $.Break {
                return null;
            }
        };
        yes($.f4(foo) instanceof $.F4);
    }

    @Test
    public void f4ShallReturnTheSameInstanceIfAppliedToF4Type() {
        yes(calcHashCode == $.f4(calcHashCode));
    }
}
