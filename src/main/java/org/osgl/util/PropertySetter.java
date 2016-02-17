package org.osgl.util;

import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;

import java.io.Serializable;

/**
 * The implementation of this class set a value on an entity
 */
public interface PropertySetter extends Serializable, PropertyHandler {

    /**
     * After calling this method, the value will be set on a property of an entity object
     * @param entity the entity object
     * @param value the value
     * @param index optional index, used when the property is a {@link java.util.List} or {@link java.util.Map}
     */
    void set(Object entity, Object value, Object index);

}
