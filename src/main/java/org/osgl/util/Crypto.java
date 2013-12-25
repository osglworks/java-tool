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

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * Cryptography utils. Comes from play!framework under apache license
 */
public class Crypto {
    /**
     * Define a hash type enumeration for strong-typing
     */
    public enum HashType {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512");
        private String algorithm;

        HashType(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return this.algorithm;
        }
    }

    /**
     * Set-up MD5 as the default hashing algorithm
     */
    private static final HashType DEFAULT_HASH_TYPE = HashType.MD5;

    static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Sign a message with a key
     *
     * @param message The message to sign
     * @param key     The key to use
     * @return The signed message (in hexadecimal)
     * @throws java.lang.Exception
     */
    public static String sign(String message, byte[] key) {

        if (key.length == 0) {
            return message;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            mac.init(signingKey);
            byte[] messageBytes = message.getBytes("utf-8");
            byte[] result = mac.doFinal(messageBytes);
            int len = result.length;
            char[] hexChars = new char[len * 2];


            for (int charIndex = 0, startIndex = 0; charIndex < hexChars.length; ) {
                int bite = result[startIndex++] & 0xff;
                hexChars[charIndex++] = HEX_CHARS[bite >> 4];
                hexChars[charIndex++] = HEX_CHARS[bite & 0xf];
            }
            return new String(hexChars);
        } catch (UnsupportedEncodingException ex) {
            throw E.encodingException(ex);
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }

    }

    /**
     * Create a password hash using the default hashing algorithm
     *
     * @param input The password
     * @return The password hash
     */
    public static String passwordHash(String input) {
        return passwordHash(input, DEFAULT_HASH_TYPE);
    }

    /**
     * Create a password hash using specific hashing algorithm
     *
     * @param input    The password
     * @param hashType The hashing algorithm
     * @return The password hash
     */
    public static String passwordHash(String input, HashType hashType) {
        try {
            MessageDigest m = MessageDigest.getInstance(hashType.toString());
            byte[] out = m.digest(input.getBytes());
            return new String(Base64.encode(out));
        } catch (NoSuchAlgorithmException e) {
            throw E.unexpected(e);
        }
    }

    private static SecretKey secretKeyAES(String pass,String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int len = pass.length();
        if (len < 16) {
            pass = pass + "                ";
        }
        pass = pass.substring(0, 16);
        if (S.empty(salt)) {
            salt = pass;
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");


        // NOTE: last argument is the key length, and it is 256
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), 1024, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return(secret);
    }

    /**
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      The String to encrypt
     * @param privateKey The key used to encrypt
     * @return An hexadecimal encrypted string
     */
    public static String encryptAES(String value, String privateKey) {
        try {
            SecretKey key = secretKeyAES(privateKey, privateKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Codec.byteToHexString(cipher.doFinal(value.getBytes()));
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }

    /**
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      The String to encrypt
     * @param privateKey The key used to encrypt
     * @param salt       The salt
     * @return An hexadecimal encrypted string
     */
    public static String encryptAES(String value, String privateKey, String salt) {
        try {
            SecretKey key = secretKeyAES(privateKey, salt);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Codec.byteToHexString(cipher.doFinal(value.getBytes()));
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }


    /**
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @return The decrypted String
     */
    public static String decryptAES(String value, String privateKey) {
        try {
            SecretKey key = secretKeyAES(privateKey, privateKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Codec.hexStringToByte(value)));
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }


    /**
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @return The decrypted String
     */
    public static String decryptAES(String value, String privateKey, String salt) {
        try {
            SecretKey key = secretKeyAES(privateKey, salt);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Codec.hexStringToByte(value)));
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }

    public static void main(String[] args) {
        String key = S.random(64);
        String s = "Hello world!";
//        String es = encryptBlowfish(s, key);
//        String s1 = decryptBlowfish(es, key);

        String es = encryptAES(s, key);
        String s1 = decryptAES(es, key);

        System.out.println(String.format("%s\n %s\n %s\n %s", key, s, es, s1));
    }
}
