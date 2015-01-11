package org.osgl;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.osgl.util.E;

import java.lang.reflect.Method;

/**
 * It shows reflection is 10 times slower than
 * direct method invocation
 */
@BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 10)
public class BenchmarkBase extends TestBase {
    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    public static class Foo {
        public boolean bar() {
            return false;
        }
    }

    private static Method fooBar;

    static {
        try {
            fooBar = Foo.class.getDeclaredMethod("bar");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Test
    public void reflectionMethodInvocation() throws Exception {
        Foo foo = new Foo();
        for (int i = 0; i < 1000 * 1000 * 10; ++i) {
            fooBar.invoke(foo);
        }
    }

    @Test
    public void directMethodInvocation() {
        Foo foo = new Foo();
        for (int i = 0; i < 1000 * 1000 * 100; ++i) {
            no(foo.bar());
        }
    }
}
