package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
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
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;

public class Gh92 extends TestBase {

    @Test
    public void test() {
        int end = 3, start = 1;
        int[] ia0 = new int[end - start];
        int[] ia = $.copy(C.range(start, end)).to(ia0);
        same(ia0, ia);
        eq(2, ia.length);
        eq(1, ia[0]);
        eq(2, ia[1]);
    }

}
