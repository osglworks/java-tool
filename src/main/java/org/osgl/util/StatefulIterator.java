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

import org.osgl.$;

import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/10/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class StatefulIterator<T> extends ReadOnlyIterator<T> {

    private $.Option<T> current = $.none();

    /**
     * If there are still elements, then return the an option describing the next element,
     * otherwise return {@link Lang.Option#NONE}
     *
     * @return either next element or none if no element in the iterator
     */
    protected abstract $.Option<T> getCurrent();

    public boolean hasNext() {
        if (current.isDefined()) {
            return true;
        }
        current = getCurrent();
        return current.isDefined();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T ret = current.get();
        current = $.none();
        return ret;
    }

}
