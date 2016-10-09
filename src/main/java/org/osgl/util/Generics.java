package org.osgl.util;

import netscape.security.ParameterizedTarget;
import org.osgl.$;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utilities for generic relevant operations
 */
public class Generics {

    public static <T> Class<T> classOf(Type type) {
        if (type instanceof Class) {
            return $.cast(type);
        } else if (type instanceof ParameterizedType) {
            return $.cast(((ParameterizedType) type).getRawType());
        } else {
            throw new IllegalArgumentException("Cannot find class from type: " + type);
        }
    }

    /**
     * Returns implementation of type parameters declared in the root class/interface by the given sub class
     * <p>
     * For example, suppose a super class has generic type params: `MyBase<MODEL, QUERY>`, and a
     * sub class is declared as `MyModel<MyModel, MyQuery>`, then passing `MyModel.class` to
     * this method shall return a list of `{MyModel.class, MyQuery.class}`
     * <p>
     *
     * If the specified class doesn't have generic type declared then it shall return
     * an empty list
     *
     *
     * @param theClass the end class
     * @param rootClass the root class or interface
     * @return a list of type variable implementation on root class
     */
    public static List<Class> typeParamImplementations(Class theClass, Class rootClass) {
        if (rootClass.getTypeParameters().length == 0) {
            return C.list();
        }
        return typeParamImplementations(theClass, rootClass, new ArrayList<Class>());
    }

    private static List<Class> typeParamImplementations(Class theClass, Class rootClass, List<Class> subClassTypeParams) {
        Type superType = null;
        Type[] interfaces = theClass.getGenericInterfaces();
        for (Type intf : interfaces) {
            if (intf instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) intf).getRawType();
                if ($.eq(rawType, rootClass)) {
                    superType = intf;
                }
            }
        }
        if (null == superType) {
            superType = theClass.getGenericSuperclass();
        }
        TypeVariable<Class>[] declaredTypeVariables = theClass.getTypeParameters();
        if (superType instanceof ParameterizedType) {
            ParameterizedType pSuperType = $.cast(superType);
            Type[] superTypeParams = pSuperType.getActualTypeArguments();
            List<Class> nextList = new ArrayList<Class>();
            for (Type stp: superTypeParams) {
                if (stp instanceof Class) {
                    nextList.add((Class) stp);
                } else if (stp instanceof ParameterizedType) {
                    nextList.add((Class) ((ParameterizedType) stp).getRawType());
                } else if (stp instanceof TypeVariable) {
                    boolean found = false;
                    for (int i = 0; i < declaredTypeVariables.length; ++i) {
                        TypeVariable declared = declaredTypeVariables[i];
                        if ($.eq(declared, stp)) {
                            nextList.add(subClassTypeParams.get(i));
                            found = true;
                        }
                    }
                    E.illegalStateIf(!found, "Cannot find type implementation for %s", theClass);
                }
            }
            Class superClass = (Class) pSuperType.getRawType();
            if ($.eq(superClass, rootClass)) {
                return nextList;
            }
            return typeParamImplementations(superClass, rootClass, nextList);
        }
        throw E.unexpected("Cannot find type param implementation: super type %s of %s is not a parameterized type", superType, theClass);
    }


}
