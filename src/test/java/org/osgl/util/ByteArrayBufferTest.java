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

import java.nio.charset.StandardCharsets;

public class ByteArrayBufferTest extends TestBase {

    private static final byte[] abc = "abc".getBytes(StandardCharsets.UTF_8);
    private static final byte[] ott = new byte[]{1, 2, 3};

    @Test
    public void itShallNotReuseUnconsumedBuffer() {
        ByteArrayBuffer sb = ByteArrayBuffer.buffer();
        sb.append(abc);
        ByteArrayBuffer sb2 = ByteArrayBuffer.buffer();
        sb2.append(ott);
        assertNotSame(sb, sb2);
        // we need to consume the buffer to avoid
        // break of next test case
        sb.consume();
        sb2.consume();
    }

    @Test
    public void itShallReuseConsumedBuffer() {
        ByteArrayBuffer sb = ByteArrayBuffer.buffer();
        sb.append(abc);
        eq("abc", sb.consumeToString());
        ByteArrayBuffer sb2 = ByteArrayBuffer.buffer();
        sb.append(ott);
        assertSame(sb, sb2);
        assertArrayEquals(ott, sb2.consume());
        assertSame(sb, sb2);
    }

}
