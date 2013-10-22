package org.osgl.util;

import org.osgl._;

import java.util.Map;

/**
 * Number utilities and functors
 */


    /**
     * The <code>double</code> value that is closer than any other to
     * <i>e</i>, the base of the natural logarithms.
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The <code>double</code> value that is closer than any other to
     * <i>pi</i>, the ratio of the circumference of a circle to its
     * diameter.
     */
    public static final double PI = 3.14159265358979323846;

    public static double exp(double a) {
        return StrictMath.exp(a); // default impl. delegates to StrictMath
    }

    public static double log(double a) {
        return StrictMath.log(a); // default impl. delegates to StrictMath
    }

    public static double log10(double a) {
        return StrictMath.log10(a); // default impl. delegates to StrictMath
    }

    public static double sqrt(double a) {
        return StrictMath.sqrt(a);
    }


    public static double cbrt(double a) {
        return StrictMath.cbrt(a);
    }

    public static double ceil(double a) {
        return StrictMath.ceil(a);
    }

    public static double floor(double a) {
        return StrictMath.floor(a);
    }

    public static double pow(double a, double b) {
        return StrictMath.pow(a, b);
    }

    public static int round(float a) {
        return Math.round(a);
    }

    public static long round(double a) {
        return Math.round(a);
    }

    public static double random() {
        return Math.random();
    }

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }

    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }

    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    public static long min(long a, long b) {
        return Math.min(a, b);
    }

    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    public static int sign(Number number) {
        if (null == number) {
            return 0;
        }
        int n = number.intValue();
        if (n == 0) {
            return 0;
        }
        if (n > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public static final boolean eq(Number a, Number b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        return (a.doubleValue() - b.doubleValue()) < 0.0000000001;
    }

    public static final boolean lt(Number a, Number b) {
        return a.doubleValue() < b.doubleValue();
    }

    public static final boolean gt(Number a, Number b) {
        return a.doubleValue() > b.doubleValue();
    }

    public static final Num num(Number number) {
        return new Num(number);
    }

    public static final Num num(String s) {
        if (S.empty(s)) {
            return new Num(0);
        }
        if (s.contains(".")) {
            return new Num(Double.parseDouble(s));
        } else if (s.length() > 0) {
            return new Num(Long.parseLong(s));
        } else {
            return new Num(Integer.parseInt(s));
        }
    }

    public static enum Type {
        BYTE(1) {
            @Override
            Number add(Number a, Number b) {
                return a.byteValue() + b.byteValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.byteValue() - b.byteValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.byteValue() * b.byteValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.byteValue() / b.byteValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.byteValue();
            }
        }, SHORT(5){
            @Override
            Number add(Number a, Number b) {
                return a.shortValue() + b.shortValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.shortValue() - b.shortValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.shortValue() * b.shortValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.shortValue() / b.shortValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.shortValue();
            }
        }, INT(10) {
            @Override
            Number add(Number a, Number b) {
                return a.intValue() + b.intValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.intValue() - b.intValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.intValue() * b.intValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.intValue() / b.intValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.intValue();
            }
        }, LONG(15) {
            @Override
            Number add(Number a, Number b) {
                return a.longValue() + b.longValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.longValue() - b.longValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.longValue() * b.longValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.longValue() / b.longValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.longValue();
            }
        }, FLOAT(20){
            @Override
            Number add(Number a, Number b) {
                return a.floatValue() + b.floatValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.floatValue() - b.floatValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.floatValue() * b.floatValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.floatValue() / b.floatValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.floatValue();
            }
        }, DOUBLE(25) {
            @Override
            Number add(Number a, Number b) {
                return a.doubleValue() + b.doubleValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.doubleValue() - b.doubleValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.doubleValue() * b.doubleValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.doubleValue() / b.doubleValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.doubleValue();
            }
        };
        
        private int _w;

        private Type(int weight) {
            _w = weight;
        }

        abstract Number add(Number a, Number b);
        abstract Number sub(Number a, Number b);
        abstract Number mul(Number a, Number b);
        abstract Number div(Number a, Number b);
        abstract Number valueOf(Number n);
        
        public boolean lt(Type type) {
            return _w < type._w;
        }
        
        public boolean gt(Type type) {
            return _w > type._w;
        }

        public boolean eq(Type type) {
            return type == this;
        }
    }

    private static Map<Class<? extends Number>, Type> _m; static {
        _m = C0.map(
                Byte.class, Type.BYTE,
                Short.class, Type.SHORT,
                Integer.class, Type.INT,
                Long.class, Type.LONG,
                Float.class, Type.FLOAT,
                Double.class, Type.DOUBLE
        );
    }
    
    private static Type _type(Class<? extends Number> c) {
        Type t = _m.get(c);
        if (null == t) {
            t = Type.DOUBLE;
        }
        return t;
    }
    
    private static Type _type(Number n) {
        if (n instanceof Num) {
            return ((Num)n)._t;
        } else {
            return _type(n.getClass());
        }
    }
    
    public static enum Op implements _.Func2<Number, Number, Number> {
        ADD {
            @Override
            public Number apply(Number a, Number b) {
                return t(a, b).add(a, b); 
            }
        },
        SUB {
            @Override
            public Number apply(Number a, Number b) {
                return t(a, b).sub(a, b);
            }
        },
        MUL {
            @Override
            public Number apply(Number a, Number b) {
                return t(a, b).mul(a, b);
            }
        },
        DIV {
            @Override
            public Number apply(Number a, Number b) {
                return t(t(a, b), Type.FLOAT).div(a, b);
            }
        };

        //public abstract Number apply(Number a, Number b);
        
        
        private static Type t(Number a, Number b) {
            Type ta = _type(a);
            Type tb = _type(b);
            return ta.gt(tb) ? ta : tb;
        }

        private static Type t(Type a, Type b) {
            return a.gt(b) ? a : b;
        }
        
        @Override
        public Number apply(Number o, Number o2) {
            return apply(o, o2);
        }

        public _.F0<Number> curry(final Number a, final Number b) {
            return curry(a, b);
        }

        public _.F1<Number, Number> curry(final Number b) {
            return _.curry(this, a, b);
        }

        public _.F1<Number, Number> curry(final Number b) {
            return _.curry(this, b);
        }
    }
    
    public static class Num<T extends Number> extends Number {
    
        private Number _n;
        private Type _t;

        public Num(Number n) {
            if (n instanceof Num) {
                _n = ((Num)n)._n;
                _t = ((Num)n)._t;
            } else {
                this._n = n;
                this._t = _m.get(n.getClass());
                if (null == _t) {
                    // TODO handle BigInteger, AtomicLong and BigDecimal
                    _t = Type.DOUBLE;
                }
            }
        }

        public T get() {
            return (T) _n;
        }

        public <T extends Number> T as(Class<T> cls) {
            return (T)_type(cls).valueOf(_n);
        }

        @Override
        public String toString() {
            return String.valueOf(_n);
        }

        @Override
        public int intValue() {
            return _n.intValue();
        }

        @Override
        public long longValue() {
            return _n.longValue();
        }

        @Override
        public float floatValue() {
            return _n.floatValue();
        }

        @Override
        public double doubleValue() {
            return _n.doubleValue();
        }

        public Num add(Number n) {
            return valueOf(Op.ADD.apply(_n, n));
        }

        public Num sub(Number n) {
            return valueOf(Op.SUB.apply(_n, n));
        }
        public Num mul(Number n) {
            return valueOf(Op.MUL.apply(_n, n));
        }

        public Num div(Number n) {
            return valueOf(Op.DIV.apply(_n, n));
        }

        public double exp() {
            return N.exp(doubleValue());
        }

        public double log() {
            return N.log(doubleValue());
        }

        public double log10() {
            return N.log10(doubleValue());
        }

        public double sqrt() {
            return N.sqrt(doubleValue());
        }

        public double cbrt() {
            return N.cbrt(doubleValue());
        }

        public double ceil() {
            return N.ceil(doubleValue());
        }

        public double floor() {
            return N.floor(doubleValue());
        }

        public double pow(double b) {
            return N.pow(doubleValue(), b);
        }

        public int sign() {
            return N.sign(_n);
        }

        public boolean eq(Number number) {
            return N.eq(this, number);
        }

        public boolean lt(Number number) {
            return N.lt(this, number);
        }

        public boolean gt(Number number) {
            return N.gt(this, number);
        }

        public static Num valueOf(Number n) {
            if (n instanceof Num) {
                return (Num)n;
            } else {
                return new Num(n);
            }
        }

    }

    public static final class f {

        public static final _.F1 DBL = mul(2);

        public static <T extends Number> _.F1<Number, T> dbl() {
            return DBL;
        }

        public static <T extends Number> _.F1<Number, T> dbl(Class<T> clz) {
            return mul(2, clz);
        }

        public static final _.F1 HALF = div(2);

        public static <T extends Number> _.F1<Number, T> half() {
            return HALF;
        }

        public static <T extends Number> _.F1<Number, T> half(Class<T> clz) {
            return div(2, clz);
        }

        public static <T extends Number> _.F1<Number, T> add(final Number n) {
            return new _.F1<Number, T>() {
                @Override
                public Number apply(T t) {
                    return N.num(t).add(n).get();
                }
            };
        }

        public static <T extends Number> _.F1<Number, T> add(final Number n, final Class<T> clz) {
            return new _.F1<Number, T>() {
                @Override
                public Number apply(T t) {
                    return N.num(t).add(n).as(clz);
                }
            };
        }

        public static <T extends Number> _.F1<Number, T> mul(final Number n) {
            return new _.F1<Number, T>() {
                @Override
                public Number apply(T t) {
                    return N.num(t).mul(n).get();
                }
            };
        }

        public static <T extends Number> _.F1<Number, T> mul(final Number n, final Class<T> clz) {
            return new _.F1<Number, T>() {
                @Override
                public Number apply(T t) {
                    return N.num(t).mul(n).as(clz);
                }
            };
        }

        public static <T extends Number> _.F1<Number, T> div(final Number n) {
            return new _.F1<Number, T>() {
                @Override
                public Number apply(T t) {
                    return N.num(t).div(n).get();
                }
            };
        }
        
        public static <T extends Number> _.F1<Number, T> div(final Number n, final Class<T> clz) {
            return new _.F1<Number, T>() {
                @Override
                public Number apply(T t) {
                    return N.num(t).div(n).as(clz);
                }
            };
        }
        
        public static <T extends Number> _.F2<T, T, T> aggregate(final Class<T> clz) {
            return new _.F2<T, T, T>() {
                @Override
                public T apply(T e, T v) {
                    return (T)N.num(e).add(v).as(clz);
                }
            };
        }
    }

}
