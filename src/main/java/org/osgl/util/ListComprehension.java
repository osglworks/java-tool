package org.osgl.util;

import java.util.Iterator;
import java.util.List;

/**
 * Simulate python's list comprehension
 */
public class ListComprehension<T> implements Iterable<T> {

    private final Iterable<? extends T> itr;

    public ListComprehension(Iterable<? extends T> itr) {
        this.itr = itr;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<? extends T> it = itr.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }

    public C.List<T> asList() {
        return C.list(itr);
    }

    public C.List<T> asList(boolean readonly) {
        return readonly ? C.list(itr) : C.newList(itr);
    }

    public <TYPE> ListComprehension<TYPE> map(Class<TYPE> clz, F.IFunc1... mappers) {
        return map(mappers);
    }

    public <TYPE> ListComprehension<TYPE> map(final F.IFunc1... mappers) {
        final Iterator<? extends T> it = itr.iterator();
        return new ListComprehension<TYPE>(new Itr<TYPE>() {
            @Override
            public Iterator<TYPE> iterator() {
                return new Iterator<TYPE>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public TYPE next() {
                        Object o = it.next();
                        for (F.IFunc1 mapper : mappers) {
                            o = mapper.run(o);
                        }
                        return (TYPE) o;
                    }

                    @Override
                    public void remove() {
                        E.unsupport();
                    }
                };
            }
        });
    }

    public <TYPE> ListComprehension<TYPE> map2(Class<TYPE> cls, final F.IFunc2... mappers) {
        return map2(mappers);
    }

    public <TYPE> ListComprehension<TYPE> map2(final F.IFunc2... mappers) {
        final Iterator<? extends T> it = itr.iterator();
        return new ListComprehension<TYPE>(new Itr<TYPE>() {
            @Override
            public Iterator<TYPE> iterator() {
                return new Iterator<TYPE>() {
                    int cursor = 0;

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public TYPE next() {
                        Object o = it.next();
                        for (F.IFunc2 mapper : mappers) {
                            o = mapper.run(cursor, o);
                        }
                        cursor++;
                        return (TYPE) o;
                    }

                    @Override
                    public void remove() {
                        E.unsupport();
                    }
                };
            }
        });
    }

    public <TYPE> ListComprehension<TYPE> apply(Class<TYPE> clz, F.IFunc1... mappers) {
        return apply(mappers);
    }

    public <TYPE> ListComprehension<TYPE> apply(final F.IFunc1... mappers) {
        ListComprehension<TYPE> lc = map(mappers);
        return lc.walkthrough();
    }

    public <TYPE> ListComprehension<TYPE> apply2(Class<TYPE> cls, final F.IFunc2... mappers) {
        return apply2(mappers);
    }

    public <TYPE> ListComprehension<TYPE> apply2(final F.IFunc2... mappers) {
        ListComprehension<TYPE> lc = map2(mappers);
        return lc.walkthrough();
    }

    public boolean all(final F.IFunc1<Boolean, T> test) {
        return and(test);
    }

    public boolean any(final F.IFunc1<Boolean, T> test) {
        return or(test);
    }

    public ListComprehension<T> filter(final F.IFunc1<Boolean, T>... filters) {
        return filter(C.list(filters));
    }

    public ListComprehension<T> filter(final List<F.IFunc1<Boolean, T>> filters) {
        switch (filters.size()) {
            case 0:
                return this;
            case 1:
                return filter_(filters.get(0));
            default:
                return filter(C.head(filters, -1));
        }
    }

    private ListComprehension<T> filter_(F.IFunc1<Boolean, T> filter) {
        List<T> l = C.newList();
        accept(F.guardedVisitor(filter, C.f.addTo(l)));
        return valueOf(l);
    }

    public ListComprehension<T> filterOnIndex(F.IFunc1<Boolean, Integer> filter) {
        List<T> l = C.newList();
        accept(F.indexGuardedVisitor(filter, C.f.addTo(l)));
        return valueOf(l);
    }

    private F.T2<T, ListComprehension<T>> pop() {
        final Iterator<T> itr = this.iterator();
        final T t = itr.next();
        return F.T2(t, valueOf(itr));
    }

    public <E> E reduce(final E initVal, final F.IFunc2<E, T, E> func2) {
        return ListComprehension.reduce(initVal, this, func2);
    }

    public static <E, T> E reduce(final E initVal, final ListComprehension<T> lc, final F.IFunc2<E, T, E> func) {
        E v = initVal;
        Iterator<T> itr = lc.iterator();
        try {
            while (itr.hasNext()) {
                v = func.run(itr.next(), v);
            }
        } catch (F.Break b) {
            return b.get();
        }
        return v;
    }

    public T reduce(final F.IFunc2<T, T, T> func2) {
        return ListComprehension.reduce(this, func2);
    }

    public static <T> T reduce(final ListComprehension<T> lc, final F.IFunc2<T, T, T> func) {
        Iterator<T> itr = lc.iterator();
        if (!itr.hasNext()) {
            return null;
        }
        T v = itr.next();
        try {
            while (itr.hasNext()) {
                v = func.run(itr.next(), v);
            }
        } catch (F.Break b) {
            return b.get();
        }
        return v;
    }

    public final boolean or(final F.IFunc1<Boolean, T> test) {
        return !and(_.f.not(test));
    }

    public final boolean and(final F.IFunc1<Boolean, T> test) {
        return reduce(true, new F.F2<Boolean, T, Boolean>(){
            @Override
            public Boolean run(T t, Boolean aBoolean) {
                if (!test.run(t)) {
                    throw new F.Break(false);
                } else {
                    return true;
                }
            }
        });
    }

    public T first(final F.IFunc1<Boolean, T> cond) {
        return C.fold(map(new F.Visitor<T>() {
            @Override
            public void visit(T t) throws F.Break {
                if (cond.run(t)) {
                    throw new F.Break(t);
                }
            }
        }));
    }

    public <E> E first(final F.IFunc1<Boolean, T> cond, final F.Transformer<T, E> transformer) {
        return C.fold(map(F.guardedVisitor(cond, new F.Visitor<T>() {
            @Override
            public void visit(T t) throws F.Break {
                throw new F.Break(transformer.run(t));
            }
        })));
    }

    /**
     * Alias of {@link #accept(org.osgl.util.F.IFunc1)}
     *
     * @param visitor
     */
    public void each(F.IFunc1<?, T> visitor) {
        accept(visitor);
    }

    /**
     * Alias of {@link #accept(org.osgl.util.F.IFunc2)}
     *
     * @param visitor
     */
    public void each(F.IFunc2<?, Integer, T> visitor) {
        accept(visitor);
    }

    public void accept(F.IFunc1<?, T> visitor) {
        //this.map(visitor).walkthrough();
        for (T t : this) {
            visitor.run(t);
        }
    }

    public void accept(F.IFunc2<?, Integer, T> visitor) {
        //this.map2(visitor).walkthrough();
        int i = 0;
        for (T t : this) {
            visitor.run(i++, t);
        }
    }

    public ListComprehension<T> walkthrough() {
        C.walkThrough(itr);
        return this;
    }

    public void println() {
        apply(IO.f.println());
    }

    public static <E> ListComprehension<E> valueOf(Iterable<? extends E> it) {
        return new ListComprehension<E>(it);
    }

    public static <E> ListComprehension<E> valueOf(final Iterator<E> itr) {
        return new ListComprehension<E>(new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return itr;
            }
        });
    }

    public static <E> ListComprehension<E> valueOf(ListComprehension<E> lc) {
        return lc;
    }

    public static <E> ListComprehension<E> valueOf(E... ta) {
        return new ListComprehension<E>(Itr.valueOf(ta));
    }

    public static ListComprehension<Integer> valueOf(int[] ta) {
        return new ListComprehension<Integer>(Itr.valueOf(ta));
    }

    public static ListComprehension<Long> valueOf(long[] ta) {
        return new ListComprehension<Long>(Itr.valueOf(ta));
    }

    public static ListComprehension<Boolean> valueOf(boolean[] ta) {
        return new ListComprehension<Boolean>(Itr.valueOf(ta));
    }


    public static ListComprehension<Float> valueOf(float[] ta) {
        return new ListComprehension<Float>(Itr.valueOf(ta));
    }

    public static ListComprehension<Double> valueOf(double[] ta) {
        return new ListComprehension<Double>(Itr.valueOf(ta));
    }


    public static ListComprehension<Byte> valueOf(byte[] ta) {
        return new ListComprehension<Byte>(Itr.valueOf(ta));
    }


    public static ListComprehension<Short> valueOf(short[] ta) {
        return new ListComprehension<Short>(Itr.valueOf(ta));
    }


    public static ListComprehension<Character> valueOf(char[] ta) {
        return new ListComprehension<Character>(Itr.valueOf(ta));
    }
}
