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

public class BigLinesIteratorTest extends BigLineTestBase {

    private static final int FILE_LINES = 123456;

    public BigLinesIteratorTest() {
        super(FILE_LINES);
    }

    @Test
    public void testIteratingWithBigBuffer() {
        int i = 0;
        for (String s : bigLines.asIterable(FILE_LINES + 100)) {
            eq(i++, Integer.parseInt(s));
        }
        eq(FILE_LINES, i);
    }

    @Test
    public void testIteratingWithSmallBuffer() {
        int i = 0;
        for (String s : bigLines.asIterable(1000)) {
            eq(i++, Integer.parseInt(s));
        }
        eq(FILE_LINES, i);
    }

    @Test
    public void testIteratingWithEvenBuffer() {
        int i = 0;
        for (String s : bigLines.asIterable(FILE_LINES)) {
            eq(i++, Integer.parseInt(s));
        }
        eq(FILE_LINES, i);
    }

}
