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

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 22/10/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexFilteredIterator<T> extends StatefulIterator<T> {
    private final Iterator<? extends T> itr_;
    private final $.Predicate<Integer> filter_;
    private int cursor;

    public IndexFilteredIterator(Iterator<? extends T> iterator, $.Function<Integer, Boolean> filter) {
        E.NPE(iterator, filter);
        itr_ = iterator;
        filter_ = $.predicate(filter);
    }

    private boolean rawHasNext() {
        return itr_.hasNext();
    }

    private T rawNext() {
        cursor++;
        return itr_.next();
    }

    @Override
    protected $.Option<T> getCurrent() {
        while (rawHasNext()) {
            int curCursor = cursor;
            T t = rawNext();
            if (filter_.test(curCursor)) {
                return $.some(t);
            }
        }
        return $.none();
    }
}
