package org.osgl.issues.gh181;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2019 OSGL (Open Source General Library)
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
import org.osgl.TestBase;
import org.osgl.util.Generics;

import java.util.Map;

public class Gh197 extends TestBase {

    public static class GrandParent<T> {
        T t;
    }

    public static class Req<V, ID> {
        ID id;
        V v;
    }

    public static class Parent<K, V, RQ extends Req<K, V>> extends GrandParent<RQ> {
    }

    public static class Me extends Parent<String, Integer, Req<String, Integer>> {}

    @Test
    public void test() {
        Map<String, Class> lookup = Generics.buildTypeParamImplLookup(Me.class);
        eq(String.class, lookup.get("K"));
        eq(Integer.class, lookup.get("V"));
        eq(Req.class, lookup.get("RQ"));
        eq(String.class, lookup.get("RQ.V"));
    }

}
