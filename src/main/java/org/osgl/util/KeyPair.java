package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2020 OSGL (Open Source General Library)
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

public class KeyPair extends S.Pair {

    public KeyPair(java.security.KeyPair keyPair) {
        super(Codec.encodeUrlSafeBase64(keyPair.getPrivate().getEncoded()), Codec.encodeUrlSafeBase64(keyPair.getPublic().getEncoded()));
    }

    public KeyPair(String privateKey, String publicKey) {
        super(privateKey, publicKey);
    }

    public byte[] getPrivateKey() {
        return Codec.decodeUrlSafeBase64(getPrivateKeyAsString());
    }

    public byte[] getPublicKey() {
        return Codec.decodeUrlSafeBase64(getPublicKeyAsString());
    }

    public String getPrivateKeyAsString() {
        return left();
    }

    public String getPublicKeyAsString() {
        return right();
    }
}
