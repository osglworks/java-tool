package com.osgl;

import org.junit.Test;
import org.osgl.util.S;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 17/06/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StrTest extends TestBase {

    protected S.Str s = S.str("/usr/bin/bash");
    
    public void testAfter() {
        eq(s.after("/").get(), "bash");
    }
    
    @Test
    public void testBefore() {
        eq(s.before("/").get(), "");
    }
    
    @Test
    public void testAfterFirst() {
        eq(s.afterFirst("/").get(), "usr/bin/bash");
    }
    
    @Test
    public void testBeforeLast() {
        eq(s.beforeLast("/").get(), "/usr/bin");
    }

    public static void main(String[] args) {
        run(StrTest.class);
    }
}
