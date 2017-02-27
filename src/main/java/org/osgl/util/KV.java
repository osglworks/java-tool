package org.osgl.util;

import java.util.Map;

/**
 * A instance of `KV` is a Map with {@link ValueObject} indexed with {@link String}
 */
public interface KV extends Map<String, ValueObject> {

    /**
     * Add an new pair (key, val) into the store.
     * @param key the key
     * @param val the Object typed value (instead of encoded ValueObject type)
     * @return this KV instance
     */
    KV putValue(String key, Object val);

    /**
     * Returns a decoded value by key
     * @param key the key index the value object
     * @param <T> the generic type of the value
     * @return the decoded value
     */
    <T> T getValue(String key);

    /**
     * Put all (key, value) pair from an normal map into this KV map. Note the original
     * map contains String typed key and Object typed value. After they have been put
     * into this KV map. The original value will be encoded by {@link ValueObject}
     *
     * @param kvMap the original map
     * @return this KV instance after all pairs has been put into this KV
     */
    KV putValues(Map<String, Object> kvMap);

    /**
     * Returns a `Map` contains (key, value) pairs that stored in this
     * `KV` map. The value in the pair should be the {@link ValueObject#value()}
     * stored in the {@link ValueObject} in this `KV` map
     *
     * @return the map of key and raw value stored in this store
     */
    Map<String, Object> toMap();
}
