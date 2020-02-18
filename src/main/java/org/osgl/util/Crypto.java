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

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

/**
 * Cryptography utils. Comes from play!framework under apache license
 */
public enum Crypto {
    ;

    private static CryptoService svc;
    private static final String ALGO = "AES/CBC/PKCS5Padding";

    public static void setCryptoService(CryptoService service) {
        SecurityManager security = System.getSecurityManager();
       	if (security != null) {
       	    security.checkPermission(new RuntimePermission("OSGL.SetCryptoService"));
       	}
        svc = service;
    }

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

    public static char[] generatePassword() {
        return generatePassword(null);
    }

    public static char[] generatePassword(int len) {
        return generatePassword(new char[len]);
    }

    public static char[] generatePassword(char[] ca) {
        return generatePassword(ca, new SecureRandom());
    }

    private static char[] generatePassword(char[] ca, Random r) {
        int len = null == ca ? 0 : ca.length;
        if (0 == len) {
            len = Math.abs(r.nextInt(6)) + 12;
            ca = new char[len];
        }
        char[] chars = S._COMMON_CHARS_;
        int charsLen = S._COMMON_CHARS_LEN_;
        while (len-- > 0) {
            int i = r.nextInt(charsLen);
            ca[len] = chars[i];
        }
        return ca;
    }

    /**
     * Sign a message with a key
     *
     * @param message The message to sign
     * @param key     The key to use
     * @return The signed message (in hexadecimal)
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

    /**
     * Create a password hash using the default hashing algorithm
     *
     * @param input The password
     * @return The password hash
     */
    public static char[] passwordHash(char[] input) {
        return passwordHash(input, DEFAULT_HASH_TYPE);
    }

    /**
     * Create a password hash using specific hashing algorithm
     *
     * @param input    The password
     * @param hashType The hashing algorithm
     * @return The password hash
     */
    public static char[] passwordHash(char[] input, HashType hashType) {
        try {
            MessageDigest m = MessageDigest.getInstance(hashType.toString());
            byte[] out = m.digest(toByte(input));
            return Base64.encode(out);
        } catch (NoSuchAlgorithmException e) {
            throw E.unexpected(e);
        }
    }

    /**
     * This method is deprecated. Please use {@link #encryptAES(String, byte[])} instead
     *
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      The String to encrypt
     * @param privateKey The key used to encrypt
     * @return An hexadecimal encrypted string
     */
    @Deprecated
    public static String encryptAES(String value, String privateKey) {
        return encryptAES(value, privateKey.getBytes(Charsets.UTF_8));
    }

    /**
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      The String to encrypt
     * @param privateKey The key used to encrypt
     * @return An hexadecimal encrypted string
     */
    public static String encryptAES(String value, byte[] privateKey) {
        if (null == value) {
            return null;
        }
        try {
            if (null != svc) return svc.encrypt(value, privateKey);
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            md.update(privateKey);
            byte[] ba = md.digest();
            byte[] key = new byte[32], iv = new byte[16];
            System.arraycopy(ba, 0, key, 0, 32);
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);

            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            ba = cipher.doFinal(value.getBytes(Charsets.UTF_8));

            byte[] ba2 = new byte[ba.length + 16];
            System.arraycopy(ba, 0, ba2, 0, ba.length);
            System.arraycopy(iv, 0, ba2, ba.length, 16);

            return Codec.byteToHexString(ba2);
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }

    /**
     * This method is deprecated, please use {@link #encryptAES(String, byte[])} instead
     *
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      The String to encrypt
     * @param privateKey The key used to encrypt
     * @param salt       The salt
     * @return An hexadecimal encrypted string
     */
    @Deprecated
    public static String encryptAES(String value, String privateKey, String salt) {
        return encryptAES(value, privateKey.getBytes(Charsets.UTF_8), salt.getBytes(Charsets.UTF_8));
    }

    /**
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      The String to encrypt
     * @param privateKey The key used to encrypt
     * @param salt       The salt
     * @return An hexadecimal encrypted string
     */
    public static String encryptAES(String value, byte[] privateKey, byte[] salt) {
        if (null == value) {
            return null;
        }
        try {
            if (null != svc) return svc.encrypt(value, privateKey, salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(privateKey);
            byte[] key = md.digest();
            md = MessageDigest.getInstance("SHA-1");
            md.update(salt);
            byte[] tmp = md.digest();
            byte[] iv = new byte[16];
            System.arraycopy(tmp, 0, iv, 0, 16);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] ba = cipher.doFinal(value.getBytes("utf-8"));
            return Codec.byteToHexString(ba);
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }


    /**
     * This method is deprecated. please use {@link #decryptAES(String, byte[])} instead
     *
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @return The decrypted String
     */
    @Deprecated
    public static String decryptAES(String value, String privateKey) {
        return decryptAES(value, privateKey.getBytes(Charsets.UTF_8));
    }

    /**
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @return The decrypted String
     */
    public static String decryptAES(String value, byte[] privateKey) {
        if (null == value) {
            return null;
        }
        try {
            if (null != svc) return svc.decrypt(value, privateKey);
            byte[] ba0 = Codec.hexStringToByte(value);
            byte[] baVal = new byte[ba0.length - 16];
            System.arraycopy(ba0, 0, baVal, 0, ba0.length - 16);
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            md.update(privateKey);
            byte[] ba = md.digest();
            byte[] key = new byte[32], iv = new byte[16];
            System.arraycopy(ba, 0, key, 0, 32);
            System.arraycopy(ba0, ba0.length - 16, iv, 0, 16);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            ba = cipher.doFinal(baVal);
            return new String(ba);
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }


    /**
     * This method is deprecated. please use {@link #decryptAES(String, byte[], byte[])} instead
     *
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @param salt       the salt
     * @return The decrypted String
     */
    @Deprecated
    public static String decryptAES(String value, String privateKey, String salt) {
        return decryptAES(value, privateKey.getBytes(Charsets.UTF_8), salt.getBytes(Charsets.UTF_8));
    }

    /**
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     *
     * @param value      An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @param salt       the salt
     * @return The decrypted String
     */
    public static String decryptAES(String value, byte[] privateKey, byte[] salt) {
        if (null == value) {
            return null;
        }
        try {
            if (null != svc) return svc.decrypt(value, privateKey, salt);
            byte[] baVal = Codec.hexStringToByte(value);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(privateKey);
            byte[] key = md.digest();
            md = MessageDigest.getInstance("SHA-1");
            md.update(salt);
            byte[] tmp = md.digest();
            byte[] iv = new byte[16];
            System.arraycopy(tmp, 0, iv, 0, 16);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] ba = cipher.doFinal(baVal);
            return new String(ba);
        } catch (Exception ex) {
            throw E.unexpected(ex);
        }
    }

    public static final String ALGO_RSA = "RSA";

    public static String encryptRSA(String value, byte[] publicKey) {
        try {
            PublicKey key = KeyFactory.getInstance(ALGO_RSA).generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance(ALGO_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ba = cipher.doFinal(value.getBytes(Charsets.UTF_8));
            return Codec.byteToHexString(ba);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public static String decryptRSA(String value, byte[] privateKey) {
        try {
            PrivateKey key = KeyFactory.getInstance(ALGO_RSA)
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKey));

            Cipher cipher = Cipher.getInstance(ALGO_RSA);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] ba = cipher.doFinal(Codec.hexStringToByte(value));
            return new String(ba);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(1024);
    }

    public static KeyPair generateKeyPair(int keysize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO_RSA);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(keysize, random);
            KeyPair generateKeyPair = keyGen.generateKeyPair();
            return generateKeyPair;
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }


    /**
     * Generate a secret string from random byte array
     * @param len the number of bytes used to generate the secret
     * @return the secret
     */
    public static String genSecret(int len) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        String s = Codec.encodeUrlSafeBase64(bytes);
        if (s.endsWith(".")) {
            s = S.beforeFirst(s, ".");
        }
        return s;
    }

    /**
     * Generate secret string using 4 bytes
     * @return the secret
     */
    public static String genSecret() {
        return genSecret(4);
    }

    private static final char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final int[] POWER_OF_TEN = {
            1,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
            100000000,
            1000000000
    };

    private static final int DIGITS_SIZE = digits.length;

    /**
     * Generate random digital string with the size specified. The
     * method use the {@link SecureRandom}
     * @param len the number of digits in the string generated
     * @return a string contains random digits
     */
    public static String genRandomDigits(int len) {
        E.illegalArgumentIf(len < 1);
        if (len < 10) {
            SecureRandom random = new SecureRandom();
            int n = random.nextInt(POWER_OF_TEN[len]);
            int base = POWER_OF_TEN[len - 1];
            if (n < base) {
                n += base;
            }
            return String.valueOf(n);
        } else {
            return genRandomX(len, digits, DIGITS_SIZE);
        }
    }

    /**
     * Generate random digital string of random size range from 4 to 12 inclusive
     * @return a random digital string
     * @see #genRandomDigits(int)
     */
    public static String genRandomDigits() {
        SecureRandom random = new SecureRandom();
        int n = random.nextInt(8) + 4;
        return genRandomDigits(n);
    }

    private final static char[] symbols = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'i' , 'j' , 'k' , 'm' , 'n' ,
            'o' , 'p' , 'q' , 'r' , 's' , 't' ,
            'u' , 'v' , 'w' , 'x' , 'y' , 'z' ,
            'A' , 'B' , 'C' , 'D' , 'E' , 'F' ,
            'G' , 'H' , 'J' , 'K' , 'L' ,
            'M' , 'N' , 'P' , 'Q' , 'R' ,
            'S' , 'T' , 'U' , 'V' , 'W' , 'X' ,
            'Y' , 'Z' , '!' , '.' , '-' , '*' ,
    };

    private final static int SYMBOL_SIZE = symbols.length;

    /**
     * Generate random String with readable characters. This
     * method will use the {@link SecureRandom}
     * @param len the size of the string returned
     * @return the string generated
     */
    public static String genRandomStr(int len) {
        E.illegalArgumentIf(len < 1);
        return genRandomX(len, symbols, SYMBOL_SIZE);
    }

    private static String genRandomX(int len, char[] space, int spaceSize) {
        SecureRandom random = new SecureRandom();
        char[] ca = new char[len];
        for (int i = 0; i < len; ++i) {
            ca[i] = space[random.nextInt(spaceSize)];
        }
        return new String(ca);
    }

    /**
     * Generate random string with random length from 4 to 12 inclusive
     * @return the string generated
     * @see #genRandomStr(int)
     */
    public static String genRandomStr() {
        SecureRandom random = new SecureRandom();
        int len = random.nextInt(12) + 4;
        return genRandomStr(len);
    }

    private static byte[] toByte(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    public static void main(String[] args) {
        KeyPair keyPair = Crypto.generateKeyPair();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        String s = "Hello world";
        String encrypted = encryptRSA(s, publicKey);
        System.out.println(decryptRSA(encrypted, privateKey));
    }

}
