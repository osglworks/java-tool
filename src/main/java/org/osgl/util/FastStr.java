package org.osgl.util;

import org.apache.commons.codec.Charsets;
import org.osgl.$;
import org.osgl.Osgl;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.*;

/**
 * FastStr implements the same contract of Str with the a char array.
 * This class is marked as Fast because of the following points:
 * 1. When unsafeOf(String) is called, it share the internal value (the char array) of the
 * String been passed in
 * 2. when unsafeOf(char[]) is called, it use the char array passed in directly without
 * copy operation
 * 3. subList and substring works at O(1) because it will NOT copy the internal char array
 * Note, this class shall be used with caution as it might prevent a very large char array
 * from been garbage collected when the char array is passed to a FastStr or substr of
 * the fast string
 */
public class FastStr extends StrBase<FastStr>
        implements RandomAccess, CharSequence, java.io.Serializable, Comparable<FastStr> {
    public static final FastStr EMPTY_STR = new FastStr() {
        @Override
        public String toString() {
            return "";
        }
    };

    @Override
    protected Class<FastStr> _impl() {
        return FastStr.class;
    }

    @Override
    protected FastStr _empty() {
        return EMPTY_STR;
    }

    private final char[] buf;

    // low end point inclusive
    private final int begin;

    // high end point exclusive
    private final int end;

    private int hash;

    private FastStr() {
        buf = new char[0];
        begin = 0;
        end = 0;
    }

    private FastStr(char[] buf) {
        this(buf, 0, buf.length);
    }

    private FastStr(char[] buf, int start, int end) {
        this.buf = buf;
        this.begin = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - begin;
    }

    @Override
    public boolean isEmpty() {
        return EMPTY_STR == this || buf.length == 0 || end <= begin ;
    }

    @Override
    public boolean isBlank() {
        if (isEmpty()) {
            return true;
        }
        for (int i = begin; i < end; ++i) {
            char c = buf[i];
            if (c > ' ') {
                return false;
            }
        }
        return true;
    }

    @Override
    public FastStr subList(int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new StringIndexOutOfBoundsException(fromIndex);
        }
        int len = size();
        if (toIndex > len) {
            throw new StringIndexOutOfBoundsException(toIndex);
        }
        int subLen = toIndex - fromIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        if (fromIndex == toIndex) {
            return EMPTY_STR;
        }
        int newFrom = toInternalId(fromIndex);
        int newTo = toInternalId(toIndex);
        return new FastStr(buf, newFrom, newTo);
    }

    @Override
    public FastStr takeWhile($.Function<? super Character, Boolean> predicate) {
        if (isEmpty()) {
            return EMPTY_STR;
        }
        int sz = size(), b = toInternalId(0), e = -1;
        for (int i = 0; i < sz; ++i) {
            char c = charAt(i);
            e = toInternalId(i);
            if (!predicate.apply(c)) break;
        }
        return unsafeOf(buf, b, e);
    }

    @Override
    public FastStr dropWhile($.Function<? super Character, Boolean> predicate) {
        int sz = size();
        if (sz == 0) return EMPTY_STR;
        int b = -1, e = toInternalId(sz);
        for (int i = 0; i < sz; ++i) {
            char c = charAt(i);
            b = toInternalId(i);
            if (!predicate.apply(c)) break;
        }
        return unsafeOf(buf, b, e);
    }

    @Override
    public FastStr remove($.Function<? super Character, Boolean> predicate) {
        final int sz = size();
        if (sz == 0) return EMPTY_STR;
        char[] newBuf = null;
        boolean removed = false;
        int curNew = 0;
        for (int i = 0; i < sz; ++i) {
            char c = charAt(i);
            if (predicate.apply(c)) {
                if (null == newBuf) {
                    removed = true;
                    newBuf = new char[sz];
                    System.arraycopy(buf, 0, newBuf, 0, i);
                }
            } else {
                if (null != newBuf) {
                    newBuf[curNew] = c;
                }
                curNew++;
            }
        }
        if (!removed) {
            // nothing removed
            return this;
        }
        return unsafeOf(newBuf, 0, curNew);
    }

    @Override
    public FastStr insert(int index, char character) throws StringIndexOutOfBoundsException {
        E.NPE(character);
        int len = size();
        if (len < Math.abs(index)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (index < 0) {
            index = len + index;
        }
        char[] newBuf = new char[len + 1];
        if (index > 0) {
            System.arraycopy(buf, begin, newBuf, 0, index);
        }
        if (index < len) {
            System.arraycopy(buf, begin + index, newBuf, index + 1, len - index);
        }
        newBuf[index] = character;
        return unsafeOf(newBuf, 0, len + 1);
    }

    @Override
    public FastStr insert(int index, Character character) throws StringIndexOutOfBoundsException {
        E.NPE(character);
        int len = size();
        if (len < Math.abs(index)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (index < 0) {
            index = len + index;
        }
        char[] newBuf = new char[len + 1];
        if (index > 0) {
            System.arraycopy(buf, begin, newBuf, 0, index);
        }
        if (index < len) {
            System.arraycopy(buf, begin + index, newBuf, index + 1, len - index);
        }
        newBuf[index] = character;
        return unsafeOf(newBuf, 0, len + 1);
    }

    @Override
    public FastStr insert(int index, StrBase<?> str) throws StringIndexOutOfBoundsException {
        return insert(index, str.toCharArray());
    }

    @Override
    public FastStr insert(int index, Character... ca) throws StringIndexOutOfBoundsException {
        return insert(0, $.asPrimitive(ca));
    }

    @Override
    public FastStr insert(int index, char... ca) throws StringIndexOutOfBoundsException {
        int delta = ca.length;
        if (delta == 0) {
            return this;
        }
        int len = size();
        if (len < Math.abs(index)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (index < 0) {
            index = len + index;
        }
        char[] newBuf = new char[len + delta];
        if (index > 0) {
            System.arraycopy(buf, begin, newBuf, 0, index);
        }
        System.arraycopy(ca, 0,  newBuf, index, delta);
        if (index < len) {
            System.arraycopy(buf, begin + index, newBuf, index + delta, len - index);
        }
        return unsafeOf(newBuf, 0, len + delta);
    }

    @Override
    public FastStr insert(int index, String s) throws StringIndexOutOfBoundsException {
        return insert(index, s.toCharArray());
    }

    @Override
    public FastStr reverse() {
        int sz = size();
        char[] newBuf = new char[sz];
        for (int i = 0, j = sz - 1; i < sz; ) {
            newBuf[j--] = buf[toInternalId(i++)];
        }
        return new FastStr(newBuf, 0, sz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public FastStr append(Collection<? extends Character> collection) {
        int sz = size(), sz2 = collection.size();
        if (0 == sz2) return this;
        if (0 == sz) return of((Collection<Character>) collection);
        char[] newBuf = new char[sz + sz2];
        copyTo(newBuf, 0);
        int i = sz;
        Iterator<? extends Character> itr = collection.iterator();
        while (itr.hasNext()) {
            char c = itr.next();
            newBuf[i++] = c;
        }
        return new FastStr(newBuf, 0, sz + sz2);
    }

    @Override
    public FastStr append(C.List<Character> list) {
        int sz = size(), sz2 = list.size();
        if (0 == sz2) return this;
        if (0 == sz) return of(list);
        char[] newBuf = new char[sz + sz2];
        copyTo(newBuf, 0);
        for (int i = 0; i < sz2; ++i) {
            newBuf[sz + i] = list.get(i);
        }
        return new FastStr(newBuf, 0, sz + sz2);
    }

    @Override
    public FastStr append(Character character) {
        int sz = size();
        char[] newBuf = new char[sz + 1];
        copyTo(newBuf, 0);
        newBuf[sz] = character;
        return new FastStr(newBuf, 0, sz + 1);
    }

    public FastStr append(FastStr s) {
        int sz = size(), sz2 = s.size();
        if (sz == 0) return s;
        if (sz2 == 0) return this;
        int newSz = sz + sz2;
        char[] newBuf = new char[newSz];
        copyTo(newBuf, 0);
        s.copyTo(newBuf, sz);
        return new FastStr(newBuf, 0, newSz);
    }

    public FastStr append(String s) {
        int sz = size(), sz2 = s.length();
        if (0 == sz) return of(s);
        if (0 == sz2) return this;
        int newSz = sz + sz2;
        char[] newBuf = new char[newSz];
        copyTo(newBuf, 0);
        boolean done = false;
        if (sz2 > 512) {
            try {
                char[] sBuf = Unsafe.bufOf(s);
                System.arraycopy(sBuf, 0, newBuf, sz, sz2);
                done = true;
            } catch (RuntimeException e) {
                // ignore
            }
        }
        if (!done) {
            for (int i = 0; i < sz2; ++i) {
                newBuf[sz + i] = s.charAt(i);
            }
        }
        return new FastStr(newBuf, 0, newSz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public FastStr prepend(Collection<? extends Character> collection) {
        int sz = size(), sz2 = collection.size();
        if (0 == sz2) return this;
        if (0 == sz) return of((Collection<Character>) collection);
        int newSz = sz + sz2, i = 0;
        char[] newBuf = new char[newSz];
        Iterator<? extends Character> itr = collection.iterator();
        while (itr.hasNext()) {
            newBuf[i++] = itr.next();
        }
        copyTo(newBuf, sz2);
        return new FastStr(newBuf, 0, newSz);
    }

    @Override
    public FastStr prepend(C.List<Character> list) {
        int sz = size();
        if (0 == sz) return of(list);
        int sz2 = list.size();
        if (0 == sz2) return this;
        if (1 == sz2) {
            return prepend(list.get(0));
        }
        int newSz = sz + sz2;
        char[] newBuf = new char[newSz];
        for (int i = 0; i < sz2; ++i) {
            newBuf[i] = list.get(i);
        }
        copyTo(newBuf, sz2);
        return new FastStr(newBuf, 0, newSz);
    }

    @Override
    public FastStr prepend(Character character) {
        // check if I can back begin pointer for one step
        if (begin > 0) {
            if (buf[begin - 1] == character) {
                return FastStr.unsafeOf(buf, begin - 1, end);
            }
        }
        int sz = size();
        char[] newBuf = new char[++sz];
        newBuf[0] = character;
        copyTo(newBuf, 1);
        return new FastStr(newBuf, 0, sz);
    }


    @Override
    public FastStr prepend(FastStr s) {
        return s.append(this);
    }

    @Override
    public FastStr prepend(String s) {
        int sz = size();
        if (0 == sz) return of(s);
        int sz2 = s.length();
        if (0 == sz2) return this;
        if (sz2 == 1) {
            return prepend(s.charAt(0));
        }
        int newSz = sz + sz2;
        char[] newBuf = new char[newSz];
        boolean done = false;
        if (sz2 > 512) {
            try {
                char[] sBuf = Unsafe.bufOf(s);
                System.arraycopy(sBuf, 0, newBuf, 0, sz2);
                done = true;
            } catch (RuntimeException e) {
                // ignore
            }
        }
        if (!done) {
            for (int i = 0; i < sz2; ++i) {
                newBuf[i] = s.charAt(i);
            }
        }
        copyTo(newBuf, sz2);
        return new FastStr(newBuf, 0, newSz);
    }

    @Override
    public char charAt(int index) {
        return buf[toInternalId(index)];
    }

    @Override
    public FastStr subSequence(int start, int end) {
        return subList(start, end);
    }

    @Override
    public FastStr copy() {
        if (EMPTY_STR == this) return this;
        return unsafeOf(charArray(), 0, size());
    }

    public FastStr times(int n) {
        E.illegalArgumentIf(n < 0, "n cannot be negative");
        if (0 == n) return EMPTY_STR;
        if (1 == n) return this;
        int sz = size();
        if (0 == sz) return this;
        int newSz = sz * n;
        char[] newBuf = new char[newSz];
        for (int i = 0; i < n; ++i) {
            copyTo(newBuf, i * sz);
        }
        return new FastStr(newBuf, 0, newSz);
    }

    public FastStr canonical() {
        if (EMPTY_STR == this) return this;
        if (begin == 0) return this;
        return unsafeOf(unsafeChars(), 0, size());
    }

    @Override
    public int compareTo(FastStr o) {
        int len1 = size();
        int len2 = o.size();
        int lim = Math.min(len1, len2);
        char v1[] = buf;
        char v2[] = o.buf;

        int k = 0;
        while (k < lim) {
            char c1 = v1[toInternalId(k)];
            char c2 = v2[o.toInternalId(k)];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    @Override
    public String toString() {
        char[] newBuf = charArray();
        try {
            return Unsafe.stringOf(newBuf);
        } catch (Exception e) {
            return new String(newBuf);
        }
    }

    public FastStr toFastStr() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof FastStr) {
            FastStr that = (FastStr) o;
            return contentEquals(that);
        } else if (o instanceof StrBase) {
            StrBase that = (StrBase)o;
            return that.contentEquals(this);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (isEmpty()) return 0;
        int h = hash;
        if (h == 0) {
            for (int i = begin; i < end; ++i) {
                h = 31 * h + buf[i];
            }
            hash = h;
        }
        return h;
    }

    // --- String utilities ---

    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        int sz = size();
        if (srcEnd > sz) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        System.arraycopy(buf, toInternalId(srcBegin), dst, dstBegin, srcEnd - srcBegin);
    }

    public byte[] getBytes() {
        String s = toString();
        return s.getBytes();
    }

    public byte[] getBytes(String charsetName) {
        String s = toString();
        if (null == charsetName) {
            return s.getBytes();
        }
        try {
            return s.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }

    @Override
    public byte[] getBytes(Charset charset) {
        String s = toString();
        if (null == charset) {
            return s.getBytes();
        }
        return s.getBytes(charset);
    }

    public byte[] getBytesAscII() {
        int sz = size();
        if (sz == 0) {
            return new byte[0];
        }
        try {
            char[] chars;
            if (sz == buf.length && begin == 0) {
                chars = buf;
            } else {
                chars = new char[sz];
                System.arraycopy(buf, begin, chars, 0, sz);
            }
            return Unsafe.stringOf(chars).getBytes(Charsets.US_ASCII);
        } catch (Exception e) {
            return toString().getBytes(Charsets.US_ASCII);
        }
    }

    @Override
    public byte[] getBytesUTF8() {
        int sz = size();
        if (sz == 0) {
            return new byte[0];
        }
        try {
            char[] chars;
            if (sz == buf.length && begin == 0) {
                chars = buf;
            } else {
                chars = new char[sz];
                System.arraycopy(buf, begin, chars, 0, sz);
            }
            return Unsafe.stringOf(chars).getBytes(Charsets.UTF_8);
        } catch (Exception e) {
            return toString().getBytes(Charsets.UTF_8);
        }
    }

    /**
     * Wrapper of {@link String#contentEquals(CharSequence)}
     *
     * @param x the char sequence to be compared
     * @return true if content equals content of the specified char sequence
     */
    public boolean contentEquals(CharSequence x) {
        if (x == this) return true;
        if (isEmpty()) return x.length() == 0;
        int sz = size(), sz2 = x.length();
        if (sz != sz2) return false;
        for (int i = 0; i < sz; ++i) {
            char c = buf[toInternalId(i)];
            char c1 = x.charAt(i);
            if (c != c1) return false;
        }
        return true;
    }

    public boolean contentEquals(FastStr x) {
        if (x == this) return true;
        if (null == x) return false;
        int sz = size(), sz2 = x.size();
        if (sz != sz2) return false;
        for (int i = begin, j = x.begin; i < sz; ) {
            if (buf[i++] != x.buf[j++]) return false;
        }
        return true;
    }

    /**
     * Wrapper of {@link String#equalsIgnoreCase(String)}
     *
     * @param x the char sequence to be compared
     * @return {@code true} if the argument is not {@code null} and it
     * represents an equivalent {@code String} ignoring case; {@code
     * false} otherwise
     */
    public boolean equalsIgnoreCase(CharSequence x) {
        if (x == this) return true;
        if (null == x || size() != x.length()) return false;
        if (isEmpty() && x.length() == 0) return true;
        return regionMatches(true, 0, x, 0, size());
    }

    public boolean equalsIgnoreCase(FastStr x) {
        if (x == this) return true;
        if (null == x || size() != x.size()) return false;
        return regionMatches(true, 0, x.buf, 0, size());
    }

    public int compareTo(CharSequence x) {
        int len1 = size();
        int len2 = x.length();
        int lim = Math.min(len1, len2);
        char v1[] = buf;
        try {
            char v2[] = Unsafe.bufOf(x);
            int k = 0;
            while (k < lim) {
                char c1 = v1[toInternalId(k)];
                char c2 = v2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
        } catch (RuntimeException e) {
            int k = 0;
            while (k < lim) {
                char c1 = v1[toInternalId(k)];
                char c2 = x.charAt(k);
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
        }
        return len1 - len2;
    }

    public int compareToIgnoreCase(FastStr o) {
        int len1 = size();
        int len2 = o.size();
        int lim = Math.min(len1, len2);
        char v1[] = buf;
        char v2[] = o.buf;

        int k = 0;
        while (k < lim) {
            char c1 = v1[toInternalId(k)];
            char c2 = v2[o.toInternalId(k)];
            if (c1 != c2) {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 != c2) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                }
            }
            k++;
        }
        return len1 - len2;
    }

    public int compareToIgnoreCase(CharSequence o) {
        int len1 = size();
        int len2 = o.length();
        int lim = Math.min(len1, len2);
        char v1[] = buf;
        int k = 0;

        try {
            char v2[] = Unsafe.bufOf(o);
            while (k < lim) {
                char c1 = v1[toInternalId(k)];
                char c2 = v2[k];
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        if (c1 != c2) {
                            return c1 - c2;
                        }
                    }
                }
                k++;
            }
        } catch (RuntimeException e) {
            while (k < lim) {
                char c1 = v1[toInternalId(k)];
                char c2 = o.charAt(k);
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        if (c1 != c2) {
                            return c1 - c2;
                        }
                    }
                }
                k++;
            }
        }
        return len1 - len2;
    }

    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 FastStr other, int ooffset, int len
    ) {
        return regionMatches(ignoreCase, toffset, other.unsafeChars(), ooffset, len);
    }

    private boolean regionMatches(boolean ignoreCase, int toffset, char[] other, int ooffset, int len) {
        char ta[] = buf;
        int to = toInternalId(toffset);
        char pa[] = other;
        int po = ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (to < 0)
                || (toffset > (long) length() - len)
                || (ooffset > (long) other.length - len)) {
            return false;
        }
        while (len-- > 0) {
            char c1 = ta[to++];
            char c2 = pa[po++];
            if (c1 == c2) {
                continue;
            }
            if (ignoreCase) {
                // If characters don't match but case may be ignored,
                // try converting both characters to uppercase.
                // If the results match, then the comparison scan should
                // continue.
                char u1 = Character.toUpperCase(c1);
                char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }
                // Unfortunately, conversion to uppercase does not work properly
                // for the Georgian alphabet, which has strange rules about case
                // conversion.  So we need to make one last check before
                // exiting.
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 CharSequence other, int ooffset, int len
    ) {
        char[] otherBuf = bufOf(other);
        return regionMatches(ignoreCase, toffset, otherBuf, ooffset, len);
    }

    public boolean startsWith(FastStr prefix, int toffset) {
        if (prefix.isEmpty()) return true;
        int sz2 = prefix.size(), sz = size();
        if (toffset < 0 || toffset > sz - sz2) {
            return false;
        }
        int po = 0, pc = sz2, to = toffset;
        char[] buf1 = buf, buf2 = prefix.buf;
        while (--pc >= 0) {
            if (buf1[toInternalId(to++)] != buf2[prefix.toInternalId(po++)]) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWith(CharSequence suffix, int toffset) {
        if (suffix.length() == 0) return true;
        int sz2 = suffix.length(), sz = size();
        if (toffset < 0 || toffset > sz - sz2) {
            return false;
        }
        int po = 0, pc = sz2, to = toffset;
        char[] buf1 = buf;
        try {
            char[] buf2 = Unsafe.bufOf(suffix);
            while (--pc >= 0) {
                if (buf1[toInternalId(to++)] != buf2[po++]) {
                    return false;
                }
            }
            return true;
        } catch (RuntimeException e) {
            while (--pc >= 0) {
                if (buf1[toInternalId(to++)] != suffix.charAt(po++)) {
                    return false;
                }
            }
            return true;
        }
    }


    public boolean endsWith(CharSequence suffix, int toffset) {
        int prefixSz = suffix.length();
        if (0 == prefixSz) {
            return true;
        }
        int matchStart = length() - toffset;
        if (matchStart < prefixSz) {
            return false;
        }
        for (int i = toInternalId(matchStart - 1), j = prefixSz - 1; j >= 0; --i, --j) {
            char c0 = buf[i];
            char c1 = suffix.charAt(j);
            if (c0 != c1) {
                return false;
            }
        }
        return true;
    }



    @Override
    public boolean endsWith(FastStr prefix, int toffset) {
        int prefixSz = prefix.length();
        if (0 == prefixSz) {
            return true;
        }
        int matchStart = length() - toffset;
        if (matchStart < prefixSz) {
            return false;
        }
        char[] prefixBuf = prefix.buf;
        for (int i = toInternalId(matchStart - 1), j = prefix.toInternalId(prefixSz - 1); j >= prefix.begin; --i, --j) {
            char c0 = buf[i];
            char c1 = prefixBuf[j];
            if (c0 != c1) {
                return false;
            }
        }
        return true;
    }

    public int indexOf(int ch, int fromIndex) {
        final int max = size();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex > max) {
            return -1;
        }
        fromIndex = toInternalId(fromIndex);
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            final char[] buf = this.buf;
            for (int i = fromIndex; i < end; ++i) {
                if (buf[i] == ch) {
                    return toExternalId(i);
                }
            }
            return -1;
        } else {
            return toExternalId(indexOfSupplementary(ch, fromIndex));
        }
    }

    public int lastIndexOf(int ch, int fromIndex) {
        fromIndex = toInternalId(fromIndex);
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            final char[] value = this.buf;
            int i = Math.min(fromIndex, length() - 1 + begin);
            for (; i >= begin; i--) {
                if (value[i] == ch) {
                    return toExternalId(i);
                }
            }
            return -1;
        } else {
            return toExternalId(lastIndexOfSupplementary(ch, fromIndex));
        }
    }

    @Override
    public int indexOf(CharSequence str, int fromIndex) {
        char[] strBuf = bufOf(str);
        return S.indexOf(buf, begin, size(), strBuf, 0, strBuf.length, fromIndex);
    }

    @Override
    public int indexOf(FastStr str, int fromIndex) {
        char[] buf = str.buf;
        return S.indexOf(this.buf, this.begin, this.size(), buf, str.begin, str.size(), fromIndex);
    }

    @Override
    public int lastIndexOf(CharSequence str, int fromIndex) {
        char[] strBuf = bufOf(str);
        int sz = size();
        return lastIndexOf(buf, begin, sz, strBuf, 0, strBuf.length, fromIndex);
    }

    @Override
    public int lastIndexOf(FastStr str, int fromIndex) {
        int sz = size();
        return lastIndexOf(buf, begin, sz, str.buf, str.begin, str.size(), fromIndex);
    }

    /**
     * Wrapper of {@link String#substring(int)}
     *
     * @param beginIndex the begin index
     * @return a String instance that is equivalent to a sub part of this FastStr
     */
    public String substring(int beginIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        int subLen = size() - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return (beginIndex == 0) ? toString() : new String(buf, toInternalId(beginIndex), subLen);
    }

    /**
     * Wrapper of {@link String#substring(int, int)}
     *
     * @param beginIndex begin index
     * @param endIndex end index
     * @return a String instance that is equivalent to sub part of this FastStr
     */
    @Override
    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        char[] buf = this.buf;
        int sz = buf.length;
        if (endIndex > sz) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return ((beginIndex == 0) && (endIndex == sz)) ? toString()
                : new String(buf, toInternalId(beginIndex), subLen);
    }

    /**
     * Wrapper of {@link String#replace(char, char)} but return FastStr instance
     *
     * @param oldChar char to be replaced
     * @param newChar char used to replace {@code oldChar}
     * @return a FastStr instance with all {@code oldChar} been replaced with {@code newChar}
     */
    @Override
    public FastStr replace(char oldChar, char newChar) {
        if (oldChar != newChar) {
            char[] val = this.buf; /* avoid getfield opcode */
            int len = length();
            int i = begin - 1;

            while (++i < end) {
                if (val[i] == oldChar) {
                    break;
                }
            }
            if (i < end) {
                char buf[] = new char[len];
                for (int j = begin; j < i; j++) {
                    buf[j - begin] = val[j];
                }
                while (i < end) {
                    char c = val[i];
                    buf[i - begin] = (c == oldChar) ? newChar : c;
                    i++;
                }
                return new FastStr(buf, 0, len);
            }
        }
        return this;
    }

    /**
     * Wrapper of {@link String#matches(String)}
     *
     * @param regex the regular expression to checked against this FastStr
     * @return {@code true} if this FastStr matches {@code regex}
     */
    @Override
    public boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }

    /**
     * Wrapper of {@link String#contains(CharSequence)}
     *
     * @param s the char sequence to be found
     * @return {@code true} if {@code s} has been found
     */
    @Override
    public boolean contains(CharSequence s) {
        if (s instanceof FastStr) {
            return indexOf((FastStr) s) > -1;
        }
        return indexOf(s.toString()) > -1;
    }

    /**
     * Wrapper of {@link String#replaceFirst(String, String)} but return FastStr inance
     *
     * @param regex the regular expression specifies the place to be replaced
     * @param replacement the string to replace the found part
     * @return a FastStr that has the first found part replaced
     */
    @Override
    public FastStr replaceFirst(String regex, String replacement) {
        return unsafeOf(Pattern.compile(regex).matcher(this).replaceFirst(replacement));
    }

    /**
     * Wrapper of {@link String#replaceAll(String, String)} but return FastStr type instance
     *
     * @param regex the regular expression specifies the pattern to be replaced
     * @param replacement the replacement string
     * @return a FastStr instance with all found part replaced
     */
    @Override
    public FastStr replaceAll(String regex, String replacement) {
        return unsafeOf(Pattern.compile(regex).matcher(this).replaceAll(replacement));
    }

    /**
     * Wrapper of {@link String#replace(CharSequence, CharSequence)} but return FastStr type instance
     *
     * @param target the char sequence to be replaced
     * @param replacement the char sequence used to replace {@code target}
     * @return a FastStr instance with all {@code target} being replaced with
     *         {@code replacement}
     */
    @Override
    public FastStr replace(CharSequence target, CharSequence replacement) {
        return unsafeOf(Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
                this).replaceAll(Matcher.quoteReplacement(replacement.toString())));
    }

    /**
     * Wrapper of {@link String#split(String, int)} but return an immutable List of FastStr instances
     *
     * @param regex the regular expression matches the seperator
     * @param limit the result threshold
     * @return a {@link org.osgl.util.C.List} of FastStr instances split from this FastStr
     */
    @Override
    public C.List<FastStr> split(String regex, int limit) {
        /* fastpath if the regex is a
         (1)one-char String and this character is not one of the
            RegEx's meta characters ".$|()[{^?*+\\", or
         (2)two-char String and the first char is the backslash and
            the second is not the ascii digit or ascii letter.
         */
        char ch = 0;
        if (((regex.length() == 1 &&
                ".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) ||
                (regex.length() == 2 &&
                        regex.charAt(0) == '\\' &&
                        (((ch = regex.charAt(1))-'0')|('9'-ch)) < 0 &&
                        ((ch-'a')|('z'-ch)) < 0 &&
                        ((ch-'A')|('Z'-ch)) < 0)) &&
                (ch < Character.MIN_HIGH_SURROGATE ||
                        ch > Character.MAX_LOW_SURROGATE)) {
            int off = 0;
            int next = 0;
            boolean limited = limit > 0;
            C.List<FastStr> list = C.newList();
            while ((next = indexOf(ch, off)) != -1) {
                if (!limited || list.size() < limit - 1) {
                    list.add(substr(off, next));
                    off = next + 1;
                } else {    // last one
                    //assert (list.size() == limit - 1);
                    list.add(substr(off, buf.length));
                    off = buf.length;
                    break;
                }
            }
            // If no match was found, return this
            if (off == 0) {
                return C.listOf(this);
            }

            // Add remaining segment
            if (!limited || list.size() < limit) {
                list.add(substr(off, buf.length));
            }

            // Construct result
            int resultSize = list.size();
            if (limit == 0) {
                while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
                    resultSize--;
                }
            }
            return list.subList(0, resultSize);
        }
        String[] sa = Pattern.compile(regex).split(this, limit);
        int len = sa.length;
        FastStr[] ssa = new FastStr[len];
        for (int i = 0; i < len; ++i) {
            ssa[i] = unsafeOf(sa[i]);
        }
        return C.listOf(ssa);
    }


    /**
     * Wrapper of {@link String#toLowerCase(java.util.Locale)} but return FastStr type instance
     *
     * @param locale the locale
     * @return a FastStr instance with all characters from this FastStr
     *         be converted into lowercase based on the locale specified
     */
    public FastStr toLowerCase(Locale locale) {
        String s = toString();
        return unsafeOf(s.toLowerCase(locale));
    }

    /**
     * Wrapper of {@link String#toUpperCase(java.util.Locale)} but return FastStr type instance
     *
     * @param locale the locale
     * @return a FastStr instance with all characters from this FastStr
     *         be converted into uppercase based on the locale specified
     */
    @Override
    public FastStr toUpperCase(Locale locale) {
        String s = toString();
        return unsafeOf(s.toUpperCase(locale));
    }

    /**
     * Wrapper of {@link String#trim()} and return FastStr type instance
     *
     * @return a FastStr instance without leading and tail space characters
     *         from this FastStr instance
     */
    @Override
    public FastStr trim() {
        char[] value = this.buf;
        int len = end;
        int st = begin;
        char[] val = value;    /* avoid getfield opcode */

        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return ((st > begin) || (len < size())) ? new FastStr(value, st, len) : this;
    }

    /**
     * Alias of {@link #toCharArray()}
     *
     * @return char array buf copy of this FastStr
     */
    public char[] charArray() {
        char[] newBuf = new char[size()];
        copyTo(newBuf, 0);
        return newBuf;
    }

    /**
     * Return char array buf of this FastStr instance.
     * Note this method might return the char array buf directly
     * without copy operation
     * @return the char array buf of this FastStr
     */
    public char[] unsafeChars() {
        if (begin == 0) return buf;
        char[] newBuf = new char[size()];
        copyTo(newBuf, 0);
        return newBuf;
    }

    /**
     * Wrapper of {@link String#intern()}
     *
     * @return the intern of the string
     */
    @Override
    public String intern() {
        return toString().intern();
    }

    // -- extensions
    @Override
    public FastStr afterFirst(String s) {
        return afterFirst(unsafeOf(s));
    }

    @Override
    public FastStr afterLast(String s) {
        return afterLast(unsafeOf(s));
    }

    @Override
    public FastStr afterFirst(FastStr s) {
        int pos = indexOf(s);
        if (pos < 0) return EMPTY_STR;
        return substr(pos + s.size());
    }

    @Override
    public FastStr afterLast(FastStr s) {
        int pos = lastIndexOf(s);
        if (pos < 0) return EMPTY_STR;
        return substr(pos + s.size());
    }

    @Override
    public FastStr afterFirst(char c) {
        int pos = indexOf(c);
        if (pos < 0) return EMPTY_STR;
        return substr(pos + 1);
    }

    @Override
    public FastStr afterLast(char c) {
        int pos = lastIndexOf(c);
        if (pos < 0) return EMPTY_STR;
        return substr(pos + 1);
    }

    public FastStr beforeFirst(String s) {
        return beforeFirst(unsafeOf(s));
    }

    public FastStr beforeLast(String s) {
        return beforeLast(unsafeOf(s));
    }

    public FastStr beforeFirst(FastStr s) {
        int pos = indexOf(s);
        if (pos < 0) return EMPTY_STR;
        return substr(0, pos);
    }

    public FastStr beforeLast(FastStr s) {
        int pos = lastIndexOf(s);
        if (pos < 0) return EMPTY_STR;
        return substr(0, pos);
    }

    @Override
    public FastStr beforeFirst(char c) {
        int pos = indexOf(c);
        if (pos < 0) return EMPTY_STR;
        return substr(0, pos);
    }

    @Override
    public FastStr beforeLast(char c) {
        int pos = lastIndexOf(c);
        if (pos < 0) return EMPTY_STR;
        return substr(0, pos);
    }

    public FastStr strip(String prefix, String suffix) {
        FastStr s = this;
        if (startsWith(prefix)) s = s.substr(prefix.length());
        if (s.endsWith(suffix)) s = s.substr(0, s.size() - suffix.length());
        return s;
    }

    public FastStr strip(FastStr prefix, FastStr suffix) {
        FastStr s = this;
        if (startsWith(prefix)) s = s.substr(prefix.size());
        if (s.endsWith(suffix)) s = s.substr(0, s.size() - suffix.size());
        return s;
    }

    public FastStr urlEncode() {
        String s;
        try {
            s = Unsafe.stringOf(buf);
        } catch (Exception e) {
            s = toString();
        }
        return unsafeOf(S.urlEncode(s));
    }

    public FastStr decodeBASE64() {
        String s;
        try {
            s = Unsafe.stringOf(buf);
        } catch (Exception e) {
            s = toString();
        }
        return unsafeOf(S.decodeBASE64(s));
    }

    public FastStr encodeBASE64() {
        String s;
        try {
            s = Unsafe.stringOf(buf);
        } catch (Exception e) {
            s = toString();
        }
        return unsafeOf(S.encodeBASE64(s));
    }

    @Override
    public FastStr capFirst() {
        if (isEmpty()) return this;
        int sz = size();
        char[] buf = this.buf;
        char[] newBuf = S.unsafeCapFirst(buf, begin, sz);
        if (buf == newBuf) return this;
        return unsafeOf(newBuf, 0, sz);
    }

    @Override
    public int count(String search, boolean overlap) {
        char[] searchBuf = bufOf(search);
        return count(searchBuf, 0, searchBuf.length, overlap);
    }

    @Override
    public int count(FastStr search, boolean overlap) {
        return count(search.buf, search.begin, search.size(), overlap);
    }

    private int toInternalId(int index) {
        return begin + index;
    }

    private int toExternalId(int index) {
        return index - begin;
    }

    private void copyTo(char[] buf, int begin) {
        System.arraycopy(this.buf, this.begin, buf, begin, size());
    }

    private int indexOfSupplementary(int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char[] buf = this.buf;
            final char hi = highSurrogate(ch);
            final char lo = lowSurrogate(ch);
            final int max = size() - 1;
            for (int i = fromIndex; i < max; i++) {
                if (buf[toInternalId(i)] == hi && buf[toInternalId(i + 1)] == lo) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int lastIndexOfSupplementary(int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char[] buf = this.buf;
            char hi = highSurrogate(ch);
            char lo = lowSurrogate(ch);
            int i = Math.min(fromIndex, buf.length - 2);
            for (; i >= 0; i--) {
                if (buf[toInternalId(i)] == hi && buf[toInternalId(i + 1)] == lo) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int count(char[] search, int searchOffset, int searchCount, boolean overlap) {
        if (isEmpty()) return 0;
        return S.count(buf, begin, size(), search, searchOffset, searchCount, overlap);
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
    private static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
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

    private static char[] bufOf(String s) {
        try {
            return Unsafe.bufOf(s);
        } catch (Exception e) {
            return s.toCharArray();
        }
    }

    // ---- factory methods

    /**
     * Construct a FastStr from a char array
     * @param ca the char array
     * @return a FastStr
     */
    public static FastStr of(char[] ca) {
        if (ca.length == 0) return EMPTY_STR;
        char[] newArray = new char[ca.length];
        System.arraycopy(ca, 0, newArray, 0, ca.length);
        return new FastStr(ca);
    }

    /**
     * Construct a FastStr from a CharSequence
     * @param cs the CharSequence instance
     * @return a FastStr
     */
    public static FastStr of(CharSequence cs) {
        if (cs instanceof FastStr) {
            return (FastStr)cs;
        }
        return of(cs.toString());
    }

    /**
     * Construct a FastStr from a String
     * @param s the String
     * @return a FastStr
     */
    public static FastStr of(String s) {
        int sz = s.length();
        if (sz == 0) return EMPTY_STR;
        char[] buf = s.toCharArray();
        return new FastStr(buf, 0, sz);
    }

    /**
     * Construct a FastStr from a StringBuilder
     * @param sb the string builder
     * @return a FastStr
     */
    public static FastStr of(StringBuilder sb) {
        int sz = sb.length();
        if (0 == sz) return EMPTY_STR;
        char[] buf = new char[sz];
        for (int i = 0; i < sz; ++i) {
            buf[i] = sb.charAt(i);
        }
        return new FastStr(buf, 0, sz);
    }

    /**
     * Construct a FastStr from a StringBuffer
     * @param sb the string buffer
     * @return the FastStr
     */
    public static FastStr of(StringBuffer sb) {
        int sz = sb.length();
        if (0 == sz) return EMPTY_STR;
        char[] buf = new char[sz];
        for (int i = 0; i < sz; ++i) {
            buf[i] = sb.charAt(i);
        }
        return new FastStr(buf, 0, sz);
    }

    /**
     * Construct a FastStr instance from an iterable of characters
     * @param itr the character iterable
     * @return the FastStr
     */
    public static FastStr of(Iterable<Character> itr) {
        StringBuilder sb = new StringBuilder();
        for (Character c : itr) {
            sb.append(c);
        }
        return of(sb);
    }

    /**
     * Construct a FastStr instance from a collection of characters
     * @param col the character collection
     * @return a FastStr instance
     */
    public static FastStr of(Collection<Character> col) {
        int sz = col.size();
        if (0 == sz) return EMPTY_STR;
        char[] buf = new char[sz];
        Iterator<Character> itr = col.iterator();
        int i = 0;
        while (itr.hasNext()) {
            buf[i++] = itr.next();
        }
        return new FastStr(buf, 0, sz);
    }

    /**
     * Construct a FastStr instance from an iterator of characters
     * @param itr the character iterator
     * @return a FastStr instance consists of all chars in the iterator
     */
    public static FastStr of(Iterator<Character> itr) {
        StringBuilder sb = new StringBuilder();
        while (itr.hasNext()) {
            sb.append(itr.next());
        }
        return of(sb);
    }

    public static FastStr of(byte[] bytes, String encoding) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = Charset.forName(encoding).decode(byteBuffer);
        char[] chars = Arrays.copyOfRange(charBuffer.array(), charBuffer.position(), charBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return FastStr.of(chars);
    }

    /**
     * Construct a FastStr instance from a String instance.
     * The FastStr instance will share the char array buf with
     * the String instance
     * @param s the string instance
     * @return A FastStr instance
     */
    public static FastStr unsafeOf(String s) {
        int sz = s.length();
        if (sz == 0) return EMPTY_STR;
        char[] buf = bufOf(s);
        return new FastStr(buf, 0, sz);
    }

    /**
     * Construct a FastStr instance from char array without array copying
     * @param buf the char array
     * @return a FastStr instance from the char array
     */
    @SuppressWarnings("unused")
    public static FastStr unsafeOf(char[] buf) {
        E.NPE(buf);
        return new FastStr(buf, 0, buf.length);
    }

    /**
     * Construct a FastStr instance from char array, from the start position, finished at end position
     * without copying the array. This method might use the array directly instead of copying elements
     * from the array. Thus it is extremely important that the array buf passed in will NOT be updated
     * outside the FastStr instance.
     * @param buf the char array
     * @param start the start position (inclusive)
     * @param end the end position (exclusive)
     * @return a FastStr instance that consist of chars specified
     */
    public static FastStr unsafeOf(char[] buf, int start, int end) {
        E.NPE(buf);
        E.illegalArgumentIf(start < 0 || end > buf.length);
        if (end < start) return EMPTY_STR;
        return new FastStr(buf, start, end);
    }

    /**
     * Return the char array that backed the char sequence specified
     * @param chars the char sequence
     * @return an array of chars of the char sequence
     */
    @SuppressWarnings("unused")
    public static char[] bufOf(CharSequence chars) {
        return FastStr.of(chars).charArray();
    }

    private static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10)
                + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));

    }

    private static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
    }

}
