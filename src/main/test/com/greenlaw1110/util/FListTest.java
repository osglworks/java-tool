package com.greenlaw1110.util;

import com.greenlaw110.TestBase;
import com.greenlaw110.util.C;
import com.greenlaw110.util.F;
import com.greenlaw110.util.S;
import org.junit.Before;
import org.junit.Test;

import static com.greenlaw110.util._.f.lt;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 17/06/13
 * Time: 6:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class FListTest extends TestBase {

    protected F.List<Integer> liro;
    protected F.List<Integer> lirw;
    
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
        F.List<Integer> l = liro.prepend(7, 6);
        eq(liro.size(), 5);
        eq(l.size(), 7);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "7654321");
    }
    
    @Test
    public void testAppend() {
        F.List<Integer> l = liro.append(0);
        eq(liro.size(), 5);
        eq(l.size(), 6);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "543210");
    }
    
    @Test
    public void testKeepReadOnly() {
        F.List<Integer> l = liro.append(7, 6);
        yes(l.ro());
        
        l = lirw.append(7, 6);
        yes(l.rw());
    }
    
    @Test
    public void testWithout() {
        F.List<Integer> l = liro.without(7, 6, 3);
        eq(l.size(), 4);
        eq(l.reduce(new StringBuilder(), S.f.CONCAT).toString(), "5421");
    }
    
    @Test
    public void testIntersect() {
        F.List<Integer> l = liro.intersect(7, 6, 3);
        eq(l.size(), 1);
        eq(l.get(0), 3);
    }
    
    @Test
    public void testFirst() {
        eq(liro.first(lt(4)), 3);
    }
    
    @Test
    public void testLast() {
        eq(liro.last(lt(4)), 1);
    }
    
}
