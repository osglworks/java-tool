package org.osgl.util;

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
