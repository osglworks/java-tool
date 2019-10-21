package org.osgl.cache.impl;

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

import org.osgl.cache.CacheService;
import org.osgl.util.E;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InteralCacheService implements CacheService {

    private Map<String, Object> store = new WeakHashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private State state = State.INITIALIZED;

    @Override
    public void put(String key, Object value, int ttl) {
        E.unsupport();
    }

    @Override
    public void put(String key, Object value) {
        writeLock.lock();
        try {
            store.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void evict(String key) {
        writeLock.lock();
        try {
            store.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public <T> T get(String key) {
        readLock.lock();
        try {
            return (T) store.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int incr(String key) {
        throw E.unsupport();
    }

    @Override
    public int incr(String key, int ttl) {
        throw E.unsupport();
    }

    @Override
    public int decr(String key) {
        throw E.unsupport();
    }

    @Override
    public int decr(String key, int ttl) {
        throw E.unsupport();
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            store.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void setDefaultTTL(int ttl) {
        throw E.unsupport();
    }

    @Override
    public void shutdown() {
        this.state = State.SHUTDOWN;
    }

    @Override
    public void startup() {
        this.state = State.STARTED;
    }

    @Override
    public State state() {
        return this.state;
    }
}
