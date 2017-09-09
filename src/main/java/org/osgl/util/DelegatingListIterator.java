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
 * Date: 6/10/13
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
class DelegatingListIterator<T>
extends DelegatingIterator<T> implements ListIterator<T> {

    DelegatingListIterator(ListIterator<T> itr, boolean readOnly) {
        super(itr, readOnly);
    }

    @Override
    protected ListIterator<T> itr() {
        return (ListIterator)super.itr();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPrevious() {
        return itr().hasPrevious();
    }

    @Override
    public T previous() {
        return itr().previous();
    }

    @Override
    public int nextIndex() {
        return itr().nextIndex();
    }

    @Override
    public int previousIndex() {
        return itr().previousIndex();
    }

    @Override
    public void set(T t) {
        mutableOperation();
        itr().set(t);
    }

    @Override
    public void add(T t) {
        mutableOperation();
        itr().add(t);
    }

}
