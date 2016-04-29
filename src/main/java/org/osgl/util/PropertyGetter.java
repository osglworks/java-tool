package org.osgl.util;

import java.io.Serializable;

public interface PropertyGetter extends Serializable, PropertyHandler {

    public static enum NullValuePolicy {
        /**
         * Return {@code null} when {@code null} value encountered.
         * This is the default policy
         */
        RETURN_NULL,
        /**
         * Throws {@link NullPointerException} when {@code null} value encountered
         */
        NPE,
        /**
         * Create new instance when {@code null} value encountered.
         * This is useful when use {@link PropertyGetter} along with {@link PropertySetter}
         * to set a value on a property path. E.g. set "a string" to {@code "bar.0.zee"} on
         * {@code foo} entity
         */
        CREATE_NEW
    }

    /**
     * Set null value policy
     * @param nvp the policy
     */
    void setNullValuePolicy(NullValuePolicy nvp);

    /**
     * Get the property value from an entity
     * @param entity the entity object
     * @param index optional index, used when the property is a {@link java.util.List} or {@link java.util.Map}
     * @return the property value
     */
    Object get(Object entity, Object index);

    /**
     * Returns the corresponding setter
     * @return the setter
     */
    PropertySetter setter();

}
