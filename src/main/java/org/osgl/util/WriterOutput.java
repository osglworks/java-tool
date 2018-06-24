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

import org.osgl.$;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

public class WriterOutput implements Output {
    private Writer w;

    public WriterOutput(Writer w) {
        this.w = $.requireNotNull(w);
    }

    @Override
    public void open() {
        // nothing to do
    }

    @Override
    public void close() {
        IO.close(w);
    }

    @Override
    public void flush() {
        IO.flush(w);
    }

    @Override
    public Output append(CharSequence csq) {
        IO.append(csq, w);
        return this;
    }

    @Override
    public Output append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end));
    }

    @Override
    public Output append(char c) {
        IO.write(c, w);
        return this;
    }

    @Override
    public Output append(byte[] bytes) {
        return append(new String(bytes));
    }

    @Override
    public Output append(byte[] bytes, int start, int end) {
        int len = end - start;
        byte[] ba = new byte[len];
        System.arraycopy(bytes, start, ba, 0, len);
        return append(ba);
    }

    @Override
    public Output append(byte b) {
        return append((char) b);
    }

    @Override
    public Output append(ByteBuffer buffer) {
        int len = buffer.limit();
        byte[] ba = new byte[len];
        buffer.get(ba);
        return append(ba);
    }

    @Override
    public OutputStream asOutputStream() {
        return new WriterOutputStream(w);
    }

    @Override
    public Writer asWriter() {
        return w;
    }
}
