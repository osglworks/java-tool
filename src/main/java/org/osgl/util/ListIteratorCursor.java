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
import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 26/10/13
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
class ListIteratorCursor<T> extends CursorBase<T> implements C.List.Cursor<T> {
    private final ListIterator<T> itr_;
    private boolean parked = false;
    ListIteratorCursor(ListIterator<T> itr) {
        E.NPE(itr);
        itr_ = itr;
    }

    @Override
    public T next() {
        return itr_.next();
    }

    @Override
    public T previous() {
        return itr_.previous();
    }

    @Override
    public void update(T t) throws IndexOutOfBoundsException, NullPointerException {
        itr_.previous();
        itr_.set(t);
    }

    @Override
    public void remove() throws NoSuchElementException {
        itr_.remove();
    }

    @Override
    public void add(T t) throws IndexOutOfBoundsException {
        itr_.add(t);
    }

    @Override
    public int index() {
        return itr_.previousIndex();
    }

    @Override
    public boolean hasNext() {
        return itr_.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return itr_.hasPrevious();
    }

    @Override
    public C.List.Cursor<T> parkLeft() {
        while (itr_.hasPrevious()) {
            itr_.previous();
        }
        parked = true;
        return this;
    }

    @Override
    public C.List.Cursor<T> parkRight() {
        while (itr_.hasNext()) {
            itr_.nextIndex();
        }
        parked = true;
        return this;
    }
}
