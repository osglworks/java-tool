package org.osgl.util;

import org.osgl.$;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {

    private ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = $.requireNotNull(buf);
    }

    @Override
    public int read(byte[] bytes, int off, int len) {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }

    @Override
    public int read() {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }
}
