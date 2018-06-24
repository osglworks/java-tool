package org.osgl.util.algo;

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

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.S;

public abstract class StringSearch implements $.Func3<char[], char[], Integer, Integer> {
    /**
     * Search a char array in another char array with from index specified
     *
     * @param text the char array to be searched
     * @param target the pattern to be searched across the text
     * @param from the from index it shall be non-negative number. If from index is negative number then
     *             it starts from `0`; if `from` is greater than or equals to the text length then `-1`
     *             will be returned
     * @return index of the search pattern in the text. If the search pattern is found
     *         in the text region specified then it returns it's index in the text; otherwise
     *         an negative number, typically `-1` is returned
     */
    public abstract int search(char[] text, char[] target, int from);

    @Override
    public final Integer apply(char[] text, char[] target, Integer from) throws NotAppliedException, $.Break {
        return search(text, target, from);
    }

    public static StringSearch wrap(final $.Func3<char[], char[], Integer, Integer> searchLogic) {
        return $.requireNotNull(searchLogic) instanceof StringSearch ? (StringSearch) searchLogic : new StringSearch() {
            @Override
            public int search(char[] text, char[] target, int from) {
                return searchLogic.apply(text, target, from);
            }
        };
    }

    /**
     * Implement a simple string search algorithm using JDK `String.indexOf` algorithm
     */
    public static class SimpleStringSearch extends StringSearch {
        @Override
        public int search(char[] text, char[] target, int from) {
            if (from < 0) {
                from = 0;
            }
            int txtLen = text.length, targetLen = target.length;
            if (txtLen - from < targetLen) {
                return -1;
            }
            return  S.indexOf(text, 0, txtLen, target, 0, targetLen, from);
        }
    }
}
