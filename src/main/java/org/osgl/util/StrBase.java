package org.osgl.util;

import org.osgl._;

import java.util.*;

public abstract class StrBase<T extends StrBase<T>> extends ListBase<Character>
implements RandomAccess, CharSequence, java.io.Serializable, Comparable<T> {

    protected StrBase(){}

    protected abstract Class<T> _impl();
    protected abstract T _empty();

    @Override
    public abstract T subList(int fromIndex, int toIndex) throws StringIndexOutOfBoundsException;
    @Override
    public abstract T insert(int index, Character character) throws StringIndexOutOfBoundsException;
    @Override
    public abstract T remove(_.Function<? super Character, Boolean> predicate);
    @Override
    public abstract T takeWhile(_.Function<? super Character, Boolean> predicate);
    @Override
    public abstract T dropWhile(_.Function<? super Character, Boolean> predicate);
    @Override
    public abstract T reverse();
    @Override
    public abstract T append(Collection<? extends Character> collection);
    @Override
    public abstract T append(C.List<Character> list);
    @Override
    public abstract T append(Character character);
    public abstract T append(T s);
    public abstract T append(String s);

    @Override
    public abstract T prepend(Collection<? extends Character> collection);
    @Override
    public abstract T prepend(C.List<Character> list);
    @Override
    public abstract T prepend(Character character);
    public abstract T prepend(T s);
    public abstract T prepend(String s);
    @Override
    public abstract T subSequence(int start, int end);
    public abstract T times(int n);
    public abstract void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin);
    /**
     * Returns byte array of string composed of only ascii chars
     */
    public abstract byte[] getBytesAscII();
    public abstract byte[] getBytesUTF8();
    public abstract FastStr toFastStr();
    public abstract boolean contentEquals(CharSequence chars);
    public abstract boolean contentEquals(T t);
    public abstract int indexOf(int ch, int fromIndex);
    public abstract int indexOf(String s, int fromIndex);
    public abstract int indexOf(T s, int fromIndex);
    public abstract int lastIndexOf(int ch, int fromIndex);
    public abstract int lastIndexOf(String s, int fromIndex);
    public abstract int lastIndexOf(T s, int fromIndex);
    public abstract String substring(int beginIndex);
    public abstract boolean equalsIgnoreCase(String x);
    public abstract boolean equalsIgnoreCase(CharSequence x);
    public abstract int compareTo(String x);
    public abstract int compareToIgnoreCase(T x);
    public abstract int compareToIgnoreCase(String x);
    public abstract boolean regionMatches(boolean ignoreCase, int toffset, T other, int ooffset, int len);
    public abstract boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len);
    public abstract boolean startsWith(T prefix, int toffset);
    public abstract boolean startsWith(String prefix, int toffset);
    public abstract boolean endsWith(T suffix);
    public abstract boolean endsWith(String suffix);
    public abstract String substring(int beginIndex, int endIndex);
    public abstract T replace(char oldChar, char newChar);
    public abstract T replaceFirst(String regex, String replacement);
    public abstract boolean matches(String regex);
    public abstract boolean contains(CharSequence s);
    public abstract T replaceAll(String regex, String replacement);
    public abstract T replace(CharSequence target, CharSequence replacement);
    public abstract C.List<T> split(String regex, int limit);
    public abstract T toLowerCase(Locale locale);
    public abstract T toUpperCase(Locale locale);
    public abstract T trim();
    public abstract char[] chars();
    public abstract String intern();
    public abstract T afterFirst(String s);
    public abstract T afterFirst(T s);
    public abstract T afterFirst(char c);
    public abstract T afterLast(String s);
    public abstract T afterLast(T characters);
    public abstract T afterLast(char c);
    public abstract T beforeFirst(String s);
    public abstract T beforeFirst(char c);
    public abstract T beforeFirst(T s);
    public abstract T beforeLast(String s);
    public abstract T beforeLast(T s);
    public abstract T beforeLast(char c);
    public abstract T strip(String prefix, String suffix);
    public abstract T urlEncode();
    public abstract T decodeBASE64();
    public abstract T encodeBASE64();
    public abstract T capFirst();
    public abstract int count(String search, boolean overlap);
    public abstract int count(T search, boolean overlap);

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof StrBase) {
            StrBase that = (StrBase)o;
            return that.contentEquals(this);
        }
        return false;
    }


    private T me() {
        return (T)this;
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.READONLY, C.Feature.LIMITED, C.Feature.ORDERED, C.Feature.IMMUTABLE, C.Feature.LAZY, C.Feature.PARALLEL, C.Feature.RANDOM_ACCESS);
    }

    @Override
    public T lazy() {
        return me();
    }

    @Override
    public T eager() {
        return me();
    }

    @Override
    public T snapshot() {
        return me();
    }

    @Override
    public T readOnly() {
        return me();
    }

    @Override
    public T copy() {
        return me();
    }

    public final void copy(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    @Override
    protected T setFeature(C.Feature feature) {
        return me();
    }

    @Override
    public T parallel() {
        return me();
    }

    @Override
    protected T unsetFeature(C.Feature feature) {
        return me();
    }

    @Override
    public final Character get(int index) {
        return charAt(index);
    }

    @Override
    public final int size() {
        return length();
    }

    @Override
    public final Character set(int index, Character element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(int index, Character element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Character remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Character> listIterator(int index) {
        return new LstItr(index);
    }

    private class Itr implements Iterator<Character> {
        protected int cursor = 0;
        protected final int len = length();

        @Override
        public boolean hasNext() {
            return cursor != len;
        }

        @Override
        public Character next() {
            if (cursor >= len) {
                throw new NoSuchElementException();
            }
            return charAt(cursor++);
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
            return charAt(--cursor);
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
            return indexOf(c);
        } else if (o instanceof String) {
            String str = (String)o;
            return indexOf(str);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Character) {
            Character c = (Character)o;
            return lastIndexOf(c);
        } else if (o instanceof String) {
            String str = (String)o;
            return lastIndexOf(str);
        }
        return -1;
    }

    @Override
    public T take(int n) {
        int sz = size();
        if (sz == 0) return _empty();
        if (n == 0) {
            return _empty();
        } else if (n < 0) {
            n = -n;
            if (n >= sz) return me();
            return drop(sz - n);
        } else if (n >= sz) {
            return me();
        }
        return subList(0, n);
    }

    @Override
    public T head(int n) {
        return take(n);
    }

    @Override
    public T drop(int n) throws IndexOutOfBoundsException {
        int sz = size();
        if (n < 0) {
            n = -n;
            if (n >= sz) return _empty();
            return take(sz - n);
        }
        if (n == 0) return me();
        if (n > sz) return _empty();
        return subList(n, sz);
    }

    @Override
    public <R> C.List<R> map(_.Function<? super Character, ? extends R> mapper) {
        int sz = size();
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
        if (isEmpty()) {
            return Nil.list();
        }
        return super.flatMap(mapper);
    }

    @Override
    public T filter(_.Function<? super Character, Boolean> predicate) {
        if (isEmpty()) return _empty();
        return remove(_.Predicate.negate(predicate));
    }

    @Override
    public T accept(_.Function<? super Character, ?> visitor) {
        super.accept(visitor);
        return me();
    }

    @Override
    public T acceptLeft(_.Function<? super Character, ?> visitor) {
        super.acceptLeft(visitor);
        return me();
    }

    @Override
    public T acceptRight(_.Function<? super Character, ?> visitor) {
        super.acceptRight(visitor);
        return me();
    }

    @Override
    public T tail() {
        int sz = size();
        if (0 == sz) {
            throw new UnsupportedOperationException();
        }
        return subList(1, sz);
    }

    @Override
    public T tail(int n) {
        int sz = size();
        if (n < 0) {
            return head(-n);
        } else if (n == 0) {
            return _empty();
        } else if (n >= sz) {
            return me();
        }
        return subList(sz - n, sz);
    }


    public String value() {
        return toString();
    }

    public String val() {
        return toString();
    }

    // --- String utilities ---

    public final T substr(int beginIndex, int endIndex) {
        return subList(beginIndex, endIndex);
    }

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
    public T maxLength(int max) {
        if (isEmpty()) return _empty();
        if (length() < (max - 3)) return me();
        return subList(0, max).append("...");
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
    public T cutOff(int max) {
        return maxLength(max);
    }


    /**
     * Wrapper of {@link String#contentEquals(StringBuffer)}
     *
     * @param stringBuffer
     * @return true if content equals the content of the specified buffer
     */
    public final boolean contentEquals(StringBuffer stringBuffer) {
        synchronized (stringBuffer) {
            return contentEquals((CharSequence) stringBuffer);
        }
    }


    /**
     * Alias of {@link #contentEquals(CharSequence)}
     *
     * @param x
     * @return
     */
    public final boolean eq(CharSequence x) {
        return contentEquals(x);
    }

    /**
     * Alias of {@link #contentEquals(T)}
     *
     * @param x
     * @return
     */
    public final boolean eq(T x) {
        return contentEquals(x);
    }

    /**
     * Alias of {@link #contentEquals(StringBuffer)}
     *
     * @param stringBuffer
     * @return
     */
    public final boolean eq(StringBuffer stringBuffer) {
        return contentEquals(stringBuffer);
    }

    /**
     * Alias of {@link #indexOf(String, int)}
     *
     * @param x
     * @param fromIndex
     * @return
     */
    public final int pos(String x, int fromIndex) {
        return indexOf(x, fromIndex);
    }


    public final int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    public final int indexOf(T x) {
        return indexOf(x, 0);
    }

    public final int indexOf(String str) {
        return indexOf(str, 0);
    }

    /**
     * Alias of {@link #indexOf(int)}
     * @param ch
     * @return
     */
    public final int pos(int ch) {
        return indexOf(ch, 0);
    }

    /**
     * @param x
     * @return
     */
    public final int pos(String x) {
        return indexOf(x, 0);
    }

    public final int pos(T x) {
        return indexOf(x, 0);
    }
    /**
     * Alias of {@link #indexOf(int, int)}
     * @param ch
     * @param fromIndex
     * @return
     */
    public final int pos(int ch, int fromIndex) {
        return indexOf(ch, fromIndex);
    }


    /**
     * Alias of {@link #indexOf(T, int)}
     *
     * @param x
     * @param fromIndex
     * @return
     */
    public final int pos(T x, int fromIndex) {
        return indexOf(x, fromIndex);
    }

    /**
     * Wrapper of {@link String#lastIndexOf(int)}
     * @param ch
     * @return
     */
    public final int lastIndexOf(int ch) {
        return lastIndexOf(ch, size());
    }

    public final int lastIndexOf(String str) {
        return lastIndexOf(str, size() - 1);
    }

    public final int lastIndexOf(T str) {
        return lastIndexOf(str, size() - 1);
    }

    /**
     *  Alias of {@link #lastIndexOf(int)}
     * @param ch
     * @return
     */
    public final int rpos(int ch) {
        return lastIndexOf(ch, size() - 1);
    }


    /**
     *
     * @param x
     * @return
     */
    public final int rpos(T x) {
        return lastIndexOf(x);
    }


    public final int rpos(String str, int fromIndex) {
        return lastIndexOf(str, fromIndex);
    }

    public final int rpos(T str, int fromIndex) {
        return lastIndexOf(str, fromIndex);
    }
    /**
     *
     * @param x
     * @return
     */
    public final int rpos(String x) {
        return lastIndexOf(x, size() - 1);
    }

    /**
     * Alias of {@link #lastIndexOf(int, int)}
     * @param ch
     * @param fromIndex
     * @return
     */
    public final int rpos(int ch, int fromIndex) {
        return lastIndexOf(ch, fromIndex);
    }

    public final boolean contains(char c) {
        return indexOf(c) > -1;
    }

    public final boolean regionMatches(int toffset, T other, int ooffset, int len) {
        return regionMatches(false, toffset, other, ooffset, len);
    }

    public final boolean regionMatches(int toffset, String other, int ooffset, int len) {
        return regionMatches(false, toffset, other, ooffset, len);
    }

    public final boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    public final boolean startsWith(T prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Synonym of {@link #substring(int)} but return Str instead of String
     * @param beginIndex
     * @return A str of
     */
    public final T substr(int beginIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        return (beginIndex == 0) ? me() : substr(beginIndex, size());
    }

    public final T concat(String str) {
        return append(str);
    }

    public final T concat(T str) {
        return append(str);
    }


    public final C.List<T> split(String regex) {
        return split(regex, 0);
    }


    public final T toLowerCase() {
        return toLowerCase(Locale.getDefault());
    }

    public final T lower() {
        return toLowerCase();
    }

    public final T lower(Locale locale) {
        return toLowerCase(locale);
    }

    public final T toUpperCase() {
        return toUpperCase(Locale.getDefault());
    }

    public final T upper() {
        return toLowerCase();
    }

    public final T upper(Locale locale) {
        return toUpperCase(locale);
    }

    public final T before(String s) {
        return beforeFirst(s);
    }

    public final T before(T s) {
        return beforeFirst(s);
    }

    public final T after(T s) {
        return afterLast(s);
    }

    public final T after(String s) {
        return afterLast(s);
    }

    public final int count(String s) {
        return count(s, false);
    }

    public final int count(T s) {
        return count(s, false);
    }

    public final int countWithOverlap(String s) {
        return count(s, true);
    }

    public final int countWithOverlap(T s) {
        return count(s, true);
    }

    /**
     * Wrapper of {@link String#toCharArray()}
     *
     * @return
     */
    public final char[] toCharArray() {
        return chars();
    }

    public static enum F {
        ;
        public _.Comparator<StrBase> NATURAL_ORDER = new _.Comparator<StrBase>() {
            @Override
            public int compare(StrBase o1, StrBase o2) {
                return o1.toFastStr().compareTo(o2.toFastStr());
            }
        };

        public _.Comparator<StrBase> REVERSE_ORDER = _.F.reverse(NATURAL_ORDER);
    }
}
