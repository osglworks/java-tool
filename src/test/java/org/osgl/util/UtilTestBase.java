package org.osgl.util;

import org.osgl.TestBase;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/11/13
 * Time: 9:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class UtilTestBase extends TestBase {

    protected <T> C.Sequence<T> seqOf(T... a) {
        return IteratorSeq.of(Arrays.asList(a).iterator());
    }

    public static void eq(Iterable<?> t1, Iterable<?> t2) {
        Iterator<?> i1 = t1.iterator();
        Iterator<?> i2 = t2.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            Object o1 = i1.next();
            Object o2 = i2.next();
            eq(o1, o2);
        }
        eq(i1.hasNext(), i2.hasNext());
    }

}
