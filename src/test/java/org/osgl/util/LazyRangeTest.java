package org.osgl.util;

import org.junit.Before;
import org.junit.Test;
import org.osgl.TestBase;
import org.osgl._;
import org.osgl.exception.InvalidArgException;

import java.io.IOException;
import java.util.Random;

public class LazyRangeTest extends TestBase {

    protected C.Range<Integer> range;

    @Before
    public void setUp() {
        range = C.range(0, 100);
    }

    @Test
    public void from() {
        eq(0, range.from());
    }

    @Test
    public void to() {
        eq(100, range.to());
    }

    @Test
    public void merge() {
        eq(C.range(0, 200), range.merge(C.range(100, 200)));
        eq(C.range(0, 200), range.merge(C.range(0, 200)));
        eq(C.range(0, 100), range.merge(C.range(0, 100)));
        eq(C.range(-100, 100), range.merge(C.range(-100, 0)));
        eq(C.range(-100, 100), range.merge(C.range(-100, 50)));
        eq(C.range(-1, 101), range.merge(C.range(-1, 101)));
        eq(C.range(0, 100), range.merge(C.range(10, 20)));
    }

    @Test
    public void merge1() {
        eq(C.range(0, 200), range.merge(C.range(200, 100)));
    }

    @Test(expected = InvalidArgException.class)
    public void merge2() {
        range.merge(C.range(101, 200));
    }

    @Test
    public void reverse() {
        eq(C.range(99, -1), range.reverse());
    }

    @Test
    public void last() {
        eq(99, range.last());
    }

    @Test
    public void tail() {
        eq(C.range(1, 100), range.tail());
    }

    @Test
    public void tailN() {
        eq(C.range(90, 100), range.tail(10));
    }

    @Test
    public void take() {
        eq(C.range(0, 10), range.take(10));
    }

    @Test
    public void drop() {
        eq(C.range(10, 100), range.drop(10));
    }

    @Test
    public void contains() {
        yes(range.contains(new Random().nextInt(100)));
        no(range.contains(new Random().nextInt(100) + 100));
    }

    @Test
    public void containsAll() {
        yes(range.containsAll(C.range(10, 20)));
    }

    @Test
    public void serialize() throws IOException {
        C.Range<Integer> newRange = _.materialize(_.serialize(range));
        eq(newRange, range);
    }

}
