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

public class DelegatingListTest extends ListTestBase {

    @Override
    protected C.List<Integer> prepareData(int... ia) {
        C.List<Integer> l = prepareEmptyData();
        l.append(C.list(ia));
        return l;
    }

    @Override
    protected C.List<Integer> prepareEmptyData() {
        return C.newList();
    }

    @Override
    protected <T> C.List<T> prepareTypedData(T... ta) {
        C.List<T> l = C.newSizedList(ta.length);
        for (T t : ta) {
            l.add(t);
        }
        return l;
    }
}
