package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Provide default implementation to some {@link C.Traversable} interface
 */
public abstract class TraversalBase<T> extends FeaturedBase implements C.Traversable<T> {

    private volatile int hc_;

    /**
     * Iterate through this traversal and apply the visitor function specified
     * to each element iterated
     *
     * @param visitor
     */
    protected void forEach(_.Function<? super T, ?> visitor) {
        C.forEach(this, visitor);
    }

    /**
     * Sub class can override this method to provide more efficient algorithm to
     * generate hash code. The default implementation use
     * {@link _#iterableHashCode(Iterable)} to generate the hash code
     *
     * @return hash code of this traversal
     */
    protected int generateHashCode() {
        return _.iterableHashCode(this);
    }


    /**
     * Iterate through the traversal to aggregate hash code of
     * all element. If the traversal is {@link C.Feature#IMMUTABLE}
     * a cached hashcode will be {@link #generateHashCode() generated}
     * at first time calling this method and returned directly for
     * the following calls
     *
     * @return the hash code of this traversal
     */
    @Override
    public int hashCode() {
        if (is(C.Feature.IMMUTABLE)) {
            if (0 == hc_) {
                hc_ = generateHashCode();
            }
            return hc_;
        } else {
            return generateHashCode();
        }
    }

    @Override
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    /**
     * Iterate through the traversal to apply the accumulator to
     * the result of previous application and the element being
     * iterated. If the traversal is empty then return the
     * identity specified
     *
     * @param identity    the identity value for the accumulating function
     * @param accumulator the function the combine two values
     * @param <R> the type of the identity and the return value
     * @return the reduce result
     */
    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        R ret = identity;
        for (T t : this) {
            ret = accumulator.apply(ret, t);
        }
        return ret;
    }

    /**
     * Iterate through the traversal to apply the accumulator to
     * the result of previous application and the element being iterated.
     * If the traversal is empty then return {@link _.Option#NONE},
     * otherwise an {@link _.Option} wrapping the accumulated result
     * is returned
     *
     * @param accumulator the function the combine two values
     * @return {@code _.NONE} if the traversal is empty or an option describing
     *         the final accumulated value
     */
    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        Iterator<T> itr = iterator();
        if (!itr.hasNext()) {
            return _.none();
        }
        T ret = itr.next();
        while (itr.hasNext()) {
            ret = accumulator.apply(ret, itr.next());
        }
        return _.some(ret);
    }

    /**
     * Iterate the traversal to check if any element applied to the predicate
     * the iteration process stop when the element is found and return
     * an option describing the element. If no element applied to the predicate
     * then {@link _.Option#NONE} is returned
     *
     * @param predicate the function map element to Boolean
     * @return an option describing the element match the predicate or none
     *         if no such element found in the traversal
     */
    @Override
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        for (T t : this) {
            if (predicate.apply(t)) {
                return _.some(t);
            }
        }
        return _.none();
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return findOne(predicate).isDefined();
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return noneMatch(_.F.negate(predicate));
    }

    @Override
    public C.Traversable<T> accept(_.Function<? super T, ?> visitor) {
        forEach(visitor);
        return this;
    }

}
