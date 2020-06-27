package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@Deprecated
public enum Unsafe {
    ;

    private static final char[] EMPTY_CHAR_ARRAY = new char[]{};
    private static Field FASTSTR_BUF;
    private static Constructor<String> SHARED_STR_CONSTRUCTOR = null;

    static {
        try {
            FASTSTR_BUF = FastStr.class.getDeclaredField("buf");
            FASTSTR_BUF.setAccessible(true);
            char[] ca = new char[0];
            if ($.JAVA_VERSION == 8) {
                SHARED_STR_CONSTRUCTOR = String.class.getDeclaredConstructor(ca.getClass(), Boolean.TYPE);
                SHARED_STR_CONSTRUCTOR.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            throw E.unexpected(e);
        } catch (NoSuchMethodException e) {
            throw E.unexpected(e);
        }
    }

    /**
     * Get the char array buf out of a string directly
     *
     * @param s A string
     * @return the char array that backed the string
     */
    public static char[] bufOf(String s) {
        if (null == s) return EMPTY_CHAR_ARRAY;
        return s.toCharArray();
    }

    /**
     * Returns char array that backed the char sequence specified
     * @param chars the char sequence
     * @return the char array of the char sequence
     */
    public static char[] bufOf(CharSequence chars) {
        if (null == chars) return EMPTY_CHAR_ARRAY;
        return FastStr.of(chars).unsafeChars();
    }

    /**
     * Returns a string directly from the char array supplied without
     * copy operation
     *
     * @param buf the char array used to construct the return string
     * @return the string that backed by the char array specified
     */
    public static String stringOf(char[] buf) {
        if (buf.length < 256 || null == SHARED_STR_CONSTRUCTOR) {
            return new String(buf);
        }
        try {
            return SHARED_STR_CONSTRUCTOR.newInstance(buf, true);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    /**
     * Get the char array buf from a FastStr instance
     * @param s the FastStr instance
     * @return the char array buf of the FastStr instance
     */
    @SuppressWarnings("unused")
    public static char[] bufOf(FastStr s) {
        if (null == s) return EMPTY_CHAR_ARRAY;
        try {
            return (char[]) FASTSTR_BUF.get(s);
        } catch (IllegalAccessException e) {
            throw E.unexpected(e);
        }
    }


}
