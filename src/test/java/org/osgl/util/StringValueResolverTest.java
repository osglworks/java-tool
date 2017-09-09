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

import org.junit.Test;
import org.osgl.OsglToolTestBase;

import static org.osgl.util.StringValueResolver.predefined;

public abstract class StringValueResolverTest extends OsglToolTestBase {

    public abstract static class PredefinedTest {
        private final StringValueResolver _p;
        private final StringValueResolver _r;
        private final Object empty;

        protected PredefinedTest(Class primitive, Class reference, Object empty) {
            _p = predefined(primitive);
            _r = predefined(reference);
            this.empty = empty;
        }

        @Test
        public void testEmptyStr() {
            eq(empty, _p.resolve(""));
            assertNull(_r.resolve(""));
        }

        protected void verify(Object expected, String s) {
            eq(expected, _p.resolve(s));
            eq(expected, _r.resolve(s));
        }

        protected void verifyException(Class<? extends Exception> expected, String s) {
            try {
                _p.resolve(s);
                assertFalse(S.fmt("Expection %s expected", expected), true);
            } catch (Exception e) {
                if (!expected.isAssignableFrom(e.getClass())) {
                    assertFalse(S.fmt("Expection %s expected, found: %s", expected, e.getClass()), true);
                }
            }
            try {
                _r.resolve(s);
                assertFalse(S.fmt("Expection %s expected", expected), true);
            } catch (Exception e) {
                if (!expected.isAssignableFrom(e.getClass())) {
                    assertFalse(S.fmt("Expection %s expected, found: %s", expected, e.getClass()), true);
                }
            }
        }
    }

    public static class CharResolverTest extends PredefinedTest {

        public CharResolverTest() {
            super(char.class, Character.class, '\0');
        }

        @Test
        public void testBlankStr() {
            verify(' ', " ");
        }

        @Test
        public void testValid() {
            verify('a', "a");
        }

        @Test
        public void testMultiByteChar() {
            verify('林', "林");
        }

        @Test(expected = IllegalArgumentException.class)
        public void testMultiCharStr() {
            verify('H', "Hello");
        }
    }

    public static class ByteResolverTest extends PredefinedTest {
        public ByteResolverTest() {
            super(byte.class, Byte.class, (byte)0);
        }

        @Test
        public void testValidStr() {
            verify((byte)127, "127");
        }

        @Test
        public void testNegative() {
            verify((byte)-127, "-127");
        }

        @Test
        public void testOutOfRange() {
            verifyException(NumberFormatException.class, "128");
        }

        @Test
        public void testInvalidStr() {
            verifyException(NumberFormatException.class, "af3");
        }
    }

    public static class ShortResolverTest extends PredefinedTest {
        public ShortResolverTest() {
            super(short.class, Short.class, (short)0);
        }

        @Test
        public void testValidStr() {
            verify((short)256, "256");
        }


        @Test
        public void testNegative() {
            verify((short)-32768, "-32768");
        }

        @Test
        public void testOutOfRange() {
            verifyException(NumberFormatException.class, "32768");
        }

        @Test
        public void testInvalidStr() {
            verifyException(NumberFormatException.class, "ad032");
        }

    }

    public static class IntResolverTest extends PredefinedTest {

        public IntResolverTest() {
            super(int.class, Integer.class, 0);
        }

        @Test
        public void testValidStr() {
            verify(5, "5");
        }

        @Test
        public void testOutOfRange() {
            verifyException(NumberFormatException.class, (((Long)(((long)Integer.MAX_VALUE) + 1l)).toString()));
        }

        @Test
        public void testNegative() {
            verify(-342134, "-342134");
        }

        @Test
        public void testWithRounding() {
            verify(6, "5.5");
            verify(5, "5.4");
        }

        @Test
        public void testInvalidStr() {
            verifyException(IllegalArgumentException.class, "$14");
        }

    }

    public static class LongResolverTest extends PredefinedTest {
        public LongResolverTest() {
            super(long.class, Long.class, 0l);
        }

        @Test
        public void testValidStr() {
            verify(9999999999999l, "9999999999999");
        }

        @Test
        public void testNegative() {
            verify(-1l, "-1");
        }

        @Test
        public void testInvalidStr() {
            verifyException(NumberFormatException.class, "3dsf");
        }
    }

    public static class FloatResolverTest extends PredefinedTest {
        public FloatResolverTest() {
            super(float.class, Float.class, 0f);
        }

        @Test
        public void testValidStr() {
            verify(342.34f, "342.34");
        }

        @Test
        public void testNegative() {
            verify(-32.120f, "-32.120");
        }

        @Test
        public void testInvalidStr() {
            verifyException(NumberFormatException.class, "ac3d");
        }
    }

    public static class DoubleResolverTest extends PredefinedTest {
        public DoubleResolverTest() {
            super(double.class, Double.class, 0d);
        }

        @Test
        public void testValidStr() {
            verify(342.3443242432d, "342.3443242432");
        }

        @Test
        public void testNegative() {
            verify(-32.120d, "-32.120");
        }

        @Test
        public void testInvalidStr() {
            verifyException(NumberFormatException.class, "ac3d");
        }
    }

    public static class BooleanResolverTest extends PredefinedTest {
        public BooleanResolverTest() {
            super(boolean.class, Boolean.class, false);
        }

        @Test
        public void testValidStr() {
            verify(false, "False");
            verify(true, "tRue");
            verify(false, "abc");
        }
    }

}
