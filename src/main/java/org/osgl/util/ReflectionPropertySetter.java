package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implement {@link PropertySetter} using Java reflection
 */
public class ReflectionPropertySetter<ENTITY, PROP> extends ReflectionPropertyHandler implements PropertySetter<ENTITY, PROP> {

    public ReflectionPropertySetter(Class c, Method m, Field f) {
        super(c, m, f);
    }

    @Override
    public Void apply(ENTITY entity, PROP value) throws NotAppliedException, Osgl.Break {
        setProperty(entity, value);
        return null;
    }

    private void setProperty(ENTITY entity, PROP value) throws NotAppliedException, Osgl.Break {
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
        return value;
    }

}
