package com.greenlaw110.util;

import java.util.Iterator;
import java.util.List;

/**
 * Simulate python's list comprehension
 */
public class ListComprehension<T> implements Iterable<T> {
    
    private final Iterable<T> itr;

    public ListComprehension(Iterable<T> itr) {
        this.itr = itr;
    }
    
    @Override
    public Iterator<T> iterator() {
        return itr.iterator();
    }
    
    public F.List<T> asList() {
        return C.list(itr);
    }
    
    public F.List<T> asList(boolean readonly) {
        return readonly ? C.list(itr) : C.newList(itr);
    }
    
    public <TYPE> ListComprehension<TYPE> map(Class<TYPE> clz, F.IFunc1... mappers) {
        return map(mappers);
    }
    
    public <TYPE> ListComprehension<TYPE> map(final F.IFunc1... mappers) {
        final Iterator<T> it = itr.iterator();
        return new ListComprehension<TYPE>(new Itr <TYPE>() {
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
                        return (TYPE)o;
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
        final Iterator<T> it = itr.iterator();
        return new ListComprehension<TYPE>(new Itr <TYPE>() {
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
                        return (TYPE)o;
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
        ListComprehension <TYPE> lc = map(mappers);
        return lc.walkthrough();
    }
    
    public <TYPE> ListComprehension<TYPE> apply2(Class<TYPE> cls, final F.IFunc2... mappers) {
        return apply2(mappers);
    }
    
    public <TYPE> ListComprehension<TYPE> apply2(final F.IFunc2... mappers) {
        ListComprehension <TYPE> lc = map2(mappers);
        return lc.walkthrough();
    }
    
    public boolean all(final F.IFunc1<Boolean, T> test) {
        return reduce(true, this, new F.F2<Boolean, Boolean, T>() {
            @Override
            public Boolean run(Boolean initVal, T t) {
                if (!test.run(t)) {
                    throw new F.Visitor.Break(false);
                }
                return true;
            }
        });
    }
    
    public boolean any(final F.IFunc1<Boolean, T> test) {
        return !all(_.f.not(test));
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

    public <E> E reduce(final E initVal, final F.IFunc2<E, E, T> func2) {
        return ListComprehension.reduce(initVal, this, func2);
    }
    
    public static <E, T> E reduce(final E initVal, final ListComprehension<T> lc, final F.IFunc2<E, E, T> func) {
        if (lc.iterator().hasNext()) {
            F.T2<T, ListComprehension<T>> t2 = lc.pop();
            try {
                return reduce(func.run(initVal, t2._1), t2._2, func);
            } catch (F.Visitor.Break b) {
                return b.get();
            }
        } else {
            return initVal;
        }
    }
    
    public T first(final F.IFunc1<Boolean, T> cond) {
        return C.fold(map(new F.Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                if (cond.run(t)) {
                    throw new Break(t);
                }
            }
        }));
    }
    
    public <E> E first(final F.IFunc1<Boolean, T> cond, final F.Transformer<T, E> transformer) {
        return C.fold(map(F.guardedVisitor(cond, new F.Visitor<T>() {
            @Override
            public void visit(T t) throws Break {
                throw new Break(transformer.run(t));
            }
        })));
    }
    
    public void each(F.IFunc1<?, T> visitor) {
        accept(visitor);
    }

    public void each(F.IFunc2<?, Integer, T> visitor) {
        accept(visitor);
    }
        
    public void accept(F.IFunc1<?, T> visitor) {
        //this.map(visitor).walkthrough();
        for (T t: this) {
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
        apply(_.f.println());
    }
    
    public static <E> ListComprehension<E> valueOf(Iterable<E> it) {
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
