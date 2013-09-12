package org.osgl.util;

import com.osgl.TestBase;
import org.junit.Test;

/**
 * Test N number utilities
 */
public class NTest extends TestBase {
    @Test
    public void test() {
        N.Num n = N.num("3");
        eq(n.intValue(), 3);
        
        eq(n.mul(2).intValue(), 6);
        
        eq(n.div(2).doubleValue(), 1.5);
        
        n = N.num("3.2");
        yes((n.doubleValue() - 3.2) < 0.000000001);
    }
}
