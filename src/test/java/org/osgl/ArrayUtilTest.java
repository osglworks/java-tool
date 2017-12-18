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

public class ArrayUtilTest extends TestBase {

    @Test
    public void testReverse() {
        int[] ia = {1, 2, 3};
        yes($.eq2(new int[] {3, 2, 1}, $.reverse(ia)));
    }

    @Test
    public void testReverseObjectArray() {
        String[] sa = {"1", "2", "3", "4"};
        yes($.eq2(new String[] {"4", "3", "2", "1"}, $.reverse(sa)));
    }

    @Test
    public void testConcat() {
        int[] a1 = {1, 2}, a2 = {3, 4}, a3 = {5};
        yes($.eq2(new int[] {1, 2, 3, 4, 5}, $.concat(a1, a2, a3)));
    }
}
