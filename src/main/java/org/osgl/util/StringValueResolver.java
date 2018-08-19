package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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
import org.osgl.exception.NotAppliedException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * A String value resolver resolves a {@link String string value} into
 * a certain type of object instance.
 */
public abstract class StringValueResolver<T> extends $.F1<String, T> {

    private final Type targetType;

    protected Map<String, Object> attributes = new HashMap<String, Object>();

    public StringValueResolver() {
        targetType = findTargetType();
    }

    protected StringValueResolver(Class<T> targetType) {
        this.targetType = $.requireNotNull(targetType);
    }

    public abstract T resolve(String value);

    public Class<T> targetType() {
        return Generics.classOf(targetType);
    }

    public Type genericTargetType() {
        return targetType;
    }

    private Type findTargetType() {
        return findTargetType(getClass());
    }

    private static Type findTargetType(Class<?> clazz) {
        List<Type> typeParams = Generics.typeParamImplementations(clazz, StringValueResolver.class);
        if (typeParams.size() > 0) {
            return typeParams.get(0);
        }
        throw E.unsupport("Cannot identify the target type from %s", clazz);
    }

    @Override
    public final T apply(String s) throws NotAppliedException, $.Break {
        return resolve(s);
    }

    /**
     * Set attribute of this resolver.
     *
     * Note use this method only on new resolver instance instead of shared instance
     *
     * @param key the attribute key
     * @param value the attribute value
     * @return this resolver instance
     */
    public StringValueResolver<T> attribute(String key, Object value) {
        if (null == value) {
            attributes.remove(value);
        } else {
            attributes.put(key, value);
        }
        return this;
    }

    /**
     * Set attributes to this resolver
     *
     * Note use this method only on new resolver instance instead of shared instance
     *
     * @param attributes the attributes map
     * @return this resolver instance
     */
    public StringValueResolver<T> attributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

    /**
     * Clear all attributes on this resolver
     * @return this resolver instance
     */
    public StringValueResolver<T> clearAttributes() {
        attributes.clear();
        return this;
    }

    /**
     * Get attribute of this resolver by key specified
     * @param key the attribute key
     * @param <V> the generic type variable of attribute value
     * @return the attribute value
     */
    protected <V> V attribute(String key) {
        return (V) attributes.get(key);
    }

    /**
     * Returns an amended copy of this resolver based on the {@link AnnotationAware}
     * provided. This method allows resolver implementation to tune the
     * resolving logic when a certain annotation is provided
     *
     * By default return `this` resolver instance
     *
     * @param beanSpec the bean spec with annotation information
     * @return the amended copy of this resolver
     */
    public StringValueResolver<T> amended(AnnotationAware beanSpec) {
        return this;
    }

    public static <T> StringValueResolver<T> wrap(final $.Function<String, T> func, final Class<T> targetType) {
        if (func instanceof StringValueResolver) {
            return (StringValueResolver) func;
        } else {
            return new StringValueResolver<T>(targetType) {
                @Override
                public T resolve(String value) {
                    return func.apply(value);
                }
            };
        }
    }

    // For primary types
    private static final StringValueResolver<Boolean> _boolean = new StringValueResolver<Boolean>() {
        @Override
        public Boolean resolve(String value) {
            if (S.empty(value)) {
                return Boolean.FALSE;
            }
            return Boolean.parseBoolean(value);
        }
    };
    private static final StringValueResolver<Boolean> _Boolean = new StringValueResolver<Boolean>() {
        @Override
        public Boolean resolve(String value) {
            if (S.empty(value)) {
                return null;
            }
            return Boolean.parseBoolean(value);
        }
    };
    private static final Map<String, Character> PREDEFINED_CHARS = new HashMap<>();

    static {
        PREDEFINED_CHARS.put("\\b", '\b');
        PREDEFINED_CHARS.put("\\f", '\f');
        PREDEFINED_CHARS.put("\\n", '\n');
        PREDEFINED_CHARS.put("\\r", '\r');
        PREDEFINED_CHARS.put("\\t", '\t');
        PREDEFINED_CHARS.put("\\", '\"');
        PREDEFINED_CHARS.put("\\'", '\'');
        PREDEFINED_CHARS.put("\\\\", '\\');
    }

    /**
     * Parsing String into char. The rules are:
     *
     * 1. if there value is null or empty length String then return `defval` specified
     * 2. if the length of the String is `1`, then return that one char in the string
     * 3. if the value not starts with '\', then throw `IllegalArgumentException`
     * 4. if the value starts with `\\u` then parse the integer using `16` radix. The check
     *    the range, if it fall into Character range, then return that number, otherwise raise
     *    `IllegalArgumentException`
     * 5. if the value length is 2 then check if it one of {@link #PREDEFINED_CHARS}, if found
     *    then return
     * 6. check if it valid OctalEscape defined in the <a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">spec</a>
     *    if pass the check then return that char
     * 7. all other cases throw `IllegalArgumentException`
     *
     * @param value the string value to be resolved
     * @param defVal the default value when string value is `null` or empty
     * @return the char resolved from the string
     */
    private static Character resolveChar(String value, Character defVal) {
        if (null == value) {
            return defVal;
        }
        switch (value.length()) {
            case 0:
                return defVal;
            case 1:
                return value.charAt(0);
            default:
                if (value.startsWith("\\")) {
                    if (value.length() == 2) {
                        Character c = PREDEFINED_CHARS.get(value);
                        if (null != c) {
                            return c;
                        }
                    }
                    try {
                        String s = value.substring(1);
                        if (s.startsWith("u")) {
                            int i = Integer.parseInt(s.substring(1), 16);
                            if (i > Character.MAX_VALUE || i < Character.MIN_VALUE) {
                                throw new IllegalArgumentException("Invalid character: " + value);
                            }
                            return (char) i;
                        } else if (s.length() > 3) {
                            throw new IllegalArgumentException("Invalid character: " + value);
                        } else {
                            if (s.length() == 3) {
                                int i = Integer.parseInt(s.substring(0, 1));
                                if (i > 3) {
                                    throw new IllegalArgumentException("Invalid character: " + value);
                                }
                            }
                            int i = Integer.parseInt(s, 8);
                            return (char) i;
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid character: " + value);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid character: " + value);
                }
        }
    }

    /**
     * Returns the char resolver based on {@link #resolveChar(String, Character)} with `\0` as
     * default value
     */
    private static final StringValueResolver<Character> _char = new StringValueResolver<Character>() {
        @Override
        public Character resolve(String value) {
            return resolveChar(value, '\0');
        }
    };

    /**
     * Returns the char resolver based on {@link #resolveChar(String, Character)} with `null` as
     * default value
     */
    private static final StringValueResolver<Character> _Char = new StringValueResolver<Character>() {
        @Override
        public Character resolve(String value) {
            return resolveChar(value, null);
        }
    };

    private static final StringValueResolver<char[]> _charArray = new StringValueResolver<char[]>() {
        @Override
        public char[] resolve(String value) {
            return null == value ? new char[0] : value.toCharArray();
        }
    };

    private static final StringValueResolver<Byte> _byte = new StringValueResolver<Byte>() {
        @Override
        public Byte resolve(String value) {
            if (S.blank(value)) {
                return (byte) 0;
            }
            return Byte.parseByte(value);
        }
    };
    private static final StringValueResolver<Byte> _Byte = new StringValueResolver<Byte>() {
        @Override
        public Byte resolve(String value) {
            if (S.blank(value)) {
                return null;
            }
            return Byte.parseByte(value);
        }
    };
    private static final StringValueResolver<Short> _short = new StringValueResolver<Short>() {
        @Override
        public Short resolve(String value) {
            if (S.blank(value)) {
                return (short) 0;
            }
            return Short.valueOf(value);
        }
    };
    private static final StringValueResolver<Short> _Short = new StringValueResolver<Short>() {
        @Override
        public Short resolve(String value) {
            if (S.blank(value)) {
                return null;
            }
            return Short.valueOf(value);
        }
    };

    private static int _int(String s) {
        s = s.trim();
        if (s.contains(".")) {
            float f = Float.valueOf(s);
            return Math.round(f);
        } else if (s.contains("*")) {
            List<String> factors = S.fastSplit(s, "*");
            int n = 1;
            for (String factor : factors) {
                n *= _int(factor);
            }
            return n;
        } else {
            return Integer.valueOf(s);
        }
    }

    private static final StringValueResolver<Integer> _int = new StringValueResolver<Integer>() {
        @Override
        public Integer resolve(String value) {
            if (S.blank(value)) {
                return 0;
            }
            return _int(value);
        }
    };
    private static final StringValueResolver<Integer> _Integer = new StringValueResolver<Integer>() {
        @Override
        public Integer resolve(String value) {
            if (S.blank(value)) {
                return null;
            }
            return _int(value);
        }
    };
    private static final StringValueResolver<Long> _long = new StringValueResolver<Long>() {
        @Override
        public Long resolve(String value) {
            if (S.blank(value)) {
                return 0l;
            }
            return _long(value);
        }
    };
    private static final StringValueResolver<Long> _Long = new StringValueResolver<Long>() {
        @Override
        public Long resolve(String value) {
            if (S.blank(value)) {
                return null;
            }
            return _long(value);
        }
    };

    private static long _long(String s) {
        s = s.trim();
        if (s.contains(".")) {
            double d = Double.valueOf(s);
            return Math.round(d);
        } else if (s.contains("*")) {
            List<String> factors = S.fastSplit(s, "*");
            long n = 1l;
            for (String factor : factors) {
                n *= _long(factor);
            }
            return n;
        } else {
            return Long.valueOf(s);
        }
    }
    private static final StringValueResolver<Float> _float = new StringValueResolver<Float>() {
        @Override
        public Float resolve(String value) {
            if (S.blank(value)) {
                return 0f;
            }
            float n = _float(value);
            if (Float.isInfinite(n) || Float.isNaN(n)) {
                throw new IllegalArgumentException("float value out of scope: " + value);
            }
            return n;
        }
    };
    private static final StringValueResolver<Float> _Float = new StringValueResolver<Float>() {
        @Override
        public Float resolve(String value) {
            if (S.blank(value)) {
                return null;
            }
            float n = _float(value);
            if (Float.isInfinite(n) || Float.isNaN(n)) {
                throw new IllegalArgumentException("float value out of scope: " + value);
            }
            return n;
        }
    };

    private static float _float(String s) {
        s = s.trim();
        if (s.contains("*")) {
            List<String> factors = S.fastSplit(s, "*");
            float n = 1f;
            for (String factor : factors) {
                n *= _float(factor);
            }
            return n;
        } else {
            return Float.valueOf(s);
        }
    }
    private static final StringValueResolver<Double> _double = new StringValueResolver<Double>() {
        @Override
        public Double resolve(String value) {
            if (S.blank(value)) {
                return 0d;
            }
            double n = _double(value);
            if (Double.isInfinite(n) || Double.isNaN(n)) {
                throw new IllegalArgumentException("double value out of scope: " + value);
            }
            return n;
        }
    };
    private static final StringValueResolver<Double> _Double = new StringValueResolver<Double>() {
        @Override
        public Double resolve(String value) {
            if (S.blank(value)) {
                return null;
            }
            double n = _double(value);
            if (Double.isInfinite(n) || Double.isNaN(n)) {
                throw new IllegalArgumentException("double value out of scope: " + value);
            }
            return n;
        }
    };

    private static double _double(String s) {
        s = s.trim();
        if (s.contains("*")) {
            List<String> factors = S.fastSplit(s, "*");
            double n = 1d;
            for (String factor : factors) {
                n *= _double(factor);
            }
            return n;
        } else {
            return Double.valueOf(s);
        }
    }
    private static final StringValueResolver<String> _String = wrap($.F.<String>identity(), String.class);
    private static final StringValueResolver<Str> _Str = new StringValueResolver<Str>() {
        @Override
        public Str resolve(String value) {
            if (null == value) {
                return null;
            }
            return S.str(value);
        }
    };
    private static final StringValueResolver<FastStr> _FastStr = new StringValueResolver<FastStr>() {
        @Override
        public FastStr resolve(String value) {
            if (null == value) {
                return null;
            }
            return FastStr.of(value);
        }
    };
    private static final StringValueResolver<Locale> _Locale = new StringValueResolver<Locale>() {
        @Override
        public Locale resolve(String value) {
            return Locale.forLanguageTag(value);
        }
    };

    private static Map<Class, StringValueResolver> predefined = C.newMap(
            boolean.class, _boolean,
            Boolean.class, _Boolean,
            char.class, _char,
            Character.class, _Char,
            byte.class, _byte,
            Byte.class, _Byte,
            short.class, _short,
            Short.class, _Short,
            int.class, _int,
            Integer.class, _Integer,
            long.class, _long,
            Long.class, _Long,
            float.class, _float,
            Float.class, _Float,
            double.class, _double,
            Double.class, _Double,
            String.class, _String,
            Locale.class, _Locale,
            Str.class, _Str,
            FastStr.class, _FastStr,
            char[].class, _charArray,
            BigInteger.class, new BigIntegerValueObjectCodec(),
            BigDecimal.class, new BigDecimalValueObjectCodec(),
            Keyword.class, new KeywordValueObjectCodec()
    );

    public static <T> void addPredefinedResolver(Class<T> type, StringValueResolver<T> resolver) {
        predefined.put(type, resolver);
    }

    public static Map<Class, StringValueResolver> predefined() {
        return C.Map(predefined);
    }

    @SuppressWarnings("unchecked")
    public static <T> StringValueResolver<T> predefined(Class<T> type) {
        return predefined.get(type);
    }

    public static void main(String[] args) {
        System.out.println(_float.targetType());
        System.out.println(_String.targetType());
    }
}
