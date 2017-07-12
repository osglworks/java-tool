package org.osgl.util;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Test;
import org.osgl.BenchmarkBase;

@BenchmarkOptions(warmupRounds = 100 * 100 * 5, benchmarkRounds = 100 * 100 * 100 * 5)
public class FastSplitBenchmark extends BenchmarkBase {

    String toBeSplited = "abc**12345**abxyd**123425**asfadfa**9$$#sdd";

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 1)
    public void testStringSplit() {
        String[] sa = toBeSplited.split("\\*\\*");
        yes(sa.length == 6);
    }

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 1)
    public void testFastSplit() {
        S.List sl = S.fastSplit(toBeSplited, "**");
        UtilTestBase.eq(sl, C.listOf(toBeSplited.split("\\*\\*")));
    }

    @Test
    public void benchmarkStringSplit() {
        toBeSplited.split("\\*\\*");
    }

    @Test
    public void benchmarkFastSplit() {
        S.fastSplit(toBeSplited, "**");
    }

}
