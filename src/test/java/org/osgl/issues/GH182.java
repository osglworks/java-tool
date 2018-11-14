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

import java.util.*;

public class GH182 extends TestBase {

    class Bean {
        private Date begTime;
        public Bean(int year, int mon, int dayOfMon) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MARCH, mon);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMon);
            begTime = calendar.getTime();
        }
    }

    @Test
    public void test() {
        Bean src = new Bean(1954, 12, 1);
        Map<String, Object> map = new HashMap<>();
        $.copy(src).to(map);
        Object obj = map.get("begTime");
        eq(obj, src.begTime);
    }

}
