package org.osgl;

import org.junit.Test;

public class BreakTest extends TestBase {

    @Test
    public void testGetPayload() {
        Object payload = new Object();
        $.Break b = new $.Break(payload);
        eq(payload, b.get());
    }


}
