package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 22/10/13
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
class FilteredIterator<T> extends StatefulIterator<T> {
    private final Iterator<T> itr_;
    private final _.Predicate<T> filter_;

    FilteredIterator(Iterator<T> iterator, _.Function<? super T, Boolean> filter) {
        E.NPE(iterator, filter);
        itr_ = iterator;
        filter_ = _.predicate(filter);
    }

    @Override
    protected _.Option<T> getCurrent() {
        while (itr_.hasNext()) {
            T t = itr_.next();
            if (filter_.test(t)) {
                return _.some(t);
            }
        }
        return _.none();
    }
}
