package org.osgl;

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

import org.osgl.cache.CacheService;
import org.osgl.cache.impl.InteralCacheService;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.*;
import org.osgl.util.algo.StringReplace;
import org.osgl.util.algo.StringSearch;

import java.util.*;
import java.util.regex.Pattern;

public class OsglConfig {

    private static CacheService internalCache = new InteralCacheService();

    public static void setInternalCache(CacheService cache) {
        internalCache = $.requireNotNull(cache);
    }

    public static CacheService internalCache() {
        return internalCache;
    }

    /**
     * Default string search logic
     */
    public static StringSearch DEF_STRING_SEARCH = new StringSearch.SimpleStringSearch();

    /**
     * Default string replace logic
     */
    public static StringReplace DEF_STRING_REPLACE = new StringReplace.SimpleStringReplace();

    public static $.Function<Class, ?> INSTANCE_FACTORY = new $.Function<Class, Object>() {
        @Override
        public Object apply(Class aClass) throws NotAppliedException, Lang.Break {
            if (List.class == aClass) {
                return new ArrayList<>();
            } else if (Map.class == aClass) {
                return new HashMap<>();
            } else if (Set.class == aClass) {
                return new HashSet<>();
            } else if (SortedSet.class == aClass) {
                return new TreeSet<>();
            } else if (SortedMap.class == aClass) {
                return new TreeMap<>();
            } else if (C.Map.class == aClass) {
                return C.newMap();
            } else if (C.List.class == aClass) {
                return C.newList();
            }
            return $.newInstance(aClass);
        }
    };

    public static $.Function<Class, ?> globalInstanceFactory() {
        return INSTANCE_FACTORY;
    }

    public static void registerGlobalInstanceFactory($.Function<Class, ?> instanceFactory) {
        INSTANCE_FACTORY = $.requireNotNull(instanceFactory);
    }

    private static final Set<String> immutableClassNames = new HashSet<>();
    private static $.Predicate<Class> immutableClassPredicate = $.F.no();
    static {
        immutableClassNames.addAll(IO.read(OsglConfig.class.getResource("immutable-classes.list")).toLines());
    }

    public static void registerImmutableClassNames(Collection<String> immutableClassNames) {
        OsglConfig.immutableClassNames.addAll(immutableClassNames);
    }

    public static void registerImmutableClassPredicate($.Predicate<Class> predicate) {
        immutableClassPredicate = $.requireNotNull(predicate);
    }

    static boolean isImmutable(Class<?> c) {
        return immutableClassNames.contains(c.getName()) || immutableClassPredicate.test(c);
    }


    private static Set<String> mappingDisabledFields = new HashSet<>();
    private static List<$.Predicate<String>> mappingDisabledFieldPredicates = new ArrayList<>();

    /**
     * Check if a field name or map key should be ignored by global filter in mapping process.
     * @param s a field name or map key
     * @return `true` if the field name or map key should be ignored in mapping process.
     */
    public static boolean globalMappingFilter_shouldIgnore(String s) {
        if (mappingDisabledFields.contains(s)) {
            return true;
        }
        if (mappingDisabledFieldPredicates.isEmpty()) {
            return false;
        }
        for ($.Predicate<String> tester : mappingDisabledFieldPredicates) {
            if (tester.test(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Register global mapping filters.
     *
     * @param filterSpec
     *      the first filter spec
     * @param filterSpecs
     *      other filter specs
     * @see #addGlobalMappingFilter(String)
     */
    public static void addGlobalMappingFilters(String filterSpec, String ... filterSpecs) {
        addGlobalMappingFilter(filterSpec);
        for (String s : filterSpecs) {
            addGlobalMappingFilter(s);
        }
    }

    /**
     * Register global mapping filters
     *
     * @param filterSpecs
     *      A collection of filter specs
     * @see #addGlobalMappingFilter(String)
     */
    public static void addGlobalMappingFilters(Collection<String> filterSpecs) {
        for (String filter : filterSpecs) {
            addGlobalMappingFilter(filter);
        }
    }

    /**
     * Register a global mapping filter. Unlike normal mapping filter, global mapping filter spec
     *
     * * checks only immediate level field name or map key
     * * it is always a black list filter
     * * it supports different matching verb:
     *      - `contains:<payload>` - if test string contains `payload` then it shall be ignored
     *      - `starts:<payload>` - if test string starts with `payload` then it shall be ignored
     *      - `ends:<payload>` - if test string ends with `payload` then it shall be ignored
     *      - `reg:<payload>` - if test string matches reg exp pattern `payload` then it shall be ignored
     * * if the filter spec dose not fall in the above category, then it is added into a set, and
     *   if any test string exactly matches any item in that set, it shall be ignored.
     *
     * @param filterSpec
     *      the filter spec. It can contains multiple filter specs separated by `,`
     */
    public static void addGlobalMappingFilter(String filterSpec) {
        List<String> list = S.fastSplit(filterSpec, ",");
        for (String s : list) {
            if (S.blank(s)) {
                continue;
            }
            addSingleGlobalMappingFilter(s.trim());
        }
    }

    private static void addSingleGlobalMappingFilter(String filterSpec) {
        E.illegalArgumentIf(S.blank(filterSpec), "Invalid filter: " + filterSpec);
        if (filterSpec.startsWith("contains:")) {
            String text = filterSpec.substring(9);
            E.illegalArgumentIf(S.blank(text), "Invalid filter: " + filterSpec);
            mappingDisabledFieldPredicates.add(S.F.contains(text));
        } else if (filterSpec.startsWith("reg:")) {
            String text = filterSpec.substring(4);
            E.illegalArgumentIf(S.blank(text), "Invalid filter: " + filterSpec);
            final Pattern pattern = Pattern.compile(text);
            mappingDisabledFieldPredicates.add(new Lang.Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return pattern.matcher(s).matches();
                }
            });
        } else if (filterSpec.startsWith("starts:")) {
            String text = filterSpec.substring(7);
            E.illegalArgumentIf(S.blank(text), "Invalid filter: " + filterSpec);
            mappingDisabledFieldPredicates.add(S.F.startsWith(text));
        } else if (filterSpec.startsWith("ends:")) {
            String text = filterSpec.substring(5);
            E.illegalArgumentIf(S.blank(text), "Invalid filter: " + filterSpec);
            mappingDisabledFieldPredicates.add(S.F.endsWith(text));
        } else {
            mappingDisabledFields.add(filterSpec);
        }
    }

    public static void setThreadLocalBufferLimit(int limit) {
        UtilConfig.setThreadLocalBufferLimit(limit);
    }


    public static void setThreadLocalCharBufferLimit(int limit) {
        UtilConfig.setThreadLocalCharBufferLimit(limit);
    }

    public static int getThreadLocalCharBufferLimit() {
        return UtilConfig.getThreadLocalCharBufferLimit();
    }

    public static void setThreadLocalCharBufferInitSize(int size) {
        UtilConfig.setThreadLocalCharBufferInitSize(size);
    }

    public static int getThreadLocalCharBufferInitSize() {
        return UtilConfig.getThreadLocalCharBufferInitSize();
    }

    public static void setThreadLocalByteArrayBufferLimit(int limit) {
        UtilConfig.setThreadLocalByteArrayBufferLimit(limit);
    }

    public static int getThreadLocalByteArrayBufferLimit() {
        return UtilConfig.getThreadLocalByteArrayBufferLimit();
    }

    public static void setThreadLocalByteArrayBufferInitSize(int size) {
        UtilConfig.setThreadLocalByteArrayBufferInitSize(size);
    }

    public static int getThreadLocalByteArrayBufferInitSize() {
        return UtilConfig.getThreadLocalByteArrayBufferInitSize();
    }
}
