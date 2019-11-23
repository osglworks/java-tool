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
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public abstract class BigLinesLineCountTest {

    public static abstract class CountTestBase extends BigLineTestBase {
        public CountTestBase(int lines) {
            super(lines);
        }

        @Test
        public void testLineCount() {
            eq(lines, bigLines.lines());
        }

    }
    


    public static class EmptyFileLineCountTest extends CountTestBase {
        public EmptyFileLineCountTest() {
            super(0);
        }
        @Test
        public void testIteratingWithBigBuffer() {
            int i = 0;
            for (String s : bigLines.asIterable(100)) {
                eq(i++, Integer.parseInt(s));
            }
            eq(0, i);
        }
    }

    public static class OneLineFileLineCountTest extends CountTestBase {
        public OneLineFileLineCountTest() {
            super(1);
        }
    }

    public static class TwoLinesFileLineCountTest extends CountTestBase {
        public TwoLinesFileLineCountTest() {
            super(2);
        }
    }

    public static class MultipleLinesFileLineCountTest extends CountTestBase {
        public MultipleLinesFileLineCountTest() {
            super(100);
        }
    }

}
