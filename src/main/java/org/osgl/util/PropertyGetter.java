package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.io.Serializable;

public interface PropertyGetter<OBJECT, PROP> extends Osgl.Function<OBJECT, PROP>, Serializable {
}
