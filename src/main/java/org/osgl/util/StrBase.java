package org.osgl.util;

import org.osgl.$;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.PatternSyntaxException;

/**
 * The abstract base class for {@link Str} and {@link FastStr}. This class aggregates
 * methods from both {@link CharSequence} and {@link org.osgl.util.C.List} to provide
 * easy manipulation and also functional programming facilities to char sequence
 * @param <T> the implementation class type
 */
public abstract class StrBase<T extends StrBase<T>> extends ListBase<Character>
implements RandomAccess, CharSequence, java.io.Serializable, Comparable<T> {

    protected StrBase(){
    }

    /**
     * Return the class of the implementation
     * @return the implementation class
     */
    protected abstract Class<T> _impl();

    /**
     * Check if the str is empty
     * @return {@code true} if the str is empty
     */
    protected abstract T _empty();

    /**
     * Returns a sub str that is a part of this str
     * @param fromIndex the from (inclusive)
     * @param toIndex the to (exclusive)
     * @return a sub str start at {@code fromIndex} and end at (@code toIndex)
     * @throws StringIndexOutOfBoundsException if {@code fromIndex} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     * @see #subSequence(int, int)
     * @see #substr(int, int)
     */
    @Override
    public abstract T subList(int fromIndex, int toIndex) throws StringIndexOutOfBoundsException;

    /**
     * Return a new str with character inserted into the char sequence of this str
     * @param index the position to insert the character
     * @param character the char to be inserted
     * @return an new str with char inserted into the position specified
     * @throws StringIndexOutOfBoundsException if {@code index} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     */
    public abstract T insert(int index, char character) throws StringIndexOutOfBoundsException;

    /**
     * Return a new str with character inserted into the char sequence of this str
     * @param index the position to insert the character
     * @param character the char to be inserted
     * @return an new str with char inserted into the position specified
     * @throws StringIndexOutOfBoundsException if {@code index} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     */
    @Override
    public abstract T insert(int index, Character character) throws StringIndexOutOfBoundsException;

    /**
     * Return a new str with character array inserted into the char sequence of this str
     * @param index the position to insert the character
     * @param ca the char array to be inserted
     * @return an new str with char inserted into the position specified
     * @throws StringIndexOutOfBoundsException if {@code index} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     */
    @Override
    public abstract T insert(int index, Character... ca) throws StringIndexOutOfBoundsException;

    /**
     * Return a new str with character array inserted into the char sequence of this str
     * @param index the position to insert the character
     * @param ta the char array to be inserted
     * @return an new str with char inserted into the position specified
     * @throws StringIndexOutOfBoundsException if {@code index} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     */
    public abstract T insert(int index, char... ta) throws StringIndexOutOfBoundsException;

    /**
     * Returns a new str with a string specified inserted into the char sequence of this str
     * @param index the position to insert the string
     * @param str the str to be inserted
     * @return an new str with string inserted into the position specified
     * @throws StringIndexOutOfBoundsException if {@code index} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     */
    public abstract T insert(int index, StrBase<?> str) throws StringIndexOutOfBoundsException;

    /**
     * Returns a new str with a string specified inserted into the char sequence of this str
     * @param index the position to insert the string
     * @param s the string to be inserted
     * @return an new str with string inserted into the position specified
     * @throws StringIndexOutOfBoundsException if {@code index} is lesser than
     *         {@code 0} or greater than or equals to {@link #size()} of the str
     */
    public abstract T insert(int index, String s) throws StringIndexOutOfBoundsException;

    /**
     * Returns a str that with all characters in this str instance except those matches the
     * test of predicate function specified
     * @param predicate the function determine which character in this str will be removed
     * @return a str with characters identified by {@code predicate} removed
     */
    @Override
    public abstract T remove($.Function<? super Character, Boolean> predicate);

    /**
     * Return a str that contains the first N characters in this string, the characters
     * in the new str must matches the test specified by the predicate function. The match
     * operation stop at the first character that does not match the predicate
     * @param predicate test if the character should be put into the new str or should stop matching
     * @return the new str with first N characters matches the test by predicate
     */
    @Override
    public abstract T takeWhile($.Function<? super Character, Boolean> predicate);

    /**
     * Return a str that without the first N characters in the string. The characters
     * been dropped must matches the test specified by teh predicate function. The match
     * operation stop at the first character that matches the predicate
     * @param predicate the function test if character should be dropped in the new str
     *                  or stop match operation
     * @return the new str as described
     */
    @Override
    public abstract T dropWhile($.Function<? super Character, Boolean> predicate);

    /**
     * Returns a str that contains all characters in this str instance but in a
     * reversed order.
     * @return a reversed str
     */
    @Override
    public abstract T reverse();

    /**
     * Return a str instance contains all characters of this str append with all
     * characters in the collection specified
     * @param collection the character collection to be appended
     * @return the new str instance as described
     */
    @Override
    public abstract T append(Collection<? extends Character> collection);

    /**
     * Return a str instance contains all characters of this str instance append with
     * all characters in the list specified
     * @param list the list of characters to be appended
     * @return the new str instance as described
     */
    @Override
    public abstract T append(C.List<Character> list);

    /**
     * Return a str instance contains all characters of this str instance append with
     * all characters in the array specified
     * @param array the array of characters to be appended
     * @return the new str instance as described
     */
    public abstract T append(char... array);

    /**
     * Returns a str instance contains all characters of this str instance appended with
     * the character specified
     * @param character the character to be appended
     * @return the new str instance as described
     */
    @Override
    public abstract T append(Character character);

    /**
     * Returns a str instance contains all characters of this str instance appended with
     * all characters in teh str specified
     * @param s the str in which characters to be appended
     * @return the new str instance as described
     */
    public abstract T append(T s);

    /**
     * Returns a str instance contains all characters of this str instance appended
     * with all characters in the {@link String} specified
     * @param s the {@link String} in which characters to be appended
     * @return the new str instance as described
     */
    public abstract T append(String s);

    /**
     * Returns a str instance contains all characters of this str instance prepended
     * with all characters in teh collection
     * @param collection the collection in which characters to be prepended
     * @return the new str instance as described
     */
    @Override
    public abstract T prepend(Collection<? extends Character> collection);

    /**
     * Return a str instance contains all characters of this str instance prepended with
     * all characters in the list specified
     * @param list the list of characters to be prepended
     * @return the new str instance as described
     */
    @Override
    public abstract T prepend(C.List<Character> list);

    /**
     * Return a str instance contains all characters of this str instance prepended with
     * all characters in the array specified
     * @param chars the array of characters to be prepended
     * @return the new str instance as described
     */
    public abstract T prepend(char ... chars);


    /**
     * Returns a str instance contains all characters of this str instance prepended with
     * the character specified
     * @param character the character to be preppended
     * @return the new str instance as described
     */
    @Override
    public abstract T prepend(Character character);

    /**
     * Returns a str instance contains all characters of this str instance prepended with
     * all characters in the str specified
     * @param s the str in which characters to be prepended
     * @return the new str instance as described
     */
    public abstract T prepend(T s);

    /**
     * Returns a str instance contains all characters of this str instance prepended
     * with all characters in the {@link String} specified
     * @param s the {@link String} in which characters to be prepended
     * @return the new str instance as described
     */
    public abstract T prepend(String s);

    /**
     * Return a part of this str specified by start inclusive and end exclusive position.
     * <p>Note calling this method has the same effect with calling {@link #subList(int, int)}
     * and calling {@link #substr(int, int)}</p>
     * @param start the start position
     * @param end the end position
     * @return the part of this str as described
     * @see #subList(int, int)
     * @see #substr(int, int)
     */
    @Override
    public abstract T subSequence(int start, int end);

    /**
     * Returns a str instance that repeat this str's char sequence for {@code n}
     * times
     * @param n repeat times
     * @return the new str as described
     */
    public abstract T times(int n);

    /**
     * Returns a str instance with `times` of char `c` left pad to the current str
     * @param c the char used to pad to this str
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T padLeft(char c, int times);

    /**
     * Alias of {@link #padLeft(char, int)}
     * @param c the char used to pad to this str
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T lpad(char c, int times);

    /**
     * Returns a str instance with `times` of char `' '` (space) left pad to the current str
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T padLeft(int times);

    /**
     * Alias of {@link #padLeft(int)}
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T lpad(int times);

    /**
     * Returns a str instance with `times` of char `c` right pad to the current str
     * @param c the char used to pad to this str
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T padRight(char c, int times);

    /**
     * Alias of {@link #padRight(char, int)}
     * @param c the char used to pad to this str
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T rpad(char c, int times);

    /**
     * Returns a str instance with `times` of char `' '` (space) right pad to the current str
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T padRight(int times);

    /**
     * Alias of {@link #padRight(int)}
     * @param times the repeat times
     * @return the str instance as described above
     */
    public abstract T rpad(int times);

    /**
     * Copy part of the char sequence into another array
     * @param srcBegin the start of copy inclusive
     * @param srcEnd the end of copy exclusive
     * @param dst the destination array
     * @param dstBegin the start of past in destination array
     */
    public abstract void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin);

    /**
     * Returns byte array of string composed of only ascii chars
     * @return ASCII char array of this str
     */
    public abstract byte[] getBytesAscII();

    /**
     * Returns byte array of string composed of UTF-8 chars
     * @return UTF-8 char array of this str
     */
    public abstract byte[] getBytesUTF8();

    /**
     * Encodes this str into a sequence of bytes using the
     * platform's default charset, storing the result into a new byte array.
     *
     * @return  The resultant byte array
     * @see String#getBytes()
     */
    public abstract byte[] getBytes();

    /**
     * Encodes this str into a sequence of bytes using the given
     * {@linkplain java.nio.charset.Charset charset}, storing the result into a
     * new byte array.
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset} to be used to encode
     *         the {@code String}. If {@code charset} is {@code null} then
     *         this function shall return the result of {@link #getBytes()}
     *
     * @return  The resultant byte array
     * @see String#getBytes(Charset)
     */
    public abstract byte[] getBytes(Charset charset);

    /**
     * Encodes this str into a sequence of bytes using the named
     * charset, storing the result into a new byte array.
     *
     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}. If {@code charsetName} is {@code null} then this
     *         function shall return the result of {@link #getBytes()}
     *
     * @return  The resultant byte array
     *
     * @see String#getBytes(String)
     */
    public abstract byte[] getBytes(String charsetName);

    /**
     * Return a {@link FastStr} that contains all the char sequence
     * in this str instance
     * @return a FastStr as described
     */
    public abstract FastStr toFastStr();

    /**
     * Check if this str content is equals to the char sequence specified
     * @param chars the char sequence to be checked agains this str
     * @return {@code true} if content of this str equals to that of the chars
     */
    public abstract boolean contentEquals(CharSequence chars);

    /**
     * Check if this str content iks equals to the str specified
     * @param str the str instance
     * @return {@code true} if content of this str equals to that of the str
     */
    public abstract boolean contentEquals(T str);

    /**
     * Check if this str is blank (empty or all space characters)
     * @return {@code true} if this str is blank
     */
    public abstract boolean isBlank();

    /**
     * Returns the index within this str of the first occurrence of
     * the specified character.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     * @see     String#indexOf(int)
     */
    public final int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    /**
     * Returns the index within this str of the first occurrence of
     * the specified object. The function will check if the object is
     * an instance of StrBase, String, Character or Integer, and delegate
     * to the {@code indexOf(...)} override methods respectively.
     * <p>If the object is not of the above types, then {@code -1} returned</p>
     * @param o the object
     * @return the index of the first occurrence of the object in this str
     */
    @Override
    public int indexOf(Object o) {
        if (getClass().isAssignableFrom(o.getClass())) {
            T str = $.cast(o);
            return indexOf(str);
        } else if (o instanceof CharSequence) {
            CharSequence str = (CharSequence) o;
            return indexOf(str);
        } else if (o instanceof Character) {
            Character c = (Character) o;
            return indexOf((int) c);
        } else if (o instanceof Integer) {
            Integer n = (Integer) o;
            return indexOf(n);
        }
        return -1;
    }

    /**
     * Returns the index within this str of the first occurrence of
     * the specified character.
     *
     * @param ch           a character (Unicode code point).
     * @param fromIndex    the from index
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     * @see     String#indexOf(int, int)
     */
    public abstract int indexOf(int ch, int fromIndex);

    /**
     * Returns the index within this str of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index from which to start the search.
     * @return  the index of the first occurrence of the specified substring,
     *          starting at the specified index,
     *          or {@code -1} if there is no such occurrence.
     * @see     String#indexOf(String, int)
     */
    public abstract int indexOf(CharSequence str, int fromIndex);

    /**
     * Locate another str inside this str, start at the specified index
     * @param s the search string
     * @param fromIndex from where the search should begin
     * @return the location found or {@code -1} if not found
     */
    public abstract int indexOf(T s, int fromIndex);

    /**
     * Returns the index within this str of the first occurrence of the
     * specified character list
     * @param list the list of chars to search for
     * @return the index of the first occurrence of the specified char list
     */
    public int indexOf(java.util.List<Character> list) {
        return indexOf((CharSequence) FastStr.of(list));
    }

    /**
     * Returns the index within this str of the first occurrence of the
     * specified character list, starting at the specified index
     * @param list the list of chars to search for
     * @param startIndex the index from which to start the search
     * @return the index of the first occurrence of the specified char list
     */
    public int indexOf(java.util.List<Character> list, int startIndex) {
        return indexOf((CharSequence) FastStr.of(list), startIndex);
    }

    /**
     * Returns the index within this str of the last occurrence of
     * the specified character.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     * @see String#lastIndexOf(int)
     */
    public final int lastIndexOf(int ch) {
        return lastIndexOf(ch, size());
    }

    /**
     * Returns the index within this str of the last occurrence of the
     * specified substring.  The last occurrence of the empty string ""
     * is considered to occur at the index value {@code this.length()}.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str   the substring to search for.
     * @return  the index of the last occurrence of the specified substring,
     *          or {@code -1} if there is no such occurrence.
     * @see String#lastIndexOf(String)
     */
    public final int lastIndexOf(CharSequence str) {
        return lastIndexOf(str, size() - 1);
    }


    /**
     * Returns the index within this str of the last occurrence of the
     * specified substr.  The last occurrence of the empty str
     * is considered to occur at the index value {@code this.length()}.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str   the substring to search for.
     * @return  the index of the last occurrence of the specified substring,
     *          or {@code -1} if there is no such occurrence.
     * @see #lastIndexOf(CharSequence)
     */
    public final int lastIndexOf(T str) {
        return lastIndexOf(str, size() - 1);
    }


    /**
     * Returns the index within this str of the last occurrence of the
     * specified character list
     * @param list the list of chars to search for
     * @return the index of the last occurrence of the specified char list
     */
    public int lastIndexOf(java.util.List<Character> list) {
        return lastIndexOf((CharSequence) FastStr.of(list));
    }

    /**
     * Returns the index within this str of the last occurrence of the
     * specified character list, starting at the specified index from tail
     * to head
     * @param list the list of chars to search for
     * @param startIndex the index from which to start the search backwards
     * @return the index of the last occurrence of the specified char list
     */
    public int lastIndexOf(java.util.List<Character> list, int startIndex) {
        return lastIndexOf((CharSequence) FastStr.of(list), startIndex);
    }

    /**
     * Returns the index within this str of the last occurrence of
     * the specified object. The function will check if the object is
     * an instance of StrBase, String, Character or Integer, and delegate
     * to the {@code lastIndexOf(...)} override methods respectively.
     * <p>If the object is not of the above types, then {@code -1} returned</p>
     * @param o the object
     * @return the index of the last occurrence of the object in this str
     */
    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof String) {
            String str = (String)o;
            return lastIndexOf(str);
        } else if (StrBase.class.isAssignableFrom(o.getClass())) {
            T str = $.cast(o);
            return lastIndexOf(str);
        } else if (o instanceof Character) {
            Character c = (Character) o;
            return lastIndexOf((int) c);
        } else if (o instanceof Integer) {
            Integer n = (Integer) o;
            return lastIndexOf(n);
        }
        return -1;
    }

    /**
     * Returns the index within this str of the last occurrence of
     * the specified character, searching backward starting at the
     * specified index.
     *
     * @param   ch          a character (Unicode code point).
     * @param   fromIndex   the index to start the search from. There is no
     *          restriction on the value of <code>fromIndex</code>. If it is
     *          greater than or equal to the length of this str, it has
     *          the same effect as if it were equal to one less than the
     *          length of this str: this entire str may be searched.
     *          If it is negative, it has the same effect as if it were -1:
     *          -1 is returned.
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object that is less
     *          than or equal to <code>fromIndex</code>, or <code>-1</code>
     *          if the character does not occur before that point.
     * @see     String#lastIndexOf(int, int)
     */
    public abstract int lastIndexOf(int ch, int fromIndex);

    /**
     * Returns the index within this str of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> &lt;= fromIndex &amp;&amp; this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str         the string to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the last occurrence of the specified substring,
     *          searching backward from the specified index,
     *          or {@code -1} if there is no such occurrence.
     * @see String#lastIndexOf(String)
     */
    public abstract int lastIndexOf(CharSequence str, int fromIndex);

    /**
     * Returns the index within this str of the last occurrence of the
     * specified str, searching backward starting at the specified index.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> &lt;= fromIndex &amp;&amp; this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str         the str to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the last occurrence of the specified str,
     *          searching backward from the specified index,
     *          or {@code -1} if there is no such occurrence.
     * @see String#lastIndexOf(String)
     * @see #lastIndexOf(CharSequence, int)
     */
    public abstract int lastIndexOf(T str, int fromIndex);

    /**
     * Return a sub string of type {@link java.lang.String} of this
     * str starts from {@code beginIndex} specified till the end
     * of the char array of this str
     * @param beginIndex the start index of the sub string
     * @return the sub string as described
     */
    public abstract String substring(int beginIndex);

    /**
     * Compare content of the str and the specified char sequence, case insensitive
     *
     * @param x the char sequence to be checked
     * @return {@code true} if the argument is not {@code null} and it
     * represents an equivalent {@code String} ignoring case; {@code
     * false} otherwise
     */
    public abstract boolean equalsIgnoreCase(CharSequence x);

    /**
     * Compare the char sequence specified against this str
     * @param x the char sequence to be compared to this str
     * @return the result of the comparison
     * @see String#compareTo(String)
     */
    public abstract int compareTo(CharSequence x);

    /**
     * Compare the str specified against this str in a case insensitive
     * manner
     * @param x the str to be compared to this str
     * @return the result of the comparison
     * @see String#compareToIgnoreCase(String)
     */
    public abstract int compareToIgnoreCase(T x);

    /**
     * Compare the char sequence specified against this str in a case insensitive
     * manner
     * @param x the char sequence to be compared to this str
     * @return the result of the comparison
     * @see String#compareToIgnoreCase(String)
     */
    public abstract int compareToIgnoreCase(CharSequence x);

    /**
     * Tests if two string regions are equal.
     *
     * @param   ignoreCase   if <code>true</code>, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       str.
     * @param   other        the str argument.
     * @param   ooffset      the starting offset of the subregion in the str
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  <code>true</code> if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          <code>false</code> otherwise. Whether the matching is exact
     *          or case insensitive depends on the <code>ignoreCase</code>
     *          argument.
     * @see String#regionMatches(boolean, int, String, int, int)
     */
    public abstract boolean regionMatches(boolean ignoreCase, int toffset, T other, int ooffset, int len);

    /**
     * Tests if two string regions are equal.
     *
     * @param   ignoreCase   if <code>true</code>, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       str.
     * @param   other        the char sequence argument.
     * @param   ooffset      the starting offset of the subregion in the str
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  <code>true</code> if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          <code>false</code> otherwise. Whether the matching is exact
     *          or case insensitive depends on the <code>ignoreCase</code>
     *          argument.
     * @see String#regionMatches(boolean, int, String, int, int)
     */
    public abstract boolean regionMatches(boolean ignoreCase, int toffset, CharSequence other, int ooffset, int len);

    /**
     * Check if this str starts with the given char sequence
     * @param prefix the char sequence
     * @return {@code true} if this str starts with the char sequence specified
     *         or {@code false} otherwise
     */
    public final boolean startsWith(CharSequence prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Check if this str starts with the given str
     * @param prefix the str argument
     * @return {@code true} if this str starts with the str specified
     *         or {@code false} otherwise
     */
    public final boolean startsWith(T prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Check this str instance starts with str, with the search starts at
     * specified offset
     * @param prefix the str arguments
     * @param toffset the offset where the search begins
     * @return {@code true} if the search matches or {@code false} otherwise
     */
    public abstract boolean startsWith(T prefix, int toffset);

    /**
     * Check if this str starts with a char sequence, with the search starts at
     * the specified offset
     * @param prefix the char sequence argument
     * @param toffset the start offset where search begins
     * @return {@code true} if search matches or {@code false} otherwise
     */
    public abstract boolean startsWith(CharSequence prefix, int toffset);


    /**
     * Check if this str ends with the given char sequence
     * @param prefix the char sequence
     * @return {@code true} if this str ends with the char sequence specified
     *         or {@code false} otherwise
     */
    public final boolean endsWith(CharSequence prefix) {
        return endsWith(prefix, 0);
    }

    /**
     * Check if this str ends with the given str
     * @param prefix the str argument
     * @return {@code true} if this str ends with the str specified
     *         or {@code false} otherwise
     */
    public final boolean endsWith(T prefix) {
        return endsWith(prefix, 0);
    }

    /**
     * Check this str instance ends with str, with the search starts at
     * specified offset
     * @param prefix the str arguments
     * @param toffset the offset where the search begins (backwards)
     * @return {@code true} if the search matches or {@code false} otherwise
     */
    public abstract boolean endsWith(T prefix, int toffset);

    /**
     * Check if this str ends with a char sequence, with the search starts at
     * the specified offset
     * @param prefix the char sequence argument
     * @param toffset the start offset where search begins backwards
     * @return {@code true} if search matches or {@code false} otherwise
     */
    public abstract boolean endsWith(CharSequence prefix, int toffset);

    /**
     * Returns a new string that is a substring of this str. The
     * substring begins at the specified <code>beginIndex</code> and
     * extends to the character at index <code>endIndex - 1</code>.
     * Thus the length of the substring is <code>endIndex-beginIndex</code>.
     * <p>
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @param      endIndex     the ending index, exclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if the
     *             <code>beginIndex</code> is negative, or
     *             <code>endIndex</code> is larger than the length of
     *             this <code>String</code> object, or
     *             <code>beginIndex</code> is larger than
     *             <code>endIndex</code>.
     * @see String#substring(int, int)
     */
    public abstract String substring(int beginIndex, int endIndex);

    /**
     * Returns a new str resulting from replacing all occurrences of
     * <code>oldChar</code> in this string with <code>newChar</code>.
     * <p>
     * If the character <code>oldChar</code> does not occur in the
     * character sequence represented by this <code>str</code> object,
     * then a reference to this <code>str</code> object is returned.
     * Otherwise, a new <code>str</code> object is created that
     * represents a character sequence identical to the character sequence
     * represented by this <code>str</code> object, except that every
     * occurrence of <code>oldChar</code> is replaced by an occurrence
     * of <code>newChar</code>.
     *
     * @param   oldChar   the old character.
     * @param   newChar   the new character.
     * @return  a str derived from this str by replacing every
     *          occurrence of <code>oldChar</code> with <code>newChar</code>.
     */
    public abstract T replace(char oldChar, char newChar);

    /**
     * Replaces each substring of this str that matches the literal target
     * sequence with the specified literal replacement sequence. The
     * replacement proceeds from the beginning of the str to the end, for
     * example, replacing "aa" with "b" in the str "aaa" will result in
     * "ba" rather than "ab".
     *
     * @param  target The sequence of char values to be replaced
     * @param  replacement The replacement sequence of char values
     * @return  The resulting string
     * @throws NullPointerException if <code>target</code> or
     *         <code>replacement</code> is <code>null</code>.
     */
    public abstract T replace(CharSequence target, CharSequence replacement);

    /**
     * Replaces each substr of this str that matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with the
     * given replacement.
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     * @param   replacement
     *          the string to be substituted for each match
     *
     * @return  The resulting <tt>str</tt>
     *
     * @throws PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     * @see java.lang.String#replaceAll(String, String)
     */
    public abstract T replaceAll(String regex, String replacement);

    /**
     * Replaces the first substring of this str that matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with the
     * given replacement.
     *
     * @param   regex
     *          the regular expression to which this str is to be matched
     * @param   replacement
     *          the string to be substituted for the first match
     *
     * @return  The resulting <tt>str</tt>
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     * @see java.lang.String#replaceFirst(String, String)
     */
    public abstract T replaceFirst(String regex, String replacement);

    /**
     * Tells whether or not this str matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     *
     * @return  <tt>true</tt> if, and only if, this string matches the
     *          given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     * @see java.lang.String#matches(String)
     */
    public abstract boolean matches(String regex);

    public abstract boolean contains(CharSequence s);
    public abstract C.List<T> split(String regex, int limit);
    public abstract T toLowerCase(Locale locale);
    public abstract T toUpperCase(Locale locale);
    public abstract T trim();
    public abstract char[] charArray();
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

    /**
     * Returns a copy of this str. The char buf is copied
     */
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
    public <R> C.List<R> map($.Function<? super Character, ? extends R> mapper) {
        int sz = size();
        if (0 == sz) {
            return Nil.list();
        }
        ListBuilder<R> lb = new ListBuilder<R>(sz);
        forEach($.visitor($.f1(mapper).andThen(C.F.addTo(lb))));
        return lb.toList();
    }

    @Override
    public <R> C.List<R> flatMap($.Function<? super Character, ? extends Iterable<? extends R>> mapper
    ) {
        if (isEmpty()) {
            return Nil.list();
        }
        return super.flatMap(mapper);
    }

    @Override
    public T filter($.Function<? super Character, Boolean> predicate) {
        if (isEmpty()) return _empty();
        return remove($.Predicate.negate(predicate));
    }

    @Override
    public T accept($.Visitor<? super Character> visitor) {
        super.accept(visitor);
        return me();
    }

    @Override
    public T acceptLeft($.Visitor<? super Character> visitor) {
        super.acceptLeft(visitor);
        return me();
    }

    @Override
    public T acceptRight($.Visitor<? super Character> visitor) {
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
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param max the maximum length of the result
     * @return A StrBase instance that contains at most `max` number of chars of this instance
     */
    public T maxLength(int max) {
        if (isEmpty()) return _empty();
        if (length() < (max - 3)) return me();
        return subList(0, max).append("...");
    }

    /**
     * <p>Return a string no longer than specified max length.
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     *
     * @param max the maximum length of the result
     * @return the string described above
     */
    public T cutOff(int max) {
        return maxLength(max);
    }


    /**
     * Wrapper of {@link String#contentEquals(StringBuffer)}
     *
     * @param stringBuffer string buffer
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
     * @param x an instance of {@link CharSequence} type
     * @return {@code true} if chars inside this instance equals to the chars inside x
     */
    public final boolean eq(CharSequence x) {
        return contentEquals(x);
    }

    /**
     * Alias of {@link #contentEquals(StrBase)}
     *
     * @param x another instance
     * @return {@code true} if this instance equals to the x
     */
    public final boolean eq(T x) {
        return contentEquals(x);
    }

    /**
     * Alias of {@link #contentEquals(StringBuffer)}
     *
     * @param stringBuffer the string buffer
     * @return `true` if content of this instance equals to content of the string buffer
     */
    public final boolean eq(StringBuffer stringBuffer) {
        return contentEquals(stringBuffer);
    }

    /**
     * Alias of {@link #indexOf(CharSequence, int)}
     *
     * @param x the substr to search for
     * @param fromIndex the index from where the search starts, backwards
     * @return the index of the occurrence
     */
    @SuppressWarnings("unused")
    public final int pos(CharSequence x, int fromIndex) {
        return indexOf(x, fromIndex);
    }

    /**
     * Alias of {@link #indexOf(CharSequence)}
     *
     * @param x the substr to search for
     * @return the index of the occurrence
     */
    @SuppressWarnings("unused")
    public final int pos(CharSequence x) {
        return indexOf(x);
    }

    /**
     * Locate another str inside this str, start at the specified index
     * @param x the search str
     * @return the location found or {@code -1} if not found
     */
    public final int indexOf(T x) {
        return indexOf(x, 0);
    }

    /**
     * Returns the index within this str of the first occurrence of the
     * specified substring, starting at 0.
     *
     * @param   str         the substring to search for.
     * @return  the index of the first occurrence of the specified substring,
     *          starting at the specified index,
     *          or {@code -1} if there is no such occurrence.
     * @see     String#indexOf(String, int)
     */
    public final int indexOf(CharSequence str) {
        return indexOf(str, 0);
    }

    /**
     * Alias of {@link #indexOf(int)}
     * @param ch the character to be located
     * @return the position of the character in this str
     */
    @SuppressWarnings("unused")
    public final int pos(int ch) {
        return indexOf(ch, 0);
    }

    /**
     * Alias of {@link #indexOf(StrBase)}
     * @param x the str to be located
     * @return the position of the str in this str
     */
    @SuppressWarnings("unused")
    public final int pos(T x) {
        return indexOf(x, 0);
    }

    /**
     * Alias of {@link #indexOf(int, int)}
     * @param ch the char to be located
     * @param fromIndex the index the search begins
     * @return the position of the char in this str
     */
    @SuppressWarnings("unused")
    public final int pos(int ch, int fromIndex) {
        return indexOf(ch, fromIndex);
    }


    /**
     * Alias of {@link #indexOf(StrBase, int)}
     *
     * @param x a StrBase to search
     * @param fromIndex the index from where the search should begins
     * @return the found position or {@code -1} if not found
     */
    @SuppressWarnings("unused")
    public final int pos(T x, int fromIndex) {
        return indexOf(x, fromIndex);
    }

    /**
     *  Alias of {@link #lastIndexOf(int)}
     * @param ch the char to be located
     * @return the last occurrence position the char found in this str
     */
    @SuppressWarnings("unused")
    public final int rpos(int ch) {
        return lastIndexOf(ch, size() - 1);
    }


    /**
     * alias of {@link #lastIndexOf(StrBase)}
     * @param x the str to be located
     * @return the last occurrence position the str found in this str
     */
    @SuppressWarnings("unused")
    public final int rpos(T x) {
        return lastIndexOf(x);
    }

    /**
     * alias of {@link #lastIndexOf(CharSequence, int)}
     * @param str the string to be located
     * @param fromIndex the index from where the search begins backwards
     * @return the last occurrence of the string in this str
     */
    @SuppressWarnings("unused")
    public final int rpos(CharSequence str, int fromIndex) {
        return lastIndexOf(str, fromIndex);
    }

    /**
     * alias of {@link #lastIndexOf(StrBase, int)}
     * @param str the str to be located
     * @param fromIndex the index from where the search begins backwards
     * @return the last occurrence of the str in this str
     */
    @SuppressWarnings("unused")
    public final int rpos(T str, int fromIndex) {
        return lastIndexOf(str, fromIndex);
    }

    /**
     * alias of {@link #lastIndexOf(CharSequence)}
     * @param str the string to be located
     * @return the last occurrence of the string in this str
     */
    @SuppressWarnings("unused")
    public final int rpos(CharSequence str) {
        return lastIndexOf(str, size() - 1);
    }

    /**
     * Alias of {@link #lastIndexOf(int, int)}
     * @param ch the char to be located
     * @param fromIndex the index from where the search begins backwards
     * @return the last occurrence of the char in this str
     */
    @SuppressWarnings("unused")
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

    /**
     * Synonym of {@link #substring(int)} but return Str instead of String
     * @param beginIndex the begin index
     * @return A sub str that starts from the index specified
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
     * @return char array backed this instance
     */
    public final char[] toCharArray() {
        return charArray();
    }

    public static enum F {
        ;
        public $.Comparator<StrBase> NATURAL_ORDER = new $.Comparator<StrBase>() {
            @Override
            public int compare(StrBase o1, StrBase o2) {
                return o1.toFastStr().compareTo(o2.toFastStr());
            }
        };

        public $.Comparator<StrBase> REVERSE_ORDER = $.F.reverse(NATURAL_ORDER);
    }
}
