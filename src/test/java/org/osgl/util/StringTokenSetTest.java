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

import org.junit.Test;
import org.osgl.TestBase;

public class StringTokenSetTest extends TestBase {

    @Test
    public void testEmptySet() {
        StringTokenSet set = new StringTokenSet();
        yes(set.isEmpty());
        eq(S.EMPTY_ARRAY, set.toArray());
    }

    @Test
    public void testSingleElementSet() {
        StringTokenSet set = new StringTokenSet("abc");
        no(set.isEmpty());
        eq(1, set.size());
        yes(set.contains("abc"));
        yes(set.containsAll(C.set("abc")));
        no(set.containsAll(C.set("abc", "xyz")));
        eq(new String[]{"abc"}, set.toArray());
    }

    @Test
    public void testMultipleElementSet() {
        StringTokenSet set = new StringTokenSet("abc,xyz");
        no(set.isEmpty());
        eq(2, set.size());
        yes(set.contains("abc"));
        yes(set.containsAll(C.set("abc")));
        yes(set.containsAll(C.set("abc", "xyz")));
        eq(new String[]{"abc", "xyz"}, set.toArray());
    }

    @Test
    public void testAdd() {
        StringTokenSet set = new StringTokenSet();
        set.add("abc");
        eq(1, set.size());
        eq(new String[]{"abc"}, set.toArray());
        set.add("xyz");
        eq(2, set.size());
        eq(new String[]{"abc", "xyz"}, set.toArray());
    }

    @Test
    public void testRemove() {
        StringTokenSet set = new StringTokenSet("abc,xyz,mmm");
        no(set.remove(new Object()));
        yes(set.remove("xyz"));
        eq(2, set.size());
        eq(new String[]{"abc","mmm"}, set.toArray(new String[2]));
        yes(set.remove("abc"));
        eq(1, set.size());
        eq(new String[]{"mmm", null}, set.toArray(new String[2]));
        yes(set.remove("mmm"));
        yes(set.isEmpty());
        eq(S.EMPTY_ARRAY, set.toArray());
    }

    @Test
    public void testHashCodeAndEquality() {
        StringTokenSet set1 = new StringTokenSet("abc,xyz,mmm");
        StringTokenSet set2 = new StringTokenSet("xyz,abc,mmm");
        eq(set1, set2);
        eq(set1.hashCode(), set2.hashCode());
        StringTokenSet set3 = new StringTokenSet("xyz,abc,mmm3");
        ne(set1, set3);
    }

}
