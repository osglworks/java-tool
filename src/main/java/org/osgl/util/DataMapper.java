package org.osgl.util;

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

import static org.osgl.util.DataMapper.Rule.KEYWORD_MATCHING;
import static org.osgl.util.DataMapper.Rule.STRICT_NAME_TYPE;

import org.osgl.$;
import org.osgl.OsglConfig;
import org.osgl.exception.MappingException;
import org.osgl.util.converter.TypeConverterRegistry;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Map data from one structure into another structure. The target data structure
 * must be exists and must be mutable to do the data mapping.
 *
 * ## Error reporting
 *
 * `DataMapper` consumer can dictate on how `DataMapper` to handle exceptions by
 * passing boolean value `ignoreError` to constructor.
 *
 * If `ignoreError` is set to `true` then `DataMapper` will not raise error for
 * any exception raised during mapping process. It will simply skip to the next
 * field.
 *
 * Otherwise it will raise {@link MappingException} when
 *
 * * Exceptions raised
 * * Type conversion failed
 *
 * ## Mapping rule
 *
 * Three mapping rule has been defined:
 *
 * * {@link Rule#STRICT_NAME_TYPE}
 * - mapping only happens between exact same name and type
 * * {@link Rule#STRICT_NAME}
 * - mapping happens between exact same name, and do type conversion if needed
 * * {@link Rule#KEYWORD_MATCHING}
 * - mapping happens if the {@link Keyword} of source field name
 * and target field name matches. For example `foo_bar` can be mapped to `fooBar`.
 *
 * ## Field name based mapping
 *
 * `DataMapper` do field name based mapping, in other words, it does not rely
 * on JavaBean properties to do the mapping. For example if source object
 * has a field name `foo`, the value of `source.foo` will be mapped to
 * the target object's field `foo`: `target.foo`.
 *
 * ## Recursive mapping
 *
 * `DataMapper` do recursive mapping. If target object has embedded structure
 * and source object has the corresponding value, then that value will be
 * mapped to target object's embedded structure recursively.
 *
 * ## Type conversion
 *
 * When mapping rule is not {@link Rule#STRICT_NAME_TYPE}, the mapper will try to
 * convert from source type to target type when types doesn't match. Otherwise
 * it will raise an {@link MappingException}.
 *
 * ## Mapping to array
 *
 * For target field that is an array, tt will first try to do type conversion
 * in case special converter has been defined.
 *
 * If there is not converter defined or when root mapping target is an array,
 * then it will check if source component can be converted to an {@link Iterable}.
 * If it's okay then it will add elements from the iterable to the target array.
 * In case element exists in the target array then it will do recursive mapping
 * from the source element to the target element.
 *
 * If source array/collection has more elements that the target array can hold,
 * then the extra elements will be ignored
 *
 * ## Mapping to a List
 *
 * Determine the list element type. If this is a recursive mapping, list element
 * type can be found from the field type of upper level target. Otherwise, it
 * will try to determine list type from the existing elements of the list. If
 * there is no element in the list then it assume list can be put in any type
 * of object.
 *
 * For target field that is an list, it will first try to do type conversion
 * in case special converter has been defined.
 *
 * If there is no converter defined, then it will check if source component can be
 * converted to an {@link Iterable}. If it's okay then it will add elements from
 * the iterable to the target list. In case the element in the position is not
 * null in the target list, it will do recursive mapping from the source element
 * to the target element.
 *
 * If source array/collection has more elements than the target list, then
 * the extra elements will be added to the list
 *
 * ## Mapping to a Set
 *
 * Determine the set element type. If this is a recursive mapping, set element
 * type can be found from the field type of upper level target. Otherwise, it
 * will try to determine list type from the existing elements of the set. If
 * there is no element in the list then it assume set can be put in any type
 * of object.
 *
 * For target field that is a set, it will first try to do type conversion in
 * case special converter has been defined.
 *
 * If there is no converter defined, then it will check if source component can be
 * converted to an {@link Iterable}. If it's okay then it will add elements from
 * the iterable to the target set. Unlike mapping to an array or a list, there is
 * no recursive element mapping happening for set, it just add the elements from
 * source iterable into the target set, after type conversion if needed.
 *
 * ## Mapping to a Map
 *
 * When mapping to a map, it will check if target map has existing non-null value
 * for a certain property, if it exists, then it will do recursive mapping from
 * property value to the target map entry value.
 *
 * If there are extra properties in the source object, they will be added into the
 * target map if the type can be converted.
 *
 * When mapping to a map, the {@link Rule#KEYWORD_MATCHING} won't effect, it is
 * treated as {@link Rule#STRICT_NAME}
 *
 * ## Mapping from a Map
 *
 * Again it will try to do type conversion to allow custom data mapping for certain types
 * happening. In case no type conversion is done then
 *
 * It treats map entry as fields of a class, literally the entry is treated as the field name
 * and entry value is treated as field value. And then start from there to do the
 * data mapping process.
 *
 * ## `null` value
 *
 * If `null` value encountered in source, the corresponding field/map entry will be set to `null`
 * in the target.
 */
public class DataMapper {

    public enum Rule {
        /**
         * Both field name and type must match exactly.
         */
        STRICT_NAME_TYPE,

        /**
         * field name must match exactly.
         */
        STRICT_NAME,

        /**
         * field name match by {@link Keyword} equality.
         *
         * For example, the
         */
        KEYWORD_MATCHING;

        public boolean keywordMatching() {
            return this == KEYWORD_MATCHING;
        }
    }

    class PropertyFilter extends $.Predicate<String> {

        /**
         * Keep a set of properties that can be copied.
         *
         * Note if both {@link #whiteList} and `blackList` contains
         * elements, then `whiteList` is ignored.
         */
        private Set<String> whiteList = C.set();

        /**
         * Keep a set of properties that shall not be copied.
         */
        private Set<String> blackList = C.set();

        /**
         * Contains the {@link Keyword} correspondence of
         * {@link #whiteList}.
         */
        private Set<Keyword> whiteKeywords = C.set();

        /**
         * Contains the {@link Keyword} correspondence of
         * {@link #blackList}
         */
        private Set<Keyword> blackKeywords = C.set();

        private boolean allEmpty = true;

        PropertyFilter(String spec) {
            if (S.blank(spec)) {
                return;
            }
            List<String> words = S.fastSplit(spec, ",");
            boolean useBlackList = false;
            for (String word : words) {
                if (useBlackList && !word.startsWith("-")) {
                    // ignore black list
                    continue;
                }
                if (word.startsWith("-")) {
                    useBlackList = true;
                    word = word.substring(1);
                }
                word = word.trim();
                if (useBlackList) {
                    if (rule == Rule.KEYWORD_MATCHING) {
                        if (blackKeywords == C.EMPTY_SET) {
                            blackKeywords = new HashSet<>();
                        }
                        blackKeywords.add(Keyword.of(word));
                    } else {
                        if (blackList == C.EMPTY_SET) {
                            blackList = new HashSet<>();
                        }
                        blackList.add(word);
                    }
                } else {
                    if (rule == Rule.KEYWORD_MATCHING) {
                        if (whiteKeywords == C.EMPTY_SET) {
                            whiteKeywords = new HashSet<>();
                        }
                        whiteKeywords.add(Keyword.of(word));
                    } else {
                        if (whiteList == C.EMPTY_SET) {
                            whiteList = new HashSet<>();
                        }
                        whiteList.add(word);
                    }
                }
            }
            if (useBlackList) {
                whiteKeywords = C.set();
                whiteList = C.set();
            }
            allEmpty = whiteKeywords.isEmpty() && whiteList.isEmpty() && blackKeywords.isEmpty() && blackList.isEmpty();
        }


        @Override
        public boolean test(String s) {
            E.illegalArgumentIf(S.blank(s));
            if (allEmpty) {
                return true;
            }
            String prefix = context.toString();
            if (S.notBlank(prefix)) {
                s = S.pathConcat(prefix, '.', s);
            }
            if (rule == KEYWORD_MATCHING) {
                Keyword keyword = Keyword.of(s);
                return blackKeywords.isEmpty() ? whiteKeywords.contains(keyword) : !blackKeywords.contains(keyword);
            } else {
                return blackList.isEmpty() ? whiteList.contains(s) : !blackList.contains(s);
            }
        }

    }

    /**
     * Default mapping rule is {@link Rule#KEYWORD_MATCHING}
     */
    private Rule rule;

    /**
     * Keep track the object hierarchies
     */
    private StringBuilder context = new StringBuilder();

    /**
     * Decide whether copy a field or not
     */
    private PropertyFilter filter;

    private Object source;
    private Class<?> sourceType;

    private Object target;
    private Class<?> targetType;
    private Class<?> targetComponentType;
    private Class<?> targetKeyType;

    /**
     * convert hints indexed by type
     */
    Map<Class, Object> conversionHints;

    /**
     * used to create new instance of certain type
     */
    $.Function<Class, ?> instanceFactory;

    /**
     * Allow inject custom type converters
     */
    TypeConverterRegistry typeConverterRegistry;

    /**
     * If set to `false` then it will raise {@link org.osgl.exception.MappingException}
     * whenever an error is encountered during mapping process.
     */
    private boolean ignoreError;

    public DataMapper(Object source, Object target, Class<?> targetKeyType, Class<?> targetComponentType, Rule rule, String filterSpec, boolean ignoreError, Map<Class, Object> conversionHints, $.Function<Class, ?> instanceFactory, TypeConverterRegistry typeConverterRegistry) {
        this.targetType = target.getClass();
        E.illegalArgumentIf(isImmutable(targetType), "target type is immutable: " + targetType.getName());
        this.sourceType = source.getClass();
        if (rule == STRICT_NAME_TYPE) {
            if (!targetType.isAssignableFrom(sourceType)) {
                logError("Type mismatch. Source type: %s; Target type: %s", sourceType.getName(), targetType.getName());
                return;
            }
        }
        this.targetKeyType = targetKeyType;
        this.targetComponentType = targetComponentType;
        this.rule = $.requireNotNull(rule);
        this.filter = new PropertyFilter(filterSpec);
        this.conversionHints = null == conversionHints ? C.<Class, Object>Map() : conversionHints;
        this.instanceFactory = null == instanceFactory ? OsglConfig.INSTANCE_FACTORY : instanceFactory;
        this.source = source;
        this.target = target;
        this.ignoreError = ignoreError;
        this.typeConverterRegistry = null == typeConverterRegistry ? TypeConverterRegistry.INSTANCE : typeConverterRegistry;
        this.doMapping();
    }

    private DataMapper(Object source, Object target, String targetName, Class targetKeyType, Class targetComponentType, DataMapper parentMapper) {
        this.sourceType = source.getClass();
        this.source = source;
        this.targetType = target.getClass();
        this.target = target;
        this.targetKeyType = targetKeyType;
        this.targetComponentType = targetComponentType;
        this.rule = parentMapper.rule;
        this.filter = parentMapper.filter;
        this.ignoreError = parentMapper.ignoreError;
        this.conversionHints = parentMapper.conversionHints;
        this.instanceFactory = parentMapper.instanceFactory;
        this.typeConverterRegistry = parentMapper.typeConverterRegistry;
        this.context = new StringBuilder();
        String parentContext = parentMapper.context.toString();
        this.context.append(parentContext);
        if (S.notBlank(targetName)) {
            if (S.notBlank(parentContext)) {
                this.context.append(".").append(targetName);
            } else {
                this.context.append(targetName);
            }
        } else {
            // this case is the array or collection element copy
        }
        this.doMapping();
    }

    public Object getTarget() {
        return target;
    }

    private void doMapping() {
        boolean targetIsArray = targetType.isArray();
        boolean targetIsCollection = !targetIsArray && Collection.class.isAssignableFrom(targetType);
        if (targetIsArray || targetIsCollection) {
            mapToArrayOrCollection(targetIsArray);
        } else {
            boolean targetIsMap = Map.class.isAssignableFrom(targetType);
            if (targetIsMap) {
                mapToMap();
            } else {
                mapToPojo();
            }
        }
    }

    /*
     * Array mapping require the source type to
     * be sequenced, i.e. array or iterable
     */
    private void mapToArrayOrCollection(boolean targetIsArray) {
        Collection targetCollection = null;
        List targetList = null;
        int targetLen = 0;
        boolean targetIsList = false;
        if (targetIsArray) {
            targetLen = Array.getLength(target);
            if (targetLen == 0) {
                return;
            }
            targetComponentType = targetType.getComponentType();
        } else {
            targetCollection = (Collection) target;
            if (List.class.isAssignableFrom(targetType)) {
                targetIsList = true;
                targetList = (List) target;
                targetLen = targetList.size();
            }
            if (null == targetComponentType) {
                targetComponentType = $.commonSuperTypeOf(targetCollection);
            }
        }
        Iterable sourceIterable;
        if (!isSequence(sourceType)) {
            // try convert source to an iterable
            sourceIterable = convertSourceTo(Iterable.class);
            if (null == sourceIterable) {
                if (rule != Rule.STRICT_NAME_TYPE) {
                    List pseudoList = new ArrayList<>();
                    // try to treat the source object as source component
                    if (!targetComponentType.isAssignableFrom(sourceType)) {
                        Object convertedSource = convertSourceTo(targetComponentType);
                        if (null != convertedSource) {
                            pseudoList.add(convertedSource);
                        } else {
                            logMappingFailure();
                            return;
                        }
                    } else {
                        pseudoList.add(source);
                    }
                    sourceIterable = pseudoList;
                } else {
                    logMappingFailure();
                    return;
                }
            }
        } else {
            sourceIterable = convertSourceTo(Iterable.class);
        }
        // now try to map from sourceIterable to target
        Iterator itr = sourceIterable.iterator();
        if (targetIsArray || null != targetList) {
            int cursor = 0;
            while (cursor < targetLen && itr.hasNext()) {
                Object sourceComponent = itr.next();
                if (null == sourceComponent) {
                    if (targetIsArray) {
                        Array.set(target, cursor, null);
                    } else {
                        targetList.set(cursor, null);
                    }
                    continue;
                }
                Object targetComponent = convert(sourceComponent).to(targetComponentType);
                if (null != targetComponent) {
                    if (targetIsList) {
                        targetList.set(cursor, targetComponent);
                    } else {
                        Array.set(target, cursor, targetComponent);
                    }
                } else {
                    targetComponent = targetIsList ? targetList.get(cursor) : Array.get(target, cursor);
                    if (null == targetComponent) {
                        targetComponent = instanceFactory.apply(targetComponentType);
                        if (targetIsList) {
                            targetList.set(cursor, targetComponent);
                        } else {
                            Array.set(target, cursor, targetComponent);
                        }
                    }
                    new DataMapper(sourceComponent, targetComponent, "", null, targetComponentType, this);
                }
            }
        }
        if (!targetIsArray) {
            while (itr.hasNext()) {
                Object sourceComponent = itr.next();
                Object targetComponent = convert(sourceComponent).to(targetComponentType);
                if (null != targetComponent) {
                    targetCollection.add(targetComponent);
                }
            }
        }
    }

    private void mapToMap() {
        Map targetMap = (Map) target;
        if (null == targetKeyType) {
            targetKeyType = $.commonSuperTypeOf(targetMap.keySet());
            if (null == targetKeyType) {
                targetKeyType = String.class;
            }
        }
        if (null == targetComponentType) {
            targetComponentType = $.commonSuperTypeOf(targetMap.values());
            if (null == targetComponentType) {
                targetComponentType = Object.class;
            }
        }
        Map targetMapKeywordLookup = null;
        if (rule.keywordMatching() && String.class == targetKeyType) {
            targetMapKeywordLookup = new HashMap();
            for (Object key : targetMap.keySet()) {
                targetMapKeywordLookup.put(Keyword.of(key.toString()), key);
            }
        }
        for ($.Pair<Object, $.Producer<Object>> sourceProperty : sourceProperties()) {
            Object sourceKey = sourceProperty.left();
            Object sourceVal = sourceProperty.right();
            Object targetKey;
            if (targetMapKeywordLookup != null) {
                Keyword keywordTargetKey = Keyword.of(sourceKey.toString());
                targetKey = targetMapKeywordLookup.get(keywordTargetKey);
            } else {
                targetKey = convert(sourceKey).to(targetKeyType);
            }
            if (null == sourceVal) {
                targetMap.remove(targetKey);
                continue;
            }
            Object targetVal = convert(sourceVal).to(targetComponentType);
            if (null != targetVal) {
                targetMap.put(targetKey, targetVal);
            } else {
                targetVal = targetMap.get(targetKey);
                if (targetVal == null) {
                    targetVal = instanceFactory.apply(targetComponentType);
                }
                new DataMapper(sourceVal, targetVal, targetKey.toString(), targetKeyType, targetComponentType, this);
            }
        }
    }

    private void mapToPojo() {
        List<Field> targetFields = $.fieldsOf(targetType);
        String prefix = context.toString();
        Map<Object, Object> sourceMap = Map.class.isAssignableFrom(sourceType) ? (Map) source : null;
        Map<Keyword, Object> sourceMapByKeyword = null;
        if (rule.keywordMatching()) {
            sourceMapByKeyword = new HashMap<>();
            if (null != sourceMap) {
                for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
                    sourceMapByKeyword.put(Keyword.of(entry.getKey().toString()), entry.getValue());
                }
            } else {
                for (Field sourceField : $.fieldsOf(sourceType)) {
                    sourceMapByKeyword.put(Keyword.of(sourceField.getName()), sourceField);
                }
            }
        }
        for (Field targetField : targetFields) {
            Class<?> targetFieldType = targetField.getType();
            String targetFieldName = targetField.getName();
            String key = S.notBlank(prefix) ? S.pathConcat(prefix, '.', targetFieldName) : targetFieldName;
            if (!filter.test(key)) {
                continue;
            }
            Object sourcePropValue;
            if (null != sourceMapByKeyword) {
                sourcePropValue = sourceMapByKeyword.get(Keyword.of(targetFieldName));
                if (null == sourcePropValue) {
                    continue;
                }
                if (sourcePropValue instanceof Field) {
                    sourcePropValue = $.getFieldValue(source, (Field) sourcePropValue);
                }
            } else if (null != sourceMap) {
                sourcePropValue = sourceMap.get(targetFieldName);
            } else {
                Field sourceField = $.fieldOf(sourceType, targetFieldName);
                if (null == sourceField) {
                    continue;
                }
                sourcePropValue = $.getFieldValue(source, sourceField);
            }
            if (null == sourcePropValue) {
                $.setFieldValue(target, targetField, null);
            } else {
                Object targetFieldValue = convert(sourcePropValue).to(targetFieldType);
                if (null != targetFieldValue) {
                    $.setFieldValue(target, targetField, targetFieldValue);
                    continue;
                }
                targetFieldValue = $.getFieldValue(target, targetField);
                if (null == targetFieldValue) {
                    targetFieldValue = instanceFactory.apply(targetFieldType);
                    $.setFieldValue(target, targetField, targetFieldValue);
                }
                Class targetKeyType = null;
                Class targetComponentType = null;
                if (Map.class.isAssignableFrom(targetFieldType)) {
                    Type targetGenericType = targetField.getGenericType();
                    if (targetGenericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) targetGenericType;
                        Type[] ta = pt.getActualTypeArguments();
                        if (ta.length > 1) {
                            Type k = ta[0];
                            Type v = ta[1];
                            if (k instanceof Class) {
                                targetKeyType = (Class) k;
                            }
                            if (v instanceof Class) {
                                targetComponentType = (Class) v;
                            }
                        }
                    }
                } else if (Collection.class.isAssignableFrom(targetFieldType)) {
                    Type targetGenericType = targetField.getGenericType();
                    if (targetGenericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) targetGenericType;
                        Type[] ta = pt.getActualTypeArguments();
                        if (ta.length > 0) {
                            Type v = ta[0];
                            if (v instanceof Class) {
                                targetComponentType = (Class) v;
                            }
                        }
                    }
                }
                new DataMapper(sourcePropValue, targetFieldValue, targetFieldName, targetKeyType, targetComponentType, this);
            }
        }
    }

    private Iterable<$.Pair<Object, $.Producer<Object>>> sourceProperties() {
        if (Map.class.isAssignableFrom(sourceType)) {
            return C.list(((Map<Object, Object>) source).entrySet())
                    .filter(mapEntryFilter())
                    .map(new $.Transformer<Map.Entry, $.Pair<Object, $.Producer<Object>>>() {
                        @Override
                        public $.T2<Object, $.Producer<Object>> transform(final Map.Entry entry) {
                            $.Producer<Object> producer = new $.Producer<Object>() {
                                @Override
                                public Object produce() {
                                    return entry.getValue();
                                }
                            };
                            Object key = entry.getKey();
                            if (rule.keywordMatching() && key instanceof String) {
                                key = Keyword.of(key.toString());
                            }
                            return $.T2(key, producer);
                        }
                    });
        } else {
            final List<Field> fields = $.fieldsOf(sourceType);
            return C.list(fields)
                    .filter(fieldFilter())
                    .map(new $.Transformer<Field, $.Pair<Object, $.Producer<Object>>>() {
                        @Override
                        public $.Pair<Object, $.Producer<Object>> transform(final Field field) {
                            Object name = field.getName();
                            if (rule.keywordMatching()) {
                                name = Keyword.of(name.toString());
                            }
                            $.Producer<Object> producer = new $.Producer<Object>() {
                                @Override
                                public Object produce() {
                                    return $.getFieldValue(source, field);
                                }
                            };
                            return $.T2(name, producer);
                        }
                    });
        }
    }

    private $.Predicate<Map.Entry> mapEntryFilter() {
        if (filter.allEmpty) {
            return $.F.yes();
        }
        return new $.Predicate<Map.Entry>() {
            @Override
            public boolean test(Map.Entry entry) {
                String key = entry.getKey().toString();
                String prefix = context.toString();
                if (S.notBlank(prefix)) {
                    key = S.pathConcat(prefix, '.', key);
                }
                return filter.test(key);
            }
        };
    }

    private $.Predicate<Field> fieldFilter() {
        if (filter.allEmpty) {
            return $.F.yes();
        }
        return new $.Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                String key = field.getName();
                String prefix = context.toString();
                if (S.notBlank(prefix)) {
                    key = S.pathConcat(prefix, '.', key);
                }
                return filter.test(key);
            }
        };
    }

    private void logError(Throwable cause, String message, Object... messageArgs) {
        if (!ignoreError) {
            mappingError(cause, message, messageArgs);
        }
    }

    private void logError(String message, Object... messageArgs) {
        if (!ignoreError) {
            mappingError(message, messageArgs);
        }
    }

    private void logMappingFailure() {
        logError("Mapping failure");
    }

    private <T> T convertSourceTo(Class<T> type) {
        try {
            return convertStage(!ignoreError).to(type);
        } catch (Exception e) {
            logError(e, "Cannot convert source into " + type.getName());
            return null;
        }
    }

    private <T> T tryConvertSourceTo(Class<T> type) {
        return convertStage(false).to(type);
    }

    private $._ConvertStage<?> convertStage() {
        return convert(source, !ignoreError);
    }

    private $._ConvertStage<?> convertStage(boolean reportError) {
        return convert(source, reportError);
    }

    private $._ConvertStage<?> convert(Object source) {
        return convert(source, !ignoreError);
    }

    private $._ConvertStage<?> tryConvert(Object source) {
        return convert(source, false);
    }

    private $._ConvertStage<?> convert(Object source, boolean reportError) {
        $._ConvertStage stage = $.convert(source).customTypeConverters(typeConverterRegistry).hint(convertHintOf(sourceType));
        if (!reportError) {
            stage.reportError();
        }
        return stage;
    }

    private Object convertHintOf(Class type) {
        return conversionHints.get(type);
    }

    private void mappingError(Throwable cause, String message, Object... messageArgs) {
        throw new MappingException(source, target, cause, message, messageArgs);
    }

    private MappingException mappingError(String message, Object... messageArgs) {
        throw new MappingException(source, target, message, messageArgs);
    }

    private static boolean isSequence(Class<?> targetType) {
        return targetType.isArray() || Collection.class.isAssignableFrom(targetType);
    }

    private static Class elementTypeOf(Object o) {
        Class<?> type = o.getClass();
        if (type.isArray()) {
            return type.getComponentType();
        }
        Collection collection = (Collection) o;
        return $.commonSuperTypeOf(collection);
    }

    private static boolean isImmutable(Class<?> type) {
        return $.isSimpleType(type) || Date.class.isAssignableFrom(type);
    }

}
