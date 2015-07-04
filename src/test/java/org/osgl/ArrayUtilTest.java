package org.osgl;

import org.junit.Test;

public class ArrayUtilTest extends TestBase {

    @Test
    public void testReverse() {
        int[] ia = {1, 2, 3};
        yes(_.eq2(new int[] {3, 2, 1}, _.reverse(ia)));
    }

    @Test
    public void testReverseObjectArray() {
        String[] sa = {"1", "2", "3", "4"};
        yes(_.eq2(new String[] {"4", "3", "2", "1"}, _.reverse(sa)));
    }

    @Test
    public void testConcat() {
        int[] a1 = {1, 2}, a2 = {3, 4}, a3 = {5};
        yes(_.eq2(new int[] {1, 2, 3, 4, 5}, _.concat(a1, a2, a3)));
    }
}
