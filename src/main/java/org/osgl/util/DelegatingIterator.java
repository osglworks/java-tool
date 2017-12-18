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

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 6/10/13
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class DelegatingIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> itr_;
    protected final boolean readOnly;

    DelegatingIterator(Iterator<? extends T> itr, boolean readOnly) {
        this.itr_ = itr;
        this.readOnly = readOnly;
    }

    protected Iterator<? extends T> itr() {
        return itr_;
    }

    protected final void mutableOperation() {
        if (readOnly) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasNext() {
        return itr_.hasNext();
    }

    @Override
    public T next() {
        return itr_.next();
    }

    @Override
    public void remove() {
        mutableOperation();
        itr_.remove();
    }

    public static <T> Iterator<T> of(Iterator<? extends T> iterator, boolean readOnly) {
        return new DelegatingIterator<T>(iterator, readOnly);
    }
}
