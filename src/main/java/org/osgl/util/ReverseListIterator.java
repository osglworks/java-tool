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

import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 6/11/13
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
class ReverseListIterator<T> implements ListIterator<T> {
    private ListIterator<T> itr;
    ReverseListIterator(ListIterator<T> itr) {
        this.itr = itr;
    }

    @Override
    public boolean hasNext() {
        return itr.hasPrevious();
    }

    @Override
    public T next() {
        return itr.previous();
    }

    @Override
    public boolean hasPrevious() {
        return itr.hasNext();
    }

    @Override
    public T previous() {
        return itr.next();
    }

    @Override
    public int nextIndex() {
        return itr.previousIndex();
    }

    @Override
    public int previousIndex() {
        return itr.nextIndex();
    }

    @Override
    public void remove() {
        itr.remove();
    }

    @Override
    public void set(T t) {
        itr.set(t);
    }

    @Override
    public void add(T t) {
        //TODO fix me
        itr.add(t);
    }
}
