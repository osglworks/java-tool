package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.S;

/**
 * Created by luog on 20/05/2014.
 */
public class F0Test extends TestBase {

    protected _.F0<String> notApplied = new _.F0<String>() {
        @Override
        public String apply() throws NotAppliedException, _.Break {
            throw new NotAppliedException();
        }
    };

    protected _.F0<String> illegalState = new _.F0<String>() {
        @Override
        public String apply() throws NotAppliedException, _.Break {
            throw new IllegalStateException();
        }
    };

    protected _.F0<String> foo = new _.F0<String>() {
        @Override
        public String apply() throws NotAppliedException, _.Break {
            return "foo";
        }
    };

    protected _.F0<String> createFoo(final String s) {
        return new _.F0<String>() {
            @Override
            public String apply() throws NotAppliedException, _.Break {
                return s;
            }
        };
    }


    @Test
    public void elseShallNotBeCalledNormally() {
        eq("foo", notApplied.applyOrElse(foo));
        eq("foo", notApplied.orElse(foo).apply());
    }

    @Test
    public void elseShallBeCalledOnException() {
        eq("foo", illegalState.applyOrElse(foo));
    }

    @Test
    public void elseShallBeCalledOnException2() {
        eq("foo", illegalState.orElse(foo).apply());
    }

    @Test
    public void testChainedStyleAndThen() {
        int n = foo.andThen(S.F.LENGTH).apply();
        eq(3, n);
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        String s = foo.andThen(createFoo("foo1"), createFoo("foo2")).apply();
        eq("foo2", s);
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        yes(notApplied.lift().apply().notDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(foo.lift().apply().isDefined());
    }
}
