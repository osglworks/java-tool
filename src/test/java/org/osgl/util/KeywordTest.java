package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

/**
 * Test {@link Keyword}
 */
public class KeywordTest extends TestBase {

    private Keyword keyword;

    @Test
    public void testKeywordFromCamelCase() {
        verify("CamelCase");
    }

    @Test
    public void testKeywordFromSeparatedWordGroups() {
        verify("camel-case");
        verify("camel case");
        verify("Camel, Case");
        verify("camel:case");
        verify("CAMEL_CASE");
    }

    @Test
    public void testCamelCaseWithSeparators() {
        keyword = Keyword.of("CamelCase and Separators");
        eq("camel-case-and-separators", keyword.dashed());
    }

    @Test
    public void testAllUpperCases() {
        keyword = Keyword.of("ALL_UPPERCASES");
        eq("all-uppercases", keyword.dashed());
    }

    @Test
    public void testX() {
        keyword = Keyword.of("thisURLis valid");
        eq("this-url-is-valid", keyword.dashed());
    }

    private void verify(String s) {
        keyword = Keyword.of(s);
        eq("camel-case", keyword.dashed());
        eq("camel_case", keyword.underscore());
        eq("Camel case", keyword.readable());
        eq("CamelCase", keyword.camelCase());
        eq("CAMEL_CASE", keyword.constant());
    }

}
