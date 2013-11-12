package org.osgl.util;

import org.osgl._;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/10/13
 * Time: 8:51 PM
 * To change this template use File | Settings | File Templates.
 */
class CompositeIterator<T> extends StatefulIterator<T> {
    private final Iterator<T> left_;
    private final Iterator<T> right_;
    private volatile boolean leftOver_;

    CompositeIterator(Iterator<T> i1, Iterator<T> i2) {
        E.NPE(i1, i2);
        left_ = i1;
        right_ = i2;
    }

    @Override
    protected _.Option<T> getCurrent() {
        if (leftOver_) {
            if (right_.hasNext()) {
                return _.some(right_.next());
            } else {
                return _.none();
            }
        } else {
            if (left_.hasNext()) {
                return _.some(left_.next());
            } else {
                leftOver_ = true;
                return getCurrent();
            }
        }
    }
}
