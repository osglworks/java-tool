package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgl.$;

import java.util.*;

/**
 * `StringTokenSet` implement {@link Set} based on a list of String tokens separated by
 *  {@link #SEPARATOR}.
 *
 *  Examples:
 *
 *  * `"foo,bar"` contains `"foo"` and `"bar"`
 *  * `",,x,,y"` contains three empty string and `"x"`, `"y"`
 *
 *  This data structure is better to be used in case the string token set are unlikely to
 *  change and the number of string tokens are not very big (no bigger than 20)
 *
 *  This data structure is not thread safe
 */
public class StringTokenSet implements Set<String> {

    private String data;
    private boolean sorted;
    private int size;
    private String[] array;

    /**
     * The default separator: `,`
     */
    public static final String SEPARATOR = ",";
    public static final char SEPARATOR_CHAR = ',';


    public StringTokenSet() {
    }

    /**
     * Construct a `StringTokenSet` with given string
     * @param data the string contains tokens separated by {@link #SEPARATOR}
     */
    public StringTokenSet(String data) {
        StringTokenSet set = StringTokenSet.of(data);
        $.copy(set).to(this);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return 0 == size;
    }

    @Override
    public boolean contains(Object o) {
        if (null == data || !String.class.isInstance(o)) {
            return false;
        }
        int loc = locate((String) o);
        return loc > -1;
    }

    @Override
    public Iterator<String> iterator() {
        if (null == data) {
            return Iterators.nil();
        }
        final List<String> list = S.fastSplit(data, SEPARATOR);
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        ensureArray();
        return $.cloneOf(array);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return $.cast(toArray());
        }
        ensureArray();
        System.arraycopy(array, 0, a, 0, size);
        return a;
    }

    @Override
    public boolean add(String s) {
        if (contains(s)) {
            return false;
        }
        if (null == data) {
            data = s;
        } else {
            data += SEPARATOR + s;
        }
        size++;
        array = null;
        sorted = false;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (null == data || !String.class.isInstance(o)) {
            return false;
        }
        String s = (String) o;
        int loc = locate(s);
        if (loc < 0) {
            return false;
        }
        if (1 == size) {
            size = 0;
            data = null;
        } else {
            int sz = s.length();
            if (loc + sz >= data.length()) {
                data = data.substring(0, loc - 1);
            } else if (loc == 0) {
                data = data.substring(sz + 1);
            } else {
                String prefix = data.substring(0, loc - 1);
                String suffix = data.substring(loc + sz);
                data = prefix + suffix;
            }
            size--;
        }
        array = null;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        boolean changed = false;
        Set<String> set = C.Set(c);
        for (String s : set) {
            boolean b = add(s);
            changed = changed || b;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<String> toBeRemoved = new HashSet<>();
        for (String s: this) {
            if (!c.contains(s)) {
                toBeRemoved.add(s);
            }
        }
        for (String s : toBeRemoved) {
            remove(s);
        }
        return !toBeRemoved.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            boolean b = remove(o);
            changed = changed || b;
        }
        return changed;
    }

    @Override
    public void clear() {
        data = null;
        array = null;
        size = 0;
    }

    @Override
    public int hashCode() {
        sort();
        return $.hc(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof StringTokenSet) {
            StringTokenSet that = $.cast(obj);
            if ($.eq(that.data, this.data)) {
                return true;
            }
            sort();
            that.sort();
            return $.eq(data, that.data);
        }
        if (obj instanceof Set) {
            Set set = (Set) obj;
            return set.equals(this);
        }
        return false;
    }

    @Override
    public String toString() {
        return data;
    }

    public StringTokenSet sort() {
        if (!sorted) {
            ensureArray();
            Arrays.sort(array);
            data = S.join(C.listOf(array)).by(SEPARATOR).get();
            sorted = true;
        }
        return this;
    }

    private int locate(String s) {
        int sz = s.length();
        int loc = data.indexOf(s);
        while (loc != -1) {
            boolean beginInTheMiddle = loc > 0 && data.charAt(loc - 1) != SEPARATOR_CHAR;
            if (beginInTheMiddle) {
                loc = data.indexOf(s, loc + 1);
                continue;
            }
            int end = loc + sz;
            boolean endInTheMiddle = size > end && data.charAt(end + 1) != SEPARATOR_CHAR;
            if (endInTheMiddle) {
                loc = data.indexOf(s, loc + 1);
                continue;
            }
            return loc;
        }
        return -1;
    }

    private void ensureArray() {
        if (null != array) {
            return;
        }
        if (0 == size) {
            array = S.EMPTY_ARRAY;
            return;
        }
        array = new String[size];
        int cur = 0;
        int i = 0;
        int loc = data.indexOf((int) SEPARATOR_CHAR, cur);
        while (loc != -1) {
            array[i++] = data.substring(cur, loc);
            cur = loc + 1;
            loc = data.indexOf((int) SEPARATOR_CHAR, cur);
        }
        array[i] = data.substring(cur, data.length());
    }

    public static StringTokenSet of(String data) {
        StringTokenSet set = new StringTokenSet();
        if (null != data) {
            set.addAll(S.fastSplit(data, SEPARATOR));
        }
        return set;
    }

    public static String merge(String a, String b) {
        if (null == a) {
            if (null == b) {
                return null;
            }
            return of(b).toString();
        } else if (null == b) {
            return of(a).toString();
        }
        String s = S.concat(a, SEPARATOR, b);
        return of(s).toString();
    }

    public String merge(String a, String ... others) {
        return of(S.concat(a, S.join(others).by(SEPARATOR))).toString();
    }

    public String merge(String[] sa) {
        return of(S.join(sa).by(SEPARATOR).get()).toString();
    }

    public String merge(Collection<String> sa) {
        return of(S.join(sa).by(SEPARATOR).get()).toString();
    }

}
