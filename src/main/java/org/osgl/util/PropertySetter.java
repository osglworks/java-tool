package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.io.Serializable;

public interface PropertySetter<OBJECT, PROP> extends Osgl.Func2<OBJECT, PROP, Void>, Serializable {
}
