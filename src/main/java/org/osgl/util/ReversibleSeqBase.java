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

import java.util.NoSuchElementException;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/11/13
 * Time: 6:41 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class ReversibleSeqBase<T> extends SequenceBase<T> implements C.ReversibleSequence<T> {
    @Override
    public C.ReversibleSequence<T> lazy() {
        super.lazy();
        return this;
    }

    @Override
    public C.ReversibleSequence<T> eager() {
        super.eager();
        return this;
    }

    @Override
    public C.ReversibleSequence<T> parallel() {
        super.parallel();
        return this;
    }

    @Override
    public C.ReversibleSequence<T> sequential() {
        super.sequential();
        return this;
    }

    @Override
    public ReversibleSeqBase<T> accept($.Function<? super T, ?> visitor) {
        super.accept(visitor);
        return this;
    }

    @Override
    public ReversibleSeqBase<T> each($.Function<? super T, ?> visitor) {
        return accept(visitor);
    }

    @Override
    public ReversibleSeqBase<T> forEach($.Function<? super T, ?> visitor) {
        return accept(visitor);
    }

    @Override
    public C.ReversibleSequence<T> acceptLeft($.Function<? super T, ?> visitor) {
        super.acceptLeft(visitor);
        return this;
    }

    @Override
    public C.ReversibleSequence<T> head(int n) {
        if (n == 0) {
            return Nil.rseq();
        } else if (n < 0) {
            if (isLimited()) {
                return drop(size() + n);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            if (isLimited() && n >= size()) {
                return this;
            }
            return IndexFilteredRSeq.of(this, $.F.lessThan(n));
        }
    }

    @Override
    public C.ReversibleSequence<T> tail() throws UnsupportedOperationException {
        return IndexFilteredRSeq.of(this, $.F.greaterThan(0));
    }

    @Override
    public C.ReversibleSequence<T> take(int n) {
        return head(n);
    }

    @Override
    public C.ReversibleSequence<T> takeWhile($.Function<? super T, Boolean> predicate) {
        return FilteredRSeq.of(this, predicate, FilteredIterator.Type.WHILE);
    }

    @Override
    public C.ReversibleSequence<T> drop(int n) throws IllegalArgumentException {
        int sz = size();
        if (n < 0) {
            n = -n;
            if (n >= sz) return Nil.rseq();
            return take(sz - n);
        }
        if (n == 0) {
            return this;
        }
        return IndexFilteredRSeq.of(this, $.F.gte(n));
    }

    @Override
    public C.ReversibleSequence<T> dropWhile($.Function<? super T, Boolean> predicate) {
        return FilteredRSeq.of(this, $.F.negate(predicate), FilteredIterator.Type.UNTIL);
    }

    @Override
    public C.ReversibleSequence<T> append(T t) {
        return CompositeRSeq.of(this, $.val(t));
    }

    @Override
    public C.ReversibleSequence<T> prepend(T t) {
        return CompositeRSeq.of($.val(t), this);
    }

    @Override
    public C.ReversibleSequence<T> filter($.Function<? super T, Boolean> predicate) {
        return FilteredRSeq.of(this, predicate);
    }

    @Override
    public <R> C.ReversibleSequence<R> map($.Function<? super T, ? extends R> mapper) {
        return MappedRSeq.of(this, mapper);
    }

    @Override
    public <R> C.ReversibleSequence<R> flatMap($.Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return FlatMappedRSeq.of(this, mapper);
    }

    @Override
    public C.ReversibleSequence<T> append(C.ReversibleSequence<T> seq) {
        if (seq.isEmpty()) {
            return this;
        }
        return CompositeRSeq.of(this, seq);
    }

    @Override
    public C.ReversibleSequence<T> prepend(C.ReversibleSequence<T> seq) {
        if (seq.isEmpty()) {
            return this;
        }
        return CompositeRSeq.of(seq, this);
    }

    @Override
    public T last() throws UnsupportedOperationException, NoSuchElementException {
        return reverseIterator().next();
    }

    @Override
    public C.ReversibleSequence<T> tail(int n) throws UnsupportedOperationException, IndexOutOfBoundsException {
        boolean immutable = isImmutable();
        int sz = size();
        if (n < 0) {
            return head(-n);
        } else if (n == 0) {
            return Nil.rseq();
        } else if (n >= sz) {
            return this;
        }
        return reverse().take(n).reverse();
    }

    @Override
    public C.ReversibleSequence<T> reverse() throws UnsupportedOperationException {
        if (isEmpty()) {
            return Nil.rseq();
        }
        return ReversedRSeq.of(this);
    }

    @Override
    public <R> R reduceRight(R identity, $.Func2<R, T, R> accumulator) {
        return reverse().reduceLeft(identity, accumulator);
    }

    @Override
    public $.Option<T> reduceRight($.Func2<T, T, T> accumulator) {
        return reverse().reduceLeft(accumulator);
    }

    @Override
    public $.Option<T> findLast($.Function<? super T, Boolean> predicate) {
        return reverse().findFirst(predicate);
    }

    @Override
    public C.ReversibleSequence<T> acceptRight($.Function<? super T, ?> visitor) {
        return reverse().acceptLeft(visitor);
    }

    @Override
    public <T2> C.ReversibleSequence<$.T2<T, T2>> zip(C.ReversibleSequence<T2> rseq) {
        return new ZippedRSeq<T, T2>(this, rseq);
    }

    @Override
    public <T2> C.ReversibleSequence<$.T2<T, T2>> zipAll(C.ReversibleSequence<T2> rseq, T def1, T2 def2) {
        return new ZippedRSeq<T, T2>(this, rseq, def1, def2);
    }
}
