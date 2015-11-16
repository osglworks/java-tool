package org.osgl.util;

import org.osgl.$;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 4/10/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
class MappedIterator<T, R> implements Iterator<R> {

    private Iterator<? extends T> data;
    private $.F1<? super T, ? extends R> mapper;

    MappedIterator
    (Iterator<? extends T> itr, $.Function<? super T, ? extends R> mapper) {
        E.NPE(itr, mapper);
        this.data = itr;
        this.mapper = $.f1(mapper);
    }

    protected Iterator<? extends T> data() {
        return data;
    }

    protected $.F1<? super T, ? extends R> mapper() {
        return mapper;
    }

    @Override
    public boolean hasNext() {
        return data.hasNext();
    }

    @Override
    public R next() {
        return mapper.apply(data.next());
    }

    @Override
    public void remove() {
        data.remove();
    }

    @Override
    public int hashCode() {
        return $.hc(data, mapper);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MappedIterator) {
            MappedIterator that = (MappedIterator)obj;
            return $.eq(that.data, data) && $.eq(that.mapper, mapper);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MappedIterator\nmapper[");
        sb.append(mapper).append("]\nbuf[\n").append(data).append("\n]");
        return sb.toString();
    }
}
