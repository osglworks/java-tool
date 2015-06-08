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

import org.osgl._;
import org.osgl.exception.NotAppliedException;
import org.osgl.storage.ISObject;
import org.osgl.storage.impl.SObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * IO utilities
 */
// Some code come from Play!Framework IO.java, under Apache License 2.0
public class IO {

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            //
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
     * Returns a byte array output stream
     */
    public static OutputStream os() {
        return new ByteArrayOutputStream();
    }

    /**
     * Returns a file output stream
     */
    public static OutputStream os(File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns a string writer
     */
    public static Writer writer() {
        return new StringWriter();
    }

    /**
     * Returns a file writer
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
     */
    public static InputStream is() {
        byte[] ba = {};
        return new ByteArrayInputStream(ba);
    }

    /**
     * Returns a file input stream
     */
    public static InputStream is(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    public static InputStream is(byte[] ba) {
        return new ByteArrayInputStream(ba);
    }

    /**
     * Returns an input stream from a string which will be encoded with
     * CharSet.defaultCharset()
     */
    public static InputStream is(String content) {
        return is(content.getBytes());
    }

    public static InputStream is(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Returns an empty reader
     */
    public static Reader reader() {
        return new StringReader("");
    }

    /**
     * Returns a file reader
     */
    public static Reader reader(File file) {
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
     * Returns a string reader
     */
    public static Reader reader(String content) {
        return new StringReader(content);
    }

    public static BufferedOutputStream buffered(OutputStream os) {
        if (os instanceof BufferedOutputStream) {
            return (BufferedOutputStream)os;
        } else {
            return new BufferedOutputStream(os);
        }
    }

    public static BufferedInputStream buffered(InputStream is) {
        if (is instanceof BufferedInputStream) {
            return (BufferedInputStream)is;
        } else {
            return new BufferedInputStream(is);
        }
    }

    public static BufferedWriter buffered(Writer w) {
        if (w instanceof BufferedWriter) {
            return (BufferedWriter)w;
        } else {
            return new BufferedWriter(w);
        }
    }

    public static BufferedReader buffered(Reader r) {
        if (r instanceof BufferedReader) {
            return (BufferedReader)r;
        } else {
            return new BufferedReader(r);
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
     * Read binary content of a file (warning does not use on large file !)
     * @param file The file te read
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
     * @param file The file to read
     * @return The String content
     */
    public static String readContentAsString(File file) {
        return readContentAsString(file, "utf-8");
    }

    /**
     * Read file content to a String
     *
     * @param url The url resource to read
     * @return The String content
     */
    public static String readContentAsString(URL url, String encoding) {
        try {
            return readContentAsString(url.openStream());
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Read file content to a String (always use utf-8)
     *
     * @param url the url resource to read
     * @return The String content
     */
    public static String readContentAsString(URL url) {
        return readContentAsString(url, "utf-8");
    }

    /**
     * Read file content to a String
     *
     * @param file The file to read
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
        return readLines(file, "utf-8");
    }


    public static List<String> readLines(File file, String encoding) {
        List<String> lines = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            lines = readLines(is, encoding);
        } catch (IOException ex) {
            throw E.ioException(ex);
        } finally {
            close(is);
        }
        return lines;
    }

    public static List<String> readLines(InputStream is, String encoding) {
        if (encoding == null) {
            return readLines(is);
        } else {
            InputStreamReader r = null;
            try {
                r = new InputStreamReader(is, encoding);
            } catch (UnsupportedEncodingException e) {
                throw E.encodingException(e);
            }
            return readLines(r);
        }
    }

    public static List<String> readLines(InputStream inputStream) {
        InputStreamReader r = new InputStreamReader(inputStream);
        return readLines(r);
    }

    public static List<String> readLines(Reader input) {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        try {
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        return list;
    }

    /**
     * Write String content to a file (always use utf-8)
     *
     * @param content The content to write
     * @param file    The file to write
     */
    public static void writeContent(CharSequence content, File file) {
        writeContent(content, file, "utf-8");
    }

    /**
     * Write String content to a file (always use utf-8)
     *
     * @param content The content to write
     * @param file    The file to write
     */
    public static void writeContent(CharSequence content, File file, String encoding) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os, encoding));
            printWriter.println(content);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            throw E.unexpected(e);
        } finally {
            close(os);
        }
    }

    public static void writeContent(CharSequence content, Writer writer) {
        try {
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.println(content);
            printWriter.flush();
            writer.flush();
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            close(writer);
        }
    }

    /**
     * Copy content from input stream to output stream without closing the output stream
     * 
     * @param is
     * @param os
     */
    public static void append(InputStream is, OutputStream os) {
        copy(is, os, false);
    }
    
    public static void copy(InputStream is, OutputStream os) {
        copy(is, os, true);
    }

    /**
     * Copy an stream to another one. It close the input stream anyway.
     *
     * If the param closeOs is true then close the output stream
     */
    public static void copy(InputStream is, OutputStream os, boolean closeOs) {
        try {
            int read;
            byte[] buffer = new byte[8096];
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
        } catch(IOException e) {
            throw E.ioException(e);
        } finally {
            close(is);
            if (closeOs) {
                close(os);
            }
        }
    }

    /**
     * Alias of {@link #copy(java.io.InputStream, java.io.OutputStream)}
     * @param is
     * @param os
     */
    public static void write(InputStream is, OutputStream os) {
        copy(is, os);
    }
    
    public static void write(InputStream is, File f) {
        try {
            copy(is, new BufferedOutputStream(new FileOutputStream(f)));
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Write binary data to a file
     * @param data The binary data to write
     * @param file The file to write
     */
    public static void write(byte[] data, File file) {
        try {
            write(new ByteArrayInputStream(data), new BufferedOutputStream(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            throw E.ioException(e);
        }
    }

    /**
     * Write binary data to an output steam
     * @param data the binary data to write
     * @param os the output stream
     */
    public static void write(byte[] data, OutputStream os) {
        write(new ByteArrayInputStream(data), os);
    }

    // If target does not exist, it will be created.
    public static void copyDirectory(File source, File target) {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }
            for (String child: source.list()) {
                copyDirectory(new File(source, child), new File(target, child));
            }
        } else {
            try {
                write(new FileInputStream(source),  new FileOutputStream(target));
            } catch (IOException e) {
                throw E.ioException(e);
            }
        }
    }

    public static ISObject zip(ISObject... objects) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        try {
            for (ISObject obj: objects) {
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
        return SObject.of(baos.toByteArray());
    }

    public static File zip(File... files) {
        try {
            File temp = File.createTempFile("osgl", ".zip");
            zipInto(temp, files);
            return temp;
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

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
        public static <T> _.Function<?, T> println() {
            return PRINTLN;
        }
        
        public static _.Function PRINTLN = print("", "\n", System.out);

        public static <T> _.Function<?, T> print() {
            return PRINT;
        }
        
        public static _.Function PRINT = print("", "", System.out);
        
        public static <T> _.Function<T, ?> print(String prefix, String suffix) {
            return print(prefix, suffix, System.out);
        }
        
        public static <T> _.Function<T, ?> print(String prefix, String suffix, PrintStream ps) {
            return new _.F4<T, String, String, PrintStream, Void>() {
                @Override
                public Void apply(T t, String prefix, String suffix, PrintStream ps) {
                    StringBuilder sb = new StringBuilder(prefix).append(t).append(suffix);
                    ps.print(sb);
                    return null;
                }
            }.curry(prefix, suffix, ps);
        }

        public static final _.Function<File, InputStream> FILE_TO_IS = new _.F1<File, InputStream>() {
            @Override
            public InputStream apply(File file) throws NotAppliedException, _.Break {
                try {
                    return new BufferedInputStream(new FileInputStream(file));
                } catch (IOException e) {
                    throw E.ioException(e);
                }
            }
        };
    }
}
