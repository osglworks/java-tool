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
