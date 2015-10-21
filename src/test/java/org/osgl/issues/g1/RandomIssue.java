package org.osgl.issues.g1;

import org.junit.Test;
import org.osgl.TestBase;
import org.osgl._;
import org.osgl.util.C;

public class RandomIssue extends TestBase {
    @Test
    public void testRandomIssue() {
        int lower = 50;
        int upper = 200;
        C.Range<Integer> range = C.range(lower, upper);
        for (int i = 0; i < 100000; ++i) {
            int r = _.random(range);
            yes(50 <= r);
            yes(r < 200);
        }
    }
}
