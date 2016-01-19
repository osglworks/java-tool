package org.osgl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A map of (String, {@link ValueObject})
 */
@SuppressWarnings("unused")
public class KVStore extends HashMap<String, ValueObject> implements Map<String, ValueObject> {

    public KVStore() {
    }

    /**
     * Put a simple data into the store with a key. The type of simple data
     * should be allowed by {@link ValueObject}
     * @param key the key
     * @param val the value
     * @return this store instance after the put operation finished
     * @see ValueObject
     */
    public KVStore putValue(String key, Object val) {
        put(key, ValueObject.of(val));
        return this;
    }

    /**
     * Get {@link ValueObject#value() value object value} by key from the
     * store.
     * @param key the key
     * @param <T> the generic type of the return value
     * @return the value stored in the value object associated with the key
     * @see ValueObject#value()
     */
    public <T> T getValue(String key) {
        return get(key).value();
    }

    /**
     * Put a map of (key, value) pair into the store. The value could be any type
     * that supported by {@link ValueObject}
     * @param kvMap a map of {key, value} pair
     * @return the store after put operation finished
     */
    public void putValues(Map<String, Object> kvMap) {
        for (String key : kvMap.keySet()) {
            put(key, ValueObject.of(kvMap.get(key)));
        }
    }

}
