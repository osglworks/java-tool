/* 
 * Copyright (C) 2013 The Java Tool project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.osgl.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Common String utilities
 */
public class S {

    /**
     * Return a {@link StringBuilder} instance pre populated with specified objects.
     * <p>If any object in the param list is <code>null</code>, then an empty string
     * <code>""</code> is appended to the builder</p>
     *
     * @param objs
     * @return A string builder
     */
    public final static StringBuilder builder(Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objs) {
            sb.append(null == o ? "" : o.toString());
        }
        return sb;
    }

    /**
     * A handy alias for {@link String#format(String, Object...)}
     *
     * @param tmpl
     * @param args
     * @return the formatted string
     */
    public final static String fmt(String tmpl, Object... args) {
        if (args.length == 0) {
            return tmpl;
        } else {
            return String.format(tmpl, args);
        }
    }

    /**
     * A handy alias for {@link String#format(java.util.Locale, String, Object...)}
     *
     * @param locale
     * @param tmpl
     * @param args
     * @return the formatted string
     */
    public final static String fmt(Locale locale, String tmpl, Object... args) {
        if (args.length == 0) {
            return tmpl;
        }
        return String.format(locale, tmpl, args);
    }

    public final static Str str(Object o) {
        X.NPE(o);
        return new Str(o.toString());
    }

    public static Str str(boolean x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(char x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(short x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(int x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(long x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(double x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(float x) {
        return new Str(String.valueOf(x));
    }

    public static Str str(char[] x) {
        return new Str(String.valueOf(x));
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

    public final static String string(Object o) {
        return null == o ? "" : o.toString();
    }

    public final static String str(byte[] ba) {
        return new String(ba);
    }

    public static String str(byte[] ba, String encode) {
        try {
            return new String(ba, encode);
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
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
                              Iterable<?> iterable) {
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
                              Iterable<?> iterable, boolean quoted, boolean separateFixes) {
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
     * <p>Return a string no longer than specified max length.
     * <p/>
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

    /**
     * <p>Return a string no longer than specified max length.
     * <p/>
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
     * alias of {@link #empty(String)}
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return (null == s || "".equals(s.trim()));
    }

    /**
     * Determine if a string is all blank or empty or null
     *
     * @param s
     * @return true if the string is null or empty or all blanks
     */
    public static boolean empty(String s) {
        return (null == s || "".equals(s.trim()));
    }

    /**
     * alias of {@link #notEmpty(String)}
     *
     * @param s
     * @return true if <code>s</code> is <code>null</code> or empty or all in blank
     */
    public static boolean isNotEmpty(String s) {
        return notEmpty(s);
    }

    /**
     * Antonym of {@link #empty(String)}
     *
     * @param s
     * @return true if <code>s</code> is not <code>null</code> or empty or all in blank
     */
    public static boolean notEmpty(String s) {
        return !empty(s);
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
     * Antonym of {@link #anyEmpty(String...)}
     *
     * @param sa
     * @return <code>false</code> if anyone of the specified string is empty
     */
    public static boolean noEmpty(String... sa) {
        return !anyEmpty(sa);
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

    public final static String capFirst(String s) {
        if (null == s) {
            return "";
        }
        return ("" + s.charAt(0)).toUpperCase() + s.substring(1);
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
     * <p/>
     * <p>For example: </p>
     * <p/>
     * <pre><code>Object o = "xxBByy";
     * String s = S.strip(o, "xx", "yy")</code></pre>
     * <p/>
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

    /**
     * Null safety trim
     * @param s
     * @return
     */
    public static String trim(String s) {
        return null == s ? "" : s.trim();
    }

    /**
     * Null safety toUpperCase
     * @param s
     * @return
     */
    public static final String toUpperCase(String s) {
        return null == s ? "" : s.toUpperCase();
    }

    /**
     * Alias of {@link #toUpperCase(String)}
     * @param s
     * @return
     */
    public static final String upper(String s) {
        return toUpperCase(s);
    }

    /**
     * Null safety toLowerCase
     * @param s
     * @return
     */
    public static final String toLowerCase(String s) {
        return null == s ? "" : s.toLowerCase();
    }

    /**
     * Alias of {@link #toLowerCase(String)}
     * @param s
     * @return
     */
    public static final String lower(String s) {
        return toLowerCase(s);
    }

    /**
     * Generate random string.
     * <p/>
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
        StringBuffer sb = new StringBuffer(len);
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

    // --- An easy to use String wrapper
    public static class Str implements CharSequence, Comparable<Str> {

        private String s;

        // --- constructors

        /**
         * Construct with a String instance
         *
         * @param s
         */
        public Str(String s) {
            X.NPE(s);
            this.s = s;
        }

        // --- accessor

        /**
         * Get the internal String instance
         * <p>Same effect as {@link #toString()}</p>
         *
         * @return the string
         */
        public String get() {
            return s;
        }

        // --- Object methods

        @Override
        public String toString() {
            return s;
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }
            if (obj instanceof Str) {
                return ((Str) obj).s.equals(s);
            }
            return false;
        }

        // ---- String methods and aliases

        /**
         * @return length of the internal String
         */
        public int length() {
            return s.length();
        }

        /**
         * Alias of {@link #length()}
         *
         * @return
         */
        public int size() {
            return s.length();
        }

        /**
         * Alias of {@link #length()}
         * @return
         */
        public int len() {
            return s.length();
        }

        /**
         * @return true if the internal string is empty
         */
        public boolean isEmpty() {
            return s.isEmpty();
        }

        /**
         * Alias of {@link #isEmpty()}
         *
         * @return
         */
        public boolean empty() {
            return s.isEmpty();
        }

        /**
         * antonym of {@link #isEmpty()}
         *
         * @return
         */
        public boolean notEmpty() {
            return !s.isEmpty();
        }

        /**
         * Wrapper of {@link String#charAt(int)}
         *
         * @param id
         * @return the char at the index
         */
        public char charAt(int id) {
            return s.charAt(id);
        }

        /**
         * alias of {@link #charAt(int)}
         *
         * @param id
         * @return the char at the index
         */
        public char get(int id) {
            return s.charAt(id);
        }

        /**
         * Wrapper of {@link String#getChars(int, int, char[], int)}
         *
         * @param srcBegin
         * @param srcEnd
         * @param dst
         * @param dstBegin
         */
        public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
            s.getChars(srcBegin, srcEnd, dst, dstBegin);
        }

        /**
         * Alias of {@link #getChars(int, int, char[], int)}
         *
         * @param srcBegin
         * @param srcEnd
         * @param dst
         * @param dstBegin
         */
        public void copy(int srcBegin, int srcEnd, char dst[], int dstBegin) {
            s.getChars(srcBegin, srcEnd, dst, dstBegin);
        }

        /**
         * Wrapper of {@link String#getBytes(java.nio.charset.Charset)}. However this method
         * converts checked exception to runtime exception
         *
         * @param charsetName
         * @return the byte array
         */
        public byte[] getBytes(String charsetName) {
            try {
                return s.getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                throw E.encodingException(e);
            }
        }

        /**
         * Wrapper of {@link String#getBytes()}
         *
         * @return
         */
        public byte[] getBytes() {
            return s.getBytes();
        }

        /**
         * Wrapper of {@link String#contentEquals(StringBuffer)}
         *
         * @param stringBuffer
         * @return true if content equals the content of the specified buffer
         */
        public boolean contentEquals(StringBuffer stringBuffer) {
            return s.contentEquals(stringBuffer);
        }

        /**
         * Alias of {@link #contentEquals(StringBuffer)}
         *
         * @param stringBuffer
         * @return
         */
        public boolean eq(StringBuffer stringBuffer) {
            return s.contentEquals(stringBuffer);
        }

        /**
         * Wrapper of {@link String#contentEquals(CharSequence)}
         *
         * @param x
         * @return true if content equals content of the specified char sequence
         */
        public boolean contentEquals(CharSequence x) {
            return s.contentEquals(x);
        }

        /**
         * Alias of {@link #contentEquals(CharSequence)}
         *
         * @param x
         * @return
         */
        public boolean eq(CharSequence x) {
            return s.contentEquals(x);
        }

        /**
         * @param x
         * @return <code>true</code> if the content of this str equals to the specified str
         */
        public boolean contentEquals(Str x) {
            if (null == x) {
                return false;
            }
            return x.s.equals(s);
        }

        /**
         * Alias of {@link #contentEquals(org.osgl.util.S.Str)}
         *
         * @param x
         * @return
         */
        public boolean eq(Str x) {
            return contentEquals(x);
        }

        /**
         * Wrapper of {@link String#equalsIgnoreCase(String)}
         *
         * @param x
         * @return {@code true} if the argument is not {@code null} and it
         *         represents an equivalent {@code String} ignoring case; {@code
         *         false} otherwise
         */
        public boolean equalsIgnoreCase(String x) {
            return s.equalsIgnoreCase(x);
        }

        /**
         * Compare content of the str and the specified char sequence, case insensitive
         *
         * @param x
         * @return {@code true} if the argument is not {@code null} and it
         *         represents an equivalent {@code String} ignoring case; {@code
         *         false} otherwise
         */
        public boolean equalsIgnoreCase(CharSequence x) {
            return null == x ? false : s.equalsIgnoreCase(x.toString());
        }

        /**
         * Wrapper of {@link String#compareTo(String)}
         *
         * @param x
         * @return
         */
        public int compareTo(Str x) {
            return s.compareTo(x.s);
        }

        public int compareTo(String x) {
            return s.compareTo(x);
        }

        public int compareToIgnoreCase(Str x) {
            return s.compareToIgnoreCase(x.s);
        }

        public int compmareToIgnoreCase(String x) {
            return s.compareToIgnoreCase(x);
        }

        public boolean regionMatches(int toffset, Str other, int ooffset, int len) {
            return s.regionMatches(toffset, other.s, ooffset, len);
        }

        public boolean regionMatches(int toffset, String other, int ooffset, int len) {
            return s.regionMatches(toffset, other, ooffset, len);
        }

        public boolean regionMatches(boolean ignoreCase, int toffset,
                                     Str other, int ooffset, int len) {
            return s.regionMatches(ignoreCase, toffset, other.s, ooffset, len);
        }

        public boolean regionMatches(boolean ignoreCase, int toffset,
                                     String other, int ooffset, int len) {
            return s.regionMatches(ignoreCase, toffset, other, ooffset, len);
        }
        
        public boolean startsWith(Str prefix, int toffset) {
            return s.startsWith(prefix.s, toffset);
        }

        public boolean startsWith(String prefix, int toffset) {
            return s.startsWith(prefix, toffset);
        }

        public boolean startsWith(String prefix) {
            return s.startsWith(prefix);
        }

        public boolean startsWith(Str prefix) {
            return s.startsWith(prefix.s);
        }

        public boolean endsWith(Str suffix) {
            return s.endsWith(suffix.s);
        }

        public boolean endsWith(String suffix) {
            return s.endsWith(suffix);
        }
        
        public int indexOf(int ch) {
            return s.indexOf(ch);
        }

        /**
         * Alias of {@link #indexOf(int)}
         * @param ch
         * @return
         */
        public int pos(int ch) {
            return s.indexOf(ch);
        }

        /**
         * Wrapper of {@link String#indexOf(String, int)}
         * @param ch
         * @param fromIndex
         * @return
         */
        public int indexOf(int ch, int fromIndex) {
            return s.indexOf(ch, fromIndex);
        }

        /**
         * Alias of {@link #indexOf(int, int)}
         * @param ch
         * @param fromIndex
         * @return
         */
        public int pos(int ch, int fromIndex) {
            return s.indexOf(ch, fromIndex);
        }

        /**
         * Wrapper of {@link String#lastIndexOf(int)}
         * @param ch
         * @return
         */
        public int lastIndexOf(int ch) {
            return s.lastIndexOf(ch);
        }

        /**
         *  Alias of {@link #lastIndexOf(int)}
         * @param ch
         * @return
         */
        public int rpos(int ch) {
            return s.lastIndexOf(ch);
        }

        /**
         * Wrapper of {@link String#lastIndexOf(int, int)}
         * @param ch
         * @param fromIndex
         * @return
         */
        public int lastIndexOf(int ch, int fromIndex) {
            return s.lastIndexOf(ch, fromIndex);
        }

        /**
         * Alias of {@link #lastIndexOf(int, int)}
         * @param ch
         * @param fromIndex
         * @return
         */
        public int rpos(int ch, int fromIndex) {
            return s.lastIndexOf(ch, fromIndex);
        }
        
        public int indexOf(String str) {
            return s.indexOf(str);
        }
        
        /**
         * Alias of {@link #indexOf(String)}
         * @param x
         * @return
         */
        public int pos(String x) {
            return s.indexOf(x);
        }

        public int indexOf(Str x) {
            return s.indexOf(x.s);
        }

        public int pos(Str x) {
            return s.indexOf(x.s);
        }

        public int indexOf(String str, int fromIndex) {
            return s.indexOf(str, fromIndex);
        }

        /**
         * Alias of {@link #indexOf(String, int)}
         * @param x
         * @param fromIndex
         * @return
         */
        public int pos(String x, int fromIndex) {
            return s.indexOf(x, fromIndex);
        }
        
        public int indexOf(Str str, int fromIndex) {
            return s.indexOf(str.s, fromIndex);
        }

        /**
         * Alias of {@link #indexOf(Str, int)}
         * @param x
         * @param fromIndex
         * @return
         */
        public int pos(Str x, int fromIndex) {
            return s.indexOf(x.s, fromIndex);
        }

        public int lastIndexOf(String str) {
            return s.lastIndexOf(str);
        }

        /**
         * Alias of {@link #lastIndexOf(String)}
         * @param x
         * @return
         */
        public int rpos(String x) {
            return s.lastIndexOf(x);
        }
        
        public int lastIndexOf(Str str) {
            return s.lastIndexOf(str.s);
        }

        /**
         * Alias of {@link #lastIndexOf(Str)}
         * @param x
         * @return
         */
        public int rpos(Str x) {
            return s.lastIndexOf(x.s);
        }
        
        public int lastIndexOf(String str, int fromIndex) {
            return s.lastIndexOf(str, fromIndex);
        }

        public int rpos(String str, int fromIndex) {
            return s.lastIndexOf(str, fromIndex);
        }

        public int lastIndexOf(Str str, int fromIndex) {
            return s.lastIndexOf(str.s, fromIndex);
        }

        public int rpos(Str str, int fromIndex) {
            return s.lastIndexOf(str.s, fromIndex);
        }

        /**
         * Wrapper of {@link String#substring(int)}
         * @param beginIndex
         * @return
         */
        public String substring(int beginIndex) {
            return s.substring(beginIndex);
        }

        /**
         * Synonym of {@link #substring(int)} but return Str instead of String
         * @param beginIndex
         * @return A str of 
         */
        public Str substr(int beginIndex) {
            return str(s.substring(beginIndex));
        }

        /**
         * Wrapper of {@link #substring(int, int)}
         * @param beginIndex
         * @param endIndex
         * @return
         */
        public String substring(int beginIndex, int endIndex) {
            return s.substring(beginIndex, endIndex);
        }

        /**
         * Synonym of {@link #substring(int, int)} but return Str instead of String
         * 
         * @param beginIndex
         * @param endIndex
         * @return
         */
        public Str substr(int beginIndex, int endIndex) {
            return str(s.substring(beginIndex, endIndex));
        }

        /**
         * Wrapper of {@link String#subSequence(int, int)}
         * @param beginIndex
         * @param endIndex
         * @return
         */
        public CharSequence subSequence(int beginIndex, int endIndex) {
            return substr(beginIndex, endIndex);
        }

        /**
         * Wrapper of {@link String#concat(String)} but return Str instance
         * @param str
         * @return
         */
        public Str concat(String str) {
            return str(s.concat(str));
        }

        /**
         * Wrapper of {@link String#replace(char, char)} but return Str instance
         * @param oldChar
         * @param newChar
         * @return
         */
        public Str replace(char oldChar, char newChar) {
            return str(s.replace(oldChar, newChar));
        }

        /**
         * Wrapper of {@link String#matches(String)}
         * @param regex
         * @return
         */
        public boolean matches(String regex) {
            return s.matches(regex);
        }

        /**
         * Wrapper of {@link String#contains(CharSequence)}
         * @param s
         * @return
         */
        public boolean contains(CharSequence s) {
            return this.s.contains(s);
        }

        /**
         * Wrapper of {@link String#replaceFirst(String, String)} but return Str inance
         * @param regex
         * @param replacement
         * @return
         */
        public Str replaceFirst(String regex, String replacement) {
            return str(s.replaceFirst(regex, replacement));
        }

        /**
         * Wrapper of {@link String#replaceAll(String, String)} but return Str type instance
         * @param regex
         * @param replacement
         * @return
         */
        public Str replaceAll(String regex, String replacement) {
            return str(s.replaceAll(regex, replacement));
        }

        /**
         * Wrapper of {@link String#replace(CharSequence, CharSequence)} but return Str type instance
         * @param target
         * @param replacement
         * @return
         */
        public Str replace(CharSequence target, CharSequence replacement) {
            return str(s.replace(target, replacement));
        }

        /**
         * Wrapper of {@link String#split(String, int)} but return an immutable List of Str instances
         * @param regex
         * @param limit
         * @return
         */
        public C0.List<Str> split(String regex, int limit) {
            String[] sa = s.split(regex, limit);
            int len = sa.length;
            Str[] ssa = new Str[len];
            for (int i = 0; i < len; ++i) {
                ssa[i] = str(sa[i]);
            }
            return C0.list(ssa);
        }

        /**
         * Wrapper of {@link String#split(String)} but return an immutable List of Str instances
         * @param regex
         * @return
         */
        public C0.List<Str> split(String regex) {
            return split(regex, 0);
        }

        /**
         * Wrapper of {@link String#toLowerCase()} but return Str type instance
         * 
         * @return
         */
        public Str toLowerCase() {
            return str(s.toLowerCase());
        }

        /**
         * Alias of {@link #toLowerCase()}
         * @return
         */
        public Str lower() {
            return toLowerCase();
        }

        /**
         * Wrapper of {@link String#toLowerCase(java.util.Locale)} but return Str type instance
         * @param locale
         * @return
         */
        public Str toLowerCase(Locale locale) {
            return str(s.toLowerCase(locale));
        }

        /**
         * Alias of {@link #toLowerCase(java.util.Locale)}
         * @param locale
         * @return
         */
        public Str lower(Locale locale) {
            return toLowerCase(locale);
        }

        /**
         * Wrapper of {@link String#toUpperCase()} but return Str type instance
         * @return
         */
        public Str toUpperCase() {
            return str(s.toUpperCase());
        }

        /**
         * Alias of {@link #toUpperCase()}
         * @return
         */
        public Str upper() {
            return toUpperCase();
        }

        /**
         * Wrapper of {@link String#toUpperCase(java.util.Locale)} but return Str type instance
         * @param locale
         * @return
         */
        public Str toUpperCase(Locale locale) {
            return str(s.toUpperCase(locale));
        }

        /**
         * Alias of {@link #toUpperCase(java.util.Locale)}
         * @param locale
         * @return
         */
        public Str upper(Locale locale) {
            return toUpperCase(locale);
        }

        /**
         * Wrapper of {@link String#trim()} and return Str type instance
         * @return
         */
        public Str trim() {
            return str(s.trim());
        }

        /**
         * Wrapper of {@link String#toCharArray()}
         * @return
         */
        public char[] toCharArray() {
            return s.toCharArray();
        }

        /**
         * Alias of {@link #toCharArray()}
         * @return
         */
        public char[] chars() {
            return s.toCharArray();
        }

        /**
         * Wrapper of {@link String#intern()}
         * @return
         */
        public String intern() {
            return s.intern();
        }
        
        // -- extensions
        public Str after(String s) {
            return str(S.after(this.s, s));
        }

        public Str afterFirst(String s) {
            return str(S.afterFirst(this.s, s));
        }

        public Str afterLast(String s) {
            return str(S.afterLast(this.s, s));
        }

        public Str before(String s) {
            return str(S.before(this.s, s));
        }

        public Str beforeFirst(String s) {
            return str(S.beforeFirst(this.s, s));
        }

        public Str beforeLast(String s) {
            return str(S.beforeLast(this.s, s));
        }

        public Str strip(String prefix, String suffix) {
            return str(S.strip(s, prefix, suffix));
        }

        public Str urlEncode() {
            return str(S.urlEncode(s));
        }

        public Str decodeBASE64() {
            return str(S.decodeBASE64(s));
        }

        public Str encodeBASE64() {
            return str(S.encodeBASE64(s));
        }

        public Str cutOff(int n) {
            return str(S.cutOff(s, n));
        }

        public Str capFirst() {
            return str(S.capFirst(s));
        }

        public int count(String search) {
            return S.count(s, search);
        }

        public int count(Str search) {
            return S.count(s, search.get());
        }

        public int countWithOverlay(String search) {
            return S.count(s, search, true);
        }

        public int countWithOverlay(Str search) {
            return S.count(s, search.get(), true);
        }

        /**
         * Return an new Str of the first N characters
         *
         * @param n
         * @return
         */
        public Str head(int n) {
            return str(S.first(s, n));
        }

        /**
         * Return an new Str of the last N characters
         *
         * @param n
         * @return
         */
        public Str tail(int n) {
            return str(S.last(s, n));
        }

    }

    // --- functors 
    public static class f {

        public static final _.F1 TO_UPPERCASE = new _.F1<String, String>() {
            @Override
            public String apply(String s) {
                return S.toUpperCase(s);
            }
        };

        public static final _.F1<String, String> toUpperCase() {
            return TO_UPPERCASE;
        }

        public static final _.F1 TO_LOWERCASE = new _.F1<String, String>() {
            @Override
            public String apply(String s) {
                return S.toLowerCase(s);
            }
        };

        public static final _.F1<String, String> toLowerCase() {
            return TO_LOWERCASE;
        }

        public static final _.F1 CAP_FIRST = new _.F1<String, String>() {
            @Override
            public String apply(String s) {
                return S.capFirst(s);
            }
        };

        public static final _.F1<String, String> capFirst() {
            return CAP_FIRST;
        }

        public static final _.If<String> startsWith(final String prefix) {
            return new _.If<String>() {
                @Override
                public boolean eval(String s) {
                    return s.startsWith(prefix);
                }
            };
        }

        public static final _.If<String> endsWith(final String suffix) {
            return new _.If<String>() {
                @Override
                public boolean eval(String s) {
                    return s.endsWith(suffix);
                }
            };
        }

        public static final _.If<String> matches(final String reg) {
            final Pattern pattern = Pattern.compile(reg);
            return new _.If<String>() {
                @Override
                public boolean eval(String s) {
                    return pattern.matcher(s).matches();
                }
            };
        }

        public static final _.Transformer<String, Integer> size() {
            return size;
        }

        public static final _.Transformer<String, Integer> size = new _.Transformer<String, Integer>() {
            @Override
            public Integer transform(String s) {
                return null == s ? 0 : s.length();
            }
        };

        public static final <T> _.F1<StringBuilder, T> builder(final StringBuilder sb) {
            return CONCAT.curry(sb);
        }

        public static final <T> _.F2<StringBuilder, T, StringBuilder> concat() {
            return CONCAT;
        }

        public static _.F2 CONCAT = new _.F2<StringBuilder, Object, StringBuilder>() {
            @Override
            public StringBuilder apply(Object s2, StringBuilder sb) {
                return sb.append(s2);
            }
        };

        public static <T> _.Transformer<T, String> format(final String tmpl) {
            return new _.Transformer<T, String>() {
                @Override
                public String transform(T t) {
                    return String.format(tmpl, t);
                }
            };
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            System.out.println(random(i + 5));
        }
    }
}
