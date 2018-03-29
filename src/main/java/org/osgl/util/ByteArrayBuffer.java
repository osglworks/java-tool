package org.osgl.util;

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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

class ByteArrayBuffer extends ByteArrayOutputStream {

    static int BUFFER_INIT_SIZE = 1024;
    static int BUFFER_RETENTION_LIMIT = 1024 * 10;

    private boolean consumed;

    ByteArrayBuffer(int size) {
        super(size);
        consumed = false;
    }

    public final boolean consumed() {
        return consumed;
    }

    public final byte[] consume() {
        consumed = true;
        return toByteArray();
    }

    String consumeToString() {
        return new String(consume());
    }

    public void reset() {
        super.reset();
        this.consumed = false;
    }

    public final boolean isEmpty() {
        return count == 0;
    }

    public final int length() {
        return count;
    }

    public ByteArrayBuffer append(byte[] bytes) {
        return append(bytes, 0, bytes.length);
    }

    public ByteArrayBuffer append(byte[] bytes, int start, int end) {
        ensureNotConsumed();
        int size = end - start;
        if (0 == size) {
            return this;
        }
        write(bytes, start, end);
        return this;
    }

    public ByteArrayBuffer append(byte b) {
        ensureNotConsumed();
        write(b);
        return this;
    }

    public ByteArrayBuffer append(ByteBuffer buffer) {
        ensureNotConsumed();
        int n = buffer.remaining();
        for (int i = 0; i < n; i++)
            write(buffer.get());
        return this;
    }

    private int capacity() {
        return buf.length;
    }

    private void ensureNotConsumed() {
        E.illegalStateIf(consumed, "this buffer has already been consumed");
    }

    private static final ThreadLocal<ByteArrayBuffer> _buf = new ThreadLocal<ByteArrayBuffer>() {
        @Override
        protected ByteArrayBuffer initialValue() {
            ByteArrayBuffer buf = new ByteArrayBuffer(BUFFER_INIT_SIZE);
            buf.consume();
            return buf;
        }
    };

    static ByteArrayBuffer buffer() {
        ByteArrayBuffer buf = _buf.get();
        if (!buf.consumed() || buf.capacity() > BUFFER_RETENTION_LIMIT) {
            buf = new ByteArrayBuffer(BUFFER_INIT_SIZE);
            _buf.set(buf);
            return buf;
        }
        buf.reset();
        return buf;
    }


}
