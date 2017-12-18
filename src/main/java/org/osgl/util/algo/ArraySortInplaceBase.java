package org.osgl.util.algo;

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
import org.osgl.exception.NotAppliedException;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ArraySortInplaceBase<T> implements ArraySort<T> {

    @Override
    public final T[] apply(T[] ts, Integer from, Integer to, Comparator<T> comp) throws NotAppliedException, $.Break {
        return sort(ts, from, to, comp);
    }

    protected abstract T[] sort0(T[] ts, int from, int to, Comparator<T> comp);

    public final T[] sort(T[] ts, int from, int to, Comparator<T> comp) {
        Util.checkIndex(ts, from, to);
        if (null == comp) {
            comp = $.F.NATURAL_ORDER;
        }
        if (to == from) {
            return ts;
        }
        if (to < from) {
            return sort0(ts, to, from, comp);
        } else {
            return sort0(ts, from, to, comp);
        }
    }

}
