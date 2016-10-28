package org.osgl.util;

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
     */
    @Deprecated
    String encrypt(String content, String privateKey) throws Exception;

    /**
     * Encrypt a String content using private key specified
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @return the encrypted content
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
     */
    @Deprecated
    String encrypt(String content, String privateKey, String salt) throws Exception;

    /**
     * Encrypt a string content using private key specified along with salt
     * @param content the content to be encrypted
     * @param privateKey the key to encrypt the content
     * @param salt the salt string
     * @return the encrypted content
     */
    String encrypt(String content, byte[] privateKey, byte[] salt) throws Exception;

    /**
     * This method is deprecated, please use {@link #decrypt(String, byte[])} instead
     *
     * Decrypt a secret using the private key specified
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @return the original content
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
     */
    @Deprecated
    String decrypt(String secret, String privateKey, String salt) throws Exception;

    /**
     * Decrypt a secret using the private key specified
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @return the original content
     */
    String decrypt(String secret, byte[] privateKey) throws Exception;

    /**
     * Decrypt a secret using the private key specified along with the salt
     * @param secret the encrypted content
     * @param privateKey the key to decrypt the secret
     * @param salt the salt string
     * @return the original content
     */
    String decrypt(String secret, byte[] privateKey, byte[] salt) throws Exception;
}
