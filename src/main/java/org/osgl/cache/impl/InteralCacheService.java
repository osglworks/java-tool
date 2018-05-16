package org.osgl.cache.impl;

import org.osgl.cache.CacheService;
import org.osgl.util.E;

import java.util.concurrent.ConcurrentHashMap;

public class InteralCacheService implements CacheService {

    private ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public void put(String key, Object value, int ttl) {
        E.unsupport();
    }

    @Override
    public void put(String key, Object value) {
        store.put(key, value);
    }

    @Override
    public void evict(String key) {
        store.remove(key);
    }

    @Override
    public <T> T get(String key) {
        return (T) store.get(key);
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
        store.clear();
    }

    @Override
    public void setDefaultTTL(int ttl) {
        throw E.unsupport();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void startup() {
    }
}
