package org.osgl.util;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Character.highSurrogate;
import static java.lang.Character.lowSurrogate;

/**
 * String utilities
 */
public class S {

    /**
     * The invisible separator used by program: "\u0000"
     */
    public static final String HSEP = "\u0000";

    /**
     * A commonly used separator: [,;:\\s]+
     */
    public static final String COMMON_SEP = "[,;:\\s]+";

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
     * alias of {@link #empty(String)}
     *
     * @param s the string to be checked
     * @return true if `s` is `null` or `empty`
     */
    public static boolean isEmpty(String s) {
        return empty(s);
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
    public static boolean isAllBlank(String ... sa) {
        return allBlank(sa);
    }

    /**
     * Check if all of the specified string is {@link #blank(String) blank}
     *
     * @param sa the strings to be checked
     * @return true if all of the specified string is blank
     */
    public static boolean allBlank(String ... sa) {
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
    public static boolean isAnyBlank(String ... sa) {
        return anyBlank(sa);
    }

    /**
     * Check if anyone of the specified string is {@link #empty(String) blank}
     *
     * @param sa the strings to be checked
     * @return <code>true</code> if anyone of the specified string is blank
     */
    public static boolean anyBlank(String ... sa) {
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
    public static boolean noBlank(String ... sa) {
        return !anyBlank(sa);
    }

    /**
     * Check if a string is integer or long
     * @param s the string
     * @return {@code true} if the string is integer or long
     */
    public static boolean isIntOrLong(String s) {
        return N.isInt(s);
    }

    /**
     * Check if a string is integer or long
     * @param s the string
     * @return {@code true} if the string is integer or long
     */
    public static boolean isInt(String s) {
        return N.isInt(s);
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

    public static String concat(String s1, String s2, String s3, String s4, String s5, String ... extra) {
        S.Buffer sb = S.buffer(s1).a(s2).a(s3).a(s4).a(s5);
        for (String s : extra) {
            sb.a(s);
        }
        return sb.toString();
    }

    public static String concat(Object o1, Object o2, Object o3, Object o4, Object o5, Object ... extra) {
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

    public static boolean endsWith(String string, String suffix) {
        return string.endsWith(suffix);
    }

    public static boolean endsWith(String string, char suffix) {
        return string.charAt(string.length() - 1) == suffix;
    }

    public static boolean startsWith(String string, String prefix) {
        return string.startsWith(prefix);
    }

    public static boolean startsWith(String string, char prefix) {
        return string.charAt(0) == prefix;
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
                int len = slen * times;
                StringBuilder sb = len > 100 ? builder() : newSizedBuilder(len);
                for (int i = 0; i < times; ++i) {
                    sb.append(s);
                }
                return sb.toString();
        }
    }

    /**
     * Alias of {@link #join(String, int)}
     * @param s     the string to be joined
     * @param times the times the string to be joined
     * @return the result
     */
    public static String times(String s, int times) {
        return join(s, times);
    }

    /**
     * Return a string composed of `times` of char `c`
     * @param c the character
     * @param times the number of times the c in the string returned
     * @return the string as described
     */
    public static String times(char c, int times) {
        char[] ca = new char[times];
        for (int i = 0; i < times; ++i) {
            ca[i] = c;
        }
        return new String(ca);
    }

    public static String quote(String s, char quote) {
        if (null == s) {
            return String.valueOf(new char[]{quote, quote});
        }
        return S.sizedBuffer(s.length() + 2).append(quote).append(s).append(quote).toString();
    }

    public static String quote(String s, String quote) {
        if (null == s) {
            return times(quote, 2);
        }
        return S.concat(quote, s, quote);
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
        return s0.substring(i + search.length(), s0.length());
    }

    public static String afterFirst(String s0, String search) {
        if (null == s0) {
            return "";
        }
        int i = s0.indexOf(search);
        if (i == -1) {
            return "";
        }
        return s0.substring(i + search.length(), s0.length());
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

    /**
     * Count how many times a search string occurred in the give string
     *
     * @param s string to be searched
     * @param search the search token
     * @return the times the search token appeared in `s` without overlap calculation
     */
    public static int count(String s, String search) {
        return count(s, search, false);
    }

    /**
     * Count how many times a search string occurred in the give string
     *
     * @param s string to be searched
     * @param search the search token
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
            char[] buf = Unsafe.bufOf(s);
            char[] newBuf = unsafeCapFirst(buf, 0, buf.length);
            if (newBuf == buf) return s;
            return Unsafe.stringOf(newBuf);
        } catch (Exception e) {
            return capFirst(s);
        }
    }

    /**
     * Convert the char at begin position in the buf to upper case.
     *
     * If char is already upper case, then it returns the buf directly, otherwise,
     * it returns an new char array copy from begin to end
     * @param buf
     * @param begin start inclusive
     * @param end stop exclusive
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
     * @param s1 string 1
     * @param s2 String 2
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
     * @param s1 string 1
     * @param s2 string 2
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
     * @param s1 string 1
     * @param s2 String 2
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
     * @param s1 string 1
     * @param s2 string 2
     * @param modifier the modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return <code>true</code> if s1 equals to s2 as per modifier
     */
    public static boolean isEqual(String s1, String s2, int modifier) {
        return equal(s1, s2, modifier);
    }

    /**
     * Strip the prefix and suffix from an object's String representation and
     * return the result
     * <p>For example: </p>
     * <pre><code>Object o = "xxBByy";
     * String s = S.strip(o, "xx", "yy")</code></pre>
     * <p>At the end above code, <code>s</code> should be "BB"</p>
     *
     * @param o the object to which string representation will be used
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
     * Left pad a string with character specified
     * @param s the string
     * @param c the character
     * @param number the number of character to pad to the left
     * @return an new string with specified number of character `c` padded to `s` at left
     */
    public static String padLeft(String s, char c, int number) {
        return S.concat(S.times(c, number), s);
    }

    /**
     * Left pad a string with number of space specified
     * @param s the string
     * @param number the number of space to left pad to `s`
     * @return the string as described
     */
    public static String padLeft(String s, int number) {
        return padLeft(s, ' ', number);
    }

    /**
     * Alias of {@link #padLeft(String, char, int)}
     * @param s the string
     * @param c the char
     * @param number number of char to be left pad to `s`
     * @return the string as described above
     */
    public static String lpad(String s, char c, int number) {
        return padLeft(s, c, number);
    }

    /**
     * Alias of {@link #padLeft(String, int)}
     * @param s the string
     * @param number the number of space to left pad to `s`
     * @return the string as described
     */
    public static String lpad(String s, int number) {
        return padLeft(s, ' ', number);
    }

    /**
     * Right pad a string with character specified
     * @param s the string
     * @param c the character
     * @param number the number of character to pad to the left
     * @return an new string with specified number of character `c` padded to `s` at right
     */
    public static String padRight(String s, char c, int number) {
        return S.concat(s, S.times(c, number));
    }

    /**
     * Right pad a string with number of space specified
     * @param s the string
     * @param number the number of space to right pad to `s`
     * @return the string as described
     */
    public static String padRight(String s, int number) {
        return padRight(s, ' ', number);
    }

    /**
     * Alias of {@link #padRight(String, char, int)}
     * @param s the string
     * @param c the char
     * @param number number of char to be left pad to `s`
     * @return the string as described above
     */
    public static String rpad(String s, char c, int number) {
        return padLeft(s, c, number);
    }

    /**
     * Alias of {@link #padRight(String, int)}
     * @param s the string
     * @param number the number of space to left pad to `s`
     * @return the string as described
     */
    public static String rpad(String s, int number) {
        return padLeft(s, ' ', number);
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
     * Get the extension of a filename
     *
     * @param fileName the (supposed) file name
     * @return the extension from the file name
     */
    public static String fileExtension(String fileName) {
        return S.after(fileName, ".");
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate random string.
     * The generated string is safe to be used as filename
     *
     * @param len the number of chars in the returned string
     * @return a random string with specified number of chars
     */
    public static String random(int len) {
        final char[] chars = {'0', '1', '2', '3', '4',
                              '5', '6', '7', '8', '9', '$', '#', '^', '&', '_',
                              'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                              'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                              'u', 'v', 'w', 'x', 'y', 'z',
                              'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                              'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                              'U', 'V', 'W', 'X', 'Y', 'Z',
                              '~', '!', '@'};

        final int max = chars.length;
        Random r = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(len);
        while (len-- > 0) {
            int i = r.nextInt(max);
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * @return a random string with 8 chars
     */
    public static final String random() {
        return random(8);
    }

    /**
     * Get string representation of an object instance
     *
     * @param o      the instance to be displayed
     * @param quoted whether display quotation mark
     * @return the string representation of object
     */
    public final static String string(Object o, boolean quoted) {
        return quoted ? String.format("\"%s\"", o) : null == o ? "" : o.toString();
    }

    public static String string(Object o) {
        if (null == o) {
            return "";
        }
        return o.toString();
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
            return new Buffer();
        }
    };

    /**
     * Returns a {@link Buffer} instance. If the thread local instance is consumed already
     * then return it. Otherwise, return an new `Buffer` instance
     * @return a `Buffer` instance as described above
     */
    public static Buffer buffer() {
        Buffer sb = _buf.get();
        return sb.consumed() ? sb.reset() : new Buffer();
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

    public static Buffer buffer(Object o) {
        return buffer().append(o);
    }

    public static Buffer buffer(String s) {
        return buffer().append(s);
    }


    /**
     * Return an new StringBuilder instance
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

    /**
     * Search for strings. Copied from jdk String.indexOf.
     * The source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source        the characters being searched.
     * @param   sourceOffset  offset of the source string.
     * @param   sourceCount   count of the source string.
     * @param   target        the characters being searched for.
     * @param   targetOffset  offset of the target string.
     * @param   targetCount   count of the target string.
     * @param   fromIndex     the index to begin searching from.
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
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
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

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
        try {
            return Unsafe.bufOf(s);
        } catch (Exception e) {
            return s.toCharArray();
        }
    }

    public static enum F {
        ;

        public static $.F2<String, String, Boolean> STARTS_WITH = new $.F2<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws NotAppliedException, $.Break {
                return s.startsWith(s2);
            }
        };

        public static $.Predicate<String> startsWith(final String prefix) {
            return $.predicate(STARTS_WITH.curry(prefix));
        }

        public static $.F2<String, String, Boolean> ENDS_WITH = new $.F2<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws NotAppliedException, $.Break {
                return s.endsWith(s2);
            }
        };

        public static $.Predicate<String> endsWith(final String suffix) {
            return $.predicate(ENDS_WITH.curry(suffix));
        }

        public static $.F2<String, String, Boolean> CONTAINS = new $.F2<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws NotAppliedException, $.Break {
                return s.contains(s2);
            }
        };

        public static $.Predicate<String> contains(final String search) {
            return $.predicate(CONTAINS.curry(search));
        }

        public static $.F1<String, String> TO_UPPERCASE = new $.F1<String, String>() {
            @Override
            public String apply(String s) throws NotAppliedException, $.Break {
                return s.toUpperCase();
            }
        };

        public static $.F1<String, String> TO_LOWERCASE = new $.F1<String, String>() {
            @Override
            public String apply(String s) throws NotAppliedException, $.Break {
                return s.toLowerCase();
            }
        };

        public static $.Transformer<String, String> NULL_SAFE = new $.Transformer<String, String>() {
            @Override
            public String transform(String s) {
                return null == s ? "" : s;
            }
        };

        public static $.F1<String, String> TRIM = new $.F1<String, String>() {
            @Override
            public String apply(String s) throws NotAppliedException, $.Break {
                return s.trim();
            }
        };

        public static $.F1<String, String> CAP_FIRST = new $.F1<String, String>() {
            @Override
            public String apply(String s) throws NotAppliedException, $.Break {
                return S.capFirst(s);
            }
        };

        public static $.Predicate<String> IS_EMPTY = new $.Predicate<String>() {
            @Override
            public boolean test(String s) throws NotAppliedException, $.Break {
                return S.isEmpty(s);
            }
        };

        public static $.Predicate<String> IS_BLANK = new $.Predicate<String>() {
            @Override
            public boolean test(String s) {
                return S.isBlank(s);
            }
        };

        public static $.Predicate<String> NOT_EMPTY = IS_EMPTY.negate();

        public static $.F2<String, Integer, String> MAX_LENGTH = new $.F2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) throws NotAppliedException, $.Break {
                return S.maxLength(s, n);
            }
        };

        /**
         * A split function that use the {@link #COMMON_SEP} to split Strings
         */
        public static $.F1<String, List<String>> SPLIT = split(COMMON_SEP);

        public static $.F1<String, List<String>> split(final String sep) {
            return new $.F1<String, List<String>>() {
                @Override
                public List<String> apply(String s) throws NotAppliedException, $.Break {
                    return C.listOf(s.split(sep));
                }
            };
        }

        public static $.F1<String, String> maxLength(int n) {
            return MAX_LENGTH.curry(n);
        }

        public static $.F2<String, Integer, String> LAST = new $.F2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) throws NotAppliedException, $.Break {
                return S.last(s, n);
            }
        };

        public static $.F1<String, String> last(int n) {
            return LAST.curry(n);
        }

        public static $.F2<String, Integer, String> FIRST = new $.F2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) throws NotAppliedException, $.Break {
                return S.first(s, n);
            }
        };

        public static $.F1<String, String> first(final int n) {
            return FIRST.curry(n);
        }

        public static $.F0<String> RANDOM = new $.F0<String>() {
            @Override
            public String apply() throws NotAppliedException, $.Break {
                return S.random();
            }
        };

        public static $.F1<Integer, String> RANDOM_N = new $.F1<Integer, String>() {
            @Override
            public String apply(Integer n) throws NotAppliedException, $.Break {
                return S.random(n);
            }
        };

        public static $.F0<String> random() {
            return RANDOM;
        }

        public static $.F0<String> random(int n) {
            return RANDOM_N.curry(n);
        }

        public static $.F1<String, Integer> LENGTH = new $.F1<String, Integer>() {
            @Override
            public Integer apply(String s) throws NotAppliedException, $.Break {
                return s.length();
            }
        };

        public static $.F1<String, String> append(final String appendix) {
            return new $.F1<String, String>() {
                @Override
                public String apply(String s) throws NotAppliedException, $.Break {
                    return S.newBuilder(s).append(appendix).toString();
                }
            };
        }

        public static $.F1<String, String> prepend(final String prependix) {
            return new $.F1<String, String>() {
                @Override
                public String apply(String s) throws NotAppliedException, $.Break {
                    return S.newBuilder(prependix).append(s).toString();
                }
            };
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
    public static class Buffer implements Appendable, CharSequence {

        /**
         * track if {@link #toString()} method is called
         */
        private boolean consumed;

        /**
         * The count is the number of characters used.
         */
        private int count;

        /**
         * The value is used for character storage.
         */
        private char[] value;

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
            consumed = true;
        }

        public boolean consumed() {
            return consumed;
        }

        private Buffer consume() {
            this.consumed = true;
            return this;
        }

        public Buffer reset() {
            this.consumed = false;
            this.setLength(0);
            return this;
        }

        /**
         * Returns the length (character count).
         *
         * @return  the length of the sequence of characters currently
         *          represented by this object
         */
        @Override
        public int length() {
            return count;
        }

        /**
         * Returns the current capacity. The capacity is the amount of storage
         * available for newly inserted characters, beyond which an allocation
         * will occur.
         *
         * @return  the current capacity
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
         * @param   minimumCapacity   the minimum desired capacity.
         */
        public void ensureCapacity(int minimumCapacity) {
            if (minimumCapacity > 0)
                ensureCapacityInternal(minimumCapacity);
        }

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
         * The maximum size of array to allocate (unless necessary).
         * Some VMs reserve some header words in an array.
         * Attempts to allocate larger arrays may result in
         * OutOfMemoryError: Requested array size exceeds VM limit
         */
        private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

        /**
         * Returns a capacity at least as large as the given minimum capacity.
         * Returns the current capacity increased by the same amount + 2 if
         * that suffices.
         * Will not return a capacity greater than {@code MAX_ARRAY_SIZE}
         * unless the given minimum capacity is greater than that.
         *
         * @param  minCapacity the desired minimum capacity
         * @throws OutOfMemoryError if minCapacity is less than zero or
         *         greater than Integer.MAX_VALUE
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
         *
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
         * @param      newLength   the new length
         * @throws     IndexOutOfBoundsException  if the
         *               {@code newLength} argument is negative.
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
         *
         * <p>If the {@code char} value specified by the index is a
         * <a href="Character.html#unicode">surrogate</a>, the surrogate
         * value is returned.
         *
         * @param      index   the index of the desired {@code char} value.
         * @return     the {@code char} value at the specified index.
         * @throws     IndexOutOfBoundsException  if {@code index} is
         *             negative or greater than or equal to {@code length()}.
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
         *
         * <p> If the {@code char} value specified at the given index
         * is in the high-surrogate range, the following index is less
         * than the length of this sequence, and the
         * {@code char} value at the following index is in the
         * low-surrogate range, then the supplementary code point
         * corresponding to this surrogate pair is returned. Otherwise,
         * the {@code char} value at the given index is returned.
         *
         * @param      index the index to the {@code char} values
         * @return     the code point value of the character at the
         *             {@code index}
         * @exception  IndexOutOfBoundsException  if the {@code index}
         *             argument is negative or not less than the length of this
         *             sequence.
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
         *
         * <p> If the {@code char} value at {@code (index - 1)}
         * is in the low-surrogate range, {@code (index - 2)} is not
         * negative, and the {@code char} value at {@code (index -
         * 2)} is in the high-surrogate range, then the
         * supplementary code point value of the surrogate pair is
         * returned. If the {@code char} value at {@code index -
         * 1} is an unpaired low-surrogate or a high-surrogate, the
         * surrogate value is returned.
         *
         * @param     index the index following the code point that should be returned
         * @return    the Unicode code point value before the given index.
         * @exception IndexOutOfBoundsException if the {@code index}
         *            argument is less than 1 or greater than the length
         *            of this sequence.
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
         * the text range.
         * @param endIndex the index after the last {@code char} of
         * the text range.
         * @return the number of Unicode code points in the specified text
         * range
         * @exception IndexOutOfBoundsException if the
         * {@code beginIndex} is negative, or {@code endIndex}
         * is larger than the length of this sequence, or
         * {@code beginIndex} is larger than {@code endIndex}.
         */
        public int codePointCount(int beginIndex, int endIndex) {
            if (beginIndex < 0 || endIndex > count || beginIndex > endIndex) {
                throw new IndexOutOfBoundsException();
            }
            return Character.codePointCount(value, beginIndex, endIndex-beginIndex);
        }

        /**
         * Returns the index within this sequence that is offset from the
         * given {@code index} by {@code codePointOffset} code
         * points. Unpaired surrogates within the text range given by
         * {@code index} and {@code codePointOffset} count as
         * one code point each.
         *
         * @param index the index to be offset
         * @param codePointOffset the offset in code points
         * @return the index within this sequence
         * @exception IndexOutOfBoundsException if {@code index}
         *   is negative or larger then the length of this sequence,
         *   or if {@code codePointOffset} is positive and the subsequence
         *   starting with {@code index} has fewer than
         *   {@code codePointOffset} code points,
         *   or if {@code codePointOffset} is negative and the subsequence
         *   before {@code index} has fewer than the absolute value of
         *   {@code codePointOffset} code points.
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
         * @param      srcBegin   start copying at this offset.
         * @param      srcEnd     stop copying at this offset.
         * @param      dst        the array to copy the data into.
         * @param      dstBegin   offset into {@code dst}.
         * @throws     IndexOutOfBoundsException  if any of the following is true:
         *             <ul>
         *             <li>{@code srcBegin} is negative
         *             <li>{@code dstBegin} is negative
         *             <li>the {@code srcBegin} argument is greater than
         *             the {@code srcEnd} argument.
         *             <li>{@code srcEnd} is greater than
         *             {@code this.length()}.
         *             <li>{@code dstBegin+srcEnd-srcBegin} is greater than
         *             {@code dst.length}
         *             </ul>
         */
        public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
        {
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
         * @param      index   the index of the character to modify.
         * @param      ch      the new character.
         * @throws     IndexOutOfBoundsException  if {@code index} is
         *             negative or greater than or equal to {@code length()}.
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
         * @param   obj   an {@code Object}.
         * @return  a reference to this object.
         */
        public Buffer append(Object obj) {
            return append(String.valueOf(obj));
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
         * @param   str   a string.
         * @return  a reference to this object.
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
                return this.append((String)s);
            if (s instanceof Buffer)
                return this.append((Buffer)s);

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
                return this.prepend((String)s);
            if (s instanceof Buffer)
                return this.prepend((Buffer)s);
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
         * @param   s the sequence to append.
         * @param   start   the starting index of the subsequence to be appended.
         * @param   end     the end index of the subsequence to be appended.
         * @return  a reference to this object.
         * @throws     IndexOutOfBoundsException if
         *             {@code start} is negative, or
         *             {@code start} is greater than {@code end} or
         *             {@code end} is greater than {@code s.length()}
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
         * @param   str   the characters to be appended.
         * @return  a reference to this object.
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
         * to a string by the method {@link String#valueOf(char[],int,int)},
         * and the characters of that string were then
         * {@link #append(String) appended} to this character sequence.
         *
         * @param   str      the characters to be appended.
         * @param   offset   the index of the first {@code char} to append.
         * @param   len      the number of {@code char}s to append.
         * @return  a reference to this object.
         * @throws IndexOutOfBoundsException
         *         if {@code offset < 0} or {@code len < 0}
         *         or {@code offset+len > str.length}
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
         * @param   b   a {@code boolean}.
         * @return  a reference to this object.
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
         * @param   c   a {@code char}.
         * @return  a reference to this object.
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
         * @param   i   an {@code int}.
         * @return  a reference to this object.
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
         * @param   l   a {@code long}.
         * @return  a reference to this object.
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
         * @param   f   a {@code float}.
         * @return  a reference to this object.
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
         * @param   d   a {@code double}.
         * @return  a reference to this object.
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
         * @param      start  The beginning index, inclusive.
         * @param      end    The ending index, exclusive.
         * @return     This object.
         * @throws     StringIndexOutOfBoundsException  if {@code start}
         *             is negative, greater than {@code length()}, or
         *             greater than {@code end}.
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
                System.arraycopy(value, start+len, value, start, count-end);
                count -= len;
            }
            return this;
        }

        /**
         * Appends the string representation of the {@code codePoint}
         * argument to this sequence.
         *
         * <p> The argument is appended to the contents of this sequence.
         * The length of this sequence increases by
         * {@link Character#charCount(int) Character.charCount(codePoint)}.
         *
         * <p> The overall effect is exactly as if the argument were
         * converted to a {@code char} array by the method
         * {@link Character#toChars(int)} and the character in that array
         * were then {@link #append(char[]) appended} to this character
         * sequence.
         *
         * @param   codePoint   a Unicode code point
         * @return  a reference to this object.
         * @exception IllegalArgumentException if the specified
         * {@code codePoint} isn't a valid Unicode code point
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
         *
         * <p>Note: If the character at the given index is a supplementary
         * character, this method does not remove the entire character. If
         * correct handling of supplementary characters is required,
         * determine the number of {@code char}s to remove by calling
         * {@code Character.charCount(thisSequence.codePointAt(index))},
         * where {@code thisSequence} is this sequence.
         *
         * @param       index  Index of {@code char} to remove
         * @return      This object.
         * @throws      StringIndexOutOfBoundsException  if the {@code index}
         *              is negative or greater than or equal to
         *              {@code length()}.
         */
        public Buffer deleteCharAt(int index) {
            if ((index < 0) || (index >= count))
                throw new StringIndexOutOfBoundsException(index);
            System.arraycopy(value, index+1, value, index, count-index-1);
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
         * @param      start    The beginning index, inclusive.
         * @param      end      The ending index, exclusive.
         * @param      str   String that will replace previous contents.
         * @return     This object.
         * @throws     StringIndexOutOfBoundsException  if {@code start}
         *             is negative, greater than {@code length()}, or
         *             greater than {@code end}.
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
         * @param      start    The beginning index, inclusive.
         * @return     The new string.
         * @throws     StringIndexOutOfBoundsException  if {@code start} is
         *             less than zero, or greater than the length of this object.
         */
        public String substring(int start) {
            return substring(start, count);
        }

        /**
         * Returns a new character sequence that is a subsequence of this sequence.
         *
         * <p> An invocation of this method of the form
         *
         * <pre>{@code
         * sb.subSequence(begin,&nbsp;end)}</pre>
         *
         * behaves in exactly the same way as the invocation
         *
         * <pre>{@code
         * sb.substring(begin,&nbsp;end)}</pre>
         *
         * This method is provided so that this class can
         * implement the {@link CharSequence} interface.
         *
         * @param      start   the start index, inclusive.
         * @param      end     the end index, exclusive.
         * @return     the specified subsequence.
         *
         * @throws  IndexOutOfBoundsException
         *          if {@code start} or {@code end} are negative,
         *          if {@code end} is greater than {@code length()},
         *          or if {@code start} is greater than {@code end}
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
         * @param      start    The beginning index, inclusive.
         * @param      end      The ending index, exclusive.
         * @return     The new string.
         * @throws     StringIndexOutOfBoundsException  if {@code start}
         *             or {@code end} are negative or greater than
         *             {@code length()}, or {@code start} is
         *             greater than {@code end}.
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
         * @param      index    position at which to insert subarray.
         * @param      str       A {@code char} array.
         * @param      offset   the index of the first {@code char} in subarray to
         *             be inserted.
         * @param      len      the number of {@code char}s in the subarray to
         *             be inserted.
         * @return     This object
         * @throws     StringIndexOutOfBoundsException  if {@code index}
         *             is negative or greater than {@code length()}, or
         *             {@code offset} or {@code len} are negative, or
         *             {@code (offset+len)} is greater than
         *             {@code str.length}.
         */
        public Buffer insert(int index, char[] str, int offset,
                             int len)
        {
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      obj      an {@code Object}.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * @param      offset   the offset.
         * @param      str      a string.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      str      a character array.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,CharSequence,int,int) insert}(dstOffset, s, 0, s.length())
         * method.
         *
         * <p>If {@code s} is {@code null}, then the four characters
         * {@code "null"} are inserted into this sequence.
         *
         * @param      dstOffset   the offset.
         * @param      s the sequence to be inserted
         * @return     a reference to this object.
         * @throws     IndexOutOfBoundsException  if the offset is invalid.
         */
        public Buffer insert(int dstOffset, CharSequence s) {
            if (s == null)
                s = "null";
            if (s instanceof String)
                return this.insert(dstOffset, (String)s);
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
         *
         * <p>If {@code s} is {@code null}, then this method inserts
         * characters as if the s parameter was a sequence containing the four
         * characters {@code "null"}.
         *
         * @param      dstOffset   the offset in this sequence.
         * @param      s       the sequence to be inserted.
         * @param      start   the starting index of the subsequence to be inserted.
         * @param      end     the end index of the subsequence to be inserted.
         * @return     a reference to this object.
         * @throws     IndexOutOfBoundsException  if {@code dstOffset}
         *             is negative or greater than {@code this.length()}, or
         *              {@code start} or {@code end} are negative, or
         *              {@code start} is greater than {@code end} or
         *              {@code end} is greater than {@code s.length()}
         */
        public Buffer insert(int dstOffset, CharSequence s,
                             int start, int end) {
            if (s == null)
                s = "null";
            if ((dstOffset < 0) || (dstOffset > this.length()))
                throw new IndexOutOfBoundsException("dstOffset "+dstOffset);
            if ((start < 0) || (end < 0) || (start > end) || (end > s.length()))
                throw new IndexOutOfBoundsException(
                        "start " + start + ", end " + end + ", s.length() "
                                + s.length());
            int len = end - start;
            ensureCapacityInternal(count + len);
            System.arraycopy(value, dstOffset, value, dstOffset + len,
                    count - dstOffset);
            for (int i=start; i<end; i++)
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      b        a {@code boolean}.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      c        a {@code char}.
         * @return     a reference to this object.
         * @throws     IndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      i        an {@code int}.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      l        a {@code long}.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      f        a {@code float}.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
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
         * {@link #insert(int,String) inserted} into this character
         * sequence at the indicated offset.
         * <p>
         * The {@code offset} argument must be greater than or equal to
         * {@code 0}, and less than or equal to the {@linkplain #length() length}
         * of this sequence.
         *
         * @param      offset   the offset.
         * @param      d        a {@code double}.
         * @return     a reference to this object.
         * @throws     StringIndexOutOfBoundsException  if the offset is invalid.
         */
        public Buffer insert(int offset, double d) {
            return insert(offset, String.valueOf(d));
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
         * @param   str   any string.
         * @return  if the string argument occurs as a substring within this
         *          object, then the index of the first character of the first
         *          such substring is returned; if it does not occur as a
         *          substring, {@code -1} is returned.
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
         * @param   str         the substring for which to search.
         * @param   fromIndex   the index from which to start the search.
         * @return  the index within this string of the first occurrence of the
         *          specified substring, starting at the specified index.
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
         * @param   str   the substring to search for.
         * @return  if the string argument occurs one or more times as a substring
         *          within this object, then the index of the first character of
         *          the last such substring is returned. If it does not occur as
         *          a substring, {@code -1} is returned.
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
         * @param   str         the substring to search for.
         * @param   fromIndex   the index to start the search from.
         * @return  the index within this sequence of the last occurrence of the
         *          specified substring.
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
         *
         * Let <i>n</i> be the character length of this character sequence
         * (not the length in {@code char} values) just prior to
         * execution of the {@code reverse} method. Then the
         * character at index <i>k</i> in the new character sequence is
         * equal to the character at index <i>n-k-1</i> in the old
         * character sequence.
         *
         * <p>Note that the reverse operation may result in producing
         * surrogate pairs that were unpaired low-surrogates and
         * high-surrogates before the operation. For example, reversing
         * "\u005CuDC00\u005CuD800" produces "\u005CuD800\u005CuDC00" which is
         * a valid surrogate pair.
         *
         * @return  a reference to this object.
         */
        public Buffer reverse() {
            boolean hasSurrogates = false;
            int n = count - 1;
            for (int j = (n-1) >> 1; j >= 0; j--) {
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

        /** Outlined helper method for reverse() */
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
         *
         * After this method is called, the buffer of this Buffer
         * instance will be reset to 0, meaning this Buffer is
         * consumed
         *
         * @return  a string representation of this sequence of characters.
         */
        @Override
        public String toString() {
            // Create a copy, don't share the array
            String retval = new String(value, 0, count);
            consume();
            return retval;
        }

        /**
         * Needed by {@code String} for the contentEquals method.
         */
        final char[] getValue() {
            return value;
        }


        final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
                99999999, 999999999, Integer.MAX_VALUE };

        // Requires positive x
        static int stringSize(int x) {
            for (int i=0; ; i++)
                if (x <= sizeTable[i])
                    return i+1;
        }


        final static char [] DigitTens = {
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
        } ;

        final static char [] DigitOnes = {
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
        } ;

        /**
         * All possible chars for representing a number as a String
         */
        final static char[] digits = {
                '0' , '1' , '2' , '3' , '4' , '5' ,
                '6' , '7' , '8' , '9' , 'a' , 'b' ,
                'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
                'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
                'o' , 'p' , 'q' , 'r' , 's' , 't' ,
                'u' , 'v' , 'w' , 'x' , 'y' , 'z'
        };

        /**
         * Places characters representing the integer i into the
         * character array buf. The characters are placed into
         * the buffer backwards starting with the least significant
         * digit at the specified index (exclusive), and working
         * backwards from there.
         *
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
                buf [--charPos] = DigitOnes[r];
                buf [--charPos] = DigitTens[r];
            }

            // Fall thru to fast mode for smaller numbers
            // assert(i <= 65536, i);
            for (;;) {
                q = (i * 52429) >>> (16+3);
                r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
                buf [--charPos] = digits [r];
                i = q;
                if (i == 0) break;
            }
            if (sign != 0) {
                buf [--charPos] = sign;
            }
        }

        // Requires positive x
        static int stringSize(long x) {
            long p = 10;
            for (int i=1; i<19; i++) {
                if (x < p)
                    return i;
                p = 10*p;
            }
            return 19;
        }


        /**
         * Places characters representing the integer i into the
         * character array buf. The characters are placed into
         * the buffer backwards starting with the least significant
         * digit at the specified index (exclusive), and working
         * backwards from there.
         *
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
                r = (int)(i - ((q << 6) + (q << 5) + (q << 2)));
                i = q;
                buf[--charPos] = DigitOnes[r];
                buf[--charPos] = DigitTens[r];
            }

            // Get 2 digits/iteration using ints
            int q2;
            int i2 = (int)i;
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
            for (;;) {
                q2 = (i2 * 52429) >>> (16+3);
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
            dst[index+1] = lowSurrogate(codePoint);
            dst[index] = highSurrogate(codePoint);
        }
    }
}
