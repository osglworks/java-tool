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
import org.osgl.Osgl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The base class for {@link ReflectionPropertyGetter} and {@link ReflectionPropertySetter}
 */
abstract class ReflectionPropertyHandler extends PropertyHandlerBase {

    protected transient Class entityClass;
    protected transient Method m;
    protected String mn;
    protected transient Field f;
    protected String fn;
    protected transient Class propertyClass;
    protected String propertyClassName;

    ReflectionPropertyHandler(Osgl.Function<Class<?>, Object> objectFactory,
                              Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                              Class entityClass, Method m, Field f) {
        super(objectFactory, stringValueResolver);
        init(entityClass, m, f);
    }

    ReflectionPropertyHandler(Osgl.Function<Class<?>, Object> objectFactory,
                              Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                              PropertyGetter.NullValuePolicy nullValuePolicy,
                              Class entityClass, Method m, Field f) {
        super(objectFactory, stringValueResolver, nullValuePolicy);
        init(entityClass, m, f);
    }

    ReflectionPropertyHandler(Class entityClass, Method m, Field f) {
        init(entityClass, m, f);
    }

    ReflectionPropertyHandler(PropertyGetter.NullValuePolicy nullValuePolicy,
                              Class entityClass, Method m, Field f) {
        super(nullValuePolicy);
        init(entityClass, m, f);
    }

    public Class getPropertyClass(Object entity) {
        if (null != propertyClass) {
            return propertyClass;
        }
        return $.classForName(propertyClassName, entity.getClass().getClassLoader());
    }

    protected void ensureMethodOrField(Object obj) {
        if (null != m || null != f) return;
        try {
            if (null == entityClass) {
                entityClass = obj.getClass();
            }
            if (null != mn) {
                m = entityClass.getMethod(mn);
                m.setAccessible(true);
            } else if (null != fn) {
                f = entityClass.getDeclaredField(fn);
                f.setAccessible(true);
            } else {
                throw E.unexpected("neither method name nor field name found");
            }
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    private void init(Class c, Method m, Field f) {
        E.illegalArgumentIf(null == m && null == f);
        this.entityClass = c;
        this.m = m;
        this.f = f;
        if (null != m) {
            this.mn = m.getName();
            this.propertyClass = m.getReturnType();
            if (void.class.equals(this.propertyClass)) {
                this.propertyClass = m.getParameterTypes()[0];
            }
        } else {
            this.fn = f.getName();
            this.propertyClass = f.getType();
        }
        this.propertyClassName = propertyClass.getName();
    }
}
