package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implement {@link PropertySetter} using Java reflection
 */
public class ReflectionPropertySetter<OBJECT, PROP> extends ReflectionPropertyHandler implements PropertySetter<OBJECT, PROP> {

    public ReflectionPropertySetter(Class c, Method m, Field f) {
        super(c, m, f);
    }

    @Override
    public Void apply(OBJECT object, PROP prop) throws NotAppliedException, Osgl.Break {
        setProperty(object, prop);
        return null;
    }

    private void setProperty(OBJECT object, PROP value) throws NotAppliedException, Osgl.Break {
        if (null == object) {
            return;
        }
        ensureMethodOrField(object);
        try {
            if (null != m) {
                m.invoke(object, value);
            } else {
                f.set(object, value);
            }
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

}
