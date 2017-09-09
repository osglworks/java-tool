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

import org.junit.Test;
import org.osgl.OsglToolTestBase;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Test {@link Generics#typeParamImplementations(Class, Class)}
 */
public class ClassTypeParameterFinderTest extends OsglToolTestBase {

    private static class C1<A, B, C> implements I1<B> {}

    private static class C2<X, Y> extends C1<Y, X, Y> implements I2<Y, X> {}

    private static class C3 extends C2<String, Integer>{}

    private interface I1<FOO> {}

    private interface I2<FOO, BAR> {}

    @Test
    public void testClassRoot() {
        List<Type> typeParams = Generics.typeParamImplementations(C3.class, C1.class);
        eq(3, typeParams.size());
        eq(typeParams.get(0), Integer.class);
        eq(typeParams.get(1), String.class);
        eq(typeParams.get(2), Integer.class);
    }

    @Test
    public void testInterfaceRoot() {
        List<Type> typeParams = Generics.typeParamImplementations(C3.class, I1.class);
        eq(1, typeParams.size());
        eq(String.class, typeParams.get(0));

        typeParams = Generics.typeParamImplementations(C3.class, I2.class);
        eq(2, typeParams.size());
        eq(Integer.class, typeParams.get(0));
        eq(String.class, typeParams.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument() {
        List<Type> typeParams = Generics.typeParamImplementations(C2.class, C1.class);
        yes(typeParams.isEmpty());
    }

}
