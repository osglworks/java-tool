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
import org.osgl.OsglConfig;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.S;

/**
 * Base class for implementing algorithm that perform replacement on {@link char[]}
 */
public abstract class StringReplace implements $.Func4<char[], char[], char[], Integer, char[]> {

    /**
     * Apply the function to replace all target in text with replacement and return a
     * `char[]` contains the result with all target replaced with replacement
     *
     * @param text the text
     * @param target the target to search
     * @param replacement the replacement of target
     * @param firstId the first index of targe inside text. If negative means not searched yet
     * @return the replaced result as described above
     * @throws NotAppliedException
     * @throws $.Break
     */
    @Override
    public final char[] apply(char[] text, char[] target, char[] replacement, Integer firstId) throws NotAppliedException, $.Break {
        return replace(text, target, replacement, firstId);
    }

    /**
     * Sub class shall implement the replacement logic in this method
     * @param text the text in which search string will be replaced
     * @param target the target string to be replaced
     * @param replacement the replacement string
     * @param firstId the first index of targe inside text. If negative means not searched yet
     * @return result of the replacement
     */
    public abstract char[] replace(char[] text, char[] target, char[] replacement, int firstId);

    public static StringReplace wrap(final $.Func4<char[], char[], char[], Integer, char[]> replaceLogic) {
        return $.requireNotNull(replaceLogic) instanceof StringReplace ? (StringReplace) replaceLogic : new StringReplace() {
            @Override
            public char[] replace(char[] text, char[] target, char[] replacement, int firstId) {
                return replaceLogic.apply(text, target, replacement, firstId);
            }
        };
    }

    public static class SimpleStringReplace extends StringReplace {

        private final StringSearch searcher;

        public SimpleStringReplace(StringSearch searcher) {
            this.searcher = $.requireNotNull(searcher);
        }

        public SimpleStringReplace() {
            this(OsglConfig.DEF_STRING_SEARCH);
        }

        @Override
        public char[] replace(char[] text, char[] target, char[] replacement, int firstId) {
            StringSearch searcher = this.searcher;
            S.Buffer buf;
            int textLen = text.length, targetLen = target.length, i = 0, j = 0;
            if (textLen == 0 || targetLen == 0) {
                return text;
            }
            i = firstId < 0 ? searcher.search(text, target, 0) : firstId;
            if (i < 0) {
                return text;
            }
            buf = S.buffer();
            if (i > j) {
                buf.append(text, j, i - j);
            }
            buf.append(replacement);
            i += targetLen;
            j = i;
            do {
                i = searcher.search(text, target, i);
                if (i < 0) {
                    break;
                }
                if (i > j) {
                    buf.append(text, j, i - j);
                }
                buf.append(replacement);
                i += targetLen;
                j = i;
            } while (true);
            if (textLen > j) {
                buf.append(text, j, textLen - j);
            }
            int len = buf.length();
            char[] result = new char[len];
            buf.getChars(0, len, result, 0);
            return result;
        }
    }
}
