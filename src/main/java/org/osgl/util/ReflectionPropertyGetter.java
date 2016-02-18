package org.osgl.util;

import org.osgl.$;
import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implement {@link PropertyGetter} using Java reflection
 */
public class ReflectionPropertyGetter extends ReflectionPropertyHandler implements PropertyGetter {

    private ReflectionPropertyHandlerFactory factory;

    public ReflectionPropertyGetter(Osgl.Function<Class<?>, Object> objectFactory,
                                    Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                                    Class entityClass, Method m, Field f,
                                    ReflectionPropertyHandlerFactory factory) {
        super(objectFactory, stringValueResolver, entityClass, m, f);
        this.factory = $.notNull(factory);
    }

    public ReflectionPropertyGetter(Osgl.Function<Class<?>, Object> objectFactory,
                                    Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                                    NullValuePolicy nullValuePolicy,
                                    Class entityClass, Method m, Field f,
                                    ReflectionPropertyHandlerFactory factory) {
        super(objectFactory, stringValueResolver, nullValuePolicy, entityClass, m, f);
        this.factory = $.notNull(factory);
    }

    public ReflectionPropertyGetter(Class entityClass, Method m, Field f,
                                    ReflectionPropertyHandlerFactory factory) {
        super(entityClass, m, f);
        this.factory = $.notNull(factory);
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
    private Object getProperty(Object entity) throws NotAppliedException, Osgl.Break {
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
