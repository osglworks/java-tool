package org.osgl;

import org.junit.Test;

public class BreakTest extends TestBase {

    @Test
    public void testGetPayload() {
        Object payload = new Object();
        _.Break b = new _.Break(payload);
        eq(payload, b.get());
    }


}
