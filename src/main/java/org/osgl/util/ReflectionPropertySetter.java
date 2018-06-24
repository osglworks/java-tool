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

import org.osgl.Lang;
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implement {@link PropertySetter} using Java reflection
 */
public class ReflectionPropertySetter extends ReflectionPropertyHandler implements PropertySetter {

    public ReflectionPropertySetter(Class c, Method m, Field f) {
        super(c, m, f);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
    }

    public ReflectionPropertySetter(Lang.Function<Class<?>, Object> objectFactory,
                             Lang.Func2<String, Class<?>, ?> stringValueResolver,
                             Class entityClass, Method m, Field f) {
        super(objectFactory, stringValueResolver, PropertyGetter.NullValuePolicy.CREATE_NEW, entityClass, m, f);
    }

    @Override
    public void set(Object entity, Object value, Object index) {
        setProperty(entity, value);
    }

    private void setProperty(Object entity, Object value) throws NotAppliedException, Lang.Break {
        if (null == entity) {
            return;
        }
        ensureMethodOrField(entity);
        try {
            doSet(entity, value);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    protected void doSet(Object entity, Object value) throws Exception {
        Class requiredClass = super.getPropertyClass(entity);
        value = convertValue(requiredClass, value);
        if (null != m) {
            m.invoke(entity, value);
        } else {
            f.set(entity, value);
        }
    }

    protected Object convertValue(Class requiredClass, Object value) {
        if (null == value || requiredClass.isAssignableFrom(value.getClass())) {
            return value;
        }
        return stringValueResolver.apply(S.string(value), requiredClass);
    }

}
