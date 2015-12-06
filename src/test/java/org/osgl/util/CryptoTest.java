package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

public class CryptoTest extends TestBase {
    @Test
    public void testGenRandomDigits() {
        for (int i = 1; i < 100; ++i) {
            String s = Crypto.genRandomDigits(i);
            yes(s.length() == i);
            for (char c : s.toCharArray()) {
                yes((c >= '0' && c <= '9'));
            }
        }
    }
}
