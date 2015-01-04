package org.osgl;

import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.osgl.util.S;

import java.util.Arrays;
import java.util.Random;

/**
 * The test base case
 */
public abstract class TestBase extends Assert {

    protected static void eq(Object[] a1, Object[] a2) {
        yes(Arrays.equals(a1, a2));
    }

    protected static void eq(Object o1, Object o2) {
        assertEquals(o1, o2);
    }

    protected static void yes(Boolean expr, String msg, Object... args) {
        assertTrue(S.fmt(msg, args), expr);
    }

    protected static void yes(Boolean expr) {
        assertTrue(expr);
    }

    protected static void no(Boolean expr, String msg, Object... args) {
        assertFalse(S.fmt(msg, args), expr);
    }

    protected static void no(Boolean expr) {
        assertFalse(expr);
    }

    protected static void fail(String msg, Object... args) {
        assertFalse(S.fmt(msg, args), true);
    }

    protected static void run(Class<? extends TestBase> cls) {
        new JUnitCore().run(cls);
    }
    
    protected static void println(String tmpl, Object... args) {
        System.out.println(String.format(tmpl, args));
    }


    protected static String newRandStr() {
        return S.random(new Random().nextInt(30) + 15);
    }
}
