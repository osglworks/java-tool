package org.osgl.util;

import org.osgl.Osgl;

/**
 * Represent an immutable value
 * @param <T> the generic type of the object stored in the value
 */
public final class Const<T> {

    private T v;

    /**
     * Construct a Const with a value
     * @param value the value to be stored in the `Const`
     */
    private Const(T value) {
        v = value;
    }

    /**
     * Returns the object stored in this `Val`
     * @return the object stored
     */
    public T get() {
        return v;
    }

    @Override
    public String toString() {
        return S.string(v);
    }

    public Osgl.Var<T> toVar() {
        return Osgl.var(v);
    }

    public Osgl.Val<T> toVal() {
        return Osgl.val(v);
    }

    @Override
    public boolean equals(Object o) {
        return (this == o || ((o instanceof Const) && Osgl.eq(((Const)o).v, v)));
    }

    @Override
    public int hashCode() {
        return Osgl.hc(v);
    }

    public static <E> Const of(E t) {
        return new Const<E>(t);
    }

    public static <E> Const of(Osgl.Var<E> var) {
        return null == var ? new Const<E>(null) : new Const<E>(var.get());
    }

}
