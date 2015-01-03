package org.osgl.util;

import org.osgl._;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

public class Str extends StrBase<Str> {

    public static final Str EMPTY_STR = new Str("");

    @Override
    protected Class<Str> _impl() {
        return Str.class;
    }

    @Override
    protected Str _empty() {
        return EMPTY_STR;
    }

    private String s;
    private Str(String s) {
        this.s = s;
    }

    protected Str() {}

    @Override
    public int length() {
        return s.length();
    }

    @Override
    public boolean isEmpty() {
        return EMPTY_STR == this || null == s || s.isEmpty();
    }

    @Override
    public Str subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return EMPTY_STR;
        }
        return of(s.substring(fromIndex, toIndex));
    }

    @Override
    public Str takeWhile(_.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), predicate, FilteredIterator.Type.WHILE));
    }

    @Override
    public Str dropWhile(_.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), _.F.negate(predicate), FilteredIterator.Type.UNTIL));
    }

    @Override
    public Str remove(_.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), _.F.negate(predicate), FilteredIterator.Type.ALL));
    }

    @Override
    public Str insert(int index, Character character) throws IndexOutOfBoundsException {
        StringBuilder sb = new StringBuilder(s.substring(0, index));
        sb.append(character);
        sb.append(s.substring(index, size()));
        return of(sb);
    }

    @Override
    public Str reverse() {
        return of(new StringBuilder(s).reverse());
    }

    @Override
    public Str append(Collection<? extends Character> collection) {
        int sz = s.length(), sz2 = collection.size();
        if (0 == sz2) return this;
        if (0 == sz) return of((Collection<Character>)collection);
        StringBuilder sb = new StringBuilder(sz + sz2);
        sb.append(s).append(collection);
        return of(sb);
    }

    @Override
    public Str append(C.List<Character> list) {
        int sz = s.length(), sz2 = list.size();
        if (0 == sz2) return this;
        if (0 == sz) return of(list);
        StringBuilder sb = new StringBuilder(sz + sz2);
        sb.append(s).append(list);
        return of(sb);
    }

    @Override
    public Str append(Character character) {
        StringBuilder sb = new StringBuilder(s).append(character);
        return of(sb);
    }

    public Str append(Str s) {
        if (s.isEmpty()) {
            return this;
        }
        if (isEmpty()) {
            return s;
        }
        return of(this.s.concat(s.s));
    }

    public Str append(String s) {
        if ("".equals(s)) {
            return this;
        }
        if (EMPTY_STR.equals(this)) {
            return of(s);
        }
        return of(this.s.concat(s));
    }

    @Override
    public Str prepend(Collection<? extends Character> collection) {
        int sz = s.length(), sz2 = collection.size();
        if (0 == sz2) return this;
        if (0 == sz) return of((Collection<Character>)collection);
        StringBuilder sb = new StringBuilder(sz + sz2);
        sb.append(collection).append(s);
        return of(sb);
    }

    @Override
    public Str prepend(C.List<Character> list) {
        int sz = s.length(), sz2 = list.size();
        if (0 == sz2) return this;
        if (0 == sz) return of(list);
        StringBuilder sb = new StringBuilder(sz + sz2);
        sb.append(list).append(s);
        return of(sb);
    }

    @Override
    public Str prepend(Character character) {
        StringBuilder sb = new StringBuilder(character).append(s);
        return of(sb);
    }

    @Override
    public Str prepend(Str s) {
        if (EMPTY_STR.equals(s)) {
            return this;
        }
        if (EMPTY_STR.equals(this)) {
            return s;
        }
        return of(s.s.concat(this.s));
    }

    @Override
    public Str prepend(String s) {
        if ("".equals(s)) {
            return this;
        }
        if (EMPTY_STR.equals(this)) {
            return of(s);
        }
        return of(s.concat(this.s));
    }

    @Override
    public char charAt(int index) {
        return s.charAt(index);
    }

    @Override
    public Str subSequence(int start, int end) {
        return subList(start, end);
    }

    /**
     * Return a joined str of this for n times
     * @param n the times this str to be joined
     * @return the joined str
     */
    @Override
    public Str times(int n) {
        return of(S.times(s, n));
    }

    @Override
    public int compareTo(Str o) {
        return s.compareTo(o.s);
    }

    @Override
    public String toString() {
        return s;
    }

    /**
     * Returns a FastStr instance that shares
     * the char array buf of the back String
     */
    @Override
    public FastStr toFastStr() {
        return FastStr.unsafeOf(s);
    }



    @Override
    public int hashCode() {
        return s.hashCode();
    }

    // --- String utilities ---

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

    public byte[] getBytesAscII() {
        if (isEmpty()) return new byte[0];
        int sz = size();
        byte[] ret = new byte[sz];
        try {
            char[] buf = Unsafe.bufOf(s);
            for (int i = 0; i < sz; ++i) {
                ret[i] = (byte) buf[i];
            }
        } catch (Exception e) {
            for (int i = 0; i < sz; ++i) {
                ret[i] = (byte) charAt(i);
            }
        }
        return ret;
    }

    public byte[] getBytesUTF8() {
        return s.getBytes(Charset.forName("UTF-8"));
    }
    /**
     * Wrapper of {@link String#contentEquals(CharSequence)}
     *
     * @param x
     * @return true if content equals content of the specified char sequence
     */
    @Override
    public boolean contentEquals(CharSequence x) {
        return s.contentEquals(x);
    }

    /**
     * @param x
     * @return <code>true</code> if the content of this str equals to the specified str
     */
    @Override
    public boolean contentEquals(Str x) {
        if (null == x) {
            return false;
        }
        return x.s.equals(s);
    }

    /**
     * Wrapper of {@link String#equalsIgnoreCase(String)}
     *
     * @param x
     * @return {@code true} if the argument is not {@code null} and it
     *         represents an equivalent {@code String} ignoring case; {@code
     *         false} otherwise
     */
    @Override
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
    @Override
    public boolean equalsIgnoreCase(CharSequence x) {
        return null == x ? false : s.equalsIgnoreCase(x.toString());
    }

    @Override
    public int compareTo(String x) {
        return s.compareTo(x);
    }

    @Override
    public int compareToIgnoreCase(Str x) {
        return s.compareToIgnoreCase(x.s);
    }

    @Override
    public int compareToIgnoreCase(String x) {
        return s.compareToIgnoreCase(x);
    }

    @Override
    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 Str other, int ooffset, int len) {
        return s.regionMatches(ignoreCase, toffset, other.s, ooffset, len);
    }

    @Override
    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 String other, int ooffset, int len) {
        return s.regionMatches(ignoreCase, toffset, other, ooffset, len);
    }

    @Override
    public boolean startsWith(Str prefix, int toffset) {
        return s.startsWith(prefix.s, toffset);
    }

    @Override
    public boolean startsWith(String prefix, int toffset) {
        return s.startsWith(prefix, toffset);
    }

    @Override
    public boolean endsWith(Str suffix) {
        return s.endsWith(suffix.s);
    }

    @Override
    public boolean endsWith(String suffix) {
        return s.endsWith(suffix);
    }

    /**
     * Wrapper of {@link String#indexOf(String, int)}
     * @param ch
     * @param fromIndex
     * @return
     */
    @Override
    public int indexOf(int ch, int fromIndex) {
        return s.indexOf(ch, fromIndex);
    }

    /**
     * Wrapper of {@link String#lastIndexOf(int, int)}
     * @param ch
     * @param fromIndex
     * @return
     */
    @Override
    public int lastIndexOf(int ch, int fromIndex) {
        return s.lastIndexOf(ch, fromIndex);
    }

    @Override
    public int indexOf(String str, int fromIndex) {
        return s.indexOf(str, fromIndex);
    }

    @Override
    public int indexOf(Str str, int fromIndex) {
        return s.indexOf(str.s, fromIndex);
    }

    @Override
    public int lastIndexOf(String str, int fromIndex) {
        return s.lastIndexOf(str, fromIndex);
    }

    @Override
    public int lastIndexOf(Str str, int fromIndex) {
        return s.lastIndexOf(str.s, fromIndex);
    }

    /**
     * Wrapper of {@link String#substring(int)}
     * @param beginIndex
     * @return
     */
    @Override
    public String substring(int beginIndex) {
        return s.substring(beginIndex);
    }

    /**
     * Wrapper of {@link #substring(int, int)}
     * @param beginIndex
     * @param endIndex
     * @return
     */
    @Override
    public String substring(int beginIndex, int endIndex) {
        return s.substring(beginIndex, endIndex);
    }

    /**
     * Wrapper of {@link String#replace(char, char)} but return Str instance
     * @param oldChar
     * @param newChar
     * @return
     */
    @Override
    public Str replace(char oldChar, char newChar) {
        return of(s.replace(oldChar, newChar));
    }

    /**
     * Wrapper of {@link String#matches(String)}
     * @param regex
     * @return
     */
    @Override
    public boolean matches(String regex) {
        return s.matches(regex);
    }

    /**
     * Wrapper of {@link String#contains(CharSequence)}
     * @param s
     * @return
     */
    @Override
    public boolean contains(CharSequence s) {
        return this.s.contains(s);
    }

    /**
     * Wrapper of {@link String#replaceFirst(String, String)} but return Str inance
     * @param regex
     * @param replacement
     * @return
     */
    @Override
    public Str replaceFirst(String regex, String replacement) {
        return of(s.replaceFirst(regex, replacement));
    }

    /**
     * Wrapper of {@link String#replaceAll(String, String)} but return Str type instance
     * @param regex
     * @param replacement
     * @return
     */
    @Override
    public Str replaceAll(String regex, String replacement) {
        return of(s.replaceAll(regex, replacement));
    }

    /**
     * Wrapper of {@link String#replace(CharSequence, CharSequence)} but return Str type instance
     * @param target
     * @param replacement
     * @return
     */
    @Override
    public Str replace(CharSequence target, CharSequence replacement) {
        return of(s.replace(target, replacement));
    }

    /**
     * Wrapper of {@link String#split(String, int)} but return an immutable List of Str instances
     * @param regex
     * @param limit
     * @return
     */
    @Override
    public C.List<Str> split(String regex, int limit) {
        String[] sa = s.split(regex, limit);
        int len = sa.length;
        Str[] ssa = new Str[len];
        for (int i = 0; i < len; ++i) {
            ssa[i] = of(sa[i]);
        }
        return C.listOf(ssa);
    }

    /**
     * Wrapper of {@link String#toLowerCase(java.util.Locale)} but return Str type instance
     * @param locale
     * @return
     */
    @Override
    public Str toLowerCase(Locale locale) {
        return of(s.toLowerCase(locale));
    }

    /**
     * Wrapper of {@link String#toUpperCase(java.util.Locale)} but return Str type instance
     * @param locale
     * @return
     */
    @Override
    public Str toUpperCase(Locale locale) {
        return of(s.toUpperCase(locale));
    }

    /**
     * Wrapper of {@link String#trim()} and return Str type instance
     * @return
     */
    @Override
    public Str trim() {
        return of(s.trim());
    }

    /**
     * Alias of {@link #toCharArray()}
     * @return
     */
    @Override
    public char[] chars() {
        return s.toCharArray();
    }

    /**
     * Wrapper of {@link String#intern()}
     * @return
     */
    @Override
    public String intern() {
        return s.intern();
    }

    // -- extensions
    @Override
    public Str afterFirst(Str s) {
        return afterFirst(s.s);
    }

    @Override
    public Str afterLast(Str s) {
        return afterLast(s.s);
    }

    @Override
    public Str beforeLast(Str s) {
        return beforeLast(s.s);
    }

    @Override
    public Str afterFirst(String s) {
        return of(S.afterFirst(this.s, s));
    }

    @Override
    public Str afterLast(String s) {
        return of(S.afterLast(this.s, s));
    }

    @Override
    public Str afterFirst(char c) {
        return afterFirst(String.valueOf(c));
    }

    @Override
    public Str afterLast(char c) {
        return afterLast(String.valueOf(c));
    }

    @Override
    public Str beforeFirst(Str s) {
        return of(S.beforeFirst(this.s, s.s));
    }

    @Override
    public Str beforeFirst(String s) {
        return of(S.beforeFirst(this.s, s));
    }

    @Override
    public Str beforeLast(String s) {
        return of(S.beforeLast(this.s, s));
    }

    @Override
    public Str strip(String prefix, String suffix) {
        return of(S.strip(s, prefix, suffix));
    }

    @Override
    public Str beforeFirst(char c) {
        return beforeFirst(String.valueOf(c));
    }

    @Override
    public Str beforeLast(char c) {
        return beforeLast(String.valueOf(c));
    }

    @Override
    public Str urlEncode() {
        return of(S.urlEncode(s));
    }

    @Override
    public Str decodeBASE64() {
        return of(S.decodeBASE64(s));
    }

    @Override
    public Str encodeBASE64() {
        return of(S.encodeBASE64(s));
    }

    @Override
    public Str capFirst() {
        return of(S.capFirst(s));
    }

    @Override
    public int count(String search, boolean overlap) {
        return S.count(s, search, overlap);
    }

    @Override
    public int count(Str search, boolean overlap) {
        return S.count(s, search.s, overlap);
    }

    // --- factory methods

    public static Str of(String s) {
        if (S.empty(s)) return EMPTY_STR;
        return new Str(s);
    }

    public static Str of(StringBuilder sb) {
        if (sb.length() == 0) return EMPTY_STR;
        return new Str(sb.toString());
    }

    public static Str of(Iterable<Character> itr) {
        if (itr instanceof Str) {
            return (Str)itr;
        }
        if (itr instanceof Collection) {
            return of((Collection<Character>) itr);
        }
        StringBuilder sb = new StringBuilder();
        for (Character c : itr) {
            sb.append(c);
        }
        return of(sb);
    }

    public static Str of(Collection<Character> col) {
        if (col instanceof Str) {
            return (Str)col;
        }
        StringBuilder sb = new StringBuilder(col.size());
        sb.append(col.toArray(new Character[]{}));
        return of(sb);
    }

    public static Str of(Iterator<Character> itr) {
        StringBuilder sb = new StringBuilder();
        while (itr.hasNext()) {
            sb.append(itr.next());
        }
        return of(sb);
    }


    public static void main(String[] args) {
        Str s = S.str("abc");
        System.out.println(s.drop(1));
        System.out.println(s.drop(-1));
    }

}
