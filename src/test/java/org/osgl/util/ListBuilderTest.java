package org.osgl.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgl.$;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 8:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListBuilderTest extends UtilTestBase {

    protected ListBuilder<Integer> lb;

    @Before
    public void setUp() {
        lb = new ListBuilder<Integer>();
    }

    @Test
    public void testGeneral() {
        lb.add(1);
        lb.add(2);
        eq(seqOf(1, 2), lb);
        C.List<Integer> l = lb.toList();
        eq(seqOf(1, 2), l);
        assertTrue(l instanceof ImmutableList);
        eq(l.size(), 2);
    }

    @Test
    public void testAppend() {
        lb.append(1);
        eq(seqOf(1), lb);

        lb.clear();
        lb.append(1, 2);
        eq(seqOf(1, 2), lb);

        lb.clear();
        lb.append(1, 2, 3);
        eq(seqOf(1, 2, 3), lb);

        lb.clear();
        lb.append(1, 2, 3, 4);
        eq(seqOf(1, 2, 3, 4), lb);

        lb.clear();
        lb.append(1, 2, 3, 4, 5);
        eq(seqOf(1, 2, 3, 4, 5), lb);

        lb.clear();
        lb.append(1, 2, 3, 4, 5, 6);
        eq(seqOf(1, 2, 3, 4, 5, 6), lb);

        lb.clear();
        lb.append(1, 2, 3, 4, 5);
        eq(seqOf(1, 2, 3, 4, 5), lb);
        lb.clear();
        Integer[] ia = new Integer[100];
        for (int i = 0; i < 100; ++i) {
            ia[i] = i + 6;
        }
        lb.append(1, 2, 3, 4, 5, ia);
        C.List<Integer> l = lb.toList();
        eq(105, l.size());
        for (int i = 0; i < 105; ++i) {
            eq(i + 1, l.get(i));
        }
    }

    @Test
    public void testAppendCollections() {
        Collection<Integer> c1 = Arrays.asList(1, 2);
        Collection<Integer> c2 = Arrays.asList(3, 4);

        lb.append(c1, c2);
        eq(seqOf(1, 2, 3, 4), lb);

        lb.clear();
        Collection<Integer> c3 = Arrays.asList(5, 6);
        Collection<Integer> c4 = Arrays.asList(7, 8);
        eq(C.range(1, 9), lb.append(c1, c2, c3, c4));
    }

    @Test
    public void testAppendArrays() {
        Integer[] a1 = {1, 2};
        Integer[] a2 = {3, 4};

        eq(C.range(1, 5), lb.append(a1, a2));

        Integer[] a3 = {5, 6};
        Integer[] a4 = {7, 8};
        lb.clear();
        eq(C.range(1, 9), lb.append(a1, a2, a3, a4));
    }

    @Test
    public void testExpandCapacity() {
        for (int i = 0; i < 100 * 100; ++i) {
            lb.append(i);
        }
        for (int i = 0; i < 100 * 100; ++i) {
            eq(i, lb.get(i));
        }
    }

    @Test
    @Ignore
    public void benchmarkBulkAppend() {
        final int max = 100, times = 200;

        List<Integer> il = new ArrayList<Integer>();
        for (int i = 0; i < max; ++i) {
            il.add(i);
        }

        long ts;
        int fails = 0;
        for (int cnt = 0; cnt < times; ++cnt) {
//            lb.clear();
//            ts = _.ns();
//            for (int i = 0; i < max; ++i) {
//                lb.append(i);
//            }
//            long t2 = _.ns() - ts;

//            List<Integer> linkedList = new LinkedList<Integer>();
//            ts = _.ns();
//            linkedList.addAll(il);
//            long t4 = _.ns() - ts;
//

            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            ts = $.ns();
            arrayList.addAll(il);
            long t3 = $.ns() - ts;

            ListBuilder<Integer> lb = new ListBuilder<Integer>();
            ts = $.ns();
            lb.addAll(il);
            long t1 = $.ns() - ts;

            //if (!(t3 > t1)) fails++;
            boolean fail = ((t1 > t3) && (t1 - t3) * 100 / t1 > 5);
            //_.echo("%s > %s vs %s [%s]", cnt, t1, t3, (fail ? "fail" : "ok"));
            if (fail) fails++;
        }
        yes((fails * 3) / (times * 2)  < 1, "fails: %s", fails);
    }

    @Test
    @Ignore
    public void benchmarkAppendCollections() {
        final int max = 28, times = 100;
        Integer[] a = new Integer[max];
        for (int i = 0; i < max; ++i) {
            a[i] = i;
        }

        Collection<Integer> c1 = Arrays.asList(a);
        Collection<Integer> c2 = Arrays.asList(a);
        Collection<Integer> c3 = Arrays.asList(a);

        long ts;
        int fails = 0;
        for (int cnt = 0; cnt < times; ++cnt) {
            ArrayList<Integer> al = new ArrayList<Integer>();
            ts = $.ns();
            al.addAll(c1);
            al.addAll(c2);
            al.addAll(c3);
            long t3 = $.ns() - ts;

            ListBuilder<Integer> lb = new ListBuilder<Integer>();
            ts = $.ns();
            lb.append(c1, c2, c3);
            long t1 = $.ns() - ts;

            boolean fail = ((t1 > t3));
            //_.echo("%s > %s vs %s [%s]", cnt, t1, t3, (fail ? "fail" : "ok"));
            if (fail) fails++;
        }
        yes((fails * 3) / (times * 2)  < 1, "fails: %s", fails);
        System.out.println("fails: " + fails);
    }

    @Test(expected = IllegalStateException.class)
    public void testConsumed1() {
        lb.toList();
        lb.append(1);
    }

    @Test
    public void testInsert() {
        lb.append(1, 2, 4);
        lb.add(2, 3);
        eq(lb.toList(), C.list(1, 2, 3, 4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInsert2() {
        lb.append(1, 2, 4);
        lb.add(4, 5);
    }

    @Test
    public void testToSet() {
        lb = new ListBuilder<Integer>(3);
        lb.append(3);
        lb.append(4);
        Set<Integer> set = lb.toSet();
        eq(2, set.size());
    }

}
