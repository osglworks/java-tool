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

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Ignore;
import org.junit.Test;
import org.osgl.BenchmarkBase;
import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.util.Random;

@Ignore
@BenchmarkOptions(warmupRounds = 2, benchmarkRounds = 10)
public class UnsafeBenchmark extends BenchmarkBase {

    private static final char[] LOWER_CASE_LETTERS = Unsafe.bufOf("abcdefghijklmnopqrstuvwxyz");

    private static String _short = S.random(8);
    private static String _mid = S.random(128);
    private static String _long = S.random(1024 * 16);
    private static String _longAllLowerCases = _allLowerCases(1024 * 16);

    public UnsafeBenchmark() {}

    private static final $.F1<String, String> JDK_ITERATION = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return new String(s.toCharArray());
        }
    };

    private static final $.F1<String, String> UNSAFE_ITERATION = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return new String(Unsafe.bufOf(s));
        }
    };

    @Test
    public void JDK_short_iter() {
        runTest(_short, JDK_ITERATION);
    }

    @Test
    public void Unsafe_short_iter() {
        runTest(_short, UNSAFE_ITERATION);
    }

    @Test
    public void JDK_mid_iter() {
        runTest(_mid, JDK_ITERATION);
    }

    @Test
    public void Unsafe_mid_iter() {
        runTest(_mid, UNSAFE_ITERATION);
    }

    @Test
    public void JDK_long_iter() {
        runTest(_long, JDK_ITERATION);
    }

    @Test
    public void Unsafe_long_iter() {
        runTest(_long, UNSAFE_ITERATION);
    }

    private void runTest(String s, $.F1<String, String> func) {
        int fact = 1000;
        if (s == _mid) {
            if (func == JDK_ITERATION || func == UNSAFE_ITERATION) {
                fact = 1000;
            } else {
                fact = 100;
            }
        } else if (s == _long || s == _longAllLowerCases) {
            if (func == JDK_ITERATION || func == UNSAFE_ITERATION) {
                fact = 100;
            } else {
                fact = 10;
            }
        }
        for (int i = 0; i < 1000 * fact; ++i) {
            func.apply(s);
        }
    }

    private static String _allLowerCases(int len) {
        final char[] chars = LOWER_CASE_LETTERS;
        final int max = chars.length;
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        while (len-- > 0) {
            int i = r.nextInt(max);
            sb.append(chars[i]);
        }
        return sb.toString();
    }
}
