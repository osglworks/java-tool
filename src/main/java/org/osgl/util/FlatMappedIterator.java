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
 * Date: 8/10/13
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
class FlatMappedIterator<T, R> extends StatefulIterator<R> {
    private final Iterator<? extends T> data;
    private final $.Function<? super T, ? extends Iterable<? extends R>> mapper;
    private Iterator<? extends R> curMapped = null;

    FlatMappedIterator(Iterator<? extends T> itr, $.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        E.NPE(itr, mapper);
        this.data = itr;
        this.mapper = mapper;
    }

    protected Iterator<? extends T> data() {return data;}

    protected $.Function<? super T, ? extends Iterable<? extends R>> mapper() {return mapper;}

    @Override
    protected $.Option<R> getCurrent() {
        while (null == curMapped || !curMapped.hasNext()) {
            if (!data.hasNext()) {
                return $.none();
            }
            curMapped = mapper.apply(data.next()).iterator();
        }
        return $.some((R)curMapped.next());
    }
}
