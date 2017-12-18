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
import org.osgl.TestBase;

public class UnsafeTest extends TestBase {
    private String _short;
    private String _mid;
    private String _long;

    public UnsafeTest() {
        _short = S.random(8);
        _mid = S.random(128);
        _long = S.random(4096);
    }

    static void ceq(CharSequence c1, CharSequence c2) {
        eq(c1.toString(), c2.toString());
    }
}
