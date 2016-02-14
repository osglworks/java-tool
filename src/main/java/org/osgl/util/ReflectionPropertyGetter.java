package org.osgl.util;

import org.osgl.$;
import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implement {@link PropertyGetter} using Java reflection
 */
public class ReflectionPropertyGetter<OBJECT, PROP> extends ReflectionPropertyHandler implements PropertyGetter<OBJECT, PROP> {

    public ReflectionPropertyGetter(Class c, Method m, Field f) {
        super(c, m, f);
    }

    @Override
    public PROP apply(OBJECT object) throws NotAppliedException, Osgl.Break {
        return getProperty(object);
    }


    @SuppressWarnings("unchecked")
    private PROP getProperty(OBJECT object) throws NotAppliedException, Osgl.Break {
        if (null == object) {
            return null;
        }
        ensureMethodOrField(object);
        try {
            Object v;
            if (null != m) {
                v = m.invoke(object);
            } else {
                v = f.get(object);
            }
            return Osgl.cast(v);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

}
