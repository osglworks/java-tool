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

import java.lang.reflect.Array;
import java.util.Iterator;

public class ArrayObjectIterator implements Iterator {

    private Object o;
    private int len;
    private int cursor;

    public ArrayObjectIterator(Object o) {
        E.NPE(o);
        E.illegalArgumentIfNot(o.getClass().isArray());
        this.o = o;
        this.len = Array.getLength(o);
    }

    @Override
    public boolean hasNext() {
        return cursor < len;
    }

    @Override
    public Object next() {
        return Array.get(o, cursor++);
    }

    @Override
    public void remove() {
        E.unsupport();
    }

}
