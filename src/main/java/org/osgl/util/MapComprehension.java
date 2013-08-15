package org.osgl.util;

import java.util.Iterator;
import java.util.Map;

/**
 * Simulate python's list comprehension
 */
public class MapComprehension<K, V> implements Iterable<C0.Map.Entry<K, V>> {

    private final Map<? extends K, ? extends V> _m;

    public MapComprehension(Map<? extends K, ? extends V> map) {
        _m = map;
    }

    @Override
    public Iterator<C0.Map.Entry<K, V>> iterator() {
        return new Iterator<C0.Map.Entry<K, V>>() {
            private final Iterator keys = _m.keySet().iterator();
            public boolean hasNext() {
                return keys.hasNext();
            }

            @Override
            public C0.Map.Entry<K, V> next() {
                K k = (K)keys.next();
                V v = _m.get(k);
                return C0.Map.Entry.valueOf(k, v);
            }

            @Override
            public void remove() {
                throw E.unsupport();
            }
        };
    }

    public C0.Map<K, V> asMap() {
        if (_m instanceof C0.Map) {
            return (C0.Map<K, V>)_m;
        } else {
            return C0.map(_m);
        }
    }

    public C0.Map<K, V> asMap(boolean readonly) {
        return readonly ? C0.map(_m) : C0.newMap(_m);
    }
//
//    public <K, V> MapComprehension<K, V> map(Class<TYPE> clz, F.IFunc1... mappers) {
//        return map(mappers);
//    }
//
//    public <TYPE> MapComprehension<TYPE> map(final F.IFunc1... mappers) {
//        final Iterator<? extends V> it = itr.iterator();
//        return new MapComprehension<TYPE>(new Itr<TYPE>() {
//            @Override
//            public Iterator<TYPE> iterator() {
//                return new Iterator<TYPE>() {
//                    @Override
//                    public boolean hasNext() {
//                        return it.hasNext();
//                    }
//
//                    @Override
//                    public TYPE next() {
//                        Object o = it.next();
//                        for (F.IFunc1 mapper : mappers) {
//                            o = mapper.apply(o);
//                        }
//                        return (TYPE) o;
//                    }
//
//                    @Override
//                    public void remove() {
//                        E.unsupport();
//                    }
//                };
//            }
//        });
//    }
//
//    public <TYPE> MapComprehension<TYPE> map2(Class<TYPE> cls, final F.IFunc2... mappers) {
//        return map2(mappers);
//    }
//
//    public <TYPE> MapComprehension<TYPE> map2(final F.IFunc2... mappers) {
//        final Iterator<? extends V> it = itr.iterator();
//        return new MapComprehension<TYPE>(new Itr<TYPE>() {
//            @Override
//            public Iterator<TYPE> iterator() {
//                return new Iterator<TYPE>() {
//                    int cursor = 0;
//
//                    @Override
//                    public boolean hasNext() {
//                        return it.hasNext();
//                    }
//
//                    @Override
//                    public TYPE next() {
//                        Object o = it.next();
//                        for (F.IFunc2 mapper : mappers) {
//                            o = mapper.apply(cursor, o);
//                        }
//                        cursor++;
//                        return (TYPE) o;
//                    }
//
//                    @Override
//                    public void remove() {
//                        E.unsupport();
//                    }
//                };
//            }
//        });
//    }
//
//    public <TYPE> MapComprehension<TYPE> apply(Class<TYPE> clz, F.IFunc1... mappers) {
//        return apply(mappers);
//    }
//
//    public <TYPE> MapComprehension<TYPE> apply(final F.IFunc1... mappers) {
//        MapComprehension<TYPE> lc = map(mappers);
//        return lc.walkthrough();
//    }
//
//    public <TYPE> MapComprehension<TYPE> apply2(Class<TYPE> cls, final F.IFunc2... mappers) {
//        return apply2(mappers);
//    }
//
//    public <TYPE> MapComprehension<TYPE> apply2(final F.IFunc2... mappers) {
//        MapComprehension<TYPE> lc = map2(mappers);
//        return lc.walkthrough();
//    }
//
//    public boolean all(final F.IFunc1<Boolean, V> test) {
//        return and(test);
//    }
//
//    public boolean any(final F.IFunc1<Boolean, V> test) {
//        return or(test);
//    }
//
//    public MapComprehension<V> filter(final F.IFunc1<Boolean, V>... filters) {
//        return filter(C.list(filters));
//    }
//
//    public MapComprehension<V> filter(final List<F.IFunc1<Boolean, V>> filters) {
//        switch (filters.size()) {
//            case 0:
//                return this;
//            case 1:
//                return filter_(filters.get(0));
//            default:
//                return filter(C.head(filters, -1));
//        }
//    }
//
//    private MapComprehension<V> filter_(F.IFunc1<Boolean, V> filter) {
//        List<V> l = C.newList();
//        accept(F.guardedVisitor(filter, C.f.addTo(l)));
//        return valueOf(l);
//    }
//
//    public MapComprehension<V> filterOnIndex(F.IFunc1<Boolean, Integer> filter) {
//        List<V> l = C.newList();
//        accept(F.indexGuardedVisitor(filter, C.f.addTo(l)));
//        return valueOf(l);
//    }
//
//    private F.T2<V, MapComprehension<V>> pop() {
//        final Iterator<V> itr = this.iterator();
//        final V t = itr.next();
//        return F.T2(t, valueOf(itr));
//    }
//
//    public <E> E reduce(final E initVal, final F.IFunc2<E, V, E> func2) {
//        return MapComprehension.reduce(initVal, this, func2);
//    }
//
//    public static <E, V> E reduce(final E initVal, final MapComprehension<V> lc, final F.IFunc2<E, V, E> func) {
//        E v = initVal;
//        Iterator<V> itr = lc.iterator();
//        try {
//            while (itr.hasNext()) {
//                v = func.apply(itr.next(), v);
//            }
//        } catch (F.Break b) {
//            return b.get();
//        }
//        return v;
//    }
//
//    public V reduce(final F.IFunc2<V, V, V> func2) {
//        return MapComprehension.reduce(this, func2);
//    }
//
//    public static <V> V reduce(final MapComprehension<V> lc, final F.IFunc2<V, V, V> func) {
//        Iterator<V> itr = lc.iterator();
//        if (!itr.hasNext()) {
//            return null;
//        }
//        V v = itr.next();
//        try {
//            while (itr.hasNext()) {
//                v = func.apply(itr.next(), v);
//            }
//        } catch (F.Break b) {
//            return b.get();
//        }
//        return v;
//    }
//
//    public final boolean or(final F.IFunc1<Boolean, V> test) {
//        return !and(_.f.not(test));
//    }
//
//    public final boolean and(final F.IFunc1<Boolean, V> test) {
//        return reduce(true, new F.F2<Boolean, V, Boolean>(){
//            @Override
//            public Boolean apply(V t, Boolean aBoolean) {
//                if (!test.apply(t)) {
//                    throw new F.Break(false);
//                } else {
//                    return true;
//                }
//            }
//        });
//    }
//
//    public V first(final F.IFunc1<Boolean, V> cond) {
//        return C.fold(map(new F.Visitor<V>() {
//            @Override
//            public void visit(V t) throws F.Break {
//                if (cond.apply(t)) {
//                    throw new F.Break(t);
//                }
//            }
//        }));
//    }
//
//    public <E> E first(final F.IFunc1<Boolean, V> cond, final F.Transformer<V, E> transformer) {
//        return C.fold(map(F.guardedVisitor(cond, new F.Visitor<V>() {
//            @Override
//            public void visit(V t) throws F.Break {
//                throw new F.Break(transformer.apply(t));
//            }
//        })));
//    }
//
//    /**
//     * Alias of {@link #accept(org.osgl.util.F.IFunc1)}
//     *
//     * @param visitor
//     */
//    public void each(F.IFunc1<?, V> visitor) {
//        accept(visitor);
//    }
//
//    /**
//     * Alias of {@link #accept(org.osgl.util.F.IFunc2)}
//     *
//     * @param visitor
//     */
//    public void each(F.IFunc2<?, Integer, V> visitor) {
//        accept(visitor);
//    }
//
//    public void accept(F.IFunc1<?, V> visitor) {
//        //this.map(visitor).walkthrough();
//        for (V t : this) {
//            visitor.apply(t);
//        }
//    }
//
//    public void accept(F.IFunc2<?, Integer, V> visitor) {
//        //this.map2(visitor).walkthrough();
//        int i = 0;
//        for (V t : this) {
//            visitor.apply(i++, t);
//        }
//    }
//
//    public MapComprehension<V> walkthrough() {
//        C.walkThrough(itr);
//        return this;
//    }
//
//    public void println() {
//        apply(IO.f.println());
//    }
//
//    public static <E> MapComprehension<E> valueOf(Iterable<? extends E> it) {
//        return new MapComprehension<E>(it);
//    }
//
//    public static <E> MapComprehension<E> valueOf(final Iterator<E> itr) {
//        return new MapComprehension<E>(new Iterable<E>() {
//            @Override
//            public Iterator<E> iterator() {
//                return itr;
//            }
//        });
//    }
//
//    public static <E> MapComprehension<E> valueOf(MapComprehension<E> lc) {
//        return lc;
//    }
//
//    public static <E> MapComprehension<E> valueOf(E... ta) {
//        return new MapComprehension<E>(Itr.valueOf(ta));
//    }
//
//    public static MapComprehension<Integer> valueOf(int[] ta) {
//        return new MapComprehension<Integer>(Itr.valueOf(ta));
//    }
//
//    public static MapComprehension<Long> valueOf(long[] ta) {
//        return new MapComprehension<Long>(Itr.valueOf(ta));
//    }
//
//    public static MapComprehension<Boolean> valueOf(boolean[] ta) {
//        return new MapComprehension<Boolean>(Itr.valueOf(ta));
//    }
//
//
//    public static MapComprehension<Float> valueOf(float[] ta) {
//        return new MapComprehension<Float>(Itr.valueOf(ta));
//    }
//
//    public static MapComprehension<Double> valueOf(double[] ta) {
//        return new MapComprehension<Double>(Itr.valueOf(ta));
//    }
//
//
//    public static MapComprehension<Byte> valueOf(byte[] ta) {
//        return new MapComprehension<Byte>(Itr.valueOf(ta));
//    }
//
//
//    public static MapComprehension<Short> valueOf(short[] ta) {
//        return new MapComprehension<Short>(Itr.valueOf(ta));
//    }
//
//
//    public static MapComprehension<Character> valueOf(char[] ta) {
//        return new MapComprehension<Character>(Itr.valueOf(ta));
//    }
}
