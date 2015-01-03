package org.osgl.util;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestRule;

@Ignore
public abstract class StrBenchmarkBase<T extends StrBase<T>> extends StrTestUtil<T> {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
}
