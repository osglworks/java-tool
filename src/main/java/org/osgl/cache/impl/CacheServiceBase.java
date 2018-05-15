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

import org.osgl.cache.CacheService;

abstract class CacheServiceBase implements CacheService {
    @Override
    public int incr(String key) {
        Object o = get(key);
        if (null == o) {
            put(key, 1);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() + 1);
            return (Integer) o;
        }
        throw new IllegalStateException("Only int or long value support incr operation");
    }

    @Override
    public int incr(String key, int ttl) {
        Object o = get(key);
        if (null == o) {
            put(key, 1, ttl);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() + 1, ttl);
            return (Integer) o;
        }
        throw new IllegalStateException("Only int or long value support incr operation");
    }

    @Override
    public int decr(String key) {
        Object o = get(key);
        if (null == o) {
            put(key, -1);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() - 1);
            return (Integer)o;
        }
        throw new IllegalStateException("Only int or long value support decr operation");
    }

    @Override
    public int decr(String key, int ttl) {
        Object o = get(key);
        if (null == o) {
            put(key, -1, ttl);
            return 0;
        }
        if (o instanceof Integer) {
            put(key, ((Integer) o).intValue() - 1, ttl);
            return (Integer)o;
        }
        throw new IllegalStateException("Only int or long value support decr operation");
    }

}
