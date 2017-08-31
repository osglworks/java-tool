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
        verify("camelCase");
        verify("Camel Case");
    }

    @Test
    public void testCamelCaseWithSeparators() {
        keyword = Keyword.of("CamelCase and Separators");
        eq("camel-case-and-separators", keyword.dashed());
        eq("Camel-Case-And-Separators", keyword.httpHeader());
        eq(C.listOf("camel", "case", "and", "separators"), keyword.tokens());
    }

    @Test
    public void testAllUpperCases() {
        keyword = Keyword.of("ALL_UPPERCASES");
        eq("all-uppercases", keyword.dashed());
        eq(C.listOf("all", "uppercases"), keyword.tokens());
    }

    @Test
    public void testX() {
        keyword = Keyword.of("thisURLis valid");
        eq("this-url-is-valid", keyword.dashed());
        eq(C.listOf("this", "url", "is", "valid"), keyword.tokens());
    }

    private void verify(String s) {
        keyword = Keyword.of(s);
        eq("camel-case", keyword.dashed());
        eq("camel_case", keyword.underscore());
        eq("Camel case", keyword.readable());
        eq("CamelCase", keyword.camelCase());
        eq("CAMEL_CASE", keyword.constantName());
        eq("Camel-Case", keyword.httpHeader());
        eq("camelCase", keyword.javaVariable());
        eq("Camel Case", keyword.header());
        eq("camel case", keyword.text());
        eq(C.listOf("camel", "case"), keyword.tokens());
    }

}
