package org.osgl.util;

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
    public static List<Type> typeParamImplementations(Class theClass, Class rootClass) {
        if (rootClass.getTypeParameters().length == 0) {
            return C.list();
        }
        try {
            return typeParamImplementations(theClass, rootClass, new ArrayList<Type>());
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(S.fmt("Cannot infer type parameter implementation on %s against %s", theClass.getName(), rootClass.getName()), e);
        }
    }

    private static List<Type> typeParamImplementations(Class theClass, Class rootClass, List<Type> subClassTypeParams) {
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
        Class superClass = null;
        while (!(superType instanceof ParameterizedType)) {
            if (null == superClass) {
                superClass = theClass.getSuperclass();
            }
            superType = superClass.getGenericSuperclass();
            superClass = superClass.getSuperclass();
        }
        if (superType instanceof ParameterizedType) {
            TypeVariable<Class>[] declaredTypeVariables = theClass.getTypeParameters();
            ParameterizedType pSuperType = $.cast(superType);
            Type[] superTypeParams = pSuperType.getActualTypeArguments();
            List<Type> nextList = new ArrayList<Type>();
            for (Type stp: superTypeParams) {
                if (stp instanceof Class || stp instanceof ParameterizedType) {
                    nextList.add(stp);
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
            superClass = (Class) pSuperType.getRawType();
            if ($.eq(superClass, rootClass)) {
                return nextList;
            }
            return typeParamImplementations(superClass, rootClass, nextList);
        }
        throw E.unexpected("Cannot find type param implementation: super type %s of %s is not a parameterized type", superType, theClass);
    }


}
