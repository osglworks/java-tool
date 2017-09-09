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
import org.osgl.util.S;

/**
 * Created by luog on 20/05/2014.
 */
public class F0Test extends OsglToolTestBase {

    protected $.F0<String> notApplied = new $.F0<String>() {
        @Override
        public String apply() throws NotAppliedException, $.Break {
            throw new NotAppliedException();
        }
    };

    protected $.F0<String> illegalState = new $.F0<String>() {
        @Override
        public String apply() throws NotAppliedException, $.Break {
            throw new IllegalStateException();
        }
    };

    protected $.F0<String> foo = new $.F0<String>() {
        @Override
        public String apply() throws NotAppliedException, $.Break {
            return "foo";
        }
    };

    protected $.F0<String> createFoo(final String s) {
        return new $.F0<String>() {
            @Override
            public String apply() throws NotAppliedException, $.Break {
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
