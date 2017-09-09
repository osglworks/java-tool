package org.osgl.util;

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
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.TestBase;

import java.io.StringReader;
import java.util.List;

/**
 * Test {@link IO} utilities
 */
public class IOTest extends TestBase {

    protected static String content;
    protected static C.List<String> lines;
    protected static int lineNumber;

    @BeforeClass
    public static void prepareContent() {
        lineNumber = 5 + N.randInt(10);
        lines = C.newList();
        for (int i = 0; i < lineNumber; ++i) {
            lines.add(S.random());
        }
        content = S.join("\n", lines);
    }

    @Test
    public void readLineWithoutLimit() {
        List<String> read = IO.readLines(new StringReader(content));
        eq(lines, read);
    }

    @Test
    public void readLinesWithLimitLargerThanContentLines() {
        List<String> read = IO.readLines(new StringReader(content), lineNumber + 5);
        eq(lines, read);
    }

    @Test
    public void readLinesWithLimitSmallerThanContentLines() {
        List<String> read = IO.readLines(new StringReader(content), lineNumber - 3);
        eq(lines.take(lineNumber - 3), read);
    }
}
