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

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 12/11/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayMergeSortInplace<T> extends ArraySortInplaceBase<T> implements ArraySort<T> {

    protected T[] sort0(T[] ts, int from, int to, Comparator<T> comp) {
        if (to - from < 8) {
            ArrayInsertionSortInplace<T> is = Algorithms.arrayInsertionSortInplace();
            return is.sort0(ts, from, to, comp);
        }
        Object[] merged = ArrayMergeSort.mergeSort(ts, from, to, comp);
        System.arraycopy(merged, 0, ts, from, to - from);
        return ts;
    }

    public static void main(String[] args) {
        Integer[] ia = {5, 4, 3, 1, 3, 2, 1, 2};
        ArrayMergeSortInplace<Integer> s = new ArrayMergeSortInplace<Integer>();
        Integer[] a = s.apply(ia, 1, 7, $.F.REVERSE_ORDER);
        System.out.println(Arrays.toString(a));
    }

}
