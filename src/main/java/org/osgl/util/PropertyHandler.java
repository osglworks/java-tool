package org.osgl.util;

import org.osgl.Osgl;

public interface PropertyHandler {
    void setObjectFactory(Osgl.Function<Class<?>, Object> factory);
    void setStringValueResolver(Osgl.Func2<String, Class<?>, ?> stringValueResolver);
}
