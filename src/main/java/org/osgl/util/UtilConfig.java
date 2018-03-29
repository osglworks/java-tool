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

public class UtilConfig {

    public static void setThreadLocalBufferLimit(int limit) {
        setThreadLocalByteArrayBufferLimit(limit);
        setThreadLocalCharBufferLimit(limit);
    }

    public static void setThreadLocalCharBufferLimit(int limit) {
        if (limit < 256) {
            limit = 256;
        } else if (limit < S.BUFFER_INIT_SIZE) {
            limit = S.BUFFER_INIT_SIZE;
        }
        S.BUFFER_RETENTION_LIMIT = limit;
    }

    public static int getThreadLocalCharBufferLimit() {
        return S.BUFFER_RETENTION_LIMIT;
    }

    public static void setThreadLocalCharBufferInitSize(int size) {
        if (size < 64) {
            size = 64;
        } else if (size > S.BUFFER_RETENTION_LIMIT) {
            size = S.BUFFER_RETENTION_LIMIT;
        }
        S.BUFFER_INIT_SIZE = size;
    }

    public static int getThreadLocalCharBufferInitSize() {
        return S.BUFFER_INIT_SIZE;
    }

    public static void setThreadLocalByteArrayBufferLimit(int limit) {
        if (limit < 256) {
            limit = 256;
        } else if (limit < ByteArrayBuffer.BUFFER_INIT_SIZE) {
            limit = ByteArrayBuffer.BUFFER_INIT_SIZE;
        }
        ByteArrayBuffer.BUFFER_RETENTION_LIMIT = limit;
    }

    public static int getThreadLocalByteArrayBufferLimit() {
        return ByteArrayBuffer.BUFFER_RETENTION_LIMIT;
    }

    public static void setThreadLocalByteArrayBufferInitSize(int size) {
        if (size < 64) {
            size = 64;
        } else if (size > ByteArrayBuffer.BUFFER_RETENTION_LIMIT) {
            size = ByteArrayBuffer.BUFFER_RETENTION_LIMIT;
        }
        ByteArrayBuffer.BUFFER_INIT_SIZE = size;
    }

    public static int getThreadLocalByteArrayBufferInitSize() {
        return ByteArrayBuffer.BUFFER_INIT_SIZE;
    }

}
