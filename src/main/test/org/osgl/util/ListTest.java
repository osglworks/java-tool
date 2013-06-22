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

    protected C.List<Integer> liro;
    protected C.List<Integer> lirw;
    
    @Before
    public void setUp() {
        liro = C.list(5, 4, 3, 2, 1);
        yes(liro.ro());
        eq(liro.size(), 5);
        lirw = C.newList(5, 4, 3, 2, 1);
        no(lirw.ro());
        eq(lirw.size(), 5);
    }
    
    @Test
    public void testPrepend() {
        C.List<Integer> l = liro.prepend(7, 6);
        eq(liro.size(), 5);
        eq(l.size(), 7);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "7654321");
    }
    
    @Test
    public void testAppend() {
        C.List<Integer> l = liro.append(0);
        eq(liro.size(), 5);
        eq(l.size(), 6);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "543210");
    }
    
    @Test
    public void testKeepReadOnly() {
        C.List<Integer> l = liro.append(7, 6);
        yes(l.ro());
        
        l = lirw.append(7, 6);
        yes(l.rw());
    }
    
    @Test
    public void testWithout() {
        C.List<Integer> l = liro.without(7, 6, 3);
        eq(l.size(), 4);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "5421");
    }
    
    @Test
    public void testIntersect() {
        C.List<Integer> l = liro.intersect(7, 6, 3);
        eq(l.size(), 1);
        eq(l.get(0), 3);
    }
    
    @Test
    public void testFirst() {
        /* 5, 4, 3, 2, 1*/
        eq(liro.first(_.f.lt(4)), 3);
        eq(liro.first(_.f.lt(0)), null);
    }
    
    @Test
    public void testLast() {
        /* 5, 4, 3, 2, 1*/
        eq(liro.last(_.f.lt(4)), 1);
    }
    
    @Test
    public void testMap() {
        C.List<String> sl = C.list("chengdu,sydney".split(","));
        sl = sl.map(S.f.TO_UPPERCASE);
        eq(sl.join(), "CHENGDU,SYDNEY");
    }
    
    @Test
    public void testReduce() {
        eq(liro.reduce(0, N.f.aggregate(Integer.class)).intValue(), 15);
    }
    
    @Test
    public void foo() {
        C.List<Integer> list2 = C.list(1, 2, 3);
        F.IFunc2<Integer, Integer, Integer> func2 = _.f.sum();
        int x = list2.reduce(0, func2);
        System.out.println(x);
    }

    public static void main(String[] args) {
        run(ListTest.class);
    }
    
}
