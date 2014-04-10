package org.osgl.util;

import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.AbstractSet;
import java.util.EnumSet;

/**
 * Created by luog on 1/02/14.
 */
public abstract class SetBase<T> extends AbstractSet<T> implements C.Set<T> {

    @Override
    public C.Set<T> parallel() {
        setFeature(C.Feature.PARALLEL);
        return this;
    }

    @Override
    public C.Set<T> sequential() {
        unsetFeature(C.Feature.PARALLEL);
        return this;
    }

    @Override
    public C.Set<T> lazy() {
        setFeature(C.Feature.LAZY);
        return this;
    }

    @Override
    public C.Set<T> eager() {
        unsetFeature(C.Feature.LAZY);
        return this;
    }

    @Override
    public C.Set<T> filter(_.Function<? super T, Boolean> predicate) {
        boolean immutable = isImmutable();
        int sz = size();
        // TODO: handle lazy filter
        if (immutable) {
            if (0 == sz) {
                return Nil.set();
            }
            ListBuilder<T> lb = new ListBuilder<T>(sz);
            forEach(_.predicate(predicate).ifThen(C.F.addTo(lb)));
            return lb.toSet();
        } else {
            if (0 == sz) {
                return C.newSet();
            }
            C.Set<T> set = C.newSet();
            forEach(_.predicate(predicate).ifThen(C.F.addTo(set)));
            return set;
        }
    }

    @Override
    public SetBase<T> accept(_.Function<? super T, ?> visitor) {
        return forEach(visitor);
    }

    @Override
    public <R> C.Traversable<R> map(_.Function<? super T, ? extends R> mapper) {
        boolean immutable = isImmutable();
        int sz = size();
        if (isLazy()) {
            return MappedTrav.of(this, mapper);
        }
        if (immutable) {
            if (0 == sz) {
                return Nil.set();
            }
            ListBuilder<R> lb = new ListBuilder<R>(sz);
            forEach(_.f1(mapper).andThen(C.F.addTo(lb)));
            return lb.toList();
        } else {
            if (0 == sz) {
                return C.newList();
            }
            C.List<R> l = C.newSizedList(sz);
            forEach(_.f1(mapper).andThen(C.F.addTo(l)));
            return l;
        }
    }

    @Override
    public <R> C.Traversable<R> flatMap(_.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return null;
    }

    @Override
    public <R> R reduce(R identity, _.Func2<R, T, R> accumulator) {
        return null;
    }

    @Override
    public _.Option<T> reduce(_.Func2<T, T, T> accumulator) {
        return null;
    }

    @Override
    public boolean allMatch(_.Function<? super T, Boolean> predicate) {
        return false;
    }

    @Override
    public boolean anyMatch(_.Function<? super T, Boolean> predicate) {
        return false;
    }

    @Override
    public boolean noneMatch(_.Function<? super T, Boolean> predicate) {
        return false;
    }

    @Override
    public _.Option<T> findOne(_.Function<? super T, Boolean> predicate) {
        return null;
    }


    // utilities
    protected final boolean isLazy() {
        return is(C.Feature.LAZY);
    }

    protected final boolean isImmutable() {
        return is(C.Feature.IMMUTABLE);
    }

    protected final boolean isReadOnly() {
        return is(C.Feature.READONLY);
    }

    protected final boolean isMutable() {
        return !isImmutable() && !isReadOnly();
    }

    /**
     * Sub class could override this method to implement iterating in parallel.
     *
     * <p>The iterating support partial function visitor by ignoring the
     * {@link org.osgl.exception.NotAppliedException} thrown out by visitor's apply
     * method call</p>
     *
     * @param visitor
     * @throws org.osgl._.Break if visitor needs to terminate the iteration
     */
    public SetBase<T> forEach(_.Function<? super T, ?> visitor) throws _.Break {
        for (T t : this) {
            try {
                visitor.apply(t);
            } catch (NotAppliedException e) {
                // ignore
            }
        }
        return this;
    }

    @Override
    public SetBase<T> each(_.Function<? super T, ?> visitor) {
        return forEach(visitor);
    }

    // --- Featured methods

    volatile private EnumSet<C.Feature> features_;

    protected final EnumSet<C.Feature> features_() {
        if (null == features_) {
            synchronized (this) {
                if (null == features_) {
                    features_ = initFeatures();
                    assert(null != features_);
                }
            }
        }
        return features_;
    }

    /**
     * Sub class should override this method to provide initial feature
     * set for the feature based instance
     *
     * @return the initial feature set configuration
     */
    abstract protected EnumSet<C.Feature> initFeatures();

    @Override
    public final EnumSet<C.Feature> features() {
        return EnumSet.copyOf(features_());
    }

    @Override
    public final boolean is(C.Feature feature) {
        return features_().contains(feature);
    }

    protected SetBase<T> setFeature(C.Feature feature) {
        features_().add(feature);
        return this;
    }

    protected SetBase<T> unsetFeature(C.Feature feature) {
        features_().remove(feature);
        return this;
    }

    // --- eof Featured methods



}
