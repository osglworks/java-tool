package org.osgl.util;

import com.osgl.TestBase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 17/06/13
 * Time: 6:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListTest extends TestBase {

    protected C0.List<Integer> liro;
    protected C0.List<Integer> lirw;
    
    @Before
    public void setUp() {
        liro = C0.list(5, 4, 3, 2, 1);
        yes(liro.ro());
        eq(liro.size(), 5);
        lirw = C0.newList(5, 4, 3, 2, 1);
        no(lirw.ro());
        eq(lirw.size(), 5);
    }
    
    @Test
    public void testPrepend() {
        C0.List<Integer> l = liro.prepend(7, 6);
        eq(liro.size(), 5);
        eq(l.size(), 7);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "7654321");
    }
    
    @Test
    public void testAppend() {
        C0.List<Integer> l = liro.append(0);
        eq(liro.size(), 5);
        eq(l.size(), 6);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "543210");
    }
    
    @Test
    public void testKeepReadOnly() {
        C0.List<Integer> l = liro.append(7, 6);
        yes(l.ro());
        
        l = lirw.append(7, 6);
        yes(l.rw());
    }
    
    @Test
    public void testWithout() {
        C0.List<Integer> l = liro.without(7, 6, 3);
        eq(l.size(), 4);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "5421");
    }
    
    @Test
    public void testIntersect() {
        C0.List<Integer> l = liro.intersect(7, 6, 3);
        eq(l.size(), 1);
        eq(l.get(0), 3);
    }
    
    @Test
    public void testFirst() {
        /* 5, 4, 3, 2, 1*/
        eq(liro.first(X.f.lt(4)), 3);
        eq(liro.first(X.f.lt(0)), null);
    }
    
    @Test
    public void testLast() {
        /* 5, 4, 3, 2, 1*/
        eq(liro.last(X.f.lt(4)), 1);
    }
    
    @Test
    public void testMap() {
        C0.List<String> sl = C0.list("chengdu,sydney".split(","));
        sl = sl.map(S.f.TO_UPPERCASE);
        eq(sl.join(), "CHENGDU,SYDNEY");
    }
    
    @Test
    public void testReduce() {
        eq(liro.reduce(N.f.aggregate(Integer.class)), 15);
        eq(liro.reduce(10, N.f.aggregate(Integer.class)), 25);
    }
    
    @Test
    public void testAndOr() {
        C0.List<Integer> c1 = C0.list(1, 2, 3);
        eq(true, c1.and(X.f.gt(0)));
        eq(true, c1.or(X.f.gt(0)));
    }
    
    public static void main(String[] args) {
        run(ListTest.class);
    }
    
}
