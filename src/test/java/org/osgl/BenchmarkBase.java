package org.osgl;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestRule;

@Ignore
public class BenchmarkBase {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
}
