package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

/**
 * Created by luog on 7/01/14.
 */
public class STest extends TestBase {
    @Test
    public void testAppend() {
        eq(S.str("a").append("b"), S.str("ab"));
    }

    @Test
    public void testPrepend() {
        eq(S.str("a").prepend("b"), S.str("ba"));
    }
}
