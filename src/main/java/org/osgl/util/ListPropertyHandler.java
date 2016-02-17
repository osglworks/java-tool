package org.osgl.util;

import org.osgl.$;
import org.osgl.Osgl;

class ListPropertyHandler extends PropertyHandlerBase {

    protected final Class<?> itemType;

    ListPropertyHandler(Class<?> itemType) {
        this.itemType = $.notNull(itemType);
    }

    ListPropertyHandler(PropertyGetter.NullValuePolicy nullValuePolicy, Class<?> itemType) {
        super(nullValuePolicy);
        this.itemType = $.notNull(itemType);
    }

    ListPropertyHandler(Osgl.Function<Class<?>, Object> objectFactory,
                        Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                        Class<?> itemType) {
        super(objectFactory, stringValueResolver);
        this.itemType = $.notNull(itemType);
    }

    ListPropertyHandler(Osgl.Function<Class<?>, Object> objectFactory,
                        Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                        PropertyGetter.NullValuePolicy nullValuePolicy,
                        Class<?> itemType) {
        super(objectFactory, stringValueResolver, nullValuePolicy);
        this.itemType = $.notNull(itemType);
    }

}
