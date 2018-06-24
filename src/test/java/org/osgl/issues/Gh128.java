package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Gh128 extends TestBase {

    List<Integer> list = C.list(1, 2, 3, 4, 5, 6, 7, 8);

    @Test
    public void test() {
        Random r = ThreadLocalRandom.current();
        for (int i = 0; i < 100; ++i) {
            int min = r.nextInt(6);
            List<Integer> result = $.randomSubList(list, min);
            System.out.println(result);
            yes(result.size() >= min);
            yes(result.size() <= 8);
        }
    }

}
