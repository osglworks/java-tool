package org.osgl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple implementation of {@link KV} using {@link HashMap}
 */
@SuppressWarnings("unused")
public class KVStore extends HashMap<String, ValueObject> implements KV {

    /**
     * Create an empty {@code KVStore}
     */
    public KVStore() {
    }

    /**
     * Create a {@code KVStore} by coping another {@code KVStore}
     * @param copy the KVStore in which all (K,V) pairs will be copied into the new KVStore instance
     */
    public KVStore(KVStore copy) {
        putAll(copy);
    }

    /**
     * Create a {@code KVStore} with a (String, Object) map
     * @param values the map in which all entries will be stored into the new KVStore instance
     */
    public KVStore(Map<String, Object> values) {
        putValues(values);
    }

    /**
     * Put a simple data into the store with a key. The type of simple data
     * should be allowed by {@link ValueObject}
     * @param key the key
     * @param val the value
     * @return this store instance after the put operation finished
     * @see ValueObject
     */
    @Override
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
    @Override
    public <T> T getValue(String key) {
        ValueObject vo = get(key);
        if (null == vo) {
            return null;
        }
        return vo.value();
    }

    /**
     * Put a map of (key, value) pair into the store. The value could be any type
     * that supported by {@link ValueObject}
     * @param kvMap a map of {key, value} pair
     */
    @Override
    public KVStore putValues(Map<String, Object> kvMap) {
        for (String key : kvMap.keySet()) {
            put(key, ValueObject.of(kvMap.get(key)));
        }
        return this;
    }

    /**
     * Returns a `Map` contains (key, value) pairs that stored in this
     * `KVStore`. The value in the pair should be the {@link ValueObject#value()}
     * stored in the {@link ValueObject} in this `KVStore`
     *
     * @return the map of key and raw value stored in this store
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = C.newMap();
        for (Map.Entry<String, ValueObject> entry : entrySet()) {
            map.put(entry.getKey(), entry.getValue().value());
        }
        return map;
    }

}
