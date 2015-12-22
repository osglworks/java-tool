package org.osgl.util;

import org.junit.Test;
import org.osgl._;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SequenceTestBase extends TraversableTestBase {
    @Override
    protected C.Sequence<Integer> prepareData() {
        return (C.Sequence<Integer>)super.prepareData();
    }

    @Override
    protected abstract C.Sequence<Integer> prepareData(int... ia);

    @Override
    protected abstract C.Sequence<Integer> prepareEmptyData();

    protected abstract <T> C.Sequence<T> prepareTypedData(T... ta);

    protected C.Sequence<Integer> data() {
        return (C.Sequence<Integer>) data;
    }

    @Test
    public void testFirst() {
        data = prepareData(1, 2, 3);
        eq(1, data().first());
    }

    @Test
    public void testHead() {
        data = prepareData(1, 2, 3);
        eq(1, data().head());
    }

    @Test
    public void testHeadN() {
        data = prepareData(1, 2, 3, 4, 5);
        eq(seqOf(1, 2, 3), data().head(3));
        eq(Nil.seq(), data().head(0));
        eq(data, data().head(5));
        eq(data, data().head(6));
        if (data.is(C.Feature.LIMITED)) {
            eq(seqOf(4, 5), data().head(-2));
        } else {
            try {
                eq(seqOf(4, 5), data().head(-2));
                fail("expected: UnsupportedOperationException");
            } catch (UnsupportedOperationException e) {
                // ignore
            }
        }
    }

    @Test
    public void testTail() {
        data = prepareData(1, 2, 3);
        eq(seqOf(2, 3), data().tail());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyTail() {
        data = prepareEmptyData();
        data().tail();
    }

    @Test
    public void testTake() {
        data = prepareData(1, 2, 3, 4, 5);
        eq(seqOf(1, 2, 3), data().take(3));
        eq(seqOf(1, 2), data().take(2));
        eq(Nil.seq(), data().take(0));
        eq(data, data().take(5));
        eq(data, data().take(6));
        if (data.is(C.Feature.LIMITED)) {
            eq(seqOf(4, 5), data().head(-2));
        } else {
            try {
                eq(seqOf(4, 5), data().head(-2));
                fail("expected: UnsupportedOperationException");
            } catch (UnsupportedOperationException e) {
                // ignore
            }
        }
    }


    @Test
    public void testTakeWhile() {
        data = prepareData(1, 2, 3, 4, 5);
        eq(seqOf(1, 2, 3), data().takeWhile(_.F.lessThan(4)));
        eq(Nil.seq(), data().takeWhile(_.F.lessThan(0)));
        eq(data, data().takeWhile(_.F.lessThan(6)));
    }

    @Test
    public void testDrop() {
        data = prepareData(1, 2, 3, 4, 5);
        eq(seqOf(4, 5), data().drop(3));
        eq(Nil.seq(), data().drop(5));
        eq(Nil.seq(), data().drop(6));
        eq(data, data().drop(0));
        if (data.is(C.Feature.LIMITED)) {
            eq(seqOf(1, 2, 3, 4), data().drop(-1));
        } else {
            try {
                data().drop(-1);
                fail("unlimited sequence cannot drop with negative number");
            } catch (IndexOutOfBoundsException e) {
                // success
            }
        }
    }

    @Test
    public void testDropWhile() {
        data = prepareData(1, 2, 3, 4, 5);
        eq(seqOf(4, 5), data().dropWhile(_.F.lt(4)));
    }

    @Test
    public void testAppend() {
        data = prepareData(1, 2);
        eq(seqOf(1, 2, 3, 4), data().append(seqOf(3, 4)));
        if (isMutable()) {
            eq(seqOf(1, 2, 3, 4, 5), data().append(5));
        } else {
            data = prepareData(1, 2);
            eq(seqOf(1, 2, 3), data().append(3));
        }
    }

    @Test
    public void testPrepend() {
        data = prepareData(1, 2);
        eq(seqOf(3, 4, 1, 2), data().prepend(seqOf(3, 4)));
        data = prepareData(1, 2);
        eq(seqOf(3, 1, 2), data().prepend(3));
    }

    @Test
    public void testReduceLeft() {
        data = prepareData(5, 4, 3, 2, 1);
        int poor = data().reduceLeft(N.F.subtract(Integer.class)).get();
        eq(5 - 4 - 3 - 2 - 1, poor);
        poor = data().reduceLeft(100, N.F.subtract(Integer.class));
        eq(100 - 5 - 4 - 3 - 2 - 1, poor);
    }

    @Test
    public void testFindFirst() {
        C.Sequence<_.T2<Integer, Integer>> data = prepareTypedData(_.T2(1, 5), _.T2(2, 6), _.T2(2, 8), _.T2(3, 4));
        _.Option<_.T2<Integer, Integer>> found = data.findFirst(new _.Predicate<_.T2<Integer, Integer>>() {
            @Override
            public boolean test(_.T2<Integer, Integer> x) {
                return x._1 == 2;
            }
        });
        yes(found.get()._2 == 6);
    }

    @Test
    public void testAcceptLeft() {
        data = prepareData(1, 2, 3, 4, 5);
        _.Var<Integer> var = _.var(0);
//        data().acceptLeft(new _.Visitor<Integer>() {
//            @Override
//            public void visit(Integer integer) throws _.Break {
//                bag[0] = bag[0] + integer;
//            }
//        });
        data().acceptLeft(var.f.updater(N.F.addTwo(Integer.class)));
        eq(5 * 6 / 2, var.get());
    }

    @Test
    public void testZip() {
        data = prepareData(1, 3);
        eq(seqOf(_.T2(1, 2), _.T2(3, 4)), data().zip(seqOf(2, 4, 6)));
        eq(seqOf(_.T2(1, 2)), data().zip(seqOf(2)));
    }

    @Test
    public void testZipAll() {
        data = prepareData(1, 3);
        eq(seqOf(_.T2(1, 2), _.T2(3, 4), _.T2(-1, 6)), data().zipAll(seqOf(2, 4, 6), -1, -2));
        eq(seqOf(_.T2(1, 2), _.T2(3, -2)), data().zipAll(seqOf(2), -1, -2));
    }

    @Test
    public void testCount() {
        eq(1, prepareData(1, 2, 3).count(2));
        eq(2, prepareData(1, 2, 3, 2).count(2));
        eq(2, prepareData(1, 2, 2, 3).count(2));
    }

}
