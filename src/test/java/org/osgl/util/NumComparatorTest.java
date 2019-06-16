package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2019 OSGL (Open Source General Library)
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
import org.osgl.TestBase;

import static org.osgl.util.N.Comparator.*;

public class NumComparatorTest extends TestBase {

    @Test
    public void test() {
        yes(EQ.compare(1d, 1.0d));
        yes(GTE.compare(1d, 1.0d));
        yes(LTE.compare(1d, 1.0d));
        no(GT.compare(1d, 1.0d));
        no(LT.compare(1d, 1.0d));

        yes(GT.compare(1.1d, 1.0d));
        yes(LT.compare(1.1d, 1.2d));
    }

}
