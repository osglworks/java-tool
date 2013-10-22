package org.osgl.util;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A general {@link Iterable} wrapper
 */
public class Itr<T> implements Iterable<T>  {
    protected Object _o = null;
    protected int size = -1;
    protected Iterator<T> iterator = new ItrItr<T>(this);
    
    public Itr() {}
    
    private Itr(int size) {
        this.size = size;
    }
    
    public Itr(Iterator<T> itr) {
        iterator = itr;
    }
    
    protected T get(int i) {
        throw E.unsupport();
    }
    
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new ItrItr<T>(this);
    }

    protected static class ItrItr<T> implements Iterator<T> {
        private Itr<T> _;
        ItrItr(Itr<T> I) {
            _ = I;
        }
        private int cursor = 0;

        @Override
        public T next() {
            return _.get(cursor++);
        }

        @Override
        public boolean hasNext() {
            return cursor < _.size();
        }

        @Override
        public void remove() {
            E.unsupport();
        }
    }
    
    public Iterable<T> reverse() {
        if (-1 == size) {
            return C0.EMPTY_LIST;
        }
        return new Iterable<T>() {
            private int cursor = size - 1;
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return cursor > -1;
                    }

                    @Override
                    public T next() {
                        return get(cursor--);
                    }

                    @Override
                    public void remove() {
                        E.unsupport();
                    }
                };
            }
        };
    }
    
    private static final Iterator NULL_ITERATOR = Collections.EMPTY_LIST.iterator();

    private static final Itr NULL_INSTANCE = new Itr() {
        @Override
        protected Object get(int i) {
            return null;
        }
    }; 
    
    public static <T> Itr<T> valueOf(final T... ta) {
        return new Itr<T>(ta.length) {
            @Override
            protected T get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Integer> valueOf(final int... ta) {
        return new Itr<Integer>(ta.length) {
            @Override
            protected Integer get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Long> valueOf(final long... ta) {
        return new Itr<Long>(ta.length) {
            @Override
            protected Long get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Float> valueOf(final float... ta) {
        return new Itr<Float>(ta.length) {
            @Override
            protected Float get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Double> valueOf(final double... ta) {
        return new Itr<Double>(ta.length) {
            @Override
            protected Double get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Short> valueOf(final short... ta) {
        return new Itr<Short>(ta.length) {
            @Override
            protected Short get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Character> valueOf(final char... ta) {
        return new Itr<Character>(ta.length) {
            @Override
            protected Character get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Byte> valueOf(final byte... ta) {
        return new Itr<Byte>(ta.length) {
            @Override
            protected Byte get(int i) {
                return ta[i];
            }
        };
    }

    public static Itr<Boolean> valueOf(final boolean... ta) {
        return new Itr<Boolean>(ta.length) {
            @Override
            protected Boolean get(int i) {
                return ta[i];
            }
        };
    }

    public static <T extends Comparable<T>> Itr<T> valueOf(final Range<T> range) {
        Itr<T> itr = new Itr<T>(range.size()) {
            @Override
            protected T get(int i) {
                throw E.unsupport();
            }

            @Override
            public Iterable<T> reverse() {
                return valueOf(range.reversed());
            }
        };
        itr.iterator = range.iterator();
        return itr;
    }
    
    public static Itr valueOf(final Object obj) {
        if (null == obj) {
            return NULL_INSTANCE;
        }
        if (obj instanceof Iterable) {
            return valueOf((Iterable)obj);
        }
        Class c = obj.getClass();
        if (c.isArray()) {
            Class ct = c.getComponentType();
            if (ct.equals(int.class)) {
                return valueOf((int[])obj);
            } else if (ct.equals(long.class)) {
                return valueOf((long[])obj);
            } else if (ct.equals(float.class)) {
                return valueOf((float[])obj);
            } else if (ct.equals(double.class)) {
                return valueOf((double[])obj);
            } else if (ct.equals(char.class)) {
                return valueOf((char[])obj);
            } else if (ct.equals(byte.class)) {
                return valueOf((byte[])obj);
            } else if (ct.equals(boolean.class)) {
                return valueOf((boolean[])obj);
            }
        }
        if (obj.getClass().isArray()) {
            return new Itr(Array.getLength(obj)) {
                @Override
                protected Object get(int i) {
                    return Array.get(obj, i);
                }
            };
        }
        return new Itr(1) {
            @Override
            protected Object get(int i) {
                return obj;
            }
        };
    }
    
    public static <T> Itr<T> valueOf(final List<T> l) {
        return new Itr<T>(l.size()) {
            @Override
            protected T get(int i) {
                return l.get(i);
            }
        };
    }
    
    public static <T> Itr<T> valueOf(final Iterable<T> itr) {
        if (itr instanceof List) {
            return valueOf((List<T>)itr);
        }
        return new Itr<T>(itr.iterator());
    }
}
