package org.osgl.util;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class LazySeqTest extends SequenceTestBase {

    private static class MyLazySeq<T> extends LazySeq<T> {
        MyLazySeq(final List<T> data,  final int cursor) {
            super(data.get(cursor), new $.F0<C.Sequence<T>>() {
                @Override
                public C.Sequence<T> apply() throws NotAppliedException, $.Break {
                    if (cursor < data.size() - 1) {
                        return new MyLazySeq<T>(data, cursor + 1);
                    }
                    return Nil.seq();
                }
            });
        }
    }

    @Override
    protected C.Sequence<Integer> prepareData(final int... ia) {
        return new MyLazySeq<Integer>(Arrays.asList($.asObject(ia)), 0);
    }

    @Override
    protected C.Sequence<Integer> prepareEmptyData() {
        return Nil.list();
    }

    @Override
    protected <T> C.Sequence<T> prepareTypedData(T... ta) {
        return new MyLazySeq<T>(Arrays.asList(ta), 0);
    }
}
