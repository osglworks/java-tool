/*
 * Copyright (C) 2013 The Java Tool project
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

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.storage.ISObject;
import org.osgl.storage.impl.SObject;
import org.w3c.dom.Document;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.stream.ImageInputStream;

/**
 * IO utilities
 */
// Some code come from Play!Framework IO.java, under Apache License 2.0
public class IO {

    /**
     * An `InputStreamHandler` provides logic to read an
     * {@link InputStream} into specified {@link Type}.
     *
     * If the read is successful then it returns an
     * instance of given type. Otherwise it raised
     * an {@link org.osgl.exception.UnexpectedException}.
     */
    public interface InputStreamHandler {
        /**
         * Report if the given `type` is supported by
         * the handler.
         *
         * @param type
         *      A type
         * @return
         *      `true` if the `type` specified is supported or
         *      `false` otherwise.
         */
        boolean support(Type type);

        /**
         * Read an {@link InputStream} and construct an instance
         * of `type`.
         *
         * @param is
         *      the input stream
         * @param targetType
         *      the type
         * @param hint
         *      the read hint
         * @param <T>
         *      the generic type
         * @return
         *      an instance of the type
         */
        <T> T read(InputStream is, Type targetType, MimeType mimeType, Object hint);
    }

    private static class InputStreamHandlerDispatcher implements InputStreamHandler {
        private List<InputStreamHandler> realHandlers = new ArrayList<>();
        private InputStreamHandler priotyHandler;
        void add(InputStreamHandler handler) {
            realHandlers.add(handler);
        }

        InputStreamHandlerDispatcher(InputStreamHandler h) {
            if (!realHandlers.contains($.requireNotNull(h))) {
                realHandlers.add(h);
            }
        }

        @Override
        public boolean support(Type type) {
            InputStreamHandler h = priotyHandler;
            if (null != h && h.support(type)) {
                return true;
            }
            for (InputStreamHandler h0: realHandlers) {
                if (h0.support(type)) {
                    priotyHandler = h0;
                    return true;
                }
            }
            return false;
        }

        @Override
        public <T> T read(InputStream is, Type targetType, MimeType mimeType, Object hint) {
            InputStreamHandler h = priotyHandler;
            if (null != h && h.support(targetType)) {
                return h.read(is, targetType, mimeType, hint);
            }
            for (InputStreamHandler h0: realHandlers) {
                if (h0.support(targetType)) {
                    priotyHandler = h0;
                    return h0.read(is, targetType, mimeType, hint);
                }
            }
            throw new UnsupportedOperationException("Type[%s] not supported");
        }
    }

    private static Map<MimeType, InputStreamHandlerDispatcher> inputStreamHandlerLookup = new HashMap<>();

    /**
     * Associate an {@link InputStreamHandler} with a specific {@link MimeType}
     * @param type
     *      The mimetype the handler listen to
     * @param handler
     *      the handler
     */
    public static void registerInputStreamHandler(MimeType type, InputStreamHandler handler) {
        $.requireNotNull(handler);
        InputStreamHandlerDispatcher dispatcher = inputStreamHandlerLookup.get(type);
        if (null == dispatcher) {
            dispatcher = new InputStreamHandlerDispatcher(handler);
            inputStreamHandlerLookup.put(type, dispatcher);
        } else {
            dispatcher.add(handler);
        }
    }

    /**
     * Register an {@link InputStreamHandler} with all {@link MimeType mime types} by {@link MimeType.Trait}.
     *
     * @param trait
     *      the trait of mimetype
     * @param handler
     *      the input stream handler
     */
    public static void registerInputStreamHandler(MimeType.Trait trait, InputStreamHandler handler) {
        $.requireNotNull(handler);
        List<MimeType> mimeTypes = MimeType.filterByTrait(trait);
        for (MimeType mimeType : mimeTypes) {
            registerInputStreamHandler(mimeType, handler);
        }
    }

    /**
     * A stage class support fluent IO write operations.
     *
     * Examples:
     *
     * ```java
     * IO.write(myFile).to(anotherFile);
     * IO.write("ABC").to(IO.file("abc.txt");
     * ```
     *
     * @param <STAGE>
     *         type parameter specify the implementation class
     */
    public static abstract class WriteStageBase<SOURCE, STAGE extends WriteStageBase> {

        protected SOURCE source;

        protected WriteStageBase(SOURCE source) {
            this.source = $.requireNotNull(source);
        }

        /**
         * Flag indicate if it shall close the target (output stream or writer)
         * after written the source content into it.
         */
        protected boolean closeSink;

        /**
         * Used when there are reader/inputstream or writer/outputstream conversion,
         */
        protected Charset charset = StandardCharsets.UTF_8;

        /**
         * Specify that it shall close the target (output stream or writer) once
         * the written operation finished.
         *
         * @return
         */
        public STAGE ensureCloseSink() {
            this.closeSink = true;
            return me();
        }

        /**
         * Specify the encoding {@link Charset} when there are inputstream/reader
         * or outputstream/writer conversion.
         *
         * @param charset
         *         the charset to be used to encode byte array into string
         * @return this write stage instance
         */
        public STAGE encoding(Charset charset) {
            this.charset = $.requireNotNull(charset);
            return me();
        }

        /**
         * Commit the write stage to a {@link Writer}.
         *
         * @param sink
         *         the target writer to which this write stage is committed.
         * @return the number of chars that has been written to the writer.
         */
        public int to(Writer sink) {
            try {
                return doWriteTo(sink);
            } catch (IOException e) {
                throw E.ioException(e);
            } finally {
                if (closeSink) {
                    close(sink);
                } else {
                    flush(sink);
                }
            }
        }

        /**
         * Commit this write stage to a {@link OutputStream}.
         *
         * @param sink
         *         the target output stream to which this write stage is committed.
         * @return the number of bytes that has been written to the output stream.
         */
        public int to(OutputStream sink) {
            try {
                return doWriteTo(sink);
            } catch (IOException e) {
                throw E.ioException(e);
            } finally {
                if (closeSink) {
                    close(sink);
                } else {
                    flush(sink);
                }
            }
        }

        /**
         * Commit this write stage into a {@link File}.
         *
         * @param file
         *         the target file to which this write stage is committed.
         * @return the number of bytes that has been written to the file.
         */
        public int to(File file) {
            BufferedOutputStream bos = buffered(outputStream(file));
            return to(bos);
        }

        /**
         * Sub class to implement the commit to a `Writer` logic.
         *
         * @param sink
         *         the writer target to which this write stage committed.
         * @return the number of chars written to the writer.
         * @throws IOException
         *         in case IOException encountered.
         */
        protected abstract int doWriteTo(Writer sink) throws IOException;

        /**
         * Sub class to implement the commit to a `OutputStream` logic.
         *
         * @param sink
         *         the output stream target to which this write stage committed.
         * @return the number of bytes written to the output stream.
         * @throws IOException
         *         in case IOException encountered.
         */
        protected abstract int doWriteTo(OutputStream sink) throws IOException;

        protected final STAGE me() {
            return (STAGE) this;
        }
    }

    /**
     * Implement a {@link WriteStageBase} for source of {@link CharSequence}.
     */
    public static class CharSequenceWriteStage extends WriteStageBase<CharSequence, CharSequenceWriteStage> {
        CharSequenceWriteStage(CharSequence csq) {
            super(csq);
        }

        public int doWriteTo(Writer sink) throws IOException {
            String s = source.toString();
            sink.write(s);
            return s.length();
        }

        @Override
        protected int doWriteTo(OutputStream sink) throws IOException {
            byte[] ba = source.toString().getBytes(charset);
            sink.write(ba);
            return ba.length;
        }
    }

    public static class BufferedImageWriteStage extends WriteStageBase<BufferedImage, BufferedImageWriteStage> {

        private String contentType = "image/png";

        protected BufferedImageWriteStage(BufferedImage bufferedImage) {
            super(bufferedImage);
        }

        protected BufferedImageWriteStage(BufferedImage image, String contentType) {
            super(image);
            this.contentType = S.requireNotBlank(contentType);
        }

        @Override
        protected int doWriteTo(Writer sink) {
            throw E.unsupport();
        }

        @Override
        protected int doWriteTo(OutputStream sink) {
            Img.source(source).writeTo(sink, contentType);
            return -1;
        }
    }

    public static class XMLDocumentWriteStage extends WriteStageBase<Document, XMLDocumentWriteStage> {

        private boolean pretty;

        protected XMLDocumentWriteStage(Document xmldoc) {
            super(xmldoc);
        }

        @Override
        protected int doWriteTo(Writer sink) {
            return doWriteTo($.convert(sink).to(OutputStream.class));
        }

        @Override
        protected int doWriteTo(OutputStream sink) {
            XML.print(source, pretty, sink);
            return -1;
        }

        public XMLDocumentWriteStage pretty() {
            this.pretty = true;
            return this;
        }
    }

    public static class ReaderWriteStage extends WriteStageBase<Reader, ReaderWriteStage> {
        boolean closeSource = true;
        boolean consumed;

        ReaderWriteStage(Reader source) {
            super(source);
        }

        ReaderWriteStage keepSourceOpen() {
            this.closeSource = false;
            return this;
        }

        @Override
        protected int doWriteTo(Writer sink) throws IOException {
            ensureNotConsumed();
            char[] buffer = new char[1024];
            int len, ttl = 0;
            try {
                while ((len = source.read(buffer)) >= 0) {
                    sink.write(buffer, 0, len);
                    ttl += len;
                }
                return ttl;
            } finally {
                if (closeSource) {
                    close(source);
                }
                consumed = true;
            }
        }

        @Override
        protected int doWriteTo(OutputStream sink) throws IOException {
            ensureNotConsumed();
            try {
                return new InputStreamWriteStage(new ReaderInputStream(source, charset)).doWriteTo(sink);
            } finally {
                consumed = true;
                if (closeSource) {
                    close(source);
                }
            }
        }

        private void ensureNotConsumed() {
            E.illegalStateIf(consumed, "Reader already consumed");
        }
    }

    public static class InputStreamWriteStage extends WriteStageBase<InputStream, InputStreamWriteStage> {
        boolean closeSource = true;
        boolean consumed;

        InputStreamWriteStage(InputStream source) {
            super(source);
        }

        InputStreamWriteStage keepSourceOpen() {
            this.closeSource = false;
            return this;
        }

        @Override
        protected int doWriteTo(OutputStream sink) throws IOException {
            ensureNotConsumed();
            try {
                int read, total = 0;
                byte[] buffer = new byte[8096];
                while ((read = source.read(buffer)) > -1) {
                    sink.write(buffer, 0, read);
                    total += read;
                }
                return total;
            } finally {
                consumed = true;
                if (closeSource) {
                    close(source);
                }
            }
        }

        @Override
        protected int doWriteTo(Writer sink) throws IOException {
            ensureNotConsumed();
            try {
                return new ReaderWriteStage(new InputStreamReader(source, charset)).doWriteTo(sink);
            } finally {
                consumed = true;
                if (closeSource) {
                    close(source);
                }
            }
        }

        private void ensureNotConsumed() {
            E.illegalStateIf(consumed, "Input stream already consumed");
        }

    }


    public static class FileWriteStage extends WriteStageBase<File, FileWriteStage> {

        FileWriteStage(File file) {
            super(file);
        }

        @Override
        protected int doWriteTo(Writer sink) throws IOException {
            return new ReaderWriteStage(buffered(reader(source))).doWriteTo(sink);
        }

        @Override
        protected int doWriteTo(OutputStream sink) throws IOException {
            return new InputStreamWriteStage(buffered(inputStream(source))).doWriteTo(sink);
        }
    }

    public static class UrlWriteStage extends WriteStageBase<URL, UrlWriteStage> {
        UrlWriteStage(URL url) {
            super(url);
        }

        @Override
        protected int doWriteTo(Writer sink) throws IOException {
            return new ReaderWriteStage(buffered(reader(source))).doWriteTo(sink);
        }

        @Override
        protected int doWriteTo(OutputStream sink) throws IOException {
            return new InputStreamWriteStage(buffered(is(source))).doWriteTo(sink);
        }
    }

    public static class SObjectWriteStage extends WriteStageBase<ISObject, SObjectWriteStage> {
        protected SObjectWriteStage(ISObject isObject) {
            super(isObject);
        }

        @Override
        protected int doWriteTo(Writer sink) throws IOException {
            try {
                return write(source.asInputStream()).doWriteTo(sink);
            } finally {
                if (closeSink) {
                    close(sink);
                }
            }
        }

        @Override
        protected int doWriteTo(OutputStream sink) throws IOException {
            try {
                return write(source.asInputStream()).doWriteTo(sink);
            } finally {
                if (closeSink) {
                    close(sink);
                }
            }
        }
    }

    public static abstract class ReadStageBase<SOURCE, STAGE extends ReadStageBase> {

        protected SOURCE source;
        protected String sourceName;
        private MimeType mimeType;
        protected Object hint;

        /**
         * Used when there are reader/inputstream or writer/outputstream conversion,
         */
        protected Charset charset = StandardCharsets.UTF_8;

        public ReadStageBase(SOURCE source) {
            this.source = $.requireNotNull(source);
        }

        /**
         * Specify the encoding {@link Charset} when there are inputstream/reader
         * or outputstream/writer conversion.
         *
         * @param charset
         *         the charset to be used to encode byte array into string
         * @return this write stage instance
         */
        public STAGE encoding(Charset charset) {
            this.charset = $.requireNotNull(charset);
            return me();
        }

        public STAGE hint(Object hint) {
            this.hint = hint;
            return me();
        }

        public STAGE sourceName(String name) {
            this.sourceName = name;
            if (null == mimeType) {
                mimeType = MimeType.findByFileExtension(S.fileExtension(sourceName));
            }
            return me();
        }

        public STAGE contentType(MimeType type) {
            this.mimeType = type;
            return me();
        }

        public STAGE contentType(String contentType) {
            MimeType type = MimeType.findByContentType(contentType);
            if (null == type) {
                type = MimeType.findByFileExtension(contentType);
            }
            if (null != type) {
                this.mimeType = type;
            } else {
                E.unexpected("Content type unrecognized: " + contentType);
            }
            return me();
        }

        public String toString() {
            return readContentAsString(toInputStream());
        }

        public List<String> toLines() {
            return readLines(toReader());
        }

        public List<String> toLine(int limit) {
            E.illegalArgumentIf(limit < 0);
            return readLines(toReader(), limit);
        }

        public ISObject toSObject() {
            return SObject.of(toInputStream());
        }

        public Reader toReader() {
            return new InputStreamReader(toInputStream(), charset);
        }

        public Properties toProperties() {
            return loadProperties(toInputStream());
        }

        public InputStream toInputStream() {
            try {
                return load();
            } catch (IOException e) {
                throw E.ioException(e);
            } finally {
                ensureCloseSource();
            }
        }

        public byte[] toByteArray() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copy(toInputStream(), baos);
            return baos.toByteArray();
        }

        public <T> T to(Class<T> type) {
            return _to(type);
        }

        public <T> T to(BeanInfo beanInfo) {
            return _to(beanInfo.type());
        }

        public <T> T to(TypeReference typeReference) {
            return _to(typeReference.getType());
        }

        private <T> T _to(Type type) {
            Object o;
            InputStreamHandlerDispatcher inputStreamHandler = null;
            MimeType mimeType = this.mimeType;
            if (null == mimeType) {
                String sourceName = sourceName();
                if (null != sourceName) {
                    String suffix = S.fileExtension(sourceName);
                    mimeType = MimeType.findByFileExtension(suffix);
                }
            }
            if (null != mimeType) {
                inputStreamHandler = inputStreamHandlerLookup.get(mimeType);
            }
            if (String.class == type) {
                o = toString();
            } else if (TypeReference.LIST_STRING == type) {
                if (null != inputStreamHandler && inputStreamHandler.support(type)) {
                    return inputStreamHandler.read(toInputStream(), type, mimeType, hint);
                }
                o = toLines();
            } else if (InputStream.class == type) {
                o = toInputStream();
            } else if (Reader.class == type) {
                o = toReader();
            } else if (ISObject.class == type || SObject.class == type) {
                o = toSObject();
            } else if (Properties.class == type) {
                if (null != inputStreamHandler && inputStreamHandler.support(type)) {
                    return inputStreamHandler.read(toInputStream(), type, mimeType, hint);
                }
                o = toProperties();
            } else if (byte[].class == type) {
                o = toByteArray();
            } else {
                if (null != inputStreamHandler) {
                    return inputStreamHandler.read(toInputStream(), type, mimeType, hint);
                }
                o = toUnknownType(type);
            }
            return $.cast(o);
        }

        protected abstract InputStream load() throws IOException;

        protected void ensureCloseSource() {
        }

        protected STAGE me() {
            return (STAGE) this;
        }

        protected <T> T toUnknownType(Type type) {
            if (null != mimeType) {
                InputStreamHandler h = inputStreamHandlerLookup.get(mimeType);
                if (null != h) {
                    return h.read(toInputStream(), type, mimeType, hint);
                }
            }
            throw new UnsupportedOperationException("target type not supported: " + type);
        }

        protected String sourceName() {
            return sourceName;
        }
    }

    public static class InputStreamReadStage extends ReadStageBase<InputStream, InputStreamReadStage> {


        public InputStreamReadStage(InputStream inputStream) {
            super(inputStream);
        }


        @Override
        protected InputStream load() {
            return source;
        }
    }

    public static class BufferedImageReadStage extends ReadStageBase<BufferedImage, BufferedImageReadStage> {
        private String contentType = "image/png";
        public BufferedImageReadStage(BufferedImage img) {
            super(img);
        }
        public BufferedImageReadStage(BufferedImage img, String contentType) {
            super(img);
            this.contentType = S.requireNotBlank(contentType);
        }

        @Override
        protected InputStream load() throws IOException {
            return inputStream(toByteArray());
        }

        @Override
        public byte[] toByteArray() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Img.source(source).writeTo(baos, contentType);
            return baos.toByteArray();
        }
    }

    public static class ReaderReadStage extends ReadStageBase<Reader, ReaderReadStage> {
        public ReaderReadStage(Reader reader) {
            super(reader);
        }

        @Override
        protected InputStream load() {
            return new ReaderInputStream(source, charset);
        }
    }

    public static class FileReadStage extends ReadStageBase<File, FileReadStage> {

        public FileReadStage(File file) {
            super(file);
            sourceName = file.getName();
        }

        @Override
        protected InputStream load() {
            return buffered(inputStream(source));
        }

    }

    public static class CharSequenceReadStage extends ReadStageBase<CharSequence, CharSequenceReadStage> {
        private String lineSep = OS.get().lineSeparator();

        public CharSequenceReadStage(CharSequence charSequence) {
            super(charSequence);
        }

        @Override
        protected InputStream load() throws IOException {
            return new ReaderInputStream(toReader(), charset);
        }

        @Override
        public Reader toReader() {
            return new StringReader(source.toString());
        }

        @Override
        public String toString() {
            return source.toString();
        }

        public CharSequenceReadStage lineSeparator(String lineSeparator) {
            this.lineSep = $.requireNotNull(lineSeparator);
            return this;
        }

        @Override
        public List<String> toLines() {
            return S.fastSplit(source.toString(), lineSep);
        }

        @Override
        public ISObject toSObject() {
            return SObject.of(source.toString());
        }

        @Override
        public byte[] toByteArray() {
            return source.toString().getBytes(charset);
        }
    }

    public static class SObjectReadStage extends ReadStageBase<ISObject, SObjectReadStage> {
        public SObjectReadStage(ISObject isObject) {
            super(isObject);
            sourceName = isObject.getFilename();
        }

        @Override
        protected InputStream load() {
            return source.asInputStream();
        }
    }

    public static class UrlReadStage extends ReadStageBase<URL, UrlReadStage> {
        public UrlReadStage(URL url) {
            super(url);
            sourceName(url.getPath());
        }

        @Override
        protected InputStream load() {
            return inputStream(source);
        }
    }

    public static class ByteBufferReadStage extends ReadStageBase<ByteBuffer, ByteBufferReadStage> {
        public ByteBufferReadStage(ByteBuffer buffer) {
            super(buffer);
        }

        @Override
        protected InputStream load() {
            return new ByteBufferInputStream(source);
        }
    }

    public static CharSequenceWriteStage write(CharSequence csq) {
        return new CharSequenceWriteStage(csq);
    }

    public static ReaderWriteStage write(Reader reader) {
        return new ReaderWriteStage(reader);
    }

    public static InputStreamWriteStage write(InputStream inputStream) {
        return new InputStreamWriteStage(inputStream);
    }

    public static InputStreamWriteStage write(byte[] bytes) {
        return write(new ByteArrayInputStream(bytes));
    }

    public static FileWriteStage write(File file) {
        return new FileWriteStage(file);
    }

    public static UrlWriteStage write(URL url) {
        return new UrlWriteStage(url);
    }

    public static BufferedImageWriteStage write(BufferedImage img) {
        return new BufferedImageWriteStage(img);
    }

    public static BufferedImageWriteStage write(BufferedImage img, String contentType) {
        return new BufferedImageWriteStage(img, contentType);
    }

    public static XMLDocumentWriteStage write(Document document) {
        return new XMLDocumentWriteStage(document);
    }

    public static InputStreamWriteStage write(ByteBuffer byteBuffer) {
        return write(new ByteBufferInputStream(byteBuffer));
    }

    public static SObjectWriteStage write(ISObject sobj) {
        return new SObjectWriteStage(sobj);
    }

    public static CharSequenceReadStage read(CharSequence csq) {
        return new CharSequenceReadStage(csq);
    }

    public static CharSequenceWriteStage write(char[] chars) {
        return write(FastStr.of(chars));
    }

    public static ReaderReadStage read(Reader reader) {
        return new ReaderReadStage(reader);
    }

    public static InputStreamReadStage read(InputStream inputStream) {
        return new InputStreamReadStage(inputStream);
    }

    public static SObjectReadStage read(ISObject sobj) {
        return new SObjectReadStage(sobj);
    }

    public static InputStreamReadStage read(byte[] bytes) {
        return read(new ByteArrayInputStream(bytes));
    }

    public static ByteBufferReadStage read(ByteBuffer byteBuffer) {
        return new ByteBufferReadStage(byteBuffer);
    }

    public static UrlReadStage read(URL url) {
        return new UrlReadStage(url);
    }

    public static FileReadStage read(File file) {
        return new FileReadStage(file);
    }

    public static BufferedImageReadStage read(BufferedImage image) {
        return new BufferedImageReadStage(image);
    }

    public static BufferedImageReadStage read(BufferedImage image, String contentType) {
        return new BufferedImageReadStage(image, contentType);
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            // ignore
        }
    }

    public static void flush(Flushable flushable) {
        try {
            flushable.flush();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    public static void flush(ObjectOutput oo) {
        try {
            oo.flush();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    public static void flush(ImageInputStream flushable) {
        if (null == flushable) {
            return;
        }
        try {
            flushable.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    public static File child(File file, String fn) {
        return new File(file, fn);
    }

    public static List<File> children(File file) {
        return Arrays.asList(file.listFiles());
    }

    public static File parent(File file) {
        return file.getParentFile();
    }

    public static File tmpFile() {
        return tmpFile(S.random(3), null, null);
    }

    public static File tmpFile(String prefix, String suffix) {
        return tmpFile(prefix, suffix, null);
    }

    public static File tmpFile(String prefix, String suffix, File dir) {
        if (null == prefix) {
            prefix = S.random(3);
        }
        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns a byte array output stream.
     *
     * This method is deprecated. Please use {@link #baos()} instead
     *
     * @return an output stream
     */
    @Deprecated
    public static OutputStream os() {
        return new ByteArrayOutputStream();
    }

    /**
     * Returns a byte array output stream
     *
     * @return an output stream
     */
    public static ByteArrayOutputStream baos() {
        return new ByteArrayOutputStream();
    }

    /**
     * Returns a file output stream
     *
     * @param file
     *         the file to which the returned output stream can be used to write to
     * @return an output stream that can be used to write to file specified
     */
    public static OutputStream outputStream(File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns a file output stream.
     *
     * Use {@link #outputStream(File)} to replace this method.
     *
     * @param file
     *         the file to which the returned output stream can be used to write to
     * @return an output stream that can be used to write to file specified
     */
    @Deprecated
    public static OutputStream os(File file) {
        return outputStream(file);
    }

    /**
     * Returns a string writer
     *
     * @return an writer that write to string
     */
    public static Writer writer() {
        return new StringWriter();
    }

    /**
     * Returns a file writer
     *
     * @param file
     *         the file to be written
     * @return a writer
     */
    public static Writer writer(File file) {
        try {
            return new FileWriter(file);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns an empty input stream
     *
     * @return an empty input stream
     */
    public static InputStream inputStream() {
        byte[] ba = {};
        return new ByteArrayInputStream(ba);
    }

    /**
     * Returns an empty input stream.
     *
     * Use {@link #inputStream()} to replace this method.
     *
     * @return an empty input stream
     */
    @Deprecated
    public static InputStream is() {
        return inputStream();
    }

    /**
     * Returns a file input stream
     *
     * @param file
     *         the file to be read
     * @return inputstream that read the file
     */
    public static InputStream inputStream(File file) {
        // workaround http://stackoverflow.com/questions/36880692/java-file-does-not-exists-but-file-getabsolutefile-exists
        if (!file.exists()) {
            file = file.getAbsoluteFile();
        }
        if (!file.exists()) {
            throw E.ioException("File does not exists: %s", file.getPath());
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns a file input stream.
     *
     * Use {@link #inputStream(File)} to replace this method
     *
     * @param file
     *         the file to be read
     * @return inputstream that read the file
     */
    @Deprecated
    public static InputStream is(File file) {
        return inputStream(file);
    }

    /**
     * Create an input stream from given byte array.
     *
     * @param ba
     *         the byte array
     * @return an input stream
     */
    public static InputStream inputStream(byte[] ba) {
        return new ByteArrayInputStream(ba);
    }

    /**
     * Use {@link #inputStream(byte[])} to replace this method
     *
     * @param ba
     * @return
     */
    @Deprecated
    public static InputStream is(byte[] ba) {
        return inputStream(ba);
    }

    /**
     * Create an input stream from string content which
     * will be encoded with UTF-8
     *
     * @param content
     *         the string content
     * @return an new inputstream
     */
    public static InputStream inputStream(String content) {
        return inputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns an input stream from a string which will be encoded with
     * CharSet.defaultCharset().
     *
     * use {@link #inputStream(String)} to replace this method.
     *
     * @param content
     *         the content to be read
     * @return input stream instance that read the content
     */
    @Deprecated
    public static InputStream is(String content) {
        return inputStream(content.getBytes());
    }

    /**
     * Create an input stream from a URL.
     *
     * @param url
     *         the URL.
     * @return the new inputstream.
     */
    public static InputStream inputStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Use {@link #inputStream(URL)} to replace this method.
     */
    @Deprecated
    public static InputStream is(URL url) {
        return inputStream(url);
    }

    /**
     * Returns an empty reader
     *
     * @return a reader that reads empty string ""
     */
    public static Reader reader() {
        return new StringReader("");
    }

    /**
     * Returns a file reader
     *
     * @param file
     *         the file to be read
     * @return a reader that reads the file specified
     */
    public static Reader reader(File file) {
        E.illegalArgumentIfNot(file.canRead(), "file not readable: " + file.getPath());
        try {
            return new FileReader(file);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    public static Reader reader(byte[] ba) {
        return new StringReader(new String(ba));
    }

    public static Reader reader(InputStream is) {
        return new InputStreamReader(is);
    }

    public static Reader reader(URL url) {
        try {
            return reader(url.openStream());
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns a string reader from a content specified
     *
     * @param content
     *         the content to be read
     * @return a string reader instance
     */
    public static Reader reader(String content) {
        return new StringReader(content);
    }

    public static BufferedOutputStream buffered(OutputStream os) {
        if (os instanceof BufferedOutputStream) {
            return (BufferedOutputStream) os;
        } else {
            return new BufferedOutputStream(os);
        }
    }

    public static BufferedInputStream buffered(InputStream is) {
        if (is instanceof BufferedInputStream) {
            return (BufferedInputStream) is;
        } else {
            return new BufferedInputStream(is);
        }
    }

    public static BufferedWriter buffered(Writer w) {
        if (w instanceof BufferedWriter) {
            return (BufferedWriter) w;
        } else {
            return new BufferedWriter(w);
        }
    }

    public static BufferedReader buffered(Reader r) {
        if (r instanceof BufferedReader) {
            return (BufferedReader) r;
        } else {
            return new BufferedReader(r);
        }
    }

    /**
     * Returns checksum of a file.
     *
     * @param file
     *         the file
     * @return the checksum of the file
     */
    public static String checksum(File file) {
        return checksum(inputStream(file));
    }

    /**
     * Returns checksum from an input stream.
     *
     * @param is
     *         the inputstream
     * @return the checksum of the content from the inputstream
     */
    public static String checksum(InputStream is) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] dataBytes = new byte[1024];

            int nread;

            while ((nread = is.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            S.Buffer sb = S.buffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw E.unexpected("SHA1 algorithm not found");
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    public static String checksum(byte[] ba) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(ba);
            byte[] mdbytes = md.digest();
            S.Buffer sb = S.buffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw E.unexpected("SHA1 algorithm not found");
        }
    }

    public static void delete(File file) {
        delete(file, false);
    }

    public static void delete(File f, boolean deleteChildren) {
        if (null == f || !f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            String[] sa = f.list();
            if (null != sa && sa.length > 0) {
                if (!deleteChildren) {
                    return;
                } else {
                    for (File f0 : f.listFiles()) {
                        delete(f0, true);
                    }
                }
            }
        }
        if (!f.delete()) {
            f.deleteOnExit();
        }
    }

    /**
     * Load properties from a URL
     *
     * @param url the URL of the properties file
     * @return
     *      the properties contains the content of the URL or an empty properties
     *      if the URL is invalid or null
     */
    public static Properties loadProperties(URL url) {
        if (null == url) {
            return new Properties();
        }
        return loadProperties(inputStream(url));
    }

    /**
     * Load properties from a file
     *
     * @param file
     *         the properties file
     * @return the properties loaded from the file specified
     */
    public static Properties loadProperties(File file) {
        return loadProperties(IO.inputStream(file));
    }

    /**
     * Load properties from an inputStream
     *
     * @param inputStream
     *         the input stream to property source
     * @return the properties loaded from the input stream specified
     */
    public static Properties loadProperties(InputStream inputStream) {
        Properties prop = new Properties();
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            IO.close(inputStream);
        }
        return prop;
    }

    /**
     * Load properties from an inputStream
     *
     * @param reader
     *         the reader to property source
     * @return the properties loaded from the reader specified
     */
    public static Properties loadProperties(Reader reader) {
        Properties prop = new Properties();
        try {
            prop.load(reader);
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            IO.close(reader);
        }
        return prop;
    }

    /**
     * Load properties from a string content
     *
     * @param content
     *         the content of a properties file
     * @return the properties loaded
     */
    public static Properties loadProperties(String content) {
        return loadProperties(new StringReader(content));
    }

    /**
     * Read binary content of a file (warning does not use on large file !)
     *
     * @param file
     *         The file te read
     * @return The binary data
     */
    public static byte[] readContent(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            copy(new BufferedInputStream(new FileInputStream(file)), baos);
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
        return baos.toByteArray();
    }

    public static byte[] readContent(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(is, baos);
        return baos.toByteArray();
    }

    /**
     * Read file content to a String (always use utf-8)
     *
     * @param file
     *         The file to read
     * @return The String content
     */
    public static String readContentAsString(File file) {
        return readContentAsString(file, "utf-8");
    }

    /**
     * Read file content to a String
     *
     * @param url
     *         The url resource to read
     * @param encoding
     *         encoding used to read the file into string content
     * @return The String content
     */
    public static String readContentAsString(URL url, String encoding) {
        try {
            return readContentAsString(url.openStream(), encoding);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Read file content to a String (always use utf-8)
     *
     * @param url
     *         the url resource to read
     * @return The String content
     */
    public static String readContentAsString(URL url) {
        return readContentAsString(url, "utf-8");
    }

    /**
     * Read file content to a String
     *
     * @param file
     *         The file to read
     * @param encoding
     *         encoding used to read the file into string content
     * @return The String content
     */
    public static String readContentAsString(File file, String encoding) {
        try {
            return readContentAsString(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    public static String readContentAsString(InputStream is) {
        return readContentAsString(is, "utf-8");
    }

    public static String readContentAsString(InputStream is, String encoding) {
        try {
            StringWriter result = new StringWriter();
            PrintWriter out = new PrintWriter(result);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                if (lineNo++ > 0) out.println();
                out.print(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            close(is);
        }
    }

    public static List<String> readLines(File file) {
        return readLines(file, 0);
    }

    public static List<String> readLines(File file, int limit) {
        return readLines(file, "utf-8", limit);
    }

    public static List<String> readLines(File file, String encoding) {
        return readLines(file, encoding, 0);
    }

    public static List<String> readLines(File file, String encoding, int limit) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return readLines(is, encoding, limit);
        } catch (IOException ex) {
            throw E.ioException(ex);
        } finally {
            close(is);
        }
    }

    public static List<String> readLines(InputStream is, String encoding) {
        return readLines(is, encoding, 0);
    }

    public static List<String> readLines(InputStream is, String encoding, int limit) {
        if (encoding == null) {
            return readLines(is, limit);
        } else {
            InputStreamReader r;
            try {
                r = new InputStreamReader(is, encoding);
            } catch (UnsupportedEncodingException e) {
                throw E.encodingException(e);
            }
            return readLines(r, limit);
        }
    }

    public static List<String> readLines(InputStream inputStream) {
        return readLines(inputStream, 0);
    }

    public static List<String> readLines(InputStream inputStream, int limit) {
        InputStreamReader r = new InputStreamReader(inputStream);
        return readLines(r, limit);
    }

    public static List<String> readLines(Reader input) {
        return readLines(input, 0);
    }

    public static List<String> readLines(Reader input, int limit) {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        if (limit < 1) {
            limit = Integer.MAX_VALUE;
        }
        try {
            int n = 0;
            String line = reader.readLine();
            while ((n++ < limit) && line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        return list;
    }

    public static List<String> readLines(URL url) {
        return readLines(url, 0);
    }

    public static List<String> readLines(URL url, int limit) {
        try {
            return readLines(url.openStream(), limit);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    public static List<String> readLines(URL url, String encode) {
        return readLines(url, encode, 0);
    }

    public static List<String> readLines(URL url, String encode, int limit) {
        try {
            return readLines(url.openStream(), encode, limit);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Write String content to a file (always use utf-8).
     *
     * This method is deprecated. Please use {@link #write(CharSequence, File)} instead
     *
     * @param content
     *         The content to write
     * @param file
     *         The file to write
     */
    @Deprecated
    public static void writeContent(CharSequence content, File file) {
        write(content, file, "utf-8");
    }

    /**
     * Write string content to a file with encoding specified.
     *
     * This is deprecated. Please use {@link #write(CharSequence, File, String)} instead
     */
    @Deprecated
    public static void writeContent(CharSequence content, File file, String encoding) {
        write(content, file, encoding);
    }

    /**
     * Write content into a writer.
     *
     * This method is deprecated. Please use {@link #write(CharSequence, Writer)} instead.
     */
    @Deprecated
    public static void writeContent(CharSequence content, Writer writer) {
        write(content, writer);
    }

    /**
     * Write string content to a file with UTF-8 encoding.
     *
     * @param content
     *         the content to be written to the file
     * @param file
     *         the file to which the content be written to
     */
    public static void write(CharSequence content, File file) {
        write(content, file, "utf-8");
    }

    /**
     * Write String content to a file with encoding specified
     *
     * @param content
     *         The content to write
     * @param file
     *         The file to write
     * @param encoding
     *         encoding used to write the content to file
     */
    public static void write(CharSequence content, File file, String encoding) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os, encoding));
            printWriter.print(content);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            throw E.unexpected(e);
        } finally {
            close(os);
        }
    }

    /**
     * Write a character to a {@link Writer}.
     *
     * The writer is leave open after the character after writing.
     *
     * @param c
     *      the character to be written
     * @param writer
     *      the writer to which the character is written
     */
    public static void write(char c, Writer writer) {
        try {
            writer.write(c);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Alias of {@link #write(char, Writer)}
     */
    public static void append(char c, Writer writer) {
        write(c, writer);
    }

    /**
     * write content into a writer without closing the writer when finished.
     *
     * @param content
     *         the content to be written to the writer
     * @param writer
     *         to where the content be written
     */
    public static void append(CharSequence content, Writer writer) {
        write(content, writer, false);
    }

    /**
     * Write content into a writer and close the writer.
     *
     * @param content
     *         the content to be written to the writer
     * @param writer
     *         to where the content be written
     */
    public static void write(CharSequence content, Writer writer) {
        write(content, writer, true);
    }

    /**
     * Write content into a writer.
     *
     * @param content
     *         the content to be written to the writer
     * @param writer
     *         to where the content be written
     */
    public static void write(CharSequence content, Writer writer, boolean closeOs) {
        try {
            writer.write(content.toString());
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            if (closeOs) {
                close(writer);
            }
        }
    }

    /**
     * Copy content from input stream to output stream without closing the output stream.
     *
     * The input stream is closed anyway.
     *
     * @param is
     *         input stream
     * @param os
     *         output stream
     * @return number of bytes appended
     */
    public static int append(InputStream is, OutputStream os) {
        return copy(is, os, false);
    }

    /**
     * Read from InputStream `is` and write to OutputStream `os`.
     *
     * After writing both input stream and output stream are closed.
     *
     * @param is
     *      The input stream
     * @param os
     *      The output stream
     * @return
     *      the number of bytes copied
     */
    public static int copy(InputStream is, OutputStream os) {
        return copy(is, os, true);
    }

    /**
     * Alias of {@link #copy(java.io.InputStream, java.io.OutputStream)}
     */
    public static int write(InputStream is, OutputStream os) {
        return copy(is, os);
    }

    /**
     * Copy an stream to another one. It close the input stream anyway.
     *
     * If the param closeOs is true then close the output stream.
     *
     * @param is
     *         input stream
     * @param os
     *         output stream
     * @param closeOs
     *         specify whether it shall close output stream after operation
     * @return number of bytes copied
     */
    public static int copy(InputStream is, OutputStream os, boolean closeOs) {
        if (closeOs) {
            return write(is).ensureCloseSink().to(os);
        } else {
            return write(is).to(os);
        }
    }

    /**
     * Alias of {@link #copy(InputStream, OutputStream, boolean)}
     */
    public static int write(InputStream is, OutputStream os, boolean closeSink) {
        return copy(is, os, closeSink);
    }

    /**
     * Read from inputstream and write into file.
     * @param is
     *      the inputstream
     * @param f
     *      the file
     * @return
     *      the number of bytes written to the file
     */
    public static int write(InputStream is, File f) {
        try {
            return copy(is, new BufferedOutputStream(new FileOutputStream(f)));
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Copy from a `Reader` into a `Writer`.
     *
     * Both reader and writer are closed.
     *
     * @param reader
     *         A reader - the source
     * @param writer
     *         a writer - the target
     * @return the number of chars copied
     */
    public static int copy(Reader reader, Writer writer) {
        return copy(reader, writer, true);
    }

    /**
     * Copy from a `Reader` into a `Writer`.
     *
     * @param reader
     *         A reader - the source
     * @param writer
     *         a writer - the target
     * @param closeWriter
     *         indicate if it shall close the writer after operation
     * @return the number of chars copied
     */
    public static int copy(Reader reader, Writer writer, boolean closeWriter) {
        if (closeWriter) {
            return write(reader).ensureCloseSink().to(writer);
        } else {
            return write(reader).to(writer);
        }
    }

    /**
     * Write a byte into outputstream.
     *
     * The outputstream is not closed after written.
     *
     * @param b
     *      the byte to be written
     * @param os
     *      the output stream into which the byte is written
     */
    public static void write(byte b, OutputStream os) {
        try {
            os.write(b);
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Alias of {@link #write(byte, OutputStream)}.
     */
    public static void append(byte b, OutputStream outputStream) {
        write(b, outputStream);
    }

    /**
     * Write binary data to a file
     *
     * @param data
     *         The binary data to write
     * @param file
     *         The file to write
     */
    public static void write(byte[] data, File file) {
        try {
            write(new ByteArrayInputStream(data), new BufferedOutputStream(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Write binary data to an outputstream and then close the outputstream
     *
     * @param data
     *         the binary data to write
     * @param os
     *         the output stream
     */
    public static void write(byte[] data, OutputStream os) {
        write(data, os, true);
    }

    /**
     * Write binary data to an output stream without closing the outputstream.
     *
     * @param data
     *      the binary data to be written.
     * @param os
     *      the output stream to which the binary data is written
     */
    public static void append(byte[] data, OutputStream os) {
        write(data, os, false);
    }

    /**
     * Write binary data to an outputstream.
     *
     * @param data
     *      the binary data to write
     * @param os
     *      the output stream
     * @param closeSink
     *      if `true` then close the output stream once finished writing
     */
    public static void write(byte[] data, OutputStream os, boolean closeSink) {
        try {
            os.write(data);
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            if (closeSink) {
                close(os);
            }
        }
    }

    /**
     * Alias of {@link #copy(Reader, Writer)}
     */
    public static int write(Reader reader, Writer writer) {
        return copy(reader, writer);
    }

    /**
     * Alias of {@link #copy(Reader, Writer, boolean)}
     */
    public static int write(Reader reader, Writer writer, boolean closeWriter) {
        return copy(reader, writer, closeWriter);
    }


    // If target does not exist, it will be created.
    public static void copyDirectory(File source, File target) {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }
            for (String child : source.list()) {
                copyDirectory(new File(source, child), new File(target, child));
            }
        } else {
            try {
                write(new FileInputStream(source), new FileOutputStream(target));
            } catch (IOException e) {
                if (target.isDirectory()) {
                    if (!target.exists()) {
                        if (!target.mkdirs()) {
                            throw E.ioException("cannot copy [%s] to [%s]", source, target);
                        }
                    }
                    target = new File(target, source.getName());
                } else {
                    File targetFolder = target.getParentFile();
                    if (!targetFolder.exists()) {
                        if (!targetFolder.mkdirs()) {
                            throw E.ioException("cannot copy [%s] to [%s]", source, target);
                        }
                    }
                }
                try {
                    write(new FileInputStream(source), new FileOutputStream(target));
                } catch (IOException e0) {
                    throw E.ioException(e0);
                }
            }
        }
    }

    /**
     * Zip a list of sobject into a single sobject.
     *
     * @param objects
     *         the sobjects to be zipped.
     * @return an sobject that is a zip package of `objects`.
     */
    public static ISObject zip(ISObject... objects) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        try {
            for (ISObject obj : objects) {
                ZipEntry entry = new ZipEntry(obj.getAttribute(SObject.ATTR_FILE_NAME));
                InputStream is = obj.asInputStream();
                zos.putNextEntry(entry);
                copy(is, zos, false);
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            close(zos);
        }
        return SObject.of(Codec.encodeUrl(S.random()), baos.toByteArray());
    }

    /**
     * Zip a list of files into a single file. The name of the zip file
     * is randomly picked up in the temp dir.
     *
     * @param files
     *         the files to be zipped.
     * @return a file that is a zip package of the `files`
     */
    public static File zip(File... files) {
        try {
            File temp = File.createTempFile("osgl", ".zip");
            zipInto(temp, files);
            return temp;
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Zip a list of files into specified target file.
     *
     * @param target
     *         the target file as the zip package
     * @param files
     *         the files to be zipped.
     */
    public static void zipInto(File target, File... files) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
            byte[] buffer = new byte[128];
            for (File f : files) {
                ZipEntry entry = new ZipEntry(f.getName());
                InputStream is = new BufferedInputStream(new FileInputStream(f));
                zos.putNextEntry(entry);
                int read = 0;
                while ((read = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
                IO.close(is);
            }
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            IO.close(zos);
        }
    }

    public static final class F {
        public static <T> $.Function<?, T> println() {
            return PRINTLN;
        }

        public static $.Function PRINTLN = print("", "\n", System.out);

        public static <T> $.Function<?, T> print() {
            return PRINT;
        }

        public static $.Function PRINT = print("", "", System.out);

        public static <T> $.Function<T, ?> print(String prefix, String suffix) {
            return print(prefix, suffix, System.out);
        }

        public static <T> $.Function<T, ?> print(String prefix, String suffix, PrintStream ps) {
            return new $.F4<T, String, String, PrintStream, Void>() {
                @Override
                public Void apply(T t, String prefix, String suffix, PrintStream ps) {
                    StringBuilder sb = new StringBuilder(prefix).append(t).append(suffix);
                    ps.print(sb);
                    return null;
                }
            }.curry(prefix, suffix, ps);
        }

        public static final $.Function<File, InputStream> FILE_TO_IS = new $.F1<File, InputStream>() {
            @Override
            public InputStream apply(File file) throws NotAppliedException, $.Break {
                try {
                    return new BufferedInputStream(new FileInputStream(file));
                } catch (IOException e) {
                    throw E.ioException(e);
                }
            }
        };
    }
}
