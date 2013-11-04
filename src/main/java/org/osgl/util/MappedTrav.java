package org.osgl.util;

import org.osgl._;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 24/10/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
class MappedTrav<T, R> extends TraversalBase<R> {
    private final Iterable<T> itrb_;
    private final _.F1<T, R> mapper_;
    MappedTrav(Iterable<T> iterable, _.Function<? super T, ? extends R> mapper) {
        E.NPE(iterable, mapper);
        itrb_ = iterable;
        mapper_ = _.f1(mapper);
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return null;
    }

    @Override
    public Iterator<R> iterator() {
        return Iterators.map(itrb_.iterator(), mapper_);
    }

    @Override
    public C.Traversable<R> filter(_.Function<? super R, Boolean> predicate) {
        return null;
    }

    @Override
    public <R1> C.Traversable<R1> flatMap(_.Function<? super R, ? extends Iterable<? extends R1>> mapper
    ) {
        //TODO ...
        return null;
    }

    @Override
    public <R1> C.Traversable<R1> map(_.Function<? super R, ? extends R1> mapper) {
        //TODO ...
        return null;
    }

    @Override
    public int size() throws UnsupportedOperationException {
        //TODO ...
        return 0;
    }
}
