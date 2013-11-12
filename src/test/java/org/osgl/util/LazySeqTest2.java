package org.osgl.util;

import org.junit.Before;
import org.junit.Test;
import org.osgl._;
import org.osgl.exception.NotAppliedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/09/13
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class LazySeqTest2 extends UtilTestBase {

    C.Sequence<Integer> seq;

    @Before
    public void setup() {
        seq = seqOf(1, 2, 3, 4, 5, 6, 7);
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
        eq(seqOf(1, 2, 3), seq.head(3));
        eq(Nil.seq(), seq.head(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void headNegN() {
        eq(seqOf(5, 6, 7), seq.head(-3));
    }

    @Test
    public void empty() {
        no(seq.isEmpty());
    }

    @Test
    public void tail() {
        eq(seqOf(2, 3, 4, 5, 6, 7), seq.tail());
    }

    @Test
    public void take() {
        headN();
    }

    @Test
    public void takeWhile() {
        eq(seqOf(1, 2, 3), seq.takeWhile(_.F.lt(4)));
    }

    @Test
    public void drop() {
        eq(seqOf(4, 5, 6, 7), seq.drop(3));
    }

    @Test
    public void dropWhile() {
        eq(seqOf(4, 5, 6, 7), seq.dropWhile(_.F.lt(4)));
    }

    @Test
    public void append() {
        eq(seqOf(1, 2, 3, 4, 5, 6, 7, 8, 9), seq.append(seqOf(8, 9)));
    }

    @Test
    public void prepend() {
        eq(seqOf(8, 9, 1, 2, 3, 4, 5, 6, 7), seq.prepend(seqOf(8, 9)));
    }

    @Test
    public void map() {
        eq(seqOf("1", "2"), seq.take(2).map(_.F.asString()));
    }

    @Test
    public void flatMap() {
        eq(seqOf(0, 0, 1, 0, 1, 2), seq.take(3).flatMap(new _.F1<Integer, Iterable<Integer>>() {
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
        eq(seqOf(1, 3, 5, 7), seq.filter(even));
        setup();
        eq(seqOf(2, 4, 6), seq.filter(_.F.negate(even)));
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
        setup();
        yes(seq.findOne(_.F.gt(0)).isDefined());
    }


}
