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

import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedList<A, B> extends ListBase<$.T2<A, B>> {
    private List<A> a;
    private List<B> b;
    private $.Option<A> defA = $.none();
    private $.Option<B> defB = $.none();

    ZippedList(List<A> a, List<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedList(List<A> a, List<B> b, A defA, B defB) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
        this.defA = $.some(defA);
        this.defB = $.some(defB);
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.LAZY, C.Feature.READONLY, C.Feature.LIMITED);
    }

    @Override
    public int size() {
        if (defA.isDefined()) {
            return Math.max(a.size(), b.size());
        } else {
            return Math.min(a.size(), b.size());
        }
    }

    @Override
    public $.T2<A, B> get(int index) {
        return $.T2(a.get(index), b.get(index));
    }

    @Override
    public ListIterator<$.T2<A, B>> listIterator(int index) {
        if (defA.isDefined()) {
            return new ZippedListIterator<A, B>(a.listIterator(), b.listIterator(), defA.get(), defB.get());
        }
        return new ZippedListIterator<A, B>(a.listIterator(), b.listIterator());
    }


}
