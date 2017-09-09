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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/10/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompositeList<T> extends ListBase<T> implements C.List<T>, Serializable {

    private final C.List<T> left;

    private final C.List<T> right;

    CompositeList(C.List<T> left, C.List<T> right) {
        this.left = left;
        this.right = right;
    }

    static <T1> CompositeList<T1> of(C.List<T1> left, C.List<T1> right) {
        return new CompositeList<T1>(left, right);
    }

    protected UnsupportedOperationException noMutableOperation() {
        throw new UnsupportedOperationException("mutable operation not allowed in this read only structure");
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        EnumSet<C.Feature> fs = EnumSet.of(C.Feature.READONLY, C.Feature.ORDERED);
        C.Feature[] fa = {
            C.Feature.IMMUTABLE,
            C.Feature.RANDOM_ACCESS,
            C.Feature.LAZY,
            C.Feature.PARALLEL
        };
        C.List<T> l = left;
        C.List<T> r = right;
        for (C.Feature f : fa) {
            if (l.is(f) && r.is(f)) {
                fs.add(f);
            }
        }
        return fs;
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        ListIterator<T> l, r;
        int sz = left.size();
        if (index < sz) {
            l = left.listIterator(index);
            r = right.listIterator();
        } else {
            l = left.listIterator();
            r = right.listIterator(index - sz);
        }

        return new CompositeListIterator<T>(l, r);
    }

    @Override
    public T get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        C.List<T> l = left, r = right;
        int ls = l.size(), rs = r.size();
        if (index >= ls + rs) {
            throw new IllegalArgumentException();
        }
        if (index < ls) {
            return l.get(index);
        } else {
            return r.get(index - ls);
        }
    }

    @Override
    public int size() {
        return left.size() + right.size();
    }
}
