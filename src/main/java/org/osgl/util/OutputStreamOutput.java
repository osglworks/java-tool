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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class OutputStreamOutput implements Output {
    private OutputStream os;

    public OutputStreamOutput(OutputStream os) {
        this.os = $.requireNotNull(os);
    }

    @Override
    public void open() {
        // nothing to do here
    }

    @Override
    public void close() {
        IO.close(os);
    }

    @Override
    public void flush() {
        IO.flush(os);
    }

    @Override
    public Output append(CharSequence csq) {
        return append(csq.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Output append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end));
    }

    @Override
    public Output append(char c) {
        if (c < 128) {
            return append((byte) c);
        }
        String s = String.valueOf(c);
        return append(s);
    }

    @Override
    public Output append(byte[] bytes) {
        IO.append(new ByteArrayInputStream(bytes), os);
        return this;
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
        IO.write(b, os);
        return this;
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
        return os;
    }

    @Override
    public Writer asWriter() {
        return new OutputStreamWriter(os);
    }
}
