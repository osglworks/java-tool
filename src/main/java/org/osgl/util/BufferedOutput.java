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

/**
 * This class implements a buffered output. By setting up such
 * an output, an application can write to the underline output
 * necessarily causing a call to the underlying system for each
 * char or byte written.
 *
 * Note it is highly recommend to use `BufferedOutput` in a
 * single thread context in order to reuse the thread local
 * allocated buffer space.
 *
 * This class is not thread safe.
 */
public class BufferedOutput implements Output {
    /**
     * The target output
     */
    private Output sink;

    /**
     * The character based buffer
     */
    private S.Buffer charBuf;

    /**
     * The byte buffer
     */
    private ByteArrayBuffer byteBuf;

    private int charBufLimit;
    private int byteBufLimit;

    private BufferedOutput(Output output) {
        sink = $.requireNotNull(output);
        charBufLimit = OsglConfig.getThreadLocalCharBufferLimit();
        byteBufLimit = OsglConfig.getThreadLocalByteArrayBufferLimit();
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
        if (charBuf().length() + size >= charBufLimit) {
            flush();
        }
        charBuf().append(csq);
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
        if (charBuf().length() + size >= charBufLimit) {
            flush();
        }
        charBuf().append(chars, start, end);
        return this;
    }

    @Override
    public Output append(char c) {
        if (charBuf().length() + 1 >= charBufLimit) {
            flush();
        }
        charBuf().append(c);
        return this;
    }

    @Override
    public Output append(byte[] bytes) {
        int size = bytes.length;
        if (0 == size) {
            return this;
        }
        if (byteBuf().length() + size >= byteBufLimit) {
            flush();
        }
        byteBuf().append(bytes);
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
        if (byteBuf().length() + size >= byteBufLimit) {
            flush();
        }
        byteBuf().append(bytes, start, end);
        return this;
    }

    @Override
    public Output append(byte b) {
        if (byteBuf().length() + 1 >= byteBufLimit) {
            flush();
        }
        byteBuf().append(b);
        return this;
    }

    @Override
    public Output append(ByteBuffer buffer) {
        if (byteBuf().length() + buffer.remaining() >= byteBufLimit) {
            flush();
        }
        byteBuf().append(buffer);
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
        flushCharBuf();
        flushByteBuf();
    }

    private void flushCharBuf() {
        if (null == charBuf || charBuf.isEmpty()) {
            return;
        }
        String s = charBuf.toString();
        charBuf.reset();
        sink.append(s);
    }

    private void flushByteBuf() {
        if (null == byteBuf || byteBuf.isEmpty()) {
            return;
        }
        byte[] bytes = byteBuf.consume();
        byteBuf.reset();
        sink.append(bytes);
    }

    private S.Buffer charBuf() {
        E.illegalStateIf(null != byteBuf, "This buffered output has already been used to output byte stream");
        if (null == this.charBuf) {
            this.charBuf = S.buffer();
        }
        return this.charBuf;
    }

    private ByteArrayBuffer byteBuf() {
        E.illegalStateIf(null != charBuf, "This buffered output has already been used to output char stream");
        if (null == this.byteBuf) {
            this.byteBuf = ByteArrayBuffer.buffer();
        }
        return this.byteBuf;
    }

    public static Output wrap(Output output) {
        int limit = OsglConfig.getThreadLocalCharBufferLimit();
        if (limit <= 2048) {
            // buffer too small (less than FastJSON SerializeWriter's internal buffer), no need to wrap it
            return output;
        }
        return new BufferedOutput(output);
    }

}
