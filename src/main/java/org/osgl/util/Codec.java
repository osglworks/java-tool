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

    public static final char URL_SAFE_BASE64_PADDING_CHAR = UrlSafeBase64.CHAR_PADDING;

    /**
     * @return an UUID String
     */
    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Alias of {@link #UUID()}
     *
     * @return an UUID string
     */
    public static String uuid() {
        return UUID();
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
     *
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
     * @deprecated Use {@link #encodeBase64(byte[])} instead
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

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static int hexToByte(char ch) {
        if ('0' <= ch && ch <= '9') return ch - '0';
        if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
        if ('a' <= ch && ch <= 'f') return ch - 'a' + 10;
        return -1;
    }

    private static final String[] byteToHexTable = new String[]{
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F",
            "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F",
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
            "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF",
            "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
            "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF",
            "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF",
            "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF",
            "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"
    };

    /**
     * Write a byte array as hexadecimal String.
     *
     * @return bytes
     */
    public static String byteToHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        S.Buffer sb = S.buffer();
        for (byte b : bytes) {
            sb.append(byteToHexTable[b & 0xFF]);
        }
        return sb.toString();
    }

    /**
     * Transform an hexadecimal String to a byte array.
     *
     * @param hexString the string
     * @return the byte array of the hex string
     */
    public static byte[] hexStringToByte(String hexString) {
        if (hexString == null || hexString.length() == 0) {
            return new byte[]{};
        }
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            byteArray[i / 2] = (byte) (hexToByte(hexString.charAt(i)) * 16 + hexToByte(hexString.charAt(i + 1)));
        }
        return byteArray;
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
