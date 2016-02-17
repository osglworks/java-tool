package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.util.List;
import java.util.Map;

public class SimpleObjectFactory extends Osgl.F1<Class<?>, Object> {

    public static final SimpleObjectFactory INSTANCE = new SimpleObjectFactory();

    @Override
    public Object apply(Class<?> aClass) throws NotAppliedException, Osgl.Break {
        if (List.class.isAssignableFrom(aClass)) {
            return C.newList();
        } else if (Map.class.isAssignableFrom(aClass)) {
            return C.newMap();
        }
        return Osgl.newInstance(aClass);
    }
}
