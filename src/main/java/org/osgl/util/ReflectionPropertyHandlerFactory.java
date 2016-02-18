package org.osgl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Create {@link PropertyGetter} and {@link PropertySetter} based on Java reflection
 */
public class ReflectionPropertyHandlerFactory implements PropertyHandlerFactory {
    @Override
    public PropertySetter createPropertySetter(Class c, String propName) {
        String p = S.capFirst(propName);
        String setter = "set" + p;
        String isser = "is" + p;
        Method[] ma = c.getMethods();
        for (Method m : ma) {
            String mn = m.getName();
            if (S.neq(setter, mn) && S.neq(isser, mn) && S.neq(p, mn)) {
                continue;
            }
            Class[] ca = m.getParameterTypes();
            if (ca != null && ca.length == 1) {
                return newSetter(c, m, null);
            }
        }
        try {
            Field f = c.getDeclaredField(propName);
            f.setAccessible(true);
            return newSetter(c, null, f);
        } catch (NoSuchFieldException e) {
            throw E.unexpected(e, "Cannot find access method to field %s on class %s", propName, c);
        }
    }

    @Override
    public PropertyGetter createPropertyGetter(Class c, String propName, boolean requireField) {
        PropertyGetter propertyGetter;
        if (requireField) {
            try {
                return getterViaField(c, propName);
            } catch (Exception e) {
                // ignore: try the java bean getter later on
            }
        }
        String p = S.capFirst(propName);
        String getter = "get" + p;
        try {
            Method m = c.getMethod(getter);
            propertyGetter = newGetter(c, m, null);
        } catch (NoSuchMethodException e) {
            String isser = "is" + p;
            try {
                Method m = c.getMethod(isser);
                propertyGetter = newGetter(c, m, null);
            } catch (NoSuchMethodException e1) {
                try {
                    // try jquery style getter
                    Method m = c.getMethod(propName);
                    propertyGetter = newGetter(c, m, null);
                } catch (NoSuchMethodException e2) {
                    propertyGetter = getterViaField(c, propName);
                }
            }
        }
        return propertyGetter;
    }

    @Override
    public MapPropertyGetter createMapPropertyGetter(Class keyType, Class valType) {
        return new MapPropertyGetter(keyType, valType);
    }

    @Override
    public MapPropertySetter createMapPropertySetter(Class keyType, Class valType) {
        return new MapPropertySetter(keyType, valType);
    }

    @Override
    public ListPropertyGetter createListPropertyGetter(Class itemType) {
        return new ListPropertyGetter(itemType);
    }

    @Override
    public ListPropertySetter createListPropertySetter(Class itemType) {
        return new ListPropertySetter(itemType);
    }

    private PropertyGetter getterViaField(Class entityClass, String propName) {
        while (!Object.class.equals(entityClass)) {
            try {
                Field f = entityClass.getDeclaredField(propName);
                f.setAccessible(true);
                return newGetter(entityClass, null, f);
            } catch (NoSuchFieldException e3) {
                entityClass = entityClass.getSuperclass();
                throw E.unexpected(e3, "Cannot find access method to field %s on class %s", propName, entityClass);
            }
        }
        throw E.unexpected("entity class is Object.class");
    }

    protected PropertyGetter newGetter(Class c, Method m, Field f) {
        return new ReflectionPropertyGetter(c, m, f, this);
    }

    protected PropertySetter newSetter(Class c, Method m, Field f) {
        return new ReflectionPropertySetter(c, m, f);
    }


}
