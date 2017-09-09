
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

public class F5Test extends FuncTestBase {

    private $.F5<String, String, String, String, String, Integer> calcHashCode = new $.F5<String, String, String, String, String, Integer>() {
        @Override
        public Integer apply(String s1, String s2, String s3, String s4, String s5) throws NotAppliedException, $.Break {
            E.NPE(s1, s2, s3, s4, s5);
            return (s1 + s2 + s3 + s4 + s5).hashCode();
        }
    };

    private $.F5<String, String, String, String, String, Integer> calcStringSize = new $.F5<String, String, String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3, String s4, String s5) throws NotAppliedException, $.Break {
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
        eq(null, $.F5.apply(rs1, rs2, rs3, rs4, rs5));
    }

    @Test
    public void f5ShallChangeFunc3ImplToF5Type() {
        $.Func5<String, String, String, String, String, Integer> foo = new $.Func5<String, String, String, String, String, Integer>() {
            @Override
            public Integer apply(String s, String s2, String s3, String s4, String s5) throws NotAppliedException, $.Break {
                return null;
            }
        };
        yes($.f5(foo) instanceof $.F5);
    }

    @Test
    public void f5ShallReturnTheSameInstanceIfAppliedToF5Type() {
        yes(calcHashCode == $.f5(calcHashCode));
    }
}
