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

import org.junit.Test;
import org.osgl.$;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/11/13
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ReversibleSeqTestBase extends SequenceTestBase {
    @Override
    protected C.ReversibleSequence<Integer> prepareData() {
        return (C.ReversibleSequence<Integer>) super.prepareData();
    }

    @Override
    protected abstract C.ReversibleSequence<Integer> prepareData(int... ia);

    @Override
    protected abstract C.ReversibleSequence<Foo> preparePojoData(Foo... fooArray);

    @Override
    protected abstract C.ReversibleSequence<Integer> prepareEmptyData();

    protected abstract <T> C.ReversibleSequence<T> prepareTypedData(T... ta);


    protected C.ReversibleSequence<Integer> data() {
        return (C.ReversibleSequence<Integer>)data;
    }

    @Test
    public void testLast() {
        eq(5, data().last());
    }

    @Test
    public void testTailN() {
        eq(data, data().tail(5));
        eq(data, data().tail(6));
        eq(seqOf(3, 4, 5), data().tail(3));
        eq(seqOf(1, 2), data().tail(-2));
    }

    @Test
    public void testReverse() {
        eq(seqOf(5, 4, 3, 2, 1), data().reverse());
    }

    @Test
    public void testReverseIterator() {
        Iterator<Integer> itr = data().reverseIterator();
        StringBuilder sb = new StringBuilder();
        for (;itr.hasNext();) {
            sb.append(itr.next());
        }
        eq("54321", sb.toString());
    }

    @Test
    public void testReduceRight() {
        int poor = data().reduceRight(N.F.subtract(Integer.class)).get();
        eq(5 - 4 - 3 - 2 - 1, poor);
        poor = data().reduceRight(100, N.F.subtract(Integer.class));
        eq(100 - 5 - 4 - 3 - 2 - 1, poor);
    }

    @Test
    public void testAcceptRight() {
        data = prepareData(1, 2, 3, 4, 5);
        final $.Var<Integer> bag = $.var(0);
        data().acceptRight($.visitor(bag.f.updater(N.F.addTwo(Integer.class))));
//        data().acceptRight(new _.Visitor<Integer>() {
//            @Override
//            public void visit(Integer integer) throws _.Break {
//                bag.set(bag.get() + integer);
//            }
//        });
        eq(5 * 6 / 2, bag.get());
    }

    @Test
    public void testFindLast() {
        C.ReversibleSequence<$.T2<Integer, Integer>> data = prepareTypedData($.T2(1, 5), $.T2(2, 6), $.T2(2, 8), $.T2(3, 4));
        $.Option<$.T2<Integer, Integer>> found = data.findLast(new $.Predicate<$.T2<Integer, Integer>>() {
            @Override
            public boolean test($.T2<Integer, Integer> x) {
                return x._1 == 2;
            }
        });
        yes(found.get()._2 == 8);
    }
}
