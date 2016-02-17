package org.osgl.util;

import org.osgl.$;
import org.osgl.Osgl;

abstract class PropertyHandlerBase implements PropertyHandler {
    protected Osgl.Function<Class<?>, Object> objectFactory;
    protected Osgl.Func2<String, Class<?>, ?> stringValueResolver;
    protected PropertyGetter.NullValuePolicy nullValuePolicy;

    PropertyHandlerBase() {
        this(SimpleObjectFactory.INSTANCE, SimpleStringValueResolver.INSTANCE);
    }

    PropertyHandlerBase(PropertyGetter.NullValuePolicy nullValuePolicy) {
        this(SimpleObjectFactory.INSTANCE, SimpleStringValueResolver.INSTANCE, nullValuePolicy);
    }

    PropertyHandlerBase(Osgl.Function<Class<?>, Object> objectFactory, Osgl.Func2<String, Class<?>, ?> stringValueResolver) {
        setObjectFactory(objectFactory);
        setStringValueResolver(stringValueResolver);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.RETURN_NULL);
    }

    PropertyHandlerBase(Osgl.Function<Class<?>, Object> objectFactory,
                        Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                        PropertyGetter.NullValuePolicy nullValuePolicy) {
        setObjectFactory(objectFactory);
        setStringValueResolver(stringValueResolver);
        if (null == nullValuePolicy) {
            nullValuePolicy = PropertyGetter.NullValuePolicy.RETURN_NULL;
        }
        setNullValuePolicy(nullValuePolicy);
    }

    @Override
    public void setObjectFactory(Osgl.Function<Class<?>, Object> factory) {
        this.objectFactory = $.notNull(factory);
    }

    @Override
    public void setStringValueResolver(Osgl.Func2<String, Class<?>, ?> stringValueResolver) {
        this.stringValueResolver = $.notNull(stringValueResolver);
    }

    public void setNullValuePolicy(PropertyGetter.NullValuePolicy nvp) {
        this.nullValuePolicy = $.notNull(nvp);
    }
}
