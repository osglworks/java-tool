package org.osgl.util.converter;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.N;
import org.osgl.util.S;

import java.lang.reflect.Field;
import java.util.*;

public class TypeConverterRegistry {

    private static final Map<Class, Object> NULL_VALS = C.Map(
            boolean.class, false,
            char.class, '\0',
            byte.class, 0,
            short.class, 0,
            int.class, 0,
            float.class, 0,
            long.class, 0,
            double.class, 0
    );

    public static final $.TypeConverter<Void, Object> NULL_CONVERTER = new $.TypeConverter<Void, Object>(Void.class, Object.class) {
        @Override
        public Object convert(Void aVoid) {
            return null;
        }
    };

    public static final TypeConverterRegistry INSTANCE = new TypeConverterRegistry();

    private Map<$.Pair<Class, Class>, $.TypeConverter> paths = new HashMap<>();

    public TypeConverterRegistry() {
        registerBuiltInConverters();
    }

    public <FROM, TO> $.TypeConverter<FROM, TO> get(Class<FROM> fromType, Class<TO> toType) {
        $.TypeConverter<FROM, TO> converter = $.cast(paths.get($.Pair(fromType, toType)));
        if (null == converter && Void.class == fromType) {
            return $.cast(NULL_CONVERTER);
        }
        if (null == converter) {
            Set<Class> types = $.allTypesOf(fromType);
            for (Class c : types) {
                $.TypeConverter converter1 = get(c, toType);
                if (null != converter1) {
                    register(converter1, fromType, toType);
                    return converter1;
                }
            }
        }
        return converter;
    }

    public TypeConverterRegistry register($.TypeConverter typeConverter) {
        for ($.Pair<Class, Class> key : allKeyOf(typeConverter)) {
            if (!paths.containsKey(key)) {
                addIntoPath(key, typeConverter);
            }
        }
        buildPaths(typeConverter);
        return this;
    }

    private void register($.TypeConverter typeConverter, Class fromType, Class toType) {
        $.Pair<Class, Class> key = $.Pair(fromType, toType);
        register(typeConverter, key);
    }

    private void register($.TypeConverter typeConverter, $.Pair<Class, Class> key) {
        addIntoPath(key, typeConverter);
        buildPaths(typeConverter, key.left(), key.right());
    }

    private void registerBuiltInConverters() {
        for (Class<? extends Number> numberClass: N.NUMBER_CLASSES) {
            addIntoPath(keyOf(numberClass, Number.class), new $.TypeConverter(numberClass, Number.class) {
                @Override
                public Object convert(Object o) {
                    return o;
                }
            });
        }
        for (Field field : $.TypeConverter.class.getFields()) {
            if ($.TypeConverter.class.isAssignableFrom(field.getType())) {
                try {
                    $.TypeConverter converter = $.cast(field.get(null));
                    register(converter);
                } catch (IllegalAccessException e) {
                    throw E.unexpected(e);
                }
            }
        }
        for (final Map.Entry<Class, Object> nullValEntry : NULL_VALS.entrySet()) {
            register(new $.TypeConverter<Void, Object>(Void.class, nullValEntry.getKey()) {
                @Override
                public Object convert(Void aVoid) {
                    return nullValEntry.getValue();
                }
            });
        }
        register(NULL_CONVERTER);
    }

    private $.Pair<Class, Class> keyOf(Class<?> from, Class<?> to) {
        return $.cast($.Pair(from, to));
    }

    private $.Pair<Class, Class> keyOf($.TypeConverter typeConverter) {
        return $.Pair(typeConverter.fromType, typeConverter.toType);
    }

    private Set<$.Pair<Class, Class>> allKeyOf($.TypeConverter typeConverter) {
        Set<$.Pair<Class, Class>> set = new HashSet<>();
        Class fromType = typeConverter.fromType;
        Class toType = typeConverter.toType;
        set.add($.Pair(fromType, toType));
        for (Class intf : $.interfacesOf(toType)) {
            set.add($.Pair(fromType, intf));
        }
        for (Class parent : $.superClassesOf(toType)) {
            set.add($.Pair(fromType, parent));
        }
        return set;
    }

    private void buildPaths($.TypeConverter typeConverter) {
        buildPaths(typeConverter, typeConverter.fromType, typeConverter.toType);
    }

    private void buildPaths($.TypeConverter typeConverter, Class fromType, Class toType) {
        Set<$.TypeConverter> upstreams = upstreams(fromType);
        for ($.TypeConverter upstream : upstreams) {
            $.TypeConverter chained = new ChainedConverter(upstream, typeConverter);
            $.Pair<Class, Class> key = keyOf(chained);
            $.TypeConverter current = paths.get(key);
            if (null == current || isShorterPath(chained, current)) {
                if (typeConverter.fromType.isAssignableFrom(upstream.fromType)){
                    register(typeConverter, key);
                } else {
                    register(chained, key);
                }
            }
        }
        Set<$.TypeConverter> downstreams = downstreams(toType);
        for ($.TypeConverter downstream : downstreams) {
            $.TypeConverter chained = new ChainedConverter(typeConverter, downstream);
            $.Pair<Class, Class> key = keyOf(chained);
            $.TypeConverter current = paths.get(key);
            if (null == current || isShorterPath(chained, current)) {
                if (downstream.toType.isAssignableFrom(typeConverter.toType)) {
                    register(typeConverter, key);
                } else {
                    register(chained, key);
                }
            }
        }
    }

    private Set<$.TypeConverter> upstreams(Class toType) {
        Set<$.TypeConverter> set = new HashSet<>();
        for (Map.Entry<$.Pair<Class, Class>, $.TypeConverter> entry : paths.entrySet()) {
            if (toType.isAssignableFrom(entry.getKey().right())) {
                set.add(entry.getValue());
            }
        }
        return set;
    }

    private Set<$.TypeConverter> downstreams(Class fromType) {
        Set<$.TypeConverter> set = new HashSet<>();
        for (Map.Entry<$.Pair<Class, Class>, $.TypeConverter> entry : paths.entrySet()) {
            if (entry.getKey().left().isAssignableFrom(fromType)) {
                set.add(entry.getValue());
            }
        }
        return set;
    }

    private void addIntoPath($.Pair<Class, Class> key, $.TypeConverter converter) {
        paths.put(key, converter);
        Class<?> toType = key.right();
        if (Number.class.isAssignableFrom(toType)) {
            Class<?> primitiveToType = $.primitiveTypeOf(toType);
            if (null != primitiveToType && toType != primitiveToType) {
                addIntoPath(key.set2(primitiveToType), converter);
            }
        }
    }


    private static class ChainedConverter extends $.TypeConverter {

        private final $.TypeConverter upstream;
        private final $.TypeConverter downstream;

        public ChainedConverter($.TypeConverter upstream, $.TypeConverter downStream) {
            super(upstream.fromType, downStream.toType);
            this.upstream = upstream;
            this.downstream = downStream;
        }

        @Override
        public Object convert(Object o) {
            return downstream.convert(upstream.convert(o));
        }

        @Override
        public String toString() {
            return S.concat(upstream, " | ", downstream);
        }
    }

    private static boolean isShorterPath($.TypeConverter left, $.TypeConverter right) {
        int leftHops = hops(left), rightHops = hops(right);
        return leftHops < rightHops;
    }

    private static int hops($.TypeConverter typeConverter) {
        if (!(typeConverter instanceof ChainedConverter)) {
            return distance(typeConverter);
        }
        ChainedConverter chainedConverter = $.cast(typeConverter);
        return hops(chainedConverter.upstream) + hops(chainedConverter.downstream);
    }

    private static int distance($.TypeConverter typeConverter) {
        return distance(typeConverter.fromType) + distance(typeConverter.toType);
    }

    private static int distance(Class<?> type) {
        if (type == Object.class) {
            return 100;
        }
        if (type.isInterface()) {
            Set<Class> interfaces = $.interfacesOf(type);
            return 100 - interfaces.size();
        }
        if (type.isArray()) {
            return distance(type.getComponentType());
        }
        if ($.isSimpleType(type)) {
            return 0;
        }
        return distance(type.getSuperclass()) - 1;
    }

}
