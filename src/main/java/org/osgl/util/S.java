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

import static java.lang.Character.highSurrogate;
import static java.lang.Character.lowSurrogate;

import org.osgl.$;
import org.osgl.OsglConfig;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.algo.StringReplace;

import java.io.File;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * The namespace for OSGL string utilities.
 *
 * Alias of {@link StringUtil}
 */
public class S {

    S() {}

    // ---- CONSTANTS DEFINITION ----

    /**
     * An empty string array constant
     */
    public static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The invisible separator used by program: "\u0000"
     */
    public static final String HSEP = "\u0000";

    /**
     * Alias of {@link #HSEP}
     */
    public static final String HIDDEN_SEPARATOR = HSEP;

    /**
     * The invisible separator char `'\u0000'`
     */
    public static final char HSEP_CHAR = '\u0000';

    /**
     * Alias of {@link #HSEP_CHAR}
     */
    public static final char HIDDEN_SEPARATOR_CHAR = HSEP_CHAR;

    /**
     * A commonly used separator: [,;:\\s]+
     */
    public static final String COMMON_SEP = "[,;:\\s]+";

    /**
     * A precompiled {@link Pattern} of {@link #COMMON_SEP}
     */
    public static final Pattern PATTERN_COMMON_SEP = Pattern.compile(COMMON_SEP);

    /**
     * The {@link File#separator}
     */
    public static final String FILE_SEP = File.separator;

    /**
     * The {@link File#separatorChar}
     */
    public static final char FILE_SEP_CHAR = File.separatorChar;

    /**
     * The {@link File#pathSeparator}
     */
    public static final String PATH_SEP = File.pathSeparator;

    /**
     * The {@link File#pathSeparatorChar
     */
    public static final char PATH_SEP_CHAR = File.pathSeparatorChar;

    /**
     * The single quote: `"'"`
     */
    public static final String SINGLE_QUOTE = ",";

    /**
     * The double quote: `"\""`
     */
    public static final String DOUBLE_QUOTE = "\"";

    /**
     * The char `'`
     */
    public static final char SINGLE_QUOTE_CHAR = '\'';

    /**
     * The char `"`
     */
    public static final char DOUBLE_QUOTE_CHAR = '"';

    /**
     * A pair of double quotes: `"` and `"`
     */
    public static final Pair DOUBLE_QUOTES = pair(DOUBLE_QUOTE, DOUBLE_QUOTE);

    /**
     * A pair of single quotes: `'` and `'`
     */
    public static final Pair SINGLE_QUOTES = pair(SINGLE_QUOTE, SINGLE_QUOTE);

    /**
     * A pair of parentheses: `(` and `)`
     */
    public static final Pair PARENTHESES = pair("(", ")");

    /**
     * A pair of brackets: `[` and `]`
     */
    public static final Pair BRACKETS = pair("[", "]");

    /**
     * Alias of {@link #BRACKETS}
     */
    public static final Pair SQUARE_BRACKETS = BRACKETS;

    /**
     * A pair of braces: `{` and `}`
     */
    public static final Pair BRACES = pair("{", "}");

    /**
     * Alias of {@link #BRACES}
     */
    public static final Pair CURLY_BRACES = BRACES;

    /**
     * A pair of angle brackets: `<` and `>`
     */
    public static final Pair DIAMOND = pair("<", ">");

    /**
     * Alias of {@link #DIAMOND}
     */
    public static final Pair ANGLE_BRACKETS = DIAMOND;

    /**
     * A pair of single angle quotation mark: `‹` (`u2039`) and `›` (`u203a`)
     */
    public static final Pair SINGLE_ANGLE_QUOTATION_MARK = pair("‹", "›");

    /**
     * A pair of double angle quotation mark: `\u00ab` (`u00ab`) and `\u00bb` (`u00bb`)
     */
    public static final Pair DOUBLE_ANGLE_QUOTATION_MARK = pair("«", "»");

    /**
     * A pair of 书名号 (ShuMingHao, a Chevron-like symbol): `\u300a` (`u300a`) and `\u300b` (`u300b`)
     */
    public static final Pair 书名号 = pair("《", "》");

    /**
     * Alias of {@link #书名号}
     */
    public static final Pair SHU_MING_HAO = 书名号;

    // ---- EOF CONSTANTS DEFINITION ----

    public static String getCommonSep() {
        return COMMON_SEP;
    }

    public final static String fmt(String tmpl) {
        return tmpl;
    }

    /**
     * A handy alias for {@link String#format(String, Object...)}
     *
     * @param tmpl the message template
     * @param args the message arguments
     * @return the formatted string
     */
    public final static String fmt(String tmpl, Object... args) {
        if (0 == args.length) return tmpl;
        return String.format(tmpl, args);
    }


    /**
     * Returns the template or `""` if template is `null`
     * @param template a string
     * @return the template or `""` if template is `null`
     */
    public static String msgFmt(String template) {
        return S.string(template);
    }

    /**
     * A handy alias for {@link MessageFormat#format(String, Object...)}
     *
     * @param template the message template
     * @param args the message arguments
     * @return the formatted string or `""` if template is `null`
     */
    public static String msgFmt(String template, Object... args) {
        if (0 == args.length) return template;
        if (null == template) return "";
        return MessageFormat.format(template, args);
    }

    /**
     * alias of {@link #empty(String)}
     *
     * @param s the string to be checked
     * @return true if `s` is `null` or `empty`
     */
    public static boolean isEmpty(String s) {
        return empty(s);
    }

    /**
     * Alias of {@link #empty(CharSequence)}
     *
     * @param csq
     *      the char sequence to be tested
     * @return
     *      `true` if the char sequence is `null` or empty
     */
    public static boolean isEmpty(CharSequence csq) {
        return null == csq || 0 == csq.length();
    }

    /**
     * Determine if a string is empty or null
     *
     * @param s the string to be checked
     * @return true if the string is null or empty (no spaces)
     */
    public static boolean empty(String s) {
        return (null == s || "".equals(s));
    }

    /**
     * Check if a `CharSequence` is `null` or empty.
     *
     * @param csq
     *      the char sequence to be checked.
     * @return
     *      `true` if the char sequence is `null` or empty
     */
    public static boolean empty(CharSequence csq) {
        return null == csq || 0 == csq.length();
    }

    /**
     * Determine if a string is all blank or empty or null
     *
     * @param s the string to be checked
     * @return true if the string is null or empty or all blanks
     */
    public static boolean blank(String s) {
        return (null == s || "".equals(s.trim()));
    }

    /**
     * alias of {@link #blank(String)}
     *
     * @param s the string to be checked
     * @return true if `s` is `null` or empty or blank
     */
    public static boolean isBlank(String s) {
        return blank(s);
    }

    /**
     * alias of {@link #notEmpty(String)}
     *
     * @param s the string to be checked
     * @return true if <code>s</code> is <code>null</code> or empty
     */
    public static boolean isNotEmpty(String s) {
        return notEmpty(s);
    }

    /**
     * Antonym of {@link #empty(String)}
     *
     * @param s the string to be checked
     * @return true if <code>s</code> is not <code>null</code> or empty
     */
    public static boolean notEmpty(String s) {
        return !empty(s);
    }

    /**
     * Antonym of {@link #blank(String)}
     *
     * @param s the string to be checked
     * @return true if <code>s</code> is not <code>null</code> or empty or all in blank
     */
    public static boolean notBlank(String s) {
        return !blank(s);
    }

    /**
     * Antonym of {@link #blank(String)}
     *
     * @param s the string to be checked
     * @return true if <code>s</code> is not <code>null</code> or empty or all in blank
     */
    public static boolean isNotBlank(String s) {
        return !blank(s);
    }

    /**
     * Check if all of the specified string is {@link #empty(String) empty}
     *
     * @param sa the string to be checked
     * @return true if all of the specified string is empty
     */
    public static boolean isAllEmpty(String... sa) {
        return allEmpty(sa);
    }

    /**
     * Alias of {@link #isAllEmpty(String...)}
     *
     * @param sa the strings to be checked
     * @return true if all of the specified string is empty
     */
    public static boolean allEmpty(String... sa) {
        for (String s : sa) {
            if (!empty(s)) return false;
        }
        return true;
    }

    /**
     * Alias of {@link #allBlank(String...)}
     *
     * @param sa the strings to be checked
     * @return true if all of the specified string is blank
     */
    public static boolean isAllBlank(String... sa) {
        return allBlank(sa);
    }

    /**
     * Check if all of the specified string is {@link #blank(String) blank}
     *
     * @param sa the strings to be checked
     * @return true if all of the specified string is blank
     */
    public static boolean allBlank(String... sa) {
        for (String s : sa) {
            if (!blank(s)) return false;
        }
        return true;
    }

    /**
     * Check if anyone of the specified string is {@link #empty(String) empty}
     *
     * @param sa the strings to be checked
     * @return <code>true</code> if anyone of the specified string is empty
     */
    public static boolean isAnyEmpty(String... sa) {
        return anyEmpty(sa);
    }

    /**
     * Alias of {@link #isAnyEmpty(String...)}
     *
     * @param sa the strings to be checked
     * @return <code>true</code> if anyone of the specified string is empty
     */
    public static boolean anyEmpty(String... sa) {
        for (String s : sa) {
            if (empty(s)) return true;
        }
        return false;
    }

    /**
     * Alias of {@link #anyBlank(String...)}
     *
     * @param sa the strings to be checked
     * @return <code>true</code> if anyone of the specified string is blank
     */
    public static boolean isAnyBlank(String... sa) {
        return anyBlank(sa);
    }

    /**
     * Check if anyone of the specified string is {@link #empty(String) blank}
     *
     * @param sa the strings to be checked
     * @return <code>true</code> if anyone of the specified string is blank
     */
    public static boolean anyBlank(String... sa) {
        for (String s : sa) {
            if (blank(s)) return true;
        }
        return false;
    }

    /**
     * Antonym of {@link #anyEmpty(String...)}
     *
     * @param sa the strings to be checked
     * @return <code>false</code> if anyone of the specified string is empty
     */
    public static boolean noEmpty(String... sa) {
        return !anyEmpty(sa);
    }

    /**
     * Antonym of {@link #anyBlank(String...)}
     *
     * @param sa the strings to be checked
     * @return <code>false</code> if anyone of the specified string is empty
     */
    public static boolean noBlank(String... sa) {
        return !anyBlank(sa);
    }

    /**
     * Check if a string is integer or long
     *
     * @param s the string
     * @return {@code true} if the string is integer or long
     * @see N#isInt(String)
     */
    public static boolean isIntOrLong(String s) {
        return N.isInt(s);
    }

    /**
     * Check if a string is integer or long
     *
     * @param s the string
     * @return {@code true} if the string is integer or long
     * @see N#isInt(String)
     */
    public static boolean isInt(String s) {
        return N.isInt(s);
    }

    /**
     * Check if a string is numeric
     *
     * @param s the string to be checked
     * @return `true` if `s` is numeric string
     * @see N#isNumeric(String)
     */
    public static boolean isNumeric(String s) {
        return N.isNumeric(s);
    }

    /**
     * Throw IllegalArgumentException if the string specified
     * {@link #isBlank(String) is blank}, otherwise return
     * the string specified.
     *
     * Error message template and arguments will be used
     * to construct the error message if `s` is blank
     *
     * @param s the string to be tested
     * @param errorTemplate error message template
     * @param errorArgs error message arguments
     * @return the string if it is not blank
     * @throws IllegalArgumentException if the string `s` is blank
     */
    public static String requireNotBlank(String s, String errorTemplate, Object ... errorArgs) {
        E.illegalArgumentIf(isBlank(s), errorTemplate, errorArgs);
        return s;
    }

    /**
     * Throw IllegalArgumentException if the string specified
     * {@link #isBlank(String) is blank}, otherwise return
     * the string specified
     *
     * @param s the string to be tested
     * @return the string if it is not blank
     * @throws IllegalArgumentException if the string `s` is blank
     */
    public static String requireNotBlank(String s) {
        E.illegalArgumentIf(isBlank(s));
        return s;
    }

    /**
     * Throw IllegalArgumentException if the string specified
     * {@link #isEmpty(String)}  is empty}, otherwise return
     * the string specified
     *
     * @param s the string to be tested
     * @return the string if it is not empty
     * @throws IllegalArgumentException if the string `s` is empty
     */
    public static String requireNotEmpty(String s) {
        E.illegalArgumentIf(isEmpty(s));
        return s;
    }

    /**
     * Throw IllegalArgumentException if the string specified
     * {@link #isEmpty(String)}  is empty}, otherwise return
     * the string specified.
     *
     * Error message template and arguments will be used
     * to construct the error message if `s` is blank
     *
     * @param s the string to be tested
     * @param errorTemplate error message template
     * @param errorArgs error message arguments
     * @return the string if it is not empty
     * @throws IllegalArgumentException if the string `s` is empty
     */
    public static String requireNotEmpty(String s, String errorTemplate, Object ... errorArgs) {
        E.illegalArgumentIf(isEmpty(s));
        return s;
    }

    /**
     * Return the string of first N chars.
     * <p>If n is negative number, then return a string of the first N chars</p>
     * <p>If n is larger than the length of the string, then return the string</p>
     *
     * @param s the string from which the first `n` chars will be returned
     * @param n the number of chars to be returned from `s`
     * @return the string consists of the first `n` chars of the specified string `s`
     */
    public static String first(String s, int n) {
        if (n < 0) {
            return last(s, n * -1);
        }
        if (n >= s.length()) {
            return s;
        }
        return s.substring(0, n);
    }

    public static int len(String s1) {
        return null == s1 ? 0 : s1.length();
    }

    public static int len(String s1, String... sa) {
        int len = len(s1);
        for (String s : sa) {
            len += len(s);
        }
        return len;
    }

    /**
     * Split string by separator into two parts and return in a {@link T2} object
     *
     * **Note** this will only check the first position of the separator, anything after that will
     * be put into the second element of the `T2` instance, including the following separator char
     *
     * **Note** it will put the entire string into the first element of the return object if
     * no separator is found in the string and leave the second element to be an empty string
     *
     * @param string    the string to be split
     * @param separator the separator character
     * @return a `T2` instance contains the two parts
     */
    public static T2 binarySplit(String string, char separator) {
        int pos = string.indexOf(separator);
        if (pos < 0) {
            return new T2(string, "");
        }
        return new S.T2(string.substring(0, pos), string.substring(pos + 1, string.length()));
    }

    /**
     * Split string by separator into three parts and return in a {@link T3} object
     * <p>
     *
     * **Note** this will only check the first two positions of the separator, anything after that will
     * be put into the third element of the `T2` instance, including the following separator char
     * <p>
     *
     * **Note** it will put the entire string into the first element of the return object if
     * no separator is found in the string and leave the second element to be an empty string
     *
     * @param string    the string to be split
     * @param separator the separator character
     * @return a `T2` instance contains the two parts
     */
    public static T3 tripleSplit(String string, char separator) {
        int pos = string.indexOf(separator);
        if (pos < 0) {
            return new S.T3(string, "", "");
        }
        int pos2 = string.indexOf(separator, pos + 1);
        if (pos2 < 0) {
            return new S.T3(string.substring(0, pos), string.substring(pos + 1, string.length()), "");
        }
        return new S.T3(string.substring(0, pos), string.substring(pos + 1, pos2), string.substring(pos2 + 1, string.length()));
    }

    /**
     * Split a string by separator literal and return a list of strings
     *
     * Note：
     * * Unlike {@link String#split(String)} method, this will NOT do regex based split
     * * If there are consecutive separators they will be treated as a single separator
     * * Leading or ending separator will be trimmed
     * * If separator not found then the string will be returned in a single element list
     *
     * @param string    the string to be split
     * @param separator the string literal to split the string
     * @return a list of strings
     * @throws IllegalArgumentException if the separator is empty or `null`
     */
    public static List fastSplit(String string, String separator) {
        E.illegalArgumentIf(S.isEmpty(separator), "seperator must not be empty string or null");
        if (S.isEmpty(string)) {
            return ImmutableStringList.of(EMPTY_ARRAY);
        }
        ListBuilder<String> lb = ListBuilder.create();
        int lastPos = 0, gap = separator.length(), len = string.length();
        while (true) {
            int pos = string.indexOf(separator, lastPos);
            String part = string.substring(lastPos, pos < 0 ? len : pos);
            if (notEmpty(part)) {
                lb.add(part);
            }
            if (pos < 0) {
                break;
            }
            lastPos = pos + gap;
        }
        return ImmutableStringList.of(lb);
    }

    /**
     * Split a char sequence by regex and return a list of strings
     *
     * @param csq
     *      the target string to be split
     * @param regex
     *      the regex to split the string
     * @return
     *      a list of string
     */
    public static List split(CharSequence csq, String regex) {
        return isEmpty(csq) ? list() : listOf(csq.toString().split(regex));
    }

    /**
     * Split a char sequence by regex and return a list of strings
     *
     * @param csq
     *      the target string to be split
     * @param regex
     *      the regex pattern to split the string
     * @return
     *      a list of string
     */
    public static List split(CharSequence csq, Pattern regex) {
        return isEmpty(csq) ? list() : listOf(regex.split(csq));
    }

    /**
     * Split a string into a list of strings by specified separator char
     * <p>
     * * If there are consecutive separators they will be treated as a single separator
     * * Leading or ending separator will be trimmed
     * * If separator not found then the string will be returned in a single element list
     *
     * @param string    the string to be split
     * @param separator the char to split the string
     * @return a list of strings
     */
    public static List split(String string, char separator) {
        if (S.isEmpty(string)) {
            return ImmutableStringList.of(EMPTY_ARRAY);
        }
        ListBuilder<String> lb = ListBuilder.create();
        int lastPos = 0, len = string.length();
        while (true) {
            int pos = string.indexOf(separator, lastPos);
            String part = string.substring(lastPos, pos < 0 ? len : pos);
            if (notEmpty(part)) {
                lb.add(part);
            }
            if (pos < 0) {
                break;
            }
            lastPos = pos + 1;
            while (++lastPos < len && separator == string.charAt(lastPos)) {
            }
            lastPos--;
        }
        return ImmutableStringList.of(lb);
    }

    public static String concat(String s1, String s2) {
        return buffer().append(s1).append(s2).toString();
    }

    public static String concat(Object o1, Object o2) {
        return S.concat(string(o1), string(o2));
    }

    public static String concat(String s1, String s2, String s3) {
        return buffer().append(s1).append(s2).append(s3).toString();
    }

    public static String concat(Object o1, Object o2, Object o3) {
        return concat(string(o1), string(o2), string(o3));
    }

    public static String concat(String s1, String s2, String s3, String s4) {
        return buffer().append(s1).append(s2).append(s3).append(s4).toString();
    }

    public static String concat(Object o1, Object o2, Object o3, Object o4) {
        return concat(string(o1), string(o2), string(o3), string(o4));
    }

    public static String concat(String s1, String s2, String s3, String s4, String s5) {
        return S.buffer(s1).append(s2).append(s3).append(s4).append(s5)
                .toString();
    }

    public static String concat(Object o1, Object o2, Object o3, Object o4, Object o5) {
        return concat(string(o1), string(o2), string(o3), string(o4), string(o5));
    }

    public static String concat(String s1, String s2, String s3, String s4, String s5, String... extra) {
        S.Buffer sb = S.buffer(s1).a(s2).a(s3).a(s4).a(s5);
        for (String s : extra) {
            sb.a(s);
        }
        return sb.toString();
    }

    public static String concat(Object o1, Object o2, Object o3, Object o4, Object o5, Object... extra) {
        int len = extra.length;
        String[] sa = new String[len];
        for (int i = 0; i < len; ++i) {
            sa[i] = string(extra[i]);
        }
        return concat(string(o1), string(o2), string(o3), string(o4), string(o5), sa);
    }

    public static String concat(String[] sa) {
        int len = sa.length;
        S.Buffer buf = S.sizedBuffer(len * 8);
        for (int i = 0; i < len; ++i) {
            buf.a(sa[i]);
        }
        return buf.toString();
    }

    public static String concat(Object[] oa) {
        int len = oa.length;
        S.Buffer buf = S.sizedBuffer(len * 8);
        for (int i = 0; i < len; ++i) {
            buf.a(oa[i]);
        }
        return buf.toString();
    }

    public static class _Is {
        private String s;
        private _Is(Object object) {
            this.s = string(object);
        }
        public boolean empty() {
            return s.isEmpty();
        }
        public boolean blank() {
            return s.trim().isEmpty();
        }
        public boolean contains(CharSequence content) {
            return s.contains(content);
        }
        public boolean startsWith(String prefix, int toffset) {
            return s.startsWith(prefix, toffset);
        }
        public boolean startsWith(String prefix) {
            return s.startsWith(prefix);
        }
        public boolean endsWith(String suffix) {
            return s.endsWith(suffix);
        }
        public boolean equalsTo(CharSequence content) {
            return null == content ? isEmpty(s) : s.contentEquals(content);
        }
        public boolean numeric() {
            return N.isNumeric(s);
        }
        public boolean integer() {
            return N.isInt(s);
        }
        public boolean wrappedWith(String left, String right) {
            return s.length() >= (left.length() + right.length()) && s.startsWith(left) && s.endsWith(right);
        }
        public boolean wrappedWith($.Tuple<String, String> wrapper) {
            return wrappedWith(wrapper.left(), wrapper.right());
        }
    }

    public static _Is is(Object content) {
        return new _Is(content);
    }

    public static boolean endsWith(String string, String suffix) {
        return string(string).endsWith(suffix);
    }

    public static boolean endsWith(String string, char suffix) {
        String s = string(string);
        return !s.isEmpty() && s.charAt(string.length() - 1) == suffix;
    }

    public static boolean startsWith(String string, String prefix) {
        return string(string).startsWith(prefix);
    }

    public static boolean startsWith(String string, char prefix) {
        String s = string(string);
        return !s.isEmpty() && s.charAt(0) == prefix;
    }

    public static class _Ensure {
        private String s;
        private _Ensure(Object object) {
            this.s = string(object);
        }
        public String startWith(String prefix) {
            return ensureStartsWith(s, prefix);
        }
        public String startWith(char prefix) {
            return ensureStartsWith(s, prefix);
        }
        public String endWith(String suffix) {
            return ensureEndsWith(s, suffix);
        }
        public String endWith(char suffix) {
            return ensureEndsWith(s, suffix);
        }
        public String wrappedWith(String left, String right) {
            return ensureWrappedWith(s, left, right);
        }
        public String wrappedWith(String wrapper) {
            return ensureWrappedWith(s, wrapper, wrapper);
        }
        public String wrappedWith($.Tuple<String, String> wrapper) {
            return ensureWrappedWith(s, wrapper);
        }
        public String strippedOff(String left, String right) {
            return ensureStrippedOff(s, left, right);
        }
        public String strippedOff(String wrapper) {
            return ensureStrippedOff(s, wrapper, wrapper);
        }
        public String strippedOff($.Tuple<String, String> wrapper) {
            return ensureStrippedOff(s, wrapper);
        }
    }

    public static _Ensure ensure(Object object) {
        return new _Ensure(object);
    }

    public static String ensureStartsWith(String string, String prefix) {
        return startsWith(string, prefix) ? string : concat(prefix, string);
    }

    public static String ensureStartsWith(String string, char prefix) {
        return startsWith(string, prefix) ? string : newSizedBuffer(string.length() + 1).append(prefix).append(string).toString();
    }

    public static String ensureEndsWith(String string, String suffix) {
        return endsWith(string, suffix) ? string : concat(string, suffix);
    }

    public static String ensureEndsWith(String string, char suffix) {
        return endsWith(string, suffix) ? string : newSizedBuffer(string.length() + 1).append(string).append(suffix).toString();
    }

    public static String ensureWrappedWith(String string, String left, String right) {
        String retVal = string(string);
        if (!retVal.startsWith(left)) {
            retVal = left + retVal;
        }
        if (!retVal.endsWith(right)) {
            retVal = retVal + right;
        }
        return retVal;
    }

    public static String ensureWrappedWith(String string, $.Tuple<String, String> wrapper) {
        return ensureWrappedWith(string, wrapper.left(), wrapper.right());
    }

    public static String ensureStrippedOff(String string, String left, String right) {
        String retVal = string(string);
        if (retVal.startsWith(left)) {
            retVal = retVal.substring(left.length());
        }
        if (retVal.endsWith(right)) {
            retVal = retVal.substring(0, retVal.length() - right.length());
        }
        return retVal;
    }

    public static String ensureStrippedOff(String string, $.Tuple<String, String> wrapper) {
        return ensureStrippedOff(string, wrapper.left(), wrapper.right());
    }

    public static class _SplitStage {
        private CharSequence s;
        private String separator;
        private Pattern pattern;
        private boolean useRegex;
        private $.Tuple<String, String> elementWrapper;

        private _SplitStage(CharSequence s) {
            this.s = s;
        }

        public _SplitStage by(String separator) {
            if (useRegex) {
                this.pattern = Pattern.compile(separator);
            } else {
                this.separator = requireNotEmpty(separator);
            }
            return this;
        }

        public _SplitStage by(Pattern pattern) {
            this.pattern = $.requireNotNull(pattern);
            return this;
        }

        public _SplitStage stripElementWrapper($.Tuple<String, String> wrapper) {
            this.elementWrapper = $.requireNotNull(wrapper);
            return this;
        }

        public _SplitStage stripElementWrapper(String prefix, String suffix) {
            this.elementWrapper = pair(prefix, suffix);
            return this;
        }

        public _SplitStage useRegex() {
            useRegex = true;
            if (null == pattern && isNotEmpty(separator)) {
                pattern = Pattern.compile(separator);
            }
            return this;
        }

        public C.List<String> get() {
            if (S.isEmpty(s)) {
                return list();
            }
            E.illegalStateIf($.allNull(separator, pattern));
            C.List<String> list;
            if (useRegex && null == pattern) {
                pattern = Pattern.compile(separator);
                list = split(s, pattern);
            } else {
                list = fastSplit(s.toString(), separator);
            }
            if (null != elementWrapper) {
                list = list.map(F.strip(elementWrapper));
            }
            return list;
        }

    }

    public static _SplitStage split(CharSequence csq) {
        return new _SplitStage(csq);
    }


    public static class _Replace2 {
        private String replacement;
        private $.Function<String, String> replacementFunction;
        protected String keyword;
        protected Pattern pattern;
        private StringReplace replacer = OsglConfig.DEF_STRING_REPLACE;

        private _Replace2(String keyword, String replacement) {
            this.keyword = keyword;
            this.replacement = replacement;
        }

        private _Replace2(Pattern pattern, String replacement) {
            this.pattern = pattern;
            this.replacement = replacement;
        }

        private _Replace2(String keyword, $.Function<String, String> replacement) {
            this.keyword = keyword;
            this.replacementFunction = replacement;
        }

        public _Replace2 usingRegEx() {
            this.pattern = Pattern.compile(keyword);
            return this;
        }

        public _Replace2 replacer($.Func4<char[], char[], char[], Integer, char[]> replacer) {
            this.replacer = StringReplace.wrap(replacer);
            return this;
        }

        public String in(String text) {
            if (null != replacementFunction) {
                E.illegalStateIf(null != pattern, "Replace with function doesnot support regex search");
                replacement = replacementFunction.apply(keyword);
            }
            if (null != pattern) {
                return pattern.matcher(text).replaceAll(replacement);
            }
            if (text.length() < keyword.length()) {
                return text;
            }
            int firstId = text.indexOf(this.keyword);
            if (firstId < 0) {
                return text;
            }
            char[] textArray = text.toCharArray();
            char[] target = this.keyword.toCharArray();
            char[] replace = replacement.toCharArray();
            char[] result = this.replacer.replace(textArray, target, replace, firstId);
            return (result == textArray) ? text : new String(result);
        }
    }

    public static class _Replace {
        private String text;
        protected String keyword;
        protected Pattern pattern;
        private StringReplace replacer = OsglConfig.DEF_STRING_REPLACE;

        private _Replace(String text, String keyword) {
            this.text = text;
            this.keyword = keyword;
        }

        private _Replace(String text, Pattern pattern) {
            this.text = text;
            this.pattern = pattern;
        }

        public _Replace usingRegEx() {
            this.pattern = Pattern.compile(keyword);
            return this;
        }

        public _Replace replacer($.Func4<char[], char[], char[], Integer, char[]> replacer) {
            this.replacer = StringReplace.wrap(replacer);
            return this;
        }

        public String with($.Function<String, String> replacement) {
            E.illegalStateIf(null != pattern, "Replace with function doesnot support regex search");
            return with(replacement.apply(this.keyword));
        }

        public String with(String replacement) {
            if (null != pattern) {
                return pattern.matcher(text).replaceAll(replacement);
            } else {
                if (text.length() < keyword.length()) {
                    return text;
                }
                int firstId = this.text.indexOf(this.keyword);
                if (firstId < 0) {
                    return this.text;
                }
                char[] text = this.text.toCharArray();
                char[] target = this.keyword.toCharArray();
                char[] replace = replacement.toCharArray();
                char[] result = this.replacer.replace(text, target, replace, firstId);
                return (result == text) ? this.text : new String(result);
            }
        }
    }

    public static class _WrapReplace extends _Replace {
        public _WrapReplace(String text, String keyword) {
            super(text, keyword);
        }
        public String with($.Tuple<String, String> wrapper) {
            return with(F.wrapper(wrapper));
        }

        public String with(String left, String right) {
            return with(F.wrapper(left, right));
        }

        public String with($.Function<String, String> replacement) {
            E.illegalStateIf(null != pattern, "Replace with function doesnot support regex search");
            return super.with(replacement.apply(this.keyword));
        }

        public String with(String wrapper) {
            return with(wrapper, wrapper);
        }
    }

    public static class _ReplaceChar {
        private String text;
        private char toBeReplaced;
        private _ReplaceChar(String s, char toBeReplaced) {
            this.text = string(s);
            this.toBeReplaced = toBeReplaced;
        }
        public String with(char replacement) {
            return text.replace(toBeReplaced, replacement);
        }
    }

    public static class _ReplaceCharStage {
        private char search;
        private char replacement;

        public _ReplaceCharStage(char search) {
            this.search = search;
        }

        public _ReplaceCharStage with(char replacement) {
            this.replacement = replacement;
            return this;
        }

        public String in(String text) {
            return text.replace(search, replacement);
        }
    }

    public static class _ReplaceStage {
        private String keyword;
        private Pattern pattern;
        private _ReplaceStage(String keyword) {
            this.keyword = keyword;
        }
        private _ReplaceStage(Pattern pattern) {
            this.pattern = pattern;
        }
        public _Replace in(String text) {
            return null == pattern ? new _Replace(text, keyword) : new _Replace(text, pattern);
        }
        public _Replace2 with(String replacement) {
            return null == pattern ? new _Replace2(keyword, replacement) : new _Replace2(pattern, replacement);
        }
    }

    public static class _Have {
        private String s;
        private _Have(Object s) {
            this.s = string(s);
        }
        private _Have(char[] ca) {
            this.s = new String(ca);
        }
        private _Have(String s) {
            this.s = null == s ? "" : s;
        }
        public _Replace replace(String literal) {
            return new _Replace(s, literal);
        }
        public _Replace replace(Pattern pattern) {
            return new _Replace(s, pattern);
        }
        public _ReplaceChar replace(char c) {
            return new _ReplaceChar(s, c);
        }
    }

    public static _Have have(char[] ca) {
        return new _Have(ca);
    }

    public static _Have have(Object o) {
        return new _Have(o);
    }

    public static _Have have(String s) {
        return new _Have(s);
    }

    public static _Have take(Object o) {
        return new _Have(o);
    }

    public static _Have take(String s) {
        return new _Have(s);
    }

    public static _Have given(Object o) {
        return new _Have(o);
    }

    public static _Have given(String s) {
        return new _Have(s);
    }

    public static _ReplaceStage replace(Pattern pattern) {
        return new _ReplaceStage(pattern);
    }

    public static _ReplaceStage replace(Object keyword) {
        if (keyword instanceof Pattern) {
            return new _ReplaceStage((Pattern) keyword);
        }
        return new _ReplaceStage(string(keyword));
    }

    public static _ReplaceStage replace(String keyword) {
        return new _ReplaceStage(null == keyword ? "" : keyword);
    }

    public static _ReplaceCharStage replace(char c) {
        return new _ReplaceCharStage(c);
    }

    public static String pathConcat(String prefix, char sep, String suffix) {
        boolean prefixHasSep = endsWith(prefix, sep);
        boolean suffixHasSep = startsWith(suffix, sep);
        int prefixLen = len(prefix), suffixLen = len(suffix);
        int len = prefixLen + suffixLen + 1;
        S.Buffer buffer = sizedBuffer(len).append(prefix);
        if (prefixHasSep && suffixHasSep) {
            return buffer.deleteCharAt(prefixLen - 1).append(suffix).toString();
        } else if (prefixHasSep || suffixHasSep) {
            return buffer.append(suffix).toString();
        } else {
            return buffer.append(sep).append(suffix).toString();
        }
    }

    /**
     * Join a list of object into a string
     *
     * @param separator the symbol used to separate the listed itmes
     * @param iterable  a list of object instances
     * @return a string representation of the listed objects
     */
    public static String join(String separator, Iterable<?> iterable) {
        return join(separator, null, null, iterable);
    }

    /**
     * Join a list of object into a string, prefix and suffix will be added if supplied
     *
     * @param separator the symbols used to separate the listed items and prefix and suffix
     * @param prefix    the symbols prepended to the beginning of the item list
     * @param suffix    the symbols appended to the end of the item list
     * @param iterable  an iterable
     * @return a string representation of the listed objects
     */
    public static String join(String separator, String prefix, String suffix,
                              Iterable<?> iterable
    ) {
        return join(separator, prefix, suffix, iterable, false, true);
    }

    /**
     * Join a list of object into a string, prefix and suffix will be added if supplied
     *
     * @param separator     the symbols used to separate the listed items and prefix and suffix
     * @param prefix        the symbols prepended to the beginning of the item list
     * @param suffix        the symbols appended to the end of the item list
     * @param iterable      an iterable
     * @param quoted        if true then each listed item will be quoted with quotation mark
     * @param separateFixes if false then no separator after prefix and no separator before suffix
     * @return a string representation of the listed objects
     */
    public static String join(String separator, String prefix, String suffix,
                              Iterable<?> iterable, boolean quoted, boolean separateFixes
    ) {
        S.Buffer sb = buffer();

        if (null != prefix) {
            sb.append(prefix);
            if (separateFixes)
                sb.append(separator);
        }

        Iterator<?> itr = iterable.iterator();

        if (itr.hasNext()) {
            sb.append(string(itr.next(), quoted));
        }

        while (itr.hasNext()) {
            sb.append(separator).append(string(itr.next(), quoted));
        }

        if (null != suffix) {
            if (separateFixes)
                sb.append(separator);
            sb.append(suffix);
        }
        return sb.toString();
    }

    public static _IterableJoiner join(Iterable<?> iterable) {
        return new _IterableJoiner(iterable);
    }

    public static _IterableJoiner join(byte[] array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static _IterableJoiner join(short[] array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static _IterableJoiner join(int[] array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static _IterableJoiner join(long[] array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static _IterableJoiner join(float[] array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static _IterableJoiner join(double[] array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static _IterableJoiner join(Object ... array) {
        return new _IterableJoiner(C.listOf(array));
    }

    public static class _IterableJoiner {
        private Iterable<?> iterable;
        private String separator;
        private String prefix;
        private String suffix;
        private $.Tuple<String, String> wrapper;
        private $.Predicate<String> filter;
        private boolean separateFix;
        private _IterableJoiner(Iterable<?> iterable) {
            this.iterable = $.requireNotNull(iterable);
        }

        public _IterableJoiner by(String separator) {
            this.separator = separator;
            return this;
        }

        public _IterableJoiner withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public _IterableJoiner withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public _IterableJoiner wrapElementWith(String wrapper) {
            this.wrapper = binary(wrapper, wrapper);
            return this;
        }

        public _IterableJoiner wrapElementWith(String left, String right) {
            this.wrapper = binary(left, right);
            return this;
        }

        public _IterableJoiner wrapElementWith($.Tuple<String, String> wrapper) {
            this.wrapper = wrapper;
            return this;
        }

        public _IterableJoiner filter($.Predicate<String> filter) {
            this.filter = $.requireNotNull(filter);
            return this;
        }

        public _IterableJoiner ignoreEmptyElement() {
            this.filter = F.NOT_EMPTY;
            return this;
        }

        public _IterableJoiner separatorWithPrefixAndSuffix() {
            this.separateFix = true;
            return this;
        }

        public String get() {
            return toString();
        }

        public String join() {
            return toString();
        }

        @Override
        public String toString() {
            S.Buffer sb = buffer();

            if (null != prefix) {
                sb.append(prefix);
                if (separateFix)
                    sb.append(separator);
            }

            Iterator<?> itr = iterable.iterator();

            if (itr.hasNext()) {
                append(sb, null, itr.next(), wrapper, filter);
            }

            while (itr.hasNext()) {
                append(sb, separator, itr.next(), wrapper, filter);
            }

            if (null != suffix) {
                if (separateFix) {
                    sb.append(separator);
                }
                sb.append(suffix);
            }
            return sb.toString();
        }

        private void append(S.Buffer sb, String separator, Object o, $.Tuple<String, String> wrapper, $.Predicate<String> filter) {
            String s = null == o ? null : o.toString();
            if (null != filter && !filter.test(s)) {
                return;
            }
            if (null != wrapper) {
                s = wrap(s, wrapper._1, wrapper._2);
                if (null != filter && !filter.test(s)) {
                    return;
                }
            }
            if (null != separator) {
                sb.append(separator);
            }
            sb.append(s);
        }

    }

    public static _StringRepeater repeat(String content) {
        return new _StringRepeater(content);
    }

    /**
     * Join an array of strings into a string
     *
     * @param separator the symbol used to separate the listed itmes
     * @param list      the array of strings
     * @return a string joined
     */
    public static String join(String separator, String... list) {
        S.Buffer sb = buffer();

        if (list.length > 0) {
            sb.append(list[0]);
            for (int i = 1; i < list.length; ++i)
                sb.append(separator).append(list[i]);
        }

        return sb.toString();
    }

    public static class _StringRepeater {
        private String content;
        private String separator;
        private $.Tuple<String, String> wrapper;
        private _StringRepeater(String content) {
            this.content = content;
        }
        public _StringRepeater joinedBy(String separator) {
            this.separator = separator;
            return this;
        }
        public _StringRepeater wrapWith($.Tuple<String, String> wrapper) {
            this.wrapper = wrapper;
            return this;
        }
        public _StringRepeater wrapWith(String wrapper) {
            this.wrapper = binary(wrapper, wrapper);
            return this;
        }
        public _StringRepeater wrapWith(String left, String right) {
            this.wrapper = binary(left, right);
            return this;
        }
        public String times(int times) {
            String content = null == wrapper ? this.content : wrap(this.content, wrapper);
            return null == separator ? S.times(content, times) : S.join(separator, content, times);
        }
        public String x(int times) {
            return times(times);
        }
        public String forOneTime() {
            return content;
        }
        public String forTwoTimes() {
            return x(2);
        }
        public String forThreeTimes() {
            return x(3);
        }
        public String forFourTimes() {
            return x(4);
        }
        public String forFiveTimes() {
            return x(5);
        }
        public String forTimes(int times) {
            return x(times);
        }
    }

    /**
     * Join a string by separator for n times
     *
     * @param separator the separator
     * @param s         the string to be joined
     * @param times     the times the string to be joined
     * @return the result
     */
    public static String join(String separator, String s, int times) {
        E.illegalArgumentIf(times < 0, "times must not be negative");
        switch (times) {
            case 0:
                return "";
            case 1:
                return s;
            default:
                int slen = s.length();
                int len = (slen + len(separator)) * times;
                StringBuilder sb = len > 100 ? builder() : newSizedBuilder(len);
                sb.append(s);
                for (int i = 1; i < times; ++i) {
                    sb.append(separator).append(s);
                }
                return sb.toString();
        }
    }

    /**
     * Join a string for n times
     *
     * @param s     the string to be joined
     * @param times the times the string to be joined
     * @return the result
     */
    public static String join(String s, int times) {
        E.illegalArgumentIf(times < 0, "times must not be negative");
        switch (times) {
            case 0:
                return "";
            case 1:
                return s;
            default:
                int slen = s.length();
//                if (1 == slen) {
//                    return times(s.charAt(0), times);
//                }
                int len = slen * times;
                char[] src = s.toCharArray();
                char[] sink = new char[len];
                for (int i = 0; i < times; ++i) {
                    System.arraycopy(src, 0, sink, i * slen, slen);
                }
                return new String(sink);
        }
    }

    /**
     * Alias of {@link #join(String, int)}
     *
     * @param s     the string to be joined
     * @param times the times the string to be joined
     * @return the result
     */
    public static String times(String s, int times) {
        return join(s, times);
    }

    public static _CharRepeater repeat(char c) {
        return new _CharRepeater(c);
    }

    public static class _CharRepeater {
        private char c;
        private _CharRepeater(char c) {
            this.c = c;
        }
        public String times(int times) {
            return S.times(c, times);
        }
        public String x(int times) {
            return S.times(c, times);
        }
        public String forOneTime() {
            return String.valueOf(c);
        }
        public String forTwoTimes() {
            return x(2);
        }
        public String forThreeTimes() {
            return x(3);
        }
        public String forFourTimes() {
            return x(4);
        }
        public String forFiveTimes() {
            return x(5);
        }
        public String forTimes(int times) {
            return x(times);
        }
    }

    /**
     * Return a string composed of `times` of char `c`
     *
     * @param c     the character
     * @param times the number of times the c in the string returned
     * @return the string as described
     */
    public static String times(char c, int times) {
        E.illegalArgumentIf(times < 0);
        if (0 == times) {
            return "";
        }
        char[] ca = new char[times];
        for (int i = 0; i < times; ++i) {
            ca[i] = c;
        }
        return new String(ca);
    }

    public static class _WrapStringBuilder {
        private String content;
        private _WrapStringBuilder(String content) {
            this.content = content;
        }
        private _WrapStringBuilder(Object content) {
            this.content = string(content);
        }

        public String with(String wrapper) {
            return wrap(content, wrapper);
        }

        public String with(String left, String right) {
            return wrap(content, left, right);
        }

        public String with($.Tuple<String, String> wrapper) {
            return wrap(content, wrapper);
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static _WrapStringBuilder wrap(Object content) {
        return new _WrapStringBuilder(string(content));
    }

    public static _WrapStringBuilder wrap(String content) {
        return new _WrapStringBuilder(content);
    }

    public static String wrap(Object content, char symbol) {
        return wrap(content, symbol, symbol);
    }

    public static String wrap(String text, char symbol) {
        return wrap(text, symbol, symbol);
    }

    public static String wrap(Object content, char left, char right) {
        return wrap(string(content), left, right);
    }

    public static String wrap(String text, char left, char right) {
        if (null == text) {
            return String.valueOf(new char[]{left, right});
        }
        int textLen = text.length();
        char[] ca = new char[textLen + 2];
        ca[0] = left; ca[textLen + 1] = right;
        System.arraycopy(text.toCharArray(), 0, ca, 1, textLen);
        return String.valueOf(ca);
    }

    public static String wrap(Object content, String left, String right) {
        return wrap(string(content), left, right);
    }

    public static String wrap(String text, String left, String right) {
        return S.concat(left, text, right);
    }

    public static String wrap(Object content, $.Tuple<String, String> wrapper) {
        return concat(wrapper._1, content, wrapper._2);
    }

    public static String wrap(String text, $.Tuple<String, String> wrapper) {
        return concat(wrapper._1, text, wrapper._2);
    }

    public static String wrap(Object content, String wrapper) {
        return wrap(string(content), wrapper);
    }

    public static String wrap(String text, String wrapper) {
        return quote(text, wrapper);
    }

    /**
     * This method is deprecated. Please use {@link #wrap(Object, char)}
     * instead
     *
     * @param content the content object
     * @param mark the quotation mark
     * @return a string that wrap the content string with quotation mark
     */
    @Deprecated
    public static String quote(Object content, char mark) {
        return quote(string(content), mark);
    }

    /**
     * This method is deprecated. Please use {@link #wrap(String, char)}
     * instead
     *
     * @param content the content object
     * @param mark the quotation mark
     * @return a string that wrap the content string with quotation mark
     */
    @Deprecated
    public static String quote(String content, char mark) {
        if (null == content) {
            return String.valueOf(new char[]{mark, mark});
        }
        return S.sizedBuffer(content.length() + 2).append(mark).append(content).append(mark).toString();
    }

    /**
     * This method is deprecated. Please use {@link #wrap(Object, char)}
     * instead
     *
     * @param content the content object
     * @param mark the quotation mark
     * @return a string that wrap the content string with quotation mark
     */
    @Deprecated
    public static String quote(Object content, String mark) {
        return quote(string(content), mark);
    }

    /**
     * This method is deprecated. Please use {@link #wrap(String, char)}
     * instead
     *
     * @param s the content string
     * @param mark the quotation mark
     * @return a string that wrap the content string with quotation mark
     */
    @Deprecated
    public static String quote(String s, String mark) {
        if (null == s) {
            return times(mark, 2);
        }
        return S.concat(mark, s, mark);
    }

    public static class _Cut {
        private String s;
        private _Cut(Object object) {
            s = string(object);
        }
        public String by(int chars) {
            return first(chars);
        }
        public String first(int chars) {
            return s.substring(0, chars);
        }
        public String last(int chars) {
            return S.last(s, chars);
        }
        public String before(String search) {
            return S.before(s, search);
        }
        public String beforeFirst(String search) {
            return S.before(s, search, false);
        }
        public String beforeLast(String search) {
            return S.before(s, search, true);
        }
        public String after(String search) {
            return S.after(s, search);
        }
        public String afterFirst(String search) {
            return S.after(s, search, true);
        }
        public String afterLast(String search) {
            return S.after(s, search, false);
        }
    }

    public static _Cut cut(Object object) {
        return new _Cut(object);
    }

    /**
     * <p>Return a string no longer than specified max length.
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param s   the original string
     * @param max the maximum length of the result
     * @return the string as described
     */
    public static String cutOff(String s, int max) {
        return maxLength(s, max);
    }

    /**
     * <p>Return a string no longer than specified max length.
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param s   the original string
     * @param max the maximum length of the result
     * @return the string as described
     */
    public static String maxLength(String s, int max) {
        if (null == s)
            return "";
        if (s.length() < (max - 3))
            return s;
        String s0 = s.substring(0, max);
        return s0 + "...";
    }

    /**
     * Return last n chars
     *
     * @param s the string from which last `n` chars will be returned
     * @param n the number of chars should be returned from `s`
     * @return a string consists of the last `n` chars in `s`
     */
    public static String last(String s, int n) {
        if (n < 0) {
            return first(s, n * -1);
        }
        int len = s.length();
        if (n >= len) {
            return s;
        }
        return s.substring(len - n, s.length());
    }

    public static String after(String s0, String search) {
        return after(s0, search, false);
    }

    public static String after(String s0, String search, boolean first) {
        if (first) {
            return afterFirst(s0, search);
        } else {
            return afterLast(s0, search);
        }
    }

    public static String afterLast(String s0, String search) {
        if (null == s0) {
            return "";
        }
        int i = s0.lastIndexOf(search);
        if (i == -1) {
            return "";
        }
        return s0.substring(i + search.length());
    }

    public static String afterFirst(String s0, String search) {
        if (null == s0) {
            return "";
        }
        int i = s0.indexOf(search);
        if (i == -1) {
            return "";
        }
        return s0.substring(i + search.length());
    }

    public static String before(String s0, String search) {
        return before(s0, search, false);
    }

    public static String before(String s0, String search, boolean last) {
        if (last) {
            return beforeLast(s0, search);
        } else {
            return beforeFirst(s0, search);
        }
    }

    public static String beforeFirst(String s0, String search) {
        if (null == s0) {
            return "";
        }
        int i = s0.indexOf(search);
        if (i == -1) {
            return "";
        }
        return s0.substring(0, i);
    }

    public final static String beforeLast(String s0, String search) {
        if (null == s0) {
            return "";
        }
        int i = s0.lastIndexOf(search);
        if (i == -1) {
            return "";
        }
        return s0.substring(0, i);
    }

    /**
     * Null safety trim
     *
     * @param s the string to be trimed
     * @return the trimed string
     */
    public static String trim(String s) {
        return null == s ? "" : s.trim();
    }

    public static class _CountStage {
        private String search;
        private boolean overlap;
        private _CountStage(String search) {
            this.search = requireNotEmpty(search);
        }
        public _CountStage withOverlap() {
            this.overlap = true;
            return this;
        }
        public int in(String text) {
            return count(text, search, overlap);
        }
    }

    public static _CountStage count(String search) {
        return new _CountStage(search);
    }

    /**
     * Count how many times a search string occurred in the give string
     *
     * @param s      string to be searched
     * @param search the search token
     * @return the times the search token appeared in `s` without overlap calculation
     */
    public static int count(String s, String search) {
        return count(s, search, false);
    }

    /**
     * Count how many times a search string occurred in the give string
     *
     * @param s       string to be searched
     * @param search  the search token
     * @param overlap specify if it should take overlap into considerations
     * @return the times the search token appeared in `s`
     */
    public static int count(String s, String search, boolean overlap) {
        int n = 0, l = search.length();
        while (true) {
            int i = s.indexOf(search);
            if (-1 == i) {
                return n;
            }
            n++;
            if (overlap) {
                s = s.substring(i + 1);
            } else {
                s = s.substring(i + l);
            }
        }
    }

    public static String camelCase(CharSequence s) {
        return Keyword.of(s).camelCase();
    }

    public static String underscore(CharSequence s) {
        return Keyword.of(s).underscore();
    }

    public static String dashed(CharSequence s) {
        return Keyword.of(s).dashed();
    }

    public static String hyphenated(CharSequence s) {
        return dashed(s);
    }

    public static String dotted(CharSequence s) {
        return Keyword.of(s).dotted();
    }

    public static String acronym(CharSequence s) {
        return Keyword.of(s).acronym();
    }

    public static String capFirst(String s) {
        if (null == s || "" == s) {
            return "";
        }
        return ("" + s.charAt(0)).toUpperCase() + s.substring(1);
    }

    public static String lowerFirst(String s) {
        if (null == s || "" == s) {
            return "";
        }
        return ("" + s.charAt(0)).toLowerCase() + s.substring(1);
    }

    public static String unsafeCapFirst(String s) {
        if (null == s) {
            return "";
        }
        try {
            char[] buf = s.toCharArray();
            char[] newBuf = unsafeCapFirst(buf, 0, buf.length);
            if (newBuf == buf) return s;
            return new String(newBuf);
        } catch (Exception e) {
            return capFirst(s);
        }
    }

    /**
     * Convert the char at begin position in the buf to upper case.
     * <p>
     * If char is already upper case, then it returns the buf directly, otherwise,
     * it returns an new char array copy from begin to end
     *
     * @param buf
     * @param begin start inclusive
     * @param end   stop exclusive
     * @return
     */
    static char[] unsafeCapFirst(char[] buf, int begin, int end) {
        int sz = end - begin;
        if (begin == end) return buf;
        char c = buf[begin];
        if (Character.isUpperCase(c)) {
            return buf;
        }
        char[] newBuf = new char[sz];
        newBuf[begin] = Character.toUpperCase(c);
        if (sz == begin + 1) return newBuf;
        System.arraycopy(buf, begin + 1, newBuf, 1, sz - begin - 1);
        return newBuf;
    }

    /**
     * Returns the character (Unicode code point) at the specified
     * index. The index refers to <code>char</code> values
     * (Unicode code units) and ranges from <code>0</code> to
     * length<code> - 1</code>.
     * <p> If the <code>char</code> value specified at the given index
     * is in the high-surrogate range, the following index is less
     * than the length of this <code>String</code>, and the
     * <code>char</code> value at the following index is in the
     * low-surrogate range, then the supplementary code point
     * corresponding to this surrogate pair is returned. Otherwise,
     * the <code>char</code> value at the given index is returned.
     *
     * @param value the char buf
     * @param index the index to the <code>char</code> values
     * @return the code point value of the character at the
     * <code>index</code>
     * @throws IndexOutOfBoundsException if the <code>index</code>
     *                                   argument is negative or not less than the length of this
     *                                   string.
     * @since 1.5
     */
    static final int codePointAt(char[] value, int index) {
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return codePointAtImpl(value, index, value.length);
    }

    private static int codePointAtImpl(char[] a, int index, int limit) {
        char c1 = a[index++];
        if (Character.isHighSurrogate(c1)) {
            if (index < limit) {
                char c2 = a[index];
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
            }
        }
        return c1;
    }


    /**
     * equal modifier: specify {@link #equal(String, String, int) equal} comparison
     * should ignore leading and after spaces. i.e. it will call <code>trim()</code>
     * method on strings before comparison
     */
    public static final int IGNORECASE = 0x00001000;

    /**
     * equal modifier: specify {@link #equal(String, String, int) equal} comparison
     * should be case insensitive
     */
    public static final int IGNORESPACE = 0x00002000;

    /**
     * alias of {@link #equal(String, String)}
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean eq(String s1, String s2) {
        return equal(s1, s2, 0);
    }

    /**
     * Alias of {@link #equal(String, String, int)}
     *
     * @param s1       string 1
     * @param s2       String 2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return `true` if `s1` equals to `s2` according to `modifier`
     */
    public static boolean eq(String s1, String s2, int modifier) {
        return equal(s1, s2, modifier);
    }

    /**
     * Antonym of {@link #equal(String, String)}
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 doesn't equal to s2
     */
    public static boolean neq(String s1, String s2) {
        return !equal(s1, s2);
    }

    /**
     * Antonym of {@link #equal(String, String, int)}
     *
     * @param s1       string 1
     * @param s2       string 2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return <code>true</code> if s1 doesn't equal to s2 as per modifier
     */
    public static boolean neq(String s1, String s2, int modifier) {
        return !equal(s1, s2, modifier);
    }

    /**
     * Return true if 2 strings are equals to each other without
     * ignore space and case sensitive.
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean equal(String s1, String s2) {
        return equal(s1, s2, 0);
    }

    /**
     * Return false if 2 strings are equals to each other
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return `true` if s1 does not equal to s2
     */
    public static boolean notEqual(String s1, String s2) {
        return !equal(s1, s2, 0);
    }

    /**
     * alias of {@link #eq(String, String)}
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    /**
     * Return true if 2 strings are equals to each other as per rule specified
     *
     * @param s1       string 1
     * @param s2       String 2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return `true` if `s1` equals to `s2` according to `modifier`
     */
    public static boolean equal(String s1, String s2, int modifier) {
        if (null == s1) {
            return s2 == null;
        }
        if (null == s2)
            return false;
        if ((modifier & IGNORESPACE) != 0) {
            s1 = s1.trim();
            s2 = s2.trim();
        }
        if ((modifier & IGNORECASE) != 0) {
            return s1.equalsIgnoreCase(s2);
        } else {
            return s1.equals(s2);
        }
    }

    /**
     * Check if all strings are equal to each other
     *
     * @param modifier specify whether ignore space or case sensitive
     * @param sa       the list of strings
     * @return <code>true</code> if all strings are equal to each other as per modifier specified
     */
    public static boolean equal(int modifier, String... sa) {
        int len = sa.length;
        if (len < 2) {
            throw new IllegalArgumentException("At least 2 strings required");
        }
        String s = sa[0];
        for (int i = 1; i < len; ++i) {
            String s1 = sa[i];
            if (!equal(s, s1, modifier)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Alias of {@link #equal(String, String, int)}
     *
     * @param s1       string 1
     * @param s2       string 2
     * @param modifier the modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return <code>true</code> if s1 equals to s2 as per modifier
     */
    public static boolean isEqual(String s1, String s2, int modifier) {
        return equal(s1, s2, modifier);
    }

    public static class _StripStage {
        private String content;
        private _StripStage(Object o) {
            content = S.string(o);
        }

        public String of($.Tuple<String, String> wrapper) {
            return strip(content, wrapper);
        }

        public String of(String left, String right) {
            return strip(content, left, right);
        }

        public String of(String wrapper) {
            return strip(content, wrapper, wrapper);
        }

    }

    public static _StripStage strip(Object o) {
        return new _StripStage(o);
    }

    /**
     * Strip the prefix and suffix from an object's String representation and
     * return the result
     *
     * For example:
     *
     * ```java
     * Object o = "xxBByy";
     * String s = S.strip(o, "xx", "yy")
     * ```
     *
     * At the end above code, `s` should be "BB"
     *
     * @param o      the object to which string representation will be used
     * @param prefix the prefix
     * @param suffix the suffix
     * @return the String result as described above
     */
    public static String strip(Object o, String prefix, String suffix) {
        if (null == o) {
            return "";
        }
        String s = o.toString();
        s = s.trim();
        if (s.startsWith(prefix)) s = s.substring(prefix.length());
        if (s.endsWith(suffix)) s = s.substring(0, s.length() - suffix.length());
        return s;
    }

    /**
     * Strip the prefix and suffix of an Object's string representation and return
     * the result.
     *
     * @param o
     *      the object to which string representation will be used
     * @param wrapper
     *      a pair of prefix and suffix
     * @return
     *      the String result as described above.
     * @see #strip(Object, String, String)
     */
    public static String strip(Object o, $.Tuple<String, String> wrapper) {
        return strip(o, wrapper.left(), wrapper.right());
    }

    /**
     * Add leading zero to number
     * @param number
     *      the number to which leading zero to be padded
     * @param digits
     *      the number of digits of the result string, max number is 20.
     * @return
     *      a String with leading zero which has `digits` of digits
     */
    public static String padLeadingZero(int number, int digits) {
        if (digits < 2) {
            return Integer.toString(number);
        }
        if (digits > 9) {
            long l = N.powOfTenLong(digits);
            if (l > number) {
                return Long.toString(l + number).substring(1);
            }
            return Integer.toString(number);
        } else {
            int i = N.powOfTen(digits);
            if (i > number) {
                return Integer.toString(i + number).substring(1);
            }
            return Integer.toString(number);
        }
    }

    public static String padLeadingZero(long number, int digits) {
        E.illegalArgumentIf(digits > 20);
        if (digits < 2) {
            return S.string(number);
        }
        if (digits > 9) {
            long l = N.powOfTenLong(digits);
            if (l > number) {
                return Long.toString(l + number).substring(1);
            }
            return String.valueOf(number);
        } else {
            int i = N.powOfTen(digits);
            if (i > number) {
                return String.valueOf(i + number).substring(1);
            }
            return Long.toString(number);
        }
    }

    /**
     * Left pad a string with character specified
     *
     * @param s      the string
     * @param c      the character
     * @param number the number of character to pad to the left
     * @return an new string with specified number of character `c` padded to `s` at left
     */
    public static String padLeft(String s, char c, int number) {
        return S.concat(S.times(c, number), s);
    }

    /**
     * Left pad a string with number of space specified
     *
     * @param s      the string
     * @param number the number of space to left pad to `s`
     * @return the string as described
     */
    public static String padLeft(String s, int number) {
        return padLeft(s, ' ', number);
    }

    /**
     * Alias of {@link #padLeft(String, char, int)}
     *
     * @param s      the string
     * @param c      the char
     * @param number number of char to be left pad to `s`
     * @return the string as described above
     */
    public static String lpad(String s, char c, int number) {
        return padLeft(s, c, number);
    }

    /**
     * Alias of {@link #padLeft(String, int)}
     *
     * @param s      the string
     * @param number the number of space to left pad to `s`
     * @return the string as described
     */
    public static String lpad(String s, int number) {
        return padLeft(s, ' ', number);
    }

    /**
     * Right pad a string with character specified
     *
     * @param s      the string
     * @param c      the character
     * @param number the number of character to pad to the left
     * @return an new string with specified number of character `c` padded to `s` at right
     */
    public static String padRight(String s, char c, int number) {
        return S.concat(s, S.times(c, number));
    }

    /**
     * Right pad a string with number of space specified
     *
     * @param s      the string
     * @param number the number of space to right pad to `s`
     * @return the string as described
     */
    public static String padRight(String s, int number) {
        return padRight(s, ' ', number);
    }

    /**
     * Alias of {@link #padRight(String, char, int)}
     *
     * @param s      the string
     * @param c      the char
     * @param number number of char to be left pad to `s`
     * @return the string as described above
     */
    public static String rpad(String s, char c, int number) {
        return padLeft(s, c, number);
    }

    /**
     * Alias of {@link #padRight(String, int)}
     *
     * @param s      the string
     * @param number the number of space to left pad to `s`
     * @return the string as described
     */
    public static String rpad(String s, int number) {
        return padLeft(s, ' ', number);
    }

    /**
     * Centers a String in a larger String of size size using the space character (' ').
     *
     * If the size is less than the String length, the String is returned.
     * A null String is treated as empty string `""`.
     * A negative size is treated as zero.
     *
     * Equivalent to `center(str, size, " ")`.
     *
     * @param s
     *      the string to center
     * @param length
     *      the size of new string
     * @return centered String
     */
    public static String center(String s, int length) {
        return center(s, length, ' ');
    }

    /**
     * Centers a String in a larger String of size size.
     *
     * Uses a supplied character as the value to pad the String with.
     *
     * If the size is less than the String length, the String is returned.
     * A `null` String is treated as empty string`""`.
     * A negative size is treated as zero.
     *
     * @param s
     *      The string to center
     * @param length
     *      The size of the new string
     * @param padChar
     *       the character to pad the new String with
     * @return centered String as described above
     */
    public static String center(String s, int length, char padChar) {
        if (null == s) {
            s = "";
        }
        if (length < 0) {
            length = 0;
        }
        int sLen = s.length();
        if (sLen >= length) {
            return s;
        }
        int left = (length - sLen) / 2;
        int right = length - sLen - left;
        return S.concat(S.times(padChar, left), s, S.times(padChar, right));
    }

    /**
     * Reverse a String.
     * @param s
     *      the string to be reversed.
     * @return reversed string
     */
    public static String reversed(String s) {
        return S.buffer(s).reverse().toString();
    }

    /**
     * Decode Base64 encoded string
     *
     * @param str the string to be decoded
     * @return decoded string
     */
    public static String decodeBASE64(String str) {
        return new String(Codec.decodeBase64(str), Charsets.UTF_8);
    }

    public static String decodeBase64(String string) {
        return decodeBASE64(string);
    }

    /**
     * Encode a string using Base64 encoding
     *
     * @param str the string to be encoded
     * @return encoded string
     */
    public static String encodeBASE64(String str) {
        return Codec.encodeBase64(str);
    }

    public static String encodeBase64(String str) {
        return encodeBASE64(str);
    }

    /**
     * perform URL encoding on a giving string
     *
     * @param s the string to be encoded
     * @return return encoded string
     */
    public static String urlEncode(String s) {
        if (null == s) {
            return "";
        }
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public static String urlDecode(String s) {
        if (null == s) {
            return "";
        }
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public static String dos2unix(String s) {
        return s.replace("\n\r", "\n");
    }

    public static String unix2dos(String s) {
        return dos2unix(s).replace("\n", "\n\r");
    }

    /**
     * Get the extension of a filename.
     *
     * The returned string will be trimmed and converted to lowercase
     *
     * @param fileName the (supposed) file name
     * @return the extension from the file name
     */
    public static String fileExtension(String fileName) {
        return S.after(fileName, ".").trim().toLowerCase();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * digital characters from `0` to `9`
     */
    public static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * alphabetic characters include both lowercase and uppercase characters
     */
    public static final char[] ALPHABETICS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
    };

    static final char[] _COMMON_CHARS_ = {'0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '$', '#', '^', '&', '_',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            '~', '!', '@'};
    static final int _COMMON_CHARS_LEN_ = _COMMON_CHARS_.length;

    static final char[] _URL_SAFE_CHARS_ = {'0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '-', '.', '_',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '~'
    };
    static final int _URL_SAFE_CHARS_LEN = _URL_SAFE_CHARS_.length;

    /**
     * Generate random string.
     * The generated string is safe to be used as filename.
     *
     * @param len the number of chars in the returned string
     * @return a random string with specified number of chars
     */
    public static String random(int len) {
        return random(len, ThreadLocalRandom.current());
    }

    /**
     * Generate URL safe random string.
     * The generated string is safe to be used as part of URL.
     *
     * @param len the number of chars in the returned string
     * @return a random string with specified number of chars
     */
    public static String urlSafeRandom(int len) {
        return urlSafeRandom(len, ThreadLocalRandom.current());
    }

    /**
     * @return a random string with 8 chars
     */
    public static String random() {
        return random(8);
    }

    /**
     * @return a URL safe random string with 8 chars
     */
    public static String urlSafeRandom() {
        return urlSafeRandom(8);
    }

    /**
     * return a random string with 2 to 5 (inclusive) chars.
     */
    public static String shortRandom() {
        return random(2 + N.randInt(4));
    }

    /**
     * return a URL safe random string with 2 to 5 (inclusive) chars.
     */
    public static String shortUrlSafeRandom() {
        return urlSafeRandom(2 + N.randInt(4));
    }

    /**
     * return a random string with 6 to 15 (inclusive) chars.
     */
    public static String mediumRandom() {
        return random(6 + N.randInt(10));
    }

    /**
     * return a URL safe random string with 6 to 15 (inclusive) chars.
     */
    public static String mediumUrlSafeRandom() {
        return urlSafeRandom(6 + N.randInt(10));
    }

    /**
     * return a random string with 16 to 100 (inclusive) chars.
     */
    public static String longRandom() {
        return random(16 + N.randInt(85));
    }

    /**
     * return a URL safe random string with 16 to 100 (inclusive) chars.
     */
    public static String longUrlSafeRandom() {
        return urlSafeRandom(16 + N.randInt(85));
    }

    /**
     * This is the secure version of {@link #random(int)}.
     */
    public static String secureRandom(int len) {
        return random(len, new SecureRandom());
    }

    /**
     * This is the secure version of {@link #urlSafeRandom(int)}.
     */
    public static String secureUrlSafeRandom(int len) {
        return random(len, new SecureRandom());
    }

    /**
     * This is the secure version of {@link #random()}.
     */
    public static String secureRandom() {
        return secureRandom(8);
    }

    /**
     * This is the secure version of {@link #urlSafeRandom()}.
     */
    public static String secureUrlSafeRandom() {
        return secureUrlSafeRandom(8);
    }

    /**
     * Generate a random string with specified length and a random instance
     * @param len
     *      the length of the random string
     * @param r
     *      the `Random` instance
     * @return
     *      the random string
     */
    public static String random(int len, Random r) {
        return random(len, r, _COMMON_CHARS_, _COMMON_CHARS_LEN_);
    }

    /**
     * Generate a URL safe random string with specified length and a random instance
     * @param len
     *      the length of the random string
     * @param r
     *      the `Random` instance
     * @return
     *      the random string
     */
    public static String urlSafeRandom(int len, Random r) {
        return random(len, r, _URL_SAFE_CHARS_, _URL_SAFE_CHARS_LEN);
    }

    /**
     * Generate a random string with specified length, a `Random` instance, a customized
     * character pool
     * @param len
     *      the generated random string length
     * @param r
     *      the `Random` instance
     * @param pool
     *      the customized character pool
     * @return
     *      the random string
     */
    public static String random(int len, Random r, char[] pool) {
        return random(len, r, pool, pool.length);
    }

    private static String random(int len, Random r, char[] pool, int poolSize) {
        StringBuilder sb = new StringBuilder(len);
        while (len-- > 0) {
            int i = r.nextInt(poolSize);
            sb.append(pool[i]);
        }
        return sb.toString();
    }

    /**
     * Get string representation of an object instance
     *
     * @param o      the instance to be displayed
     * @param quoted whether display quotation mark
     * @return the string representation of object
     */
    public final static String string(Object o, boolean quoted) {
        String s = string(o);
        return quoted ? wrap(s, '"') : s;
    }

    public static String string(Object o) {
        if (null == o) {
            return "";
        }
        return o.toString();
    }

    public static String string(char c) {
        return String.valueOf(c);
    }

    public static String string(char[] ca) {
        return String.valueOf(ca);
    }

    public static String string(int n) {
        return String.valueOf(n);
    }

    public static String string(long l) {
        return String.valueOf(l);
    }

    public static String string(float f) {
        return String.valueOf(f);
    }

    public static String string(double d) {
        return String.valueOf(d);
    }

    public static Str str(Object o) {
        if (null == o) return Str.EMPTY_STR;
        return Str.of(o.toString());
    }

    public static Str str(char... ca) {
        return Str.of(ca);
    }

    /**
     * Return an new {@link Buffer} instance with predefined size specified
     *
     * @param size the initial capacity of the buffer
     * @return an new `Buffer` instance
     */
    public static Buffer newSizedBuffer(int size) {
        return new Buffer(size);
    }

    public static Buffer sizedBuffer(int size) {
        return size > 100 ? buffer() : newSizedBuffer(size);
    }

    /**
     * Returns an new {@link Buffer} instance
     *
     * @return an new Buffer instance
     */
    public static Buffer newBuffer() {
        return new Buffer();
    }

    public static Buffer newBuffer(byte o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(short o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(char o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(int o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(float o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(long o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(double o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(Object o) {
        return newBuffer().append(o);
    }

    public static Buffer newBuffer(String s) {
        return newBuffer().append(s);
    }

    private static final ThreadLocal<Buffer> _buf = new ThreadLocal<Buffer>() {
        @Override
        protected Buffer initialValue() {
            Buffer buf = new Buffer();
            buf.consumed = true;
            return buf;
        }
    };

    static int BUFFER_RETENTION_LIMIT = 1024;
    static int BUFFER_INIT_SIZE = 512;

    /**
     * Returns a {@link Buffer} instance. If the thread local instance is consumed already
     * then return it. Otherwise, return an new `Buffer` instance
     *
     * @return a `Buffer` instance as described above
     */
    public static Buffer buffer() {
        Buffer sb = _buf.get();
        if (!sb.consumed() || sb.capacity() > BUFFER_RETENTION_LIMIT) {
            sb = new Buffer(BUFFER_INIT_SIZE);
            _buf.set(sb);
            return sb;
        }
        sb.reset();
        return sb;
    }

    public static Buffer buffer(boolean o) {
        return buffer().append(o);
    }

    public static Buffer buffer(byte o) {
        return buffer().append(o);
    }

    public static Buffer buffer(short o) {
        return buffer().append(o);
    }

    public static Buffer buffer(char o) {
        return buffer().append(o);
    }

    public static Buffer buffer(int o) {
        return buffer().append(o);
    }

    public static Buffer buffer(float o) {
        return buffer().append(o);
    }

    public static Buffer buffer(long o) {
        return buffer().append(o);
    }

    public static Buffer buffer(double o) {
        return buffer().append(o);
    }

    public static Buffer buffer(char[] ca) {
        return buffer().append(ca);
    }

    public static Buffer buffer(Object o) {
        if (null == o) {
            return buffer();
        }
        Class<?> clz = o.getClass();
        if (clz == char[].class) {
            return buffer().append((char[]) o);
        } else if (clz == Character[].class) {
            Character[] ca = (Character[])o;
            Buffer buf = buffer();
            for (Character c : ca) {
                buf.append(c);
            }
            return buf;
        }  else {
            return buffer().append(o);
        }
    }

    public static Buffer buffer(String s) {
        return buffer().append(s);
    }


    /**
     * Return an new StringBuilder instance
     *
     * @return the new StringBuilder instance
     */
    public static StringBuilder newBuilder() {
        return new StringBuilder();
    }

    public static StringBuilder newBuilder(byte o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(short o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(char o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(int o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(float o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(long o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(double o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(Object o) {
        return newBuilder().append(o);
    }

    public static StringBuilder newBuilder(String s) {
        return newBuilder().append(s);
    }


    private static final ThreadLocal<StringBuilder> _sb = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    /**
     * Returns the ThreadLocal StringBuilder instance with length set to 0
     *
     * @return the thread local StringBuilder instance
     */
    public static StringBuilder builder() {
        StringBuilder sb = _sb.get();
        sb.setLength(0);
        return sb;
    }

    public static StringBuilder builder(boolean o) {
        return builder().append(o);
    }

    public static StringBuilder builder(byte o) {
        return builder().append(o);
    }

    public static StringBuilder builder(short o) {
        return builder().append(o);
    }

    public static StringBuilder builder(char o) {
        return builder().append(o);
    }

    public static StringBuilder builder(int o) {
        return builder().append(o);
    }

    public static StringBuilder builder(float o) {
        return builder().append(o);
    }

    public static StringBuilder builder(long o) {
        return builder().append(o);
    }

    public static StringBuilder builder(double o) {
        return builder().append(o);
    }

    public static StringBuilder builder(Object o) {
        return builder().append(o);
    }

    public static StringBuilder builder(String s) {
        return builder().append(s);
    }

    @Deprecated
    public static StringBuilder sizedBuilder(int capacity) {
        return new StringBuilder(capacity);
    }

    public static StringBuilder newSizedBuilder(int capacity) {
        return new StringBuilder(capacity);
    }

    public static T2 pair($.T2<String, String> t2) {
        return new T2(t2);
    }

    public static T2 pair(String left, String right) {
        return new T2(left, right);
    }

    public static T2 pair(char left, char right) {
        return new T2(String.valueOf(left), String.valueOf(right));
    }

    public static T2 binary($.T2<String, String> t2) {
        return new T2(t2);
    }

    public static T2 binary(String left, String right) {
        return new T2(left, right);
    }

    /**
     * Search for strings. Copied from jdk String.indexOf.
     * The source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     */
    public static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       char[] target, int targetOffset, int targetCount,
                       int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first) ;
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++)
                    ;

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    static int count(char[] source, int sourceOffset, int sourceCount,
                     char[] search, int searchOffset, int searchCount, boolean overlap) {
        int n = 0;
        while (true) {
            int i = indexOf(source, sourceOffset, sourceCount, search, searchOffset, searchCount, 0);
            if (i < 0) {
                return n;
            }
            n++;
            if (overlap) {
                sourceOffset += 1;
            } else {
                sourceOffset += searchCount;
            }
        }
    }


    /**
     * Search string pattern in another string. Copied from JDK String
     * The source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
                           char[] target, int targetOffset, int targetCount,
                           int fromIndex
    ) {
        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        char strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

    static char[] bufOf(String s) {
        return s.toCharArray();
    }

    public enum F {
        ;

        public static $.F2<String, String, Boolean> STARTS_WITH = new $.F2<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws NotAppliedException, $.Break {
                return null != s && s.startsWith(s2);
            }
        };

        public static $.Predicate<String> startsWith(final String prefix) {
            return $.predicate(STARTS_WITH.curry(prefix));
        }

        public static $.F2<String, String, Boolean> ENDS_WITH = new $.F2<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws NotAppliedException, $.Break {
                return null != s && s.endsWith(s2);
            }
        };

        public static $.Predicate<String> endsWith(final String suffix) {
            return $.predicate(ENDS_WITH.curry(suffix));
        }

        public static $.F2<String, String, Boolean> CONTAINS = new $.F2<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws NotAppliedException, $.Break {
                return null != s && s.contains(s2);
            }
        };

        public static $.Predicate<String> contains(final String search) {
            return $.predicate(CONTAINS.curry(search));
        }

        public static $.Transformer<String, String> TO_UPPERCASE = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) throws NotAppliedException, $.Break {
                return null == s ? null : s.toUpperCase();
            }
        };

        public static $.Transformer<String, String> TO_LOWERCASE = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) throws NotAppliedException, $.Break {
                return null == s ? null : s.toLowerCase();
            }
        };

        public static $.Transformer<String, String> NULL_SAFE = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) {
                return null == s ? "" : s;
            }
        };

        public static $.Transformer<String, String> TRIM = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) throws NotAppliedException, $.Break {
                return null == s ? null : s.trim();
            }
        };

        public static $.Transformer<String, String> CAP_FIRST = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) throws NotAppliedException, $.Break {
                return null == s ? null : S.capFirst(s);
            }
        };

        public static $.Transformer<String, String> LOWER_FIRST = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) throws NotAppliedException, $.Break {
                return null == s ? null : S.lowerFirst(s);
            }
        };

        public static $.Predicate<String> IS_EMPTY = new $.Predicate<String>() {
            @Override
            public boolean test(String s) throws NotAppliedException, $.Break {
                return S.isEmpty(s);
            }
        };

        public static $.Predicate<String> NOT_EMPTY = IS_EMPTY.negate();

        public static $.Predicate<String> IS_BLANK = new $.Predicate<String>() {
            @Override
            public boolean test(String s) {
                return S.isBlank(s);
            }
        };

        public static $.Predicate<String> NOT_BLANK = IS_BLANK.negate();

        public static $.F2<String, Integer, String> MAX_LENGTH = new $.F2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) throws NotAppliedException, $.Break {
                return null == s ? null : S.maxLength(s, n);
            }
        };

        /**
         * A split function that use the {@link #COMMON_SEP} to split Strings
         */
        public static $.Transformer<String, List> SPLIT = split(COMMON_SEP);

        public static $.Transformer<String, List> split(final String sep) {
            return new $.Transformer<String, List>() {
                @Override
                public List transform(String s) throws NotAppliedException, $.Break {
                    return ImmutableStringList.of(s.split(sep));
                }
            };
        }

        public static $.Transformer<String, String> maxLength(int n) {
            return $.Transformer.adapt(MAX_LENGTH.curry(n));
        }

        public static $.F2<String, Integer, String> LAST = new $.F2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) throws NotAppliedException, $.Break {
                return null == s ? null : S.last(s, n);
            }
        };

        public static $.Transformer<String, String> last(int n) {
            return $.Transformer.adapt(LAST.curry(n));
        }

        public static $.F2<String, Integer, String> FIRST = new $.F2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) throws NotAppliedException, $.Break {
                return null == s ? null : S.first(s, n);
            }
        };

        public static $.Transformer<String, String> first(final int n) {
            return $.Transformer.adapt(FIRST.curry(n));
        }

        public static $.Transformer<String, String> dropHead(final int n) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) {
                    if (null == s) {
                        return null;
                    }
                    if (n > s.length()) {
                        return "";
                    }
                    return s.substring(n);
                }
            };
        }

        public static $.Transformer<String, String> dropHeadIfStartsWith(final String prefix) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) {
                    return null == s ? null : s.startsWith(prefix) ? s.substring(prefix.length()) : s;
                }
            };
        }

        public static $.Transformer<String, String> dropTail(final int n) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) {
                    if (null == s) {
                        return null;
                    }
                    int len = s.length();
                    if (n > len) {
                        return "";
                    }
                    return s.substring(0, len - n);
                }
            };
        }

        public static $.Transformer<String, String> dropTailIfEndsWith(final String suffix) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) {
                    return null == s ? null : s.endsWith(suffix) ? S.cut(s).beforeLast(suffix) : s;
                }
            };
        }

        public static $.F0<String> RANDOM = new $.F0<String>() {
            @Override
            public String apply() throws NotAppliedException, $.Break {
                return S.random();
            }
        };

        public static $.Transformer<Integer, String> RANDOM_N = new $.Transformer<Integer, String>() {
            @Override
            public String transform(Integer n) throws NotAppliedException, $.Break {
                return S.random(n);
            }
        };

        public static $.F0<String> random() {
            return RANDOM;
        }

        public static $.F0<String> random(int n) {
            return RANDOM_N.curry(n);
        }

        public static $.Transformer<String, Integer> LENGTH = new $.Transformer<String, Integer>() {
            @Override
            public Integer transform(String s) throws NotAppliedException, $.Break {
                return s.length();
            }
        };

        public static $.Transformer<String, String> append(final String appendix) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) throws NotAppliedException, $.Break {
                    return S.newBuilder(s).append(appendix).toString();
                }
            };
        }

        public static $.Transformer<String, String> prepend(final String prependix) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) throws NotAppliedException, $.Break {
                    return S.newBuilder(prependix).append(s).toString();
                }
            };
        }

        public static $.Transformer<String, String> wrapper(final String left, final String right) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) throws $.Break, NotAppliedException {
                    return left + s + right;
                }
            };
        }

        public static $.Transformer<String, String> wrapper(String wrapper) {
            return wrapper(wrapper, wrapper);
        }

        public static $.Transformer<String, String> wrapper(final $.Tuple<String, String> wrapper) {
            return wrapper(wrapper.left(), wrapper.right());
        }

        public static $.Transformer<String, String> strip(String wrapper) {
            return strip(wrapper, wrapper);
        }

        public static $.Transformer<String, String> strip(final String left, final String right) {
            return new $.Transformer<String, String>() {
                @Override
                public String transform(String s) {
                    return S.strip(s, left, right);
                }
            };
        }

        public static $.Transformer<String, String> strip(final $.Tuple<String, String> wrapper) {
            return strip(wrapper.left(), wrapper.right());
        }

    }

    /**
     * A `S.Buffer` is OSGL's implementation of JDK's
     * {@link StringBuilder} with additional method to track
     * the state of the instance, i.e. decide whether the
     * constructor's buffer has been consumed (via {@link #toString()}
     * method.
     *
     * **Note** when buffer is consumed (i.e. the `toString()`) method
     * is called, the length of the buffer will be reset to `0`. However
     * the internal char array will be leave as it is.
     *
     * **Note** this class is **NOT** thread safe
     *
     * **Note** Unlike {@link StringBuilder} when appending `null`
     * it will **NOT** change the state of this object.
     */
    public static class Buffer extends Writer implements Appendable, CharSequence {

        /**
         * The value is used for character storage.
         */
        private char[] value;

        /**
         * The count is the number of characters used.
         */
        private int count;

        /**
         * track if {@link #toString()} method is called
         */
        private boolean consumed;

        /**
         * This no-arg constructor is necessary for serialization of subclasses.
         */
        public Buffer() {
            this(16);
        }

        /**
         * Creates an Buffer of the specified capacity.
         */
        public Buffer(int capacity) {
            value = new char[capacity];
            consumed = false;
        }

        public final boolean consumed() {
            return consumed;
        }

        private String consume() {
            return toString();
        }

        public Buffer reset() {
            this.setLength(0);
            this.consumed = false;
            return this;
        }

        public Buffer clear() {
            this.setLength(0);
            return this;
        }

        /**
         * Returns the length (character count).
         *
         * @return the length of the sequence of characters currently
         * represented by this object
         */
        @Override
        public int length() {
            return count;
        }

        /**
         * Check if the buffer is empty. Calling this method is essentially equivalent to calling
         *
         * ```java
         * 0 == length()
         * ```
         * @return `true` if this buffer is empty or `false` otherwise
         */
        public boolean isEmpty() {
            return 0 == count;
        }

        /**
         * Returns the current capacity. The capacity is the amount of storage
         * available for newly inserted characters, beyond which an allocation
         * will occur.
         *
         * @return the current capacity
         */
        public int capacity() {
            return value.length;
        }

        /**
         * Ensures that the capacity is at least equal to the specified minimum.
         * If the current capacity is less than the argument, then a new internal
         * array is allocated with greater capacity. The new capacity is the
         * larger of:
         * <ul>
         * <li>The {@code minimumCapacity} argument.
         * <li>Twice the old capacity, plus {@code 2}.
         * </ul>
         * If the {@code minimumCapacity} argument is nonpositive, this
         * method takes no action and simply returns.
         * Note that subsequent operations on this object can reduce the
         * actual capacity below that requested here.
         *
         * @param minimumCapacity the minimum desired capacity.
         */
        public void ensureCapacity(int minimumCapacity) {
            if (minimumCapacity > 0)
                ensureCapacityInternal(minimumCapacity);
        }

        /**
         * Attempts to reduce storage used for the character sequence.
         * If the buffer is larger than necessary to hold its current sequence of
         * characters, then it may be resized to become more space efficient.
         * Calling this method may, but is not required to, affect the value
         * returned by a subsequent call to the {@link #capacity()} method.
         */
        public void trimToSize() {
            if (count < value.length) {
                value = Arrays.copyOf(value, count);
            }
        }

        /**
         * Sets the length of the character sequence.
         * The sequence is changed to a new character sequence
         * whose length is specified by the argument. For every nonnegative
         * index <i>k</i> less than {@code newLength}, the character at
         * index <i>k</i> in the new character sequence is the same as the
         * character at index <i>k</i> in the old sequence if <i>k</i> is less
         * than the length of the old character sequence; otherwise, it is the
         * null character {@code '\u005Cu0000'}.
         * <p>
         * In other words, if the {@code newLength} argument is less than
         * the current length, the length is changed to the specified length.
         * <p>
         * If the {@code newLength} argument is greater than or equal
         * to the current length, sufficient null characters
         * ({@code '\u005Cu0000'}) are appended so that
         * length becomes the {@code newLength} argument.
         * <p>
         * The {@code newLength} argument must be greater than or equal
         * to {@code 0}.
         *
         * @param newLength the new length
         * @throws IndexOutOfBoundsException if the
         *                                   {@code newLength} argument is negative.
         */
        public void setLength(int newLength) {
            if (newLength < 0)
                throw new StringIndexOutOfBoundsException(newLength);
            ensureCapacityInternal(newLength);

            if (count < newLength) {
                Arrays.fill(value, count, newLength, '\0');
            }

            count = newLength;
        }

        /**
         * Returns the {@code char} value in this sequence at the specified index.
         * The first {@code char} value is at index {@code 0}, the next at index
         * {@code 1}, and so on, as in array indexing.
         * <p>
         * The index argument must be greater than or equal to
         * {@code 0}, and less than the length of this sequence.
         * <p>
         * <p>If the {@code char} value specified by the index is a
         * <a href="Character.html#unicode">surrogate</a>, the surrogate
         * value is returned.
         *
         * @param index the index of the desired {@code char} value.
         * @return the {@code char} value at the specified index.
         * @throws IndexOutOfBoundsException if {@code index} is
         *                                   negative or greater than or equal to {@code length()}.
         */
        @Override
        public char charAt(int index) {
            if ((index < 0) || (index >= count))
                throw new StringIndexOutOfBoundsException(index);
            return value[index];
        }

        /**
         * Alias of {@link #charAt(int)}
         */
        public char get(int index) {
            return charAt(index);
        }

        /**
         * Returns the character (Unicode code point) at the specified
         * index. The index refers to {@code char} values
         * (Unicode code units) and ranges from {@code 0} to
         * {@link #length()}{@code  - 1}.
         * <p>
         * <p> If the {@code char} value specified at the given index
         * is in the high-surrogate range, the following index is less
         * than the length of this sequence, and the
         * {@code char} value at the following index is in the
         * low-surrogate range, then the supplementary code point
         * corresponding to this surrogate pair is returned. Otherwise,
         * the {@code char} value at the given index is returned.
         *
         * @param index the index to the {@code char} values
         * @return the code point value of the character at the
         * {@code index}
         * @throws IndexOutOfBoundsException if the {@code index}
         *                                   argument is negative or not less than the length of this
         *                                   sequence.
         */
        public int codePointAt(int index) {
            if ((index < 0) || (index >= count)) {
                throw new StringIndexOutOfBoundsException(index);
            }
            return Character.codePointAt(value, index, count);
        }

        /**
         * Returns the character (Unicode code point) before the specified
         * index. The index refers to {@code char} values
         * (Unicode code units) and ranges from {@code 1} to {@link
         * #length()}.
         * <p>
         * <p> If the {@code char} value at {@code (index - 1)}
         * is in the low-surrogate range, {@code (index - 2)} is not
         * negative, and the {@code char} value at {@code (index -
         * 2)} is in the high-surrogate range, then the
         * supplementary code point value of the surrogate pair is
         * returned. If the {@code char} value at {@code index -
         * 1} is an unpaired low-surrogate or a high-surrogate, the
         * surrogate value is returned.
         *
         * @param index the index following the code point that should be returned
         * @return the Unicode code point value before the given index.
         * @throws IndexOutOfBoundsException if the {@code index}
         *                                   argument is less than 1 or greater than the length
         *                                   of this sequence.
         */
        public int codePointBefore(int index) {
            int i = index - 1;
            if ((i < 0) || (i >= count)) {
                throw new StringIndexOutOfBoundsException(index);
            }
            return Character.codePointBefore(value, index, 0);
        }

        /**
         * Returns the number of Unicode code points in the specified text
         * range of this sequence. The text range begins at the specified
         * {@code beginIndex} and extends to the {@code char} at
         * index {@code endIndex - 1}. Thus the length (in
         * {@code char}s) of the text range is
         * {@code endIndex-beginIndex}. Unpaired surrogates within
         * this sequence count as one code point each.
         *
         * @param beginIndex the index to the first {@code char} of
         *                   the text range.
         * @param endIndex   the index after the last {@code char} of
         *                   the text range.
         * @return the number of Unicode code points in the specified text
         * range
         * @throws IndexOutOfBoundsException if the
         *                                   {@code beginIndex} is negative, or {@code endIndex}
         *                                   is larger than the length of this sequence, or
         *                                   {@code beginIndex} is larger than {@code endIndex}.
         */
        public int codePointCount(int beginIndex, int endIndex) {
            if (beginIndex < 0 || endIndex > count || beginIndex > endIndex) {
                throw new IndexOutOfBoundsException();
            }
            return Character.codePointCount(value, beginIndex, endIndex - beginIndex);
        }

        /**
         * Returns the index within this sequence that is offset from the
         * given {@code index} by {@code codePointOffset} code
         * points. Unpaired surrogates within the text range given by
         * {@code index} and {@code codePointOffset} count as
         * one code point each.
         *
         * @param index           the index to be offset
         * @param codePointOffset the offset in code points
         * @return the index within this sequence
         * @throws IndexOutOfBoundsException if {@code index}
         *                                   is negative or larger then the length of this sequence,
         *                                   or if {@code codePointOffset} is positive and the subsequence
         *                                   starting with {@code index} has fewer than
         *                                   {@code codePointOffset} code points,
         *                                   or if {@code codePointOffset} is negative and the subsequence
         *                                   before {@code index} has fewer than the absolute value of
         *                                   {@code codePointOffset} code points.
         */
        public int offsetByCodePoints(int index, int codePointOffset) {
            if (index < 0 || index > count) {
                throw new IndexOutOfBoundsException();
            }
            return Character.offsetByCodePoints(value, 0, count,
                    index, codePointOffset);
        }

        /**
         * Characters are copied from this sequence into the
         * destination character array {@code dst}. The first character to
         * be copied is at index {@code srcBegin}; the last character to
         * be copied is at index {@code srcEnd-1}. The total number of
         * characters to be copied is {@code srcEnd-srcBegin}. The
         * characters are copied into the subarray of {@code dst} starting
         * at index {@code dstBegin} and ending at index:
         * <pre>{@code
         * dstbegin + (srcEnd-srcBegin) - 1
         * }</pre>
         *
         * @param srcBegin start copying at this offset.
         * @param srcEnd   stop copying at this offset.
         * @param dst      the array to copy the data into.
         * @param dstBegin offset into {@code dst}.
         * @throws IndexOutOfBoundsException if any of the following is true:
         *                                   <ul>
         *                                   <li>{@code srcBegin} is negative
         *                                   <li>{@code dstBegin} is negative
         *                                   <li>the {@code srcBegin} argument is greater than
         *                                   the {@code srcEnd} argument.
         *                                   <li>{@code srcEnd} is greater than
         *                                   {@code this.length()}.
         *                                   <li>{@code dstBegin+srcEnd-srcBegin} is greater than
         *                                   {@code dst.length}
         *                                   </ul>
         */
        public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
            if (srcBegin < 0)
                throw new StringIndexOutOfBoundsException(srcBegin);
            if ((srcEnd < 0) || (srcEnd > count))
                throw new StringIndexOutOfBoundsException(srcEnd);
            if (srcBegin > srcEnd)
                throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
            System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
        }

        /**
         * The character at the specified index is set to {@code ch}. This
         * sequence is altered to represent a new character sequence that is
         * identical to the old character sequence, except that it contains the
         * character {@code ch} at position {@code index}.
         * <p>
         * The index argument must be greater than or equal to
         * {@code 0}, and less than the length of this sequence.
         *
         * @param index the index of the character to modify.
         * @param ch    the new character.
         * @throws IndexOutOfBoundsException if {@code index} is
         *                                   negative or greater than or equal to {@code length()}.
         */
        public void setCharAt(int index, char ch) {
            if ((index < 0) || (index >= count))
                throw new StringIndexOutOfBoundsException(index);
            value[index] = ch;
        }

        /**
         * Alias of {@link #setCharAt(int, char)}
         */
        public void set(int index, char ch) {
            setCharAt(index, ch);
        }

        /**
         * Appends the string representation of the {@code Object} argument.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(Object)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param obj an {@code Object}.
         * @return a reference to this object.
         */
        public Buffer append(Object obj) {
            return append(string(obj));
        }

        /**
         * Alias of {@link #append(Object)}
         */
        public Buffer a(Object obj) {
            return append(obj);
        }

        public Buffer prepend(Object obj) {
            return prepend(String.valueOf(obj));
        }

        /**
         * Alias of {@link #prepend(Object)}
         */
        public Buffer p(Object obj) {
            return prepend(obj);
        }

        /**
         * Appends the specified string to this character sequence.
         * <p>
         * The characters of the {@code String} argument are appended, in
         * order, increasing the length of this sequence by the length of the
         * argument. If {@code str} is {@code null}, then nothing is appended
         * <p>
         * Let <i>n</i> be the length of this character sequence just prior to
         * execution of the {@code append} method. Then the character at
         * index <i>k</i> in the new character sequence is equal to the character
         * at index <i>k</i> in the old character sequence, if <i>k</i> is less
         * than <i>n</i>; otherwise, it is equal to the character at index
         * <i>k-n</i> in the argument {@code str}.
         *
         * @param str a string.
         * @return a reference to this object.
         */
        public Buffer append(String str) {
            if (str == null)
                return appendNull();
            int len = str.length();
            ensureCapacityInternal(count + len);
            str.getChars(0, len, value, count);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #append(String)}
         */
        public Buffer a(String str) {
            return append(str);
        }

        public Buffer prepend(String str) {
            if (null == str)
                return prependNull();
            int len = str.length();
            ensureCapacityInternal(count + len);
            System.arraycopy(value, 0, value, len, count);
            str.getChars(0, len, value, 0);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #prepend(String)}
         */
        public Buffer p(String str) {
            return prepend(str);
        }

        // Documentation in subclasses because of synchro difference
        public Buffer append(StringBuffer sb) {
            if (sb == null)
                return appendNull();
            int len = sb.length();
            ensureCapacityInternal(count + len);
            sb.getChars(0, len, value, count);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #append(StringBuffer)}
         */
        public Buffer a(StringBuffer sb) {
            return append(sb);
        }

        // Documentation in subclasses because of synchro difference
        public Buffer prepend(StringBuffer sb) {
            if (sb == null)
                return appendNull();
            int len = sb.length();
            ensureCapacityInternal(count + len);
            System.arraycopy(value, 0, value, len, count);
            sb.getChars(0, len, value, 0);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #prepend(StringBuffer)}
         */
        public Buffer p(StringBuffer sb) {
            return prepend(sb);
        }

        // Documentation in subclasses because of synchro difference
        public Buffer append(StringBuilder sb) {
            if (sb == null)
                return appendNull();
            int len = sb.length();
            ensureCapacityInternal(count + len);
            sb.getChars(0, len, value, count);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #append(StringBuilder)}
         */
        public Buffer a(StringBuilder sb) {
            return append(sb);
        }

        // Documentation in subclasses because of synchro difference
        public Buffer prepend(StringBuilder sb) {
            if (sb == null)
                return prependNull();
            int len = sb.length();
            ensureCapacityInternal(count + len);
            System.arraycopy(value, 0, value, len, count);
            sb.getChars(0, len, value, 0);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #prepend(StringBuilder)}
         */
        public Buffer p(StringBuilder sb) {
            return prepend(sb);
        }

        public Buffer append(Buffer asb) {
            if (asb == null)
                return appendNull();
            int len = asb.length();
            ensureCapacityInternal(count + len);
            asb.getChars(0, len, value, count);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #append(Buffer)}
         */
        public Buffer a(Buffer asb) {
            return append(asb);
        }

        public Buffer prepend(Buffer asb) {
            if (asb == null)
                return prependNull();
            int len = asb.length();
            ensureCapacityInternal(count + len);
            System.arraycopy(value, 0, value, len, count);
            asb.getChars(0, len, value, 0);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #prepend(Buffer)}
         */
        public Buffer p(Buffer asb) {
            return prepend(asb);
        }

        // Documentation in subclasses because of synchro difference
        @Override
        public Buffer append(CharSequence s) {
            if (s == null)
                return appendNull();
            if (s instanceof String)
                return this.append((String) s);
            if (s instanceof Buffer)
                return this.append((Buffer) s);

            return this.append(s, 0, s.length());
        }

        /**
         * Alias of {@link #append(CharSequence)}
         */
        public Buffer a(CharSequence s) {
            return append(s);
        }

        public Buffer prepend(CharSequence s) {
            if (s == null)
                return prependNull();
            if (s instanceof String)
                return this.prepend((String) s);
            if (s instanceof Buffer)
                return this.prepend((Buffer) s);
            if (s instanceof StringBuffer) {
                return this.prepend((StringBuffer) s);
            }
            if (s instanceof StringBuilder) {
                return this.prepend((StringBuilder) s);
            }

            return this.append(s, 0, s.length());
        }

        /**
         * Alias of {@link #prepend(CharSequence)}
         */
        public Buffer p(CharSequence s) {
            return prepend(s);
        }


        private Buffer appendNull() {
            return this;
        }

        private Buffer prependNull() {
            return this;
        }

        /**
         * Appends a subsequence of the specified {@code CharSequence} to this
         * sequence.
         * <p>
         * Characters of the argument {@code s}, starting at
         * index {@code start}, are appended, in order, to the contents of
         * this sequence up to the (exclusive) index {@code end}. The length
         * of this sequence is increased by the value of {@code end - start}.
         * <p>
         * Let <i>n</i> be the length of this character sequence just prior to
         * execution of the {@code append} method. Then the character at
         * index <i>k</i> in this character sequence becomes equal to the
         * character at index <i>k</i> in this sequence, if <i>k</i> is less than
         * <i>n</i>; otherwise, it is equal to the character at index
         * <i>k+start-n</i> in the argument {@code s}.
         * <p>
         * If {@code s} is {@code null}, then this method appends
         * characters as if the s parameter was a sequence containing the four
         * characters {@code "null"}.
         *
         * @param s     the sequence to append.
         * @param start the starting index of the subsequence to be appended.
         * @param end   the end index of the subsequence to be appended.
         * @return a reference to this object.
         * @throws IndexOutOfBoundsException if
         *                                   {@code start} is negative, or
         *                                   {@code start} is greater than {@code end} or
         *                                   {@code end} is greater than {@code s.length()}
         */
        @Override
        public Buffer append(CharSequence s, int start, int end) {
            if (s == null)
                s = "null";
            if ((start < 0) || (start > end) || (end > s.length()))
                throw new IndexOutOfBoundsException(
                        "start " + start + ", end " + end + ", s.length() "
                                + s.length());
            int len = end - start;
            ensureCapacityInternal(count + len);
            for (int i = start, j = count; i < end; i++, j++)
                value[j] = s.charAt(i);
            count += len;
            return this;
        }

        /**
         * Appends the string representation of the {@code char} array
         * argument to this sequence.
         * <p>
         * The characters of the array argument are appended, in order, to
         * the contents of this sequence. The length of this sequence
         * increases by the length of the argument.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(char[])},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param str the characters to be appended.
         * @return a reference to this object.
         */
        public Buffer append(char[] str) {
            int len = str.length;
            ensureCapacityInternal(count + len);
            System.arraycopy(str, 0, value, count, len);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #append(char[])}
         */
        public Buffer a(char[] str) {
            return append(str);
        }

        public Buffer prepend(char[] str) {
            int len = str.length;
            ensureCapacityInternal(count + len);
            System.arraycopy(value, 0, value, count, count);
            System.arraycopy(str, 0, value, count, 0);
            count += len;
            return this;
        }

        /**
         * Alias of {@link #prepend(char[])}
         */
        public Buffer p(char[] str) {
            return prepend(str);
        }

        /**
         * Appends the string representation of a subarray of the
         * {@code char} array argument to this sequence.
         * <p>
         * Characters of the {@code char} array {@code str}, starting at
         * index {@code offset}, are appended, in order, to the contents
         * of this sequence. The length of this sequence increases
         * by the value of {@code len}.
         * <p>
         * The overall effect is exactly as if the arguments were converted
         * to a string by the method {@link String#valueOf(char[], int, int)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param str    the characters to be appended.
         * @param offset the index of the first {@code char} to append.
         * @param len    the number of {@code char}s to append.
         * @return a reference to this object.
         * @throws IndexOutOfBoundsException if {@code offset < 0} or {@code len < 0}
         *                                   or {@code offset+len > str.length}
         */
        public Buffer append(char str[], int offset, int len) {
            if (len > 0)                // let arraycopy report AIOOBE for len < 0
                ensureCapacityInternal(count + len);
            System.arraycopy(str, offset, value, count, len);
            count += len;
            return this;
        }

        /**
         * Appends the string representation of the {@code boolean}
         * argument to the sequence.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(boolean)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param b a {@code boolean}.
         * @return a reference to this object.
         */
        public Buffer prepend(boolean b) {
            if (b) {
                ensureCapacityInternal(count + 4);
                System.arraycopy(value, 0, value, 4, count);
                int cursor = 0;
                value[cursor++] = 't';
                value[cursor++] = 'r';
                value[cursor++] = 'u';
                value[cursor++] = 'e';
                count += 4;
            } else {
                ensureCapacityInternal(count + 5);
                System.arraycopy(value, 0, value, 5, count);
                int cursor = 0;
                value[cursor++] = 'f';
                value[cursor++] = 'a';
                value[cursor++] = 'l';
                value[cursor++] = 's';
                value[cursor++] = 'e';
                count += 5;
            }
            return this;
        }

        /**
         * Alias of {@link #prepend(boolean)}
         */
        public Buffer p(boolean b) {
            return prepend(b);
        }

        public Buffer append(boolean b) {
            if (b) {
                ensureCapacityInternal(count + 4);
                value[count++] = 't';
                value[count++] = 'r';
                value[count++] = 'u';
                value[count++] = 'e';
            } else {
                ensureCapacityInternal(count + 5);
                value[count++] = 'f';
                value[count++] = 'a';
                value[count++] = 'l';
                value[count++] = 's';
                value[count++] = 'e';
            }
            return this;
        }

        /**
         * Alias of {@link #append(boolean)}
         */
        public Buffer a(boolean b) {
            return append(b);
        }

        /**
         * Appends the string representation of the {@code char}
         * argument to this sequence.
         * <p>
         * The argument is appended to the contents of this sequence.
         * The length of this sequence increases by {@code 1}.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(char)},
         * and the character in that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param c a {@code char}.
         * @return a reference to this object.
         */
        @Override
        public Buffer append(char c) {
            ensureCapacityInternal(count + 1);
            value[count++] = c;
            return this;
        }

        /**
         * alias of {@link #append(char)}
         */
        public Buffer a(char c) {
            return append(c);
        }

        public Buffer prepend(char c) {
            ensureCapacityInternal(count + 1);
            System.arraycopy(value, 0, value, 1, count);
            value[0] = c;
            return this;
        }

        /**
         * alias of {@link #prepend(char)}
         */
        public Buffer p(char c) {
            return prepend(c);
        }

        /**
         * Appends the string representation of the {@code int}
         * argument to this sequence.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(int)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param i an {@code int}.
         * @return a reference to this object.
         */
        public Buffer append(int i) {
            if (i == Integer.MIN_VALUE) {
                append("-2147483648");
                return this;
            }
            int appendedLength = (i < 0) ? stringSize(-i) + 1
                    : stringSize(i);
            int spaceNeeded = count + appendedLength;
            ensureCapacityInternal(spaceNeeded);
            getChars(i, spaceNeeded, value);
            count = spaceNeeded;
            return this;
        }


        /**
         * alias of {@link #append(int)}
         */
        public Buffer a(int i) {
            return append(i);
        }


        public Buffer prepend(int i) {
            if (i == Integer.MIN_VALUE) {
                prepend("-2147483648");
                return this;
            }
            int appendedLength = (i < 0) ? stringSize(-i) + 1
                    : stringSize(i);
            int spaceNeeded = count + appendedLength;
            ensureCapacityInternal(spaceNeeded);
            System.arraycopy(value, 0, value, appendedLength, count);
            getChars(i, appendedLength, value);
            count = spaceNeeded;
            return this;
        }


        /**
         * alias of {@link #prepend(int)}
         */
        public Buffer p(int i) {
            return prepend(i);
        }


        /**
         * Appends the string representation of the {@code long}
         * argument to this sequence.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(long)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param l a {@code long}.
         * @return a reference to this object.
         */
        public Buffer append(long l) {
            if (l == Long.MIN_VALUE) {
                append("-9223372036854775808");
                return this;
            }
            int appendedLength = (l < 0) ? stringSize(-l) + 1
                    : stringSize(l);
            int spaceNeeded = count + appendedLength;
            ensureCapacityInternal(spaceNeeded);
            getChars(l, spaceNeeded, value);
            count = spaceNeeded;
            return this;
        }

        /**
         * alias of {@link #append(long)}
         */
        public Buffer a(long l) {
            return append(l);
        }


        public Buffer prepend(long l) {
            if (l == Long.MIN_VALUE) {
                append("-9223372036854775808");
                return this;
            }
            int appendedLength = (l < 0) ? stringSize(-l) + 1
                    : stringSize(l);
            int spaceNeeded = count + appendedLength;
            ensureCapacityInternal(spaceNeeded);
            System.arraycopy(value, 0, value, appendedLength, count);
            getChars(l, appendedLength, value);
            count = spaceNeeded;
            return this;
        }

        /**
         * alias of {@link #prepend(long)}
         */
        public Buffer p(long l) {
            return prepend(l);
        }

        /**
         * Appends the string representation of the {@code float}
         * argument to this sequence.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(float)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param f a {@code float}.
         * @return a reference to this object.
         */
        public Buffer append(float f) {
            return this.append(String.valueOf(f));
        }

        /**
         * Alias of {@link #append(float)}
         */
        public Buffer a(float f) {
            return append(f);
        }

        public Buffer prepend(float f) {
            return prepend(String.valueOf(f));
        }

        /**
         * Alias of {@link #prepend(float)}
         */
        public Buffer p(float f) {
            return prepend(f);
        }

        /**
         * Appends the string representation of the {@code double}
         * argument to this sequence.
         * <p>
         * The overall effect is exactly as if the argument were converted
         * to a string by the method {@link String#valueOf(double)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param d a {@code double}.
         * @return a reference to this object.
         */
        public Buffer append(double d) {
            return this.append(String.valueOf(d));
        }

        /**
         * Alias of {@link #append(double)}
         */
        public Buffer a(double d) {
            return append(d);
        }


        public Buffer prepend(double d) {
            return this.prepend(String.valueOf(d));
        }

        /**
         * Alias of {@link #prepend(double)}
         */
        public Buffer p(double d) {
            return prepend(d);
        }

        /**
         * Removes the characters in a substring of this sequence.
         * The substring begins at the specified {@code start} and extends to
         * the character at index {@code end - 1} or to the end of the
         * sequence if no such character exists. If
         * {@code start} is equal to {@code end}, no changes are made.
         *
         * @param start The beginning index, inclusive.
         * @param end   The ending index, exclusive.
         * @return This object.
         * @throws StringIndexOutOfBoundsException if {@code start}
         *                                         is negative, greater than {@code length()}, or
         *                                         greater than {@code end}.
         */
        public Buffer delete(int start, int end) {
            if (start < 0)
                throw new StringIndexOutOfBoundsException(start);
            if (end > count)
                end = count;
            if (start > end)
                throw new StringIndexOutOfBoundsException();
            int len = end - start;
            if (len > 0) {
                System.arraycopy(value, start + len, value, start, count - end);
                count -= len;
            }
            return this;
        }

        /**
         * Appends the string representation of the {@code codePoint}
         * argument to this sequence.
         * <p>
         * <p> The argument is appended to the contents of this sequence.
         * The length of this sequence increases by
         * {@link Character#charCount(int) Character.charCount(codePoint)}.
         * <p>
         * <p> The overall effect is exactly as if the argument were
         * converted to a {@code char} array by the method
         * {@link Character#toChars(int)} and the character in that array
         * were then {@link #append(char[]) appended} to this character
         * sequence.
         *
         * @param codePoint a Unicode code point
         * @return a reference to this object.
         * @throws IllegalArgumentException if the specified
         *                                  {@code codePoint} isn't a valid Unicode code point
         */
        public Buffer appendCodePoint(int codePoint) {
            final int count = this.count;

            if (Character.isBmpCodePoint(codePoint)) {
                ensureCapacityInternal(count + 1);
                value[count] = (char) codePoint;
                this.count = count + 1;
            } else if (Character.isValidCodePoint(codePoint)) {
                ensureCapacityInternal(count + 2);
                toSurrogates(codePoint, value, count);
                this.count = count + 2;
            } else {
                throw new IllegalArgumentException();
            }
            return this;
        }

        /**
         * Removes the {@code char} at the specified position in this
         * sequence. This sequence is shortened by one {@code char}.
         * <p>
         * <p>Note: If the character at the given index is a supplementary
         * character, this method does not remove the entire character. If
         * correct handling of supplementary characters is required,
         * determine the number of {@code char}s to remove by calling
         * {@code Character.charCount(thisSequence.codePointAt(index))},
         * where {@code thisSequence} is this sequence.
         *
         * @param index Index of {@code char} to remove
         * @return This object.
         * @throws StringIndexOutOfBoundsException if the {@code index}
         *                                         is negative or greater than or equal to
         *                                         {@code length()}.
         */
        public Buffer deleteCharAt(int index) {
            if ((index < 0) || (index >= count))
                throw new StringIndexOutOfBoundsException(index);
            System.arraycopy(value, index + 1, value, index, count - index - 1);
            count--;
            return this;
        }

        /**
         * Replaces the characters in a substring of this sequence
         * with characters in the specified {@code String}. The substring
         * begins at the specified {@code start} and extends to the character
         * at index {@code end - 1} or to the end of the
         * sequence if no such character exists. First the
         * characters in the substring are removed and then the specified
         * {@code String} is inserted at {@code start}. (This
         * sequence will be lengthened to accommodate the
         * specified String if necessary.)
         *
         * @param start The beginning index, inclusive.
         * @param end   The ending index, exclusive.
         * @param str   String that will replace previous contents.
         * @return This object.
         * @throws StringIndexOutOfBoundsException if {@code start}
         *                                         is negative, greater than {@code length()}, or
         *                                         greater than {@code end}.
         */
        public Buffer replace(int start, int end, String str) {
            if (start < 0)
                throw new StringIndexOutOfBoundsException(start);
            if (start > count)
                throw new StringIndexOutOfBoundsException("start > length()");
            if (start > end)
                throw new StringIndexOutOfBoundsException("start > end");

            if (end > count)
                end = count;
            int len = str.length();
            int newCount = count + len - (end - start);
            ensureCapacityInternal(newCount);

            System.arraycopy(value, end, value, start + len, count - end);
            char[] strVal = str.toCharArray();
            System.arraycopy(value, 0, strVal, start, value.length);
            count = newCount;
            return this;
        }

        /**
         * Returns a new {@code String} that contains a subsequence of
         * characters currently contained in this character sequence. The
         * substring begins at the specified index and extends to the end of
         * this sequence.
         *
         * @param start The beginning index, inclusive.
         * @return The new string.
         * @throws StringIndexOutOfBoundsException if {@code start} is
         *                                         less than zero, or greater than the length of this object.
         */
        public String substring(int start) {
            return substring(start, count);
        }

        /**
         * Returns a new character sequence that is a subsequence of this sequence.
         * <p>
         * <p> An invocation of this method of the form
         * <p>
         * <pre>{@code
         * sb.subSequence(begin,&nbsp;end)}</pre>
         * <p>
         * behaves in exactly the same way as the invocation
         * <p>
         * <pre>{@code
         * sb.substring(begin,&nbsp;end)}</pre>
         * <p>
         * This method is provided so that this class can
         * implement the {@link CharSequence} interface.
         *
         * @param start the start index, inclusive.
         * @param end   the end index, exclusive.
         * @return the specified subsequence.
         * @throws IndexOutOfBoundsException if {@code start} or {@code end} are negative,
         *                                   if {@code end} is greater than {@code length()},
         *                                   or if {@code start} is greater than {@code end}
         */
        @Override
        public CharSequence subSequence(int start, int end) {
            return substring(start, end);
        }

        /**
         * Returns a new {@code String} that contains a subsequence of
         * characters currently contained in this sequence. The
         * substring begins at the specified {@code start} and
         * extends to the character at index {@code end - 1}.
         *
         * @param start The beginning index, inclusive.
         * @param end   The ending index, exclusive.
         * @return The new string.
         * @throws StringIndexOutOfBoundsException if {@code start}
         *                                         or {@code end} are negative or greater than
         *                                         {@code length()}, or {@code start} is
         *                                         greater than {@code end}.
         */
        public String substring(int start, int end) {
            if (start < 0)
                throw new StringIndexOutOfBoundsException(start);
            if (end > count)
                throw new StringIndexOutOfBoundsException(end);
            if (start > end)
                throw new StringIndexOutOfBoundsException(end - start);
            return new String(value, start, end - start);
        }

        /**
         * Inserts the string representation of a subarray of the {@code str}
         * array argument into this sequence. The subarray begins at the
         * specified {@code offset} and extends {@code len} {@code char}s.
         * The characters of the subarray are inserted into this sequence at
         * the position indicated by {@code index}. The length of this
         * sequence increases by {@code len} {@code char}s.
         *
         * @param index  position at which to insert subarray.
         * @param str    A {@code char} array.
         * @param offset the index of the first {@code char} in subarray to
         *               be inserted.
         * @param len    the number of {@code char}s in the subarray to
         *               be inserted.
         * @return This object
         * @throws StringIndexOutOfBoundsException if {@code index}
         *                                         is negative or greater than {@code length()}, or
         *                                         {@code offset} or {@code len} are negative, or
         *                                         {@code (offset+len)} is greater than
         *                                         {@code str.length}.
         */
        public Buffer insert(int index, char[] str, int offset,
                             int len) {
            if ((index < 0) || (index > length()))
                throw new StringIndexOutOfBoundsException(index);
            if ((offset < 0) || (len < 0) || (offset > str.length - len))
                throw new StringIndexOutOfBoundsException(
                        "offset " + offset + ", len " + len + ", str.length "
                                + str.length);
            ensureCapacityInternal(count + len);
            System.arraycopy(value, index, value, index + len, count - index);
            System.arraycopy(str, offset, value, index, len);
            count += len;
            return this;
        }

        /**
         * Inserts the string representation of the {@code Object}
         * argument into this character sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(Object)},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param obj    an {@code Object}.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, Object obj) {
            return insert(offset, String.valueOf(obj));
        }

        /**
         * Inserts the string into this character sequence.
         * <p>
         * The characters of the {@code String} argument are inserted, in
         * order, into this sequence at the indicated offset, moving up any
         * characters originally above that position and increasing the length
         * of this sequence by the length of the argument. If
         * {@code str} is {@code null}, then the four characters
         * {@code "null"} are inserted into this sequence.
         * <p>
         * The character at index <i>k</i> in the new character sequence is
         * equal to:
         * <ul>
         * <li>the character at index <i>k</i> in the old character sequence, if
         * <i>k</i> is less than {@code offset}
         * <li>the character at index <i>k</i>{@code -offset} in the
         * argument {@code str}, if <i>k</i> is not less than
         * {@code offset} but is less than {@code offset+str.length()}
         * <li>the character at index <i>k</i>{@code -str.length()} in the
         * old character sequence, if <i>k</i> is not less than
         * {@code offset+str.length()}
         * </ul><p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param str    a string.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, String str) {
            if ((offset < 0) || (offset > length()))
                throw new StringIndexOutOfBoundsException(offset);
            if (str == null)
                str = "null";
            int len = str.length();
            ensureCapacityInternal(count + len);
            System.arraycopy(value, offset, value, offset + len, count - offset);
            char[] strVal = str.toCharArray();
            System.arraycopy(value, 0, strVal, offset, value.length);
            count += len;
            return this;
        }

        /**
         * Inserts the string representation of the {@code char} array
         * argument into this sequence.
         * <p>
         * The characters of the array argument are inserted into the
         * contents of this sequence at the position indicated by
         * {@code offset}. The length of this sequence increases by
         * the length of the argument.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(char[])},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param str    a character array.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, char[] str) {
            if ((offset < 0) || (offset > length()))
                throw new StringIndexOutOfBoundsException(offset);
            int len = str.length;
            ensureCapacityInternal(count + len);
            System.arraycopy(value, offset, value, offset + len, count - offset);
            System.arraycopy(str, 0, value, offset, len);
            count += len;
            return this;
        }

        /**
         * Inserts the specified {@code CharSequence} into this sequence.
         * <p>
         * The characters of the {@code CharSequence} argument are inserted,
         * in order, into this sequence at the indicated offset, moving up
         * any characters originally above that position and increasing the length
         * of this sequence by the length of the argument s.
         * <p>
         * The result of this method is exactly the same as if it were an
         * invocation of this object's
         * {@link #insert(int, CharSequence, int, int) insert}(dstOffset, s, 0, s.length())
         * method.
         * <p>
         * <p>If {@code s} is {@code null}, then the four characters
         * {@code "null"} are inserted into this sequence.
         *
         * @param dstOffset the offset.
         * @param s         the sequence to be inserted
         * @return a reference to this object.
         * @throws IndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int dstOffset, CharSequence s) {
            if (s == null)
                s = "null";
            if (s instanceof String)
                return this.insert(dstOffset, (String) s);
            return this.insert(dstOffset, s, 0, s.length());
        }

        /**
         * Inserts a subsequence of the specified {@code CharSequence} into
         * this sequence.
         * <p>
         * The subsequence of the argument {@code s} specified by
         * {@code start} and {@code end} are inserted,
         * in order, into this sequence at the specified destination offset, moving
         * up any characters originally above that position. The length of this
         * sequence is increased by {@code end - start}.
         * <p>
         * The character at index <i>k</i> in this sequence becomes equal to:
         * <ul>
         * <li>the character at index <i>k</i> in this sequence, if
         * <i>k</i> is less than {@code dstOffset}
         * <li>the character at index <i>k</i>{@code +start-dstOffset} in
         * the argument {@code s}, if <i>k</i> is greater than or equal to
         * {@code dstOffset} but is less than {@code dstOffset+end-start}
         * <li>the character at index <i>k</i>{@code -(end-start)} in this
         * sequence, if <i>k</i> is greater than or equal to
         * {@code dstOffset+end-start}
         * </ul><p>
         * The {@code dstOffset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         * <p>The start argument must be nonnegative, and not greater than
         * {@code end}.
         * <p>The end argument must be greater than or equal to
         * {@code start}, and less than or equal to the length of s.
         * <p>
         * <p>If {@code s} is {@code null}, then this method inserts
         * characters as if the s parameter was a sequence containing the four
         * characters {@code "null"}.
         *
         * @param dstOffset the offset in this sequence.
         * @param s         the sequence to be inserted.
         * @param start     the starting index of the subsequence to be inserted.
         * @param end       the end index of the subsequence to be inserted.
         * @return a reference to this object.
         * @throws IndexOutOfBoundsException if {@code dstOffset}
         *                                   is negative or greater than {@code this.length()}, or
         *                                   {@code start} or {@code end} are negative, or
         *                                   {@code start} is greater than {@code end} or
         *                                   {@code end} is greater than {@code s.length()}
         */
        public Buffer insert(int dstOffset, CharSequence s,
                             int start, int end) {
            if (s == null)
                s = "null";
            if ((dstOffset < 0) || (dstOffset > this.length()))
                throw new IndexOutOfBoundsException("dstOffset " + dstOffset);
            if ((start < 0) || (end < 0) || (start > end) || (end > s.length()))
                throw new IndexOutOfBoundsException(
                        "start " + start + ", end " + end + ", s.length() "
                                + s.length());
            int len = end - start;
            ensureCapacityInternal(count + len);
            System.arraycopy(value, dstOffset, value, dstOffset + len,
                    count - dstOffset);
            for (int i = start; i < end; i++)
                value[dstOffset++] = s.charAt(i);
            count += len;
            return this;
        }

        /**
         * Inserts the string representation of the {@code boolean}
         * argument into this sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(boolean)},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param b      a {@code boolean}.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, boolean b) {
            return insert(offset, String.valueOf(b));
        }

        /**
         * Inserts the string representation of the {@code char}
         * argument into this sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(char)},
         * and the character in that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param c      a {@code char}.
         * @return a reference to this object.
         * @throws IndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, char c) {
            ensureCapacityInternal(count + 1);
            System.arraycopy(value, offset, value, offset + 1, count - offset);
            value[offset] = c;
            count += 1;
            return this;
        }

        /**
         * Inserts the string representation of the second {@code int}
         * argument into this sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(int)},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param i      an {@code int}.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, int i) {
            return insert(offset, String.valueOf(i));
        }

        /**
         * Inserts the string representation of the {@code long}
         * argument into this sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(long)},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param l      a {@code long}.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, long l) {
            return insert(offset, String.valueOf(l));
        }

        /**
         * Inserts the string representation of the {@code float}
         * argument into this sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(float)},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param f      a {@code float}.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, float f) {
            return insert(offset, String.valueOf(f));
        }

        /**
         * Inserts the string representation of the {@code double}
         * argument into this sequence.
         * <p>
         * The overall effect is exactly as if the second argument were
         * converted to a string by the method {@link String#valueOf(double)},
         * and the characters of that string were then
         * {@link #insert(int, String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param offset the offset.
         * @param d      a {@code double}.
         * @return a reference to this object.
         * @throws StringIndexOutOfBoundsException if the offset is invalid.
         */
        public Buffer insert(int offset, double d) {
            return insert(offset, String.valueOf(d));
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            append(cbuf, off, len);
        }

        /**
         * Write a character `c` to this buf.
         *
         * Special note, this method is **NOT** the same with
         * {@link #append(int)}, which will append String representation
         * of passed in int, while this method, instead,
         * treats the int as a character
         *
         * @param c
         *      the character `c`
         */
        @Override
        public void write(int c) {
            append((char) c);
        }

        @Override
        public void write(char[] cbuf) {
            write(cbuf, 0, cbuf.length);
        }

        @Override
        public void write(String str) {
            write(str, 0, str.length());
        }

        @Override
        public void write(String str, int off, int len) {
            append(str, off, off + len);
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() {

        }

        /**
         * Returns the index within this string of the first occurrence of the
         * specified substring. The integer returned is the smallest value
         * <i>k</i> such that:
         * <pre>{@code
         * this.toString().startsWith(str, <i>k</i>)
         * }</pre>
         * is {@code true}.
         *
         * @param str any string.
         * @return if the string argument occurs as a substring within this
         * object, then the index of the first character of the first
         * such substring is returned; if it does not occur as a
         * substring, {@code -1} is returned.
         */
        public int indexOf(String str) {
            return indexOf(str, 0);
        }

        /**
         * Returns the index within this string of the first occurrence of the
         * specified substring, starting at the specified index.  The integer
         * returned is the smallest value {@code k} for which:
         * <pre>{@code
         *     k >= Math.min(fromIndex, this.length()) &&
         *                   this.toString().startsWith(str, k)
         * }</pre>
         * If no such value of <i>k</i> exists, then -1 is returned.
         *
         * @param str       the substring for which to search.
         * @param fromIndex the index from which to start the search.
         * @return the index within this string of the first occurrence of the
         * specified substring, starting at the specified index.
         */
        public int indexOf(String str, int fromIndex) {
            char[] buf = str.toCharArray();
            return S.indexOf(this.value, 0, count, buf, 0, buf.length, fromIndex);
        }

        /**
         * Returns the index within this string of the rightmost occurrence
         * of the specified substring.  The rightmost empty string "" is
         * considered to occur at the index value {@code this.length()}.
         * The returned index is the largest value <i>k</i> such that
         * <pre>{@code
         * this.toString().startsWith(str, k)
         * }</pre>
         * is true.
         *
         * @param str the substring to search for.
         * @return if the string argument occurs one or more times as a substring
         * within this object, then the index of the first character of
         * the last such substring is returned. If it does not occur as
         * a substring, {@code -1} is returned.
         */
        public int lastIndexOf(String str) {
            return lastIndexOf(str, count);
        }

        /**
         * Returns the index within this string of the last occurrence of the
         * specified substring. The integer returned is the largest value <i>k</i>
         * such that:
         * <pre>{@code
         *     k <= Math.min(fromIndex, this.length()) &&
         *                   this.toString().startsWith(str, k)
         * }</pre>
         * If no such value of <i>k</i> exists, then -1 is returned.
         *
         * @param str       the substring to search for.
         * @param fromIndex the index to start the search from.
         * @return the index within this sequence of the last occurrence of the
         * specified substring.
         */
        public int lastIndexOf(String str, int fromIndex) {
            char[] buf = str.toCharArray();
            return S.lastIndexOf(value, 0, count, buf, 0, buf.length, fromIndex);
        }

        /**
         * Causes this character sequence to be replaced by the reverse of
         * the sequence. If there are any surrogate pairs included in the
         * sequence, these are treated as single characters for the
         * reverse operation. Thus, the order of the high-low surrogates
         * is never reversed.
         * <p>
         * Let <i>n</i> be the character length of this character sequence
         * (not the length in {@code char} values) just prior to
         * execution of the {@code reverse} method. Then the
         * character at index <i>k</i> in the new character sequence is
         * equal to the character at index <i>n-k-1</i> in the old
         * character sequence.
         * <p>
         * <p>Note that the reverse operation may result in producing
         * surrogate pairs that were unpaired low-surrogates and
         * high-surrogates before the operation. For example, reversing
         * "\u005CuDC00\u005CuD800" produces "\u005CuD800\u005CuDC00" which is
         * a valid surrogate pair.
         *
         * @return a reference to this object.
         */
        public Buffer reverse() {
            boolean hasSurrogates = false;
            int n = count - 1;
            for (int j = (n - 1) >> 1; j >= 0; j--) {
                int k = n - j;
                char cj = value[j];
                char ck = value[k];
                value[j] = ck;
                value[k] = cj;
                if (Character.isSurrogate(cj) ||
                        Character.isSurrogate(ck)) {
                    hasSurrogates = true;
                }
            }
            if (hasSurrogates) {
                reverseAllValidSurrogatePairs();
            }
            return this;
        }

        /**
         * The maximum size of array to allocate (unless necessary).
         * Some VMs reserve some header words in an array.
         * Attempts to allocate larger arrays may result in
         * OutOfMemoryError: Requested array size exceeds VM limit
         */
        private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

        /**
         * For positive values of {@code minimumCapacity}, this method
         * behaves like {@code ensureCapacity}, however it is never
         * synchronized.
         * If {@code minimumCapacity} is non positive due to numeric
         * overflow, this method throws {@code OutOfMemoryError}.
         */
        private void ensureCapacityInternal(int minimumCapacity) {
            // overflow-conscious code
            if (minimumCapacity - value.length > 0) {
                value = Arrays.copyOf(value,
                        newCapacity(minimumCapacity));
            }
        }

        /**
         * Returns a capacity at least as large as the given minimum capacity.
         * Returns the current capacity increased by the same amount + 2 if
         * that suffices.
         * Will not return a capacity greater than {@code MAX_ARRAY_SIZE}
         * unless the given minimum capacity is greater than that.
         *
         * @param minCapacity the desired minimum capacity
         * @throws OutOfMemoryError if minCapacity is less than zero or
         *                          greater than Integer.MAX_VALUE
         */
        private int newCapacity(int minCapacity) {
            // overflow-conscious code
            int newCapacity = (value.length << 1) + 2;
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }
            return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
                    ? hugeCapacity(minCapacity)
                    : newCapacity;
        }

        private int hugeCapacity(int minCapacity) {
            if (Integer.MAX_VALUE - minCapacity < 0) { // overflow
                throw new OutOfMemoryError();
            }
            return (minCapacity > MAX_ARRAY_SIZE)
                    ? minCapacity : MAX_ARRAY_SIZE;
        }

        /**
         * Outlined helper method for reverse()
         */
        private void reverseAllValidSurrogatePairs() {
            for (int i = 0; i < count - 1; i++) {
                char c2 = value[i];
                if (Character.isLowSurrogate(c2)) {
                    char c1 = value[i + 1];
                    if (Character.isHighSurrogate(c1)) {
                        value[i++] = c1;
                        value[i] = c2;
                    }
                }
            }
        }


        /**
         * Returns a string representing the data in this sequence.
         * A new {@code String} object is allocated and initialized to
         * contain the character sequence currently represented by this
         * object. This {@code String} is then returned. Subsequent
         * changes to this sequence do not affect the contents of the
         * {@code String}.
         * <p>
         * After this method is called, the buffer of this Buffer
         * instance will be reset to 0, meaning this Buffer is
         * consumed
         *
         * @return a string representation of this sequence of characters.
         */
        @Override
        public String toString() {
            // Create a copy, don't share the array
            String retval = new String(value, 0, count);
            this.consumed = true;
            return retval;
        }

        public String view() {
            return new String(value, 0, count);
        }

        /**
         * Needed by {@code String} for the contentEquals method.
         */
        final char[] getValue() {
            return value;
        }


        final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
                99999999, 999999999, Integer.MAX_VALUE};

        // Requires positive x
        static int stringSize(int x) {
            for (int i = 0; ; i++)
                if (x <= sizeTable[i])
                    return i + 1;
        }


        final static char[] DigitTens = {
                '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
                '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
                '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
                '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
                '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
                '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
                '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
                '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
                '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
        };

        final static char[] DigitOnes = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        };

        /**
         * All possible chars for representing a number as a String
         */
        final static char[] digits = {
                '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f', 'g', 'h',
                'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z'
        };

        /**
         * Places characters representing the integer i into the
         * character array buf. The characters are placed into
         * the buffer backwards starting with the least significant
         * digit at the specified index (exclusive), and working
         * backwards from there.
         * <p>
         * Will fail if i == Integer.MIN_VALUE
         */
        static void getChars(int i, int index, char[] buf) {
            int q, r;
            int charPos = index;
            char sign = 0;

            if (i < 0) {
                sign = '-';
                i = -i;
            }

            // Generate two digits per iteration
            while (i >= 65536) {
                q = i / 100;
                // really: r = i - (q * 100);
                r = i - ((q << 6) + (q << 5) + (q << 2));
                i = q;
                buf[--charPos] = DigitOnes[r];
                buf[--charPos] = DigitTens[r];
            }

            // Fall thru to fast mode for smaller numbers
            // assert(i <= 65536, i);
            for (; ; ) {
                q = (i * 52429) >>> (16 + 3);
                r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
                buf[--charPos] = digits[r];
                i = q;
                if (i == 0) break;
            }
            if (sign != 0) {
                buf[--charPos] = sign;
            }
        }

        // Requires positive x
        static int stringSize(long x) {
            long p = 10;
            for (int i = 1; i < 19; i++) {
                if (x < p)
                    return i;
                p = 10 * p;
            }
            return 19;
        }


        /**
         * Places characters representing the integer i into the
         * character array buf. The characters are placed into
         * the buffer backwards starting with the least significant
         * digit at the specified index (exclusive), and working
         * backwards from there.
         * <p>
         * Will fail if i == Long.MIN_VALUE
         */
        static void getChars(long i, int index, char[] buf) {
            long q;
            int r;
            int charPos = index;
            char sign = 0;

            if (i < 0) {
                sign = '-';
                i = -i;
            }

            // Get 2 digits/iteration using longs until quotient fits into an int
            while (i > Integer.MAX_VALUE) {
                q = i / 100;
                // really: r = i - (q * 100);
                r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
                i = q;
                buf[--charPos] = DigitOnes[r];
                buf[--charPos] = DigitTens[r];
            }

            // Get 2 digits/iteration using ints
            int q2;
            int i2 = (int) i;
            while (i2 >= 65536) {
                q2 = i2 / 100;
                // really: r = i2 - (q * 100);
                r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
                i2 = q2;
                buf[--charPos] = DigitOnes[r];
                buf[--charPos] = DigitTens[r];
            }

            // Fall thru to fast mode for smaller numbers
            // assert(i2 <= 65536, i2);
            for (; ; ) {
                q2 = (i2 * 52429) >>> (16 + 3);
                r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
                buf[--charPos] = digits[r];
                i2 = q2;
                if (i2 == 0) break;
            }
            if (sign != 0) {
                buf[--charPos] = sign;
            }
        }

        static void toSurrogates(int codePoint, char[] dst, int index) {
            // We write elements "backwards" to guarantee all-or-nothing
            dst[index + 1] = lowSurrogate(codePoint);
            dst[index] = highSurrogate(codePoint);
        }
    }

    public static String pluralize(Object word) {
        return Inflector.getInstance().pluralize(word);
    }

    public static String singularize(Object word) {
        return Inflector.getInstance().singularize(word);
    }

    public static class Binary extends $.T2<String, String> {
        public Binary(String _1, String _2) {
            super(_1, _2);
        }

        public Binary($.Tuple<String, String> t2) {
            super(t2._1, t2._2);
        }
    }

    public static class Pair extends Binary {
        public Pair(String _1, String _2) {
            super(_1, _2);
        }

        public Pair($.Tuple<String, String> tuple) {
            super(tuple);
        }
    }

    public static class T2 extends Pair {
        public T2(String _1, String _2) {
            super(_1, _2);
        }

        public T2($.T2<String, String> t2) {
            super(t2);
        }
    }

    public static class Triple extends $.T3<String, String, String> {
        public Triple(String _1, String _2, String _3) {
            super(_1, _2, _3);
        }
        public Triple($.Triple<String, String, String> t3) {
            super(t3._1, t3._2, t3._3);
        }
    }

    public static class T3 extends Triple {
        public T3(String _1, String _2, String _3) {
            super(_1, _2, _3);
        }

        public T3($.Triple<String, String, String> t3) {
            super(t3);
        }
    }


    public static class Quadruple extends $.T4<String, String, String, String> {
        public Quadruple(String _1, String _2, String _3, String _4) {
            super(_1, _2, _3, _4);
        }
        public Quadruple($.Quadruple<String, String, String, String> t4) {
            super(t4._1, t4._2, t4._3, t4._4);
        }
    }

    public static class T4 extends Quadruple {
        public T4(String _1, String _2, String _3, String _4) {
            super(_1, _2, _3, _4);
        }

        public T4($.Quadruple<String, String, String, String> t4) {
            super(t4);
        }
    }

    public static class Quintuple extends $.T5<String, String, String, String, String> {
        public Quintuple(String _1, String _2, String _3, String _4, String _5) {
            super(_1, _2, _3, _4, _5);
        }
        public Quintuple($.Quintuple<String, String, String, String, String> t5) {
            super(t5._1, t5._2, t5._3, t5._4, t5._5);
        }
    }

    public static class T5 extends Quintuple {
        public T5(String _1, String _2, String _3, String _4, String _5) {
            super(_1, _2, _3, _4, _5);
        }

        public T5($.Quintuple<String, String, String, String, String> t5) {
            super(t5);
        }
    }

    public interface List extends C.List<String> {
    }

    public static class Val extends $.Val<String> implements List {
        public Val(String value) {
            super(value);
        }
    }

    public static class Var extends $.Var<String> implements List {
        public Var(String value) {
            super(value);
        }
    }

    public static final List EMPTY_LIST = new Nil.EmptyStringList();

    /**
     * Returns an empty immutable list
     *
     * @return the empty list
     */
    public static List list() {
        return EMPTY_LIST;
    }

    public static List list(String s) {
        return val(s);
    }

    public static List list(String s1, String s2) {
        return ImmutableStringList.of(new String[]{s1, s2});
    }

    public static List list(String s1, String s2, String ... sa) {
        return ImmutableStringList.of($.concat(new String[]{s1, s2}, sa));
    }

    public static List list(Iterable<String> iterable) {
        return ImmutableStringList.of(iterable);
    }

    public static List listOf(String... sa) {
        return ImmutableStringList.of(sa);
    }

    public static List newList() {
        return new DelegatingStringList(10);
    }

    public static List newList(Iterable<String> iterable) {
        return new DelegatingStringList(iterable);
    }

    public static List newList(String string) {
        List list = new DelegatingStringList(10);
        list.add(string);
        return list;
    }

    public static List newList(String s1, String s2) {
        List list = new DelegatingStringList(10);
        list.add(s1);
        list.add(s2);
        return list;
    }

    public static List newList(String s1, String s2, String s3) {
        List list = new DelegatingStringList(10);
        list.add(s1);
        list.add(s2);
        list.add(s3);
        return list;
    }

    public static List newList(String s1, String s2, String s3, String... sa) {
        List list = newList(s1, s2, s3);
        for (String s : sa) {
            list.add(s);
        }
        return list;
    }

    public static List newListOf(String[] sa) {
        List list = new DelegatingStringList(sa.length);
        for (String s : sa) {
            list.add(s);
        }
        return list;
    }

    public static Var var(String s) {
        return new Var(s);
    }

    public static Val val(String s) {
        return new Val(s);
    }

    public static void main(String[] args) {
        System.out.println(S.join(new int[]{1,2,3}).by("-").get());
    }


    // Note we must move this down here as when it invokes $.concat
    // it will call static constructor of $ and in turn call back
    // to S static methods which involves `_buf` by when is not
    // initialized yet
    /**
     * digits plus alphabetic characters
     */
    public static final char[] ALPHANUMERICS = $.concat(DIGITS, ALPHABETICS);
}
