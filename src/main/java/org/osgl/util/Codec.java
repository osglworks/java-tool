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

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.osgl.exception.UnexpectedException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Utility class for encoding and decoding
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
     *
     * @param value The plain String
     * @return The base64 encoded String
     * @deprecated Use {@link #encodeBase64(String)} instead
     */
    @Deprecated
    public static String encodeBASE64(String value) {
        return new String(Base64.encode(value.getBytes(Charsets.UTF_8)));
    }

    /**
     * Encode a String to base64
     *
     * @param value The plain String
     * @return The base64 encoded String
     */
    public static String encodeBase64(String value) {
        return new String(Base64.encode(value.getBytes(Charsets.UTF_8)));
    }

    /**
     * Encode a String to base64 using variant URL safe encode scheme
     * @param value the plain string
     * @return the base64 encoded String that is URL safe
     */
    public static String encodeUrlSafeBase64(String value) {
        return new String(UrlSafeBase64.encode(value.getBytes(Charsets.UTF_8)));
    }

    /**
     * Encode binary data to base64
     *
     * @param value The binary data
     * @return The base64 encoded String
     * @deprecated  Use {@link #encodeBase64(byte[])} instead
     */
    @Deprecated
    public static String encodeBASE64(byte[] value) {
        return encodeBase64(value);
    }

    /**
     * Encode binary data to base64
     *
     * @param value The binary data
     * @return The base64 encoded String
     */
    public static String encodeBase64(byte[] value) {
        return new String(Base64.encode(value));
    }

    /**
     * Encode binary data to base64 use Url safe variant
     *
     * @param value The binary data
     * @return The base64 encoded String that is URL safe
     */
    public static String encodeUrlSafeBase64(byte[] value) {
        return new String(UrlSafeBase64.encode(value));
    }

    /**
     * Decode a base64 value
     *
     * @param value The base64 encoded String
     * @return decoded binary data
     * @deprecated Use {@link #decodeBase64(String)} instead
     */
    @Deprecated
    public static byte[] decodeBASE64(String value) {
        return Base64.decode(value);
    }

    /**
     * Decode a base64 value
     *
     * @param value The base64 encoded String
     * @return decoded binary data
     */
    public static byte[] decodeBase64(String value) {
        return Base64.decode(value);
    }

    /**
     * Decode a URL safe base64 value
     *
     * @param value The base64 encoded String that is encoded using {@link #encodeUrlSafeBase64(String)}
     * @return decoded binary data
     */
    public static byte[] decodeUrlSafeBase64(String value) {
        return UrlSafeBase64.decode(value);
    }

    /**
     * Build an hexadecimal MD5 hash for a String
     *
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexMD5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Build an hexadecimal SHA1 hash for a String
     *
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexSHA1(String value) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Write a byte array as hexadecimal String.
     * @return bytes
     */
    public static String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    /**
     * Transform an hexadecimal String to a byte array.
     * @param hexString the string
     * @return the byte array of the hex string
     */
    public static byte[] hexStringToByte(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            throw new UnexpectedException(e);
        }
    }

    public static String encodeUrl(String s, Charset enc) {
        try {
            return URLEncoder.encode(s, enc.name());
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }

    public static String encodeUrl(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }

    public static String decodeUrl(String s, Charset enc) {
        try {
            return URLDecoder.decode(s, enc.name());
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }

    public static String decodeUrl(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw E.encodingException(e);
        }
    }

}
