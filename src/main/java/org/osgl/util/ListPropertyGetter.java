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

import java.util.List;

/**
 * Implement {@link PropertyGetter} on a {@link java.util.List} type entity
 */
public class ListPropertyGetter extends ListPropertyHandler implements PropertyGetter {

    public ListPropertyGetter(Class<?> itemType) {
        super(itemType);
    }

    public ListPropertyGetter(PropertyGetter.NullValuePolicy nullValuePolicy, Class<?> itemType) {
        super(nullValuePolicy, itemType);
    }

    public ListPropertyGetter(Osgl.Function<Class<?>, Object> objectFactory,
                              Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                              Class<?> itemType) {
        super(objectFactory, stringValueResolver, itemType);
    }

    public ListPropertyGetter(Osgl.Function<Class<?>, Object> objectFactory,
                              Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                              PropertyGetter.NullValuePolicy nullValuePolicy,
                              Class<?> itemType) {
        super(objectFactory, stringValueResolver, nullValuePolicy, itemType);
    }

    @Override
    public Object get(Object entity, Object index) {
        List list = (List) entity;
        int id = -1;
        if (index instanceof Integer) {
            id = (Integer) index;
        } else {
            String s = S.string(index);
            if (S.notBlank(s)) {
                id = Integer.parseInt(s);
            }
        }
        Object val = null;
        if (id > -1 && id < list.size()) {
            val = list.get(id);
        }
        if (null == val) {
            switch (nullValuePolicy) {
                case NPE:
                    throw new NullPointerException();
                case CREATE_NEW:
                    val = objectFactory.apply(itemType);
                    if (id < 0) {
                        list.add(val);
                    } else if (id >= list.size()) {
                        for (int i = list.size(); i < id - 1; ++i) {
                            list.add(null);
                        }
                        list.add(val);
                    } else {
                        list.set(id, val);
                    }
                default:
                    // do nothing
            }
        }
        return val;
    }

    @Override
    public PropertySetter setter() {
        return new ListPropertySetter(objectFactory, stringValueResolver, itemType);
    }
}
