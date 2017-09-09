package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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

import org.osgl.Osgl;

import java.util.Map;

/**
 * Implement {@link PropertySetter} on {@link java.util.Map} type entity specifically
 */
public class MapPropertySetter extends MapPropertyHandler implements PropertySetter {

    public MapPropertySetter(Class<?> keyType, Class<?> valType) {
        super(keyType, valType);
    }

    MapPropertySetter(Osgl.Function<Class<?>, Object> objectFactory,
                      Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                      Class<?> keyType,
                      Class<?> valType) {
        super(objectFactory, stringValueResolver, keyType, valType);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
    }

    @Override
    public void set(Object entity, Object value, Object index) {
        Map map = (Map) entity;
        Object key = keyFrom(index);
        map.put(key, value);
    }

}
