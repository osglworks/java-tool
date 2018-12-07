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

public class GH189 extends TestBase {

    public static class Bean {
        public Integer id = 1;

        public Bean(Integer id) {
            this.id = id;
        }

        public Bean() {
        }
    }

    @Test
    public void test() {
        Bean src = new Bean(null);
        Bean tgt = $.copy(src).to(Bean.class);
        isNull(tgt.id);
    }

}
