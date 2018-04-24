package org.osgl.issues;

import org.junit.Test;
import org.osgl.TestBase;
import org.osgl.util.S;

public class Gh74 extends TestBase  {

    @Test
    public void test() {
        eq("abc", S.buffer(new char[]{'a', 'b', 'c'}).toString());
        eq("abc", S.buffer(new Character[]{'a', 'b', 'c'}).toString());
    }
}
