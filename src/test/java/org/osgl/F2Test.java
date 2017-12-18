
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
import org.osgl.util.N;

/**
 * Created by luog on 8/06/2014.
 */
public class F2Test extends FuncTestBase {

    private $.F2<Number, Number, Number> divide = N.F.DIVIDE;
    private $.F2<Number, Number, Number> multiply = N.F.MULTIPLY;
    private $.F2<Number, Number, Number> inCaseFailed = new $.F2<Number, Number, Number>() {
        @Override
        public Number apply(Number number, Number number2) throws NotAppliedException, $.Break {
            return ERROR_VAL;
        }
    };

    @Test
    public void elseShallNotBeCalledNormally() {
        eq(RAND_1 / RAND_2, divide.applyOrElse(RAND_1, RAND_2, inCaseFailed).doubleValue());
        eq(RAND_1 / RAND_2, divide.orElse(inCaseFailed).apply(RAND_1, RAND_2).doubleValue());
    }

    @Test
    public void elseShallBeCalledInCaseNotApplied() {
        eq(ERROR_VAL, divide.applyOrElse(RAND_1, 0, inCaseFailed).doubleValue());
        eq(ERROR_VAL, divide.orElse(inCaseFailed).apply(RAND_1, 0).doubleValue());
    }

    @Test
    public void testCurry() {
        eq(RAND_1 / RAND_2, divide.curry(RAND_2).apply(RAND_1).doubleValue());
    }

    @Test
    public void testCurry2() {
        eq(RAND_1 / RAND_2, divide.curry(RAND_1, RAND_2).apply().doubleValue());
    }

    @Test
    public void testChainedStyleAndThen() {
        eq(-1 * (RAND_1 / RAND_2), divide.andThen(NEGATIVE).apply(RAND_1, RAND_2).doubleValue());
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        eq(RAND_1 * RAND_2, divide.andThen(multiply).apply(RAND_1, RAND_2).doubleValue());
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        yes(divide.lift().apply(RAND_1, 0).notDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(divide.lift().apply(RAND_1, RAND_2).isDefined());
    }
}
