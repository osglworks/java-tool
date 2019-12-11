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
import org.osgl.Lang;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URL;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XML {

    public static final Object HINT_PRETTY = new Object();

    private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    private static final ThreadLocal<DocumentBuilder> builder = new ThreadLocal<DocumentBuilder>() {
        @Override
        protected DocumentBuilder initialValue() {
            try {
                return builderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw E.invalidConfiguration(e, "error getting DocumentBuilder");
            }
        }
    };

    private static final ThreadLocal<TransformerFactory> transformerFactory = new ThreadLocal<TransformerFactory>() {
        @Override
        protected TransformerFactory initialValue() {
            return TransformerFactory.newInstance();
        }
    };

    private static final ThreadLocal<Transformer> transformer = new ThreadLocal<Transformer>() {
        @Override
        protected Transformer initialValue() {
            try {
                return transformerFactory.get().newTransformer();
            } catch (Exception e) {
                throw E.unexpected(e);
            }
        }
    };

    public static void print(Document document, OutputStream os) {
        print(document, false, os);
    }

    public static void print(Document document, boolean pretty, OutputStream os) {
        Transformer t = transformer.get();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        if (pretty) {
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        }
        try {
            t.transform(new DOMSource(document), new StreamResult(os));
        } catch (TransformerException e) {
            throw E.unexpected(e);
        } finally {
            t.reset();
        }
    }

    public static String toString(Document document, boolean pretty) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(document, pretty, os);
        byte[] ba = os.toByteArray();
        return $.convert(ba).toString();
    }

    public static String toString(Document document) {
        return toString(document, false);
    }

    public static Document read(String content) {
        DocumentBuilder b = builder.get();
        try {
            return b.parse(IO.inputStream(content));
        } catch (Exception e) {
            throw E.unexpected(e);
        } finally {
            b.reset();
        }
    }

    public static Document read(InputStream is) {
        DocumentBuilder b = builder.get();
        try {
            return b.parse(is);
        } catch (Exception e) {
            throw E.unexpected(e);
        } finally {
            b.reset();
        }
    }

    public static Document read(Reader reader) {
        DocumentBuilder b = builder.get();
        try {
            return b.parse($.convert(reader).to(InputStream.class));
        } catch (Exception e) {
            throw E.unexpected(e);
        } finally {
            b.reset();
        }
    }

    public static Document read(URL url) {
        DocumentBuilder b = builder.get();
        try {
            return b.parse(IO.inputStream(url));
        } catch (Exception e) {
            throw E.unexpected(e);
        } finally {
            b.reset();
        }
    }

    public static Document read(File file) {
        DocumentBuilder b = builder.get();
        try {
            return b.parse(file);
        } catch (Exception e) {
            throw E.unexpected(e);
        } finally {
            b.reset();
        }
    }

    public static final Lang.TypeConverter<String, Document> STRING_TO_XML_DOCUMENT = Lang.TypeConverter.STRING_TO_XML_DOCUMENT;

    public static final Lang.TypeConverter<InputStream, Document> IS_TO_XML_DOCUMENT = Lang.TypeConverter.IS_TO_XML_DOCUMENT;

    public static final Lang.TypeConverter<Document, String> XML_DOCUMENT_TO_STRING = Lang.TypeConverter.XML_DOCUMENT_TO_STRING;

    public static void init() {}

}
