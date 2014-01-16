package org.osgl.util;

/**
 * Created by luog on 16/01/14.
 */
public interface CryptoService {
    /**
     * Encrypt a String content using private key specified
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @return the encrypted content
     */
    String encrypt(String content, String privateKey) throws Exception;

    /**
     * Encrypt a string content using private key specified along with salt
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @param salt the salt string
     * @return the encrypted content
     */
    String encrypt(String content, String privateKey, String salt) throws Exception;

    /**
     * Decrypt a secret using the private key specified
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @return the original content
     */
    String decrypt(String secret, String privateKey) throws Exception;

    /**
     * Decrypt a secret using the private key specified along with the salt
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @param salt the salt string
     * @return the original content
     */
    String decrypt(String secret, String privateKey, String salt) throws Exception;
}
