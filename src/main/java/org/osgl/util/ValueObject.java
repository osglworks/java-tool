package org.osgl.util;

import org.osgl.$;

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
public class ValueObject {

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
                return S.builder("\"").append(toString(vo)).append("\"").toString();
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
        return type() == Type.UDF;
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

    private Type typeOf(Object o) {
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

}
