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

import org.junit.Before;
import org.junit.Ignore;
import org.osgl.util.N;
import org.osgl.util.S;

@Ignore
public class FuncTestBase extends OsglToolTestBase {

    protected final double RAND_1 = N.randDouble();
    protected final double RAND_2 = N.randDouble();
    protected final double ERROR_VAL = Double.MIN_VALUE;
    protected final $.F1<Number, Number> NEGATIVE = N.F.NEGATIVE;
    protected String rs1, rs2, rs3, rs4, rs5;

    @Before
    public void setup() {
        rs1 = S.random();
        rs2 = S.random();
        rs3 = S.random();
        rs4 = S.random();
        rs5 = S.random();
    }

}
