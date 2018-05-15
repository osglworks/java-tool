/*
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgl.cache.impl;

/*-
 * #%L
 * OSGL Cache API
 * %%
 * Copyright (C) 2017 OSGL (Open Source General Library)
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
import org.osgl.logging.L;
import org.osgl.logging.Logger;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple cache service implementation based on concurrent hash map
 */
public class SimpleCacheService extends CacheServiceBase {

    private String name;

    private final ReentrantLock lock = new ReentrantLock();

    private static final Logger logger = L.get(SimpleCacheService.class);

    private static class TimerThreadFactory implements ThreadFactory {
        private String name;

        TimerThreadFactory(String name) {
            this.name = name;
        }

        private static final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            SecurityManager s = System.getSecurityManager();
            ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

            Thread t = new Thread(group, r, "simple-cache-service-" + threadNumber.getAndIncrement(), 0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private ScheduledExecutorService scheduler = null;

    public SimpleCacheService(String name) {
        this.name = name;
        startup();
    }

    private static class Item implements Comparable<Item> {
        String key;
        Object value;
        long ts;
        int ttl;

        Item(String key, Object value, int ttl) {
            this.key = key;
            this.value = value;
            this.ttl = ttl;
            this.ts = $.ms();
        }

        @Override
        public int compareTo(Item that) {
            if (null == that) {
                return 1;
            }
            long myTs = ts + ttl * 1000;
            long hisTs = that.ts + that.ttl * 1000;
            return myTs < hisTs ? -1 : myTs > hisTs ? 1 : key.compareTo(that.key);
        }
    }

    private static class SoftItem extends SoftReference<Item> implements Comparable<SoftItem> {
        public SoftItem(String key, Object value, int ttl) {
            super(new Item(key, value, ttl));
        }

        @Override
        public int compareTo(SoftItem that) {
            if (null == that) {
                return 1;
            }
            Item myItem = get();
            if (null == myItem) {
                return -1;
            }
            Item hisItem = that.get();
            return myItem.compareTo(hisItem);
        }
    }

    private Map<String, SoftItem> cache_ = new WeakHashMap<>();
    private Queue<SoftItem> items_ = new PriorityQueue<>();

    @Override
    public void put(String key, Object value, int ttl) {
        if (null == key) throw new NullPointerException();
        if (0 >= ttl) {
            ttl = defaultTTL;
        }
        lock.lock();
        try {
            SoftItem item = cache_.get(key);
            Item item1 = null == item ? null : item.get();
            if (null == item1) {
                SoftItem newItem = new SoftItem(key, value, ttl);
                cache_.put(key, newItem);
                items_.offer(newItem);
            } else {
                item1.value = value;
                item1.ttl = ttl;
                item1.ts = $.ms();
                // so that we can re-position the item in the queue
                items_.remove(item);
                items_.offer(item);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T get(String key) {
        lock.lock();
        try {
            SoftItem item = cache_.get(key);
            if (null == item) {
                return null;
            }
            Item item1 = item.get();
            return null == item1 ? null : (T) item1.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, defaultTTL);
    }

    @Override
    public void evict(String key) {
        lock.lock();
        try {
            cache_.remove(key);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void clear() {
        lock.lock();
        try {
            cache_.clear();
            items_.clear();
        } finally {
            lock.unlock();
        }
    }

    private int defaultTTL = 60;

    @Override
    public void setDefaultTTL(int ttl) {
        if (ttl == 0) throw new IllegalArgumentException("time to live value couldn't be zero");
        this.defaultTTL = ttl;
    }

    @Override
    public synchronized void shutdown() {
        clear();
        if (null != scheduler) {
            scheduler.shutdown();
            scheduler = null;
        }
    }

    @Override
    public synchronized void startup() {
        if (null == scheduler) {
            scheduler = new ScheduledThreadPoolExecutor(1, new TimerThreadFactory(name));
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (items_.isEmpty()) {
                        return;
                    }
                    boolean trace = logger.isTraceEnabled();
                    long now = System.currentTimeMillis();
                    if (trace) {
                        logger.trace(">>>>now:%s", now);
                    }
                    lock.lock();
                    try {
                        while (true) {
                            SoftItem item0 = items_.peek();
                            if (null == item0) {
                                break;
                            }
                            Item item = item0.get();
                            if (null == item) {
                                // garbage collected ?
                                items_.poll();
                                continue;
                            }
                            long ts = item.ts + item.ttl * 1000;
                            if ((ts) < now + 50) {
                                cache_.remove(item.key);
                                items_.poll();
                                if (trace) {
                                    logger.trace("- %s at %s", item.key, ts);
                                }
                                continue;
                            } else {
                                if (!cache_.containsKey(item.key)) {
                                    // evicted or garbage collected
                                    items_.poll();
                                    if (trace) {
                                        logger.trace("cached item evicted: %s", item.key);
                                    }
                                    continue;
                                }
                                if (trace) {
                                    logger.trace(">>>>ts:  %s", ts);
                                }
                            }
                            break;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdown();
                }
            });
        }
    }

}
