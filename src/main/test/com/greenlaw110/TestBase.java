package com.greenlaw110;

import org.junit.Assert;
import org.junit.internal.RealSystem;
import org.junit.runner.JUnitCore;

/**
 * The test base case
 */
public abstract class TestBase extends Assert {
    protected void eq(Object o1, Object o2) {
        assertEquals(o1, o2);
    }

    protected void yes(Boolean expr) {
        assertTrue(expr);
    }

    protected void no(Boolean expr) {
        assertFalse(expr);
    }

    protected static void run(Class<? extends TestBase> cls) {
        new JUnitCore().runMain(new RealSystem(), cls.getName());
    }
    
    protected static void println(String tmpl, Object... args) {
        System.out.println(String.format(tmpl, args));
    }
}
