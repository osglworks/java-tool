/* 
 * Copyright (C) 2013 The Rythm Engine project
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

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Define a range and methods to operate on the range.
 *
 * This is an abstract class. And there are two predefined concrete implementation for <code>int</code>
 * and <code>char</code> types. User application can extend to this class and define their own range
 * implementations.
 *
 * @author Gelin Luo
 * @version 0.2
 */
public abstract class Range<TYPE extends Comparable<TYPE>> implements Iterable<TYPE> {
    private final TYPE minInclusive;
    private TYPE maxExclusive;

    /**
     * Create a <code>Range</code> with min (inclusive) and max (exclusive)
     *
     * @param minInclusive the bottom end of the range (included in the range)
     * @param maxExclusive the ceiling of the range (excluded from the range)
     * @throws org.osgl.exception.InvalidArgException if minInclusive is greater than maxExclusive
     * @throws NullPointerException if either minInclusive or maxExclusive is <code>null</code>
     *
     * @since 0.2
     */
    public Range(final TYPE minInclusive, final TYPE maxExclusive) {
        E.NPE(minInclusive, maxExclusive);
        E.invalidArgIf(minInclusive.compareTo(maxExclusive) > 0, "min[%s] is greater than max[%s]", minInclusive, maxExclusive);
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }

    /**
     * Return the min (inclusive) element in this range
     *
     * @since 0.2
     */
    public TYPE min() {
        return minInclusive;
    }

    /**
     * Return the max (exclusive) element of this range
     *
     * @since 0.2
     */
    public TYPE max() {
        return maxExclusive;
    }

    /**
     * Subclass to implement this method to return immediate next element of the specified element as per
     * the logic of the Range implementation. For example, for a Range of int, <code>next(2)</code> should
     * yield <code>3</code>, while for a Range of character, <code>next(m)</code> should yield <code>n</code>.
     *
     * @throws org.osgl.exception.InvalidRangeException if the next element exceeds the maximum of range
     *         of the <code>TYPE</code>. Note it is possible to return an element that is out of the Range
     *         itself. For example, an int range <code>[3 .. 5)</code>, <code>next(5)</code> exceeds the
     *         range, but the number <code>6</code> is inside the range of type <code>int</code> and thus
     *         it is valid. <code>next(Integer.MAX_VALUE)</code>, however will throw out the
     *         <code>InvalidRangeException</code>
     */
    protected abstract TYPE next(TYPE element);

    /**
     * Subclass to implement this method to return immediate previous element of the specified element as per
     * the logic of the Range implementation. For example, for a Range of int, <code>next(2)</code> should
     * yield <code>1</code>, while for a Range of character, <code>next(m)</code> should yield <code>l</code>
     *
     * @throws NullPointerException if the element specified is <code>null</code>
     * @throws org.osgl.exception.InvalidRangeException if the previous element exceeds the maximum of range
     *         of the <code>TYPE</code>. Note it is possible to return an element that is out of the Range
     *         itself. For example, an int range <code>[3 .. 5)</code>, <code>prev(3)</code> exceeds the
     *         range, but the number <code>2</code> is inside the range of type <code>int</code> and thus
     *         it is valid. <code>prev(Integer.MIN_VALUE)</code> however, will throw out the
     *         <code>InvalidRangeException</code>
     */
    protected abstract TYPE prev(TYPE element);

    /**
     * Check if a given element is inside the range. If the element less than <code>min()</code> or
     * great than <code>prev(max())</code>, then the element is considered to be outside of the range.
     *
     * @see Comparable
     * @since 0.2
     *
     * @return <code>true</code> if the element is inside the range, or <code>false</code> otherwise
     * @throws NullPointerException if the element specified is <code>null</code>
     */
    public boolean include(TYPE element) {
        if (element.compareTo(min()) < 0) {
            return false;
        }
        if (element.compareTo(prev(max())) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Return the size of the range
     */
    public abstract int size();

    /**
     * Alias of {@link #size()}
     */
    public int len() {
        return size();
    }

    /**
     * Alias of {@link #size()}
     */
    public int length() {
        return size();
    }

    @Override
    public String toString() {
        return S.fmt("[%s .. %s)", min(), max());
    }

    private static final Pattern P_NUM = Pattern.compile("([0-9]+)(\\s*\\.\\.\\s*|\\s+(to|till)\\s+)([0-9]+)");
    private static final Pattern P_CHR = Pattern.compile("'(\\w)'(\\s*\\.\\.\\s*|\\s+(to|till)\\s+)'(\\w)'");


    /**
     * Parse a String expression and return a range.
     *
     * @throws org.osgl.exception.InvalidArgException if the string specified cannot be parsed
     */
    @SuppressWarnings("unchecked")
    public static Range valueOf(String s) {
        boolean open = true;
        if (s.endsWith("]")) {
            open = false;
        }
        s = s.trim();
        s = S.strip(s, "[", ")");
        s = S.strip(s, "[", "]");
        java.util.regex.Matcher m = P_NUM.matcher(s);
        boolean isChar = false;
        if (!m.matches()) {
            m = P_CHR.matcher(s);
            isChar = true;
        }

        E.invalidArgIf(!m.matches(), "Unknown range expression: %s", s);

        Range r;
        if (isChar) {
            char min = m.group(1).charAt(0), max = m.group(4).charAt(0);
            r = valueOf(min, max);
        } else {
            int min = Integer.valueOf(m.group(1)), max = Integer.valueOf(m.group(4));
            r = valueOf(min, max);
        }
        String verb = m.group(3);
        if ("till".equals(verb)) {
            open = false;
        }
        if (!open) {
            r.maxExclusive = r.next(r.maxExclusive);
        }
        
        return r;
    }

    @Override
    public Iterator<TYPE> iterator() {
        return new Iterator<TYPE>() {
            private TYPE cur = minInclusive;

            @Override
            public boolean hasNext() {
                return !cur.equals(maxExclusive);
            }

            @Override
            public TYPE next() {
                TYPE retVal = cur;
                cur = Range.this.next(cur);
                return retVal;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public Range<TYPE> reversed() {
        final Range<TYPE> me = Range.this;
        TYPE maxExcl = me.prev((me.minInclusive));
        TYPE minIncl = me.prev(me.maxExclusive);
        return new Range<TYPE>(minIncl, maxExcl) {
            
            @Override
            protected TYPE next(TYPE element) {
                return me.prev(element);
            }

            @Override
            protected TYPE prev(TYPE element) {
                return me.next(element);
            }

            @Override
            public boolean include(TYPE element) {
                return me.include(element);
            }

            @Override
            public int size() {
                return me.size();
            }
        };
    }
    
    public static Range<Integer> valueOf(final int minInclusive, final int maxExclusive) {
        if (maxExclusive < minInclusive) {
            return valueOf(maxExclusive, minInclusive);
        }
        return new Range<Integer>(minInclusive, maxExclusive) {
            @Override
            protected Integer next(Integer element) {
                if (element == Integer.MAX_VALUE) {
                    throw new IndexOutOfBoundsException();
                }
                return ++element;
            }

            @Override
            protected Integer prev(Integer element) {
                if (element == Integer.MIN_VALUE) {
                    throw new IndexOutOfBoundsException();
                }
                return --element;
            }

            @Override
            public int size() {
                return max() - min();
            }

            @Override
            public boolean include(Integer element) {
                return (min() <= element) && (element < max());
            }
        };
    }

    public static Range<Character> valueOf(final char minInclusive, final char maxExclusive) {
        return new Range<Character>(minInclusive, maxExclusive) {
            @Override
            protected Character next(Character element) {
                return (char) (element + 1);
            }

            @Override
            protected Character prev(Character element) {
                return (char) (element - 1);
            }

            @Override
            public int size() {
                char min = min();
                char max = max();
                return (int) max - (int) min;
            }

            @Override
            public boolean include(Character element) {
                return (min() <= element) && (element < max());
            }
        };
    }

}
