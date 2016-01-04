package org.osgl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The base class for {@link ReflectionPropertyGetter} and {@link ReflectionPropertySetter}
 */
public abstract class ReflectionPropertyHandler {
    protected transient Class c;
    protected transient Method m;
    protected String mn;
    protected transient Field f;
    protected String fn;

    ReflectionPropertyHandler(Class c, Method m, Field f) {
        E.illegalArgumentIf(null == m && null == f);
        this.c = c;
        this.m = m;
        this.f = f;
        if (null != m) {
            this.mn = m.getName();
        } else {
            this.fn = f.getName();
        }
    }

    protected void ensureMethodOrField(Object obj) {
        if (null != m || null != f) return;
        try {
            if (null == c) {
                c = obj.getClass();
            }
            if (null != mn) {
                m = c.getMethod(mn);
                m.setAccessible(true);
            } else if (null != fn) {
                f = c.getDeclaredField(fn);
                f.setAccessible(true);
            } else {
                throw E.unexpected("neither method name nor field name found");
            }
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
}
