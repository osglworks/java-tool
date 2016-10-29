package org.osgl.util;

import org.junit.Test;
import org.osgl.$;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ListTestBase extends ReversibleSeqTestBase {

    protected C.List<Integer> listOf(int... ia) {
        return C.list(ia);
    }

    @Override
    protected C.List<Integer> prepareData() {
        return (C.List<Integer>) super.prepareData();
    }

    @Override
    protected abstract C.List<Integer> prepareData(int... ia);

    @Override
    protected abstract C.List<Integer> prepareEmptyData();

    protected abstract <T> C.List<T> prepareTypedData(T... ta);

    protected final <T> ArrayList<T> arrayList(T... ta) {
        ArrayList<T> l = new ArrayList<T>();
        l.addAll(Arrays.asList(ta));
        return l;
    }

    protected final <T> LinkedList<T> linkedList(T... ta) {
        LinkedList<T> l = new LinkedList<T>();
        l.addAll(Arrays.asList(ta));
        return l;
    }


    protected C.List<Integer> data() {
        return (C.List<Integer>)data;
    }

    protected C.List<Integer> l() {
        return data();
    }

    @Test
    public void testSubList() {
        eq(seqOf(1, 2), l().subList(0, 2));
        eq(seqOf(2, 3, 4), l().subList(1, 4));

        try {
            l().subList(1, 6);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            //pass
        }

        try {
            l().subList(-1, 2);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testAddAll() {
        if (!isMutable()) {
            return;
        }
        boolean b = l().addAll(Arrays.asList(1, 2, 3));
        yes(b);
        eq(seqOf(1, 2, 3, 4, 5, 1, 2, 3), data());

        data = prepareEmptyData();
        b = l().addAll(Nil.LIST);
        no(b);
        yes(data.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testROAddAll() {
        if (isMutable()) {
            throw new UnsupportedOperationException(); // to make junit happy
        }
        l().addAll(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testInsertCharMutable() {
        if (!isMutable()) {
            return;
        }
        l().insert(0, 0);
        eq(seqOf(0, 1, 2, 3, 4, 5), data);
        setUp();
        l().insert(1, 0);
        eq(seqOf(1, 0, 2, 3, 4, 5), data);
        setUp();
        l().insert(5, 0);
        eq(seqOf(1, 2, 3, 4, 5, 0), data);
        setUp();
        l().insert(-1, 0);
        eq(seqOf(1, 2, 3, 4, 0, 5), data);

        setUp();
        try {
            l().insert(6, 0);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testInsertCharImmutable() {
        if (!isImmutable()) {
            return;
        }
        C.List<Integer> l = l().insert(0, 0);
        eq(seqOf(0, 1, 2, 3, 4, 5), l);
        l = l().insert(1, 0);
        eq(seqOf(1, 0, 2, 3, 4, 5), l);
        l = l().insert(5, 0);
        eq(seqOf(1, 2, 3, 4, 5, 0), l);
        l = l().insert(-1, 0);
        eq(seqOf(1, 2, 3, 4, 0, 5), l);
        yes(l.is(C.Feature.IMMUTABLE));
        try {
            l().insert(6, 0);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testInsertListMutable() {
        if (!isMutable()) {
            return;
        }
        l().insert(0, C.listOf(0, 5));
        eq(seqOf(0, 5, 1, 2, 3, 4, 5), data);
        setUp();
        l().insert(1, C.listOf(0, 5));
        eq(seqOf(1, 0, 5, 2, 3, 4, 5), data);
        setUp();
        l().insert(5, C.listOf(0, 7));
        eq(seqOf(1, 2, 3, 4, 5, 0, 7), data);
        setUp();
        l().insert(-1, C.listOf(0, 7));
        eq(seqOf(1, 2, 3, 4, 0, 7, 5), data);

        setUp();
        try {
            l().insert(6, C.listOf(0, 3));
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testInsertListImmutable() {
        if (!isImmutable()) {
            return;
        }
        C.List<Integer> l = l().insert(0, C.listOf(0, 5));
        eq(seqOf(0, 5, 1, 2, 3, 4, 5), l);
        l = l().insert(1, C.listOf(0, 5));
        eq(seqOf(1, 0, 5, 2, 3, 4, 5), l);
        l = l().insert(5, C.listOf(0, 7));
        eq(seqOf(1, 2, 3, 4, 5, 0, 7), l);
        l = l().insert(-1, C.listOf(0, 7));
        eq(seqOf(1, 2, 3, 4, 0, 7, 5), l);
        yes(l.is(C.Feature.IMMUTABLE));
        try {
            l().insert(6, C.listOf(0, 3));
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testInsertArrayMutable() {
        if (!isMutable()) {
            return;
        }
        l().insert(0, 0, 5);
        eq(seqOf(0, 5, 1, 2, 3, 4, 5), data);
        setUp();
        l().insert(1, 0, 5);
        eq(seqOf(1, 0, 5, 2, 3, 4, 5), data);
        setUp();
        l().insert(5, 0, 7);
        eq(seqOf(1, 2, 3, 4, 5, 0, 7), data);
        setUp();
        l().insert(-1, 0, 7);
        eq(seqOf(1, 2, 3, 4, 0, 7, 5), data);

        setUp();
        try {
            l().insert(6, 0, 3);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }


    @Test
    public void testInsertArrayImmutable() {
        if (!isImmutable()) {
            return;
        }
        C.List<Integer> l = l().insert(0, 0, 5);
        eq(seqOf(0, 5, 1, 2, 3, 4, 5), l);
        l = l().insert(1, 0, 5);
        eq(seqOf(1, 0, 5, 2, 3, 4, 5), l);
        l = l().insert(5, 0, 7);
        eq(seqOf(1, 2, 3, 4, 5, 0, 7), l);
        l = l().insert(-1, 0, 7);
        eq(seqOf(1, 2, 3, 4, 0, 7, 5), l);
        yes(l.is(C.Feature.IMMUTABLE));
        try {
            l().insert(6, 0, 3);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }


    @Test
    public void testROInsert() {
        if (isMutable()) {
            return;
        }
        C.List<Integer> l1 = l().insert(0, 0);
        eq(seqOf(0, 1, 2, 3, 4, 5), l1);
        l1 = l().insert(1, 0);
        eq(seqOf(1, 0, 2, 3, 4, 5), l1);
        l1 = l().insert(5, 0);
        eq(seqOf(1, 2, 3, 4, 5, 0), l1);
        l1 = l().insert(-1, 0);
        eq(seqOf(1, 2, 3, 4, 0, 5), l1);
        try {
            l().insert(6, 0);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void testDrop() {
        data = prepareData(1, 2, 3, 4, 5);
        eq(seqOf(3, 4, 5), l().drop(2));
        eq(seqOf(1, 2, 3), l().drop(-2));
    }

    @Test
    public void testAppendList() {
        List<Integer> al = arrayList(0, 1);
        eq(seqOf(1, 2, 3, 4, 5, 0, 1), l().append(al));
    }
    
    @Test
    public void testPrependList() {
    	List<Integer> al = arrayList(0, 1);
    	eq(seqOf(0, 1, 1, 2, 3, 4, 5), l().prepend(al));
    }

    @Test
    public void testUnique() {
        data = prepareData(1, 2, 3, 2, 4);
        eq(seqOf(1, 2, 3, 4), l().unique());

    }

    @Test
    public void testUnique2() {
        data = prepareData(1, 1, 1);
        eq(seqOf(1), l().unique());
    }

    @Test
    public void testUniqueByComparator() {
        C.List<OddEvenElement> l = C.listOf(2, 2, 3, 4, 1, 5).map(OddEvenElement.F.OF_INT);
        eq(2, l.unique(new OddEvenElement.Comp()).size());
    }

    @Test
    public void testZipList() {
        data = prepareData(1, 3);
        C.List<?> l = data().zip(arrayList(2, 4, 6));
        eq(seqOf($.T2(1, 2), $.T2(3, 4)), l);
        l = data().zip(arrayList(2));
        eq(seqOf($.T2(1, 2)), l);
    }

    @Test
    public void testZipAllList() {
        data = prepareData(1, 3);
        C.List<?> l = data().zipAll(arrayList(2, 4, 6), -1, -2);
        eq(seqOf($.T2(1, 2), $.T2(3, 4), $.T2(-1, 6)), l);
        l = data().zipAll(arrayList(2), -1, -2);
        eq(seqOf($.T2(1, 2), $.T2(3, -2)), l);
    }

    @Test
    public void testReverse() {
        data = prepareData(1, 2);
        eq(seqOf(2, 1), data().reverse());
    }

    @Test
    public void testWithout() {
        data = prepareData(1, 2, 3);
        eq(seqOf(1, 3), data().without(arrayList(2, 4, 6)));
        eq(seqOf(1, 3), data().without(2));
        eq(seqOf(1), data().without(2, 4, 6, 3));
    }

    @Test
    public void testLocateFirst() {
        eq(1, data().locateFirst($.F.lessThan(3)).get());
    }

    @Test
    public void testLocateLast() {
        eq(2, data().locateLast($.F.lessThan(3)).get());
    }

    @Test
    public void testLocate() {
        yes(data().locate($.F.lessThan(3)).get() < 3);
    }

    @Test
    public void testToArray() {
        Object[] oa = {1, 2, 3, 4, 5};
        eq(oa, data().toArray());
    }

    @Test
    public void testToArray2() {
        Integer[] oa = {1, 2, 3, 4, 5};
        eq(oa, data().toArray(new Integer[]{}));
    }

    @Test
    public void testToArray3() {
        Integer[] oa = {1, 2, 3, 4, 5};
        Integer[] ob = new Integer[5];
        data().toArray(ob);
        eq(oa, ob);
    }

    @Test
    public void testSerialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        data = prepareData(1, 2, 3);
        oos.writeObject(data());
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        List copy = (List)new ObjectInputStream(bais).readObject();
        eq(copy, data);
    }

}
