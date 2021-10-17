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

import org.osgl.$;
import org.osgl.exception.UnexpectedException;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

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
     * For example, suppose a super class has generic type params: `MyBase&lt;MODEL, QUERY&gt;`, and a
     * sub class is declared as `MyModel&lt;MyModel, MyQuery&gt;`, then passing `MyModel.class` to
     * this method shall return a list of `{MyModel.class, MyQuery.class}`
     * <p>
     * <p>
     * If the specified class doesn't have generic type declared then it shall return
     * an empty list
     *
     * @param theClass  the end class
     * @param rootClass the root class or interface
     * @return a list of type variable implementation on root class
     */
    public static List<Type> typeParamImplementations(Class theClass, Class rootClass) {
        if (rootClass.getTypeParameters().length == 0) {
            return C.list();
        }
        try {
            return typeParamImplementations(theClass, rootClass, new ArrayList<Type>(), true);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(S.fmt("Cannot infer type parameter implementation on %s against %s", theClass.getName(), rootClass.getName()), e);
        }
    }

    /**
     * Same function with {@link #typeParamImplementations(Class, Class)}.
     *
     * However this method will return an empty list instead of throwing out exception when
     * type parameter not found.
     *
     * @param theClass  the end class
     * @param rootClass the root class or interface
     * @return a list of type variable implementation on root class
     */
    public static List<Type> tryGetTypeParamImplementations(Class theClass, Class rootClass) {
        if (rootClass.getTypeParameters().length == 0) {
            return C.list();
        }
        try {
            return typeParamImplementations(theClass, rootClass, new ArrayList<Type>(), false);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(S.fmt("Cannot infer type parameter implementation on %s against %s", theClass.getName(), rootClass.getName()), e);
        }
    }

    public static Map<String, Class> subLookup(Map<String, Class> lookup, String prefix) {
        if (S.blank(prefix)) {
            return lookup;
        }
        if (!prefix.endsWith(".")) {
            prefix += ".";
        }
        Map<String, Class> subLookup = new HashMap<>();
        if (null == lookup) {
            return subLookup;
        }
        for (Map.Entry<String, Class> entry : lookup.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                subLookup.put(key.substring(prefix.length()), entry.getValue());
            }
        }
        return subLookup;
    }

    /**
     * Build class type variable name and type variable implementation lookup
     *
     * @param theClass the class to build the lookup
     * @return the lookup
     */
    public static Map<String, Class> buildTypeParamImplLookup(Class theClass) {
        Map<String, Class> lookup = new HashMap<>();
        buildTypeParamImplLookup(theClass, lookup);
        return lookup;
    }

    public static void buildTypeParamImplLookup(Class theClass, Map<String, Class> lookup) {
        if (null == theClass || Object.class == theClass) {
            return;
        }

        // what type variable of the super class implemented by this class
        Type superType = theClass.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType ptype = $.cast(superType);
            Type[] typeParams = ptype.getActualTypeArguments();
            TypeVariable[] typeArgs = theClass.getSuperclass().getTypeParameters();
            buildTypeParamImplLookup("", typeParams, typeArgs, lookup);
        }

        buildTypeParamImplLookup(theClass.getSuperclass(), lookup);
    }

    public static void buildTypeParamImplLookup(String prefix, Type[] typeParams, TypeVariable[] typeArgs, Map<String, Class> lookup) {
        int len = typeParams.length;
        for (int i = 0; i < len; ++i) {
            Type typeParam = typeParams[i];
            if (typeParam instanceof Class) {
                TypeVariable typeVar = typeArgs[i];
                lookup.put(lookupKey(typeVar, prefix), (Class) typeParam);
            } else if (typeParam instanceof TypeVariable) {
                TypeVariable var = $.cast(typeParam);
                String name = var.getName();
                Class impl = lookup.get(name);
                TypeVariable typeVar = typeArgs[i];
                if (null != impl) {
                    lookup.put(lookupKey(typeVar, prefix), impl);
                } else {
                    Type[] ta = var.getBounds();
                    if (null != ta && ta.length == 1) {
                        Type bound = ta[0];
                        if (bound instanceof Class) {
                            lookup.put(lookupKey(typeVar, prefix), (Class) bound);
                        }
                    }
                }
                // cascade populate nested type lookup pairs
                Map<String, Class> subLookup = subLookup(lookup, name);
                if (!subLookup.isEmpty()) {
                    String newPrefix = typeVar.getName() + ".";
                    for (Map.Entry<String, Class> entry : subLookup.entrySet()) {
                        lookup.put(newPrefix + entry.getKey(), entry.getValue());
                    }
                }
            } else if (typeParam instanceof ParameterizedType) {
                ParameterizedType ptype0 = (ParameterizedType) typeParam;
                TypeVariable typeVar = typeArgs[i];
                Type rawType = ptype0.getRawType();
                if (rawType instanceof Class) {
                    lookup.put(lookupKey(typeVar, prefix), (Class) rawType);
                    buildTypeParamImplLookup(lookupKey(typeVar, prefix), ptype0.getActualTypeArguments(), ((Class) rawType).getTypeParameters(), lookup);
                } else {
                    throw new UnexpectedException("Unknown typeParam: " + ptype0);
                }
            }
        }
    }

    private static String lookupKey(TypeVariable typeVar, String prefix) {
        return S.isBlank(prefix) ? typeVar.getName() : S.concat(prefix, ".", typeVar.getName());
    }

    /**
     * Return the real return type of a method.
     *
     * Normally it returns {@link Method#getReturnType()}
     *
     * In case a method is declared in a super type and the return type is declared as a generic {@link TypeVariable},
     * when a sub class with type variable implementation presented, then it shall return the implementation type. E.g
     *
     * Super type:
     *
     * ```java
     * public abstract class Foo<T> {
     *     T getFoo();
     * }
     * ```
     *
     * Sub type:
     *
     * ```java
     * public class StringFoo extends Foo<String> {
     *     String getFoo() {return "foo";}
     * }
     * ```
     *
     * Usage of `getReturnType`:
     *
     * ```java
     * Method method = Foo.class.getMethod("getFoo");
     * Class<?> realReturnType = Generics.getReturnType(method, StringFoo.class); // return String.class
     * ```
     *
     * @param method the method
     * @param theClass the class on which the method is invoked
     * @return the return type
     */
    public static Class<?> getReturnType(Method method, Class<?> theClass) {
        Type type = method.getGenericReturnType();
        if (type == Class.class) {
            return $.cast(type);
        }
        if (type instanceof TypeVariable) {
            Map<String, Class> lookup = Generics.buildTypeParamImplLookup(theClass);
            String name = ((TypeVariable) type).getName();
            Class<?> realType = lookup.get(name);
            if (null != realType) {
                return realType;
            }
        }
        return method.getReturnType();
    }

    private static List<Type> typeParamImplementations(Class theClass, Class rootClass, List<Type> subClassTypeParams, boolean raiseExceptionIfNotFound) {
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
        boolean theClassIsInterface = theClass.isInterface();
        while (!(superType instanceof ParameterizedType) && Object.class != superType) {
            if (theClassIsInterface) {
                try {
                    if (null == superClass) {
                        superClass = theClass;
                    }
                    Type[] types = superClass.getGenericInterfaces();
                    if (types.length == 0) {
                        break;
                    }
                    superType = types[0];
                    Class[] intfs = theClass.getInterfaces();
                    superClass = intfs[0];
                } catch (RuntimeException e) {
                    throw new UnexpectedException("Cannot find type implementation for %s", theClass);
                }
            } else {
                if (null == superClass) {
                    superClass = theClass.getSuperclass();
                }
                superType = superClass.getGenericSuperclass();
                superClass = superClass.getSuperclass();
            }
        }
        if (superType instanceof ParameterizedType) {
            TypeVariable<Class>[] declaredTypeVariables = theClass.getTypeParameters();
            ParameterizedType pSuperType = $.cast(superType);
            Type[] superTypeParams = pSuperType.getActualTypeArguments();
            List<Type> nextList = new ArrayList<>();
            for (Type stp : superTypeParams) {
                if (stp instanceof Class || stp instanceof ParameterizedType) {
                    nextList.add(stp);
                } else if (stp instanceof TypeVariable) {
                    boolean found = false;
                    for (int i = 0; i < declaredTypeVariables.length; ++i) {
                        if (subClassTypeParams.size() <= i) {
                            break;
                        }
                        TypeVariable declared = declaredTypeVariables[i];
                        if ($.eq(declared, stp)) {
                            nextList.add(subClassTypeParams.get(i));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if (raiseExceptionIfNotFound) {
                            E.illegalArgumentIf(!found, "Cannot find type implementation for %s", theClass);
                        }
                        return C.list();
                    }
                }
            }
            superClass = (Class) pSuperType.getRawType();
            if ($.eq(superClass, rootClass)) {
                return nextList;
            }
            return typeParamImplementations(superClass, rootClass, nextList, raiseExceptionIfNotFound);
        }
        if (raiseExceptionIfNotFound) {
            throw E.unexpected("Cannot find type param implementation: super type %s of %s is not a parameterized type", superType, theClass);
        }
        return C.list();
    }

    public static void main(String[] args) {
        Class c = Serializable.class;
        System.out.println(c.getGenericSuperclass());
        System.out.println($.toString2(c.getGenericInterfaces()));
    }


}
