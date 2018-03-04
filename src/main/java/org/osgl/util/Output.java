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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * `Output` is an {@link Appendable} with extra methods to append {@link ByteBuffer} and
 * `byte[]`.
 *
 * An `Output` is a stateful object, it can be {@link #open() opened} and {@link #close() closed}.
 *
 * An `Output` instance can create {@link OutputStream} or {@link Writer} based on itself.
 *
 * **Note** the behavior differences between an `Output` and an `Appendable`: when a char sequence
 * is `null`, nothing will be appended to the `Output` while a literal `null` will be appended
 * to the `Appendable`
 *
 * Depending on implementation `Output` might be or not be thread safe.
 */
public interface Output extends Appendable, Closeable, Flushable {

    /**
     * Prepare this `Output` instance for appending. If any append method
     * is called before calling `open()`, then `open()` will be called
     * implicitly before appending happens.
     */
    void open();

    /**
     * Close this `Output` instance. Call to any append method after
     * the `Output` is closed will result in {@link IllegalStateException}
     */
    void close();

    /**
     * Flushes the output.  If the output has saved any characters from the
     * various append() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     *
     * <p> If the intended destination of this output is an abstraction provided
     * by the underlying operating system, for example a file, then flushing the
     * stream guarantees only that bytes previously written to the stream are
     * passed to the operating system for writing; it does not guarantee that
     * they are actually written to a physical device such as a disk drive.
     */
    void flush();

    /**
     * Appends the specified character sequence to this <tt>Output</tt>.
     *
     * <p> Depending on which class implements the character sequence
     * <tt>csq</tt>, the entire sequence may not be appended.  For
     * instance, if <tt>csq</tt> is a {@link java.nio.CharBuffer} then
     * the subsequence to append is defined by the buffer's position and limit.
     *
     * @param csq
     *         The character sequence to append.  If <tt>csq</tt> is
     *         <tt>null</tt>, then nothing will be appended
     * @return A reference to this <tt>Output</tt>
     */
    Output append(CharSequence csq);

    /**
     * Appends a subsequence of the specified character sequence to this
     * <tt>Output</tt>.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     *     out.append(csq.subSequence(start, end)) </pre>
     *
     * @param csq
     *         The character sequence from which a subsequence will be
     *         appended.  If <tt>csq</tt> is <tt>null</tt>, then nothing
     *         will be appended.
     * @param start
     *         The index of the first character in the subsequence
     * @param end
     *         The index of the character following the last character in the
     *         subsequence
     * @return A reference to this <tt>Output</tt>
     * @throws IndexOutOfBoundsException
     *         If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *         is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *         <tt>csq.length()</tt>
     */
    Output append(CharSequence csq, int start, int end);

    /**
     * Appends the specified character to this <tt>Output</tt>.
     *
     * @param c
     *         The character to append
     * @return A reference to this <tt>Output</tt>
     */
    Output append(char c);

    /**
     * Appends a byte array to this `Output`.
     *
     * @param bytes
     *         The byte array will be appended.
     * @return A reference to this `Output`
     */
    Output append(byte[] bytes);

    /**
     * Appends a subsequence of the specified byte array to this
     * <tt>Output</tt>.
     *
     * <p> An invocation of this method of the form <tt>out.append(bytes, start,
     * end)</tt> when <tt>bytes</tt> is not <tt>null</tt>, behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     *     out.append(csq.subSequence(start, end)) </pre>
     *
     * @param bytes
     *         The byte array from which a subsequence will be
     *         appended.  If <tt>bytes</tt> is <tt>null</tt>, then nothing
     *         will be appended.
     * @param start
     *         The index of the first character in the subsequence
     * @param end
     *         The index of the character following the last character in the
     *         subsequence
     * @return A reference to this <tt>Output</tt>
     * @throws IndexOutOfBoundsException
     *         If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *         is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *         <tt>csq.length()</tt>
     */
    Output append(byte[] bytes, int start, int end);

    /**
     * Appends a byte to this `Output`.
     *
     * @param b
     *         A byte will be appended.
     * @return A reference to this `Output`.
     */
    Output append(byte b);

    /**
     * Appends the specified buffer into this <tt>Appendable</tt>.
     *
     * @param buffer
     *         The buffer to append.
     * @return A reference to this <tt>Output</tt>
     */
    Output append(ByteBuffer buffer);

    /**
     * Create an {@link OutputStream} based on this `Output` instance.
     *
     * Calling to this method before calling to any other method including
     * `asOutputStream()` itself will result in {@link IllegalStateException}.
     *
     * @return an {@link OutputStream} reference backed by this `Output`.
     */
    OutputStream asOutputStream();

    /**
     * Create an {@link Writer} based on this `Output` instance.
     *
     * Calling to this method before calling to any other method including
     * `asOutputStream()` itself will result in {@link IllegalStateException}.
     *
     * @return an {@link Writer} reference backed by this `Output`.
     */
    Writer asWriter();

    class Adaptors {

        public static Output of(final OutputStream os) {
            if (os instanceof Output) {
                return (Output) os;
            }
            final Writer w = new OutputStreamWriter(os);
            return new Output() {
                @Override
                public void open() {
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
                    IO.writeContent(csq, w);
                    return this;
                }

                @Override
                public Output append(CharSequence csq, int start, int end) {
                    IO.writeContent(csq.subSequence(start, end), w);
                    return this;
                }

                @Override
                public Output append(char c) {
                    IO.write(c, w);
                    return this;
                }

                @Override
                public Output append(byte[] bytes) {
                    IO.write(bytes, os);
                    return this;
                }

                @Override
                public Output append(byte[] bytes, int start, int end) {
                    try {
                        os.write(bytes, start, end);
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                    return this;
                }

                @Override
                public Output append(byte b) {
                    IO.write(b, os);
                    return null;
                }

                @Override
                public Output append(ByteBuffer buffer) {
                    int len = buffer.remaining();
                    byte[] bytes = new byte[len];
                    buffer.get(bytes);
                    IO.write(bytes, os);
                    return this;
                }

                @Override
                public OutputStream asOutputStream() {
                    return os;
                }

                @Override
                public Writer asWriter() {
                    return w;
                }
            };
        }

        public static Output of(final Writer w) {
            final OutputStream os = new WriterOutputStream(w, StandardCharsets.UTF_8);
            return new Output() {
                @Override
                public void open() {
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
                    IO.writeContent(csq, w);
                    return this;
                }

                @Override
                public Output append(CharSequence csq, int start, int end) {
                    IO.writeContent(csq.subSequence(start, end), w);
                    return this;
                }

                @Override
                public Output append(char c) {
                    IO.write(c, w);
                    return this;
                }

                @Override
                public Output append(byte[] bytes) {
                    IO.write(bytes, os);
                    return this;
                }

                @Override
                public Output append(byte[] bytes, int start, int end) {
                    try {
                        os.write(bytes, start, end);
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                    return this;
                }

                @Override
                public Output append(byte b) {
                    IO.write(b, os);
                    return this;
                }

                @Override
                public Output append(ByteBuffer buffer) {
                    int len = buffer.remaining();
                    byte[] bytes = new byte[len];
                    buffer.get(bytes);
                    IO.write(bytes, os);
                    return this;
                }

                @Override
                public OutputStream asOutputStream() {
                    return os;
                }

                @Override
                public Writer asWriter() {
                    return w;
                }
            };
        }

        public static Output of(final Appendable appendable) {
            if (appendable instanceof Output) {
                return (Output) appendable;
            }
            return new Output() {

                @Override
                public void open() {

                }

                @Override
                public void close() {

                }

                @Override
                public void flush() {

                }

                @Override
                public Output append(CharSequence csq) {
                    if (null == csq) {
                        return this;
                    }
                    try {
                        appendable.append(csq);
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                    return this;
                }

                @Override
                public Output append(CharSequence csq, int start, int end) {
                    if (null == csq) {
                        return this;
                    }
                    try {
                        appendable.append(csq, start, end);
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                    return this;
                }

                @Override
                public Output append(char c) {
                    try {
                        appendable.append(c);
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                    return this;
                }

                @Override
                public Output append(byte[] bytes) {
                    if (null == bytes) {
                        return this;
                    }
                    append(ByteBuffer.wrap(bytes));
                    return this;
                }

                @Override
                public Output append(byte[] bytes, int start, int end) {
                    append(ByteBuffer.wrap(bytes, start, end));
                    return this;
                }

                @Override
                public Output append(byte b) {
                    append((char) (b & 0xFF));
                    return this;
                }

                @Override
                public Output append(ByteBuffer buffer) {
                    try {
                        appendable.append(buffer.asCharBuffer());
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                    return this;
                }

                @Override
                public OutputStream asOutputStream() {
                    return Adaptors.asOutputStream(this);
                }

                @Override
                public Writer asWriter() {
                    return Adaptors.asWriter(this);
                }
            };
        }

        public static OutputStream asOutputStream(final Output output) {
            return new OutputStream() {
                @Override
                public void write(int b) {
                    output.append((byte) b);
                }

                @Override
                public void write(byte[] b) throws IOException {
                    output.append(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    output.append(b, off, len);
                }

                @Override
                public void flush() throws IOException {
                    output.flush();
                }

                @Override
                public void close() throws IOException {
                    output.close();
                }
            };
        }

        public static Writer asWriter(final Output output) {
            return new Writer() {
                @Override
                public void write(char[] c, int off, int len) {
                    if ((off < 0) || (off > c.length) || (len < 0) ||
                            ((off + len) > c.length) || ((off + len) < 0)) {
                        throw new IndexOutOfBoundsException();
                    } else if (len == 0) {
                        return;
                    }
                    output.append(CharBuffer.wrap(c, off, len));
                }

                @Override
                public void flush() {
                    output.flush();
                }

                @Override
                public void close() {
                    output.close();
                }
            };
        }

    }
}
