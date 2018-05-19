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
package org.osgl.storage;

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
import osgl.version.Version;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Represent an item stored in an <code>IStorageService</code>
 *
 * @author greenl
 */
public interface ISObject extends Serializable {


    Version VERSION = Version.of(ISObject.class);


    /**
     * A standard attribute: content-type
     */
    public static final String ATTR_CONTENT_TYPE = "content-type";

    /**
     * A standard attribute: filename
     */
    public static final String ATTR_FILE_NAME = "filename";

    /**
     * The storage service ID
     */
    public static final String ATTR_SS_ID = "ss_id";

    /**
     * The storage service context path
     */
    public static final String ATTR_SS_CTX = "ss_ctx";

    /**
     * Store the URL point to this sobject
     */
    public static final String ATTR_URL = "url";

    /**
     * Store the content length
     */
    public static final String ATTR_CONTENT_LENGTH = "length";

    /**
     * @return key of this object
     */
    String getKey();

    /**
     * @return length of the object
     */
    long getLength();

    /**
     * Returns URL set to this SObject.
     *
     * <p>
     *     Calling to this method shall have the same result as
     *     calling {@link #getAttribute(String)} using
     *     {@link ISObject#ATTR_URL}:
     * </p>
     *
     * <pre><code>
     *     String url = sobj.getAttribute(ISObject.ATTR_URL);
     * </code></pre>
     * @return the url to this SObject
     */
    String getUrl();

    /**
     * Returns {@link #ATTR_FILE_NAME filename} attribute
     * @return filename
     */
    String getFilename();

    /**
     * Return {@link #ATTR_CONTENT_TYPE content type} attribute
     * @return content type
     */
    String getContentType();

    /**
     * Set {@link #ATTR_FILE_NAME filename} attribute
     * @param filename the filename to be set
     */
    void setFilename(String filename);

    /**
     * Set {@link #ATTR_CONTENT_TYPE content type} attribute
     * @param contentType the content type to be set
     */
    void setContentType(String contentType);

    /**
     * Return attribute associated with this storage object by key. If there is
     * no such attribute found then <code>null</code> is returned
     *
     * @return the attribute if found or <code>null</code> if not found
     */
    String getAttribute(String key);

    /**
     * Set an attribute to the storage object associated by key specified.
     *
     * @param key attribute key
     * @param val attribute value
     */
    ISObject setAttribute(String key, String val);

    /**
     * Set attributes to the storage object
     *
     * @param attrs
     * @return the object with attributes set
     */
    ISObject setAttributes(Map<String, String> attrs);

    /**
     * @return <code>true</code> if the storage object has attributes
     */
    boolean hasAttribute();

    /**
     * @return a copy of attributes of this storage object
     */
    Map<String, String> getAttributes();

    /**
     * Is content is empty
     *
     * @return if the instance is empty
     */
    public boolean isEmpty();

    /**
     * Is this storage object valid. A storage object is not valid
     * if the file/input stream is not readable
     *
     * @return true if this instance is valid or false otherwise
     */
    public boolean isValid();

    /**
     * Return previous exception that cause the sobject invalid
     *
     * @return the previous exception
     */
    public Throwable getException();

    /**
     * @return the the stuff content as an file
     */
    File asFile() throws UnexpectedIOException;

    /**
     * @return the stuff content as a string
     */
    String asString() throws UnexpectedIOException;

    /**
     * @return the stuff content as a string using the charset to encode
     */
    String asString(Charset charset) throws UnexpectedIOException;

    /**
     * @return the stuff content as a byte array
     */
    byte[] asByteArray() throws UnexpectedIOException;

    /**
     * Returns an {@link InputStream} connect to the sobject. Note it is
     * caller's responsibility to close the inputStream
     *
     * @return the stuff content as an input stream
     */
    InputStream asInputStream() throws UnexpectedIOException;

    /**
     * Consume the inputstream of this storage object one time and then close the input stream
     *
     * @param consumer the consumer function
     */
    void consumeOnce($.Function<InputStream, ?> consumer) throws UnexpectedIOException;

    /**
     * Returns {@code true} if this SObject is dumb
     *
     * @return {@code true} if this instance is dumb
     */
    boolean isDumb();

}
