package org.osgl;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.rules.TestRule;

/**
 * It shows reflection is 10 times slower than
 * direct method invocation
 */
@BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 10)
public class BenchmarkBase extends TestBase {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

}
