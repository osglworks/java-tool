package org.osgl.util;

/*-
 * #%L
 * OSGL MVC
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
import org.osgl.OsglConfig;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

public class BufferedOutput implements Output {
    private Output sink;
    private S.Buffer buffer;
    private int limit;

    private BufferedOutput(Output output) {
        sink = $.notNull(output);
        buffer = S.buffer();
        limit = OsglConfig.getStringBufferRententionLimit();
    }

    @Override
    public void open() {
        sink.open();
    }

    @Override
    public void close() {
        flush();
        sink.close();
    }

    @Override
    public Output append(CharSequence csq) {
        int size = csq.length();
        if (0 == size) {
            return this;
        }
        if (buffer.length() + size >= limit) {
            flush();
        }
        buffer.append(csq);
        return this;
    }

    @Override
    public Output append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end));
    }

    public Output append(char[] chars, int start, int end) {
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > chars.length)
            throw new StringIndexOutOfBoundsException(end);
        if (start > end)
            throw new StringIndexOutOfBoundsException(end - start);
        int size = end - start;
        if (size == 0) {
            return this;
        }
        if (buffer.length() + size >= limit) {
            flush();
        }
        buffer.append(chars, start, end);
        return this;
    }

    @Override
    public Output append(char c) {
        if (buffer.length() + 1 >= limit) {
            flush();
        }
        buffer.append(c);
        return this;
    }

    @Override
    public Output append(byte[] bytes) {
        int size = bytes.length;
        if (buffer.length() + size >= limit) {
            flush();
        }
        buffer.append(bytes);
        return this;
    }

    @Override
    public Output append(byte[] bytes, int start, int end) {
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > bytes.length)
            throw new StringIndexOutOfBoundsException(end);
        if (start > end)
            throw new StringIndexOutOfBoundsException(end - start);
        int size = end - start;
        if (0 == size) {
            return this;
        }
        if (buffer.length() + size >= limit) {
            flush();
        }
        buffer.append(bytes, start, end);
        return this;
    }

    @Override
    public Output append(byte b) {
        if (buffer.length() + 1 >= limit) {
            flush();
        }
        buffer.append(b);
        return this;
    }

    @Override
    public Output append(ByteBuffer buffer) {
        flush();
        sink.append(buffer);
        return this;
    }

    @Override
    public OutputStream asOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) {
                append((byte) b);
            }

            @Override
            public void write(byte[] b) {
                append(b);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                append(b, off, len);
            }

            @Override
            public void close() {
                BufferedOutput.this.close();
            }
        };
    }

    @Override

    public Writer asWriter() {
        return new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) {
                BufferedOutput.this.append(cbuf, off, len);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
                BufferedOutput.this.close();
            }
        };
    }

    public void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        String s = buffer.toString();
        sink.append(s);
        buffer.reset();
    }

    public static Output wrap(Output output) {
        int limit = OsglConfig.getStringBufferRententionLimit();
        if (limit <= 2048) {
            // buffer too small (less than FastJSON SerializeWriter's internal buffer), no need to wrap it
            return output;
        }
        return new BufferedOutput(output);
    }
}
