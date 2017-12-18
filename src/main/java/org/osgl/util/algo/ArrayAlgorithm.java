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
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ArrayAlgorithm extends Algorithm {
    static enum Util {
        ;
        public static void checkIndex(Object[] array, int index) {
            if (index < 0 || index >= array.length) {
                throw new IndexOutOfBoundsException();
            }
        }

        public static void checkIndex(Object[] array, int from, int to) {
            if (array.length == 0) return;
            if (from < 0 || from >= array.length || to < 0 || to > array.length) {
                throw new IndexOutOfBoundsException();
            }
        }
    }
}
