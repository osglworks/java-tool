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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.osgl.$;
import org.osgl.Lang;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.N;
import org.osgl.util.S;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class TypeConverterRegistry {

    private static class Node {
        private Comparator<Node> nodeComparator = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int delta = distanceTo(o1.type) - distanceTo(o2.type);
                if (0 != delta) {
                    return delta;
                }
                return o1.type.getName().compareTo(o2.type.getName());
            }

            private int distanceTo(Class<?> target) {
                return distanceBetween(type, target);
            }
        };
        private Comparator<Link> linkComparator = new Comparator<Link>() {
            @Override
            public int compare(Link o1, Link o2) {
                if (o1 == o2) {
                    return 0;
                }
                int n = o1.converter.hops() - o2.converter.hops();
                if (n != 0) {
                    return n;
                }
                Node from1 = o1.from;
                Node to1 = o1.to;
                Node from2 = o2.from;
                Node to2 = o2.to;
                n = distanceBetween(from1.type, to1.type) - distanceBetween(from2.type, to2.type);
                if (n != 0) {
                    return n;
                }
                if (from1 == from2) {
                    return to1.toString().compareTo(to2.toString());
                } else if (to1 == to2) {
                    return from1.toString().compareTo(from2.toString());
                } else {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        };
        private Class<?> type;
        private SortedSet<Node> superTypes = new TreeSet<>(nodeComparator);
        private SortedSet<Node> subTypes = new TreeSet<>(nodeComparator);
        private SortedSet<Link> fanIn = new TreeSet<>(linkComparator);
        private HashMap<Node, Link> fanOut = new HashMap<>();

        private Node(Class<?> type, TypeConverterRegistry registry) {
            this.type = $.requireNotNull(type);
            this.exploreSuperTypes(registry);
        }

        @Override
        public String toString() {
            return S.fmt("[%s]", type.getName());
        }

        private void addFanIn(Link link) {
            fanIn.add(link);
            for (Node superType : superTypes) {
                superType.fanIn.add(link);
            }
        }

        private void addFanOut(Link link) {
            Link existing = fanOut.get(link.to);
            if (null == existing || linkComparator.compare(link, existing) < 0) {
                fanOut.put(link.to, link);
                for (Node subType : subTypes) {
                    existing = subType.fanOut.get(link.to);
                    if (null == existing || linkComparator.compare(link, existing) < 0) {
                        subType.fanOut.put(link.to, link);
                    }
                }
            }
        }

        private List<Link> fanOutLinks() {
            List<Link> links = new ArrayList<>();
            links.addAll(fanOut.values());
            Collections.sort(links, linkComparator);
            return links;
        }

        private void exploreSuperTypes(TypeConverterRegistry registry) {
            Set<Class> interfaces = $.interfacesOf(type);
            for (Class intf : interfaces) {
                if (intf == Serializable.class || intf == Comparable.class) {
                    continue;
                }
                Node superNode = registry.nodeOf(intf);
                superTypes.add(superNode);
                superNode.subTypes.add(this);
            }
            Class<?> superType = type.getSuperclass();
            while (superType != null && superType != Object.class) {
                Node superNode = registry.nodeOf(superType);
                superTypes.add(superNode);
                superNode.subTypes.add(this);
                superType = superType.getSuperclass();
            }
        }

        private Link pathTo(Node target, TypeConverterRegistry registry, Set<Node> crDetector4To, Set<Node> crDetector4From) {
            Link directLink = fanOut.get(target);
            if (null != directLink) {
                return directLink;
            }
            SortedSet<Link> candidates = new TreeSet<>(linkComparator);
            exploreLinks(candidates, target, registry, crDetector4To, crDetector4From);
            return candidates.isEmpty() ? null : candidates.iterator().next();
        }

        private void exploreLinks(Set<Link> linkJar, Node target, TypeConverterRegistry registry, Set<Node> crDetector4To, Set<Node> crDetector4From) {
            Link direct = fanOut.get(target);
            if (null != direct) {
                linkJar.add(direct);
                return;
            }
            for (Link link : fanOutLinks()) {
                if (link.to == this || crDetector4To.contains(link.to) || crDetector4From.contains(link.from)) {
                    continue;
                }
                crDetector4To.add(link.to);
                crDetector4From.add(link.from);
                Link downstream = link.to.pathTo(target, registry, crDetector4To, crDetector4From);
                crDetector4To.remove(link.to);
                crDetector4From.remove(link.from);
                if (null != downstream) {
                    linkJar.add(link.cascadeWith(downstream, registry));
                }
            }
            for (Node superType : superTypes) {
                superType.exploreLinks(linkJar, target, registry, crDetector4To, crDetector4From);
            }
        }

    }

    private static class Link {
        private Node from;
        private Node to;
        private $.TypeConverter converter;
        private Link($.TypeConverter converter, TypeConverterRegistry registry) {
            this.from = registry.nodeOf(converter.fromType);
            this.to = registry.nodeOf(converter.toType);
            this.converter = converter;
            this.from.addFanOut(this);
            this.to.addFanIn(this);
        }

        @Override
        public String toString() {
            return S.fmt("%s => %s", from, to);
        }

        private Link cascadeWith(Link downstream, TypeConverterRegistry registry) {
            E.unexpectedIfNot(downstream.isSource(this.to));
            $.TypeConverter chained = new ChainedConverter(this.converter, downstream.converter);
            return new Link(chained, registry);
        }

        private boolean isSource(Node node) {
            if (node == from || from.subTypes.contains(node)) {
                return true;
            }
            if (from.type.isAssignableFrom(node.type)) {
                from.subTypes.add(node);
                return true;
            }
            return false;
        }

        private boolean isTarget(Node node) {
            return (node == to || node.superTypes.contains(to));
        }
    }

    public static $.TypeConverter ME_TO_ME = new $.TypeConverter(false) {
        @Override
        public Object convert(Object o) {
            return o;
        }
    };

    private Map<Class, Node> nodeMap = new IdentityHashMap<>();

    private Map<$.TypeConverter, Link> linkMap = new IdentityHashMap<>();

    private synchronized Node nodeOf(Class<?> type) {
        Node node = nodeMap.get(type);
        if (null == node) {
            node = new Node(type, this);
            nodeMap.put(type, node);
        }
        return node;
    }

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

    public static final TypeConverterRegistry INSTANCE = new TypeConverterRegistry(true);

    private Map<$.Pair<Class, Class>, $.TypeConverter> paths = new HashMap<>();

    private TypeConverterRegistry parent;

    public TypeConverterRegistry() {
        this(false);
    }

    private TypeConverterRegistry(boolean isGlobalInstance) {
        if (isGlobalInstance) {
            registerBuiltInConverters();
        } else {
            parent = INSTANCE;
        }
    }

    public synchronized <FROM, TO> $.TypeConverter<FROM, TO> get(Class<FROM> fromType, Class<TO> toType) {
        fromType = fromType.isArray() ? fromType : $.wrapperClassOf(fromType);
        toType = toType.isArray() ? toType : $.wrapperClassOf(toType);
        if (fromType == toType || toType.isAssignableFrom(fromType)) {
            return ME_TO_ME;
        }
        $.Pair<Class, Class> key = keyOf(fromType, toType);
        $.TypeConverter converter = paths.get(key);
        if (null == converter) {
            Node node = nodeOf(fromType);
            Link link = node.pathTo(nodeOf(toType), this, new HashSet<Node>(), new HashSet<Node>());
            if (null != link) {
                paths.put(key, link.converter);
                return link.converter;
            }
        }
        if (null == converter) {
            if (null != parent) {
                converter = parent.get(fromType, toType);
            } else if (String.class == toType) {
                converter = $.TypeConverter.ANY_TO_STRING;
                paths.put(key, converter);
            } else if (Boolean.class == toType) {
                converter = $.TypeConverter.ANY_TO_BOOLEAN;
                paths.put(key, converter);
            } else if (JSONObject.class == toType) {
                converter = $.TypeConverter.ANY_TO_JSON_OBJECT;
                paths.put(key, converter);
            } else if (JSONArray.class == toType) {
                converter = $.TypeConverter.ANY_TO_JSON_ARRAY;
                paths.put(key, converter);
            }
        }
        if (null == converter) {
            if (Enum.class.isAssignableFrom(toType)) {
                converter = Lang.TypeConverter.stringToEnum((Class<Enum>) toType);
                if (String.class != fromType) {
                    converter = new ChainedConverter($.TypeConverter.ANY_TO_STRING, converter);
                }
                paths.put(key, converter);
            }
        }
        return converter;
    }

    public synchronized TypeConverterRegistry register($.TypeConverter typeConverter) {
        if (!linkMap.containsKey(typeConverter)) {
            linkMap.put(typeConverter, new Link(typeConverter, this));
            $.Pair<Class, Class> key = keyOf(typeConverter);
            addIntoPath(key, typeConverter);
        }
        return this;
    }

    public int size() {
        return paths.size();
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
            addIntoPath(keyOf(numberClass, String.class), Lang.TypeConverter.ANY_TO_STRING);
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

    private List<$.Pair<Class, Class>> allKeyOf($.TypeConverter typeConverter) {
        List<$.Pair<Class, Class>> set = new ArrayList<>();
        Class fromType = typeConverter.fromType;
        Class toType = typeConverter.toType;
        set.add($.Pair(fromType, toType));
        for (Class intf : $.interfacesOf(toType)) {
            if (intf == Comparable.class || intf == Serializable.class) {
                continue;
            }
            set.add($.Pair(fromType, intf));
        }
        for (Class parent : $.superClassesOf(toType)) {
            if (parent == Object.class) {
                continue;
            }
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
        //System.out.println(S.fmt(">>>>> add [%s] into path", key));
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
        public int hops() {
            return upstream.hops() + downstream.hops();
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
            return 1000;
        }
        if (type.isInterface()) {
            Set<Class> interfaces = $.interfacesOf(type);
            return 999 - interfaces.size();
        }
        if (type.isArray()) {
            return distance(type.getComponentType());
        }
        if ($.isSimpleType(type)) {
            return 0;
        }
        return distance(type.getSuperclass()) - 1;
    }

    private static int distanceBetween(Class<?> source, Class<?> target) {
        return Math.abs(distance(source) - distance(target));
    }

}
