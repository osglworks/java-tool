package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgl.$;
import org.osgl.exception.InvalidArgException;
import org.osgl.exception.NotAppliedException;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Implement {@link C.Range} using {@link LazySeq}.
 */
public class LazyRange<ELEMENT> extends LazySeq<ELEMENT>
implements C.Range<ELEMENT>, Serializable {

    private final ELEMENT to;

    private final Comparator<ELEMENT> order;

    private final $.Func2<ELEMENT, Integer, ELEMENT> step;

    protected final int ordering;

    private final int size;

    protected final $.F1<ELEMENT, ELEMENT> next;

    protected final $.F1<ELEMENT, ELEMENT> prev;

    public LazyRange(final ELEMENT from, final ELEMENT to, final $.Func2<ELEMENT, Integer, ELEMENT> step) {
        this(from, to, $.F.NATURAL_ORDER, step);
    }

    public LazyRange(final ELEMENT from, final ELEMENT to, final Comparator<ELEMENT> order,
                     final $.Func2<ELEMENT, Integer, ELEMENT> step
    ) {
        E.NPE(from, to, order, step);

        ordering = N.sign(order.compare(from, to));
        boolean eq = $.eq(from, to);
        E.invalidArgIf(eq, "[from] shall not be equals to [to]");

        // check if step align with order
        ELEMENT next = step.apply(from, -ordering);
        int ordering2 = order.compare(from, next);
        if (N.sign(ordering2) != N.sign(ordering)) {
            E.invalidArg("step function doesn't align to the direction between [from] and [to]");
        }

        // find out the size of the range
        if (from instanceof Number) {
            int n0 = ((Number)from).intValue();
            int n1 = ((Number)to).intValue();
            int n2 = ((Number)next).intValue();

            int distance = n1 - n0;
            int unit = n2 - n0;
            int mod = distance%unit;
            if (mod > 0) {
                size = (distance + mod) / unit - 1;
            } else {
                size = distance / unit;
            }
        } else {
            size = -1;
        }

        this.to = to;
        this.head = from;
        this.order = order;
        this.step = step;

        $.F2<ELEMENT, Integer, ELEMENT> f2 = $.f2(step());
        this.next = f2.curry(-ordering);
        this.prev = f2.curry(ordering);
        this.tail = new $.F0<C.Sequence<ELEMENT>>() {
            @Override
            public C.Sequence<ELEMENT> apply() throws NotAppliedException, $.Break {
                if ($.eq(from, to)) {
                    return Nil.seq();
                } else {
                    return of(LazyRange.this.next.apply(from), to);
                }
            }
        };

        this.setFeature(C.Feature.LIMITED);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof C.Range) {
            C.Range<ELEMENT> that = (C.Range<ELEMENT>) obj;
            return $.eq(that.from(), from()) && $.eq(that.to(), to()) && $.eq(that.order(), order()) && $.eq(that.step(), step());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return $.hc(from(), to(), order(), step());
    }

    @Override
    public String toString() {
        return new StringBuilder("[").append(from()).append(",").append(to()).append(")").toString();
    }

    @Override
    public int size() throws UnsupportedOperationException {
        if (size < 0) {
            throw new UnsupportedOperationException();
        } else {
            return size;
        }
    }

    protected LazyRange<ELEMENT> of(ELEMENT from, ELEMENT to) {
        return new LazyRange<ELEMENT>(from, to, order, step);
    }

    public final ELEMENT from() {
        return head();
    }

    @Override
    public final ELEMENT to() {
        return to;
    }

    @Override
    public Comparator<ELEMENT> order() {
        return order;
    }

    @Override
    public $.Func2<ELEMENT, Integer, ELEMENT> step() {
        return step;
    }

    @Override
    public C.Range<ELEMENT> merge(C.Range<ELEMENT> r2) throws InvalidArgException {
        if ($.ne(step(), r2.step()) || $.ne(order(), r2.order())) {
            throw E.invalidArg("r2 and this range does not have the same step or order operator");
        }
        int ordering2 = N.sign(order.compare(r2.from(), r2.to()));
        if (ordering2 != ordering) {
            throw E.invalidArg("r2 and this range doesn't have the same ordering direction");
        }
        ELEMENT from1 = from(), to1 = step().apply(to, -1), from2 = r2.from(), to2 = r2.step().apply(r2.to(), -1);
        boolean fromInThis = contains(from2), toInThis = contains(to2);
        if (fromInThis && toInThis) {
            return this;
        }
        boolean fromInThat = r2.contains(from1), toInThat = r2.contains(to1);
        if (fromInThat && toInThat) {
            return r2;
        }
        if ((fromInThis && toInThat) || ($.eq(to(), from2))) {
            return of(from1, r2.to());
        }
        if ((toInThis && fromInThat) || ($.eq(from1, r2.to()))) {
            return of(from2, to);
        }
        throw E.invalidArg("r2 and this range cannot be merged together");
    }

    @Override
    public ELEMENT last() throws UnsupportedOperationException {
        return prev.apply(to);
    }

    @Override
    public C.Range<ELEMENT> tail() throws UnsupportedOperationException {
        ELEMENT from = next.apply(from());
        if ($.eq(from, to)) {
            return Nil.range();
        }
        return of(next.apply(from()), to);
    }

    @Override
    public C.Range<ELEMENT> head(int n) {
        return take(n);
    }

    @Override
    public C.Range<ELEMENT> tail(int n) throws UnsupportedOperationException {
        E.illegalArgumentIf(n <= 0, "n must be a positive int");
        return of(step().apply(to, -n), to);
    }

    @Override
    public C.Range<ELEMENT> take(int n) {
        E.invalidArgIf(n <= 0, "n must be a positive int");
        ELEMENT from = from();
        return of(from, step().apply(from, n));
    }

    @Override
    public C.Range<ELEMENT> drop(int n) {
        E.invalidArgIf(n <= 0, "n must be a positive int");
        ELEMENT from = from();
        return of(step().apply(from, n), to);
    }

    @Override
    public C.Range<ELEMENT> reverse() throws UnsupportedOperationException {
        return of(prev.apply(to), prev.apply(from()));
    }

    @Override
    public Iterator<ELEMENT> reverseIterator() {
        return reverse().iterator();
    }

    @Override
    public <R> R reduceRight(R identity, $.Func2<R, ELEMENT, R> accumulator) {
        return reverse().reduceLeft(identity, accumulator);
    }

    @Override
    public LazyRange<ELEMENT> accept($.Visitor<? super ELEMENT> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public LazyRange<ELEMENT> forEach($.Visitor<? super ELEMENT> visitor) {
        return accept(visitor);
    }

    @Override
    public LazyRange<ELEMENT> each($.Visitor<? super ELEMENT> visitor) {
        return accept(visitor);
    }

    @Override
    public LazyRange<ELEMENT> acceptLeft($.Visitor<? super ELEMENT> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public LazyRange<ELEMENT> acceptRight($.Visitor<? super ELEMENT> visitor) {
        reverse().acceptLeft(visitor);
        return this;
    }

    @Override
    public $.Option<ELEMENT> reduceRight($.Func2<ELEMENT, ELEMENT, ELEMENT> accumulator) {
        return reverse().reduceLeft(accumulator);
    }

    @Override
    public $.Option<ELEMENT> findLast($.Function<? super ELEMENT, Boolean> predicate) {
        return reverse().findFirst(predicate);
    }

    @Override
    public boolean contains(ELEMENT t) {
        E.NPE(t);

        if (0 == ordering) {
            return $.eq(to, t);
        }

        ELEMENT from = from();

        if ($.eq(from, t)) {
            return true;
        }

        int withFrom = order.compare(t, from);
        if (ordering < 0 && withFrom < 0) {
            return false;
        }
        int withTo = order.compare(t, to);
        return withFrom * withTo < 0;
    }

    @Override
    public boolean containsAll(C.Range<ELEMENT> range) {
        E.NPE(range);
        return contains(range.from()) && contains(prev.apply(range.to()));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new InvalidObjectException("Proxy required");
    }

    private static class SerializationProxy<ELEMENT> implements Serializable {
        ELEMENT from;
        ELEMENT to;
        Comparator<ELEMENT> order;
        $.Func2<ELEMENT, Integer, ELEMENT> step;

        SerializationProxy(LazyRange<ELEMENT> r) {
            from = r.from();
            to = r.to();
            order = r.order;
            step = r.step;
        }

        private Object readResolve() {
            return new LazyRange<ELEMENT>(from, to, order, step);
        }

        private static final long serialVersionUID = 21864874113505L;
    }

    private Object writeReplace() {
        return new SerializationProxy<ELEMENT>(this);
    }

}
