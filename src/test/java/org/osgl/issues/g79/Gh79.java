package org.osgl.issues.g79;

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

import static org.osgl.Lang.requireNotNull;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;
import org.osgl.util.N;

import java.util.List;
import java.util.Map;

public class Gh79 extends TestBase {

    @Test
    public void test() {
        Map<String, Integer> foo1 = C.Map("id", N.randInt());
        Map<String, Integer> foo2 = C.Map("id", N.randInt());
        List<Map<String, Integer>> fooList = C.list(foo1, foo2);
        Map<String, List<Map<String, Integer>>> fooMap = C.Map("abc", fooList);
        Map<String, Map<String, List<Map<String, Integer>>>> bar = C.Map("fooMap", fooMap);
        List<Map<String, Map<String, List<Map<String, Integer>>>>> barList = C.list(bar);
        Map<String, List<Map<String, Map<String, List<Map<String, Integer>>>>>> barMap = C.Map("xyz", barList);
        Map<String, Map<String, List<Map<String, Map<String, List<Map<String, Integer>>>>>>> beanData = C.Map("barMap", barMap);
        Bean bean = new Bean();
        $.map(beanData).to(bean);
        List<Bar> theBarList = bean.barMap.get("xyz");
        requireNotNull(theBarList);
        eq(1, theBarList.size());
        Bar theBar = theBarList.get(0);
        requireNotNull(theBar);
        List<Foo> theFooList = theBar.fooMap.get("abc");
        eq(2, theFooList.size());
        Foo theFoo1 = theFooList.get(0);
        eq(foo1.get("id"), theFoo1.id);
        Foo theFoo2 = theFooList.get(1);
        eq(foo2.get("id"), theFoo2.id);
    }

}
