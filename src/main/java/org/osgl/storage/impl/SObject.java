/* 
 * Copyright (C) 2013 The Java Storage project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.osgl.storage.impl;

/*-
 * #%L
 * Java Storage Service
 * %%
 * Copyright (C) 2013 - 2017 OSGL (Open Source General Library)
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
import org.osgl.exception.UnexpectedIOException;
import org.osgl.storage.ISObject;
import org.osgl.storage.IStorageService;
import org.osgl.util.*;

import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link ISObject}
 */
public abstract class SObject implements ISObject {

    private String key;
    private Map<String, String> attrs = new HashMap<>();
    protected boolean valid = true;
    protected Throwable cause = null;

    SObject(String key) {
        if (null == key) {
            throw new NullPointerException();
        }
        this.key = key;
    }

    @Override
    public boolean isDumb() {
        return false;
    }

    public static SObject getInvalidObject(String key, Throwable cause) {
        SObject sobj = of(key, "");
        sobj.valid = false;
        sobj.cause = cause;
        return sobj;
    }

    public String getKey() {
        return key;
    }

    protected void setAttrs(Map<String, String> attrs) {
        if (null == attrs) return;
        this.attrs.putAll(attrs);
    }

    @Override
    public String getUrl() {
        return attrs.get(ATTR_URL);
    }

    @Override
    public String getFilename() {
        return getAttribute(ATTR_FILE_NAME);
    }

    @Override
    public String getContentType() {
        return getAttribute(ATTR_CONTENT_TYPE);
    }

    @Override
    public void setFilename(String filename) {
        E.illegalArgumentIf(S.blank(filename));
        setAttribute(ATTR_FILE_NAME, filename);
    }

    @Override
    public void setContentType(String contentType) {
        E.illegalArgumentIf(S.blank(contentType));
        setAttribute(ATTR_CONTENT_TYPE, contentType);
    }

    @Override
    public String getAttribute(String key) {
        return attrs.get(key);
    }

    @Override
    public ISObject setAttribute(String key, String val) {
        attrs.put(key, val);
        return this;
    }

    @Override
    public ISObject setAttributes(Map<String, String> attrs) {
        setAttrs(attrs);
        return this;
    }

    @Override
    public boolean hasAttribute() {
        return !attrs.isEmpty();
    }

    @Override
    public Map<String, String> getAttributes() {
        return C.newMap(attrs);
    }

    @Override
    public boolean isEmpty() {
        String s = asString();
        return null == s || "".equals(s);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Throwable getException() {
        return cause;
    }

    @Override
    public void consumeOnce($.Function<InputStream, ?> consumer) {
        InputStream is = null;
        try {
            is = asInputStream();
            consumer.apply(is);
        } finally {
            IO.close(is);
        }
    }

    protected final String suffix() {
        String originalFilename = getAttribute(ATTR_FILE_NAME);
        if (S.notBlank(originalFilename)) {
            int pos = originalFilename.lastIndexOf(".");
            if (pos > -1) {
                return originalFilename.substring(pos, originalFilename.length());
            }
        }
        return "";
    }

    /**
     * Construct an SObject with file specified. The key to the
     * sobject is the file's path
     *
     * @param file the file
     * @return an SObject of the file specified
     */
    public static SObject of(File file) {
        return of(file.getPath(), file);
    }

    /**
     * Construct an SObject with key and file specified
     *
     * @see #of(String, File, Map)
     */
    public static SObject of(String key, File file) {
        if (file.canRead() && file.isFile()) {
            SObject sobj = new FileSObject(key, file);
            String fileName = file.getName();
            sobj.setAttribute(ATTR_FILE_NAME, file.getName());
            String fileExtension = S.fileExtension(fileName);
            MimeType mimeType = MimeType.findByFileExtension(fileExtension);
            String type = null != mimeType ? mimeType.type() : null;
            sobj.setAttribute(ATTR_CONTENT_TYPE, type);
            sobj.setAttribute(ATTR_CONTENT_LENGTH, S.string(file.length()));
            return sobj;
        } else {
            return getInvalidObject(key, new IOException("File is a directory or not readable"));
        }
    }

    /**
     * Deprecated
     *
     * @see #of(String, File)
     */
    @Deprecated
    public static SObject valueOf(String key, File f) {
        return of(key, f);
    }

    /**
     * Construct an SObject with specified key, file and attributes
     * specified in {@link Map}
     *
     * @see #of(String, File, String...)
     */
    public static SObject of(String key, File file, Map<String, String> attributes) {
        SObject sobj = of(key, $.requireNotNull(file));
        sobj.setAttributes(attributes);
        return sobj;
    }

    /**
     * @see #of(String, File, Map)
     */
    @Deprecated
    public static SObject valueOf(String key, File file, Map<String, String> conf) {
        return of(key, file, conf);
    }

    /**
     * Construct an SObject with key, file and attributes specified in
     * key1, val1, key2, val2... sequence
     *
     * @see #of(String, File, Map)
     */
    public static SObject of(String key, File file, String... attrs) {
        SObject sobj = of(key, $.requireNotNull(file));
        Map<String, String> map = C.Map(attrs);
        sobj.setAttributes(map);
        return sobj;
    }

    /**
     * Deprecated
     *
     * @see #of(String, File, String...)
     */
    @Deprecated
    public static SObject valueOf(String key, File file, String... attrs) {
        return of(key, file, attrs);
    }

    /**
     * Construct an sobject with specified input stream and a randomly
     * generated key.
     * <p>
     * <p>Node the sobject constrcuted from input stream has limits
     * please see the comment to {@link #of(String, InputStream)}
     * </p>
     *
     * @see #of(String, InputStream)
     */
    public static SObject of(InputStream is) {
        return of(randomKey(), $.requireNotNull(is));
    }

    /**
     * Load an sobject from classpath by given url path
     *
     * This method will call {@link Class#getResource(String)} method to open
     * an inputstream to the resource and then construct an SObject with the
     * inputstream
     *
     * @param url the resource url path
     * @return the sobject instance if loaded successfully or `null` if cannot load resource from the url
     */
    public static SObject loadResource(String url) {
        InputStream is = SObject.class.getResourceAsStream(url);
        if (null == is) {
            return null;
        }
        String filename = S.afterLast(url, "/");
        if (S.blank(filename)) {
            filename = url;
        }
        return of(randomKey(), is, ATTR_FILE_NAME, filename);
    }

    /**
     * Construct an SObject with key and input stream. Note unlike sobject
     * constructed with String, byte array or file, the sobject constructed
     * with input stream can only be read for one time. If the program
     * tries to access the Sobject the second time, it will encountered an
     * {@link UnexpectedIOException}. Another limit of this sobject is it
     * does not support {@link org.osgl.storage.ISObject#getLength()} method
     * <p>
     * <p>If it needs to construct an SObject without these limits from
     * an input stream, then it shall first read the inputstream into
     * a bytearray, and use the byte array to construct the sobject like
     * following code</p>
     * <p>
     * <pre><code>
     * InputStream is = ...
     * ...
     * ISObject sobj = SObject.of(IO.readContent(is))
     * </code><pre>
     */
    public static SObject of(String key, InputStream is) {
        try {
            return new InputStreamSObject(key, $.requireNotNull(is));
        } catch (Exception e) {
            return getInvalidObject(key, e);
        }
    }

    /**
     * deprecated
     *
     * @see #of(String, InputStream)
     */
    @Deprecated
    public static SObject valueOf(String key, InputStream is) {
        return of(key, is);
    }

    /**
     * Construct a sobject with key, input stream and attributes specified in a
     * {@link Map}.
     * <p>
     * <p>Node the sobject constrcuted from input stream has limits
     * please see the comment to {@link #of(String, InputStream)}
     * </p>
     *
     * @see #of(String, InputStream)
     */
    public static SObject of(String key, InputStream is, Map<String, String> conf) {
        SObject sobj = of(key, is);
        sobj.setAttributes(conf);
        return sobj;
    }

    /**
     * deprecated
     *
     * @see #of(String, InputStream, Map)
     */
    @Deprecated
    public static SObject valueOf(String key, InputStream is, Map<String, String> conf) {
        return of(key, is, conf);
    }

    /**
     * Construct a sobject with key, input stream and attributes specified in a
     * sequence like key1, val1, key2, val2, ...
     * <p>
     * <p>Node the sobject constrcuted from input stream has limits
     * please see the comment to {@link #of(String, InputStream)}
     * </p>
     *
     * @see #of(String, InputStream)
     */
    public static SObject of(String key, InputStream is, String... attrs) {
        SObject sobj = of(key, is);
        Map<String, String> map = C.Map(attrs);
        sobj.setAttributes(map);
        return sobj;
    }

    /**
     * deprecated
     *
     * @see #of(String, File, String...)
     */
    @Deprecated
    public static SObject valueOf(String key, InputStream is, String... attrs) {
        return of(key, is, attrs);
    }

    /**
     * Construct an sobject with specified content in String and a randomly
     * generated key
     *
     * @see #of(String, String)
     */
    public static SObject of(String content) {
        return new StringSObject(randomKey(), content);
    }

    /**
     * Construct an sobject with content and key specified
     *
     * @see #of(String, String, Map)
     */
    public static SObject of(String key, String content) {
        return new StringSObject(key, $.requireNotNull(content));
    }

    /**
     * Deprecated
     *
     * @see #of(String, String)
     */
    @Deprecated
    public static SObject valueOf(String key, String content) {
        return of(key, content);
    }

    /**
     * Construct an sobject with content, key and attributes specified in
     * {@link Map}
     */
    public static SObject of(String key, String content, Map<String, String> attrs) {
        SObject sobj = of(key, content);
        sobj.setAttributes(attrs);
        return sobj;
    }

    /**
     * Deprecated
     *
     * @see #of(String, String, Map)
     */
    @Deprecated
    public static SObject valueOf(String key, String content, Map<String, String> attrs) {
        return of(key, content, attrs);
    }

    /**
     * Construct an sobject with key, content and attributes specified in sequence
     * key1, val1, key2, val2, ...
     *
     * @see #of(String, String, Map)
     */
    public static SObject of(String key, String content, String... attrs) {
        SObject sobj = of(key, content);
        Map<String, String> map = C.Map(attrs);
        sobj.setAttributes(map);
        return sobj;
    }

    /**
     * Deprecated
     *
     * @see #of(String, String, String...)
     */
    @Deprecated
    public static SObject valueOf(String key, String content, String... attrs) {
        return of(key, content, attrs);
    }

    /**
     * Construct an sobject with content in byte array and
     * a random generated key
     *
     * @see #of(String, byte[])
     */
    public static SObject of(byte[] buf) {
        return of(randomKey(), $.requireNotNull(buf));
    }

    /**
     * Construct an sobject with specified key and byte array.
     *
     * Note the byte array will be used directly without copying into an new array.
     *
     * @see #of(String, byte[], Map)
     */
    public static SObject of(String key, byte[] buf) {
        return new ByteArraySObject(key, $.requireNotNull(buf));
    }

    /**
     * Construct an SObject with random generated key, byte array and number of bytes
     * @param buf the source byte array
     * @param len the number of bytes in the array should be stored in the returing object
     * @return an SObject as described above
     */
    public static SObject of(byte[] buf, int len) {
        return of(randomKey(), buf, len);
    }

    /**
     * Construct an SObject with specified key, byte array and number of bytes
     * @param key the key
     * @param buf the source byte array
     * @param len the number of bytes in the array should be stored in the returing object
     * @return an SObject as described above
     */
    public static SObject of(String key, byte[] buf, int len) {
        if (len <= 0) {
            return of(key, new byte[0]);
        }
        if (len >= buf.length) {
            return of(key, buf);
        }
        byte[] ba = new byte[len];
        System.arraycopy(buf, 0, ba, 0, len);
        return of(key, ba);
    }

    /**
     * Deprecated
     *
     * @see #of(String, byte[])
     */
    @Deprecated
    public static SObject valueOf(String key, byte[] buf) {
        return of(key, buf);
    }

    /**
     * Construct an sobject with specified key, content as byte array
     * and attributes in {@link Map}
     *
     * @see #of(String, byte[], String...)
     */
    public static SObject of(String key, byte[] buf, Map<String, String> attrs) {
        SObject sobj = of(key, $.requireNotNull(buf));
        sobj.setAttributes(attrs);
        return sobj;
    }

    /**
     * Deprecated
     *
     * @see #of(String, byte[], Map)
     */
    @Deprecated
    public static SObject valueOf(String key, byte[] buf, Map<String, String> attrs) {
        return of(key, buf, attrs);
    }

    /**
     * Construct an sobject with specified key, content in byte array and
     * attributes in sequence of key1, val1, key1, val2, ...
     *
     * @see #of(String, byte[], Map)
     */
    public static SObject of(String key, byte[] buf, String... attrs) {
        SObject sobj = of(key, $.requireNotNull(buf));
        Map<String, String> map = C.Map(attrs);
        sobj.setAttributes(map);
        return sobj;
    }

    /**
     * Deprecated
     *
     * @see #of(String, byte[], String...)
     */
    @Deprecated
    public static SObject valueOf(String key, byte[] buf, String... attrs) {
        return of(key, buf, attrs);
    }

    public static SObject valueOf(String key, ISObject copy) {
        SObject sobj = of(key, copy.asByteArray());
        sobj.setAttrs(copy.getAttributes());
        return sobj;
    }

    public static SObject lazyLoad(String key, IStorageService ss) {
        return new LazyLoadSObject(key, ss);
    }

    public static SObject lazyLoad(String key, IStorageService ss, Map<String, String> conf) {
        SObject sobj = lazyLoad(key, ss);
        sobj.setAttributes(conf);
        return sobj;
    }

    public static SObject lazyLoad(String key, IStorageService ss, String... attrs) {
        SObject sobj = lazyLoad(key, ss);
        Map<String, String> map = C.Map(attrs);
        sobj.setAttributes(map);
        return sobj;
    }

    private static File createTempFile(String suffix) {
        try {
            return File.createTempFile("sobj_", suffix);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    static class StringSObject extends SObject {
        private String s_ = null;
        private boolean dumb = false;

        StringSObject(String key, String s) {
            super(key);
            s_ = null == s ? "" : s;
        }

        @Override
        public byte[] asByteArray() {
            return s_.getBytes();
        }

        @Override
        public File asFile() {
            File tmpFile = createTempFile(suffix());
            IO.write(s_, tmpFile);
            return tmpFile;
        }

        @Override
        public InputStream asInputStream() {
            return IO.is(asByteArray());
        }

        @Override
        public String asString() {
            return s_;
        }

        @Override
        public String asString(Charset charset) throws UnexpectedIOException {
            return s_;
        }

        @Override
        public long getLength() {
            return s_.length();
        }

        @Override
        public boolean isDumb() {
            return dumb;
        }

        @Override
        public int hashCode() {
            return $.hc(getKey());
        }

        @Override
        public String toString() {
            return s_;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SObject) {
                SObject that = (SObject) obj;
                return $.eq(that.getKey(), getKey()) && $.eq(that.asString().toString(), toString());
            }
            return false;
        }
    }

    static class FileSObject extends SObject {
        private File file_;
        private SoftReference<byte[]> cache;

        FileSObject(String key, File file) {
            super(key);
            E.NPE(file);
            file_ = file;
        }

        private synchronized byte[] read() {
            if (null != cache) {
                byte[] ba = cache.get();
                if (null != ba) return ba;
            }
            byte[] ba = IO.readContent(file_);
            cache = new SoftReference<byte[]>(ba);
            return ba;
        }

        @Override
        public long getLength() {
            return file_.length();
        }

        @Override
        public File asFile() throws UnexpectedIOException {
            return file_;
        }

        @Override
        public String asString() throws UnexpectedIOException {
            return asString(StandardCharsets.UTF_8);
        }

        @Override
        public String asString(Charset charset) throws UnexpectedIOException {
            return new String(read(), charset);
        }

        @Override
        public byte[] asByteArray() throws UnexpectedIOException {
            return read();
        }

        @Override
        public InputStream asInputStream() throws UnexpectedIOException {
            return IO.inputStream(file_);
        }

    }

    static class ByteArraySObject extends SObject {
        protected byte[] buf_;

        ByteArraySObject(String key, byte[] buf) {
            super(key);
            E.NPE(buf);
            buf_ = buf;
        }

        @Override
        public byte[] asByteArray() {
            int len = buf_.length;
            byte[] ba = new byte[len];
            System.arraycopy(buf_, 0, ba, 0, len);
            return ba;
        }

        @Override
        public File asFile() {
            File tmpFile = createTempFile(suffix());
            IO.write(buf_, tmpFile);
            return tmpFile;
        }

        @Override
        public InputStream asInputStream() {
            return IO.inputStream(buf_);
        }

        @Override
        public String asString() {
            return asString(StandardCharsets.UTF_8);
        }

        @Override
        public String asString(Charset charset) throws UnexpectedIOException {
            return new String(buf_, charset);
        }

        @Override
        public long getLength() {
            return buf_.length;
        }
    }

    static class InputStreamSObject extends SObject {
        private final InputStream is_;

        InputStreamSObject(String key, InputStream is) {
            super(key);
            E.NPE(is);
            this.is_ = is;
        }

        @Override
        public byte[] asByteArray() {
            return IO.readContent(is_);
        }

        @Override
        public File asFile() {
            File tmpFile = createTempFile(suffix());
            IO.write(is_, tmpFile);
            return tmpFile;
        }

        @Override
        public InputStream asInputStream() {
            return is_;
        }

        @Override
        public String asString() {
            return asString(StandardCharsets.UTF_8);
        }

        @Override
        public String asString(Charset charset) throws UnexpectedIOException {
            return new String(asByteArray(), charset);
        }

        @Override
        public long getLength() {
            throw E.unsupport();
        }
    }

    static class LazyLoadSObject extends SObject {
        private volatile ISObject sobj_;
        private IStorageService ss_;

        LazyLoadSObject(String key, IStorageService ss) {
            super(key);
            E.NPE(ss);
            ss_ = ss;
        }

        private ISObject force() {
            if (null == sobj_) {
                synchronized (this) {
                    if (null == sobj_) sobj_ = ss_.get(getKey());
                }
            }
            return sobj_;
        }

        @Override
        public long getLength() {
            return null == sobj_ ? -1 : sobj_.getLength();
        }

        @Override
        public File asFile() throws UnexpectedIOException {
            return force().asFile();
        }

        @Override
        public String asString() throws UnexpectedIOException {
            return force().asString();
        }

        @Override
        public String asString(Charset charset) throws UnexpectedIOException {
            return force().asString(charset);
        }

        @Override
        public byte[] asByteArray() throws UnexpectedIOException {
            return force().asByteArray();
        }

        @Override
        public InputStream asInputStream() throws UnexpectedIOException {
            return force().asInputStream();
        }
    }

    private static String randomKey() {
        return Codec.encodeUrl(S.random());
    }
}
