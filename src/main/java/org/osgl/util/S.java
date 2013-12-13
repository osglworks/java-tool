package org.osgl.util;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/09/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class S {

    /**
     * A handy alias for {@link String#format(String, Object...)}
     *
     * @param tmpl
     * @param args
     * @return the formatted string
     */
    public final static String fmt(String tmpl, Object... args) {
        if (args.length == 0) {
            return tmpl;
        } else {
            return String.format(tmpl, args);
        }
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

    public static String string(Object o) {
        if (null == o) {
            return "";
        }
        return o.toString();
    }

}
