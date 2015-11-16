package org.osgl.util;

import org.junit.Before;
import org.junit.Test;
import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TraversableTestBase extends UtilTestBase {


    protected C.Traversable<Integer> data;

    protected final boolean isMutable() {
        return !(data.is(C.Feature.IMMUTABLE) || data.is(C.Feature.READONLY));
    }

    protected final boolean isLazy() {
        return data.is(C.Feature.LAZY);
    }

    protected final boolean isImmutable() {
        return data.is(C.Feature.IMMUTABLE);
    }

    /**
     * Prepare a traversable with 1,2,3,4,5
     * @return
     */
    protected C.Traversable<Integer> prepareData() {
        return prepareData(1, 2, 3, 4, 5);
    }

    protected abstract C.Traversable<Integer> prepareData(int ... ia);

    protected abstract C.Traversable<Integer> prepareEmptyData();

    @Before
    public void setUp() {
        data = prepareData();
    }

    @Test
    public void testIterator() {
        Iterator<Integer> itr = data.iterator();
        StringBuilder sb = new StringBuilder();
        for (;itr.hasNext();) {
            sb.append(itr.next());
        }
        eq("12345", sb.toString());
    }

    @Test
    public void testEmpty() {
        data = prepareData(1);
        no(data.isEmpty());

        data = prepareEmptyData();
        yes(data.isEmpty());
    }

    @Test
    public void testSize() {
        data = prepareData(1);
        if (data.is(C.Feature.LIMITED)) {
            eq(1, data.size());
        } else {
            try {
                eq(1, data.size());
                assertTrue("expected: UnsupportedOperationException", false);
            } catch (UnsupportedOperationException e) {
                // success
            }
        }
    }

    @Test
    public void testMap() {
        data = prepareData(1);
        C.Traversable<String> newData = data.map($.F.asString());
        eq(seqOf("1"), newData);
    }

    @Test
    public void testFlatMap() {
        data = prepareData(1, 4);
        C.Traversable<Integer> newData = data.flatMap(new $.F1<Integer, C.Traversable<Integer>>(){
            @Override
            public C.Traversable<Integer> apply(Integer integer) throws NotAppliedException, $.Break {
                return C.range(0, integer);
            }
        });
        eq(seqOf(0, 0, 1, 2, 3), newData);
    }

    @Test
    public void testFilter() {
        data = prepareData(1, 2, 3, 4, 5, 6, 7);
        C.Traversable<Integer> newData = data.filter(N.F.IS_EVEN);
        eq(seqOf(2, 4, 6), newData);
    }

    @Test
    public void testReduce() {
        data = prepareData(1, 2, 3, 4, 5);
        int sum = data.reduce(N.F.aggregate(Integer.class)).get();
        eq(5 * 6 / 2, sum);

        sum = data.reduce(0, N.F.aggregate(Integer.class));
        eq(5 * 6 / 2, sum);
    }

    @Test
    public void testMatches() {
        data = prepareData(1, 2, 3);
        yes(data.allMatch($.F.greaterThan(0)));
        no(data.allMatch($.F.greaterThan(1)));

        yes(data.anyMatch($.F.greaterThan(1)));
        no(data.anyMatch($.F.lessThan(0)));

        yes(data.noneMatch($.F.lessThan(0)));
        no(data.noneMatch($.F.lessThan(2)));
    }

    @Test
    public void testAccept() {
        data = prepareData(1, 2, 3);
        final Integer[] bot = {1};
        data.accept(new $.Visitor<Integer>() {
            @Override
            public void visit(Integer integer) throws $.Break {
                bot[0] = bot[0] * integer;
            }
        });
        eq(1 * 2 * 3, bot[0]);
    }

}
