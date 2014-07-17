
package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.N;

/**
 * Created by luog on 8/06/2014.
 */
public class F2Test extends TestBase {

    private double rand1 = N.randDouble();
    private double rand2 = N.randDouble();
    private double ERROR_VAL = Double.MIN_VALUE;

    private _.F2<Number, Number, Number> divide = N.F.DIVIDE;
    private _.F2<Number, Number, Number> multiply = N.F.MULTIPLY;
    private _.F1<Number, Number> negative = N.F.NEGATIVE;
    private _.F2<Number, Number, Number> inCaseFailed = new _.F2<Number, Number, Number>() {
        @Override
        public Number apply(Number number, Number number2) throws NotAppliedException, _.Break {
            return ERROR_VAL;
        }
    };

    @Test
    public void elseShallNotBeCalledNormally() {
        eq(rand1 / rand2, divide.applyOrElse(rand1, rand2, inCaseFailed).doubleValue());
        eq(rand1 / rand2, divide.orElse(inCaseFailed).apply(rand1, rand2).doubleValue());
    }

    @Test
    public void elseShallBeCalledInCaseNotApplied() {
        eq(ERROR_VAL, divide.applyOrElse(rand1, 0, inCaseFailed).doubleValue());
        eq(ERROR_VAL, divide.orElse(inCaseFailed).apply(rand1, 0).doubleValue());
    }

    @Test
    public void testCurry() {
        eq(rand1 / rand2, divide.curry(rand2).apply(rand1).doubleValue());
    }

    @Test
    public void testCurry2() {
        eq(rand1 / rand2, divide.curry(rand1, rand2).apply().doubleValue());
    }

    @Test
    public void testChainedStyleAndThen() {
        eq(-1 * (rand1 / rand2), divide.andThen(negative).apply(rand1, rand2).doubleValue());
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        eq(rand1 * rand2, divide.andThen(multiply).apply(rand1, rand2).doubleValue());
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        yes(divide.lift().apply(rand1, 0).notDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(divide.lift().apply(rand1, rand2).isDefined());
    }
}
