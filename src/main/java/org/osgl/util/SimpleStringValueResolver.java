package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.util.Map;

public class SimpleStringValueResolver extends Osgl.F2<String, Class<?>, Object> {

    public static final SimpleStringValueResolver INSTANCE = new SimpleStringValueResolver();

    protected Map<Class, StringValueResolver> resolvers = C.newMap();

    public SimpleStringValueResolver() {
        registerPredefinedResolvers();
    }

    @Override
    public Object apply(String s, Class<?> aClass) throws NotAppliedException, Osgl.Break {
        StringValueResolver r = resolvers.get(aClass);
        if (null != r) {
            return r.resolve(s);
        }
        if (null != s && Enum.class.isAssignableFrom(aClass)) {
            return Enum.valueOf(((Class<Enum>) aClass), s);
        }
        return null;
    }

    private void registerPredefinedResolvers() {
        resolvers.putAll(StringValueResolver.predefined());
    }
}
