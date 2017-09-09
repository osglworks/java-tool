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

public class SBufferTest extends TestBase {

    @Test
    public void itShallReuseConsumedBuffer() {
        S.Buffer sb = S.buffer("abc");
        eq("abc", sb.toString());
        S.Buffer sb2 = S.buffer("123");
        assertSame(sb, sb2);
        eq("123", sb2.toString());
        assertSame(sb, sb2);
    }

    @Test
    public void itShallNotReuseUnconsumedBuffer() {
        S.Buffer sb = S.buffer("abc");
        S.Buffer sb2 = S.buffer("123");
        assertNotSame(sb, sb2);
    }

    @Test
    public void testPrepend() {
        S.Buffer sb = S.newBuffer("abc");
        sb.prepend("1234");
        eq("1234abc", sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(true);
        eq("trueabc", sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(100);
        eq("100abc", sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(Long.MAX_VALUE);
        eq(S.builder(Long.MAX_VALUE).append("abc").toString(), sb.toString());
        sb = S.newBuffer("abc");
        sb.prepend(3.3f);
        eq("3.3abc", sb.toString());
    }

}
