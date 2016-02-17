package org.osgl.util;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/09/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class S {

    /**
     * The invisible separator used by program: "\u0000"
     */
    public static final String HSEP = "\u0000";

    /**
     * A commonly used separator: [,;:\\s]+
     */
    public static final String COMMON_SEP = "[,;:\\s]";

    public final static String fmt(String tmpl) {
        return tmpl;
    }

    /**
     * A handy alias for {@link String#format(String, Object...)}
     *
     * @param tmpl
     * @param args
     * @return the formatted string
     */
    public final static String fmt(String tmpl, Object... args) {
        if (0 == args.length) return tmpl;
        return String.format(tmpl, args);
    }

    /**
     * alias of {@link #empty(String)}
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return empty(s);
    }

    /**
     * Determine if a string is empty or null
     *
     * @param s
     * @return true if the string is null or empty (no spaces)
     */
    public static boolean empty(String s) {
        return (null == s || "".equals(s));
    }

    /**
     * Determine if a string is all blank or empty or null
     *
     * @param s
     * @return true if the string is null or empty or all blanks
     */
    public static boolean blank(String s) {
        return (null == s || "".equals(s.trim()));
    }

    /**
     * alias of {@link #blank(String)}
     *
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        return blank(s);
    }

    /**
     * alias of {@link #notEmpty(String)}
     *
     * @param s
     * @return true if <code>s</code> is <code>null</code> or empty
     */
    public static boolean isNotEmpty(String s) {
        return notEmpty(s);
    }

    /**
     * Antonym of {@link #empty(String)}
     *
     * @param s
     * @return true if <code>s</code> is not <code>null</code> or empty
     */
    public static boolean notEmpty(String s) {
        return !empty(s);
    }

    /**
     * Antonym of {@link #blank(String)}
     *
     * @param s
     * @return true if <code>s</code> is not <code>null</code> or empty or all in blank
     */
    public static boolean notBlank(String s) {
        return !blank(s);
    }

    /**
     * Antonym of {@link #blank(String)}
     *
     * @param s
     * @return true if <code>s</code> is not <code>null</code> or empty or all in blank
     */
    public static boolean isNotBlank(String s) {
        return !blank(s);
    }

    /**
     * Check if all of the specified string is {@link #empty(String) empty}
     *
     * @param sa
     * @return true if all of the specified string is empty
     */
    public static boolean isAllEmpty(String... sa) {
        return allEmpty(sa);
    }

    /**
     * Alias of {@link #isAllEmpty(String...)}
     *
     * @param sa
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
     * @param sa
     * @return true if all of the specified string is blank
     */
    public static boolean isAllBlank(String ... sa) {
        return allBlank(sa);
    }

    /**
     * Check if all of the specified string is {@link #blank(String) blank}
     *
     * @param sa
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
     * @param sa
     * @return <code>true</code> if anyone of the specified string is empty
     */
    public static boolean isAnyEmpty(String... sa) {
        return anyEmpty(sa);
    }

    /**
     * Alias of {@link #isAnyEmpty(String...)}
     *
     * @param sa
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
     * @param sa
     * @return <code>true</code> if anyone of the specified string is blank
     */
    public static boolean isAnyBlank(String ... sa) {
        return anyBlank(sa);
    }

    /**
     * Check if anyone of the specified string is {@link #empty(String) blank}
     *
     * @param sa
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
     * @param sa
     * @return <code>false</code> if anyone of the specified string is empty
     */
    public static boolean noEmpty(String... sa) {
        return !anyEmpty(sa);
    }

    /**
     * Antonym of {@link #anyBlank(String...)}
     *
     * @param sa
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
        return s.matches("-?\\d+(\\d+)?");
    }

    /**
     * Return the string of first N chars.
     * <p>If n is negative number, then return a string of the first N chars</p>
     * <p>If n is larger than the length of the string, then return the string</p>
     *
     * @param s
     * @param n
     * @return
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
        StringBuilder sb = new StringBuilder();

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
        StringBuilder sb = new StringBuilder();

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
                StringBuilder sb = builder(s);
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
                StringBuilder sb = builder(s);
                for (int i = 1; i < times; ++i) {
                    sb.append(s);
                }
                return sb.toString();
        }
    }

    /**
     * Alias of {@link #join(String, int)}
     */
    public static String times(String s, int times) {
        return join(s, times);
    }

    /**
     * <p>Return a string no longer than specified max length.
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param s   the original string
     * @param max the maximum length of the result
     * @return
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
     * @return
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
     * @param s
     * @param n
     * @return
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
     * @param s
     * @return
     */
    public static String trim(String s) {
        return null == s ? "" : s.trim();
    }

    /**
     * Count how many times a search string occurred in the give string
     *
     * @param s
     * @param search
     */
    public static int count(String s, String search) {
        return count(s, search, false);
    }

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

    public static String capFirst(String s) {
        if (null == s || "" == s) {
            return "";
        }
        return ("" + s.charAt(0)).toUpperCase() + s.substring(1);
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
     * @param s1
     * @param s2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean eq(String s1, String s2) {
        return equal(s1, s2, 0);
    }

    /**
     * Alias of {@link #equal(String, String, int)}
     *
     * @param s1
     * @param s2
     * @param modifier
     * @return <code>true</code> if s1 equals to s2 as per modifier
     */
    public static boolean eq(String s1, String s2, int modifier) {
        return equal(s1, s2, modifier);
    }

    /**
     * Antonym of {@link #equal(String, String)}
     *
     * @param s1
     * @param s2
     * @return <code>true</code> if s1 doesn't equal to s2
     */
    public static boolean neq(String s1, String s2) {
        return !equal(s1, s2);
    }

    /**
     * Antonym of {@link #equal(String, String, int)}
     *
     * @param s1
     * @param s2
     * @param modifier
     * @return <code>true</code> if s1 doesn't equal to s2 as per modifier
     */
    public static boolean neq(String s1, String s2, int modifier) {
        return !equal(s1, s2, modifier);
    }

    /**
     * Return true if 2 strings are equals to each other without
     * ignore space and case sensitive.
     *
     * @param s1
     * @param s2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean equal(String s1, String s2) {
        return equal(s1, s2, 0);
    }

    /**
     * Return false if 2 strings are equals to each other
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean notEqual(String s1, String s2) {
        return !equal(s1, s2, 0);
    }

    /**
     * alias of {@link #eq(String, String)}
     *
     * @param s1
     * @param s2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    /**
     * Return true if 2 strings are equals to each other as per rule specified
     *
     * @param s1
     * @param s2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return
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
     * @param s1
     * @param s2
     * @param modifier
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
     * @param o
     * @param prefix
     * @param suffix
     * @return the String result
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
     * Decode Base64 encoded string
     *
     * @param str
     * @return decoded string
     */
    public static String decodeBASE64(String str) {
        try {
            return new String(Codec.decodeBASE64(str), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }

    /**
     * Encode a string using Base64 encoding
     *
     * @param str
     * @return encoded string
     */
    public static String encodeBASE64(String str) {
        return Codec.encodeBASE64(str);
    }

    /**
     * perform URL encoding on a giving string
     *
     * @param s
     * @return
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

    /**
     * Get the extension of a filename
     *
     * @param fileName
     * @return the extension
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
     * @param len
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
        Random r = new Random();
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

    public static StringBuilder builder(boolean o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(byte o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(short o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(char o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(int o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(float o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(long o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(double o) {
        return new StringBuilder(String.valueOf(o));
    }

    public static StringBuilder builder(Object o) {
        return new StringBuilder(string(o));
    }

    public static StringBuilder builder(String s) {
        return new StringBuilder(s);
    }

    public static StringBuilder builder() {
        return new StringBuilder();
    }

    public static StringBuilder sizedBuilder(int capacity) {
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
                    return S.builder(s).append(appendix).toString();
                }
            };
        }

        public static $.F1<String, String> prepend(final String prependix) {
            return new $.F1<String, String>() {
                @Override
                public String apply(String s) throws NotAppliedException, $.Break {
                    return S.builder(prependix).append(s).toString();
                }
            };
        }

    }

}
