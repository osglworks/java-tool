package org.osgl.util;

import org.osgl._;

import java.util.NoSuchElementException;

/**
 * Provide default implementation to some {@link C.Sequence} interface
 */
public abstract class SequenceBase<T>
extends TraversalBase<T> implements C.Sequence<T> {

    @Override
    public C.Sequence<T> accept(_.Function<? super T, ?> visitor) {
        forEach(visitor);
        return this;
    }

    protected void forEachLeft(_.Function<? super T, ?> visitor) {
        forEach(visitor);
    }

    @Override
    public T first() throws NoSuchElementException {
        return iterator().next();
    }

    @Override
    public T head() throws NoSuchElementException {
        return first();
    }

    @Override
    @SuppressWarnings("unchecked")
    public C.Sequence<T> acceptLeft(_.Function<? super T, ?> visitor) {
        forEachLeft(visitor);
        return this;
    }

    /**
     * Delegate to {@link TraversalBase#reduce(Object, org.osgl._.Func2)}
     * @param identity {@inheritDoc}
     * @param accumulator {@inheritDoc}
     * @param <R> {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <R> R reduceLeft(R identity, _.Func2<R, T, R> accumulator) {
        return reduce(identity, accumulator);
    }

    /**
     * Delegate to {@link TraversalBase#reduce(org.osgl._.Func2)}
     * @param accumulator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public _.Option<T> reduceLeft(_.Func2<T, T, T> accumulator) {
        return reduce(accumulator);
    }

    /**
     * Delegate to {@link TraversalBase#findOne(org.osgl._.Function)}
     * @param predicate the function map the element to Boolean
     * @return {@inheritDoc}
     */
    @Override
    public _.Option<T> findFirst(_.Function<? super T, Boolean> predicate) {
        return findOne(predicate);
    }

}
