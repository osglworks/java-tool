package org.osgl.util;

import org.osgl.$;

import java.io.Serializable;
import java.util.Map;

/**
 * A {@code ValueObject} encapsulate data of simple types in common Java application.
 * <p>Simple type here refers to</p>
 * <ul>
 * <li>Primary types</li>
 * <li>String</li>
 * <li>Enum</li>
 * <li>Types with {@link Codec} {@link #register(Codec) registered}</li>
 * </ul>
 * <p>
 * {@code ValueObject} is immutable
 * </p>
 */
public class ValueObject implements Serializable {

    private static final long serialVersionUID = -6103505642730947577L;

    public static interface Codec<T> {
        Class<T> targetClass();

        T parse(String s);

        String toString(T o);

        String toJSONString(T o);
    }

    private static Map<Class, Codec> codecRegistry = C.newMap();

    @SuppressWarnings("unchecked")
    private static enum Type {
        BOOL() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.blVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.blVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(boolean.class != type || Boolean.class != type);
                return (T) Boolean.valueOf(s);
            }
        },
        BYTE() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.byVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.byVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(byte.class != type || Byte.class != type);
                return (T) Byte.valueOf(s);
            }
        },
        CHAR() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.chVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.chVal = $.cast(o);
            }

            @Override
            String toJSONString(ValueObject vo) {
                return S.builder("\"").append(toString(vo)).append("\"").toString();
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(char.class != type || Character.class != type);
                return (T) Character.valueOf(s.charAt(0));
            }
        },
        SHORT() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.shVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.shVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(short.class != type || Short.class != type);
                return (T) Short.valueOf(s);
            }
        },
        INT() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.iVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.iVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(int.class != type || Integer.class != type);
                return (T) Integer.valueOf(s);
            }
        },
        FLOAT() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.fVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.fVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(float.class != type || Float.class != type);
                return (T) Float.valueOf(s);
            }
        },
        LONG() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.lVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.lVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(long.class != type || Long.class != type);
                return (T) Long.valueOf(s);
            }
        },
        DOUBLE() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.dVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.dVal = $.cast(o);
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(double.class != type || Double.class != type);
                return (T) Double.valueOf(s);
            }
        },
        STRING() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.sVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.sVal = S.string(o);
            }

            @Override
            String toJSONString(ValueObject vo) {
                String string = toString(vo);
                if (string == null || string.length() == 0) {
                    return "\"\"";
                }

                char c;
                int i;
                int len = string.length();
                StringBuilder sb = new StringBuilder(len + 4);
                String t;

                sb.append('"');
                for (i = 0; i < len; i += 1) {
                    c = string.charAt(i);
                    switch (c) {
                        case '\\':
                        case '"':
                            sb.append('\\');
                            sb.append(c);
                            break;
                        case '/':
                            //                if (b == '<') {
                            sb.append('\\');
                            //                }
                            sb.append(c);
                            break;
                        case '\b':
                            sb.append("\\b");
                            break;
                        case '\t':
                            sb.append("\\t");
                            break;
                        case '\n':
                            sb.append("\\n");
                            break;
                        case '\f':
                            sb.append("\\f");
                            break;
                        case '\r':
                            sb.append("\\r");
                            break;
                        default:
                            if (c < ' ') {
                                t = "000" + Integer.toHexString(c);
                                sb.append("\\u" + t.substring(t.length() - 4));
                            } else {
                                sb.append(c);
                            }
                    }
                }
                sb.append('"');
                return sb.toString();
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(String.class != type);
                return (T) s;
            }
        },

        ENUM() {
            @Override
            <T> T get(ValueObject vo) {
                return (T) vo.eVal;
            }

            @Override
            void set(Object o, ValueObject vo) {
                vo.eVal = $.cast(o);
            }

            @Override
            String toJSONString(ValueObject vo) {
                return S.builder("\"").append(toString(vo)).append("\"").toString();
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                E.illegalArgumentIf(!Enum.class.isAssignableFrom(type));
                Class<? extends Enum> typedType = $.cast(type);
                return (T) Enum.valueOf(typedType, s);
            }
        },
        UDF() {
            @Override
            <T> T get(ValueObject vo) {
                return $.cast(vo.udf);
            }

            @Override
            void set(Object o, ValueObject vo) {
                Codec c = findCodec(o.getClass());
                E.illegalArgumentIf(null == c, "Cannot find registered codec for value class: %s", o.getClass());
                vo.udf = o;
            }

            @Override
            <T> T decode(String s, Class<T> type) {
                Codec codec = findCodec(type);
                return (T) codec.parse(s);
            }

            @Override
            String encode(Object o) {
                Codec codec = findCodec(o.getClass());
                E.illegalArgumentIf(null == codec, "Cannot find registered codec for value class: %s", o.getClass());
                return codec.toString(o);
            }

            @Override
            String toString(ValueObject vo) {
                Class objType = vo.udf.getClass();
                Codec codec = findCodec(objType);
                return null != codec ? codec.toString(vo.udf) : super.toString(vo);
            }

            @Override
            String toJSONString(ValueObject vo) {
                Class objType = vo.udf.getClass();
                Codec codec = findCodec(objType);
                return null != codec ? codec.toJSONString(vo.udf) : super.toJSONString(vo);
            }

            private Codec findCodec(Class c) {
                Codec codec = codecRegistry.get(c);
                if (null != codec) {
                    return codec;
                }
                Class[] ifs = c.getInterfaces();
                if (null != ifs) {
                    for (Class c0 : ifs) {
                        codec = findCodec(c0);
                        if (null != codec) {
                            return codec;
                        }
                    }
                }
                Class sc = c.getSuperclass();
                return null == sc ? null : findCodec(sc);
            }

        };

        abstract <T> T get(ValueObject vo);

        abstract void set(Object o, ValueObject vo);

        String toString(ValueObject vo) {
            return S.string(get(vo));
        }

        String toJSONString(ValueObject vo) {
            return toString(vo);
        }

        abstract <T> T decode(String s, Class<T> type);

        String encode(Object o) {
            return S.string(o);
        }
    }

    private transient Type type;

    private Boolean blVal;
    private Byte byVal;
    private Character chVal;
    private Short shVal;
    private Integer iVal;
    private Float fVal;
    private Long lVal;
    private Double dVal;
    private String sVal;
    private Enum eVal;
    private Object udf;

    public ValueObject() {
        this("");
    }

    public ValueObject(boolean b) {
        blVal = b;
        type = Type.BOOL;
    }

    public ValueObject(byte b) {
        byVal = b;
        type = Type.BYTE;
    }

    public ValueObject(char c) {
        chVal = c;
        type = Type.CHAR;
    }

    public ValueObject(short s) {
        shVal = s;
        type = Type.SHORT;
    }

    public ValueObject(int i) {
        iVal = i;
        type = Type.INT;
    }

    public ValueObject(float f) {
        fVal = f;
        type = Type.FLOAT;
    }

    public ValueObject(long l) {
        lVal = l;
        type = Type.LONG;
    }

    public ValueObject(double d) {
        dVal = d;
        type = Type.DOUBLE;
    }

    public ValueObject(String s) {
        sVal = $.notNull(s);
        type = Type.STRING;
    }

    public ValueObject(CharSequence s) {
        sVal = s.toString();
        type = Type.STRING;
    }

    public ValueObject(Enum e) {
        eVal = e;
        type = Type.ENUM;
    }

    public ValueObject(Object o) {
        if (o instanceof ValueObject) {
            ValueObject that = (ValueObject) o;
            type = that.type();
            type.set(that.value(), this);
        } else {
            type = typeOf(o);
            type.set(o, this);
        }
    }

    public ValueObject(ValueObject copy) {
        type = copy.type();
        type.set(copy.value(), this);
    }

    public boolean booleanValue() {
        return blVal;
    }

    public byte byteValue() {
        return byVal;
    }

    public char charValue() {
        return chVal;
    }

    public short shortValue() {
        return shVal;
    }

    public int intValue() {
        return iVal;
    }

    public float floatValue() {
        return fVal;
    }

    public long longValue() {
        return lVal;
    }

    public double doubleValue() {
        return dVal;
    }

    public String stringValue() {
        return $.notNull(sVal);
    }

    public <T extends Enum> T enumValue() {
        return $.cast(eVal);
    }

    public <T> T value() {
        return type().get(this);
    }

    public boolean isUDF() {
        Type type = type();
        return type == Type.UDF || type == Type.ENUM;
    }

    @Override
    public int hashCode() {
        return $.hc(type().get(this));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ValueObject) {
            ValueObject that = (ValueObject) obj;
            return $.eq(that.type().get(that), this.type().get(this));
        }
        return false;
    }

    @Override
    public String toString() {
        return type().toString(this);
    }

    public String toJSONString() {
        return type().toJSONString(this);
    }

    public static void register(Codec codec) {
        codecRegistry.put(codec.targetClass(), codec);
    }

    public static void unregister(Codec codec) {
        codecRegistry.remove(codec.targetClass());
    }

    /**
     * Decode a object instance from a string with given target object type
     *
     * @param string     the string encoded the value of the instance
     * @param targetType the class of the instance decoded from the string
     * @param <T>        the generic type of the instance
     * @return the instance decoded
     */
    public static <T> T decode(String string, Class<T> targetType) {
        Type type = typeOf(targetType);
        return type.decode(string, targetType);
    }

    /**
     * Encode a object into a String
     *
     * @param o the object to be encoded
     * @return the encoded string representation of the object
     * @throws IllegalArgumentException when object is a UDF type and Codec is not registered
     */
    public static String encode(Object o) {
        Type type = typeOf(o);
        return type.encode(o);
    }

    public static ValueObject of(Object o) {
        if (o instanceof ValueObject) {
            return $.cast(o);
        }
        return new ValueObject(o);
    }

    private Type type() {
        if (null == type) {
            type = findType();
        }
        return type;
    }

    private Type findType() {
        if (sVal != null) {
            type = Type.STRING;
        }
        if (iVal != null) {
            return Type.INT;
        }
        if (dVal != null) {
            return Type.DOUBLE;
        }
        if (eVal != null) {
            return Type.ENUM;
        }
        if (lVal != null) {
            return Type.LONG;
        }
        if (fVal != null) {
            return Type.FLOAT;
        }
        if (blVal != null) {
            return Type.BOOL;
        }
        if (byVal != null) {
            return Type.BYTE;
        }
        if (chVal != null) {
            return Type.CHAR;
        }
        if (shVal != null) {
            return Type.SHORT;
        }
        return Type.UDF;
    }

    private static Type typeOf(Object o) {
        if (null == o) {
            return Type.STRING;
        }
        if (o instanceof CharSequence) {
            return Type.STRING;
        }
        if (o instanceof Integer) {
            return Type.INT;
        }
        if (o instanceof Boolean) {
            return Type.BOOL;
        }
        if (o instanceof Enum) {
            return Type.ENUM;
        }
        if (o instanceof Double) {
            return Type.DOUBLE;
        }
        if (o instanceof Long) {
            return Type.LONG;
        }
        if (o instanceof Float) {
            return Type.FLOAT;
        }
        if (o instanceof Character) {
            return Type.CHAR;
        }
        if (o instanceof Byte) {
            return Type.BYTE;
        }
        if (o instanceof Short) {
            return Type.SHORT;
        }
        return Type.UDF;
    }

    private static Type typeOf(Class c) {
        E.NPE(c);
        if (String.class.isAssignableFrom(c)) {
            return Type.STRING;
        }
        if (Integer.class.isAssignableFrom(c) || int.class.isAssignableFrom(c)) {
            return Type.INT;
        }
        if (Boolean.class.isAssignableFrom(c) || boolean.class.isAssignableFrom(c)) {
            return Type.BOOL;
        }
        if (Enum.class.isAssignableFrom(c)) {
            return Type.ENUM;
        }
        if (Double.class.isAssignableFrom(c) || double.class.isAssignableFrom(c)) {
            return Type.DOUBLE;
        }
        if (Long.class.isAssignableFrom(c) || long.class.isAssignableFrom(c)) {
            return Type.LONG;
        }
        if (Character.class.isAssignableFrom(c) || char.class.isAssignableFrom(c)) {
            return Type.CHAR;
        }
        if (Byte.class.isAssignableFrom(c) || byte.class.isAssignableFrom(c)) {
            return Type.BYTE;
        }
        if (Short.class.isAssignableFrom(c) || short.class.isAssignableFrom(c)) {
            return Type.SHORT;
        }
        return Type.UDF;
    }

    static {
        register(BigDecimalValueObjectCodec.INSTANCE);
        register(BigIntegerValueObjectCodec.INSTANCE);
        register(KeywordValueObjectCodec.INSTANCE);
    }

}
