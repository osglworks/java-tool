package org.osgl.util;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Test;
import org.osgl.BenchmarkBase;
import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.util.Random;

@BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 10)
public class UnsafeBenchmark extends BenchmarkBase {

    private String _short;
    private String _mid;
    private String _long;
    private String _longAllLowerCases;

    private static final char[] LOWER_CASE_LETTERS = Unsafe.bufOf("abcdefghijklmnopqrstuvwxyz");

    private static final $.F1<String, String> JDK_TO_LOWER_CASE = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return s.toLowerCase();
        }
    };

    private static final $.F1<String, String> UNSAFE_TO_LOWER_CASE = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return Unsafe.toLowerCase(s);
        }
    };

    private static final $.F1<String, String> JDK_ITERATION = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            int sz = s.length();
            char[] buf = new char[sz];
            for (int i = 0; i < sz; ++i) {
                buf[i] = s.charAt(i);
            }
            return new String(buf);
        }
    };

    private static final $.F1<String, String> UNSAFE_ITERATION = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            char[] buf = Unsafe.bufOf(s);
            int sz = buf.length;
            char[] newBuf = new char[sz];
            for (int i = 0; i < sz; ++i) {
                newBuf[i] = buf[i];
            }
            return new String(buf);
        }
    };

    public UnsafeBenchmark() {
        _short = S.random(8);
        _mid = S.random(128);
        _long = S.random(4096);
        _longAllLowerCases = _allLowerCases(4096);
    }

    @Test
    public void JDK_short_toLowerCase() {
        runTest(_short, JDK_TO_LOWER_CASE);
    }

    @Test
    public void Unsafe_short_toLowerCase() {
        runTest(_short, UNSAFE_TO_LOWER_CASE);
    }

    @Test
    public void JDK_mid_toLowerCase() {
        runTest(_mid, JDK_TO_LOWER_CASE);
    }

    @Test
    public void Unsafe_mid_toLowerCase() {
        runTest(_mid, UNSAFE_TO_LOWER_CASE);
    }

    @Test
    public void JDK_long_toLowerCase() {
        runTest(_long, JDK_TO_LOWER_CASE);
    }

    @Test
    public void Unsafe_long_toLowerCase() {
        runTest(_long, UNSAFE_TO_LOWER_CASE);
    }

    @Test
    public void JDK_long2_toLowerCase() {
        runTest(_longAllLowerCases, JDK_TO_LOWER_CASE);
    }

    @Test
    public void Unsafe_long2_toLowerCase() {
        runTest(_longAllLowerCases, UNSAFE_TO_LOWER_CASE);
    }

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

    private String _allLowerCases(int len) {
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
