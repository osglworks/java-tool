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

/**
 * Created by luog on 16/01/14.
 */
public interface CryptoService {
    /**
     * This method is deprecated. Please use {@link #encrypt(String, byte[])} instead
     *
     * Encrypt a String content using private key specified
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @return the encrypted content
     * @throws Exception when any exception happened
     */
    @Deprecated
    String encrypt(String content, String privateKey) throws Exception;

    /**
     * Encrypt a String content using private key specified
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @return the encrypted content
     * @throws Exception when any exception happened
     */
    String encrypt(String content, byte[] privateKey) throws Exception;

    /**
     * This method is deprecated. Please use {@link #encrypt(String, byte[], byte[])} instead
     *
     * Encrypt a string content using private key specified along with salt
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @param salt the salt string
     * @return the encrypted content
     * @throws Exception when any exception happened
     */
    @Deprecated
    String encrypt(String content, String privateKey, String salt) throws Exception;

    /**
     * Encrypt a string content using private key specified along with salt
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @param salt the salt string
     * @return the encrypted content
     * @throws Exception when any exception happened
     */
    String encrypt(String content, byte[] privateKey, byte[] salt) throws Exception;

    /**
     * This method is deprecated, please use {@link #decrypt(String, byte[])} instead
     *
     * Decrypt a secret using the private key specified
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @return the original content
     * @throws Exception when any exception happened
     */
    @Deprecated
    String decrypt(String secret, String privateKey) throws Exception;

    /**
     * This method is deprecated, please use {@link #decrypt(String, String, String)} instead
     *
     * Decrypt a secret using the private key specified along with the salt
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @param salt the salt string
     * @return the original content
     * @throws Exception when any exception happened
     */
    @Deprecated
    String decrypt(String secret, String privateKey, String salt) throws Exception;

    /**
     * Decrypt a secret using the private key specified
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @return the original content
     * @throws Exception when any exception happened
     */
    String decrypt(String secret, byte[] privateKey) throws Exception;

    /**
     * Decrypt a secret using the private key specified along with the salt
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @param salt the salt string
     * @return the original content
     * @throws Exception when any exception happened
     */
    String decrypt(String secret, byte[] privateKey, byte[] salt) throws Exception;
}
