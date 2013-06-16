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
package com.greenlaw110.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

/**
 * Utility class for encoding and decoding
 * 
 * <p>Part of the code comes from play!framework under apache license</p>
 */
public class Codec {

    /**
     * @return an UUID String
     */
    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Encode a String to base64
     * @param value The plain String
     * @return The base64 encoded String
     */
    public static String encodeBASE64(String value) {
        try {
            return new String(Base64.encode(value.getBytes("utf-8")));
        } catch (UnsupportedEncodingException ex) {
            throw E.encodingException(ex);
        }
    }

    /**
     * Encode binary data to base64 
     * @param value The binary data
     * @return The base64 encoded String
     */
    public static String encodeBASE64(byte[] value) {
        return new String(Base64.encode(value));
    }

    /**
     * Decode a base64 value
     * @param value The base64 encoded String
     * @return decoded binary data
     */
    public static byte[] decodeBASE64(String value) {
        return Base64.decode(value);
    }
    
    public static String toHexString(String s) {
        return toHexString(s, "utf-8");
    }

    public static String toHexString(String s, String encode) {
        try {
            return toHexString(s.getBytes(encode));
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }
    
    public static String toHexString(byte[] bytes) {
        if (bytes.length == 0) return "";
        return String.format("%x", new BigInteger(bytes));
    }

    public static String fromHexString(String hex) {
        return fromHexString(hex, "utf-8");
    }

    public static String fromHexString(String hex, String encode) {
        try {
            return new String(bytesFromHexString(hex), encode);
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }
    
    public static byte[] bytesFromHexString(String hex) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[512];
            int _start=0;
            for (int i = 0; i < hex.length(); i+=2) {
                buffer[_start++] = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
                if (_start >=buffer.length || i+2>=hex.length()) {
                    bout.write(buffer);
                    Arrays.fill(buffer, 0, buffer.length, (byte) 0);
                    _start  = 0;
                }
            }
            return bout.toByteArray();
        } catch (IOException e) {
            throw E.ioException(e);
        }
    }

}
