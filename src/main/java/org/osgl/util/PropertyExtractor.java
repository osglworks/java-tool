package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.io.Serializable;

public abstract class PropertyExtractor<OBJECT, PROP> extends Osgl.F1<OBJECT, PROP> implements Serializable {

    @Override
    public PROP apply(OBJECT object) throws NotAppliedException, Osgl.Break {
        return extract(object);
    }

    protected abstract PROP extract(OBJECT object);
}
