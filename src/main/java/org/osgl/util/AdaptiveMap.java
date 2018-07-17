package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
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

import org.osgl.$;

import java.util.Map;
import java.util.Set;

public interface AdaptiveMap<T extends AdaptiveMap> {

    Map<String, Object> internalMap();

    /**
     * Add or replace a key/val pair into the active record
     *
     * @param key the key
     * @param val the value
     * @return the active record instance
     */
    T putValue(String key, Object val);

    /**
     * Merge a key/val pair in the active record.
     *
     * If the key specified does not exists then insert the key/val pair into the record.
     *
     * If there are existing key/val pair then merge it with the new one:
     *
     * 1. if the val is simple type or cannot be merged, then replace the existing value with new value
     * 2. if the val can be merged, e.g. it is a POJO or another adaptive record, then merge the new value into the old value. Merge shall happen recursively
     *
     * @param key the key
     * @param val the value
     * @return the active record instance
     */
    T mergeValue(String key, Object val);

    /**
     * Add all key/val pairs from specified kv map into this active record
     *
     * @param kvMap the key/value pairs
     * @return this active record instance
     */
    T putValues(Map<String, Object> kvMap);

    /**
     * Merge all key/val pairs from specified kv map into this active record
     *
     * @param kvMap the key/value pairs
     * @return this active record instance
     * @see #mergeValue(String, Object)
     */
    T mergeValues(Map<String, Object> kvMap);

    /**
     * Get value from the active record by key specified
     *
     * @param key the key
     * @param <T> the generic type of the value
     * @return the value or `null` if not found
     */
    <T> T getValue(String key);

    /**
     * Export the key/val pairs from this active record into a map
     *
     * @return the exported map contains all key/val pairs stored in this active record
     */
    Map<String, Object> toMap();

    /**
     * Get the size of the data stored in the active record
     *
     * @return the active record size
     */
    int size();

    /**
     * Check if the active records has a value associated with key specified
     *
     * @param key the key
     * @return `true` if there is value associated with the key in the record, or `false` otherwise
     */
    boolean containsKey(String key);

    /**
     * Returns a set of keys that has value stored in the active record
     *
     * @return the key set
     */
    Set<String> keySet();

    /**
     * Returns a set of entries stored in the active record
     *
     * @return the entry set
     */
    Set<Map.Entry<String, Object>> entrySet();

    /**
     * Returns a set of entries stored in the active record. For
     * field entries, use the field filter specified to check
     * if it needs to be added into the return set
     *
     * @param fieldFilter the function that returns `true` or `false` for
     *                    bean spec of a certain field declared in the class
     * @return the entry set with field filter applied
     */
    Set<Map.Entry<String, Object>> entrySet($.Function<BeanInfo, Boolean> fieldFilter);

    /**
     * Returns a Map typed object backed by this active record
     *
     * @return a Map backed by this active record
     */
    Map<String, Object> asMap();

}
