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

/**
 * Create {@link PropertyGetter} and {@link PropertySetter}
 */
public interface PropertyHandlerFactory {
    /**
     * Create a {@link PropertySetter}
     * @param c the class of the entity
     * @param propName the name of the property
     * @return the property setter
     */
    PropertySetter createPropertySetter(Class c, String propName);

    /**
     * Create a {@link PropertyGetter}
     * @param c the class of the entity
     * @param propName the name of the property
     * @param requireField force to get the field directly instead of using getter method
     * @return the property getter
     */
    PropertyGetter createPropertyGetter(Class c, String propName, boolean requireField);

    MapPropertyGetter createMapPropertyGetter(Class keyType, Class valType);

    MapPropertySetter createMapPropertySetter(Class keyType, Class valType);

    ListPropertyGetter createListPropertyGetter(Class itemType);

    ListPropertySetter createListPropertySetter(Class itemType);
}
