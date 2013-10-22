package org.osgl.util;

import org.osgl._;

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

    public C0.List<T> asList() {
        return C0.list(itr);
    }

    public C0.List<T> asList(boolean readonly) {
        return readonly ? C0.list(itr) : C0.newList(itr);
    }

    public <TYPE> ListComprehension<TYPE> map(Class<TYPE> clz, _.Func1... mappers) {
        return map(mappers);
    }

    public <TYPE> ListComprehension<TYPE> map(final _.Func1... mappers) {
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
                        for (_.Func1 mapper : mappers) {
                            o = mapper.apply(o);
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

    public <TYPE> ListComprehension<TYPE> map2(Class<TYPE> cls, final _.Func2... mappers) {
        return map2(mappers);
    }

    public <TYPE> ListComprehension<TYPE> map2(final _.Func2... mappers) {
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
                        for (_.Func2 mapper : mappers) {
                            o = mapper.apply(cursor, o);
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

    public <TYPE> ListComprehension<TYPE> apply(Class<TYPE> clz, _.Func1... mappers) {
        return apply(mappers);
    }

    public <TYPE> ListComprehension<TYPE> apply(final _.Func1... mappers) {
        ListComprehension<TYPE> lc = map(mappers);
        return lc.walkthrough();
    }

    public <TYPE> ListComprehension<TYPE> apply2(Class<TYPE> cls, final _.Func2... mappers) {
        return apply2(mappers);
    }

    public <TYPE> ListComprehension<TYPE> apply2(final _.Func2... mappers) {
        ListComprehension<TYPE> lc = map2(mappers);
        return lc.walkthrough();
    }

    public boolean all(final _.Func1<Boolean, T> test) {
        return and(test);
    }

    public boolean any(final _.Func1<Boolean, T> test) {
        return or(test);
    }

    public ListComprehension<T> filter(final _.Func1<Boolean, T>... filters) {
        return filter(C0.list(filters));
    }

    public ListComprehension<T> filter(final List<_.Func1<Boolean, T>> filters) {
        switch (filters.size()) {
            case 0:
                return this;
            case 1:
                return filter_(filters.get(0));
            default:
                return filter(C0.head(filters, -1));
        }
    }

    private ListComprehension<T> filter_(_.Func1<Boolean, T> filter) {
        List<T> l = C0.newList();
        accept(_.guardedVisitor(filter, C0.f.addTo(l)));
        return valueOf(l);
    }

    public ListComprehension<T> filterOnIndex(_.Func1<Boolean, Integer> filter) {
        List<T> l = C0.newList();
        accept(_.indexGuardedVisitor(filter, C0.f.addTo(l)));
        return valueOf(l);
    }

    private _.T2<T, ListComprehension<T>> pop() {
        final Iterator<T> itr = this.iterator();
        final T t = itr.next();
        return _.T2(t, valueOf(itr));
    }

    public <E> E reduce(final E initVal, final _.Func2<E, T, E> func2) {
        return ListComprehension.reduce(initVal, this, func2);
    }

    public static <E, T> E reduce(final E initVal, final ListComprehension<T> lc, final _.Func2<E, T, E> func) {
        E v = initVal;
        Iterator<T> itr = lc.iterator();
        try {
            while (itr.hasNext()) {
                v = func.apply(itr.next(), v);
            }
        } catch (_.Break b) {
            return b.get();
        }
        return v;
    }

    public T reduce(final _.Func2<T, T, T> func2) {
        return ListComprehension.reduce(this, func2);
    }

    public static <T> T reduce(final ListComprehension<T> lc, final _.Func2<T, T, T> func) {
        Iterator<T> itr = lc.iterator();
        if (!itr.hasNext()) {
            return null;
        }
        T v = itr.next();
        try {
            while (itr.hasNext()) {
                v = func.apply(itr.next(), v);
            }
        } catch (_.Break b) {
            return b.get();
        }
        return v;
    }
    
    public final ListComprehension<T> reverse() {
        return C0.lc(asList().reverse());
    }

    public final boolean or(final _.Func1<Boolean, T> test) {
        return !and(X.f.not(test));
    }

    public final boolean and(final _.Func1<Boolean, T> test) {
        for (T t : itr) {
            if (!test.apply(t)) {
                return false;
            }
        }
        return true;
    }

    public T first(final _.Func1<Boolean, T> cond) {
        for (T t : itr) {
            if (cond.apply(t)) {
                return t;
            }
        }
        return null;
    }

    public T last(final _.Func1<Boolean, T> cond) {
        return reverse().first(cond);
    }

    public <E> E first(final _.Func1<Boolean, T> cond, final _.Transformer<T, E> transformer) {
        for (T t : itr) {
            if (cond.apply(t)) {
                return transformer.apply(t);
            }
        }
        return null;
    }

    /**
     * Alias of {@link #accept(org.osgl._.Func1)}
     *
     * @param visitor
     */
    public void each(_.Func1<?, T> visitor) {
        accept(visitor);
    }

    /**
     * Alias of {@link #accept(org.osgl._.Func2)}
     *
     * @param visitor
     */
    public void each(_.Func2<?, Integer, T> visitor) {
        accept(visitor);
    }

    public void accept(_.Func1<?, T> visitor) {
        //this.map(visitor).walkthrough();
        for (T t : this) {
            visitor.apply(t);
        }
    }

    public void accept(_.Func2<?, Integer, T> visitor) {
        //this.map2(visitor).walkthrough();
        int i = 0;
        for (T t : this) {
            visitor.apply(i++, t);
        }
    }

    public ListComprehension<T> walkthrough() {
        C0.walkThrough(itr);
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
