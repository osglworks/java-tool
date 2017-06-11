package org.osgl.util;

import org.osgl.$;

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
    public boolean isBlank() {
        return isEmpty() || "".equals(s.trim());
    }

    @Override
    public Str subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return EMPTY_STR;
        }
        return of(s.substring(fromIndex, toIndex));
    }

    @Override
    public Str takeWhile($.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), predicate, FilteredIterator.Type.WHILE));
    }

    @Override
    public Str dropWhile($.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), $.F.negate(predicate), FilteredIterator.Type.UNTIL));
    }

    @Override
    public Str remove($.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), $.F.negate(predicate), FilteredIterator.Type.ALL));
    }

    @Override
    public Str insert(int index, char character) throws StringIndexOutOfBoundsException {
        int len = s.length();
        if (len < Math.abs(index)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (index < 0) {
            index = len + index;
        }
        StringBuilder sb = new StringBuilder(s.substring(0, index));
        sb.append(character);
        sb.append(s.substring(index, size()));
        return of(sb);
    }

    @Override
    public Str insert(int index, Character character) throws IndexOutOfBoundsException {
        return insert_(index, (Object) character);
    }

    @Override
    public Str insert(int index, Character... ca) throws StringIndexOutOfBoundsException {
        return insert_(index, Str.of(ca));
    }

    @Override
    public Str insert(int index, char... ca) throws StringIndexOutOfBoundsException {
        return insert_(index, Str.of(ca));
    }

    @Override
    public Str insert(int index, StrBase<?> str) throws StringIndexOutOfBoundsException {
        return insert_(index, (Object) str);
    }

    @Override
    public Str insert(int index, String s) throws StringIndexOutOfBoundsException {
        return insert_(index, (Object) s);
    }

    private Str insert_(int index, Object o) throws StringIndexOutOfBoundsException {
        int len = s.length();
        if (len < Math.abs(index)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (index < 0) {
            index = len + index;
        }
        StringBuilder sb = new StringBuilder(s.substring(0, index));
        sb.append(o);
        sb.append(s.substring(index, size()));
        return of(sb);
    }

    @Override
    public Str reverse() {
        return of(new StringBuilder(s).reverse());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Str append(Collection<? extends Character> collection) {
        int sz = s.length(), sz2 = collection.size();
        if (0 == sz2) {
            return this;
        }
        if (0 == sz) {
            return of((Collection<Character>)collection);
        }
        StringBuilder sb = new StringBuilder(sz + sz2).append(s);
        for (Character c: collection) {
            sb.append(c);
        }
        return of(sb);
    }

    @Override
    public Str append(C.List<Character> list) {
        int sz = s.length(), sz2 = list.size();
        if (0 == sz2) return this;
        if (0 == sz) return of(list);
        StringBuilder sb = new StringBuilder(sz + sz2).append(s);
        for (Character c : list) {
            sb.append(c);
        }
        return of(sb);
    }

    @Override
    public Str append(char... array) {
        int sz = s.length(), sz2 = array.length;
        if (0 == sz2) return this;
        if (0 == sz) return of(array);
        StringBuilder sb = new StringBuilder(sz + sz2).append(s);
        for (Character c : array) {
            sb.append(c);
        }
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
    @SuppressWarnings("unchecked")
    public Str prepend(Collection<? extends Character> collection) {
        int sz = s.length(), sz2 = collection.size();
        if (0 == sz2) return this;
        if (0 == sz) return of((Collection<Character>)collection);
        StringBuilder sb = new StringBuilder(sz + sz2);
        for (char c : collection) {
            sb.append(c);
        }
        sb.append(this.s);
        return of(sb);
    }

    @Override
    public Str prepend(C.List<Character> list) {
        int sz = s.length(), sz2 = list.size();
        if (0 == sz2) return this;
        if (0 == sz) return of(list);
        StringBuilder sb = new StringBuilder(sz + sz2);
        for (char c : list) {
            sb.append(c);
        }
        sb.append(this.s);
        return of(sb);
    }

    @Override
    public Str prepend(char... chars) {
        int sz = s.length(), sz2 = chars.length;
        if (0 == sz2) return this;
        if (0 == sz) return of(chars);
        StringBuilder sb = new StringBuilder(sz + sz2);
        for (char c : chars) {
            sb.append(c);
        }
        sb.append(this.s);
        return of(sb);
    }

    @Override
    public Str prepend(Character character) {
        StringBuilder sb = new StringBuilder().append(character).append(s);
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

    @Override
    public Str times(int n) {
        return of(S.times(s, n));
    }

    @Override
    public Str padLeft(char c, int times) {
        char[] ca = new char[times];
        $.fill(c, ca);
        return prepend(ca);
    }

    @Override
    public Str lpad(char c, int times) {
        return padLeft(c, times);
    }

    @Override
    public Str padLeft(int times) {
        return padLeft(' ', times);
    }

    @Override
    public Str lpad(int times) {
        return padLeft(times);
    }

    @Override
    public Str padRight(char c, int times) {
        char[] ca = new char[times];
        $.fill(c, ca);
        return append(ca);
    }

    @Override
    public Str rpad(char c, int times) {
        return padRight(c, times);
    }

    @Override
    public Str padRight(int times) {
        return padRight(' ', times);
    }

    @Override
    public Str rpad(int times) {
        return padRight(times);
    }

    @Override
    public int compareTo(Str o) {
        return s.compareTo(o.s);
    }

    @Override
    public String toString() {
        return s;
    }

    @Override
    public FastStr toFastStr() {
        return FastStr.unsafeOf(s);
    }



    @Override
    public int hashCode() {
        return s.hashCode();
    }

    // --- String utilities ---

    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        s.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    public byte[] getBytes() {
        return s.getBytes();
    }

    public byte[] getBytes(String charsetName) {
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
        return null == charset ? s.getBytes() : s.getBytes(charset);
    }

    public byte[] getBytesAscII() {
        if (isEmpty()) return new byte[0];
        return s.getBytes(Charsets.US_ASCII);
    }

    public byte[] getBytesUTF8() {
        return s.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public boolean contentEquals(CharSequence x) {
        return s.contentEquals(x);
    }

    @Override
    public boolean contentEquals(Str x) {
        if (null == x) {
            return false;
        }
        return x.s.equals(s);
    }

    @Override
    public boolean equalsIgnoreCase(CharSequence x) {
        return null != x && s.equalsIgnoreCase(x.toString());
    }

    @Override
    public int compareTo(CharSequence x) {
        return s.compareTo(x.toString());
    }

    @Override
    public int compareToIgnoreCase(Str x) {
        return s.compareToIgnoreCase(x.s);
    }

    @Override
    public int compareToIgnoreCase(CharSequence x) {
        return s.compareToIgnoreCase(x.toString());
    }

    @Override
    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 Str other, int ooffset, int len) {
        return s.regionMatches(ignoreCase, toffset, other.s, ooffset, len);
    }

    @Override
    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 CharSequence other, int ooffset, int len) {
        return s.regionMatches(ignoreCase, toffset, other.toString(), ooffset, len);
    }

    @Override
    public boolean startsWith(Str prefix, int toffset) {
        return s.startsWith(prefix.s, toffset);
    }

    @Override
    public boolean startsWith(CharSequence prefix, int toffset) {
        return s.startsWith(prefix.toString(), toffset);
    }

    private boolean endsWith(String suffix, int offset) {
        int prefixSz = suffix.length();
        if (0 == prefixSz) {
            return true;
        }
        int matchStart = length() - offset;
        if (matchStart < prefixSz) {
            return false;
        }
        String matchStr = s.substring(0, matchStart);
        return matchStr.endsWith(suffix);
    }

    @Override
    public boolean endsWith(CharSequence suffix, int offset) {
        return endsWith(suffix.toString(), offset);
    }

    @Override
    public boolean endsWith(Str suffix, int toffset) {
        return endsWith(suffix.toString(), toffset);
    }

    @Override
    public int indexOf(int ch, int fromIndex) {
        return s.indexOf(ch, fromIndex);
    }

    @Override
    public int lastIndexOf(int ch, int fromIndex) {
        return s.lastIndexOf(ch, fromIndex);
    }

    @Override
    public int indexOf(CharSequence str, int fromIndex) {
        return s.indexOf(str.toString(), fromIndex);
    }

    @Override
    public int indexOf(Str str, int fromIndex) {
        return s.indexOf(str.s, fromIndex);
    }

    @Override
    public int lastIndexOf(CharSequence str, int fromIndex) {
        return s.lastIndexOf(str.toString(), fromIndex);
    }

    @Override
    public int lastIndexOf(Str str, int fromIndex) {
        return s.lastIndexOf(str.s, fromIndex);
    }

    @Override
    public String substring(int beginIndex) {
        return s.substring(beginIndex);
    }

    @Override
    public String substring(int beginIndex, int endIndex) {
        return s.substring(beginIndex, endIndex);
    }

    @Override
    public Str replace(char oldChar, char newChar) {
        String s1 = s.replace(oldChar, newChar);
        if (s1 == s) {
            return this;
        }
        return of(s1);
    }

    @Override
    public boolean matches(String regex) {
        return s.matches(regex);
    }

    @Override
    public boolean contains(CharSequence s) {
        return this.s.contains(s);
    }

    @Override
    public Str replaceFirst(String regex, String replacement) {
        return of(s.replaceFirst(regex, replacement));
    }

    @Override
    public Str replaceAll(String regex, String replacement) {
        return of(s.replaceAll(regex, replacement));
    }

    @Override
    public Str replace(CharSequence target, CharSequence replacement) {
        return of(s.replace(target, replacement));
    }

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

    @Override
    public Str toLowerCase(Locale locale) {
        return of(s.toLowerCase(locale));
    }

    @Override
    public Str toUpperCase(Locale locale) {
        return of(s.toUpperCase(locale));
    }

    @Override
    public Str trim() {
        return of(s.trim());
    }

    @Override
    public char[] charArray() {
        return s.toCharArray();
    }

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

    public static Str of(Character ... chars) {
        if (chars.length == 0) return EMPTY_STR;
        char[] ca = $.asPrimitive(chars);
        return new Str(new String(ca));
    }

    public static Str of(char ... ca) {
        if (ca.length == 0) return EMPTY_STR;
        return new Str(new String(ca));
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

}
