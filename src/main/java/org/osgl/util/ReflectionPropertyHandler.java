package org.osgl.util;

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
