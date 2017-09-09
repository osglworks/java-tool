package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgl.$;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Provide default implementation to some {@link C.Traversable} interface
 */
public abstract class
TraversableBase<T> extends FeaturedBase implements C.Traversable<T> {

    private volatile int hc_;

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.LAZY, C.Feature.READONLY);
    }

    /**
     * Iterate through this traversal and apply the visitor function specified
     * to each element iterated
     *
     * @param visitor the visitor
     */
    public TraversableBase<T> forEach($.Function<? super T, ?> visitor) {
        C.forEach(this, visitor);
        return this;
    }

    /**
     * Sub class can override this method to provide more efficient algorithm to
     * generate hash code. The default implementation use
     * {@link $#iterableHashCode(Iterable)} to generate the hash code
     *
     * @return hash code of this traversal
     */
    protected int generateHashCode() {
        return $.iterableHashCode(this);
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
        if (!is(C.Feature.LIMITED)) {
            return super.hashCode();
        }
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
    public C.Traversable<T> lazy() {
        setFeature(C.Feature.LAZY);
        return this;
    }

    @Override
    public C.Traversable<T> eager() {
        unsetFeature(C.Feature.LAZY);
        return this;
    }

    @Override
    public C.Traversable<T> parallel() {
        setFeature(C.Feature.PARALLEL);
        return this;
    }

    @Override
    public C.Traversable<T> sequential() {
        unsetFeature(C.Feature.PARALLEL);
        return this;
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
    public <R> R reduce(R identity, $.Func2<R, T, R> accumulator) {
        R ret = identity;
        for (T t : this) {
            ret = accumulator.apply(ret, t);
        }
        return ret;
    }

    /**
     * Iterate through the traversal to apply the accumulator to
     * the result of previous application and the element being iterated.
     * If the traversal is empty then return {@link $.Option#NONE},
     * otherwise an {@link $.Option} wrapping the accumulated result
     * is returned
     *
     * @param accumulator the function the combine two values
     * @return {@code _.NONE} if the traversal is empty or an option describing
     *         the final accumulated value
     */
    @Override
    public $.Option<T> reduce($.Func2<T, T, T> accumulator) {
        Iterator<T> itr = iterator();
        if (!itr.hasNext()) {
            return $.none();
        }
        T ret = itr.next();
        while (itr.hasNext()) {
            ret = accumulator.apply(ret, itr.next());
        }
        return $.some(ret);
    }

    /**
     * Iterate the traversal to check if any element applied to the predicate
     * the iteration process stop when the element is found and return
     * an option describing the element. If no element applied to the predicate
     * then {@link $.Option#NONE} is returned
     *
     * @param predicate the function map element to Boolean
     * @return an option describing the element match the predicate or none
     *         if no such element found in the traversal
     */
    @Override
    public $.Option<T> findOne($.Function<? super T, Boolean> predicate) {
        for (T t : this) {
            if (predicate.apply(t)) {
                return $.some(t);
            }
        }
        return $.none();
    }

    @Override
    public boolean anyMatch($.Function<? super T, Boolean> predicate) {
        return findOne(predicate).isDefined();
    }

    @Override
    public boolean noneMatch($.Function<? super T, Boolean> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public boolean allMatch($.Function<? super T, Boolean> predicate) {
        return noneMatch($.F.negate(predicate));
    }

    @Override
    public C.Traversable<T> accept($.Function<? super T, ?> visitor) {
        return forEach(visitor);
    }

    @Override
    public C.Traversable<T> each($.Function<? super T, ?> visitor) {
        return forEach(visitor);
    }

    @Override
    public <R> C.Traversable<R> map($.Function<? super T, ? extends R> mapper) {
        return MappedTrav.of(this, mapper);
    }

    @Override
    public <R> C.Traversable<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper
    ) {
        return FlatMappedTrav.of(this, mapper);
    }

    @Override
    public C.Traversable<T> filter($.Function<? super T, Boolean> predicate) {
        return FilteredTrav.of(this, predicate);
    }
}
