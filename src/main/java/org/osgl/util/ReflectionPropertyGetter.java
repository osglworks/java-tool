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
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implement {@link PropertyGetter} using Java reflection
 */
public class ReflectionPropertyGetter extends ReflectionPropertyHandler implements PropertyGetter {

    private ReflectionPropertyHandlerFactory factory;

    public ReflectionPropertyGetter(Lang.Function<Class<?>, Object> objectFactory,
                                    Lang.Func2<String, Class<?>, ?> stringValueResolver,
                                    Class entityClass, Method m, Field f,
                                    ReflectionPropertyHandlerFactory factory) {
        super(objectFactory, stringValueResolver, entityClass, m, f);
        this.factory = $.requireNotNull(factory);
    }

    public ReflectionPropertyGetter(Lang.Function<Class<?>, Object> objectFactory,
                                    Lang.Func2<String, Class<?>, ?> stringValueResolver,
                                    NullValuePolicy nullValuePolicy,
                                    Class entityClass, Method m, Field f,
                                    ReflectionPropertyHandlerFactory factory) {
        super(objectFactory, stringValueResolver, nullValuePolicy, entityClass, m, f);
        this.factory = $.requireNotNull(factory);
    }

    public ReflectionPropertyGetter(Class entityClass, Method m, Field f,
                                    ReflectionPropertyHandlerFactory factory) {
        super(entityClass, m, f);
        this.factory = $.requireNotNull(factory);
    }

    public ReflectionPropertyGetter(NullValuePolicy nullValuePolicy,
                                    Class entityClass, Method m, Field f) {
        super(nullValuePolicy, entityClass, m, f);
    }

    // Index is not used in the JavaBean context
    @Override
    public Object get(Object entity, Object index) {
        return getProperty(entity);
    }

    @SuppressWarnings("unchecked")
    private Object getProperty(Object entity) throws NotAppliedException, Lang.Break {
        if (null == entity) {
            return null;
        }
        ensureMethodOrField(entity);
        try {
            Object v;
            if (null != m) {
                v = m.invoke(entity);
            } else {
                v = f.get(entity);
            }
            if (null == v) {
                switch (nullValuePolicy) {
                    case NPE:
                        throw new NullPointerException();
                    case CREATE_NEW:
                        v = objectFactory.apply(getPropertyClass(entity));
                        PropertySetter setter = setter();
                        setter.set(entity, v, null);
                        return v;
                    default:
                        return null;
                }
            }
            return v;
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public PropertySetter setter() {
        String prop;
        if (null != m) {
            prop = m.getName();
            if (prop.startsWith("get")) {
                prop = prop.substring(3);
            }
        } else {
            prop = f.getName();
        }
        return factory.createPropertySetter(entityClass, prop);
    }
}
