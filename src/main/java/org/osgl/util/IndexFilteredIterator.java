package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 22/10/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexFilteredIterator<T> extends StatefulIterator<T> {
    private final Iterator<T> itr_;
    private final _.Predicate<Integer> filter_;
    private int cursor;

    public IndexFilteredIterator(Iterator<T> iterator, _.Function<Integer, Boolean> filter) {
        E.NPE(iterator, filter);
        itr_ = iterator;
        filter_ = _.predicate(filter);
    }

    private boolean rawHasNext() {
        return itr_.hasNext();
    }

    private T rawNext() {
        cursor++;
        return itr_.next();
    }

    @Override
    protected _.Option<T> getCurrent() {
        while (rawHasNext()) {
            T t = rawNext();
            if (filter_.test(cursor)) {
                return _.some(t);
            }
        }
        return _.none();
    }
}
