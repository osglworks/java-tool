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

import java.util.Comparator;

class OddEvenElement {
    private int v;
    OddEvenElement(int v) {
        this.v = v;
    }

    @Override
    public int hashCode() {
        return v;
    }

    public boolean isEven() {
        return v % 2 == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof OddEvenElement) {
            OddEvenElement that = (OddEvenElement)obj;
            return that.v == this.v;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(v);
    }

    static class Comp implements Comparator<OddEvenElement> {
        @Override
        public int compare(OddEvenElement o1, OddEvenElement o2) {
            if (o1.isEven()) {
                return o2.isEven() ? 0 : -1;
            } else {
                return o2.isEven() ? 1 : 0;
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }
    }

    static enum F {
        ;
        static final $.Transformer<Integer, OddEvenElement> OF_INT = new $.Transformer<Integer, OddEvenElement>() {
            @Override
            public OddEvenElement transform(Integer integer) {
                return new OddEvenElement(integer);
            }
        };
    }
}
