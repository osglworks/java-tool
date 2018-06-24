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

import org.osgl.$;
import org.osgl.Lang;

import java.util.List;

/**
 * Implement {@link PropertySetter} on {@link java.util.List} type entity specifically
 */
public class ListPropertySetter extends ListPropertyHandler implements PropertySetter {

    public ListPropertySetter(Class<?> itemType) {
        super(itemType);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
    }

    public ListPropertySetter(Lang.Function<Class<?>, Object> objectFactory,
                       Lang.Func2<String, Class<?>, ?> stringValueResolver,
                       Class<?> itemType) {
        super(objectFactory, stringValueResolver, itemType);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
    }

    @Override
    public void set(Object entity, Object value, Object index) {
        List list = (List) entity;
        int sz = list.size();
        int id = -1;
        if (index instanceof Integer) {
            id = $.cast(index);
        } else {
            id = Integer.parseInt(S.string(index));
        }
        if (sz < id + 1) {
            for (int i = sz; i < id; ++i) {
                list.add(null);
            }
            list.add(value);
        } else {
            list.set(id, value);
        }
    }

}
