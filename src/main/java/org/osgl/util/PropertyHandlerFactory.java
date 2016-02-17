package org.osgl.util;

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
