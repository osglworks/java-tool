package org.osgl.util;

import org.osgl._;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by luog on 13/12/13.
 */
public class Str extends ListBase<Character>
implements RandomAccess, CharSequence, java.io.Serializable, Comparable<Str> {

    public static final Str EMPTY_STR = new Str("");

    private String s;

    private Str(String s) {
        this.s = s;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY, C.Feature.LIMITED, C.Feature.ORDERED, C.Feature.IMMUTABLE, C.Feature.LAZY, C.Feature.PARALLEL, C.Feature.RANDOM_ACCESS);
    }

    @Override
    public int size() {
        return s.length();
    }

    @Override
    public Character get(int index) {
        return s.charAt(index);
    }


    @Override
    public Character set(int index, Character element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Character element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Character remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Character> listIterator(int index) {
        return new LstItr(index);
    }

    private class Itr implements Iterator<Character> {
        protected int cursor = 0;
        protected final int len = s.length();

        @Override
        public boolean hasNext() {
            return cursor != len;
        }

        @Override
        public Character next() {
            if (cursor >= len) {
                throw new NoSuchElementException();
            }
            return s.charAt(cursor++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    private class LstItr extends Itr implements ListIterator<Character> {

        LstItr() {this(0);}

        LstItr(int index) {
            if (index < 0 || index > len) {
                throw new IndexOutOfBoundsException();
            }
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public Character previous() {
            if (cursor < 0) {
                throw new NoSuchElementException();
            }
            cursor--;
            return s.charAt(--cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(Character t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Character t) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Character) {
            Character c = (Character)o;
            return s.indexOf(c);
        } else if (o instanceof String) {
            String str = (String)o;
            return s.indexOf(str);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Character) {
            Character c = (Character)o;
            return s.lastIndexOf(c);
        } else if (o instanceof String) {
            String str = (String)o;
            return s.lastIndexOf(str);
        }
        return -1;
    }

    @Override
    public Str subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return EMPTY_STR;
        }
        return of(s.substring(fromIndex, toIndex));
    }

    @Override
    protected Str setFeature(C.Feature feature) {
        super.setFeature(feature);
        return this;
    }

    @Override
    public Str parallel() {
        super.parallel();
        return this;
    }

    @Override
    public Str lazy() {
        super.lazy();
        return this;
    }

    @Override
    public Str eager() {
        super.eager();
        return this;
    }

    @Override
    public Str snapshot() {
        return this;
    }

    @Override
    public Str readOnly() {
        return this;
    }

    @Override
    public Str copy() {
        return this;
    }

    @Override
    protected Str unsetFeature(C.Feature feature) {
        super.unsetFeature(feature);
        return this;
    }

    @Override
    public Str take(int n) {
        int sz = s.length();
        if (sz == 0) return EMPTY_STR;
        if (n == 0) {
            return EMPTY_STR;
        } else if (n < 0) {
            n = -n;
            if (n >= sz) return this;
            return drop(sz - n);
        } else if (n >= sz) {
            return this;
        }
        return subList(0, n);
    }

    @Override
    public Str head(int n) {
        return take(n);
    }

    @Override
    public Str takeWhile(_.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), predicate, FilteredIterator.Type.WHILE));
    }

    @Override
    public Str drop(int n) throws IndexOutOfBoundsException {
        int sz = size();
        if (n < 0) {
            n = -n;
            if (n >= sz) return EMPTY_STR;
            return take(sz - n);
        }
        if (n == 0) return this;
        if (n > sz) return EMPTY_STR;
        return subList(n, sz);
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
    public <R> C.List<R> map(_.Function<? super Character, ? extends R> mapper) {
        if ("".equals(s)) {
            return Nil.list();
        }
        int sz = s.length();
        if (0 == sz) {
            return Nil.list();
        }
        ListBuilder<R> lb = new ListBuilder<R>(sz);
        forEach(_.f1(mapper).andThen(C.F.addTo(lb)));
        return lb.toList();
    }

    @Override
    public <R> C.List<R> flatMap(_.Function<? super Character, ? extends Iterable<? extends R>> mapper
    ) {
        if ("".equals(s)) {
            return Nil.list();
        }
        return super.flatMap(mapper);
    }

    @Override
    public Str filter(_.Function<? super Character, Boolean> predicate) {
        if ("".equals(s)) {
            return EMPTY_STR;
        }
        return of(new FilteredIterator<Character>(iterator(), predicate, FilteredIterator.Type.ALL));
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
    public Str without(Collection<? super Character> col) {
        return filter(_.F.negate(C.F.containsIn(col)));
    }

    @Override
    public Str accept(_.Function<? super Character, ?> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public Str acceptLeft(_.Function<? super Character, ?> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public Str acceptRight(_.Function<? super Character, ?> visitor) {
        super.acceptRight(visitor);
        return this;
    }

    @Override
    public Str tail() {
        int sz = size();
        if (0 == sz) {
            throw new UnsupportedOperationException();
        }
        return subList(1, sz);
    }

    @Override
    public Str tail(int n) {
        int sz = size();
        if (n < 0) {
            return head(-n);
        } else if (n == 0) {
            return EMPTY_STR;
        } else if (n >= sz) {
            return this;
        }
        return subList(sz - n, sz);
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
        if (EMPTY_STR.equals(s)) {
            return this;
        }
        if (EMPTY_STR.equals(this)) {
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
    public C.List<Character> prepend(Collection<? extends Character> collection) {
        int sz = s.length(), sz2 = collection.size();
        if (0 == sz2) return this;
        if (0 == sz) return of((Collection<Character>)collection);
        StringBuilder sb = new StringBuilder(sz + sz2);
        sb.append(collection).append(s);
        return of(sb);
    }

    @Override
    public C.List<Character> prepend(C.List<Character> list) {
        int sz = s.length(), sz2 = list.size();
        if (0 == sz2) return this;
        if (0 == sz) return of(list);
        StringBuilder sb = new StringBuilder(sz + sz2);
        sb.append(list).append(s);
        return of(sb);
    }

    @Override
    public C.List<Character> prepend(Character character) {
        StringBuilder sb = new StringBuilder(character).append(s);
        return of(sb);
    }


    public Str prepend(Str s) {
        if (EMPTY_STR.equals(s)) {
            return this;
        }
        if (EMPTY_STR.equals(this)) {
            return s;
        }
        return of(s.s.concat(this.s));
    }

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
    public int length() {
        return s.length();
    }

    @Override
    public char charAt(int index) {
        return s.charAt(index);
    }

    @Override
    public Str subSequence(int start, int end) {
        return subList(start, end);
    }

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

    @Override
    public int compareTo(Str o) {
        return s.compareTo(o.s);
    }

    @Override
    public String toString() {
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Str) {
            return ((Str) o).s.equals(s);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    public String value() {return s;}

    public String val() {return s;}

    // --- String utilities ---

    public boolean empty() {
        return isEmpty();
    }

    public boolean notEmpty() {
        return !isEmpty();
    }

    /**
     * <p>Return a string no longer than specified max length.
     * <p/>
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param max the maximum length of the result
     * @return
     */
    public Str cutOff(int max) {
        return maxLength(max);
    }

    /**
     * <p>Return a string no longer than specified max length.
     * <p/>
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param max the maximum length of the result
     * @return
     */
    public Str maxLength(int max) {
        if (null == s) return EMPTY_STR;
        if (s.length() < (max - 3)) return this;
        return subList(0, max).append("...");
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
     * Alias of {@link #contentEquals(Str)}
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
        return of(s.substring(beginIndex));
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
        return of(s.substring(beginIndex, endIndex));
    }

    /**
     * Wrapper of {@link String#concat(String)} but return Str instance
     * @param str
     * @return
     */
    public Str concat(String str) {
        return of(s.concat(str));
    }

    /**
     * Wrapper of {@link String#replace(char, char)} but return Str instance
     * @param oldChar
     * @param newChar
     * @return
     */
    public Str replace(char oldChar, char newChar) {
        return of(s.replace(oldChar, newChar));
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
        return of(s.replaceFirst(regex, replacement));
    }

    /**
     * Wrapper of {@link String#replaceAll(String, String)} but return Str type instance
     * @param regex
     * @param replacement
     * @return
     */
    public Str replaceAll(String regex, String replacement) {
        return of(s.replaceAll(regex, replacement));
    }

    /**
     * Wrapper of {@link String#replace(CharSequence, CharSequence)} but return Str type instance
     * @param target
     * @param replacement
     * @return
     */
    public Str replace(CharSequence target, CharSequence replacement) {
        return of(s.replace(target, replacement));
    }

    /**
     * Wrapper of {@link String#split(String, int)} but return an immutable List of Str instances
     * @param regex
     * @param limit
     * @return
     */
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
     * Wrapper of {@link String#split(String)} but return an immutable List of Str instances
     * @param regex
     * @return
     */
    public C.List<Str> split(String regex) {
        return split(regex, 0);
    }

    /**
     * Wrapper of {@link String#toLowerCase()} but return Str type instance
     *
     * @return
     */
    public Str toLowerCase() {
        return of(s.toLowerCase());
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
        return of(s.toLowerCase(locale));
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
        return of(s.toUpperCase());
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
        return of(s.toUpperCase(locale));
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
        return of(s.trim());
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
    public Str after(Str s) {
        return after(s.s);
    }

    public Str after(String s) {
        return of(S.after(this.s, s));
    }

    public Str afterFirst(String s) {
        return of(S.afterFirst(this.s, s));
    }

    public Str afterLast(String s) {
        return of(S.afterLast(this.s, s));
    }

    public Str before(String s) {
        return of(S.before(this.s, s));
    }

    public Str beforeFirst(String s) {
        return of(S.beforeFirst(this.s, s));
    }

    public Str beforeLast(String s) {
        return of(S.beforeLast(this.s, s));
    }

    public Str strip(String prefix, String suffix) {
        return of(S.strip(s, prefix, suffix));
    }

    public Str urlEncode() {
        return of(S.urlEncode(s));
    }

    public Str decodeBASE64() {
        return of(S.decodeBASE64(s));
    }

    public Str encodeBASE64() {
        return of(S.encodeBASE64(s));
    }

    public Str capFirst() {
        return of(S.capFirst(s));
    }

    public int count(String search) {
        return S.count(s, search);
    }

    public int count(Str search) {
        return S.count(s, search.value());
    }

    public int countWithOverlay(String search) {
        return S.count(s, search, true);
    }

    public int countWithOverlay(Str search) {
        return S.count(s, search.value(), true);
    }

    public static void main(String[] args) {
        Str s = S.str("abc");
        System.out.println(s.drop(1));
        System.out.println(s.drop(-1));
    }

}
