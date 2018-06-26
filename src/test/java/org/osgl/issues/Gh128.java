package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
