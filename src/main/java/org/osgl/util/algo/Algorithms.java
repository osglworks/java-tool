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

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 5/11/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Algorithms {
    ;
    public static final ArrayReverse ARRAY_REVERSE = new ArrayReverse();
    public static final <T> ArrayReverse<T> arrayReverse() {
        return (ArrayReverse<T>)ARRAY_REVERSE;
    }

    public static final ArrayReverseInplace ARRAY_REVERSE_INPLACE = new ArrayReverseInplace();
    public static final <T> ArrayReverseInplace<T> arrayReverseInplace() {
        return (ArrayReverseInplace<T>)ARRAY_REVERSE_INPLACE;
    }

    public static final ArrayInsertionSort ARRAY_INSERTION_SORT = new ArrayInsertionSort();
    public static final <T> ArrayInsertionSort<T> arrayInsertionSort() {
        return ARRAY_INSERTION_SORT;
    }


    public static final ArrayInsertionSortInplace ARRAY_INSERTION_SORT_INPLACE = new ArrayInsertionSortInplace();
    public static final <T> ArrayInsertionSortInplace<T> arrayInsertionSortInplace() {
        return ARRAY_INSERTION_SORT_INPLACE;
    }
}
