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
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
class FilteredIterator<T> extends StatefulIterator<T> {

    static enum Type {
        ALL,
        WHILE,
        UNTIL;
        <T> Iterator<T> filter(Iterator<T> raw, $.Function<? super T, Boolean> predicate) {
            return new FilteredIterator<T>(raw, predicate, this);
        }
    }

    private final Iterator<? extends T> itr_;
    private final $.Predicate<T> filter_;
    private final Type type_;
    private boolean start_;

    FilteredIterator(Iterator<? extends T> iterator, $.Function<? super T, Boolean> filter) {
        this(iterator, filter, Type.ALL);
    }

    FilteredIterator(Iterator<? extends T> iterator, $.Function<? super T, Boolean> filter, Type type) {
        E.NPE(iterator, filter);
        itr_ = iterator;
        filter_ = $.predicate(filter);
        type_ = type;
    }

    protected Iterator<? extends T> data() {return itr_;}

    @Override
    protected $.Option<T> getCurrent() {
        boolean ok;
        while (itr_.hasNext()) {
            T t = itr_.next();
            switch (type_) {
            case ALL:
                ok = filter_.test(t);
                if (ok) {
                    return $.some(t);
                } else {
                    continue;
                }
            case WHILE:
                ok = filter_.test(t);
                if (ok) {
                    return $.some(t);
                } else {
                    return $.none();
                }
            case UNTIL:
                if (start_) {
                    return $.some(t);
                }
                ok = filter_.test(t);
                if (ok) {
                    start_ = true;
                    return $.some(t);
                } else {
                    continue;
                }
            }
        }
        return $.none();
    }

}
