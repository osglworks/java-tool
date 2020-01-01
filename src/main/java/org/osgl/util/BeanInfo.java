package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public interface BeanInfo<T extends BeanInfo> extends AnnotationAware {
    /**
     * Returns the {@link Type} of the Bean.
     * @return the bean type.
     */
    Type type();

    List<Type> typeParams();

    /**
     * Returns the {@link Class raw type} of the Bean.
     * @return the bean class.
     */
    Class rawType();

    /**
     * Returns the Bean name.
     *
     * @return the bean name.
     */
    String name();

    /**
     * Check if the Bean is an array.
     *
     * @return `true` if the bean is an array or `false` otherwise.
     */
    boolean isArray();

    /**
     * Returns annotations marked on the Bean directly.
     * @return the annotations.
     */
    Annotation[] allAnnotations();

    /**
     * Returns the modifiers of the Bean.
     * 
     * @return the modifiers of the bean
     */
    int getModifiers();

    /**
     * Check if the Bean is transient (applied to field bean).
     * @return `true` if the field the bean represented is transient
     */
    boolean isTransient();

    /**
     * Check if the Bean is static (applied to field bean)
     * @return `true` if the field represented by the bean is static
     */
    boolean isStatic();

    /**
     * Check if the Bean is private (applied to field bean)
     * @return `true` if the field represented by the bean is private
     */
    boolean isPrivate();

    /**
     * Check if the Bean is public (applied to field bean)
     * @return `true` if the field represented by the bean is public
     */
    boolean isPublic();

    /**
     * Check if the Bean is protected (applied to field bean)
     * @return `true` if the field represented by the bean is protected
     */
    boolean isProtected();

    /**
     * Check if the Bean is final (applied to field bean)
     * @return `true` if the field represented by the bean is final
     */
    boolean isFinal();

    /**
     * Check if the Bean is static (applied to field bean)
     * @return `true` if the field represented by the bean is static
     */
    boolean isInterface();

    /**
     * Check if the Bean is an instance of class `c`.
     *
     * @param c the class
     * @return `true` if the bean is an instance of class `c`
     */
    boolean isInstanceOf(Class c);

    /**
     * Check if specified object instance is an instance of the type represented by this Bean.
     * @param o the object to be tested.
     * @return `true` if `o` is an instance of the type of this Bean
     */
    boolean isInstance(Object o);

    // this will move to upper level using Java8 default method in next major version
    class Util {
        public static boolean isGetter(Method method) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
                return false;
            }
            Class<?> returnType = method.getReturnType();
            if (void.class == returnType || Void.class == returnType) {
                return false;
            }
            if (method.getParameterTypes().length > 0) {
                return false;
            }
            String name = method.getName();
            if (name.length() < 4) {
                return false;
            }
            return name.startsWith("get") && !name.equals("getClass");
        }

        public static boolean isSetter(Method method) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
                return false;
            }
            Class<?> returnType = method.getReturnType();
            if (void.class != returnType) {
                return false;
            }
            if (method.getParameterTypes().length != 1) {
                return false;
            }
            String name = method.getName();
            if (name.length() < 4) {
                return false;
            }
            return name.startsWith("set");
        }
    }
}
