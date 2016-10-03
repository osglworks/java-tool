package org.osgl.util;

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
