
package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.N;

/**
 * Created by luog on 8/06/2014.
 */
public class F2Test extends FuncTestBase {

    private _.F2<Number, Number, Number> divide = N.F.DIVIDE;
    private _.F2<Number, Number, Number> multiply = N.F.MULTIPLY;
    private _.F2<Number, Number, Number> inCaseFailed = new _.F2<Number, Number, Number>() {
        @Override
        public Number apply(Number number, Number number2) throws NotAppliedException, _.Break {
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
