package org.osgl.util;

import org.osgl._;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public enum Unsafe {
;

    private static final char[] EMPTY_CHAR_ARRAY = new char[]{};
    private static Field STRING_BUF;
    private static Constructor<String> SHARED_STR_CONSTRUCTOR;
    static {
        try {
            STRING_BUF = String.class.getDeclaredField("value");
            STRING_BUF.setAccessible(true);
            char[] ca = new char[0];
            SHARED_STR_CONSTRUCTOR = String.class.getDeclaredConstructor(ca.getClass(), Boolean.TYPE);
            SHARED_STR_CONSTRUCTOR.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw E.unexpected(e);
        } catch (NoSuchMethodException e) {
            throw E.unexpected(e);
        }
    }

    /**
     * Get the char array buf out of a string directly
     * @param s
     * @return
     */
    public static char[] bufOf(String s) {
        if (null == s) return EMPTY_CHAR_ARRAY;
        try {
            return (char[]) STRING_BUF.get(s);
        } catch (IllegalAccessException e) {
            throw E.unexpected(e);
        }
    }

    /**
     * Returns a string directly from the char array supplied without
     * copy operation
     * @param buf
     * @return
     */
    public static String sharedString(char[] buf) {
        try {
            return SHARED_STR_CONSTRUCTOR.newInstance(buf, true);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    /**
     * Convert characters in char array to lower case. Note this method
     * doesn't count the case for 'tr', 'az' and 'lt' language.
     * If all characters in the buf are lower cases then it will not
     * create new char array, instead return the char array passed in
     * @param buf
     * @return
     */
    public static char[] toLowerCase(char[] buf) {
        final int sz = buf.length;
        if (sz == 0) return buf;
        boolean needsConvert = false;
        char[] newBuf = null;
        for (int i = 0; i < sz;) {
            char c = buf[i++];
            boolean isLowerCase = Character.isLowerCase(c);
            if (!isLowerCase) {
                if (!needsConvert) {
                    needsConvert = true;
                    newBuf = new char[sz];
                    System.arraycopy(buf, 0, newBuf, 0, i);
                }
                newBuf[i] = Character.toLowerCase(c);
            }
        }
        return needsConvert ? newBuf : buf;
    }

    /**
     * Convert a string to lower case. Note this method
     * doesn't count the case for 'tr', 'az' and 'lt' language.
     * If all characters in the string are lower cases then it will not
     * create new string, instead return the original string passed in
     * @param s
     * @return
     */
    public static String toLowerCase(String s) {
        char[] buf = bufOf(s);
        char[] newBuf = toLowerCase(buf);
        if (buf == newBuf) return s; // not changed
        return sharedString(newBuf);
    }

    /**
     * Convert characters in char array to upper case. Note this method
     * doesn't count the case for 'tr', 'az' and 'lt' language.
     * If all characters in the buf are upper cases then it will not
     * create new char array, instead return the char array passed in
     * @param buf
     * @return
     */
    public static char[] toUpperCase(char[] buf) {
        final int sz = buf.length;
        if (sz == 0) return buf;
        boolean needsConvert = false;
        char[] newBuf = null;
        for (int i = 0; i < sz;) {
            char c = buf[i++];
            boolean isUpperCase = Character.isUpperCase(c);
            if (!isUpperCase) {
                if (!needsConvert) {
                    needsConvert = true;
                    newBuf = new char[sz];
                    System.arraycopy(buf, 0, newBuf, 0, i);
                }
                newBuf[i] = Character.toUpperCase(c);
            }
        }
        return needsConvert ? newBuf : buf;
    }

    /**
     * Convert a string to upper case. Note this method
     * doesn't count the case for 'tr', 'az' and 'lt' language.
     * If all characters in the string are upper cases then it will not
     * create new string, instead return the original string passed in
     * @param s
     * @return
     */
    public static String toUpperCase(String s) {
        char[] buf = bufOf(s);
        char[] newBuf = toUpperCase(buf);
        if (buf == newBuf) return s; // not changed
        return sharedString(newBuf);
    }

    public static void main(String[] args) throws Exception {
        String s = S.random(512);
        for (int i = 0; i < 1000; ++i) {
            char[] ca = s.toCharArray();
        }
        for (int i = 0; i < 1000; ++i) {
            char[] ca = bufOf(s);
        }

        long ts = _.ms();
        for (int i = 0; i < 1000 * 1000; ++i) {
            char[] ca = s.toCharArray();
        }
        long ts1 = _.ms() - ts;
        ts = _.ms();
        for (int i = 0; i < 1000 * 1000; ++i) {
            char[] ca = bufOf(s);
        }
        long ts2 = _.ms() - ts;
        System.out.printf("toCharArray: %s; bufOf: %s", ts1, ts2);
    }
}
