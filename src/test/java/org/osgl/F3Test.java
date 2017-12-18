
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

public class F3Test extends FuncTestBase {

    private $.F3<String, String, String, Integer> calcHashCode = new $.F3<String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3) throws NotAppliedException, $.Break {
            E.NPE(s, s2, s3);
            return (s + s2 + s3).hashCode();
        }
    };

    private $.F3<String, String, String, Integer> calcStringSize = new $.F3<String, String, String, Integer>() {
        @Override
        public Integer apply(String s, String s2, String s3) throws NotAppliedException, $.Break {
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
        eq(null, $.F3.apply(rs1, rs2, rs3));
    }

    @Test
    public void f3ShallChangeFunc3ImplToF3Type() {
        $.Func3<String, String, String, Integer> foo = new $.Func3<String, String, String, Integer>() {
            @Override
            public Integer apply(String s, String s2, String s3) throws NotAppliedException, $.Break {
                return null;
            }
        };
        yes($.f3(foo) instanceof $.F3);
    }

    @Test
    public void f3ShallReturnTheSameInstanceIfAppliedToF3Type() {
        yes(calcHashCode == $.f3(calcHashCode));
    }
}
