package org.osgl.util;

import org.junit.Before;
import org.junit.Test;
import org.osgl.TestBase;
import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/09/13
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class LazySeqTest extends TestBase {

    C.Sequence<Integer> seq;

    private <T> C.Sequence<T> of(T... a) {
        return IteratorSeq.of(Arrays.asList(a).iterator());
    }

    private void sameContent(C.Traversable<?> t1, C.Traversable<?> t2) {
        Iterator<?> i1 = t1.iterator();
        Iterator<?> i2 = t2.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            Object o1 = i1.next();
            Object o2 = i2.next();
            no(_.ne(o1, o2));
        }
        no(i1.hasNext() || i2.hasNext());
    }

    @Before
    public void setup() {
        seq = of(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    public void head() {
        eq(1, seq.head());
    }

    @Test
    public void first() {
        eq(1, seq.head());
    }

    @Test
    public void headN() {
        sameContent(of(1, 2, 3), seq.head(3));
        sameContent(Nil.seq(), seq.head(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void headNegN() {
        sameContent(of(5, 6, 7), seq.head(-3));
    }

    @Test
    public void empty() {
        no(seq.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void last() {
        //yes(_.eq(7, seq.last()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reverse() {
        //seq.reverse();
    }

    @Test
    public void tail() {
        sameContent(of(2, 3, 4, 5, 6, 7), seq.tail());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tailN() {
        //seq.tail(5);
    }

    @Test
    public void take() {
        headN();
    }

    @Test
    public void takeWhile() {
        sameContent(of(1, 2, 3), seq.takeWhile(_.F.lt(4)));
    }

    @Test
    public void drop() {
        sameContent(of(4, 5, 6, 7), seq.drop(3));
    }

    @Test
    public void dropWhile() {
        sameContent(of(4, 5, 6, 7), seq.dropWhile(_.F.lt(4)));
    }

    @Test
    public void append() {
        sameContent(of(1, 2, 3, 4, 5, 6, 7, 8, 9), seq.append(of(8, 9)));
    }

    @Test
    public void prepend() {
        sameContent(of(8, 9, 1, 2, 3, 4, 5, 6, 7), seq.prepend(of(8, 9)));
    }

    @Test
    public void map() {
        sameContent(of("1", "2"), seq.take(2).map(_.F.asString()));
    }

    @Test
    public void flatMap() {
        sameContent(of(0, 0, 1, 0, 1, 2), seq.take(3).flatMap(new _.F1<Integer, Iterable<Integer>>(){
            @Override
            public Iterable<Integer> apply(Integer integer) throws NotAppliedException, _.Break {
                List<Integer> l = new ArrayList<Integer>();
                for (int i = 0; i < integer; ++i) {
                    l.add(i);
                }
                return l;
            }
        }));
    }

    @Test
    public void filter() {
        _.Predicate<Integer> even = new _.Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) != 0;
            }
        };
        sameContent(of(1, 3, 5, 7), seq.filter(even));
        sameContent(of(2, 4, 6), seq.filter(_.F.negate(even)));
    }

    private _.F2<Integer, Integer, Integer> aggregate = new _.F2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer i1, Integer i2) throws NotAppliedException, _.Break {
            return i1 + i2;
        }
    };

    @Test
    public void reduce() {
        eq((7 + 1) * 7 / 2, seq.reduce(aggregate).get());
    }

    @Test
    public void reduceIdentity() {
        eq((7 + 1) * 7 / 2, seq.reduce(0, aggregate));
    }

    @Test
    public void allMatch() {
        yes(seq.allMatch(_.F.lt(10)));
    }

    @Test
    public void anyMatch() {
        yes(seq.anyMatch(_.F.lt(3)));
    }

    @Test
    public void noneMatch() {
        yes(seq.noneMatch(_.F.lt(0)));
    }

    @Test
    public void findOne() {
        eq(_.none(), seq.findOne(_.F.lt(0)));
        yes(seq.findOne(_.F.gt(0)).isDefined());
    }


}
