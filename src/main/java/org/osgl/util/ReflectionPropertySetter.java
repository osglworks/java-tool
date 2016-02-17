package org.osgl.util;

import org.osgl.Osgl;
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

    ReflectionPropertySetter(Osgl.Function<Class<?>, Object> objectFactory,
                             Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                             Class entityClass, Method m, Field f) {
        super(objectFactory, stringValueResolver, PropertyGetter.NullValuePolicy.CREATE_NEW, entityClass, m, f);
    }

    @Override
    public void set(Object entity, Object value, Object index) {
        setProperty(entity, value);
    }

    private void setProperty(Object entity, Object value) throws NotAppliedException, Osgl.Break {
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
