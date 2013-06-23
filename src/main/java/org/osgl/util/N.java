package org.osgl.util;

/**
 * Number utilities and functors
 */
public class N {


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

    public static class Num extends Number {
        private Number n;

        public Num(Number n) {
            this.n = n;
        }

        private double v() {
            return doubleValue();
        }

        public <T extends Number> T as(Class<T> cls) {
            if (Integer.class.isAssignableFrom(cls)) {
                return (T) (Integer) n.intValue();
            } else if (Double.class.isAssignableFrom(cls)) {
                return (T) (Double) n.doubleValue();
            } else if (Long.class.isAssignableFrom(cls)) {
                return (T) (Long) n.longValue();
            } else if (Float.class.isAssignableFrom(cls)) {
                return (T) (Float) n.floatValue();
            } else if (Short.class.isAssignableFrom(cls)) {
                return (T) (Short) n.shortValue();
            } else if (Byte.class.isAssignableFrom(cls)) {
                return (T) (Byte) n.byteValue();
            } else {
                throw org.osgl.util.E.unsupport("cannot cast Num to type %s", cls);
            }
        }

        @Override
        public String toString() {
            return String.valueOf(n);
        }

        @Override
        public int intValue() {
            return n.intValue();
        }

        @Override
        public long longValue() {
            return n.longValue();
        }

        @Override
        public float floatValue() {
            return n.floatValue();
        }

        @Override
        public double doubleValue() {
            return n.doubleValue();
        }

        public Num add(Number n) {
            return new Num(v() + n.doubleValue());
        }

        public Num add(byte n) {
            return new Num(v() + n);
        }

        public Num add(short n) {
            return new Num(v() + n);
        }

        public Num add(int n) {
            return new Num(v() + n);
        }

        public Num add(long n) {
            return new Num(v() + n);
        }

        public Num add(float n) {
            return new Num(v() + n);
        }

        public Num add(double n) {
            return new Num(v() + n);
        }

        public Num sub(Number n) {
            return new Num(v() - n.doubleValue());
        }

        public Num sub(byte n) {
            return new Num(v() - n);
        }

        public Num sub(short n) {
            return new Num(v() - n);
        }

        public Num sub(int n) {
            return new Num(v() - n);
        }

        public Num sub(long n) {
            return new Num(v() - n);
        }

        public Num sub(float n) {
            return new Num(v() - n);
        }

        public Num sub(double n) {
            return new Num(v() - n);
        }

        public Num mul(byte n) {
            return new Num(v() * n);
        }

        public Num mul(Number n) {
            return new Num(v() * n.doubleValue());
        }

        public Num mul(short n) {
            return new Num(v() * n);
        }

        public Num mul(int n) {
            return new Num(v() * n);
        }

        public Num mul(long n) {
            return new Num(v() * n);
        }

        public Num mul(float n) {
            return new Num(v() * n);
        }

        public Num mul(double n) {
            return new Num(v() * n);
        }

        public Num div(Number n) {
            return new Num(v() / n.doubleValue());
        }

        public Num div(byte n) {
            return new Num(v() / n);
        }

        public Num div(short n) {
            return new Num(v() / n);
        }

        public Num div(int n) {
            return new Num(v() / n);
        }

        public Num div(long n) {
            return new Num(v() / n);
        }

        public Num div(float n) {
            return new Num(v() / n);
        }

        public Num div(double n) {
            return new Num(v() / n);
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
            return N.sign(n);
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

    }

    public static final class f {

        public static final F.F1 DBL = mul(2);

        public static <T extends Number> F.F1<Number, T> dbl() {
            return DBL;
        }

        public static final F.F1 HALF = div(2);

        public static <T extends Number> F.F1<Number, T> half() {
            return HALF;
        }

        public static <T extends Number> F.F1<Number, T> add(final Number n) {
            return new F.F1<Number, T>() {
                @Override
                public Number run(T t) {
                    return N.num(t).add(n);
                }
            };
        }

        public static <T extends Number> F.F1<Number, T> mul(final Number n) {
            return new F.F1<Number, T>() {
                @Override
                public Number run(T t) {
                    return N.num(t).mul(n);
                }
            };
        }


        public static <T extends Number> F.F1<Number, T> div(final Number n) {
            return new F.F1<Number, T>() {
                @Override
                public Number run(T t) {
                    return N.num(t).div(n);
                }
            };
        }

        public static <T extends Number> F.F2<T, T, T> aggregate(final Class<T> clz) {
            return new F.F2<T, T, T>() {
                @Override
                public T run(T e, T v) {
                    return N.num(e).add(v).as(clz);
                }
            };
        }
    }

}
