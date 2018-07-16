package org.osgl;

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

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;

@RunWith(Enclosed.class)
public class ArrayUtilsTest {

    public static class ResetTest extends TestBase {
        @Test
        public void resetBoolean() {
            boolean[] ba = new boolean[103];
            Arrays.fill(ba, true);
            $.resetArray(ba);
            for (int i = 0; i < 103; ++i) {
                no(ba[i]);
            }

            ba = new boolean[]{true, true, true};
            $.resetArray(ba);
            no(ba[0]);
            no(ba[1]);
            no(ba[2]);
        }

        @Test
        public void resetByte() {
            byte[] array = {2, 3};
            $.resetArray(array);
            eq((byte)0, array[0]);
            eq((byte)0, array[1]);
        }

        @Test
        public void resetShort() {
            short[] array = {2, 3};
            $.resetArray(array);
            eq((short)0, array[0]);
            eq((short)0, array[1]);
        }

        @Test
        public void resetChar() {
            char[] array = {2, 3};
            $.resetArray(array);
            eq((char)0, array[0]);
            eq((char)0, array[1]);
        }

        @Test
        public void resetInt() {
            int[] array = {2, 3};
            $.resetArray(array);
            eq(0, array[0]);
            eq(0, array[1]);

            array = new int[300];
            Arrays.fill(array, 3);
            array = $.resetArray(array);
            for (int i = 0; i < 300; ++i) {
                eq(0, array[i]);
            }

            array = new int[303];
            Arrays.fill(array, 5);
            array = $.resetArray(array);
            for (int i = 0; i < 300; ++i) {
                eq(0, array[i]);
            }
        }

        @Test
        public void resetLong() {
            long[] array = {2, 3};
            $.resetArray(array);
            eq((long)0, array[0]);
            eq((long)0, array[1]);
        }

        @Test
        public void resetFloat() {
            float[] array = {2, 3};
            $.resetArray(array);
            eq((float)0, array[0]);
            eq((float)0, array[1]);
        }

        @Test
        public void resetDouble() {
            double[] array = {2, 3};
            $.resetArray(array);
            eq((double)0, array[0]);
            eq((double)0, array[1]);
        }


        @Test
        public void resetDate() {
            Date[] array = {new Date(), new Date()};
            array = $.resetArray(array);
            eq(null, array[0]);
            eq(null, array[1]);
        }

        @Test
        public void resetIntArrayAsObject() {
            int[] array = {2, 3};
            $.resetArray((Object) array);
            eq(0, array[0]);
            eq(0, array[1]);

            array = new int[300];
            Arrays.fill(array, 3);
            $.resetArray((Object) array);
            for (int i = 0; i < 300; ++i) {
                eq(0, array[i]);
            }

            array = new int[303];
            Arrays.fill(array, 5);
            $.resetArray((Object) array);
            for (int i = 0; i < 300; ++i) {
                eq(0, array[i]);
            }
        }

        @Test
        public void resetDateArrayAsObject() {
            Date[] array = {new Date(), new Date()};
            $.resetArray((Object) array);
            eq(null, array[0]);
            eq(null, array[1]);
        }

    }

    public static class ConcatTest extends TestBase {
        @Test
        public void concat2arrays() {
            String[] empty = {};
            String[] a1 = {"A", "B"};
            String[] a2 = {"C", "D"};
            eq(new String[]{"A", "B", "C", "D"}, $.concat(a1, a2));
            same(a1, $.concat(a1, empty));
            same(a2, $.concat(empty, a2));
        }

        @Test
        public void concat2intArrays() {
            int[] empty = {};
            int[] a1 = {1, 2};
            int[] a2 = {3, 4};
            eq(new int[]{1, 2, 3, 4}, $.concat(a1, a2));
            same(a1, $.concat(a1, empty));
            same(a2, $.concat(empty, a2));
        }

        @Test
        public void concat3intArrays() {
            int[] empty = {};
            int[] a1 = {1, 2};
            int[] a2 = {3, 4};
            int[] a3 = {5, 6};
            eq(new int[]{1, 2, 3, 4, 5, 6}, $.concat(a1, a2, a3));
            eq(a1, $.concat(a1, empty, empty));
            eq(a2, $.concat(empty, a2, empty));
            eq(a3, $.concat(empty, empty, a3));
            eq($.concat(a1, a3), $.concat(a1, empty, a3));
        }

        @Test
        public void concat2booleanArrays() {
            boolean[] empty = {};
            boolean[] a1 = {true, false};
            boolean[] a2 = {true, true};
            eq(new boolean[]{true, false, true, true}, $.concat(a1, a2));
            same(a1, $.concat(a1, empty));
            same(a2, $.concat(empty, a2));
        }
    }


}
