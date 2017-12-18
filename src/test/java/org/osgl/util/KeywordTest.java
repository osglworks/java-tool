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
        eq(C.listOf("camel", "case"), keyword.tokens());
    }

}
