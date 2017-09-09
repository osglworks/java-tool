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

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedSeq<A, B> extends SequenceBase<$.T2<A, B>> {

    private Iterable<A> a;
    private Iterable<B> b;

    private $.Option<A> defA = $.none();
    private $.Option<B> defB = $.none();


    ZippedSeq(Iterable<A> a, Iterable<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedSeq(Iterable<A> a, Iterable<B> b, A defA, B defB) {
        this(a, b);
        this.defA = $.some(defA);
        this.defB = $.some(defB);
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (a instanceof Collection && b instanceof Collection) {
            int szA = ((Collection) a).size();
            int szB = ((Collection) b).size();
            if (defA.isDefined()) {
                return Math.max(szA, szB);
            } else {
                return Math.min(szA, szB);
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<$.T2<A, B>> iterator() {
        final Iterator<A> ia = a.iterator();
        final Iterator<B> ib = b.iterator();
        if (defA.isDefined()) {
            return new ZippedIterator<A, B>(ia, ib, defA.get(), defB.get());
        } else {
            return new ZippedIterator<A, B>(ia, ib);
        }
    }
}
