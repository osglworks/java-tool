package org.osgl.util;

import java.util.EnumSet;

/**
 * Provide common logic to handle {@link C.Featured} interface
 */
public abstract class FeaturedBase implements C.Featured {

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

    protected FeaturedBase setFeature(C.Feature feature) {
        features_().add(feature);
        return this;
    }

    protected FeaturedBase unsetFeature(C.Feature feature) {
        features_().remove(feature);
        return this;
    }
}
