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
import org.osgl.util.E;

/**
 * Return an new array contains elements specified by from and to in an array in reverse order
 */
public class ArrayReverse<T> implements ArrayAlgorithm, $.Func3<T[], Integer, Integer,  T[]> {
    @Override
    public T[] apply(T[] ts, Integer from, Integer to) throws NotAppliedException, $.Break {
        return reverse(ts, from, to);
    }

    public T[] reverse(T[] ts, int from, int to) {
        E.NPE(ts);
        Util.checkIndex(ts, from, to);
        if (to < from) {
            int t = to; to = from; from = t;
        }
        int len = to - from;
        T[] newTs = $.newArray(ts, len);
        if (0 == len) {
            return newTs;
        }
        int steps = len / 2, max = to - 1;
        for (int i = from; i < from + steps; ++i) {
            newTs[i] = ts[max - i];
            newTs[max - i] = ts[i];
        }
        return newTs;
    }
}
