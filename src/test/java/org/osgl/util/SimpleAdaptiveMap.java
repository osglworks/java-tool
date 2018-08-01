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
import org.osgl.Lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleAdaptiveMap implements AdaptiveMap<SimpleAdaptiveMap> {

    private Map<String, Object> map = new HashMap<>();

    @Override
    public Map<String, Object> internalMap() {
        return map;
    }

    @Override
    public SimpleAdaptiveMap putValue(String key, Object val) {
        map.put(key, val);
        return this;
    }

    @Override
    public SimpleAdaptiveMap mergeValue(String key, Object val) {
        Object existing = map.get(key);
        if (null != existing) {
            val = $.merge(val).to(existing);
        }
        map.put(key, val);
        return this;
    }

    @Override
    public SimpleAdaptiveMap putValues(Map<String, Object> kvMap) {
        map.putAll(kvMap);
        return this;
    }

    @Override
    public SimpleAdaptiveMap mergeValues(Map<String, Object> kvMap) {
        for (Map.Entry<String, Object> entry : kvMap.entrySet()) {
            mergeValue(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public <T> T getValue(String key) {
        return (T) map.get(key);
    }

    @Override
    public Map<String, Object> toMap() {
        return C.newMap(map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet(Lang.Function<BeanInfo, Boolean> fieldFilter) {
        throw E.unsupport();
    }

    @Override
    public Map<String, Object> asMap() {
        return map;
    }
}
