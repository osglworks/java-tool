package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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
import org.osgl.OsglToolTestBase;

public class CryptoTest extends OsglToolTestBase {
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
