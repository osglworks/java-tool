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

    static enum Type {
        ALL,
        WHILE,
        UNTIL;
        <T> Iterator<T> filter(Iterator<T> raw, _.Function<? super T, Boolean> predicate) {
            return new FilteredIterator<T>(raw, predicate, this);
        }
    }

    private final Iterator<? extends T> itr_;
    private final _.Predicate<T> filter_;
    private final Type type_;
    private boolean start_;

    FilteredIterator(Iterator<? extends T> iterator, _.Function<? super T, Boolean> filter) {
        this(iterator, filter, Type.ALL);
    }

    FilteredIterator(Iterator<? extends T> iterator, _.Function<? super T, Boolean> filter, Type type) {
        E.NPE(iterator, filter);
        itr_ = iterator;
        filter_ = _.predicate(filter);
        type_ = type;
    }

    @Override
    protected _.Option<T> getCurrent() {
        boolean ok;
        while (itr_.hasNext()) {
            T t = itr_.next();
            switch (type_) {
            case ALL:
                ok = filter_.test(t);
                if (ok) {
                    return _.some(t);
                } else {
                    continue;
                }
            case WHILE:
                ok = filter_.test(t);
                if (ok) {
                    return _.some(t);
                } else {
                    return _.none();
                }
            case UNTIL:
                if (start_) {
                    return _.some(t);
                }
                ok = filter_.test(t);
                if (ok) {
                    start_ = true;
                    return _.some(t);
                } else {
                    continue;
                }
            }
        }
        return _.none();
    }

}
