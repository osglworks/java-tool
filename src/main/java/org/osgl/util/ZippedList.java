package org.osgl.util;

import org.osgl.$;

import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/11/13
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
class ZippedList<A, B> extends ListBase<$.T2<A, B>> {
    private List<A> a;
    private List<B> b;
    private $.Option<A> defA = $.none();
    private $.Option<B> defB = $.none();

    ZippedList(List<A> a, List<B> b) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
    }

    ZippedList(List<A> a, List<B> b, A defA, B defB) {
        E.NPE(a, b);
        this.a = a;
        this.b = b;
        this.defA = $.some(defA);
        this.defB = $.some(defB);
    }

    @Override
    protected EnumSet<C.Feature> initFeatures() {
        return EnumSet.of(C.Feature.LAZY, C.Feature.READONLY, C.Feature.LIMITED);
    }

    @Override
    public int size() {
        if (defA.isDefined()) {
            return Math.max(a.size(), b.size());
        } else {
            return Math.min(a.size(), b.size());
        }
    }

    @Override
    public $.T2<A, B> get(int index) {
        return $.T2(a.get(index), b.get(index));
    }

    @Override
    public ListIterator<$.T2<A, B>> listIterator(int index) {
        if (defA.isDefined()) {
            return new ZippedListIterator<A, B>(a.listIterator(), b.listIterator(), defA.get(), defB.get());
        }
        return new ZippedListIterator<A, B>(a.listIterator(), b.listIterator());
    }


}
