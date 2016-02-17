package org.osgl.util;

import org.osgl.$;
import org.osgl.Osgl;

import java.util.List;

/**
 * Implement {@link PropertySetter} on {@link java.util.List} type entity specifically
 */
public class ListPropertySetter extends ListPropertyHandler implements PropertySetter {

    public ListPropertySetter(Class<?> itemType) {
        super(itemType);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
    }

    public ListPropertySetter(Osgl.Function<Class<?>, Object> objectFactory,
                       Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                       Class<?> itemType) {
        super(objectFactory, stringValueResolver, itemType);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.CREATE_NEW);
    }

    @Override
    public void set(Object entity, Object value, Object index) {
        List list = (List) entity;
        int sz = list.size();
        int id = -1;
        if (index instanceof Integer) {
            id = $.cast(index);
        } else {
            id = Integer.parseInt(S.string(index));
        }
        if (sz < id + 1) {
            for (int i = sz; i < id; ++i) {
                list.add(null);
            }
            list.add(value);
        } else {
            list.set(id, value);
        }
    }

}
