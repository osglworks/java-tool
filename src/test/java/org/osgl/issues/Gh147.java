package org.osgl.issues;

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

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.AdaptiveMap;
import org.osgl.util.SimpleAdaptiveMap;

import java.util.HashMap;
import java.util.Map;

public class Gh147 extends TestBase {
    @Test
    public void testToMap() {
        Map<String, Object> src = new HashMap<>();
        src.put("a", 1);
        Map tgt = $.map(src).map("a").to("b").to(Map.class);
        eq(1, tgt.get("b"));
    }

    @Test
    public void testToAdaptiveMap() {
        Map<String, Object> src = new HashMap<>();
        src.put("a", 1);
        AdaptiveMap tgt = $.map(src).map("a").to("b").to(SimpleAdaptiveMap.class);
        eq(1, tgt.getValue("b"));
    }

}
