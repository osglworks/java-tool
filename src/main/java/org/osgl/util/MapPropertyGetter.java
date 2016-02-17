package org.osgl.util;

import org.osgl.Osgl;

import java.util.Map;

/**
 * Implement {@link PropertyGetter} on a {@link java.util.Map} type entity
 */
public class MapPropertyGetter extends MapPropertyHandler implements PropertyGetter {

    public MapPropertyGetter(Class<?> keyType, Class<?> valType) {
        super(keyType, valType);
    }

    public MapPropertyGetter(PropertyGetter.NullValuePolicy nullValuePolicy, Class<?> keyType, Class<?> valType) {
        super(nullValuePolicy, keyType, valType);
    }

    public MapPropertyGetter(Osgl.Function<Class<?>, Object> objectFactory,
                             Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                             Class<?> keyType,
                             Class<?> valType) {
        super(objectFactory, stringValueResolver, keyType, valType);
    }

    public MapPropertyGetter(Osgl.Function<Class<?>, Object> objectFactory,
                             Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                             PropertyGetter.NullValuePolicy nullValuePolicy,
                             Class<?> keyType,
                             Class<?> valType) {
        super(objectFactory, stringValueResolver, nullValuePolicy, keyType, valType);
    }

    @Override
    public Object get(Object entity, Object index) {
        Map map = (Map) entity;
        Object key = keyFrom(index);
        Object val = map.get(key);
        if (null == val) {
            switch (nullValuePolicy) {
                case NPE:
                    throw new NullPointerException();
                case CREATE_NEW:
                    val = objectFactory.apply(valType);
                    map.put(key, val);
                default:
                    // do nothing
            }
        }
        return val;
    }

    @Override
    public PropertySetter setter() {
        return new MapPropertySetter(objectFactory, stringValueResolver, keyType, valType);
    }
}
