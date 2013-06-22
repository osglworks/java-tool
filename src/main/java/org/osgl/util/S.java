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

import org.osgl.exception.UnexpectedException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Common String utilities
 */
public class S {

    /**
     * Define an instance to be used in views
     */
    public final static S INSTANCE = new S();

    /**
     * Define an instance to be used in views
     */
    public final static S instance = INSTANCE;

    public final static String fmt(String tmpl, Object... args) {
        if (args.length == 0) {
            return tmpl;
        } else {
            return String.format(tmpl, args);
        }
    }

    public final static String format(String tmpl, Object... args) {
        return fmt(tmpl, args);
    }

    public final static Str str(Object o) {
        return Str.valueOf(o);
    }

    public static Str valueOf(Object o) {
        return Str.valueOf(S.string(o));
    }

    public static Str valueOf(int i) {
        return Str.valueOf(i);
    }

    public static Str valueOf(boolean b) {
        return Str.valueOf(b);
    }

    public static Str valueOf(char[] ca) {
        return Str.valueOf(ca);
    }

    public static Str valueOf(long l) {
        return Str.valueOf(l);
    }

    public static Str valueOf(char c) {
        return Str.valueOf(c);
    }

    public static Str valueOf(double d) {
        return Str.valueOf(d);
    }

    public static Str valueOf(float f) {
        return Str.valueOf(f);
    }
    
    public final static StringBuilder builder(Object ... objs) {
        return C.lc(objs).reduce(new StringBuilder(), S.f.append());
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
        return maxSize(s, max);
    }

    /**
     * Return first N chars
     * @param s
     * @param n
     * @return
     */
    public static String first(String s, int n) {
        return s.substring(0, n);
    }

    /**
     * Return last n chars
     * @param s
     * @param n
     * @return
     */
    public static String last(String s, int n) {
        int len = s.length();
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
    public static String maxSize(String s, int max) {
        if (null == s)
            return "";
        if (s.length() < (max - 3))
            return s;
        String s0 = s.substring(0, max);
        return s0 + "...";
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
        if (null == o) return "";
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
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Exception e) {
            throw new UnexpectedException(e);
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
    
    public static String trim(String s) {
        return null == s ? "" : s.trim();
    }

    public static final String toUpperCase(String s) {
        return null == s ? "" : s.toUpperCase();
    }

    public static final String toLowerCase(String s) {
        return null == s ? "" : s.toLowerCase();
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
    
    public static class Str {
        private String s;
        public Str(String s) {
            _.NPE(s);
            this.s = s;
        }
        
        public String get() {
            return s;
        }
        
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

        public static Str valueOf(Object o) {
            return new Str(S.string(o));
        }

        public static Str valueOf(int i) {
            return new Str(String.valueOf(i));
        }

        public static Str valueOf(boolean b) {
            return new Str(String.valueOf(b));
        }

        public static Str valueOf(char[] ca) {
            return new Str(String.valueOf(ca));
        }

        public static Str valueOf(long l) {
            return new Str(String.valueOf(l));
        }

        public static Str valueOf(char c) {
            return new Str(String.valueOf(c));
        }

        public static Str valueOf(double d) {
            return new Str(String.valueOf(d));
        }

        public static Str valueOf(float f) {
            return new Str(String.valueOf(f));
        }
        // -- helpers
        public Str after(String s) {
            return valueOf(S.after(this.s, s));
        }

        public Str afterFirst(String s) {
            return valueOf(S.afterFirst(this.s, s));
        }

        public Str afterLast(String s) {
            return valueOf(S.afterLast(this.s, s));
        }

        public Str before(String s) {
            return valueOf(S.before(this.s, s));
        }

        public Str beforeFirst(String s) {
            return valueOf(S.beforeFirst(this.s, s));
        }

        public Str beforeLast(String s) {
            return valueOf(S.beforeLast(this.s, s));
        }
        
        public Str trim() {
            return valueOf(s.trim());
        }
        
        public Str upperCase() {
            return valueOf(s.toUpperCase());
        }
        
        public Str lowerCase() {
            return valueOf(s.toLowerCase());
        }
        
        public Str replace(CharSequence target, CharSequence replacement) {
            return valueOf(s.replace(target, replacement));
        }
        
        public Str replaceAll(String regex, String replacement) {
            return valueOf(s.replaceAll(regex, replacement));
        }

        public Str capFirst() {
            return valueOf(S.capFirst(s));
        }

        public Str strip(String prefix, String suffix) {
            return valueOf(S.strip(s, prefix, suffix));
        }

        public Str urlEncode() {
            return valueOf(S.urlEncode(s));
        }
        
        public Str decodeBASE64() {
            return valueOf(S.decodeBASE64(s));
        }
        
        public Str encodeBASE64() {
            return valueOf(S.encodeBASE64(s));
        }
        
        public Str cutOff(int n) {
            return valueOf(S.cutOff(s, n));
        }
        
        public Str first(int n) {
            return valueOf(S.first(s, n));
        }

        public Str last(int n) {
            return valueOf(S.last(s, n));
        }

        public boolean startsWith(String prefix) {
            return s.startsWith(prefix);
        }

        public boolean endsWith(String suffix) {
            return s.endsWith(suffix);
        }
    }

    // --- functors 
    public static class f {
    
        public static F.F1 TO_UPPERCASE = toUpperCase();
        
        public static F.F1<String, String> toUpperCase() {
            return new F.F1<String, String>() {
                @Override
                public String run(String s) {
                    return S.toUpperCase(s);
                }
            };
        }
    
        public static F.F1 TO_LOWERCASE = toLowerCase();
        
        public static F.F1<String, String> toLowerCase() {
            return new F.F1<String, String>() {
                @Override
                public String run(String s) {
                    return S.toLowerCase(s);
                }
            };
        }

        public static F.F1 CAP_FIRST = capFirst();
        
        public static F.F1<String, String> capFirst() {
            return new F.F1<String, String>() {
                @Override
                public String run(String s) {
                    return S.capFirst(s);
                }
            };
        }

        public static F.If<String> startsWith(final String prefix) {
            return new F.If<String>() {
                @Override
                public boolean eval(String s) {
                    return s.startsWith(prefix);
                }
            };
        }

        public static F.If<String> endsWith(final String suffix) {
            return new F.If<String>() {
                @Override
                public boolean eval(String s) {
                    return s.endsWith(suffix);
                }
            };
        }

        public static F.If<String> matches(final String reg) {
            final Pattern pattern = Pattern.compile(reg);
            return new F.If<String>() {
                @Override
                public boolean eval(String s) {
                    return pattern.matcher(s).matches();
                }
            };
        }

        public static F.Transformer<String, Integer> size() {
            return size;
        }

        public static F.Transformer<String, Integer> size =
                new F.Transformer<String, Integer>() {
                    @Override
                    public Integer transform(String s) {
                        return null == s ? 0 : s.length();
                    }
                };

        public static F.IFunc1<StringBuilder, ?> build(final StringBuilder sb) {
            return BUILD.curry(sb);
        }

        public static F.IFunc2<StringBuilder, ?, StringBuilder> build() {
            return BUILD;
        }

        public static F.IFunc2<StringBuilder, ?, StringBuilder> BUILD =
                new F.F2<StringBuilder, Object, StringBuilder>() {
                    @Override
                    public StringBuilder run(Object s2, StringBuilder sb) {
                        return sb.append(s2);
                    }
                };
    
        public static <T> F.IFunc2<StringBuilder, StringBuilder, T> concat(final String suffix) {
            return new F.F2<StringBuilder, StringBuilder, T>() {
                @Override
                public StringBuilder run(StringBuilder sb, T o) {
                    return sb.append(o).append(suffix);
                }
            };
        }
    
        public static <T> F.IFunc2<StringBuilder, StringBuilder, T> concat(final String prefix, final String suffix) {
            return new F.F2<StringBuilder, StringBuilder, T>() {
                @Override
                public StringBuilder run(StringBuilder sb, T o) {
                    return sb.append(prefix).append(o).append(suffix);
                }
            };
        }
    
        public static F.IFunc2 CONCAT = 
                new F.F2<StringBuilder, StringBuilder, Object>() {
                    @Override
                    public StringBuilder run(StringBuilder sb, Object o) {
                        return sb.append(o);
                    }
                };
    
        public static F.IFunc2<StringBuilder, StringBuilder, Object> append(final Object sep) {
            return new F.F2<StringBuilder, StringBuilder, Object>() {
                @Override
                public StringBuilder run(StringBuilder sb, Object s) {
                    sb.append(sep).append(s);
                    return sb;
                }
            };
        }
        
        public static <T> F.IFunc2<StringBuilder, T, StringBuilder> append() {
            return APPEND;
        }
        
        public static F.IFunc2 APPEND = 
            new F.F2<StringBuilder, Object, StringBuilder>() {
                @Override
                public StringBuilder run(Object s, StringBuilder sb) {
                    sb.append(s);
                    return sb;
                }
            };
        
        public static <T> F.Transformer<T, String> format(final String tmpl) {
            return new F.Transformer<T, String>() {
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
