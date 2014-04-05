package org.osgl;

import org.junit.Test;
import org.osgl.util.C;

/**
 * Created by luog on 4/04/14.
 */
public class _Test extends TestBase {

    @Test
    public void testRandom() {
        C.Range<Integer> r = C.range(10, 100);
        for (int i = 0; i < 100; ++i) {
            int n = _.random(r);
            yes(n >= 10);
            yes(n < 100);
        }
    }

}
