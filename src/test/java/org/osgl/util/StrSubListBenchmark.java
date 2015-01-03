package org.osgl.util;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.Clock;
import org.junit.Ignore;
import org.junit.Test;
import org.osgl.BenchmarkBase;

@BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1, callgc = false, clock = Clock.REAL_TIME, concurrency = -1)
@Ignore
public final class StrSubListBenchmark extends BenchmarkBase {

    private StrBase str = Str.of(S.random(5000));
    private StrBase faststr = FastStr.unsafeOf(S.random(5000));

    @Test
    public void str_left10() {
        runTest(str, 0, 10);
    }

    @Test
    public void str_mid10() {
        runTest(str, 2000, 2010);
    }

    @Test
    public void str_right10() {
        runTest(str, 4990, 5000);
    }

    @Test
    public void str_right500() {
        runTest(str, 4500, 5000);
    }

    @Test
    public void str_mid500() {
        runTest(str, 1800, 2300);
    }

    @Test
    public void str_left500() {
        runTest(str, 0, 500);
    }

    //@Test
    public void str_right2000() {
        runTest(str, 300, 5000);
    }

    //@Test
    public void str_mid2000() {
        runTest(str, 1500, 3500);
    }

    //@Test
    public void str_left2000() {
        runTest(str, 0, 2000);
    }

    @Test
    public void faststr_left10() {
        runTest(faststr, 0, 10);
    }

    @Test
    public void faststr_mid10() {
        runTest(faststr, 2000, 2010);
    }

    @Test
    public void faststr_right10() {
        runTest(faststr, 4990, 5000);
    }

    @Test
    public void faststr_right500() {
        runTest(faststr, 4500, 5000);
    }

    @Test
    public void faststr_mid500() {
        runTest(faststr, 1800, 2300);
    }

    @Test
    public void faststr_left500() {
        runTest(faststr, 0, 500);
    }

    //@Test
    public void faststr_right2000() {
        runTest(faststr, 300, 5000);
    }

    //@Test
    public void faststr_mid2000() {
        runTest(faststr, 1500, 3500);
    }

    //@Test
    public void faststr_left2000() {
        runTest(faststr, 0, 2000);
    }

    private void runTest(StrBase s, int from, int to) {
        for (int i = 0; i < 1000 * 1000; ++i) {
            s.subList(from, to);
        }
    }

}
