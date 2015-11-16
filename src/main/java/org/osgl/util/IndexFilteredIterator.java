package org.osgl.util;

import org.osgl.$;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 22/10/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexFilteredIterator<T> extends StatefulIterator<T> {
    private final Iterator<? extends T> itr_;
    private final $.Predicate<Integer> filter_;
    private int cursor;

    public IndexFilteredIterator(Iterator<? extends T> iterator, $.Function<Integer, Boolean> filter) {
        E.NPE(iterator, filter);
        itr_ = iterator;
        filter_ = $.predicate(filter);
    }

    private boolean rawHasNext() {
        return itr_.hasNext();
    }

    private T rawNext() {
        cursor++;
        return itr_.next();
    }

    @Override
    protected $.Option<T> getCurrent() {
        while (rawHasNext()) {
            int curCursor = cursor;
            T t = rawNext();
            if (filter_.test(curCursor)) {
                return $.some(t);
            }
        }
        return $.none();
    }
}
