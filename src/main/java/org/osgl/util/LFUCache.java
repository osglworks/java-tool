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

import java.util.*;

/**
 * A simple thread-safe LFU cache.
 *
 * Disclaim: the source code is adapted from https://github.com/Tsien/LFUCache/
 *
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> {

    private class Node {
        private V v;
        private int count;

        Node(V v) {
            this.v = v;
            this.count = 0;
        }

        int touch() {
            return ++this.count;
        }
    }

    // a hash map holding <key, <frequency, value>> nodes
    private final Map<K, Node> store;

    // a list of LinkedHashSet, accessCountList[i] has elements with accessCount = i
    private final LinkedHashSet<K>[] accessCountList;

    // the minimum frequency in the cache
    private int minFreq;

    // the size of the cache; it is also the upper bound of possible frequency
    private final int capacity;

    // the number of evicted elements when reaching capacity
    private final int evictNum;

    /**
     * Create a new LFU cache.
     *
     * @param cap         the size of the cache
     * @param evictFactor the percentage of elements for replacement
     * @return a newly created LFU cache
     */
    @SuppressWarnings("unchecked")
    public LFUCache(int cap, double evictFactor) {
        if (cap <= 0 || evictFactor <= 0 || evictFactor >= 1) {
            throw new IllegalArgumentException("Eviction factor or Capacity is illegal.");
        }
        capacity = cap;
        minFreq = 0;  // the initial smallest frequency
        evictNum = Math.min(cap, (int) Math.ceil(cap * evictFactor));

        store = new HashMap<>();
        accessCountList = new LinkedHashSet[cap];
        for (int i = 0; i < cap; ++i) {
            accessCountList[i] = new LinkedHashSet<K>();
        }
    }

    /**
     * Update access count of the node in the cache if the key exists.
     * Increase the access count of this node and move it to the next counter set.
     * If the access count reaches the capacity, move it the end of current frequency set.
     */
    private synchronized void touch(K key) {
        if (store.containsKey(key)) { // sanity checking
            Node node = store.get(key);
            int id = Math.min(node.count, capacity - 1);
            accessCountList[id].remove(key);
            int newCount = node.touch();
            if (newCount < capacity) {
                store.put(key, node);
                accessCountList[newCount].add(key);
                if (id == minFreq && accessCountList[minFreq].isEmpty()) {
                    // update current minimum frequency
                    ++minFreq;
                }
            } else {
                // LRU: put the most recent visited to the end of set
                accessCountList[id].add(key);
            }
        }
    }

    /**
     * Evict the least frequent elements in the cache
     * The number of evicted elements is configured by eviction factor
     */
    private synchronized void evict() {
        for (int i = 0; i < evictNum && minFreq < capacity; ++i) {
            // get the first element in the current minimum frequency set
            K key = (K) accessCountList[minFreq].iterator().next();
            accessCountList[minFreq].remove(key);
            store.remove(key);
            while (minFreq < capacity && accessCountList[minFreq].isEmpty()) {
                // skip empty frequency sets
                ++minFreq;
            }
        }
    }

    /**
     * Get the value of key.
     * If the key does not exist, return null.
     *
     * @param key the key to query
     * @return the value of the key
     */
    public synchronized V get(K key) {
        if (!store.containsKey(key)) {
            return null;
        }
        // update frequency
        touch(key);
        return store.get(key).v;
    }

    /**
     * Set key to hold the value.
     * If key already holds a value, it is overwritten.
     *
     * @param key   the key of the node
     * @param value the value of the node
     * @return
     */
    public synchronized void set(K key, V value) {
        Node node = store.get(key);
        if (null != node) {
            node.v = value;
            touch(key);  // update frequency
            return;
        }
        if (store.size() >= capacity) {
            evict();
        }
        store.put(key, new Node(value));
        accessCountList[0].add(key);
        // set the minimum frequency back to 0
        minFreq = 0;
    }

    /**
     * Returns the values of all specified keys.
     * For every key that does not exist, null is returned.
     *
     * @param keys a list of keys to query
     * @return query results, a map of key/val extracted
     */
    public synchronized Map<K, V> mget(List<K> keys) {
        Map<K, V> ret = new LinkedHashMap<>();
        for (K key : keys) {
            V val = get(key);
            if (null != val) {
                ret.put(key, val);
            }
        }
        return ret;
    }

    /**
     * Sets the given keys to their respective values.
     * MSET replaces existing values with new values, just as regular SET.
     *
     * @param data a map contains the key/val pairs to be set.
     */
    public synchronized void mset(Map<K, V> data) {
        for (Map.Entry<K, V> entry : data.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Increments the value stored at key by delta.
     * If the key does not exist, it is set to 0 before performing the operation.
     * Only works for integer value.
     * This function will increase frequency by 1
     *
     * @param key   the key needed to be increased
     * @param delta increment
     * @return the value after increment
     */
    @SuppressWarnings("unchecked")
    public synchronized Integer incr(K key, Integer delta) {
        if (!store.containsKey(key)) {
            set(key, (V) delta);
            return delta;
        }
        Node node = store.get(key);
        Integer I = (Integer) node.v;
        if (null == I) {
            I = 0;
        }
        I += delta;
        node.v = (V) I;
        // update frequency
        touch(key);
        return I;
    }

    /**
     * Decrements the value stored at key by delta.
     * If the key does not exist, it is set to 0 before performing the operation.
     * Only works for integer value.
     * This function will increase frequency by 2
     *
     * @param key   the key needed to be decreased
     * @param delta decrement
     * @return the value after decrement
     */
    public synchronized Integer decr(K key, Integer delta) {
        return incr(key, -delta);
    }

    /**
     * Only for testing purpose
     * Print the content of the cache in the order of frequency
     * @return
     */
    public void print() {
        int f = minFreq;
        System.out.println("=========================");
        System.out.println("What is in cache?");
        while (f < capacity) {
            for (Object key : accessCountList[f]) {
                System.out.print("(" + key + ", " + store.get(key).count + " : " + store.get(key).v + "), ");
            }
            ++f;
        }
        System.out.println("\n=========================");
    }

}
